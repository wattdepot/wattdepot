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
import org.wattdepot.common.domainmodel.Labels;
import org.wattdepot.common.exception.IdNotFoundException;
import org.wattdepot.common.util.tstamp.Tstamp;
import org.wattdepot.extension.openeis.OpenEISLabels;
import org.wattdepot.extension.openeis.domainmodel.TimeInterval;

import javax.xml.datatype.XMLGregorianCalendar;
import java.util.Date;
import java.util.logging.Level;

/**
 * Base class for creating longitudinal benchmarks.
 *
 * @author Cam Moore
 *         Created by carletonmoore on 4/18/15.
 */
public class LongitudinalBenchmarkingServer extends OpenEISServer {
  private String depositoryId;
  private String sensorId;
  private String baseStartString;
  private TimeInterval interval;
  private String comparisonStartString;
  private Integer numberOfIntervals;

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
    this.baseStartString = getQuery().getValues(OpenEISLabels.BASELINE_START);
    this.interval = TimeInterval.fromParameter(getQuery().getValues(OpenEISLabels.BASELINE_DURATION));
    this.comparisonStartString = getQuery().getValues(OpenEISLabels.COMPARISON_START);
    this.numberOfIntervals = Integer.parseInt(getQuery().getValues(OpenEISLabels.NUM_INTERVALS));
  }

  /**
   * Calculates the baseline and comparison values.
   *
   * @return An InterpolatedValueList of the baseline and comparison values.
   */
  public InterpolatedValueList doRetrieve() {
    getLogger().log(
        Level.INFO,
        "GET /wattdepot/{" + orgId + "}/" + OpenEISLabels.OPENEIS + "/" + OpenEISLabels.LONGITUDINAL_BENCHMARKING +
            "/?" + Labels.DEPOSITORY + "={" + depositoryId + "}&" + Labels.SENSOR + "={" + sensorId + "}&" +
            OpenEISLabels.BASELINE_START + "={" + baseStartString + "}&" + OpenEISLabels.BASELINE_DURATION +
            "={" + interval + "&" + OpenEISLabels.COMPARISON_START + "={" + comparisonStartString + "}&" +
            OpenEISLabels.NUM_INTERVALS + "={" + numberOfIntervals + "}");
    try {
      Depository depository = depot.getDepository(depositoryId, orgId, true);
      InterpolatedValueList ret = null;
      if (depository.getMeasurementType().getName().startsWith("Energy")) {
        XMLGregorianCalendar baseCal = Tstamp.makeTimestamp(baseStartString);
        Date baseStart = baseCal.toGregorianCalendar().getTime();
        XMLGregorianCalendar compareCal = Tstamp.makeTimestamp(comparisonStartString);
        Date compDate = compareCal.toGregorianCalendar().getTime();
        ret = getDifferenceValues(depositoryId, sensorId, baseStart, interval, 1, true);
        InterpolatedValueList compareValues = getDifferenceValues(depositoryId, sensorId, compDate, interval, numberOfIntervals, true);
        for (InterpolatedValue val : compareValues.getInterpolatedValues()) {
          ret.getInterpolatedValues().add(val);
        }
      }
      return ret;
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