/**
 * DepositoryValueServerResource.java This file is part of WattDepot.
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
package org.wattdepot.server.http.api;

import java.text.ParseException;
import java.util.Date;
import java.util.logging.Level;

import javax.xml.datatype.DatatypeConfigurationException;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;
import org.wattdepot.common.domainmodel.Depository;
import org.wattdepot.common.domainmodel.Labels;
import org.wattdepot.common.domainmodel.InterpolatedValue;
import org.wattdepot.common.domainmodel.Sensor;
import org.wattdepot.common.domainmodel.SensorGroup;
import org.wattdepot.common.exception.IdNotFoundException;
import org.wattdepot.common.exception.MeasurementGapException;
import org.wattdepot.common.exception.MisMatchedOwnerException;
import org.wattdepot.common.exception.NoMeasurementException;
import org.wattdepot.common.util.DateConvert;
import org.wattdepot.server.ServerProperties;

/**
 * DepositoryValueServerResource - ServerResouce that handles the GET
 * /wattdepot/{org-id}/depository/{depository-id}/value/ response.
 * 
 * @author Cam Moore
 * 
 */
public class DepositoryValueServer extends WattDepotServerResource {
  private String depositoryId;
  private String sensorId;
  private String start;
  private String end;
  private String timestamp;
  private String latest;
  private String earliest;
  private String gapSeconds;
  
