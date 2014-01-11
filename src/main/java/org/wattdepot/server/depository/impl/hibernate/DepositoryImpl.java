/**
 * DepositoryImpl.java This file is part of WattDepot.
 *
 * Copyright (C) 2013  Cam Moore
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.wattdepot.server.depository.impl.hibernate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.Session;
import org.wattdepot.common.domainmodel.Depository;
import org.wattdepot.common.domainmodel.MeasuredValue;
import org.wattdepot.common.domainmodel.Measurement;
import org.wattdepot.common.exception.MeasurementGapException;
import org.wattdepot.common.exception.MeasurementTypeException;
import org.wattdepot.common.exception.NoMeasurementException;
import org.wattdepot.server.ServerProperties;

/**
 * DepositoryImpl - Implementation of Depository that stores the Measurements in
 * a Hibernate database.
 * 
 * @author Cam Moore
 * 
 */
@Entity
@Table(name = "DEPOSITORIES")
public class DepositoryImpl {

  /** Database primary key. */
  @Id
  @GeneratedValue
  private Long pk;
  /** The unique id it is also a slug used in URIs. */
  private String id;
  /** Name of the Depository. */
  private String name;
  /** MeasurementType of the measurements stored in this depository. */
  @ManyToOne
  private MeasurementTypeImpl type;
  /** This depository's organization. */
  @ManyToOne
  private OrganizationImpl org;
  @Transient
  private ServerProperties serverProperties;

  /**
   * Default constructor.
   */
  public DepositoryImpl() {
    super();
  }

  /**
   * @param id the id.
   * @param name the name.
   * @param type the type.
   * @param org the organization.
   */
  public DepositoryImpl(String id, String name, MeasurementTypeImpl type, OrganizationImpl org) {
    this.id = id;
    this.name = name;
    this.type = type;
    this.org = org;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.datamodel.Depository#deleteMeasurement(org.wattdepot.datamodel
   * .Measurement)
   */
  public void deleteMeasurement(Measurement meas) {
    Measurement impl = getMeasurement(meas.getId());
    Session session = Manager.getFactory(serverProperties).openSession();
    session.beginTransaction();
    session.delete(impl);
    // session.delete(meas);
    session.getTransaction().commit();
    session.close();
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    DepositoryImpl other = (DepositoryImpl) obj;
    if (id == null) {
      if (other.id != null) {
        return false;
      }
    }
    else if (!id.equals(other.id)) {
      return false;
    }
    if (name == null) {
      if (other.name != null) {
        return false;
      }
    }
    else if (!name.equals(other.name)) {
      return false;
    }
    if (org == null) {
      if (other.org != null) {
        return false;
      }
    }
    else if (!org.equals(other.org)) {
      return false;
    }
    if (pk == null) {
      if (other.pk != null) {
        return false;
      }
    }
    else if (!pk.equals(other.pk)) {
      return false;
    }
    if (type == null) {
      if (other.type != null) {
        return false;
      }
    }
    else if (!type.equals(other.type)) {
      return false;
    }
    return true;
  }

  /**
   * @param sensorId The id of the Sensor making the measurements.
   * @return The earliest measurement Value
   * @throws NoMeasurementException If there aren't any measurements around the
   *         time.
   */
  public MeasuredValue getEarliestMeasuredValue(String sensorId) throws NoMeasurementException {
    MeasuredValue value = null;
    Session session = Manager.getFactory(serverProperties).openSession();
    MeasurementTypeImpl type = getType();

    @SuppressWarnings("unchecked")
    List<MeasurementImpl> result = (List<MeasurementImpl>) session
        .createQuery(
            "FROM MeasurementImpl WHERE depositoryId = :name AND sensorId = :sensorId "
                + "AND date IN (SELECT min(date) FROM MeasurementImpl WHERE "
                + "depositoryId = :name AND sensorId = :sensorId)").setParameter("name", getId())
        .setParameter("sensorId", sensorId).list();
    if (result.size() > 0) {
      MeasurementImpl meas = result.get(0);
      value = new MeasuredValue(sensorId, meas.getValue(), type.toMeasurementType());
      value.setDate(meas.getTimestamp());
    }

    session.close();
    return value;
  }

  /**
   * @return the id
   */
  public String getId() {
    return id;
  }

