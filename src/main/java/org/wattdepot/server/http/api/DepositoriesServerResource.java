/**
 * DepositorysServerResource.java This file is part of WattDepot.
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
package org.wattdepot.server.http.api;

import java.util.logging.Level;

import org.restlet.data.Status;
import org.wattdepot.common.domainmodel.Depository;
import org.wattdepot.common.domainmodel.DepositoryList;
import org.wattdepot.common.exception.IdNotFoundException;
import org.wattdepot.common.http.api.DepositoriesResource;

/**
 * DepositorysServerResource - ServerResource that handles Depositories.
 * 
 * @author Cam Moore
 * 
 */
public class DepositoriesServerResource extends WattDepotServerResource implements
    DepositoriesResource {

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.restlet.DepositorysResource#retrieve()
   */
  @Override
  public DepositoryList retrieve() {
    getLogger().log(Level.INFO, "GET /wattdepot/{" + orgId + "}/depositories/");
    DepositoryList list = new DepositoryList();
    try {
      for (Depository d : depot.getDepositories(orgId)) {
        list.getDepositories().add(d);
      }
    }
    catch (IdNotFoundException e) {
      setStatus(Status.CLIENT_ERROR_BAD_REQUEST, orgId
          + " is not a defined Organization id.");
    }
    return list;
  }

}
