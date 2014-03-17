/**
 * DepositoryValuesServer.java This file is part of WattDepot.
 *
 * Copyright (C) 2013  Yongwen Xu
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

import org.restlet.data.Status;
import org.restlet.resource.ResourceException;
import org.wattdepot.common.domainmodel.Depository;
import org.wattdepot.common.domainmodel.Labels;
import org.wattdepot.common.domainmodel.InterpolatedValue;
import org.wattdepot.common.domainmodel.MeasuredValueList;
import org.wattdepot.common.domainmodel.Sensor;
import org.wattdepot.common.domainmodel.SensorGroup;
import org.wattdepot.common.exception.IdNotFoundException;
import org.wattdepot.common.exception.MisMatchedOwnerException;
import org.wattdepot.common.exception.NoMeasurementException;
import org.wattdepot.common.util.DateConvert;
import org.wattdepot.common.util.tstamp.Tstamp;

/**
 * DepositoryValuesServer - Base class for handling the Depository values HTTP
 * API ("/wattdepot/{org-id}/depository/{depository-id}/values/").
 * 
 * @author Yongwen Xu
 * 
 */
public class DepositoryValuesServer extends WattDepotServerResource {
  private String depositoryId;
  private String sensorId;
  private String start;
  private String end;
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
            + Labels.VALUES + "/?" + Labels.SENSOR + "={" + sensorId + "}&" + Labels.START + "={"
            + start + "}&" + Labels.END + "={" + end + "}&" + Labels.INTERVAL + "={" + interval
            + "}&" + Labels.VALUE_TYPE + "={" + dataType + "}");
    if (isInRole(orgId)) {
      if (start != null && end != null && interval != null) {
        MeasuredValueList ret = new MeasuredValueList();
        try {
          Depository depository = depot.getDepository(depositoryId, orgId, true);
          if (depository != null) {
            XMLGregorianCalendar startTime = DateConvert.parseCalString(start);
            XMLGregorianCalendar endTime = DateConvert.parseCalString(end);

            // interval is in minute
            int intervalMinutes = Integer.parseInt(interval);

            // Build list of timestamps, starting with startTime, separated by
            // intervalMilliseconds
            List<XMLGregorianCalendar> timestampList = Tstamp.getTimestampList(startTime, endTime,
                intervalMinutes);
            if (timestampList != null) {
              Sensor sensor = depot.getSensor(sensorId, orgId, true);
              if (sensor != null) {
                Date previous = null;
                for (int i = 0; i < timestampList.size(); i++) {
                  Date timestamp = DateConvert.convertXMLCal(timestampList.get(i));
                  Double value = new Double(0);
                  if ("point".equals(dataType)) {
                    try {
                      value = depot.getValue(depositoryId, orgId, sensor.getId(), timestamp, true);
                    }
                    catch (NoMeasurementException e) { // NOPMD
                      // no measurements around the time so return 0?
                    }
                  }
                  else {
                    if (previous != null) {
                      try {
                        value = depot.getValue(depositoryId, orgId, sensor.getId(), previous,
                            timestamp, true);
                      }
                      catch (NoMeasurementException e) { // NOPMD
                        // No measurements so return 0,
                      }
                    }
                    previous = timestamp;
                  }
                  InterpolatedValue mValue = new InterpolatedValue(sensor.getId(), value,
                      depository.getMeasurementType(), timestamp);

                  mValue.setDate(timestamp);

                  ret.getMeasuredValues().add(mValue);
                }
              }
              else {
                // TODO CAM this code doesn't work! Need to aggregate the values
                // not just put them in.
                SensorGroup group = depot.getSensorGroup(sensorId, orgId, true);
                if (group != null) {
                  Date previous = null;
                  for (int i = 0; i < timestampList.size(); i++) {
                    Date timestamp = DateConvert.convertXMLCal(timestampList.get(i));
                    Double value = new Double(0);
                    for (String s : group.getSensors()) {
                      Sensor sens = depot.getSensor(s, orgId, true);
                      if (sens != null) {
                        if ("point".equals(dataType)) {
                          try {
                            value += depot.getValue(depositoryId, orgId, sens.getId(), timestamp,
                                true);
                          }
                          catch (NoMeasurementException e) { // NOPMD
                            // No measurements so return 0;
                          }
                        }
                        else {
                          if (previous != null) {
                            try {
                              value += depot.getValue(depositoryId, orgId, sens.getId(),
                                  previous, timestamp, true);
                            }
                            catch (NoMeasurementException e) { // NOPMD
                              // No measurements so return 0,
                            }
                          }
                        }
                      }
                    }
                    previous = timestamp;
                    InterpolatedValue mValue = new InterpolatedValue(group.getId(), value,
                        depository.getMeasurementType(), timestamp);

                    mValue.setDate(timestamp);
                    ret.getMeasuredValues().add(mValue);
                  }
                }
              }
            }
            else {
              setStatus(Status.CLIENT_ERROR_BAD_REQUEST, "Bad start, end, or interval.");
            }
          }
          else {
            setStatus(Status.CLIENT_ERROR_BAD_REQUEST, depositoryId + " is not defined.");
          }
        }
        catch (MisMatchedOwnerException e) {
          setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
        }
        catch (ParseException e) {
          setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
        }
        catch (DatatypeConfigurationException e) {
          setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
        }
        catch (IdNotFoundException e) {
          setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
        }
        getLogger().info(ret.toString());
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
}
