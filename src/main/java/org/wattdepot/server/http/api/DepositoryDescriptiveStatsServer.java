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

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;
import org.wattdepot.common.domainmodel.Depository;
import org.wattdepot.common.domainmodel.DescriptiveStats;
import org.wattdepot.common.domainmodel.Labels;
import org.wattdepot.common.domainmodel.Measurement;
import org.wattdepot.common.domainmodel.Sensor;
import org.wattdepot.common.domainmodel.SensorGroup;
import org.wattdepot.common.exception.CounterRollOverException;
import org.wattdepot.common.exception.IdNotFoundException;
import org.wattdepot.common.exception.MisMatchedOwnerException;
import org.wattdepot.common.exception.NoMeasurementException;
import org.wattdepot.common.util.DateConvert;
import org.wattdepot.common.util.tstamp.Tstamp;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.XMLGregorianCalendar;
import java.text.ParseException;
import java.util.List;
import java.util.logging.Level;

/**
 * DepositoryDescriptiveStatsServer - Base class for handling historical values HTTP requests.
 *
 * @author Cam Moore
 */
public class DepositoryDescriptiveStatsServer extends WattDepotServerResource {
  private String depositoryId;
  private String hourlyDailyChoice;
  private String sensorId;
  private String timestamp;
  private String valueType;
  private Integer samples;

