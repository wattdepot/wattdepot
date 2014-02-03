/**
 * SensorMeasurementSummary.java This file is part of WattDepot.
 *
 * Copyright (C) 2014  Cam Moore
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

/**
 * SensorMeasurementSummary a summary of the Measurements created by a given
 * Sensor in a depository. The summary has a start date, end date and number of
 * Measurements in the period.
 * 
 * @author Cam Moore
 * 
 */
public class SensorMeasurementSummary {
  /** The id of the sensor making the measurements. */
  private String sensorId;
  /** The id of the depository storing the measurements. */
  private String depositoryId;
  /** The number of measurements in the interval. */
  private Integer numMeasurements;
  /** The start of the interval. */
  private Date start;
  /** The end of the interval. */
  private Date end;

  /**
   * Default constructor.
   */
  public SensorMeasurementSummary() {

  }

  /**
   * @param sensorId2 The sensor id.
   * @param depositoryId2 The depository id.
   * @param start The start of the period.
   * @param end The endo of the period.
   * @param size The number of measurements in the period.
   */
  public SensorMeasurementSummary(String sensorId2, String depositoryId2, Date start, Date end,
      int size) {
    this.sensorId = sensorId2;
    this.depositoryId = depositoryId2;
    this.start = new Date(start.getTime());
    this.end = new Date(end.getTime());
    this.numMeasurements = size;
  }

  /**
   * @return the sensorId
   */
  public String getSensorId() {
    return sensorId;
  }

  /**
   * @param sensorId the sensorId to set
   */
  public void setSensorId(String sensorId) {
    this.sensorId = sensorId;
  }

  /**
   * @return the depositoryId
   */
  public String getDepositoryId() {
    return depositoryId;
  }

  /**
   * @param depositoryId the depositoryId to set
   */
  public void setDepositoryId(String depositoryId) {
    this.depositoryId = depositoryId;
  }

  /**
   * @return the numMeasurements
   */
  public Integer getNumMeasurements() {
    return numMeasurements;
  }

  /**
   * @param numMeasurements the numMeasurements to set
   */
  public void setNumMeasurements(Integer numMeasurements) {
    this.numMeasurements = numMeasurements;
  }

  /**
   * @return the start
   */
  public Date getStart() {
    return new Date(start.getTime());
  }

  /**
   * @param start the start to set
   */
  public void setStart(Date start) {
    this.start = new Date(start.getTime());
  }

  /**
   * @return the end
   */
  public Date getEnd() {
    return new Date(end.getTime());
  }

  /**
   * @param end the end to set
   */
  public void setEnd(Date end) {
    this.end = new Date(end.getTime());
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
    result = prime * result + ((depositoryId == null) ? 0 : depositoryId.hashCode());
    result = prime * result + ((end == null) ? 0 : end.hashCode());
    result = prime * result + ((numMeasurements == null) ? 0 : numMeasurements.hashCode());
    result = prime * result + ((sensorId == null) ? 0 : sensorId.hashCode());
    result = prime * result + ((start == null) ? 0 : start.hashCode());
    return result;
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
    SensorMeasurementSummary other = (SensorMeasurementSummary) obj;
    if (depositoryId == null) {
      if (other.depositoryId != null) {
        return false;
      }
    }
    else if (!depositoryId.equals(other.depositoryId)) {
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
    if (numMeasurements == null) {
      if (other.numMeasurements != null) {
        return false;
      }
    }
    else if (!numMeasurements.equals(other.numMeasurements)) {
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
    if (start == null) {
      if (other.start != null) {
        return false;
      }
    }
    else if (!start.equals(other.start)) {
      return false;
    }
    return true;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "SensorMeasurementSummary [sensorId=" + sensorId + ", depositoryId=" + depositoryId
        + ", numMeasurements=" + numMeasurements + ", start=" + start + ", end=" + end + "]";
  }

}
