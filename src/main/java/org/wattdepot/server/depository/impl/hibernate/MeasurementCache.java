/*
 * This file is part of WattDepot.
 *
 *  Copyright (C) 2014  Cam Moore
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

package org.wattdepot.server.depository.impl.hibernate;

import org.wattdepot.common.domainmodel.InterpolatedValue;

import java.util.HashMap;

/**
 * Caches the earliest and latest measurements organized by Organization, Depository and Sensor.
 * Created by Cam Mmoore on 12/3/14.
 * @author Cam Moore
 */
public class MeasurementCache {
  // OrgId, DepositoryId, SensorId, MeasurementChachePair
  private HashMap<String, HashMap<String, HashMap<String, MeasurementCachePair>>> cache;

  /** Default constructor.*/
  public MeasurementCache() {
    this.cache = new HashMap<String, HashMap<String, HashMap<String, MeasurementCachePair>>>();
  }

  /**
   * Gets the earliest measurement date for the given sensor.
   * @param orgId The organization id.
   * @param depotId The depository id.
   * @param sensorId The sensor id.
   * @return The earliest measurement as an InterpolatedValue or null if there aren't any measurements.
   */
  public InterpolatedValue getEarliest(String orgId, String depotId, String sensorId) {
    HashMap<String, HashMap<String, MeasurementCachePair>> orgMap = cache.get(orgId);
    if (orgMap == null) {
      return null;
    }
    HashMap<String, MeasurementCachePair> depotMap = orgMap.get(depotId);
    if (depotMap == null) {
      return null;
    }
    MeasurementCachePair sensorInfo = depotMap.get(sensorId);
    if (sensorInfo == null) {
      return null;
    }
    else {
      return sensorInfo.getEarliest();
    }
  }

  /**
   * Gets the date of the latest measurement for the given sensor.
   * @param orgId The organization id.
   * @param depotId The depository id.
   * @param sensorId The sensor id.
   * @return The latest measurement as an InterpolatedValue or null if there aren't any measurements.
   */
  public InterpolatedValue getLatest(String orgId, String depotId, String sensorId) {
    HashMap<String, HashMap<String, MeasurementCachePair>> orgMap = cache.get(orgId);
    if (orgMap == null) {
      return null;
    }
    HashMap<String, MeasurementCachePair> depotMap = orgMap.get(depotId);
    if (depotMap == null) {
      return null;
    }
    MeasurementCachePair sensorInfo = depotMap.get(sensorId);
    if (sensorInfo == null) {
      return null;
    }
    else {
      return sensorInfo.getLatest();
    }
  }

  /**
   * Updates the Sensor's information with the given InterpolatedValue.
   * @param orgId The organization id.
   * @param depotId The depository id.
   * @param sensorId The sensor id.
   * @param iv The interpolated value for the measurement.
   */
  public void update(String orgId, String depotId, String sensorId, InterpolatedValue iv) {
    HashMap<String, HashMap<String, MeasurementCachePair>> orgMap = cache.get(orgId);
    if (orgMap == null) {
      orgMap = new HashMap<String, HashMap<String, MeasurementCachePair>>();
      cache.put(orgId, orgMap);
    }
    HashMap<String, MeasurementCachePair> depotMap = orgMap.get(depotId);
    if (depotMap == null) {
      depotMap = new HashMap<String, MeasurementCachePair>();
      orgMap.put(depotId, depotMap);
    }
    MeasurementCachePair sensorInfo = depotMap.get(sensorId);
    if (sensorInfo == null) {
      sensorInfo = new MeasurementCachePair(iv);
      depotMap.put(sensorId, sensorInfo);
    }
    else {
      sensorInfo.update(iv);
    }
  }
}
