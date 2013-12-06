/**
 * MeasurementGapException.java  This file is part of WattDepot 3.
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
package org.wattdepot3.exception;

/**
 * An exception that is thrown when a repository the gap between two measurements is too large.
 * 
 * @author Cam Moore
 */
public class MeasurementGapException extends Exception {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  /**
   * 
   */
  public MeasurementGapException() {

  }

  /**
   * @param message A String the message about the Exception.
   */
  public MeasurementGapException(String message) {
    super(message);
  }

  /**
   * @param cause The Throwable cause of the Exception.
   */
  public MeasurementGapException(Throwable cause) {
    super(cause);
  }

  /**
   * @param message A String message about the exception.
   * @param cause The Throwable cause of the exception.
   */
  public MeasurementGapException(String message, Throwable cause) {
    super(message, cause);
  }

}
