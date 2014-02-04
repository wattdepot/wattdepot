/**
 * WattDepotPersistence.java created This file is part of WattDepot.
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
package org.wattdepot.server;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.wattdepot.common.domainmodel.CollectorProcessDefinition;
import org.wattdepot.common.domainmodel.Depository;
import org.wattdepot.common.domainmodel.InterpolatedValue;
import org.wattdepot.common.domainmodel.Measurement;
import org.wattdepot.common.domainmodel.MeasurementRateSummary;
import org.wattdepot.common.domainmodel.MeasurementType;
import org.wattdepot.common.domainmodel.Organization;
import org.wattdepot.common.domainmodel.Property;
import org.wattdepot.common.domainmodel.Sensor;
import org.wattdepot.common.domainmodel.SensorGroup;
import org.wattdepot.common.domainmodel.SensorMeasurementSummary;
import org.wattdepot.common.domainmodel.SensorModel;
import org.wattdepot.common.domainmodel.UserInfo;
import org.wattdepot.common.domainmodel.UserPassword;
import org.wattdepot.common.exception.BadSlugException;
import org.wattdepot.common.exception.IdNotFoundException;
import org.wattdepot.common.exception.MeasurementGapException;
import org.wattdepot.common.exception.MeasurementTypeException;
import org.wattdepot.common.exception.MisMatchedOwnerException;
import org.wattdepot.common.exception.NoMeasurementException;
import org.wattdepot.common.exception.UniqueIdException;
import org.wattdepot.common.util.SensorModelHelper;
import org.wattdepot.common.util.Slug;
import org.wattdepot.common.util.UnitsHelper;

/**
 * WattDepot persistence abstract interface. Different persistence
 * implementations should subclass this class and implement their persistence
 * schemes.
 * 
 * @author Cam Moore
 * 
 */
public abstract class WattDepotPersistence {

  /** The name of the Power MeasurementType. */
  public static final String POWER_TYPE_NAME = "Power";
  /** The name of the Energy MeasurementType. */
  public static final String ENERGY_TYPE_NAME = "Energy";

  private ServerProperties properties;

  /**
   * Defines a new CollectorProcessDefinition. This does not start any
   * processes.
   * 
   * @param id The unique id.
   * @param name The unique name.
   * @param sensorId The id of the Sensor to poll.
   * @param pollingInterval The polling interval.
   * @param depositoryId The id of the depository to use.
   * @param properties The properties associated with this
   *        CollectorProcessDefinition.
   * @param orgId the id of the owner of the CollectorProcessDefinition
   * @return The defined CollectorProcessDefinition.
   * @throws UniqueIdException if the id is already used for another
   *         CollectorProcessDefinintion.
   * @throws MisMatchedOwnerException if the given owner doesn't match the
   *         owners of the Sensor or Depository.
   * @throws IdNotFoundException if the sensorId or orgId are not defined.
   * @throws BadSlugException if the given id isn't valid.
   */
  public abstract CollectorProcessDefinition defineCollectorProcessDefinition(
      String id, String name, String sensorId, Long pollingInterval,
      String depositoryId, Set<Property> properties, String orgId)
      throws UniqueIdException, MisMatchedOwnerException, IdNotFoundException,
      BadSlugException;

  /**
   * Defines a new WattDepository in WattDepot.
   * 
   * @param id The unique id.
   * @param name The name.
   * @param measurementType the Measurement Type.
   * @param orgId the id of the owner of the WattDepository.
   * @return the defined WattDepository.
   * @throws UniqueIdException if the id is already used for another
   *         WattDepository.
   * @throws IdNotFoundException if the orgId is not defined.
   * @throws BadSlugException if the id isn't valid.
   */
  public abstract Depository defineDepository(String id, String name,
      MeasurementType measurementType, String orgId) throws UniqueIdException,
      IdNotFoundException, BadSlugException;

