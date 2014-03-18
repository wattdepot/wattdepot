/**
 * MeasurementSummaryClient.java This file is part of WattDepot.
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

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.wattdepot.client.http.api.WattDepotClient;
import org.wattdepot.common.domainmodel.CollectorProcessDefinition;
import org.wattdepot.common.domainmodel.Depository;
import org.wattdepot.common.domainmodel.InterpolatedValue;
import org.wattdepot.common.domainmodel.MeasurementList;
import org.wattdepot.common.domainmodel.Sensor;
import org.wattdepot.common.exception.BadCredentialException;
import org.wattdepot.common.exception.BadSensorUriException;
import org.wattdepot.common.exception.IdNotFoundException;
import org.wattdepot.common.util.DateConvert;
import org.wattdepot.common.util.tstamp.Tstamp;

/**
 * MeasurementSummaryClient polls the WattDepot server asking for the all the
 * measurements in a depository.
 * 
 * @author Cam Moore
 * 
 */
public class MeasurementSummaryClient extends TimerTask {
  /** Flag for debugging messages. */
  private boolean debug;
  /** The client used to communicate with the WattDepot server. */
  private WattDepotClient client;
  /** The Depository for storing measurements. */
  private Depository depository;
  /** The Sensor making the measurements. */
  private Sensor sensor;
  /** The time of the earliest measurement. */
  private Date earliest;
  /** Flag for using a window. */
  private boolean windowP;

  /**
   * @param serverUri The URI for the WattDepot server.
   * @param username The name of a user defined in the WattDepot server.
   * @param orgId the id of the organization the user is in.
   * @param password The password for the user.
   * @param collectorId The CollectorProcessDefinitionId used to initialize this
   *        collector.
   * @param debug flag for debugging messages.
   * @throws BadCredentialException if the user or password don't match the
   *         credentials in WattDepot.
   * @throws IdNotFoundException if the processId is not defined.
   * @throws BadSensorUriException if the Sensor's URI isn't valid.
   */
  public MeasurementSummaryClient(String serverUri, String username, String orgId, String password,
      String collectorId, boolean debug) throws BadCredentialException, IdNotFoundException,
      BadSensorUriException {
    this.client = new WattDepotClient(serverUri, username, orgId, password);
    this.debug = debug;
    CollectorProcessDefinition definition = client.getCollectorProcessDefinition(collectorId);
    this.depository = client.getDepository(definition.getDepositoryId());
    this.sensor = client.getSensor(definition.getSensorId());
    InterpolatedValue v = client.getEarliestValue(this.depository, sensor);
    this.earliest = v.getDate();
  }

  /**
   * @param args command line arguments.
   */
  public static void main(String[] args) {
    Options options = new Options();
    options.addOption("h", false, "Usage: MeasurementSummaryClient -s <server uri> -u <username>"
        + " -p <password> -o <orgId> -c <collectorId> -m <millisecond sleep> [-d]");
    options.addOption("s", "server", true, "WattDepot Server URI. (http://server.wattdepot.org)");
    options.addOption("u", "username", true, "Username");
    options.addOption("o", "organizationId", true, "User's Organization id.");
    options.addOption("p", "password", true, "Password");
    options.addOption("c", "collector", true, "Collector Process Definition Id");
    options.addOption("m", "milliseconds", true, "Number of milliseconds to sleep between polls.");
    options.addOption("d", "debug", false, "Displays sensor data as it is sent to the server.");
    options.addOption("w", "windowing", false,
        "Use a window instead of getting all the measurements.");

    CommandLine cmd = null;
    String serverUri = null;
    String username = null;
    String organizationId = null;
    String password = null;
    String collectorId = null;
    Integer milliSeconds = null;
    boolean debug = false;
    boolean windowP = false;

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
      formatter.printHelp("StressTestCollector", options);
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
    if (cmd.hasOption("c")) {
      collectorId = cmd.getOptionValue("c");
    }
    else {
      collectorId = "stress_test";
    }
    if (cmd.hasOption("m")) {
      milliSeconds = Integer.parseInt(cmd.getOptionValue("m"));
    }
    else {
      milliSeconds = 1000;
    }
    debug = cmd.hasOption("d");
    windowP = cmd.hasOption("w");
    if (debug) {
      System.out.println("Measurement Summary Client:");
      System.out.println("    WattDepotServer: " + serverUri);
      System.out.println("    Username: " + username);
      System.out.println("    OrganizationId: " + organizationId);
      System.out.println("    Password :" + password);
      System.out.println("    CPD Id: " + collectorId);
      System.out.println("    MilliSeconds: " + milliSeconds);
      System.out.println("    Windowing: " + windowP);
    }
    Timer t = new Timer();
    try {
      MeasurementSummaryClient c = new MeasurementSummaryClient(serverUri, username,
          organizationId, password, collectorId, debug);
      c.windowP = windowP;
      System.out.format("Started MeasurementSummaryClient at %s%n", Tstamp.makeTimestamp());
      t.schedule(c, 0, milliSeconds);
    }
    catch (BadCredentialException e) {
      e.printStackTrace();
    }
    catch (IdNotFoundException e) {
      e.printStackTrace();
    }
    catch (BadSensorUriException e) {
      e.printStackTrace();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.util.TimerTask#run()
   */
  @Override
  public void run() {
    Date now = new Date();
    MeasurementList meas = null;
    if (windowP) {
      XMLGregorianCalendar end = Tstamp.makeTimestamp(now.getTime());
      XMLGregorianCalendar start = Tstamp.incrementSeconds(end, -30);
      meas = client.getMeasurements(depository, sensor, DateConvert.convertXMLCal(start),
          DateConvert.convertXMLCal(end));
      if (debug) {
        System.out
            .println("At " + end + " there are " + meas.getMeasurements().size()
                + " measurements in the window. Query took "
                + Tstamp.diff(end, Tstamp.makeTimestamp()));
      }
    }
    else {
      meas = client.getMeasurements(depository, sensor, this.earliest, now);
      if (debug) {
        XMLGregorianCalendar start = Tstamp.makeTimestamp(now.getTime());
        XMLGregorianCalendar end = Tstamp.makeTimestamp();
        System.out.println("Query started at " + start + " there are "
            + meas.getMeasurements().size() + " measurements. Query ended at " + end + ". Took "
            + Tstamp.diff(start, end));
      }
    }
  }

}
