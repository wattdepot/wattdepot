/**
 * DepositoryMaximumValuesServerResource.java This file is part of WattDepot.
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

import java.util.Date;

import org.restlet.data.Status;
import org.wattdepot.common.analysis.DescriptiveStats;
import org.wattdepot.common.domainmodel.Depository;
import org.wattdepot.common.domainmodel.InterpolatedValue;
import org.wattdepot.common.domainmodel.InterpolatedValueList;
import org.wattdepot.common.domainmodel.MeasurementList;
import org.wattdepot.common.exception.IdNotFoundException;
import org.wattdepot.common.exception.MisMatchedOwnerException;
import org.wattdepot.common.http.api.DepositoryMaximumValuesResource;

/**
 * DepositoryMaximumValuesServerResource - Calculates the Maximum value for the
 * measurements during the interval from the start date till the end date.
 * 
 * @author Cam Moore
 * 
 */
public class DepositoryMaximumValuesServerResource extends DepositoryMeasurementsIntervalServer
    implements DepositoryMaximumValuesResource {

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.common.http.api.DepositoryMaximumValuesResource#retrieve()
   */
  @Override
  public InterpolatedValueList retrieve() {
    InterpolatedValueList ret = new InterpolatedValueList();
    // loop from start till end getting the measurements in the interval
    Date tempStart = startDate;
    Date tempEnd = incrementMinutes(tempStart, interval);
    try {
      Depository deposit = depot.getDepository(depositoryId, orgId, true);
      while (tempEnd.before(endDate)) {
        InterpolatedValue interpolatedValue = new InterpolatedValue(sensorId, 0.0, deposit.getMeasurementType(),
            tempStart, tempEnd);
        MeasurementList meas = getMeasurements(depositoryId, orgId, sensorId, tempStart, tempEnd, interpolatedValue);
        DescriptiveStats stat = new DescriptiveStats(meas, valueType);
        Double value = stat.getMax();
        if (Double.isNaN(value)) {
//          value = 0.0;
          interpolatedValue.setValue(value);
          ret.getMissingData().add(interpolatedValue);
        }
        else {
          interpolatedValue.setValue(value);
          ret.getInterpolatedValues().add(interpolatedValue);
        }
        tempStart = tempEnd;
        tempEnd = incrementMinutes(tempStart, interval);
      }
    }
    catch (IdNotFoundException e) {
      setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
      return null;
    }
    catch (MisMatchedOwnerException e) {
      setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
      return null;
    }

    return ret;
  }

}
