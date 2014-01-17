/**
 * IntervalValue.java This file is part of WattDepot.
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
 * IntervalValue - represents an interval value derived by subtracting two
 * InterpolatedValues. Has sensor id, value and measurement type.
 * 
 * @author Cam Moore
 * 
 */
public class IntervalValue {
  /** The id of the sensor making the measurement. */
  private String sensorId;
  /** The value of the measurement. */
  private Double value;
  /** The type of the measurement. */
  private MeasurementType measurementType;
  /** The start of the interval. */
  private Date start;
  /** The end of the interval. */
  private Date end;

  /**
   * Hide the default constructor.
   */
  protected IntervalValue() {

  }

  /**
   * Creates a new IntervalValue.
   * 
   * @param sensorId The id of the sensor that made the measurement.
   * @param value The value of the measurement.
   * @param measurementType The type of the measurement.
   */
  public IntervalValue(String sensorId, Double value, MeasurementType measurementType, Date start,
      Date end) {
    this.sensorId = sensorId;
    this.value = value;
    this.measurementType = measurementType;
    this.start = start;
    this.end = end;
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
    IntervalValue other = (IntervalValue) obj;
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
   * @return the end
   */
  public Date getEnd() {
    return end;
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
   * @return the start
   */
  public Date getStart() {
    return start;
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
    result = prime * result + ((end == null) ? 0 : end.hashCode());
    result = prime * result + ((start == null) ? 0 : start.hashCode());
    result = prime * result + ((measurementType == null) ? 0 : measurementType.hashCode());
    result = prime * result + ((sensorId == null) ? 0 : sensorId.hashCode());
    result = prime * result + ((value == null) ? 0 : value.hashCode());
    return result;
  }

  /**
   * @param end the end to set
   */
  public void setEnd(Date end) {
    this.end = end;
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
   * @param start the start to set
   */
  public void setStart(Date start) {
    this.start = start;
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
      return "IntervalValue [sensorId=" + sensorId + ", value=" + value + ", measurementType="
          + measurementType + ", start=" + DateConvert.convertDate(start) + ", end="
          + DateConvert.convertDate(end) + "]";
    }
    catch (DatatypeConfigurationException e) {
      // shouldn't happen
      e.printStackTrace();
    }
    return "IntervalValue [sensorId=" + sensorId + ", value=" + value + ", measurementType="
        + measurementType + ", start=" + start + ", end=" + end + "]";
  }

}
