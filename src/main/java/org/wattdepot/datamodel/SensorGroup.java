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
package org.wattdepot.datamodel;

import java.util.Set;

import org.wattdepot.util.Slug;

/**
 * SensorGroup represents a group of Sensors. Used for aggregating sensor
 * measurements.
 * 
 * @author Cam Moore
 * 
 */
public class SensorGroup {
  /** The unique id for this group. */
  private String id;
  /** The name of the group. */
  private String name;
  /** The List of sensors the compose this group. */
  protected Set<Sensor> sensors;
  /** The owner of this sensor model. */
  private UserGroup owner;

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
   * @param owner
   *          the owner of the location.
   */
  public SensorGroup(String name, Set<Sensor> sensors, UserGroup owner) {
    this.id = Slug.slugify(name);
    this.name = name;
    this.sensors = sensors;
    this.owner = owner;
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
  public UserGroup getOwner() {
    return owner;
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
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    result = prime * result + ((owner == null) ? 0 : owner.hashCode());
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
  public boolean isOwner(UserGroup group) {
    if (owner != null) {
      if (owner.equals(group) || group.equals(UserGroup.ADMIN_GROUP)) {
        return true;
      }
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
   * @param id
   *          the id to set
   */
  public void setId(String id) {
    this.id = id;
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
   * @param owner
   *          the owner to set
   */
  public void setOwner(UserGroup owner) {
    this.owner = owner;
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
    return "SensorGroup [id=" + id + ", sensors=" + sensors + ", owner=" + owner + "]";
  }

}
