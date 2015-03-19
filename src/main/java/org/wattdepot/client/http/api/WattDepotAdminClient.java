/**
 * WattDepotAdminClient.java This file is part of WattDepot.
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
package org.wattdepot.client.http.api;

import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;
import org.wattdepot.client.WattDepotAdminInterface;
import org.wattdepot.common.domainmodel.Labels;
import org.wattdepot.common.domainmodel.Organization;
import org.wattdepot.common.domainmodel.OrganizationList;
import org.wattdepot.common.domainmodel.UserInfo;
import org.wattdepot.common.domainmodel.UserInfoList;
import org.wattdepot.common.domainmodel.UserPassword;
import org.wattdepot.common.exception.BadCredentialException;
import org.wattdepot.common.exception.IdNotFoundException;
import org.wattdepot.common.http.api.OrganizationPutResource;
import org.wattdepot.common.http.api.OrganizationResource;
import org.wattdepot.common.http.api.OrganizationsResource;
import org.wattdepot.common.http.api.UserInfoPutResource;
import org.wattdepot.common.http.api.UserInfoResource;
import org.wattdepot.common.http.api.UserInfosResource;
import org.wattdepot.common.http.api.UserPasswordResource;

/**
 * WattDepotAdminClient - Admin level client.
 * 
 * @author Cam Moore
 * 
 */
public class WattDepotAdminClient extends WattDepotClient implements WattDepotAdminInterface {

