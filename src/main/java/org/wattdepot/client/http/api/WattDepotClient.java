/**
 * WattDepotClient.java This file is part of WattDepot.
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
package org.wattdepot.client.http.api;

import java.util.Date;
import java.util.logging.Logger;

import javax.xml.datatype.DatatypeConfigurationException;

import org.restlet.data.ChallengeResponse;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.Reference;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;
import org.wattdepot.client.ClientProperties;
import org.wattdepot.client.WattDepotInterface;
import org.wattdepot.common.domainmodel.CollectorMetaData;
import org.wattdepot.common.domainmodel.CollectorMetaDataList;
import org.wattdepot.common.domainmodel.Depository;
import org.wattdepot.common.domainmodel.DepositoryList;
import org.wattdepot.common.domainmodel.Labels;
import org.wattdepot.common.domainmodel.MeasuredValue;
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
import org.wattdepot.common.exception.BadCredentialException;
import org.wattdepot.common.exception.IdNotFoundException;
import org.wattdepot.common.exception.MeasurementGapException;
import org.wattdepot.common.exception.MeasurementTypeException;
import org.wattdepot.common.exception.NoMeasurementException;
import org.wattdepot.common.http.api.CollectorMetaDataResource;
import org.wattdepot.common.http.api.CollectorMetaDatasResource;
import org.wattdepot.common.http.api.DepositoriesResource;
import org.wattdepot.common.http.api.DepositoryMeasurementPutResource;
import org.wattdepot.common.http.api.DepositoryMeasurementResource;
import org.wattdepot.common.http.api.DepositoryMeasurementsResource;
import org.wattdepot.common.http.api.DepositoryPutResource;
import org.wattdepot.common.http.api.DepositoryResource;
import org.wattdepot.common.http.api.DepositoryValueResource;
import org.wattdepot.common.http.api.MeasurementTypePutResource;
import org.wattdepot.common.http.api.MeasurementTypeResource;
import org.wattdepot.common.http.api.MeasurementTypesResource;
import org.wattdepot.common.http.api.SensorGroupResource;
import org.wattdepot.common.http.api.SensorGroupsResource;
import org.wattdepot.common.http.api.SensorLocationResource;
import org.wattdepot.common.http.api.SensorLocationsResource;
import org.wattdepot.common.http.api.SensorModelPutResource;
import org.wattdepot.common.http.api.SensorModelResource;
import org.wattdepot.common.http.api.SensorModelsResource;
import org.wattdepot.common.http.api.SensorResource;
import org.wattdepot.common.http.api.SensorsResource;
import org.wattdepot.common.util.DateConvert;
import org.wattdepot.common.util.logger.WattDepotLogger;

/**
 * WattDepotClient - high-level Java implementation that communicates with a
 * WattDepot server. It implements the WattDepotInterface that hides the HTTP
 * Labels.
 * 
 * 
 * @author Cam Moore
 * 
 */
public class WattDepotClient implements WattDepotInterface {

  /** The URI for the WattDepot server. */
  private String wattDepotUri;
  /** The HTTP authentication approach. */
  private ChallengeScheme scheme = ChallengeScheme.HTTP_BASIC;
  /** The credentials for this client. */
  private ChallengeResponse authentication;
  /** The Group this client belongs to. */
  private String groupId;
  /** The logger for this client. */
  private Logger logger;
  /** The client properties. */
  private ClientProperties properties;

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
    this.properties = new ClientProperties();
    this.logger = WattDepotLogger.getLogger("org.wattdepot.client",
        properties.get(ClientProperties.CLIENT_HOME_DIR));
    logger.finest("Client " + serverUri + ", " + username + ", " + password);
    this.authentication = new ChallengeResponse(this.scheme, username, password);
    if (serverUri == null) {
      throw new IllegalArgumentException("serverUri cannot be null");
    }
    if (!serverUri.endsWith("/")) {
      throw new IllegalArgumentException("serverUri must end with '/'");
    }
    this.wattDepotUri = serverUri + Labels.WATTDEPOT + "/";

    ClientResource client = null;

