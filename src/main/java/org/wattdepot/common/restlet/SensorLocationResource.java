/**
 * SensorLocationResource.java This file is part of WattDepot.
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
package org.wattdepot.common.restlet;

import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Put;
import org.wattdepot.common.domainmodel.SensorLocation;

/**
 * LocationResource - HTTP Interface for data model Location.
 * 
 * @author Cam Moore
 * 
 */
public interface SensorLocationResource {

  /**
   * Defines GET /wattdepot/{group_id}/location/{location_id} API call.
   * 
   * @return The Location with the given id. The id is sent in the request.
   */
  @Get("json") // Use JSON as transport encoding.
  public SensorLocation retrieve();

  /**
   * Defines the PUT /wattdepot/{group_id}/location/ API call.
   * 
   * @param sensorLocation
   *          The Location to store.
   */
  @Put("json")
  public void store(SensorLocation sensorLocation);

  /**
   * Defined the DEL /wattdepot/{group_id}/location/{location_id} API call. The id is sent
   * in the request.
   */
  @Delete
  public void remove();

}
