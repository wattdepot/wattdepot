/**
 * MeasurementType.java This file is part of WattDepot 3.
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
package org.wattdepot3.datamodel;

import javax.measure.unit.Unit;

import org.wattdepot3.util.Slug;

/**
 * MeasurementType - Defines the type of a Measurement. This class includes a
 * human readable name, slug, and JScience Unit<?>.
 * 
 * @author Cam Moore
 * 
 */
public class MeasurementType {
  /** The name of the MeasurementType. */
  private String name;
  /** The unique id for the MeasurementType. */
  private String id;
  /** The Units for the measurement type. */
  private Unit<?> unit;
  /** String property stored in persistence. */
  private String units;

  /**
   * Default constructor.
   */
  public MeasurementType() {

  }

  /**
   * Creates a new MeasurementType.
   * 
   * @param name
   *          the name of the type.
   * @param unit
   *          the units of measurement.
   */
  public MeasurementType(String name, Unit<?> unit) {
    this.name = name;
    this.id = Slug.slugify(name);
    this.unit = unit;
    this.units = this.unit.toString();
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
    MeasurementType other = (MeasurementType) obj;
    if (name == null) {
      if (other.name != null) {
        return false;
      }
    }
    else if (!name.equals(other.name)) {
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
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((id == null) ? 0 : id.hashCode());
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
   * @param name
   *          the name to set
   */
  public void setName(String name) {
    this.name = name;
    if (this.id == null) {
      this.id = Slug.slugify(name);
    }
  }

  /**
   * @param units
   *          the units to set
   */
  public void setUnits(String units) {
    this.units = units;
    this.unit = Unit.valueOf(units);
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "MeasurementType [name=" + name + ", units=" + units + "]";
  }

  /**
   * @return The Unit<?> associated with this MeasurementType.
   */
  public Unit<?> unit() {
    return unit;
  }
}
