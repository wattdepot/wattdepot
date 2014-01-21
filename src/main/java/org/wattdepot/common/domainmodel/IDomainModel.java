/**
 * IDomainModel.java This file is part of WattDepot.
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

import org.wattdepot.common.exception.BadSlugException;

/**
 * IDomainModel defines common methods for all domain model classes.
 * 
 * @author Cam Moore
 * 
 */
public interface IDomainModel {

  /**
   * @return the name of the instance.
   */
  public String getName();

  /**
   * @return The id of the owner of this instance.
   */
  public String getOrganizationId();

  /**
   * The slug is the name of the instance lower-cased, spaces replaced with '-',
   * and special characters removed.
   * 
   * @return The slug for this instance.
   */
  public String getId();

  /**
   * Sets the name of the instance. If the slug is not set it sets the slug as
   * well.
   * 
   * @param name the name of the instance.
   */
  public void setName(String name);

  /**
   * Sets the id of the owner of this instance.
   * 
   * @param ownerId the id of the owner.
   */
  public void setOrganizationId(String ownerId);

  /**
   * Sets the instance's slug.
   * 
   * @param id the new slug.
   * @throws BadSlugException if the id is not a valid slug.
   */
  public void setId(String id) throws BadSlugException;
}