  /**
   * Defines a new MeasurementType in WattDepot.
   * 
   * @param id The unique id.
   * @param name the name of the MeasurementType.
   * @param units the units for the MeasurementType. Must be a
   *        javax.measure.unit.Unit toString() value.
   * @return the defined MeasurementType.
   * @throws UniqueIdException if the id derived from name is already defined.
   * @throws BadSlugException if the id isn't valid.
   */
  public abstract MeasurementType defineMeasurementType(String id, String name,
      String units) throws UniqueIdException, BadSlugException;

  /**
   * @param id The unique id.
   * @param name The unique name.
   * @param users The members of the group.
   * @return The defined Organization.
   * @throws UniqueIdException If the id is already used for another
   *         Organization.
   * @throws BadSlugException if the id isn't valid.
   * @throws IdNotFoundException if the user's are not defined.
   */
  public abstract Organization defineOrganization(String id, String name,
      Set<String> users) throws UniqueIdException, BadSlugException,
      IdNotFoundException;

  /**
   * @param id The unique id.
   * @param name The name of the sensor.
   * @param uri The URI for the sensor.
   * @param modelId The id of the SensorModel.
   * @param properties the properties associated with this Sensor.
   * @param orgId the id of the owner of the Sensor.
   * @return the defined Sensor.
   * @throws UniqueIdException if the id is already used for another Sensor.
   * @throws MisMatchedOwnerException if the given owner doesn't match the
   *         owners of the SensorModel.
   * @throws IdNotFoundException if modelId, or orgId are not actual Ids.
   * @throws BadSlugException if the id isn't valid.
   */
  public abstract Sensor defineSensor(String id, String name, String uri,
      String modelId, Set<Property> properties, String orgId)
      throws UniqueIdException, MisMatchedOwnerException, IdNotFoundException,
      BadSlugException;

  /**
   * @param id The unique id.
   * @param name The unique name.
   * @param sensors A set of the Sensors that make up the SensorGroup
   * @param orgId the owner of the SensorGroup.
   * @return the defined SensorGroup.
   * @throws UniqueIdException if the id is already used for another
   *         SensorGroup.
   * @throws MisMatchedOwnerException if the given owner doesn't match the
   *         owners of the Sensors.
   * @throws IdNotFoundException if sensorIds, or orgId are not actual Ids.
   * @throws BadSlugException id the id isn't valid.
   */
  public abstract SensorGroup defineSensorGroup(String id, String name,
      Set<String> sensors, String orgId) throws UniqueIdException,
      MisMatchedOwnerException, IdNotFoundException, BadSlugException;

  /**
   * Defines a new SensorModel in WattDepot.
   * 
   * @param id The unique id.
   * @param name The unique name.
   * @param protocol The protocol used by a meter.
   * @param type The type of the meter.
   * @param version The version the meter is using.
   * @return the defined SensorModel.
   * @throws UniqueIdException if the id is already used for another
   *         SensorModel.
   * @throws BadSlugException if the id isn't valid.
   */
  public abstract SensorModel defineSensorModel(String id, String name,
      String protocol, String type, String version) throws UniqueIdException,
      BadSlugException;

  /**
   * Defines a new UserInfo with the given information.
   * 
   * @param userId The unique userId.
   * @param firstName The user's name.
   * @param lastName The user's last name.
   * @param email The user's email address.
   * @param orgId The id of the user's organization.
   * @param properties The additional properties of the user.
   * @param password the password for the user.
   * @return The defined UserInfo.
   * @throws UniqueIdException if the id is already used for another UserInfo.
   * @throws IdNotFoundException Organization is not defined.
   */
  public abstract UserInfo defineUserInfo(String userId, String firstName,
      String lastName, String email, String orgId, Set<Property> properties,
      String password) throws UniqueIdException, IdNotFoundException;

  /**
   * Deletes the given CollectorProcessDefinition.
   * 
   * @param id The unique id of the CollectorProcessDefinition.
   * @param orgId the group id of the user making the request.
   * @throws IdNotFoundException If the id is not known or defined.
   * @throws MisMatchedOwnerException if the orgId doesn't match the owner of
   *         the sensor process.
   */
  public abstract void deleteCollectorProcessDefinition(String id, String orgId)
      throws IdNotFoundException, MisMatchedOwnerException;

