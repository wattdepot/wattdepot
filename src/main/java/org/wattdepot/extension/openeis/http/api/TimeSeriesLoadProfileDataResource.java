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

package org.wattdepot.extension.openeis.http.api;

import org.restlet.resource.Get;
import org.wattdepot.common.domainmodel.InterpolatedValueList;

/**
 * TimeSeriesLoadProfileDataResource - HTTP interface for getting the last month's load time series data.
 * See OpenEIS Time Series Load Profiling.
 * @author Cam Moore
 */
public interface TimeSeriesLoadProfileDataResource {
  /**
   * Defines GET <br/>
   * /wattdepot/{org-id}/openeis/time-series-load-profiling/data/?depository={depository_id}&sensor={sensor_id}.
   *
   * @return The InterpolatedValueList.
   */
  @Get("json")
  InterpolatedValueList retrieve();

}
