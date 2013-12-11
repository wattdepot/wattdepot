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
package org.wattdepot.server.impl.restlet;

import java.util.logging.Level;

import org.restlet.data.Status;
import org.restlet.resource.ResourceException;
import org.wattdepot.common.domainmodel.Depository;
import org.wattdepot.common.domainmodel.UserGroup;
import org.wattdepot.common.exception.IdNotFoundException;
import org.wattdepot.common.exception.MissMatchedOwnerException;
import org.wattdepot.common.exception.UniqueIdException;
import org.wattdepot.common.restlet.DepositoryResource;

/**
 * DepositoryServerResource - Handles Depository HTTP API
 * ("/wattdepot/{group_id}/depository/",
 * "/wattdepot/{group_id}/depository/{depository_id}").
 * 
 * @author Cam Moore
 * 
 */
public class DepositoryServerResource extends WattDepotServerResource implements DepositoryResource {

  /** The depository_id in the request. */
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
   * @see org.wattdepot.restlet.DepositoryResource#retrieve()
   */
  @Override
  public Depository retrieve() {
    getLogger().log(Level.INFO, "GET /wattdepot/{" + groupId + "}/depository/{" + depositoryId + "}");
    Depository depo = null;
    try {
      depo = depot.getWattDeposiory(depositoryId, groupId);
    }
    catch (MissMatchedOwnerException e) {
      setStatus(Status.CLIENT_ERROR_EXPECTATION_FAILED, e.getMessage());
    }
    if (depo == null) {
      setStatus(Status.CLIENT_ERROR_EXPECTATION_FAILED, "Depository " + depositoryId
          + " is not defined.");
    }
    return depo;
  }

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

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.restlet.DepositoryResource#remove()
   */
  @Override
  public void remove() {
    getLogger().log(Level.INFO, "DEL /wattdepot/{" + groupId + "}/depository/{" + depositoryId + "}");
    try {
      depot.deleteWattDepository(depositoryId, groupId);
    }
    catch (IdNotFoundException e) {
      setStatus(Status.CLIENT_ERROR_FAILED_DEPENDENCY, e.getMessage());
    }
    catch (MissMatchedOwnerException e) {
      setStatus(Status.CLIENT_ERROR_FAILED_DEPENDENCY, e.getMessage());
    }
  }

}
