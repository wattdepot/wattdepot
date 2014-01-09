/**
 * MeasuredValueList.java This file is part of WattDepot.
 *
 * Copyright (C) 2013  Yongwen Xu
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

import java.util.ArrayList;

/**
 * OrganizationList - a list of organizations.
 * 
 * @author Cam Moore
 * 
 */
public class OrganizationList {
  private ArrayList<Organization> organizations;

  /**
   * Default Constructor.
   */
  public OrganizationList() {
    organizations = new ArrayList<Organization>();
  }

  /**
   * @return the organizations
   */
  public ArrayList<Organization> getOrganizations() {
    return organizations;
  }

  /**
   * @param organizations the organizations to set
   */
  public void setOrganizations(ArrayList<Organization> organizations) {
    this.organizations = organizations;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "OrganizationList [organizations=" + organizations + "]";
  }

}
