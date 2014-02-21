/**
 * UserPassword.java This file is part of WattDepot.
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
package org.wattdepot.common.domainmodel;

import org.wattdepot.server.ServerProperties;
import org.wattdepot.server.StrongAES;

/**
 * UserPassword - UserId and encrypted password pair.
 * 
 * @author Cam Moore
 * 
 */
public class UserPassword {
  /** Name of property used to store the admin password. */
  public static final String ADMIN_USER_PASSWORD = "wattdepot-server.admin.password";

  /** The password for the admin user. */
  public static final UserPassword ROOT = new UserPassword(UserInfo.ROOT.getUid(),
      UserInfo.ROOT.getOrganizationId(), "admin");
  private String uid;
  private String encryptedPassword;
  private String orgId;

  static {
    String password = System.getenv().get(ServerProperties.ADMIN_USER_PASSWORD);
    if (password != null) {
      ROOT.setPassword(password);
    }
  }

  /**
   * Default constructor.
   */
  public UserPassword() {

  }

  /**
   * Creates a new UserPassword object, encrypting the plainTextPassword and
   * storing the hash.
   * 
   * @param id
   *          The user's id.
   * @param orgId
   *          the user's organization id.
   * @param plainTextPassword
   *          The plain text password.
   */
  public UserPassword(String id, String orgId, String plainTextPassword) {
    this.uid = id;
    this.encryptedPassword = StrongAES.getInstance().encrypt(plainTextPassword);
    this.orgId = orgId;
  }

  /**
   * Checks the given password.
   * 
   * @param inputPassword
   *          The password to check.
   * @return True if the password is correct.
   */
  public boolean checkPassword(String inputPassword) {
    return StrongAES.getInstance().encrypt(inputPassword).equals(encryptedPassword);
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
    if (!getClass().isAssignableFrom(obj.getClass())
        && !obj.getClass().isAssignableFrom(getClass()) && getClass() != obj.getClass()) {
      return false;
    }
    UserPassword other = (UserPassword) obj;
    if (encryptedPassword == null) {
      if (other.encryptedPassword != null) {
        return false;
      }
    }
    else if (!encryptedPassword.equals(other.encryptedPassword)) {
      return false;
    }
    if (uid == null) {
      if (other.uid != null) {
        return false;
      }
    }
    else if (!uid.equals(other.uid)) {
      return false;
    }
    if (orgId == null) {
      if (other.orgId != null) {
        return false;
      }
    }
    else if (!orgId.equals(other.orgId)) {
      return false;
    }
    return true;
  }

  /**
   * @return the encryptedPassword
   */
  public String getEncryptedPassword() {
    return encryptedPassword;
  }

  /**
   * @return the id
   */
  public String getUid() {
    return uid;
  }

  /**
   * @return the organization id.
   */
  public String getOrganizationId() {
    return orgId;
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
    result = prime * result + ((uid == null) ? 0 : uid.hashCode());
    result = prime * result + ((orgId == null) ? 0 : orgId.hashCode());
    return result;
  }

  /**
   * @param encryptedPassword
   *          the encryptedPassword to set
   */
  public void setEncryptedPassword(String encryptedPassword) {
    this.encryptedPassword = encryptedPassword;
  }

  /**
   * @param id
   *          the id to set
   */
  public void setUid(String id) {
    this.uid = id;
  }

  /**
   * @param orgId
   *          the new organization id to set.
   */
  public void setOrganizationId(String orgId) {
    this.orgId = orgId;
  }

  /**
   * Sets the encrypted password by encrypting the plain text.
   * 
   * @param plainText
   *          the plain text password.
   */
  public void setPassword(String plainText) {
    this.encryptedPassword = StrongAES.getInstance().encrypt(plainText);
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "UserPassword [id=" + uid + ", orgId=" + orgId + ", encryptedPassword=" + encryptedPassword + "]";
  }
}
