/**
 * ServerProperties.java This file is part of WattDepot.
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

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.logging.Logger;

import org.wattdepot.common.domainmodel.UserInfo;
import org.wattdepot.common.domainmodel.UserPassword;
import org.wattdepot.common.util.UserHome;

/**
 * ServerProperties - Provides access to the values stored in the
 * wattdepot-server.properties file.
 * 
 * @author Cam Moore
 * 
 */
public class ServerProperties {
  /** The full path to the server's home directory. */
  public static final String SERVER_HOME_DIR = "wattdepot-server.homedir";
  /** The hostname key. */
  public static final String HOSTNAME_KEY = "wattdepot-server.hostname";
  /** Name of property used to store the admin username. */
  public static final String ADMIN_USER_NAME = "wattdepot-server.admin.name";
  /** The environment variable for storing the admin's name. */
  public static final String ADMIN_USER_NAME_ENV = "WATTDEPOT_ADMIN_NAME";
  /** Name of property used to store the admin password. */
  public static final String ADMIN_USER_PASSWORD = "wattdepot-server.admin.password";
  /** The environment variable for storing the admin's password. */
  public static final String ADMIN_USER_PASSWORD_ENV = "WATTDEPOT_ADMIN_PASSWORD";
  /**
   * The environment variable name that holds the salt used for encrypting
   * passwords in WattDepot.
   */
  public static final String WATTDEPOT_SALT_ENV = "WATTDEPOT_SALT";
  /** The WattDepot implementation class. */
  public static final String WATT_DEPOT_IMPL_KEY = "wattdepot-server.wattdepot.impl";
  /** The wattdepot server port key. */
  public static final String PORT_KEY = "wattdepot-server.port";
  /** The context root key. */
  public static final String CONTEXT_ROOT_KEY = "wattdepot-server.context.root";

  /** The option to enable SSL. */
  public static final String SSL = "wattdepot-server.ssl";
  /** The path to the keystore holding the certificate. */
  public static final String SSL_KEYSTORE_PATH= "wattdepot-server.ssl.keystore.path";
  /** The password for the keystore */
  public static final String SSL_KEYSTORE_PASSWORD = "wattdepot-server.ssl.keystore.password";
  /** The password for the key */
  public static final String SSL_KEYSTORE_KEY_PASSWORD = "wattdepot-server.ssl.keystore.key.password";
  /** The type of keystore */
  public static final String SSL_KEYSTORE_TYPE = "wattdepot-server.ssl.keystore.type";

  /** The database connection driver class. */
  public static final String DB_CONNECTION_DRIVER = "wattdepot-server.db.connection.driver";
  /** The database connection driver url. */
  public static final String DB_CONNECTION_URL = "wattdepot-server.db.connection.url";
  /** The database connection driver url environment variable. */
  public static final String DB_CONNECTION_URL_ENV = "WATTDEPOT_DATABASE_URL";
  /** The database url. */
  public static final String DATABASE_URL = "wattdepot-server.database.url";
  /** The database username. */
  public static final String DB_USER_NAME = "wattdepot-server.db.username";
  /** The database password. */
  public static final String DB_PASSWORD = "wattdepot-server.db.password";
  /** The database show sql. */
  public static final String DB_SHOW_SQL = "wattdepot-server.db.show.sql";
  /**
   * The database drop&create tables. valid values are 'validate' | 'update' |
   * 'create' | 'create-drop'.
   */
  public static final String DB_TABLE_UPDATE = "wattdepot-server.db.update";
  /**
   * Enable logging in the server. Logging may hide some stacktraces. Should be
   * True for production.
   */
  public static final String ENABLE_LOGGING_KEY = "wattdepot-server.enable.logging";
  /** Check the Session opens vs close. */
  public static final String CHECK_SESSIONS = "wattdepot-server.check.sessions";
  /** The logging level key. */
  public static final String LOGGING_LEVEL_KEY = "wattdepot-server.logging.level";
  /** Enable timing of Server operations. */
  public static final String SERVER_TIMING_KEY = "wattdepot-server.enable.timing";
  /** The WattDepot implementation class during testing. */
  public static final String TEST_WATT_DEPOT_IMPL_KEY = "wattdepot-server.test.wattdepot.impl";
  /** The wattdepot server port key during testing. */
  public static final String TEST_PORT_KEY = "wattdepot-server.test.port";
  /** Heroku key. */
  public static final String USE_HEROKU_KEY = "wattdepot-server.heroku";
  /** Heroku test key. */
  public static final String TEST_HEROKU_KEY = "wattdepot-server.test.heroku";
  /** The hostname for Heroku. */
  public static final String HEROKU_HOSTNAME_KEY = "wattdepot-server.heroku.hostname";
  /** The heroku database URL. */
  public static final String HEROKU_DATABASE_URL_KEY = "wattdepot-server.heroku.db.url";