  /**
   * Deletes the given WattDepository.
   * 
   * @param id The unique id of the WattDepository.
   * @param orgId the group id of the user making the request.
   * @throws IdNotFoundException If the id is not known or defined.
   * @throws MisMatchedOwnerException if the orgId doesn't match the owner of
   *         the sensor process.
   */
  public abstract void deleteDepository(String id, String orgId)
      throws IdNotFoundException, MisMatchedOwnerException;

  /**
   * @param depotId the id of the Depository storing the measurement.
   * @param orgId the id of the Organization.
   * @param measId The id of the measurement to delete.
   * @throws IdNotFoundException if there is a problem with the ids.
   */
  public abstract void deleteMeasurement(String depotId, String orgId,
      String measId) throws IdNotFoundException;

  /**
   * Deletes the given measurement type.
   * 
   * @param id The unique id for the MeasurementType to delete.
   * @throws IdNotFoundException if the id is not a known MeasurementType.
   */
  public abstract void deleteMeasurementType(String id)
      throws IdNotFoundException;

  /**
   * @param id The unique id of the Organization.
   * @throws IdNotFoundException If the id is not known or defined.
   */
  public abstract void deleteOrganization(String id) throws IdNotFoundException;

  /**
   * Deletes the given Sensor.
   * 
   * @param id The unique id of the Sensor.
   * @param orgId the group id of the user making the request.
   * @throws IdNotFoundException If the id is not known or defined.
   * @throws MisMatchedOwnerException if the orgId doesn't match the owner of
   *         the sensor.
   */
  public abstract void deleteSensor(String id, String orgId)
      throws IdNotFoundException, MisMatchedOwnerException;

  /**
   * Deletes the given SensorGroup.
   * 
   * @param id The unique id of the SensorGroup.
   * @param orgId the group id of the user making the request.
   * @throws IdNotFoundException If the id is not known or defined.
   * @throws MisMatchedOwnerException if the orgId doesn't match the owner of
   *         the sensor group.
   */
  public abstract void deleteSensorGroup(String id, String orgId)
      throws IdNotFoundException, MisMatchedOwnerException;

  /**
   * Deletes the given SensorModel.
   * 
   * @param id The unique id of the SensorModel.
   * @throws IdNotFoundException If the id is not known or defined.
   */
  public abstract void deleteSensorModel(String id) throws IdNotFoundException;

  /**
   * @param id The unique id of the User.
   * @param orgId the id of the organization the user is a member.
   * @throws IdNotFoundException If the id is not known or defined.
   */
  public abstract void deleteUser(String id, String orgId)
      throws IdNotFoundException;

  /**
   * @param userId The id of the UserPassword to delete.
   * @param orgId the user's organization id.
   * @throws IdNotFoundException If the id is not known or defined.
   */
  public abstract void deleteUserPassword(String userId, String orgId)
      throws IdNotFoundException;

  /**
   * @param id The unique id for the CollectorProcessDefinition.
   * @param orgId the group id of the user making the request.
   * @return The CollectorProcessDefinition with the given id.
   * @throws IdNotFoundException if either id is not defined.
   */
  public abstract CollectorProcessDefinition getCollectorProcessDefinition(
      String id, String orgId) throws IdNotFoundException;

  /**
   * @param orgId the id of the owner Organization.
   * @return A list of the defined CollectorProcessDefinition Ids.
   * @throws IdNotFoundException if the orgId is not defined.
   */
  public abstract List<String> getCollectorProcessDefinitionIds(String orgId)
      throws IdNotFoundException;

  /**
   * @param orgId the group id of the user making the request.
   * @return The known/defined CollectorProcessDefinitiones owned by the given
   *         group id.
   * @throws IdNotFoundException if the orgId is not defined.
   */
  public abstract List<CollectorProcessDefinition> getCollectorProcessDefinitions(
      String orgId) throws IdNotFoundException;

