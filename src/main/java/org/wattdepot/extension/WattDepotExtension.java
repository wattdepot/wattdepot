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

package org.wattdepot.extension;

import org.wattdepot.server.http.api.WattDepotServerResource;

import java.util.Map;

/**
 * WattDepotExtension - Interface for all WattDepot extensions.
 * @author Cam Moore
 * Created by carletonmoore on 4/9/15.
 */
public interface WattDepotExtension {
  /**
   * Returns the Base URI for this extension. Base URIs must be unique.
   * @return The Base URI as a String.
   */
  String getBaseURI();

  /**
   * Returns the mapping of URIs to WattDepotServerResources.
   * @return The Map of URIs to WattDepotServerResources.
   */
  Map<String, Class<? extends WattDepotServerResource>> getServerResourceMapping();
}
