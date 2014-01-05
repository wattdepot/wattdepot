/**
 * SensorLocationResource.java This file is part of WattDepot.
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
import org.wattdepot.common.domainmodel.SensorLocation;
import org.wattdepot.common.exception.IdNotFoundException;
import org.wattdepot.common.exception.MisMatchedOwnerException;
import org.wattdepot.common.http.api.SensorLocationResource;

/**
 * LocationResource - WattDepot 3 Location Resource handles the Location HTTP
 * API ("/wattdepot/{org-id}/location/{location-id}").
 * 
 * @author Cam Moore
 * 
 */
public class SensorLocationServerResource extends WattDepotServerResource implements
    SensorLocationResource {

  private String locationId;

  /*
   * (non-Javadoc)
   * 
   * @see org.restlet.resource.Resource#doInit()
   */
  @Override
  protected void doInit() throws ResourceException {
    super.doInit();
    this.locationId = getAttribute(Labels.LOCATION_ID);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.restlet.LocationResource#retrieve()
   */
  @Override
  public SensorLocation retrieve() {
    getLogger().log(Level.INFO, "GET /wattdepot/{" + orgId + "}/location/{" + locationId + "}");
    SensorLocation loc = null;
    try {
      depot.getOrganization(orgId);
    }
    catch (IdNotFoundException e1) {
      setStatus(Status.CLIENT_ERROR_BAD_REQUEST, orgId + " does not exist.");
    }
    try {
      loc = depot.getSensorLocation(locationId, orgId);
    }
    catch (IdNotFoundException e) {
      setStatus(Status.CLIENT_ERROR_EXPECTATION_FAILED, "SensorLocation " + locationId
          + " is not defined.");
    }
    return loc;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.restlet.LocationResource#store(org.wattdepot.datamodel
   * .Location)
   */
  @Override
  public void update(SensorLocation sensorLocation) {
    getLogger().log(Level.INFO, "POST /wattdepot/{" + orgId + "}/location/ with " + sensorLocation);
    try {
      depot.getOrganization(orgId);
    }
    catch (IdNotFoundException e) {
      setStatus(Status.CLIENT_ERROR_BAD_REQUEST, orgId + " does not exist.");
    }
    try {
      depot.updateSensorLocation(sensorLocation);
    }
    catch (IdNotFoundException e) {
      setStatus(Status.CLIENT_ERROR_BAD_REQUEST, sensorLocation.getName() + " is not defined.");
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.restlet.LocationResource#remove()
   */
  @Override
  public void remove() {
    getLogger().log(Level.INFO, "DEL /wattdepot/{" + orgId + "}/location/{" + locationId + "}");
    try {
      depot.deleteSensorLocation(locationId, orgId);
    }
    catch (IdNotFoundException e) {
      setStatus(Status.CLIENT_ERROR_FAILED_DEPENDENCY, e.getMessage());
    }
    catch (MisMatchedOwnerException e) {
      setStatus(Status.CLIENT_ERROR_FAILED_DEPENDENCY, e.getMessage());
    }
  }
}
