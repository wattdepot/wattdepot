/**
 * SensorList.java This file is part of WattDepot 3.
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
package org.wattdepot.datamodel;

import java.util.ArrayList;

/**
 * SensorList - Attempt at getting a list across the wire.
 *
 * @author Cam Moore
 *
 */
public class SensorList {
  private ArrayList<Sensor> sensors;
  
  /**
   * Default Constructor.
   */
  public SensorList() {
    sensors = new ArrayList<Sensor>();
  }

  /**
   * @return the groups
   */
  public ArrayList<Sensor> getSensors() {
    return sensors;
  }

  /**
   * @param groups the groups to set
   */
  public void setSensors(ArrayList<Sensor> groups) {
    this.sensors = groups;
  }

}
