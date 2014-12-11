/**
 * WattDepotInterface.java This file is part of WattDepot.
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
package org.wattdepot.client;

import java.util.Date;

import org.wattdepot.common.domainmodel.CollectorProcessDefinition;
import org.wattdepot.common.domainmodel.CollectorProcessDefinitionList;
import org.wattdepot.common.domainmodel.Depository;
import org.wattdepot.common.domainmodel.DepositoryList;
import org.wattdepot.common.domainmodel.MeasurementPruningDefinition;
import org.wattdepot.common.domainmodel.MeasurementPruningDefinitionList;
import org.wattdepot.common.domainmodel.InterpolatedValue;
import org.wattdepot.common.domainmodel.Measurement;
import org.wattdepot.common.domainmodel.MeasurementList;
import org.wattdepot.common.domainmodel.MeasurementType;
import org.wattdepot.common.domainmodel.MeasurementTypeList;
import org.wattdepot.common.domainmodel.Sensor;
import org.wattdepot.common.domainmodel.SensorGroup;
import org.wattdepot.common.domainmodel.SensorGroupList;
import org.wattdepot.common.domainmodel.SensorList;
import org.wattdepot.common.domainmodel.SensorModel;
import org.wattdepot.common.domainmodel.SensorModelList;
import org.wattdepot.common.exception.IdNotFoundException;
import org.wattdepot.common.exception.MeasurementGapException;
import org.wattdepot.common.exception.MeasurementTypeException;
import org.wattdepot.common.exception.NoMeasurementException;

/**
 * WattDepotInterface - The CRUD interface to the WattDepot server for regular
 * users.
 * 
 * @author Cam Moore
 * 
 */
public interface WattDepotInterface {

  /**
   * Deletes the given CollectorProcessDefinitionData.
   * 
   * @param process the CollectorProcessDefinitionData to delete.
   * @throws IdNotFoundException if the CollectorProcessDefinitionData is not
   *         found in the server.
   */
  @SuppressWarnings("PMD.UnusedModifier")
  public void deleteCollectorProcessDefinition(CollectorProcessDefinition process)
      throws IdNotFoundException;

  /**
   * Deletes the given Depository.
   * 
   * @param depository the Depository to delete.
   * @throws IdNotFoundException if the Depository is not found in the server.
   */
  @SuppressWarnings("PMD.UnusedModifier")
  public void deleteDepository(Depository depository) throws IdNotFoundException;

  /**
   * Deletes the given measurement from the given depository.
   * 
   * @param depository the Depository that stores the measurement.
   * @param measurement the Measurement to delete.
   * @throws IdNotFoundException if the Measurement is not found in the
   *         Depository.
   */
  @SuppressWarnings("PMD.UnusedModifier")
  public void deleteMeasurement(Depository depository, Measurement measurement)
      throws IdNotFoundException;

  /**
   * Deletes the given MeasurementPruningDefinition from the WattDepotServer.
   * 
   * @param gcd The MeasurementPruningDefinition to delete.
   * @throws org.wattdepot.common.exception.IdNotFoundException if the MPD is not
   * found.
   */
  @SuppressWarnings("PMD.UnusedModifier")
  public void deleteMeasurementPruningDefinition(MeasurementPruningDefinition gcd) throws IdNotFoundException;

  /**
   * Deletes the given MeasurementType.
   * 
   * @param type the measurement type to delete.
   * @throws IdNotFoundException if the MeasurementType is not found in the
   *         Depository.
   */
  @SuppressWarnings("PMD.UnusedModifier")
  public void deleteMeasurementType(MeasurementType type) throws IdNotFoundException;

  /**
   * Deletes the given Sensor.
   * 
   * @param sensor the Sensor to delete.
   * @throws IdNotFoundException if the Sensor is not found in the server.
   */
  @SuppressWarnings("PMD.UnusedModifier")
  public void deleteSensor(Sensor sensor) throws IdNotFoundException;

  /**
   * Deletes the given SensorGroup.
   * 
   * @param group the SensorGroup to delete.
   * @throws IdNotFoundException if the SensorGroup is not found in the server.
   */
  @SuppressWarnings("PMD.UnusedModifier")
  public void deleteSensorGroup(SensorGroup group) throws IdNotFoundException;

