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
import org.wattdepot.common.domainmodel.Organization;
import org.wattdepot.common.exception.IdNotFoundException;
import org.wattdepot.common.http.api.OrganizationResource;

/**
 * UserGroupServerResource - Handles the HTTP API
 * ("/wattdepot/{org-id}/organization/{org-id}").
 * 
 * @author Cam Moore
 * 
 */
public class OrganizationServerResource extends WattDepotServerResource implements
    OrganizationResource {

  private String userGroupId;

  /*
   * (non-Javadoc)
   * 
   * @see org.restlet.resource.Resource#doInit()
   */
  @Override
  protected void doInit() throws ResourceException {
    super.doInit();
    this.userGroupId = getAttribute(Labels.ORGANIZATION_ID2);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.restlet.UserGroupResource#retrieve()
   */
  @Override
  public Organization retrieve() {
    getLogger()
        .log(Level.INFO, "GET /wattdepot/{" + orgId + "}/organization/{" + userGroupId + "}");
    Organization group = null;
    try {
      group = depot.getOrganization(userGroupId);
    }
    catch (IdNotFoundException e) {
      setStatus(Status.CLIENT_ERROR_BAD_REQUEST, orgId + " is not a defined Organization.");
    }
    return group;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.restlet.UserGroupResource#remove()
   */
  @Override
  public void remove() {
    getLogger()
        .log(Level.INFO, "DEL /wattdepot/{" + orgId + "}/organization/{" + userGroupId + "}");
    try {
      depot.deleteOrganization(userGroupId);
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
      setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
    }
  }

  /**
   * @param userId
   *          The id of the UserInfo instance.
   * @return The Restlet User that corresponds to the given UserInfo.
   */
  private User getUser(String userId) {
    WattDepotApplication app = (WattDepotApplication) getApplication();
    MemoryRealm realm = (MemoryRealm) app.getComponent().getRealm("WattDepot Security");
    for (User user : realm.getUsers()) { // loop through all the Restlet users
      if (user.getIdentifier().equals(userId)) {
        return user;
      }
    }
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.common.http.api.OrganizationResource#update(org.wattdepot
   * .common.domainmodel.Organization)
   */
  @Override
  public void update(Organization organization) {
    try {
      depot.updateOrganization(organization);
      // update the Realm
      WattDepotApplication app = (WattDepotApplication) getApplication();
      // create the new Role for the group
      String roleName = organization.getId();
      Role role = app.getRole(roleName);
      MemoryRealm realm = (MemoryRealm) app.getComponent().getRealm("WattDepot Security");
      for (String userId : organization.getUsers()) {
        realm.map(getUser(userId), role);
      }
    }
    catch (IdNotFoundException e) {
      setStatus(Status.CLIENT_ERROR_BAD_REQUEST, organization + " isn't a defined Organization.");
    }
  }
}