  /**
   * @param orgId the group id of the user making the request.
   * @return The known/defined WattDepositories owned by the given group id.
   * @throws IdNotFoundException if orgId is not defined.
   */
  public abstract List<Depository> getDepositories(String orgId)
      throws IdNotFoundException;

  /**
   * @param id The unique id for the Depository to get.
   * @param orgId the id of the owner's Organization.
   * @return The WattDepository with the given id.
   * @throws IdNotFoundException if either id is not defined.
   */
  public abstract Depository getDepository(String id, String orgId)
      throws IdNotFoundException;

  /**
   * @param orgId the id of the owner UserGroup.
   * @return A list of the defined WattDepository Ids.
   * @throws IdNotFoundException if orgId is not defined.
   */
  public abstract List<String> getDepositoryIds(String orgId)
      throws IdNotFoundException;

  /**
   * @param depotId the id of the Depository.
   * @param orgId the Organziation's id.
   * @param sensorId The id of the Sensor making the measurements.
   * @return The earliest measurement Value
   * @throws NoMeasurementException If there aren't any measurements around the
   *         time.
   * @throws IdNotFoundException if there is a problem with the ids.
   */
  public abstract InterpolatedValue getEarliestMeasuredValue(String depotId,
      String orgId, String sensorId) throws NoMeasurementException,
      IdNotFoundException;

  /**
   * @param depotId the id of the Depository.
   * @param orgId the Organziation's id.
   * @param sensorId The id of the Sensor making the measurements.
   * @return The latest measurement Value
   * @throws NoMeasurementException If there aren't any measurements around the
   *         time.
   * @throws IdNotFoundException if there is a problem with the ids.
   */
  public abstract InterpolatedValue getLatestMeasuredValue(String depotId,
      String orgId, String sensorId) throws NoMeasurementException,
      IdNotFoundException;

  /**
   * @param depotId the id of the Depository storing the measurements.
   * @param orgId the Organziation's id.
   * @param measId The measurement id.
   * @return The Measurement with the given id or null.
   * @throws IdNotFoundException if there are problems with the ids.
   */
  public abstract Measurement getMeasurement(String depotId, String orgId,
      String measId) throws IdNotFoundException;

  /**
   * @param depotId the id of the Depository storing the measurements.
   * @param orgId the Organziation's id.
   * @param sensorId the id of the Sensor.
   * @return A list of all the measurements made by the Sensor.
   * @throws IdNotFoundException if there is a problem with the ids.
   */
  public abstract List<Measurement> getMeasurements(String depotId,
      String orgId, String sensorId) throws IdNotFoundException;

  /**
   * @param depotId the id of the Depository storing the measurements.
   * @param orgId the Organziation's id.
   * @param sensorId The id of the Sensor.
   * @param start The start of the interval.
   * @param end The end of the interval.
   * @return A list of the measurements in the interval.
   * @throws IdNotFoundException if there is a problem with the ids.
   */
  public abstract List<Measurement> getMeasurements(String depotId,
      String orgId, String sensorId, Date start, Date end)
      throws IdNotFoundException;

  /**
   * @param id The unique id for the MeasurementType.
   * @return The MeasurementType with the given id.
   * @throws IdNotFoundException if the id is not defined.
   */
  public abstract MeasurementType getMeasurementType(String id)
      throws IdNotFoundException;

  /**
   * @return A List of the defined MeasurementTypes.
   */
  public abstract List<MeasurementType> getMeasurementTypes();

  /**
   * @param id the unique id for the Organization.
   * @return The UserGroup with the given id.
   * @throws IdNotFoundException if the id isn't a defined Organization's id.
   */
  public abstract Organization getOrganization(String id)
      throws IdNotFoundException;

  /**
   * @return A list of the defined organization Ids.
   */
  public abstract List<String> getOrganizationIds();

