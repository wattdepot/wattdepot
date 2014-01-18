/**
 * MeasurementTypeImpl.java This file is part of WattDepot.
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

import javax.measure.unit.Unit;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.wattdepot.common.domainmodel.MeasurementType;

/**
 * MeasurementTypeImpl - Hibernate implementation of the MeasurementType.
 * 
 * @author Cam Moore
 * 
 */
@Entity
@Table(name = "MEASUREMENT_TYPES")
public class MeasurementTypeImpl {

  /** Database primary key. */
  @Id
  @GeneratedValue
  private Long pk;
  /** The unique id for the MeasurementType. */
  private String id;
  /** The name of the MeasurementType. */
  private String name;
  /** String property stored in persistence. */
  private String units;

  /**
   * Default constructor.
   */
  public MeasurementTypeImpl() {

  }

  /**
   * @param id the id.
   * @param name the name.
   * @param units the units.
   */
  public MeasurementTypeImpl(String id, String name, String units) {
    this.id = id;
    this.name = name;
    this.units = units;
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
    MeasurementTypeImpl other = (MeasurementTypeImpl) obj;
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
    if (units == null) {
      if (other.units != null) {
        return false;
      }
    }
    else if (!units.equals(other.units)) {
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
   * @return the units
   */
  public String getUnits() {
    return units;
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
    result = prime * result + ((units == null) ? 0 : units.hashCode());
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
   * @param units the units to set
   */
  public void setUnits(String units) {
    this.units = units;
  }

  /**
   * @return The domainmodel MeasurementType that is equivalent to this
   *         MeasurementTypeImpl.
   */
  public MeasurementType toMeasurementType() {
    MeasurementType ret = new MeasurementType(id, name, Unit.valueOf(getUnits()));
    return ret;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "MeasurementTypeImpl [pk=" + pk + ", id=" + id + ", name=" + name + ", units=" + units
        + "]";
  }

}
