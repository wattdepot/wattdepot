/**
 * GarbageCollectionDefinitionImpl.java This file is part of WattDepot.
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

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.wattdepot.common.domainmodel.GarbageCollectionDefinition;

/**
 * GarbageCollectionDefinitionImpl - Hibernate implementation of
 * GarbageCollectionDefinition.
 * 
 * @author Cam Moore
 * 
 */
@Entity
@Table(name = "GC_DEFINITIONS")
public class GarbageCollectionDefinitionImpl {
  /** Database primary key. */
  @Id
  @GeneratedValue
  private Long pk;
  /** A unique id for the GarbageCollectionDefinition. */
  private String id;
  /** The human readable name. */
  private String name;
  /** The depository to store the measurements in. */
  @ManyToOne
  private DepositoryImpl depository;
  /** The sensor making the measurements. */
  private String sensor;
  /** The gcd's organization. */
  @ManyToOne
  private OrganizationImpl org;
  /** The number of days from now to ignore. */
  private Integer ignoreWindowDays;
  /** The number of days after the ignore window to garbage collect. */
  private Integer collectWindowDays;
  /** The minimum gap between measurements in seconds. */
  private Integer minGapSeconds;
  /** The last time this garbage collector was started. */
  private Date lastStarted;
  /** The last time this garbage collector completed. */
  private Date lastCompleted;
  /** The number of measurements deleted during the last gc run.. */
  private Integer numMeasurementsCollected;

  /**
   * Default constructor.
   */
  public GarbageCollectionDefinitionImpl() {

  }

  /**
   * @param id The id.
   * @param name The name.
   * @param depository The depository.
   * @param sensor The sensor.
   * @param org The organization.
   * @param ignore The ignore window.
   * @param collect The collection window.
   * @param gap The minimum gap between measurements.
   */
  public GarbageCollectionDefinitionImpl(String id, String name, DepositoryImpl depository,
      String sensor, OrganizationImpl org, Integer ignore, Integer collect, Integer gap) {
    this.id = id;
    this.name = name;
    this.depository = depository;
    this.sensor = sensor;
    this.org = org;
    this.ignoreWindowDays = ignore;
    this.collectWindowDays = collect;
    this.minGapSeconds = gap;
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
    GarbageCollectionDefinitionImpl other = (GarbageCollectionDefinitionImpl) obj;
    if (collectWindowDays == null) {
      if (other.collectWindowDays != null) {
        return false;
      }
    }
    else if (!collectWindowDays.equals(other.collectWindowDays)) {
      return false;
    }
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
    if (ignoreWindowDays == null) {
      if (other.ignoreWindowDays != null) {
        return false;
      }
    }
    else if (!ignoreWindowDays.equals(other.ignoreWindowDays)) {
      return false;
    }
    if (minGapSeconds == null) {
      if (other.minGapSeconds != null) {
        return false;
      }
    }
    else if (!minGapSeconds.equals(other.minGapSeconds)) {
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
    if (sensor == null) {
      if (other.sensor != null) {
        return false;
      }
    }
    else if (!sensor.equals(other.sensor)) {
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
    return true;
  }

  /**
   * @return the collectWindowDays
   */
  public Integer getCollectWindowDays() {
    return collectWindowDays;
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
   * @return the ignoreWindowDays
   */
  public Integer getIgnoreWindowDays() {
    return ignoreWindowDays;
  }

  /**
   * @return the lastCompleted
   */
  public Date getLastCompleted() {
    if (lastCompleted != null) {
      return new Date(lastCompleted.getTime());
    }
    return null;
  }

  /**
   * @return the lastStarted
   */
  public Date getLastStarted() {
    if (lastStarted != null) {
      return new Date(lastStarted.getTime());
    }
    return null;
  }

  /**
   * @return the minGapSeconds
   */
  public Integer getMinGapSeconds() {
    return minGapSeconds;
  }

  /**
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * @return the numMeasurementsCollected
   */
  public Integer getNumMeasurementsCollected() {
    return numMeasurementsCollected;
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
   * @return the sensor
   */
  public String getSensor() {
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
    result = prime * result + ((collectWindowDays == null) ? 0 : collectWindowDays.hashCode());
    result = prime * result + ((depository == null) ? 0 : depository.hashCode());
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    result = prime * result + ((ignoreWindowDays == null) ? 0 : ignoreWindowDays.hashCode());
    result = prime * result + ((minGapSeconds == null) ? 0 : minGapSeconds.hashCode());
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((pk == null) ? 0 : pk.hashCode());
    result = prime * result + ((sensor == null) ? 0 : sensor.hashCode());
    result = prime * result + ((org == null) ? 0 : org.hashCode());
    return result;
  }

  /**
   * @param collectWindowDays the collectWindowDays to set
   */
  public void setCollectWindowDays(Integer collectWindowDays) {
    this.collectWindowDays = collectWindowDays;
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
   * @param ignoreWindowDays the ignoreWindowDays to set
   */
  public void setIgnoreWindowDays(Integer ignoreWindowDays) {
    this.ignoreWindowDays = ignoreWindowDays;
  }

  /**
   * @param lastCompleted the lastCompleted to set
   */
  public void setLastCompleted(Date lastCompleted) {
    if (lastCompleted != null) {
      this.lastCompleted = new Date(lastCompleted.getTime());
    }
  }

  /**
   * @param lastStarted the lastStarted to set
   */
  public void setLastStarted(Date lastStarted) {
    if (lastStarted != null) {
      this.lastStarted = new Date(lastStarted.getTime());
    }
  }

  /**
   * @param minGapSeconds the minGapSeconds to set
   */
  public void setMinGapSeconds(Integer minGapSeconds) {
    this.minGapSeconds = minGapSeconds;
  }

  /**
   * @param name the name to set
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * @param numMeasurementsCollected the numMeasurementsCollected to set
   */
  public void setNumMeasurementsCollected(Integer numMeasurementsCollected) {
    this.numMeasurementsCollected = numMeasurementsCollected;
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
   * @param sensor the sensor to set
   */
  public void setSensor(String sensor) {
    this.sensor = sensor;
  }

  /**
   * @return the GarbageCollectionDefinition equivalent.
   */
  public GarbageCollectionDefinition toGCD() {
    return new GarbageCollectionDefinition(id, name, depository.getId(), sensor,
        org.getId(), ignoreWindowDays, collectWindowDays, minGapSeconds);
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "GarbageCollectionDefinitionImpl [pk=" + pk + ", id=" + id + ", name=" + name
        + ", depository=" + depository + ", sensor=" + sensor + ", organization=" + org
        + ", ignoreWindowDays=" + ignoreWindowDays + ", collectWindowDays=" + collectWindowDays
        + ", minGapSeconds=" + minGapSeconds + ", lastStarted=" + lastStarted + ", lastCompleted="
        + lastCompleted + ", numMeasurementsCollected=" + numMeasurementsCollected + "]";
  }

}
