/**
 * SensorModelServerResource.java This file is part of WattDepot.
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
import org.wattdepot.common.domainmodel.Labels;
import org.wattdepot.common.domainmodel.SensorModel;
import org.wattdepot.common.exception.IdNotFoundException;
import org.wattdepot.common.http.api.SensorModelResource;

/**
 * SensorModelServerResource - Handles the SensorModel HTTP API
 * ("/wattdepot/sensormodel/", "/wattdepot/sensormodel/{sensormodel_id}").
 * 
 * @author Cam Moore
 * 
 */
public class SensorModelServerResource extends WattDepotServerResource implements
    SensorModelResource {

  /** The sensormodel_id from the request. */
  private String sensorModelId;

  /*
   * (non-Javadoc)
   * 
   * @see org.restlet.resource.Resource#doInit()
   */
  @Override
  protected void doInit() throws ResourceException {
    super.doInit();
    this.sensorModelId = getAttribute(Labels.SENSOR_MODEL_ID);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.restlet.SensorModelResource#retrieve()
   */
  @Override
  public SensorModel retrieve() {
    getLogger().log(Level.INFO, "GET /wattdepot/sensormodel/{" + sensorModelId + "}");
    SensorModel model = null;
    model = depot.getSensorModel(sensorModelId);
    if (model == null) {
      setStatus(Status.CLIENT_ERROR_EXPECTATION_FAILED, "SensorModel " + sensorModelId
          + " is not defined.");
    }
    return model;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.restlet.SensorModelResource#store(org.wattdepot
   * .datamodel.SensorModel)
   */
  @Override
  public void update(SensorModel sensormodel) {
    getLogger().log(Level.INFO,
        "POST /wattdepot/sensormodel/{" + sensorModelId + "} with " + sensormodel);
    if (depot.getSensorModelIds().contains(sensormodel.getId())) {
      depot.updateSensorModel(sensormodel);
    }
    else {
      setStatus(Status.CLIENT_ERROR_CONFLICT, "Can't update unknown SensorModel.");
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.restlet.SensorModelResource#remove()
   */
  @Override
  public void remove() {
    getLogger().log(Level.INFO, "DEL /wattdepot/sensormodel/{" + sensorModelId + "}");
    try {
      depot.deleteSensorModel(sensorModelId);
    }
    catch (IdNotFoundException e) {
      setStatus(Status.CLIENT_ERROR_FAILED_DEPENDENCY, e.getMessage());
    }
  }

}
