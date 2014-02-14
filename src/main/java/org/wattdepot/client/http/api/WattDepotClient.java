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
import org.wattdepot.common.domainmodel.CollectorProcessDefinition;
import org.wattdepot.common.domainmodel.CollectorProcessDefinitionList;
import org.wattdepot.common.domainmodel.Depository;
import org.wattdepot.common.domainmodel.DepositoryList;
import org.wattdepot.common.domainmodel.InterpolatedValue;
import org.wattdepot.common.domainmodel.Labels;
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
import org.wattdepot.common.exception.BadCredentialException;
import org.wattdepot.common.exception.IdNotFoundException;
import org.wattdepot.common.exception.MeasurementGapException;
import org.wattdepot.common.exception.MeasurementTypeException;
import org.wattdepot.common.exception.NoMeasurementException;
import org.wattdepot.common.http.api.CollectorProcessDefinitionPutResource;
import org.wattdepot.common.http.api.CollectorProcessDefinitionResource;
import org.wattdepot.common.http.api.CollectorProcessDefinitionsResource;
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
import org.wattdepot.common.http.api.SensorGroupPutResource;
import org.wattdepot.common.http.api.SensorGroupResource;
import org.wattdepot.common.http.api.SensorGroupsResource;
import org.wattdepot.common.http.api.SensorModelPutResource;
import org.wattdepot.common.http.api.SensorModelResource;
import org.wattdepot.common.http.api.SensorModelsResource;
import org.wattdepot.common.http.api.SensorPutResource;
import org.wattdepot.common.http.api.SensorResource;
import org.wattdepot.common.http.api.SensorsResource;
import org.wattdepot.common.util.DateConvert;
import org.wattdepot.common.util.logger.WattDepotLogger;
import org.wattdepot.common.util.logger.WattDepotLoggerUtil;

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
  private String organizationId;
  /** The logger for this client. */
  private Logger logger;
  /** The client properties. */
  private ClientProperties properties;

  /**
   * Creates a new WattDepotClient.
   * 
   * @param serverUri The URI of the WattDepot server (e.g.
   *        "http://server.wattdepot.org/")
   * @param username The name of the user. The user must be defined in the
   *        WattDepot server.
   * @param orgId the organization the user is in.
   * @param password The password for the user.
   * @throws BadCredentialException If the user or password don't match the
   *         credentials on the WattDepot server.
   */
  public WattDepotClient(String serverUri, String username, String orgId,
      String password) throws BadCredentialException {
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
    client = makeClient(orgId + "/");
    WattDepotLoggerUtil.removeClientLoggerHandlers();
    try {
      client.head();
      if (client.getLocationRef() != null) {
        String path = client.getLocationRef().getPath();
        path = path.substring(0, path.length() - 1);
        int lastSlash = path.lastIndexOf('/') + 1;
        organizationId = path.substring(lastSlash);
      }
      else {
        organizationId = orgId;
      }
      client.release();
    }
    catch (ResourceException e) {
      throw new BadCredentialException(e.getMessage()
          + " username and or password are not corect.");
    }
    WattDepotLoggerUtil.removeClientLoggerHandlers();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.client.WattDepotInterface#deleteCollectorProcessDefinition
   * (org.wattdepot .datamodel.CollectorProcessDefinition)
   */
  @Override
  public void deleteCollectorProcessDefinition(
      CollectorProcessDefinition process) throws IdNotFoundException {
    ClientResource client = makeClient(this.organizationId + "/"
        + Labels.COLLECTOR_PROCESS_DEFINITION + "/" + process.getId());
    CollectorProcessDefinitionResource resource = client
        .wrap(CollectorProcessDefinitionResource.class);
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
  public void deleteDepository(Depository depository)
      throws IdNotFoundException {
    ClientResource client = makeClient(this.organizationId + "/"
        + Labels.DEPOSITORY + "/" + depository.getId());
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
   * @see
   * org.wattdepot.client.WattDepotInterface#deleteMeasurement(org.wattdepot
   * .datamodel.Depository, org.wattdepot.datamodel.Measurement)
   */
  @Override
  public void deleteMeasurement(Depository depository, Measurement measurement)
      throws IdNotFoundException {
    ClientResource client = makeClient(this.organizationId + "/"
        + Labels.DEPOSITORY + "/" + depository.getId() + "/"
        + Labels.MEASUREMENT + "/" + measurement.getId());
    DepositoryMeasurementResource resource = client
        .wrap(DepositoryMeasurementResource.class);
    try {
      resource.remove();
    }
    catch (ResourceException e) {
      throw new IdNotFoundException(measurement
          + " is not stored in WattDepot.");
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
  public void deleteMeasurementType(MeasurementType type)
      throws IdNotFoundException {
    ClientResource client = makeClient(Labels.PUBLIC + "/"
        + Labels.MEASUREMENT_TYPE + "/" + type.getId());
    MeasurementTypeResource resource = client
        .wrap(MeasurementTypeResource.class);
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
    ClientResource client = makeClient(this.organizationId + "/"
        + Labels.SENSOR + "/" + sensor.getId());
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
    ClientResource client = makeClient(this.organizationId + "/"
        + Labels.SENSOR_GROUP + "/" + group.getId());
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
    ClientResource client = makeClient(Labels.PUBLIC + "/"
        + Labels.SENSOR_MODEL + "/" + model.getId());
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
   * @see
   * org.wattdepot.client.WattDepotInterface#getCollectorProcessDefinitions()
   */
  @Override
  public CollectorProcessDefinitionList getCollectorProcessDefinitions() {
    ClientResource client = makeClient(this.organizationId + "/"
        + Labels.COLLECTOR_PROCESS_DEFINITIONS + "/");
    CollectorProcessDefinitionsResource resource = client
        .wrap(CollectorProcessDefinitionsResource.class);
    CollectorProcessDefinitionList ret = resource.retrieve();
    client.release();
    return ret;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.client.WattDepotInterface#getCollectorProcessDefinition(java
   * .lang .String)
   */
  @Override
  public CollectorProcessDefinition getCollectorProcessDefinition(String id)
      throws IdNotFoundException {
    ClientResource client = makeClient(this.organizationId + "/"
        + Labels.COLLECTOR_PROCESS_DEFINITION + "/" + id);
    CollectorProcessDefinitionResource resource = client
        .wrap(CollectorProcessDefinitionResource.class);
    try {
      CollectorProcessDefinition ret = resource.retrieve();
      client.release();
      return ret;
    }
    catch (ResourceException e) {
      throw new IdNotFoundException(id
          + " is not a known CollectorProcessDefinition. ");
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
   * @see org.wattdepot.client.WattDepotInterface#getDepositories()
   */
  @Override
  public DepositoryList getDepositories() {
    ClientResource client = makeClient(this.organizationId + "/"
        + Labels.DEPOSITORIES + "/");
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
    ClientResource client = makeClient(this.organizationId + "/"
        + Labels.DEPOSITORY + "/" + id);
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

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.client.WattDepotInterface#getEarliestMeasurement(org.wattdepot
   * .common.domainmodel.Depository, org.wattdepot.common.domainmodel.Sensor)
   */
  @Override
  public InterpolatedValue getEarliestValue(Depository depository, Sensor sensor) {
    ClientResource client = makeClient(this.organizationId + "/"
        + Labels.DEPOSITORY + "/" + depository.getId() + "/" + Labels.VALUE
        + "/" + "?sensor=" + sensor.getId() + "&earliest=true");
    DepositoryValueResource resource = client
        .wrap(DepositoryValueResource.class);
    InterpolatedValue ret = resource.retrieve();
    client.release();
    return ret;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.client.WattDepotInterface#getEarliestValue(org.wattdepot.
   * common.domainmodel.Depository,
   * org.wattdepot.common.domainmodel.SensorGroup)
   */
  @Override
  public InterpolatedValue getEarliestValue(Depository depository,
      SensorGroup group) {
    ClientResource client = makeClient(this.organizationId + "/"
        + Labels.DEPOSITORY + "/" + depository.getId() + "/" + Labels.VALUE
        + "/" + "?sensor=" + group.getId() + "&earliest=true");
    DepositoryValueResource resource = client
        .wrap(DepositoryValueResource.class);
    InterpolatedValue ret = resource.retrieve();
    client.release();
    return ret;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.client.WattDepotInterface#getLatestMeasurement(org.wattdepot
   * .common.domainmodel.Depository, org.wattdepot.common.domainmodel.Sensor)
   */
  @Override
  public InterpolatedValue getLatestValue(Depository depository, Sensor sensor) {
    ClientResource client = makeClient(this.organizationId + "/"
        + Labels.DEPOSITORY + "/" + depository.getId() + "/" + Labels.VALUE
        + "/" + "?sensor=" + sensor.getId() + "&latest=true");
    DepositoryValueResource resource = client
        .wrap(DepositoryValueResource.class);
    InterpolatedValue ret = resource.retrieve();
    client.release();
    return ret;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.client.WattDepotInterface#getLatestValue(org.wattdepot.common
   * .domainmodel.Depository, org.wattdepot.common.domainmodel.SensorGroup)
   */
  @Override
  public InterpolatedValue getLatestValue(Depository depository,
      SensorGroup group) {
    ClientResource client = makeClient(this.organizationId + "/"
        + Labels.DEPOSITORY + "/" + depository.getId() + "/" + Labels.VALUE
        + "/" + "?sensor=" + group.getId() + "&latest=true");
    DepositoryValueResource resource = client
        .wrap(DepositoryValueResource.class);
    InterpolatedValue ret = resource.retrieve();
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
  public MeasurementList getMeasurements(Depository depository, Sensor sensor,
      Date start, Date end) {
    try {
      ClientResource client = makeClient(this.organizationId + "/"
          + Labels.DEPOSITORY + "/" + depository.getId() + "/"
          + Labels.MEASUREMENTS + "/" + "?sensor=" + sensor.getId() + "&start="
          + DateConvert.convertDate(start) + "&end="
          + DateConvert.convertDate(end));
      DepositoryMeasurementsResource resource = client
          .wrap(DepositoryMeasurementsResource.class);
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
   * @see org.wattdepot.client.WattDepotInterface#getMeasurements(org.wattdepot
   * .datamodel.Depository, org.wattdepot.datamodel.SensorGroup, java.util.Date,
   * java.util.Date)
   */
  @Override
  public MeasurementList getMeasurements(Depository depository,
      SensorGroup group, Date start, Date end) {
    try {
      ClientResource client = makeClient(this.organizationId + "/"
          + Labels.DEPOSITORY + "/" + depository.getId() + "/"
          + Labels.MEASUREMENTS + "/" + "?sensor=" + group.getId() + "&start="
          + DateConvert.convertDate(start) + "&end="
          + DateConvert.convertDate(end));
      DepositoryMeasurementsResource resource = client
          .wrap(DepositoryMeasurementsResource.class);
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
  public MeasurementType getMeasurementType(String id)
      throws IdNotFoundException {
    ClientResource client = makeClient(Labels.PUBLIC + "/"
        + Labels.MEASUREMENT_TYPE + "/" + id);
    MeasurementTypeResource resource = client
        .wrap(MeasurementTypeResource.class);
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
    ClientResource client = makeClient(Labels.PUBLIC + "/"
        + Labels.MEASUREMENT_TYPES + "/");
    MeasurementTypesResource resource = client
        .wrap(MeasurementTypesResource.class);
    MeasurementTypeList ret = resource.retrieve();
    client.release();
    return ret;
  }

  /**
   * @return the organization id.
   */
  public String getOrganizationId() {
    return organizationId;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.client.WattDepotInterface#getSensor(java.lang.String)
   */
  @Override
  public Sensor getSensor(String id) throws IdNotFoundException {
    ClientResource client = makeClient(this.organizationId + "/"
        + Labels.SENSOR + "/" + id);
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
    ClientResource client = makeClient(this.organizationId + "/"
        + Labels.SENSOR_GROUP + "/" + id);
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
    ClientResource client = makeClient(this.organizationId + "/"
        + Labels.SENSOR_GROUPS + "/");
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
    ClientResource client = makeClient(Labels.PUBLIC + "/"
        + Labels.SENSOR_MODEL + "/" + id);
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
    ClientResource client = makeClient(Labels.PUBLIC + "/"
        + Labels.SENSOR_MODELS + "/");
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
    ClientResource client = makeClient(this.organizationId + "/"
        + Labels.SENSORS + "/");
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
      client = makeClient(this.organizationId + "/" + Labels.DEPOSITORY + "/"
          + depository.getId() + "/" + Labels.VALUE + "/" + "?sensor="
          + sensor.getId() + "&timestamp=" + DateConvert.convertDate(timestamp));
      DepositoryValueResource resource = client
          .wrap(DepositoryValueResource.class);
      InterpolatedValue ret = resource.retrieve();
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
  public Double getValue(Depository depository, Sensor sensor, Date start,
      Date end) throws NoMeasurementException {
    ClientResource client = null;
    try {
      client = makeClient(this.organizationId + "/" + Labels.DEPOSITORY + "/"
          + depository.getId() + "/" + Labels.VALUE + "/" + "?sensor="
          + sensor.getId() + "&start=" + DateConvert.convertDate(start)
          + "&end=" + DateConvert.convertDate(end));
      DepositoryValueResource resource = client
          .wrap(DepositoryValueResource.class);
      InterpolatedValue ret = resource.retrieve();
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
  public Double getValue(Depository depository, Sensor sensor, Date start,
      Date end, Long gapSeconds) throws NoMeasurementException,
      MeasurementGapException {
    ClientResource client = null;
    try {
      client = makeClient(this.organizationId + "/" + Labels.DEPOSITORY + "/"
          + depository.getId() + "/" + Labels.VALUE + "/" + "?sensor="
          + sensor.getId() + "&start=" + DateConvert.convertDate(start)
          + "&end=" + DateConvert.convertDate(end) + "&gap=" + gapSeconds);
      DepositoryValueResource resource = client
          .wrap(DepositoryValueResource.class);
      InterpolatedValue ret = resource.retrieve();
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
  public Double getValue(Depository depository, Sensor sensor, Date timestamp,
      Long gapSeconds) throws NoMeasurementException, MeasurementGapException {
    ClientResource client = null;
    try {
      client = makeClient(this.organizationId + "/" + Labels.DEPOSITORY + "/"
          + depository.getId() + "/" + Labels.VALUE + "/" + "?sensor="
          + sensor.getId() + "&timestamp=" + DateConvert.convertDate(timestamp)
          + "&gap=" + gapSeconds);
      DepositoryValueResource resource = client
          .wrap(DepositoryValueResource.class);
      InterpolatedValue ret = resource.retrieve();
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
   * .Depository, org.wattdepot.datamodel.SensorGroup, java.util.Date)
   */
  @Override
  public Double getValue(Depository depository, SensorGroup group,
      Date timestamp) throws NoMeasurementException {
    ClientResource client = null;
    try {
      client = makeClient(this.organizationId + "/" + Labels.DEPOSITORY + "/"
          + depository.getId() + "/" + Labels.VALUE + "/" + "?sensor="
          + group.getId() + "&timestamp=" + DateConvert.convertDate(timestamp));
      DepositoryValueResource resource = client
          .wrap(DepositoryValueResource.class);
      InterpolatedValue ret = resource.retrieve();
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
   * @see org.wattdepot.client.WattDepotInterface#getValue(org.wattdepot.common.
   * domainmodel.Depository, org.wattdepot.common.domainmodel.SensorGroup,
   * java.util.Date, java.util.Date)
   */
  @Override
  public Double getValue(Depository depository, SensorGroup group, Date start,
      Date end) throws NoMeasurementException {
    ClientResource client = null;
    try {
      client = makeClient(this.organizationId + "/" + Labels.DEPOSITORY + "/"
          + depository.getId() + "/" + Labels.VALUE + "/" + "?sensor="
          + group.getId() + "&start=" + DateConvert.convertDate(start)
          + "&end=" + DateConvert.convertDate(end));
      DepositoryValueResource resource = client
          .wrap(DepositoryValueResource.class);
      InterpolatedValue ret = resource.retrieve();
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
   * @see org.wattdepot.client.WattDepotInterface#getValue(org.wattdepot.common.
   * domainmodel.Depository, org.wattdepot.common.domainmodel.SensorGroup,
   * java.util.Date, java.util.Date, java.lang.Long)
   */
  @Override
  public Double getValue(Depository depository, SensorGroup group, Date start,
      Date end, Long gapSeconds) throws NoMeasurementException,
      MeasurementGapException {
    ClientResource client = null;
    try {
      client = makeClient(this.organizationId + "/" + Labels.DEPOSITORY + "/"
          + depository.getId() + "/" + Labels.VALUE + "/" + "?sensor="
          + group.getId() + "&start=" + DateConvert.convertDate(start)
          + "&end=" + DateConvert.convertDate(end) + "&gap=" + gapSeconds);
      DepositoryValueResource resource = client
          .wrap(DepositoryValueResource.class);
      InterpolatedValue ret = resource.retrieve();
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
   * @see org.wattdepot.client.WattDepotInterface#getValue(org.wattdepot.common.
   * domainmodel.Depository, org.wattdepot.common.domainmodel.SensorGroup,
   * java.util.Date, java.lang.Long)
   */
  @Override
  public Double getValue(Depository depository, SensorGroup group,
      Date timestamp, Long gapSeconds) throws NoMeasurementException,
      MeasurementGapException {
    ClientResource client = null;
    try {
      client = makeClient(this.organizationId + "/" + Labels.DEPOSITORY + "/"
          + depository.getId() + "/" + Labels.VALUE + "/" + "?sensor="
          + group.getId() + "&timestamp=" + DateConvert.convertDate(timestamp)
          + "&gap=" + gapSeconds);
      DepositoryValueResource resource = client
          .wrap(DepositoryValueResource.class);
      InterpolatedValue ret = resource.retrieve();
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
    ClientResource client = makeClient("");
    client.head();
    boolean healthy = client.getStatus().isSuccess();
    client.release();
    return healthy;
  }

  /**
   * Creates a ClientResource for the given request. Calling code MUST release
   * the ClientResource when finished.
   * 
   * @param requestString A String, the request portion of the WattDepot HTTP
   *        API, such as "
   * @return The client resource.
   */
  public ClientResource makeClient(String requestString) {
    logger.fine(this.wattDepotUri + requestString);
    Reference reference = new Reference(this.wattDepotUri + requestString);
    ClientResource client = new ClientResource(reference);
    client.setChallengeResponse(authentication);
    return client;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.client.WattDepotInterface#putCollectorProcessDefinition(org
   * .wattdepot .datamodel.CollectorProcessDefinition)
   */
  @Override
  public void putCollectorProcessDefinition(CollectorProcessDefinition process) {
    ClientResource client = makeClient(this.organizationId + "/"
        + Labels.COLLECTOR_PROCESS_DEFINITION + "/");
    CollectorProcessDefinitionPutResource resource = client
        .wrap(CollectorProcessDefinitionPutResource.class);
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
    ClientResource client = makeClient(this.organizationId + "/"
        + Labels.DEPOSITORY + "/");
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
   * @see org.wattdepot.client.WattDepotInterface#putMeasurement(org.wattdepot.
   * datamodel.Depository, org.wattdepot.datamodel.Measurement)
   */
  @Override
  public void putMeasurement(Depository depository, Measurement measurement)
      throws MeasurementTypeException {
    if (!depository.getMeasurementType().getUnits()
        .equals(measurement.getMeasurementType())) {
      throw new MeasurementTypeException("Depository " + depository.getName()
          + " stores " + depository.getMeasurementType() + " not "
          + measurement.getMeasurementType());
    }
    ClientResource client = makeClient(this.organizationId + "/"
        + Labels.DEPOSITORY + "/" + depository.getId() + "/"
        + Labels.MEASUREMENT + "/");
    DepositoryMeasurementPutResource resource = client
        .wrap(DepositoryMeasurementPutResource.class);
    try {
      resource.store(measurement);
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
    ClientResource client = makeClient(Labels.PUBLIC + "/"
        + Labels.MEASUREMENT_TYPE + "/");
    MeasurementTypePutResource resource = client
        .wrap(MeasurementTypePutResource.class);
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
    ClientResource client = makeClient(this.organizationId + "/"
        + Labels.SENSOR + "/");
    SensorPutResource resource = client.wrap(SensorPutResource.class);
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
    ClientResource client = makeClient(this.organizationId + "/"
        + Labels.SENSOR_GROUP + "/");
    SensorGroupPutResource resource = client.wrap(SensorGroupPutResource.class);
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
    ClientResource client = makeClient(Labels.PUBLIC + "/"
        + Labels.SENSOR_MODEL + "/");
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
   * org.wattdepot.client.WattDepotInterface#updateCollectorProcessDefinition
   * (org.wattdepot .datamodel.CollectorProcessDefinition)
   */
  @Override
  public void updateCollectorProcessDefinition(
      CollectorProcessDefinition process) {
    ClientResource client = makeClient(this.organizationId + "/"
        + Labels.COLLECTOR_PROCESS_DEFINITION + "/" + process.getId());
    CollectorProcessDefinitionResource resource = client
        .wrap(CollectorProcessDefinitionResource.class);
    try {
      resource.update(process);
    }
    finally {
      client.release();
    }
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
   * @see
   * org.wattdepot.client.WattDepotInterface#updateMeasurementType(org.wattdepot
   * .datamodel.MeasurementType)
   */
  @Override
  public void updateMeasurementType(MeasurementType type) {
    ClientResource client = makeClient(Labels.PUBLIC + "/"
        + Labels.MEASUREMENT_TYPE + "/" + type.getId());
    MeasurementTypeResource resource = client
        .wrap(MeasurementTypeResource.class);
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
    ClientResource client = makeClient(this.organizationId + "/"
        + Labels.SENSOR + "/" + sensor.getId());
    SensorResource resource = client.wrap(SensorResource.class);
    try {
      resource.update(sensor);
    }
    finally {
      client.release();
    }
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
    ClientResource client = makeClient(this.organizationId + "/"
        + Labels.SENSOR_GROUP + "/" + group.getId());
    SensorGroupResource resource = client.wrap(SensorGroupResource.class);
    try {
      resource.update(group);
    }
    finally {
      client.release();
    }
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
    ClientResource client = makeClient(Labels.PUBLIC + "/"
        + Labels.SENSOR_MODEL + "/" + model.getId());
    SensorModelResource resource = client.wrap(SensorModelResource.class);
    try {
      resource.update(model);
    }
    finally {
      client.release();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.client.WattDepotInterface#isDefinedCollectorProcessDefinition
   * (java.lang.String)
   */
  @Override
  public boolean isDefinedCollectorProcessDefinition(String id) {
    try {
      getCollectorProcessDefinition(id);
      return true;
    }
    catch (IdNotFoundException e) {
      return false;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.client.WattDepotInterface#isDefinedDepository(java.lang.String
   * )
   */
  @Override
  public boolean isDefinedDepository(String id) {
    try {
      getDepository(id);
      return true;
    }
    catch (IdNotFoundException e) {
      return false;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.client.WattDepotInterface#isDefinedMeasurementType(java.lang
   * .String)
   */
  @Override
  public boolean isDefinedMeasurementType(String id) {
    try {
      getMeasurementType(id);
      return true;
    }
    catch (IdNotFoundException e) {
      return false;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.client.WattDepotInterface#isDefinedSensor(java.lang.String)
   */
  @Override
  public boolean isDefinedSensor(String id) {
    try {
      getSensor(id);
      return true;
    }
    catch (IdNotFoundException e) {
      return false;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.client.WattDepotInterface#isDefinedSensorGroup(java.lang.
   * String)
   */
  @Override
  public boolean isDefinedSensorGroup(String id) {
    try {
      getSensorGroup(id);
      return true;
    }
    catch (IdNotFoundException e) {
      return false;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.client.WattDepotInterface#isDefinedSensorModel(java.lang.
   * String)
   */
  @Override
  public boolean isDefinedSensorModel(String id) {
    try {
      getSensorModel(id);
      return true;
    }
    catch (IdNotFoundException e) {
      return false;
    }
  }

}
