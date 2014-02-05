/**
 * MeasurementTypesServerResource.java This file is part of WattDepot.
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

import org.wattdepot.common.domainmodel.MeasurementType;
import org.wattdepot.common.domainmodel.MeasurementTypeList;
import org.wattdepot.common.http.api.MeasurementTypesResource;

/**
 * MeasurementTypesServerResource - ServerResource that handles the URI
 * "/wattdepot/measurementtypes/".
 * 
 * @author Cam Moore
 * 
 */
public class MeasurementTypesServerResource extends WattDepotServerResource implements
    MeasurementTypesResource {

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.restlet.MeasurementTypesResource#retrieve()
   */
  @Override
  public MeasurementTypeList retrieve() {
    getLogger().log(Level.INFO, "GET /wattdepot/public/measurement-types/");
    MeasurementTypeList list = new MeasurementTypeList();
    for (MeasurementType mt : depot.getMeasurementTypes()) {
      list.getMeasurementTypes().add(mt);
    }
    return list;
  }

}