  /** String for false. */
  public static final String FALSE = "false";
  /** String for true. */
  public static final String TRUE = "true";

  /** Where we store the properties. */
  private Properties properties;

  /**
   * Creates a new ServerProperties instance using the default filename. Prints
   * an error to the console if problems occur on loading.
   * @throws Exception if errors occur.
   */
  public ServerProperties() throws Exception {
    this(null);
  }

  /**
   * Creates a new ServerProperties instance loaded from the given filename.
   * Prints an error to the console if problems occur on loading.
   * 
   * @param serverSubdir The name of the subdirectory used to store all files
   *        for this server.
   * @throws Exception if errors occur.
   */
  public ServerProperties(String serverSubdir) throws Exception {
    initializeProperties(serverSubdir);
  }

  /**
   * Sets the properties to their "test" equivalents and updates the system
   * properties.
   */
  public void setTestProperties() {
    properties.setProperty(PORT_KEY, properties.getProperty(TEST_PORT_KEY));
    properties.setProperty(WATT_DEPOT_IMPL_KEY, properties.getProperty(TEST_WATT_DEPOT_IMPL_KEY));
    // turn off logging during testing.
    properties.setProperty(LOGGING_LEVEL_KEY, "SEVERE");
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
      if (key.contains("password") || key.contains("database.url")) {
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
   * @param key Should be one of the public static final strings in this class.
   * @return The value of the key, or null if not found.
   */
  public String get(String key) {
    return this.properties.getProperty(key);
  }

  /**
   * Sets the given property.
   * @param key the key.
   * @param value the value
   */
  public void set(String key, String value) {
    this.properties.setProperty(key, value);
  }
  
  /**
   * Ensures that the there is no leading or trailing whitespace in the property
   * values. The fact that we need to do this indicates a bug in Java's
   * Properties implementation to me.
   * 
   * @param properties The properties.
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
   * Reads in the properties in ~/.wattdepot3/server/wattdepot-server.properties
   * if this file exists, and provides default values for all properties not
   * mentioned in this file. Will also add any pre-existing System properties
   * that start with "wattdepot-server.".
   * 
   * @param serverSubdir The name of the subdirectory used to store all files
   *        for this server.
   * @throws Exception if errors occur.
   */
  private void initializeProperties(String serverSubdir) throws Exception {
    Logger logger = Logger.getLogger("org.wattdepot.properties");
    String userHome = UserHome.getHomeString();
    String wattDepot3Home = userHome + "/.wattdepot3/";
    String serverHome = null;
    if (serverSubdir == null) {
      serverHome = wattDepot3Home + "server";
    }
    else {
      serverHome = wattDepot3Home + serverSubdir;
    }
    String propFileName = serverHome + "/wattdepot-server.properties";
    this.properties = new Properties();
    // Use properties from file, if they exist.
    FileInputStream stream = null;
    try {
      stream = new FileInputStream(propFileName);
      properties.load(stream);
      logger.info("Loading Server properties from: " + propFileName);

    }
    catch (IOException e) {
      logger.info(propFileName + " not found. Using default server properties.");
    }
    finally {
      if (stream != null) {
        stream.close();
      }
    }
    processDatabaseURL(properties.getProperty(DATABASE_URL));
//    processDatabaseURL(properties.getProperty(DB_CONNECTION_URL));
    // grab all of the properties in the environment
    Map<String, String> systemProps = System.getenv();
    for (Map.Entry<String, String> prop : systemProps.entrySet()) {
      if (prop.getKey().startsWith(ADMIN_USER_NAME_ENV)) {
        properties.setProperty(ADMIN_USER_NAME, prop.getValue());
      }
      if (prop.getKey().startsWith(ADMIN_USER_PASSWORD_ENV)) {
        properties.setProperty(ADMIN_USER_PASSWORD, prop.getValue());
      }
      if (prop.getKey().startsWith(DB_CONNECTION_URL_ENV)) {
        processDatabaseURL(prop.getValue());
      }
      if (prop.getKey().startsWith("DATABASE_URL")) {
        processDatabaseURL(prop.getValue());
      }
    }
    if (!properties.containsKey(ADMIN_USER_NAME) || !properties.containsKey(ADMIN_USER_PASSWORD) || !properties.containsKey(DB_CONNECTION_URL)) {
      StringBuilder sb = new StringBuilder();
      if (!properties.containsKey(ADMIN_USER_NAME)) {
        sb.append("WattDepot Admin name not set. ");
      }
      if (!properties.containsKey(ADMIN_USER_PASSWORD)) {
        sb.append("WattDepot Admin password not set. ");
      }
      if (!(properties.containsKey(DB_CONNECTION_URL_ENV) || properties.containsKey("DATABASE_URL"))) {
        sb.append("WattDepot database url not set. ");
      }
      throw new SecurityException(sb.toString());
    }
    if (properties.containsKey(SSL) && properties.getProperty(SSL).equals(TRUE)) {
      StringBuilder sb = new StringBuilder();
      if (!properties.containsKey(SSL_KEYSTORE_PASSWORD)) {
        sb.append("Keystore password is not set. ");
      }
      if (!properties.containsKey(SSL_KEYSTORE_KEY_PASSWORD)) {
        sb.append("Keystore key password is not set. ");
      }
      if (sb.length() != 0) {
        throw new SecurityException(sb.toString());
      }
    }

    UserInfo.ROOT.setUid(properties.getProperty(ADMIN_USER_NAME));
    UserInfo.ROOT.setPassword(properties.getProperty(ADMIN_USER_PASSWORD));
    UserPassword.ROOT.setUid(properties.getProperty(ADMIN_USER_NAME));
    UserPassword.ROOT.setPassword(properties.getProperty(ADMIN_USER_PASSWORD));
    String defaultWattDepotImpl = "org.wattdepot.server.depository.impl.hibernate.WattDepotPersistenceImpl";
    // Set default values if not set
    if (!properties.containsKey(SERVER_HOME_DIR)) {
      properties.setProperty(SERVER_HOME_DIR, serverHome);
    }
    if (!properties.containsKey(WATT_DEPOT_IMPL_KEY)) {
      properties.setProperty(WATT_DEPOT_IMPL_KEY, defaultWattDepotImpl);
    }
    if (!properties.containsKey(HOSTNAME_KEY)) {
      properties.setProperty(HOSTNAME_KEY, "localhost");
    }
    if (!properties.containsKey(PORT_KEY)) {
      properties.setProperty(PORT_KEY, "8192");
    }
    if (!properties.containsKey(SSL)) {
      properties.setProperty(SSL, FALSE);
    }
    if (!properties.containsKey(SSL_KEYSTORE_PATH)) {
      properties.setProperty(SSL_KEYSTORE_PATH, serverHome + "/wattdepot.jks");
    }
    if (!properties.containsKey(SSL_KEYSTORE_TYPE)) {
      properties.setProperty(SSL_KEYSTORE_TYPE, "JKS");
    }
    if (!properties.containsKey(DB_CONNECTION_DRIVER)) {
      properties.setProperty(DB_CONNECTION_DRIVER, "org.postgresql.Driver");
    }
    if (!properties.containsKey(DB_CONNECTION_URL)) {
      properties.setProperty(DB_CONNECTION_URL, "jdbc:postgresql://localhost:5432/wattdepot");
    }
    if (!properties.containsKey(DB_SHOW_SQL)) {
      properties.setProperty(DB_SHOW_SQL, FALSE);
    }
    if (!properties.containsKey(DB_TABLE_UPDATE)) {
      properties.setProperty(DB_TABLE_UPDATE, "update");
    }
    if (!properties.containsKey(ENABLE_LOGGING_KEY)) {
      properties.setProperty(ENABLE_LOGGING_KEY, TRUE);
    }
    if (!properties.containsKey(CHECK_SESSIONS)) {
      properties.setProperty(CHECK_SESSIONS, FALSE);
    }
    if (!properties.containsKey(LOGGING_LEVEL_KEY)) {
      properties.setProperty(LOGGING_LEVEL_KEY, "INFO");
    }
    if (!properties.containsKey(CONTEXT_ROOT_KEY)) {
      properties.setProperty(CONTEXT_ROOT_KEY, "wattdepot");
    }
    if (!properties.containsKey(SERVER_TIMING_KEY)) {
      properties.setProperty(SERVER_TIMING_KEY, FALSE);
    }
    if (!properties.containsKey(TEST_PORT_KEY)) {
      properties.setProperty(TEST_PORT_KEY, "8194");
    }
    if (!properties.containsKey(TEST_WATT_DEPOT_IMPL_KEY)) {
      properties.setProperty(TEST_WATT_DEPOT_IMPL_KEY, defaultWattDepotImpl);
    }
    if (!properties.containsKey(USE_HEROKU_KEY)) {
      properties.setProperty(USE_HEROKU_KEY, FALSE);
    }
    if (!properties.containsKey(TEST_HEROKU_KEY)) {
      properties.setProperty(TEST_HEROKU_KEY, FALSE);
    }
    logger.finest(echoProperties());
    trimProperties(properties);
    logger.finest(echoProperties());

    // get PORT and DATABASE_URL for heroku
    String webPort = System.getenv("PORT");
    if (webPort != null && !webPort.isEmpty()) {
      properties.setProperty(PORT_KEY, webPort);
    }

    String databaseURL = System.getenv("DATABASE_URL");
    if (databaseURL != null && !databaseURL.isEmpty()) {
      URI dbUri = new URI(databaseURL);
      String username = dbUri.getUserInfo().split(":")[0];
      String password = dbUri.getUserInfo().split(":")[1];
      String dbUrl = "jdbc:postgresql://" + dbUri.getHost() +  dbUri.getPath();
      properties.setProperty(DB_USER_NAME, username);
      properties.setProperty(DB_PASSWORD, password);
      properties.setProperty(DB_CONNECTION_URL, dbUrl);
    }
//    logger.severe(echoProperties());
  }

  /**
   * Parses the given database url to set the database username, password, and connection url.
   * @param url the database url.
   * @throws java.net.URISyntaxException if there is a problem with the url.
   */
  private void processDatabaseURL(String url) throws URISyntaxException {
    if (url != null && !url.isEmpty()) {
      URI dbUri = new URI(url);
      if (dbUri.getUserInfo() != null && dbUri.getUserInfo().indexOf(":") != -1) {
        String username = dbUri.getUserInfo().split(":")[0];
        String password = dbUri.getUserInfo().split(":")[1];
        properties.setProperty(DB_USER_NAME, username);
        properties.setProperty(DB_PASSWORD, password);
      }
      String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ":" + dbUri.getPort() +  dbUri.getPath();
      properties.setProperty(DB_CONNECTION_URL, dbUrl);
    }

  }

  /**
   * Returns the fully qualified host name, such as
   * "http://localhost:9876/wattdepot/". Note, the String will end with "/", so
   * there is no need to append another if you are constructing a URI.
   * 
   * @return The fully qualified host name.
   */
  public String getFullHost() {
    // We have a special case for Heroku here, which leaves out the port number.
    // This is needed
    // because Heroku apps listen on a private port on localhost, but remote
    // connections into
    // the server always come on port 80. This causes problems because the port
    // number is used
    // in at least 3 places: by the Server to decide what port number to bind to
    // (on Heroku this
    // is the private port # given by the $PORT environment variable), the
    // announced URL of the
    // server to the public (always 80 on Heroku, though usually left out of URI
    // to default to
    // 80), and the URI of parent resources such as a Source in a SensorData
    // resource (should be
    // the public port on Heroku).
    if (properties.getProperty(USE_HEROKU_KEY).equals(TRUE)
        || properties.getProperty(TEST_HEROKU_KEY).equals(TRUE)) {
      return "http://" + get(HOSTNAME_KEY) + ":80/" + get(CONTEXT_ROOT_KEY) + "/";
    }
    else {
      return "http://" + get(HOSTNAME_KEY) + ":" + get(PORT_KEY) + "/" + get(CONTEXT_ROOT_KEY)
          + "/";
    }
  }

}
