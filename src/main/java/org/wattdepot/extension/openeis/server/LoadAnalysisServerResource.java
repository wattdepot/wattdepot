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

package org.wattdepot.extension.openeis.server;

import org.restlet.data.Status;
import org.restlet.resource.ResourceException;
import org.wattdepot.common.domainmodel.Depository;
import org.wattdepot.common.domainmodel.InterpolatedValue;
import org.wattdepot.common.domainmodel.InterpolatedValueList;
import org.wattdepot.common.domainmodel.InterpolatedValueValueComparator;
import org.wattdepot.common.domainmodel.Labels;
import org.wattdepot.common.domainmodel.Measurement;
import org.wattdepot.common.exception.IdNotFoundException;
import org.wattdepot.common.exception.NoMeasurementException;
import org.wattdepot.common.util.tstamp.Tstamp;
import org.wattdepot.extension.openeis.OpenEISLabels;
import org.wattdepot.extension.openeis.domainmodel.LoadAnalysis;
import org.wattdepot.extension.openeis.http.api.LoadAnalysisResource;

import javax.xml.datatype.XMLGregorianCalendar;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;

/**
 * LoadAnalysisServerResource - Handles LoadAnalysis request.
 *
 * @author Cam Moore
 *         Created by carletonmoore on 4/20/15.
 */
public class LoadAnalysisServerResource extends OpenEISServer implements LoadAnalysisResource {
  private String depositoryId;
  private String sensorId;
  private String startString;
  private String endString;

  /*
 * (non-Javadoc)
 *
 * @see org.restlet.resource.Resource#doInit()
 */
  @Override
  protected void doInit() throws ResourceException {
    super.doInit();
    this.depositoryId = getQuery().getValues(Labels.DEPOSITORY);
    this.sensorId = getQuery().getValues(Labels.SENSOR);
    this.startString = getQuery().getValues(Labels.START);
    this.endString = getQuery().getValues(Labels.END);
  }

  @Override
  public LoadAnalysis retrieve() {
    getLogger().log(
        Level.INFO,
        "GET /wattdepot/{" + orgId + "}/" + OpenEISLabels.OPENEIS + "/" + OpenEISLabels.LOAD_ANALYSIS +
            "/?" + Labels.DEPOSITORY + "={" + depositoryId + "}&" + Labels.SENSOR + "={" + sensorId + "}&" +
            Labels.START + "={" + startString + "}&" + Labels.END + "={" + endString + "}");
    try {
      Depository depository = depot.getDepository(depositoryId, orgId, true);
      Date start = Tstamp.makeTimestamp(startString).toGregorianCalendar().getTime();
      Date end = Tstamp.makeTimestamp(endString).toGregorianCalendar().getTime();
      InterpolatedValueList values;
      if (depository.getMeasurementType().getName().startsWith("Power")) {
        values = getHourlyPointData(depositoryId, sensorId, start, end, false);
      }
      else if (depository.getMeasurementType().getName().startsWith("Energy")) {
        values = getHourlyDifferenceData(depositoryId, sensorId, start, end);
      }
      else {
        setStatus(Status.CLIENT_ERROR_BAD_REQUEST, depositoryId + " is not a Power Depository.");
        return null;
      }
      InterpolatedValueValueComparator c = new InterpolatedValueValueComparator();
      Double peak = Collections.max(values.getInterpolatedValues(), c).getValue();
      int numValues = values.getInterpolatedValues().size();
      int numDays = numValues / 24;
      int hour = 0;
      Double dailyMax = Double.MIN_VALUE;
      Double dailyMin = Double.MAX_VALUE;
      Double aveDailyMax = 0.0;
      Double aveDailyMin = 0.0;
      Double aveDailyRange = 0.0;
      for (InterpolatedValue val : values.getInterpolatedValues()) {
        Double v = val.getValue();
        if (dailyMax < v) {
          dailyMax = v;
        }
        if (dailyMin > v) {
          dailyMin = v;
        }
        if (hour == 23) {
          aveDailyMax += dailyMax;
          aveDailyMin += dailyMin;
          aveDailyRange += dailyMax - dailyMin;
          dailyMax = Double.MIN_VALUE;
          dailyMin = Double.MAX_VALUE;
          hour = 0;
        }
        hour++;
      }
      aveDailyMax = aveDailyMax / numDays;
      aveDailyMin = aveDailyMin / numDays;
      aveDailyRange = aveDailyRange / numDays;
      ArrayList<InterpolatedValue> list = values.getInterpolatedValues();
      Collections.sort(list, c);
      int fifthPercentileIndex = (int) Math.round(numValues * 0.05);
      int nintyFifthPercentileIndex = (int) Math.round(numValues * 0.95);
      Double fifth = list.get(fifthPercentileIndex).getValue();
      Double nintyFifth = list.get(nintyFifthPercentileIndex).getValue();
      LoadAnalysis analysis = new LoadAnalysis(start, end, peak, aveDailyMax, aveDailyMin, aveDailyRange, fifth / nintyFifth);
      return analysis;
    }
    catch (IdNotFoundException e) {
      setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
      return null;
    }
    catch (Exception e) {
      setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
      return null;
    }
  }

