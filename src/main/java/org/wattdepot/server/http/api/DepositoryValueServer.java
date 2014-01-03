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
package org.wattdepot.server.http.api;

import java.text.ParseException;
import java.util.Date;
import java.util.logging.Level;

import javax.xml.datatype.DatatypeConfigurationException;

import org.restlet.data.Status;
import org.restlet.resource.ResourceException;
import org.wattdepot.common.domainmodel.Labels;
import org.wattdepot.common.domainmodel.MeasuredValue;
import org.wattdepot.common.domainmodel.Sensor;
import org.wattdepot.common.exception.MeasurementGapException;
import org.wattdepot.common.exception.MisMatchedOwnerException;
import org.wattdepot.common.exception.NoMeasurementException;
import org.wattdepot.common.util.DateConvert;
import org.wattdepot.server.depository.impl.hibernate.DepositoryImpl;

/**
 * DepositoryValueServerResource - ServerResouce that handles the GET
 * /wattdepot/{org-id}/depository/{depository-id}/value/ response.
 * 
 * @author Cam Moore
 * 
 */
public class DepositoryValueServer extends WattDepotServerResource {
  private String depositoryId;
  private String sensorId;
  private String start;
  private String end;
  private String timestamp;
  private String latest;
  private String earliest;
  private String gapSeconds;

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
    this.timestamp = getQuery().getValues(Labels.TIMESTAMP);
    this.latest = getQuery().getValues(Labels.LATEST);
    this.earliest = getQuery().getValues(Labels.EARLIEST);
    this.orgId = getAttribute(Labels.ORGANIZATION_ID);
    this.depositoryId = getAttribute(Labels.DEPOSITORY_ID);
    this.gapSeconds = getQuery().getValues(Labels.GAP);
  }

  /**
   * retrieve the depository value for a sensor.
   * 
   * @return measured value.
   */
  public MeasuredValue doRetrieve() {
    getLogger().log(
        Level.INFO,
        "GET /wattdepot/{" + orgId + "}/depository/{" + depositoryId
            + "}/value/?sensor={" + sensorId + "}&start={" + start + "}&end={"
            + end + "}&timestamp={" + timestamp + "}&latest={" + latest
            + "}&earliest={" + earliest + "}&gap={" + gapSeconds + "}");
    try {
      DepositoryImpl deposit = (DepositoryImpl) depot.getWattDepository(
          depositoryId, orgId);
      if (deposit != null) {
        Sensor sensor = depot.getSensor(sensorId, orgId);
        if (sensor != null) {
          Double value = null;
          Date startDate = null;
          Date endDate = null;
          Date time = null;

          if (earliest != null) {
            return deposit.getEarliestMeasuredValue(sensorId);
          }
          else if (latest != null) {
            return deposit.getLatestMeasuredValue(sensorId);
          }
          else if (timestamp != null) {
            time = DateConvert.parseCalStringToDate(timestamp);
            if (gapSeconds != null) {
              value = deposit.getValue(sensor.getSlug(), time,
                  Long.parseLong(gapSeconds));
            }
            else {
              value = deposit.getValue(sensor.getSlug(), time);
            }
          }
          else if (start != null && end != null) {
            startDate = DateConvert.parseCalStringToDate(start);
            endDate = DateConvert.parseCalStringToDate(end);
            if (gapSeconds != null) {
              value = deposit.getValue(sensor.getSlug(), startDate, endDate,
                  Long.parseLong(gapSeconds));
            }
            else {
              value = deposit.getValue(sensor.getSlug(), startDate, endDate);
            }
          }

          MeasuredValue val = new MeasuredValue(sensorId, value,
              deposit.getMeasurementType());
          if (end != null) {
            val.setDate(endDate);
          }
          else if (time != null) {
            val.setDate(time);
          }
          return val;
        }
        else {
          setStatus(Status.CLIENT_ERROR_BAD_REQUEST, "Could not find sensor "
              + sensorId);
        }
      }
      else {
        setStatus(Status.CLIENT_ERROR_BAD_REQUEST, "Could not find depository "
            + depositoryId);
      }
    }
    catch (MisMatchedOwnerException e) {
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
