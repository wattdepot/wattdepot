/**
 * SensorStatus.java This file is part of WattDepot.
 * <p/>
 * Copyright (C) 2015  Cam Moore
 * <p/>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.wattdepot.common.domainmodel;

import javax.xml.datatype.XMLGregorianCalendar;

/**
 * SensorStatus represents the status of a given Sensor. Used to report the current status of a sensor.
 *
 * @author Cam Moore
 *
 */
public class SensorStatus {
  /** The id of the sensor. */
  String sensorId;
  SensorStatusEnum statusEnum;
  XMLGregorianCalendar timestamp;

  /**
   * @return The Sensor Id.
   */
  public String getSensorId() {
    return sensorId;
  }

  /**
   * Sets the Sensor Id.
   * @param sensorId the new Sensor Id.
   */
  public void setSensorId(String sensorId) {
    this.sensorId = sensorId;
  }

  /**
   * @return The status of the sensor.
   */
  public SensorStatusEnum getStatus() {
    return statusEnum;
  }

  /**
   * Sets the status of the sensor.
   * @param statusEnum the new SensorStatusEnum.
   */
  public void setStatus(SensorStatusEnum statusEnum) {
    this.statusEnum = statusEnum;
  }

  /**
   * @return The time of the status as an XMLGregorianCalendar instance.
   */
  public XMLGregorianCalendar getTimestamp() {
    return timestamp;
  }

  /**
   * Sets the time of the status.
   * @param timestamp the new time.
   */
  public void setTimestamp(XMLGregorianCalendar timestamp) {
    this.timestamp = timestamp;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof SensorStatus)) {
      return false;
    }

    SensorStatus that = (SensorStatus) o;

    if (sensorId != null ? !sensorId.equals(that.sensorId) : that.sensorId != null) {
      return false;
    }
    if (statusEnum != that.statusEnum) {
      return false;
    }
    return !(timestamp != null ? !timestamp.equals(that.timestamp) : that.timestamp != null);

  }

  @Override
  public int hashCode() {
    int result = sensorId != null ? sensorId.hashCode() : 0;
    result = 31 * result + (statusEnum != null ? statusEnum.hashCode() : 0);
    result = 31 * result + (timestamp != null ? timestamp.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "SensorStatus{" +
        "sensorId='" + sensorId + '\'' +
        ", statusEnum=" + statusEnum +
        ", timestamp=" + timestamp +
        '}';
  }
}
