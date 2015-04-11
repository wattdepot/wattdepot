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

package org.wattdepot.extension.openeis;

import org.wattdepot.extension.WattDepotExtension;
import org.wattdepot.extension.openeis.http.api.OpenEISAPI;
import org.wattdepot.extension.openeis.http.api.OpenEISUIServerResource;
import org.wattdepot.extension.openeis.server.HeatMapGivzServerResource;
import org.wattdepot.extension.openeis.server.HeatMapServerResource;
import org.wattdepot.extension.openeis.server.TimeSeriesLoadProfileGvizServerResource;
import org.wattdepot.extension.openeis.server.TimeSeriesLoadProfileServerResource;
import org.wattdepot.server.http.api.WattDepotServerResource;

import java.util.HashMap;
import java.util.Map;

/**
 * OpenEISExtension - A WattDepot extension that supports several of the OpenEIS algorithms.
 * @author Cam Moore
 * Created by carletonmoore on 4/9/15.
 */
public class OpenEISExtension implements WattDepotExtension {
  @Override
  public String getBaseURI() {
    return OpenEISAPI.BASE_URI;
  }

  @Override
  public Map<String, Class<? extends WattDepotServerResource>> getServerResourceMapping() {
    HashMap<String, Class<? extends WattDepotServerResource>> mapping = new HashMap<String, Class<? extends WattDepotServerResource>>();
    // OpenEIS algorithms
    mapping.put(OpenEISAPI.OPENEIS_UI_URI, OpenEISUIServerResource.class);
    mapping.put(OpenEISAPI.OPENEIS_TIME_SERIES_LOAD_DATA_URI, TimeSeriesLoadProfileServerResource.class);
    mapping.put(OpenEISAPI.OPENEIS_TIME_SERIES_LOAD_GVIZ_URI, TimeSeriesLoadProfileGvizServerResource.class);
    mapping.put(OpenEISAPI.OPENEIS_HEAT_MAP_DATA_URI, HeatMapServerResource.class);
    mapping.put(OpenEISAPI.OPENEIS_HEAT_MAP_GVIS_URI, HeatMapGivzServerResource.class);

    return mapping;
  }
}
