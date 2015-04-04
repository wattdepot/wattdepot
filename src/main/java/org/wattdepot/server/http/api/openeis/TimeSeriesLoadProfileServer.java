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

package org.wattdepot.server.http.api.openeis;

import org.restlet.data.Status;
import org.restlet.resource.ResourceException;
import org.wattdepot.common.domainmodel.Depository;
import org.wattdepot.common.domainmodel.InterpolatedValue;
import org.wattdepot.common.domainmodel.InterpolatedValueList;
import org.wattdepot.common.domainmodel.Labels;
import org.wattdepot.common.domainmodel.Measurement;
import org.wattdepot.common.exception.IdNotFoundException;
import org.wattdepot.common.util.tstamp.Tstamp;
import org.wattdepot.server.http.api.WattDepotServerResource;

import javax.xml.datatype.XMLGregorianCalendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;

/**
 * TimeSeriesLoadProfileServer - Base class for handling Time Series Load Profile requests.
 *
 * @author Cam Moore
 */
public class TimeSeriesLoadProfileServer extends WattDepotServerResource {
  private String depositoryId;
  private String sensorId;


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
  }

  /**
   * Retrieves the last month's hourly power data for the given depository and sensor.
   * @return An InterpolatedValueList of the hourly power data.
   */
  public InterpolatedValueList doRetrieve() {
    getLogger().log(
        Level.INFO,
        "GET /wattdepot/{" + orgId + "}/" + Labels.OPENEIS + "/" + Labels.TIME_SERIES_LOAD_PROFILING +
            "/?" + Labels.DEPOSITORY + "={" + depositoryId + "}&" + Labels.SENSOR + "={" + sensorId + "}");
    if (isInRole(orgId)) {
      InterpolatedValueList ret = new InterpolatedValueList();
      try {
        Depository depository = depot.getDepository(depositoryId, orgId, true);
        if (depository.getMeasurementType().getName().startsWith("Power")) {
          XMLGregorianCalendar now = Tstamp.makeTimestamp();
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
        }
        else {
          setStatus(Status.CLIENT_ERROR_BAD_REQUEST, "Wrong Measurement type.");
          return null;
        }
      }
      catch (IdNotFoundException e) {
        setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
        return null;
      }
      return ret;
    }
    else {
      setStatus(Status.CLIENT_ERROR_BAD_REQUEST, "Bad credentials.");
      return null;
    }
  }
}
