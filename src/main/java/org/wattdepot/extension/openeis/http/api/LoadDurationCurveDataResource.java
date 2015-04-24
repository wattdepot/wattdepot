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
 * LoadDurationCurveDataResource - HTTP interface for getting the load duration curve data.
 * See OpenEIS Load Duration Curve.
 * @author Cam Moore
 * Created by carletonmoore on 4/22/15.
 */
public interface LoadDurationCurveDataResource {
  /**
   * Defines GET <br/>
   * /wattdepot/{org-id}/openeis/load-duration-curve/data/?depository={depository_id}&sensor={sensor_id}
   * &start={start}&end={end}.
   *
   * @return The InterpolatedValueList.
   */
  @Get("json")
  InterpolatedValueList retrieve();

}
