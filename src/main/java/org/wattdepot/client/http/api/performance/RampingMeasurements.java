/**
 * RampingMeasurements.java This file is part of WattDepot.
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
import java.util.TimerTask;

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
 * RampingMeasurements slowly puts more and more measurements per second until
 * WattDepot cannot keep up.
 * 
 * @author Cam Moore
 * 
 */
public class RampingMeasurements extends TimerTask {

  private Timer timer;
  private PutMeasurementTask task;
  private Integer numRuns;
  private Integer milliseconds;
  private String uri;
  private String username;
  private String orgId;
  private String password;
  private boolean debug;

  /**
   * Initializes the RampingMeasurements instance.
   * 
   * @param serverUri The URI for the WattDepot server.
   * @param username The name of a user defined in the WattDepot server.
   * @param orgId the id of the organization the user is in.
   * @param password The password for the user.
   * @param debug flag for debugging messages.
   * @throws BadCredentialException if the user or password don't match the
   *         credentials in WattDepot.
   * @throws IdNotFoundException if the processId is not defined.
   * @throws BadSensorUriException if the Sensor's URI isn't valid.
   */
  public RampingMeasurements(String serverUri, String username, String orgId, String password,
      boolean debug) throws BadCredentialException, IdNotFoundException, BadSensorUriException {
    this.numRuns = 0;
    this.milliseconds = 1000;
    this.uri = serverUri;
    this.username = username;
    this.orgId = orgId;
    this.password = password;
    this.debug = debug;
    this.task = new PutMeasurementTask(serverUri, username, orgId, password, debug);
    this.timer = new Timer("Timer-" + numRuns);
    this.timer.schedule(task, 0, milliseconds);
  }

  /**
   * @param args command line arguments.
   * @throws BadSensorUriException if there is a problem with the sensor URI.
   * @throws IdNotFoundException If the ids are not defined.
   * @throws BadCredentialException if there is a problem with the credentials.
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
    boolean debug = false;

    options.addOption("h", false, "Usage: RampingMeasurements -s <server uri> -u <username>"
        + " -p <password> -o <orgId> [-d]");
    options.addOption("s", "server", true, "WattDepot Server URI. (http://server.wattdepot.org)");
    options.addOption("u", "username", true, "Username");
    options.addOption("o", "organizationId", true, "User's Organization id.");
    options.addOption("p", "password", true, "Password");
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
      formatter.printHelp("RampingMeasurements", options);
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
      System.out.println("Ramping Measurements:");
      System.out.println("    WattDepotServer: " + serverUri);
      System.out.println("    Username: " + username);
      System.out.println("    OrganizationId: " + organizationId);
      System.out.println("    Password :" + password);
    }

    Timer t = new Timer("outer");
    t.schedule(new RampingMeasurements(serverUri, username, organizationId, password, debug), 0,
        15 * 1000);
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.util.TimerTask#run()
   */
  @Override
  public void run() {
    // stop the Puts
    this.timer.cancel();
    this.timer = null;
    this.numRuns += 1;
    System.out.println("put " + task.getNumberOfRuns() + " measurements with average time of "
        + (task.getAverageTime() / 1E9) + " seconds per put. Max of " + task.getMaxTime()
        + " min " + task.getMinTime());
    if (this.milliseconds > 101) {
      this.milliseconds = this.milliseconds - 100;
      this.timer = new Timer("T-" + numRuns);
      try {
        this.task = new PutMeasurementTask(uri, username, orgId, password, debug);
        this.timer.schedule(this.task, 0, this.milliseconds);
      }
      catch (BadCredentialException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      catch (IdNotFoundException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      catch (BadSensorUriException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    else {
      System.out.println("put " + task.getNumberOfRuns() + " measurements with average time of "
          + (task.getAverageTime() / 1E9) + " seconds per put. Max of " + task.getMaxTime()
          + " min " + task.getMinTime());
      System.out.println("Done.");
    }

  }

}
