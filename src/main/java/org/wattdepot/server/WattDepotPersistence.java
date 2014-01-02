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

import java.util.List;
import java.util.Set;

import org.wattdepot.common.domainmodel.CollectorProcessDefinition;
import org.wattdepot.common.domainmodel.Depository;
import org.wattdepot.common.domainmodel.MeasurementType;
import org.wattdepot.common.domainmodel.Property;
import org.wattdepot.common.domainmodel.Sensor;
import org.wattdepot.common.domainmodel.SensorGroup;
import org.wattdepot.common.domainmodel.SensorLocation;
import org.wattdepot.common.domainmodel.SensorModel;
import org.wattdepot.common.domainmodel.Organization;
import org.wattdepot.common.domainmodel.UserInfo;
import org.wattdepot.common.domainmodel.UserPassword;
import org.wattdepot.common.exception.IdNotFoundException;
import org.wattdepot.common.exception.MisMatchedOwnerException;
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
   * @param name
   *          The unique name.
   * @param sensorId
   *          The id of the Sensor to poll.
   * @param pollingInterval
   *          The polling interval.
   * @param depositoryId
   *          The id of the depository to use.
   * @param ownerId
   *          the id of the owner of the CollectorProcessDefinition
   * @return The defined CollectorProcessDefinition.
   * @throws UniqueIdException
   *           if the id is already used for another
   *           CollectorProcessDefinintion.
   * @throws MisMatchedOwnerException
   *           if the given owner doesn't match the owners of the Sensor or
   *           Depository.
   * @throws IdNotFoundException
   *           if the sensorId or ownerId are not defined.
   */
  public abstract CollectorProcessDefinition defineCollectorProcessDefinition(String name,
      String sensorId, Long pollingInterval, String depositoryId, String ownerId)
      throws UniqueIdException, MisMatchedOwnerException, IdNotFoundException;

  /**
   * Defines a new Location in WattDepot.
   * 
   * @param name
   *          The unique name.
   * @param latitude
   *          The decimal Latitude.
   * @param longitude
   *          The decimal Longitude.
   * @param altitude
   *          The altitude in meters w.r.t. MSL.
   * @param description
   *          A String description of the Location.
   * @param ownerId
   *          The id of the owner of the Location.
   * @return the defined Location.
   * @throws UniqueIdException
   *           if the id is already used for another Location.
   */
  public abstract SensorLocation defineLocation(String name, Double latitude, Double longitude,
      Double altitude, String description, String ownerId) throws UniqueIdException;

  /**
   * Defines a new MeasurementType in WattDepot.
   * 
   * @param name
   *          the name of the MeasurementType.
   * @param units
   *          the units for the MeasurementType. Must be a
   *          javax.measure.unit.Unit toString() value.
   * @return the defined MeasurementType.
   * @throws UniqueIdException
   *           if the slug derived from name is already defined.
   */
  public abstract MeasurementType defineMeasurementType(String name, String units)
      throws UniqueIdException;

  /**
   * @param name
   *          The unique name.
   * @param users
   *          The members of the group.
   * @return The defined Organization.
   * @throws UniqueIdException
   *           If the id is already used for another Organization.
   */
  public abstract Organization defineOrganization(String name, Set<String> users)
      throws UniqueIdException;

  /**
   * @param name
   *          The name of the sensor.
   * @param uri
   *          The URI for the sensor.
   * @param locationId
   *          The id of the location of the sensor
   * @param modelId
   *          The id of the SensorModel.
   * @param ownerId
   *          the id of the owner of the Sensor.
   * @return the defined Sensor.
   * @throws UniqueIdException
   *           if the id is already used for another Sensor.
   * @throws MisMatchedOwnerException
   *           if the given owner doesn't match the owners of the Location or
   *           SensorModel.
   * @throws IdNotFoundException
   *           if locationId, modelId, or ownerId are not actual Ids.
   */
  public abstract Sensor defineSensor(String name, String uri, String locationId, String modelId,
      String ownerId) throws UniqueIdException, MisMatchedOwnerException, IdNotFoundException;

  /**
   * @param name
   *          The unique name.
   * @param sensors
   *          A set of the Sensors that make up the SensorGroup
   * @param ownerId
   *          the owner of the SensorGroup.
   * @return the defined SensorGroup.
   * @throws UniqueIdException
   *           if the id is already used for another SensorGroup.
   * @throws MisMatchedOwnerException
   *           if the given owner doesn't match the owners of the Sensors.
   * @throws IdNotFoundException
   *           if locationId, modelId, or ownerId are not actual Ids.
   */
  public abstract SensorGroup defineSensorGroup(String name, Set<String> sensors, String ownerId)
      throws UniqueIdException, MisMatchedOwnerException, IdNotFoundException;

  /**
   * Defines a new SensorModel in WattDepot.
   * 
   * @param name
   *          The unique name.
   * @param protocol
   *          The protocol used by a meter.
   * @param type
   *          The type of the meter.
   * @param version
   *          The version the meter is using.
   * @return the defined SensorModel.
   * @throws UniqueIdException
   *           if the id is already used for another SensorModel.
   */
  public abstract SensorModel defineSensorModel(String name, String protocol, String type,
      String version) throws UniqueIdException;

  /**
   * Defines a new UserInfo with the given information.
   * 
   * @param userId
   *          The unique userId.
   * @param firstName
   *          The user's name.
   * @param lastName
   *          The user's last name.
   * @param email
   *          The user's email address.
   * @param orgId
   *          The id of the user's organization.
   * @param properties
   *          The additional properties of the user.
   * @return The defined UserInfo.
   * @throws UniqueIdException
   *           if the id is already used for another UserInfo.
   */
  public abstract UserInfo defineUserInfo(String userId, String firstName, String lastName,
      String email, String orgId, Set<Property> properties) throws UniqueIdException;

  /**
   * @param userId
   *          The unique userId.
   * @param orgId
   *          the user's organization id.
   * @param password
   *          The user's password.
   * @return The defined UserPassword.
   * @throws UniqueIdException
   *           if the id is already used for another UserInfo.
   */
  public abstract UserPassword defineUserPassword(String userId, String orgId, String password)
      throws UniqueIdException;

  /**
   * Defines a new WattDepository in WattDepot.
   * 
   * @param name
   *          The name.
   * @param measurementType
   *          the Measurement Type.
   * @param ownerId
   *          the id of the owner of the WattDepository.
   * @return the defined WattDepository.
   * @throws UniqueIdException
   *           if the id is already used for another WattDepository.
   */
  public abstract Depository defineWattDepository(String name, MeasurementType measurementType,
      String ownerId) throws UniqueIdException;

  /**
   * Deletes the given CollectorProcessDefinition.
   * 
   * @param slug
   *          The unique slug of the CollectorProcessDefinition.
   * @param groupId
   *          the group id of the user making the request.
   * @throws IdNotFoundException
   *           If the id is not known or defined.
   * @throws MisMatchedOwnerException
   *           if the groupId doesn't match the owner of the sensor process.
   */
  public abstract void deleteCollectorProcessDefinition(String slug, String groupId)
      throws IdNotFoundException, MisMatchedOwnerException;

  /**
   * Deletes the given location.
   * 
   * @param id
   *          The unique id of the location to delete.
   * @param groupId
   *          the group id of the user making the request.
   * @throws IdNotFoundException
   *           If the id is not known or defined.
   * @throws MisMatchedOwnerException
   *           if the groupId doesn't match the owner of the location.
   */
  public abstract void deleteLocation(String id, String groupId) throws IdNotFoundException,
      MisMatchedOwnerException;

  /**
   * Deletes the given measurement type.
   * 
   * @param slug
   *          The unique id for the MeasurementType to delete.
   * @throws IdNotFoundException
   *           if the slug is not a known MeasurementType.
   */
  public abstract void deleteMeasurementType(String slug) throws IdNotFoundException;

  /**
   * @param id
   *          The unique id of the Organization.
   * @throws IdNotFoundException
   *           If the id is not known or defined.
   */
  public abstract void deleteOrganization(String id) throws IdNotFoundException;

  /**
   * Deletes the given Sensor.
   * 
   * @param id
   *          The unique id of the Sensor.
   * @param groupId
   *          the group id of the user making the request.
   * @throws IdNotFoundException
   *           If the id is not known or defined.
   * @throws MisMatchedOwnerException
   *           if the groupId doesn't match the owner of the sensor.
   */
  public abstract void deleteSensor(String id, String groupId) throws IdNotFoundException,
      MisMatchedOwnerException;

  /**
   * Deletes the given SensorGroup.
   * 
   * @param id
   *          The unique id of the SensorGroup.
   * @param groupId
   *          the group id of the user making the request.
   * @throws IdNotFoundException
   *           If the id is not known or defined.
   * @throws MisMatchedOwnerException
   *           if the groupId doesn't match the owner of the sensor group.
   */
  public abstract void deleteSensorGroup(String id, String groupId) throws IdNotFoundException,
      MisMatchedOwnerException;

  /**
   * Deletes the given SensorModel.
   * 
   * @param id
   *          The unique id of the SensorModel.
   * @throws IdNotFoundException
   *           If the id is not known or defined.
   */
  public abstract void deleteSensorModel(String id) throws IdNotFoundException;

  /**
   * @param id
   *          The unique id of the User.
   * @param orgId
   *          the id of the organization the user is a member.
   * @throws IdNotFoundException
   *           If the id is not known or defined.
   */
  public abstract void deleteUser(String id, String orgId) throws IdNotFoundException;

  /**
   * @param userId
   *          The id of the UserPassword to delete.
   * @param orgId
   *          the user's organization id.
   * @throws IdNotFoundException
   *           If the id is not known or defined.
   */
  public abstract void deleteUserPassword(String userId, String orgId) throws IdNotFoundException;

  /**
   * Deletes the given WattDepository.
   * 
   * @param id
   *          The unique id of the WattDepository.
   * @param groupId
   *          the group id of the user making the request.
   * @throws IdNotFoundException
   *           If the id is not known or defined.
   * @throws MisMatchedOwnerException
   *           if the groupId doesn't match the owner of the sensor process.
   */
  public abstract void deleteWattDepository(String id, String groupId) throws IdNotFoundException,
      MisMatchedOwnerException;

  /**
   * @param id
   *          The unique id for the CollectorProcessDefinition.
   * @param groupId
   *          the group id of the user making the request.
   * @return The CollectorProcessDefinition with the given id.
   * @throws MisMatchedOwnerException
   *           if the groupId doesn't match the owner of the sensor process.
   */
  public abstract CollectorProcessDefinition getCollectorProcessDefinition(String id, String groupId)
      throws MisMatchedOwnerException;

  /**
   * @param groupId
   *          the id of the owner Organization.
   * @return A list of the defined CollectorProcessDefinition Ids.
   */
  public abstract List<String> getCollectorProcessDefinitionIds(String groupId);

  /**
   * @param groupId
   *          the group id of the user making the request.
   * @return The known/defined CollectorProcessDefinitiones owned by the given
   *         group id.
   */
  public abstract List<CollectorProcessDefinition> getCollectorProcessDefinitions(String groupId);

  /**
   * @param id
   *          The unique id for the Location.
   * @param groupId
   *          the group id of the user making the request.
   * @return The Location with the given id.
   * @throws MisMatchedOwnerException
   *           if the groupId doesn't match the owner of the location.
   */
  public abstract SensorLocation getLocation(String id, String groupId)
      throws MisMatchedOwnerException;

  /**
   * @param groupId
   *          the id of the owner Organization.
   * @return A list of the defined Location Ids.
   */
  public abstract List<String> getLocationIds(String groupId);

  /**
   * @param groupId
   *          the group id of the user making the request.
   * @return The known/defined Locations owned by the given group id.
   */
  public abstract List<SensorLocation> getLocations(String groupId);

  /**
   * @param slug
   *          The unique id for the MeasurementType.
   * @return The MeasurementType with the given slug.
   */
  public abstract MeasurementType getMeasurementType(String slug);

  /**
   * @return A List of the defined MeasurementTypes.
   */
  public abstract List<MeasurementType> getMeasurementTypes();

  /**
   * @param id
   *          the unique id for the Organization.
   * @return The UserGroup with the given id.
   */
  public abstract Organization getOrganization(String id);

  /**
   * @return A list of the defined organization Ids.
   */
  public abstract List<String> getOrganizationIds();

  /**
   * @return The known/defined Organizations.
   */
  public abstract List<Organization> getOrganizations();

  /**
   * @param id
   *          The unique id for the Sensor.
   * @param groupId
   *          the group id of the user making the request.
   * @return The Sensor with the given id.
   * @throws MisMatchedOwnerException
   *           if the groupId doesn't match the owner of the sensor.
   */
  public abstract Sensor getSensor(String id, String groupId) throws MisMatchedOwnerException;

  /**
   * @param id
   *          The unique id for the SensorGroup.
   * @param groupId
   *          the group id of the user making the request.
   * @return The SensorGroup with the given id.
   * @throws MisMatchedOwnerException
   *           if the groupId doesn't match the owner of the sensor group.
   */
  public abstract SensorGroup getSensorGroup(String id, String groupId)
      throws MisMatchedOwnerException;

  /**
   * @param groupId
   *          the id of the owner UserGroup.
   * @return A list of the defined SensorGroup Ids.
   */
  public abstract List<String> getSensorGroupIds(String groupId);

  /**
   * @param groupId
   *          the group id of the user making the request.
   * @return The known/defined SensorGroups owned by the given group id.
   */
  public abstract List<SensorGroup> getSensorGroups(String groupId);

  /**
   * @param groupId
   *          the id of the owner UserGroup.
   * @return A list of the defined Sensor Ids.
   */
  public abstract List<String> getSensorIds(String groupId);

  /**
   * @param id
   *          The unique id for the SensorModel.
   * @return The SensorModel with the given id.
   */
  public abstract SensorModel getSensorModel(String id);

  /**
   * @return A list of the defined SensorModel Ids.
   */
  public abstract List<String> getSensorModelIds();

  /**
   * @return The known/defined SensorModels owned by the given group id.
   */
  public abstract List<SensorModel> getSensorModels();

  /**
   * @param groupId
   *          the group id of the user making the request.
   * @return The known/defined Sensors owned by the given group id.
   */
  public abstract List<Sensor> getSensors(String groupId);

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
   * @param id
   *          the unique id for the UserInfo.
   * @param orgId
   *          the id of the organization the user is in.
   * @return The UserInfo with the given id.
   */
  public abstract UserInfo getUser(String id, String orgId);

  /**
   * @param orgId
   *          the id of the organization the user is in.
   * @return A list of the defined user Ids.
   */
  public abstract List<String> getUserIds(String orgId);

  /**
   * @param id
   *          The user's id.
   * @param orgId
   *          the id of the organization the user is in.
   * @return the UserPassword instance associated with the user.
   */
  public abstract UserPassword getUserPassword(String id, String orgId);

  /**
   * @param orgId
   *          the id of the organization the user is in.
   * @return The known/defined UserInfos.
   */
  public abstract List<UserInfo> getUsers(String orgId);

  /**
   * @param user
   *          The user.
   * @return The UserGroup that the user is in.
   */
  public abstract Organization getUsersGroup(UserInfo user);

  /**
   * @param id
   *          The unique id for the WattDepository to get.
   * @param groupId
   *          the group id of the user making the request.
   * @return The WattDepository with the given id.
   * @throws MisMatchedOwnerException
   *           if the groupId doesn't match the owner of the sensor process.
   */
  public abstract Depository getWattDeposiory(String id, String groupId)
      throws MisMatchedOwnerException;

  /**
   * @param groupId
   *          the group id of the user making the request.
   * @return The known/defined WattDepositories owned by the given group id.
   */
  public abstract List<Depository> getWattDepositories(String groupId);

  /**
   * @param groupId
   *          the id of the owner UserGroup.
   * @return A list of the defined WattDepository Ids.
   */
  public abstract List<String> getWattDepositoryIds(String groupId);

  /**
   * Ensures the base set of MeasurementTypes are defined in WattDepot.
   */
  public void initializeMeasurementTypes() {
    for (String key : UnitsHelper.quantities.keySet()) {
      MeasurementType mt = getMeasurementType(Slug.slugify(key));
      if (mt == null) {
        try {
          defineMeasurementType(key, UnitsHelper.quantities.get(key).toString());
        }
        catch (UniqueIdException e) {
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
      SensorModel sm = getSensorModel(Slug.slugify(key));
      if (sm == null) {
        SensorModel model = SensorModelHelper.models.get(key);
        try {
          defineSensorModel(model.getName(), model.getProtocol(), model.getType(),
              model.getVersion());
        }
        catch (UniqueIdException e) {
          e.printStackTrace();
        }
      }
    }

  }

  /**
   * @param properties
   *          the properties to set
   */
  public void setServerProperties(ServerProperties properties) {
    this.properties = properties;
  }

  /**
   * Updates the given sensor process in the persistent store.
   * 
   * @param process
   *          The updated CollectorProcessDefinition.
   * @return The updated process from persistence.
   */
  public abstract CollectorProcessDefinition updateCollectorProcessDefinition(
      CollectorProcessDefinition process);

  /**
   * Updates the given location in the persistent store.
   * 
   * @param loc
   *          The updated Location.
   * @return The updated location from persistence.
   */
  public abstract SensorLocation updateLocation(SensorLocation loc);

  /**
   * Updates the given measurement type in the persistent store.
   * 
   * @param type
   *          The updated MeasurementType.
   * @return The updated MeasurementType from persistence.
   */
  public abstract MeasurementType updateMeasurementType(MeasurementType type);

  /**
   * @param org
   *          The updated Organization.
   * @return The updated organization from persistence.
   */
  public abstract Organization updateOrganization(Organization org);

  /**
   * Updates the given sensor in the persistent store.
   * 
   * @param sensor
   *          The updated Sensor.
   * @return The updated sensor from persistence.
   */
  public abstract Sensor updateSensor(Sensor sensor);

  /**
   * Updates the given sensor group in the persistent store.
   * 
   * @param group
   *          The updated SensorGroup.
   * @return The updated sensor group from persistence.
   */
  public abstract SensorGroup updateSensorGroup(SensorGroup group);

  /**
   * Updates the given sensor model in the persistent store.
   * 
   * @param model
   *          The updated SensorModel.
   * @return The updated model from persistence.
   */
  public abstract SensorModel updateSensorModel(SensorModel model);

  /**
   * @param user
   *          The updated UserInfo.
   * @return The updated user from persistence.
   */
  public abstract UserInfo updateUserInfo(UserInfo user);

  /**
   * @param password
   *          The UserPassword to update.
   * @return The updated password from persistence.
   */
  public abstract UserPassword updateUserPassword(UserPassword password);

}
