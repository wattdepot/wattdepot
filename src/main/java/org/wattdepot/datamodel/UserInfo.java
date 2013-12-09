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
package org.wattdepot.datamodel;

import java.util.HashSet;
import java.util.Set;

/**
 * User - represents a user of WattDepot3.
 * 
 * @author Cam Moore
 * 
 */
public class UserInfo {
  /** Name of property used to store the admin username. */
  public static final String ADMIN_USER_NAME = "wattdepot3-server.admin.name";
  
  /** The admin user. */
  public static final UserInfo ADMIN = new UserInfo("admin", "admin", null, null, true,
      new HashSet<Property>());

  /** A unique id for the User. */
  private String id;
  /** The User's first name. */
  private String firstName;
  /** The User's last name. */
  private String lastName;
  /** The User's email address. */
  private String email;
  /** True if the user is an admin. */
  private Boolean admin;
  /** Additional properties of the user. */
  private Set<Property> properties;

  static {
    String adminName = System.getenv(ADMIN_USER_NAME);
    if (adminName != null) {
      ADMIN.setId(adminName);
    }
  }

  /**
   * The default constructor.
   */
  public UserInfo() {

  }

  /**
   * Creates a new UserInfo with the given information.
   * 
   * @param id
   *          The unique id.
   * @param firstName
   *          The user's name.
   * @param lastName
   *          The user's last name.
   * @param email
   *          The user's email address.
   * @param admin
   *          True if they are an admin.
   * @param properties
   *          The additional properties for the user.
   */
  public UserInfo(String id, String firstName, String lastName, String email, 
      Boolean admin, Set<Property> properties) {
    this.id = id;
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    this.admin = admin;
    this.properties = properties;
  }

  /**
   * @param e
   *          The Property to add.
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
    if (getClass() != obj.getClass()) {
      return false;
    }
    UserInfo other = (UserInfo) obj;
    if (admin == null) {
      if (other.admin != null) {
        return false;
      }
    }
    else if (!admin.equals(other.admin)) {
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
   * @return the admin
   */
  public Boolean getAdmin() {
    return admin;
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
   * @return the unique id.
   */
  public String getId() {
    return id;
  }

  /**
   * @return the lastName
   */
  public String getLastName() {
    return lastName;
  }

  /**
   * @return the properties
   */
  public Set<Property> getProperties() {
    return properties;
  }

  /**
   * @param key
   *          The key.
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

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((admin == null) ? 0 : admin.hashCode());
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    result = prime * result + ((firstName == null) ? 0 : firstName.hashCode());
    result = prime * result + ((properties == null) ? 0 : properties.hashCode());
    return result;
  }

  /**
   * @param o
   *          The Property to remove.
   * @return true if removed.
   * @see java.util.List#remove(java.lang.Object)
   */
  public boolean removeProperty(Object o) {
    return properties.remove(o);
  }

  /**
   * @param admin
   *          the admin to set
   */
  public void setAdmin(Boolean admin) {
    this.admin = admin;
  }

  /**
   * @param email
   *          the email to set
   */
  public void setEmail(String email) {
    this.email = email;
  }

  /**
   * @param firstName
   *          the firstName to set
   */
  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  /**
   * @param id
   *          the id to set
   */
  public void setId(String id) {
    this.id = id;
  }

  /**
   * @param lastName
   *          the lastName to set
   */
  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  /**
   * @param properties
   *          the properties to set
   */
  public void setProperties(Set<Property> properties) {
    this.properties = properties;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "User {\"id\"=\"" + id + "\", \"firstname\"=\"" + firstName + "\", \"lastname\"=\"" + lastName
        + "\", \"email\"=\"" + email + "\", \"admin\"=" + admin
        + ", \"properties\"=" + properties + "}";
  }

}
