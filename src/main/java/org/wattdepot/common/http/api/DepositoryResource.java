/**
 * DepositoryResource.java This file is part of WattDepot.
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
import org.wattdepot.common.domainmodel.Depository;

/**
 * DepositoryResource - HTTP Interface for data model Depository.
 * 
 * @author Cam Moore
 * 
 */
@SuppressWarnings("PMD.UnusedModifier")
public interface DepositoryResource {
  /**
   * Defines GET /wattdepot/{org-id}/depository/{depository-id} API call.
   * 
   * @return The Depository with the given id.
   */
  @Get("json")
  // Use JSON as transport encoding.
  public Depository retrieve();

  /**
   * Defines the POST /wattdepot/{org-id}/depository/{depository-id} API call.
   * 
   * @param depository
   *          The Depository to store.
   */
  @Post("json")
  public void update(Depository depository);

  /**
   * Defined the DEL /wattdepot/{org-id}/depository/{depository-id} API call.
   */
  @Delete
  public void remove();

}
