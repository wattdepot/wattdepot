/**
 * BadSlugException.java This file is part of WattDepot.
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
package org.wattdepot.common.exception;

/**
 * BadSlugException is thrown when the slug of an instance is not a valid slug,
 * only contains lowercase letter, numbers and '-'. No other characters.
 * 
 * @author Cam Moore
 * 
 */
public class BadSlugException extends Exception {

  /** serial version UID. */
  private static final long serialVersionUID = 4574446966521917423L;

  /** Default Constructor. */
  public BadSlugException() {
    super();
  }

  /**
   * @param message
   *          A String message about the exception.
   * @param cause
   *          The Throwable cause of the exception.
   */
  public BadSlugException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * @param message
   *          A String message about the exception.
   */
  public BadSlugException(String message) {
    super(message);
  }

  /**
   * @param cause
   *          The Throwable cause of the exception.
   */
  public BadSlugException(Throwable cause) {
    super(cause);
  }

}