  /**
   * @return the DescriptiveStats for the given request.
   */
  public DescriptiveStats doRetrieve() {
    getLogger().log(
        Level.INFO,
        "GET /wattdepot/{" + orgId + "}/" + Labels.DEPOSITORY + "/{" + depositoryId + "}/" + Labels.DESCRIPTIVE_STATS + "/"
            + hourlyDailyChoice + "/?" + Labels.SENSOR + "={" + sensorId + "}&" + Labels.TIMESTAMP + "={"
            + timestamp + "}&" + Labels.VALUE_TYPE + "={" + valueType + "}&" + Labels.SAMPLES + "={" + samples + "}");
    if (isInRole(orgId)) {
      DescriptiveStats ret = new DescriptiveStats();
      try {
        Depository depository = depot.getDepository(depositoryId, orgId, true);
        if (depository != null) {
          XMLGregorianCalendar time = DateConvert.parseCalString(timestamp);
          XMLGregorianCalendar begin = DateConvert.getBeginning(time, hourlyDailyChoice);
          XMLGregorianCalendar end = DateConvert.getEnding(time, hourlyDailyChoice);
          Double minimum = Double.MAX_VALUE; // for this time period
          Double maximum = Double.MIN_VALUE; // for this time period
          Double average = 0.0;
          Double upQuartile = 0.0;
          Double lowQuartile = 0.0;
          DescriptiveStatistics statistics = new DescriptiveStatistics();
          for (int i = 0; i < samples; i++) {
            begin = Tstamp.incrementDays(begin, -7); // back it up a week
            end = Tstamp.incrementDays(end, -7);
            Sensor sensor = depot.getSensor(sensorId, orgId, false);
            if (valueType.equals(Labels.POINT)) { // since it is point data must calculate the average of the measurements
              if (sensor != null) {
                ret.addDefinedSensor(sensorId);
                statistics = updateStatistics(depositoryId, sensorId, begin, end, statistics);
                ret.addReportingSensor(sensorId);
                if (statistics.getMin() < minimum) {
                  minimum = statistics.getMin();
                }
                if (statistics.getMax() > maximum) {
                  maximum = statistics.getMax();
                }
                average += statistics.getMean();
                upQuartile += statistics.getPercentile(75.0);
                lowQuartile += statistics.getPercentile(25.0);
              }
              else {
                SensorGroup group = depot.getSensorGroup(sensorId, orgId, false);
                if (group != null) {
                  Double groupMin = 0.0;
                  Double groupMax = 0.0;
                  Double groupAve = 0.0;
                  Double lowerQuartile = 0.0;
                  Double upperQuartile = 0.0;
                  for (String s : group.getSensors()) {
                    ret.addDefinedSensor(s);
                    try {
                      statistics = getStatistics(depositoryId, s, begin, end);
                      ret.addReportingSensor(s);
                      groupMin += statistics.getMin();
                      groupMax += statistics.getMax();
                      groupAve += statistics.getMean();
                      lowerQuartile += statistics.getPercentile(0.25);
                      upperQuartile += statistics.getPercentile(0.75);
                    }
                    catch (NoMeasurementException nme) {
                      // skip this sensor
                      ret.addMissingSensor(s);
                    }
                  }
                  if (maximum < groupMax) {
                    maximum = groupMax;
                  }
                  if (minimum > groupMin && groupMin != 0.0) {
                    minimum = groupMin;
                  }
                  average += groupAve;
                  upQuartile += upperQuartile;
                  lowQuartile += lowerQuartile;
                }
              }
            }
            else { // difference values
              if (sensor != null) {
                ret.addDefinedSensor(sensorId);
                statistics.addValue(depot.getValue(depositoryId, orgId, sensorId, DateConvert.convertXMLCal(begin), DateConvert.convertXMLCal(end), false));
                ret.addReportingSensor(sensorId);
              }
              else {
                SensorGroup group = depot.getSensorGroup(sensorId, orgId, false);
                if (group != null) {
                  Double d = 0.0;
                  for (String s : group.getSensors()) {
                    try {
                      ret.addDefinedSensor(s);
                      d += depot.getValue(depositoryId, orgId, s, DateConvert.convertXMLCal(begin), DateConvert.convertXMLCal(end), false);
                      ret.addReportingSensor(s);
                    }
                    catch (NoMeasurementException nme) {
                      // just skip this sensor.
                      ret.addMissingSensor(s);
                    }
                  }
                  statistics.addValue(d);
                }
              }
            }
          }
          if (valueType.equals(Labels.POINT)) {
            average /= samples;
            lowQuartile /= samples;
            upQuartile /= samples;
          }
          else {
            average = statistics.getMean();
            minimum = statistics.getMin();
            maximum = statistics.getMax();
            lowQuartile = statistics.getPercentile(25.0);
            upQuartile = statistics.getPercentile(75.0);
          }
          ret.setDepositoryId(depositoryId);
          ret.setSensorId(sensorId);
          ret.setAverage(average);
          ret.setMaximum(maximum);
          ret.setMinimum(minimum);
          ret.setLowerQuartile(lowQuartile);
          ret.setUpperQuartile(upQuartile);
          ret.setNumSamples(samples);
          ret.setWindowWidth(hourlyDailyChoice);
          ret.setValueType(valueType);
          ret.setTimestamp(DateConvert.parseCalStringToDate(timestamp));
          return ret;
        }
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
        setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
        return null;
      }
      catch (MisMatchedOwnerException e) {
        setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
        return null;
      }
      catch (NoMeasurementException e) {
        setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
        return null;
      }
      catch (CounterRollOverException e) {
        setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
        return null;
      }
      return null;
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

  /**
   * @param depositoryId the depository id.
   * @param sensorId     the sensor id.
   * @param begin        the begin time.
   * @param end          the end time.
   * @return DescriptiveStatistics of the measurements for the peroiod.
   * @throws IdNotFoundException    if there is a problem with the sensorId.
   * @throws NoMeasurementException if there are no measurements during the period.
   */
  private DescriptiveStatistics getStatistics(String depositoryId, String sensorId, XMLGregorianCalendar begin, XMLGregorianCalendar end) throws IdNotFoundException, NoMeasurementException {
    List<Measurement> measurements = depot.getMeasurements(depositoryId, orgId, sensorId, DateConvert.convertXMLCal(begin), DateConvert.convertXMLCal(end), false);
    if (measurements == null || measurements.size() == 0) {
      throw new NoMeasurementException("No measurements for " + sensorId + " from " + begin.toString() + " to " + end.toString());
    }
    DescriptiveStatistics ret = new DescriptiveStatistics();
    for (Measurement m : measurements) {
      ret.addValue(m.getValue());
    }
    return ret;
  }

  /**
   * @param depositoryId the depository id.
   * @param sensorId     the sensor id.
   * @param begin        the begin time.
   * @param end          the end time.
   * @param statistics   the statistics object to update.
   * @return the updated object.
   * @throws IdNotFoundException if there is a problem with the sensorId.
   */
  private DescriptiveStatistics updateStatistics(String depositoryId, String sensorId, XMLGregorianCalendar begin, XMLGregorianCalendar end, DescriptiveStatistics statistics) throws IdNotFoundException {
    List<Measurement> measurements = depot.getMeasurements(depositoryId, orgId, sensorId, DateConvert.convertXMLCal(begin), DateConvert.convertXMLCal(end), false);
    for (Measurement m : measurements) {
      statistics.addValue(m.getValue());
    }
    return statistics;
  }
}
