/**
 * Labels.java This file is part of WattDepot.
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
package org.wattdepot.common.domainmodel;

/**
 * Labels Strings used to create the HTTP URLs and requests.
 * 
 * @author Cam Moore
 * 
 */
public class Labels {
  /** The String indicating admin group. */
  public static final String ADMIN = "admin";

  /** The String indicating average operation. */
  public static final String AVERAGE = "average";

  /** The label for a given CollectorProcessDefinition. */
  public static final String COLLECTOR_PROCESS_DEFINITION = "collector-process-definition";

  /** The label for the CollectorProcessDefinition id. */
  public static final String COLLECTOR_PROCESS_DEFINITION_ID = "collector-process-definition-id";

  /** The label for the CollectorProcessDefinition id variable. */
  public static final String COLLECTOR_PROCESS_DEFINITION_ID_VAR = "{"
      + COLLECTOR_PROCESS_DEFINITION_ID + "}";

  /** The label for all CollectorProcessDefinition. */
  public static final String COLLECTOR_PROCESS_DEFINITIONS = "collector-process-definitions";

  /** The label for getting daily data. */
  public static final String DAILY = "daily";

  /** The label for getting data. */
  public static final String DATA = "data";

  /** The label for all the Depositories. */
  public static final String DEPOSITORIES = "depositories";

  /** The label for a given Depository. */
  public static final String DEPOSITORY = "depository";

  /** Label for the depository id. */
  public static final String DEPOSITORY_ID = "depository-id";

  /** Label for the depository id. */
  public static final String DEPOSITORY_ID_VAR = "{" + DEPOSITORY_ID + "}";

  /** Label for difference InterpolatedValues. */
  public static final String DIFFERENCE = "difference";

  /** The label for the earliest time. */
  public static final String EARLIEST = "earliest";

  /** The label for the end timestamp. */
  public static final String END = "end";

  /** The label for a given MeasurementPruningDefinition. */
  public static final String MEASUREMENT_PRUNING_DEFINITION = "measurement-pruning-definition";

  /** The label for the MeasurementPruningDefinition id. */
  public static final String MEASUREMENT_PRUNING_DEFINITION_ID = "measurement-pruning-definition-id";

  /** The label for the MeasurementPruningDefinition id variable. */
  public static final String MEASUREMENT_PRUNING_DEFINITION_ID_VAR = "{"
      + MEASUREMENT_PRUNING_DEFINITION_ID + "}";

  /** The label for all MeasurementPruningDefinitions. */
  public static final String MEASUREMENT_PRUNING_DEFINITIONS = "measurement-pruning-definitions";
  
  /** The label for gap. */
  public static final String GAP = "gap";

  /** The label for google visualization. */
  public static final String GVIZ = "gviz";

  /** The label for hourly samples for a single data. */
  public static final String DAY_HOURLY = "day-hourly";

  /** The label for hourly values or samples. */
  public static final String HOURLY = "hourly";

  /** The label for sampling internal or interval values. */
  public static final String INTERVAL = "interval";

  /** The label for the latest time. */
  public static final String LATEST = "latest";

  /** The label for a given Location. */
  public static final String LOCATION = "location";

  /** Label for the location id. */
  public static final String LOCATION_ID = "location-id";

  /** Label for the location id. */
  public static final String LOCATION_ID_VAR = "{" + LOCATION_ID + "}";

  /** The label for all the Locations. */
  public static final String LOCATIONS = "locations";

  /** The label for the maximum operation. */
  public static final String MAXIMUM = "maximum";

  /** The label for a given Measurement. */
  public static final String MEASUREMENT = "measurement";

  /** The label for the Measurement id. */
  public static final String MEASUREMENT_ID = "measurement-id";

  /** The label for the Measurement id. */
  public static final String MEASUREMENT_ID_VAR = "{" + MEASUREMENT_ID + "}";

  /** The label for a given MeasurementType. */
  public static final String MEASUREMENT_TYPE = "measurement-type";

  /** Label for the measurment type id. */
  public static final String MEASUREMENT_TYPE_ID = "measurement-type-id";

