/**
 * SensorServerResource.java This file is part of WattDepot 3.
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
package org.wattdepot3.server.restlet;

import java.util.logging.Level;

import org.restlet.data.Status;
import org.restlet.resource.ResourceException;
import org.wattdepot3.datamodel.Sensor;
import org.wattdepot3.datamodel.UserGroup;
import org.wattdepot3.exception.IdNotFoundException;
import org.wattdepot3.exception.MissMatchedOwnerException;
import org.wattdepot3.exception.UniqueIdException;
import org.wattdepot3.restlet.SensorResource;

/**
 * SensorServerResource - Handles the Sensor HTTP API
 * ("/wattdepot/{group_id}/sensor/",
 * "/wattdepot/{group_id}/sensor/{sensor_id}").
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
    this.sensorId = getAttribute("sensor_id");
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot3.restlet.SensorResource#retrieve()
   */
  @Override
  public Sensor retrieve() {
    getLogger().log(Level.INFO, "GET /wattdepot/{" + groupId + "}/sensor/{" + sensorId + "}");
    Sensor sensor = null;
    try {
      sensor = depot.getSensor(sensorId, groupId);
    }
    catch (MissMatchedOwnerException e) {
      setStatus(Status.CLIENT_ERROR_FORBIDDEN, e.getMessage());
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
   * org.wattdepot3.restlet.SensorResource#store(org.wattdepot3.datamodel.Sensor
   * )
   */
  @Override
  public void store(Sensor sensor) {
    getLogger().log(Level.INFO, "PUT /wattdepot/{" + groupId + "}/sensor/ with " + sensor);
    UserGroup owner = depot.getUserGroup(groupId);
    if (owner != null) {
      if (!depot.getSensorIds(groupId).contains(sensor.getId())) {
        try {
          depot.defineSensor(sensor.getName(), sensor.getUri(), sensor.getLocation(),
              sensor.getModel(), owner);
        }
        catch (UniqueIdException e) {
          setStatus(Status.CLIENT_ERROR_FAILED_DEPENDENCY, e.getMessage());
        }
        catch (MissMatchedOwnerException e) {
          setStatus(Status.CLIENT_ERROR_FAILED_DEPENDENCY, e.getMessage());
        }
      }
      else {
        depot.updateSensor(sensor);
      }
    }
    else {
      setStatus(Status.CLIENT_ERROR_BAD_REQUEST, groupId + " does not exist.");
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot3.restlet.SensorResource#remove()
   */
  @Override
  public void remove() {
    getLogger().log(Level.INFO, "DEL /wattdepot/{" + groupId + "}/sensor/{" + sensorId + "}");
    try {
      depot.deleteSensor(sensorId, groupId);
    }
    catch (IdNotFoundException e) {
      setStatus(Status.CLIENT_ERROR_FAILED_DEPENDENCY, e.getMessage());
    }
    catch (MissMatchedOwnerException e) {
      setStatus(Status.CLIENT_ERROR_FAILED_DEPENDENCY, e.getMessage());
    }
  }

}
