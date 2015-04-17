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
import org.wattdepot.extension.openeis.OpenEISLabels;
import org.wattdepot.extension.openeis.domainmodel.TimeInterval;

import java.util.logging.Level;

/**
 * TimeSeriesLoadProfileServer - Base class for handling Time Series Load Profile requests.
 *
 * @author Cam Moore
 */
public class TimeSeriesLoadProfileServer extends OpenEISServer {
  private String depositoryId;
  private String sensorId;
  private TimeInterval interval;


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
    switch (getQuery().getValues("duration")) {
      case "1w":
        interval = TimeInterval.ONE_WEEK;
        break;
      case "2w":
        interval = TimeInterval.TWO_WEEKS;
        break;
      case "3w":
        interval = TimeInterval.THREE_WEEKS;
        break;
      case "4w":
        interval = TimeInterval.FOUR_WEEKS;
        break;
      case "1m":
        interval = TimeInterval.ONE_MONTH;
        break;
      case "2m":
        interval = TimeInterval.TWO_MONTHS;
        break;
      case "3m":
        interval = TimeInterval.THREE_MONTHS;
        break;
      case "4m":
        interval = TimeInterval.FOUR_MONTHS;
        break;
      case "5m":
        interval = TimeInterval.FIVE_MONTHS;
        break;
      case "6m":
        interval = TimeInterval.SIX_MONTHS;
        break;
      case "1y":
        interval = TimeInterval.ONE_YEAR;
        break;
      default:
        interval = TimeInterval.ONE_MONTH;
    }
  }

  /**
   * Retrieves the last month's hourly power data for the given depository and sensor.
   * @return An InterpolatedValueList of the hourly power data.
   */
  public InterpolatedValueList doRetrieve() {
    getLogger().log(
        Level.INFO,
        "GET /wattdepot/{" + orgId + "}/" + OpenEISLabels.OPENEIS + "/" + OpenEISLabels.TIME_SERIES_LOAD_PROFILING +
            "/?" + Labels.DEPOSITORY + "={" + depositoryId + "}&" + Labels.SENSOR + "={" + sensorId + "}&duration={" + interval + "}");
    try {
      Depository depository = depot.getDepository(depositoryId, orgId, true);
      if (depository.getMeasurementType().getName().startsWith("Power")) {
        return getHourlyPointData(depositoryId, sensorId, interval);
      }
      else if ( depository.getMeasurementType().getName().startsWith("Energy")) {
        return getDifferenceData(depositoryId, sensorId, interval);
      }
      else {
        setStatus(Status.CLIENT_ERROR_BAD_REQUEST, depositoryId + " is not a Power/Energy Depository.");
        return null;
      }
    }
    catch (IdNotFoundException e) {
      setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
      return null;
    }
  }
}
