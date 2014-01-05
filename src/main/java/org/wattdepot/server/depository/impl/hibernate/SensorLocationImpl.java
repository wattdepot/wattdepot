/**
 * SensorLocationImpl.java This file is part of WattDepot.
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

import org.wattdepot.common.domainmodel.SensorLocation;
import org.wattdepot.common.exception.BadSlugException;

/**
 * SensorLocationImpl - Hibernate implementation of the SensorLocation class.
 * Added Long primary key.
 * 
 * @author Cam Moore
 * 
 */
@SuppressWarnings("PMD.UselessOverridingMethod")
public class SensorLocationImpl extends SensorLocation {

  /** Database primary key. */
  private Long pk;
  /** Foreign key of the owner. */
  private Long ownerFk;

  /**
   * Default constructor.
   */
  public SensorLocationImpl() {
    super();
  }

  /**
   * @param name
   *          The name of the SensorLocation.
   * @param latitude
   *          Its decimal Latitude.
   * @param longitude
   *          Its decimal Longitude.
   * @param altitude
   *          Its altitude in meters.
   * @param description
   *          A description of the location.
   * @param ownerId
   *          the id of the Owner.
   */
  public SensorLocationImpl(String name, Double latitude, Double longitude, Double altitude,
      String description, String ownerId) {
    super(name, latitude, longitude, altitude, description, ownerId);
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
    if (obj.getClass().equals(SensorLocation.class)) {
      return super.equals(obj);
    }
    if (!super.equals(obj)) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    SensorLocationImpl other = (SensorLocationImpl) obj;
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
   * @see org.wattdepot.common.domainmodel.SensorLocation#getAltitude()
   */
  @Override
  public Double getAltitude() {
    return super.getAltitude();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.common.domainmodel.SensorLocation#getDescription()
   */
  @Override
  public String getDescription() {
    return super.getDescription();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.common.domainmodel.SensorLocation#getLatitude()
   */
  @Override
  public Double getLatitude() {
    return super.getLatitude();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.common.domainmodel.SensorLocation#getLongitude()
   */
  @Override
  public Double getLongitude() {
    return super.getLongitude();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.common.domainmodel.SensorLocation#getName()
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
   * @see org.wattdepot.common.domainmodel.SensorLocation#getOwnerId()
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
   * @see org.wattdepot.common.domainmodel.SensorLocation#getSlug()
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
   * @see
   * org.wattdepot.common.domainmodel.SensorLocation#setAltitude(java.lang.Double
   * )
   */
  @Override
  public void setAltitude(Double altitude) {
    super.setAltitude(altitude);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.common.domainmodel.SensorLocation#setDescription(java.lang
   * .String)
   */
  @Override
  public void setDescription(String description) {
    super.setDescription(description);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.common.domainmodel.SensorLocation#setLatitude(java.lang.Double
   * )
   */
  @Override
  public void setLatitude(Double latitude) {
    super.setLatitude(latitude);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.common.domainmodel.SensorLocation#setLongitude(java.lang.
   * Double)
   */
  @Override
  public void setLongitude(Double longitude) {
    super.setLongitude(longitude);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.common.domainmodel.SensorLocation#setName(java.lang.String)
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
   * org.wattdepot.common.domainmodel.SensorLocation#setOwnerId(java.lang.String
   * )
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
   * @see
   * org.wattdepot.common.domainmodel.SensorLocation#setSlug(java.lang.String)
   */
  @Override
  public void setSlug(String slug) throws BadSlugException {
    super.setSlug(slug);
  }

}
