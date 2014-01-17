/**
 * UserPasswordServerResource.java This file is part of WattDepot.
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
import org.wattdepot.common.domainmodel.UserPassword;
import org.wattdepot.common.exception.IdNotFoundException;
import org.wattdepot.common.exception.UniqueIdException;
import org.wattdepot.common.http.api.UserPasswordResource;

/**
 * UserPasswordServerResource - Handles the UserPassword HTTP API
 * ("/wattdepot/{org-id}/user-password/{user-id}").
 * 
 * @author Cam Moore
 * 
 */
public class UserPasswordServerResource extends WattDepotServerResource implements
    UserPasswordResource {
  private String userId;

  /*
   * (non-Javadoc)
   * 
   * @see org.restlet.resource.Resource#doInit()
   */
  @Override
  protected void doInit() throws ResourceException {
    super.doInit();
    this.userId = getAttribute(Labels.USER_PASSWORD_ID);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.restlet.UserPasswordResource#retrieve()
   */
  @Override
  public UserPassword retrieve() {
    getLogger().log(Level.INFO, "GET /wattdepot/{" + orgId + "}/user-password/{" + userId + "}");
    UserPassword user;
    try {
      user = depot.getUserPassword(userId, orgId);
      // don't return the plain text password.
      user.setPlainText("********");
      return user;
    }
    catch (IdNotFoundException e) {
      setStatus(Status.CLIENT_ERROR_EXPECTATION_FAILED, "User " + userId + " is not defined.");
    }
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.restlet.UserPasswordResource#store(org.wattdepot.datamodel
   * .UserPassword)
   */
  @Override
  public void store(UserPassword user) {
    getLogger().log(Level.INFO, "PUT /wattdepot/{" + orgId + "}/user-password/ with " + user);
    if (!depot.getUserIds(orgId).contains(user.getUid())) {
      try {
        depot.defineUserPassword(user.getUid(), orgId, user.getPlainText(),
            user.getEncryptedPassword());
      }
      catch (UniqueIdException e) {
        setStatus(Status.CLIENT_ERROR_CONFLICT, e.getMessage());
      }
      catch (IdNotFoundException e) {
        setStatus(Status.CLIENT_ERROR_EXPECTATION_FAILED, user + " isn't defined so can't update.");
      }
    }
    else {
      try {
        depot.updateUserPassword(user);
      }
      catch (IdNotFoundException e) {
        setStatus(Status.CLIENT_ERROR_EXPECTATION_FAILED, user + " isn't defined so can't update.");
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.restlet.UserPasswordResource#remove()
   */
  @Override
  public void remove() {
    getLogger().log(Level.INFO, "DEL /wattdepot/{" + orgId + "}/user-password/{" + userId + "}");
    try {
      depot.deleteUserPassword(userId, orgId);
    }
    catch (IdNotFoundException e) {
      setStatus(Status.CLIENT_ERROR_EXPECTATION_FAILED, e.getMessage());
    }
  }

}
