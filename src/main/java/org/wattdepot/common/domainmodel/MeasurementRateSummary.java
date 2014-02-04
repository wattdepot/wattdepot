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
  /** Number of measurements in the last minute. */
  private Integer oneMinuteCount;
  /** The rate of measurements per second in the last minute. */
  private Double oneMinuteRate;
  /** Number of measurements in the last fifteen minutes. */
  private Integer fifteenMinuteCount;
  /** The rate of measurements per second in the last fifteen minutes. */
  private Double fifteenMinuteRate;
  /** Number of measurements in the last hour. */
  private Integer hourCount;
  /** The rate of measurements per second in the last hour. */
  private Double hourRate;
  /** Number of measurements in the last day. */
  private Integer dayCount;
  /** The rate of measurements per second in the last day. */
  private Double dayRate;

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
   * @return the oneMinuteCount
   */
  public Integer getOneMinuteCount() {
    return oneMinuteCount;
  }

  /**
   * @param oneMinuteCount the oneMinuteCount to set
   */
  public void setOneMinuteCount(Integer oneMinuteCount) {
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
   * @return the fifteenMinuteCount
   */
  public Integer getFifteenMinuteCount() {
    return fifteenMinuteCount;
  }

  /**
   * @param fifteenMinuteCount the fifteenMinuteCount to set
   */
  public void setFifteenMinuteCount(Integer fifteenMinuteCount) {
    this.fifteenMinuteCount = fifteenMinuteCount;
  }

  /**
   * @return the fifteenMinuteRate
   */
  public Double getFifteenMinuteRate() {
    return fifteenMinuteRate;
  }

  /**
   * @param fifteenMinuteRate the fifteenMinuteRate to set
   */
  public void setFifteenMinuteRate(Double fifteenMinuteRate) {
    this.fifteenMinuteRate = fifteenMinuteRate;
  }

  /**
   * @return the hourCount
   */
  public Integer getHourCount() {
    return hourCount;
  }

  /**
   * @param hourCount the hourCount to set
   */
  public void setHourCount(Integer hourCount) {
    this.hourCount = hourCount;
  }

  /**
   * @return the hourRate
   */
  public Double getHourRate() {
    return hourRate;
  }

  /**
   * @param hourRate the hourRate to set
   */
  public void setHourRate(Double hourRate) {
    this.hourRate = hourRate;
  }

  /**
   * @return the dayCount
   */
  public Integer getDayCount() {
    return dayCount;
  }

  /**
   * @param dayCount the dayCount to set
   */
  public void setDayCount(Integer dayCount) {
    this.dayCount = dayCount;
  }

  /**
   * @return the dayRate
   */
  public Double getDayRate() {
    return dayRate;
  }

  /**
   * @param dayRate the dayRate to set
   */
  public void setDayRate(Double dayRate) {
    this.dayRate = dayRate;
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
    result = prime * result + ((dayCount == null) ? 0 : dayCount.hashCode());
    result = prime * result + ((dayRate == null) ? 0 : dayRate.hashCode());
    result = prime * result
        + ((depositoryId == null) ? 0 : depositoryId.hashCode());
    result = prime * result
        + ((fifteenMinuteCount == null) ? 0 : fifteenMinuteCount.hashCode());
    result = prime * result
        + ((fifteenMinuteRate == null) ? 0 : fifteenMinuteRate.hashCode());
    result = prime * result + ((hourCount == null) ? 0 : hourCount.hashCode());
    result = prime * result + ((hourRate == null) ? 0 : hourRate.hashCode());
    result = prime * result
        + ((oneMinuteCount == null) ? 0 : oneMinuteCount.hashCode());
    result = prime * result
        + ((oneMinuteRate == null) ? 0 : oneMinuteRate.hashCode());
    result = prime * result + ((sensorId == null) ? 0 : sensorId.hashCode());
    result = prime * result + ((timestamp == null) ? 0 : timestamp.hashCode());
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
    if (dayCount == null) {
      if (other.dayCount != null) {
        return false;
      }
    }
    else if (!dayCount.equals(other.dayCount)) {
      return false;
    }
    if (dayRate == null) {
      if (other.dayRate != null) {
        return false;
      }
    }
    else if (!dayRate.equals(other.dayRate)) {
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
    if (fifteenMinuteCount == null) {
      if (other.fifteenMinuteCount != null) {
        return false;
      }
    }
    else if (!fifteenMinuteCount.equals(other.fifteenMinuteCount)) {
      return false;
    }
    if (fifteenMinuteRate == null) {
      if (other.fifteenMinuteRate != null) {
        return false;
      }
    }
    else if (!fifteenMinuteRate.equals(other.fifteenMinuteRate)) {
      return false;
    }
    if (hourCount == null) {
      if (other.hourCount != null) {
        return false;
      }
    }
    else if (!hourCount.equals(other.hourCount)) {
      return false;
    }
    if (hourRate == null) {
      if (other.hourRate != null) {
        return false;
      }
    }
    else if (!hourRate.equals(other.hourRate)) {
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
    return true;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "MeasurementRateSummary [sensorId=" + sensorId + ", depositoryId="
        + depositoryId + ", timestamp=" + timestamp + ", oneMinuteCount="
        + oneMinuteCount + ", oneMinuteRate=" + oneMinuteRate
        + ", fifteenMinuteCount=" + fifteenMinuteCount + ", fifteenMinuteRate="
        + fifteenMinuteRate + ", hourCount=" + hourCount + ", hourRate="
        + hourRate + ", dayCount=" + dayCount + ", dayRate=" + dayRate + "]";
  }

}
