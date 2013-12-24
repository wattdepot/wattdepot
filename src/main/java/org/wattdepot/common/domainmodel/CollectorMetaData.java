/**
 * SensorProcess.java This file is part of WattDepot.
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
package org.wattdepot.common.domainmodel;

import java.util.HashSet;
import java.util.Set;

import org.wattdepot.common.util.Slug;

/**
 * SensorProcess - Represents a process that queries a Sensor and produces
 * measurements.
 * 
 * @author Cam Moore
 * 
 */
public class CollectorMetaData {
  /** A unique id for the CollectorMetaData. */
  private String id;
  /** The human readable name. */
  private String name;
  /** The sensor making the measurements. */
  protected Sensor sensor;
  /** The number of seconds between polls. */
  protected Long pollingInterval;
  /** The id of the depository where the measurements are stored. */
  protected String depositoryId;
  /** Additional properties for the SensorProcess. */
  protected Set<Property> properties;
  /** The owner of this sensor process. */
  private Organization owner;

  /**
   * Hide the default constructor.
   */
  protected CollectorMetaData() {

  }

  /**
   * @param name
   *          The name of the CollectorMetaData.
   * @param sensor
   *          The sensor that measures the environment.
   * @param poll
   *          The number of seconds between polls.
   * @param depositoryId
   *          The depository_id where measurements are stored.
   * @param owner
   *          the owner of the sensor process.
   */
  public CollectorMetaData(String name, Sensor sensor, Long poll, String depositoryId,
      Organization owner) {
    this.id = Slug.slugify(name);
    this.name = name;
    this.sensor = sensor;
    this.pollingInterval = poll;
    this.depositoryId = depositoryId;
    this.properties = new HashSet<Property>();
    this.owner = owner;
  }

  /**
   * @param e
   *          The Property to add.
   * @return true if added.
   * @see java.util.List#add(java.lang.Object)
   */
  public boolean addProperty(Property e) {
    return properties.add(e);
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    CollectorMetaData other = (CollectorMetaData) obj;
    if (depositoryId == null) {
      if (other.depositoryId != null) {
        return false;
      }
    }
    else if (!depositoryId.equals(other.depositoryId)) {
      return false;
    }
    if (id == null) {
      if (other.id != null) {
        return false;
      }
    }
    else if (!id.equals(other.id)) {
      return false;
    }
    if (owner == null) {
      if (other.owner != null) {
        return false;
      }
    }
    else if (!owner.equals(other.owner)) {
      return false;
    }
    if (pollingInterval == null) {
      if (other.pollingInterval != null) {
        return false;
      }
    }
    else if (!pollingInterval.equals(other.pollingInterval)) {
      return false;
    }
    if (properties == null) {
      if (other.properties != null) {
        return false;
      }
    }
    else if (!properties.equals(other.properties)) {
      return false;
    }
    if (sensor == null) {
      if (other.sensor != null) {
        return false;
      }
    }
    else if (!sensor.equals(other.sensor)) {
      return false;
    }
    return true;
  }

  /**
   * @return the depositoryId
   */
  public String getDepositoryId() {
    return depositoryId;
  }

  /**
   * @return the id
   */
  public String getId() {
    return id;
  }

  /**
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * @return the owner
   */
  public Organization getOwner() {
    return owner;
  }

  /**
   * @return the pollingInterval.
   */
  public Long getPollingInterval() {
    return pollingInterval;
  }

  /**
   * @return the properties.
   */
  public Set<Property> getProperties() {
    return properties;
  }

  /**
   * @param key
   *          The key.
   * @return The value of associated with the key.
   */
  public Property getProperty(String key) {
    for (Property p : properties) {
      if (p.getKey().equals(key)) {
        return p;
      }
    }
    return null;
  }

  /**
   * @return the sensor
   */
  public Sensor getSensor() {
    return sensor;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((depositoryId == null) ? 0 : depositoryId.hashCode());
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    result = prime * result + ((owner == null) ? 0 : owner.hashCode());
    result = prime * result + ((pollingInterval == null) ? 0 : pollingInterval.hashCode());
    result = prime * result + ((properties == null) ? 0 : properties.hashCode());
    result = prime * result + ((sensor == null) ? 0 : sensor.hashCode());
    return result;
  }

  /**
   * Determines if the given group is the owner of this location.
   * 
   * @param group
   *          the UserGroup to check.
   * @return True if the group owns the Location or the group is the
   *         ADMIN_GROUP.
   */
  public boolean isOwner(Organization group) {
    if (owner != null && (owner.equals(group) || group.equals(Organization.ADMIN_GROUP))) {
      return true;
    }
    return false;
  }

  /**
   * @param o
   *          The Property to remove.
   * @return true if removed.
   * @see java.util.List#remove(java.lang.Object)
   */
  public boolean removeProperty(Object o) {
    return properties.remove(o);
  }

  /**
   * @param depositoryId
   *          the depositoryId to set
   */
  public void setDepositoryId(String depositoryId) {
    this.depositoryId = depositoryId;
  }

  /**
   * @param id
   *          the id to set
   */
  public void setId(String id) {
    this.id = id;
  }

  /**
   * @param name
   *          the name to set
   */
  public void setName(String name) {
    this.name = name;
    if (this.id == null) {
      this.id = Slug.slugify(name);
    }
  }

  /**
   * @param owner
   *          the owner to set
   */
  public void setOwner(Organization owner) {
    this.owner = owner;
  }

  /**
   * @param pollingInterval
   *          the pollingInterval to set
   */
  public void setPollingInterval(Long pollingInterval) {
    this.pollingInterval = pollingInterval;
  }

  /**
   * @param properties
   *          the properties to set
   */
  public void setProperties(Set<Property> properties) {
    this.properties = properties;
  }

  /**
   * @param sensor
   *          the sensor to set
   */
  public void setSensor(Sensor sensor) {
    this.sensor = sensor;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "CollectorMetaData [name=" + name + ", sensor=" + sensor + ", pollingInterval="
        + pollingInterval + ", depositoryId=" + depositoryId + ", properties=" + properties
        + ", owner=" + owner + "]";
  }

}
