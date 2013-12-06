/**
 * SensorModelResource.java This file is part of WattDepot 3.
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
package org.wattdepot.restlet;

import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Put;
import org.wattdepot.datamodel.SensorModel;

/**
 * SensorModelResource - HTTP Interface for data model SensorModel.
 * 
 * @author Cam Moore
 * 
 */
public interface SensorModelResource {

  /**
   * Defines GET /wattdepot/sensormodel/{sensormodel_id} API call.
   * 
   * @return The SensorModel with the given id. The id is sent in the request.
   */
  @Get("json") // Use JSON as transport encoding.
  public SensorModel retrieve();

  /**
   * Defines the PUT /wattdepot/sensormodel/ API call.
   * 
   * @param sensormodel
   *          The SensorModel to store.
   */
  @Put
  public void store(SensorModel sensormodel);

  /**
   * Defined the DEL /wattdepot/sensormodel/{sensormodel_id} API call. The id is
   * sent in the request.
   */
  @Delete
  public void remove();

}
