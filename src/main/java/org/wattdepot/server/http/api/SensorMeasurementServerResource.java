/**
 * SensorMeasurementServerResource.java This file is part of WattDepot.
 *
 * Copyright (C) 2014  Cam Moore
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
import org.wattdepot.common.domainmodel.Labels;
import org.wattdepot.common.domainmodel.Measurement;
import org.wattdepot.common.domainmodel.MeasurementList;
import org.wattdepot.common.domainmodel.Sensor;
import org.wattdepot.common.domainmodel.SensorGroup;
import org.wattdepot.common.domainmodel.SensorMeasurementSummary;
import org.wattdepot.common.exception.IdNotFoundException;
import org.wattdepot.common.exception.MisMatchedOwnerException;
import org.wattdepot.common.http.api.SensorMeasurementResource;
import org.wattdepot.common.util.DateConvert;

/**
 * SensorMeasurementServerResource - Handles the SensorMeasurementSummary HTTP
 * API (
 * "/wattdepot/{org-id}/depository/{depository-id}/?sensor={sensor-id}&amp;start={start}&amp;end=
 * { e n d }).
 * 
 * @author Cam Moore
 * 
 */
public class SensorMeasurementServerResource extends WattDepotServerResource implements
    SensorMeasurementResource {
  /** The depository id. */
  private String depositoryId;
  /** The sensor id. */
  private String sensorId;
  /** The start time. */
  private String start;
  /** The end time. */
  private String end;

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

  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.common.http.api.SensorMeasurementResource#retrieve()
   */
  @Override
  public SensorMeasurementSummary retrieve() {
    getLogger().log(
        Level.INFO,
        "GET /wattdepot/{" + orgId + "}/depository/{" + depositoryId + "}/summary/?sensor={"
            + sensorId + "}&start={" + start + "}&end={" + end + "}");
    SensorMeasurementSummary ret = null;
    if (isInRole(orgId)) {
      MeasurementList meas = getMeasurements();
      if (meas != null) {
        try {
          ret = new SensorMeasurementSummary(sensorId, depositoryId,
              DateConvert.parseCalStringToDate(start), DateConvert.parseCalStringToDate(end), meas
                  .getMeasurements().size());
        }
        catch (ParseException e) {
          e.printStackTrace();
          setStatus(Status.SERVER_ERROR_INTERNAL);
        }
        catch (DatatypeConfigurationException e) {
          e.printStackTrace();
          setStatus(Status.SERVER_ERROR_INTERNAL);
        }
      }
    }
    return ret;
  }

  /**
   * @return The measurements between start and end for the given sensor and
   *         depository.
   */
  private MeasurementList getMeasurements() {
    if (start != null && end != null) {
      MeasurementList ret = new MeasurementList();
      try {
        Depository depository = depot.getDepository(depositoryId, orgId);
        if (depository != null) {
          Date startDate = DateConvert.parseCalStringToDate(start);
          Date endDate = DateConvert.parseCalStringToDate(end);
          if (startDate != null && endDate != null) {
            try {
              Sensor sensor = depot.getSensor(sensorId, orgId);
              for (Measurement meas : depot.getMeasurements(depositoryId, orgId, sensor.getId(),
                  startDate, endDate)) {
                ret.getMeasurements().add(meas);
              }
            }
            catch (IdNotFoundException nf) {
              try {
                SensorGroup group = depot.getSensorGroup(sensorId, orgId);
                for (String s : group.getSensors()) {
                  Sensor sensor = depot.getSensor(s, orgId);
                  for (Measurement meas : depot.getMeasurements(depositoryId, orgId,
                      sensor.getId(), startDate, endDate)) {
                    ret.getMeasurements().add(meas);
                  }
                }
              }
              catch (IdNotFoundException nf1) {
                setStatus(Status.CLIENT_ERROR_BAD_REQUEST, sensorId + " is not defined");
              }
            }
          }
          else {
            setStatus(Status.CLIENT_ERROR_BAD_REQUEST, "Start date and/or end date missing.");
          }
        }
        else {
          setStatus(Status.CLIENT_ERROR_BAD_REQUEST, depositoryId + " is not defined.");
        }
      }
      catch (IdNotFoundException e) {
        setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
      }
      catch (ParseException e) {
        setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
      }
      catch (DatatypeConfigurationException e) {
        setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
      }
      catch (MisMatchedOwnerException e) {
        setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
      }
      return ret;
    }
    else {
      setStatus(Status.CLIENT_ERROR_BAD_REQUEST, "No start and/or end dates.");
      return null;
    }
  }
}
