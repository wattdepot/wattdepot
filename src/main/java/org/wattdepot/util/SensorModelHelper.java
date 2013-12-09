/**
 * SensorModelHelper.java This file is part of WattDepot.
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
package org.wattdepot.util;

import java.util.HashMap;
import java.util.Map;

import org.wattdepot.datamodel.SensorModel;

/**
 * SensorModelHelper - Utility class that Holds the default SensorModel instances.
 *
 * @author Cam Moore
 *
 */
public class SensorModelHelper {
  /** The eGauge Sensor Model name. */
  public static final String EGAUGE = "eGauge";
  /** The Shark Sensor Model name. */
  public static final String SHARK = "Shark";
  /** The Modbus protocol name. */
  public static final String MODBUS = "Modbus";
  /** Holds the default SensorModels. */
  public static final Map<String, SensorModel> models = new HashMap<String, SensorModel>();
  
  static {
    models.put(EGAUGE, new SensorModel(EGAUGE, "xml", EGAUGE, "1.0"));
    models.put(SHARK, new SensorModel(SHARK, MODBUS, SHARK, "1.03"));
  }
}
