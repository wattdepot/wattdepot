/**
 * SensorImpl.java This file is part of WattDepot.
 *
 * Copyright (C) 2014  Cam Moore
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
package org.wattdepot.server.depository.impl.hibernate;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.wattdepot.common.domainmodel.Property;
import org.wattdepot.common.domainmodel.Sensor;

/**
 * SensorImpl - Hibernate implementation of Sensor class. Has primary key and
 * foreign key relationships.
 * 
 * @author Cam Moore
 * 
 */
@Entity
@Table(name = "SENSORS")
public class SensorImpl {

  /** The database primary key. */
  @Id
  @GeneratedValue
  private Long pk;
  /** A unique id for the Sensor usable in URLs. */
  private String id;
  /** The name of the Sensor. */
  private String name;
  /** The URI to the sensor. */
  private String uri;
  /** The model of the sensor. */
  @ManyToOne
  private SensorModelImpl model;
  /** Additional properties of the sensor. */
  @OneToMany
  private Set<PropertyImpl> properties;
  /** This sensor's organization. */
  @ManyToOne
  private OrganizationImpl org;

  /**
   * 
   */
  public SensorImpl() {
    super();
  }

  /**
   * @param id the sensor's id.
   * @param name the sensor's name.
   * @param uri the sensor's uri.
   * @param model the sensor model.
   * @param properties the sensor's properties.
   * @param org the sensor's organization.
   */
  public SensorImpl(String id, String name, String uri, SensorModelImpl model,
      Set<PropertyImpl> properties, OrganizationImpl org) {
    this.id = id;
    this.name = name;
    this.uri = uri;
    this.model = model;
    this.properties = properties;
    this.org = org;
  }

  /**
   * @return the pk
   */
   public Long getPk() {
    return pk;
  }

  /**
   * @param pk the pk to set
   */
  @SuppressWarnings("unused")
  private void setPk(Long pk) {
    this.pk = pk;
  }

  /**
   * @return the id
   */
  public String getId() {
    return id;
  }

  /**
   * @param id the id to set
   */
  public void setId(String id) {
    this.id = id;
  }

  /**
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * @param name the name to set
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * @return the uri
   */
  public String getUri() {
    return uri;
  }

  /**
   * @param uri the uri to set
   */
  public void setUri(String uri) {
    this.uri = uri;
  }

  /**
   * @return the model
   */
  public SensorModelImpl getModel() {
    return model;
  }

  /**
   * @param model the model to set
   */
  public void setModel(SensorModelImpl model) {
    this.model = model;
  }

  /**
   * @return the properties
   */
  public Set<PropertyImpl> getProperties() {
    return properties;
  }

  /**
   * @param properties the properties to set
   */
  public void setProperties(Set<PropertyImpl> properties) {
    this.properties = properties;
  }

  /**
   * @return the org
   */
  public OrganizationImpl getOrg() {
    return org;
  }

  /**
   * @param org the org to set
   */
  public void setOrg(OrganizationImpl org) {
    this.org = org;
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
    result = prime * result + ((model == null) ? 0 : model.hashCode());
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((org == null) ? 0 : org.hashCode());
    result = prime * result + ((pk == null) ? 0 : pk.hashCode());
    result = prime * result + ((properties == null) ? 0 : properties.hashCode());
    result = prime * result + ((uri == null) ? 0 : uri.hashCode());
    return result;
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
    SensorImpl other = (SensorImpl) obj;
    if (id == null) {
      if (other.id != null) {
        return false;
      }
    }
    else if (!id.equals(other.id)) {
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
    if (name == null) {
      if (other.name != null) {
        return false;
      }
    }
    else if (!name.equals(other.name)) {
      return false;
    }
    if (org == null) {
      if (other.org != null) {
        return false;
      }
    }
    else if (!org.equals(other.org)) {
      return false;
    }
    if (pk == null) {
      if (other.pk != null) {
        return false;
      }
    }
    else if (!pk.equals(other.pk)) {
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
   * @return the equivalent Sensor to this.
   */
  public Sensor toSensor() {
    Set<Property> prop = new HashSet<Property>();
    for (PropertyImpl i : properties) {
      prop.add(new Property(i.getKey(), i.getValue()));
    }
    return new Sensor(id, name, uri, model.getId(), prop, org.getId());
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "SensorImpl [pk=" + pk + ", id=" + id + ", name=" + name + ", uri=" + uri + ", model="
        + model + ", properties=" + properties + ", org=" + org + "]";
  }

}
