/**
 * SensorProcessesServerResource.java This file is part of WattDepot.
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

import org.wattdepot.common.domainmodel.CollectorProcessDefinition;
import org.wattdepot.common.domainmodel.CollectorProcessDefinitionList;
import org.wattdepot.common.http.api.CollectorProcessDefinitionsResource;

/**
 * SensorProcessesServerResource - Handles the SensorProcesses HTTP API
 * ("/wattdepot/{org-id}/collector-process-definitions/").
 * 
 * @author Cam Moore
 * 
 */
public class CollectorProcessDefinitionsServerResource extends WattDepotServerResource implements
    CollectorProcessDefinitionsResource {

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.restlet.SensorProcessesResouce#retrieve()
   */
  @Override
  public CollectorProcessDefinitionList retrieve() {
    getLogger().log(Level.INFO, "GET /wattdepot/{" + orgId + "}/collector-process-definitions/");
    CollectorProcessDefinitionList ret = new CollectorProcessDefinitionList();
    for (CollectorProcessDefinition sp : depot.getCollectorProcessDefinitions(orgId)) {
      ret.getDefinitions().add(sp);
    }
    return ret;
  }
}
