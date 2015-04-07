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
 * EnergySignatureDataResource - HTTP interface for getting the last year's energy signature data.
 * See OpenEIS Energy Signature.
 * @author Cam Moore
 */

public interface EnergySignatureGvizResource {
  /**
   * Defines GET <br/>
   * /wattdepot/{org-id}/openeis/energy-signature/gviz/?power-depository={depository_id}&power-sensor={sensor_id}
   * &temperature-depository={depository_id}&temperature-sensor={sensor_id}.
   *
   * @return The Google Visualization String.
   */
  @Get("json")
  String retrieve();


}