  /**
   * @return The known/defined Organizations.
   */
  public abstract List<Organization> getOrganizations();

  /**
   * @param id The unique id for the Sensor.
   * @param orgId the group id of the user making the request.
   * @return The Sensor with the given id.
   * @throws MisMatchedOwnerException if the owners of the pieces don't match.
   * @throws IdNotFoundException if the ids are not defined.
   */
  public abstract Sensor getSensor(String id, String orgId)
      throws MisMatchedOwnerException, IdNotFoundException;

  /**
   * @param id The unique id for the SensorGroup.
   * @param orgId the group id of the user making the request.
   * @return The SensorGroup with the given id.
   * @throws IdNotFoundException if the ids are not defined.
   */
  public abstract SensorGroup getSensorGroup(String id, String orgId)
      throws IdNotFoundException;

  /**
   * @param orgId the id of the owner UserGroup.
   * @return A list of the defined SensorGroup Ids.
   * @throws IdNotFoundException if orgId is not defined.
   */
  public abstract List<String> getSensorGroupIds(String orgId)
      throws IdNotFoundException;

  /**
   * @param orgId the id of the Organization owning the SensorGroups.
   * @return The known/defined SensorGroups owned by the given Organization id.
   * @throws IdNotFoundException if the orgId is not defined.
   */
  public abstract List<SensorGroup> getSensorGroups(String orgId)
      throws IdNotFoundException;

  /**
   * @param orgId the id of the owner UserGroup.
   * @return A list of the defined Sensor Ids.
   * @throws IdNotFoundException if orgId is not defined.
   */
  public abstract List<String> getSensorIds(String orgId)
      throws IdNotFoundException;

  /**
   * @param id The unique id for the SensorModel.
   * @return The SensorModel with the given id.
   * @throws IdNotFoundException if id isn't a defined SensorModel id.
   */
  public abstract SensorModel getSensorModel(String id)
      throws IdNotFoundException;

  /**
   * @return A list of the defined SensorModel Ids.
   */
  public abstract List<String> getSensorModelIds();

  /**
   * @return The known/defined SensorModels owned by the given group id.
   */
  public abstract List<SensorModel> getSensorModels();

  /**
   * @param orgId the group id of the user making the request.
   * @return The known/defined Sensors owned by the given group id.
   * @throws IdNotFoundException if orgId is not defined.
   */
  public abstract List<Sensor> getSensors(String orgId)
      throws IdNotFoundException;

  /**
   * @return the properties
   */
  public ServerProperties getServerProperties() {
    return properties;
  }

  /**
   * @return the sessionClose
   */
  public int getSessionClose() {
    throw new RuntimeException("Not implemented.");
  }

  /**
   * @return the sessionOpen
   */
  public int getSessionOpen() {
    throw new RuntimeException("Not implemented.");
  }

  /**
   * @param depotId The depository id.
   * @param orgId The organization id.
   * @param sensorId The Sensor id.
   * @param start The start of the period.
   * @param end The end of the period.
   * @return The SensorMeasurementSummary for the given sensor, depository and
   *         period.
   * @throws IdNotFoundException if there is a problem with the ids.
   */
  public abstract SensorMeasurementSummary getSummary(String depotId,
      String orgId, String sensorId, Date start, Date end)
      throws IdNotFoundException;

  /**
   * @param depotId The depository id.
   * @param orgId The organization id.
   * @param sensorId The Sensor id.
   * @return The current MeasurementRateSummary.
   * @throws IdNotFoundException if there is a problem with the ids.
   */
  public abstract MeasurementRateSummary getRateSummary(String depotId,
      String orgId, String sensorId) throws IdNotFoundException;

  /**
   * @param id the unique id for the UserInfo.
   * @param orgId the id of the organization the user is in.
   * @return The UserInfo with the given id.
   * @throws IdNotFoundException if the combination of id and orgId isn't a
   *         defined UserInfo.
   */
  public abstract UserInfo getUser(String id, String orgId)
      throws IdNotFoundException;

