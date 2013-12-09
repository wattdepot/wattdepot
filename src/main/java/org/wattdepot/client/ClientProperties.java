/**
 * ClientProperties.java This file is part of WattDepot.
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
package org.wattdepot.client;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.logging.Logger;

import org.wattdepot.util.UserHome;

/**
 * ClientProperties - Provides access to the values stored in the
 * wattdepot3-client.properties file.
 * 
 * @author Cam Moore
 * 
 */
public class ClientProperties {
  /** The full path to the client's home directory. */
  public static final String CLIENT_HOME_DIR = "wattdepot3-client.homedir";
  /** Name of property used to store the username. */
  public static final String USER_NAME = "wattdepot3-client.user.name";
  /** Name of property used to store the password. */
  public static final String USER_PASSWORD = "wattdepot3-client.user.password";
  /** The wattdepot3 server host. */
  public static final String WATTDEPOT_SERVER_HOST = "wattdepot3-server.hostname";
  /** The wattdepot3 server port key. */
  public static final String PORT_KEY = "wattdepot3-server.port";
  /** The wattdepot3 server port key during testing. */
  public static final String TEST_PORT_KEY = "wattdepot3-server.test.port";
  /** The logging level key. */
  public static final String LOGGING_LEVEL_KEY = "wattdepot-client.logging.level";

  /** Where we store the properties. */
  private Properties properties;

  /**
   * Creates a new ClientProperties instance using the default filename. Prints
   * an error to the console if problems occur on loading.
   */
  public ClientProperties() {
    this(null);
  }

  /**
   * Creates a new ClientProperties instance loaded from the given directory
   * name. Prints an error to the console if problems occur on loading.
   * 
   * @param clientSubdir
   *          The name of the subdirectory used to store all files for the
   *          client.
   */
  public ClientProperties(String clientSubdir) {
    try {
      initializeProperties(clientSubdir);
    }
    catch (Exception e) {
      Logger.getLogger("org.wattdepot.properties").info(
          "Error initializing client properties. " + e.getMessage());
    }
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
   * Sets the properties to their "test" equivalents and updates the system
   * properties.
   */
  public void setTestProperties() {
    properties.setProperty(PORT_KEY, properties.getProperty(TEST_PORT_KEY));
    trimProperties(properties);
    // update the system properties object to reflect these new values.
    Properties systemProperties = System.getProperties();
    systemProperties.putAll(properties);
    System.setProperties(systemProperties);

  }

  /**
   * Reads in the properties in wattdepot3-client.properties if the file exists,
   * and provides default values for all properties not mentioned in this file.
   * Will also add any pre-existing System properties that start with
   * "wattdepot3-client.".
   * 
   * @param clientSubdir
   *          The name of the subdirectory used to store all files for this
   *          client.
   * @throws Exception
   *           if there is a problem.
   */
  private void initializeProperties(String clientSubdir) throws Exception {
    Logger logger = Logger.getLogger("org.wattdepot.properties");
    String userHome = UserHome.getHomeString();
    String wattDepot3Home = userHome + "/.wattdepot3/";
    String clientHome = null;
    if (clientSubdir == null) {
      clientHome = wattDepot3Home + "client";
    }
    else {
      clientHome = wattDepot3Home + clientSubdir;
    }
    String propFileName = clientHome + "/wattdepot-client.properties";
    String defaultUserName = "admin";
    String defaultUserPassword = "admin";
    String defaultServerHost = "localhost";
    String defaultPort = "8192";
    String defaultTestPort = "8194";
    this.properties = new Properties();
    // Set default values
    properties.setProperty(CLIENT_HOME_DIR, clientHome);
    properties.setProperty(USER_NAME, defaultUserName);
    properties.setProperty(USER_PASSWORD, defaultUserPassword);
    properties.setProperty(WATTDEPOT_SERVER_HOST, defaultServerHost);
    properties.setProperty(PORT_KEY, defaultPort);
    properties.setProperty(TEST_PORT_KEY, defaultTestPort);
    properties.setProperty(LOGGING_LEVEL_KEY, "INFO");
    // Use properties from file, if they exist.
    FileInputStream stream = null;
    try {
      stream = new FileInputStream(propFileName);
      properties.load(stream);
      logger.info("Loading Server properties from: " + propFileName);
    }
    catch (IOException e) {
      logger.info(propFileName + " not found. Using default client properties.");
    }
    finally {
      if (stream != null) {
        stream.close();
      }
    }
    // grab all of the properties in the environment
    Map<String, String> systemProps = System.getenv();
    for (Map.Entry<String, String> prop : systemProps.entrySet()) {
      if (prop.getKey().startsWith("wattdepot-server.")) {
        properties.setProperty(prop.getKey(), prop.getValue());
      }
    }
    addClientSystemProperties(this.properties);
    trimProperties(properties);
  }

  /**
   * Finds any System properties whose key begins with "wattdepot-server.", and
   * adds those key-value pairs to the passed Properties object.
   * 
   * @param properties
   *          The properties instance to be updated with the WattDepot system
   *          properties.
   */
  private void addClientSystemProperties(Properties properties) {
    Properties systemProperties = System.getProperties();
    for (Map.Entry<Object, Object> entry : systemProperties.entrySet()) {
      String sysPropName = (String) entry.getKey();
      if (sysPropName.startsWith("wattdepot-client.")) {
        String sysPropValue = (String) entry.getValue();
        properties.setProperty(sysPropName, sysPropValue);
      }
    }
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

}
