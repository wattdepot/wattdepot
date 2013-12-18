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
package org.wattdepot.common.http.api;

import org.restlet.resource.Get;
import org.wattdepot.common.domainmodel.MeasuredValue;

/**
 * DepositoryValueResource - HTTP Interface for getting the MeasuredValues. <br/>
 * (/wattdepot/{group_id}/depository/{depository_id}/value)
 * 
 * @author Cam Moore
 *         Yongwen Xu
 * 
 */
public interface DepositoryValueResource {

  /**
   * Defines GET <br/>
   * /wattdepot/{group_id}/depository/{depository_id}/value/?
   *    sensor={sensorId}&start={start}&end={end}&gap={gapSeconds}
   * <br/> or GET
   * /wattdepot/{group_id}/depository/{depository_id}/value/?
   *    sensor={sensorId}&timestamp={timestamp}&gap={gapSeconds}
   * <br/> or GET
   * /wattdepot/{group_id}/depository/{depository_id}/value/?
   *    sensor={sensorId}&latest=true
   * <br/> or GET
   * /wattdepot/{group_id}/depository/{depository_id}/value/?
   *    sensor={sensorId}&earliest=true
   *
   * @return The MeasuredValue.
   */
  @Get("json")
  // Use JSON as transport encoding.
  public MeasuredValue retrieve();
}
