/**
 * NOAAWeatherCollector.java This file is part of WattDepot.
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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;

import javax.measure.unit.Unit;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.w3c.dom.Document;
import org.wattdepot.common.domainmodel.Depository;
import org.wattdepot.common.domainmodel.Measurement;
import org.wattdepot.common.domainmodel.MeasurementType;
import org.wattdepot.common.domainmodel.Organization;
import org.wattdepot.common.domainmodel.Property;
import org.wattdepot.common.domainmodel.Sensor;
import org.wattdepot.common.domainmodel.SensorModel;
import org.wattdepot.common.domainmodel.UserInfo;
import org.wattdepot.common.exception.BadCredentialException;
import org.wattdepot.common.exception.BadSensorUriException;
import org.wattdepot.common.exception.IdNotFoundException;
import org.wattdepot.common.exception.MeasurementTypeException;
import org.wattdepot.common.util.SensorModelHelper;
import org.wattdepot.common.util.tstamp.Tstamp;
import org.xml.sax.SAXException;

/**
 * NOAAWeatherCollector - Collector for eGauge meters.
 * 
 * @author Cam Moore
 * 
 */
public class NOAAWeatherCollector extends MultiThreadedCollector {

  /** The URI to access the NOAA Weather data. */
  private String noaaWeatherUri;
  /** The name of the register to be polled by the collector. */
  private String registerName;
  /** The MeasurementType the Depository stores. */
  private MeasurementType measType;
  /** The Unit of the depository. */
  private Unit<?> measUnit;
  /** The eGuage sensor. */
  private Sensor sensor;

  /**
   * Initializes the NOAAWeatherCollector.
   * 
   * @param serverUri The URI for the WattDepot server.
   * @param username The name of a user defined in the WattDepot server.
   * @param orgId the id of the organization.
   * @param password The password for the user.
   * @param collectorId The CollectorProcessDefinitionId used to initialize this
   *        collector.
   * @param debug flag for debugging messages.
   * @throws BadCredentialException if the user or password don't match the
   *         credentials in WattDepot.
   * @throws IdNotFoundException if the processId is not defined.
   * @throws BadSensorUriException if the Sensor's URI isn't valid.
   */
  public NOAAWeatherCollector(String serverUri, String username, String orgId, String password,
      String collectorId, boolean debug) throws BadCredentialException, IdNotFoundException,
      BadSensorUriException {
    super(serverUri, username, orgId, password, collectorId, debug);
    this.measType = depository.getMeasurementType();
    this.measUnit = Unit.valueOf(measType.getUnits());
    this.sensor = client.getSensor(definition.getSensorId());

    Property prop = this.definition.getProperty("registerName");
    if (prop != null) {
      this.registerName = prop.getValue();
    }
    try {
      new URL(sensor.getUri());
      this.noaaWeatherUri = sensor.getUri();
    }
    catch (MalformedURLException e) {
      throw new BadSensorUriException(client.getSensor(definition.getSensorId()).getUri()
          + " is not a valid URI.");
    }
  }

