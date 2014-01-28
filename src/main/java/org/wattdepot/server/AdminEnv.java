/**
 * AdminEnv.java This file is part of WattDepot.
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
package org.wattdepot.server;

import org.wattdepot.common.domainmodel.UserInfo;
import org.wattdepot.common.domainmodel.UserPassword;

/**
 * AdminEnv tests to see if the UserInfo.ADMIN gets their uid and password from
 * the environment variables.
 * 
 * @author Cam Moore
 * 
 */
public class AdminEnv {

  /**
   * 
   */
  public AdminEnv() {
    // TODO Auto-generated constructor stub
  }

  /**
   * @param args command line arguments, they are ignored.
   */
  public static void main(String[] args) {
    System.out.println(UserInfo.ROOT);
    System.out.println(UserPassword.ROOT);

  }

}
