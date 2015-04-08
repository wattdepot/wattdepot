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

package org.wattdepot.extension.openeis.server;

import org.restlet.data.Status;
import org.restlet.resource.ResourceException;
import org.wattdepot.common.domainmodel.Depository;
import org.wattdepot.common.domainmodel.InterpolatedValueList;
import org.wattdepot.common.domainmodel.Labels;
import org.wattdepot.common.exception.IdNotFoundException;

import java.util.logging.Level;

/**
 * TimeSeriesLoadProfileServer - Base class for handling Time Series Load Profile requests.
 *
 * @author Cam Moore
 */
public class TimeSeriesLoadProfileServer extends OpenEISServer {
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
    this.depositoryId = getQuery().getValues(Labels.DEPOSITORY);
    this.sensorId = getQuery().getValues(Labels.SENSOR);
  }

  /**
   * Retrieves the last month's hourly power data for the given depository and sensor.
   * @return An InterpolatedValueList of the hourly power data.
   */
  public InterpolatedValueList doRetrieve() {
    getLogger().log(
        Level.INFO,
        "GET /wattdepot/{" + orgId + "}/" + Labels.OPENEIS + "/" + Labels.TIME_SERIES_LOAD_PROFILING +
            "/?" + Labels.DEPOSITORY + "={" + depositoryId + "}&" + Labels.SENSOR + "={" + sensorId + "}");
    try {
      Depository depository = depot.getDepository(depositoryId, orgId, true);
      if (depository.getMeasurementType().getName().startsWith("Power")) {
        return getHourlyPointDataMonth(depositoryId, sensorId);
      }
      else {
        setStatus(Status.CLIENT_ERROR_BAD_REQUEST, depositoryId + " is not a Power Depository.");
        return null;
      }
    }
    catch (IdNotFoundException e) {
      setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
      return null;
    }
  }
}
