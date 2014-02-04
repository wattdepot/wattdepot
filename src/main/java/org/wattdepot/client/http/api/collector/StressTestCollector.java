/**
 * StressTestCollector.java This file is part of WattDepot.
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
import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;

import javax.measure.unit.Unit;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.wattdepot.client.http.api.WattDepotClient;
import org.wattdepot.common.domainmodel.CollectorProcessDefinition;
import org.wattdepot.common.domainmodel.Depository;
import org.wattdepot.common.domainmodel.Measurement;
import org.wattdepot.common.domainmodel.MeasurementType;
import org.wattdepot.common.domainmodel.Property;
import org.wattdepot.common.domainmodel.Sensor;
import org.wattdepot.common.domainmodel.SensorModel;
import org.wattdepot.common.exception.BadCredentialException;
import org.wattdepot.common.exception.BadSensorUriException;
import org.wattdepot.common.exception.IdNotFoundException;
import org.wattdepot.common.exception.MeasurementTypeException;
import org.wattdepot.common.util.SensorModelHelper;
import org.wattdepot.common.util.logger.WattDepotLoggerUtil;
import org.wattdepot.common.util.tstamp.Tstamp;

/**
 * StressTestCollector is a stand alone collector that stresses the
 * WattDepotServer by producing fake Measurements.
 * 
 * @author Cam Moore
 * 
 */
public class StressTestCollector extends TimerTask {

  /** Flag for debugging messages. */
  private boolean debug;
  /** The definition about the collector. */
  private CollectorProcessDefinition definition;
  /** The client used to communicate with the WattDepot server. */
  private WattDepotClient client;
  /** The Depository for storing measurements. */
  private Depository depository;
  /**
   * Stores the fake wattHours value that will be incremented each time it is
   * sent to the server.
   */
  private double wattHours = 0;

  /**
   * Default constructor.
   */
  public StressTestCollector() {
    // TODO Auto-generated constructor stub
  }

  /**
   * Initializes the StressTestCollector.
   * 
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
  public StressTestCollector(String serverUri, String username, String orgId,
      String password, String collectorId, boolean debug)
      throws BadCredentialException, IdNotFoundException, BadSensorUriException {
    this.client = new WattDepotClient(serverUri, username, orgId, password);
    this.debug = debug;
    if (!client.isDefinedCollectorProcessDefinition(collectorId)) {
      initializeStressTestItems(collectorId);
    }
    this.definition = client.getCollectorProcessDefinition(collectorId);
    this.depository = client.getDepository(definition.getDepositoryId());

  }

  /**
   * @param args command line arguments.
   */
  public static void main(String[] args) {
    WattDepotLoggerUtil.removeClientLoggerHandlers();
    Options options = new Options();
    options
        .addOption(
            "h",
            false,
            "Usage: StressTestCollector -s <server uri> -u <username>"
                + " -p <password> -o <orgId> -c <collectorId> -n <num sim sensors> -m <millisecond sleep> [-d]");
    options.addOption("s", "server", true,
        "WattDepot Server URI. (http://server.wattdepot.org)");
    options.addOption("u", "username", true, "Username");
    options.addOption("o", "organizationId", true, "User's Organization id.");
    options.addOption("p", "password", true, "Password");
    options
        .addOption("c", "collector", true, "Collector Process Definition Id");
    options.addOption("n", "numberOfSensors", true,
        "Number of Simulated Sensors");
    options.addOption("m", "milliseconds", true,
        "Number of milliseconds to sleep between measurements.");
    options.addOption("d", "debug", false,
        "Displays sensor data as it is sent to the server.");

    CommandLine cmd = null;
    String serverUri = null;
    String username = null;
    String organizationId = null;
    String password = null;
    String collectorId = null;
    Integer numSensors = null;
    Integer milliSeconds = null;
    boolean debug = false;

    CommandLineParser parser = new PosixParser();
    HelpFormatter formatter = new HelpFormatter();
    try {
      cmd = parser.parse(options, args);
    }
    catch (ParseException e) {
      System.err.println("Command line parsing failed. Reason: "
          + e.getMessage() + ". Exiting.");
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
    if (cmd.hasOption("n")) {
      numSensors = Integer.parseInt(cmd.getOptionValue("n"));
    }
    else {
      numSensors = 10;
    }
    if (cmd.hasOption("m")) {
      milliSeconds = Integer.parseInt(cmd.getOptionValue("m"));
    }
    else {
      milliSeconds = 1000;
    }
    debug = cmd.hasOption("d");
    if (debug) {
      System.out.println("Stress Test Collector:");
      System.out.println("    WattDepotServer: " + serverUri);
      System.out.println("    Username: " + username);
      System.out.println("    OrganizationId: " + organizationId);
      System.out.println("    Password :" + password);
      System.out.println("    CPD Id: " + collectorId);
      System.out.println("    Num Sensors: " + numSensors);
      System.out.println("    MilliSeconds: " + milliSeconds);
    }

    for (int i = 0; i < numSensors; i++) {
      Timer t = new Timer();
      try {
        StressTestCollector collector = new StressTestCollector(serverUri,
            username, organizationId, password, collectorId, debug);
        System.out.format("Started StressTestCollector at %s%n",
            Tstamp.makeTimestamp());
        t.schedule(collector, 0, milliSeconds);
        Thread.sleep(1000 / numSensors);
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
      catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * @return Generates a fake Measurement with the current time.
   */
  private Measurement generateFakeMeasurement() {
    Date now = new Date();
    Unit<?> unit = Unit
        .valueOf(this.depository.getMeasurementType().getUnits());
    Measurement ret = new Measurement(this.definition.getSensorId(), now,
        wattHours++, unit);
    return ret;
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
        System.err.format("%s does not store %s measurements%n",
            depository.getName(), meas.getMeasurementType());
      }
      if (debug) {
        System.out.println(meas);
      }
    }
  }

  /**
   * Ensures that there is a StressTest sensor, depository, and collector
   * process definition.
   * @param collectorId the id of the collector to define.
   */
  private void initializeStressTestItems(String collectorId) {
    SensorModel model = SensorModelHelper.models.get(SensorModelHelper.STRESS);
    Sensor stressSensor = new Sensor("Stress Test Sensor",
        "http://stress.test.sensor", model.getId(), client.getOrganizationId());
    if (!client.isDefinedSensor(stressSensor.getId())) {
      client.putSensor(stressSensor);
    }
    MeasurementType measType = null;
    if (client.isDefinedMeasurementType("energy-wh")) {
      try {
        measType = client.getMeasurementType("energy-wh");
        Depository depository = new Depository("Stress Test Energy", measType,
            client.getOrganizationId());
        if (!client.isDefinedDepository(depository.getId())) {
          client.putDepository(depository);
        }
        CollectorProcessDefinition cpd = new CollectorProcessDefinition(
            collectorId, "Stress Test Collector", stressSensor.getId(), 1L,
            depository.getId(), new HashSet<Property>(),
            client.getOrganizationId());
        if (!client.isDefinedCollectorProcessDefinition(cpd.getId())) {
          client.putCollectorProcessDefinition(cpd);
        }
      }
      catch (IdNotFoundException e) { // NOPMD
        // can't happen
      }
    }
  }
}
