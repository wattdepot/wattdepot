/**
 * CollectorProcessDefinition.java This file is part of WattDepot.
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

import org.wattdepot.common.exception.BadSlugException;
import org.wattdepot.common.util.Slug;

/**
 * CollectorProcessDefinition - Represents a process that queries a Sensor and
 * produces measurements.
 * 
 * @author Cam Moore
 * 
 */
public class CollectorProcessDefinition implements IDomainModel {
  /** A unique id for the CollectorProcessDefinition. */
  private String id;
  /** The human readable name. */
  private String name;
  /** The id of the sensor making the measurements. */
  protected String sensorId;
  /** The number of seconds between polls. */
  protected Long pollingInterval;
  /** The id of the depository where the measurements are stored. */
  protected String depositoryId;
  /** Additional properties for the Collector. */
  protected Set<Property> properties;
  /** The id of the owner of this collector. */
  private String ownerId;

  /**
   * Hide the default constructor.
   */
  protected CollectorProcessDefinition() {

  }

  /**
   * @param name The name of the CollectorProcessDefinition.
   * @param sensorId The id of the sensor that measures the environment.
   * @param poll The number of seconds between polls.
   * @param depositoryId The depository_id where measurements are stored.
   * @param ownerId the id of the owner of the collector.
   */
  public CollectorProcessDefinition(String name, String sensorId, Long poll, String depositoryId,
      String ownerId) {
    this(Slug.slugify(name), name, sensorId, poll, depositoryId, new HashSet<Property>(), ownerId);
  }

  /**
   * @param id The unique id for the CollectorProcessDefinition.
   * @param name The name of the CollectorProcessDefinition.
   * @param sensorId The id of the sensor that measures the environment.
   * @param poll The number of seconds between polls.
   * @param depositoryId The depository_id where measurements are stored.
   * @param properties The properties associated with this
   *        CollectorProcessDefinition.
   * @param ownerId the id of the owner of the collector.
   */
  public CollectorProcessDefinition(String id, String name, String sensorId, Long poll,
      String depositoryId, Set<Property> properties, String ownerId) {
    this.id = id;
    this.name = name;
    this.sensorId = sensorId;
    this.pollingInterval = poll;
    this.depositoryId = depositoryId;
    this.properties = properties;
    this.ownerId = ownerId;
  }

  /**
   * @param e The Property to add.
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
    if (!getClass().isAssignableFrom(obj.getClass())
        && !obj.getClass().isAssignableFrom(getClass()) && getClass() != obj.getClass()) {
      return false;
    }
    if (obj.getClass().equals(Object.class)) {
      return false;
    }
    CollectorProcessDefinition other = (CollectorProcessDefinition) obj;
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
    if (ownerId == null) {
      if (other.ownerId != null) {
        return false;
      }
    }
    else if (!ownerId.equals(other.ownerId)) {
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
    if (sensorId == null) {
      if (other.sensorId != null) {
        return false;
      }
    }
    else if (!sensorId.equals(other.sensorId)) {
      return false;
    }
    if (name == null) {
      if (other.name != null) {
        return false;
      }
    }
    else if (!name.equals(other.name)) {
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
   * @return the slug
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
  public String getOrganizationId() {
    return ownerId;
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
   * @param key The key.
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
  public String getSensorId() {
    return sensorId;
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
    result = prime * result + ((ownerId == null) ? 0 : ownerId.hashCode());
    result = prime * result + ((pollingInterval == null) ? 0 : pollingInterval.hashCode());
    result = prime * result + ((properties == null) ? 0 : properties.hashCode());
    result = prime * result + ((sensorId == null) ? 0 : sensorId.hashCode());
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    return result;
  }

  /**
   * Determines if the given group is the owner of this location.
   * 
   * @param group the UserGroup to check.
   * @return True if the group owns the Location or the group is the
   *         ADMIN_GROUP.
   */
  public boolean isOwner(Organization group) {
    if (ownerId != null
        && (ownerId.equals(group.getId()) || group.equals(Organization.ADMIN_GROUP))) {
      return true;
    }
    return false;
  }

  /**
   * @param o The Property to remove.
   * @return true if removed.
   * @see java.util.List#remove(java.lang.Object)
   */
  public boolean removeProperty(Object o) {
    return properties.remove(o);
  }

  /**
   * @param depositoryId the depositoryId to set
   */
  public void setDepositoryId(String depositoryId) {
    this.depositoryId = depositoryId;
  }

  /**
   * @param slug the id to set
   * @exception BadSlugException if the slug is not a valid slug.
   */
  public void setId(String slug) throws BadSlugException {
    if (Slug.validateSlug(slug)) {
      this.id = slug;
    }
    else {
      throw new BadSlugException(slug + " is not a valid slug.");
    }
  }

  /**
   * @param name the name to set
   */
  public void setName(String name) {
    this.name = name;
    if (this.id == null) {
      this.id = Slug.slugify(name);
    }
  }

  /**
   * @param ownerId the id of the owner to set
   */
  public void setOrganizationId(String ownerId) {
    this.ownerId = ownerId;
  }

  /**
   * @param pollingInterval the pollingInterval to set
   */
  public void setPollingInterval(Long pollingInterval) {
    this.pollingInterval = pollingInterval;
  }

  /**
   * @param properties the properties to set
   */
  public void setProperties(Set<Property> properties) {
    this.properties = properties;
  }

  /**
   * @param sensorId the id of the sensor to set
   */
  public void setSensorId(String sensorId) {
    this.sensorId = sensorId;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "CollectorProcessDefinition [id=" + id + ", name=" + name + ", sensorId=" + sensorId
        + ", pollingInterval=" + pollingInterval + ", depositoryId=" + depositoryId
        + ", properties=" + properties + ", ownerId=" + ownerId + "]";
  }

}
