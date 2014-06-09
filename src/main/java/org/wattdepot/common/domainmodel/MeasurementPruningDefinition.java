/**
 * MeasurementPruningDefinition.java This file is part of WattDepot.
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
package org.wattdepot.common.domainmodel;

import java.util.Date;

import javax.xml.datatype.DatatypeConfigurationException;

import org.wattdepot.common.util.DateConvert;
import org.wattdepot.common.util.Slug;
import org.wattdepot.common.util.tstamp.Tstamp;

/**
 * MeasurementPruningDefinition - Represents the information about a process
 * that down samples the measurements for a particular Depository and Sensor.
 * The process ensures that there is a minimum gap between measurements.
 * 
 * @author Cam Moore
 * 
 */
public class MeasurementPruningDefinition implements IDomainModel {

  /** The unique id for the GarbageCollectorDefinition. */
  private String id;
  /** The name of the GarbageCollectorDefinition. */
  private String name;
  /** The Depository's id. */
  private String depositoryId;
  /** The Sensor's id. */
  private String sensorId;
  /** The organization id. */
  private String orgId;
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
  /** The expected time of the next run. */
  private Date nextRun;

  /**
   * Default constructor.
   */
  public MeasurementPruningDefinition() {

  }

  /**
   * @param name The name of the garbage collection definition.
   * @param depositoryId The id of the depository.
   * @param sensorId The id of the sensor.
   * @param orgId The id of the organization.
   * @param ignore The number of days to ignore starting from now.
   * @param collect The number of days to garbage collect in.
   * @param gap The minimum gap between measurements in seconds.
   */
  public MeasurementPruningDefinition(String name, String depositoryId, String sensorId,
      String orgId, Integer ignore, Integer collect, Integer gap) {
    this(Slug.slugify(name), name, depositoryId, sensorId, orgId, ignore, collect, gap);
  }

  /**
   * @param slug The id for the garbage collection definition.
   * @param name The name of the garbage collection definition.
   * @param depositoryId The id of the depository.
   * @param sensorId The id of the sensor.
   * @param orgId The id of the organization.
   * @param ignore The number of days to ignore starting from now.
   * @param collect The number of days to garbage collect in.
   * @param gap The minimum gap between measurements in seconds.
   */
  public MeasurementPruningDefinition(String slug, String name, String depositoryId,
      String sensorId, String orgId, Integer ignore, Integer collect, Integer gap) {
    this.id = slug;
    this.name = name;
    this.depositoryId = depositoryId;
    this.sensorId = sensorId;
    this.orgId = orgId;
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
    MeasurementPruningDefinition other = (MeasurementPruningDefinition) obj;
    if (collectWindowDays == null) {
      if (other.collectWindowDays != null) {
        return false;
      }
    }
    else if (!collectWindowDays.equals(other.collectWindowDays)) {
      return false;
    }
    if (depositoryId == null) {
      if (other.depositoryId != null) {
        return false;
      }
    }
    else if (!depositoryId.equals(other.depositoryId)) {
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
    if (sensorId == null) {
      if (other.sensorId != null) {
        return false;
      }
    }
    else if (!sensorId.equals(other.sensorId)) {
      return false;
    }
    if (orgId == null) {
      if (other.orgId != null) {
        return false;
      }
    }
    else if (!orgId.equals(other.orgId)) {
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
   * @return the depositoryId
   */
  public String getDepositoryId() {
    return depositoryId;
  }

  /**
   * @return The duration of the last run in milliseconds.
   */
  public Long getDuration() {
    if (lastStarted != null && lastCompleted != null) {
      return lastCompleted.getTime() - lastStarted.getTime();
    }
    else {
      return 0l;
    }
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
   * @return the nextRun
   */
  public Date getNextRun() {
    if (nextRun != null) {
      return new Date(nextRun.getTime());
    }
    else {
      return null;
    }
  }

  /**
   * @return the numMeasurementsCollected
   */
  public Integer getNumMeasurementsCollected() {
    return numMeasurementsCollected;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.common.domainmodel.IDomainModel#getOrganizationId()
   */
  @Override
  public String getOrganizationId() {
    return orgId;
  }

  /**
   * @return the orgId
   */
  public String getOrgId() {
    return orgId;
  }

  /**
   * @return the sensorId
   */
  public String getSensorId() {
    return sensorId;
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
    result = prime * result + ((depositoryId == null) ? 0 : depositoryId.hashCode());
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    result = prime * result + ((ignoreWindowDays == null) ? 0 : ignoreWindowDays.hashCode());
    result = prime * result + ((minGapSeconds == null) ? 0 : minGapSeconds.hashCode());
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((sensorId == null) ? 0 : sensorId.hashCode());
    result = prime * result + ((orgId == null) ? 0 : orgId.hashCode());
    return result;
  }

  /**
   * @param collectWindowDays the collectWindowDays to set
   */
  public void setCollectWindowDays(Integer collectWindowDays) {
    this.collectWindowDays = collectWindowDays;
  }

  /**
   * @param depositoryId the depositoryId to set
   */
  public void setDepositoryId(String depositoryId) {
    this.depositoryId = depositoryId;
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
      try {
        if (collectWindowDays > 1) {
          this.nextRun = DateConvert.convertXMLCal(Tstamp.incrementDays(
              DateConvert.convertDate(lastCompleted), collectWindowDays - 1));
        }
        else {
          this.nextRun = DateConvert.convertXMLCal(Tstamp.incrementDays(
              DateConvert.convertDate(lastCompleted), 1));
        }
      }
      catch (DatatypeConfigurationException e) {
        // shouldn't happen
        e.printStackTrace();
      }
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
   * @param nextRun the nextRun to set
   */
  public void setNextRun(Date nextRun) {
    this.nextRun = new Date(nextRun.getTime());
  }

  /**
   * @param numMeasurementsCollected the numMeasurementsCollected to set
   */
  public void setNumMeasurementsCollected(Integer numMeasurementsCollected) {
    this.numMeasurementsCollected = numMeasurementsCollected;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.common.domainmodel.IDomainModel#setOrganizationId(java.lang
   * .String)
   */
  @Override
  public void setOrganizationId(String ownerId) {
    this.orgId = ownerId;

  }

  /**
   * @param orgId the orgId to set
   */
  public void setOrgId(String orgId) {
    this.orgId = orgId;
  }

  /**
   * @param sensorId the sensorId to set
   */
  public void setSensorId(String sensorId) {
    this.sensorId = sensorId;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "MeasurementPruningDefinition [id=" + id + ", name=" + name + ", depositoryId="
        + depositoryId + ", sensorId=" + sensorId + ", orgId=" + orgId + ", ignoreWindowDays="
        + ignoreWindowDays + ", collectWindowDays=" + collectWindowDays + ", minGapSeconds="
        + minGapSeconds + ", lastStarted=" + lastStarted + ", lastCompleted=" + lastCompleted
        + ", numMeasurementsCollected=" + numMeasurementsCollected + "]";
  }

}
