/**
 * API.java created This file is part of WattDepot.
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
package org.wattdepot.common.http.api;

import org.wattdepot.common.domainmodel.Labels;

/**
 * API - Defines the Strings that make up the HTTP API.
 * 
 * @author Cam Moore
 * 
 */
public class API {

  /**
   * <b>/wattdepot/</b> URI for seeing if the server is alive. Supports GET
   * requests.
   */
  public static final String BASE_URI = "/" + Labels.WATTDEPOT + "/";

  /**
   * <b>/wattdepot/{org-id}/</b> URI for the group administration User
   * Interface. Supports GET requests.
   */
  public static final String ADMIN_URI = BASE_URI + Labels.ORGANIZATION_ID_VAR + "/";

  /**
   * <b>/wattdepot/{org-id}/collector-process-definition/</b> URI for storing
   * CollectorProcessDefinition. Use PUT requests.
   */
  public static final String COLLECTOR_PROCESS_DEFINITION_PUT_URI = BASE_URI
      + Labels.ORGANIZATION_ID_VAR + "/" + Labels.COLLECTOR_PROCESS_DEFINITION + "/";

  /**
   * <b>/wattdepot/{org-id}/collector-process-definition/{collector-process-
   * definition-id}</b> URI for manipulating CollectorProcessDefinition.
   * Supports GET, POST, and DELETE requests.
   */
  public static final String COLLECTOR_PROCESS_DEFINITION_URI = BASE_URI
      + Labels.ORGANIZATION_ID_VAR + "/" + Labels.COLLECTOR_PROCESS_DEFINITION + "/"
      + Labels.COLLECTOR_PROCESS_DEFINITION_ID_VAR;

  /**
   * <b>/wattdepot/{org-id}/collector-process-definitions/</b> URI for getting
   * all the defined CollectorProcessDefinition. Use GET requests.
   */
  public static final String COLLECTOR_PROCESS_DEFINITIONS_URI = BASE_URI
      + Labels.ORGANIZATION_ID_VAR + "/" + Labels.COLLECTOR_PROCESS_DEFINITIONS + "/";

  /**
   * <b>/wattdepot/{org-id}/depositories/</b> URI for getting all Depositories.
   * Use GET requests.
   */
  public static final String DEPOSITORIES_URI = BASE_URI + Labels.ORGANIZATION_ID_VAR + "/"
      + Labels.DEPOSITORIES + "/";

  /**
   * <b>/wattdepot/{org-id}/depository/</b> URI for storing new Depositories.
   * Use PUT requests.
   */
  public static final String DEPOSITORY_PUT_URI = BASE_URI + Labels.ORGANIZATION_ID_VAR + "/"
      + Labels.DEPOSITORY + "/";

  /**
   * <b>/wattdepot/{org-id}/depository/{depository-id}/summary/</b> URI for
   * getting all the sensors that have stored measurements in the depository.
   * Use GET requests.
   */
  public static final String DEPOSITORY_SENSOR_SUMMARY_URI = BASE_URI + Labels.ORGANIZATION_ID_VAR
      + "/" + Labels.DEPOSITORY + "/" + Labels.DEPOSITORY_ID_VAR + "/" + Labels.SUMMARY + "/";

  /**
   * <b>/wattdepot/{org-id}/depository/{depository-id}/sensors/</b> URI for
   * getting all the sensors that have stored measurements in the depository.
   * Use GET requests.
   */
  public static final String DEPOSITORY_SENSORS_URI = BASE_URI + Labels.ORGANIZATION_ID_VAR + "/"
      + Labels.DEPOSITORY + "/" + Labels.DEPOSITORY_ID_VAR + "/" + Labels.SENSORS + "/";

  /**
   * <b>/wattdepot/{org-id}/depository/{depository-id}</b> URI for Depository
   * manipulation. Supports GET, POST, and DELETE requests.
   */
  public static final String DEPOSITORY_URI = BASE_URI + Labels.ORGANIZATION_ID_VAR + "/"
      + Labels.DEPOSITORY + "/" + Labels.DEPOSITORY_ID_VAR;

  /**
   * <b>/wattdepot/{org-id}/measurement-pruning-definition/</b> URI for putting a
   * MeasurementPruningDefinition. Use PUT requests.
   */
  public static final String MEASUREMENT_PRUNING_DEFINITION_PUT_URI = BASE_URI
      + Labels.ORGANIZATION_ID_VAR + "/" + Labels.MEASUREMENT_PRUNING_DEFINITION + "/";

  /**
   * <b>/wattdepot/{org-id}/measurement-pruning-definition/{measurement-pruning-
   * definition-id}</b> URI for getting a MeasurementPruningDefinition. Use GET
   * requests.
   */
  public static final String MEASUREMENT_PRUNING_DEFINITION_URI = BASE_URI
      + Labels.ORGANIZATION_ID_VAR + "/" + Labels.MEASUREMENT_PRUNING_DEFINITION + "/"
      + Labels.MEASUREMENT_PRUNING_DEFINITION_ID_VAR;

