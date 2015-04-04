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

package org.wattdepot.common.http.api.openeis;

import org.restlet.resource.Get;

/**
 * HeatMapGvizResource - HTTP interface for getting the last year's load heat map data as a Google Visualization String.
 * See OpenEIS Heat Map.
 * @author Cam Moore
 */
public interface HeatMapGvizResource {
  /**
   * Defines GET <br/>
   * /wattdepot/{org-id}/openeis/heat-map/gviz/?power-depository={power_depository_id}&power-sensor={power_sensor_id}
   * &temp-depository={temp_depository_id}&temp-sensor={temp_sensor_id}.
   *
   * @return The Google Visualization String.
   */
  @Get("json")
  String retrieve();

}
