/**
 * DepositorySensorSummaryServerResource.java This file is part of WattDepot.
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
import org.restlet.resource.ResourceException;
import org.wattdepot.common.domainmodel.Labels;
import org.wattdepot.common.domainmodel.MeasurementRateSummary;
import org.wattdepot.common.exception.IdNotFoundException;
import org.wattdepot.common.exception.NoMeasurementException;
import org.wattdepot.common.http.api.DepositorySensorSummaryResource;
import org.wattdepot.server.ServerProperties;

/**
 * DepositorySensorSummaryServerResource - Handles the Depository Sensor Summary
 * HTTP API (
 * "/wattdepot/{org-id}/depository/{depository-id}/summary/?sensor={sensor-id}"
 * ).
 * 
 * @author Cam Moore
 * 
 */
public class DepositorySensorSummaryServerResource extends WattDepotServerResource implements
    DepositorySensorSummaryResource {

  private String depositoryId;
  private String sensorId;

  /*
   * (non-Javadoc)
   * 
   * @see org.restlet.resource.Resource#doInit()
   */
  @Override
  protected void doInit() throws ResourceException {
    super.doInit();
    this.depositoryId = getAttribute(Labels.DEPOSITORY_ID);
    this.sensorId = getQuery().getValues(Labels.SENSOR);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.common.http.api.DepositorySensorSummaryResource#retrieve()
   */
  @Override
  public MeasurementRateSummary retrieve() {
    getLogger().log(
        Level.INFO,
        "GET /wattdepot/{" + orgId + "}/depository/{" + depositoryId + "}/summary/?sensor={"
            + sensorId + "}");
    Long startTime = null;
    if (depot.getServerProperties().get(ServerProperties.SERVER_TIMING_KEY)
        .equals(ServerProperties.TRUE)) {
      startTime = System.nanoTime();
    }
    MeasurementRateSummary sum = null;
    if (isInRole(orgId)) {
      try {
        sum = depot.getRateSummary(depositoryId, orgId, sensorId, false);
      }
      catch (IdNotFoundException e) {
        setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
      }
      catch (NoMeasurementException e) {
        setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
      }
    }
    if (depot.getServerProperties().get(ServerProperties.SERVER_TIMING_KEY)
        .equals(ServerProperties.TRUE)) {
      Long endTime = System.nanoTime();
      getLogger().log(
          Level.SEVERE,
          "GET /wattdepot/{" + orgId + "}/depository/{" + depositoryId + "}/summary/?sensor={"
              + sensorId + "} took " + ((endTime - startTime) / 1E9) + " seconds.");
    }
    return sum;
  }

}