  /**
   * @param orgId the id of the organization the user is in.
   * @return A list of the defined user Ids.
   * @throws IdNotFoundException if orgId is not defined.
   */
  public abstract List<String> getUserIds(String orgId)
      throws IdNotFoundException;

  /**
   * @param id The user's id.
   * @param orgId the id of the organization the user is in.
   * @return the UserPassword instance associated with the user.
   * @throws IdNotFoundException if id and orgId aren't a defined UserPassword.
   */
  public abstract UserPassword getUserPassword(String id, String orgId)
      throws IdNotFoundException;

  /**
   * @return All the known/defined UserInfos.
   */
  public abstract List<UserInfo> getUsers();

  /**
   * @param orgId the id of the organization the user is in.
   * @return The known/defined UserInfos in the given organization.
   * @throws IdNotFoundException if orgId is not defined.
   */
  public abstract List<UserInfo> getUsers(String orgId)
      throws IdNotFoundException;

  /**
   * @param depotId the id of the depository.
   * @param orgId the Organziation's id.
   * @param sensorId The id of the Sensor making the measurements.
   * @param timestamp The time of the value.
   * @return The Value 'measured' at the given time, most likely an interpolated
   *         value.
   * @throws NoMeasurementException If there aren't any measurements around the
   *         time.
   * @throws IdNotFoundException if there is a problem with the ids.
   */
  public abstract Double getValue(String depotId, String orgId,
      String sensorId, Date timestamp) throws NoMeasurementException,
      IdNotFoundException;

  /**
   * @param depotId the id of the depository.
   * @param orgId the Organziation's id.
   * @param sensorId The id of the Sensor making the measurements.
   * @param start The start of the period.
   * @param end The end of the period.
   * @return The value measured the difference between the end value and the
   *         start value.
   * @throws NoMeasurementException if there are no measurements around the
   *         start or end time.
   * @throws IdNotFoundException if there is a problem with the ids.
   */
  public abstract Double getValue(String depotId, String orgId,
      String sensorId, Date start, Date end) throws NoMeasurementException,
      IdNotFoundException;

  /**
   * @param depotId the id of the depository.
   * @param orgId the Organziation's id.
   * @param sensorId The id of the Sensor making the measurements.
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
   * @throws IdNotFoundException if there is a problem with the ids.
   */
  public abstract Double getValue(String depotId, String orgId,
      String sensorId, Date start, Date end, Long gapSeconds)
      throws NoMeasurementException, MeasurementGapException,
      IdNotFoundException;

  /**
   * @param depotId the id of the depository.
   * @param orgId the Organziation's id.
   * @param sensorId The id of the Sensor making the measurements.
   * @param timestamp The time of the value.
   * @param gapSeconds The maximum number of seconds that measurements need to
   *        be within the start and end.
   * @return The Value 'measured' at the given time, most likely an interpolated
   *         value.
   * @throws NoMeasurementException If there aren't any measurements around the
   *         time.
   * @throws MeasurementGapException if the measurements around timestamp are
   *         too far apart.
   * @throws IdNotFoundException if there is a problem with the ids.
   */
  public abstract Double getValue(String depotId, String orgId,
      String sensorId, Date timestamp, Long gapSeconds)
      throws NoMeasurementException, MeasurementGapException,
      IdNotFoundException;

  /**
   * Ensures the base set of MeasurementTypes are defined in WattDepot.
   */
  public void initializeMeasurementTypes() {
    for (String key : UnitsHelper.quantities.keySet()) {
      try {
        getMeasurementType(Slug.slugify(key));
      }
      catch (IdNotFoundException e1) {
        try {
          defineMeasurementType(Slug.slugify(key), key, UnitsHelper.quantities
              .get(key).toString());
        }
        catch (UniqueIdException e) {
          e.printStackTrace();
        }
        catch (BadSlugException e) {
          e.printStackTrace();
        }
      }
    }
  }

