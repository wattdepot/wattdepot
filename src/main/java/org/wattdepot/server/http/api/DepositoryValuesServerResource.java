/**
 * DepositoryValuesServerResource.java This file is part of WattDepot.
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
package org.wattdepot.server.http.api;

import org.wattdepot.common.domainmodel.MeasuredValueList;
import org.wattdepot.common.http.api.DepositoryValuesResource;


/**
 * DepositoryValuesServerResource - ServerResouce that handles the GET
 * /wattdepot/{org-id}/depository/{depository-id}/values/ response.
 * 
 * @author Yongwen Xu
 * 
 */
public class DepositoryValuesServerResource extends DepositoryValuesServer implements
    DepositoryValuesResource {
  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.restlet.DepositoryValueResource#retrieve()
   */
  @Override
  public MeasuredValueList retrieve() {
    return doRetrieve();
  }
}
