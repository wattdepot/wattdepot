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
import org.wattdepot.common.exception.IdNotFoundException;
import org.wattdepot.common.exception.NoMeasurementException;
import org.wattdepot.common.util.DateConvert;
import org.wattdepot.common.util.tstamp.Tstamp;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.XMLGregorianCalendar;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;

/**
 * DepositoryHourlyServer - Base class for handling hourly sample HTTP requests.
 *
 * @author Cam Moore
 */
public class DepositoryHourlyValuesServer extends WattDepotServerResource {
  private String depositoryId;
  private String sensorId;
  private String start;
  private String end;
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
    this.dataType = getQuery().getValues(Labels.VALUE_TYPE);
  }


  /**
   * retrieve the hourly depository value list for a sensor.
   *
   * @return measurement list.
   */
  public InterpolatedValueList doRetrieve() {
    getLogger().log(
        Level.INFO,
        "GET /wattdepot/{" + orgId + "}/" + Labels.DEPOSITORY + "/{" + depositoryId + "}/" + Labels.HOURLY + "/"
            + Labels.VALUES + "/?" + Labels.SENSOR + "={" + sensorId + "}&" + Labels.START + "={"
            + start + "}&" + Labels.END + "={" + end + "}&" + Labels.VALUE_TYPE + "={" + dataType + "}");
    if (isInRole(orgId)) {
      if (start != null && end != null && dataType != null) {
        InterpolatedValueList ret = new InterpolatedValueList();
        try {
          Depository depository = depot.getDepository(depositoryId, orgId, true);
          if (depository != null) {
            XMLGregorianCalendar startTime = DateConvert.parseCalString(start);
            startTime.setTime(0, 0, 0, 0); // beginning of the day
            XMLGregorianCalendar endTime = DateConvert.parseCalString(end);
            endTime = Tstamp.incrementDays(endTime, 1);
            endTime.setTime(0, 0, 0, 0); // beginning of the next day.
            List<XMLGregorianCalendar> times = Tstamp.getTimestampList(startTime, endTime, 60);
            for (int i = 1; i < times.size(); i++) {
              XMLGregorianCalendar begin = times.get(i - 1);
              Date beginDate = begin.toGregorianCalendar().getTime();
              XMLGregorianCalendar end = times.get(i);
              Date endDate = end.toGregorianCalendar().getTime();
              Double val = 0.0;
              if (dataType.equals("point")) {  // need to calculate the average value for the hourly intervals
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
              }
              else {  // calculate the difference
                try {
                  val = depot.getValue(depositoryId, orgId, sensorId, beginDate, endDate, false);
                }
                catch (NoMeasurementException nme) {
                  val = Double.NaN;
                }
              }
              ret.getInterpolatedValues().add(new InterpolatedValue(sensorId, val, depository.getMeasurementType(), beginDate, endDate));
            }
          }
        }
        catch (ParseException e) {
          setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
          return null;
        }
        catch (DatatypeConfigurationException e) {
          setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
          return null;
        }
        catch (IdNotFoundException e) {
          setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
          return null;
        }
        return ret;
      }
      else {
        setStatus(Status.CLIENT_ERROR_BAD_REQUEST, "Missing start and/or end times or value type.");
        return null;
      }
    }
    else {
      setStatus(Status.CLIENT_ERROR_BAD_REQUEST, "Bad credentials.");
      return null;
    }
  }
}