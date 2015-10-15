/**
 * WattDepotServerResource.java This file is part of WattDepot.
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

import org.restlet.data.Header;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;
import org.restlet.util.Series;
import org.wattdepot.common.domainmodel.Labels;
import org.wattdepot.server.WattDepotPersistence;

/**
 * WattDepotServerResource - Base class for WattDepot ServerResources. Gets the
 * WattDepot instance from the WattDepotApplication.
 * 
 * @author Cam Moore
 * 
 */
public class WattDepotServerResource extends ServerResource {
  /** The WattDepot instance. */
  protected WattDepotPersistence depot;
  /** The orgId in the request. */
  protected String orgId;
  

  /*
   * (non-Javadoc)
   * 
   * @see org.restlet.resource.Resource#doInit()
   */
  @Override
  @SuppressWarnings("unchecked")
  protected void doInit() throws ResourceException {
    Series<Header> responseHeaders = (Series<Header>) getResponse().getAttributes().get("org.restlet.http.headers");
    WattDepotApplication app = (WattDepotApplication) getApplication();
    this.depot = app.getDepot();
    this.orgId = getAttribute(Labels.ORGANIZATION_ID);
    if (responseHeaders == null) {
      responseHeaders = new Series<Header>(Header.class);
      getResponse().getAttributes().put("org.restlet.http.headers", responseHeaders);
    }
    responseHeaders.add(new Header("X-Clacks-Overhead", "GNU Terry Pratchett"));
  }
}
