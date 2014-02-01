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
package org.wattdepot.common.domainmodel;

import java.util.HashSet;
import java.util.Set;

import org.wattdepot.common.util.Slug;

/**
 * Sensor - Represents the device making measurements.
 * 
 * @author Cam Moore
 * 
 */
public class Sensor implements IDomainModel {
  /** A unique id for the Sensor usable in URLs. */
  private String id;
  /** The name of the Sensor. */
  private String name;
  /** The URI to the sensor. */
  private String uri;
  /** The id of the model of the sensor. */
  private String modelId;
  /** Additional properties of the sensor. */
  private Set<Property> properties;
  /** The owner of this sensor. */
  private String orgId;

  /**
   * Default constructor.
   */
  public Sensor() {
    this.properties = new HashSet<Property>();
  }

  /**
   * @param name The name.
   * @param uri The URI to the meter.
   * @param modelId The id of the meter's model.
   * @param ownerId the id of the owner of the sensor.
   */
  public Sensor(String name, String uri, String modelId, String ownerId) {
    this(Slug.slugify(name), name, uri, modelId, new HashSet<Property>(), ownerId);
  }

  /**
   * @param id The unique id.
   * @param name The name.
   * @param uri The URI to the meter.
   * @param modelId The id of the meter's model.
   * @param properties The properties associated with this Sensor.
   * @param ownerId the id of the owner of the sensor.
   */
  public Sensor(String id, String name, String uri, String modelId, Set<Property> properties,
      String ownerId) {
    this.id = id;
    this.name = name;
    this.uri = uri;
    this.modelId = modelId;
    this.properties = properties;
    this.orgId = ownerId;
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
    Sensor other = (Sensor) obj;
    if (id == null) {
      if (other.id != null) {
        return false;
      }
    }
    else if (!id.equals(other.id)) {
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
    if (modelId == null) {
      if (other.modelId != null) {
        return false;
      }
    }
    else if (!modelId.equals(other.modelId)) {
      return false;
    }
    if (orgId == null) {
      if (other.orgId != null) {
        return false;
      }
    }
    else if (!orgId.equals(other.orgId)) {
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
   * @return the modelId
   */
  public String getModelId() {
    return modelId;
  }

  /**
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * @return the id of the owner
   */
  public String getOrganizationId() {
    return orgId;
  }

  /**
   * @return the properties
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
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((modelId == null) ? 0 : modelId.hashCode());
    result = prime * result + ((orgId == null) ? 0 : orgId.hashCode());
    result = prime * result + ((properties == null) ? 0 : properties.hashCode());
    result = prime * result + ((uri == null) ? 0 : uri.hashCode());
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
    if (orgId != null
        && (orgId.equals(group.getId()) || group.equals(Organization.ADMIN_GROUP))) {
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
   * @param id the id to set
   */
  public void setId(String id) {
    this.id = id;
  }

  /**
   * @param model the id of the model to set
   */
  public void setModelId(String model) {
    this.modelId = model;
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
    this.orgId = ownerId;
  }

  /**
   * @param properties the properties to set
   */
  public void setProperties(Set<Property> properties) {
    this.properties = properties;
  }

  /**
   * @param uri the uri to set
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
    return "Sensor [id=" + id + ", name=" + name + ", uri=" + uri + ", modelId=" + modelId
        + ", properties=" + properties + ", organizationId=" + orgId + "]";
  }

}
