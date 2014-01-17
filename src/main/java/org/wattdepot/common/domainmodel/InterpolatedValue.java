/**
 * InterpolatedValue.java This file is part of WattDepot.
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

import javax.xml.datatype.DatatypeConfigurationException;

import org.wattdepot.common.util.DateConvert;

/**
 * InterpolatedValue - represents an interpolated value derived from
 * measurements. Has sensor id, value and measurement type.
 * 
 * @author Cam Moore
 * 
 */
public class InterpolatedValue {
  /** The id of the sensor making the measurement. */
  private String sensorId;
  /** The value of the measurement. */
  private Double value;
  /** The type of the measurement. */
  private MeasurementType measurementType;
  /** The time of the interpolated value. */
  private Date date;

  /**
   * Hide the default constructor.
   */
  protected InterpolatedValue() {

  }

  /**
   * Creates a new InterpolatedValue.
   * 
   * @param sensorId The id of the sensor that made the measurement.
   * @param value The value of the measurement.
   * @param measurementType The type of the measurement.
   * @param date the time of the value.
   */
  public InterpolatedValue(String sensorId, Double value, MeasurementType measurementType, Date date) {
    this.sensorId = sensorId;
    this.value = value;
    this.measurementType = measurementType;
    this.date = date;
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
    InterpolatedValue other = (InterpolatedValue) obj;
    if (date == null) {
      if (other.date != null) {
        return false;
      }
    }
    else if (!date.equals(other.date)) {
      return false;
    }
    if (measurementType == null) {
      if (other.measurementType != null) {
        return false;
      }
    }
    else if (!measurementType.equals(other.measurementType)) {
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
   * @param meas a Measurement
   * @return true if this InterpolatedValue has the same sensorId, time,
   *         MeasurementType, and value as the Measurement.
   */
  public boolean equivalent(Measurement meas) {
    if (!sensorId.equals(meas.getSensorId())) {
      return false;
    }
    if (!date.equals(meas.getDate())) {
      return false;
    }
    if (!measurementType.getUnits().equals(meas.getMeasurementType())) {
      return false;
    }
    if (Math.abs(this.value - meas.getValue()) > 0.0001) {
      return false;
    }
    return true;
  }

  /**
   * @return the date
   */
  public Date getDate() {
    return new Date(date.getTime());
  }

  /**
   * @return the measurementType
   */
  public MeasurementType getMeasurementType() {
    return measurementType;
  }

  /**
   * @return the sensorId
   */
  public String getSensorId() {
    return sensorId;
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
    result = prime * result + ((date == null) ? 0 : date.hashCode());
    result = prime * result + ((measurementType == null) ? 0 : measurementType.hashCode());
    result = prime * result + ((sensorId == null) ? 0 : sensorId.hashCode());
    result = prime * result + ((value == null) ? 0 : value.hashCode());
    return result;
  }

  /**
   * @param date the date to set
   */
  public void setDate(Date date) {
    this.date = new Date(date.getTime());
  }

  /**
   * @param measurementType the measurementType to set
   */
  public void setMeasurementType(MeasurementType measurementType) {
    this.measurementType = measurementType;
  }

  /**
   * @param sensorId the sensorId to set
   */
  public void setSensorId(String sensorId) {
    this.sensorId = sensorId;
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
      return "InterpolatedValue [sensorId=" + sensorId + ", value=" + value + ", measurementType="
          + measurementType + ", date=" + DateConvert.convertDate(date) + "]";
    }
    catch (DatatypeConfigurationException e) {
      // shouldn't happen
      e.printStackTrace();
    }
    return "InterpolatedValue [sensorId=" + sensorId + ", value=" + value + ", measurementType="
        + measurementType + ", date=" + date + "]";
  }

}
