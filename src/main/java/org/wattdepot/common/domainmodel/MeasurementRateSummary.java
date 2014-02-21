/**
 * MeasurementRateSummary.java This file is part of WattDepot.
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

/**
 * MeasurementRateSummary a summary of the Measurements created by a given
 * Sensor in a Depository. This class breaks down the time intervals to the last
 * minute, last fifteen minutes, last hour and last day.
 * 
 * @author Cam Moore
 * 
 */
public class MeasurementRateSummary {
  /** The id of the sensor making the measurements. */
  private String sensorId;
  /** The id of the depository storing the measurements. */
  private String depositoryId;
  /** The time of the summary. */
  private Date timestamp;
  /** The latest value. */
  private Double latestValue;
  /** The type of the measurements. */
  private MeasurementType type;
  /** Number of measurements in the last minute. */
  private Long oneMinuteCount;
  /** The rate of measurements per second in the last minute. */
  private Double oneMinuteRate;
  /** Total number of measurements. */
  private Long totalCount;

  /**
   * Default constructor.
   */
  public MeasurementRateSummary() {

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
   * @return the timestamp
   */
  public Date getTimestamp() {
    return new Date(timestamp.getTime());
  }

  /**
   * @param timestamp the timestamp to set
   */
  public void setTimestamp(Date timestamp) {
    this.timestamp = new Date(timestamp.getTime());
  }

  /**
   * @return the latestValue
   */
  public Double getLatestValue() {
    return latestValue;
  }

  /**
   * @param latestValue the latestValue to set
   */
  public void setLatestValue(Double latestValue) {
    this.latestValue = latestValue;
  }

  /**
   * @return the type
   */
  public MeasurementType getType() {
    return type;
  }

  /**
   * @param type the type to set
   */
  public void setType(MeasurementType type) {
    this.type = type;
  }

  /**
   * @return the oneMinuteCount
   */
  public Long getOneMinuteCount() {
    return oneMinuteCount;
  }

  /**
   * @param oneMinuteCount the oneMinuteCount to set
   */
  public void setOneMinuteCount(Long oneMinuteCount) {
    this.oneMinuteCount = oneMinuteCount;
  }

  /**
   * @return the oneMinuteRate
   */
  public Double getOneMinuteRate() {
    return oneMinuteRate;
  }

  /**
   * @param oneMinuteRate the oneMinuteRate to set
   */
  public void setOneMinuteRate(Double oneMinuteRate) {
    this.oneMinuteRate = oneMinuteRate;
  }

  /**
   * @return the totalCount
   */
  public Long getTotalCount() {
    return totalCount;
  }

  /**
   * @param totalCount the totalCount to set
   */
  public void setTotalCount(Long totalCount) {
    this.totalCount = totalCount;
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
    result = prime * result + ((oneMinuteCount == null) ? 0 : oneMinuteCount.hashCode());
    result = prime * result + ((oneMinuteRate == null) ? 0 : oneMinuteRate.hashCode());
    result = prime * result + ((sensorId == null) ? 0 : sensorId.hashCode());
    result = prime * result + ((timestamp == null) ? 0 : timestamp.hashCode());
    result = prime * result + ((latestValue == null) ? 0 : latestValue.hashCode());
    result = prime * result + ((totalCount == null) ? 0 : totalCount.hashCode());
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
    MeasurementRateSummary other = (MeasurementRateSummary) obj;
    if (latestValue == null) {
      if (other.latestValue != null) {
        return false;
      }
    }
    else if (!latestValue.equals(other.latestValue)) {
      return false;
    }
    if (depositoryId == null) {
      if (other.depositoryId != null) {
        return false;
      }
    }
    else if (!depositoryId.equals(other.depositoryId)) {
      return false;
    }
    if (oneMinuteCount == null) {
      if (other.oneMinuteCount != null) {
        return false;
      }
    }
    else if (!oneMinuteCount.equals(other.oneMinuteCount)) {
      return false;
    }
    if (oneMinuteRate == null) {
      if (other.oneMinuteRate != null) {
        return false;
      }
    }
    else if (!oneMinuteRate.equals(other.oneMinuteRate)) {
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
    if (totalCount == null) {
      if (other.totalCount != null) {
        return false;
      }
    }
    else if (!totalCount.equals(other.totalCount)) {
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
    return "MeasurementRateSummary [sensorId=" + sensorId + ", depositoryId=" + depositoryId
        + ", timestamp=" + timestamp + ", latest value =" + latestValue + ", oneMinuteCount="
        + oneMinuteCount + ", oneMinuteRate=" + oneMinuteRate + ", totalCount=" + totalCount + "]";
  }

}
