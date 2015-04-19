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
   * @return An InterpolatedValueList of the hourly data.
   */
  public InterpolatedValueList getHourlyPointData(String depositoryId, String sensorId, TimeInterval howLong) {
    if (isInRole(orgId)) {
      InterpolatedValueList ret = new InterpolatedValueList();
      try {
        Depository depository = depot.getDepository(depositoryId, orgId, true);
        XMLGregorianCalendar now = Tstamp.makeTimestamp();
        now.setTime(0, 0, 0, 0); // set now to midnight
        XMLGregorianCalendar past = Tstamp.incrementDays(now, howLong.getNumDays() * -1);
        List<XMLGregorianCalendar> times = Tstamp.getTimestampList(past, now, 60);
//        for (int i = 1; i < times.size(); i++) {
        for (int i = times.size() - 1; i > 0; i--) {
          XMLGregorianCalendar begin = times.get(i - 1);
          Date beginDate = begin.toGregorianCalendar().getTime();
          XMLGregorianCalendar end = times.get(i);
          Date endDate = end.toGregorianCalendar().getTime();
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
   * Returns the average point data for the last month.
   *
   * @param depositoryId The Depository.
   * @param sensorId     The Sensor
   * @return An InterpolatedValueList of the hourly data.
   */
  public InterpolatedValueList getHourlyPointDataMonth(String depositoryId, String sensorId) {
    return getHourlyPointData(depositoryId, sensorId, TimeInterval.ONE_MONTH);
  }

  /**
   * Returns the average point data for the last year.
   *
   * @param depositoryId The Depository.
   * @param sensorId     The Sensor
   * @return An InterpolatedValueList of the hourly data.
   */
  public InterpolatedValueList getHourlyPointDataYear(String depositoryId, String sensorId) {
    return getHourlyPointData(depositoryId, sensorId, TimeInterval.ONE_YEAR);
  }

  /**
   * Returns the hourly difference data for the given depositoryId, sensorId, and for the time interval.
   *
   * @param depositoryId The Depository.
   * @param sensorId     The Sensor collecting the data.
   * @param howLong      The length of time.
   * @return An InterpolatedValueList of the hourly data.
   */
  public InterpolatedValueList getHourlyDifferenceData(String depositoryId, String sensorId, TimeInterval howLong) {
    if (isInRole(orgId)) {
      InterpolatedValueList ret = new InterpolatedValueList();
      try {
        Depository depository = depot.getDepository(depositoryId, orgId, true);
        XMLGregorianCalendar now = Tstamp.makeTimestamp();
        now.setTime(0, 0, 0, 0); // set now to midnight
        XMLGregorianCalendar past = Tstamp.incrementDays(now, howLong.getNumDays() * -1);
        List<XMLGregorianCalendar> times = Tstamp.getTimestampList(past, now, 60);
        for (int i = 1; i < times.size(); i++) {
          XMLGregorianCalendar begin = times.get(i - 1);
          Date beginDate = begin.toGregorianCalendar().getTime();
          XMLGregorianCalendar end = times.get(i);
          Date endDate = end.toGregorianCalendar().getTime();
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
   * Returns an InterpolatedValueList of the values starting at start with an interval of howLong.
   *
   * @param depositoryId The Depository.
   * @param sensorId     The Sensor.
   * @param start        The start date.
   * @param howLong      The period to calculate the difference.
   * @param numIntervals How many values to calculate.
   * @return an InterpolatedValueList of the values.
   */
  public InterpolatedValueList getDifferenceValues(String depositoryId, String sensorId, Date start, TimeInterval howLong, int numIntervals) {
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
          ret.getInterpolatedValues().add(new InterpolatedValue(sensorId, val, depository.getMeasurementType(), begin, end));
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
   * Returns the InterpolatedValues one per year starting at the earliest measurement.
   *
   * @param depositoryId The Depository Id.
   * @param sensorId     The Sensor Id.
   * @param strict       if true method will return null if there isn't enough data.
   * @return The list of difference values one per year for the given depository and sensor.
   */
  public InterpolatedValueList getAnnualDifferenceData(String depositoryId, String sensorId, boolean strict) {
    if (isInRole(orgId)) {
      InterpolatedValueList ret = new InterpolatedValueList();
      try {
        Depository depository = depot.getDepository(depositoryId, orgId, true);
        InterpolatedValue earliest = depot.getEarliestMeasuredValue(depositoryId, orgId, sensorId, true);
        InterpolatedValue latest = depot.getLatestMeasuredValue(depositoryId, orgId, sensorId, true);
        XMLGregorianCalendar first = Tstamp.makeTimestamp(earliest.getEnd().getTime());
        XMLGregorianCalendar last = Tstamp.makeTimestamp(latest.getEnd().getTime());
        if (strict && Tstamp.daysBetween(first, last) < 365) {
          setStatus(Status.SERVER_ERROR_INTERNAL, "Not enough measurements for " + sensorId + " need at least 1 years worth of measurements.");
          return null;
        }
        List<XMLGregorianCalendar> times = Tstamp.getTimestampList(first, last, 60 * 24 * 365);
        for (int i = 1; i < times.size(); i++) {
          XMLGregorianCalendar begin = times.get(i - 1);
          Date beginDate = begin.toGregorianCalendar().getTime();
          XMLGregorianCalendar end = times.get(i);
          Date endDate = end.toGregorianCalendar().getTime();
          Double val = depot.getValue(depositoryId, orgId, sensorId, beginDate, endDate, false);
          ret.getInterpolatedValues().add(new InterpolatedValue(sensorId, val, depository.getMeasurementType(), beginDate, endDate));
        }
        return ret;
      }
      catch (IdNotFoundException e) {
        setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
        return null;
      }
      catch (NoMeasurementException e) {
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