  /**
   * @param sensorId The id of the Sensor making the measurements.
   * @return The latest measurement Value
   * @throws NoMeasurementException If there aren't any measurements around the
   *         time.
   */
  public MeasuredValue getLatestMeasuredValue(String sensorId) throws NoMeasurementException {
    MeasuredValue value = null;
    Session session = Manager.getFactory(serverProperties).openSession();
    MeasurementTypeImpl type = getType();

    @SuppressWarnings("unchecked")
    List<MeasurementImpl> result = (List<MeasurementImpl>) session
        .createQuery(
            "FROM MeasurementImpl WHERE depositoryId = :name AND sensorId = :sensor "
                + "AND date IN (SELECT max(date) FROM MeasurementImpl WHERE "
                + "depositoryId = :name AND sensorId = :sensor)").setParameter("name", getId())
        .setParameter("sensor", sensorId).list();
    if (result.size() > 0) {
      MeasurementImpl meas = result.get(0);
      value = new MeasuredValue(sensorId, meas.getValue(), type.toMeasurementType());
      value.setDate(meas.getTimestamp());
    }

    session.close();
    return value;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.datamodel.Depository#getMeasurement(java.lang.String)
   */
  public Measurement getMeasurement(String measId) {
    Measurement ret = null;
    Session session = Manager.getFactory(serverProperties).openSession();
    session.beginTransaction();
    @SuppressWarnings("unchecked")
    List<MeasurementImpl> measurements = (List<MeasurementImpl>) session.createQuery(
        "FROM MeasurementImpl").list();
    for (MeasurementImpl meas : measurements) {
      if (meas.getId().equals(measId)) {
        ret = meas.toMeasurement();
      }
    }
    session.getTransaction().commit();
    session.close();
    return ret;
  }

  /**
   * @param session The session with an open transaction.
   * @param sensorId The id of the Sensor.
   * @return A List of the Measurements made by the sensor.
   */
  public List<Measurement> getMeasurements(Session session, String sensorId) {
    List<Measurement> ret = new ArrayList<Measurement>();
    @SuppressWarnings("unchecked")
    List<MeasurementImpl> measurements = (List<MeasurementImpl>) session
        .createQuery("FROM MeasurementImpl WHERE depositoryId = :depository")
        .setParameter("depository", getId()).list();
    for (MeasurementImpl meas : measurements) {
      if (meas.getSensor().getId().equals(sensorId)) {
        ret.add(meas.toMeasurement());
      }
    }
    return ret;

  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.datamodel.Depository#getMeasurements(java.lang.String)
   */
  public List<Measurement> getMeasurements(String sensorId) {
    Session session = Manager.getFactory(serverProperties).openSession();
    session.beginTransaction();
    List<Measurement> ret = getMeasurements(session, sensorId);
    session.getTransaction().commit();
    session.close();
    return ret;

  }

  /**
   * @param sensorId The Sensor.
   * @param start The start of the interval.
   * @param end The end of the interval.
   * @return A list of the measurements in the interval.
   */
  public List<Measurement> getMeasurements(String sensorId, Date start, Date end) {
    List<Measurement> ret = new ArrayList<Measurement>();
    Session session = Manager.getFactory(serverProperties).openSession();
    session.beginTransaction();
    @SuppressWarnings("unchecked")
    List<MeasurementImpl> measurements = (List<MeasurementImpl>) session
        .createQuery(
            "FROM MeasurementImpl WHERE date >= :start AND date <= :end AND depositoryId = :depository")
        .setParameter("start", start).setParameter("end", end).setParameter("depository", getId())
        .list();
    for (MeasurementImpl meas : measurements) {
      if (meas.getSensor().getId().equals(sensorId)) {
        ret.add(meas.toMeasurement());
      }
    }
    session.getTransaction().commit();
    session.close();
    return ret;
  }

  /**
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * @return the org
   */
  public OrganizationImpl getOrg() {
    return org;
  }

  /**
   * @return the pk
   */
  public Long getPk() {
    return pk;
  }

  /**
   * @return the type
   */
  public MeasurementTypeImpl getType() {
    return type;
  }

  /**
   * @param sensorId The id of the Sensor making the measurements.
   * @param timestamp The time of the value.
   * @return The Value 'measured' at the given time, most likely an interpolated
   *         value.
   * @throws NoMeasurementException If there aren't any measurements around the
   *         time.
   */
  public Double getValue(String sensorId, Date timestamp) throws NoMeasurementException {
    Double ret = null;
    Session session = Manager.getFactory(serverProperties).openSession();
    session.beginTransaction();

    @SuppressWarnings("unchecked")
    List<MeasurementImpl> result = (List<MeasurementImpl>) session
        .createQuery(
            "FROM MeasurementImpl WHERE date = :time AND measurementType = :measType"
                + " AND depositoryId = :name").setParameter("time", timestamp)
        .setParameter("measType", getType()).setParameter("name", getId()).list();
    if (result.size() > 0) {
      for (MeasurementImpl meas : result) {
        if (meas.getSensor().getId().equals(sensorId)) {
          ret = meas.getValue();
        }
      }
    }
    else {
      // need to get the stradle
      @SuppressWarnings("unchecked")
      List<MeasurementImpl> before = (List<MeasurementImpl>) session
          .createQuery(
              "FROM MeasurementImpl WHERE date <= :time AND measurementType = :measType"
                  + " AND depositoryId = :name").setParameter("time", timestamp)
          .setParameter("measType", getType()).setParameter("name", getId()).list();
      @SuppressWarnings("unchecked")
      List<MeasurementImpl> after = (List<MeasurementImpl>) session
          .createQuery(
              "FROM MeasurementImpl WHERE date >= :time AND measurementType = :measType"
                  + " AND depositoryId = :name").setParameter("time", timestamp)
          .setParameter("measType", getType()).setParameter("name", getId()).list();
      MeasurementImpl justBefore = null;
      for (MeasurementImpl b : before) {
        if (b.getSensor().getId().equals(sensorId)) {
          if (justBefore == null) {
            justBefore = b;
          }
          else if (b.getTimestamp().compareTo(justBefore.getTimestamp()) > 0) {
            justBefore = b;
          }
        }
      }
      if (justBefore == null) {
        session.getTransaction().commit();
        session.close();
        throw new NoMeasurementException("Cannot find measurement before " + timestamp);
      }
      MeasurementImpl justAfter = null;
      for (MeasurementImpl a : after) {
        if (a.getSensor().getId().equals(sensorId)) {
          if (justAfter == null) {
            justAfter = a;
          }
          else if (a.getTimestamp().compareTo(justBefore.getTimestamp()) > 0) {
            justAfter = a;
          }
        }
      }
      if (justAfter == null) {
        session.getTransaction().commit();
        session.close();
        throw new NoMeasurementException("Cannot find measurement after " + timestamp);
      }
      Double val1 = justBefore.getValue();
      Double val2 = justAfter.getValue();
      Double deltaV = val2 - val1;
      Long t1 = justBefore.getTimestamp().getTime();
      Long t2 = justAfter.getTimestamp().getTime();
      Long deltaT = t2 - t1;
      Long t3 = timestamp.getTime();
      Long toDate = t3 - t1;
      Double slope = deltaV / deltaT;
      ret = val1 + (slope * toDate);
    }
    session.getTransaction().commit();
    session.close();
    return ret;
  }

  /**
   * @param sensorId The id of the Sensor making the measurements.
   * @param start The start of the period.
   * @param end The end of the period.
   * @return The value measured the difference between the end value and the
   *         start value.
   * @throws NoMeasurementException if there are no measurements around the
   *         start or end time.
   */
  public Double getValue(String sensorId, Date start, Date end) throws NoMeasurementException {
    Double endVal = getValue(sensorId, end);
    Double startVal = getValue(sensorId, start);
    if (endVal != null && startVal != null) {
      return endVal - startVal;
    }
    return null;
  }

  /**
   * @param sensorId The Sensor making the measurements.
   * @param start The start of the interval.
   * @param end The end of the interval
   * @param gapSeconds The maximum number of seconds that measurements need to
   *        be within the start and end.
   * @return The value measured the difference between the end value and the
   *         start value.
   * @throws NoMeasurementException if there are no measurements around the
   *         start or end time.
   * @throws MeasurementGapException if the measurements around start or end are
   *         too far apart.
   */
  public Double getValue(String sensorId, Date start, Date end, Long gapSeconds)
      throws NoMeasurementException, MeasurementGapException {
    Double endVal = getValue(sensorId, end, gapSeconds);
    Double startVal = getValue(sensorId, start, gapSeconds);
    if (endVal != null && startVal != null) {
      return endVal - startVal;
    }
    return null;
  }

  /**
   * @param sensorId The Sensor making the measurements.
   * @param timestamp The time of the value.
   * @param gapSeconds The maximum number of seconds that measurements need to
   *        be within the start and end.
   * @return The Value 'measured' at the given time, most likely an interpolated
   *         value.
   * @throws NoMeasurementException If there aren't any measurements around the
   *         time.
   * @throws MeasurementGapException if the measurements around timestamp are
   *         too far apart.
   */
  public Double getValue(String sensorId, Date timestamp, Long gapSeconds)
      throws NoMeasurementException, MeasurementGapException {
    Double ret = null;
    Session session = Manager.getFactory(serverProperties).openSession();
    session.beginTransaction();

    @SuppressWarnings("unchecked")
    List<MeasurementImpl> result = (List<MeasurementImpl>) session
        .createQuery(
            "FROM MeasurementImpl WHERE date = :time AND measurementType = :measType"
                + " AND depositoryId = :name").setParameter("time", timestamp)
        .setParameter("measType", getType()).setParameter("name", getId()).list();
    if (result.size() > 0) {
      for (MeasurementImpl meas : result) {
        if (meas.getSensor().getId().equals(sensorId)) {
          ret = meas.getValue();
        }
      }
    }
    else {
      // need to get the stradle
      @SuppressWarnings("unchecked")
      List<MeasurementImpl> before = (List<MeasurementImpl>) session
          .createQuery(
              "FROM MeasurementImpl WHERE date <= :time AND measurementType = :measType"
                  + " AND depositoryId = :name").setParameter("time", timestamp)
          .setParameter("measType", getType()).setParameter("name", getId()).list();
      @SuppressWarnings("unchecked")
      List<MeasurementImpl> after = (List<MeasurementImpl>) session
          .createQuery(
              "FROM MeasurementImpl WHERE date >= :time AND measurementType = :measType"
                  + " AND depositoryId = :name").setParameter("time", timestamp)
          .setParameter("measType", getType()).setParameter("name", getId()).list();
      MeasurementImpl justBefore = null;
      for (MeasurementImpl b : before) {
        if (b.getSensor().getId().equals(sensorId)) {
          if (justBefore == null) {
            justBefore = b;
          }
          else if (b.getTimestamp().compareTo(justBefore.getTimestamp()) > 0) {
            justBefore = b;
          }
        }
      }
      if (justBefore == null) {
        session.getTransaction().commit();
        session.close();
        throw new NoMeasurementException("Cannot find measurement before " + timestamp);
      }
      MeasurementImpl justAfter = null;
      for (MeasurementImpl a : after) {
        if (a.getSensor().getId().equals(sensorId)) {
          if (justAfter == null) {
            justAfter = a;
          }
          else if (a.getTimestamp().compareTo(justBefore.getTimestamp()) > 0) {
            justAfter = a;
          }
        }
      }
      if (justAfter == null) {
        session.getTransaction().commit();
        session.close();
        throw new NoMeasurementException("Cannot find measurement after " + timestamp);
      }
      Double val1 = justBefore.getValue();
      Double val2 = justAfter.getValue();
      Double deltaV = val2 - val1;
      Long t1 = justBefore.getTimestamp().getTime();
      Long t2 = justAfter.getTimestamp().getTime();
      Long deltaT = t2 - t1;
      if ((deltaT / 1000) > gapSeconds) {
        session.getTransaction().commit();
        session.close();
        throw new MeasurementGapException("Gap of " + (deltaT / 1000) + "s is longer than "
            + gapSeconds);
      }
      Long t3 = timestamp.getTime();
      Long toDate = t3 - t1;
      Double slope = deltaV / deltaT;
      ret = val1 + (slope * toDate);
    }
    session.getTransaction().commit();
    session.close();
    return ret;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((org == null) ? 0 : org.hashCode());
    result = prime * result + ((pk == null) ? 0 : pk.hashCode());
    result = prime * result + ((type == null) ? 0 : type.hashCode());
    return result;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.datamodel.Depository#getSensors()
   */
  public List<String> listSensors() {
    Session session = Manager.getFactory(serverProperties).openSession();
    session.beginTransaction();
    @SuppressWarnings("unchecked")
    List<MeasurementImpl> result = (List<MeasurementImpl>) session
        .createQuery("FROM MeasurementImpl WHERE depositoryId = :name")
        .setParameter("name", getId()).list();
    ArrayList<String> sensorIds = new ArrayList<String>();
    for (MeasurementImpl meas : result) {
      if (!sensorIds.contains(meas.getSensor().getId())) {
        sensorIds.add(meas.getSensor().getId());
      }
    }
    session.getTransaction().commit();
    session.close();
    return sensorIds;
  }

  /**
   * @param session The Session with an open transaction.
   * @return A List of Sensors contributing measurements to this depository.
   */
  public List<String> listSensors(Session session) {
    @SuppressWarnings("unchecked")
    List<MeasurementImpl> result = (List<MeasurementImpl>) session
        .createQuery("FROM MeasurementImpl WHERE depositoryId = :name")
        .setParameter("name", getId()).list();
    ArrayList<String> sensors = new ArrayList<String>();
    for (MeasurementImpl meas : result) {
      if (!sensors.contains(meas.getSensor().getId())) {
        sensors.add(meas.getSensor().getId());
      }
    }
    return sensors;
  }

  /**
   * @param meas The measurement to store.
   * @throws MeasurementTypeException if the type of the measurement doesn't
   *         match the Depository measurement type.
   */
  public void putMeasurement(Measurement meas) throws MeasurementTypeException {
    if (!meas.getMeasurementType().equals(getType().getUnits())) {
      throw new MeasurementTypeException("Measurement's type " + meas.getMeasurementType()
          + " does not match " + getType());
    }
    MeasurementImpl mImpl = new MeasurementImpl();
    SensorImpl sensor = null;
    Session session = Manager.getFactory(serverProperties).openSession();
    session.beginTransaction();
    mImpl.setId(meas.getId());
    sensor = retrieveSensor(session, meas.getSensorId(), org.getId());
    mImpl.setSensor(sensor);
    mImpl.setTimestamp(meas.getDate());
    mImpl.setUnits(meas.getMeasurementType().toString());
    mImpl.setDepository(this);
    session.saveOrUpdate(mImpl);
    session.getTransaction().commit();
    session.close();
  }

  /**
   * @param session A Session with an open transaction.
   * @param id The id of the Organization.
   * @return The Organization with the given id.
   */
  @SuppressWarnings("unchecked")
  private OrganizationImpl retrieveOrganization(Session session, String id) {
    List<OrganizationImpl> ret = (List<OrganizationImpl>) session
        .createQuery("from OrganizationImpl WHERE id = :id").setParameter("id", id).list();
    if (ret.size() == 1) {
      return ret.get(0);
    }
    return null;
  }

  /**
   * @param session The session with an open transaction.
   * @param id the Sensor's id.
   * @param ownerId The owner's Organization id.
   * @return The Sensor with the given id and owned by ownerId.
   */
  @SuppressWarnings("unchecked")
  private SensorImpl retrieveSensor(Session session, String id, String ownerId) {
    OrganizationImpl org = retrieveOrganization(session, ownerId);
    List<SensorImpl> ret = (List<SensorImpl>) session
        .createQuery("FROM SensorImpl WHERE id = :id AND org = :org").setParameter("id", id)
        .setParameter("org", org).list();
    if (ret.size() == 1) {
      return ret.get(0);
    }
    return null;
  }

  /**
   * @return the serverProperties
   */
  public ServerProperties serverProperties() {
    return serverProperties;
  }

  /**
   * @param serverProperties the serverProperties to set
   */
  public void serverProperties(ServerProperties serverProperties) {
    this.serverProperties = serverProperties;
  }

  /**
   * @param id the id to set
   */
  public void setId(String id) {
    this.id = id;
  }

  /**
   * @param name the name to set
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * @param org the org to set
   */
  public void setOrg(OrganizationImpl org) {
    this.org = org;
  }

  /**
   * @param pk the pk to set
   */
  @SuppressWarnings("unused")
  private void setPk(Long pk) {
    this.pk = pk;
  }

  /**
   * @param type the type to set
   */
  public void setType(MeasurementTypeImpl type) {
    this.type = type;
  }

  /**
   * @return the Depository equivalent to this.
   */
  public Depository toDepository() {
    Depository ret = new Depository(id, name, type.toMeasurementType(), org.getId());
    return ret;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "DepositoryImpl [pk=" + pk + ", id=" + id + ", name=" + name + ", type=" + type
        + ", org=" + org + "]";
  }

}
