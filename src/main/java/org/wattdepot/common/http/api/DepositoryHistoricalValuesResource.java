/*
 * This file is part of WattDepot.
 *
 *  Copyright (C) 2015  Cam Moore
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.wattdepot.common.http.api;

import org.restlet.resource.Get;
import org.wattdepot.common.domainmodel.InterpolatedValueList;

/**
 * DepositoryHistoricalValuesResource - HTTP interface for getting the historical values
 * for a depository and sensor or sensor group. <br>
 *   (/wattdepot/{org-id}/depository/{depository-id}/{hourly | daily}/historical-values/)
 *
 * @author Cam Moore
 */
@SuppressWarnings("PMD.UnusedModifier")
public interface DepositoryHistoricalValuesResource {

  /**
   * Defines GET <br/>
   * /wattdepot/{org-id}/depository/{depository-id}/{hourly | daily}/historical-values/?sensor={sensor_id}
   * &timestamp={time}&value-type={value-type}&samples={samples}.
   *
   * @return The DescriptiveStats.
   *
   * @return
   */
  @Get("json")
  public InterpolatedValueList retrieve();
}
