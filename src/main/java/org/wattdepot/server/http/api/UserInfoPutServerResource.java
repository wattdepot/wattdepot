/**
 * UserInfoPutServerResource.java This file is part of WattDepot.
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
import org.restlet.security.MemoryRealm;
import org.restlet.security.Role;
import org.restlet.security.User;
import org.wattdepot.common.domainmodel.Organization;
import org.wattdepot.common.domainmodel.UserInfo;
import org.wattdepot.common.exception.IdNotFoundException;
import org.wattdepot.common.exception.UniqueIdException;
import org.wattdepot.common.http.api.UserInfoPutResource;

/**
 * UserInfoPutServerResource - Handles the HTTP API
 * ("/wattdepot/{org-id}/user/").
 * 
 * @author Cam Moore
 * 
 */
public class UserInfoPutServerResource extends WattDepotServerResource implements
    UserInfoPutResource {

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.common.http.api.UserInfoPutResource#store(org.wattdepot.common
   * .domainmodel.UserInfo)
   */
  @Override
  public void store(UserInfo user) {
    getLogger().log(Level.INFO, "PUT /wattdepot/{" + orgId + "}/user/ with " + user);
    if (isInRole(orgId) || isInRole(Organization.ADMIN_GROUP.getId())) {
      if (orgId.equals(user.getOrganizationId()) || orgId.equals(Organization.ADMIN_GROUP_NAME)) {
        try {
          if (!depot.getUserIds(orgId, true).contains(user.getUid())) {
            try {
              UserInfo defined = depot.defineUserInfo(user.getUid(), user.getFirstName(),
                  user.getLastName(), user.getEmail(), user.getOrganizationId(),
                  user.getProperties(), user.getPassword());
              // Add user to Realm
              WattDepotApplication app = (WattDepotApplication) getApplication();
              Role role = app.getRole(user.getOrganizationId());
              if (role == null) {
                role = new Role(user.getOrganizationId());
                app.getRoles().add(role);
              }
              MemoryRealm realm = (MemoryRealm) app.getComponent().getRealm("WattDepot Security");
              User u = new User(defined.getUid(), user.getPassword(), defined.getFirstName(),
                  defined.getLastName(), defined.getEmail());
              realm.getUsers().add(u);
              realm.map(u, role);
            }
            catch (UniqueIdException e) {
              setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
            }
            catch (IdNotFoundException e) {
              setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
            }
          }
        }
        catch (IdNotFoundException e) {
          setStatus(Status.CLIENT_ERROR_BAD_REQUEST, orgId + " is not a defined Organization id.");
        }
      }
      else {
        setStatus(Status.CLIENT_ERROR_BAD_REQUEST, "User " + user.getUid() + " is not in " + orgId);
      }
    }
    else {
      setStatus(Status.CLIENT_ERROR_BAD_REQUEST, "Bad credentials, you cannot create a User.");
    }
  }
}
