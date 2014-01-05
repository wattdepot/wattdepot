/**
 * SensorGroupsServerResource.java This file is part of WattDepot.
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

import org.restlet.data.Status;
import org.wattdepot.common.domainmodel.SensorGroup;
import org.wattdepot.common.domainmodel.SensorGroupList;
import org.wattdepot.common.exception.IdNotFoundException;
import org.wattdepot.common.http.api.SensorGroupsResource;

/**
 * SensorGroupsServerResource - Handles the SensorGroups HTTP API
 * ("/wattdepot/{org-id}/sensor-groups/").
 * 
 * @author Cam Moore
 * 
 */
public class SensorGroupsServerResource extends WattDepotServerResource implements
    SensorGroupsResource {

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.restlet.SensorGroupsResouce#retrieve()
   */
  @Override
  public SensorGroupList retrieve() {
    getLogger().log(Level.INFO, "GET /wattdepot/{" + orgId + "}/sensor-groups/");
    SensorGroupList ret = new SensorGroupList();
    try {
      for (SensorGroup sg : depot.getSensorGroups(orgId)) {
        ret.getGroups().add(sg);
      }
    }
    catch (IdNotFoundException e) {
      setStatus(Status.CLIENT_ERROR_BAD_REQUEST, orgId + " does not exist.");
    }
    return ret;
  }

}
