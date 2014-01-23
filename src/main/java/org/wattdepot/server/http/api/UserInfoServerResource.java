/**
 * UserInfoServerResource.java This file is part of WattDepot.
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
import org.restlet.resource.ResourceException;
import org.wattdepot.common.domainmodel.Labels;
import org.wattdepot.common.domainmodel.UserInfo;
import org.wattdepot.common.domainmodel.UserPassword;
import org.wattdepot.common.exception.IdNotFoundException;
import org.wattdepot.common.http.api.UserInfoResource;

/**
 * UserInfoServerResource - Handles the UserInfo HTTP API
 * ("/wattdepot/{org-id}/user/{user-id}").
 * 
 * @author Cam Moore
 * 
 */
public class UserInfoServerResource extends WattDepotServerResource implements UserInfoResource {
  private String userId;

  /*
   * (non-Javadoc)
   * 
   * @see org.restlet.resource.Resource#doInit()
   */
  @Override
  protected void doInit() throws ResourceException {
    super.doInit();
    this.userId = getAttribute(Labels.USER_ID);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.restlet.UserInfoResource#retrieve()
   */
  @Override
  public UserInfo retrieve() {
    getLogger().log(Level.INFO, "GET /wattdepot/{" + orgId + "}/user/{" + userId + "}");
    UserInfo user = null;
    try {
      user = depot.getUser(userId, orgId);
    }
    catch (IdNotFoundException e) {
      setStatus(Status.CLIENT_ERROR_BAD_REQUEST, "User " + userId + " is not defined.");
    }
    return user;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.restlet.UserInfoResource#store(org.wattdepot.datamodel
   * .UserInfo)
   */
  @Override
  public void update(UserInfo user) {
    getLogger().log(Level.INFO,
        "POST /wattdepot/{" + orgId + "}/user/{" + userId + "} with " + user);
    try {
      depot.updateUserInfo(user);
      UserPassword password = depot.getUserPassword(user.getUid(), user.getOrganizationId());
      password.setPassword(user.getPassword());
      depot.updateUserPassword(password);
    }
    catch (IdNotFoundException e) {
      setStatus(Status.CLIENT_ERROR_BAD_REQUEST, "No User " + userId + " in WattDepot.");
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.restlet.UserInfoResource#remove()
   */
  @Override
  public void remove() {
    getLogger().log(Level.INFO, "DEL /wattdepot/{" + orgId + "}/user/{" + userId + "}");
    try {
      depot.deleteUser(userId, orgId);
    }
    catch (IdNotFoundException e) {
      setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
    }
  }

}
