/**
 * PutThroughput.java This file is part of WattDepot.
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
package org.wattdepot.client.http.api.performance;

import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.wattdepot.common.exception.BadCredentialException;
import org.wattdepot.common.exception.BadSensorUriException;
import org.wattdepot.common.exception.IdNotFoundException;

/**
 * PutThroughput - Attempts to determine the maximum rate of storing
 * Measurements in a WattDepot installation.
 * 
 * @author Cam Moore
 * 
 */
public class PutThroughput extends TimerTask {

  /** Manages the PutTasks. */
  private Timer timer;
  /** The PutTask we will sample. */
  private PutTask sampleTask;
  /** The WattDepot server's URI. */
  private String serverUri;
  /** The WattDepot User. */
  private String username;
  /** The WattDepot User's organization. */
  private String orgId;
  /** The WattDepot User's password. */
  private String password;
  /** Flag for debugging. */
  private boolean debug;
  /** The number of times we've checked the stats. */
  private Integer numChecks;
  private DescriptiveStatistics averagePutTime;
  private DescriptiveStatistics averageMinPutTime;
  private DescriptiveStatistics averageMaxPutTime;

  private Long measPerSec;
  private Long calculatedMeasPerSec;

  /**
   * Initializes the PutThroughput instance.
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
  public PutThroughput(String serverUri, String username, String orgId,
      String password, boolean debug) throws BadCredentialException, IdNotFoundException,
      BadSensorUriException {
    this.serverUri = serverUri;
    this.username = username;
    this.orgId = orgId;
    this.password = password;
    this.debug = debug;
    this.numChecks = 0;
    this.measPerSec = 1l;
    this.calculatedMeasPerSec = 1l;
    this.averageMaxPutTime = new DescriptiveStatistics();
    this.averageMinPutTime = new DescriptiveStatistics();
    this.averagePutTime = new DescriptiveStatistics();
    this.timer = new Timer("throughput");
    this.sampleTask = new PutTask(serverUri, username, orgId, password, debug);
    // Starting at 1 meas/second
    this.timer.schedule(sampleTask, 0, 1000);
  }

  /**
   * @param args command line arguments -s <server uri> -u <username> -p
   *        <password> -o <orgId> -n <numSamples> [-d].
   * @throws BadSensorUriException if there is a problem with the WattDepot
   *         sensor definition.
   * @throws IdNotFoundException if there is a problem with the organization id.
   * @throws BadCredentialException if the credentials are not valid.
   */
  public static void main(String[] args) throws BadCredentialException, IdNotFoundException,
      BadSensorUriException {
    Options options = new Options();
    CommandLine cmd = null;
    String serverUri = null;
    String username = null;
    String organizationId = null;
    String password = null;
    Integer numSamples = null;
    boolean debug = false;

    options.addOption("h", false, "Usage: PutThroughput -s <server uri> -u <username>"
        + " -p <password> -o <orgId> [-d]");
    options.addOption("s", "server", true, "WattDepot Server URI. (http://server.wattdepot.org)");
    options.addOption("u", "username", true, "Username");
    options.addOption("o", "organizationId", true, "User's Organization id.");
    options.addOption("p", "password", true, "Password");
    options.addOption("n", "numSamples", true, "Number of puts to sample.");
    options.addOption("d", "debug", false, "Displays statistics as the Measurements are stored.");
    CommandLineParser parser = new DefaultParser();
    HelpFormatter formatter = new HelpFormatter();
    try {
      cmd = parser.parse(options, args);
    }
    catch (ParseException e) {
      System.err.println("Command line parsing failed. Reason: " + e.getMessage() + ". Exiting.");
      System.exit(1);
    }
    if (cmd.hasOption("h")) {
      formatter.printHelp("PutThroughput", options);
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
    if (cmd.hasOption("n")) {
      numSamples = Integer.parseInt(cmd.getOptionValue("n"));
    }
    else {
      numSamples = 13;
    }
    debug = cmd.hasOption("d");
    if (debug) {
      System.out.println("Put Throughput:");
      System.out.println("    WattDepotServer: " + serverUri);
      System.out.println("    Username: " + username);
      System.out.println("    OrganizationId: " + organizationId);
      System.out.println("    Password: ********");
      System.out.println("    Samples: " + numSamples);
    }

    Timer t = new Timer("monitoring");
    t.schedule(new PutThroughput(serverUri, username, organizationId, password, debug),
        0, numSamples * 1000);
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.util.TimerTask#run()
   */
  @Override
  public void run() {
    // wake up to check the stats.
    if (this.numChecks == 0) {
      // haven't actually run so do nothing.
      this.numChecks++;
    }
    else {
      this.timer.cancel();
      this.numChecks++;
      this.averagePutTime.addValue(sampleTask.getAverageTime() / 1E9);
      this.averageMinPutTime.addValue(sampleTask.getMinTime() / 1E9);
      this.averageMaxPutTime.addValue(sampleTask.getMaxTime() / 1E9);
      this.calculatedMeasPerSec = calculatePutRate(averagePutTime);
      this.measPerSec = calculatedMeasPerSec;
      // System.out.println("Min put time = " + (sampleTask.getMinPutTime() /
      // 1E9));
      System.out.println("Ave put time = " + (this.sampleTask.getAverageTime() / 1E9) + " => "
          + Math.round(1.0 / (this.sampleTask.getAverageTime() / 1E9)) + " meas/sec.");
      // System.out.println("Max put time = " + (sampleTask.getMaxPutTime() /
      // 1E9));
      // System.out.println("Max put rate = " +
      // calculatePutRate(averageMinPutTime));
      System.out.println("Setting rate to " + this.calculatedMeasPerSec);
      // System.out.println("Min put rate = " +
      // calculatePutRate(averageMaxPutTime));
      this.timer = new Timer("throughput");
      // if (debug) {
      // System.out.println("Starting " + this.measPerSec +
      // " threads @ 1 meas/s");
      // }
      for (int i = 0; i < measPerSec; i++) {
        try {
          this.sampleTask = new PutTask(serverUri, username, orgId, password, debug);
          timer.schedule(sampleTask, 0, 1000);
          if (debug) {
            System.out.println("Starting task " + i);
          }
        }
        catch (BadCredentialException e) { // NOPMD
          // should not happen.
        }
        catch (IdNotFoundException e) { // NOPMD
          // should not happen.
        }
        catch (BadSensorUriException e) { // NOPMD
          // should not happen
        }
      }
    }
  }

  /**
   * @param stats the DescriptiveStatistics to calculate the mean put time.
   * @return The estimated put rate based upon the time it takes to put a single
   *         measurement.
   */
  private Long calculatePutRate(DescriptiveStatistics stats) {
    double putTime = stats.getMean();
    Long ret = null;
    double numPuts = 1.0 / putTime;
    ret = Math.round(numPuts);
    return ret;
  }
}
