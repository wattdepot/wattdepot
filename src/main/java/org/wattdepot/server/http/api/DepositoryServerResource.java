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
import org.restlet.resource.ResourceException;
import org.wattdepot.common.domainmodel.Depository;
import org.wattdepot.common.domainmodel.Labels;
import org.wattdepot.common.exception.IdNotFoundException;
import org.wattdepot.common.exception.MisMatchedOwnerException;
import org.wattdepot.common.http.api.DepositoryResource;

/**
 * DepositoryServerResource - Handles Depository HTTP API
 * ("/wattdepot/{org-id}/depository/{depository-id}").
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
    this.depositoryId = getAttribute(Labels.DEPOSITORY_ID);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.restlet.DepositoryResource#retrieve()
   */
  @Override
  public Depository retrieve() {
    getLogger().log(Level.INFO, "GET /wattdepot/{" + orgId + "}/depository/{" + depositoryId + "}");
    try {
      depot.getOrganization(orgId);
    }
    catch (IdNotFoundException e1) {
      setStatus(Status.CLIENT_ERROR_BAD_REQUEST, orgId + " is not a defined Organization.");
    }
    Depository depo = null;
    try {
      depo = depot.getDepository(depositoryId, orgId);
    }
    catch (IdNotFoundException e) {
      setStatus(Status.CLIENT_ERROR_BAD_REQUEST, "Depository " + depositoryId
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
  public void update(Depository depository) {
    getLogger().log(Level.INFO,
        "POST /wattdepot/{" + orgId + "}/depository/{" + depositoryId + "} with " + depository);
    try {
      depot.getOrganization(orgId);
    }
    catch (IdNotFoundException e) {
      setStatus(Status.CLIENT_ERROR_BAD_REQUEST, orgId + " does not exist.");
    }
    setStatus(Status.CLIENT_ERROR_BAD_REQUEST, "Can't update a Depository.");
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.restlet.DepositoryResource#remove()
   */
  @Override
  public void remove() {
    getLogger().log(Level.INFO, "DEL /wattdepot/{" + orgId + "}/depository/{" + depositoryId + "}");
    try {
      depot.deleteDepository(depositoryId, orgId);
    }
    catch (IdNotFoundException e) {
      setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
    }
    catch (MisMatchedOwnerException e) {
      setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
    }
  }

}
