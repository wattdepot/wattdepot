/**
 * GvizResource.java This file is part of WattDepot.
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
package org.wattdepot.common.http.api;

import org.restlet.resource.Get;

/**
 * GvizResource - HTTP Interface for getting the Google Visualization Data Source.
 * 
 * @author Yongwen Xu
 * 
 */
public interface GvizDepositoryValueResource {

  /**
   * Defines GET /wattdepot/{group_id}/gviz/ API call.
   * 
   * @return Google Visualization data source.
   */
  @Get("json")
  // Use JSON as transport encoding.
  public String retrieve();
}
