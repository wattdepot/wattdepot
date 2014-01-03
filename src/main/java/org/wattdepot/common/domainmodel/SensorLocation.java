/**
 * Location.java This file is part of WattDepot.
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

import org.wattdepot.common.exception.BadSlugException;
import org.wattdepot.common.util.Slug;

/**
 * Location - position on the earth.
 * 
 * @author Cam Moore
 * 
 */
public class SensorLocation implements IDomainModel {
  /** The name for the Location. */
  private String name;
  /** The unique slug for the locations used in URIs. */
  private String slug;
  /** The Location's decimal Latitude. */
  private Double latitude;
  /** The Location's decimal Longitude. */
  private Double longitude;
  /** The Location's altitude in meters MSL. */
  private Double altitude;
  /** The Location's description. */
  private String description;
  /** The id of the owner of this location. */
  private String ownerId;

  /**
   * Default constructor.
   */
  public SensorLocation() {

  }

  /**
   * @param name
   *          The name.
   * @param latitude
   *          The decimal Latitude.
   * @param longitude
   *          The decimal Longitude.
   * @param altitude
   *          The altitude in meters w.r.t. MSL.
   * @param description
   *          A String description of the Location.
   * @param ownerId
   *          the id of the owner of the location.
   */
  public SensorLocation(String name, Double latitude, Double longitude, Double altitude,
      String description, String ownerId) {
    this.name = name;
    this.slug = Slug.slugify(this.name);
    this.latitude = latitude;
    this.longitude = longitude;
    this.altitude = altitude;
    this.description = description;
    this.ownerId = ownerId;
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
    if (!getClass().isAssignableFrom(obj.getClass())
        && !obj.getClass().isAssignableFrom(getClass()) && getClass() != obj.getClass()) {
      return false;
    }
    SensorLocation other = (SensorLocation) obj;
    if (name == null) {
      if (other.name != null) {
        return false;
      }
    }
    else if (!name.equals(other.name)) {
      return false;
    }
    if (altitude == null) {
      if (other.altitude != null) {
        return false;
      }
    }
    else if (!altitude.equals(other.altitude)) {
      return false;
    }
    if (description == null) {
      if (other.description != null) {
        return false;
      }
    }
    else if (!description.equals(other.description)) {
      return false;
    }
    if (latitude == null) {
      if (other.latitude != null) {
        return false;
      }
    }
    else if (!latitude.equals(other.latitude)) {
      return false;
    }
    if (longitude == null) {
      if (other.longitude != null) {
        return false;
      }
    }
    else if (!longitude.equals(other.longitude)) {
      return false;
    }
    return true;
  }

  /**
   * @return the altitude
   */
  public Double getAltitude() {
    return altitude;
  }

  /**
   * @return the description
   */
  public String getDescription() {
    return description;
  }

  /**
   * @return the id
   */
  public String getSlug() {
    return slug;
  }

  /**
   * @return the latitude
   */
  public Double getLatitude() {
    return latitude;
  }

  /**
   * @return the longitude
   */
  public Double getLongitude() {
    return longitude;
  }

  /**
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * @return the owner
   */
  public String getOwnerId() {
    return ownerId;
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
    result = prime * result + ((altitude == null) ? 0 : altitude.hashCode());
    result = prime * result + ((description == null) ? 0 : description.hashCode());
    result = prime * result + ((latitude == null) ? 0 : latitude.hashCode());
    result = prime * result + ((longitude == null) ? 0 : longitude.hashCode());
    return result;
  }

  /**
   * Determines if the given group is the owner of this location.
   * 
   * @param group
   *          the UserGroup to check.
   * @return True if the group owns the Location or the group is the
   *         ADMIN_GROUP.
   */
  public boolean isOwner(Organization group) {
    if (ownerId != null
        && (ownerId.equals(group.getSlug()) || group.equals(Organization.ADMIN_GROUP))) {
      return true;
    }
    return false;
  }

  /**
   * @param altitude
   *          the altitude to set
   */
  public void setAltitude(Double altitude) {
    this.altitude = altitude;
  }

  /**
   * @param description
   *          the description to set
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * @param slug
   *          the slug to set
   * @exception BadSlugException
   *              if the slug isn't valid.
   */
  public void setSlug(String slug) throws BadSlugException {
    if (Slug.validateSlug(slug)) {
      this.slug = slug;
    }
    else {
      throw new BadSlugException(slug + " is not a valid slug.");
    }
  }

  /**
   * @param latitude
   *          the latitude to set
   */
  public void setLatitude(Double latitude) {
    this.latitude = latitude;
  }

  /**
   * @param longitude
   *          the longitude to set
   */
  public void setLongitude(Double longitude) {
    this.longitude = longitude;
  }

  /**
   * @param name
   *          the name to set
   */
  public void setName(String name) {
    this.name = name;
    if (this.slug == null) {
      this.slug = Slug.slugify(name);
    }
  }

  /**
   * @param ownerId
   *          the id of the owner to set
   */
  public void setOwnerId(String ownerId) {
    this.ownerId = ownerId;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "Location [slug=" + slug + ", name=" + name + ", latitude=" + latitude + ", longitude="
        + longitude + ", altitude=" + altitude + ", description=" + description + ", ownerId="
        + ownerId + "]";
  }

}
