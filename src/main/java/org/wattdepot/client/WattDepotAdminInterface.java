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
import org.wattdepot.common.domainmodel.OrganizationList;
import org.wattdepot.common.domainmodel.UserInfo;
import org.wattdepot.common.domainmodel.UserInfoList;
import org.wattdepot.common.domainmodel.UserPassword;
import org.wattdepot.common.exception.IdNotFoundException;

/**
 * WattDepotAdminInterface - Provides the functionality for the admin. Supports
 * manipulation of UserInfos and Organizations.
 * 
 * @author Cam Moore
 * 
 */
//@SuppressWarnings(PMD)
public interface WattDepotAdminInterface {
  /**
   * Deletes the given User.
   * 
   * @param id the unique id of the User.
   * @param orgId the id of the user's organization.
   * @throws IdNotFoundException if the User's id is not found.
   */
  @SuppressWarnings("PMD.UnusedModifier")
  public void deleteUser(String id, String orgId) throws IdNotFoundException;

  /**
   * Deletes the given Organization.
   * 
   * @param id the unique id of the Organization.
   * @throws IdNotFoundException if the Organization's id is not found.
   */
  @SuppressWarnings("PMD.UnusedModifier")
  public void deleteOrganization(String id) throws IdNotFoundException;

  /**
   * @param id The unique id of the Organization.
   * @return The Organization with the given id.
   * @throws IdNotFoundException if the id does not exist.
   */
  @SuppressWarnings("PMD.UnusedModifier")
  public Organization getOrganization(String id) throws IdNotFoundException;

  /**
   * @return All the defined Organizations in an OrganizationList.
   */
  @SuppressWarnings("PMD.UnusedModifier")
  public OrganizationList getOrganizations();

  /**
   * @param id The unique id of the UserInfo.
   * @param orgId the id of the user's organization.
   * @return The UserInfo with the given id.
   * @throws IdNotFoundException if the id does not exist.
   */
  @SuppressWarnings("PMD.UnusedModifier")
  public UserInfo getUser(String id, String orgId) throws IdNotFoundException;

  /**
   * @param orgId the id of the users' organization.
   * 
   * @return All the defined Users in a UserInfoList.
   */
  @SuppressWarnings("PMD.UnusedModifier")
  public UserInfoList getUsers(String orgId);

  /**
   * @param id The unique id of the UserPassword.
   * @param orgId the organization id.
   * @return The UserPassword with the given id.
   * @throws IdNotFoundException if the id does not exist.
   */
  @SuppressWarnings("PMD.UnusedModifier")
  public UserPassword getUserPassword(String id, String orgId)
      throws IdNotFoundException;

  /**
   * Stores the given user in the WattDepot Server.
   * 
   * @param user The UserInfo to store.
   */
  @SuppressWarnings("PMD.UnusedModifier")
  public void putUser(UserInfo user);

  /**
   * Checks to see if the given id is a defined Organization's id.
   * 
   * @param id the id to check.
   * @return true if there is a defined Organization with the given id, false
   *         otherwise.
   */
  @SuppressWarnings("PMD.UnusedModifier")
  public boolean isDefinedOrganization(String id);

  /**
   * Checks to see if the given id and orgId is a defined UserInfo id.
   * 
   * @param id the id to check.
   * @param orgId the organization id.
   * @return true if there is a defined UserInfo with the given id and orgId, false
   *         otherwise.
   */
  @SuppressWarnings("PMD.UnusedModifier")
  public boolean isDefinedUserInfo(String id, String orgId);

  /**
   * Stores the given Organization in the WattDepot Server.
   * 
   * @param org the Organization to store.
   */
  @SuppressWarnings("PMD.UnusedModifier")
  public void putOrganization(Organization org);

  /**
   * Updates the Organization in the WattDepot Server.
   * 
   * @param org the Organization to update.
   */
  @SuppressWarnings("PMD.UnusedModifier")
  public void updateOrganization(Organization org);

  /**
   * Updates the UserInfo in the WattDepot Server.
   * 
   * @param user the UserInfo to update.
   */
  @SuppressWarnings("PMD.UnusedModifier")
  void updateUser(UserInfo user);

  /**
   * Updates the Userpassword in the WattDepot Server.
   * 
   * @param password the UserPassword to update.
   * @throws IdNotFoundException if the user id or organization ids are not
   *         defined.
   */
  @SuppressWarnings("PMD.UnusedModifier")
  void updateUserPassword(UserPassword password) throws IdNotFoundException;
}
