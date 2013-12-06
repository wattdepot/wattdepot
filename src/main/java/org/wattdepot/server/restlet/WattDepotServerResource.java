/**
 * WattDepotServerResource.java This file is part of WattDepot 3.
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
package org.wattdepot.server.restlet;

import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;
import org.wattdepot.server.WattDepot;
import org.wattdepot.server.WattDepotApplication;

/**
 * WattDepotServerResource - Base class for WattDepot ServerResources. Gets the
 * WattDepot instance from the WattDepotApplication.
 * 
 * @author Cam Moore
 * 
 */
public class WattDepotServerResource extends ServerResource {
  /** The WattDepot instance. */
  protected WattDepot depot;
  /** The groupId in the request. */
  protected String groupId;
  

  /*
   * (non-Javadoc)
   * 
   * @see org.restlet.resource.Resource#doInit()
   */
  @Override
  protected void doInit() throws ResourceException {
    WattDepotApplication app = (WattDepotApplication) getApplication();
    this.depot = app.getDepot();
    this.groupId = getAttribute("group_id");
  }
}
