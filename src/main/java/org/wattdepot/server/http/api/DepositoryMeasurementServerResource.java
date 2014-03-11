/**
 * DepositoryMeasurementServerResource.java This file is part of WattDepot.
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
import org.wattdepot.common.domainmodel.Measurement;
import org.wattdepot.common.exception.IdNotFoundException;
import org.wattdepot.common.http.api.DepositoryMeasurementResource;

/**
 * DepositoryMeasurementServerResource - Handles the Measurement HTTP API
 * ("/wattdepot/{org-id}/depository/{depository_id}/measurement/").
 * 
 * @author Cam Moore
 * 
 */
public class DepositoryMeasurementServerResource extends WattDepotServerResource implements
    DepositoryMeasurementResource {
  private String depositoryId;
  private String measId;

  /*
   * (non-Javadoc)
   * 
   * @see org.restlet.resource.Resource#doInit()
   */
  @Override
  protected void doInit() throws ResourceException {
    super.doInit();
    this.depositoryId = getAttribute(Labels.DEPOSITORY_ID);
    this.measId = getAttribute(Labels.MEASUREMENT_ID);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.common.http.api.DepositoryMeasurementResource#retrieve()
   */
  @Override
  public Measurement retrieve() {
    getLogger().log(
        Level.INFO,
        "GET /wattdepot/{" + orgId + "}/depository/{" + depositoryId + "}/measurement/{" + measId
            + "}");
    if (isInRole(orgId)) {
      Measurement ret = null;
      try {
        Depository depository = depot.getDepository(depositoryId, orgId, true);
        if (depository != null) {
          ret = depot.getMeasurement(depositoryId, orgId, measId, true);
        }
        else {
          setStatus(Status.CLIENT_ERROR_BAD_REQUEST, depositoryId + " does not exist.");
        }
      }
      catch (IdNotFoundException e) {
        setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
      }
      return ret;
    }
    else {
      setStatus(Status.CLIENT_ERROR_BAD_REQUEST, "Bad credentials.");
      return null;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.restlet.DepositoryMeasurementResource#store(org.wattdepot3
   * .datamodel.Measurement)
   */
  @Override
  public void update(Measurement meas) {
    getLogger().log(
        Level.INFO,
        "POST /wattdepot/{" + orgId + "}/depository/{" + depositoryId + "}/measurement/{" + measId
            + "} with " + meas);
    setStatus(Status.CLIENT_ERROR_BAD_REQUEST, "Cannot update Measurements.");
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot3.restlet.DepositoryMeasurementResource#remove(org.wattdepot3
   * .datamodel.Measurement)
   */
  @Override
  public void remove() {
    getLogger().log(
        Level.INFO,
        "DEL /wattdepot/{" + orgId + "}/depository/{" + depositoryId + "}/measurement/{" + measId
            + "}");
    if (isInRole(orgId)) {
      try {
        depot.deleteMeasurement(depositoryId, orgId, measId);
      }
      catch (IdNotFoundException e) {
        setStatus(Status.CLIENT_ERROR_BAD_REQUEST, depositoryId + " does not exist.");
      }
    }
    else {
      setStatus(Status.CLIENT_ERROR_BAD_REQUEST, "Bad credentials.");
    }
  }
}
