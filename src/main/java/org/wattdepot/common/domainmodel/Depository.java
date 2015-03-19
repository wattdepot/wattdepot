/**
 * Depository.java This file is part of WattDepot.
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
package org.wattdepot.common.domainmodel;

import org.wattdepot.common.util.Slug;

/**
 * Depository - Stores measurements from Sensors of the matching measurement
 * type.
 * 
 * @author Cam Moore
 * 
 */
public class Depository implements IDomainModel {
  /** Name of the Depository. */
  protected String name;
  /** The unique id it is also a slug used in URIs. */
  protected String id;
  /** Type of measurements stored in the Depository. */
  protected MeasurementType measurementType;
  /** The id of the owner of this depository. */
  protected String organizationId;

  /**
   * The default constructor.
   */
  public Depository() {

  }

  /**
   * Create a new Depository.
   * 
   * @param name The name of the Depository.
   * @param measurementType The type of the measurements this Depository
   *        accepts.
   * @param ownerId the id of the owner of the location.
   */
  public Depository(String name, MeasurementType measurementType, String ownerId) {
    this(Slug.slugify(name), name, measurementType, ownerId);
  }

  /**
   * Create a new Depository.
   * 
   * @param slug The unique slug.
   * @param name The name of the Depository.
   * @param measurementType The type of the measurements this Depository
   *        accepts.
   * @param orgId the id of the organization of the Depository.
   */
  public Depository(String slug, String name, MeasurementType measurementType, String orgId) {
    this.name = name;
    this.id = slug;
    this.measurementType = measurementType;
    this.organizationId = orgId;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (this == obj) {
      return true;
    }
    if (!getClass().isAssignableFrom(obj.getClass())
        && !obj.getClass().isAssignableFrom(getClass()) && getClass() != obj.getClass()) {
      return false;
    }
    try {
      Depository other = (Depository) obj;
      if (measurementType == null) {
        if (other.measurementType != null) {
          return false;
        }
      }
      else if (!measurementType.equals(other.measurementType)) {
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
      return true;
    }
    catch (ClassCastException e) {
      return false;
    }
  }

  /**
   * @return the id.
   */
  public String getId() {
    return id;
  }

  /**
   * @return the measurementType
   */
  public MeasurementType getMeasurementType() {
    return measurementType;
  }

  /**
   * @return the name.
   */
  public String getName() {
    return name;
  }

  /**
   * @return the id of the owner.
   */
  public String getOrganizationId() {
    return organizationId;
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
    result = prime * result + ((measurementType == null) ? 0 : measurementType.hashCode());
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    return result;
  }

  /**
   * @param id the id to set
   */
  public void setId(String id) {
    this.id = id;
  }

  /**
   * @param measurementType the measurementType to set
   */
  public void setMeasurementType(MeasurementType measurementType) {
    this.measurementType = measurementType;
  }

  /**
   * @param name the name to set.
   */
  public void setName(String name) {
    this.name = name;
    if (this.id == null) {
      this.id = Slug.slugify(name);
    }
  }

  /**
   * @param orgId the id of the owner.
   */
  public void setOrganizationId(String orgId) {
    this.organizationId = orgId;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "Depository [id=" + id + ", name=" + name + ", measurementType=" + measurementType + "]";
  }

}
