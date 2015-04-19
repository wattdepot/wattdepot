/*
 * This file is part of WattDepot.
 *
 *  Copyright (C) 2015  Cam Moore
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.wattdepot.extension.openeis.http.api;

import org.wattdepot.common.domainmodel.Labels;
import org.wattdepot.extension.openeis.OpenEISLabels;

/**
 * API - Defines the Strings that make up the WattDepot OPENEIS HTTP API.
 *
 * @author Cam Moore
 */
public class OpenEISAPI {

/************************************************************************************/
/********************* OpenEIS http://eis.lbl.gov/openeis.html API ******************/
/************************************************************************************/

  /**
   * <b>/wattdepot/</b> Base URI.
   */
  public static final String BASE_URI = org.wattdepot.common.http.api.API.BASE_URI + Labels.ORGANIZATION_ID_VAR + "/"
      + OpenEISLabels.OPENEIS + "/";

  /**
   * <b>/wattdepot/{org-id}/openeis/ui/</b> URI for the OpenEIS user interface.
   */
  public static final String OPENEIS_UI_URI = BASE_URI + "ui" + "/";

  /**
   * <b>/wattdepot/{org-id}/openeis/time-series-load-profiling/data/</b> URI to get
   * last month's power data. Use GET requests.
   */
  public static final String OPENEIS_TIME_SERIES_LOAD_DATA_URI = BASE_URI + OpenEISLabels.TIME_SERIES_LOAD_PROFILING + "/" + Labels.DATA + "/";

  /**
   * <b>/wattdepot/{org-id}/openeis/time-series-load-profiling/gviz/</b> URI to get
   * last month's power data. Use GET requests.
   */
  public static final String OPENEIS_TIME_SERIES_LOAD_GVIZ_URI = BASE_URI + OpenEISLabels.TIME_SERIES_LOAD_PROFILING + "/" + Labels.GVIZ + "/";

  /**
   * <b>/wattdepot/{org-id}/openeis/heat-map/data/</b> URI to get the last year's hourly power data. Use GET requests.
   */
  public static final String OPENEIS_HEAT_MAP_DATA_URI = BASE_URI + OpenEISLabels.HEAT_MAP + "/" + Labels.DATA + "/";

  /**
   * <b>/wattdepot/{org-id}/openeis/heat-map/gviz/</b> URI to get the last year's hourly power data. Use GET requests.
   */
  public static final String OPENEIS_HEAT_MAP_GVIS_URI = BASE_URI + OpenEISLabels.HEAT_MAP + "/" + Labels.GVIZ + "/";

  /**
   * <b>/wattdepot/{org-id}/openeis/energy-signature/data/</b> URI to get the last year's hourly power data. Use GET requests.
   */
  public static final String OPENEIS_ENERGY_SIGNATURE_DATA_URI = BASE_URI + OpenEISLabels.ENERGY_SIGNATURE + "/" + Labels.DATA + "/";

  /**
   * <b>/wattdepot/{org-id}/openeis/energy-signature/gviz/</b> URI to get the last year's hourly power data. Use GET requests.
   */
  public static final String OPENEIS_ENERGY_SIGNATURE_GVIZ_URI = BASE_URI + OpenEISLabels.ENERGY_SIGNATURE + "/" + Labels.GVIZ + "/";


  /**
   * <b>/wattdepot/{org-id}/openeis/longitudinal-benchmarking/data/</b> URI to get a longitudinal benchmark comparison data.
   * Use GET requests.
   */
  public static final String OPENEIS_LONGITUDINAL_BASELINE_DATA_URI = BASE_URI + OpenEISLabels.LONGITUDINAL_BENCHMARKING + "/"
      + Labels.DATA + "/";

  /**
   * <b>/wattdepot/{org-id}/openeis/longitudinal-benchmarking/gviz/</b> URI to get a longitudinal benchmark comparison data for google
   * visualization. Use GET requests.
   */
  public static final String OPENEIS_LONGITUDINAL_BASELINE_GVIZ_URI = BASE_URI + OpenEISLabels.LONGITUDINAL_BENCHMARKING + "/"
      + Labels.GVIZ + "/";
}
