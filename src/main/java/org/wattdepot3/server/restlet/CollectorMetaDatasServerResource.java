/**
 * SensorProcessesServerResource.java This file is part of WattDepot 3.
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

import org.wattdepot3.datamodel.CollectorMetaData;
import org.wattdepot3.datamodel.CollectorMetaDataList;
import org.wattdepot3.restlet.CollectorMetaDatasResource;

/**
 * SensorProcessesServerResource - Handles the SensorProcesses HTTP API
 * ("/wattdepot/{group_id}/sensorprocesses/").
 * 
 * @author Cam Moore
 * 
 */
public class CollectorMetaDatasServerResource extends WattDepotServerResource implements
    CollectorMetaDatasResource {

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot3.restlet.SensorProcessesResouce#retrieve()
   */
  @Override
  public CollectorMetaDataList retrieve() {
    getLogger().log(Level.INFO, "GET /wattdepot/{" + groupId + "}/sensorprocesses/");
    CollectorMetaDataList ret = new CollectorMetaDataList();
    for (CollectorMetaData sp : depot.getCollectorMetaDatas(groupId)) {
      ret.getDatas().add(sp);
    }
    return ret;
  }
}
