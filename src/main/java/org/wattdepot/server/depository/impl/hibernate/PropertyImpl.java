/**
 * PropertyImpl.java This file is part of WattDepot.
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
import javax.persistence.Table;

import org.wattdepot.common.domainmodel.Property;

/**
 * PropertyImpl - Hibernate implementation of Property.
 * 
 * @author Cam Moore
 * 
 */
@Entity
@Table(name = "PROPERTIES")
public class PropertyImpl {

  /** Database primary key. */
  @Id
  @GeneratedValue
  private Long pk;
  /** The key. */
  private String key;
  /** The value. */
  private String value;

  /**
   * 
   */
  public PropertyImpl() {

  }

  /**
   * @param key The property key.
   * @param value The property value.
   */
  public PropertyImpl(String key, String value) {
    this.key = key;
    this.value = value;
  }

  /**
   * Convenience constructor.
   * 
   * @param clone The Property to clone.
   */
  public PropertyImpl(Property clone) {
    this(clone.getKey(), clone.getValue());
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
    PropertyImpl other = (PropertyImpl) obj;
    if (key == null) {
      if (other.key != null) {
        return false;
      }
    }
    else if (!key.equals(other.key)) {
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
   * @return the key
   */
  public String getKey() {
    return key;
  }

  /**
   * @return the pk
   */
  public Long getPk() {
    return pk;
  }

  /**
   * @return the value
   */
  public String getValue() {
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
    result = prime * result + ((key == null) ? 0 : key.hashCode());
    result = prime * result + ((pk == null) ? 0 : pk.hashCode());
    result = prime * result + ((value == null) ? 0 : value.hashCode());
    return result;
  }

  /**
   * @param key the key to set
   */
  public void setKey(String key) {
    this.key = key;
  }

  /**
   * @param pk the pk to set
   */
  @SuppressWarnings("unused")
  private void setPk(Long pk) {
    this.pk = pk;
  }

  /**
   * @param value the value to set
   */
  public void setValue(String value) {
    this.value = value;
  }

  /**
   * @return A Property equivalent to this PropertyImpl.
   */
  public Property toProperty() {
    return new Property(key, value);
  }
  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "PropertyImpl [pk=" + pk + ", key=" + key + ", value=" + value + "]";
  }

}
