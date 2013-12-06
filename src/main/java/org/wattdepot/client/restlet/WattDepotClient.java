/**
 * WattDepotClient.java This file is part of WattDepot 3.
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
package org.wattdepot.client.restlet;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

import javax.xml.datatype.DatatypeConfigurationException;

import org.restlet.data.ChallengeResponse;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;
import org.wattdepot.client.WattDepotInterface;
import org.wattdepot.datamodel.CollectorMetaData;
import org.wattdepot.datamodel.CollectorMetaDataList;
import org.wattdepot.datamodel.Depository;
import org.wattdepot.datamodel.DepositoryList;
import org.wattdepot.datamodel.MeasuredValue;
import org.wattdepot.datamodel.Measurement;
import org.wattdepot.datamodel.MeasurementList;
import org.wattdepot.datamodel.MeasurementType;
import org.wattdepot.datamodel.MeasurementTypeList;
import org.wattdepot.datamodel.Sensor;
import org.wattdepot.datamodel.SensorGroup;
import org.wattdepot.datamodel.SensorGroupList;
import org.wattdepot.datamodel.SensorList;
import org.wattdepot.datamodel.SensorLocation;
import org.wattdepot.datamodel.SensorLocationList;
import org.wattdepot.datamodel.SensorModel;
import org.wattdepot.datamodel.SensorModelList;
import org.wattdepot.exception.BadCredentialException;
import org.wattdepot.exception.IdNotFoundException;
import org.wattdepot.exception.MeasurementGapException;
import org.wattdepot.exception.MeasurementTypeException;
import org.wattdepot.exception.NoMeasurementException;
import org.wattdepot.restlet.API;
import org.wattdepot.restlet.CollectorMetaDataResource;
import org.wattdepot.restlet.CollectorMetaDatasResource;
import org.wattdepot.restlet.DepositoriesResource;
import org.wattdepot.restlet.DepositoryMeasurementResource;
import org.wattdepot.restlet.DepositoryMeasurementsResource;
import org.wattdepot.restlet.DepositoryResource;
import org.wattdepot.restlet.DepositoryValueResource;
import org.wattdepot.restlet.MeasurementTypeResource;
import org.wattdepot.restlet.MeasurementTypesResource;
import org.wattdepot.restlet.SensorGroupResource;
import org.wattdepot.restlet.SensorGroupsResource;
import org.wattdepot.restlet.SensorLocationResource;
import org.wattdepot.restlet.SensorLocationsResource;
import org.wattdepot.restlet.SensorModelResource;
import org.wattdepot.restlet.SensorModelsResource;
import org.wattdepot.restlet.SensorResource;
import org.wattdepot.restlet.SensorsResource;
import org.wattdepot.util.DateConvert;

/**
 * WattDepotClient - high-level Java implementation that communicates with a
 * WattDepot3 server. It implements the WattDepotInterface that hides the HTTP
 * API.
 * 
 * 
 * @author Cam Moore
 * 
 */
public class WattDepotClient implements WattDepotInterface {

  private String wattDepotUri;
  /** The HTTP authentication approach. */
  private ChallengeScheme scheme = ChallengeScheme.HTTP_BASIC;
  private ChallengeResponse authentication;
  private String groupId;

