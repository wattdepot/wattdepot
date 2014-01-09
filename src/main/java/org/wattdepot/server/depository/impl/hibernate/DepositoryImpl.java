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

import org.hibernate.Session;
import org.wattdepot.common.domainmodel.Depository;
import org.wattdepot.common.domainmodel.MeasuredValue;
import org.wattdepot.common.domainmodel.Measurement;
import org.wattdepot.common.domainmodel.MeasurementType;
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
@SuppressWarnings("PMD.UselessOverridingMethod")
public class DepositoryImpl extends Depository {

  /** Database primary key. */
  private Long pk;
  /** Owner's foreign key. */
  private Long ownerFk;
  private ServerProperties serverProperties;

  /**
   * Default constructor.
   */
  public DepositoryImpl() {
    super();
  }

  /**
   * Creates a DepositoryImpl from a Depository.
   * 
   * @param clone
   *          The Depository to clone.
   */
  public DepositoryImpl(Depository clone) {
    super(clone.getName(), clone.getMeasurementType(), clone.getOwnerId());
  }

  /**
   * @param name
   *          The name of the depository.
   * @param measurementType
   *          The type of the measurement.
   * @param ownerId
   *          The owner.
   */
  public DepositoryImpl(String name, MeasurementType measurementType, String ownerId) {
    super(name, measurementType, ownerId);
  }

