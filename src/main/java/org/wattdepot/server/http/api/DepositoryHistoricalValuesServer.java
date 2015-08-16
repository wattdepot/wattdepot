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
import org.wattdepot.common.domainmodel.HistoricalValues;
import org.wattdepot.common.domainmodel.Labels;
import org.wattdepot.common.domainmodel.Measurement;
import org.wattdepot.common.domainmodel.Sensor;
import org.wattdepot.common.domainmodel.SensorGroup;
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
 * DepositoryHistoricalValuesServer - Base class for handling historical values HTTP requests.
 *
 * @author Cam Moore
 */
public class DepositoryHistoricalValuesServer extends WattDepotServerResource {
  private String depositoryId;
  private String hourlyDailyChoice;
  private String sensorId;
  private String timestamp;
  private String valueType;
  private Integer samples;

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
   * @return the HistoricalValues for the given request.
   */
  public HistoricalValues doRetrieve() {
    getLogger().log(
        Level.INFO,
        "GET /wattdepot/{" + orgId + "}/" + Labels.DEPOSITORY + "/{" + depositoryId + "}/" + hourlyDailyChoice + "/"
            + Labels.HISTORICAL_VALUES + "/?" + Labels.SENSOR + "={" + sensorId + "}&" + Labels.TIMESTAMP + "={"
            + timestamp + "}&" + Labels.VALUE_TYPE + "={" + valueType + "}&" + Labels.SAMPLES + "={" + samples + "}");
    if (isInRole(orgId)) {
      HistoricalValues ret = null;
      try {
        Depository depository = depot.getDepository(depositoryId, orgId, true);
        if (depository != null) {
          XMLGregorianCalendar time = DateConvert.parseCalString(timestamp);
          XMLGregorianCalendar begin = getBeginning(time, hourlyDailyChoice);
          XMLGregorianCalendar end = getEnding(time, hourlyDailyChoice);
          DescriptiveStatistics statistics = new DescriptiveStatistics();
          for (int i = 0; i < samples; i++) {
            begin = Tstamp.incrementDays(begin, -7); // back it up a week
            end = Tstamp.incrementDays(end, -7);
            try {
              Double val = getValue(depositoryId, orgId, sensorId, begin, end, valueType);
              if (val != null) {
                statistics.addValue(val);
              }
            }
            catch (NoMeasurementException e) { //NOPMD
              // do nothing since there aren't any measurements doesn't contribute to the stats.
            }
            catch (MisMatchedOwnerException e) { //NOPMD
//              e.printStackTrace();
            }
          }
          ret = new HistoricalValues();
          ret.setDepositoryId(depositoryId);
          ret.setSensorId(sensorId);
          ret.setAverage(statistics.getMean());
          ret.setMaximum(statistics.getMax());
          ret.setMinimum(statistics.getMin());
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

      return ret;
    }
    return null;
  }

  /**
   * @param depositoryId the depository id.
   * @param orgId the organization id.
   * @param sensorId the sensor or sensor group id.
   * @param begin the begin time.
   * @param end the end time.
   * @param valueType point or difference.
   * @return the value of the sensor measurements over the time interval either an average or a difference.
   * @throws NoMeasurementException if there are no measurements in the interval.
   * @throws IdNotFoundException if the depository or sensor are not defined.
   * @throws MisMatchedOwnerException if the depository or sensor are not in the organization.
   */
  private Double getValue(String depositoryId, String orgId, String sensorId, XMLGregorianCalendar begin, XMLGregorianCalendar end, String valueType) throws NoMeasurementException, IdNotFoundException, MisMatchedOwnerException {
    Double ret = null;
    if (valueType.equals(Labels.POINT)) { // since it is point data must calculate the average of the measurements
      Sensor sensor = depot.getSensor(sensorId, orgId, false);
      if (sensor != null) {
        List<Measurement> measurementList = depot.getMeasurements(depositoryId, orgId, sensorId, DateConvert.convertXMLCal(begin), DateConvert.convertXMLCal(end), false);
        ret = 0.0;
        for (Measurement m : measurementList) {
          ret += m.getValue();
        }
        ret /= measurementList.size();
      }
      else {
        SensorGroup group = depot.getSensorGroup(sensorId, orgId, false);
        if (group != null) {
          ret = 0.0;
          for (String s : group.getSensors()) {
            Double temp = 0.0;
            List<Measurement> measurementList = depot.getMeasurements(depositoryId, orgId, s, DateConvert.convertXMLCal(begin), DateConvert.convertXMLCal(end), false);
            for (Measurement m : measurementList) {
              temp += m.getValue();
            }
            if (measurementList.size() > 0) {
              temp /= measurementList.size();
            }
            ret += temp;
          }
          ret /= group.getSensors().size();
        }
      }
    }
    else {
      Sensor sensor = depot.getSensor(sensorId, orgId, false);
      if (sensor != null) {
        ret = depot.getValue(depositoryId, orgId, sensorId, DateConvert.convertXMLCal(begin), DateConvert.convertXMLCal(end), false);
      }
      else {
        SensorGroup group = depot.getSensorGroup(sensorId, orgId, false);
        if (group != null) {
          ret = 0.0;
          for (String s : group.getSensors()) {
            ret += depot.getValue(depositoryId, orgId, s, DateConvert.convertXMLCal(begin), DateConvert.convertXMLCal(end), false);
          }
        }
      }
    }
    return ret;
  }

  /**
   * @param time the current time.
   * @param hourlyDailyChoice how wide the window should be.
   * @return the beginning of the window based upon the current time and window width.
   */
  private XMLGregorianCalendar getBeginning(XMLGregorianCalendar time, String hourlyDailyChoice) {
    XMLGregorianCalendar begin = null;
    if (hourlyDailyChoice.equals(Labels.HOURLY)) {
      begin = Tstamp.makeTimestamp(time.toGregorianCalendar().getTimeInMillis());
      begin.setMinute(0);
      begin.setSecond(0);
      begin.setMillisecond(0);
    }
    else if (hourlyDailyChoice.equals(Labels.DAILY)) {
      begin = Tstamp.makeTimestamp(time.toGregorianCalendar().getTimeInMillis());
      begin.setHour(0);
      begin.setMinute(0);
      begin.setSecond(0);
      begin.setMillisecond(0);
    }
    return begin;
  }

  /**
   * @param time the current time.
   * @param hourlyDailyChoice the width of the window.
   * @return the end of the window based upon the current time and the window width.
   */
  private XMLGregorianCalendar getEnding(XMLGregorianCalendar time, String hourlyDailyChoice) {
    XMLGregorianCalendar end = null;
    if (hourlyDailyChoice.equals(Labels.HOURLY)) {
      end = Tstamp.incrementHours(time, 1);
      end.setMinute(0);
      end.setSecond(0);
      end.setMillisecond(0);
    }
    else if (hourlyDailyChoice.equals(Labels.DAILY)) {
      end = Tstamp.incrementDays(time, 1);
      end.setHour(0);
      end.setMinute(0);
      end.setSecond(0);
      end.setMillisecond(0);
    }
    return end;
  }
}