  /** Label for the measurment type id. */
  public static final String MEASUREMENT_TYPE_ID_VAR = "{" + MEASUREMENT_TYPE_ID + "}";

  /** The label for all the MeasurementTypes. */
  public static final String MEASUREMENT_TYPES = "measurement-types";

  /** The label for all the Measurements. */
  public static final String MEASUREMENTS = "measurements";

  /** The label for bulk operations. */
  public static final String BULK = "bulk";

  /** The label for the minimum operation. */
  public static final String MINIMUM = "minimum";

  /** The URI for manipulating a given Organization. */
  public static final String ORGANIZATION = "organization";

  /** Label for the organization id. */
  public static final String ORGANIZATION_ID = "org-id";

  /** Label for the organization id. */
  public static final String ORGANIZATION_ID_VAR = "{" + ORGANIZATION_ID + "}";

  /** Label for the organization id. */
  public static final String ORGANIZATION_ID2 = "org-id2";

  /** Label for the organization id. */
  public static final String ORGANIZATION_ID2_VAR = "{" + ORGANIZATION_ID2 + "}";

  /** URI component for manipulating all Organizations. */
  public static final String ORGANIZATIONS = "organizations";

  /** The label for point values. */
  public static final String POINT = "point";

  /** The label/group for public objects. */
  public static final String PUBLIC = "public";

  /** The label for a given Sensor. */
  public static final String SENSOR = "sensor";

  /** The label for a given SensorGroup. */
  public static final String SENSOR_GROUP = "sensor-group";

  /** Label for the sensor group id. */
  public static final String SENSOR_GROUP_ID = "sensor-group-id";

  /** Label for the sensor group id. */
  public static final String SENSOR_GROUP_ID_VAR = "{" + SENSOR_GROUP_ID + "}";

  /** The label for all SensorGroups. */
  public static final String SENSOR_GROUPS = "sensor-groups";

  /** Label for the sensor id. */
  public static final String SENSOR_ID = "sensor-id";

  /** Label for the sensor id. */
  public static final String SENSOR_ID_VAR = "{" + SENSOR_ID + "}";

  /** The label for a given SensorModel. */
  public static final String SENSOR_MODEL = "sensor-model";

  /** Label for the sensormodel id. */
  public static final String SENSOR_MODEL_ID = "sensor-model-id";

  /** Label for the sensormodel id. */
  public static final String SENSOR_MODEL_ID_VAR = "{" + SENSOR_MODEL_ID + "}";

  /** The label for all SensorModels. */
  public static final String SENSOR_MODELS = "sensor-models";

  /** The label for all Sensors. */
  public static final String SENSORS = "sensors";

  /** The label for Sensor Status. */
  public static final String SENSOR_STATUS = "sensor-status";

  /** The label for the start timestamp. */
  public static final String START = "start";

  /** The label for getting a summary. */
  public static final String SUMMARY = "summary";

  /** The label for the timestamp. */
  public static final String TIMESTAMP = "timestamp";

  /** The URI for manipulating a given UserInfo. */
  public static final String USER = "user";

  /** Label for the user id. */
  public static final String USER_ID = "user-id";

  /** Label for the user id. */
  public static final String USER_ID_VAR = "{" + USER_ID + "}";

  /** The label for UserPassword. */
  public static final String USER_PASSWORD = "user-password";

  /** Label for the user-password id. */
  public static final String USER_PASSWORD_ID = "user-password-id";

  /** Label for the user-password id variable. */
  public static final String USER_PASSWORD_ID_VAR = "{" + USER_PASSWORD_ID + "}";

  /** The URI for manipulating a given UserInfo. */
  public static final String USERS = "users";

  /** The label for a 'measured' value. */
  public static final String VALUE = "value";

  /** The label for determining what type of value to return. */
  public static final String VALUE_TYPE = "value-type";

  /** The label for 'measured' values. */
  public static final String VALUES = "values";

  /** The label for visualizing interpolated values. */
  public static final String VISUALIZE = "visualize";

  /** The base label used for WattDepot. */
  public static final String WATTDEPOT = "wattdepot";

  /** The label used for determining a window. */
  public static final String WINDOW = "window";

  /** The label used for getting csv values. */
  public static final String CSV = "csv";
}