    client = makeClient(Labels.ADMIN + "/");
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
   * org.wattdepot.client.WattDepotInterface#deleteCollectorMetaData(org.wattdepot
   * .datamodel.CollectorMetaData)
   */
  @Override
  public void deleteCollectorMetaData(CollectorMetaData process) throws IdNotFoundException {
    ClientResource client = makeClient(this.groupId + "/" + Labels.COLLECTOR_META_DATA + "/"
        + process.getId());
    CollectorMetaDataResource resource = client.wrap(CollectorMetaDataResource.class);
    try {
      resource.remove();
    }
    catch (ResourceException e) {
      throw new IdNotFoundException(process + " is not stored in WattDepot.");
    }
    finally {
      client.release();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.client.WattDepotInterface#deleteDepository(org.wattdepot
   * .datamodel.Depository)
   */
  @Override
  public void deleteDepository(Depository depository) throws IdNotFoundException {
    ClientResource client = makeClient(this.groupId + "/" + Labels.DEPOSITORY + "/"
        + depository.getId());
    DepositoryResource resource = client.wrap(DepositoryResource.class);
    try {
      resource.remove();
    }
    catch (ResourceException e) {
      throw new IdNotFoundException(depository + " is not stored in WattDepot.");
    }
    finally {
      client.release();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.client.WattDepotInterface#deleteLocation(org.wattdepot.
   * datamodel.Location)
   */
  @Override
  public void deleteLocation(SensorLocation sensorLocation) throws IdNotFoundException {
    ClientResource client = makeClient(this.groupId + "/" + Labels.LOCATION + "/"
        + sensorLocation.getId());
    SensorLocationResource resource = client.wrap(SensorLocationResource.class);
    try {
      resource.remove();
    }
    catch (ResourceException e) {
      throw new IdNotFoundException(sensorLocation + " is not stored in WattDepot.");
    }
    finally {
      client.release();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.client.WattDepotInterface#deleteMeasurement(org.wattdepot
   * .datamodel.Depository, org.wattdepot.datamodel.Measurement)
   */
  @Override
  public void deleteMeasurement(Depository depository, Measurement measurement)
      throws IdNotFoundException {
    ClientResource client = makeClient(this.groupId + "/" + Labels.DEPOSITORY + "/"
        + depository.getId() + "/" + Labels.MEASUREMENT + "/" + measurement.getId());
    DepositoryMeasurementResource resource = client.wrap(DepositoryMeasurementResource.class);
    try {
      resource.remove();
    }
    catch (ResourceException e) {
      throw new IdNotFoundException(measurement + " is not stored in WattDepot.");
    }
    client.release();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.client.WattDepotInterface#deleteMeasurementType(org.wattdepot
   * .datamodel.MeasurementType)
   */
  @Override
  public void deleteMeasurementType(MeasurementType type) throws IdNotFoundException {
    ClientResource client = makeClient(Labels.PUBLIC + "/" + Labels.MEASUREMENT_TYPE + "/"
        + type.getId());
    MeasurementTypeResource resource = client.wrap(MeasurementTypeResource.class);
    try {
      resource.remove();
    }
    catch (ResourceException e) {
      throw new IdNotFoundException(type + " is not stored in WattDepot.");
    }
    client.release();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.client.WattDepotInterface#deleteSensor(org.wattdepot.datamodel
   * .Sensor)
   */
  @Override
  public void deleteSensor(Sensor sensor) throws IdNotFoundException {
    ClientResource client = makeClient(this.groupId + "/" + Labels.SENSOR + "/" + sensor.getId());
    SensorResource resource = client.wrap(SensorResource.class);
    try {
      resource.remove();
    }
    catch (ResourceException e) {
      throw new IdNotFoundException(sensor + " is not stored in WattDepot.");
    }
    client.release();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.client.WattDepotInterface#deleteSensorGroup(org.wattdepot
   * .datamodel.SensorGroup)
   */
  @Override
  public void deleteSensorGroup(SensorGroup group) throws IdNotFoundException {
    ClientResource client = makeClient(this.groupId + "/" + Labels.SENSOR_GROUP + "/"
        + group.getId());
    SensorGroupResource resource = client.wrap(SensorGroupResource.class);
    try {
      resource.remove();
    }
    catch (ResourceException e) {
      throw new IdNotFoundException(group + " is not stored in WattDepot.");
    }
    finally {
      client.release();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.client.WattDepotInterface#deleteSensorModel(org.wattdepot
   * .datamodel.SensorModel)
   */
  @Override
  public void deleteSensorModel(SensorModel model) throws IdNotFoundException {
    ClientResource client = makeClient(Labels.PUBLIC + "/" + Labels.SENSOR_MODEL + "/"
        + model.getId());
    SensorModelResource resource = client.wrap(SensorModelResource.class);
    try {
      resource.remove();
    }
    catch (ResourceException e) {
      throw new IdNotFoundException(model + " is not stored in WattDepot.");
    }
    finally {
      client.release();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.client.WattDepotInterface#getCollectorMetaData(java.lang
   * .String)
   */
  @Override
  public CollectorMetaData getCollectorMetaData(String id) throws IdNotFoundException {
    ClientResource client = makeClient(this.groupId + "/" + Labels.COLLECTOR_META_DATA + "/" + id);
    CollectorMetaDataResource resource = client.wrap(CollectorMetaDataResource.class);
    try {
      CollectorMetaData ret = resource.retrieve();
      client.release();
      return ret;
    }
    catch (ResourceException e) {
      throw new IdNotFoundException(id + " is not a known CollectorMetaData. ");
    }
    finally {
      if (client != null) {
        client.release();
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.client.WattDepotInterface#getCollectorMetaDatas()
   */
  @Override
  public CollectorMetaDataList getCollectorMetaDatas() {
    ClientResource client = makeClient(this.groupId + "/" + Labels.COLLECTOR_META_DATAS + "/");
    CollectorMetaDatasResource resource = client.wrap(CollectorMetaDatasResource.class);
    CollectorMetaDataList ret = resource.retrieve();
    client.release();
    return ret;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.client.WattDepotInterface#getDepositories()
   */
  @Override
  public DepositoryList getDepositories() {
    ClientResource client = makeClient(this.groupId + "/" + Labels.DEPOSITORIES + "/");
    DepositoriesResource resource = client.wrap(DepositoriesResource.class);
    DepositoryList ret = null;
    try {
      ret = resource.retrieve();
    }
    finally {
      client.release();
    }
    return ret;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.client.WattDepotInterface#getDepository(java.lang.String)
   */
  @Override
  public Depository getDepository(String id) throws IdNotFoundException {
    ClientResource client = makeClient(this.groupId + "/" + Labels.DEPOSITORY + "/" + id);
    DepositoryResource resource = client.wrap(DepositoryResource.class);
    Depository ret = null;
    try {
      ret = resource.retrieve();
    }
    catch (ResourceException e) {
      throw new IdNotFoundException(id + " is not a known Depository id.");
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
   * @see org.wattdepot.client.WattDepotInterface#getLocation(java.lang.String)
   */
  @Override
  public SensorLocation getLocation(String id) throws IdNotFoundException {
    ClientResource client = makeClient(this.groupId + "/" + Labels.LOCATION + "/" + id);
    SensorLocationResource resource = client.wrap(SensorLocationResource.class);
    SensorLocation ret = null;
    try {
      ret = resource.retrieve();
    }
    catch (ResourceException e) {
      throw new IdNotFoundException(id + " is not a known Location id.");
    }
    finally {
      client.release();
    }
    return ret;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.client.WattDepotInterface#getLocations()
   */
  @Override
  public SensorLocationList getLocations() {
    ClientResource client = makeClient(this.groupId + "/" + Labels.LOCATIONS + "/");
    SensorLocationsResource resource = client.wrap(SensorLocationsResource.class);
    SensorLocationList ret = resource.retrieve();
    client.release();
    return ret;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.client.WattDepotInterface#getMeasurements(org.wattdepot
   * .datamodel.Depository, org.wattdepot.datamodel.Sensor, java.util.Date,
   * java.util.Date)
   */
  @Override
  public MeasurementList getMeasurements(Depository depository, Sensor sensor, Date start, Date end) {
    try {
      ClientResource client = makeClient(this.groupId + "/" + Labels.DEPOSITORY + "/"
          + depository.getId() + "/" + Labels.MEASUREMENTS + "/" + "?sensor=" + sensor.getId()
          + "&start=" + DateConvert.convertDate(start) + "&end=" + DateConvert.convertDate(end));
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
   * org.wattdepot.client.WattDepotInterface#getMeasurementType(java.lang.String
   * )
   */
  @Override
  public MeasurementType getMeasurementType(String id) throws IdNotFoundException {
    ClientResource client = makeClient(Labels.PUBLIC + "/" + Labels.MEASUREMENT_TYPE + "/" + id);
    MeasurementTypeResource resource = client.wrap(MeasurementTypeResource.class);
    try {
      MeasurementType ret = resource.retrieve();
      client.release();
      return ret;
    }
    catch (ResourceException e) {
      throw new IdNotFoundException(id + " is not a known MeasurementType. ");
    }
    finally {
      if (client != null) {
        client.release();
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.client.WattDepotInterface#getMeasurementTypes()
   */
  @Override
  public MeasurementTypeList getMeasurementTypes() {
    ClientResource client = makeClient(Labels.PUBLIC + "/" + Labels.MEASUREMENT_TYPES + "/");
    MeasurementTypesResource resource = client.wrap(MeasurementTypesResource.class);
    MeasurementTypeList ret = resource.retrieve();
    client.release();
    return ret;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.client.WattDepotInterface#getSensor(java.lang.String)
   */
  @Override
  public Sensor getSensor(String id) throws IdNotFoundException {
    ClientResource client = makeClient(this.groupId + "/" + Labels.SENSOR + "/" + id);
    SensorResource resource = client.wrap(SensorResource.class);
    try {
      Sensor ret = resource.retrieve();
      client.release();
      return ret;
    }
    catch (ResourceException e) {
      throw new IdNotFoundException(id + " is not a known Sensor. ");
    }
    finally {
      if (client != null) {
        client.release();
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.client.WattDepotInterface#getSensorGroup(java.lang.String)
   */
  @Override
  public SensorGroup getSensorGroup(String id) throws IdNotFoundException {
    ClientResource client = makeClient(this.groupId + "/" + Labels.SENSOR_GROUP + "/" + id);
    SensorGroupResource resource = client.wrap(SensorGroupResource.class);
    try {
      SensorGroup ret = resource.retrieve();
      client.release();
      return ret;
    }
    catch (ResourceException e) {
      throw new IdNotFoundException(id + " is not a known SensorGroup. ");
    }
    finally {
      if (client != null) {
        client.release();
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.client.WattDepotInterface#getSensorGroups()
   */
  @Override
  public SensorGroupList getSensorGroups() {
    ClientResource client = makeClient(this.groupId + "/" + Labels.SENSOR_GROUPS + "/");
    SensorGroupsResource resource = client.wrap(SensorGroupsResource.class);
    SensorGroupList ret = resource.retrieve();
    client.release();
    return ret;

  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.client.WattDepotInterface#getSensorModel(java.lang.String)
   */
  @Override
  public SensorModel getSensorModel(String id) throws IdNotFoundException {
    ClientResource client = makeClient(Labels.PUBLIC + "/" + Labels.SENSOR_MODEL + "/" + id);
    SensorModelResource resource = client.wrap(SensorModelResource.class);
    try {
      SensorModel ret = resource.retrieve();
      client.release();
      return ret;
    }
    catch (ResourceException e) {
      throw new IdNotFoundException(id + " is not a known SensorModel. ");
    }
    finally {
      if (client != null) {
        client.release();
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.client.WattDepotInterface#getSensorModels()
   */
  @Override
  public SensorModelList getSensorModels() {
    ClientResource client = makeClient(Labels.PUBLIC + "/" + Labels.SENSOR_MODELS + "/");
    SensorModelsResource resource = client.wrap(SensorModelsResource.class);
    SensorModelList ret = resource.retrieve();
    client.release();
    return ret;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.client.WattDepotInterface#getSensors()
   */
  @Override
  public SensorList getSensors() {
    ClientResource client = makeClient(this.groupId + "/" + Labels.SENSORS + "/");
    SensorsResource resource = client.wrap(SensorsResource.class);
    SensorList ret = resource.retrieve();
    client.release();
    return ret;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.client.WattDepotInterface#getValue(org.wattdepot.datamodel
   * .Depository, org.wattdepot.datamodel.Sensor, java.util.Date)
   */
  @Override
  public Double getValue(Depository depository, Sensor sensor, Date timestamp)
      throws NoMeasurementException {
    ClientResource client = null;
    try {
      client = makeClient(this.groupId + "/" + Labels.DEPOSITORY + "/" + depository.getId() + "/"
          + Labels.VALUE + "/" + "?sensor=" + sensor.getId() + "&timestamp="
          + DateConvert.convertDate(timestamp));
      DepositoryValueResource resource = client.wrap(DepositoryValueResource.class);
      MeasuredValue ret = resource.retrieve();
      client.release();
      if (ret != null) {
        return ret.getValue();
      }
    }
    catch (DatatypeConfigurationException e) {
      throw new NoMeasurementException(e.getMessage());
    }
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.client.WattDepotInterface#getValue(org.wattdepot.datamodel
   * .Depository, org.wattdepot.datamodel.Sensor, java.util.Date,
   * java.util.Date)
   */
  @Override
  public Double getValue(Depository depository, Sensor sensor, Date start, Date end)
      throws NoMeasurementException {
    ClientResource client = null;
    try {
      client = makeClient(this.groupId + "/" + Labels.DEPOSITORY + "/" + depository.getId() + "/"
          + Labels.VALUE + "/" + "?sensor=" + sensor.getId() + "&start="
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
   * org.wattdepot.client.WattDepotInterface#getValue(org.wattdepot.datamodel
   * .Depository, org.wattdepot.datamodel.Sensor, java.util.Date,
   * java.util.Date, java.lang.Long)
   */
  @Override
  public Double getValue(Depository depository, Sensor sensor, Date start, Date end, Long gapSeconds)
      throws NoMeasurementException, MeasurementGapException {
    ClientResource client = null;
    try {
      client = makeClient(this.groupId + "/" + Labels.DEPOSITORY + "/" + depository.getId() + "/"
          + Labels.VALUE + "/" + "?sensor=" + sensor.getId() + "&start="
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
   * org.wattdepot.client.WattDepotInterface#getValue(org.wattdepot.datamodel
   * .Depository, org.wattdepot.datamodel.Sensor, java.util.Date,
   * java.lang.Long)
   */
  @Override
  public Double getValue(Depository depository, Sensor sensor, Date timestamp, Long gapSeconds)
      throws NoMeasurementException, MeasurementGapException {
    ClientResource client = null;
    try {
      client = makeClient(this.groupId + "/" + Labels.DEPOSITORY + "/" + depository.getId() + "/"
          + Labels.VALUE + "/" + "?sensor=" + sensor.getId() + "&timestamp="
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
   * @see org.wattdepot.client.WattDepotInterface#isHealthy()
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
    logger.fine(this.wattDepotUri + requestString);
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
   * org.wattdepot.client.WattDepotInterface#putCollectorMetaData(org.wattdepot
   * .datamodel.CollectorMetaData)
   */
  @Override
  public void putCollectorMetaData(CollectorMetaData process) {
    ClientResource client = makeClient(this.groupId + "/" + Labels.COLLECTOR_META_DATA + "/"
        + process.getId());
    CollectorMetaDataResource resource = client.wrap(CollectorMetaDataResource.class);
    try {
      resource.store(process);
    }
    finally {
      client.release();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.client.WattDepotInterface#putDepository(org.wattdepot.datamodel
   * .Depository)
   */
  @Override
  public void putDepository(Depository depository) {
    ClientResource client = makeClient(this.groupId + "/" + Labels.DEPOSITORY + "/");
    DepositoryPutResource resource = client.wrap(DepositoryPutResource.class);
    try {
      resource.store(depository);
    }
    finally {
      client.release();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.client.WattDepotInterface#putLocation(org.wattdepot.datamodel
   * .Location)
   */
  @Override
  public void putLocation(SensorLocation loc) {
    ClientResource client = makeClient(this.groupId + "/" + Labels.LOCATION + "/" + loc.getId());
    SensorLocationResource resource = client.wrap(SensorLocationResource.class);
    try {
      resource.store(loc);
    }
    finally {
      client.release();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.client.WattDepotInterface#putMeasurement(org.wattdepot.
   * datamodel.Depository, org.wattdepot.datamodel.Measurement)
   */
  @Override
  public void putMeasurement(Depository depository, Measurement measurement)
      throws MeasurementTypeException {
    if (!depository.getMeasurementType().getUnits().equals(measurement.getMeasurementType())) {
      throw new MeasurementTypeException("Depository " + depository.getName() + " stores "
          + depository.getMeasurementType() + " not " + measurement.getMeasurementType());
    }
    ClientResource client = makeClient(this.groupId + "/" + Labels.DEPOSITORY + "/"
        + depository.getId() + "/" + Labels.MEASUREMENT + "/");
    DepositoryMeasurementPutResource resource = client.wrap(DepositoryMeasurementPutResource.class);
    try {
      resource.store(measurement);
    }
    catch (ResourceException e) {
      e.printStackTrace();
    }
    finally {
      client.release();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.client.WattDepotInterface#putMeasurementType(org.wattdepot
   * .datamodel.MeasurementType)
   */
  @Override
  public void putMeasurementType(MeasurementType type) {
    ClientResource client = makeClient(Labels.PUBLIC + "/" + Labels.MEASUREMENT_TYPE + "/");
    MeasurementTypePutResource resource = client.wrap(MeasurementTypePutResource.class);
    try {
      resource.store(type);
    }
    finally {
      client.release();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.client.WattDepotInterface#putSensor(org.wattdepot.datamodel
   * .Sensor)
   */
  @Override
  public void putSensor(Sensor sensor) {
    ClientResource client = makeClient(this.groupId + "/" + Labels.SENSOR + "/" + sensor.getId());
    SensorResource resource = client.wrap(SensorResource.class);
    try {
      resource.store(sensor);
    }
    finally {
      client.release();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.client.WattDepotInterface#putSensorGroup(org.wattdepot.
   * datamodel.SensorGroup)
   */
  @Override
  public void putSensorGroup(SensorGroup group) {
    ClientResource client = makeClient(this.groupId + "/" + Labels.SENSOR_GROUP + "/"
        + group.getId());
    SensorGroupResource resource = client.wrap(SensorGroupResource.class);
    try {
      resource.store(group);
    }
    finally {
      client.release();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.client.WattDepotInterface#putSensorModel(org.wattdepot.
   * datamodel.SensorModel)
   */
  @Override
  public void putSensorModel(SensorModel model) {
    ClientResource client = makeClient(Labels.PUBLIC + "/" + Labels.SENSOR_MODEL + "/"
        + model.getId());
    SensorModelPutResource resource = client.wrap(SensorModelPutResource.class);
    try {
      resource.store(model);
    }
    finally {
      client.release();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.client.WattDepotInterface#updateCollectorMetaData(org.wattdepot
   * .datamodel.CollectorMetaData)
   */
  @Override
  public void updateCollectorMetaData(CollectorMetaData process) {
    putCollectorMetaData(process);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.client.WattDepotInterface#updateDepository(org.wattdepot
   * .datamodel.Depository)
   */
  @Override
  public void updateDepository(Depository depository) {
    throw new RuntimeException("Can't update an existing Depository.");
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.client.WattDepotInterface#updateLocation(org.wattdepot.
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
   * org.wattdepot.client.WattDepotInterface#updateMeasurementType(org.wattdepot
   * .datamodel.MeasurementType)
   */
  @Override
  public void updateMeasurementType(MeasurementType type) {
    ClientResource client = makeClient(Labels.PUBLIC + "/" + Labels.MEASUREMENT_TYPE + "/"
        + type.getId());
    MeasurementTypeResource resource = client.wrap(MeasurementTypeResource.class);
    try {
      resource.update(type);
    }
    finally {
      client.release();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.client.WattDepotInterface#updateSensor(org.wattdepot.datamodel
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
   * org.wattdepot.client.WattDepotInterface#updateSensorGroup(org.wattdepot
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
   * org.wattdepot.client.WattDepotInterface#updateSensorModel(org.wattdepot
   * .datamodel.SensorModel)
   */
  @Override
  public void updateSensorModel(SensorModel model) {
    ClientResource client = makeClient(Labels.PUBLIC + "/" + Labels.SENSOR_MODEL + "/"
        + model.getId());
    SensorModelResource resource = client.wrap(SensorModelResource.class);
    try {
      resource.update(model);
    }
    finally {
      client.release();
    }
  }

}
