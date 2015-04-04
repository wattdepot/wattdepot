package org.wattdepot.server.http.api.openeis;

import org.restlet.data.Status;
import org.wattdepot.common.domainmodel.Depository;
import org.wattdepot.common.domainmodel.InterpolatedValue;
import org.wattdepot.common.domainmodel.InterpolatedValueList;
import org.wattdepot.common.domainmodel.Measurement;
import org.wattdepot.common.exception.IdNotFoundException;
import org.wattdepot.common.util.tstamp.Tstamp;
import org.wattdepot.server.http.api.WattDepotServerResource;

import javax.xml.datatype.XMLGregorianCalendar;
import java.util.Date;
import java.util.List;

/**
 * OpenEISServer - Base class that provides functionality for getting Hourly average values for point data and
 * Hourly difference values.
 * @author Cam Moore
 */
public class OpenEISServer extends WattDepotServerResource {

  /**
   * Returns the average point data for the last month.
   * @param depositoryId The Depository.
   * @param sensorId The Sensor
   * @return An InterpolatedValueList of the hourly data.
   */
  InterpolatedValueList getHourlyPointDataMonth(String depositoryId, String sensorId) {
    if (isInRole(orgId)) {
      InterpolatedValueList ret = new InterpolatedValueList();
      try {
        Depository depository = depot.getDepository(depositoryId, orgId, true);
        XMLGregorianCalendar now = Tstamp.makeTimestamp();
        now.setTime(0,0,0,0); // set now to midnight
        XMLGregorianCalendar monthAgo = Tstamp.incrementDays(now, -30);
        List<XMLGregorianCalendar> times = Tstamp.getTimestampList(monthAgo, now, 60);
        for (int i = 1; i < times.size(); i++) {
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
            val = Double.NaN;
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
   * Returns the average point data for the last year.
   * @param depositoryId The Depository.
   * @param sensorId The Sensor
   * @return An InterpolatedValueList of the hourly data.
   */
  InterpolatedValueList getHourlyPointDataYear(String depositoryId, String sensorId) {
    if (isInRole(orgId)) {
      InterpolatedValueList ret = new InterpolatedValueList();
      try {
        Depository depository = depot.getDepository(depositoryId, orgId, true);
        XMLGregorianCalendar now = Tstamp.makeTimestamp();
        XMLGregorianCalendar yearAgo = Tstamp.incrementDays(now, -365);
        List<XMLGregorianCalendar> times = Tstamp.getTimestampList(yearAgo, now, 60);
        for (int i = 1; i < times.size(); i++) {
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
            val = Double.NaN;
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
}