  /**
   * Ensures the base set of SensorModels are defined in WattDepot.
   */
  public void initializeSensorModels() {
    for (String key : SensorModelHelper.models.keySet()) {
      try {
        getSensorModel(Slug.slugify(key));
      }
      catch (IdNotFoundException e1) {
        SensorModel model = SensorModelHelper.models.get(key);
        try {
          defineSensorModel(Slug.slugify(model.getName()), model.getName(),
              model.getProtocol(), model.getType(), model.getVersion());
        }
        catch (UniqueIdException e) {
          e.printStackTrace();
        }
        catch (BadSlugException e) {
          e.printStackTrace();
        }
      }
    }

  }

  /**
   * @param depotId the id of the depository.
   * @param orgId the id of the organization.
   * @return A list of the Sensor ids contributing Measurements to the given
   *         depository.
   * @throws IdNotFoundException if there is a problem with the ids.
   */
  public abstract List<String> listSensors(String depotId, String orgId)
      throws IdNotFoundException;

  /**
   * @param depotId the id of the Depository.
   * @param orgId the Organization's id.
   * @param meas The measurement to store.
   * @throws MeasurementTypeException if the type of the measurement doesn't
   *         match the Depository measurement type.
   * @throws IdNotFoundException if there is a problem with the ids.
   */
  public abstract void putMeasurement(String depotId, String orgId,
      Measurement meas) throws MeasurementTypeException, IdNotFoundException;

  /**
   * @param properties the properties to set
   */
  public void setServerProperties(ServerProperties properties) {
    this.properties = properties;
  }

  /**
   * Cleans up the persistence layer.
   */
  public abstract void stop();

  /**
   * Updates the given sensor process in the persistent store.
   * 
   * @param process The updated CollectorProcessDefinition.
   * @return The updated process from persistence.
   * @throws IdNotFoundException if there is a problem with the ids in the
   *         CollectorProcessDefinition.
   */
  public abstract CollectorProcessDefinition updateCollectorProcessDefinition(
      CollectorProcessDefinition process) throws IdNotFoundException;

  /**
   * Updates the given measurement type in the persistent store.
   * 
   * @param type The updated MeasurementType.
   * @return The updated MeasurementType from persistence.
   */
  public abstract MeasurementType updateMeasurementType(MeasurementType type);

  /**
   * @param org The updated Organization.
   * @return The updated organization from persistence.
   * @throws IdNotFoundException if the given Organization is not defined.
   */
  public abstract Organization updateOrganization(Organization org)
      throws IdNotFoundException;

  /**
   * Updates the given sensor in the persistent store.
   * 
   * @param sensor The updated Sensor.
   * @return The updated sensor from persistence.
   * @throws IdNotFoundException if there are problems with the ids.
   */
  public abstract Sensor updateSensor(Sensor sensor) throws IdNotFoundException;

  /**
   * Updates the given sensor group in the persistent store.
   * 
   * @param group The updated SensorGroup.
   * @return The updated sensor group from persistence.
   * @throws IdNotFoundException if there is a problem with the ids.
   */
  public abstract SensorGroup updateSensorGroup(SensorGroup group)
      throws IdNotFoundException;

  /**
   * Updates the given sensor model in the persistent store.
   * 
   * @param model The updated SensorModel.
   * @return The updated model from persistence.
   * @throws IdNotFoundException if the SensorModel isn't in persistence.
   */
  public abstract SensorModel updateSensorModel(SensorModel model)
      throws IdNotFoundException;

  /**
   * @param user The updated UserInfo.
   * @return The updated user from persistence.
   * @throws IdNotFoundException if the user isn't in the persistence.
   */
  public abstract UserInfo updateUserInfo(UserInfo user)
      throws IdNotFoundException;

  /**
   * @param password The UserPassword to update.
   * @return The updated password from persistence.
   * @throws IdNotFoundException if the password is not in persistence.
   */
  public abstract UserPassword updateUserPassword(UserPassword password)
      throws IdNotFoundException;
}
