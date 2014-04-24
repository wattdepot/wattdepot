/**
 * DepositoryMeasurementsIntervalServer.java This file is part of WattDepot.
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
package org.wattdepot.server.http.api;

import java.text.ParseException;
import java.util.Date;

import javax.xml.datatype.DatatypeConfigurationException;

import org.restlet.resource.ResourceException;
import org.wattdepot.common.domainmodel.Labels;
import org.wattdepot.common.domainmodel.Measurement;
import org.wattdepot.common.domainmodel.MeasurementList;
import org.wattdepot.common.domainmodel.Sensor;
import org.wattdepot.common.domainmodel.SensorGroup;
import org.wattdepot.common.exception.IdNotFoundException;
import org.wattdepot.common.exception.MisMatchedOwnerException;
import org.wattdepot.common.util.DateConvert;

/**
 * DepositoryMeasurementsIntervalServer base class that parses the url to get
 * the values and returns measurement lists for the interval.
 * 
 * @author Cam Moore
 * 
 */
public class DepositoryMeasurementsIntervalServer extends WattDepotServerResource {
  protected String depositoryId;
  protected String sensorId;
  private String start;
  protected Date startDate;
  private String end;
  protected Date endDate;
  private String intervalStr;
  protected int interval;
  protected String valueType;

  /*
   * (non-Javadoc)
   * 
   * @see org.restlet.resource.Resource#doInit()
   */
  @Override
  protected void doInit() throws ResourceException {
    super.doInit();
    this.sensorId = getQuery().getValues(Labels.SENSOR);
    this.start = getQuery().getValues(Labels.START);
    this.end = getQuery().getValues(Labels.END);
    this.intervalStr = getQuery().getValues(Labels.INTERVAL);
    this.interval = Integer.parseInt(intervalStr);
    this.valueType = getQuery().getValues(Labels.VALUE_TYPE);
    this.depositoryId = getAttribute(Labels.DEPOSITORY_ID);
    try {
      this.startDate = DateConvert.parseCalStringToDate(start);
      this.endDate = DateConvert.parseCalStringToDate(end);
    }
    catch (ParseException e) {
      startDate = null;
      endDate = null;
    }
    catch (DatatypeConfigurationException e) {
      startDate = null;
      endDate = null;
    }

  }

  /**
   * @param depositoryId The depository id.
   * @param orgId The organization id.
   * @param sensorId The sensor id.
   * @param startDate The start date.
   * @param endDate The end date.
   * @return The measurements for the given depository and sensor during the
   *         interval from startDate to endDate.
   * @throws IdNotFoundException If any id is not defined.
   * @throws MisMatchedOwnerException If the sensor and depository have
   *         different organizations.
   */
  protected MeasurementList getMeasurements(String depositoryId, String orgId, String sensorId,
      Date startDate, Date endDate) throws IdNotFoundException, MisMatchedOwnerException {
    if (startDate != null && endDate != null) {
      MeasurementList ret = new MeasurementList();
      try {
        Sensor sensor = depot.getSensor(sensorId, orgId, true);
        for (Measurement meas : depot.getMeasurements(depositoryId, orgId, sensor.getId(),
            startDate, endDate, true)) {
          ret.getMeasurements().add(meas);
        }
      }
      catch (IdNotFoundException nf) {
        SensorGroup group = depot.getSensorGroup(sensorId, orgId, true);
        for (String s : group.getSensors()) {
          Sensor sensor = depot.getSensor(s, orgId, true);
          for (Measurement meas : depot.getMeasurements(depositoryId, orgId, sensor.getId(),
              startDate, endDate, true)) {
            ret.getMeasurements().add(meas);
          }
        }
      }
      return ret;
    }
    return null;
  }

  /**
   * @param time the date to increment.
   * @param minutes the number of minutes to increment the date by.
   * @return the new date.
   */
  protected Date incrementMinutes(Date time, int minutes) {
    long millis = time.getTime();
    millis += 1000L * 60 * minutes;
    return new Date(millis);
  }

}