  /**
   * Deletes the given SensorModel.
   * 
   * @param model the SensorModel to delete.
   * @throws IdNotFoundException if the SensorModel is not found in the server.
   */
  @SuppressWarnings("PMD.UnusedModifier")
  public void deleteSensorModel(SensorModel model) throws IdNotFoundException;

  /**
   * Retrieves the CollectorProcessDefinitionData with the given id from the
   * WattDepot Server.
   * 
   * @param id The CollectorProcessDefinitionData's id.
   * @return the CollectorProcessDefinitionData with the given id or null.
   * @exception IdNotFoundException if the given id is not a
   *            CollectorProcessDefinitionData's id.
   */
  @SuppressWarnings("PMD.UnusedModifier")
  public CollectorProcessDefinition getCollectorProcessDefinition(String id)
      throws IdNotFoundException;

  /**
   * @return The defined CollectorProcessDefinitions.
   */
  @SuppressWarnings("PMD.UnusedModifier")
  public CollectorProcessDefinitionList getCollectorProcessDefinitions();

  /**
   * @return The defined Depositories.
   */
  @SuppressWarnings("PMD.UnusedModifier")
  public DepositoryList getDepositories();

  /**
   * Retrieves the Depository with the given id from the WattDepot Server.
   * 
   * @param id The Depository's id.
   * @return the Depository with the given id or null.
   * @exception IdNotFoundException if the given id is not a Depository's id.
   */
  @SuppressWarnings("PMD.UnusedModifier")
  public Depository getDepository(String id) throws IdNotFoundException;

  /**
   * @param id The depository id.
   * @return a SensorList of the sensors contributing measurements to the
   *         depository.
   * @throws IdNotFoundException if the id is not defined.
   */
  @SuppressWarnings("PMD.UnusedModifier")
  public SensorList getDepositorySensors(String id) throws IdNotFoundException;

  /**
   * @param depository The Depository storing the Measurements.
   * @param sensor The Sensor making the Measurements.
   * @return The earliest Value.
   */
  @SuppressWarnings("PMD.UnusedModifier")
  public InterpolatedValue getEarliestValue(Depository depository, Sensor sensor);

  /**
   * @param depository The Depository storing the Measurements.
   * @param group The SensorGroup whose sensors are making the Measurements.
   * @return The earliest Value.
   */
  @SuppressWarnings("PMD.UnusedModifier")
  public InterpolatedValue getEarliestValue(Depository depository, SensorGroup group);

  /**
   * @param depository The Depository storing the Measurements.
   * @param sensor The Sensor making the Measurements.
   * @return The latest Measurement.
   */
  @SuppressWarnings("PMD.UnusedModifier")
  public InterpolatedValue getLatestValue(Depository depository, Sensor sensor);

  /**
   * @param depository The Depository storing the Measurements.
   * @param group The SensorGroup whose sensors are making the Measurements.
   * @return The latest Value.
   */
  @SuppressWarnings("PMD.UnusedModifier")
  public InterpolatedValue getLatestValue(Depository depository, SensorGroup group);

  /**
   * @param id The MeasurementPruningDefinition id.
   * @return The defined MeasurementPruningDefinition.
   * @throws IdNotFoundException if id is not defined.
   */
  @SuppressWarnings("PMD.UnusedModifier")
  public MeasurementPruningDefinition getMeasurementPruningDefinition(String id)
      throws IdNotFoundException;

  /**
   * @return All the defined MeasurementPruningDefinitions.
   */
  @SuppressWarnings("PMD.UnusedModifier")
  public MeasurementPruningDefinitionList getMeasurementPruningDefinitions();

  /**
   * @param depository The Depository storing the Measurements.
   * @param sensor The Sensor that made the measurements.
   * @param start The start time.
   * @param end The end time.
   * @return The Measurements stored in the depository made by the sensor
   *         between start and end.
   */
  @SuppressWarnings("PMD.UnusedModifier")
  public MeasurementList getMeasurements(Depository depository, Sensor sensor, Date start, Date end);

  /**
   * @param depository The Depository storing the Measurements.
   * @param group The SensorGroup whose sensors made the measurements.
   * @param start The start time.
   * @param end The end time.
   * @return The Measurements stored in the depository made by the sensor
   *         between start and end.
   */
  @SuppressWarnings("PMD.UnusedModifier")
  public MeasurementList getMeasurements(Depository depository, SensorGroup group, Date start,
      Date end);

