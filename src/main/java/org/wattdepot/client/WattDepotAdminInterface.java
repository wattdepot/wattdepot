/**
 * WattDepotAdminInterface.java This file is part of WattDepot 3.
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

import org.wattdepot.datamodel.UserGroup;
import org.wattdepot.datamodel.UserInfo;
import org.wattdepot.datamodel.UserPassword;
import org.wattdepot.exception.IdNotFoundException;

/**
 * WattDepotAdminInterface - Provides all the functionality of the
 * WattDepotInterface adding UserInfo and UserGroup functionality.
 * 
 * @author Cam Moore
 * 
 */
public interface WattDepotAdminInterface extends WattDepotInterface {
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
   * Deletes the given UserGroup.
   * 
   * @param id
   *          the unique id of the UserGroup.
   * @throws IdNotFoundException
   *           if the UserGroup's id is not found.
   */
  public void deleteUserGroup(String id) throws IdNotFoundException;

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
   * Stores the given UserGroup in the WattDepot Server.
   * 
   * @param group
   *          the UserGroup to store.
   */
  public void putUserGroup(UserGroup group);

  /**
   * Stores the given UserPassword in the WattDepot Server.
   * 
   * @param password
   *          The UserPassword to store.
   */
  public void putUserPassword(UserPassword password);
}
