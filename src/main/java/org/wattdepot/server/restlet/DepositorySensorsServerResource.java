/**
 * DepositorySensorsServerResource.java This file is part of WattDepot 3.
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
package org.wattdepot.server.restlet;

import java.util.List;
import java.util.logging.Level;

import org.restlet.data.Status;
import org.restlet.resource.ResourceException;
import org.wattdepot.datamodel.Depository;
import org.wattdepot.datamodel.Sensor;
import org.wattdepot.exception.MissMatchedOwnerException;
import org.wattdepot.restlet.DepositorySensorsResource;

/**
 * DepositorySensorsServerResource - Handles the Depository sensors HTTP API
 * ("/wattdepot/{group_id}/depository/{depository_id}/sensors/").
 * 
 * @author Cam Moore
 * 
 */
public class DepositorySensorsServerResource extends WattDepotServerResource implements
    DepositorySensorsResource {
  private String depositoryId;

  /*
   * (non-Javadoc)
   * 
   * @see org.restlet.resource.Resource#doInit()
   */
  @Override
  protected void doInit() throws ResourceException {
    super.doInit();
    this.depositoryId = getAttribute("depository_id");
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot3.restlet.DepositorySensorsResource#retrieve()
   */
  @Override
  public List<Sensor> retrieve() {
    getLogger().log(Level.INFO, "GET /wattdepot/{" + groupId + "}/depository/{" + depositoryId
        + "}/sensors/");
    Depository depository;
    try {
      depository = depot.getWattDeposiory(depositoryId, groupId);
      if (depository != null) {
        return depository.listSensors();
      }
      else {
        setStatus(Status.CLIENT_ERROR_BAD_REQUEST, depositoryId + " not defined.");
      }
    }
    catch (MissMatchedOwnerException e) {
      setStatus(Status.CLIENT_ERROR_CONFLICT, e.getMessage());
    }
    return null;
  }

}
