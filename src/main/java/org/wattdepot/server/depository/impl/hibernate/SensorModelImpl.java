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

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.wattdepot.common.domainmodel.SensorModel;

/**
 * SensorModelImpl - Hibernate implementation of the SensorModel class. Has Long
 * primary key.
 * 
 * @author Cam Moore
 * 
 */
@Entity
@Table(name = "MODELS")
public class SensorModelImpl {

  /** The database primary key. */
  @Id
  @GeneratedValue
  private Long pk;
  /** A unique id for the SensorModel, used for URLs. */
  private String id;
  /** The name for the SensorModel. */
  private String name;
  /** The protocol this sensor uses. */
  protected String protocol;
  /** The type of the sensor. */
  protected String type;
  /** The version of the sensor model. */
  protected String version;

  /**
   * Default constructor.
   */
  public SensorModelImpl() {
  }

  /**
   * @param id the model's id.
   * @param name the model's name.
   * @param protocol the model's protocol.
   * @param type the model's type.
   * @param version the model's version.
   */
  public SensorModelImpl(String id, String name, String protocol, String type, String version) {
    this.id = id;
    this.name = name;
    this.protocol = protocol;
    this.type = type;
    this.version = version;
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
    SensorModelImpl other = (SensorModelImpl) obj;
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
    if (pk == null) {
      if (other.pk != null) {
        return false;
      }
    }
    else if (!pk.equals(other.pk)) {
      return false;
    }
    if (protocol == null) {
      if (other.protocol != null) {
        return false;
      }
    }
    else if (!protocol.equals(other.protocol)) {
      return false;
    }
    if (type == null) {
      if (other.type != null) {
        return false;
      }
    }
    else if (!type.equals(other.type)) {
      return false;
    }
    if (version == null) {
      if (other.version != null) {
        return false;
      }
    }
    else if (!version.equals(other.version)) {
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
   * @return the pk
   */
  public Long getPk() {
    return pk;
  }

  /**
   * @return the protocol
   */
  public String getProtocol() {
    return protocol;
  }

  /**
   * @return the type
   */
  public String getType() {
    return type;
  }

  /**
   * @return the version
   */
  public String getVersion() {
    return version;
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
    result = prime * result + ((pk == null) ? 0 : pk.hashCode());
    result = prime * result + ((protocol == null) ? 0 : protocol.hashCode());
    result = prime * result + ((type == null) ? 0 : type.hashCode());
    result = prime * result + ((version == null) ? 0 : version.hashCode());
    return result;
  }

  /**
   * @param id the id to set
   */
  public void setId(String id) {
    this.id = id;
  }

  /**
   * @param name the name to set
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * @param pk the pk to set
   */
  @SuppressWarnings("unused")
  private void setPk(Long pk) {
    this.pk = pk;
  }

  /**
   * @param protocol the protocol to set
   */
  public void setProtocol(String protocol) {
    this.protocol = protocol;
  }

  /**
   * @param type the type to set
   */
  public void setType(String type) {
    this.type = type;
  }

  /**
   * @param version the version to set
   */
  public void setVersion(String version) {
    this.version = version;
  }

  /**
   * @return the equivalent SensorModel to this.
   */
  public SensorModel toSensorModel() {
    return new SensorModel(id, name, protocol, type, version);
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "SensorModelImpl [pk=" + pk + ", id=" + id + ", name=" + name + ", protocol=" + protocol
        + ", type=" + type + ", version=" + version + "]";
  }

}
