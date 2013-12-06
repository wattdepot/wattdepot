/**
 * DepositoryMeasurementServerResource.java This file is part of WattDepot 3.
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
package org.wattdepot3.server.restlet;

import java.util.logging.Level;

import org.restlet.data.Status;
import org.restlet.resource.ResourceException;
import org.wattdepot3.datamodel.Depository;
import org.wattdepot3.datamodel.Measurement;
import org.wattdepot3.exception.MeasurementTypeException;
import org.wattdepot3.exception.MissMatchedOwnerException;
import org.wattdepot3.restlet.DepositoryMeasurementResource;

/**
 * DepositoryMeasurementServerResource - Handles the Measurement HTTP API
 * ("/wattdepot/{group_id}/depository/{depository_id}/measurement/").
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
    this.depositoryId = getAttribute("depository_id");
    this.measId = getAttribute("meas_id");
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot3.restlet.DepositoryMeasurementResource#store(org.wattdepot3
   * .datamodel.Measurement)
   */
  @Override
  public void store(Measurement meas) {
    getLogger().log(Level.INFO, "PUT /wattdepot/{" + groupId + "}/depository/{" + depositoryId
        + "}/measurement/ with " + meas);
    try {
      Depository depository = depot.getWattDeposiory(depositoryId, groupId);
      if (depository != null) {
        depository.putMeasurement(meas);
      }
      else {
        setStatus(Status.CLIENT_ERROR_BAD_REQUEST, depositoryId + " does not exist.");
      }
    }
    catch (MissMatchedOwnerException e) {
      setStatus(Status.CLIENT_ERROR_CONFLICT, e.getMessage());
    }
    catch (MeasurementTypeException e) {
      setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
    }
  }

  /* (non-Javadoc)
   * @see org.wattdepot3.restlet.DepositoryMeasurementResource#remove(org.wattdepot3.datamodel.Measurement)
   */
  @Override
  public void remove(Measurement meas) {
    getLogger().log(Level.INFO, "DEL /wattdepot/{" + groupId + "}/depository/{" + depositoryId
        + "}/measurement/{" + measId + "} with " + meas);
    try {
      Depository depository = depot.getWattDeposiory(depositoryId, groupId);
      if (depository != null) {
        depository.deleteMeasurement(meas);
      }
      else {
        setStatus(Status.CLIENT_ERROR_BAD_REQUEST, depositoryId + " does not exist.");
      }
    }
    catch (MissMatchedOwnerException e) {
      setStatus(Status.CLIENT_ERROR_CONFLICT, e.getMessage());
    }    
  }

}
