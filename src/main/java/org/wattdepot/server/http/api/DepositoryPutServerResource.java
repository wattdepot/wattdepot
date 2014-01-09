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
import org.wattdepot.common.exception.BadSlugException;
import org.wattdepot.common.exception.IdNotFoundException;
import org.wattdepot.common.exception.UniqueIdException;
import org.wattdepot.common.http.api.DepositoryPutResource;

/**
 * DepositoryPutServerResource - Handles Depository HTTP API
 * ("/wattdepot/{org-id}/depository/").
 * 
 * @author Cam Moore
 * 
 */
public class DepositoryPutServerResource extends WattDepotServerResource
    implements DepositoryPutResource {

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.restlet.DepositoryResource#store(org.wattdepot.
   * datamodel.Depository)
   */
  @Override
  public void store(Depository depository) {
    getLogger().log(Level.INFO,
        "PUT /wattdepot/{" + orgId + "}/depository/ with " + depository);
    try {
      depot.getOrganization(orgId);
    }
    catch (IdNotFoundException e1) {
      setStatus(Status.CLIENT_ERROR_BAD_REQUEST, orgId + " does not exist.");
    }
    try {
      depot.defineDepository(depository.getId(), depository.getName(),
          depository.getMeasurementType(), orgId);
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
}
