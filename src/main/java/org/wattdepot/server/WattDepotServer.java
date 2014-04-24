/**
 * WattDepotServer.java This file is part of WattDepot.
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
package org.wattdepot.server;

import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.wattdepot.common.util.logger.LoggerUtil;
import org.wattdepot.server.http.api.WattDepotComponent;

/**
 * WattDepotServer - The main class that starts up the WattDepotServer
 * coordinates the different interfaces.
 * 
 * @author Cam Moore
 * 
 */
public class WattDepotServer {

  /** Holds the Restlet component for the server. */
  private WattDepotComponent restletServer;
  /** Holds the WattDepot persistent store for the server. */
  private WattDepotPersistence depot;
  /** Holds the ServerProperties instance associated with this server. */
  private ServerProperties serverProperties;
  /** Holds the WattDepotLogger for the server. */
  private Logger logger = null;
  /** Holds the hostname associated with this Server. */
  private String hostName = null;

  /**
   * Default constructor.
   * 
   */
  public WattDepotServer() {
  }

  /**
   * Creates a new instance of the WattDepot server. Using the default server
   * values.
   * 
   * @return The WattDepotServer.
   * @throws Exception
   *           if there is a problem starting the server.
   */
  public static WattDepotServer newInstance() throws Exception {
    return newInstance(new ServerProperties());
  }

  /**
   * Creates a new instance of the WattDepot server.
   * 
   * @param serverSubdir
   *          The name of the directory containing the server's files.
   * @return The WattDepotServer.
   * @throws Exception
   *           if there is a problem starting the server.
   */
  public static WattDepotServer newInstance(String serverSubdir)
      throws Exception {
    return newInstance(new ServerProperties(serverSubdir));
  }

  /**
   * Creates a new instance of the WattDepot server.
   * 
   * @param properties
   *          The ServerProperties used to initialize this server.
   * @return The WattDepotServer.
   * @throws Exception
   *           if there is a problem starting the server.
   */
  public static WattDepotServer newInstance(ServerProperties properties)
      throws Exception {
    int port = Integer.parseInt(properties.get(ServerProperties.PORT_KEY));
    WattDepotServer server = new WattDepotServer();
    // System.out.println("WattDepotServer.");
    // RestletLoggerUtil.showLoggers();
    boolean enableLogging = Boolean.parseBoolean(properties
        .get(ServerProperties.ENABLE_LOGGING_KEY));
    server.serverProperties = properties;
    server.hostName = server.serverProperties.getFullHost();

    // Get the WattDepotPersistence implementation.
    String depotClass = properties.get(ServerProperties.WATT_DEPOT_IMPL_KEY);
    server.depot = (WattDepotPersistence) Class.forName(depotClass)
        .getConstructor(ServerProperties.class).newInstance(properties);
    if (server.depot.getSessionOpen() != server.depot.getSessionClose()) {
      throw new RuntimeException("opens and closed mismatched.");
    }
    server.depot.initializeMeasurementTypes();
    if (server.depot.getSessionOpen() != server.depot.getSessionClose()) {
      throw new RuntimeException("opens and closed mismatched.");
    }
    server.depot.initializeSensorModels();
    if (server.depot.getSessionOpen() != server.depot.getSessionClose()) {
      throw new RuntimeException("opens and closed mismatched.");
    }
    server.depot.setServerProperties(properties);
    server.restletServer = new WattDepotComponent(server.depot, port);
    server.logger = server.restletServer.getLogger();

    // Set up logging.
    if (enableLogging) {
      LoggerUtil.useConsoleHandler();
      Logger base = LogManager.getLogManager().getLogger(Logger.GLOBAL_LOGGER_NAME);
      base = base.getParent();
      String level = properties.get(ServerProperties.LOGGING_LEVEL_KEY);
     LoggerUtil.setLoggingLevel(base, level);

      server.logger.info("Starting WattDepot server.");
      server.logger.info("Host: " + server.hostName);
      server.logger.info(server.serverProperties.echoProperties());
    }
    else {
      LoggerUtil.useConsoleHandler();
      Logger base = LogManager.getLogManager().getLogger(Logger.GLOBAL_LOGGER_NAME);
      base = base.getParent();
      LoggerUtil.setLoggingLevel(base, Level.SEVERE.toString());
    }
    server.restletServer.start();

    server.logger.info("WattDepot server now running.");

    return server;
  }

  /**
   * Creates a new WattDepotServer suitable for unit testing.
   * 
   * @return A WattDepotServer configured for testing.
   * @throws Exception
   *           if there is a problem initializing the server.
   */
  public static WattDepotServer newTestInstance() throws Exception {
    ServerProperties properties = new ServerProperties();
    properties.setTestProperties();
    return newInstance(properties);
  }

  /**
   * @return the restletServer
   */
  public WattDepotComponent getRestletServer() {
    return restletServer;
  }

  /**
   * @return the depot
   */
  public WattDepotPersistence getDepot() {
    return depot;
  }

  /**
   * @return the serverProperties
   */
  public ServerProperties getServerProperties() {
    return serverProperties;
  }

  /**
   * Stops the WattDepotServer.
   * 
   * @throws Exception
   *           if there is a problem stopping the different servers.
   */
  public void stop() throws Exception {
    this.depot.stop();
    this.restletServer.stop();
  }

  /**
   * @param args
   *          commandline arguments.
   * @throws Exception
   *           if there is a problem starting the components.
   */
  public static void main(String[] args) throws Exception {
    Options options = new Options();
    options.addOption("h", "help", false, "Print this message");
    options
        .addOption("d", "directoryName", true,
            "subdirectory under ~/.wattdepot where this server's files are to be kept.");

    CommandLine cmd = null;
    String directoryName = null;

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
      formatter.printHelp("WattDepotServer", options);
      System.exit(0);
    }
    if (cmd.hasOption("d")) {
      directoryName = cmd.getOptionValue("d");
    }
    LoggerUtil.disableLogging();
    if ((directoryName == null) || (directoryName.length() == 0)) {
      WattDepotServer.newInstance();
    }
    else {
      WattDepotServer.newInstance(directoryName);
    }
  }

}
