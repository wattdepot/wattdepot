/**
 * WattDepotServer.java This file is part of WattDepot 3.
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
package org.wattdepot3.server;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

/**
 * WattDepotServer - The main class that starts up the WattDepotServer
 * coordinates the different interfaces.
 * 
 * @author Cam Moore
 * 
 */
public class WattDepotServer {

  private WattDepotComponent restletServer;
  private WattDepot depot;
  private ServerProperties serverProperties;

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
  public static WattDepotServer newInstance(String serverSubdir) throws Exception {
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
  public static WattDepotServer newInstance(ServerProperties properties) throws Exception {
    int port = Integer.parseInt(properties.get(ServerProperties.PORT_KEY));
    WattDepotServer server = new WattDepotServer();
    server.depot = (WattDepot) Class.forName(properties.get(ServerProperties.WATT_DEPOT_IMPL_KEY))
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
    server.serverProperties = properties;
    server.restletServer.start();
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
  public WattDepot getDepot() {
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
    options.addOption("d", "directoryName", true,
        "subdirectory under ~/.wattdepot3 where this server's files are to be kept.");

    CommandLine cmd = null;
    String directoryName = null;

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
      formatter.printHelp("WattDepotServer", options);
      System.exit(0);
    }
    if (cmd.hasOption("d")) {
      directoryName = cmd.getOptionValue("d");
    }

    if ((directoryName == null) || (directoryName.length() == 0)) {
      WattDepotServer.newInstance();
    }
    else {
      WattDepotServer.newInstance(directoryName);
    }
  }

}
