/**
 * PutCollectorRate.java This file is part of WattDepot.
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
 * PutCollectorRate - Starts up a process that sends Measurements to the
 * WattDepot server at the given rate of measurements per second.
 * 
 * @author Cam Moore
 * 
 */
public class PutCollectorRate {

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
    Integer measPerSec = null;
    String cpd = null;
    boolean debug = false;

    options.addOption("h", false, "Usage: PutRate -s <server uri> -u <username>"
        + " -p <password> -o <orgId> -mps <measurementsPerSecond> [-d]");
    options.addOption("s", "server", true, "WattDepot Server URI. (http://server.wattdepot.org)");
    options.addOption("u", "username", true, "Username");
    options.addOption("o", "organizationId", true, "User's Organization id.");
    options.addOption("p", "password", true, "Password");
    options.addOption("mps", "measurementsPerSecond", true,
        "Number of measurements to put per second.");
    options.addOption("cpd", "collector", true, "The collector process definition");
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
      formatter.printHelp("PutRate", options);
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
    if (cmd.hasOption("mps")) {
      measPerSec = Integer.parseInt(cmd.getOptionValue("mps"));
    }
    else {
      measPerSec = 13;
    }
    debug = cmd.hasOption("d");
    if (cmd.hasOption("cpd")) {
      cpd = cmd.getOptionValue("cpd");
    }
    else {
      cpd = "performance-evaluation-collector";
    }
    if (debug) {
      System.out.println("Measurement Put Rate:");
      System.out.println("    WattDepotServer: " + serverUri);
      System.out.println("    Username: " + username);
      System.out.println("    OrganizationId: " + organizationId);
      System.out.println("    Password: ********");
      System.out.println("    CPD: " + cpd);
      System.out.println("    Meas/Sec: " + measPerSec);
    }

    Timer t = new Timer("monitoring");
    for (int i = 0; i < measPerSec; i++) {
      t.schedule(new PutTask(serverUri, username, organizationId, password, debug, cpd), 0, 1000);
    }
  }

}
