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
package org.wattdepot.server.impl.restlet;

import java.util.logging.Level;

import org.restlet.data.Status;
import org.restlet.resource.ResourceException;
import org.wattdepot.common.domainmodel.CollectorMetaData;
import org.wattdepot.common.domainmodel.UserGroup;
import org.wattdepot.common.exception.IdNotFoundException;
import org.wattdepot.common.exception.MissMatchedOwnerException;
import org.wattdepot.common.exception.UniqueIdException;
import org.wattdepot.common.restlet.CollectorMetaDataResource;

/**
 * CollectorMetaDataServerResource - Handles the CollectorMetaData HTTP API
 * (("/wattdepot/{group_id}/sensorprocess/",
 * "/wattdepot/{group_id}/sensorprocess/{sensorprocess_id}").
 * 
 * @author Cam Moore
 * 
 */
public class CollectorMetaDataServerResource extends WattDepotServerResource implements
    CollectorMetaDataResource {

  /** The sensorprocess_id from the request. */
  private String sensorProcessId;

  /*
   * (non-Javadoc)
   * 
   * @see org.restlet.resource.Resource#doInit()
   */
  @Override
  protected void doInit() throws ResourceException {
    super.doInit();
    this.sensorProcessId = getAttribute("collectormetadata_id");
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.restlet.CollectorMetaDataResource#retrieve()
   */
  @Override
  public CollectorMetaData retrieve() {
    getLogger().log(Level.INFO,
        "GET /wattdepot/{" + groupId + "}/collectormetadata/{" + sensorProcessId + "}");
    CollectorMetaData process = null;
    try {
      process = depot.getCollectorMetaData(sensorProcessId, groupId);
    }
    catch (MissMatchedOwnerException e) {
      setStatus(Status.CLIENT_ERROR_FORBIDDEN, e.getMessage());
    }
    if (process == null) {
      setStatus(Status.CLIENT_ERROR_EXPECTATION_FAILED, "CollectorMetaData " + sensorProcessId
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
  public void store(CollectorMetaData sensorprocess) {
    getLogger().log(Level.INFO,
        "PUT /wattdepot/{" + groupId + "}/sensorprocess/ with " + sensorprocess);
    UserGroup owner = depot.getUserGroup(groupId);
    if (owner != null) {
      if (!depot.getCollectorMetaDataIds(groupId).contains(sensorprocess.getId())) {
        try {
          depot.defineCollectorMetaData(sensorprocess.getName(), sensorprocess.getSensor(),
              sensorprocess.getPollingInterval(), sensorprocess.getDepositoryId(), owner);
        }
        catch (UniqueIdException e) {
          setStatus(Status.CLIENT_ERROR_FAILED_DEPENDENCY, e.getMessage());
        }
        catch (MissMatchedOwnerException e) {
          setStatus(Status.CLIENT_ERROR_FAILED_DEPENDENCY, e.getMessage());
        }
      }
      else {
        depot.updateCollectorMetaData(sensorprocess);
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
        "DEL /wattdepot/{" + groupId + "}/sensorprocess/{" + sensorProcessId + "}");
    try {
      depot.deleteCollectorMetaData(sensorProcessId, groupId);
    }
    catch (IdNotFoundException e) {
      setStatus(Status.CLIENT_ERROR_FAILED_DEPENDENCY, e.getMessage());
    }
    catch (MissMatchedOwnerException e) {
      setStatus(Status.CLIENT_ERROR_FAILED_DEPENDENCY, e.getMessage());
    }
  }

}