  private static DescriptiveStatistics averageGetTime = new DescriptiveStatistics();

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
    this.timestamp = getQuery().getValues(Labels.TIMESTAMP);
    this.latest = getQuery().getValues(Labels.LATEST);
    this.earliest = getQuery().getValues(Labels.EARLIEST);
    this.orgId = getAttribute(Labels.ORGANIZATION_ID);
    this.depositoryId = getAttribute(Labels.DEPOSITORY_ID);
    this.gapSeconds = getQuery().getValues(Labels.GAP);
  }

  /**
   * retrieve the depository value for a sensor.
   * 
   * @return measured value.
   */
  public InterpolatedValue doRetrieve() {
    getLogger().log(
        Level.INFO,
        "GET /wattdepot/{" + orgId + "}/depository/{" + depositoryId + "}/value/?sensor={"
            + sensorId + "}&start={" + start + "}&end={" + end + "}&timestamp={" + timestamp
            + "}&latest={" + latest + "}&earliest={" + earliest + "}&gap={" + gapSeconds + "}");
    Long startTime = null;
    if (depot.getServerProperties().get(ServerProperties.SERVER_TIMING_KEY)
        .equals(ServerProperties.TRUE)) {
      startTime = System.nanoTime();
    }
    if (isInRole(orgId)) {
      try {
        Depository deposit = depot.getDepository(depositoryId, orgId, true);
        if (deposit != null) {
          try {
            depot.getSensor(sensorId, orgId, true);
            InterpolatedValue val =  calculateValue(sensorId, start, end, timestamp, earliest, latest);
            if (depot.getServerProperties().get(ServerProperties.SERVER_TIMING_KEY)
                .equals(ServerProperties.TRUE)) {
              Long endTime = System.nanoTime();
              Long diff = endTime - startTime;
              averageGetTime.addValue(diff / 1E9);
              getLogger().log(
                  Level.SEVERE,
                  "GET /wattdepot/{" + orgId + "}/depository/{" + depositoryId
                      + "}/value/?sensor={" + sensorId + "}&start={" + start + "}&end={" + end
                      + "}&timestamp={" + timestamp + "}&latest={" + latest + "}&earliest={"
                      + earliest + "}&gap={" + gapSeconds + "} took "
                      + (diff / 1E9) + " seconds. Running average is " + averageGetTime.getMean());
            }            
            return val;
          }
          catch (IdNotFoundException inf) {
            try {
              SensorGroup group = depot.getSensorGroup(sensorId, orgId, true);
              InterpolatedValue val = null;
              // this wont work for earliest or latest.
              if (earliest != null) {
                // find the last 'earliest' time
                Date time = null;
                for (String s : group.getSensors()) {
                  InterpolatedValue v = calculateValue(s, start, end, timestamp, earliest, latest);
                  if (time == null) {
                    time = v.getDate();
                  }
                  else if (time.before(v.getDate())) {
                    time = v.getDate();
                  }
                }
                // have the time get the value at time
                for (String s : group.getSensors()) {
                  if (val == null) {
                    val = calculateValue(s, null, null, DateConvert.convertDate(time).toString(),
                        null, null);
                  }
                  else {
                    val.setValue(val.getValue()
                        + calculateValue(s, null, null, DateConvert.convertDate(time).toString(),
                            null, null).getValue());
                  }
                }
              }
              else if (latest != null) {
                // find the first 'latest' time
                Date time = null;
                for (String s : group.getSensors()) {
                  InterpolatedValue v = calculateValue(s, start, end, timestamp, earliest, latest);
                  if (time == null) {
                    time = v.getDate();
                  }
                  else if (time.after(v.getDate())) {
                    time = v.getDate();
                  }
                }
                // have the time get the value at time
                for (String s : group.getSensors()) {
                  if (val == null) {
                    val = calculateValue(s, null, null, DateConvert.convertDate(time).toString(),
                        null, null);
                  }
                  else {
                    val.setValue(val.getValue()
                        + calculateValue(s, null, null, DateConvert.convertDate(time).toString(),
                            null, null).getValue());
                  }
                }
              }
              else {
                for (String s : group.getSensors()) {
                  if (val == null) {
                    val = calculateValue(s, start, end, timestamp, earliest, latest);
                  }
                  else {
                    val.setValue(val.getValue()
                        + calculateValue(s, start, end, timestamp, earliest, latest).getValue());
                  }
                }
              }
              if (depot.getServerProperties().get(ServerProperties.SERVER_TIMING_KEY)
                  .equals(ServerProperties.TRUE)) {
                Long endTime = System.nanoTime();
                getLogger().log(
                    Level.SEVERE,
                    "GET /wattdepot/{" + orgId + "}/depository/{" + depositoryId
                        + "}/value/?sensor={" + sensorId + "}&start={" + start + "}&end={" + end
                        + "}&timestamp={" + timestamp + "}&latest={" + latest + "}&earliest={"
                        + earliest + "}&gap={" + gapSeconds + "} took "
                        + ((endTime - startTime) / 1E9) + " seconds.");
              }

              return val;
            }
            catch (IdNotFoundException inf1) {
              setStatus(Status.CLIENT_ERROR_BAD_REQUEST, "Could not find sensor " + sensorId);
            }
          }
        }
        else {
          setStatus(Status.CLIENT_ERROR_BAD_REQUEST, "Could not find depository " + depositoryId);
        }
      }
      catch (MisMatchedOwnerException e) {
        setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
      }
      catch (NoMeasurementException e1) {
        setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e1.getMessage());
      }
      catch (NumberFormatException e) {
        setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
      }
      catch (MeasurementGapException e) {
        setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
      }
      catch (ParseException e) {
        setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
        e.printStackTrace();
      }
      catch (DatatypeConfigurationException e) {
        setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
      }
      catch (IdNotFoundException e) {
        setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
      }
      return null;
    }
    else {
      setStatus(Status.CLIENT_ERROR_BAD_REQUEST, "Bad credentials.");
      return null;
    }
  }

  /**
   * @param sensorId the Sensor's id.
   * @param start The start time. Optional, must have end if not null.
   * @param end The end time. Optional, must exist if start not null.
   * @param timestamp The time of the value. Optional, must be null if start or
   *        end.
   * @param earliest if not null will get earliest value. Optional, must be null
   *        if start or end or latest or timestamp.
   * @param latest if not null will get latest value. Optional, must be null if
   *        start or end or earliest or timestamp.
   * @return The interpolated value for the sensorId and time(s).
   * @throws IdNotFoundException if sensorId is not defined.
   * @throws MisMatchedOwnerException if sensorId is not in orgId.
   * @throws NoMeasurementException if there aren't measurements around the
   *         time(s).
   * @throws ParseException if the times are not valid Date strings.
   * @throws DatatypeConfigurationException if there is a server problem.
   * @throws NumberFormatException if there is a problem.
   * @throws MeasurementGapException if the measurements are too far apart.
   */
  private InterpolatedValue calculateValue(String sensorId, String start, String end,
      String timestamp, String earliest, String latest) throws IdNotFoundException,
      MisMatchedOwnerException, NoMeasurementException, ParseException,
      DatatypeConfigurationException, NumberFormatException, MeasurementGapException {
    Depository deposit = depot.getDepository(depositoryId, orgId, true);
    Sensor sensor = depot.getSensor(sensorId, orgId, true);
    Double value = null;
    Date startDate = null;
    Date endDate = null;
    Date time = null;
    InterpolatedValue val = null;

    if (earliest != null) {
      return depot.getEarliestMeasuredValue(depositoryId, orgId, sensorId, true);
    }
    else if (latest != null) {
      return depot.getLatestMeasuredValue(depositoryId, orgId, sensorId, true);
    }
    else if (timestamp != null) {
      time = DateConvert.parseCalStringToDate(timestamp);
      if (gapSeconds != null) {
        value = depot.getValue(depositoryId, orgId, sensor.getId(), time,
            Long.parseLong(gapSeconds), true);
      }
      else {
        value = depot.getValue(depositoryId, orgId, sensor.getId(), time, true);
      }
    }
    else if (start != null && end != null) {
      startDate = DateConvert.parseCalStringToDate(start);
      endDate = DateConvert.parseCalStringToDate(end);
      if (gapSeconds != null) {
        value = depot.getValue(depositoryId, orgId, sensor.getId(), startDate, endDate,
            Long.parseLong(gapSeconds), true);
      }
      else {
        value = depot.getValue(depositoryId, orgId, sensor.getId(), startDate, endDate, true);
      }
    }

    if (end != null) {
      val = new InterpolatedValue(sensorId, value, deposit.getMeasurementType(), endDate);
    }
    else if (time != null) {
      val = new InterpolatedValue(sensorId, value, deposit.getMeasurementType(), time);
    }
    return val;
  }
}
