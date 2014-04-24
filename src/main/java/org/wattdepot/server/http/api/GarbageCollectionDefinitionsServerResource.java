/**
 * GarbageCollectionDefinitionsServerResource.java This file is part of WattDepot.
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
package org.wattdepot.server.http.api;

import java.util.logging.Level;

import org.restlet.data.Status;
import org.wattdepot.common.domainmodel.GarbageCollectionDefinition;
import org.wattdepot.common.domainmodel.GarbageCollectionDefinitionList;
import org.wattdepot.common.exception.IdNotFoundException;
import org.wattdepot.common.http.api.GarbageCollectionDefinitionsResource;

/**
 * GarbageCollectionDefinitionsServerResource - Handles the
 * GarbageCollectionDefinition HTTP API
 * ("/wattdepot/{org-id}/garbage-collection-definitions/").
 * 
 * @author Cam Moore
 * 
 */
public class GarbageCollectionDefinitionsServerResource extends WattDepotServerResource implements
    GarbageCollectionDefinitionsResource {

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.common.http.api.GarbageCollectionDefinitionsResource#retrieve
   * ()
   */
  @Override
  public GarbageCollectionDefinitionList retrieve() {
    getLogger().log(Level.INFO, "GET /wattdepot/{" + orgId + "}/garbage-collection-definitions/");
    if (isInRole(orgId)) {
      GarbageCollectionDefinitionList ret = new GarbageCollectionDefinitionList();
      try {
        for (GarbageCollectionDefinition gcd : depot.getGarbageCollectionDefinitions(orgId, true)) {
          ret.add(gcd);
        }
      }
      catch (IdNotFoundException e) {
        setStatus(Status.CLIENT_ERROR_BAD_REQUEST, orgId + " is not a defined Organization id.");
      }
      return ret;
    }
    else {
      setStatus(Status.CLIENT_ERROR_BAD_REQUEST, "Bad credentials.");
      return null;
    }
  }

}
