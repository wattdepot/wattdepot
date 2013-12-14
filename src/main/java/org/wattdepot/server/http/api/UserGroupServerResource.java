/**
 * UserGroupServerResource.java This file is part of WattDepot.
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
import org.restlet.security.Role;
import org.restlet.security.User;
import org.wattdepot.common.domainmodel.Labels;
import org.wattdepot.common.domainmodel.UserGroup;
import org.wattdepot.common.domainmodel.UserInfo;
import org.wattdepot.common.exception.IdNotFoundException;
import org.wattdepot.common.exception.UniqueIdException;
import org.wattdepot.common.http.api.UserGroupResource;

/**
 * UserGroupServerResource - Handles the HTTP API
 * ("/wattdepot/{group_id}/usergroup/",
 * "/wattdepot/{group_id}/usergroup/{usergroup_id}").
 * 
 * @author Cam Moore
 * 
 */
public class UserGroupServerResource extends WattDepotServerResource implements UserGroupResource {

  private String userGroupId;

  /*
   * (non-Javadoc)
   * 
   * @see org.restlet.resource.Resource#doInit()
   */
  @Override
  protected void doInit() throws ResourceException {
    super.doInit();
    this.userGroupId = getAttribute(Labels.USER_GROUP_ID);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.restlet.UserGroupResource#retrieve()
   */
  @Override
  public UserGroup retrieve() {
    getLogger().log(Level.INFO, "GET /wattdepot/{" + groupId + "}/usergroup/{" + userGroupId + "}");
    UserGroup group = null;
    group = depot.getUserGroup(userGroupId);
    return group;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.restlet.UserGroupResource#store(org.wattdepot.datamodel
   * .UserGroup)
   */
  @Override
  public void store(UserGroup usergroup) {
    getLogger().log(Level.INFO, "PUT /wattdepot/{" + groupId + "}/usergroup/ with " + usergroup);
    if (!depot.getUserGroupIds().contains(usergroup.getId())) {
      try {
        UserGroup defined = depot.defineUserGroup(usergroup.getName(), usergroup.getUsers());
        WattDepotApplication app = (WattDepotApplication) getApplication();
        // create the new Role for the group
        String roleName = defined.getId();
        Role role = new Role(roleName);
        app.getRoles().add(role);
        MemoryRealm realm = (MemoryRealm) app.getComponent().getRealm("WattDepot Security");
        for (User user : realm.getUsers()) { // loop through all the Restlet
                                             // users
          for (UserInfo info : defined.getUsers()) {
            if (user.getIdentifier().equals(info.getId())) {
              // assign the user to the role.
              realm.map(user, role);
            }
          }
        }
      }
      catch (UniqueIdException e) {
        setStatus(Status.CLIENT_ERROR_CONFLICT, e.getMessage());
      }
    }
    else {
      depot.updateUserGroup(usergroup);
      // update the Realm
      WattDepotApplication app = (WattDepotApplication) getApplication();
      // create the new Role for the group
      String roleName = usergroup.getId();
      Role role = app.getRole(roleName);
      MemoryRealm realm = (MemoryRealm) app.getComponent().getRealm("WattDepot Security");
      for (UserInfo info : usergroup.getUsers()) {
        realm.map(getUser(info), role);
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.restlet.UserGroupResource#remove()
   */
  @Override
  public void remove() {
    getLogger().log(Level.INFO, "DEL /wattdepot/{" + groupId + "}/usergroup/{" + userGroupId + "}");
    try {
      depot.deleteUserGroup(userGroupId);
      WattDepotApplication app = (WattDepotApplication) getApplication();
      // create the new Role for the group
      String roleName = userGroupId;
      Role role = app.getRole(roleName);
      MemoryRealm realm = (MemoryRealm) app.getComponent().getRealm("WattDepot Security");
      app.getRoles().remove(role);
      for (User user : realm.getUsers()) {
        realm.findRoles(user).remove(role);
      }
    }
    catch (IdNotFoundException e) {
      setStatus(Status.CLIENT_ERROR_FAILED_DEPENDENCY, e.getMessage());
    }
  }

  /**
   * @param info
   *          The UserInfo instance.
   * @return The Restlet User that corresponds to the given UserInfo.
   */
  private User getUser(UserInfo info) {
    WattDepotApplication app = (WattDepotApplication) getApplication();
    MemoryRealm realm = (MemoryRealm) app.getComponent().getRealm("WattDepot Security");
    for (User user : realm.getUsers()) { // loop through all the Restlet users
      if (user.getIdentifier().equals(info.getId())) {
        return user;
      }
    }
    return null;
  }
}
