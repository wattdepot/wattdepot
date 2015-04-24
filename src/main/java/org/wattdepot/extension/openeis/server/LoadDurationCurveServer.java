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
import org.wattdepot.common.domainmodel.InterpolatedValue;
import org.wattdepot.common.domainmodel.InterpolatedValueList;
import org.wattdepot.common.domainmodel.InterpolatedValueValueComparator;
import org.wattdepot.common.domainmodel.Labels;
import org.wattdepot.common.exception.IdNotFoundException;
import org.wattdepot.common.util.tstamp.Tstamp;
import org.wattdepot.extension.openeis.OpenEISLabels;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.logging.Level;

/**
 * LoadDurationCurveServer - Base class for handling Load Duration Curve requests.
 *
 * @author Cam Moore
 *         Created by carletonmoore on 4/22/15.
 */
public class LoadDurationCurveServer extends OpenEISServer {
  private String depositoryId;
  private String sensorId;
  private String startString;
  private String endString;

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
    this.startString = getQuery().getValues(Labels.START);
    this.endString = getQuery().getValues(Labels.END);
  }

  /**
   * Retrieves the hourly power data for the given depository, sensor, start and end dates.
   *
   * @return An InterpolatedValueList of the hourly power data.
   */
  public InterpolatedValueList doRetrieve() {
    getLogger().log(
        Level.INFO,
        "GET /wattdepot/{" + orgId + "}/" + OpenEISLabels.OPENEIS + "/" + OpenEISLabels.LOAD_DURATION_CURVE +
            "/?" + Labels.DEPOSITORY + "={" + depositoryId + "}&" + Labels.SENSOR + "={" + sensorId + "}&" +
            Labels.START + "={" + startString + "}&" + Labels.END + "={" + endString + "}");
    try {
      Depository depository = depot.getDepository(depositoryId, orgId, true);
      Date start = Tstamp.makeTimestamp(startString).toGregorianCalendar().getTime();
      Date end = Tstamp.makeTimestamp(endString).toGregorianCalendar().getTime();
      InterpolatedValueList values;
      if (depository.getMeasurementType().getName().startsWith("Power")) {
        values = getHourlyPointData(depositoryId, sensorId, start, end, false);
      }
      else if (depository.getMeasurementType().getName().startsWith("Energy")) {
        values = getHourlyDifferenceData(depositoryId, sensorId, start, end, false);
      }
      else {
        setStatus(Status.CLIENT_ERROR_BAD_REQUEST, depositoryId + " is not a Power Depository.");
        return null;
      }
      InterpolatedValueValueComparator c = new InterpolatedValueValueComparator();
      ArrayList<InterpolatedValue> list = values.getInterpolatedValues();
      Collections.sort(list, c);
      Collections.reverse(list);
      values.setInterpolatedValues(list);
      return values;
    }
    catch (IdNotFoundException e) {
      setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
      return null;
    }
    catch (Exception e) {
      setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
      return null;
    }
  }

}
