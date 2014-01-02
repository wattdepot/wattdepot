/**
 * UserInfoImpl.java This file is part of WattDepot.
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
package org.wattdepot.server.depository.impl.hibernate;

import java.util.Set;

import org.wattdepot.common.domainmodel.Property;
import org.wattdepot.common.domainmodel.UserInfo;

/**
 * UserInfoImpl - Hibernate implementation of UserInfo. Supports a Long pk
 * value.
 * 
 * @author Cam Moore
 * 
 */
@SuppressWarnings("PMD.UselessOverridingMethod")
public class UserInfoImpl extends UserInfo {

  /** Database primary key. */
  private Long pk;

  /**
   * Default constructor.
   */
  public UserInfoImpl() {

  }

  /**
   * @param uid
   *          The unique user id.
   * @param firstName
   *          The user's name.
   * @param lastName
   *          The user's last name.
   * @param email
   *          The user's email address.
   * @param orgId
   *          The id of the organization this user belongs to.
   * @param properties
   *          The additional properties for the user.
   */
  public UserInfoImpl(String uid, String firstName, String lastName,
      String email, String orgId, Set<Property> properties) {
    super(uid, firstName, lastName, email, orgId, properties);
  }

  /**
   * Converter constructor.
   * 
   * @param info
   *          UserInfo to clone.
   */
  public UserInfoImpl(UserInfo info) {
    super(info.getUid(), info.getFirstName(), info.getLastName(), info
        .getEmail(), info.getOrganizationId(), info.getProperties());
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.common.domainmodel.UserInfo#addProperty(org.wattdepot.common
   * .domainmodel.Property)
   */
  @Override
  public boolean addProperty(Property e) {
    // TODO Auto-generated method stub
    return super.addProperty(e);
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (this == obj) {
      return true;
    }
    if (obj.getClass().equals(UserInfo.class)) {
      return super.equals(obj);
    }
    if (!super.equals(obj)) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    UserInfoImpl other = (UserInfoImpl) obj;
    if (pk == null) {
      if (other.pk != null) {
        return false;
      }
    }
    else if (!pk.equals(other.pk)) {
      return false;
    }
    return true;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.common.domainmodel.UserInfo#getEmail()
   */
  @Override
  public String getEmail() {
    // TODO Auto-generated method stub
    return super.getEmail();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.common.domainmodel.UserInfo#getFirstName()
   */
  @Override
  public String getFirstName() {
    // TODO Auto-generated method stub
    return super.getFirstName();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.common.domainmodel.UserInfo#getLastName()
   */
  @Override
  public String getLastName() {
    // TODO Auto-generated method stub
    return super.getLastName();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.common.domainmodel.UserInfo#getOrganizationId()
   */
  @Override
  public String getOrganizationId() {
    // TODO Auto-generated method stub
    return super.getOrganizationId();
  }

  /**
   * @return the pk
   */
  public Long getPk() {
    return pk;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.common.domainmodel.UserInfo#getProperties()
   */
  @Override
  public Set<Property> getProperties() {
    // TODO Auto-generated method stub
    return super.getProperties();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.common.domainmodel.UserInfo#getProperty(java.lang.String)
   */
  @Override
  public Property getProperty(String key) {
    // TODO Auto-generated method stub
    return super.getProperty(key);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.common.domainmodel.UserInfo#getUID()
   */
  @Override
  public String getUid() {
    // TODO Auto-generated method stub
    return super.getUid();
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((pk == null) ? 0 : pk.hashCode());
    return result;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.common.domainmodel.UserInfo#removeProperty(java.lang.Object)
   */
  @Override
  public boolean removeProperty(Object o) {
    // TODO Auto-generated method stub
    return super.removeProperty(o);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.common.domainmodel.UserInfo#setEmail(java.lang.String)
   */
  @Override
  public void setEmail(String email) {
    // TODO Auto-generated method stub
    super.setEmail(email);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.common.domainmodel.UserInfo#setFirstName(java.lang.String)
   */
  @Override
  public void setFirstName(String firstName) {
    // TODO Auto-generated method stub
    super.setFirstName(firstName);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.common.domainmodel.UserInfo#setLastName(java.lang.String)
   */
  @Override
  public void setLastName(String lastName) {
    // TODO Auto-generated method stub
    super.setLastName(lastName);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.common.domainmodel.UserInfo#setOrganizationId(java.lang.String
   * )
   */
  @Override
  public void setOrganizationId(String organizationId) {
    // TODO Auto-generated method stub
    super.setOrganizationId(organizationId);
  }

  /**
   * @param pk
   *          the pk to set
   */
  public void setPk(Long pk) {
    this.pk = pk;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.common.domainmodel.UserInfo#setProperties(java.util.Set)
   */
  @Override
  public void setProperties(Set<Property> properties) {
    // TODO Auto-generated method stub
    super.setProperties(properties);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.common.domainmodel.UserInfo#setUID(java.lang.String)
   */
  @Override
  public void setUid(String id) {
    // TODO Auto-generated method stub
    super.setUid(id);
  }

}
