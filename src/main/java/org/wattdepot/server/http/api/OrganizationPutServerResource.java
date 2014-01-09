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
import org.restlet.security.MemoryRealm;
import org.restlet.security.Role;
import org.restlet.security.User;
import org.wattdepot.common.domainmodel.Organization;
import org.wattdepot.common.exception.BadSlugException;
import org.wattdepot.common.exception.IdNotFoundException;
import org.wattdepot.common.exception.UniqueIdException;
import org.wattdepot.common.http.api.OrganizationPutResource;

/**
 * UserGroupServerResource - Handles the HTTP API
 * ("/wattdepot/{org-id}/organization/").
 * 
 * @author Cam Moore
 * 
 */
public class OrganizationPutServerResource extends WattDepotServerResource
    implements OrganizationPutResource {

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.restlet.UserGroupResource#store(org.wattdepot.datamodel
   * .UserGroup)
   */
  @Override
  public void store(Organization usergroup) {
    getLogger().log(Level.INFO,
        "PUT /wattdepot/{" + orgId + "}/organization/ with " + usergroup);
    if (!depot.getOrganizationIds().contains(usergroup.getId())) {
      try {
        Organization defined = depot.defineOrganization(usergroup.getId(),
            usergroup.getName(), usergroup.getUsers());
        defined.setId(usergroup.getId());
        depot.updateOrganization(defined);
        WattDepotApplication app = (WattDepotApplication) getApplication();
        // create the new Role for the group
        String roleName = defined.getId();
        Role role = new Role(roleName);
        app.getRoles().add(role);
        MemoryRealm realm = (MemoryRealm) app.getComponent().getRealm(
            "WattDepot Security");
        for (User user : realm.getUsers()) { // loop through all the Restlet
                                             // users
          for (String userId : defined.getUsers()) {
            if (user.getIdentifier().equals(userId)) {
              // assign the user to the role.
              realm.map(user, role);
            }
          }
        }
      }
      catch (UniqueIdException e) {
        setStatus(Status.CLIENT_ERROR_CONFLICT, e.getMessage());
      }
      catch (IdNotFoundException e) {
        setStatus(Status.CLIENT_ERROR_BAD_REQUEST, orgId
            + " is not a defined Organization.");
      }
      catch (BadSlugException e) {
        setStatus(Status.CLIENT_ERROR_EXPECTATION_FAILED, e.getMessage());
      }
    }
    else {
      try {
        depot.updateOrganization(usergroup);
        // update the Realm
        WattDepotApplication app = (WattDepotApplication) getApplication();
        // create the new Role for the group
        String roleName = usergroup.getId();
        Role role = app.getRole(roleName);
        MemoryRealm realm = (MemoryRealm) app.getComponent().getRealm(
            "WattDepot Security");
        for (String userId : usergroup.getUsers()) {
          realm.map(getUser(userId), role);
        }
      }
      catch (IdNotFoundException e) {
        setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
      }
    }

  }

  /**
   * @param userId
   *          The id of the UserInfo instance.
   * @return The Restlet User that corresponds to the given UserInfo.
   */
  private User getUser(String userId) {
    WattDepotApplication app = (WattDepotApplication) getApplication();
    MemoryRealm realm = (MemoryRealm) app.getComponent().getRealm(
        "WattDepot Security");
    for (User user : realm.getUsers()) { // loop through all the Restlet users
      if (user.getIdentifier().equals(userId)) {
        return user;
      }
    }
    return null;
  }

}
