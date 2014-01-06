/**
 * SensorGroupImpl.java This file is part of WattDepot.
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

import org.wattdepot.common.domainmodel.SensorGroup;
import org.wattdepot.common.exception.BadSlugException;

/**
 * SensorGroupImpl - Hibernate implementation of the SensorGroup. Includes pk
 * and foreign keys.
 * 
 * @author Cam Moore
 * 
 */
@SuppressWarnings("PMD.UselessOverridingMethod")
public class SensorGroupImpl extends SensorGroup {

  /** The database primary key. */
  private Long pk;
  /** Foreign key of the owner. */
  private Long ownerFk;

  /**
   * 
   */
  public SensorGroupImpl() {
    super();
  }

  /**
   * @param name
   *          the name of the Sensor Group.
   * @param sensors
   *          the Sensor ids of the members of the Sensor Group.
   * @param ownerId
   *          the Organziation id that owns the Sensor Group.
   */
  public SensorGroupImpl(String name, Set<String> sensors, String ownerId) {
    super(name, sensors, ownerId);
  }

  /**
   * @param slug
   *          The unique slug for the SensorGroupImpl.
   * @param name
   *          the name of the Sensor Group.
   * @param sensors
   *          the Sensor ids of the members of the Sensor Group.
   * @param ownerId
   *          the Organziation id that owns the Sensor Group.
   */
  public SensorGroupImpl(String slug, String name, Set<String> sensors,
      String ownerId) {
    super(slug, name, sensors, ownerId);
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
    if (obj.getClass().equals(SensorGroup.class)) {
      return super.equals(obj);
    }
    if (!super.equals(obj)) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    SensorGroupImpl other = (SensorGroupImpl) obj;
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
   * @see org.wattdepot.common.domainmodel.SensorGroup#getName()
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
   * @see org.wattdepot.common.domainmodel.SensorGroup#getOwnerId()
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
   * @see org.wattdepot.common.domainmodel.SensorGroup#getSensors()
   */
  @Override
  public Set<String> getSensors() {
    return super.getSensors();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.common.domainmodel.SensorGroup#getSlug()
   */
  @Override
  public String getSlug() {
    return super.getSlug();
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
    result = prime * result + ((pk == null) ? 0 : pk.hashCode());
    result = prime * result + ((ownerFk == null) ? 0 : ownerFk.hashCode());
    return result;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.common.domainmodel.SensorGroup#setName(java.lang.String)
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
   * @see
   * org.wattdepot.common.domainmodel.SensorGroup#setOwnerId(java.lang.String)
   */
  @Override
  public void setOwnerId(String owner) {
    super.setOwnerId(owner);
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
   * @see org.wattdepot.common.domainmodel.SensorGroup#setSensors(java.util.Set)
   */
  @Override
  public void setSensors(Set<String> sensors) {
    super.setSensors(sensors);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.common.domainmodel.SensorGroup#setSlug(java.lang.String)
   */
  @Override
  public void setSlug(String slug) throws BadSlugException {
    super.setSlug(slug);
  }

}
