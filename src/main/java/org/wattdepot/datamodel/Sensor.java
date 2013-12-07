/**
 * Sensor.java This file is part of WattDepot.
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

import java.util.HashSet;
import java.util.Set;

import org.wattdepot.util.Slug;

/**
 * Sensor - Represents the device making measurements.
 * 
 * @author Cam Moore
 * 
 */
public class Sensor {
  /** A unique id for the Sensor. */
  private String id;
  /** The name of the Sensor. */
  private String name;
  /** The URI to the sensor. */
  private String uri;
  /** The location of the sensor. */
  private SensorLocation sensorLocation;
  /** The model of the sensor. */
  private SensorModel model;
  /** Additional properties of the sensor. */
  private Set<Property> properties;
  /** The owner of this sensor. */
  private UserGroup owner;

  /**
   * Default constructor.
   */
  public Sensor() {
    this.properties = new HashSet<Property>();
  }

  /**
   * @param name
   *          The name.
   * @param uri
   *          The URI to the meter.
   * @param sensorLocation
   *          The meter's location.
   * @param model
   *          The meter's model.
   * @param owner
   *          the owner of the sensor.
   */
  public Sensor(String name, String uri, SensorLocation sensorLocation, SensorModel model, UserGroup owner) {
    this.id = Slug.slugify(name);
    this.name = name;
    this.uri = uri;
    this.sensorLocation = sensorLocation;
    this.model = model;
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
    Sensor other = (Sensor) obj;
    if (id == null) {
      if (other.id != null) {
        return false;
      }
    }
    else if (!id.equals(other.id)) {
      return false;
    }
    if (sensorLocation == null) {
      if (other.sensorLocation != null) {
        return false;
      }
    }
    else if (!sensorLocation.equals(other.sensorLocation)) {
      return false;
    }
    if (model == null) {
      if (other.model != null) {
        return false;
      }
    }
    else if (!model.equals(other.model)) {
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
    if (properties == null) {
      if (other.properties != null) {
        return false;
      }
    }
    else if (!properties.equals(other.properties)) {
      return false;
    }
    if (uri == null) {
      if (other.uri != null) {
        return false;
      }
    }
    else if (!uri.equals(other.uri)) {
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
   * @return the location
   */
  public SensorLocation getLocation() {
    return sensorLocation;
  }

  /**
   * @return the model
   */
  public SensorModel getModel() {
    return model;
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
   * @return the properties
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
   * @return the sensorLocation
   */
  public SensorLocation getSensorLocation() {
    return sensorLocation;
  }

  /**
   * @return the uri
   */
  public String getUri() {
    return uri;
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
    result = prime * result + ((sensorLocation == null) ? 0 : sensorLocation.hashCode());
    result = prime * result + ((model == null) ? 0 : model.hashCode());
    result = prime * result + ((owner == null) ? 0 : owner.hashCode());
    result = prime * result + ((properties == null) ? 0 : properties.hashCode());
    result = prime * result + ((uri == null) ? 0 : uri.hashCode());
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
   *          The Property to remove.
   * @return true if removed.
   * @see java.util.List#remove(java.lang.Object)
   */
  public boolean removeProperty(Object o) {
    return properties.remove(o);
  }

  /**
   * @param id
   *          the id to set
   */
  public void setId(String id) {
    this.id = id;
  }

  /**
   * @param sensorLocation
   *          the location to set
   */
  public void setLocation(SensorLocation sensorLocation) {
    this.sensorLocation = sensorLocation;
  }

  /**
   * @param model
   *          the model to set
   */
  public void setModel(SensorModel model) {
    this.model = model;
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
   * @param properties the properties to set
   */
  public void setProperties(Set<Property> properties) {
    this.properties = properties;
  }

  /**
   * @param sensorLocation the sensorLocation to set
   */
  public void setSensorLocation(SensorLocation sensorLocation) {
    this.sensorLocation = sensorLocation;
  }

  /**
   * @param uri
   *          the uri to set
   */
  public void setUri(String uri) {
    this.uri = uri;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "Sensor [id=" + getId() + ", name=" + name + ", uri=" + uri + ", location=" + sensorLocation + ", model=" + model
        + ", properties=" + properties + "]";
  }

}
