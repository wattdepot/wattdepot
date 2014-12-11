package org.wattdepot.common.util.logger;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Provides a convenience method for Restlet logging that adjusts the output
 * Handlers. Portions of this code are adapted from
 * http://hackystat-utilities.googlecode.com/
 * 
 * @author Philip Johnson
 * @author Robert Brewer
 */
public final class LoggerUtil {

  /** Make this class non-instantiable. */
  private LoggerUtil() {
    // Do nothing.
  }

  /**
   * Forces all Loggers to use the Console for logging output.
   */
  public static void useConsoleHandler() {
    LogManager logManager = LogManager.getLogManager();
    for (Enumeration<String> en = logManager.getLoggerNames(); en
        .hasMoreElements();) {
      String logName = en.nextElement();
      Logger logger = logManager.getLogger(logName);
      if (logger != null) {
        // remove the old handlers
        Handler[] handlers = logger.getHandlers();
        for (Handler handler : handlers) {
          logger.removeHandler(handler);
        }
      }
    }
    Logger logger = logManager.getLogger("");
    ConsoleHandler handler = new ConsoleHandler();
    logger.addHandler(handler);
  }

  /**
   * Removes all the handlers from all the defined loggers. This should
   */
  public static void disableLogging() {
    Logger base = LogManager.getLogManager().getLogger(
        Logger.GLOBAL_LOGGER_NAME);
    base = base.getParent();
    base.setLevel(Level.SEVERE);
  }

  /**
   * Adjusts the Restlet Loggers so that they send their output to a file, not
   * the console.
   * 
   * @param serverHome Home directory for this server. Logging files are placed
   *        in [serverHome]/logs. If null, an IllegalArgumentException is
   *        thrown.
   * @throws IllegalArgumentException If serverHome is null.
   * @throws RuntimeException If there are problems creating directories or
   *         opening log file.
   */
  public static void useFileHandler(String serverHome) {
    LogManager logManager = LogManager.getLogManager();
    // System.out.println("In useFileHandler");
    for (Enumeration<String> en = logManager.getLoggerNames(); en
        .hasMoreElements();) {
      String logName = en.nextElement();
      if ((logName.startsWith("com.noelios")
          || logName.startsWith("org.restlet") || "global".equals(logName))
          && logManager.getLogger(logName) != null) {
        // First, get rid of current Handlers
        Logger logger = logManager.getLogger(logName);
        logger.setFilter(new HTTPClientHelperFilter());
        // System.out.println("logger is: " + logger + " name = " + logName);
        logger = logger.getParent();
        // System.out.println("parent logger is: " + logger);
        Handler[] handlers = logger.getHandlers();
        for (Handler handler : handlers) {
          logger.removeHandler(handler);
        }
        // System.out.println("Removed handlers.");
        // Define a handler that writes to the ~/.wattdepot3/<service>/logs
        // directory
        // Define a file handler that writes to the logs directory, creating it
        // if nec.
        if (serverHome == null) {
          throw new IllegalArgumentException(
              "Attempt to change Restlet logging to null filename");
        }
        else {
          File logDir = new File(serverHome + "/logs/");
          boolean dirsOk = logDir.mkdirs();
          if (!dirsOk && !logDir.exists()) {
            throw new RuntimeException("mkdirs() failed");
          }
          // System.out.println("Made this directory: " + logDir);
          String fileName = logDir + "/" + logName + ".%u.log";
          FileHandler fileHandler;
          try {
            fileHandler = new FileHandler(fileName, 500000, 10, true);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);
          }
          catch (IOException e) {
            // throw new RuntimeException
            // ("Could not open the log file for this WattDepot service.", e);
            System.out.println("Could not open log file: " + fileName + " "
                + e.getMessage());
          }
        }
      }
    }
  }

  /**
   * Adjusts the Restlet Loggers so that they dump their output rather than
   * sending it to the console.
   */
  public static void removeRestletLoggers() {
    LogManager logManager = LogManager.getLogManager();
    for (Enumeration<String> en = logManager.getLoggerNames(); en
        .hasMoreElements();) {
      String logName = en.nextElement();
      if ((logName.startsWith("com.noelios")
          || logName.startsWith("org.restlet") || "global".equals(logName) || logName
            .startsWith("org.hibernate"))
          && logManager.getLogger(logName) != null) {
        Logger logger = logManager.getLogger(logName);
        logger = logger.getParent();
        Handler[] handlers = logger.getHandlers();
        for (Handler handler : handlers) {
          logger.removeHandler(handler);
        }
      }
    }
  }

  /**
   * Sets the logging level to be used for this WattDepot logger. If the passed
   * string cannot be parsed into a Level, then INFO is set by default.
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

  /**
   * Utility for printing out the known/defined logger names.
   */
  public static void showLoggers() {
    LogManager logManager = LogManager.getLogManager();
    for (Enumeration<String> en = logManager.getLoggerNames(); en
        .hasMoreElements();) {
      String logName = en.nextElement();
      Logger logger = logManager.getLogger(logName);
      Logger parent = logger.getParent();
      System.out.print("logger name = '" + logName + "'");
      System.out.print(" level = '" + logger.getLevel() + "'");
      if (parent != null) {
        System.out.print(" parent = '" + parent.getName() + "'");
      }
      System.out.println();
    }
  }
}
