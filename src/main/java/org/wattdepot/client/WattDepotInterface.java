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
import org.wattdepot.common.domainmodel.Measurement;
import org.wattdepot.common.domainmodel.MeasurementList;
import org.wattdepot.common.domainmodel.MeasurementType;
import org.wattdepot.common.domainmodel.MeasurementTypeList;
import org.wattdepot.common.domainmodel.Sensor;
import org.wattdepot.common.domainmodel.SensorGroup;
import org.wattdepot.common.domainmodel.SensorGroupList;
import org.wattdepot.common.domainmodel.SensorList;
import org.wattdepot.common.domainmodel.SensorLocation;
import org.wattdepot.common.domainmodel.SensorLocationList;
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
   * Deletes the given Depository.
   * 
   * @param depository
   *          the Depository to delete.
   * @throws IdNotFoundException
   *           if the Depository is not found in the server.
   */
  public void deleteDepository(Depository depository)
      throws IdNotFoundException;

  /**
   * Deletes the given Location.
   * 
   * @param sensorLocation
   *          the Location to delete.
   * @throws IdNotFoundException
   *           if the Location is not found in the server.
   */
  public void deleteLocation(SensorLocation sensorLocation)
      throws IdNotFoundException;

  /**
   * Deletes the given measurement from the given depository.
   * 
   * @param depository
   *          the Depository that stores the measurement.
   * @param measurement
   *          the Measurement to delete.
   * @throws IdNotFoundException
   *           if the Measurement is not found in the Depository.
   */
  public void deleteMeasurement(Depository depository, Measurement measurement)
      throws IdNotFoundException;

  /**
   * Deletes the given MeasurementType.
   * 
   * @param type
   *          the measurement type to delete.
   * @throws IdNotFoundException
   *           if the MeasurementType is not found in the Depository.
   */
  public void deleteMeasurementType(MeasurementType type)
      throws IdNotFoundException;

  /**
   * Deletes the given Sensor.
   * 
   * @param sensor
   *          the Sensor to delete.
   * @throws IdNotFoundException
   *           if the Sensor is not found in the server.
   */
  public void deleteSensor(Sensor sensor) throws IdNotFoundException;

  /**
   * Deletes the given SensorGroup.
   * 
   * @param group
   *          the SensorGroup to delete.
   * @throws IdNotFoundException
   *           if the SensorGroup is not found in the server.
   */
  public void deleteSensorGroup(SensorGroup group) throws IdNotFoundException;

  /**
   * Deletes the given SensorModel.
   * 
   * @param model
   *          the SensorModel to delete.
   * @throws IdNotFoundException
   *           if the SensorModel is not found in the server.
   */
  public void deleteSensorModel(SensorModel model) throws IdNotFoundException;

  /**
   * Deletes the given CollectorMetaData.
   * 
   * @param process
   *          the CollectorMetaData to delete.
   * @throws IdNotFoundException
   *           if the CollectorMetaData is not found in the server.
   */
  public void deleteCollectorMetaData(CollectorProcessDefinition process)
      throws IdNotFoundException;

  /**
   * @return The defined Depositories.
   */
  public DepositoryList getDepositories();

  /**
   * Retrieves the Depository with the given id from the WattDepot Server.
   * 
   * @param id
   *          The Depository's id.
   * @return the Depository with the given id or null.
   * @exception IdNotFoundException
   *              if the given id is not a Depository's id.
   */
  public Depository getDepository(String id) throws IdNotFoundException;

  /**
   * Retrieves the Location with the given id from the WattDepot Server.
   * 
   * @param id
   *          The Location's id.
   * @return the Location with the given id or null.
   * @exception IdNotFoundException
   *              if the given id is not a Location's id.
   */
  public SensorLocation getLocation(String id) throws IdNotFoundException;

  /**
   * @return The defined Locations.
   */
  public SensorLocationList getLocations();

  /**
   * @param depository
   *          The Depository storing the Measurements.
   * @param sensor
   *          The Sensor that made the measurements.
   * @param start
   *          The start time.
   * @param end
   *          The end time.
   * @return The Measurements stored in the depository made by the sensor
   *         between start and end.
   */
  public MeasurementList getMeasurements(Depository depository, Sensor sensor,
      Date start, Date end);

  /**
   * @return The defined MeasurementTypes.
   */
  public MeasurementTypeList getMeasurementTypes();

  /**
   * @param id
   *          the unique id for the MeasurementType.
   * @return The MeasurementType.
   * @exception IdNotFoundException
   *              if the given id is not a MeasurementType's id.
   */
  public MeasurementType getMeasurementType(String id)
      throws IdNotFoundException;

  /**
   * Retrieves the Sensor with the given id from the WattDepot Server.
   * 
   * @param id
   *          The Sensor's id.
   * @return the Sensor with the given id or null.
   * @exception IdNotFoundException
   *              if the given id is not a Sensor's id.
   */
  public Sensor getSensor(String id) throws IdNotFoundException;

  /**
   * Retrieves the SensorGroup with the given id from the WattDepot Server.
   * 
   * @param id
   *          The SensorGroup's id.
   * @return the SensorGroup with the given id or null.
   * @exception IdNotFoundException
   *              if the given id is not a SensorGroup's id.
   */
  public SensorGroup getSensorGroup(String id) throws IdNotFoundException;

  /**
   * @return The defined SensorGroups.
   */
  public SensorGroupList getSensorGroups();

  /**
   * Retrieves the SensorModel with the given id from the WattDepot Server.
   * 
   * @param id
   *          The SensorModel's id.
   * @return the SensorModel with the given id or null.
   * @exception IdNotFoundException
   *              if the given id is not a SensorModel's id.
   */
  public SensorModel getSensorModel(String id) throws IdNotFoundException;

  /**
   * @return The defined SensorModels.
   */
  public SensorModelList getSensorModels();

  /**
   * Retrieves the CollectorMetaData with the given id from the WattDepot
   * Server.
   * 
   * @param id
   *          The CollectorMetaData's id.
   * @return the CollectorMetaData with the given id or null.
   * @exception IdNotFoundException
   *              if the given id is not a CollectorMetaData's id.
   */
  public CollectorProcessDefinition getCollectorMetaData(String id)
      throws IdNotFoundException;

  /**
   * @return The defined CollectorMetaDatas.
   */
  public CollectorProcessDefinitionList getCollectorMetaDatas();

  /**
   * @return The defined Sensors.
   */
  public SensorList getSensors();

  /**
   * @param depository
   *          The Depository storing the measurements.
   * @param sensor
   *          The sensor making the measurements.
   * @param timestamp
   *          The time for the measured value.
   * @return The Value 'measured' at the given time, most likely an interpolated
   *         value.
   * @throws NoMeasurementException
   *           if there aren't any measurements around the timestamp.
   */
  public Double getValue(Depository depository, Sensor sensor, Date timestamp)
      throws NoMeasurementException;

  /**
   * @param depository
   *          The Depository storing the measurements.
   * @param sensor
   *          The sensor making the measurements.
   * @param start
   *          The start of the period.
   * @param end
   *          The end of the period.
   * @return The value measured the difference between the end value and the
   *         start value.
   * @throws NoMeasurementException
   *           if there are no measurements around the start or end time.
   */
  public Double getValue(Depository depository, Sensor sensor, Date start,
      Date end) throws NoMeasurementException;

  /**
   * @param depository
   *          The Depository storing the measurements.
   * @param sensor
   *          The Sensor making the measurements.
   * @param start
   *          The start of the interval.
   * @param end
   *          The end of the interval
   * @param gapSeconds
   *          The maximum number of seconds that measurements need to be within
   *          the start and end.
   * @return The value measured the difference between the end value and the
   *         start value.
   * @throws NoMeasurementException
   *           if there are no measurements around the start or end time.
   * @throws MeasurementGapException
   *           if the measurements around start or end are too far apart.
   */
  public Double getValue(Depository depository, Sensor sensor, Date start,
      Date end, Long gapSeconds) throws NoMeasurementException,
      MeasurementGapException;

  /**
   * @param depository
   *          The Depository storing the measurements.
   * @param sensor
   *          The Sensor making the measurements.
   * @param timestamp
   *          The time of the value.
   * @param gapSeconds
   *          The maximum number of seconds that measurements need to be within
   *          the start and end.
   * @return The Value 'measured' at the given time, most likely an interpolated
   *         value.
   * @throws NoMeasurementException
   *           If there aren't any measurements around the time.
   * @throws MeasurementGapException
   *           if the measurements around timestamp are too far apart.
   */
  public Double getValue(Depository depository, Sensor sensor, Date timestamp,
      Long gapSeconds) throws NoMeasurementException, MeasurementGapException;

  /**
   * Determines the health of the WattDepot server the client is communicating
   * with.
   * 
   * @return true if the server is healthy, false if cannot connect.
   */
  public boolean isHealthy();

  /**
   * Stores the given Depository in the WattDepot Server.
   * 
   * @param depository
   *          the Depository.
   */
  public void putDepository(Depository depository);

  /**
   * Stores the given Location in the WattDepot Server.
   * 
   * @param loc
   *          the Location.
   */
  public void putLocation(SensorLocation loc);

  /**
   * @param depository
   *          The Depository to store the Measurement.
   * @param measurement
   *          The Measurement to store.
   * @throws MeasurementTypeException
   *           if the type of the measurement doesn't match the type of the
   *           depository.
   */
  public void putMeasurement(Depository depository, Measurement measurement)
      throws MeasurementTypeException;

  /**
   * Stores the given MeasurementType in the WattDepot Server.
   * 
   * @param type
   *          the MeasurementType.
   */
  public void putMeasurementType(MeasurementType type);

  /**
   * Stores the given Sensor in the WattDepot Server.
   * 
   * @param sensor
   *          the Sensor.
   */
  public void putSensor(Sensor sensor);

  /**
   * Stores the given SensorGroup in the WattDepot Server.
   * 
   * @param group
   *          the SensorGroup.
   */
  public void putSensorGroup(SensorGroup group);

  /**
   * Stores the given SensorModel in the WattDepot Server.
   * 
   * @param model
   *          the SensorModel.
   */
  public void putSensorModel(SensorModel model);

  /**
   * Stores the given CollectorMetaData in the WattDepot Server.
   * 
   * @param process
   *          the CollectorMetaData.
   */
  public void putCollectorMetaData(CollectorProcessDefinition process);

  /**
   * Updates the given Depository in the WattDepot Server.
   * 
   * @param depository
   *          The Depository to update.
   */
  public void updateDepository(Depository depository);

  /**
   * Updates the given Location in the WattDepot Server.
   * 
   * @param sensorLocation
   *          the Location to update.
   */
  public void updateLocation(SensorLocation sensorLocation);

  /**
   * Updates the given MeasurementType in the WattDepot Server.
   * 
   * @param type
   *          the MeasurementType to update.
   */
  public void updateMeasurementType(MeasurementType type);

  /**
   * Updates the given Sensor in the WattDepot Server.
   * 
   * @param sensor
   *          The Sensor to update.
   */
  public void updateSensor(Sensor sensor);

  /**
   * Updates the given SensorGroup in the WattDepot Server.
   * 
   * @param group
   *          The SensorGroup to update.
   */
  public void updateSensorGroup(SensorGroup group);

  /**
   * Updates the given SensorModel in the WattDepot Server.
   * 
   * @param model
   *          the SensorModel to update.
   */
  public void updateSensorModel(SensorModel model);

  /**
   * Updates the given CollectorMetaData in the WattDepot Server.
   * 
   * @param process
   *          The CollectorMetaData to update.
   */
  public void updateCollectorMetaData(CollectorProcessDefinition process);
}
