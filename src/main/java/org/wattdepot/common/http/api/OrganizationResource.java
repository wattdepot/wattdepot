/**
 * OrganizationResource.java This file is part of WattDepot.
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
package org.wattdepot.common.http.api;

import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.wattdepot.common.domainmodel.Organization;

/**
 * OrganizationResource - HTTP Interface for data model organization.
 * 
 * @author Cam Moore
 * 
 */
@SuppressWarnings("PMD.UnusedModifier")
public interface OrganizationResource {

  /**
   * Defines GET /wattdepot/{org-id}/organization/{org-id} API call.
   * 
   * @return The organization with the given id. The id is sent in the request.
   */
  @Get("json")
  // Use JSON as transport encoding.
  public Organization retrieve();

  /**
   * Defined the POST /wattdepot/{org-id}/organization/{org-id} API call. The id
   * is sent in the request.
   * 
   * @param organization
   *          The organization to update.
   */
  @Post
  public void update(Organization organization);

  /**
   * Defined the DEL /wattdepot/{org-id}/organization/{org-id} API call. The id
   * is sent in the request.
   */
  @Delete
  public void remove();

}