  /**
   * Returns the hourly difference data as an InterpolatedValueList.
   *
   * @param depositoryId The Depository.
   * @param sensorId     The Sensor.
   * @param start        The start of the period.
   * @param end          The end of the period.
   * @return An InterpolatedValueList of the hourly differences.
   */
  private InterpolatedValueList getHourlyDifferenceData(String depositoryId, String sensorId, Date start, Date end) {
    if (isInRole(orgId)) {
      try {
        Depository depository = depot.getDepository(depositoryId, orgId, true);
        XMLGregorianCalendar startCal = Tstamp.makeTimestamp(start.getTime());
        startCal.setTime(0, 0, 0, 0);
        XMLGregorianCalendar endCal = Tstamp.makeTimestamp(end.getTime());
        endCal.setTime(0, 0, 0, 0);
        endCal = Tstamp.incrementDays(endCal, 1);
        List<XMLGregorianCalendar> times = Tstamp.getTimestampList(startCal, endCal, 60);
        InterpolatedValueList ret = new InterpolatedValueList();
        for (int i = 1; i < times.size(); i++) {
          XMLGregorianCalendar begin = times.get(i - 1);
          Date beginDate = begin.toGregorianCalendar().getTime();
          XMLGregorianCalendar stop = times.get(i);
          Date endDate = stop.toGregorianCalendar().getTime();
          Double val = null;
          try {
            val = depot.getValue(depositoryId, orgId, sensorId, beginDate, endDate, false);
          }
          catch (NoMeasurementException e) { // if there are no measurements just add null
            val = null;
          }
          ret.getInterpolatedValues().add(new InterpolatedValue(sensorId, val, depository.getMeasurementType(), beginDate, endDate));
        }
        return ret;
      }
      catch (IdNotFoundException e) {
        setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
        return null;
      }
    }
    else {
      setStatus(Status.CLIENT_ERROR_BAD_REQUEST, "Bad credentials.");
      return null;
    }
  }

  /**
   * Returns the hourly average data as an InterpolatedValueList.
   *
   * @param depositoryId The Depository.
   * @param sensorId     The Sensor.
   * @param start        The start of the period.
   * @param end          The end of the period.
   * @param keepNulls    if true insert InterpolatedValues with a null value.
   * @return An InterpolatedValueList of the hourly averages.
   */
  private InterpolatedValueList getHourlyPointData(String depositoryId, String sensorId, Date start, Date end, boolean keepNulls) {
    if (isInRole(orgId)) {
      InterpolatedValueList ret = new InterpolatedValueList();
      try {
        Depository depository = depot.getDepository(depositoryId, orgId, true);
        XMLGregorianCalendar startCal = Tstamp.makeTimestamp(start.getTime());
        startCal.setTime(0, 0, 0, 0);
        XMLGregorianCalendar endCal = Tstamp.makeTimestamp(end.getTime());
        endCal.setTime(0, 0, 0, 0);
        endCal = Tstamp.incrementDays(endCal, 1);
        List<XMLGregorianCalendar> times = Tstamp.getTimestampList(startCal, endCal, 60);
        for (int i = 1; i < times.size(); i++) {
          XMLGregorianCalendar begin = times.get(i - 1);
          Date beginDate = begin.toGregorianCalendar().getTime();
          XMLGregorianCalendar stop = times.get(i);
          Date endDate = stop.toGregorianCalendar().getTime();
          Double val = 0.0;
          List<Measurement> measurements = depot.getMeasurements(depositoryId, orgId, sensorId, beginDate, endDate, false);
          if (measurements.size() > 0) {
            for (Measurement m : measurements) {
              val += m.getValue();
            }
            val = val / measurements.size();
          }
          else {
            val = null;
          }
          if (!keepNulls) {
            if (val != null) {
              ret.getInterpolatedValues().add(new InterpolatedValue(sensorId, val, depository.getMeasurementType(), beginDate, endDate));
            }
          }
          else {
            ret.getInterpolatedValues().add(new InterpolatedValue(sensorId, val, depository.getMeasurementType(), beginDate, endDate));
          }
        }
        return ret;
      }
      catch (IdNotFoundException e) {
        setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
        return null;
      }
    }
    else {
      setStatus(Status.CLIENT_ERROR_BAD_REQUEST, "Bad credentials.");
      return null;
    }
  }
}
