/**
 * UserServerApplication.java This file is part of WattDepot.
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
package org.wattdepot.server.http.api;

import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.data.ChallengeScheme;
import org.restlet.resource.Directory;
import org.restlet.routing.Router;
import org.restlet.security.ChallengeAuthenticator;
import org.wattdepot.common.http.api.API;
import org.wattdepot.extension.WattDepotExtension;
import org.wattdepot.server.ServerProperties;
import org.wattdepot.server.WattDepotPersistence;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * UserServerApplication Server app.
 * 
 * @author Cam Moore
 * 
 */
public class WattDepotApplication extends Application {

  private WattDepotPersistence depot;
  private WattDepotComponent component;
  private Map<String, WebSession> sessions;
  private Map<String, WattDepotExtension> extensionMap;

  /**
   * Default constructor.
   */
  public WattDepotApplication() {
    setName("WattDepot Application");
    setDescription("WattDepot HTTP API implementation");
    setAuthor("Cam Moore");
    sessions = new HashMap<String, WebSession>();
    extensionMap = new HashMap<String, WattDepotExtension>();
  }

  /**
   * @param id The WebSession id.
   * @return The WebSession with the given id or null.
   */
  public WebSession getWebSession(String id) {
    return sessions.get(id);
  }

  /**
   * Creates a new WebSession for the given user with their password. If their
   * password doesn't match returns null.
   * 
   * @param username The unique id for the user.
   * @param password Their password.
   * @return A new WebSession or null if the password doesn't match the password
   *         in the persistent store.
   */
  public WebSession createWebSession(String username, String password) {
    WebSession ret = null;
    // UserInfo info = depot.getUser(username);
    // if (info != null) {
    // UserGroup group = depot.getUsersGroup(info);
    // if (group != null) {
    // if (password.equals(info.getPassword())) {
    // String id = "" + info.hashCode() + group.hashCode() + new
    // Date().getTime();
    // ret = new WebSession(id, info.getId(), group.getId());
    // sessions.put(id, ret);
    // }
    // }
    // }
    return ret;
  }

  /**
   * Removes the WebSession from the application.
   * 
   * @param id The id of the session.
   * @return The old WebSession if it existed or null.
   */
  public WebSession removeWebSession(String id) {
    return sessions.remove(id);
  }

  /**
   * @return the depot
   */
  public WattDepotPersistence getDepot() {
    return depot;
  }

  /**
   * @param depot the depot to set
   */
  public void setDepot(WattDepotPersistence depot) {
    this.depot = depot;
  }

  /**
   * @return the component
   */
  public WattDepotComponent getComponent() {
    return component;
  }

  /**
   * @param component the component to set
   */
  public void setComponent(WattDepotComponent component) {
    this.component = component;
  }

