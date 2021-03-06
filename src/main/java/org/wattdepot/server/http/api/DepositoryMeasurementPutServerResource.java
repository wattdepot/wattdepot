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
import org.wattdepot.common.domainmodel.Sensor;
import org.wattdepot.common.exception.IdNotFoundException;
import org.wattdepot.common.exception.MeasurementTypeException;
import org.wattdepot.common.exception.MisMatchedOwnerException;
import org.wattdepot.common.http.api.DepositoryMeasurementPutResource;

/**
 * DepositoryMeasurementServerResource - Handles the Measurement HTTP API
 * ("/wattdepot/{org-id}/depository/{depository_id}/measurement/").
 * 
 * @author Cam Moore
 * 
 */
public class DepositoryMeasurementPutServerResource extends WattDepotServerResource implements
    DepositoryMeasurementPutResource {
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
   * @see
   * org.wattdepot.restlet.DepositoryMeasurementResource#store(org.wattdepot3
   * .datamodel.Measurement)
   */
  @Override
  public void store(Measurement meas) {
    getLogger().log(
        Level.INFO,
        "PUT /wattdepot/{" + orgId + "}/depository/{" + depositoryId + "}/measurement/ with "
            + meas);
    if (isInRole(orgId)) {
      try {
        Depository depository = depot.getDepository(depositoryId, orgId, true);
        if (depository != null) {
          Sensor sensor = depot.getSensor(meas.getSensorId(), orgId, true);
          if (sensor != null) {
            depot.putMeasurement(depositoryId, orgId, meas);
          }
          else {
            setStatus(Status.CLIENT_ERROR_BAD_REQUEST, "Sensor " + meas.getSensorId()
                + " does not exist");
          }
        }
        else {
          setStatus(Status.CLIENT_ERROR_BAD_REQUEST, depositoryId + " does not exist.");
        }
      }
      catch (MisMatchedOwnerException e) {
        setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
      }
      catch (MeasurementTypeException e) {
        setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
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
