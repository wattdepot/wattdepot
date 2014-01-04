/**
 * SensorModelsServerResource.java This file is part of WattDepot.
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

import org.wattdepot.common.domainmodel.SensorModel;
import org.wattdepot.common.domainmodel.SensorModelList;
import org.wattdepot.common.http.api.SensorModelsResource;

/**
 * SensorModelsServerResource - Handles the SensorGroups HTTP API
 * ("/wattdepot/public/sensor-models/").
 * 
 * @author Cam Moore
 * 
 */
public class SensorModelsServerResource extends WattDepotServerResource implements
    SensorModelsResource {

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.restlet.SensorModelsResouce#retrieve()
   */
  @Override
  public SensorModelList retrieve() {
    getLogger().log(Level.INFO, "GET /wattdepot/public/sensor-models/");
    SensorModelList ret = new SensorModelList();
    for (SensorModel sm : depot.getSensorModels()) {
      ret.getModels().add(sm);
    }
    return ret;
  }
}
