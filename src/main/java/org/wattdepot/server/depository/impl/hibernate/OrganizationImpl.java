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

import java.util.Set;

import org.wattdepot.common.domainmodel.Organization;

/**
 * OrganizationImpl - Hibernate implementation of an Organization. Has a Long
 * pk.
 * 
 * @author Cam Moore
 * 
 */
@SuppressWarnings("PMD.UselessOverridingMethod")
public class OrganizationImpl extends Organization {

  /** Database primary key. */
  private Long pk;

  /**
   * Default constructor.
   */
  public OrganizationImpl() {
    super();
  }

  /**
   * 
   * @param org
   *          The Organization to clone.
   */
  public OrganizationImpl(Organization org) {
    super(org.getName(), org.getUsers());
  }

  /**
   * @param name
   *          The name of the Organization.
   */
  public OrganizationImpl(String name) {
    super(name);
  }

  /**
   * @param name
   *          The name of the organization.
   * @param users
   *          The Users in the organization.
   */
  public OrganizationImpl(String name, Set<String> users) {
    super(name, users);
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
    if (obj.getClass().equals(Organization.class)) {
      return super.equals(obj);
    }
    if (!super.equals(obj)) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    OrganizationImpl other = (OrganizationImpl) obj;
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
   * @see org.wattdepot.common.domainmodel.Organization#getName()
   */
  @Override
  public String getName() {
    return super.getName();
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
   * @see org.wattdepot.common.domainmodel.Organization#getSlug()
   */
  @Override
  public String getSlug() {
    return super.getSlug();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.common.domainmodel.Organization#getUsers()
   */
  @Override
  public Set<String> getUsers() {
    return super.getUsers();
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
   * org.wattdepot.common.domainmodel.Organization#setName(java.lang.String)
   */
  @Override
  public void setName(String name) {
    super.setName(name);
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
   * org.wattdepot.common.domainmodel.Organization#setSlug(java.lang.String)
   */
  @Override
  public void setSlug(String slug) {
    super.setSlug(slug);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.common.domainmodel.Organization#setUsers(java.util.Set)
   */
  @Override
  public void setUsers(Set<String> users) {
    super.setUsers(users);
  }

}
