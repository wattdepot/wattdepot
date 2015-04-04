/*
 * This file is part of WattDepot.
 *
 *  Copyright (C) 2015  Cam Moore
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.wattdepot.server.http.api.openeis;

import org.restlet.data.Status;
import org.restlet.resource.ResourceException;
import org.wattdepot.common.domainmodel.Depository;
import org.wattdepot.common.domainmodel.InterpolatedValueList;
import org.wattdepot.common.domainmodel.Labels;
import org.wattdepot.common.exception.IdNotFoundException;

import java.util.logging.Level;

/**
 * EnergySignatureServer - Base class for the Heat Map requests.
 * @author Cam Moore
 */
public class EnergySignatureServer extends OpenEISServer {
  private String powerDepositoryId;
  private String powerSensorId;
  private String temperatureDepositoryId;
  private String temperatureSensorId;


  /*
   * (non-Javadoc)
   *
   * @see org.restlet.resource.Resource#doInit()
   */
  @Override
  protected void doInit() throws ResourceException {
    super.doInit();
    this.powerDepositoryId = getQuery().getValues(Labels.POWER_DEPOSITORY);
    this.powerSensorId = getQuery().getValues(Labels.POWER_SENSOR);
    this.temperatureDepositoryId = getQuery().getValues(Labels.TEMPERATURE_DEPOSITORY);
    this.temperatureSensorId = getQuery().getValues(Labels.TEMPERATURE_SENSOR);
  }

  /**
   * Retrieves the last year's hourly power data for the given depository and sensor.
   *
   * @return An InterpolatedValueList of the hourly power data.
   */
  public InterpolatedValueList doRetrieve() {
    getLogger().log(
      Level.INFO,
      "GET /wattdepot/{" + orgId + "}/" + Labels.OPENEIS + "/" + Labels.HEAT_MAP +
        "/?" + Labels.POWER_DEPOSITORY + "={" + powerDepositoryId + "}&" + Labels.POWER_SENSOR + "={" + powerSensorId + "}&"
        + Labels.TEMPERATURE_DEPOSITORY + "={" + temperatureDepositoryId + "}&" + Labels.TEMPERATURE_SENSOR + "={"
        + temperatureSensorId + "}");
    if (isInRole(orgId)) {
      InterpolatedValueList ret = new InterpolatedValueList();
      try {
        Depository powerDepository = depot.getDepository(powerDepositoryId, orgId, true);
        if (powerDepository.getMeasurementType().getName().startsWith("Power")) {
          InterpolatedValueList powerValues = getHourlyPointDataYear(powerDepositoryId, powerSensorId);
          Depository temperatureDepository = depot.getDepository(temperatureDepositoryId, orgId, true);
          if (temperatureDepository.getMeasurementType().getName().startsWith("Temperature")) {
            InterpolatedValueList temperatureValues = getHourlyPointDataYear(temperatureDepositoryId, temperatureSensorId);
          }
          else {
            setStatus(Status.CLIENT_ERROR_BAD_REQUEST, temperatureDepositoryId + " is not a temperature depository.");
            return null;
          }
        }
        else {
          setStatus(Status.CLIENT_ERROR_BAD_REQUEST, powerDepositoryId + " is not a power depository.");
          return null;
        }
      }
      catch (IdNotFoundException e) {
        setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
        return null;
      }
      return ret;
    }
    else {
      setStatus(Status.CLIENT_ERROR_BAD_REQUEST, "Bad credentials.");
      return null;
    }
  }
}