  /**
   * Creates a new WattDepotClient.
   * 
   * @param serverUri
   *          The URI of the WattDepot server (e.g.
   *          "http://server.wattdepot.org/")
   * @param username
   *          The name of the user. The user must be defined in the WattDepot
   *          server.
   * @param password
   *          The password for the user.
   * @throws BadCredentialException
   *           If the user or password don't match the credentials on the
   *           WattDepot server.
   */
  public WattDepotClient(String serverUri, String username, String password)
      throws BadCredentialException {
    System.out.println("Client " + serverUri + ", " + username + ", " + password);
    this.authentication = new ChallengeResponse(this.scheme, username, password);
    if (serverUri == null) {
      throw new IllegalArgumentException("serverUri cannot be null");
    }
    if (!serverUri.endsWith("/")) {
      throw new IllegalArgumentException("serverUri must end with '/'");
    }
    this.wattDepotUri = serverUri + API.BASE_URI;

    ClientResource client = null;

    client = makeClient(API.ADMIN_URI);
    try {
      client.head();
      if (client.getLocationRef() != null) {
        String path = client.getLocationRef().getPath();
        path = path.substring(0, path.length() - 1);
        int lastSlash = path.lastIndexOf('/') + 1;
        groupId = path.substring(lastSlash);
      }
      else {
        groupId = "admin";
      }
      client.release();
    }
    catch (ResourceException e) {
      throw new BadCredentialException(e.getMessage() + " username and or password are not corect.");
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot3.client.WattDepotInterface#deleteCollectorMetaData(org.wattdepot3
   * .datamodel.CollectorMetaData)
   */
  @Override
  public void deleteCollectorMetaData(CollectorMetaData process) throws IdNotFoundException {
    ClientResource client = makeClient(this.groupId + "/" + API.SENSOR_PROCESS_URI
        + process.getId());
    CollectorMetaDataResource resource = client.wrap(CollectorMetaDataResource.class);
    try {
      resource.remove();
    }
    catch (ResourceException e) {
      if (e.getStatus().equals(Status.CLIENT_ERROR_EXPECTATION_FAILED)) {
        throw new IdNotFoundException(process + " is not stored in WattDepot.");
      }
    }
    finally {
      client.release();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot3.client.WattDepotInterface#deleteDepository(org.wattdepot3
   * .datamodel.Depository)
   */
  @Override
  public void deleteDepository(Depository depository) throws IdNotFoundException {
    ClientResource client = makeClient(this.groupId + "/" + API.DEPOSITORY_URI + depository.getId());
    DepositoryResource resource = client.wrap(DepositoryResource.class);
    try {
      resource.remove();
    }
    catch (ResourceException e) {
      if (e.getStatus().equals(Status.CLIENT_ERROR_EXPECTATION_FAILED)) {
        throw new IdNotFoundException(depository + " is not stored in WattDepot.");
      }
    }
    finally {
      client.release();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot3.client.WattDepotInterface#deleteLocation(org.wattdepot3.
   * datamodel.Location)
   */
  @Override
  public void deleteLocation(SensorLocation sensorLocation) throws IdNotFoundException {
    ClientResource client = makeClient(this.groupId + "/" + API.LOCATION_URI
        + sensorLocation.getId());
    SensorLocationResource resource = client.wrap(SensorLocationResource.class);
    try {
      resource.remove();
    }
    catch (ResourceException e) {
      if (e.getStatus().equals(Status.CLIENT_ERROR_EXPECTATION_FAILED)) {
        throw new IdNotFoundException(sensorLocation + " is not stored in WattDepot.");
      }
    }
    finally {
      client.release();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot3.client.WattDepotInterface#deleteMeasurement(org.wattdepot3
   * .datamodel.Depository, org.wattdepot3.datamodel.Measurement)
   */
  @Override
  public void deleteMeasurement(Depository depository, Measurement measurement)
      throws IdNotFoundException {
    ClientResource client = makeClient(this.groupId + "/" + API.DEPOSITORY_URI + depository.getId()
        + "/" + API.MEASUREMENT_URI + measurement.getId());
    DepositoryMeasurementResource resource = client.wrap(DepositoryMeasurementResource.class);
    try {
      resource.remove(measurement);
    }
    catch (ResourceException e) {
      e.printStackTrace();
    }
    client.release();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot3.client.WattDepotInterface#deleteMeasurementType(org.wattdepot3
   * .datamodel.MeasurementType)
   */
  @Override
  public void deleteMeasurementType(MeasurementType type) {
    ClientResource client = makeClient(API.MEASUREMENT_TYPE_URI + type.getId());
    MeasurementTypeResource resource = client.wrap(MeasurementTypeResource.class);
    try {
      resource.remove();
    }
    catch (ResourceException e) {
      e.printStackTrace();
    }
    client.release();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot3.client.WattDepotInterface#deleteSensor(org.wattdepot3.datamodel
   * .Sensor)
   */
  @Override
  public void deleteSensor(Sensor sensor) throws IdNotFoundException {
    ClientResource client = makeClient(this.groupId + "/" + API.SENSOR_URI + sensor.getId());
    SensorResource resource = client.wrap(SensorResource.class);
    try {
      resource.remove();
    }
    catch (ResourceException e) {
      e.printStackTrace();
    }
    client.release();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot3.client.WattDepotInterface#deleteSensorGroup(org.wattdepot3
   * .datamodel.SensorGroup)
   */
  @Override
  public void deleteSensorGroup(SensorGroup group) throws IdNotFoundException {
    ClientResource client = makeClient(this.groupId + "/" + API.SENSOR_GROUP_URI + group.getId());
    SensorGroupResource resource = client.wrap(SensorGroupResource.class);
    try {
      resource.remove();
    }
    catch (ResourceException e) {
      if (e.getStatus().equals(Status.CLIENT_ERROR_EXPECTATION_FAILED)) {
        throw new IdNotFoundException(group + " is not stored in WattDepot.");
      }
    }
    finally {
      client.release();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot3.client.WattDepotInterface#deleteSensorModel(org.wattdepot3
   * .datamodel.SensorModel)
   */
  @Override
  public void deleteSensorModel(SensorModel model) throws IdNotFoundException {
    ClientResource client = makeClient(API.SENSOR_MODEL_URI + model.getId());
    SensorModelResource resource = client.wrap(SensorModelResource.class);
    try {
      resource.remove();
    }
    catch (ResourceException e) {
      if (e.getStatus().equals(Status.CLIENT_ERROR_EXPECTATION_FAILED)) {
        throw new IdNotFoundException(model + " is not stored in WattDepot.");
      }
    }
    finally {
      client.release();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot3.client.WattDepotInterface#getCollectorMetaData(java.lang
   * .String)
   */
  @Override
  public CollectorMetaData getCollectorMetaData(String id) throws IdNotFoundException {
    ClientResource client = makeClient(this.groupId + "/" + API.SENSOR_PROCESS_URI + id);
    CollectorMetaDataResource resource = client.wrap(CollectorMetaDataResource.class);
    try {
      CollectorMetaData ret = resource.retrieve();
      client.release();
      return ret;
    }
    catch (ResourceException e) {
      if (e.getStatus().equals(Status.CLIENT_ERROR_EXPECTATION_FAILED)) {
        throw new IdNotFoundException(id + " is not a known CollectorMetaData. ");
      }
      e.printStackTrace();
    }
    finally {
      if (client != null) {
        client.release();
      }
    }
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot3.client.WattDepotInterface#getCollectorMetaDatas()
   */
  @Override
  public CollectorMetaDataList getCollectorMetaDatas() {
    ClientResource client = makeClient(this.groupId + "/" + API.SENSOR_PROCESSES_URI);
    CollectorMetaDatasResource resource = client.wrap(CollectorMetaDatasResource.class);
    CollectorMetaDataList ret = resource.retrieve();
    client.release();
    return ret;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot3.client.WattDepotInterface#getDepositories()
   */
  @Override
  public DepositoryList getDepositories() {
    ClientResource client = makeClient(this.groupId + "/" + API.DEPOSITORIES_URI);
    DepositoriesResource resource = client.wrap(DepositoriesResource.class);
    DepositoryList ret = resource.retrieve();
    client.release();
    return ret;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot3.client.WattDepotInterface#getDepository(java.lang.String)
   */
  @Override
  public Depository getDepository(String id) throws IdNotFoundException {
    ClientResource client = makeClient(this.groupId + "/" + API.DEPOSITORY_URI + id);
    DepositoryResource resource = client.wrap(DepositoryResource.class);
    Depository ret = null;
    try {
      ret = resource.retrieve();
    }
    catch (ResourceException e) {
      if (e.getStatus().equals(Status.CLIENT_ERROR_EXPECTATION_FAILED)) {
        throw new IdNotFoundException(id + " is not a known Depository id.");
      }
    }
    finally {
      client.release();
    }
    return ret;
  }

  /**
   * @return the groupId
   */
  public String getGroupId() {
    return groupId;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot3.client.WattDepotInterface#getLocation(java.lang.String)
   */
  @Override
  public SensorLocation getLocation(String id) throws IdNotFoundException {
    ClientResource client = makeClient(this.groupId + "/" + API.LOCATION_URI + id);
    SensorLocationResource resource = client.wrap(SensorLocationResource.class);
    SensorLocation ret = null;
    try {
      ret = resource.retrieve();
    }
    catch (ResourceException e) {
      if (e.getStatus().equals(Status.CLIENT_ERROR_EXPECTATION_FAILED)) {
        throw new IdNotFoundException(id + " is not a known Location id.");
      }
    }
    finally {
      client.release();
    }
    return ret;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot3.client.WattDepotInterface#getLocations()
   */
  @Override
  public SensorLocationList getLocations() {
    ClientResource client = makeClient(this.groupId + "/" + API.LOCATIONS_URI);
    SensorLocationsResource resource = client.wrap(SensorLocationsResource.class);
    SensorLocationList ret = resource.retrieve();
    client.release();
    return ret;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot3.client.WattDepotInterface#getMeasurements(org.wattdepot3
   * .datamodel.Depository, org.wattdepot3.datamodel.Sensor, java.util.Date,
   * java.util.Date)
   */
  @Override
  public MeasurementList getMeasurements(Depository depository, Sensor sensor, Date start, Date end) {
    try {
      ClientResource client = makeClient(this.groupId + "/" + API.DEPOSITORY_URI
          + depository.getId() + API.MEASUREMENTS_URI + "?sensor=" + sensor.getId() + "&start="
          + DateConvert.convertDate(start) + "&end=" + DateConvert.convertDate(end));
      DepositoryMeasurementsResource resource = client.wrap(DepositoryMeasurementsResource.class);
      MeasurementList ret = resource.retrieve();
      client.release();
      return ret;
    }
    catch (DatatypeConfigurationException e) {
      e.printStackTrace();
    }
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot3.client.WattDepotInterface#getMeasurementType(java.lang.String
   * )
   */
  @Override
  public MeasurementType getMeasurementType(String id) throws IdNotFoundException {
    ClientResource client = makeClient(API.MEASUREMENT_TYPE_URI + id);
    MeasurementTypeResource resource = client.wrap(MeasurementTypeResource.class);
    try {
      MeasurementType ret = resource.retrieve();
      client.release();
      return ret;
    }
    catch (ResourceException e) {
      if (e.getStatus().equals(Status.CLIENT_ERROR_EXPECTATION_FAILED)) {
        throw new IdNotFoundException(id + " is not a known MeasurementType. ");
      }
      e.printStackTrace();
    }
    finally {
      if (client != null) {
        client.release();
      }
    }
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot3.client.WattDepotInterface#getMeasurementTypes()
   */
  @Override
  public MeasurementTypeList getMeasurementTypes() {
    ClientResource client = makeClient(API.MEASUREMENT_TYPES_URI);
    MeasurementTypesResource resource = client.wrap(MeasurementTypesResource.class);
    MeasurementTypeList ret = resource.retrieve();
    client.release();
    return ret;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot3.client.WattDepotInterface#getSensor(java.lang.String)
   */
  @Override
  public Sensor getSensor(String id) throws IdNotFoundException {
    ClientResource client = makeClient(this.groupId + "/" + API.SENSOR_URI + id);
    SensorResource resource = client.wrap(SensorResource.class);
    try {
      Sensor ret = resource.retrieve();
      client.release();
      return ret;
    }
    catch (ResourceException e) {
      if (e.getStatus().equals(Status.CLIENT_ERROR_EXPECTATION_FAILED)) {
        throw new IdNotFoundException(id + " is not a known Sensor. ");
      }
      e.printStackTrace();
    }
    finally {
      if (client != null) {
        client.release();
      }
    }
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot3.client.WattDepotInterface#getSensorGroup(java.lang.String)
   */
  @Override
  public SensorGroup getSensorGroup(String id) throws IdNotFoundException {
    ClientResource client = makeClient(this.groupId + "/" + API.SENSOR_GROUP_URI + id);
    SensorGroupResource resource = client.wrap(SensorGroupResource.class);
    try {
      SensorGroup ret = resource.retrieve();
      client.release();
      return ret;
    }
    catch (ResourceException e) {
      if (e.getStatus().equals(Status.CLIENT_ERROR_EXPECTATION_FAILED)) {
        throw new IdNotFoundException(id + " is not a known SensorGroup. ");
      }
      e.printStackTrace();
    }
    finally {
      if (client != null) {
        client.release();
      }
    }
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot3.client.WattDepotInterface#getSensorGroups()
   */
  @Override
  public SensorGroupList getSensorGroups() {
    ClientResource client = makeClient(this.groupId + "/" + API.SENSOR_GROUPS_URI);
    SensorGroupsResource resource = client.wrap(SensorGroupsResource.class);
    SensorGroupList ret = resource.retrieve();
    client.release();
    return ret;

  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot3.client.WattDepotInterface#getSensorModel(java.lang.String)
   */
  @Override
  public SensorModel getSensorModel(String id) throws IdNotFoundException {
    ClientResource client = makeClient(API.SENSOR_MODEL_URI + id);
    SensorModelResource resource = client.wrap(SensorModelResource.class);
    try {
      SensorModel ret = resource.retrieve();
      client.release();
      return ret;
    }
    catch (ResourceException e) {
      if (e.getStatus().equals(Status.CLIENT_ERROR_EXPECTATION_FAILED)) {
        throw new IdNotFoundException(id + " is not a known SensorModel. ");
      }
      e.printStackTrace();
    }
    finally {
      if (client != null) {
        client.release();
      }
    }
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot3.client.WattDepotInterface#getSensorModels()
   */
  @Override
  public SensorModelList getSensorModels() {
    ClientResource client = makeClient(API.SENSOR_MODELS_URI);
    SensorModelsResource resource = client.wrap(SensorModelsResource.class);
    SensorModelList ret = resource.retrieve();
    client.release();
    return ret;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot3.client.WattDepotInterface#getSensors()
   */
  @Override
  public SensorList getSensors() {
    ClientResource client = makeClient(this.groupId + "/" + API.SENSORS_URI);
    SensorsResource resource = client.wrap(SensorsResource.class);
    SensorList ret = resource.retrieve();
    client.release();
    return ret;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot3.client.WattDepotInterface#getValue(org.wattdepot3.datamodel
   * .Depository, org.wattdepot3.datamodel.Sensor, java.util.Date)
   */
  @Override
  public Double getValue(Depository depository, Sensor sensor, Date timestamp)
      throws NoMeasurementException {
    ClientResource client = null;
    try {
      client = makeClient(this.groupId + "/" + API.DEPOSITORY_URI + depository.getId() + "/"
          + API.VALUE_URI + "?sensor=" + sensor.getId() + "&timestamp="
          + DateConvert.convertDate(timestamp));
      DepositoryValueResource resource = client.wrap(DepositoryValueResource.class);
      MeasuredValue ret = resource.retrieve();
      client.release();
      if (ret != null) {
        return ret.getValue();
      }
    }
    catch (DatatypeConfigurationException e) {
      e.printStackTrace();
    }
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot3.client.WattDepotInterface#getValue(org.wattdepot3.datamodel
   * .Depository, org.wattdepot3.datamodel.Sensor, java.util.Date,
   * java.util.Date)
   */
  @Override
  public Double getValue(Depository depository, Sensor sensor, Date start, Date end)
      throws NoMeasurementException {
    ClientResource client = null;
    try {
      client = makeClient(this.groupId + "/" + API.DEPOSITORY_URI + depository.getId() + "/"
          + API.VALUE_URI + "?sensor=" + sensor.getId() + "&start="
          + DateConvert.convertDate(start) + "&end=" + DateConvert.convertDate(end));
      DepositoryValueResource resource = client.wrap(DepositoryValueResource.class);
      MeasuredValue ret = resource.retrieve();
      client.release();
      return ret.getValue();
    }
    catch (DatatypeConfigurationException e) {
      e.printStackTrace();
    }
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot3.client.WattDepotInterface#getValue(org.wattdepot3.datamodel
   * .Depository, org.wattdepot3.datamodel.Sensor, java.util.Date,
   * java.util.Date, java.lang.Long)
   */
  @Override
  public Double getValue(Depository depository, Sensor sensor, Date start, Date end, Long gapSeconds)
      throws NoMeasurementException, MeasurementGapException {
    ClientResource client = null;
    try {
      client = makeClient(this.groupId + "/" + API.DEPOSITORY_URI + depository.getId() + "/"
          + API.VALUE_URI + "?sensor=" + sensor.getId() + "&start="
          + DateConvert.convertDate(start) + "&end=" + DateConvert.convertDate(end) + "&gap="
          + gapSeconds);
      DepositoryValueResource resource = client.wrap(DepositoryValueResource.class);
      MeasuredValue ret = resource.retrieve();
      client.release();
      return ret.getValue();
    }
    catch (DatatypeConfigurationException e) {
      e.printStackTrace();
    }
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot3.client.WattDepotInterface#getValue(org.wattdepot3.datamodel
   * .Depository, org.wattdepot3.datamodel.Sensor, java.util.Date,
   * java.lang.Long)
   */
  @Override
  public Double getValue(Depository depository, Sensor sensor, Date timestamp, Long gapSeconds)
      throws NoMeasurementException, MeasurementGapException {
    ClientResource client = null;
    try {
      client = makeClient(this.groupId + "/" + API.DEPOSITORY_URI + depository.getId() + "/"
          + API.VALUE_URI + "?sensor=" + sensor.getId() + "&timestamp="
          + DateConvert.convertDate(timestamp) + "&gap=" + gapSeconds);
      DepositoryValueResource resource = client.wrap(DepositoryValueResource.class);
      MeasuredValue ret = resource.retrieve();
      client.release();
      return ret.getValue();
    }
    catch (DatatypeConfigurationException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * @return the wattDepotUri
   */
  public String getWattDepotUri() {
    return wattDepotUri;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot3.client.WattDepotInterface#isHealthy()
   */
  @Override
  public boolean isHealthy() {
    ClientResource client = makeClient(this.groupId + "/");
    client.head();
    boolean healthy = client.getStatus().isSuccess();
    client.release();
    return healthy;
  }

  /**
   * Creates a ClientResource for the given request. Calling code MUST release
   * the ClientResource when finished.
   * 
   * @param requestString
   *          A String, the request portion of the WattDepot HTTP API, such as "
   * @return The client resource.
   */
  public ClientResource makeClient(String requestString) {
    try {
      URL url = new URL(this.wattDepotUri + requestString);
      Reference reference = new Reference(url);
      // System.out.print(reference);
      ClientResource client = new ClientResource(reference);
      client.setChallengeResponse(authentication);
      return client;
    }
    catch (MalformedURLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    Reference reference = new Reference(this.wattDepotUri + requestString);
    // System.out.print(reference);
    ClientResource client = new ClientResource(reference);
    client.setChallengeResponse(authentication);
    return client;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot3.client.WattDepotInterface#putCollectorMetaData(org.wattdepot3
   * .datamodel.CollectorMetaData)
   */
  @Override
  public void putCollectorMetaData(CollectorMetaData process) {
    ClientResource client = makeClient(this.groupId + "/" + API.SENSOR_PROCESS_URI
        + process.getId());
    CollectorMetaDataResource resource = client.wrap(CollectorMetaDataResource.class);
    resource.store(process);
    client.release();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot3.client.WattDepotInterface#putDepository(org.wattdepot3.datamodel
   * .Depository)
   */
  @Override
  public void putDepository(Depository depository) {
    ClientResource client = makeClient(this.groupId + "/" + API.DEPOSITORY_URI + depository.getId());
    DepositoryResource resource = client.wrap(DepositoryResource.class);
    resource.store(depository);
    client.release();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot3.client.WattDepotInterface#putLocation(org.wattdepot3.datamodel
   * .Location)
   */
  @Override
  public void putLocation(SensorLocation loc) {
    ClientResource client = makeClient(this.groupId + "/" + API.LOCATION_URI + loc.getId());
    SensorLocationResource resource = client.wrap(SensorLocationResource.class);
    resource.store(loc);
    client.release();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot3.client.WattDepotInterface#putMeasurement(org.wattdepot3.
   * datamodel.Depository, org.wattdepot3.datamodel.Measurement)
   */
  @Override
  public void putMeasurement(Depository depository, Measurement measurement)
      throws MeasurementTypeException {
    if (!depository.getMeasurementType().getUnits().equals(measurement.getMeasurementType())) {
      throw new MeasurementTypeException("Depository " + depository.getName() + " stores "
          + depository.getMeasurementType() + " not " + measurement.getMeasurementType());
    }
    ClientResource client = makeClient(this.groupId + "/" + API.DEPOSITORY_URI + depository.getId()
        + "/" + API.MEASUREMENT_URI);
    DepositoryMeasurementResource resource = client.wrap(DepositoryMeasurementResource.class);
    try {
      resource.store(measurement);
    }
    catch (ResourceException e) {
      e.printStackTrace();
    }
    client.release();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot3.client.WattDepotInterface#putMeasurementType(org.wattdepot3
   * .datamodel.MeasurementType)
   */
  @Override
  public void putMeasurementType(MeasurementType type) {
    ClientResource client = makeClient(API.MEASUREMENT_TYPE_URI + type.getId());
    MeasurementTypeResource resource = client.wrap(MeasurementTypeResource.class);
    resource.store(type);
    client.release();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot3.client.WattDepotInterface#putSensor(org.wattdepot3.datamodel
   * .Sensor)
   */
  @Override
  public void putSensor(Sensor sensor) {
    ClientResource client = makeClient(this.groupId + "/" + API.SENSOR_URI + sensor.getId());
    SensorResource resource = client.wrap(SensorResource.class);
    resource.store(sensor);
    client.release();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot3.client.WattDepotInterface#putSensorGroup(org.wattdepot3.
   * datamodel.SensorGroup)
   */
  @Override
  public void putSensorGroup(SensorGroup group) {
    ClientResource client = makeClient(this.groupId + "/" + API.SENSOR_GROUP_URI + group.getId());
    SensorGroupResource resource = client.wrap(SensorGroupResource.class);
    resource.store(group);
    client.release();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot3.client.WattDepotInterface#putSensorModel(org.wattdepot3.
   * datamodel.SensorModel)
   */
  @Override
  public void putSensorModel(SensorModel model) {
    ClientResource client = makeClient(API.SENSOR_MODEL_URI + model.getId());
    SensorModelResource resource = client.wrap(SensorModelResource.class);
    resource.store(model);
    client.release();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot3.client.WattDepotInterface#updateCollectorMetaData(org.wattdepot3
   * .datamodel.CollectorMetaData)
   */
  @Override
  public void updateCollectorMetaData(CollectorMetaData process) {
    putCollectorMetaData(process);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot3.client.WattDepotInterface#updateDepository(org.wattdepot3
   * .datamodel.Depository)
   */
  @Override
  public void updateDepository(Depository depository) {
    putDepository(depository);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot3.client.WattDepotInterface#updateLocation(org.wattdepot3.
   * datamodel.Location)
   */
  @Override
  public void updateLocation(SensorLocation sensorLocation) {
    putLocation(sensorLocation);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot3.client.WattDepotInterface#updateMeasurementType(org.wattdepot3
   * .datamodel.MeasurementType)
   */
  @Override
  public void updateMeasurementType(MeasurementType type) {
    putMeasurementType(type);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot3.client.WattDepotInterface#updateSensor(org.wattdepot3.datamodel
   * .Sensor)
   */
  @Override
  public void updateSensor(Sensor sensor) {
    putSensor(sensor);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot3.client.WattDepotInterface#updateSensorGroup(org.wattdepot3
   * .datamodel.SensorGroup)
   */
  @Override
  public void updateSensorGroup(SensorGroup group) {
    putSensorGroup(group);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot3.client.WattDepotInterface#updateSensorModel(org.wattdepot3
   * .datamodel.SensorModel)
   */
  @Override
  public void updateSensorModel(SensorModel model) {
    putSensorModel(model);
  }

}
