/**
 * OrganizationImpl.java This file is part of WattDepot.
 *
 * Copyright (C) 2014  Cam Moore
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

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.wattdepot.common.domainmodel.Organization;

/**
 * OrganizationImpl - Hibernate implementation of an Organization. Has a Long
 * pk.
 * 
 * @author Cam Moore
 * 
 */
@Entity
@Table(name = "ORGANIZATIONS")
public class OrganizationImpl {

  /** Database primary key. */
  @Id
  @GeneratedValue
  private Long pk;
  /** Unique id that is also a slug usable in URLs. */
  private String id;
  private String name;
  @OneToMany(mappedBy = "org", cascade = CascadeType.PERSIST)
  private Set<UserInfoImpl> users;

  /**
   * Default constructor.
   */
  public OrganizationImpl() {
    super();
  }

  /**
   * @param id the id of the organization.
   * @param name the name of the organization.
   * @param users the users in the organization.
   */
  public OrganizationImpl(String id, String name, Set<UserInfoImpl> users) {
    this.id = id;
    this.name = name;
    this.users = users;
  }

  /**
   * @param e the UserInfoImpl to add.
   * @return true if successful.
   * @see java.util.Set#add(java.lang.Object)
   */
  public boolean addUser(UserInfoImpl e) {
    return users.add(e);
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
    OrganizationImpl other = (OrganizationImpl) obj;
    if (id == null) {
      if (other.id != null) {
        return false;
      }
    }
    else if (!id.equals(other.id)) {
      return false;
    }
    if (name == null) {
      if (other.name != null) {
        return false;
      }
    }
    else if (!name.equals(other.name)) {
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
    if (users == null) {
      if (other.users != null) {
        return false;
      }
    }
    else if (!users.equals(other.users)) {
      return false;
    }
    return true;
  }

  /**
   * @return the id
   */
  public String getId() {
    return id;
  }

  /**
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * @return the pk
   */
  public Long getPk() {
    return pk;
  }

  /**
   * @return the users
   */
  @OneToMany
  public Set<UserInfoImpl> getUsers() {
    return users;
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
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((pk == null) ? 0 : pk.hashCode());
    result = prime * result + ((users == null) ? 0 : users.hashCode());
    return result;
  }

  /**
   * @param ui the UserInfoImpl to remove from the Organization.
   * @return true if successful.
   * @see java.util.Set#remove(java.lang.Object)
   */
  public boolean removeUser(UserInfoImpl ui) {
    return users.remove(ui);
  }

  /**
   * @param id the id to set
   */
  public void setId(String id) {
    this.id = id;
  }

  /**
   * @param name the name to set
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * @param pk the pk to set
   */
  @SuppressWarnings("unused")
  private void setPk(Long pk) {
    this.pk = pk;
  }

  /**
   * @param users the users to set
   */
  public void setUsers(Set<UserInfoImpl> users) {
    this.users = users;
  }

  /**
   * @return The Organization equivalent to this.
   */
  public Organization toOrganization() {
    Set<String> u = new HashSet<String>();
    for (UserInfoImpl i : users) {
      u.add(i.getUid());
    }
    Organization ret = new Organization(id, name, u);
    return ret;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "OrganizationImpl [pk=" + pk + ", id=" + id + ", name=" + name + ", users=" + users
        + "]";
  }

}
