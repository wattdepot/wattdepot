/**
 * Organization.java This file is part of WattDepot.
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

import org.wattdepot.common.util.Slug;

/**
 * Organization - A group of users used to create a namespace for WattDepot
 * entities. Users, sensors, sensor groups, collectors, depositories, and
 * measurements all belong to a single "organization".
 * 
 * @author Cam Moore
 * 
 */
public class Organization {
  /** The name of the admin group. */
  public static final String ADMIN_GROUP_NAME = "admin";
  /** The admin user group. */
  public static final Organization ADMIN_GROUP = new Organization(ADMIN_GROUP_NAME);

  /** The public user group. All groups can see publicly owned objects. */
  public static final Organization PUBLIC_GROUP = new Organization(Labels.PUBLIC);

  /** Unique id that is also a slug usable in URLs. */
  protected String id;
  /** The name of the group. */
  protected String name;
  /** The ids of the users in this group. */
  protected Set<String> users;

  static {
    ADMIN_GROUP.add(UserInfo.ROOT.getUid());
  }

  /**
   * The default constructor.
   */
  public Organization() {
    users = new HashSet<String>();
  }

  /**
   * @param name
   *          The name of the Organization.
   */
  public Organization(String name) {
    this(Slug.slugify(name), name, new HashSet<String>());
  }

  /**
   * @param name
   *          The name of the organization.
   * @param users
   *          The Users in the organization.
   */
  public Organization(String name, Set<String> users) {
    this(Slug.slugify(name), name, users);
  }

  /**
   * @param slug
   *          The unique slug.
   * @param name
   *          The name of the organization.
   * @param users
   *          The Users in the organization.
   */
  public Organization(String slug, String name, Set<String> users) {
    this.id = slug;
    this.name = name;
    this.users = users;
  }

  /**
   * @param userId
   *          The id of the User to add.
   * @return true if successful.
   * @see java.util.List#add(java.lang.Object)
   */
  public boolean add(String userId) {
    return users.add(userId);
  }

  /**
   * @param o
   *          The user to test.
   * @return true if the user is in the group.
   * @see java.util.List#contains(java.lang.Object)
   */
  public boolean contains(Object o) {
    return users.contains(o);
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
    Organization other = (Organization) obj;
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
   * @return The unique id for the Organization.
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
   * @return the users.
   */
  public Set<String> getUsers() {
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
    result = prime * result + ((users == null) ? 0 : users.hashCode());
    return result;
  }

  /**
   * @param o
   *          The User to remove.
   * @return true if successful.
   * @see java.util.List#remove(java.lang.Object)
   */
  public boolean remove(Object o) {
    return users.remove(o);
  }

  /**
   * @param id
   *          the id to set
   */
  public void setId(String id) {
    this.id = id;
  }

  /**
   * @param name
   *          the name to set
   */
  public void setName(String name) {
    this.name = name;
    if (this.id == null) {
      this.id = Slug.slugify(name);
    }
  }

  /**
   * @param users
   *          the users to set
   */
  public void setUsers(Set<String> users) {
    this.users = users;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "Organization [id=" + id + ", name=" + name + ", users=" + users + "]";
  }

}
