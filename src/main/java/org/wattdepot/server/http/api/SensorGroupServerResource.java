/**
 * SensorGroupServerResource.java This file is part of WattDepot.
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
import org.wattdepot.common.domainmodel.SensorGroup;
import org.wattdepot.common.domainmodel.UserGroup;
import org.wattdepot.common.exception.IdNotFoundException;
import org.wattdepot.common.exception.MissMatchedOwnerException;
import org.wattdepot.common.exception.UniqueIdException;
import org.wattdepot.common.httpapi.SensorGroupResource;

/**
 * SensorGroupServerResource - Handles the SensorGroup HTTP API
 * ("/wattdepot/{group_id}/sensorgroup/",
 * "/wattdepot/{group_id}/sensorgroup/{sensorgroup_id}").
 * 
 * @author Cam Moore
 * 
 */
public class SensorGroupServerResource extends WattDepotServerResource implements
    SensorGroupResource {

  /** The sensorgroup_id from the request. */
  private String sensorGroupId;

  /*
   * (non-Javadoc)
   * 
   * @see org.restlet.resource.Resource#doInit()
   */
  @Override
  protected void doInit() throws ResourceException {
    super.doInit();
    this.sensorGroupId = getAttribute("sensorgroup_id");
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.restlet.SensorGroupResource#retrieve()
   */
  @Override
  public SensorGroup retrieve() {
    getLogger().log(Level.INFO, "GET /wattdepot/{" + groupId + "}/sensorgroup/{" + sensorGroupId + "}");
    SensorGroup group = null;
    try {
      group = depot.getSensorGroup(sensorGroupId, groupId);
    }
    catch (MissMatchedOwnerException e) {
      setStatus(Status.CLIENT_ERROR_FORBIDDEN, e.getMessage());
    }
    if (group == null) {
      setStatus(Status.CLIENT_ERROR_EXPECTATION_FAILED, "SensorGroup " + sensorGroupId
          + " is not defined.");

    }
    return group;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.restlet.SensorGroupResource#store(org.wattdepot
   * .datamodel.SensorGroup)
   */
  @Override
  public void store(SensorGroup sensorgroup) {
    getLogger().log(Level.INFO, "PUT /wattdepot/{" + groupId + "}/sensorgroup/ with " + sensorgroup);
    UserGroup owner = depot.getUserGroup(groupId);
    if (owner != null) {
      if (!depot.getSensorGroupIds(groupId).contains(sensorgroup.getId())) {
        try {
          depot.defineSensorGroup(sensorgroup.getName(), sensorgroup.getSensors(), owner);
        }
        catch (UniqueIdException e) {
          setStatus(Status.CLIENT_ERROR_FAILED_DEPENDENCY, e.getMessage());
        }
        catch (MissMatchedOwnerException e) {
          setStatus(Status.CLIENT_ERROR_FAILED_DEPENDENCY, e.getMessage());
        }
      }
      else {
        depot.updateSensorGroup(sensorgroup);
      }
    }
    else {
      setStatus(Status.CLIENT_ERROR_BAD_REQUEST, groupId + " does not exist.");
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.restlet.SensorGroupResource#remove()
   */
  @Override
  public void remove() {
    getLogger().log(Level.INFO, "DEL /wattdepot/{" + groupId + "}/sensorgroup/{" + sensorGroupId + "}");
    try {
      depot.deleteSensorGroup(sensorGroupId, groupId);
    }
    catch (IdNotFoundException e) {
      setStatus(Status.CLIENT_ERROR_FAILED_DEPENDENCY, e.getMessage());
    }
    catch (MissMatchedOwnerException e) {
      setStatus(Status.CLIENT_ERROR_FAILED_DEPENDENCY, e.getMessage());
    }
  }

}