  /**
   * @param slug
   *          The unique slug for the CollectorProcessDefinitionImpl.
   * @param name
   *          The name of the depository.
   * @param measurementType
   *          The type of the measurement.
   * @param ownerId
   *          The owner.
   */
  public DepositoryImpl(String slug, String name, MeasurementType measurementType, String ownerId) {
    super(slug, name, measurementType, ownerId);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.datamodel.Depository#deleteMeasurement(org.wattdepot.datamodel
   * .Measurement)
   */
  @Override
  public void deleteMeasurement(Measurement meas) {
    Measurement impl = getMeasurement(meas.getSlug());
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
    if (obj == null) {
      return false;
    }
    if (this == obj) {
      return true;
    }
    if (obj.getClass().equals(Depository.class)) {
      return super.equals(obj);
    }
    if (!super.equals(obj)) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    DepositoryImpl other = (DepositoryImpl) obj;
    if (serverProperties == null) {
      if (other.serverProperties != null) {
        return false;
      }
    }
    else if (!serverProperties.equals(other.serverProperties)) {
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
    if (ownerFk == null) {
      if (other.ownerFk != null) {
        return false;
      }
    }
    else if (!ownerFk.equals(other.ownerFk)) {
      return false;
    }
    return true;
  }

  /**
   * @param sensorId
   *          The id of the Sensor making the measurements.
   * @return The earliest measurement Value
   * @throws NoMeasurementException
   *           If there aren't any measurements around the time.
   */
  @Override
  public MeasuredValue getEarliestMeasuredValue(String sensorId) throws NoMeasurementException {
    MeasuredValue value = null;
    Session session = Manager.getFactory(serverProperties).openSession();
    MeasurementType type = getMeasurementType();

    @SuppressWarnings("unchecked")
    List<MeasurementImpl> result = (List<MeasurementImpl>) session
        .createQuery(
            "FROM MeasurementImpl WHERE depositoryId = :name AND sensorId = :sensorId "
                + "AND date IN (SELECT min(date) FROM MeasurementImpl WHERE "
                + "depositoryId = :name AND sensorId = :sensorId)").setParameter("name", getId())
        .setParameter("sensorId", sensorId).list();
    if (result.size() > 0) {
      MeasurementImpl meas = result.get(0);
      value = new MeasuredValue(sensorId, meas.getValue(), type);
      value.setDate(meas.getDate());
    }

    session.close();
    return value;
  }

  /**
   * @param sensorId
   *          The id of the Sensor making the measurements.
   * @return The latest measurement Value
   * @throws NoMeasurementException
   *           If there aren't any measurements around the time.
   */
  @Override
  public MeasuredValue getLatestMeasuredValue(String sensorId) throws NoMeasurementException {
    MeasuredValue value = null;
    Session session = Manager.getFactory(serverProperties).openSession();
    MeasurementType type = getMeasurementType();

    @SuppressWarnings("unchecked")
    List<MeasurementImpl> result = (List<MeasurementImpl>) session
        .createQuery(
            "FROM MeasurementImpl WHERE depositoryId = :name AND sensorId = :sensor "
                + "AND date IN (SELECT max(date) FROM MeasurementImpl WHERE "
                + "depositoryId = :name AND sensorId = :sensor)").setParameter("name", getId())
        .setParameter("sensor", sensorId).list();
    if (result.size() > 0) {
      MeasurementImpl meas = result.get(0);
      value = new MeasuredValue(sensorId, meas.getValue(), type);
      value.setDate(meas.getDate());
    }

    session.close();
    return value;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.datamodel.Depository#getMeasurement(java.lang.String)
   */
  @Override
  public Measurement getMeasurement(String measId) {
    Measurement ret = null;
    Session session = Manager.getFactory(serverProperties).openSession();
    session.beginTransaction();
    @SuppressWarnings("unchecked")
    List<MeasurementImpl> measurements = (List<MeasurementImpl>) session.createQuery(
        "FROM MeasurementImpl").list();
    for (MeasurementImpl meas : measurements) {
      if (meas.getSlug().equals(measId)) {
        ret = meas;
      }
    }
    session.getTransaction().commit();
    session.close();
    return ret;
  }

  /**
   * @param session
   *          The session with an open transaction.
   * @param sensorId
   *          The id of the Sensor.
   * @return A List of the Measurements made by the sensor.
   */
  public List<Measurement> getMeasurements(Session session, String sensorId) {
    List<Measurement> ret = new ArrayList<Measurement>();
    @SuppressWarnings("unchecked")
    List<MeasurementImpl> measurements = (List<MeasurementImpl>) session
        .createQuery("FROM MeasurementImpl WHERE depositoryId = :depository")
        .setParameter("depository", getId()).list();
    for (MeasurementImpl meas : measurements) {
      if (meas.getSensorId().equals(sensorId)) {
        ret.add(meas);
      }
    }
    return ret;

  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.datamodel.Depository#getMeasurements(java.lang.String)
   */
  @Override
  public List<Measurement> getMeasurements(String sensorId) {
    Session session = Manager.getFactory(serverProperties).openSession();
    session.beginTransaction();
    List<Measurement> ret = getMeasurements(session, sensorId);
    session.getTransaction().commit();
    session.close();
    return ret;

  }

  /**
   * @param sensorId
   *          The Sensor.
   * @param start
   *          The start of the interval.
   * @param end
   *          The end of the interval.
   * @return A list of the measurements in the interval.
   */
  @Override
  public List<Measurement> getMeasurements(String sensorId, Date start, Date end) {
    List<Measurement> ret = new ArrayList<Measurement>();
    Session session = Manager.getFactory(serverProperties).openSession();
    session.beginTransaction();
    @SuppressWarnings("unchecked")
    List<MeasurementImpl> measurements = (List<MeasurementImpl>) session
        .createQuery(
            "FROM MeasurementImpl WHERE date >= :start AND date <= :end AND depositoryId = :depository")
        .setParameter("start", start).setParameter("end", end)
        .setParameter("depository", getId()).list();
    for (MeasurementImpl meas : measurements) {
      if (meas.getSensorId().equals(sensorId)) {
        ret.add(meas);
      }
    }
    session.getTransaction().commit();
    session.close();
    return ret;
  }

  /**
   * @return the ownerFk
   */
  public Long getOwnerFk() {
    return ownerFk;
  }

  /**
   * @return the pk
   */
  public Long getPk() {
    return pk;
  }

  /**
   * @param sensorId
   *          The id of the Sensor making the measurements.
   * @param timestamp
   *          The time of the value.
   * @return The Value 'measured' at the given time, most likely an interpolated
   *         value.
   * @throws NoMeasurementException
   *           If there aren't any measurements around the time.
   */
  @Override
  public Double getValue(String sensorId, Date timestamp) throws NoMeasurementException {
    Double ret = null;
    Session session = Manager.getFactory(serverProperties).openSession();
    session.beginTransaction();

    @SuppressWarnings("unchecked")
    List<MeasurementImpl> result = (List<MeasurementImpl>) session
        .createQuery(
            "FROM MeasurementImpl WHERE date = :time AND measurementType = :measType"
                + " AND depositoryId = :name").setParameter("time", timestamp)
        .setParameter("measType", getMeasurementType().getUnits()).setParameter("name", getId())
        .list();
    if (result.size() > 0) {
      for (MeasurementImpl meas : result) {
        if (meas.getSensorId().equals(sensorId)) {
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
          .setParameter("measType", getMeasurementType().getUnits())
          .setParameter("name", getId()).list();
      @SuppressWarnings("unchecked")
      List<MeasurementImpl> after = (List<MeasurementImpl>) session
          .createQuery(
              "FROM MeasurementImpl WHERE date >= :time AND measurementType = :measType"
                  + " AND depositoryId = :name").setParameter("time", timestamp)
          .setParameter("measType", getMeasurementType().getUnits())
          .setParameter("name", getId()).list();
      MeasurementImpl justBefore = null;
      for (MeasurementImpl b : before) {
        if (b.getSensorId().equals(sensorId)) {
          if (justBefore == null) {
            justBefore = b;
          }
          else if (b.getDate().compareTo(justBefore.getDate()) > 0) {
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
        if (a.getSensorId().equals(sensorId)) {
          if (justAfter == null) {
            justAfter = a;
          }
          else if (a.getDate().compareTo(justBefore.getDate()) > 0) {
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
      Long t1 = justBefore.getDate().getTime();
      Long t2 = justAfter.getDate().getTime();
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
   * @param sensorId
   *          The id of the Sensor making the measurements.
   * @param start
   *          The start of the period.
   * @param end
   *          The end of the period.
   * @return The value measured the difference between the end value and the
   *         start value.
   * @throws NoMeasurementException
   *           if there are no measurements around the start or end time.
   */
  @Override
  public Double getValue(String sensorId, Date start, Date end) throws NoMeasurementException {
    Double endVal = getValue(sensorId, end);
    Double startVal = getValue(sensorId, start);
    if (endVal != null && startVal != null) {
      return endVal - startVal;
    }
    return null;
  }

  /**
   * @param sensorId
   *          The Sensor making the measurements.
   * @param start
   *          The start of the interval.
   * @param end
   *          The end of the interval
   * @param gapSeconds
   *          The maximum number of seconds that measurements need to be within
   *          the start and end.
   * @return The value measured the difference between the end value and the
   *         start value.
   * @throws NoMeasurementException
   *           if there are no measurements around the start or end time.
   * @throws MeasurementGapException
   *           if the measurements around start or end are too far apart.
   */
  @Override
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
   * @param sensorId
   *          The Sensor making the measurements.
   * @param timestamp
   *          The time of the value.
   * @param gapSeconds
   *          The maximum number of seconds that measurements need to be within
   *          the start and end.
   * @return The Value 'measured' at the given time, most likely an interpolated
   *         value.
   * @throws NoMeasurementException
   *           If there aren't any measurements around the time.
   * @throws MeasurementGapException
   *           if the measurements around timestamp are too far apart.
   */
  @Override
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
        .setParameter("measType", getMeasurementType().getUnits()).setParameter("name", getId())
        .list();
    if (result.size() > 0) {
      for (MeasurementImpl meas : result) {
        if (meas.getSensorId().equals(sensorId)) {
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
          .setParameter("measType", getMeasurementType().getUnits())
          .setParameter("name", getId()).list();
      @SuppressWarnings("unchecked")
      List<MeasurementImpl> after = (List<MeasurementImpl>) session
          .createQuery(
              "FROM MeasurementImpl WHERE date >= :time AND measurementType = :measType"
                  + " AND depositoryId = :name").setParameter("time", timestamp)
          .setParameter("measType", getMeasurementType().getUnits())
          .setParameter("name", getId()).list();
      MeasurementImpl justBefore = null;
      for (MeasurementImpl b : before) {
        if (b.getSensorId().equals(sensorId)) {
          if (justBefore == null) {
            justBefore = b;
          }
          else if (b.getDate().compareTo(justBefore.getDate()) > 0) {
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
        if (a.getSensorId().equals(sensorId)) {
          if (justAfter == null) {
            justAfter = a;
          }
          else if (a.getDate().compareTo(justBefore.getDate()) > 0) {
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
      Long t1 = justBefore.getDate().getTime();
      Long t2 = justAfter.getDate().getTime();
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
    int result = super.hashCode();
    result = prime * result + ((pk == null) ? 0 : pk.hashCode());
    result = prime * result + ((ownerFk == null) ? 0 : ownerFk.hashCode());
    return result;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.datamodel.Depository#getSensors()
   */
  @Override
  public List<String> listSensors() {
    Session session = Manager.getFactory(serverProperties).openSession();
    session.beginTransaction();
    @SuppressWarnings("unchecked")
    List<MeasurementImpl> result = (List<MeasurementImpl>) session
        .createQuery("FROM MeasurementImpl WHERE depositoryId = :name")
        .setParameter("name", getId()).list();
    ArrayList<String> sensorIds = new ArrayList<String>();
    for (Measurement meas : result) {
      if (!sensorIds.contains(meas.getSensorId())) {
        sensorIds.add(meas.getSensorId());
      }
    }
    session.getTransaction().commit();
    session.close();
    return sensorIds;
  }

  /**
   * @param session
   *          The Session with an open transaction.
   * @return A List of Sensors contributing measurements to this depository.
   */
  public List<String> listSensors(Session session) {
    @SuppressWarnings("unchecked")
    List<MeasurementImpl> result = (List<MeasurementImpl>) session
        .createQuery("FROM MeasurementImpl WHERE depositoryId = :name")
        .setParameter("name", getId()).list();
    ArrayList<String> sensors = new ArrayList<String>();
    for (Measurement meas : result) {
      if (!sensors.contains(meas.getSensorId())) {
        sensors.add(meas.getSensorId());
      }
    }
    return sensors;
  }

  /**
   * @param meas
   *          The measurement to store.
   * @throws MeasurementTypeException
   *           if the type of the measurement doesn't match the Depository
   *           measurement type.
   */
  @Override
  public void putMeasurement(Measurement meas) throws MeasurementTypeException {
    if (!meas.getMeasurementType().equals(getMeasurementType().getUnits())) {
      throw new MeasurementTypeException("Measurement's type " + meas.getMeasurementType()
          + " does not match " + getMeasurementType());
    }

    Session session = Manager.getFactory(serverProperties).openSession();
    session.beginTransaction();
    // check the sensor
    MeasurementImpl mImpl = new MeasurementImpl(meas);
    mImpl.setDepositoryId(getId());
    session.saveOrUpdate(mImpl);
    session.getTransaction().commit();
    session.close();
  }

  /**
   * @return the serverProperties
   */
  public ServerProperties serverProperties() {
    return serverProperties;
  }

  /**
   * @param serverProperties
   *          the serverProperties to set
   */
  public void serverProperties(ServerProperties serverProperties) {
    this.serverProperties = serverProperties;
  }

  /**
   * @param ownerFk
   *          the ownerFk to set
   */
  public void setOwnerFk(Long ownerFk) {
    this.ownerFk = ownerFk;
  }

  /**
   * @param pk
   *          the pk to set
   */
  public void setPk(Long pk) {
    this.pk = pk;
  }

}
