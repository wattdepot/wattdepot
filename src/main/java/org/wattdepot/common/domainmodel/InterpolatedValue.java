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

import org.wattdepot.common.util.DateConvert;

import javax.xml.datatype.DatatypeConfigurationException;
import java.util.ArrayList;
import java.util.Date;

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
  /** The start time of the interpolated value. */
  private Date start;
  /** The end time of the interpolated value. */
  private Date end;
  /** A list of sensorIds that contributed to this value. */
  private ArrayList<String> reportingSensors;
  /** A list of sensorIds that should have contributed to this value.  */
  private ArrayList<String> definedSensors;

  /**
   * Hide the default constructor.
   */
  protected InterpolatedValue() {
    this.reportingSensors = new ArrayList<String>();
    this.definedSensors = new ArrayList<String>();
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
    this(sensorId, value, measurementType, date, date);
    this.reportingSensors = new ArrayList<String>();
    this.definedSensors = new ArrayList<String>();
  }

  /**
   * Creates a new InterpolatedValue.
   *
   * @param sensorId The id of the sensor that made the measurement.
   * @param value The value of the measurement.
   * @param measurementType The type of the measurement.
   * @param start the start time of the value.
   * @param end the end time of the value.
   */
  public InterpolatedValue(String sensorId, Double value, MeasurementType measurementType, Date start, Date end) {
    this.sensorId = sensorId;
    this.value = value;
    this.measurementType = measurementType;
    this.start = new Date(start.getTime());
    this.end = new Date(end.getTime());
    this.reportingSensors = new ArrayList<String>();
    this.definedSensors = new ArrayList<String>();
  }

  /**
   * @return The list of defined sensorIds.
   */
  public ArrayList<String> getDefinedSensors() {
    return definedSensors;
  }

  /**
   * Sets the list of defined sensorIds.
   * @param definedSensors the new list of sensor ids.
   */
  public void setDefinedSensors(ArrayList<String> definedSensors) {
    this.definedSensors = definedSensors;
  }

  /**
   * Adds the given sensor id to the list of defined sensors.
   * @param sensorId the sensor id.
   */
  public void addDefinedSensor(String sensorId) {
    if (!definedSensors.contains(sensorId)) {
      definedSensors.add(sensorId);
    }
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
    if (start == null) {
      if (other.start != null) {
        return false;
      }
    }
    else if (!start.equals(other.start)) {
      return false;
    }
    if (end == null) {
      if (other.end != null) {
        return false;
      }
    }
    else if (!end.equals(other.end)) {
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
    if (sensorId == null) {
      if (meas.getSensorId() != null) {
        return false;
      }
    }
    else if (!sensorId.equals(meas.getSensorId())) {
      return false;
    }
    if (start == null) {
      if (meas.getDate() != null) {
        return false;
      }
    }
    else if (meas.getDate() == null || start.getTime() - meas.getDate().getTime() != 0) {
      return false;
    }
    if (measurementType == null) {
      if (meas.getMeasurementType() != null) {
        return false;
      }
    }
    else if (!measurementType.getUnits().equals(meas.getMeasurementType())) {
      return false;
    }
    if (this.value == null) {
      if (meas.getValue() != null) {
        return false;
      }
    }
    else if (meas.getValue() == null || Math.abs(this.value - meas.getValue()) > 0.0001) {
      return false;
    }
    return true;
  }

  /**
   * @return the end time for this interpolated value.
   */
  public Date getEnd() {
    return new Date(end.getTime());
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
   * @return start time of the Interpolated Value.
   */
  public Date getStart() {
    if (start != null) {
      return new Date(start.getTime());
    }
    else {
      return null;
    }
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
    result = prime * result + ((start == null) ? 0 : start.hashCode());
    result = prime * result + ((end == null) ? 0 : end.hashCode());
    result = prime * result + ((measurementType == null) ? 0 : measurementType.hashCode());
    result = prime * result + ((sensorId == null) ? 0 : sensorId.hashCode());
    result = prime * result + ((value == null) ? 0 : value.hashCode());
    return result;
  }

  /**
   * @param end end date to set.
   */
  public void setEnd(Date end) {
    this.end = new Date(end.getTime());
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
   * @param start the start date to set.
   */
  public void setStart(Date start) {
    this.start = new Date(start.getTime());
  }

  /**
   * @param value the value to set
   */
  public void setValue(Double value) {
    this.value = value;
  }

  /**
   * @return the missing sensors list.
   */
  public ArrayList<String> getReportingSensors() {
    return reportingSensors;
  }

  /**
   * Sets the missing sensors list.
   * @param reportingSensors the new list of missing sensors.
   */
  public void setReportingSensors(ArrayList<String> reportingSensors) {
    this.reportingSensors = reportingSensors;
  }

  /**
   * Ads a SensorId to the missing sensors list.
   * @param s the sensorId.
   */
  public void addReportingSensor(String s) {
    if (!reportingSensors.contains(s)) {
      reportingSensors.add(s);
    }
  }


  /**
   * Removes the sensorId from the missing sensors list.
   * @param s the sensorId.
   * @return the removed value.
   */
  public String removeMissingSensor(String s) {
    int index = reportingSensors.indexOf(s);
    if (index != -1) {
      return reportingSensors.remove(index);
    }
    return null;
  }

  /**
   * @return true if the missing sensors list is empty.
   */
  public boolean missingSensorsEmptyP() {
    return reportingSensors.isEmpty();
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
          + measurementType + ", start=" + DateConvert.convertDate(start) + ", end=" + DateConvert.convertDate(end) + "]";
    }
    catch (DatatypeConfigurationException e) {
      // shouldn't happen
      e.printStackTrace();
    }
    return "InterpolatedValue [sensorId=" + sensorId + ", value=" + value + ", measurementType="
        + measurementType + ", start=" + start + ", end= " + end + "]";
  }

}
