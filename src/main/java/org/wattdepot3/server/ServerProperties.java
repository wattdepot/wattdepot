/**
 * ServerProperties.java This file is part of WattDepot 3.
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

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import org.wattdepot3.util.UserHome;

/**
 * ServerProperties - Provides access to the values stored in the
 * wattdepot3-server.properties file.
 * 
 * @author Cam Moore
 * 
 */
public class ServerProperties {
  /** The full path to the server's home directory. */
  public static final String SERVER_HOME_DIR = "wattdepot3-server.homedir";
  /** Name of property used to store the admin username. */
  public static final String ADMIN_USER_NAME = "wattdepot3-server.admin.name";
  /** Name of property used to store the admin password. */
  public static final String ADMIN_USER_PASSWORD = "wattdepot3-server.admin.password";
  /** The WattDepot implementation class. */
  public static final String WATT_DEPOT_IMPL_KEY = "wattdepot3-server.wattdepot.impl";
  /** The wattdepot3 server port key. */
  public static final String PORT_KEY = "wattdepot3-server.port";
  /** The database connection driver class. */
  public static final String DB_CONNECTION_DRIVER = "wattdepot3-server.db.connection.driver";
  /** The database connection driver url. */
  public static final String DB_CONNECTION_URL = "wattdepot3-server.db.connection.url";
  /** The database username. */
  public static final String DB_USER_NAME = "wattdepot3-server.db.username";
  /** The database password. */
  public static final String DB_PASSWORD = "wattdepot3-server.db.password";
  /** The database show sql. */
  public static final String DB_SHOW_SQL = "wattdepot3-server.db.show.sql";
  /**
   * The database drop&create tables. valid values are 'validate' | 'update' |
   * 'create' | 'create-drop'.
   */
  public static final String DB_TABLE_UPDATE = "wattdepot3-server.db.update";
  /** The WattDepot implementation class during testing. */
  public static final String TEST_WATT_DEPOT_IMPL_KEY = "wattdepot3-server.test.wattdepot.impl";
  /** The wattdepot3 server port key during testing. */
  public static final String TEST_PORT_KEY = "wattdepot3-server.test.port";

  /** Where we store the properties. */
  private Properties properties;

  /**
   * Creates a new ServerProperties instance using the default filename. Prints
   * an error to the console if problems occur on loading.
   */
  public ServerProperties() {
    this(null);
  }

  /**
   * Creates a new ServerProperties instance loaded from the given filename.
   * Prints an error to the console if problems occur on loading.
   * 
   * @param serverSubdir
   *          The name of the subdirectory used to store all files for this
   *          server.
   */
  public ServerProperties(String serverSubdir) {
    try {
      initializeProperties(serverSubdir);
    }
    catch (Exception e) {
      System.out.println("Error initializing server properties. " + e.getMessage());
    }
  }

  /**
   * Sets the properties to their "test" equivalents and updates the system
   * properties.
   */
  public void setTestProperties() {
    properties.setProperty(PORT_KEY, properties.getProperty(TEST_PORT_KEY));
    properties.setProperty(WATT_DEPOT_IMPL_KEY, properties.getProperty(TEST_WATT_DEPOT_IMPL_KEY));
    trimProperties(properties);
    // update the system properties object to reflect these new values.
    Properties systemProperties = System.getProperties();
    systemProperties.putAll(properties);
    System.setProperties(systemProperties);
  }

  /**
   * Returns a string containing all current properties in alphabetical order.
   * Properties with the word "password" in their key list "((hidden))" rather
   * than their actual value for security reasons. This is not super
   * sophisticated - other properties such as database URLs might benefit from
   * being hidden too - but it should work for now.
   * 
   * @return A string with the properties.
   */
  public String echoProperties() {
    String cr = System.getProperty("line.separator");
    String eq = " = ";
    String pad = "                ";
    // Adding them to a treemap has the effect of alphabetizing them.
    TreeMap<String, String> alphaProps = new TreeMap<String, String>();
    for (Map.Entry<Object, Object> entry : this.properties.entrySet()) {
      String propName = (String) entry.getKey();
      String propValue = (String) entry.getValue();
      alphaProps.put(propName, propValue);
    }
    StringBuffer buff = new StringBuffer(25);
    buff.append("Server Properties:").append(cr);
    for (String key : alphaProps.keySet()) {
      if (key.contains("password")) {
        buff.append(pad).append(key).append(eq).append("((hidden))").append(cr);
      }
      else {
        buff.append(pad).append(key).append(eq).append(get(key)).append(cr);
      }
    }
    return buff.toString();
  }

