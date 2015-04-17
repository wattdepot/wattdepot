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
import org.wattdepot.common.analysis.DescriptiveStats;
import org.wattdepot.common.domainmodel.Depository;
import org.wattdepot.common.domainmodel.InterpolatedValue;
import org.wattdepot.common.domainmodel.InterpolatedValueList;
import org.wattdepot.common.domainmodel.Labels;
import org.wattdepot.common.domainmodel.XYInterpolatedValue;
import org.wattdepot.common.domainmodel.XYInterpolatedValueList;
import org.wattdepot.common.exception.IdNotFoundException;
import org.wattdepot.extension.openeis.OpenEISLabels;
import org.wattdepot.extension.openeis.domainmodel.XYInterpolatedValuesWithAnalysis;

import java.util.ArrayList;
import java.util.HashMap;
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
  private Double weatherSensitivity;


  /*
   * (non-Javadoc)
   *
   * @see org.restlet.resource.Resource#doInit()
   */
  @Override
  protected void doInit() throws ResourceException {
    super.doInit();
    this.powerDepositoryId = getQuery().getValues(OpenEISLabels.POWER_DEPOSITORY);
    this.powerSensorId = getQuery().getValues(OpenEISLabels.POWER_SENSOR);
    this.temperatureDepositoryId = getQuery().getValues(OpenEISLabels.TEMPERATURE_DEPOSITORY);
    this.temperatureSensorId = getQuery().getValues(OpenEISLabels.TEMPERATURE_SENSOR);
    this.weatherSensitivity = 0.0;
  }

  /**
   * Retrieves the last year's hourly power data for the given depository and sensor.
   *
   * @return An InterpolatedValueList of the hourly power data.
   */
  public XYInterpolatedValuesWithAnalysis doRetrieve() {
    getLogger().log(
      Level.INFO,
      "GET /wattdepot/{" + orgId + "}/" + OpenEISLabels.OPENEIS + "/" + OpenEISLabels.HEAT_MAP +
        "/?" + OpenEISLabels.POWER_DEPOSITORY + "={" + powerDepositoryId + "}&" + OpenEISLabels.POWER_SENSOR + "={" + powerSensorId + "}&"
        + OpenEISLabels.TEMPERATURE_DEPOSITORY + "={" + temperatureDepositoryId + "}&" + OpenEISLabels.TEMPERATURE_SENSOR + "={"
        + temperatureSensorId + "}");
    if (isInRole(orgId)) {
      XYInterpolatedValueList ret = new XYInterpolatedValueList();
      Double r = 0.0;
      try {
        Depository powerDepository = depot.getDepository(powerDepositoryId, orgId, true);
        if (powerDepository.getMeasurementType().getName().startsWith("Power")) {
          InterpolatedValueList powerValues = getHourlyPointDataYear(powerDepositoryId, powerSensorId);
          Depository temperatureDepository = depot.getDepository(temperatureDepositoryId, orgId, true);
          DescriptiveStats powerStats = new DescriptiveStats(powerValues);
          if (temperatureDepository.getMeasurementType().getName().startsWith("Temperature")) {
            InterpolatedValueList temperatureValues = getHourlyPointDataYear(temperatureDepositoryId, temperatureSensorId);
            DescriptiveStats temperatureStats = new DescriptiveStats(temperatureValues);
            ArrayList<InterpolatedValue> powerData = powerValues.getInterpolatedValues();
            ArrayList<InterpolatedValue> temperatureData = temperatureValues.getInterpolatedValues();
            Double xMean = powerStats.getMean();
            Double numeratorSum = 0.0;
            Double xDenomSum = 0.0;
            Double yMean = temperatureStats.getMean();
            Double yDenomSum = 0.0;
            for (int i = 0; i < powerData.size(); i++) {
              Double xVal = powerData.get(i).getValue();
              Double yVal = temperatureData.get(i).getValue();
              if (xVal != null && yVal != null) {
                Double xTerm =  xVal - xMean;
                Double yTerm =  yVal - yMean;
                numeratorSum += xTerm * yTerm;
                xDenomSum += Math.pow(xTerm, 2.0);
                yDenomSum += Math.pow(yTerm, 2.0);
              }
              ret.getValues().add(new XYInterpolatedValue(powerData.get(i), temperatureData.get(i)));
            }
            r = numeratorSum / (Math.sqrt(xDenomSum) * Math.sqrt(yDenomSum));
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
      XYInterpolatedValuesWithAnalysis analysis = new XYInterpolatedValuesWithAnalysis();
      analysis.setDataPoints(ret);
      HashMap<String, Double> regression = new HashMap<>();
      regression.put("Weather Sensitivity", r);
      analysis.setAnalysis(regression);
      return analysis;
    }
    else {
      setStatus(Status.CLIENT_ERROR_BAD_REQUEST, "Bad credentials.");
      return null;
    }
  }
}