  /**
   * <b>/wattdepot/{org-id}/measurement-pruning-definitions/</b> URI for getting
   * all defined MeasurementPruningDefinition. Use GET requests.
   */
  public static final String MEASUREMENT_PRUNING_DEFINITIONS_URI = BASE_URI
      + Labels.ORGANIZATION_ID_VAR + "/" + Labels.MEASUREMENT_PRUNING_DEFINITIONS + "/";

  /**
   * <b>/wattdepot/{org-id}/depository/{depository-id}/measurement/</b> URI for
   * putting a measurement into the depository. Use PUT requests.
   */
  public static final String MEASUREMENT_PUT_URI = BASE_URI + Labels.ORGANIZATION_ID_VAR + "/"
      + Labels.DEPOSITORY + "/" + Labels.DEPOSITORY_ID_VAR + "/" + Labels.MEASUREMENT + "/";

  /**
   * <b>/wattdepot/{org-id}/depository/{depository-id}/measurements/</b> URI for
   * putting measurements into the depository. Use PUT requests.
   */

  //TODO
  public static final String MEASUREMENTS_PUT_URI = BASE_URI + Labels.ORGANIZATION_ID_VAR + "/"
          + Labels.DEPOSITORY + "/" + Labels.DEPOSITORY_ID_VAR + "/" + Labels.MEASUREMENTS + "/";

    /**
   * <b>/wattdepot/public/measurement-type/</b> URI for storing new
   * MeasurmentTypes. Use PUT requests.
   */
  public static final String MEASUREMENT_TYPE_PUT_URI = BASE_URI + Labels.PUBLIC + "/"
      + Labels.MEASUREMENT_TYPE + "/";

  /**
   * <b>/wattdepot/public/measurement-type/{measurement-type-id}</b> URI for
   * MeasurmentType manipulation. Supports GET, POST, and DELETE requests.
   */
  public static final String MEASUREMENT_TYPE_URI = BASE_URI + Labels.PUBLIC + "/"
      + Labels.MEASUREMENT_TYPE + "/" + Labels.MEASUREMENT_TYPE_ID_VAR;

  /**
   * <b>/wattdepot/public/measurement-types/</b> URI for getting all
   * MeasurmentTypes. Use GET requests.
   */
  public static final String MEASUREMENT_TYPES_URI = BASE_URI + Labels.PUBLIC + "/"
      + Labels.MEASUREMENT_TYPES + "/";

  /**
   * <b>/wattdepot/{org-id}/depository/{depository-id}/measurement/{
   * measurement-id}</b> URI for manipulating a measurement in the depsository.
   * Supports GET and DELETE requests.
   */
  public static final String MEASUREMENT_URI = BASE_URI + Labels.ORGANIZATION_ID_VAR + "/"
      + Labels.DEPOSITORY + "/" + Labels.DEPOSITORY_ID_VAR + "/" + Labels.MEASUREMENT + "/"
      + Labels.MEASUREMENT_ID_VAR;

  /**
   * <b>/wattdepot/{org-id}/depository/{depository-id}/measurements/gviz/</b>
   * URI to get all the measurements in the depsository. Use GET requests.
   */
  public static final String MEASUREMENTS_GVIZ_URI = BASE_URI + Labels.ORGANIZATION_ID_VAR + "/"
      + Labels.DEPOSITORY + "/" + Labels.DEPOSITORY_ID_VAR + "/" + Labels.MEASUREMENTS + "/"
      + Labels.GVIZ + "/";

  /**
   * <b>/wattdepot/{org-id}/depository/{depository-id}/measurements/</b> URI to
   * get all the measurements in the depsository. Use GET requests.
   */
  public static final String MEASUREMENTS_URI = BASE_URI + Labels.ORGANIZATION_ID_VAR + "/"
      + Labels.DEPOSITORY + "/" + Labels.DEPOSITORY_ID_VAR + "/" + Labels.MEASUREMENTS + "/";
  /**
   * <b>/wattdepot/{org-id}/organization/</b> URI to store UserGroups. Use PUT
   * requests.
   */
  public static final String ORGANIZATION_PUT_URI = BASE_URI + Labels.ORGANIZATION_ID_VAR + "/"
      + Labels.ORGANIZATION + "/";

  /**
   * <b>/wattdepot/{org-id}/summary/</b> URI for the Organization Summary User
   * Interface. Supports GET requests.
   */
  public static final String ORGANIZATION_SUMMARY_URI = BASE_URI + Labels.ORGANIZATION_ID_VAR + "/"
      + Labels.SUMMARY + "/";

