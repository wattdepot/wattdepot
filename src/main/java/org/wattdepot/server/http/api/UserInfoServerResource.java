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
import org.restlet.security.MemoryRealm;
import org.restlet.security.User;
import org.wattdepot.common.domainmodel.UserGroup;
import org.wattdepot.common.domainmodel.UserInfo;
import org.wattdepot.common.domainmodel.UserPassword;
import org.wattdepot.common.exception.IdNotFoundException;
import org.wattdepot.common.exception.UniqueIdException;
import org.wattdepot.common.httpapi.UserInfoResource;

/**
 * UserInfoServerResource - Handles the UserInfo HTTP API
 * ("/wattdepot/{group_id}/user/{user_id}").
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
    this.userId = getAttribute("user_id");
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.restlet.UserInfoResource#retrieve()
   */
  @Override
  public UserInfo retrieve() {
    getLogger().log(Level.INFO, "GET /wattdepot/{" + groupId + "}/user/{" + userId + "}");
    UserInfo user = depot.getUser(userId);
    if (user == null) {
      setStatus(Status.CLIENT_ERROR_EXPECTATION_FAILED, "User " + userId + " is not defined.");
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
  public void store(UserInfo user) {
    getLogger().log(Level.INFO, "PUT /wattdepot/{" + groupId + "}/user/ with " + user);
    if (!depot.getUsers().contains(user)) {
      try {
        UserInfo defined = depot.defineUserInfo(user.getId(), user.getFirstName(),
            user.getLastName(), user.getEmail(), user.getAdmin(), user.getProperties());
        WattDepotApplication app = (WattDepotApplication) getApplication();
        UserPassword up = app.getDepot().getUserPassword(user.getId());
        if (up != null) {
          MemoryRealm realm = (MemoryRealm) app.getComponent().getRealm("WattDepot Security");
          User newUser = new User(user.getId(), up.getPlainText(), user.getFirstName(),
              user.getLastName(), user.getEmail());
          realm.getUsers().add(newUser);
          realm.map(newUser, app.getRole("User"));
          if (user.getAdmin()) {
            UserGroup.ADMIN_GROUP.add(defined);
            depot.updateUserGroup(UserGroup.ADMIN_GROUP);
          }
        }
        else {
          setStatus(Status.CLIENT_ERROR_BAD_REQUEST, "No password information for " + user.getId());
        }
      }
      catch (UniqueIdException e) {
        setStatus(Status.CLIENT_ERROR_CONFLICT, e.getMessage());
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.restlet.UserInfoResource#remove()
   */
  @Override
  public void remove() {
    getLogger().log(Level.INFO, "DEL /wattdepot/{" + groupId + "}/user/{" + userId + "}");
    try {
      depot.deleteUser(userId);
    }
    catch (IdNotFoundException e) {
      setStatus(Status.CLIENT_ERROR_EXPECTATION_FAILED, e.getMessage());
    }
  }

}
