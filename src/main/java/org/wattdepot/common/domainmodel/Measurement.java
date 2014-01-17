/**
 * Measurement.java This file is part of WattDepot.
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
package org.wattdepot.common.domainmodel;

import java.util.Date;

import javax.measure.unit.Unit;
import javax.xml.datatype.DatatypeConfigurationException;

import org.jscience.physics.amount.Amount;
import org.wattdepot.common.exception.BadSlugException;
import org.wattdepot.common.util.DateConvert;
import org.wattdepot.common.util.Slug;

//import javax.measure.unit.Unit;

/**
 * Measurement - represents a measurement from a sensor.
 * 
 * @author Cam Moore
 * 
 */
public class Measurement {
  private String id;
  private String sensorId;
  private Date timestamp;
  private Double value;
  private Amount<?> amount;
  private Unit<?> units;

  /**
   * Hide the default constructor.
   */
  public Measurement() {

  }

  /**
   * @param sensorId The id of the sensor that made the measurement.
   * @param timestamp The time of the measurement.
   * @param value The value measured.
   * @param units The type of the measurement.
   */
  public Measurement(String sensorId, Date timestamp, Double value, Unit<?> units) {
    // Can a sensor create two measurements at the same time with the same type?
    this.id = Slug.slugify(sensorId + timestamp.getTime() + units);
    this.sensorId = sensorId;
    this.timestamp = new Date(timestamp.getTime());
    this.amount = Amount.valueOf(value, units);
    this.value = amount.getEstimatedValue();
    this.units = units;
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
    if (!getClass().isAssignableFrom(obj.getClass())
        && !obj.getClass().isAssignableFrom(getClass()) && getClass() != obj.getClass()) {
      return false;
    }
    Measurement other = (Measurement) obj;
    if (units == null) {
      if (other.units != null) {
        return false;
      }
    }
    else if (!units.equals(other.units)) {
      return false;
    }
    if (sensorId == null) {
      if (other.sensorId != null) {
        return false;
      }
    }
    else if (!sensorId.equals(other.sensorId)) {
      return false;
    }
    if (timestamp == null) {
      if (other.timestamp != null) {
        return false;
      }
    }
    else if (!timestamp.equals(other.timestamp)) {
      return false;
    }
    if (value == null) {
      if (other.value != null) {
        return false;
      }
    }
    else if (!value.equals(other.value)) {
      return false;
    }
    return true;
  }

  /**
   * @param value a MeasuredValue
   * @return true if this Measurement has the same sensorId, time,
   *         MeasurementType, and value as the MeasuredValue.
   */
  public boolean equivalent(InterpolatedValue value) {
    if (!sensorId.equals(value.getSensorId())) {
      return false;
    }
    if (!timestamp.equals(value.getDate())) {
      return false;
    }
    if (Math.abs(this.value - value.getValue()) > 0.0001) {
      return false;
    }
    if (!getMeasurementType().equals(value.getMeasurementType().getUnits())) {
      return false;
    }

    return true;
  }

  /**
   * @return the timestamp
   */
  public Date getDate() {
    return new Date(timestamp.getTime());
  }

  /**
   * @return the measurementType
   */
  public String getMeasurementType() {
    return amount.getUnit().toString();
  }

  /**
   * @return the id of the sensor
   */
  public String getSensorId() {
    return sensorId;
  }

  /**
   * @return the id
   */
  public String getId() {
    return id;
  }

  /**
   * @return the value
   */
  public Double getValue() {
    return value;
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
    result = prime * result + ((units == null) ? 0 : units.hashCode());
    result = prime * result + ((sensorId == null) ? 0 : sensorId.hashCode());
    result = prime * result + ((timestamp == null) ? 0 : timestamp.hashCode());
    result = prime * result + ((value == null) ? 0 : value.hashCode());
    return result;
  }

  /**
   * @param timestamp the timestamp to set
   */
  public void setDate(Date timestamp) {
    this.timestamp = new Date(timestamp.getTime());
  }

  /**
   * The String measurementType must be a javax.measure.unit.Unit toString()
   * value.
   * 
   * @see http://jscience.org/api/javax/measure/unit/NonSI.html or
   *      http://jscience.org/api/javax/measure/unit/SI.html
   * 
   * @param measurementType the measurementType to set
   */
  public void setMeasurementType(String measurementType) {
    this.units = Unit.valueOf(measurementType);
    this.amount = Amount.valueOf(this.value, this.units);
  }

  /**
   * @param sensorId the id of the sensor to set
   */
  public void setSensorId(String sensorId) {
    this.sensorId = sensorId;
  }

  /**
   * @param id the id to set
   * @exception BadSlugException if the slug is not valid.
   */
  public void setId(String id) throws BadSlugException {
    if (Slug.validateSlug(id)) {
      this.id = id;
    }
    else {
      throw new BadSlugException(id + " is not a valid slug.");
    }
  }

  /**
   * @param value the value to set
   */
  public void setValue(Double value) {
    this.value = value;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    try {
      return "Measurement [id= " + id + ", sensorId=" + sensorId + ", timestamp="
          + DateConvert.convertDate(timestamp) + ", value=" + value + ", units=" + units + "]";
    }
    catch (DatatypeConfigurationException e) {
      // shouldn't happen
      e.printStackTrace();
    }
    return "Measurement [id= " + id + ", sensorId=" + sensorId + ", timestamp=" + timestamp
        + ", value=" + value + ", units=" + units + "]";
  }

  /**
   * @return The units for this measurement.
   */
  public Unit<?> units() {
    return this.units;
  }

  // /**
  // * @return The Units for this measurement.
  // */
  // public Unit<?> getType() {
  // return amount.getUnit();
  // }
}