  /**
   * <b>/wattdepot/{org-id}/user-group/{user-org-id}</b> URI to manipulate
   * SensorGroups. Supports GET, POST, and DELETE requests.
   */
  public static final String ORGANIZATION_URI = BASE_URI + Labels.ORGANIZATION_ID_VAR + "/"
      + Labels.ORGANIZATION + "/" + Labels.ORGANIZATION_ID2_VAR;

  /**
   * <b>/wattdepot/{org-id}/visualize/</b> URI for the Organization
   * Visualization User Interface. Supports GET requests.
   */
  public static final String ORGANIZATION_VISUALIZE_URI = BASE_URI + Labels.ORGANIZATION_ID_VAR
      + "/" + Labels.VISUALIZE + "/";

  /**
   * <b>/wattdepot/{org-id}/user-groups/</b> URI to get all defined
   * SensorGroups. Use GET requests.
   */
  public static final String ORGANIZATIONS_URI = BASE_URI + Labels.ORGANIZATION_ID_VAR + "/"
      + Labels.ORGANIZATIONS + "/";

  /**
   * <b>/wattdepot/{org-id}/sensor-group/</b> URI to store SensorGroups. Use PUT
   * requests.
   */
  public static final String SENSOR_GROUP_PUT_URI = BASE_URI + Labels.ORGANIZATION_ID_VAR + "/"
      + Labels.SENSOR_GROUP + "/";

  /**
   * <b>/wattdepot/{org-id}/sensor-group/{sensor-org-id}</b> URI to manipulate
   * SensorGroups. Supports GET, POST, and DELETE requests.
   */
  public static final String SENSOR_GROUP_URI = BASE_URI + Labels.ORGANIZATION_ID_VAR + "/"
      + Labels.SENSOR_GROUP + "/" + Labels.SENSOR_GROUP_ID_VAR;

  /**
   * <b>/wattdepot/{org-id}/sensor-groups/</b> URI to get all defined
   * SensorGroups. Use GET requests.
   */
  public static final String SENSOR_GROUPS_URI = BASE_URI + Labels.ORGANIZATION_ID_VAR + "/"
      + Labels.SENSOR_GROUPS + "/";

  /**
   * <b>/wattdepot/{org-id}/depository/{depository-id}/value/</b> URI to get all
   * the measured value. Use GET requests.
   */
  public static final String SENSOR_MEASUREMENT_SUMMARY_URI = BASE_URI + Labels.ORGANIZATION_ID_VAR
      + "/" + Labels.DEPOSITORY + "/" + Labels.DEPOSITORY_ID_VAR + "/" + Labels.SUMMARY + "/";

  /**
   * <b>/wattdepot/public/sensor-model/</b> URI for storing new SensorModels.
   * Use PUT requests.
   */
  public static final String SENSOR_MODEL_PUT_URI = BASE_URI + Labels.PUBLIC + "/"
      + Labels.SENSOR_MODEL + "/";

  /**
   * <b>/wattdepot/public/sensor-model/{sensor-model-id}</b> URI for SensorModel
   * manipulation. Supports GET, POST, and DELETE requests.
   */
  public static final String SENSOR_MODEL_URI = BASE_URI + Labels.PUBLIC + "/"
      + Labels.SENSOR_MODEL + "/" + Labels.SENSOR_MODEL_ID_VAR;

  /**
   * <b>/wattdepot/public/sensor-models/</b> URI for getting all SensorModels.
   * Use GET reqeusts.
   */
  public static final String SENSOR_MODELS_URI = BASE_URI + Labels.PUBLIC + "/"
      + Labels.SENSOR_MODELS + "/";

  /**
   * <b>/wattdepot/{org-id}/sensor/</b> URI to store Sensors. Use PUT requests.
   */
  public static final String SENSOR_PUT_URI = BASE_URI + Labels.ORGANIZATION_ID_VAR + "/"
      + Labels.SENSOR + "/";

  /**
   * <b>/wattdepot/{org-id}/sensor/{sensor-id}</b> URI to manipulate Sensors.
   * Supports GET, POST, and DELETE requests.
   */
  public static final String SENSOR_URI = BASE_URI + Labels.ORGANIZATION_ID_VAR + "/"
      + Labels.SENSOR + "/" + Labels.SENSOR_ID_VAR;

  /**
   * <b>/wattdepot/{org-id}/sensors/</b> URI to get all defined Sensors. Use GET
   * requests.
   */
  public static final String SENSORS_URI = BASE_URI + Labels.ORGANIZATION_ID_VAR + "/"
      + Labels.SENSORS + "/";

  /**
   * <b>/wattdepot/{org-id}/user/{user-id}</b> URI for manipulating UserInfos.
   * Supports GET, POST, and DELETE requests.
   */
  public static final String USER_PASSWORD_URI = BASE_URI + Labels.ORGANIZATION_ID_VAR + "/"
      + Labels.USER_PASSWORD + "/" + Labels.USER_PASSWORD_ID_VAR;

