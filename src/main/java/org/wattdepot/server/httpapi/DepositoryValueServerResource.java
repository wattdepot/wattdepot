/**
 * DepositoryValueServerResource.java This file is part of WattDepot.
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
package org.wattdepot.server.httpapi;

import java.text.ParseException;
import java.util.Date;
import java.util.logging.Level;

import javax.xml.datatype.DatatypeConfigurationException;

import org.restlet.data.Status;
import org.restlet.resource.ResourceException;
import org.wattdepot.common.domainmodel.MeasuredValue;
import org.wattdepot.common.domainmodel.Sensor;
import org.wattdepot.common.exception.MeasurementGapException;
import org.wattdepot.common.exception.MissMatchedOwnerException;
import org.wattdepot.common.exception.NoMeasurementException;
import org.wattdepot.common.httpapi.DepositoryValueResource;
import org.wattdepot.common.util.DateConvert;
import org.wattdepot.server.depository.impl.hibernate.DepositoryImpl;

/**
 * DepositoryValueServerResource - ServerResouce that handles the GET
 * /wattdepot/{group_id}/depository/{depository_id}/value/ response.
 * 
 * @author Cam Moore
 * 
 */
public class DepositoryValueServerResource extends WattDepotServerResource implements
    DepositoryValueResource {
  private String depositoryId;
  private String sensorId;
  private String start;
  private String end;
  private String timestamp;
  private String gapSeconds;

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
    this.timestamp = getQuery().getValues("timestamp");
    this.groupId = getAttribute("group_id");
    this.depositoryId = getAttribute("depository_id");
    this.gapSeconds = getQuery().getValues("gap");
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.restlet.DepositoryValueResource#retrieve()
   */
  @Override
  public MeasuredValue retrieve() {
    getLogger().log(Level.INFO, "GET /wattdepot/{" + groupId + "}/depository/{" + depositoryId
        + "}/value/?sensor={" + sensorId + "}&start={" + start + "}&end={" + end + "}&timestamp={"
        + timestamp + "}&gap={" + gapSeconds + "}");
    try {
      DepositoryImpl deposit = (DepositoryImpl) depot.getWattDeposiory(depositoryId, groupId);
      if (deposit != null) {
        Sensor sensor = depot.getSensor(sensorId, groupId);
        if (sensor != null) {
          Double value = null;
          Date startDate = null;
          Date endDate = null;
          Date time = null;
          if (start != null && end != null) {
            startDate = DateConvert.parseCalStringToDate(start);
            endDate = DateConvert.parseCalStringToDate(end);
            if (gapSeconds != null) {
              value = deposit.getValue(sensor, startDate, endDate, Long.parseLong(gapSeconds));
            }
            else {
              value = deposit.getValue(sensor, startDate, endDate);
            }
          }
          else if (timestamp != null) {
            time = DateConvert.parseCalStringToDate(timestamp);
            if (gapSeconds != null) {
              value = deposit.getValue(sensor, time, Long.parseLong(gapSeconds));
            }
            else {
              value = deposit.getValue(sensor, time);
            }
          }
          MeasuredValue val = new MeasuredValue(sensorId, value, deposit.getMeasurementType());
          if (end != null) {
            val.setDate(endDate);
          }
          else if (time != null) {
            val.setDate(time);
          }
          return val;
        }
        else {
          setStatus(Status.CLIENT_ERROR_BAD_REQUEST, "Could not find sensor " + sensorId);
        }
      }
      else {
        setStatus(Status.CLIENT_ERROR_BAD_REQUEST, "Could not find depository " + depositoryId);
      }
    }
    catch (MissMatchedOwnerException e) {
      setStatus(Status.CLIENT_ERROR_CONFLICT, e.getMessage());
    }
    catch (NoMeasurementException e1) {
      setStatus(Status.CLIENT_ERROR_EXPECTATION_FAILED, e1.getMessage());
    }
    catch (NumberFormatException e) {
      setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
    }
    catch (MeasurementGapException e) {
      setStatus(Status.CLIENT_ERROR_EXPECTATION_FAILED, e.getMessage());
    }
    catch (ParseException e) {
      setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
      e.printStackTrace();
    }
    catch (DatatypeConfigurationException e) {
      setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
    }
    return null;
  }
}
