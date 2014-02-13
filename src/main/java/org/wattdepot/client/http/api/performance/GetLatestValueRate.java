/**
 * GetLatestValueRate.java This file is part of WattDepot.
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
package org.wattdepot.client.http.api.performance;

import java.util.Timer;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.wattdepot.common.exception.BadCredentialException;
import org.wattdepot.common.exception.BadSensorUriException;
import org.wattdepot.common.exception.IdNotFoundException;
import org.wattdepot.common.util.logger.WattDepotLoggerUtil;

/**
 * GetLatestValueRate - Starts up a process that get the latest value from the
 * WattDepot server at the given rate of measurements per second.
 * 
 * @author Cam Moore
 * 
 */
public class GetLatestValueRate {

  /**
   * @param args command line arguments -s <server uri> -u <username> -p
   *        <password> -o <orgId> -mps <measurementsPerSecond> [-d].
   * @throws BadSensorUriException if there is a problem with the WattDepot
   *         sensor definition.
   * @throws IdNotFoundException if there is a problem with the organization id.
   * @throws BadCredentialException if the credentials are not valid.
   */
  public static void main(String[] args) throws BadCredentialException, IdNotFoundException,
      BadSensorUriException {
    WattDepotLoggerUtil.removeClientLoggerHandlers();
    Options options = new Options();
    CommandLine cmd = null;
    String serverUri = null;
    String username = null;
    String organizationId = null;
    String password = null;
    Integer getsPerSec = null;
    boolean debug = false;

    options.addOption("h", false, "Usage: GetLatestValueRate -s <server uri> -u <username>"
        + " -p <password> -o <orgId> -gps <getsPerSecond> [-d]");
    options.addOption("s", "server", true, "WattDepot Server URI. (http://server.wattdepot.org)");
    options.addOption("u", "username", true, "Username");
    options.addOption("o", "organizationId", true, "User's Organization id.");
    options.addOption("p", "password", true, "Password");
    options.addOption("gps", "getsPerSecond", true,
        "Number of gets per second.");
    options.addOption("d", "debug", false, "Displays statistics as the Measurements are stored.");
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
      formatter.printHelp("GetLatestValueRate", options);
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
    if (cmd.hasOption("gps")) {
      getsPerSec = Integer.parseInt(cmd.getOptionValue("gps"));
    }
    else {
      getsPerSec = 13;
    }
    debug = cmd.hasOption("d");
    if (debug) {
      System.out.println("Get Latest Value Rate:");
      System.out.println("    WattDepotServer: " + serverUri);
      System.out.println("    Username: " + username);
      System.out.println("    OrganizationId: " + organizationId);
      System.out.println("    Password: " + password);
      System.out.println("    Gets/Sec: " + getsPerSec);
    }

    Timer t = new Timer("monitoring");
    for (int i = 0; i < getsPerSec; i++) {
      t.schedule(new GetLatestValueTask(serverUri, username, organizationId, password, debug), 0,
          1000);
    }
  }

}