  /**
   * <b>/wattdepot/{org-id}/user/</b> URI for putting UserInfos. Supports PUT
   * requests.
   */
  public static final String USER_PUT_URI = BASE_URI + Labels.ORGANIZATION_ID_VAR + "/"
      + Labels.USER + "/";

  /**
   * <b>/wattdepot/{org-id}/user/{user-id}</b> URI for manipulating UserInfos.
   * Supports GET, POST, and DELETE requests.
   */
  public static final String USER_URI = BASE_URI + Labels.ORGANIZATION_ID_VAR + "/" + Labels.USER
      + "/" + Labels.USER_ID_VAR;

  /**
   * <b>/wattdepot/{org-id}/users/</b> URI for getting the UserInfos in the
   * organization. Supports GET requests.
   */
  public static final String USERS_URI = BASE_URI + Labels.ORGANIZATION_ID_VAR + "/" + Labels.USERS
      + "/";
  /**
   * <b>/wattdepot/{org-id}/depository/{depository-id}/value/gviz/</b> URI to
   * get all the measured value. Use GET requests.
   */
  public static final String VALUE_GVIZ_URI = BASE_URI + Labels.ORGANIZATION_ID_VAR + "/"
      + Labels.DEPOSITORY + "/" + Labels.DEPOSITORY_ID_VAR + "/" + Labels.VALUE + "/" + Labels.GVIZ
      + "/";

  /**
   * <b>/wattdepot/{org-id}/depository/{depository-id}/value/</b> URI to get all
   * the measured value. Use GET requests.
   */
  public static final String VALUE_URI = BASE_URI + Labels.ORGANIZATION_ID_VAR + "/"
      + Labels.DEPOSITORY + "/" + Labels.DEPOSITORY_ID_VAR + "/" + Labels.VALUE + "/";

  /**
   * <b>/wattdepot/{org-id}/depository/{depository-id}/values/average/gviz/</b>
   * URI to get all the average value for an interval of measurements. Use GET
   * requests.
   */
  public static final String VALUES_AVERAGE_GVIZ_URI = BASE_URI + Labels.ORGANIZATION_ID_VAR + "/"
      + Labels.DEPOSITORY + "/" + Labels.DEPOSITORY_ID_VAR + "/" + Labels.VALUES + "/"
      + Labels.AVERAGE + "/" + Labels.GVIZ + "/";

  /**
   * <b>/wattdepot/{org-id}/depository/{depository-id}/values/average/</b> URI
   * to get all the average value for an interval of measurements. Use GET
   * requests.
   */
  public static final String VALUES_AVERAGE_URI = BASE_URI + Labels.ORGANIZATION_ID_VAR + "/"
      + Labels.DEPOSITORY + "/" + Labels.DEPOSITORY_ID_VAR + "/" + Labels.VALUES + "/"
      + Labels.AVERAGE + "/";

  /**
   * <b>/wattdepot/{org-id}/depository/{depository-id}/values/gviz/</b> URI to
   * get all the measured value. Use GET requests.
   */
  public static final String VALUES_GVIZ_URI = BASE_URI + Labels.ORGANIZATION_ID_VAR + "/"
      + Labels.DEPOSITORY + "/" + Labels.DEPOSITORY_ID_VAR + "/" + Labels.VALUES + "/"
      + Labels.GVIZ + "/";

  /**
   * <b>/wattdepot/{org-id}/depository/{depository-id}/values/average/</b> URI
   * to get all the average value for an interval of measurements. Use GET
   * requests.
   */
  public static final String VALUES_MAXIMUM_URI = BASE_URI + Labels.ORGANIZATION_ID_VAR + "/"
      + Labels.DEPOSITORY + "/" + Labels.DEPOSITORY_ID_VAR + "/" + Labels.VALUES + "/"
      + Labels.MAXIMUM + "/";

  /**
   * <b>/wattdepot/{org-id}/depository/{depository-id}/values/average/</b> URI
   * to get all the average value for an interval of measurements. Use GET
   * requests.
   */
  public static final String VALUES_MINIMUM_URI = BASE_URI + Labels.ORGANIZATION_ID_VAR + "/"
      + Labels.DEPOSITORY + "/" + Labels.DEPOSITORY_ID_VAR + "/" + Labels.VALUES + "/"
      + Labels.MINIMUM + "/";

  /**
   * <b>/wattdepot/{org-id}/depository/{depository-id}/values/</b> URI to get
   * all the measured value. Use GET requests.
   */
  public static final String VALUES_URI = BASE_URI + Labels.ORGANIZATION_ID_VAR + "/"
      + Labels.DEPOSITORY + "/" + Labels.DEPOSITORY_ID_VAR + "/" + Labels.VALUES + "/";

}