  /**
   * @param id the unique id for the MeasurementType.
   * @return The MeasurementType.
   * @exception IdNotFoundException if the given id is not a MeasurementType's
   *            id.
   */
  @SuppressWarnings("PMD.UnusedModifier")
  public MeasurementType getMeasurementType(String id) throws IdNotFoundException;

  /**
   * @return The defined MeasurementTypes.
   */
  @SuppressWarnings("PMD.UnusedModifier")
  public MeasurementTypeList getMeasurementTypes();

  /**
   * Retrieves the Sensor with the given id from the WattDepot Server.
   * 
   * @param id The Sensor's id.
   * @return the Sensor with the given id or null.
   * @exception IdNotFoundException if the given id is not a Sensor's id.
   */
  @SuppressWarnings("PMD.UnusedModifier")
  public Sensor getSensor(String id) throws IdNotFoundException;

  /**
   * Retrieves the SensorGroup with the given id from the WattDepot Server.
   * 
   * @param id The SensorGroup's id.
   * @return the SensorGroup with the given id or null.
   * @exception IdNotFoundException if the given id is not a SensorGroup's id.
   */
  @SuppressWarnings("PMD.UnusedModifier")
  public SensorGroup getSensorGroup(String id) throws IdNotFoundException;

  /**
   * @return The defined SensorGroups.
   */
  @SuppressWarnings("PMD.UnusedModifier")
  public SensorGroupList getSensorGroups();

  /**
   * Retrieves the SensorModel with the given id from the WattDepot Server.
   * 
   * @param id The SensorModel's id.
   * @return the SensorModel with the given id or null.
   * @exception IdNotFoundException if the given id is not a SensorModel's id.
   */
  @SuppressWarnings("PMD.UnusedModifier")
  public SensorModel getSensorModel(String id) throws IdNotFoundException;

  /**
   * @return The defined SensorModels.
   */
  @SuppressWarnings("PMD.UnusedModifier")
  public SensorModelList getSensorModels();

  /**
   * @return The defined Sensors.
   */
  @SuppressWarnings("PMD.UnusedModifier")
  public SensorList getSensors();

  /**
   * @param depository The Depository storing the measurements.
   * @param sensor The sensor making the measurements.
   * @param timestamp The time for the measured value.
   * @return The Value 'measured' at the given time, most likely an interpolated
   *         value.
   * @throws NoMeasurementException if there aren't any measurements around the
   *         timestamp.
   */
  @SuppressWarnings("PMD.UnusedModifier")
  public Double getValue(Depository depository, Sensor sensor, Date timestamp)
      throws NoMeasurementException;

  /**
   * @param depository The Depository storing the measurements.
   * @param sensor The sensor making the measurements.
   * @param start The start of the period.
   * @param end The end of the period.
   * @return The value measured the difference between the end value and the
   *         start value.
   * @throws NoMeasurementException if there are no measurements around the
   *         start or end time.
   */
  @SuppressWarnings("PMD.UnusedModifier")
  public Double getValue(Depository depository, Sensor sensor, Date start, Date end)
      throws NoMeasurementException;

  /**
   * @param depository The Depository storing the measurements.
   * @param sensor The Sensor making the measurements.
   * @param start The start of the interval.
   * @param end The end of the interval
   * @param gapSeconds The maximum number of seconds that measurements need to
   *        be within the start and end.
   * @return The value measured the difference between the end value and the
   *         start value.
   * @throws NoMeasurementException if there are no measurements around the
   *         start or end time.
   * @throws MeasurementGapException if the measurements around start or end are
   *         too far apart.
   */
  @SuppressWarnings("PMD.UnusedModifier")
  public Double getValue(Depository depository, Sensor sensor, Date start, Date end, Long gapSeconds)
      throws NoMeasurementException, MeasurementGapException;

  /**
   * @param depository The Depository storing the measurements.
   * @param sensor The Sensor making the measurements.
   * @param timestamp The time of the value.
   * @param gapSeconds The maximum number of seconds that measurements need to
   *        be within the start and end.
   * @return The Value 'measured' at the given time, most likely an interpolated
   *         value.
   * @throws NoMeasurementException If there aren't any measurements around the
   *         time.
   * @throws MeasurementGapException if the measurements around timestamp are
   *         too far apart.
   */
  @SuppressWarnings("PMD.UnusedModifier")
  public Double getValue(Depository depository, Sensor sensor, Date timestamp, Long gapSeconds)
      throws NoMeasurementException, MeasurementGapException;

