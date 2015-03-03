/*
 * This file is part of WattDepot.
 *
 *  Copyright (C) 2015  Cam Moore
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.wattdepot.server.depository.impl.hibernate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Keeps track of the number of rows in each of the other tables.
 *
 * @author Cam Moore
 * Created by Cam Moore on 2/3/15.
 */
@Entity
@Table(name = "ROWCOUNT")
public class RowCount {
  /** Database primary key. */
  @Id
  @GeneratedValue
  private Long pk;
  private String tableName;
  private Long totalRows;

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "RowCount[" +
        "pk=" + pk +
        ", tableName='" + tableName + '\'' +
        ", totalRows=" + totalRows +
        ']';
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof RowCount)) {
      return false;
    }

    RowCount rowCount = (RowCount) o;

    if (pk != null ? !pk.equals(rowCount.pk) : rowCount.pk != null) {
      return false;
    }
    if (tableName != null ? !tableName.equals(rowCount.tableName) : rowCount.tableName != null) {
      return false;
    }
    if (totalRows != null ? !totalRows.equals(rowCount.totalRows) : rowCount.totalRows != null) {
      return false;
    }

    return true;
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    int result = pk != null ? pk.hashCode() : 0;
    result = 31 * result + (tableName != null ? tableName.hashCode() : 0);
    result = 31 * result + (totalRows != null ? totalRows.hashCode() : 0);
    return result;
  }

  /**
   * @return The number of rows.
   */
  public Long getTotalRows() {
    return totalRows;
  }

  /**
   * Sets the number of rows.
   * @param totalRows the new number of rows.
   */
  public void setTotalRows(Long totalRows) {
    this.totalRows = totalRows;
  }

  /**
   * @return The table name.
   */
  public String getTableName() {
    return tableName;
  }

  /**
   * @param tableName The new table name.
   */
  public void setTableName(String tableName) {
    this.tableName = tableName;
  }

  /**
   * @return the primary key.
   */
  public Long getPk() {
    return pk;
  }

  /**
   * @param pk the new primary key.
   */
  public void setPk(Long pk) {
    this.pk = pk;
  }
}
