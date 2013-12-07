/**
 * DepositoryValueResource.java This file is part of WattDepot.
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

import org.restlet.resource.Get;
import org.wattdepot.datamodel.MeasuredValue;

/**
 * DepositoryValueResource - HTTP Interface for getting the MeasuredValues. <br>
 * GET /wattdepot/depository/{depository_id}/value/?sensor={sensor_id},time
 * ={timestamp} or <br>
 * GET /wattdepot/depository/{depository_id}/value/?sensor={sensor_id
 * },start={timestamp},end={timestamp}
 * 
 * @author Cam Moore
 * 
 */
public interface DepositoryValueResource {

  /**
   * Defines GET
   * /wattdepot/depository/{depository_id}/value/?sensor={sensor_id},
   * time={timestamp} or <br>GET
   * /wattdepot/depository/{depository_id}/value/?sensor=
   * {sensor_id},start={timestamp},end={timestamp}.
   * 
   * @return The MeasuredValue.
   */
  @Get("json")
  // Use JSON as transport encoding.
  public MeasuredValue retrieve();
}
