/**
 * DepositoryMinimumValuesResource.java This file is part of WattDepot.
 *
 * Copyright (C) 2014  Cam Moore
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
import org.wattdepot.common.domainmodel.InterpolatedValueList;

/**
 * DepositoryMinimumValuesResource - HTTP Interface for getting a
 * MeasuredValueList of minimum values. <br>
 * (/wattdepot/{org-id}/depository/{depository-id}/values/minimum/)
 * 
 * @author Cam Moore
 * 
 */
@SuppressWarnings("PMD.UnusedModifier")
public interface DepositoryMinimumValuesResource {

  /**
   * Defines GET <br/>
   * /wattdepot/{org-id}/depository/{depository-id}/values/minimum/?
   * sensor={sensor_id
   * }&start={start}&end={end}&interval={interval}&value-type={type}.
   * 
   * @return The MeasuredValueList.
   */
  @Get("json")
  // Use JSON as transport encoding.
  public InterpolatedValueList retrieve();

}
