/**
 * DepositoryMeasurementsServerResource.java This file is part of WattDepot.
 *
 * Copyright (C) 2013  Cam Moore
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
import java.util.logging.Level;

import javax.xml.datatype.DatatypeConfigurationException;

import org.restlet.data.Status;
import org.restlet.resource.ResourceException;
import org.wattdepot.common.domainmodel.Depository;
import org.wattdepot.common.domainmodel.Measurement;
import org.wattdepot.common.domainmodel.MeasurementList;
import org.wattdepot.common.domainmodel.Sensor;
import org.wattdepot.common.exception.MissMatchedOwnerException;
import org.wattdepot.common.http.api.DepositoryMeasurementsResource;
import org.wattdepot.common.util.DateConvert;

/**
 * DepositoryMeasurementsServerResource - Handles the Depository measurements
 * HTTP API ("/wattdepot/{group_id}/depository/{depository_id}/measurements/").
 * 
 * @author Cam Moore
 * 
 */
public class DepositoryMeasurementsServerResource extends WattDepotServerResource implements
    DepositoryMeasurementsResource {
  private String depositoryId;
  private String sensorId;
  private String start;
  private String end;

  /*
   * (non-Javadoc)
   * 
   * @see org.restlet.resource.Resource#doInit()
   */
  @Override
  protected void doInit() throws ResourceException {
    super.doInit();
    this.sensorId = getQuery().getValues("sensor");
    this.start = getQuery().getValues("start");
    this.end = getQuery().getValues("end");
    this.depositoryId = getAttribute("depository_id");
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.restlet.DepositoryMeasurementsResource#retrieve()
   */
  @Override
  public MeasurementList retrieve() {
    getLogger().log(Level.INFO, "GET /wattdepot/{" + groupId + "}/depository/{" + depositoryId
        + "}/measurements/?sensor={" + sensorId + "}&start={" + start + "}&end={" + end + "}");
    if (start != null && end != null) {
      MeasurementList ret = new MeasurementList();
      try {
        Depository depository = depot.getWattDeposiory(depositoryId, groupId);
        if (depository != null) {
          Sensor sensor = depot.getSensor(sensorId, groupId);
          if (sensor != null) {
            Date startDate = DateConvert.parseCalStringToDate(start);
            Date endDate = DateConvert.parseCalStringToDate(end);
            if (startDate != null && endDate != null) {
              for (Measurement meas : depository.getMeasurements(sensor, startDate, endDate)) {
                ret.getMeasurements().add(meas);
              }
            }
            else {
              setStatus(Status.CLIENT_ERROR_BAD_REQUEST, "Start date and/or end date missing.");
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
      getLogger().info(ret.toString());
      return ret;
    }
    else {
      setStatus(Status.CLIENT_ERROR_BAD_REQUEST, "Missing start and/or end times.");
      return null;
    }
  }

}
