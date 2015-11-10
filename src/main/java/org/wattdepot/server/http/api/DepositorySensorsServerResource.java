/**
 * DepositorySensorsServerResource.java This file is part of WattDepot.
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

import java.util.ArrayList;
import java.util.logging.Level;

import org.restlet.data.Status;
import org.restlet.resource.ResourceException;
import org.wattdepot.common.domainmodel.Depository;
import org.wattdepot.common.domainmodel.Labels;
import org.wattdepot.common.domainmodel.Sensor;
import org.wattdepot.common.domainmodel.SensorList;
import org.wattdepot.common.exception.IdNotFoundException;
import org.wattdepot.common.exception.MisMatchedOwnerException;
import org.wattdepot.common.http.api.DepositorySensorsResource;

/**
 * DepositorySensorsServerResource - Handles the Depository sensors HTTP API
 * ("/wattdepot/{org-id}/depository/{depository-id}/sensors/").
 * 
 * @author Cam Moore
 * 
 */
public class DepositorySensorsServerResource extends WattDepotServerResource implements
    DepositorySensorsResource {
  private String depositoryId;

  /*
   * (non-Javadoc)
   * 
   * @see org.restlet.resource.Resource#doInit()
   */
  @Override
  protected void doInit() throws ResourceException {
    super.doInit();
    this.depositoryId = getAttribute(Labels.DEPOSITORY_ID);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.restlet.DepositorySensorsResource#retrieve()
   */
  @Override
  public SensorList retrieve() {
    getLogger().log(Level.INFO,
        "GET /wattdepot/{" + orgId + "}/depository/{" + depositoryId + "}/sensors/");
    if (isInRole(orgId)) {
      Depository depository;
      SensorList ret = null;
      try {
        depository = depot.getDepository(depositoryId, orgId, true);
        if (depository != null) {
          ArrayList<Sensor> sensors = new ArrayList<Sensor>();
          ret = new SensorList();
          for (String sid : depot.listSensors(depositoryId, orgId, true)) {
            Sensor s = depot.getSensor(sid, orgId, true);
            if (!sensors.contains(s)) {
              sensors.add(s);
            }
          }
          ret.setSensors(sensors);
        }
        else {
          setStatus(Status.CLIENT_ERROR_BAD_REQUEST, depositoryId + " not defined.");
        }
      }
      catch (MisMatchedOwnerException e) {
        setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
      }
      catch (IdNotFoundException e) {
        setStatus(Status.CLIENT_ERROR_BAD_REQUEST, depositoryId + " not defined.");
      }
      return ret;
    }
    else {
      setStatus(Status.CLIENT_ERROR_BAD_REQUEST, "Bad credentials.");
      return null;
    }
  }

}
