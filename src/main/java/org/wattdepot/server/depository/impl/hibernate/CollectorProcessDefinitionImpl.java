/**
 * CollectorProcessDefinitionImpl.java This file is part of WattDepot.
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

import org.wattdepot.common.domainmodel.CollectorProcessDefinition;
import org.wattdepot.common.domainmodel.Property;
import org.wattdepot.common.exception.BadSlugException;

/**
 * CollectorProcessDefinitionImpl - Hibernate implementation of
 * CollectorProcessDefinition. Supports Long references to the ids.
 * 
 * @author Cam Moore
 * 
 */
@SuppressWarnings("PMD.UselessOverridingMethod")
public class CollectorProcessDefinitionImpl extends CollectorProcessDefinition {

  /** Database primary key. */
  private Long pk;
  /** Foreign key of the sensor. */
  private Long sensorFk;
  /** Foreign key of the depository. */
  private Long depositoryFk;
  /** Foreign key of the owner. */
  private Long ownerFk;

  /**
   * Default constructor.
   */
  public CollectorProcessDefinitionImpl() {

  }

  /**
   * @param slug
   *          The unique slug for the CollectorProcessDefinitionImpl.
   * @param name
   *          The name of the CollectorProcessDefinitionImpl.
   * @param sensorId
   *          The id of the sensor that measures the environment.
   * @param poll
   *          The number of seconds between polls.
   * @param depositoryId
   *          The depository_id where measurements are stored.
   * @param properties
   *          The properties associated with this CollectorProcessDefinitionImpl.
   * @param ownerId
   *          the id of the owner of the collector.
   */
  public CollectorProcessDefinitionImpl(String slug, String name, String sensorId,
      Long poll, String depositoryId, Set<Property> properties, String ownerId) {
    super(slug, name, sensorId, poll, depositoryId, properties, ownerId);
  }

  /**
   * Converter constructor.
   * 
   * @param cpd
   *          the CollectorProcessDefinition to clone.
   */
  public CollectorProcessDefinitionImpl(CollectorProcessDefinition cpd) {
    super(cpd.getName(), cpd.getSensorId(), cpd.getPollingInterval(), cpd.getDepositoryId(), cpd
        .getOwnerId());
  }

  /**
   * @param name
   *          The name of the CollectorProcessDefinitionData.
   * @param sensorId
   *          The id of the sensor that measures the environment.
   * @param poll
   *          The number of seconds between polls.
   * @param depositoryId
   *          The depository_id where measurements are stored.
   * @param ownerId
   *          the id of the owner of the collector.
   */
  public CollectorProcessDefinitionImpl(String name, String sensorId, Long poll,
      String depositoryId, String ownerId) {
    super(name, sensorId, poll, depositoryId, ownerId);
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
    if (obj.getClass().equals(CollectorProcessDefinition.class)) {
      return super.equals(obj);
    }
    if (!super.equals(obj)) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    CollectorProcessDefinitionImpl other = (CollectorProcessDefinitionImpl) obj;
    if (depositoryFk == null) {
      if (other.depositoryFk != null) {
        return false;
      }
    }
    else if (!depositoryFk.equals(other.depositoryFk)) {
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
    if (sensorFk == null) {
      if (other.sensorFk != null) {
        return false;
      }
    }
    else if (!sensorFk.equals(other.sensorFk)) {
      return false;
    }
    return true;
  }

  /**
   * @return the depositoryFk
   */
  public Long getDepositoryFk() {
    return depositoryFk;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.common.domainmodel.CollectorProcessDefinition#getDepositoryId
   * ()
   */
  @Override
  public String getDepositoryId() {
    // TODO Auto-generated method stub
    return super.getDepositoryId();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.common.domainmodel.CollectorProcessDefinition#getName()
   */
  @Override
  public String getName() {
    // TODO Auto-generated method stub
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
   * @see
   * org.wattdepot.common.domainmodel.CollectorProcessDefinition#getOwnerId()
   */
  @Override
  public String getOwnerId() {
    // TODO Auto-generated method stub
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
   * @see
   * org.wattdepot.common.domainmodel.CollectorProcessDefinition#getPollingInterval
   * ()
   */
  @Override
  public Long getPollingInterval() {
    // TODO Auto-generated method stub
    return super.getPollingInterval();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.common.domainmodel.CollectorProcessDefinition#getProperties()
   */
  @Override
  public Set<Property> getProperties() {
    // TODO Auto-generated method stub
    return super.getProperties();
  }

  /**
   * @return the sensorFk
   */
  public Long getSensorFk() {
    return sensorFk;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.common.domainmodel.CollectorProcessDefinition#getSensorId()
   */
  @Override
  public String getSensorId() {
    // TODO Auto-generated method stub
    return super.getSensorId();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.common.domainmodel.CollectorProcessDefinition#getSlug()
   */
  @Override
  public String getId() {
    // TODO Auto-generated method stub
    return super.getId();
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
    result = prime * result + ((depositoryFk == null) ? 0 : depositoryFk.hashCode());
    result = prime * result + ((ownerFk == null) ? 0 : ownerFk.hashCode());
    result = prime * result + ((pk == null) ? 0 : pk.hashCode());
    result = prime * result + ((sensorFk == null) ? 0 : sensorFk.hashCode());
    return result;
  }

  /**
   * @param depositoryFk
   *          the depositoryFk to set
   */
  public void setDepositoryFk(Long depositoryFk) {
    this.depositoryFk = depositoryFk;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.common.domainmodel.CollectorProcessDefinition#setDepositoryId
   * (java.lang.String)
   */
  @Override
  public void setDepositoryId(String depositoryId) {
    // TODO Auto-generated method stub
    super.setDepositoryId(depositoryId);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.common.domainmodel.CollectorProcessDefinition#setName(java
   * .lang.String)
   */
  @Override
  public void setName(String name) {
    // TODO Auto-generated method stub
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
   * @see
   * org.wattdepot.common.domainmodel.CollectorProcessDefinition#setOwnerId(
   * java.lang.String)
   */
  @Override
  public void setOwnerId(String ownerId) {
    // TODO Auto-generated method stub
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
   * @see
   * org.wattdepot.common.domainmodel.CollectorProcessDefinition#setPollingInterval
   * (java.lang.Long)
   */
  @Override
  public void setPollingInterval(Long pollingInterval) {
    // TODO Auto-generated method stub
    super.setPollingInterval(pollingInterval);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.common.domainmodel.CollectorProcessDefinition#setProperties
   * (java.util.Set)
   */
  @Override
  public void setProperties(Set<Property> properties) {
    // TODO Auto-generated method stub
    super.setProperties(properties);
  }

  /**
   * @param sensorFk
   *          the sensorFk to set
   */
  public void setSensorFk(Long sensorFk) {
    this.sensorFk = sensorFk;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.common.domainmodel.CollectorProcessDefinition#setSensorId
   * (java.lang.String)
   */
  @Override
  public void setSensorId(String sensorId) {
    // TODO Auto-generated method stub
    super.setSensorId(sensorId);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.common.domainmodel.CollectorProcessDefinition#setSlug(java
   * .lang.String)
   */
  @Override
  public void setId(String slug) throws BadSlugException {
    // TODO Auto-generated method stub
    super.setId(slug);
  }

}