  /**
   * @param serverUri The URI for the WattDepot server.
   * @param username The name of a user defined in the WattDepot server.
   * @param orgId the user's organization id.
   * @param password The password for the user.
   * @param sensor The Sensor to poll.
   * @param pollingInterval The polling interval in seconds.
   * @param depository The Depository to store the measurements.
   * @param debug flag for debugging messages.
   * @throws BadCredentialException if the user or password don't match the
   *         credentials in WattDepot.
   * @throws BadSensorUriException if the Sensor's URI isn't valid.
   * @throws IdNotFoundException if the processId is not defined.
   */
  public NOAAWeatherCollector(String serverUri, String username, String orgId, String password,
      Sensor sensor, Long pollingInterval, Depository depository, boolean debug)
      throws BadCredentialException, BadSensorUriException, IdNotFoundException {
    super(serverUri, username, orgId, password, sensor.getId(), pollingInterval, depository, debug);
    this.measType = depository.getMeasurementType();
    this.measUnit = Unit.valueOf(measType.getUnits());
    this.sensor = client.getSensor(definition.getSensorId());

    Property prop = this.definition.getProperty("registerName");
    if (prop != null) {
      this.registerName = prop.getValue();
    }
    try {
      new URL(sensor.getUri());
      this.noaaWeatherUri = sensor.getUri();
    }
    catch (MalformedURLException e) {
      throw new BadSensorUriException(client.getSensor(definition.getSensorId()).getUri()
          + " is not a valid URI.");
    }

  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.client.impl.restlet.collector.MultiThreadedCollector#isValid
   * ()
   */
  @Override
  public boolean isValid() {
    boolean ret = super.isValid();
    if (ret) {
      // check the type of the Sensor
      try {
        SensorModel sm = client.getSensorModel(sensor.getModelId());
        String type = sm.getType();
        ret &= type.equals(SensorModelHelper.CURRENT_OBSERVATION);
      }
      catch (IdNotFoundException e) {
        ret = false;
      }
    }
    if ((this.registerName == null) || (this.registerName.length() == 0)) {
      return false;
    }
    // validate that measType is .
    if (!measType.getId().startsWith("temperature") && !measType.getId().startsWith("humidity")
        && !measType.getId().startsWith("cloud")) {
      return false;
    }

    return ret;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.util.TimerTask#run()
   */
  @Override
  public void run() {
    Measurement meas = null;
    DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
    domFactory.setNamespaceAware(true);
    try {
      DocumentBuilder builder = domFactory.newDocumentBuilder();

      // Have to make HTTP connection manually so we can set proper timeouts
      URL url = new URL(noaaWeatherUri);
      URLConnection httpConnection;
      httpConnection = url.openConnection();
      // Set both connect and read timeouts to 15 seconds. No point in long
      // timeouts since the
      // sensor will retry before too long anyway.
      httpConnection.setConnectTimeout(15 * 1000);
      httpConnection.setReadTimeout(15 * 1000);
      httpConnection.connect();

      // Record current time as close approximation to time for reading we are
      // about to make
      Date timestamp = new Date();

      Document doc = builder.parse(httpConnection.getInputStream());

      XPathFactory factory = XPathFactory.newInstance();
      XPath xPath = factory.newXPath();
      // path to the data
      String valString = "/current_observation/" + this.registerName + "/text()";
      XPathExpression exprValue = xPath.compile(valString);
      Object result = new Double(0);
      if (this.registerName.equals("weather")) {
        Object cloudCoverage = exprValue.evaluate(doc, XPathConstants.STRING);
        String cloudStr = cloudCoverage.toString();
        if (cloudStr.equalsIgnoreCase("sunny") || cloudStr.equalsIgnoreCase("clear")) {
          // 0 to 1/8 cloud coverage
          result = new Double(0.0625);
        }
        else if (cloudStr.equalsIgnoreCase("mostly sunny")
            || cloudStr.equalsIgnoreCase("mostly clear")) {
          // 1/8 to 2/8 cloud coverage
          result = new Double(0.1875);
        }
        else if (cloudStr.equalsIgnoreCase("partly sunny")
            || cloudStr.equalsIgnoreCase("partly cloudy")) {
          // 3/8 to 5/8 cloud coverage
          result = new Double(0.5);
        }
        else if (cloudStr.equalsIgnoreCase("mostly cloudy")) {
          // 6/8 to 7/8 cloud coverage
          result = new Double(0.8125);
        }
        else if (cloudStr.equalsIgnoreCase("cloudy")) {
          // 7/8 to 100% cloud coverage
          result = new Double(0.9375);
        }
      }
      else {
        result = exprValue.evaluate(doc, XPathConstants.NUMBER);
      }

      Double value = (Double) result;
      meas = new Measurement(definition.getSensorId(), timestamp, value, measUnit);
    }
    catch (MalformedURLException e) {
      System.err.format("URI %s was invalid leading to malformed URL%n", noaaWeatherUri);
    }
    catch (XPathExpressionException e) {
      System.err.println("Bad XPath expression, this should never happen.");
    }
    catch (ParserConfigurationException e) {
      System.err.println("Unable to configure XML parser, this is weird.");
    }
    catch (SAXException e) {
      System.err.format(
          "%s: Got bad XML from eGauge sensor %s (%s), hopefully this is temporary.%n",
          Tstamp.makeTimestamp(), sensor.getName(), e);
    }
    catch (IOException e) {
      System.err.format(
          "%s: Unable to retrieve data from eGauge sensor %s (%s), hopefully this is temporary.%n",
          Tstamp.makeTimestamp(), sensor.getName(), e);
    }

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
   * Processes the command line arguments and starts the eGauge Collector.
   * 
   * @param args command line arguments.
   */
  public static void main(String[] args) {
    Options options = new Options();
    options
        .addOption("h", "help", false,
            "Usage: NOAAWeatherCollector -s <server uri> -u <username> -p <password> -c <collectorid> [-d]");
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
      formatter.printHelp("NOAAWeatherCollector", options);
      System.exit(0);
    }
    if (cmd.hasOption("s")) {
      serverUri = cmd.getOptionValue("s");
    }
    else {
      serverUri = "http://localhost:8119/";
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
