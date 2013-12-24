/**
 * WattDepotAdminInterface.java This file is part of WattDepot.
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
package org.wattdepot.client;

import org.wattdepot.common.domainmodel.Organization;
import org.wattdepot.common.domainmodel.UserInfo;
import org.wattdepot.common.domainmodel.UserPassword;
import org.wattdepot.common.exception.IdNotFoundException;

/**
 * WattDepotAdminInterface - Provides the functionality for the admin. Supports
 * manipulation of UserInfos and Organizations.
 * 
 * @author Cam Moore
 * 
 */
public interface WattDepotAdminInterface {
  /**
   * Deletes the given User.
   * 
   * @param id
   *          the unique id of the User.
   * @throws IdNotFoundException
   *           if the User's id is not found.
   */
  public void deleteUser(String id) throws IdNotFoundException;

  /**
   * Deletes the given Organization.
   * 
   * @param id
   *          the unique id of the Organization.
   * @throws IdNotFoundException
   *           if the Organization's id is not found.
   */
  public void deleteOrganization(String id) throws IdNotFoundException;

  /**
   * @param id
   *          the unique id of the UserPassword.
   * @throws IdNotFoundException
   *           if the UserPassword is not found.
   */
  public void deleteUserPassword(String id) throws IdNotFoundException;

  /**
   * Stores the given user in the WattDepot Server.
   * 
   * @param user
   *          The UserInfo to store.
   */
  public void putUser(UserInfo user);

  /**
   * Stores the given Organization in the WattDepot Server.
   * 
   * @param org
   *          the Organization to store.
   */
  public void putOrganization(Organization org);

  /**
   * Stores the given UserPassword in the WattDepot Server.
   * 
   * @param password
   *          The UserPassword to store.
   */
  public void putUserPassword(UserPassword password);
  
  /**
   * Updates the Organization in the WattDepot Server.
   * 
   * @param org the Organization to update.
   */
  public void updateOrganization(Organization org);
}
