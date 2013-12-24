/**
 * SensorGroup.java This file is part of WattDepot.
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

import java.util.Set;

import org.wattdepot.common.exception.BadSlugException;
import org.wattdepot.common.util.Slug;

/**
 * SensorGroup represents a group of Sensors. Used for aggregating sensor
 * measurements.
 * 
 * @author Cam Moore
 * 
 */
public class SensorGroup implements IDomainModel {
  /** The unique slug for this group usable in URLs. */
  private String slug;
  /** The name of the group. */
  private String name;
  /** The List of sensors the compose this group. */
  protected Set<Sensor> sensors;
  /** The owner of this sensor model. */
  private String ownerId;

  /**
   * Hide the default constructor.
   */
  protected SensorGroup() {

  }

  /**
   * Create a new SensorGroup with the given unique id.
   * 
   * @param name
   *          The name.
   * @param sensors
   *          The set of sensors in the group.
   * @param ownerId
   *          the id of the owner of the SensorGroup.
   */
  public SensorGroup(String name, Set<Sensor> sensors, String ownerId) {
    this.slug = Slug.slugify(name);
    this.name = name;
    this.sensors = sensors;
    this.ownerId = ownerId;
  }

  /**
   * @param e
   *          The sensor to add.
   * @return true if successful.
   * @see java.util.List#add(java.lang.Object)
   */
  public boolean add(Sensor e) {
    return sensors.add(e);
  }

  /**
   * @param o
   *          The Sensor to test.
   * @return true if the Sensor is in this group.
   * @see java.util.List#contains(java.lang.Object)
   */
  public boolean contains(Object o) {
    return sensors.contains(o);
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
    SensorGroup other = (SensorGroup) obj;
    if (slug == null) {
      if (other.slug != null) {
        return false;
      }
    }
    else if (!slug.equals(other.slug)) {
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
    if (sensors == null) {
      if (other.sensors != null) {
        return false;
      }
    }
    else if (!sensors.equals(other.sensors)) {
      return false;
    }
    return true;
  }

  /**
   * @return the id
   */
  public String getSlug() {
    return slug;
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
  public String getOwnerId() {
    return ownerId;
  }

  /**
   * @return the sensors
   */
  public Set<Sensor> getSensors() {
    return sensors;
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
    result = prime * result + ((slug == null) ? 0 : slug.hashCode());
    result = prime * result + ((ownerId == null) ? 0 : ownerId.hashCode());
    result = prime * result + ((sensors == null) ? 0 : sensors.hashCode());
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
    if (ownerId != null
        && (ownerId.equals(group) || group.equals(Organization.ADMIN_GROUP))) {
      return true;
    }
    return false;
  }

  /**
   * @param o
   *          The Sensor to remove.
   * @return true if successful.
   * @see java.util.List#remove(java.lang.Object)
   */
  public boolean remove(Object o) {
    return sensors.remove(o);
  }

  /**
   * @param slug
   *          the slug to set
   * @exception BadSlugException
   *              if the slug isn't valid.
   */
  public void setSlug(String slug) throws BadSlugException {
    if (Slug.validateSlug(slug)) {
      this.slug = slug;
    }
    else {
      throw new BadSlugException(slug + " is not a valid slug.");
    }
  }

  /**
   * @param name
   *          the name to set
   */
  public void setName(String name) {
    this.name = name;
    if (this.slug == null) {
      this.slug = Slug.slugify(name);
    }
  }

  /**
   * @param owner
   *          the id of the owner to set
   */
  public void setOwnerId(String owner) {
    this.ownerId = owner;
  }

  /**
   * @param sensors
   *          the sensors to set
   */
  public void setSensors(Set<Sensor> sensors) {
    this.sensors = sensors;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "SensorGroup [id=" + slug + ", sensors=" + sensors + ", owner="
        + ownerId + "]";
  }

}
