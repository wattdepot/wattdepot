/**
 * SensorLocationsServerResource.java This file is part of WattDepot.
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
package org.wattdepot.server.http.api;

import java.util.logging.Level;

import org.wattdepot.common.domainmodel.SensorLocation;
import org.wattdepot.common.domainmodel.SensorLocationList;
import org.wattdepot.common.http.api.SensorLocationsResource;

/**
 * LocationsServerResource - ServerResource that handles the URI
 * "/wattdepot/{org-id}/locations/".
 * 
 * @author Cam Moore
 * 
 */
public class SensorLocationsServerResource extends WattDepotServerResource implements SensorLocationsResource {

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.restlet.LocationsResource#retrieve()
   */
  @Override
  public SensorLocationList retrieve() {
    getLogger().log(Level.INFO, "GET /wattdepot/{" + orgId + "}/locations/");
    SensorLocationList list = new SensorLocationList();
    for (SensorLocation l : depot.getLocations(orgId)) {
      list.getLocations().add(l);
    }
    return list;
  }

}
