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

import java.util.Set;

import org.wattdepot.common.domainmodel.Property;
import org.wattdepot.common.domainmodel.Sensor;

/**
 * SensorImpl - Hibernate implementation of Sensor class. Has primary key and
 * foreign key relationships.
 * 
 * @author Cam Moore
 * 
 */
@SuppressWarnings("PMD.UselessOverridingMethod")
public class SensorImpl extends Sensor {

  /** The database primary key. */
  private Long pk;
  /** Foreign key of the SensorLocation. */
  private Long locationFk;
  /** foreign key of the SensorModel. */
  private Long modelFk;
  /** Foreign key of the owner. */
  private Long ownerFk;

  /**
   * 
   */
  public SensorImpl() {
    super();
  }

  /**
   * @param name
   *          the name of the Sensor.
   * @param uri
   *          the URI for getting measurements.
   * @param locationId
   *          the SensorLocation slug.
   * @param modelId
   *          the SensorModel slug.
   * @param ownerId
   *          the id of the owner.
   */
  public SensorImpl(String name, String uri, String locationId, String modelId,
      String ownerId) {
    super(name, uri, locationId, modelId, ownerId);
  }

  /**
   * @param slug
   *          The unique slug for the SensorImpl.
   * @param name
   *          the name of the Sensor.
   * @param uri
   *          the URI for getting measurements.
   * @param locationId
   *          the SensorLocation slug.
   * @param modelId
   *          the SensorModel slug.
   * @param properties
   *          the properties associated with this SensorImpl.
   * @param ownerId
   *          the id of the owner.
   */
  public SensorImpl(String slug, String name, String uri, String locationId,
      String modelId, Set<Property> properties, String ownerId) {
    super(slug, name, uri, locationId, modelId, properties, ownerId);
  }

  /**
   * @return the locationFk
   */
  public Long getLocationFk() {
    return locationFk;
  }

  /**
   * @return the modelFk
   */
  public Long getModelFk() {
    return modelFk;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.common.domainmodel.Sensor#getModelId()
   */
  @Override
  public String getModelId() {
    return super.getModelId();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.common.domainmodel.Sensor#getName()
   */
  @Override
  public String getName() {
    return super.getName();
  }

  /**
   * @return the ownerFk
   */
  public Long getOwnerFk() {
    return ownerFk;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.common.domainmodel.Sensor#getOwnerId()
   */
  @Override
  public String getOwnerId() {
    return super.getOwnerId();
  }

  /**
   * @return the pk
   */
  public Long getPk() {
    return pk;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.common.domainmodel.Sensor#getProperties()
   */
  @Override
  public Set<Property> getProperties() {
    return super.getProperties();
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result
        + ((locationFk == null) ? 0 : locationFk.hashCode());
    result = prime * result + ((modelFk == null) ? 0 : modelFk.hashCode());
    result = prime * result + ((ownerFk == null) ? 0 : ownerFk.hashCode());
    result = prime * result + ((pk == null) ? 0 : pk.hashCode());
    return result;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (this == obj) {
      return true;
    }
    if (obj.getClass().equals(Sensor.class)) {
      return super.equals(obj);
    }
    if (!super.equals(obj)) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    SensorImpl other = (SensorImpl) obj;
    if (locationFk == null) {
      if (other.locationFk != null) {
        return false;
      }
    }
    else if (!locationFk.equals(other.locationFk)) {
      return false;
    }
    if (modelFk == null) {
      if (other.modelFk != null) {
        return false;
      }
    }
    else if (!modelFk.equals(other.modelFk)) {
      return false;
    }
    if (ownerFk == null) {
      if (other.ownerFk != null) {
        return false;
      }
    }
    else if (!ownerFk.equals(other.ownerFk)) {
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
    return true;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.common.domainmodel.Sensor#getSensorLocationId()
   */
  @Override
  public String getSensorLocationId() {
    return super.getSensorLocationId();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.common.domainmodel.Sensor#getSlug()
   */
  @Override
  public String getSlug() {
    return super.getSlug();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.common.domainmodel.Sensor#getUri()
   */
  @Override
  public String getUri() {
    return super.getUri();
  }

  /**
   * @param locationFk
   *          the locationFk to set
   */
  public void setLocationFk(Long locationFk) {
    this.locationFk = locationFk;
  }

  /**
   * @param modelFk
   *          the modelFk to set
   */
  public void setModelFk(Long modelFk) {
    this.modelFk = modelFk;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.common.domainmodel.Sensor#setModelId(java.lang.String)
   */
  @Override
  public void setModelId(String model) {
    super.setModelId(model);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.common.domainmodel.Sensor#setName(java.lang.String)
   */
  @Override
  public void setName(String name) {
    super.setName(name);
  }

  /**
   * @param ownerFk
   *          the ownerFk to set
   */
  public void setOwnerFk(Long ownerFk) {
    this.ownerFk = ownerFk;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.common.domainmodel.Sensor#setOwnerId(java.lang.String)
   */
  @Override
  public void setOwnerId(String ownerId) {
    super.setOwnerId(ownerId);
  }

  /**
   * @param pk
   *          the pk to set
   */
  public void setPk(Long pk) {
    this.pk = pk;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.common.domainmodel.Sensor#setProperties(java.util.Set)
   */
  @Override
  public void setProperties(Set<Property> properties) {
    super.setProperties(properties);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.common.domainmodel.Sensor#setSensorLocationId(java.lang.String
   * )
   */
  @Override
  public void setSensorLocationId(String sensorLocation) {
    super.setSensorLocationId(sensorLocation);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.common.domainmodel.Sensor#setSlug(java.lang.String)
   */
  @Override
  public void setSlug(String id) {
    super.setSlug(id);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.common.domainmodel.Sensor#setUri(java.lang.String)
   */
  @Override
  public void setUri(String uri) {
    super.setUri(uri);
  }

}
