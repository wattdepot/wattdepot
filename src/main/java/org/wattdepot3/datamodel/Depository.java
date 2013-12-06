/**
 * Depository.java This file is part of WattDepot 3.
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
package org.wattdepot3.datamodel;

import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.wattdepot3.exception.MeasurementGapException;
import org.wattdepot3.exception.MeasurementTypeException;
import org.wattdepot3.exception.NoMeasurementException;
import org.wattdepot3.util.Slug;

/**
 * Depository - Stores measurements from Sensors of the matching measurement
 * type.
 * 
 * @author Cam Moore
 * 
 */
public class Depository {
  /** The unique id for the Depository. */
  private String id;
  /** Name of the Depository. */
  protected String name;
  /** The slug used in URIs. */
  protected String slug;
  /** Type of measurements stored in the Depository. */
  protected MeasurementType measurementType;
  /** The owner of this depository. */
  protected UserGroup owner;

  /**
   * The default constructor.
   */
  public Depository() {

  }

  /**
   * Create a new Depository.
   * 
   * @param name
   *          The name of the Depository.
   * @param measurementType
   *          The type of the measurements this Depository accepts.
   * @param owner
   *          the owner of the location.
   */
  public Depository(String name, MeasurementType measurementType, UserGroup owner) {
    this.id = Slug.slugify(name);
    this.name = name;
    this.slug = Slug.slugify(name);
    this.measurementType = measurementType;
    this.owner = owner;
  }

  /**
   * @param meas
   *          The measurement to delete.
   */
  public void deleteMeasurement(Measurement meas) {
    throw new RuntimeException("Not implemented.");
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
    Depository other = (Depository) obj;
    if (measurementType == null) {
      if (other.measurementType != null) {
        return false;
      }
    }
    else if (!measurementType.equals(other.measurementType)) {
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
    return true;
  }

  /**
   * @return the id
   */
  public String getId() {
    return id;
  }

  /**
   * @param sensor
   *          the Sensor.
   * @return A list of all the measurements made by the Sensor.
   */
  public List<Measurement> getMeasurements(Sensor sensor) {
    throw new RuntimeException("Not implemented.");
  }

  /**
   * @param sensor
   *          The Sensor.
   * @param start
   *          The start of the interval.
   * @param end
   *          The end of the interval.
   * @return A list of the measurements in the interval.
   */
  public List<Measurement> getMeasurements(Sensor sensor, Date start, Date end) {
    throw new RuntimeException("Not implemented.");
  }

  /**
   * @param session
   *          A Session with an open transaction.
   * @param sensor
   *          the Sensor.
   * @return A list of all the measurements made by the Sensor.
   */
  public List<Measurement> getMeasurements(Session session, Sensor sensor) {
    throw new RuntimeException("Not implemented.");
  }

  /**
   * @return the measurementType
   */
  public MeasurementType getMeasurementType() {
    return measurementType;
  }

  /**
   * @return the name.
   */
  public String getName() {
    return name;
  }

  /**
   * @return the owner
   */
  public UserGroup getOwner() {
    return owner;
  }

  /**
   * @return the slug
   */
  public String getSlug() {
    return slug;
  }

  /**
   * @param sensor
   *          The Sensor making the measurements.
   * @param timestamp
   *          The time of the value.
   * @return The Value 'measured' at the given time, most likely an interpolated
   *         value.
   * @throws NoMeasurementException
   *           If there aren't any measurements around the time.
   */
  public Double getValue(Sensor sensor, Date timestamp) throws NoMeasurementException {
    throw new RuntimeException("Not implemented.");
  }

  /**
   * @param sensor
   *          The Sensor making the measurements.
   * @param start
   *          The start of the period.
   * @param end
   *          The end of the period.
   * @return The value measured the difference between the end value and the
   *         start value.
   * @throws NoMeasurementException
   *           if there are no measurements around the start or end time.
   */
  public Double getValue(Sensor sensor, Date start, Date end) throws NoMeasurementException {
    throw new RuntimeException("Not implemented.");
  }

  /**
   * @param sensor
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
  public Double getValue(Sensor sensor, Date start, Date end, Long gapSeconds)
      throws NoMeasurementException, MeasurementGapException {
    throw new RuntimeException("Not implemented.");
  }

  /**
   * @param sensor
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
  public Double getValue(Sensor sensor, Date timestamp, Long gapSeconds)
      throws NoMeasurementException, MeasurementGapException {
    throw new RuntimeException("Not implemented.");
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
    result = prime * result + ((measurementType == null) ? 0 : measurementType.hashCode());
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    return result;
  }

  /**
   * Determines if the given group is the owner of this location.
   * 
   * @param group
   *          the UserGroup to check.
   * @return True if the group owns the Location or the group is the
   *         ADMIN_GROUP.
   */
  public boolean isOwner(UserGroup group) {
    if (owner != null) {
      if (owner.equals(group) || group.equals(UserGroup.ADMIN_GROUP)) {
        return true;
      }
    }
    return false;
  }

  /**
   * @return A list of the Sensors contributing Measurements to this depository.
   */
  public List<Sensor> listSensors() {
    throw new RuntimeException("Not implemented.");
  }

  /**
   * @param session
   *          A Session with an open transaction.
   * @return A List of Sensors contributing measurements to this Depository.
   */
  public List<Sensor> listSensors(Session session) {
    throw new RuntimeException("Not implemented.");
  }

  /**
   * @param meas
   *          The measurement to store.
   * @throws MeasurementTypeException
   *           if the type of the measurement doesn't match the Depository
   *           measurement type.
   */
  public void putMeasurement(Measurement meas) throws MeasurementTypeException {
    throw new RuntimeException("Not implemented.");
  }

  /**
   * @param id the id to set
   */
  public void setId(String id) {
    this.id = id;
  }

  /**
   * @param measurementType
   *          the measurementType to set
   */
  public void setMeasurementType(MeasurementType measurementType) {
    this.measurementType = measurementType;
  }

  /**
   * @param name
   *          the name to set.
   */
  public void setName(String name) {
    this.name = name;
    if (this.id == null) {
      this.id = Slug.slugify(name);
    }
  }

  /**
   * @param owner
   *          the owner to set
   */
  public void setOwner(UserGroup owner) {
    this.owner = owner;
  }

  /**
   * @param slug
   *          the slug to set
   */
  public void setSlug(String slug) {
    this.slug = slug;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "Depository [name=" + name + ", measurementType=" + measurementType + "]";
  }

}
