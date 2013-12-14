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
import org.wattdepot.common.domainmodel.UserGroup;
import org.wattdepot.common.exception.IdNotFoundException;
import org.wattdepot.common.exception.MissMatchedOwnerException;
import org.wattdepot.common.exception.UniqueIdException;
import org.wattdepot.common.http.api.SensorLocationPutResource;
import org.wattdepot.common.http.api.SensorLocationResource;

/**
 * LocationResource - WattDepot 3 Location Resource handles the Location HTTP
 * API ("/wattdepot/{group_id}/location/" and
 * "/wattdepot/{group_id}/location/{location_id}").
 * 
 * @author Cam Moore
 * 
 */
public class SensorLocationPutServerResource extends WattDepotServerResource implements
    SensorLocationPutResource {

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
   * @see org.wattdepot.restlet.LocationResource#store(org.wattdepot.datamodel
   * .Location)
   */
  @Override
  public void store(SensorLocation sensorLocation) {
    getLogger()
        .log(Level.INFO, "PUT /wattdepot/{" + groupId + "}/location/ with " + sensorLocation);
    UserGroup owner = depot.getUserGroup(groupId);
    if (owner != null) {
      if (!depot.getLocationIds(groupId).contains(sensorLocation.getId())) {
        try {
          depot.defineLocation(sensorLocation.getName(), sensorLocation.getLatitude(),
              sensorLocation.getLongitude(), sensorLocation.getAltitude(),
              sensorLocation.getDescription(), owner);
        }
        catch (UniqueIdException e) {
          setStatus(Status.CLIENT_ERROR_CONFLICT, e.getMessage());
        }
      }
      else {
        depot.updateLocation(sensorLocation);
      }
    }
    else {
      setStatus(Status.CLIENT_ERROR_BAD_REQUEST, groupId + " does not exist.");
    }
  }

}
