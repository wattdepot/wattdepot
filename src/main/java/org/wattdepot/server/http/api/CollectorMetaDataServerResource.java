/**
 * CollectorMetaDataServerResource.java This file is part of WattDepot.
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
import org.wattdepot.common.domainmodel.CollectorMetaData;
import org.wattdepot.common.domainmodel.Labels;
import org.wattdepot.common.domainmodel.UserGroup;
import org.wattdepot.common.exception.IdNotFoundException;
import org.wattdepot.common.exception.MissMatchedOwnerException;
import org.wattdepot.common.exception.UniqueIdException;
import org.wattdepot.common.http.api.CollectorMetaDataResource;

/**
 * CollectorMetaDataServerResource - Handles the CollectorMetaData HTTP API
 * (("/wattdepot/{group-id}/collector-metadata/",
 * "/wattdepot/{group-id}/collector-metadata/{collector-metadata-id}").
 * 
 * @author Cam Moore
 * 
 */
public class CollectorMetaDataServerResource extends WattDepotServerResource implements
    CollectorMetaDataResource {

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
   * @see org.wattdepot.restlet.CollectorMetaDataResource#retrieve()
   */
  @Override
  public CollectorMetaData retrieve() {
    getLogger().log(Level.INFO,
        "GET /wattdepot/{" + groupId + "}/collectormetadata/{" + metaDataId + "}");
    CollectorMetaData process = null;
    try {
      process = depot.getCollectorMetaData(metaDataId, groupId);
    }
    catch (MissMatchedOwnerException e) {
      setStatus(Status.CLIENT_ERROR_FORBIDDEN, e.getMessage());
    }
    if (process == null) {
      setStatus(Status.CLIENT_ERROR_EXPECTATION_FAILED, "CollectorMetaData " + metaDataId
          + " is not defined.");
    }
    return process;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.restlet.CollectorMetaDataResource#store(org.wattdepot
   * .datamodel.CollectorMetaData)
   */
  @Override
  public void store(CollectorMetaData metadata) {
    getLogger().log(Level.INFO,
        "PUT /wattdepot/{" + groupId + "}/collector-metadata/ with " + metadata);
    UserGroup owner = depot.getUserGroup(groupId);
    if (owner != null) {
      if (!depot.getCollectorMetaDataIds(groupId).contains(metadata.getId())) {
        try {
          depot.defineCollectorMetaData(metadata.getName(), metadata.getSensor(),
              metadata.getPollingInterval(), metadata.getDepositoryId(), owner);
        }
        catch (UniqueIdException e) {
          setStatus(Status.CLIENT_ERROR_FAILED_DEPENDENCY, e.getMessage());
        }
        catch (MissMatchedOwnerException e) {
          setStatus(Status.CLIENT_ERROR_FAILED_DEPENDENCY, e.getMessage());
        }
      }
      else {
        setStatus(Status.CLIENT_ERROR_CONFLICT, "CollectorMeta " + metadata.getName()
            + " is already defined.");
      }
    }
    else {
      setStatus(Status.CLIENT_ERROR_BAD_REQUEST, groupId + " does not exist.");
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.restlet.CollectorMetaDataResource#remove()
   */
  @Override
  public void remove() {
    getLogger().log(Level.INFO,
        "DEL /wattdepot/{" + groupId + "}/collector-metadata/{" + metaDataId + "}");
    try {
      depot.deleteCollectorMetaData(metaDataId, groupId);
    }
    catch (IdNotFoundException e) {
      setStatus(Status.CLIENT_ERROR_FAILED_DEPENDENCY, e.getMessage());
    }
    catch (MissMatchedOwnerException e) {
      setStatus(Status.CLIENT_ERROR_FAILED_DEPENDENCY, e.getMessage());
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.common.http.api.CollectorMetaDataResource#update(org.wattdepot
   * .common.domainmodel.CollectorMetaData)
   */
  @Override
  public void update(CollectorMetaData metadata) {
    getLogger().log(
        Level.INFO,
        "POST /wattdepot/{" + groupId + "}/collector-metadata/{" + metaDataId + "} with "
            + metadata);
    UserGroup owner = depot.getUserGroup(groupId);
    if (owner != null) {
      if (metadata.getId().equals(metaDataId)) {
        if (depot.getCollectorMetaDataIds(groupId).contains(metadata.getId())) {
          depot.updateCollectorMetaData(metadata);
        }
      }
      else {
        setStatus(Status.CLIENT_ERROR_FAILED_DEPENDENCY, "Ids do not match.");
      }
    }
    else {
      setStatus(Status.CLIENT_ERROR_BAD_REQUEST, groupId + " does not exist.");
    }
  }

}
