/**
 * PutMeasurementTask.java This file is part of WattDepot.
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
package org.wattdepot.client.http.api.performance;

import java.util.Date;
import java.util.HashSet;
import java.util.TimerTask;

import javax.measure.unit.Unit;

import org.wattdepot.client.http.api.WattDepotClient;
import org.wattdepot.common.domainmodel.CollectorProcessDefinition;
import org.wattdepot.common.domainmodel.Depository;
import org.wattdepot.common.domainmodel.Measurement;
import org.wattdepot.common.domainmodel.MeasurementType;
import org.wattdepot.common.domainmodel.Property;
import org.wattdepot.common.domainmodel.Sensor;
import org.wattdepot.common.domainmodel.SensorModel;
import org.wattdepot.common.exception.BadCredentialException;
import org.wattdepot.common.exception.BadSensorUriException;
import org.wattdepot.common.exception.IdNotFoundException;
import org.wattdepot.common.exception.MeasurementTypeException;
import org.wattdepot.common.util.SensorModelHelper;

/**
 * PutMeasurementTask stores a Measurement using the WattDepotClient. Records
 * the amount of time the puts take.
 * 
 * @author Cam Moore
 * 
 */
public class PutMeasurementTask extends TimerTask {

  /** Flag for debugging messages. */
  private boolean debug;
  /** The definition about the collector. */
  private CollectorProcessDefinition definition;
  /** The client used to communicate with the WattDepot server. */
  private WattDepotClient client;
  /** The Depository for storing measurements. */
  private Depository depository;

  private Long totalTime;
  private Long numberOfRuns;
  private Long minPutTime;
  private Long maxPutTime;
  private String collectorId = "performance-evaluation-collector";

  /**
   * Initializes the PutMeasurementTask.
   * 
   * @param serverUri The URI for the WattDepot server.
   * @param username The name of a user defined in the WattDepot server.
   * @param orgId the id of the organization the user is in.
   * @param password The password for the user.
   * @param debug flag for debugging messages.
   * @throws BadCredentialException if the user or password don't match the
   *         credentials in WattDepot.
   * @throws IdNotFoundException if the processId is not defined.
   * @throws BadSensorUriException if the Sensor's URI isn't valid.
   */
  public PutMeasurementTask(String serverUri, String username, String orgId, String password,
      boolean debug) throws BadCredentialException, IdNotFoundException,
      BadSensorUriException {
    this.totalTime = 0l;
    this.numberOfRuns = 0l;
    this.maxPutTime = -1l;
    this.minPutTime = Long.MAX_VALUE;
    this.client = new WattDepotClient(serverUri, username, orgId, password);
    this.debug = debug;
    if (!client.isDefinedCollectorProcessDefinition(this.collectorId)) {
      initializePerformanceItems(this.collectorId);
    }
    this.definition = client.getCollectorProcessDefinition(this.collectorId);
    this.depository = client.getDepository(definition.getDepositoryId());

  }

  /**
   * @return Generates a fake Measurement with the current time.
   */
  private Measurement generateFakeMeasurement() {
    Date now = new Date();
    Unit<?> unit = Unit.valueOf(this.depository.getMeasurementType().getUnits());
    Measurement ret = new Measurement(this.definition.getSensorId(), now,
        (25.0 - 50 * Math.random()) + 1000.0, unit);
    return ret;
  }

  /**
   * @return average put time.
   */
  public Double getAveragePutTime() {
    return 1.0 * (totalTime) / numberOfRuns;
  }

  /**
   * @return the maxPutTime
   */
  public Long getMaxPutTime() {
    return maxPutTime;
  }

  /**
   * @return the minPutTime
   */
  public Long getMinPutTime() {
    return minPutTime;
  }

  /**
   * @return the numberOfRuns
   */
  public Long getNumberOfRuns() {
    return numberOfRuns;
  }

  /**
   * @return the totalTime
   */
  public Long getTotalTime() {
    return totalTime;
  }

  /**
   * Ensures that there is a Performance Evaluation sensor, depository, and
   * collector process definition.
   * 
   * @param collectorId the id of the collector to define.
   */
  private void initializePerformanceItems(String collectorId) {
    SensorModel model = SensorModelHelper.models.get(SensorModelHelper.STRESS);
    Sensor stressSensor = new Sensor("Performance Evaluation Sensor",
        "http://performance.evaluation.sensor", model.getId(), client.getOrganizationId());
    if (!client.isDefinedSensor(stressSensor.getId())) {
      client.putSensor(stressSensor);
    }
    MeasurementType measType = null;
    if (client.isDefinedMeasurementType("energy-wh")) {
      try {
        measType = client.getMeasurementType("energy-wh");
        Depository depository = new Depository("Performance Evaluation Energy", measType,
            client.getOrganizationId());
        if (!client.isDefinedDepository(depository.getId())) {
          client.putDepository(depository);
        }
        CollectorProcessDefinition cpd = new CollectorProcessDefinition(collectorId,
            "Performance Evaluation Collector", stressSensor.getId(), 1L, depository.getId(),
            new HashSet<Property>(), client.getOrganizationId());
        if (!client.isDefinedCollectorProcessDefinition(cpd.getId())) {
          client.putCollectorProcessDefinition(cpd);
        }
      }
      catch (IdNotFoundException e) { // NOPMD
        // can't happen
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.util.TimerTask#run()
   */
  @Override
  public void run() {
    Measurement meas = generateFakeMeasurement();
    if (meas != null) {
      numberOfRuns++;
      try {
        Long startTime = System.nanoTime();
        this.client.putMeasurement(depository, meas);
        Long endTime = System.nanoTime();
        Long diff = endTime - startTime;
        totalTime += diff;
        if (minPutTime > diff) {
          minPutTime = diff;
        }
        if (maxPutTime < diff) {
          maxPutTime = diff;
        }
      }
      catch (MeasurementTypeException e) {
        System.err.format("%s does not store %s measurements%n", depository.getName(),
            meas.getMeasurementType());
      }
      if (debug) {
        System.out.println(meas);
      }
    }
  }
}
