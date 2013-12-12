/**
 * UserGroupsServerResource.java This file is part of WattDepot.
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
package org.wattdepot.server.httpapi;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.logging.Level;

import org.wattdepot.common.domainmodel.Property;
import org.wattdepot.common.domainmodel.UserGroup;
import org.wattdepot.common.domainmodel.UserInfo;
import org.wattdepot.common.httpapi.UserGroupsResource;

/**
 * UserGroupsServerResource - Handles the UserGroup HTTP API
 * ("/wattdepot/{group_id}/groups").
 * 
 * @author Cam Moore
 * 
 */
public class UserGroupsServerResource extends WattDepotServerResource implements UserGroupsResource {

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.restlet.UserGroupsResource#retrieve()
   */
  @Override
  public ArrayList<UserGroup> retrieve() {
    getLogger().log(Level.INFO, "GET /wattdepot/{" + groupId + "}/usergroups/");
    ArrayList<UserGroup> ret = new ArrayList<UserGroup>();
    UserGroup g1 = new UserGroup("UH");
    UserInfo i1 = new UserInfo("cmoore", "Cam", "Moore", "cmoore@hawaii.edu", false,
        new HashSet<Property>());
    g1.add(i1);
    ret.add(g1);
    return ret;
  }

}
