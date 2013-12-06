/**
 * UniqueIdException.java This file is part of WattDepot 3.
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
package org.wattdepot.exception;

/**
 * UniqueIdException thrown when an object is defined with the same unique id as
 * another object of the same type.
 * 
 * @author Cam Moore
 * 
 */
public class BadSensorUriException extends Exception {

  /** serial version UID. */
  private static final long serialVersionUID = 4574446966521917423L;

  /** Default Constructor. */
  public BadSensorUriException() {
    super();
  }

  /**
   * @param message
   *          A String message about the exception.
   * @param cause
   *          The Throwable cause of the exception.
   */
  public BadSensorUriException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * @param message
   *          A String message about the exception.
   */
  public BadSensorUriException(String message) {
    super(message);
  }

  /**
   * @param cause
   *          The Throwable cause of the exception.
   */
  public BadSensorUriException(Throwable cause) {
    super(cause);
  }

}