  /**
   * Returns the value of the Server Property specified by the key.
   * 
   * @param key
   *          Should be one of the public static final strings in this class.
   * @return The value of the key, or null if not found.
   */
  public String get(String key) {
    return this.properties.getProperty(key);
  }

  /**
   * Ensures that the there is no leading or trailing whitespace in the property
   * values. The fact that we need to do this indicates a bug in Java's
   * Properties implementation to me.
   * 
   * @param properties
   *          The properties.
   */
  private void trimProperties(Properties properties) {
    // Have to do this iteration in a Java 5 compatible manner. no
    // stringPropertyNames().
    for (Map.Entry<Object, Object> entry : properties.entrySet()) {
      String propName = (String) entry.getKey();
      properties.setProperty(propName, properties.getProperty(propName).trim());
    }
  }

  /**
   * Reads in the properties in
   * ~/.wattdepot3/server/wattdepot3-server.properties if this file exists, and
   * provides default values for all properties not mentioned in this file. Will
   * also add any pre-existing System properties that start with
   * "wattdepot3-server.".
   * 
   * @param serverSubdir
   *          The name of the subdirectory used to store all files for this
   *          server.
   * @throws Exception
   *           if errors occur.
   */
  private void initializeProperties(String serverSubdir) throws Exception {
    String userHome = UserHome.getHomeString();
    String wattDepot3Home = userHome + "/.wattdepot3/";
    String serverHome = null;
    if (serverSubdir == null) {
      serverHome = wattDepot3Home + "server";
    }
    else {
      serverHome = wattDepot3Home + serverSubdir;
    }
    String propFileName = serverHome + "/wattdepot3-server.properties";
    String defaultAdminName = "admin";
    String defaultAdminPassword = "admin";
    String defaultWattDepotImpl = "org.wattdepot3.server.depository.impl.hibernate.WattDepotImpl";
    String defaultPort = "8192";
    String defaultTestPort = "8194";
    this.properties = new Properties();
    // Set default values
    properties.setProperty(SERVER_HOME_DIR, serverHome);
    properties.setProperty(ADMIN_USER_NAME, defaultAdminName);
    properties.setProperty(ADMIN_USER_PASSWORD, defaultAdminPassword);
    properties.setProperty(WATT_DEPOT_IMPL_KEY, defaultWattDepotImpl);
    properties.setProperty(PORT_KEY, defaultPort);
    properties.setProperty(DB_CONNECTION_DRIVER, "org.postgresql.Driver");
    properties.setProperty(DB_CONNECTION_URL, "jdbc:postgresql://localhost:5432/wattdepot3");
    properties.setProperty(DB_USER_NAME, "myuser");
    properties.setProperty(DB_PASSWORD, "secret");
    properties.setProperty(DB_SHOW_SQL, "false");
    properties.setProperty(DB_TABLE_UPDATE, "update");
    properties.setProperty(TEST_PORT_KEY, defaultTestPort);
    properties.setProperty(TEST_WATT_DEPOT_IMPL_KEY, defaultWattDepotImpl);

    // grab all of the properties in the environment
    Map<String, String> systemProps = System.getenv();
    for (Map.Entry<String, String> prop : systemProps.entrySet()) {
      if (prop.getKey().startsWith("wattdepot3-server.")) {
        properties.setProperty(prop.getKey(), prop.getValue());
      }
    }
    // Use properties from file, if they exist.
    FileInputStream stream = null;
    try {
      stream = new FileInputStream(propFileName);
      properties.load(stream);
      System.out.println("Loading Server properties from: " + propFileName);
    }
    catch (IOException e) {
      System.out.println(propFileName + " not found. Using default server properties.");
    }
    finally {
      if (stream != null) {
        stream.close();
      }
    }
    addServerSystemProperties(this.properties);
    trimProperties(properties);

    System.out.println(echoProperties());
  }

  /**
   * Finds any System properties whose key begins with "wattdepot-server.", and
   * adds those key-value pairs to the passed Properties object.
   * 
   * @param properties
   *          The properties instance to be updated with the WattDepot system
   *          properties.
   */
  private void addServerSystemProperties(Properties properties) {
    Properties systemProperties = System.getProperties();
    for (Map.Entry<Object, Object> entry : systemProperties.entrySet()) {
      String sysPropName = (String) entry.getKey();
      if (sysPropName.startsWith("wattdepot3-server.")) {
        String sysPropValue = (String) entry.getValue();
        properties.setProperty(sysPropName, sysPropValue);
      }
    }
  }

}
