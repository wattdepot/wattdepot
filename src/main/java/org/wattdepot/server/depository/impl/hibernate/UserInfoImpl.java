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

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.wattdepot.common.domainmodel.Property;
import org.wattdepot.common.domainmodel.UserInfo;

/**
 * UserInfoImpl - Hibernate implementation of UserInfo. Supports a Long pk
 * value.
 * 
 * @author Cam Moore
 * 
 */
@Entity
@Table(name = "USERS")
public class UserInfoImpl {

  /** Database primary key. */
  @Id
  @GeneratedValue
  private Long pk;
  /** A unique id for the User. */
  private String uid;
  /** The User's first name. */
  private String firstName;
  /** The User's last name. */
  private String lastName;
  /** The User's email address. */
  private String email;
  /** Additional properties of the user. */
  @OneToMany
  private Set<PropertyImpl> properties;
  /** This user's organization. */
  @ManyToOne
  private OrganizationImpl org;

  /**
   * Default constructor.
   */
  public UserInfoImpl() {

  }

  /**
   * @param uid the user's uid.
   * @param firstName the user's first name.
   * @param lastName the user's last name.
   * @param email the user's email address.
   * @param properties additional properties.
   * @param org the user's organization.
   */
  public UserInfoImpl(String uid, String firstName, String lastName, String email,
      Set<PropertyImpl> properties, OrganizationImpl org) {
    this.uid = uid;
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    this.properties = properties;
    this.org = org;
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
   * @return the properties
   */
  @OneToMany
  public Set<PropertyImpl> getProperties() {
    return properties;
  }

  /**
   * @return the uid
   */
  public String getUid() {
    return uid;
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
   * @param properties the properties to set
   */
  public void setProperties(Set<PropertyImpl> properties) {
    this.properties = properties;
  }

  /**
   * @param uid the uid to set
   */
  public void setUid(String uid) {
    this.uid = uid;
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
    result = prime * result + ((email == null) ? 0 : email.hashCode());
    result = prime * result + ((firstName == null) ? 0 : firstName.hashCode());
    result = prime * result + ((lastName == null) ? 0 : lastName.hashCode());
    result = prime * result + ((org == null) ? 0 : org.hashCode());
    result = prime * result + ((pk == null) ? 0 : pk.hashCode());
    result = prime * result + ((properties == null) ? 0 : properties.hashCode());
    result = prime * result + ((uid == null) ? 0 : uid.hashCode());
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
    UserInfoImpl other = (UserInfoImpl) obj;
    if (email == null) {
      if (other.email != null) {
        return false;
      }
    }
    else if (!email.equals(other.email)) {
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
    if (lastName == null) {
      if (other.lastName != null) {
        return false;
      }
    }
    else if (!lastName.equals(other.lastName)) {
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
    if (properties == null) {
      if (other.properties != null) {
        return false;
      }
    }
    else if (!properties.equals(other.properties)) {
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
    return true;
  }

  /**
   * @return a UserInfo equivalent to this.
   */
  public UserInfo toUserInfo() {
    Set<Property> props = new HashSet<Property>();
    for (PropertyImpl i : properties) {
      props.add(new Property(i.getKey(), i.getValue()));
    }
    UserInfo ret = new UserInfo(uid, firstName, lastName, email, org.getId(), props);
    return ret;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "UserInfoImpl [pk=" + pk + ", uid=" + uid + ", firstName=" + firstName + ", lastName="
        + lastName + ", email=" + email + ", properties=" + properties + ", org=" + org + "]";
  }

}
