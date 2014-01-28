/**
 * MeasurementImpl.java This file is part of WattDepot.
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

import java.util.Date;

import javax.measure.unit.Unit;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.wattdepot.common.domainmodel.Measurement;

/**
 * MeasurementImpl - Hibernate persistant version of Measurement. It is 'stored'
 * in a Depository.
 * 
 * @author Cam Moore
 * 
 */
@Entity
@Table(name = "MEASUREMENTS")
public class MeasurementImpl {
  /** Database primary key. */
  @Id
  @GeneratedValue
  private Long pk;
  /** The unique id. Can be used in URIs. */
  private String id;
  /** The sensor that made the measurement. */
  @ManyToOne
  private SensorImpl sensor;
  /** The time of the measurement. */
  private Date timestamp;
  /** The value of the measurement. */
  private Double value;
  /** The units of the measurement. */
  private String units;
  /** The Depository storing the measurement. */
  @ManyToOne
  private DepositoryImpl depository;

  /**
   * Default constructor.
   */
  public MeasurementImpl() {
    super();
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
    MeasurementImpl other = (MeasurementImpl) obj;
    if (id == null) {
      if (other.id != null) {
        return false;
      }
    }
    else if (!id.equals(other.id)) {
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
    if (timestamp == null) {
      if (other.timestamp != null) {
        return false;
      }
    }
    else if (!timestamp.equals(other.timestamp)) {
      return false;
    }
    if (units == null) {
      if (other.units != null) {
        return false;
      }
    }
    else if (!units.equals(other.units)) {
      return false;
    }
    if (value == null) {
      if (other.value != null) {
        return false;
      }
    }
    else if (!value.equals(other.value)) {
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
   * @return the pk
   */
  public Long getPk() {
    return pk;
  }

  /**
   * @return the sensor
   */
  public SensorImpl getSensor() {
    return sensor;
  }

  /**
   * @return the timestamp
   */
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "MEASUREMENT_DATE")
  public Date getTimestamp() {
    return new Date(timestamp.getTime());
  }

  /**
   * @return the units
   */
  public String getUnits() {
    return units;
  }

  /**
   * @return the value
   */
  public Double getValue() {
    return value;
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
    result = prime * result + ((pk == null) ? 0 : pk.hashCode());
    result = prime * result + ((sensor == null) ? 0 : sensor.hashCode());
    result = prime * result + ((timestamp == null) ? 0 : timestamp.hashCode());
    result = prime * result + ((units == null) ? 0 : units.hashCode());
    result = prime * result + ((value == null) ? 0 : value.hashCode());
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
   * @param pk the pk to set
   */
  @SuppressWarnings("unused")
  private void setPk(Long pk) {
    this.pk = pk;
  }

  /**
   * @param sensor the sensor to set
   */
  public void setSensor(SensorImpl sensor) {
    this.sensor = sensor;
  }

  /**
   * @param timestamp the timestamp to set
   */
  public void setTimestamp(Date timestamp) {
    this.timestamp = new Date(timestamp.getTime());
  }

  /**
   * @param units the units to set
   */
  public void setUnits(String units) {
    this.units = units;
  }


  /**
   * @param value the value to set
   */
  public void setValue(Double value) {
    this.value = value;
  }

  /**
   * @return The domainmodel Measurement that is equivalent to this
   *         MeasurementImpl.
   */
  public Measurement toMeasurement() {
    Measurement ret = new Measurement(sensor.getId(), timestamp, value, Unit.valueOf(units));
    return ret;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "MeasurementImpl [pk=" + pk + ", id=" + id + ", sensor=" + sensor + ", timestamp="
        + timestamp + ", value=" + value + ", units=" + units + ", depository=" + depository + "]";
  }

}
