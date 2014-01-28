/**
 * CollectorProcessDefinitionServerResource.java This file is part of WattDepot.
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
import org.wattdepot.common.domainmodel.CollectorProcessDefinition;
import org.wattdepot.common.domainmodel.Labels;
import org.wattdepot.common.exception.IdNotFoundException;
import org.wattdepot.common.exception.MisMatchedOwnerException;
import org.wattdepot.common.http.api.CollectorProcessDefinitionResource;

/**
 * CollectorProcessDefinitionServerResource - Handles the
 * CollectorProcessDefinition HTTP API (
 * "/wattdepot/{org-id}/collector-process-definition/{collector-process-definition-id}"
 * ).
 * 
 * @author Cam Moore
 * 
 */
public class CollectorProcessDefinitionServerResource extends WattDepotServerResource implements
    CollectorProcessDefinitionResource {

  /** The collector-process-definition_id from the request. */
  private String definitionId;

  /*
   * (non-Javadoc)
   * 
   * @see org.restlet.resource.Resource#doInit()
   */
  @Override
  protected void doInit() throws ResourceException {
    super.doInit();
    this.definitionId = getAttribute(Labels.COLLECTOR_PROCESS_DEFINITION_ID);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.restlet.CollectorProcessDefinitionResource#retrieve()
   */
  @Override
  public CollectorProcessDefinition retrieve() {
    getLogger().log(Level.INFO,
        "GET /wattdepot/{" + orgId + "}/CollectorProcessDefinition/{" + definitionId + "}");
    CollectorProcessDefinition process = null;
    if (isInRole(orgId)) {
      try {
        process = depot.getCollectorProcessDefinition(definitionId, orgId);
      }
      catch (IdNotFoundException e) {
        setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
      }
      if (process == null) {
        setStatus(Status.CLIENT_ERROR_BAD_REQUEST, "CollectorProcessDefinition " + definitionId
            + " is not defined.");
      }
    }
    else {
      setStatus(Status.CLIENT_ERROR_BAD_REQUEST, "Bad credentials.");
    }
    return process;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.restlet.CollectorProcessDefinitionResource#remove()
   */
  @Override
  public void remove() {
    getLogger().log(Level.INFO,
        "DEL /wattdepot/{" + orgId + "}/collector-process-definition/{" + definitionId + "}");
    if (isInRole(orgId)) {
      try {
        depot.deleteCollectorProcessDefinition(definitionId, orgId);
      }
      catch (IdNotFoundException e) {
        setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
      }
      catch (MisMatchedOwnerException e) {
        setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
      }
    }
    else {
      setStatus(Status.CLIENT_ERROR_BAD_REQUEST, "Bad credentials.");
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.common.http.api.CollectorProcessDefinitionResource#update
   * (org.wattdepot .common.domainmodel.CollectorProcessDefinition)
   */
  @Override
  public void update(CollectorProcessDefinition definition) {
    getLogger().log(
        Level.INFO,
        "POST /wattdepot/{" + orgId + "}/collector-process-definition/{" + definitionId + "} with "
            + definition);
    if (isInRole(orgId)) {
      if (definition.getId().equals(definitionId)) {
        try {
          if (depot.getCollectorProcessDefinitionIds(orgId).contains(definition.getId())) {
            try {
              depot.updateCollectorProcessDefinition(definition);
            }
            catch (IdNotFoundException e) {
              setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
            }
          }
        }
        catch (IdNotFoundException e) {
          setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
        }
      }
      else {
        setStatus(Status.CLIENT_ERROR_BAD_REQUEST, "Ids do not match.");
      }
    }
    else {
      setStatus(Status.CLIENT_ERROR_BAD_REQUEST, "Bad credentials.");
    }
  }

}
