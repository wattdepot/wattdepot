package org.wattdepot.extension.openeis.server;

import org.restlet.data.Status;
import org.wattdepot.common.domainmodel.Depository;
import org.wattdepot.common.domainmodel.InterpolatedValue;
import org.wattdepot.common.domainmodel.InterpolatedValueList;
import org.wattdepot.common.domainmodel.Measurement;
import org.wattdepot.common.exception.IdNotFoundException;
import org.wattdepot.common.exception.NoMeasurementException;
import org.wattdepot.common.util.tstamp.Tstamp;
import org.wattdepot.extension.openeis.domainmodel.TimeInterval;
import org.wattdepot.server.http.api.WattDepotServerResource;

import javax.xml.datatype.XMLGregorianCalendar;
import java.util.Date;
import java.util.List;

/**
 * OpenEISServer - Base class that provides functionality for getting Hourly average values for point data and
 * Hourly difference values.
 *
 * @author Cam Moore
 */
public class OpenEISServer extends WattDepotServerResource {

  /**
   * Returns the hourly point data for the given depositoryId, sensorId, and for the time interval.
   *
   * @param depositoryId The Depository.
   * @param sensorId     The Sensor collecting the data.
   * @param howLong      The length of time.
   * @param keepNulls    if true returns InterpolatedValues with a value of null for missing data.
   * @return An InterpolatedValueList of the hourly data.
   */
  public InterpolatedValueList getHourlyPointData(String depositoryId, String sensorId, TimeInterval howLong, boolean keepNulls) {
    XMLGregorianCalendar now = Tstamp.makeTimestamp();
    now.setTime(0, 0, 0, 0); // set now to midnight
    Date nowDate = now.toGregorianCalendar().getTime();
    XMLGregorianCalendar past = Tstamp.incrementDays(now, howLong.getNumDays() * -1);
    Date pastDate = past.toGregorianCalendar().getTime();
    return getHourlyPointData(depositoryId, sensorId, pastDate, nowDate, keepNulls);
  }

  /**
   * Returns the average point data for the last year.
   *
   * @param depositoryId The Depository.
   * @param sensorId     The Sensor
   * @param keepNulls    if true returns InterpolatedValues with a value of null for missing data.
   * @return An InterpolatedValueList of the hourly data.
   */
  public InterpolatedValueList getHourlyPointDataYear(String depositoryId, String sensorId, boolean keepNulls) {
    return getHourlyPointData(depositoryId, sensorId, TimeInterval.ONE_YEAR, keepNulls);
  }

  /**
   * Returns the hourly difference data for the given depositoryId, sensorId, and for the time interval.
   *
   * @param depositoryId The Depository.
   * @param sensorId     The Sensor collecting the data.
   * @param howLong      The length of time.
   * @param keepNulls    if true returns InterpolatedValues with a value of null for missing data.
   * @return An InterpolatedValueList of the hourly data.
   */
  public InterpolatedValueList getHourlyDifferenceData(String depositoryId, String sensorId, TimeInterval howLong, boolean keepNulls) {
    XMLGregorianCalendar now = Tstamp.makeTimestamp();
    now.setTime(0, 0, 0, 0); // set now to midnight
    Date nowDate = now.toGregorianCalendar().getTime();
    XMLGregorianCalendar past = Tstamp.incrementDays(now, howLong.getNumDays() * -1);
    Date pastDate = past.toGregorianCalendar().getTime();
    return getHourlyDifferenceData(depositoryId, sensorId, pastDate, nowDate, keepNulls);
  }

  /**
   * Returns an InterpolatedValueList of the values starting at start with an interval of howLong.
   *
   * @param depositoryId The Depository.
   * @param sensorId     The Sensor.
   * @param start        The start date.
   * @param howLong      The period to calculate the difference.
   * @param numIntervals How many values to calculate.
   * @param keepNulls    if true returns InterpolatedValues with a value of null for missing data.
   * @return an InterpolatedValueList of the values.
   */
  public InterpolatedValueList getDifferenceValues(String depositoryId, String sensorId, Date start, TimeInterval howLong, int numIntervals, boolean keepNulls) {
    if (isInRole(orgId)) {
      InterpolatedValueList ret = new InterpolatedValueList();
      try {
        Depository depository = depot.getDepository(depositoryId, orgId, true);
        XMLGregorianCalendar startCal = Tstamp.makeTimestamp(start.getTime());
        startCal.setTime(0, 0, 0, 0); // start at the beginning of the day.
        XMLGregorianCalendar endCal = Tstamp.incrementDays(startCal, howLong.getNumDays());
        for (int i = 0; i < numIntervals; i++) {
          Date begin = startCal.toGregorianCalendar().getTime();
          Date end = endCal.toGregorianCalendar().getTime();
          Double val = null;
          try {
            val = depot.getValue(depositoryId, orgId, sensorId, begin, end, false);
          }
          catch (NoMeasurementException e) {
            val = null;
          }
          InterpolatedValue v = new InterpolatedValue(sensorId, val, depository.getMeasurementType(), begin, end);
          if (val == null) {
            ret.getMissingData().add(v);
            if (keepNulls) {
              ret.getInterpolatedValues().add(v);
            }
          }
          else {
            ret.getInterpolatedValues().add(v);
          }
          startCal = Tstamp.incrementDays(startCal, howLong.getNumDays());
          endCal = Tstamp.incrementDays(endCal, howLong.getNumDays());
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
   * Returns the hourly difference data as an InterpolatedValueList.
   *
   * @param depositoryId The Depository.
   * @param sensorId     The Sensor.
   * @param start        The start of the period.
   * @param end          The end of the period.
   * @param keepNulls    if true inserts InterpolatedValues with a value of null in the return list.
   * @return An InterpolatedValueList of the hourly differences.
   */
  public InterpolatedValueList getHourlyDifferenceData(String depositoryId, String sensorId, Date start, Date end, boolean keepNulls) {
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
          InterpolatedValue iv = new InterpolatedValue(sensorId, val, depository.getMeasurementType(), beginDate, endDate);
          if (!keepNulls) {
            if (val != null) {
              ret.getInterpolatedValues().add(iv);
            }
            else {
              ret.getMissingData().add(iv);
            }
          }
          else {
            ret.getInterpolatedValues().add(iv);
            if (val == null) {
              ret.getMissingData().add(iv);
            }
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
  public InterpolatedValueList getHourlyPointData(String depositoryId, String sensorId, Date start, Date end, boolean keepNulls) {
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
          InterpolatedValue iv = new InterpolatedValue(sensorId, val, depository.getMeasurementType(), beginDate, endDate);
          if (!keepNulls) {
            if (val != null) {
              ret.getInterpolatedValues().add(iv);
            }
            else {
              ret.getMissingData().add(iv);
            }
          }
          else {
            ret.getInterpolatedValues().add(iv);
            if (val == null) {
              ret.getMissingData().add(iv);
            }
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
