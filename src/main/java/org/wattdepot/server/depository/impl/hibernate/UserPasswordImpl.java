/**
 * UserPasswordImpl.java This file is part of WattDepot.
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

import org.wattdepot.common.domainmodel.UserPassword;

/**
 * UserPasswordImpl - Hibernate implementation of UserPassword. Has a Long pk
 * value.
 * 
 * @author Cam Moore
 * 
 */
@SuppressWarnings("PMD.UselessOverridingMethod")
public class UserPasswordImpl extends UserPassword {

  /** Database primary key. */
  private Long pk;

  /**
   * Default constructor.
   */
  public UserPasswordImpl() {

  }

  /**
   * @param id
   *          The unique user id.
   * @param orgId
   *          The user's organization id.
   * @param plainTextPassword
   *          the user's plain text password.
   */
  public UserPasswordImpl(String id, String orgId, String plainTextPassword) {
    super(id, orgId, plainTextPassword);
  }

  /**
   * Converter constructor.
   * 
   * @param password
   *          The UserPassword to clone.
   */
  public UserPasswordImpl(UserPassword password) {
    super(password.getUid(), password.getOrganizationId(), password
        .getPlainText());
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
    if (obj.getClass().equals(UserPassword.class)) {
      return super.equals(obj);
    }
    if (!super.equals(obj)) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    UserPasswordImpl other = (UserPasswordImpl) obj;
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
   * @see org.wattdepot.common.domainmodel.UserPassword#getEncryptedPassword()
   */
  @Override
  public String getEncryptedPassword() {
    // TODO Auto-generated method stub
    return super.getEncryptedPassword();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.common.domainmodel.UserPassword#getId()
   */
  @Override
  public String getUid() {
    // TODO Auto-generated method stub
    return super.getUid();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.common.domainmodel.UserPassword#getOrganizationId()
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
   * @see org.wattdepot.common.domainmodel.UserPassword#getPlainText()
   */
  @Override
  public String getPlainText() {
    // TODO Auto-generated method stub
    return super.getPlainText();
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
   * org.wattdepot.common.domainmodel.UserPassword#setEncryptedPassword(java
   * .lang.String)
   */
  @Override
  public void setEncryptedPassword(String encryptedPassword) {
    // TODO Auto-generated method stub
    super.setEncryptedPassword(encryptedPassword);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.common.domainmodel.UserPassword#setId(java.lang.String)
   */
  @Override
  public void setUid(String id) {
    // TODO Auto-generated method stub
    super.setUid(id);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.common.domainmodel.UserPassword#setOrganizationId(java.lang
   * .String)
   */
  @Override
  public void setOrganizationId(String orgId) {
    // TODO Auto-generated method stub
    super.setOrganizationId(orgId);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.common.domainmodel.UserPassword#setPassword(java.lang.String)
   */
  @Override
  public void setPassword(String plainText) {
    // TODO Auto-generated method stub
    super.setPassword(plainText);
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
   * @see
   * org.wattdepot.common.domainmodel.UserPassword#setPlainText(java.lang.String
   * )
   */
  @Override
  public void setPlainText(String plainText) {
    // TODO Auto-generated method stub
    super.setPlainText(plainText);
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "UserPasswordImpl [pk=" + pk + ", id=" + getUid() + ", orgId="
        + getOrganizationId() + ", encryptedPassword=" + getEncryptedPassword()
        + "]";
  }

}
