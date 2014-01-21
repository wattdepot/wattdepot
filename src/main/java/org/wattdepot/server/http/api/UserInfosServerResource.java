/**
 * UserInfosServerResource.java This file is part of WattDepot.
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
import org.wattdepot.common.domainmodel.UserInfo;
import org.wattdepot.common.domainmodel.UserInfoList;
import org.wattdepot.common.exception.IdNotFoundException;
import org.wattdepot.common.http.api.UserInfosResource;

/**
 * UserInfosServerResource handles the /wattdepot/{org-id}/users/ request.
 * 
 * @author Cam Moore
 * 
 */
public class UserInfosServerResource extends WattDepotServerResource implements UserInfosResource {

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.common.http.api.UserInfosResource#retrieve()
   */
  @Override
  public UserInfoList retrieve() {
    getLogger().log(Level.INFO, "GET /wattdepot/{" + orgId + "}/users/");
    UserInfoList ret = new UserInfoList();
    try {
      for (UserInfo u : depot.getUsers(orgId)) {
        ret.getUsers().add(u);
      }
    }
    catch (IdNotFoundException e) {
      setStatus(Status.CLIENT_ERROR_BAD_REQUEST, orgId + " is not a defined Organization id.");
    }
    return ret;
  }

}
