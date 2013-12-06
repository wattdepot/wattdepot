/**
 * AdministratorServerResource.java This file is part of WattDepot 3.
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
package org.wattdepot3.server.restlet;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.restlet.data.LocalReference;
import org.restlet.data.MediaType;
import org.restlet.ext.freemarker.TemplateRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.Get;

/**
 * AdministratorServerResource - Administrative interface for WattDepot. It
 * handles the HTTP API ("/wattdepot/{group_id}/").
 * 
 * @author Cam Moore
 * 
 */
public class LoginPageServerResource extends WattDepotServerResource {

  /**
   * @return The admin user interface as an HTML Representation.
   */
  @Get()
  public Representation toHtml() {
    getLogger().log(Level.INFO, "GET /wattdepot/");
    Map<String, Object> dataModel = new HashMap<String, Object>();
    Representation rep = new ClientResource(LocalReference.createClapReference(getClass()
        .getPackage()) + "/LoginForm.ftl").get();

    return new TemplateRepresentation(rep, dataModel, MediaType.TEXT_HTML);
  }
}
