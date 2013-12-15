/**
 * SensorsServerResource.java This file is part of WattDepot.
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

import org.wattdepot.common.domainmodel.Sensor;
import org.wattdepot.common.domainmodel.SensorList;
import org.wattdepot.common.http.api.SensorsResource;

/**
 * SensorsServerResource - Handles the Sensors HTTP API
 * ("/wattdepot/{group_id}/sensors/").
 * 
 * @author Cam Moore
 * 
 */
public class SensorsServerResource extends WattDepotServerResource implements SensorsResource {

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.restlet.SensorsResouce#retrieve()
   */
  @Override
  public SensorList retrieve() {
    getLogger().log(Level.INFO, "GET /wattdepot/{" + groupId + "}/sensormodels/");
    SensorList ret = new SensorList();
    for (Sensor s : depot.getSensors(groupId)) {
      ret.getSensors().add(s);
    }
    return ret;
  }
}
