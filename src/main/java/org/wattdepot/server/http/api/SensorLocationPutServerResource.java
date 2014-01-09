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
import org.wattdepot.common.domainmodel.SensorLocation;
import org.wattdepot.common.exception.BadSlugException;
import org.wattdepot.common.exception.IdNotFoundException;
import org.wattdepot.common.exception.UniqueIdException;
import org.wattdepot.common.http.api.SensorLocationPutResource;

/**
 * SensorLocationPutResource - WattDepot Location Resource handles the Location
 * HTTP API ("/wattdepot/{org-id}/location/").
 * 
 * @author Cam Moore
 * 
 */
public class SensorLocationPutServerResource extends WattDepotServerResource
    implements SensorLocationPutResource {

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.restlet.LocationResource#store(org.wattdepot.datamodel
   * .Location)
   */
  @Override
  public void store(SensorLocation sensorLocation) {
    getLogger().log(Level.INFO,
        "PUT /wattdepot/{" + orgId + "}/location/ with " + sensorLocation);
    try {
      depot.getOrganization(orgId);
      if (!depot.getSensorLocationIds(orgId).contains(sensorLocation.getId())) {
        try {
          depot.defineSensorLocation(sensorLocation.getId(),
              sensorLocation.getName(), sensorLocation.getLatitude(),
              sensorLocation.getLongitude(), sensorLocation.getAltitude(),
              sensorLocation.getDescription(), orgId);
        }
        catch (UniqueIdException e) {
          setStatus(Status.CLIENT_ERROR_CONFLICT, e.getMessage());
        }
        catch (IdNotFoundException e) {
          setStatus(Status.CLIENT_ERROR_EXPECTATION_FAILED, e.getMessage());
        }
        catch (BadSlugException e) {
          setStatus(Status.CLIENT_ERROR_EXPECTATION_FAILED, e.getMessage());
        }
      }
      else {
        try {
          depot.updateSensorLocation(sensorLocation);
        }
        catch (IdNotFoundException e) {
          setStatus(Status.CLIENT_ERROR_EXPECTATION_FAILED, e.getMessage());
        }
      }
    }
    catch (IdNotFoundException e1) {
      setStatus(Status.CLIENT_ERROR_BAD_REQUEST, orgId
          + " is not a defined Organization.");
    }
  }

}
