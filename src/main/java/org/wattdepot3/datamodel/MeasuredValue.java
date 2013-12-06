/**
 * MeasuredValue.java This file is part of WattDepot 3.
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

/**
 * MeasuredValue - represents a measured value. Has sensor id, value and
 * measurement type.
 * 
 * @author Cam Moore
 * 
 */
public class MeasuredValue {
  /** The id of the sensor making the measurement. */
  private String sensorId;
  /** The value of the measurement. */
  private Double value;
  /** The type of the measurement. */
  private MeasurementType measurementType;
  /** The time of the measured value. */
  private Date date;

  /**
   * Hide the default constructor.
   */
  protected MeasuredValue() {

  }

  /**
   * Creates a new MeasuredValue.
   * 
   * @param sensorId
   *          The id of the sensor that made the measurement.
   * @param value
   *          The value of the measurement.
   * @param measurementType
   *          The type of the measurement.
   */
  public MeasuredValue(String sensorId, Double value, MeasurementType measurementType) {
    this.sensorId = sensorId;
    this.value = value;
    this.measurementType = measurementType;
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

  /**
   * @return the measurementType
   */
  public MeasurementType getMeasurementType() {
    return measurementType;
  }

  /**
   * @return the date
   */
  public Date getDate() {
    return new Date(date.getTime());
  }

  /**
   * @param date the date to set
   */
  public void setDate(Date date) {
    this.date = new Date(date.getTime());
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

  /**
   * @param measurementType the measurementType to set
   */
  public void setMeasurementType(MeasurementType measurementType) {
    this.measurementType = measurementType;
  }

}
