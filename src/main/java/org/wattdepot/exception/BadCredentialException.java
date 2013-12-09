/**
 * UniqueIdException.java This file is part of WattDepot.
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
public class BadCredentialException extends Exception {

  /** Serial Version ID. */
  private static final long serialVersionUID = -7949771009371995775L;

  /** Default Constructor. */
  public BadCredentialException() {
    super();
  }

  /**
   * @param message
   *          A String message about the exception.
   * @param cause
   *          The Throwable cause of the exception.
   */
  public BadCredentialException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * @param message
   *          A String message about the exception.
   */
  public BadCredentialException(String message) {
    super(message);
  }

  /**
   * @param cause
   *          The Throwable cause of the exception.
   */
  public BadCredentialException(Throwable cause) {
    super(cause);
  }

}