  /**
   * @param depository The Depository storing the measurements.
   * @param group The SensorGroup whose sensors are making the measurements.
   * @param timestamp The time for the measured value.
   * @return The Value 'measured' at the given time, most likely an interpolated
   *         value.
   * @throws NoMeasurementException if there aren't any measurements around the
   *         timestamp.
   */
  @SuppressWarnings("PMD.UnusedModifier")
  public Double getValue(Depository depository, SensorGroup group, Date timestamp)
      throws NoMeasurementException;

  /**
   * @param depository The Depository storing the measurements.
   * @param group The SensorGroup whose Sensors are making the measurements.
   * @param start The start of the period.
   * @param end The end of the period.
   * @return The value measured the difference between the end value and the
   *         start value.
   * @throws NoMeasurementException if there are no measurements around the
   *         start or end time.
   */
  @SuppressWarnings("PMD.UnusedModifier")
  public Double getValue(Depository depository, SensorGroup group, Date start, Date end)
      throws NoMeasurementException;

  /**
   * @param depository The Depository storing the measurements.
   * @param group The SensorGroup whose Sensors are making the measurements.
   * @param start The start of the interval.
   * @param end The end of the interval
   * @param gapSeconds The maximum number of seconds that measurements need to
   *        be within the start and end.
   * @return The value measured the difference between the end value and the
   *         start value.
   * @throws NoMeasurementException if there are no measurements around the
   *         start or end time.
   * @throws MeasurementGapException if the measurements around start or end are
   *         too far apart.
   */
  @SuppressWarnings("PMD.UnusedModifier")
  public Double getValue(Depository depository, SensorGroup group, Date start, Date end,
      Long gapSeconds) throws NoMeasurementException, MeasurementGapException;

  /**
   * @param depository The Depository storing the measurements.
   * @param group The SensorGroup whose Sensors are making the measurements.
   * @param timestamp The time of the value.
   * @param gapSeconds The maximum number of seconds that measurements need to
   *        be within the start and end.
   * @return The Value 'measured' at the given time, most likely an interpolated
   *         value.
   * @throws NoMeasurementException If there aren't any measurements around the
   *         time.
   * @throws MeasurementGapException if the measurements around timestamp are
   *         too far apart.
   */
  @SuppressWarnings("PMD.UnusedModifier")
  public Double getValue(Depository depository, SensorGroup group, Date timestamp, Long gapSeconds)
      throws NoMeasurementException, MeasurementGapException;

  /**
   * Checks to see if the given id is a defined CollectorProcessDefinition id.
   * 
   * @param id the id to check.
   * @return true if the id is a defined CollectorProcessDefinition's id.
   */
  @SuppressWarnings("PMD.UnusedModifier")
  public boolean isDefinedCollectorProcessDefinition(String id);

  /**
   * Checks to see if the given id is a defined Depository id.
   * 
   * @param id the id to check.
   * @return true if the id is a defined Depository's id.
   */
  @SuppressWarnings("PMD.UnusedModifier")
  public boolean isDefinedDepository(String id);

  /**
   * Checks to see if the given id is a defined MeasurementPruningDefinition id.
   * 
   * @param id the id to check.
   * @return true if the id is a defined MeasurementPruningDefinition's id.
   */
  @SuppressWarnings("PMD.UnusedModifier")
  public boolean isDefinedMeasurementPruningDefinition(String id);

  /**
   * Checks to see if the given id is a defined MeasurementType id.
   * 
   * @param id the id to check.
   * @return true if the id is a defined MeasurementType's id.
   */
  @SuppressWarnings("PMD.UnusedModifier")
  public boolean isDefinedMeasurementType(String id);

  /**
   * Checks to see if the given id is a defined Sensor id.
   * 
   * @param id the id to check.
   * @return true if the id is a defined Sensor's id.
   */
  @SuppressWarnings("PMD.UnusedModifier")
  public boolean isDefinedSensor(String id);

  /**
   * Checks to see if the given id is a defined SensorGroup id.
   * 
   * @param id the id to check.
   * @return true if the id is a defined SensorGroup's id.
   */
  @SuppressWarnings("PMD.UnusedModifier")
  public boolean isDefinedSensorGroup(String id);

