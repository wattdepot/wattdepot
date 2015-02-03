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

import org.wattdepot.common.domainmodel.Depository;
import org.wattdepot.common.domainmodel.Organization;
import org.wattdepot.common.domainmodel.Sensor;

import java.util.HashMap;

/**
 * In memory cache of Organizations, Depositories, and Sensors. Intended to speed up WattDepotPersistence. It is a lazy cache.
 * @author Cam Moore
 */
public class PersistenceCache {
  private HashMap<String, Organization> orgMap;
  private HashMap<String, HashMap<String, Depository>> orgDepoMap;
  private HashMap<String, HashMap<String, Sensor>> orgSensorMap;

  /** Creates a new PersistenceCache. */
  public PersistenceCache() {
    this.orgMap = new HashMap<String, Organization>();
    this.orgDepoMap = new HashMap<String, HashMap<String, Depository>>();
    this.orgSensorMap = new HashMap<String, HashMap<String, Sensor>>();
  }

  /**
   * Gets the Organization for the given orgId.
   * @param orgId the organizations id.
   * @return The Organization for the given id or null if the id is not in the cache.
   */
  public Organization getOrganization(String orgId) {
    return orgMap.get(orgId);
  }

  /**
   * Stores the Organization in the cache.
   * @param org The organization to store.
   * @return The old Organization in the cache or null if the organization is new to the cache.
   */
  public Organization putOrganization(Organization org) {
    return orgMap.put(org.getId(), org);
  }

  /**
   * Deletes the organization from the cache.
   * @param org The organization to delete.
   * @return The old organization from the cache or null if the organization wasn't in the cache.
   */
  public Organization deleteOrganization(Organization org) {
    return orgMap.remove(org.getId());
  }

  /**
   * Gets the Depository defined by the depId and orgId.
   * @param depId The depository id.
   * @param orgId The organization id.
   * @return The Depository for the given ids, or null if it is undefined.
   */
  public Depository getDepository(String depId, String orgId) {
    HashMap<String, Depository> depMap = orgDepoMap.get(orgId);
    if (depMap != null) {
      return depMap.get(depId);
    }
    return null;
  }

  /**
   * Stores the Depository in the cache.
   * @param dep The Depository to store.
   * @return The old Depository in the cache or null if the depository wasn't in the cache.
   */
  public Depository putDepository(Depository dep) {
    HashMap<String, Depository> depMap = orgDepoMap.get(dep.getOrganizationId());
    if (depMap == null) {
      depMap = new HashMap<String, Depository>();
      orgDepoMap.put(dep.getOrganizationId(), depMap);
    }
    return depMap.put(dep.getId(), dep);
  }

  /**
   * Deletes the depository from the cache.
   * @param depository The Depository to delete.
   * @return The old Depository or null if the depository wasn't in the cache.
   */
  public Depository deleteDepository(Depository depository) {
    HashMap<String, Depository> depMap = orgDepoMap.get(depository.getOrganizationId());
    if (depMap == null) {
      return null;
    }
    return depMap.remove(depository.getId());
  }

  /**
   * Gets the Sensor defined by the sensorId and orgId.
   * @param sensorId The sensor id.
   * @param orgId the organizations id.
   * @return The Sensor from the cache or null if the sensor isn't defined.
   */
  public Sensor getSensor(String sensorId, String orgId) {
    HashMap<String, Sensor> sensorMap = orgSensorMap.get(orgId);
    if (sensorMap != null) {
      return sensorMap.get(sensorId);
    }
    return null;
  }

  /**
   * Stores the sensor in the cache.
   * @param sensor The Sensor to store.
   * @return The old Sensor from the cache or null if the Sensor wasn't in the cache.
   */
  public Sensor putSensor(Sensor sensor) {
    HashMap<String, Sensor> sensorMap = orgSensorMap.get(sensor.getOrganizationId());
    if (sensorMap == null) {
      sensorMap = new HashMap<String, Sensor>();
      orgSensorMap.put(sensor.getOrganizationId(), sensorMap);
    }
    return sensorMap.put(sensor.getId(), sensor);
  }

  /**
   * Deletes the Sensor from the cache.
   * @param sensor The Sensor to delete.
   * @return The old sensor from the cache or null if the sensor wasn't in the cache.
   */
  public Sensor deleteSensor(Sensor sensor) {
    HashMap<String, Sensor> sensorMap = orgSensorMap.get(sensor.getOrganizationId());
    if (sensorMap == null) {
      return null;
    }
    return sensorMap.remove(sensor.getId());
  }
}
