/*
 * This file is part of WattDepot.
 *
 *  Copyright (C) 2015  Cam Moore
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.wattdepot.common.exception;

/**
 * Exception raised when WattDepot notices a sensor's counter rolls over. This is only raise if the sensor cannot generate power.
 *
 * @author Cam Moore
 */
public class CounterRollOverException extends Exception {

  /**
   * Default constructor.
   */
  public CounterRollOverException() {
    super();
  }

  /**
   * @param message
   *          A String message about the exception.
   * @param cause
   *          The Throwable cause of the exception.
   */
  public CounterRollOverException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * @param message
   *          A String message about the exception.
   */
  public CounterRollOverException(String message) {
    super(message);
  }

  /**
   * @param cause
   *          The Throwable cause of the exception.
   */
  public CounterRollOverException(Throwable cause) {
    super(cause);
  }
}
