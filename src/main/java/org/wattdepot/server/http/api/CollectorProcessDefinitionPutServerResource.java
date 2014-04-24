/**
 * CollectorProcessDefinitionServerResource.java This file is part of WattDepot.
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
import org.wattdepot.common.domainmodel.CollectorProcessDefinition;
import org.wattdepot.common.domainmodel.Labels;
import org.wattdepot.common.domainmodel.Sensor;
import org.wattdepot.common.exception.BadSlugException;
import org.wattdepot.common.exception.IdNotFoundException;
import org.wattdepot.common.exception.MisMatchedOwnerException;
import org.wattdepot.common.exception.UniqueIdException;
import org.wattdepot.common.http.api.API;
import org.wattdepot.common.http.api.CollectorProcessDefinitionPutResource;

/**
 * CollectorProcessDefinitionServerResource - Handles the
 * CollectorProcessDefinition HTTP API
 * ("/wattdepot/{org-id}/collector-process-definition/").
 * 
 * @author Cam Moore
 * 
 */
public class CollectorProcessDefinitionPutServerResource extends WattDepotServerResource implements
    CollectorProcessDefinitionPutResource {
  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.restlet.CollectorProcessDefinitionResource#store(org.wattdepot
   * .datamodel.CollectorProcessDefinition)
   */
  @Override
  public void store(CollectorProcessDefinition definition) {
    getLogger().log(
        Level.INFO,
        "PUT " + API.BASE_URI + "{" + orgId + "}/" + Labels.COLLECTOR_PROCESS_DEFINITION
            + "/ with " + definition);
    if (isInRole(orgId)) {
      try {
        depot.getOrganization(orgId, true);
      }
      catch (IdNotFoundException e1) {
        setStatus(Status.CLIENT_ERROR_BAD_REQUEST, orgId + " is not a defined Organization.");
      }
      try {
        if (!depot.getCollectorProcessDefinitionIds(orgId, false).contains(definition.getId())) {
          try {
            Sensor s = depot.getSensor(definition.getSensorId(), orgId, true);
            if (s != null) {
              depot.defineCollectorProcessDefinition(definition.getId(), definition.getName(),
                  s.getId(), definition.getPollingInterval(), definition.getDepositoryId(),
                  definition.getProperties(), orgId);
            }
            else {
              setStatus(Status.CLIENT_ERROR_BAD_REQUEST, "Sensor " + definition.getSensorId()
                  + " is not defined.");
            }
          }
          catch (UniqueIdException e) {
            setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
          }
          catch (MisMatchedOwnerException e) {
            setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
          }
          catch (IdNotFoundException e) {
            setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
          }
          catch (BadSlugException e) {
            setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
          }
        }
        else {
          setStatus(Status.CLIENT_ERROR_BAD_REQUEST,
              "CollectorProcessDefinition " + definition.getName() + " is already defined.");
        }
      }
      catch (IdNotFoundException e) {
        setStatus(Status.CLIENT_ERROR_BAD_REQUEST, orgId + " is not a defined Organization id.");
      }
    }
    else {
      setStatus(Status.CLIENT_ERROR_BAD_REQUEST, "Bad credentials.");
    }
  }
}
