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

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.wattdepot.common.domainmodel.UserPassword;
import org.wattdepot.server.StrongAES;

import java.security.InvalidKeyException;

/**
 * UserPasswordImpl - Hibernate implementation of UserPassword. Has a Long pk
 * value.
 * 
 * @author Cam Moore
 * 
 */
@Entity
@Table(name = "PASSWORDS")
public class UserPasswordImpl {

  /** Database primary key. */
  @Id
  @GeneratedValue
  private Long pk;
  /** The user assocated with this password. */
  @OneToOne
  private UserInfoImpl user;
  private String encryptedPassword;
  /** The user's organization. */
  @ManyToOne
  private OrganizationImpl org;

  /**
   * Default constructor.
   */
  public UserPasswordImpl() {

  }

  /**
   * @param user the user.
   * @param plainText their plaintext password.
   * @param org their organization.
   */
  public UserPasswordImpl(UserInfoImpl user, String plainText, OrganizationImpl org) {
    this.user = user;
    this.encryptedPassword = StrongAES.getInstance().encrypt(plainText);
    this.org = org;
  }

  /**
   * @return the encryptedPassword
   */
  public String getEncryptedPassword() {
    return encryptedPassword;
  }

  /**
   * @return the org
   */
  public OrganizationImpl getOrg() {
    return org;
  }

  /**
   * @return the pk
   */
  public Long getPk() {
    return pk;
  }

  /**
   * @return the user
   */
  public UserInfoImpl getUser() {
    return user;
  }

  /**
   * @param encryptedPassword the encryptedPassword to set
   */
  public void setEncryptedPassword(String encryptedPassword) {
    this.encryptedPassword = encryptedPassword;
  }

  /**
   * @param org the org to set
   */
  public void setOrg(OrganizationImpl org) {
    this.org = org;
  }

  /**
   * @param pk the pk to set
   */
  @SuppressWarnings("unused")
  private void setPk(Long pk) {
    this.pk = pk;
  }

  /**
   * @param user the user to set
   */
  public void setUser(UserInfoImpl user) {
    this.user = user;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((encryptedPassword == null) ? 0 : encryptedPassword.hashCode());
    result = prime * result + ((org == null) ? 0 : org.hashCode());
    result = prime * result + ((pk == null) ? 0 : pk.hashCode());
    result = prime * result + ((user == null) ? 0 : user.hashCode());
    return result;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    UserPasswordImpl other = (UserPasswordImpl) obj;
    if (encryptedPassword == null) {
      if (other.encryptedPassword != null) {
        return false;
      }
    }
    else if (!encryptedPassword.equals(other.encryptedPassword)) {
      return false;
    }
    if (org == null) {
      if (other.org != null) {
        return false;
      }
    }
    else if (!org.equals(other.org)) {
      return false;
    }
    if (pk == null) {
      if (other.pk != null) {
        return false;
      }
    }
    else if (!pk.equals(other.pk)) {
      return false;
    }
    if (user == null) {
      if (other.user != null) {
        return false;
      }
    }
    else if (!user.equals(other.user)) {
      return false;
    }
    return true;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "UserPasswordImpl [pk=" + pk + ", user=" + user + ", encryptedPassword="
        + encryptedPassword + ", org=" + org + "]";
  }

  /**
   * @return the equivalent UserPassword.
   */
  public UserPassword toUserPassword() {
    String decrypt = null;
    UserPassword ret = null;
    try {
      decrypt = StrongAES.getInstance().decrypt(encryptedPassword);
      ret = new UserPassword(user.getUid(), org.getId(), decrypt);
    }
    catch (InvalidKeyException e) {
      e.printStackTrace();
    }
    catch (BadPaddingException e) {
      e.printStackTrace();
    }
    catch (IllegalBlockSizeException e) {
      e.printStackTrace();
    }
    return ret;
  }
}
