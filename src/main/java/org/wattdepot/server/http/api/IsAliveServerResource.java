/**
 * IsAliveServerResource.java This file is part of WattDepot.
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

import java.io.IOException;
import java.io.InputStream;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import org.restlet.resource.Get;

/**
 * IsAliveServerResource returns a string indicating that the WattDepotServer is
 * responding to HTTP GET requests. It provides an easy way to determine if the
 * clients credentials are valid.
 * 
 * @author Cam Moore
 * 
 */
public class IsAliveServerResource extends WattDepotServerResource {
  /**
   * String to send as a response to the health request.
   */
  protected static final String HEALTH_MESSAGE_TEXT = "is alive.";

  protected static final String WATTDEPOT_MESSAGE_TEXT = "WattDepot ";

  /**
   * The GET method for plain text data.
   * 
   * @return The text representation of this resource
   */
  @Get("txt")
  public String getTxt() {
    String version = "";
    InputStream stream = getClass().getResourceAsStream("/META-INF/MANIFEST.MF");
    if (stream != null) {
      Manifest manifest;
      try {
        manifest = new Manifest(stream);
        Attributes attributes = manifest.getMainAttributes();
        version = attributes.getValue("Build-Number");
      }
      catch (IOException e) {
        version = getClass().getPackage().getImplementationVersion();        
      }
    }
    else {
      version = getClass().getPackage().getImplementationVersion();
    }
    if (version == null) {
      version = "";
    }
    else {
      version += " ";
    }
    return WATTDEPOT_MESSAGE_TEXT + version + HEALTH_MESSAGE_TEXT;
  }

}
