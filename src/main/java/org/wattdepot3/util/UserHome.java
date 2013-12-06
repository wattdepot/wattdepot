/**
 * UserHome.java This file is part of WattDepot 3.
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
package org.wattdepot3.util;

import java.io.File;

/**
 * UserHome - Provides a utility that returns the desired parent directory where
 * the ".wattdepot3" directory will be created for storage of WattDepot
 * preferences, logs, database, etc. This defaults to the user.home System
 * Property, but can be overridden by the user by providing the property
 * wattdepot3.user.home.
 * 
 * @author Cam Moore
 * 
 */
public final class UserHome {

  /** Name of property used to store the preferred location for user home directory. */
  public static final String USER_HOME_PROPERTY = "wattdepot3.user.home";

  /**
   * Hide the default constructor.
   */
  private UserHome() {
    
  }

  /**
   * Return a File instance representing the desired location of the .wattdepot3 directory. Note that
   * this directory may or may not exist.
   * 
   * @return A File instance representing the desired user.home directory.
   */
  public static File getHome() {
    return new File(getHomeString());
  }

  /**
   * Return a String representing the desired location of the .wattdepot3 directory. Note that this
   * directory may or may not exist.
   * 
   * @return A String instance representing the desired user.home directory.
   */
  public static String getHomeString() {
    return System.getProperty(USER_HOME_PROPERTY, System.getProperty("user.home"));
  }  
}
