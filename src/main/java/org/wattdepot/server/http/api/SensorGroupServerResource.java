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
import org.wattdepot.common.domainmodel.Labels;
import org.wattdepot.common.domainmodel.SensorGroup;
import org.wattdepot.common.exception.IdNotFoundException;
import org.wattdepot.common.exception.MisMatchedOwnerException;
import org.wattdepot.common.http.api.SensorGroupResource;

/**
 * SensorGroupServerResource - Handles the SensorGroup HTTP API
 * ("/wattdepot/{org-id}/sensor-group/{sensor-group_id}").
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
    this.sensorGroupId = getAttribute(Labels.SENSOR_GROUP_ID);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.restlet.SensorGroupResource#retrieve()
   */
  @Override
  public SensorGroup retrieve() {
    getLogger().log(Level.INFO,
        "GET /wattdepot/{" + orgId + "}/sensor-group/{" + sensorGroupId + "}");
    SensorGroup group = null;
    try {
      group = depot.getSensorGroup(sensorGroupId, orgId);
    }
    catch (IdNotFoundException e) {
      setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
    }
    if (group == null) {
      setStatus(Status.CLIENT_ERROR_BAD_REQUEST, "SensorGroup " + sensorGroupId
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
  public void update(SensorGroup sensorgroup) {
    getLogger().log(Level.INFO, "PUT /wattdepot/{" + orgId + "}/sensor-group/ with " + sensorgroup);
    try {
      depot.getOrganization(orgId);
    }
    catch (IdNotFoundException e) {
      setStatus(Status.CLIENT_ERROR_BAD_REQUEST, orgId + " does not exist.");
    }
    try {
      if (depot.getSensorGroupIds(orgId).contains(sensorgroup.getId())) {
        depot.updateSensorGroup(sensorgroup);
      }
      else {
        setStatus(Status.CLIENT_ERROR_BAD_REQUEST, "Can't update undefined SensorGroup "
            + sensorgroup.getName());
      }
    }
    catch (IdNotFoundException e) {
      setStatus(Status.CLIENT_ERROR_BAD_REQUEST, orgId + " does not exist.");
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.restlet.SensorGroupResource#remove()
   */
  @Override
  public void remove() {
    getLogger().log(Level.INFO,
        "DEL /wattdepot/{" + orgId + "}/sensor-group/{" + sensorGroupId + "}");
    try {
      depot.deleteSensorGroup(sensorGroupId, orgId);
    }
    catch (IdNotFoundException e) {
      setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
    }
    catch (MisMatchedOwnerException e) {
      setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
    }
  }

}
