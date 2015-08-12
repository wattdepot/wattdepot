/**
 * AllOrganizationCollectors.java This file is part of WattDepot.
 *
 * Copyright (C) 2014  Cam Moore
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
package org.wattdepot.client.http.api.collector;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.wattdepot.client.http.api.WattDepotClient;
import org.wattdepot.common.domainmodel.CollectorProcessDefinition;
import org.wattdepot.common.exception.BadCredentialException;

/**
 * AllOrganizationCollectors attempts to start all the collectors defined for an
 * Organization.
 * 
 * @author Cam Moore
 * 
 */
public class AllOrganizationCollectors {

  /**
   * Queries the WattDepot server to find all the CollectorProcessDefinitions
   * defined for the Organization. Then tries to start Collectors for each
   * definition.
   * 
   * @param args command line arguments -s &lt;server uri&gt; -u &lt;username&gt; -p
   *        &lt;password&gt; -o &lt;orgId&gt; [-d].
   * @throws BadCredentialException if the credentials are not defined on the
   *         WattDepot server.
   */
  public static void main(String[] args) throws BadCredentialException {
    Options options = new Options();
    CommandLine cmd = null;
    String serverUri = null;
    String username = null;
    String organizationId = null;
    String password = null;
    boolean debug = false;

    options.addOption("h", false, "Usage: AllOrganizationCollectors -s <server uri> -u <username>"
        + " -p <password> -o <orgId> [-d]");
    options.addOption("s", "server", true, "WattDepot Server URI. (http://server.wattdepot.org)");
    options.addOption("u", "username", true, "Username");
    options.addOption("o", "organizationId", true, "User's Organization id.");
    options.addOption("p", "password", true, "Password");
    options.addOption("d", "debug", false, "Displays debugging information.");
    CommandLineParser parser = new PosixParser();
    HelpFormatter formatter = new HelpFormatter();
    try {
      cmd = parser.parse(options, args);
    }
    catch (ParseException e) {
      System.err.println("Command line parsing failed. Reason: " + e.getMessage() + ". Exiting.");
      System.exit(1);
    }
    if (cmd.hasOption("h")) {
      formatter.printHelp("AllOrganizationCollectors", options);
      System.exit(0);
    }
    if (cmd.hasOption("s")) {
      serverUri = cmd.getOptionValue("s");
    }
    else {
      serverUri = "http://server.wattdepot.org/";
    }
    if (cmd.hasOption("u")) {
      username = cmd.getOptionValue("u");
    }
    else {
      username = "user";
    }
    if (cmd.hasOption("p")) {
      password = cmd.getOptionValue("p");
    }
    else {
      password = "default";
    }
    if (cmd.hasOption("o")) {
      organizationId = cmd.getOptionValue("o");
    }
    else {
      organizationId = "organization";
    }
    debug = cmd.hasOption("d");
    if (debug) {
      System.out.println("All Organization Collectors:");
      System.out.println("    WattDepotServer: " + serverUri);
      System.out.println("    Username: " + username);
      System.out.println("    OrganizationId: " + organizationId);
      System.out.println("    Password: ********");
    }
    WattDepotClient client = new WattDepotClient(serverUri, username, organizationId, password);
    for (CollectorProcessDefinition cpd : client.getCollectorProcessDefinitions().getDefinitions()) {
      try {
        if (debug) {
          System.out.println("Starting " + cpd.getId());
        }
        if (!MultiThreadedCollector.start(serverUri, username, organizationId, password, cpd.getId(),
            debug)) {
          System.err.println("Problem with collector " + cpd.getId() + ". Skipping it.");
        }
      }
      catch (InterruptedException e) {
        System.err.println("Couldn't start collector " + cpd.getId() + ". Skipping it.");
      }
    }
  }

}