  /**
   * Creates a new WattDepotAdminClient.
   * 
   * @param serverUri
   *          The URI of the WattDepot server (e.g.
   *          "http://server.wattdepot.org/")
   * @param username
   *          The name of the user. The user must be defined in the WattDepot
   *          server.
   * @param orgId
   *          the id of the organization the user is in.
   * @param password
   *          The password for the user.
   * @throws BadCredentialException
   *           If the user or password don't match the credentials on the
   *           WattDepot server.
   */
  public WattDepotAdminClient(String serverUri, String username, String orgId, String password)
      throws BadCredentialException {
    super(serverUri, username, orgId, password);
    if (!getOrganizationId().equals("admin")) {
      throw new BadCredentialException("Wrong group.");
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.client.WattDepotAdminInterface#deleteOrganization(java.lang
   * .String)
   */
  @Override
  public void deleteOrganization(String id) throws IdNotFoundException {
    ClientResource client = makeClient("admin/" + Labels.ORGANIZATION + "/" + id);
    OrganizationResource resource = client.wrap(OrganizationResource.class);
    try {
      resource.remove();
    }
    catch (ResourceException re) {
      throw new IdNotFoundException(id + " is not a defined Organization.");
    }
    finally {
      client.release();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.client.WattDepotAdminInterface#deleteUser(java.lang.String)
   */
  @Override
  public void deleteUser(String id, String orgId) throws IdNotFoundException {
    ClientResource client = makeClient(orgId + "/" + Labels.USER + "/" + id);
    UserInfoResource resource = client.wrap(UserInfoResource.class);
    try {
      resource.remove();
    }
    catch (ResourceException re) {
      throw new IdNotFoundException(id + " is not a defined UserInfo.");
    }
    finally {
      client.release();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.client.WattDepotAdminInterface#putOrganization(org.wattdepot
   * .common.domainmodel.Organization)
   */
  @Override
  public void putOrganization(Organization org) {
    ClientResource client = makeClient("admin/" + Labels.ORGANIZATION + "/");
    OrganizationPutResource resource = client.wrap(OrganizationPutResource.class);
    try {
      resource.store(org);
    }
    finally {
      client.release();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.client.WattDepotAdminInterface#putUser(org.wattdepot.datamodel
   * .UserInfo)
   */
  @Override
  public void putUser(UserInfo user) {
    ClientResource client = makeClient(user.getOrganizationId() + "/" + Labels.USER + "/");
    UserInfoPutResource resource = client.wrap(UserInfoPutResource.class);
    try {
      resource.store(user);
    }
    finally {
      client.release();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.client.WattDepotAdminInterface#updateOrganization(org.wattdepot
   * .common.domainmodel.Organization)
   */
  @Override
  public void updateOrganization(Organization org) {
    ClientResource client = makeClient("admin/" + Labels.ORGANIZATION + "/" + org.getId());
    OrganizationResource resource = client.wrap(OrganizationResource.class);
    try {
      resource.update(org);
    }
    finally {
      client.release();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.client.WattDepotAdminInterface#updateUser(org.wattdepot
   * .common.domainmodel.UserInfo)
   */
  @Override
  public void updateUser(UserInfo user) {
    ClientResource client = makeClient("admin/" + Labels.USER + "/" + user.getUid());
    UserInfoResource resource = client.wrap(UserInfoResource.class);
    try {
      resource.update(user);
    }
    finally {
      client.release();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.client.WattDepotAdminInterface#getOrganization(java.lang.
   * String)
   */
  @Override
  public Organization getOrganization(String id) throws IdNotFoundException {
    ClientResource client = makeClient("admin/" + Labels.ORGANIZATION + "/" + id);
    OrganizationResource resource = client.wrap(OrganizationResource.class);
    Organization ret = null;
    try {
      ret = resource.retrieve();
    }
    catch (ResourceException re) {
      throw new IdNotFoundException(id + " is not a defined Organization.");
    }
    finally {
      client.release();
    }
    return ret;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.client.WattDepotAdminInterface#getOrganizations()
   */
  @Override
  public OrganizationList getOrganizations() {
    ClientResource client = makeClient("admin/" + Labels.ORGANIZATIONS + "/");
    OrganizationsResource resource = client.wrap(OrganizationsResource.class);
    OrganizationList ret = null;
    try {
      ret = resource.retrieve();
    }
    finally {
      client.release();
    }
    return ret;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.client.WattDepotAdminInterface#getUser(java.lang.String)
   */
  @Override
  public UserInfo getUser(String id, String orgId) throws IdNotFoundException {
    ClientResource client = makeClient(orgId + "/" + Labels.USER + "/" + id);
    UserInfoResource resource = client.wrap(UserInfoResource.class);
    UserInfo ret = null;
    try {
      ret = resource.retrieve();
    }
    catch (ResourceException re) {
      throw new IdNotFoundException(id + " is not a defined UserInfo.");
    }
    finally {
      client.release();
    }
    return ret;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.client.WattDepotAdminInterface#getUsers()
   */
  @Override
  public UserInfoList getUsers(String orgId) {
    ClientResource client = makeClient(orgId + "/" + Labels.USERS + "/");
    UserInfosResource resource = client.wrap(UserInfosResource.class);
    UserInfoList ret = null;
    try {
      ret = resource.retrieve();
    }
    finally {
      client.release();
    }
    return ret;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.client.WattDepotAdminInterface#getUserPassword(java.lang.
   * String)
   */
  @Override
  public UserPassword getUserPassword(String id, String orgId) throws IdNotFoundException {
    ClientResource client = makeClient(orgId + "/" + Labels.USER_PASSWORD + "/" + id);
    UserPasswordResource resource = client.wrap(UserPasswordResource.class);
    UserPassword ret = null;
    try {
      ret = resource.retrieve();
    }
    catch (ResourceException re) {
      throw new IdNotFoundException(id + " is not a defined UserPassword.");
    }
    finally {
      client.release();
    }
    return ret;
  }

  /* (non-Javadoc)
   * @see org.wattdepot.client.WattDepotAdminInterface#updateUserPassword(org.wattdepot.common.domainmodel.UserPassword)
   */
  @Override
  public void updateUserPassword(UserPassword password) throws IdNotFoundException {
    ClientResource client = makeClient(password.getOrganizationId() + "/" + Labels.USER_PASSWORD + "/" + password.getUid());
    UserPasswordResource resource = client.wrap(UserPasswordResource.class);
    try {
      resource.update(password);
    }
    catch (ResourceException re) {
      throw new IdNotFoundException(password.getUid() + " is not a defined UserPassword.");
    }
    finally {
      client.release();
    }
  }

  /* (non-Javadoc)
   * @see org.wattdepot.client.WattDepotAdminInterface#isDefinedOrganization(java.lang.String)
   */
  @Override
  public boolean isDefinedOrganization(String id) {
    try {
      getOrganization(id);
      return true;
    }
    catch (IdNotFoundException e) {
      return false;
    }
  }

  /* (non-Javadoc)
   * @see org.wattdepot.client.WattDepotAdminInterface#isDefinedUserInfo(java.lang.String, java.lang.String)
   */
  @Override
  public boolean isDefinedUserInfo(String id, String orgId) {
    try {
      getUser(id, orgId);
      return true;
    }
    catch (IdNotFoundException e) {
      return false;
    }
  }

}
