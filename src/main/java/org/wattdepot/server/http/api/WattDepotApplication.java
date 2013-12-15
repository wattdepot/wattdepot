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

import java.util.HashMap;
import java.util.Map;

import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.data.ChallengeScheme;
import org.restlet.resource.Directory;
import org.restlet.routing.Redirector;
import org.restlet.routing.Router;
import org.restlet.security.ChallengeAuthenticator;
import org.wattdepot.common.http.api.API;
import org.wattdepot.server.WattDepotPersistence;

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

  /**
   * Default constructor.
   */
  public WattDepotApplication() {
    setName("WattDepot Application");
    setDescription("WattDepot HTTP API implementation");
    setAuthor("Cam Moore");
    sessions = new HashMap<String, WebSession>();
  }

  /**
   * @param id
   *          The WebSession id.
   * @return The WebSession with the given id or null.
   */
  public WebSession getWebSession(String id) {
    return sessions.get(id);
  }

  /**
   * Creates a new WebSession for the given user with their password. If their
   * password doesn't match returns null.
   * 
   * @param username
   *          The unique id for the user.
   * @param password
   *          Their password.
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
   * @param id
   *          The id of the session.
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
   * @param depot
   *          the depot to set
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
   * @param component
   *          the component to set
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
    String webRoot = "file:///" + System.getProperty("user.dir") + "/target/classes";
    Directory directory = new Directory(getContext(), webRoot);
    directory.setListingAllowed(true);
    router.attach("/webroot/", directory);

    // Use CLAP (ClassLoader Access Protocol) to access SPA directory from
    // filesystem or JAR
    // Directory directory = new Directory(getContext(),
    // "clap://application/spa/");
    String spaRoot = "file:///" + System.getProperty("user.dir") + "/target/classes/dist/spa";
    Directory spaDirectory = new Directory(getContext(), spaRoot);
    spaDirectory.setIndexName("index.html");
    router.attach("/spa/", spaDirectory);
    router.attach("/spa", spaDirectory);

    // For some reason, going directly to /spa/ does not direct users to
    // index.hml, so create a
    // different path that sends clients directly there.
    Redirector redirector = new Redirector(getContext(), "/wattdepot/spa/index.html",
        Redirector.MODE_CLIENT_PERMANENT);
    router.attach("/app/", redirector);

    // router.attach("/wattdepot/", LoginPageServerResource.class);
    // router.attach("/wattdepot/login/", LoginServerResource.class);
    // Group administration UI.
    router.attach(API.ADMIN_URI, AdminServerResource.class);
    // CollectorMetaData
    router.attach(API.COLLECTOR_META_DATA_PUT_URI, CollectorMetaDataPutServerResource.class);
    router.attach(API.COLLECTOR_META_DATA_URI, CollectorMetaDataServerResource.class);
    router.attach(API.COLLECTOR_META_DATAS_URI, CollectorMetaDatasServerResource.class);
    // Depositories and Measurements
    router.attach(API.DEPOSITORY_PUT_URI, DepositoryPutServerResource.class);
    router.attach(API.DEPOSITORY_URI, DepositoryServerResource.class);
    router.attach(API.DEPOSITORIES_URI, DepositoriesServerResource.class);
    router.attach(API.DEPOSITORY_SENSORS_URI, DepositorySensorsServerResource.class);
    router.attach(API.MEASUREMENT_PUT_URI, DepositoryMeasurementPutServerResource.class);
    router.attach(API.MEASUREMENT_URI, DepositoryMeasurementServerResource.class);
    router.attach(API.MEASUREMENTS_URI, DepositoryMeasurementsServerResource.class);
    router.attach(API.MEASUREMENTS_GVIZ_URI, GvizDepositoryMeasurementsServerResource.class);
    router.attach(API.VALUE_URI, DepositoryValueServerResource.class);
    router.attach(API.VALUE_GVIZ_URI, GvizDepositoryValueServerResource.class);
    // MeasurementTypes
    router.attach(API.MEASUREMENT_TYPE_PUT_URI, MeasurementTypePutServerResource.class);
    router.attach(API.MEASUREMENT_TYPE_URI, MeasurementTypeServerResource.class);
    router.attach(API.MEASUREMENT_TYPES_URI, MeasurementTypesServerResource.class);
    // Sensors
    router.attach(API.SENSOR_PUT_URI, SensorPutServerResource.class);
    router.attach(API.SENSOR_URI, SensorServerResource.class);
    router.attach(API.SENSORS_URI, SensorsServerResource.class);
    // SensorGroups
    router.attach(API.SENSOR_GROUP_PUT_URI, SensorGroupPutServerResource.class);
    router.attach(API.SENSOR_GROUP_URI, SensorGroupServerResource.class);
    router.attach(API.SENSOR_GROUPS_URI, SensorGroupsServerResource.class);
    // SensorLocations
    router.attach(API.SENSOR_LOCATION_PUT_URI, SensorLocationPutServerResource.class);
    router.attach(API.SENSOR_LOCATION_URI, SensorLocationServerResource.class);
    router.attach(API.SENSOR_LOCATIONS_URI, SensorLocationsServerResource.class);
    // SensorModels
    router.attach(API.SENSOR_MODEL_PUT_URI, SensorModelPutServerResource.class);
    router.attach(API.SENSOR_MODEL_URI, SensorModelServerResource.class);
    router.attach(API.SENSOR_MODELS_URI, SensorModelsServerResource.class);
    // Users, UserGroups, and UserPasswords
    router.attach(API.USER_URI, UserInfoServerResource.class);
    router.attach(API.USER_PASSWORD_URI, UserPasswordServerResource.class);
    router.attach(API.USER_GROUP_PUT_URI, UserGroupServerResource.class);
    router.attach(API.USER_GROUP_URI, UserGroupServerResource.class);
    router.attach(API.USER_GROUPS_URI, UserGroupsServerResource.class);

    ChallengeAuthenticator authenticator = new ChallengeAuthenticator(getContext(),
        ChallengeScheme.HTTP_BASIC, "WattDepot Realm");
    authenticator.setNext(router);
    return authenticator;

    // return router;
  }
}