/**
 * LoginServerResource.java This file is part of WattDepot.
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

import java.util.logging.Level;

import org.restlet.data.CookieSetting;
import org.restlet.data.Form;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;

/**
 * LoginServerResource - Handles the login form.
 * 
 * @author Cam Moore
 * 
 */
public class LoginServerResource extends WattDepotServerResource {

  private String username;
  private String password;

  /*
   * (non-Javadoc)
   * 
   * @see org.restlet.resource.Resource#doInit()
   */
  @Override
  protected void doInit() throws ResourceException {
    super.doInit();
    Form foo = new Form(getRequest().getEntity());
    username = foo.getFirstValue("Username");
    password = foo.getFirstValue("Password");
  }

  /**
   * logs user in.
   */
  @Post
  public void login() {
    getLogger().log(Level.INFO, "POST /wattdepot/login/");
    WattDepotApplication app = (WattDepotApplication) getApplication();
    WebSession session = app.createWebSession(username, password);
    String redirectUri = null;
    if (session != null) {
      // add the session cookie.
      getCookieSettings().add(new CookieSetting("sessionToken", session.getId()));
      redirectUri = "/wattdepot/" + session.getGroupId() + "/";
    } 
    else {
      redirectUri = "/wattdepot/";
    }
    if (redirectUri != null) {
      redirectSeeOther(redirectUri);
    }
  }
}
