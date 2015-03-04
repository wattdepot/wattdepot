/**
 * DepositorySensorContribution.java This file is part of WattDepot.
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
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

//import org.hibernate.annotations.Index;

/**
 * DepositorySensorContribution - Hibernate meta table to store the sensors that
 * have contributed measurements to depositories.
 * 
 * @author Cam Moore
 * 
 */
@Entity
@Table(name = "SENSOR_CONTRIBUTION", indexes = {@Index(name = "IDX_CONTRIBUTION", columnList = "DEPOSITORY_PK, SENSOR_PK" ) } )
public class DepositorySensorContribution {
  /** Database primary key. */
  @Id
  @GeneratedValue
  private Long pk;
  /** The sensor that made the measurements stored in the depository. */
  @ManyToOne
//  @Index(name = "IDX_CONTRIBUTION")
  private SensorImpl sensor;
  /** The Depository storing the measurement from the sensor. */
  @ManyToOne
//  @Index(name = "IDX_CONTRIBUTION")
  private DepositoryImpl depository;

  /**
   * Default constructor.
   */
  public DepositorySensorContribution() {

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
  @SuppressWarnings("unused")
  private void setPk(Long pk) {
    this.pk = pk;
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
    result = prime * result + ((pk == null) ? 0 : pk.hashCode());
    result = prime * result + ((sensor == null) ? 0 : sensor.hashCode());
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
    DepositorySensorContribution other = (DepositorySensorContribution) obj;
    if (depository == null) {
      if (other.depository != null) {
        return false;
      }
    }
    else if (!depository.equals(other.depository)) {
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
    return true;
  }

  /**
   * @return the sensor
   */
  public SensorImpl getSensor() {
    return sensor;
  }

  /**
   * @param sensor the sensor to set
   */
  public void setSensor(SensorImpl sensor) {
    this.sensor = sensor;
  }

  /**
   * @return the depository
   */
  public DepositoryImpl getDepository() {
    return depository;
  }

  /**
   * @param depository the depository to set
   */
  public void setDepository(DepositoryImpl depository) {
    this.depository = depository;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "DepositorySensorContribution [pk=" + pk + ", sensor=" + sensor + ", depository="
        + depository + "]";
  }

}
