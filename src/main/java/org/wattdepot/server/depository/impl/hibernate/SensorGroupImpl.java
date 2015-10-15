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

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.wattdepot.common.domainmodel.SensorGroup;

/**
 * SensorGroupImpl - Hibernate implementation of the SensorGroup. Includes pk
 * and foreign keys.
 * 
 * @author Cam Moore
 * 
 */
@Entity
@Table(name = "SENSOR_GROUPS")
public class SensorGroupImpl {

  /** The database primary key. */
  @Id
  @GeneratedValue
  private Long pk;
  /** The unique id for this group usable in URLs. */
  private String id;
  /** The name of the group. */
  private String name;
  /** Set of sensors that make up the group. */
  @ManyToMany
  private Set<SensorImpl> sensors;
  /** The group's organization. */
  @ManyToOne
  private OrganizationImpl org;

  /**
   * 
   */
  public SensorGroupImpl() {
    super();
  }

  /**
   * @param id the group's id.
   * @param name the group's name.
   * @param sensors the sensors in the group.
   * @param org the group's organization.
   */
  public SensorGroupImpl(String id, String name, Set<SensorImpl> sensors, OrganizationImpl org) {
    this.id = id;
    this.name = name;
    this.sensors = sensors;
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
  public void setPk(Long pk) {
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
   * @return the sensors
   */
  public Set<SensorImpl> getSensors() {
    return sensors;
  }

  /**
   * @param sensors the sensors to set
   */
  public void setSensors(Set<SensorImpl> sensors) {
    this.sensors = sensors;
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
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((org == null) ? 0 : org.hashCode());
    result = prime * result + ((pk == null) ? 0 : pk.hashCode());
    result = prime * result + ((sensors == null) ? 0 : sensors.hashCode());
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
    SensorGroupImpl other = (SensorGroupImpl) obj;
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
    if (sensors == null) {
      if (other.sensors != null) {
        return false;
      }
    }
    else if (!sensors.equals(other.sensors)) {
      return false;
    }
    return true;
  }

  /**
   * @return the equivalent SensorGroup to this.
   */
  public SensorGroup toSensorGroup() {
    Set<String> sens = new HashSet<String>();
    for (SensorImpl s : sensors) {
      sens.add(s.getId());
    }
    return new SensorGroup(id, name, sens, org.getId());
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "SensorGroupImpl [pk=" + pk + ", id=" + id + ", name=" + name + ", sensors=" + sensors
        + ", org=" + org + "]";
  }

}
