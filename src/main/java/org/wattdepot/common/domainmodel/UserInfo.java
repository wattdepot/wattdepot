/**
 * User.java This file is part of WattDepot.
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

import java.util.HashSet;
import java.util.Set;

/**
 * User - represents a user of WattDepot.
 * 
 * @author Cam Moore
 * 
 */
public class UserInfo {
  /** Name of property used to store the admin username. */
  public static final String ADMIN_USER_NAME = "wattdepot-server.admin.name";

  /** The admin user. */
  public static final UserInfo ROOT = new UserInfo("root", null, null, null,
      Organization.ADMIN_GROUP_NAME, new HashSet<Property>(), null);

  /** A unique id for the User. */
  private String uid;
  /** The User's first name. */
  private String firstName;
  /** The User's last name. */
  private String lastName;
  /** The User's email address. */
  private String email;
  /** Additional properties of the user. */
  private Set<Property> properties;
  /** orgId the organization id this user belongs to. */
  private String organizationId;
  /** The user's password. */
  private String password;


  /**
   * The default constructor.
   */
  public UserInfo() {

  }

  /**
   * Creates a new UserInfo with the given information.
   * 
   * @param id The unique id.
   * @param firstName The user's name.
   * @param lastName The user's last name.
   * @param email The user's email address.
   * @param orgId The id of the organization this user belongs to.
   * @param properties The additional properties for the user.
   * @param password the user's password.
   */
  public UserInfo(String id, String firstName, String lastName, String email, String orgId,
      Set<Property> properties, String password) {
    this.uid = id;
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    this.organizationId = orgId;
    this.properties = properties;
    this.password = password;
  }

  /**
   * @param e The Property to add.
   * @return true if added.
   * @see java.util.List#add(java.lang.Object)
   */
  public boolean addProperty(Property e) {
    return properties.add(e);
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
    UserInfo other = (UserInfo) obj;
    if (uid == null) {
      if (other.uid != null) {
        return false;
      }
    }
    else if (!uid.equals(other.uid)) {
      return false;
    }
    if (firstName == null) {
      if (other.firstName != null) {
        return false;
      }
    }
    else if (!firstName.equals(other.firstName)) {
      return false;
    }
    if (properties == null) {
      if (other.properties != null) {
        return false;
      }
    }
    else if (!properties.equals(other.properties)) {
      return false;
    }
    return true;
  }

  /**
   * @return the email
   */
  public String getEmail() {
    return email;
  }

  /**
   * @return the firstName
   */
  public String getFirstName() {
    return firstName;
  }

  /**
   * @return the lastName
   */
  public String getLastName() {
    return lastName;
  }

  /**
   * @return the organizationId
   */
  public String getOrganizationId() {
    return organizationId;
  }

  /**
   * @return the password
   */
  public String getPassword() {
    return password;
  }

  /**
   * @return the properties
   */
  public Set<Property> getProperties() {
    return properties;
  }

  /**
   * @param key The key.
   * @return The value of associated with the key.
   */
  public Property getProperty(String key) {
    for (Property p : properties) {
      if (p.getKey().equals(key)) {
        return p;
      }
    }
    return null;
  }

  /**
   * @return the unique id.
   */
  public String getUid() {
    return uid;
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
    result = prime * result + ((uid == null) ? 0 : uid.hashCode());
    result = prime * result + ((firstName == null) ? 0 : firstName.hashCode());
    result = prime * result + ((properties == null) ? 0 : properties.hashCode());
    return result;
  }

  /**
   * @param o The Property to remove.
   * @return true if removed.
   * @see java.util.List#remove(java.lang.Object)
   */
  public boolean removeProperty(Object o) {
    return properties.remove(o);
  }

  /**
   * @param email the email to set
   */
  public void setEmail(String email) {
    this.email = email;
  }

  /**
   * @param firstName the firstName to set
   */
  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  /**
   * @param lastName the lastName to set
   */
  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  /**
   * @param organizationId the organizationId to set
   */
  public void setOrganizationId(String organizationId) {
    this.organizationId = organizationId;
  }

  /**
   * @param password the password to set
   */
  public void setPassword(String password) {
    this.password = password;
  }

  /**
   * @param properties the properties to set
   */
  public void setProperties(Set<Property> properties) {
    this.properties = properties;
  }

  /**
   * @param id the id to set
   */
  public void setUid(String id) {
    this.uid = id;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "User [uid=" + uid + ", firstname=" + firstName + ", lastname=" + lastName + ", email="
        + email + ", properties=" + properties + "]";
  }

}
