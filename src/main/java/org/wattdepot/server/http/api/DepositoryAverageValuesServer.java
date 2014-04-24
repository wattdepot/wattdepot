/**
 * DepositoryAverageValuesServer.java This file is part of WattDepot.
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
import java.util.List;
import java.util.logging.Level;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;
import org.wattdepot.common.domainmodel.Depository;
import org.wattdepot.common.domainmodel.InterpolatedValue;
import org.wattdepot.common.domainmodel.Labels;
import org.wattdepot.common.domainmodel.MeasuredValueList;
import org.wattdepot.common.domainmodel.Sensor;
import org.wattdepot.common.domainmodel.SensorGroup;
import org.wattdepot.common.exception.IdNotFoundException;
import org.wattdepot.common.exception.MisMatchedOwnerException;
import org.wattdepot.common.exception.NoMeasurementException;
import org.wattdepot.common.util.DateConvert;
import org.wattdepot.common.util.tstamp.Tstamp;

/**
 * DepositoryAverageValuesServer - Base class for handling HTTP API
 * ("/wattdepot/{org-id}/depository/{depository-id}/values/average/").
 * 
 * @author Cam Moore
 * 
 */
public class DepositoryAverageValuesServer extends WattDepotServerResource {
  private String depositoryId;
  private String sensorId;
  /** The start of the range. */
  private String start;
  /** The end of the range. */
  private String end;
  /** The interval to calculate the average over. */
  private String interval;
  private String dataType;

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
    this.depositoryId = getAttribute(Labels.DEPOSITORY_ID);
    this.interval = getQuery().getValues(Labels.INTERVAL);
    this.dataType = getQuery().getValues(Labels.VALUE_TYPE);
  }

  /**
   * retrieve the depository measurement list for a sensor.
   * 
   * @return measurement list.
   */
  public MeasuredValueList doRetrieve() {
    getLogger().log(
        Level.INFO,
        "GET /wattdepot/{" + orgId + "}/" + Labels.DEPOSITORY + "/{" + depositoryId + "}/"
            + Labels.VALUES + "/" + Labels.AVERAGE + "/?" + Labels.SENSOR + "={" + sensorId + "}&"
            + Labels.START + "={" + start + "}&" + Labels.END + "={" + end + "}&" + Labels.INTERVAL
            + "={" + interval + "}&" + Labels.VALUE_TYPE + "={" + dataType + "}");
    if (isInRole(orgId)) {
      if (start != null && end != null && interval != null) {
        MeasuredValueList ret = new MeasuredValueList();
        try {
          Depository depository = depot.getDepository(depositoryId, orgId, true);
          XMLGregorianCalendar startRange = DateConvert.parseCalString(start);
          XMLGregorianCalendar endRange = DateConvert.parseCalString(end);
          int intervalMinutes = Integer.parseInt(interval);
          List<XMLGregorianCalendar> rangeList = Tstamp.getTimestampList(startRange, endRange,
              intervalMinutes);
          if (rangeList != null) {
            for (int i = 1; i < rangeList.size(); i++) {
              Date valueDate = DateConvert.convertXMLCal(rangeList.get(i));
              XMLGregorianCalendar startInterval = rangeList.get(i - 1);
              XMLGregorianCalendar endInterval = rangeList.get(i);
              List<XMLGregorianCalendar> intervalList = Tstamp.getNTimestampList(24, startInterval,
                  endInterval);
              DescriptiveStatistics stats = new DescriptiveStatistics();
              Date previous = null;
              for (int j = 0; j < intervalList.size(); j++) {
                Date timestamp = DateConvert.convertXMLCal(intervalList.get(j));
                Double value = null;
                if ("point".equals(dataType)) {
                  value = getValue(depositoryId, orgId, sensorId, timestamp);
                }
                else {
                  if (previous != null) {
                    value = getValue(depositoryId, orgId, sensorId, previous, timestamp);
                  }
                  previous = timestamp;
                }
                if (value != null) {
                  stats.addValue(value);
                }
              }

              if (!Double.isNaN(stats.getMean())) {
                ret.getMeasuredValues().add(
                    new InterpolatedValue(sensorId, stats.getMean(), depository
                        .getMeasurementType(), valueDate));
              }
            }
          }
        }
        catch (IdNotFoundException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        catch (ParseException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        catch (DatatypeConfigurationException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        return ret;
      }
      else {
        setStatus(Status.CLIENT_ERROR_BAD_REQUEST, "Missing start and/or end times or interval.");
        return null;
      }
    }
    else {
      setStatus(Status.CLIENT_ERROR_BAD_REQUEST, "Bad credentials.");
      return null;
    }
  }

  /**
   * @param depositoryId The depository.
   * @param orgId The organization.
   * @param sensorId The sensor.
   * @param timestamp When to get the value.
   * @return The value.
   */
  private Double getValue(String depositoryId, String orgId, String sensorId, Date timestamp) {
    try {
      Sensor sensor = depot.getSensor(sensorId, orgId, false);
      Double value = 0.0;
      if (sensor != null) { // just get sensor value
        try {
          value = depot.getValue(depositoryId, orgId, sensor.getId(), timestamp, true);
        }
        catch (NoMeasurementException e) {
          return null;
        }
      }
      else { // check for sensor group
        SensorGroup group = depot.getSensorGroup(sensorId, orgId, false);
        if (group != null) {
          for (String s : group.getSensors()) {
            try {
              value += depot.getValue(depositoryId, orgId, s, timestamp, true);
            }
            catch (NoMeasurementException e) { // NOPMD
              // add 0 to value so do nothing.
            }
          }
        }
      }
      return value;
    }
    catch (MisMatchedOwnerException e) {
      return null;
    }
    catch (IdNotFoundException e) {
      return null;
    }
  }

  /**
   * @param depositoryId The depository.
   * @param orgId The organization.
   * @param sensorId The sensor.
   * @param start The start of the interval.
   * @param end The end of the interval.
   * @return The value.
   */
  private Double getValue(String depositoryId, String orgId, String sensorId, Date start, Date end) {
    try {
      Sensor sensor = depot.getSensor(sensorId, orgId, false);
      Double value = 0.0;
      if (sensor != null) { // just get sensor value
        try {
          value = depot.getValue(depositoryId, orgId, sensor.getId(), start, end, true);
        }
        catch (NoMeasurementException e) {
          return null;
        }
      }
      else { // check for sensor group
        SensorGroup group = depot.getSensorGroup(sensorId, orgId, false);
        if (group != null) {
          for (String s : group.getSensors()) {
            try {
              value += depot.getValue(depositoryId, orgId, s, start, end, true);
            }
            catch (NoMeasurementException e) { // NOPMD
              // add 0 to value so do nothing.
            }
          }
        }
      }
      return value;
    }
    catch (MisMatchedOwnerException e) {
      return null;
    }
    catch (IdNotFoundException e) {
      return null;
    }

  }
}
