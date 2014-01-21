/**
 * SensorServerResource.java This file is part of WattDepot.
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

import java.util.logging.Level;

import org.restlet.data.Status;
import org.restlet.resource.ResourceException;
import org.wattdepot.common.domainmodel.Labels;
import org.wattdepot.common.domainmodel.Sensor;
import org.wattdepot.common.exception.IdNotFoundException;
import org.wattdepot.common.exception.MisMatchedOwnerException;
import org.wattdepot.common.http.api.SensorResource;

/**
 * SensorServerResource - Handles the Sensor HTTP API
 * ("/wattdepot/{org-id}/sensor/{sensor-id}").
 * 
 * @author Cam Moore
 * 
 */
public class SensorServerResource extends WattDepotServerResource implements SensorResource {
  private String sensorId;

  /*
   * (non-Javadoc)
   * 
   * @see org.restlet.resource.Resource#doInit()
   */
  @Override
  protected void doInit() throws ResourceException {
    super.doInit();
    this.sensorId = getAttribute(Labels.SENSOR_ID);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.restlet.SensorResource#remove()
   */
  @Override
  public void remove() {
    getLogger().log(Level.INFO, "DEL /wattdepot/{" + orgId + "}/sensor/{" + sensorId + "}");
    try {
      depot.deleteSensor(sensorId, orgId);
    }
    catch (IdNotFoundException e) {
      setStatus(Status.CLIENT_ERROR_FAILED_DEPENDENCY, e.getMessage());
    }
    catch (MisMatchedOwnerException e) {
      setStatus(Status.CLIENT_ERROR_FAILED_DEPENDENCY, e.getMessage());
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.restlet.SensorResource#retrieve()
   */
  @Override
  public Sensor retrieve() {
    getLogger().log(Level.INFO, "GET /wattdepot/{" + orgId + "}/sensor/{" + sensorId + "}");
    Sensor sensor = null;
    try {
      sensor = depot.getSensor(sensorId, orgId);
    }
    catch (MisMatchedOwnerException e) {
      setStatus(Status.CLIENT_ERROR_FORBIDDEN, e.getMessage());
    }
    catch (IdNotFoundException e) {
      setStatus(Status.CLIENT_ERROR_EXPECTATION_FAILED, "Sensor " + sensorId + " is not defined.");
    }
    if (sensor == null) {
      setStatus(Status.CLIENT_ERROR_EXPECTATION_FAILED, "Sensor " + sensorId + " is not defined.");
    }
    return sensor;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.common.http.api.SensorResource#update(org.wattdepot.common
   * .domainmodel.Sensor)
   */
  @Override
  public void update(Sensor sensor) {
    getLogger().log(Level.INFO,
        "POST /wattdepot/{" + orgId + "}/sensor/{" + sensorId + "} with " + sensor);
    try {
      depot.getOrganization(orgId);
    }
    catch (IdNotFoundException e) {
      setStatus(Status.CLIENT_ERROR_BAD_REQUEST, orgId + " does not exist.");
    }
    if (sensorId.equals(sensor.getId())) {
      try {
        if (depot.getSensorIds(orgId).contains(sensor.getId())) {
          try {
            depot.updateSensor(sensor);
          }
          catch (IdNotFoundException e) {
            setStatus(Status.CLIENT_ERROR_FAILED_DEPENDENCY, e.getMessage());
          }
        }
        else {
          setStatus(Status.CLIENT_ERROR_FAILED_DEPENDENCY, sensor.getName() + " is not defined.");
        }
      }
      catch (IdNotFoundException e) {
        setStatus(Status.CLIENT_ERROR_EXPECTATION_FAILED, orgId
            + " is not a defined Organization id.");
      }
    }
    else {
      setStatus(Status.CLIENT_ERROR_BAD_REQUEST, "Ids do not match.");
    }
  }

}
