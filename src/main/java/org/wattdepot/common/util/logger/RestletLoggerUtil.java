package org.wattdepot.common.util.logger;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Provides a convenience method for Restlet logging that adjusts the output Handlers. Portions of
 * this code are adapted from http://hackystat-utilities.googlecode.com/
 * 
 * @author Philip Johnson
 * @author Robert Brewer
 */
public final class RestletLoggerUtil {

  /** Make this class non-instantiable. */
  private RestletLoggerUtil() {
    // Do nothing.
  }

  /**
   * Forces all Loggers to use the Console for logging output.
   */
  public static void useConsoleHandler() {
    LogManager logManager = LogManager.getLogManager();
    for (Enumeration<String> en = logManager.getLoggerNames(); en.hasMoreElements();) {
      String logName = en.nextElement();
      System.out.println("reseting " + logName);
      Logger logger = logManager.getLogger(logName);
      // remove the old handlers
      Handler[] handlers = logger.getHandlers();
      for (Handler handler : handlers) {
        logger.removeHandler(handler);
      }
      ConsoleHandler handler = new ConsoleHandler();
      logger.addHandler(handler);
    }
  }
  
  /**
   * Adjusts the Restlet Loggers so that they send their output to a file, not the console.
   * 
   * @param serverHome Home directory for this server. Logging files are placed in
   * [serverHome]/logs. If null, an IllegalArgumentException is thrown.
   * @throws IllegalArgumentException If serverHome is null.
   * @throws RuntimeException If there are problems creating directories or opening log file.
   */
  public static void useFileHandler(String serverHome) {
    LogManager logManager = LogManager.getLogManager();
    // System.out.println("In useFileHandler");
    for (Enumeration<String> en = logManager.getLoggerNames(); en.hasMoreElements();) {
      String logName = en.nextElement();
      if ((logName.startsWith("com.noelios") || logName.startsWith("org.restlet") || "global"
          .equals(logName))
          && (logManager.getLogger(logName) != null)) {
        // First, get rid of current Handlers
        Logger logger = logManager.getLogger(logName);
        logger.setFilter(new HTTPClientHelperFilter());
//        System.out.println("logger is: " + logger + " name = " + logName);
        logger = logger.getParent();
//        System.out.println("parent logger is: " + logger);
        Handler[] handlers = logger.getHandlers();
        for (Handler handler : handlers) {
          logger.removeHandler(handler);
        }
        // System.out.println("Removed handlers.");
        // Define a handler that writes to the ~/.wattdepot3/<service>/logs directory
        // Define a file handler that writes to the logs directory, creating it if nec.
        if (serverHome == null) {
          throw new IllegalArgumentException("Attempt to change Restlet logging to null filename");
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
            System.out.println("Could not open log file: " + fileName + " " + e.getMessage());
          }
        }
      }
    }
  }

  /**
   * Adjusts the Restlet Loggers so that they dump their output rather than sending it to the
   * console.
   */
  public static void removeRestletLoggers() {
    LogManager logManager = LogManager.getLogManager();
    for (Enumeration<String> en = logManager.getLoggerNames(); en.hasMoreElements();) {
      String logName = en.nextElement();
      if ((logName.startsWith("com.noelios") || logName.startsWith("org.restlet") || "global"
          .equals(logName) || logName.startsWith("org.hibernate"))
          && (logManager.getLogger(logName) != null)) {
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
   * Utility for printing out the known/defined logger names.
   */
  public static void showLoggers() {
    LogManager logManager = LogManager.getLogManager();
    for (Enumeration<String> en = logManager.getLoggerNames(); en.hasMoreElements();) {
      String logName = en.nextElement();
      Logger logger = logManager.getLogger(logName);
      Logger parent = logger.getParent();
      System.out.print("logger name = '" + logName + "'");
      if (parent != null) {
        System.out.print(" parent = '" + parent.getName() + "'");
      }
      System.out.println();
    }
  }
}
