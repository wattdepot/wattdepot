/**
 * DepositoryServerResource.java This file is part of WattDepot.
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
import org.wattdepot.common.domainmodel.Depository;
import org.wattdepot.common.domainmodel.UserGroup;
import org.wattdepot.common.exception.UniqueIdException;
import org.wattdepot.common.http.api.DepositoryPutResource;

/**
 * DepositoryPutServerResource - Handles Depository HTTP API
 * ("/wattdepot/{group_id}/depository/").
 * 
 * @author Cam Moore
 * 
 */
public class DepositoryPutServerResource extends WattDepotServerResource implements
    DepositoryPutResource {

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.restlet.DepositoryResource#store(org.wattdepot.
   * datamodel.Depository)
   */
  @Override
  public void store(Depository depository) {
    getLogger().log(Level.INFO, "PUT /wattdepot/{" + groupId + "}/depository/ with " + depository);
    UserGroup owner = depot.getUserGroup(groupId);
    if (owner != null) {
      try {
        depot.defineWattDepository(depository.getName(), depository.getMeasurementType(), owner);
      }
      catch (UniqueIdException e) {
        setStatus(Status.CLIENT_ERROR_CONFLICT, e.getMessage());
      }
    }
    else {
      setStatus(Status.CLIENT_ERROR_BAD_REQUEST, groupId + " does not exist.");
    }
  }
}