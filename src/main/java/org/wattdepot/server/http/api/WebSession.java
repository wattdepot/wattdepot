/**
 * WebSession.java This file is part of WattDepot.
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
package org.wattdepot.server.http.api;

/**
 * WebSession - Represents a session for use in the WattDepot Web UI. Keeps
 * track of the logged in user and their group.
 * 
 * @author Cam Moore
 * 
 */
public class WebSession {

  private String id;
  private String userId;
  private String groupId;
  private int initialHash;

  /**
   * @param id
   *          The unique id for this session.
   * @param userId
   *          The user for the session.
   * @param groupId
   *          The group the user is in.
   */
  public WebSession(String id, String userId, String groupId) {
    this.id = id;
    this.userId = userId;
    this.groupId = groupId;
    this.initialHash = hashCode();
  }

  /**
   * @return True if the session is valid.
   */
  public boolean isValid() {
    return initialHash == hashCode();
  }

  /**
   * @return the id
   */
  public String getId() {
    return id;
  }

  /**
   * @param id the id to set
   */
  public void setId(String id) {
    this.id = id;
  }

  /**
   * @return the userId
   */
  public String getUserId() {
    return userId;
  }

  /**
   * @param userId the userId to set
   */
  public void setUserId(String userId) {
    this.userId = userId;
  }

  /**
   * @return the groupId
   */
  public String getGroupId() {
    return groupId;
  }

  /**
   * @param groupId the groupId to set
   */
  public void setGroupId(String groupId) {
    this.groupId = groupId;
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
    result = prime * result + ((groupId == null) ? 0 : groupId.hashCode());
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    result = prime * result + ((userId == null) ? 0 : userId.hashCode());
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
    WebSession other = (WebSession) obj;
    if (groupId == null) {
      if (other.groupId != null) {
        return false;
      }
    }
    else if (!groupId.equals(other.groupId)) {
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
    if (userId == null) {
      if (other.userId != null) {
        return false;
      }
    }
    else if (!userId.equals(other.userId)) {
      return false;
    }
    return true;
  }

}
