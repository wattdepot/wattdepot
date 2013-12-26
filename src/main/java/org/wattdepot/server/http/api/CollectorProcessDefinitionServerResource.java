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
import org.wattdepot.common.domainmodel.Organization;
import org.wattdepot.common.exception.IdNotFoundException;
import org.wattdepot.common.exception.MisMatchedOwnerException;
import org.wattdepot.common.http.api.CollectorProcessDefinitionResource;

/**
 * CollectorProcessDefinitionServerResource - Handles the CollectorProcessDefinition HTTP API
 * (("/wattdepot/{org-id}/collector-metadata/",
 * "/wattdepot/{org-id}/collector-metadata/{collector-metadata-id}").
 * 
 * @author Cam Moore
 * 
 */
public class CollectorProcessDefinitionServerResource extends WattDepotServerResource implements
    CollectorProcessDefinitionResource {

  /** The collector-metadata_id from the request. */
  private String metaDataId;

  /*
   * (non-Javadoc)
   * 
   * @see org.restlet.resource.Resource#doInit()
   */
  @Override
  protected void doInit() throws ResourceException {
    super.doInit();
    this.metaDataId = getAttribute(Labels.COLLECTOR_META_DATA_ID);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.restlet.CollectorProcessDefinitionResource#retrieve()
   */
  @Override
  public CollectorProcessDefinition retrieve() {
    getLogger().log(Level.INFO,
        "GET /wattdepot/{" + orgId + "}/CollectorProcessDefinition/{" + metaDataId + "}");
    CollectorProcessDefinition process = null;
    try {
      process = depot.getCollectorProcessDefinition(metaDataId, orgId);
    }
    catch (MisMatchedOwnerException e) {
      setStatus(Status.CLIENT_ERROR_FORBIDDEN, e.getMessage());
    }
    if (process == null) {
      setStatus(Status.CLIENT_ERROR_EXPECTATION_FAILED, "CollectorProcessDefinition " + metaDataId
          + " is not defined.");
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
        "DEL /wattdepot/{" + orgId + "}/collector-metadata/{" + metaDataId + "}");
    try {
      depot.deleteCollectorProcessDefinition(metaDataId, orgId);
    }
    catch (IdNotFoundException e) {
      setStatus(Status.CLIENT_ERROR_FAILED_DEPENDENCY, e.getMessage());
    }
    catch (MisMatchedOwnerException e) {
      setStatus(Status.CLIENT_ERROR_FAILED_DEPENDENCY, e.getMessage());
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.common.http.api.CollectorProcessDefinitionResource#update(org.wattdepot
   * .common.domainmodel.CollectorProcessDefinition)
   */
  @Override
  public void update(CollectorProcessDefinition metadata) {
    getLogger().log(
        Level.INFO,
        "POST /wattdepot/{" + orgId + "}/collector-metadata/{" + metaDataId + "} with "
            + metadata);
    Organization owner = depot.getOrganization(orgId);
    if (owner != null) {
      if (metadata.getSlug().equals(metaDataId)) {
        if (depot.getCollectorProcessDefinitionIds(orgId).contains(metadata.getSlug())) {
          depot.updateCollectorProcessDefinition(metadata);
        }
      }
      else {
        setStatus(Status.CLIENT_ERROR_FAILED_DEPENDENCY, "Ids do not match.");
      }
    }
    else {
      setStatus(Status.CLIENT_ERROR_BAD_REQUEST, orgId + " does not exist.");
    }
  }

}