  /**
   * Creates a root Router to dispatch call to server resources.
   * 
   * @return the inbound root.
   */
  @Override
  public Restlet createInboundRoot() {
    Router router = new Router(getContext());

    // Use CLAP (ClassLoader Access Protocol) to access SPA directory from
    // filesystem or JAR
    Directory directory = new Directory(getContext(), "clap://dist/");
    directory.setListingAllowed(true);
    router.attach("/webroot/", directory);

    router.attach(API.BASE_URI, IsAliveServerResource.class);
    // router.attach("/wattdepot/", LoginPageServerResource.class);
    // router.attach("/wattdepot/login/", LoginServerResource.class);
    // Group administration UI.
    router.attach(API.ADMIN_URI, AdminServerResource.class);
    router.attach(API.ORGANIZATION_SUMMARY_URI, OrgSummaryServerResource.class);
    router.attach(API.ORGANIZATION_VISUALIZE_URI, OrgVisualizerServerResource.class);
    // CollectorProcessDefinition
    router.attach(API.COLLECTOR_PROCESS_DEFINITION_PUT_URI,
        CollectorProcessDefinitionPutServerResource.class);
    router.attach(API.COLLECTOR_PROCESS_DEFINITION_URI,
        CollectorProcessDefinitionServerResource.class);
    router.attach(API.COLLECTOR_PROCESS_DEFINITIONS_URI,
        CollectorProcessDefinitionsServerResource.class);
    // Depositories and Measurements
    router.attach(API.DEPOSITORY_PUT_URI, DepositoryPutServerResource.class);
    router.attach(API.DEPOSITORY_URI, DepositoryServerResource.class);
    router.attach(API.DEPOSITORIES_URI, DepositoriesServerResource.class);
    router.attach(API.DEPOSITORY_SENSORS_URI, DepositorySensorsServerResource.class);
    router.attach(API.DEPOSITORY_SENSOR_SUMMARY_URI, DepositorySensorSummaryServerResource.class);
    router.attach(API.MEASUREMENT_PRUNING_DEFINITION_PUT_URI, MeasurementPruningDefinitionPutServerResource.class);
    router.attach(API.MEASUREMENT_PRUNING_DEFINITION_URI, MeasurementPruningDefinitionServerResource.class);
    router.attach(API.MEASUREMENT_PRUNING_DEFINITIONS_URI, MeasurementPruningDefinitionsServerResource.class);
    router.attach(API.MEASUREMENT_PUT_URI, DepositoryMeasurementPutServerResource.class);
    router.attach(API.MEASUREMENTS_PUT_URI, DepositoryMeasurementsPutServerResource.class);
    router.attach(API.MEASUREMENT_URI, DepositoryMeasurementServerResource.class);
    router.attach(API.MEASUREMENTS_URI, DepositoryMeasurementsServerResource.class);
    router.attach(API.MEASUREMENTS_GVIZ_URI, GvizDepositoryMeasurementsServerResource.class);
    router.attach(API.VALUE_URI, DepositoryValueServerResource.class);
    router.attach(API.VALUE_GVIZ_URI, GvizDepositoryValueServerResource.class);
    router.attach(API.VALUES_URI, DepositoryValuesServerResource.class);
    router.attach(API.VALUES_GVIZ_URI, GvizDepositoryValuesServerResource.class);
    router.attach(API.VALUES_AVERAGE_URI, DepositoryAverageValuesServerResource.class);
    router.attach(API.VALUES_AVERAGE_GVIZ_URI, GvizDepositoryAverageValuesServerResource.class);
    router.attach(API.VALUES_MAXIMUM_URI, DepositoryMaximumValuesServerResource.class);
    router.attach(API.VALUES_MINIMUM_URI, DepositoryMinimumValuesServerResource.class);
    router.attach(API.DAY_HOURLY_VALUES_URI, DepositoryDayHourlyValuesServerResource.class);
    router.attach(API.HOURLY_VALUES_URI, DepositoryHourlyValuesServerResource.class);
    // MeasurementTypes
    router.attach(API.MEASUREMENT_TYPE_PUT_URI, MeasurementTypePutServerResource.class);
    router.attach(API.MEASUREMENT_TYPE_URI, MeasurementTypeServerResource.class);
    router.attach(API.MEASUREMENT_TYPES_URI, MeasurementTypesServerResource.class);
    // Sensors
    router.attach(API.SENSOR_PUT_URI, SensorPutServerResource.class);
    router.attach(API.SENSOR_URI, SensorServerResource.class);
    router.attach(API.SENSORS_URI, SensorsServerResource.class);
    // Sensor Measurement Summary
    router.attach(API.SENSOR_MEASUREMENT_SUMMARY_URI, SensorMeasurementServerResource.class);
    // SensorGroups
    router.attach(API.SENSOR_GROUP_PUT_URI, SensorGroupPutServerResource.class);
    router.attach(API.SENSOR_GROUP_URI, SensorGroupServerResource.class);
    router.attach(API.SENSOR_GROUPS_URI, SensorGroupsServerResource.class);
    // SensorModels
    router.attach(API.SENSOR_MODEL_PUT_URI, SensorModelPutServerResource.class);
    router.attach(API.SENSOR_MODEL_URI, SensorModelServerResource.class);
    router.attach(API.SENSOR_MODELS_URI, SensorModelsServerResource.class);
    // Users, UserGroups, and UserPasswords
    router.attach(API.USER_PUT_URI, UserInfoPutServerResource.class);
    router.attach(API.USER_URI, UserInfoServerResource.class);
    router.attach(API.USERS_URI, UserInfosServerResource.class);
    router.attach(API.USER_PASSWORD_URI, UserPasswordServerResource.class);
    router.attach(API.ORGANIZATION_PUT_URI, OrganizationPutServerResource.class);
    router.attach(API.ORGANIZATION_URI, OrganizationServerResource.class);
    router.attach(API.ORGANIZATIONS_URI, OrganizationsServerResource.class);
    ServerProperties properties = depot.getServerProperties();
    List<WattDepotExtension> extensions = (List<WattDepotExtension>) properties.getPropertyInstance(ServerProperties.WATTDEPOT_EXTENSIONS);
    for (WattDepotExtension extension : extensions) {
      String baseURI = extension.getBaseURI();
      if (!extensionMap.containsKey(baseURI)) {
        extensionMap.put(baseURI, extension);
        Map<String, Class<? extends WattDepotServerResource>> resourceMap = extension.getServerResourceMapping();
        for (Map.Entry<String, Class<? extends WattDepotServerResource>> entry : resourceMap.entrySet()) {
          router.attach(entry.getKey(), entry.getValue());
        }
      }
    }

    ChallengeAuthenticator authenticator = new ChallengeAuthenticator(getContext(),
        ChallengeScheme.HTTP_BASIC, "WattDepot Realm");
    authenticator.setNext(router);
    return authenticator;

    // return router;
  }
}