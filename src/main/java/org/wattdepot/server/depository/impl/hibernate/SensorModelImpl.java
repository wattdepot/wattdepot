/**
 * SensorModelImpl.java This file is part of WattDepot.
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

import org.wattdepot.common.domainmodel.SensorModel;
import org.wattdepot.common.exception.BadSlugException;

/**
 * SensorModelImpl - Hibernate implementation of the SensorModel class. Has Long
 * primary key.
 * 
 * @author Cam Moore
 * 
 */
@SuppressWarnings("PMD.UselessOverridingMethod")
public class SensorModelImpl extends SensorModel {

  /** The database primary key. */
  private Long pk;

  /**
   * Default constructor.
   */
  public SensorModelImpl() {
  }

  /**
   * @param name
   *          the name of the Sensor Model.
   * @param protocol
   *          the protocol used by the Sensor Model.
   * @param type
   *          the type of the Sensor Model.
   * @param version
   *          the version of the Sensor Mode.
   */
  public SensorModelImpl(String name, String protocol, String type, String version) {
    super(name, protocol, type, version);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.common.domainmodel.SensorModel#getName()
   */
  @Override
  public String getName() {
    return super.getName();
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
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
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
    if (obj.getClass().equals(SensorModel.class)) {
      return super.equals(obj);
    }
    if (!super.equals(obj)) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    SensorModelImpl other = (SensorModelImpl) obj;
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
   * @see org.wattdepot.common.domainmodel.SensorModel#getProtocol()
   */
  @Override
  public String getProtocol() {
    return super.getProtocol();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.common.domainmodel.SensorModel#getSlug()
   */
  @Override
  public String getId() {
    return super.getId();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.common.domainmodel.SensorModel#getType()
   */
  @Override
  public String getType() {
    return super.getType();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.common.domainmodel.SensorModel#getVersion()
   */
  @Override
  public String getVersion() {
    return super.getVersion();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.common.domainmodel.SensorModel#setName(java.lang.String)
   */
  @Override
  public void setName(String name) {
    super.setName(name);
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
   * org.wattdepot.common.domainmodel.SensorModel#setProtocol(java.lang.String)
   */
  @Override
  public void setProtocol(String protocol) {
    super.setProtocol(protocol);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.common.domainmodel.SensorModel#setSlug(java.lang.String)
   */
  @Override
  public void setId(String slug) throws BadSlugException {
    super.setId(slug);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.common.domainmodel.SensorModel#setType(java.lang.String)
   */
  @Override
  public void setType(String type) {
    super.setType(type);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.common.domainmodel.SensorModel#setVersion(java.lang.String)
   */
  @Override
  public void setVersion(String version) {
    super.setVersion(version);
  }

}
