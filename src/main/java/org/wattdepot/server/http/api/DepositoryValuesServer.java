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
import org.wattdepot.common.domainmodel.MeasuredValue;
import org.wattdepot.common.domainmodel.MeasuredValueList;
import org.wattdepot.common.domainmodel.Sensor;
import org.wattdepot.common.exception.MissMatchedOwnerException;
import org.wattdepot.common.exception.NoMeasurementException;
import org.wattdepot.common.util.DateConvert;
import org.wattdepot.common.util.tstamp.Tstamp;

/**
 * DepositoryValuesServer - Base class for handling the Depository values
 * HTTP API ("/wattdepot/{org-id}/depository/{depository-id}/values/").
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
  }

  /**
   * retrieve the depository measurement list for a sensor.
   * 
   * @return measurement list. 
   */
  public MeasuredValueList doRetrieve() {
    getLogger().log(Level.INFO, "GET /wattdepot/{" + orgId + "}/depository/{" + depositoryId
        + "}/values/?sensor={" + sensorId + "}&start={" + start + "}&end={" + end
        + "}&interval={" + interval + "}");
    
    if (start != null && end != null && interval != null) {
      MeasuredValueList ret = new MeasuredValueList();
      try {
        Depository depository = depot.getWattDeposiory(depositoryId, orgId);
        if (depository != null) {
          Sensor sensor = depot.getSensor(sensorId, orgId);
          if (sensor != null) {
            
            XMLGregorianCalendar startTime = DateConvert.parseCalString(start);
            XMLGregorianCalendar endTime = DateConvert.parseCalString(end);

            // interval is in minute
            int intervalMinutes = Integer.parseInt(interval);
            
            // Build list of timestamps, starting with startTime, separated by intervalMilliseconds
            List<XMLGregorianCalendar> timestampList =
                Tstamp.getTimestampList(startTime, endTime, intervalMinutes);

            for (int i = 0; i < timestampList.size(); i++) {
              Date timestamp = DateConvert.convertXMLCal(timestampList.get(i));
              Double value = depository.getValue(sensor, timestamp);
              if (value == null) {
                value = new Double(0);
              }
              MeasuredValue mValue = new MeasuredValue(
                  sensor.getSlug(), 
                  value, 
                  depository.getMeasurementType());
              
              mValue.setDate(timestamp);

              ret.getMeasuredValues().add(mValue);
            }
          }
          else {
            setStatus(Status.CLIENT_ERROR_BAD_REQUEST, sensorId + " is not defined");
          }
        }
        else {
          setStatus(Status.CLIENT_ERROR_BAD_REQUEST, depositoryId + " is not defined.");
        }
      }
      catch (MissMatchedOwnerException e) {
        setStatus(Status.CLIENT_ERROR_CONFLICT, e.getMessage());
      }
      catch (ParseException e) {
        setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
      }
      catch (DatatypeConfigurationException e) {
        setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
      } 
      catch (NoMeasurementException e) {
        setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
      }
      getLogger().info(ret.toString());
      return ret;
    }
    else {
      setStatus(Status.CLIENT_ERROR_BAD_REQUEST, "Missing start and/or end times or interval.");
      return null;
    }
  }

}
