/**
 * SharkCollector.java This file is part of WattDepot.
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
package org.wattdepot.client.http.api.collector;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Date;

import javax.measure.unit.NonSI;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;

import net.wimpi.modbus.Modbus;
import net.wimpi.modbus.io.ModbusTCPTransaction;
import net.wimpi.modbus.msg.ExceptionResponse;
import net.wimpi.modbus.msg.ModbusResponse;
import net.wimpi.modbus.msg.ReadMultipleRegistersRequest;
import net.wimpi.modbus.msg.ReadMultipleRegistersResponse;
import net.wimpi.modbus.net.TCPMasterConnection;
import net.wimpi.modbus.util.ModbusUtil;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.jscience.physics.amount.Amount;
import org.wattdepot.common.domainmodel.Measurement;
import org.wattdepot.common.domainmodel.MeasurementType;
import org.wattdepot.common.domainmodel.Organization;
import org.wattdepot.common.domainmodel.Sensor;
import org.wattdepot.common.domainmodel.UserInfo;
import org.wattdepot.common.exception.BadCredentialException;
import org.wattdepot.common.exception.BadSensorUriException;
import org.wattdepot.common.exception.IdNotFoundException;
import org.wattdepot.common.exception.MeasurementTypeException;
import org.wattdepot.common.util.tstamp.Tstamp;

/**
 * SharkCollector - Polls a ElectroIndustries Shark power meter for power &
 * energy data periodically, and sends the results to a WattDepot server. For
 * more information about Shark meters, see:
 * http://www.electroind.com/pdf/Shark_New
 * /E149721_Shark_200-S_Meter_Manual_V.1.03.pdf
 * 
 * Inspired by the wattdepot2 SharkSensor.
 * 
 * @author Cam Moore
 * 
 */
public class SharkCollector extends MultiThreadedCollector {

  // Note that the Modbus register map in the Shark manual appears to start at
  // 1, while jamod
  // expects it to start at 0, so you must subtract 1 from the register index
  // listed in the manual!
  /** Register index for "Power & Energy Format". */
  private static final int ENERGY_FORMAT_REGISTER = 30006 - 1;
  /** Number of words (registers) that make up "Power & Energy Format". */
  private static final int ENERGY_FORMAT_LENGTH = 1;
  /** Register index for "W-hours, Total". */
  private static final int ENERGY_REGISTER = 1506 - 1;
  /** Number of words (registers) that make up "W-hours, Total". */
  private static final int ENERGY_LENGTH = 2;
  /** Register index for "Watts, 3-Ph total". */
  private static final int POWER_REGISTER = 1018 - 1;
  /** Number of words (registers) that make up "Watts, 3-Ph total". */
  private static final int POWER_LENGTH = 2;
  /** InetAddress of the meter to be polled. */
  private InetAddress meterAddress;
  /** The Shark sensor. */
  private Sensor sensor;
  /**
   * Stores the energy multiplier configured on the meter, which really means
   * whether the energy value returned is in Wh, kWh, or MWh.
   */
  private double energyMultiplier = 0;
  /**
   * Stores the configured number of energy digits after the decimal point. For
   * example, if the retrieved energy value is "12345678" and the decimals value
   * is 2, then the actual energy value is "123456.78".
   */
  private int energyDecimals = 0;
  /**
   * Whether all required parameters (such as energy format parameters from the
   * meter) have been successfully retrieved.
   */
  private boolean initialized = false;

  /** The MeasurementType the Depository stores. */
  private MeasurementType measType;
  /** The Unit of the depository. */
  private Unit<?> measUnit;
  /** The name of the sensor this collector is polling. */
  private String sensorName;

