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

package org.wattdepot.server.http.api;

import org.restlet.data.Status;
import org.restlet.resource.ResourceException;
import org.wattdepot.common.domainmodel.Depository;
import org.wattdepot.common.domainmodel.InterpolatedValue;
import org.wattdepot.common.domainmodel.InterpolatedValueList;
import org.wattdepot.common.domainmodel.Labels;
import org.wattdepot.common.domainmodel.Measurement;
import org.wattdepot.common.domainmodel.Sensor;
import org.wattdepot.common.domainmodel.SensorGroup;
import org.wattdepot.common.exception.IdNotFoundException;
import org.wattdepot.common.exception.MisMatchedOwnerException;
import org.wattdepot.common.exception.NoMeasurementException;
import org.wattdepot.common.http.api.DepositoryHistoricalValuesResource;
import org.wattdepot.common.util.DateConvert;
import org.wattdepot.common.util.tstamp.Tstamp;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.XMLGregorianCalendar;
import java.text.ParseException;
import java.util.List;
import java.util.logging.Level;

/**
 * DepositoryHistoricalValuesServer - Base class for handling historical values HTTP requests.
 *
 * @author Cam Moore
 */
public class DepositoryHistoricalValuesServer extends WattDepotServerResource implements DepositoryHistoricalValuesResource {
  private String depositoryId;
  private String hourlyDailyChoice;
  private String sensorId;
  private String timestamp;
  private String valueType;
  private Integer samples;

  @Override
  public InterpolatedValueList retrieve() {
    getLogger().log(
        Level.INFO,
        "GET /wattdepot/{" + orgId + "}/" + Labels.DEPOSITORY + "/{" + depositoryId + "}/" + Labels.DESCRIPTIVE_STATS + "/"
            + hourlyDailyChoice + "/?" + Labels.SENSOR + "={" + sensorId + "}&" + Labels.TIMESTAMP + "={"
            + timestamp + "}&" + Labels.VALUE_TYPE + "={" + valueType + "}&" + Labels.SAMPLES + "={" + samples + "}");
    if (isInRole(orgId)) {
      InterpolatedValueList interpolatedValueList = new InterpolatedValueList();
      try {
        Depository depository = depot.getDepository(depositoryId, orgId, true);
        if (depository != null) {
          XMLGregorianCalendar time = DateConvert.parseCalString(timestamp);
          XMLGregorianCalendar begin = DateConvert.getBeginning(time, hourlyDailyChoice);
          XMLGregorianCalendar end = DateConvert.getEnding(time, hourlyDailyChoice);
          for (int i = 0; i < samples; i++) {
            begin = Tstamp.incrementDays(begin, -7); // back it up a week
            end = Tstamp.incrementDays(end, -7);
            Sensor sensor = depot.getSensor(sensorId, orgId, false);
            if (valueType.equals(Labels.POINT)) { // since it is point data add all of the measurements
              if (sensor != null) { // individual sensor
                List<Measurement> measurements = depot.getMeasurements(depositoryId, orgId, sensorId, DateConvert.convertXMLCal(begin), DateConvert.convertXMLCal(end), false);
                if (measurements.size() == 0) {
                  InterpolatedValue value = new InterpolatedValue(sensorId, 0.0, depository.getMeasurementType(), DateConvert.convertXMLCal(begin), DateConvert.convertXMLCal(end));
                  value.addReportingSensor(sensorId);
                  interpolatedValueList.getInterpolatedValues().add(value);
                }
                else {
                  for (Measurement m : measurements) {
                    InterpolatedValue value = new InterpolatedValue(sensorId, m.getValue(), depository.getMeasurementType(), m.getDate());
                    value.addDefinedSensor(sensorId);
                    value.addReportingSensor(sensorId);
                    interpolatedValueList.getInterpolatedValues().add(value);
                  }
                }
              }
              else { // sensor group
                SensorGroup group = depot.getSensorGroup(sensorId, orgId, false);
                if (group != null) {
                  for (String s : group.getSensors()) {
                    List<Measurement> measurements = depot.getMeasurements(depositoryId, orgId, s, DateConvert.convertXMLCal(begin), DateConvert.convertXMLCal(end), false);
                    if (measurements.size() == 0) {
                      InterpolatedValue value = new InterpolatedValue(s, 0.0, depository.getMeasurementType(), DateConvert.convertXMLCal(begin), DateConvert.convertXMLCal(end));
                      value.addDefinedSensor(s);
                      interpolatedValueList.getInterpolatedValues().add(value);
                    }
                    else {
                      for (Measurement m : measurements) {
                        InterpolatedValue value = new InterpolatedValue(s, m.getValue(), depository.getMeasurementType(), m.getDate());
                        value.addReportingSensor(s);
                        value.addDefinedSensor(s);
                        interpolatedValueList.getInterpolatedValues().add(value);
                      }
                    }
                  }
                }
              }
            }
            else { // difference values
              InterpolatedValue value = new InterpolatedValue(sensorId, 0.0 , depository.getMeasurementType(), DateConvert.convertXMLCal(begin), DateConvert.convertXMLCal(end));
              if (sensor != null) {
                value.addDefinedSensor(sensorId);
                try {
                  Double val = depot.getValue(depositoryId, orgId, sensorId, DateConvert.convertXMLCal(begin), DateConvert.convertXMLCal(end), false);
                  value.setValue(val);
                  value.addReportingSensor(sensorId);
                }
                catch (NoMeasurementException e) { // NOPMD
                  // ok.
                }
              }
              else {
                SensorGroup group = depot.getSensorGroup(sensorId, orgId, false);
                if (group != null) {
                  for (String s : group.getSensors()) {
                    value.addDefinedSensor(s);
                    try {
                      Double val = depot.getValue(depositoryId, orgId, s, DateConvert.convertXMLCal(begin), DateConvert.convertXMLCal(end), false);
                      value.setValue(val + value.getValue());
                      value.addReportingSensor(s);
                    }
                    catch (NoMeasurementException e) { // NOPMD
                      // ok.
                    }
                  }
                }
              }
              interpolatedValueList.getInterpolatedValues().add(value);
            }
          }
        }
        return interpolatedValueList;
      }
      catch (IdNotFoundException e) {
        setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
        return null;
      }
      catch (DatatypeConfigurationException e) {
        setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
        return null;
      }
      catch (ParseException e) {
        setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
        return null;
      }
      catch (MisMatchedOwnerException e) {
        setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
        return null;
      }
    }

    return null;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.restlet.resource.Resource#doInit()
   */
  @Override
  protected void doInit() throws ResourceException {
    super.doInit();
    this.depositoryId = getAttribute(Labels.DEPOSITORY_ID);
    this.hourlyDailyChoice = getAttribute(Labels.HOURLY_DAILY);
    this.sensorId = getQuery().getValues(Labels.SENSOR);
    this.timestamp = getQuery().getValues(Labels.TIMESTAMP);
    this.valueType = getQuery().getValues(Labels.VALUE_TYPE);
    this.samples = Integer.parseInt(getQuery().getValues(Labels.SAMPLES));
  }


}
