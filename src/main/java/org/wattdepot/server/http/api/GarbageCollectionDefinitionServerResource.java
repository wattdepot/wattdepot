/**
 * GarbageCollectionDefinitionServerResource.java This file is part of WattDepot.
 *
 * Copyright (C) 2014  Cam Moore
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
import org.wattdepot.common.domainmodel.GarbageCollectionDefinition;
import org.wattdepot.common.domainmodel.Labels;
import org.wattdepot.common.exception.IdNotFoundException;
import org.wattdepot.common.http.api.API;
import org.wattdepot.common.http.api.GarbageCollectionDefinitionResource;

/**
 * GarbageCollectionDefinitionServerResource - Handles the
 * GarbageCollectionDefinition HTTP API
 * ("/wattdepot/{org-id}/garbage-collection-definition/{gcd-id}").
 * 
 * @author Cam Moore
 * 
 */
public class GarbageCollectionDefinitionServerResource extends WattDepotServerResource implements
    GarbageCollectionDefinitionResource {
  private String gcdId;

  /*
   * (non-Javadoc)
   * 
   * @see org.restlet.resource.Resource#doInit()
   */
  @Override
  protected void doInit() throws ResourceException {
    super.doInit();
    this.gcdId = getAttribute(Labels.GARBAGE_COLLECTION_DEFINITION_ID);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.common.http.api.GarbageCollectionDefinitionReasource#retrieve
   * ()
   */
  @Override
  public GarbageCollectionDefinition retrieve() {
    getLogger().log(
        Level.INFO,
        "GET " + API.BASE_URI + "{" + orgId + "}/" + Labels.GARBAGE_COLLECTION_DEFINITION + "/{"
            + gcdId + "}");
    GarbageCollectionDefinition gcd = null;
    if (isInRole(orgId)) {
      try {
        gcd = depot.getGarbageCollectionDefinition(gcdId, orgId, true);
      }
      catch (IdNotFoundException e) {
        setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
      }
      if (gcd == null) {
        setStatus(Status.CLIENT_ERROR_BAD_REQUEST, "GarbageCollectionDefinition " + gcdId
            + " is not defined.");
      }
    }
    else {
      setStatus(Status.CLIENT_ERROR_BAD_REQUEST, "Bad credentials.");
    }
    return gcd;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.common.http.api.GarbageCollectionDefinitionReasource#update
   * (org.wattdepot.common.domainmodel.GarbageCollectionDefinition)
   */
  @Override
  public void update(GarbageCollectionDefinition definition) {
    getLogger().log(
        Level.INFO,
        "POST " + API.BASE_URI + "{" + orgId + "}/" + Labels.GARBAGE_COLLECTION_DEFINITION + "/{"
            + gcdId + "} with " + definition);
    if (isInRole(orgId)) {
      if (definition.getId().equals(gcdId)) {
        try {
          if (depot.getGarbageCollectionDefinitionIds(orgId, true).contains(definition.getId())) {
            try {
              depot.updateGarbageCollectionDefinition(definition);
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

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.common.http.api.GarbageCollectionDefinitionReasource#remove()
   */
  @Override
  public void remove() {
    getLogger().log(
        Level.INFO,
        "DEL " + API.BASE_URI + "{" + orgId + "}/" + Labels.GARBAGE_COLLECTION_DEFINITION + "/{"
            + gcdId + "}");
    if (isInRole(orgId)) {
      try {
        depot.deleteGarbageCollectionDefinition(gcdId, orgId);
      }
      catch (IdNotFoundException e) {
        setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
      }      
    }
    else {
      setStatus(Status.CLIENT_ERROR_BAD_REQUEST, "Bad credentials.");
    }
  }

}
