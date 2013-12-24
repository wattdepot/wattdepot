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
import org.wattdepot.common.domainmodel.Sensor;
import org.wattdepot.common.domainmodel.Organization;
import org.wattdepot.common.exception.MissMatchedOwnerException;
import org.wattdepot.common.exception.UniqueIdException;
import org.wattdepot.common.http.api.SensorPutResource;

/**
 * SensorPutServerResource - Handles the Sensor HTTP API
 * ("/wattdepot/{org-id}/sensor/").
 * 
 * @author Cam Moore
 * 
 */
public class SensorPutServerResource extends WattDepotServerResource implements SensorPutResource {
  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.restlet.SensorResource#store(org.wattdepot.datamodel.Sensor )
   */
  @Override
  public void store(Sensor sensor) {
    getLogger().log(Level.INFO, "PUT /wattdepot/{" + orgId + "}/sensor/ with " + sensor);
    Organization owner = depot.getOrganization(orgId);
    if (owner != null) {
      if (!depot.getSensorIds(orgId).contains(sensor.getSlug())) {
        try {
          depot.defineSensor(sensor.getName(), sensor.getUri(), sensor.getSensorLocationId(),
              sensor.getModelId(), owner);
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
      setStatus(Status.CLIENT_ERROR_BAD_REQUEST, orgId + " does not exist.");
    }
  }
}
