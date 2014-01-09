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
import org.wattdepot.common.domainmodel.SensorGroup;
import org.wattdepot.common.exception.BadSlugException;
import org.wattdepot.common.exception.IdNotFoundException;
import org.wattdepot.common.exception.MisMatchedOwnerException;
import org.wattdepot.common.exception.UniqueIdException;
import org.wattdepot.common.http.api.SensorGroupPutResource;

/**
 * SensorGroupPutServerResource - Handles the SensorGroup HTTP API
 * ("/wattdepot/{org-id}/sensor-group/").
 * 
 * @author Cam Moore
 * 
 */
public class SensorGroupPutServerResource extends WattDepotServerResource
    implements SensorGroupPutResource {

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.restlet.SensorGroupResource#store(org.wattdepot
   * .datamodel.SensorGroup)
   */
  @Override
  public void store(SensorGroup sensorgroup) {
    getLogger().log(Level.INFO,
        "PUT /wattdepot/{" + orgId + "}/sensor-group/ with " + sensorgroup);
    try {
      depot.getOrganization(orgId);
    }
    catch (IdNotFoundException e1) {
      setStatus(Status.CLIENT_ERROR_BAD_REQUEST, orgId + " does not exist.");
    }
    try {
      if (!depot.getSensorGroupIds(orgId).contains(sensorgroup.getId())) {
        try {
          depot.defineSensorGroup(sensorgroup.getId(), sensorgroup.getName(),
              sensorgroup.getSensors(), orgId);
        }
        catch (UniqueIdException e) {
          setStatus(Status.CLIENT_ERROR_FAILED_DEPENDENCY, e.getMessage());
        }
        catch (MisMatchedOwnerException e) {
          setStatus(Status.CLIENT_ERROR_FAILED_DEPENDENCY, e.getMessage());
        }
        catch (IdNotFoundException e) {
          setStatus(Status.CLIENT_ERROR_FAILED_DEPENDENCY, e.getMessage());
        }
        catch (BadSlugException e) {
          setStatus(Status.CLIENT_ERROR_EXPECTATION_FAILED, e.getMessage());
        }
      }
      else {
        depot.updateSensorGroup(sensorgroup);
      }
    }
    catch (IdNotFoundException e) {
      setStatus(Status.CLIENT_ERROR_BAD_REQUEST, orgId + " does not exist.");
    }
  }
}
