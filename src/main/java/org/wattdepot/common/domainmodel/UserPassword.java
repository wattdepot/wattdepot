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

import org.jasypt.util.password.StrongPasswordEncryptor;

/**
 * UserPassword - UserId and encrypted password pair.
 * 
 * @author Cam Moore
 * 
 */
public class UserPassword {
  /** Name of property used to store the admin password. */
  public static final String ADMIN_USER_PASSWORD = "wattdepot-server.admin.password";

  /** The encryptor to use for all passwords. */
  private static final StrongPasswordEncryptor passwordEncryptor = new StrongPasswordEncryptor();

  /** The password for the admin user. */
  public static final UserPassword ADMIN = new UserPassword(UserInfo.ROOT.getId(),
      UserInfo.ROOT.getOrganizationId(), "admin");
  private String id;
  private String encryptedPassword;
  private String plainText;
  private String orgId;

  static {
    String password = System.getProperty(ADMIN_USER_PASSWORD);
    if (password != null) {
      ADMIN.setPassword(password);
      ADMIN.setPlainText(password);
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
    this.id = id;
    this.plainText = plainTextPassword;
    this.encryptedPassword = passwordEncryptor.encryptPassword(plainTextPassword);
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
    return passwordEncryptor.checkPassword(inputPassword, encryptedPassword);
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
    UserPassword other = (UserPassword) obj;
    if (encryptedPassword == null) {
      if (other.encryptedPassword != null) {
        return false;
      }
    }
    else if (!plainText.equals(other.plainText)) {
      return false;
    }
    if (id == null) {
      if (other.id != null) {
        return false;
      }
    }
    else if (!id.equals(other.id)) {
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
  public String getId() {
    return id;
  }

  /**
   * @return the organization id.
   */
  public String getOrganizationId() {
    return orgId;
  }

  /**
   * @return the plainText
   */
  public String getPlainText() {
    return plainText;
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
    result = prime * result + ((plainText == null) ? 0 : plainText.hashCode());
    result = prime * result + ((id == null) ? 0 : id.hashCode());
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
  public void setId(String id) {
    this.id = id;
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
    this.encryptedPassword = passwordEncryptor.encryptPassword(plainText);
  }

  /**
   * @param plainText
   *          the plainText to set
   */
  public void setPlainText(String plainText) {
    this.plainText = plainText;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "UserPassword [id=" + id + ", encryptedPassword=" + encryptedPassword + "]";
  }
}
