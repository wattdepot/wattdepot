package org.wattdepot3.util.logger;

import java.io.File;
import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * Supports logging of informational and error messages by a service. Uses
 * WattDepotUserHome.getHome() to determine where to put the logs directory. Portions of this code
 * are adapted from http://hackystat-utilities.googlecode.com/
 * 
 * @author Philip Johnson
 * @author Robert Brewer
 */
public final class WattDepotLogger {

  /**
   * Create a new Logger for WattDepot services.
   * 
   * @param loggerName The name of the logger to create.
   * @param serverHome Home directory for this server. Logging files are placed in
   * [serverHome]/logs. If null, an IllegalArgumentException is thrown.
   * @param hasConsole If true, then a ConsoleHandler is created.
   * @throws IllegalArgumentException If serverHome is null.
   * @throws RuntimeException If there are problems creating directories or opening log file.
   */
  private WattDepotLogger(String loggerName, String serverHome, boolean hasConsole) {
    Logger logger = Logger.getLogger(loggerName);
    logger.setUseParentHandlers(false);

    // Define a file handler that writes to the logs directory, creating it if nec.
    if (serverHome == null) {
      throw new IllegalArgumentException("Attempt to create WattDepotLogger with null serverHome");
    }
    else {
      String logDirString = serverHome + "/logs/";
      System.out.println("logDirString: " + logDirString); // DEBUG
      File logDir = new File(logDirString);
      boolean dirsOk = logDir.mkdirs();
      if (!dirsOk && !logDir.exists()) {
        throw new RuntimeException("mkdirs() failed");
      }
      String fileName = logDir + "/" + loggerName + ".%u.log";
      FileHandler fileHandler;
      try {
        fileHandler = new FileHandler(fileName, 500000, 10, true);
        fileHandler.setFormatter(new OneLineFormatter());
        logger.addHandler(fileHandler);
      }
      catch (IOException e) {
        throw new RuntimeException("Could not open the log file for this WattDepot service.", e);
      }

      // Define a console handler to also write the message to the console.
      if (hasConsole) {
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(new OneLineFormatter());
        logger.addHandler(consoleHandler);
      }
      setLoggingLevel(logger, "INFO");
    }
  }

  /**
   * Return the WattDepot Logger named with loggerName, creating it if it does not yet exist.
   * WattDepot loggers have the following characteristics:
   * <ul>
   * <li>Log messages are one line and are prefixed with a time stamp using the OneLineFormatter
   * class.
   * <li>The logger creates a Console logger and a File logger.
   * <li>The File logger is written out to the [serverHome]/logs/ directory, creating this if it is
   * not found.
   * <li>The File log name is {name}.%u.log.
   * </ul>
   * 
   * @param loggerName The name of this WattDepotLogger.
   * @param serverHome Home directory for this server. Logging files are placed in
   * [serverHome]/logs. If null, an IllegalArgumentException is thrown.
   * @return The Logger instance.
   * @throws IllegalArgumentException If serverHome is null.
   * @throws RuntimeException If there are problems creating directories or opening log file.
   */
  public static Logger getLogger(String loggerName, String serverHome) {
    return getLogger(loggerName, serverHome, true);
  }

  /**
   * Return the WattDepot Logger named with loggerName, creating it if it does not yet exist.
   * WattDepot loggers have the following characteristics:
   * <ul>
   * <li>Log messages are one line and are prefixed with a time stamp using the OneLineFormatter
   * class.
   * <li>The logger creates a File logger.
   * <li>The File logger is written out to the [serverHome]/logs/ directory, creating this if it is
   * not found.
   * <li>The File log name is {name}.%u.log.
   * <li>There is also a ConsoleHandler (if hasConsole is true).
   * </ul>
   * 
   * @param loggerName The name of this WattDepotLogger.
   * @param serverHome Home directory for this server. Logging files are placed in
   * [serverHome]/logs. If null, an IllegalArgumentException is thrown.
   * @param hasConsole If true, then a ConsoleHandler is created.
   * @return The Logger instance.
   * @throws IllegalArgumentException If serverHome is null.
   * @throws RuntimeException If there are problems creating directories or opening log file.
   */
  public static Logger getLogger(String loggerName, String serverHome, boolean hasConsole) {
    Logger logger = LogManager.getLogManager().getLogger(loggerName);
    if (logger == null) {
      new WattDepotLogger(loggerName, serverHome, hasConsole);
    }
    return LogManager.getLogManager().getLogger(loggerName);
  }

  /**
   * Sets the logging level to be used for this WattDepot logger. If the passed string cannot be
   * parsed into a Level, then INFO is set by default.
   * 
   * @param logger The logger whose level is to be set.
   * @param level The new Level.
   */
  public static void setLoggingLevel(Logger logger, String level) {
    Level newLevel = Level.INFO;
    try {
      newLevel = Level.parse(level);
    }
    catch (Exception e) {
      logger.info("Couldn't set Logging level to: " + level);
    }
    logger.setLevel(newLevel);
    for (Handler handler : logger.getHandlers()) {
      handler.setLevel(newLevel);
    }
  }
}
