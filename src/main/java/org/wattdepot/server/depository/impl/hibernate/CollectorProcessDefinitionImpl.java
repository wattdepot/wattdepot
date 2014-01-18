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

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.wattdepot.common.domainmodel.CollectorProcessDefinition;
import org.wattdepot.common.domainmodel.Property;

/**
 * CollectorProcessDefinitionImpl - Hibernate implementation of
 * CollectorProcessDefinition. Supports Long references to the ids.
 * 
 * @author Cam Moore
 * 
 */
@Entity
@Table(name = "DEFINITIONS")
public class CollectorProcessDefinitionImpl {

  /** Database primary key. */
  @Id
  @GeneratedValue
  private Long pk;
  /** A unique id for the CollectorProcessDefinition. */
  private String id;
  /** The human readable name. */
  private String name;
  /** The sensor making the measurements. */
  @ManyToOne
  private SensorImpl sensor;
  /** The number of seconds between polls. */
  private Long pollingInterval;
  /** The depository to store the measurements in. */
  @ManyToOne  
  private DepositoryImpl depository;
  /** Additional properties for the Collector. */
  @OneToMany
  private Set<PropertyImpl> properties;
  /** The cpd's organization. */
  @ManyToOne
  private OrganizationImpl org;

  /**
   * Default constructor.
   */
  public CollectorProcessDefinitionImpl() {

  }

  /**
   * @param id the id.
   * @param name the name.
   * @param sensor the sensor.
   * @param pollingInterval the polling interval.
   * @param depository the depository.
   * @param properties the properties
   * @param org the orgainzation.
   */
  public CollectorProcessDefinitionImpl(String id, String name, SensorImpl sensor,
      Long pollingInterval, DepositoryImpl depository, Set<PropertyImpl> properties,
      OrganizationImpl org) {
    this.id = id;
    this.name = name;
    this.sensor = sensor;
    this.pollingInterval = pollingInterval;
    this.depository = depository;
    this.properties = properties;
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
    CollectorProcessDefinitionImpl other = (CollectorProcessDefinitionImpl) obj;
    if (depository == null) {
      if (other.depository != null) {
        return false;
      }
    }
    else if (!depository.equals(other.depository)) {
      return false;
    }
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
    if (pollingInterval == null) {
      if (other.pollingInterval != null) {
        return false;
      }
    }
    else if (!pollingInterval.equals(other.pollingInterval)) {
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
    if (sensor == null) {
      if (other.sensor != null) {
        return false;
      }
    }
    else if (!sensor.equals(other.sensor)) {
      return false;
    }
    return true;
  }

  /**
   * @return the depository
   */
  public DepositoryImpl getDepository() {
    return depository;
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
   * @return the pollingInterval
   */
  public Long getPollingInterval() {
    return pollingInterval;
  }

  /**
   * @return the properties
   */
  @OneToMany
  public Set<PropertyImpl> getProperties() {
    return properties;
  }

  /**
   * @return the sensor
   */
  public SensorImpl getSensor() {
    return sensor;
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
    result = prime * result + ((depository == null) ? 0 : depository.hashCode());
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((org == null) ? 0 : org.hashCode());
    result = prime * result + ((pk == null) ? 0 : pk.hashCode());
    result = prime * result + ((pollingInterval == null) ? 0 : pollingInterval.hashCode());
    result = prime * result + ((properties == null) ? 0 : properties.hashCode());
    result = prime * result + ((sensor == null) ? 0 : sensor.hashCode());
    return result;
  }

  /**
   * @param depository the depository to set
   */
  public void setDepository(DepositoryImpl depository) {
    this.depository = depository;
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
   * @param pollingInterval the pollingInterval to set
   */
  public void setPollingInterval(Long pollingInterval) {
    this.pollingInterval = pollingInterval;
  }

  /**
   * @param properties the properties to set
   */
  public void setProperties(Set<PropertyImpl> properties) {
    this.properties = properties;
  }

  /**
   * @param sensor the sensor to set
   */
  public void setSensor(SensorImpl sensor) {
    this.sensor = sensor;
  }

  /**
   * @return the CollectorProcessDefinition equivalent.
   */
  public CollectorProcessDefinition toCPD() {
    Set<Property> props = new HashSet<Property>();
    for (PropertyImpl p : properties) {
      props.add(p.toProperty());
    }
    CollectorProcessDefinition cpd = new CollectorProcessDefinition(id, name, sensor.getId(),
        pollingInterval, depository.getId(), props, org.getId());
    return cpd;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "CollectorProcessDefinitionImpl [pk=" + pk + ", id=" + id + ", name=" + name
        + ", sensor=" + sensor + ", pollingInterval=" + pollingInterval + ", depository="
        + depository + ", properties=" + properties + ", org=" + org + "]";
  }

}
