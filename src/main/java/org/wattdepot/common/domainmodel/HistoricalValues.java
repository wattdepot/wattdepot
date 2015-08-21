/*
 * This file is part of WattDepot.
 *
 *  Copyright (C) 2015  Cam Moore
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.wattdepot.common.domainmodel;

import java.util.Date;

/**
 * HistoricalValues - A simple calculated historical min, average, and max for a depository, sensor, timestamp
 * and number of samples.
 * @author Cam Moore
 */
public class HistoricalValues {
  String depositoryId;
  String sensorId;
  Integer numSamples;
  Double average;
  Double minimum;
  Double maximum;
  Double lowerQuartile;
  Double upperQuartile;
  Date timestamp;
  String windowWidth;
  String valueType;

  /**
   * @return The Depository id.
   */
  public String getDepositoryId() {
    return depositoryId;
  }

  /**
   * Sets the depository id.
   * @param depositoryId the new depository id.
   */
  public void setDepositoryId(String depositoryId) {
    this.depositoryId = depositoryId;
  }

  /**
   * @return the sensor id.
   */
  public String getSensorId() {
    return sensorId;
  }

  /**
   * Sets the sensor id.
   * @param sensorId the new sensor id.
   */
  public void setSensorId(String sensorId) {
    this.sensorId = sensorId;
  }

  /**
   * @return the number of weeks of data used to calculate the values.
   */
  public Integer getNumSamples() {
    return numSamples;
  }

  /**
   * Sets the number of samples used.
   * @param numSamples the new number of samples used.
   */
  public void setNumSamples(Integer numSamples) {
    this.numSamples = numSamples;
  }

  /**
   * @return The average value of the samples.
   */
  public Double getAverage() {
    return average;
  }

  /**
   * Sets the average value.
   * @param average the new average value.
   */
  public void setAverage(Double average) {
    this.average = average;
  }

  /**
   * @return the minimum sample value.
   */
  public Double getMinimum() {
    return minimum;
  }

  /**
   * Sets the minimum value.
   * @param minimum the new minimum value.
   */
  public void setMinimum(Double minimum) {
    this.minimum = minimum;
  }

  /**
   * @return the maximum sample value.
   */
  public Double getMaximum() {
    return maximum;
  }

  /**
   * Sets the maximum value.
   * @param maximum the new maximum value.
   */
  public void setMaximum(Double maximum) {
    this.maximum = maximum;
  }

  /**
   * @return the time of the historical values.
   */
  public Date getTimestamp() {
    return new Date(timestamp.getTime());
  }

  /**
   * Sets the time of the historical values.
   * @param timestamp the new time.
   */
  public void setTimestamp(Date timestamp) {
    this.timestamp = new Date(timestamp.getTime());
  }

  /**
   * @return the width of the window, hourly or daily.
   */
  public String getWindowWidth() {
    return windowWidth;
  }

  /**
   * Sets the window width.
   * @param windowWidth the new window width.
   */
  public void setWindowWidth(String windowWidth) {
    this.windowWidth = windowWidth;
  }

  /**
   * @return point or difference.
   */
  public String getValueType() {
    return valueType;
  }

  /**
   * Sets the value type, point or difference.
   * @param valueType the new value type.
   */
  public void setValueType(String valueType) {
    this.valueType = valueType;
  }

  /**
   * @return the lower quartile.
   */
  public Double getLowerQuartile() {
    return lowerQuartile;
  }

  /**
   * Sets the lower quartile.
   * @param lowerQuartile the new lower quartile.
   */
  public void setLowerQuartile(Double lowerQuartile) {
    this.lowerQuartile = lowerQuartile;
  }

  /**
   * @return the upper quartile.
   */
  public Double getUpperQuartile() {
    return upperQuartile;
  }

  /**
   * Sets the upper quartile.
   * @param upperQuartile the new upper quartile.
   */
  public void setUpperQuartile(Double upperQuartile) {
    this.upperQuartile = upperQuartile;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof HistoricalValues)) {
      return false;
    }

    HistoricalValues that = (HistoricalValues) o;

    if (depositoryId != null ? !depositoryId.equals(that.depositoryId) : that.depositoryId != null) {
      return false;
    }
    if (sensorId != null ? !sensorId.equals(that.sensorId) : that.sensorId != null) {
      return false;
    }
    if (numSamples != null ? !numSamples.equals(that.numSamples) : that.numSamples != null) {
      return false;
    }
    if (average != null ? !average.equals(that.average) : that.average != null) {
      return false;
    }
    if (minimum != null ? !minimum.equals(that.minimum) : that.minimum != null) {
      return false;
    }
    if (maximum != null ? !maximum.equals(that.maximum) : that.maximum != null) {
      return false;
    }
    if (timestamp != null ? !timestamp.equals(that.timestamp) : that.timestamp != null) {
      return false;
    }
    if (windowWidth != null ? !windowWidth.equals(that.windowWidth) : that.windowWidth != null) {
      return false;
    }
    return !(valueType != null ? !valueType.equals(that.valueType) : that.valueType != null);

  }

  @Override
  public int hashCode() {
    int result = depositoryId != null ? depositoryId.hashCode() : 0;
    result = 31 * result + (sensorId != null ? sensorId.hashCode() : 0);
    result = 31 * result + (numSamples != null ? numSamples.hashCode() : 0);
    result = 31 * result + (average != null ? average.hashCode() : 0);
    result = 31 * result + (minimum != null ? minimum.hashCode() : 0);
    result = 31 * result + (maximum != null ? maximum.hashCode() : 0);
    result = 31 * result + (timestamp != null ? timestamp.hashCode() : 0);
    result = 31 * result + (windowWidth != null ? windowWidth.hashCode() : 0);
    result = 31 * result + (valueType != null ? valueType.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "HistoricalValues{" +
        "depositoryId='" + depositoryId + '\'' +
        ", sensorId='" + sensorId + '\'' +
        ", numSamples=" + numSamples +
        ", average=" + average +
        ", minimum=" + minimum +
        ", maximum=" + maximum +
        ", timestamp=" + timestamp +
        ", windowWidth='" + windowWidth + '\'' +
        ", valueType='" + valueType + '\'' +
        '}';
  }
}
