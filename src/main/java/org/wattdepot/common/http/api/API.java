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
   * <b>/wattdepot/{group-id}/</b> URI for the group administration User
   * Interface. Supports GET requests.
   */
  public static final String ADMIN_URI = "/" + Labels.WATTDEPOT + "/" + Labels.GROUP_ID_VAR + "/";

  /**
   * <b>/wattdepot/public/measurement-type/</b> URI for storing new
   * MeasurmentTypes. Use PUT requests.
   */
  public static final String MEASUREMENT_TYPE_PUT_URI = "/" + Labels.WATTDEPOT + "/"
      + Labels.PUBLIC + "/" + Labels.MEASUREMENT_TYPE + "/";

  /**
   * <b>/wattdepot/public/measurement-type/{measurement-type-id}</b> URI for
   * MeasurmentType manipulation. Supports GET, POST, and DELETE requests.
   */
  public static final String MEASUREMENT_TYPE_URI = "/" + Labels.WATTDEPOT + "/" + Labels.PUBLIC
      + "/" + Labels.MEASUREMENT_TYPE + "/" + Labels.MEASUREMENT_TYPE_ID_VAR;

  /**
   * <b>/wattdepot/public/measurement-types/</b> URI for getting all
   * MeasurmentTypes. Use GET requests.
   */
  public static final String MEASUREMENT_TYPES_URI = "/" + Labels.WATTDEPOT + "/" + Labels.PUBLIC
      + "/" + Labels.MEASUREMENT_TYPES + "/";

  /**
   * <b>/wattdepot/public/sensor-model/</b> URI for storing new SensorModels.
   * Use PUT requests.
   */
  public static final String SENSOR_MODEL_PUT_URI = "/" + Labels.WATTDEPOT + "/" + Labels.PUBLIC
      + "/" + Labels.SENSOR_MODEL + "/";

  /**
   * <b>/wattdepot/public/sensor-model/{sensor-model-id}</b> URI for SensorModel
   * manipulation. Supports GET, POST, and DELETE requests.
   */
  public static final String SENSOR_MODEL_URI = "/" + Labels.WATTDEPOT + "/" + Labels.PUBLIC + "/"
      + Labels.SENSOR_MODEL + "/" + Labels.SENSOR_MODEL_ID_VAR;

  /**
   * <b>/wattdepot/public/sensor-models/</b> URI for getting all SensorModels.
   * Use GET reqeusts.
   */
  public static final String SENSOR_MODELS_URI = "/" + Labels.WATTDEPOT + "/" + Labels.PUBLIC + "/"
      + Labels.SENSOR_MODELS + "/";

  /**
   * <b>/wattdepot/{group-id}/depository/</b> URI for storing new Depositories.
   * Use PUT requests.
   */
  public static final String DEPOSITORY_PUT_URI = "/" + Labels.WATTDEPOT + "/"
      + Labels.GROUP_ID_VAR + "/" + Labels.DEPOSITORY + "/";

  /**
   * <b>/wattdepot/{group-id}/depository/{depository-id}</b> URI for Depository
   * manipulation. Supports GET, POST, and DELETE requests.
   */
  public static final String DEPOSITORY_URI = "/" + Labels.WATTDEPOT + "/" + Labels.GROUP_ID_VAR
      + "/" + Labels.DEPOSITORY + "/" + Labels.DEPOSITORY_ID_VAR;

  /**
   * <b>/wattdepot/{group-id}/depository/{depository-id}/sensors/</b> URI for
   * getting all the sensors that have stored measurements in the depository.
   * Use GET requests.
   */
  public static final String DEPOSITORY_SENSORS_URI = "/" + Labels.WATTDEPOT + "/"
      + Labels.GROUP_ID_VAR + "/" + Labels.DEPOSITORY + "/" + Labels.DEPOSITORY_ID_VAR + "/"
      + Labels.SENSORS + "/";
  /**
   * <b>/wattdepot/{group-id}/depositories/</b> URI for getting all
   * Depositories. Use GET requests.
   */
  public static final String DEPOSITORIES_URI = "/" + Labels.WATTDEPOT + "/" + Labels.GROUP_ID_VAR
      + "/" + Labels.DEPOSITORIES + "/";

  /**
   * <b>/wattdepot/{group-id}/depository/{depository-id}/measurement/</b> URI
   * for putting a measurement into the depsository. Use PUT requests.
   */
  public static final String MEASUREMENT_PUT_URI = "/" + Labels.WATTDEPOT + "/"
      + Labels.GROUP_ID_VAR + "/" + Labels.DEPOSITORY + "/" + Labels.DEPOSITORY_ID_VAR + "/"
      + Labels.MEASUREMENT + "/";

  /**
   * <b>/wattdepot/{group-id}/depository/{depository-id}/measurement/{
   * measurement-id}</b> URI for manipulating a measurement in the depsository.
   * Supports GET and DELETE requests.
   */
  public static final String MEASUREMENT_URI = "/" + Labels.WATTDEPOT + "/" + Labels.GROUP_ID_VAR
      + "/" + Labels.DEPOSITORY + "/" + Labels.DEPOSITORY_ID_VAR + "/" + Labels.MEASUREMENT + "/"
      + Labels.MEASUREMENT_ID_VAR;

  /**
   * <b>/wattdepot/{group-id}/depository/{depository-id}/measurements/</b> URI
   * to get all the measurements in the depsository. Use GET requests.
   */
  public static final String MEASUREMENTS_URI = "/" + Labels.WATTDEPOT + "/" + Labels.GROUP_ID_VAR
      + "/" + Labels.DEPOSITORY + "/" + Labels.DEPOSITORY_ID_VAR + "/" + Labels.MEASUREMENTS + "/";

  /**
   * <b>/wattdepot/{group-id}/depository/{depository-id}/measurements/gviz/</b>
   * URI to get all the measurements in the depsository. Use GET requests.
   */
  public static final String MEASUREMENTS_GVIZ_URI = "/" + Labels.WATTDEPOT + "/"
      + Labels.GROUP_ID_VAR + "/" + Labels.DEPOSITORY + "/" + Labels.DEPOSITORY_ID_VAR + "/"
      + Labels.MEASUREMENTS + "/" + Labels.GVIZ + "/";

  /**
   * <b>/wattdepot/{group-id}/depository/{depository-id}/value/</b>
   * URI to get all the measured value. Use GET requests.
   */
  public static final String VALUE_URI = "/" + Labels.WATTDEPOT + "/"
      + Labels.GROUP_ID_VAR + "/" + Labels.DEPOSITORY + "/" + Labels.DEPOSITORY_ID_VAR + "/"
      + Labels.VALUE + "/";

  /**
   * <b>/wattdepot/{group-id}/depository/{depository-id}/value/gviz/</b>
   * URI to get all the measured value. Use GET requests.
   */
  public static final String VALUE_GVIZ_URI = "/" + Labels.WATTDEPOT + "/"
      + Labels.GROUP_ID_VAR + "/" + Labels.DEPOSITORY + "/" + Labels.DEPOSITORY_ID_VAR + "/"
      + Labels.VALUE + "/" + Labels.GVIZ + "/";

}
