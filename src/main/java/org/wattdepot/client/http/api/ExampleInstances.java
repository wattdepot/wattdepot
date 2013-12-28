/**
 * ExampleInstances.java This file is part of WattDepot.
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
package org.wattdepot.client.http.api;

import java.util.HashSet;
import java.util.Set;

import org.wattdepot.client.ClientProperties;
import org.wattdepot.common.domainmodel.Organization;
import org.wattdepot.common.domainmodel.Property;
import org.wattdepot.common.domainmodel.UserInfo;
import org.wattdepot.common.domainmodel.UserPassword;
import org.wattdepot.common.util.logger.WattDepotLogger;

/**
 * ExampleInstances
 * 
 * @author Cam Moore
 * 
 */
public class ExampleInstances {

  private WattDepotAdminClient admin;
  private WattDepotClient client;

  public ExampleInstances() {
    ClientProperties props = new ClientProperties();
    String serverURL = "http://" + props.get(ClientProperties.WATTDEPOT_SERVER_HOST) + ":"
        + props.get(ClientProperties.PORT_KEY) + "/";
    try {
      admin = new WattDepotAdminClient(serverURL, props.get(ClientProperties.USER_NAME),
          props.get(ClientProperties.USER_PASSWORD));
    }
    catch (Exception e) {
      System.out.println("Failed with " + props.get(ClientProperties.USER_NAME) + " and "
          + props.get(ClientProperties.USER_PASSWORD));
    }
    if (admin != null) {
      setUpOrganization();
    }
  }

  private void setUpOrganization() {
    Organization org = new Organization("University of Hawaii, Manoa");
    org.setSlug("uh");
    UserInfo user1 = new UserInfo("cmoore", "Cam", "Moore", "cmoore@hawaii.edu", org.getSlug(),
        new HashSet<Property>());
    UserInfo user2 = new UserInfo("johnson", "Philip", "Johnson", "philipmjohnson@gmail.com",
        org.getSlug(), new HashSet<Property>());
    org.getUsers().add(user1.getId());
    org.getUsers().add(user2.getId());
    admin.putUserPassword(new UserPassword(user1.getId(), "secret"));
    admin.putUserPassword(new UserPassword(user2.getId(), "secret"));
    admin.putUser(user1);
    admin.putUser(user2);
    admin.putOrganization(org);
    admin.updateOrganization(org);
    

  }

  /**
   * @param args
   */
  public static void main(String[] args) {
    ExampleInstances e = new ExampleInstances();

  }

}
