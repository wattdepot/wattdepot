/**
 * WattDepotLoggerUtil.java This file is part of WattDepot.
 *
 * Copyright (C) 2014  Cam Moore
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
package org.wattdepot.common.util.logger;

import java.util.Enumeration;
import java.util.logging.Handler;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * WattDepotLoggerUtil logger utilities for WattDepot.
 * 
 * @author Cam Moore
 * 
 */
public class WattDepotLoggerUtil {

  /**
   * Removes the log handlers for org.restlet and org.apache.
   */
  public static void removeClientLoggerHandlers() {
    // Create the logger if doesn't exist.
    Logger.getLogger("org.restlet");
    Logger.getLogger("org.apache");
    LogManager logManager = LogManager.getLogManager();
    for (Enumeration<String> en = logManager.getLoggerNames(); en.hasMoreElements();) {
      String logName = en.nextElement();
      // if ((logName.startsWith("org.apache") ||
      // logName.startsWith("org.restlet"))
      // && (logManager.getLogger(logName) != null)) {
      if (logManager.getLogger(logName) != null) {
        Logger logger = logManager.getLogger(logName);
        logger = logger.getParent();
        if (logger != null) {
          Handler[] handlers = logger.getHandlers();
          for (Handler handler : handlers) {
            logger.removeHandler(handler);
          }
        }
      }
    }
  }
}