  /**
   * Checks to see if the given id is a defined SensorModel id.
   * 
   * @param id the id to check.
   * @return true if the id is a defined SensorModel's id.
   */
  @SuppressWarnings("PMD.UnusedModifier")
  public boolean isDefinedSensorModel(String id);

  /**
   * Determines the health of the WattDepot server the client is communicating
   * with.
   * 
   * @return true if the server is healthy, false if cannot connect.
   */
  @SuppressWarnings("PMD.UnusedModifier")
  public boolean isHealthy();

  /**
   * Stores the given CollectorProcessDefinitionData in the WattDepot Server.
   * 
   * @param process the CollectorProcessDefinitionData.
   */
  @SuppressWarnings("PMD.UnusedModifier")
  public void putCollectorProcessDefinition(CollectorProcessDefinition process);

  /**
   * Stores the given Depository in the WattDepot Server.
   * 
   * @param depository the Depository.
   */
  @SuppressWarnings("PMD.UnusedModifier")
  public void putDepository(Depository depository);

  /**
   * @param depository The Depository to store the Measurement.
   * @param measurement The Measurement to store.
   * @throws MeasurementTypeException if the type of the measurement doesn't
   *         match the type of the depository.
   */
  @SuppressWarnings("PMD.UnusedModifier")
  public void putMeasurement(Depository depository, Measurement measurement)
      throws MeasurementTypeException;

  /**
   * Stores the given MeasurementPruningDefinition in the WattDepot Server.
   * 
   * @param gcd the MeasurementPruningDefinition.
   */
  @SuppressWarnings("PMD.UnusedModifier")
  public void putMeasurementPruningDefinition(MeasurementPruningDefinition gcd);

  /**
   * Stores the given MeasurementType in the WattDepot Server.
   * 
   * @param type the MeasurementType.
   */
  @SuppressWarnings("PMD.UnusedModifier")
  public void putMeasurementType(MeasurementType type);

  /**
   * Stores the given Sensor in the WattDepot Server.
   * 
   * @param sensor the Sensor.
   */
  @SuppressWarnings("PMD.UnusedModifier")
  public void putSensor(Sensor sensor);

  /**
   * Stores the given SensorGroup in the WattDepot Server.
   * 
   * @param group the SensorGroup.
   */
  @SuppressWarnings("PMD.UnusedModifier")
  public void putSensorGroup(SensorGroup group);

  /**
   * Stores the given SensorModel in the WattDepot Server.
   * 
   * @param model the SensorModel.
   */
  @SuppressWarnings("PMD.UnusedModifier")
  public void putSensorModel(SensorModel model);

  /**
   * Updates the given CollectorProcessDefinitionData in the WattDepot Server.
   * 
   * @param process The CollectorProcessDefinitionData to update.
   */
  @SuppressWarnings("PMD.UnusedModifier")
  public void updateCollectorProcessDefinition(CollectorProcessDefinition process);

  /**
   * Updates the given Depository in the WattDepot Server.
   * 
   * @param depository The Depository to update.
   */
  @SuppressWarnings("PMD.UnusedModifier")
  public void updateDepository(Depository depository);

  /**
   * Updates the given MeasurementPruningDefinition in the WattDepot Server.
   * 
   * @param gcd The MeasurementPruningDefinition to update.
   */
  @SuppressWarnings("PMD.UnusedModifier")
  public void updateMeasurementPruningDefinition(MeasurementPruningDefinition gcd);

  /**
   * Updates the given MeasurementType in the WattDepot Server.
   * 
   * @param type the MeasurementType to update.
   */
  @SuppressWarnings("PMD.UnusedModifier")
  public void updateMeasurementType(MeasurementType type);

  /**
   * Updates the given Sensor in the WattDepot Server.
   * 
   * @param sensor The Sensor to update.
   */
  @SuppressWarnings("PMD.UnusedModifier")
  public void updateSensor(Sensor sensor);

  /**
   * Updates the given SensorGroup in the WattDepot Server.
   * 
   * @param group The SensorGroup to update.
   */
  @SuppressWarnings("PMD.UnusedModifier")
  public void updateSensorGroup(SensorGroup group);

  /**
   * Updates the given SensorModel in the WattDepot Server.
   * 
   * @param model the SensorModel to update.
   */
  @SuppressWarnings("PMD.UnusedModifier")
  public void updateSensorModel(SensorModel model);
}
