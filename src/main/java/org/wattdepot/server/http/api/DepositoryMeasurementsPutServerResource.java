/**
 * DepositoryMeasurementServerResource.java This file is part of WattDepot.
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

import org.restlet.data.Status;
import org.restlet.resource.ResourceException;
import org.wattdepot.common.domainmodel.Depository;
import org.wattdepot.common.domainmodel.Labels;
import org.wattdepot.common.domainmodel.Measurement;
import org.wattdepot.common.domainmodel.MeasurementList;
import org.wattdepot.common.domainmodel.Sensor;
import org.wattdepot.common.exception.IdNotFoundException;
import org.wattdepot.common.exception.MeasurementTypeException;
import org.wattdepot.common.http.api.DepositoryMeasurementsPutResource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

/**
 * DepositoryMeasurementsPutServerResource - Handles the Measurements HTTP API
 * ("/wattdepot/{org-id}/depository/{depository_id}/measurements/").
 *
 * @author John Smedegaard
 */
public class DepositoryMeasurementsPutServerResource extends WattDepotServerResource implements
    DepositoryMeasurementsPutResource {
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
   * @see
   * org.wattdepot.restlet.DepositoryMeasurementsPutResource#store(org.wattdepot3
   * .datamodel.Measurements)
   */
  @Override
  public void store(MeasurementList measurementList) {
    getLogger().log(
        Level.INFO,
        "PUT /wattdepot/{" + orgId + "}/depository/{" + depositoryId + "}/measurements/bulk/ with "
            + measurementList);
    if (isInRole(orgId)) {
      try {

        Depository depository = depot.getDepository(depositoryId, orgId, true);
        if (depository != null) {
          // Checking that all measurements have valid sensors.
          Map<String, Sensor> sensorMap = new HashMap<>();
          List<Sensor> sensors = depot.getSensors(orgId, false);
          for (Sensor sensor : sensors) {
            sensorMap.put(sensor.getId(), sensor);
          }

          ArrayList<String> invalidSensorIds = new ArrayList<>(measurementList.getMeasurements().size());

          for (int i = 0; i < measurementList.getMeasurements().size(); i++) {
            Measurement measurement = measurementList.getMeasurements().get(i);

            if ( !sensorMap.containsKey(measurement.getSensorId())) {
              invalidSensorIds.add(measurement.getSensorId());
            }
          }
          // All sensor id's were valid.
          if (invalidSensorIds.isEmpty()) {
            depot.putMeasurementList(depositoryId, orgId, measurementList);
          }
          else {
            setStatus(Status.CLIENT_ERROR_BAD_REQUEST, "Sensors: " + invalidSensorIds
                + " does not exist. No measurements were put into WattDepot");
          }
        }
        else {
          setStatus(Status.CLIENT_ERROR_BAD_REQUEST, depositoryId + " does not exist.");
        }
      }
      catch (IdNotFoundException e) {
        setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
      }
      catch (MeasurementTypeException e) {
        setStatus(Status.CLIENT_ERROR_BAD_REQUEST, "Measurement type does not match depository..");
      }
    }
    else {
      setStatus(Status.CLIENT_ERROR_BAD_REQUEST, "Bad credentials.");
    }
  }
}