  /**
   * Initializes the SharkCollector.
   * 
   * @param serverUri
   *          The URI for the WattDepot server.
   * @param username
   *          The name of a user defined in the WattDepot server.
   * @param orgId
   *          the user's organization id.
   * @param password
   *          The password for the user.
   * @param collectorId
   *          The CollectorProcessDefinitionId used to initialize this
   *          collector.
   * @param debug
   *          flag for debugging messages.
   * @throws BadCredentialException
   *           if the user or password don't match the credentials in WattDepot.
   * @throws IdNotFoundException
   *           if the processId is not defined.
   * @throws BadSensorUriException
   *           if the Sensor's URI isn't valid.
   */
  public SharkCollector(String serverUri, String username, String orgId, String password,
      String collectorId, boolean debug) throws BadCredentialException, IdNotFoundException,
      BadSensorUriException {
    super(serverUri, username, orgId, password, collectorId, debug);
    this.measType = depository.getMeasurementType();
    this.measUnit = Unit.valueOf(measType.getUnits());
    this.sensor = client.getSensor(definition.getSensorId());
    this.sensorName = sensor.getName();

    URL sensorURL;
    try {
      sensorURL = new URL(sensor.getUri());
      String sensorHostName = sensorURL.getHost();
      this.meterAddress = InetAddress.getByName(sensorHostName);
    }
    catch (MalformedURLException e) {
      throw new BadSensorUriException(sensor.getUri() + " is not a valid URI.");
    }
    catch (UnknownHostException e) {
      throw new BadSensorUriException("Unable to resolve sensor at " + sensor.getUri());
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.util.TimerTask#run()
   */
  @Override
  public void run() {
    if (!this.initialized) {
      this.initialized = initialize();
    }
    if (initialized) {
      Measurement meas = pollMeter(this.meterAddress, this.energyMultiplier, this.energyDecimals);
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
  }

  /**
   * Processes the command line arguments and starts the Shark Collector.
   * 
   * @param args
   *          command line arguments.
   */
  public static void main(String[] args) {
    Options options = new Options();
    options.addOption("h", "help", false,
        "Usage: EGaugeCollector <server uri> <username> <password> <collectorid>");
    options.addOption("s", "server", true, "WattDepot Server URI. (http://server.wattdepot.org)");
    options.addOption("u", "username", true, "Username");
    options.addOption("o", "organizationId", true, "User's Organization id.");
    options.addOption("p", "password", true, "Password");
    options.addOption("c", "collectorId", true, "Collector Process Definition Id.");
    options.addOption("d", "debug", false, "Displays sensor data as it is sent to the server.");

    CommandLine cmd = null;
    String serverUri = null;
    String username = null;
    String organizationId = null;
    String password = null;
    String collectorId = null;
    boolean debug = false;

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
      formatter.printHelp("SharkCollector", options);
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
      collectorId = "ilima_6th_power";
    }

    debug = cmd.hasOption("d");

    if (debug) {
      System.out.println("WattDepot Server: " + serverUri);
      System.out.println("Username: " + username);
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

  /**
   * Need to retrieve some configuration parameters from meter, but only want to
   * do it once per session. Note that all failures to retrieve the parameters
   * are considered temporary for caution's sake, so a misconfigured meter might
   * continue to be polled indefinitely until the problem is resolved.
   * 
   * @return True if configuration parameters could be retrieved from meter,
   *         false otherwise.
   */
  private boolean initialize() {
    ModbusResponse response;
    ReadMultipleRegistersResponse goodResponse = null;
    try {
      response = readRegisters(this.meterAddress, ENERGY_FORMAT_REGISTER, ENERGY_FORMAT_LENGTH);
    }
    catch (Exception e) {
      System.err.format(
          "%s: Unable to retrieve energy format parameters from meter %s: %s, retrying.%n",
          Tstamp.makeTimestamp(), this.sensorName, e.getMessage());
      return false;
    }
    if (response instanceof ReadMultipleRegistersResponse) {
      goodResponse = (ReadMultipleRegistersResponse) response;
    }
    else if (response instanceof ExceptionResponse) {
      System.err
          .format(
              "Got Modbus exception response while retrieving energy format parameters from meter %s, code: %s%n",
              this.sensorName, ((ExceptionResponse) response).getExceptionCode());
      return false;
    }
    else {
      System.err
          .format(
              "Got strange Modbus reply while retrieving energy format parameters from meter %s, aborting.%n",
              this.sensorName);
      return false;
    }

    double energyMultiplier = decodeEnergyMultiplier(goodResponse);
    if (energyMultiplier == 0) {
      System.err.format("Got bad energy multiplier from meter %s energy format, aborting.%n",
          this.sensorName);
      return false;
    }

    int energyDecimals = decodeEnergyDecimals(goodResponse);
    if (energyDecimals == 0) {
      System.err.format("Got bad energy decimal format from meter %s energy format, aborting.%n",
          this.sensorName);
      return false;
    }
    // Able to retrieve all the energy format parameters, so set the class
    // fields for future use
    this.energyMultiplier = energyMultiplier;
    this.energyDecimals = energyDecimals;
    return true;

  }

  /**
   * Reads one or more consecutive registers from a device using Modbus/TCP
   * using the default TCP port (502).
   * 
   * @param address
   *          The IP address of the device to be read from
   * @param register
   *          The Modbus register to be read, as a decimal integer
   * @param length
   *          The number of registers to read
   * @return A ReadMultipleRegistersResponse containing the response from the
   *         device, or null if there was some unexpected error.
   * @throws Exception
   *           If an error occurs while attempting to read the registers.
   */
  private ModbusResponse readRegisters(InetAddress address, int register, int length)
      throws Exception {
    return readRegisters(address, Modbus.DEFAULT_PORT, register, length);
  }

  /**
   * Reads one or more consecutive registers from a device using Modbus/TCP.
   * 
   * @param address
   *          The IP address of the device to be read from
   * @param port
   *          The destination TCP port to connect to
   * @param register
   *          The Modbus register to be read, as a decimal integer
   * @param length
   *          The number of registers to read
   * @return A ReadMultipleRegistersResponse containing the response from the
   *         device, or null if there was some unexpected error.
   * @throws Exception
   *           If an error occurs while attempting to read the registers.
   */
  private ModbusResponse readRegisters(InetAddress address, int port, int register, int length)
      throws Exception {
    TCPMasterConnection connection = null;
    ModbusTCPTransaction transaction = null;
    ReadMultipleRegistersRequest request = null;
    ModbusResponse response = null;

    // Open the connection
    connection = new TCPMasterConnection(address);
    try {
      connection.setPort(port);
      connection.connect();

      // Prepare the request
      request = new ReadMultipleRegistersRequest(register, length);

      // Prepare the transaction
      transaction = new ModbusTCPTransaction(connection);
      transaction.setRequest(request);
      transaction.execute();
      response = transaction.getResponse();

    }
    finally {
      // Close the connection
      connection.close();
    }

    return response;
  }

  /**
   * Decodes the energy multiplier configured on the meter, which really means
   * whether the energy value returned is in Wh, kWh, or MWh.
   * 
   * @param response
   *          The response from the meter containing the power and energy
   *          format.
   * @return A double that represents the scale which energy readings should be
   *         multiplied by, or 0 if there was some problem decoding the value.
   */
  private double decodeEnergyMultiplier(ReadMultipleRegistersResponse response) {
    if ((response != null) && (response.getWordCount() == 1)) {
      // From Shark manual, bitmap looks like this ("-" is unused bit
      // apparently):
      // ppppiinn feee-ddd
      //
      // pppp = power scale (0-unit, 3-kilo, 6-mega, 8-auto)
      // ii = power digits after decimal point (0-3),
      // applies only if f=1 and pppp is not auto
      // nn = number of energy digits (5-8 --> 0-3)
      // eee = energy scale (0-unit, 3-kilo, 6-mega)
      // f = decimal point for power
      // (0=data-dependant placement, 1=fixed placement per ii value)
      // ddd = energy digits after decimal point (0-6)

      // Get energy scale by shifting off 4 bits and then mask with 111
      int energyScale = (response.getRegisterValue(0) >>> 4) & 7;
      switch (energyScale) {
      case 0:
        // watts
        return 1.0;
      case 3:
        // kilowatts
        return 1000.0;
      case 6:
        // megawatts
        return 1000000.0;
      default:
        // should never happen, according to manual, so return 0
        // System.err.println("Unknown energy scale from meter, defaulting to kWh");
        return 0.0;
      }
    }
    else {
      return 0.0;
    }
  }

  /**
   * Decodes the configured number of energy digits after the decimal point. For
   * example, if the retrieved energy value is "12345678" and the decimals value
   * is 2, then the actual energy value is "123456.78".
   * 
   * @param response
   *          The response from the meter containing the power and energy
   *          format.
   * @return An int that represents the number of ending digits from the energy
   *         reading that should be considered as decimals.
   */
  private int decodeEnergyDecimals(ReadMultipleRegistersResponse response) {
    if ((response != null) && (response.getWordCount() == 1)) {
      // From Shark manual, bitmap looks like this ("-" is unused bit
      // apparently):
      // ppppiinn feee-ddd
      //
      // pppp = power scale (0-unit, 3-kilo, 6-mega, 8-auto)
      // ii = power digits after decimal point (0-3),
      // applies only if f=1 and pppp is not auto
      // nn = number of energy digits (5-8 --> 0-3)
      // eee = energy scale (0-unit, 3-kilo, 6-mega)
      // f = decimal point for power
      // (0=data-dependant placement, 1=fixed placement per ii value)
      // ddd = energy digits after decimal point (0-6)

      // Get # of energy digits after decimal point by masking with 111
      return response.getRegisterValue(0) & 7;
    }
    else {
      return 0;
    }
  }

  /**
   * @return true if the depository stores power measurements.
   */
  private boolean isPower() {
    return measType.getId().startsWith("power");
  }

  /**
   * Connects to the meter, retrieves the latest data, and returns it as a
   * Measurement.
   * 
   * @param sensorAddress
   *          The address of the meter.
   * @param energyMultiplier
   *          The amount to multiply energy readings to account for unit of
   *          measurement.
   * @param energyDecimals
   *          The configured number of decimals include in the energy reading.
   * @return The meter data as a Measurement.
   */
  private Measurement pollMeter(InetAddress sensorAddress, double energyMultiplier,
      int energyDecimals) {
    return pollMeter(sensorAddress, Modbus.DEFAULT_PORT, energyMultiplier, energyDecimals);
  }

  /**
   * @param sensorAddress
   *          The address of the meter.
   * @param port
   *          The port to communicate with.
   * @param energyMultiplier
   *          The amount to multiply energy readings to account for unit of
   *          measurement.
   * @param energyDecimals
   *          The configured number of decimals include in the energy reading.
   * @return The meter data as a Measurement.
   */
  private Measurement pollMeter(InetAddress sensorAddress, int port, double energyMultiplier,
      int energyDecimals) {
    Measurement meas = null;
    // Record current time as close approximation to time for reading we are
    // about to make
    Date timestamp = new Date();
    ModbusResponse energyResponse, powerResponse;
    ReadMultipleRegistersResponse goodResponse = null;
    try {
      // Make both queries in rapid succession to reduce any lag on sensor side
      energyResponse = readRegisters(sensorAddress, port, ENERGY_REGISTER, ENERGY_LENGTH);
      powerResponse = readRegisters(sensorAddress, port, POWER_REGISTER, POWER_LENGTH);
    }
    catch (Exception e) {
      System.err.format("%s Unable to retrieve energy data from meter %s: %s.%n", new Date(),
          this.sensorName, e.getMessage());
      return null;
    }

    // First handle energy response
    if (energyResponse instanceof ReadMultipleRegistersResponse) {
      goodResponse = (ReadMultipleRegistersResponse) energyResponse;
    }
    else if (energyResponse instanceof ExceptionResponse) {
      System.err.format(
          "Got Modbus exception response while retrieving energy data from meter %s, code: %s%n",
          this.sensorName, ((ExceptionResponse) energyResponse).getExceptionCode());
      return null;
    }
    else {
      System.err.format("Got strange Modbus reply while retrieving energy data from meter %s.%n",
          this.sensorName);
      return null;
    }
    int wattHoursInt = ModbusUtil.registersToInt(extractByteArray(goodResponse));
    // Take integer value, divide by 10^energyDecimals to move decimal point to
    // right place,
    // then multiply by a value depending on units (nothing, kilo, or mega).
    double wattHours = (wattHoursInt / (Math.pow(10.0, energyDecimals))) * energyMultiplier;

    // Then handle power response
    if (powerResponse instanceof ReadMultipleRegistersResponse) {
      goodResponse = (ReadMultipleRegistersResponse) powerResponse;
    }
    else if (powerResponse instanceof ExceptionResponse) {
      System.err.format(
          "Got Modbus exception response while retrieving power data from meter %s, code: %s%n",
          this.sensorName, ((ExceptionResponse) powerResponse).getExceptionCode());
      return null;
    }
    else {
      System.err.println("Got strange Modbus reply while retrieving power data from meter.");
      return null;
    }
    float watts = ModbusUtil.registersToFloat(extractByteArray(goodResponse));
    Double value = null;
    // power is given in W
    Amount<?> power = Amount.valueOf(watts, SI.WATT);
    // energy given in Wh
    Amount<?> energy = Amount.valueOf(wattHours, SI.WATT.times(NonSI.HOUR));

    if (isPower()) {
      value = power.to(measUnit).getEstimatedValue();
    }
    else {
      value = energy.to(measUnit).getEstimatedValue();
    }
    meas = new Measurement(sensor.getId(), timestamp, value, measUnit);
    return meas;
  }

  /**
   * Given a response with two consecutive registers, extract the values as a 4
   * byte array so they can be passed to methods in ModbusUtil. It seems like
   * there should be a better way to do this.
   * 
   * @param response
   *          The response containing the two registers
   * @return a byte[4] array or null if there is a problem with the response.
   */
  private byte[] extractByteArray(ReadMultipleRegistersResponse response) {
    byte[] regBytes = new byte[4];
    if (response.getWordCount() == 2) {
      regBytes[0] = response.getRegister(0).toBytes()[0];
      regBytes[1] = response.getRegister(0).toBytes()[1];
      regBytes[2] = response.getRegister(1).toBytes()[0];
      regBytes[3] = response.getRegister(1).toBytes()[1];
      return regBytes;
    }
    else {
      return null;
    }
  }

}
