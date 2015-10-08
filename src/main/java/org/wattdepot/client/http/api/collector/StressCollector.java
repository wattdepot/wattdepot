/**
 * StressCollector.java This file is part of WattDepot.
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

import javax.measure.unit.Unit;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.wattdepot.common.domainmodel.Depository;
import org.wattdepot.common.domainmodel.Measurement;
import org.wattdepot.common.domainmodel.Organization;
import org.wattdepot.common.domainmodel.UserInfo;
import org.wattdepot.common.exception.BadCredentialException;
import org.wattdepot.common.exception.BadSensorUriException;
import org.wattdepot.common.exception.IdNotFoundException;
import org.wattdepot.common.exception.MeasurementTypeException;

/**
 * StressCollector - Collector making rapid measurements hammering the
 * WattDepotServer.
 * 
 * @author Cam Moore
 * 
 */
public class StressCollector extends MultiThreadedCollector {

  /** Stores the fake wattHours value that will be incremented each time it is sent to the server. */
  private double wattHours = 0;

  /**
   * Initializes the StressCollector.
   * 
   * @param serverUri The URI for the WattDepot server.
   * @param username The name of a user defined in the WattDepot server.
   * @param orgId the user's organization id.
   * @param password The password for the user.
   * @param collectorId The CollectorProcessDefinitionId used to initialize this
   *        collector.
   * @param debug flag for debugging messages.
   * @throws BadCredentialException if the user or password don't match the
   *         credentials in WattDepot.
   * @throws IdNotFoundException if the processId is not defined.
   * @throws BadSensorUriException if the Sensor's URI isn't valid.
   */
  public StressCollector(String serverUri, String username, String orgId, String password,
      String collectorId, boolean debug) throws BadCredentialException, IdNotFoundException,
      BadSensorUriException {
    super(serverUri, username, orgId, password, collectorId, debug);
    // TODO Auto-generated constructor stub
  }

  /**
   * @param serverUri The URI for the WattDepot server.
   * @param username The name of a user defined in the WattDepot server.
   * @param orgId the user's organization id.
   * @param password The password for the user.
   * @param sensorId The SensorId.
   * @param pollingInterval The polling interval.
   * @param depository The depository to store the measurements in.
   * @param debug flag for debugging messages.
   * @throws BadCredentialException if the user or password don't match the
   *         credentials in WattDepot.
   * @throws IdNotFoundException if the processId is not defined.
   * @throws BadSensorUriException if the Sensor's URI isn't valid.
   */
  public StressCollector(String serverUri, String username, String orgId, String password,
      String sensorId, Long pollingInterval, Depository depository, boolean debug)
      throws BadCredentialException, BadSensorUriException, IdNotFoundException {
    super(serverUri, username, orgId, password, sensorId, pollingInterval, depository, debug);
    // TODO Auto-generated constructor stub
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.util.TimerTask#run()
   */
  @Override
  public void run() {
    Measurement meas = generateFakeMeasurement();
    if (meas != null) {
      try {
        this.client.putMeasurement(depository, meas);
      }
      catch (MeasurementTypeException e) {
        System.err.format("%s does not store %s measurements%n", depository.getName(),
            meas.getMeasurementType());
      }
      if (debug) {
        System.out.println(meas);
      }
    }    
  }

  /**
   * @return Generates a fake Measurement with the current time. 
   */
  private Measurement generateFakeMeasurement() {
    Date now = new Date();
    Unit<?> unit = Unit.valueOf(this.depository.getMeasurementType().getUnits());
    Measurement ret = new Measurement(this.definition.getSensorId(), now, wattHours++, unit);
    return ret;
  }

  /**
   * Processes the command line arguments and starts the eGauge Collector.
   * 
   * @param args command line arguments.
   */
  public static void main(String[] args) {
    Options options = new Options();
    options.addOption("h", "help", false,
        "Usage: StressCollector <server uri> <username> <password> <collectorid>");
    options.addOption("s", "server", true, "WattDepot Server URI. (http://server.wattdepot.org)");
    options.addOption("u", "username", true, "Username");
    options.addOption("o", "organizationId", true, "User's Organization id.");
    options.addOption("p", "password", true, "Password");
    options.addOption("c", "collector", true, "Collector Process Definition Id");
    options.addOption("d", "debug", false, "Displays sensor data as it is sent to the server.");

    CommandLine cmd = null;
    String serverUri = null;
    String username = null;
    String organizationId = null;
    String password = null;
    String collectorId = null;
    boolean debug = false;

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
      formatter.printHelp("StressCollector", options);
      System.exit(0);
    }
    if (cmd.hasOption("s")) {
      serverUri = cmd.getOptionValue("s");
    }
    else {
      serverUri = "http://localhost:8192/";
    }
    if (cmd.hasOption("u")) {
      username = cmd.getOptionValue("u");
    }
    else {
      username = UserInfo.ROOT.getUid();
    }
    if (cmd.hasOption("o")) {
      organizationId = cmd.getOptionValue("o");
    }
    else {
      organizationId = Organization.ADMIN_GROUP.getId();
    }
    if (cmd.hasOption("p")) {
      password = cmd.getOptionValue("p");
    }
    else {
      password = "default";
    }
    if (cmd.hasOption("c")) {
      collectorId = cmd.getOptionValue("c");
    }
    else {
      collectorId = "stress_power";
    }

    debug = cmd.hasOption("d");

    if (debug) {
      System.out.println("WattDepot Server: " + serverUri);
      System.out.println("Username: " + username);
      System.out.println("OrganizationID: " + organizationId);
      System.out.println("Password: " + password);
      System.out.println("Collector Process Definition Id: " + collectorId);
      System.out.println("debug: " + debug);
      System.out.println();
    }
    try {
      if (!MultiThreadedCollector.start(serverUri, username, organizationId, password, collectorId,
          debug)) {
        System.exit(1);
      }
    }
    catch (InterruptedException e) {
      e.printStackTrace();
      System.exit(1);
    }
    catch (BadCredentialException e) {
      e.printStackTrace();
      System.exit(1);
    }
  }

}
