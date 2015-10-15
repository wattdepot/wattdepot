/**
 * DepositoryImpl.java This file is part of WattDepot.
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
package org.wattdepot.server.depository.impl.hibernate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.wattdepot.common.domainmodel.Depository;
import org.wattdepot.server.ServerProperties;

/**
 * DepositoryImpl - Implementation of Depository that stores the Measurements in
 * a Hibernate database.
 * 
 * @author Cam Moore
 * 
 */
@Entity
@Table(name = "DEPOSITORIES")
public class DepositoryImpl {

  /** Database primary key. */
  @Id
  @GeneratedValue
  private Long pk;
  /** The unique id it is also a slug used in URIs. */
  private String id;
  /** Name of the Depository. */
  private String name;
  /** MeasurementType of the measurements stored in this depository. */
  @ManyToOne
  private MeasurementTypeImpl type;
  /** This depository's organization. */
  @ManyToOne
  private OrganizationImpl org;
  @Transient
  private ServerProperties serverProperties;

  /**
   * Default constructor.
   */
  public DepositoryImpl() {
    super();
  }

  /**
   * @param id the id.
   * @param name the name.
   * @param type the type.
   * @param org the organization.
   */
  public DepositoryImpl(String id, String name, MeasurementTypeImpl type, OrganizationImpl org) {
    this.id = id;
    this.name = name;
    this.type = type;
    this.org = org;
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
    DepositoryImpl other = (DepositoryImpl) obj;
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
    if (type == null) {
      if (other.type != null) {
        return false;
      }
    }
    else if (!type.equals(other.type)) {
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
   * @return the org
   */
  public OrganizationImpl getOrg() {
    return org;
  }

  /**
   * @return the pk
   */
  public Long getPk() {
    return pk;
  }

  /**
   * @return the type
   */
  public MeasurementTypeImpl getType() {
    return type;
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
    result = prime * result + ((org == null) ? 0 : org.hashCode());
    result = prime * result + ((pk == null) ? 0 : pk.hashCode());
    result = prime * result + ((type == null) ? 0 : type.hashCode());
    return result;
  }

  /**
   * @return the serverProperties
   */
  public ServerProperties serverProperties() {
    return serverProperties;
  }

  /**
   * @param serverProperties the serverProperties to set
   */
  public void serverProperties(ServerProperties serverProperties) {
    this.serverProperties = serverProperties;
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
   * @param org the org to set
   */
  public void setOrg(OrganizationImpl org) {
    this.org = org;
  }

  /**
   * @param pk the pk to set
   */
  @SuppressWarnings("unused")
  private void setPk(Long pk) {
    this.pk = pk;
  }

  /**
   * @param type the type to set
   */
  public void setType(MeasurementTypeImpl type) {
    this.type = type;
  }

  /**
   * @return the Depository equivalent to this.
   */
  public Depository toDepository() {
    return new Depository(id, name, type.toMeasurementType(), org.getId());
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "DepositoryImpl [pk=" + pk + ", id=" + id + ", name=" + name + ", type=" + type
        + ", org=" + org + "]";
  }

}
