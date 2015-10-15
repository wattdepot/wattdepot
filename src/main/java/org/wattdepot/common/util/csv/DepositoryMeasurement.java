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

package org.wattdepot.common.util.csv;

import org.wattdepot.common.domainmodel.Depository;
import org.wattdepot.common.domainmodel.Measurement;

/**
 * Holds the Depository associated with the Measurement.
 * @author Cam Moore
 */
public class DepositoryMeasurement {
  private String depositoryId;
  private Measurement measurement;

  /**
   * Creates a new DepositoryMeasurement.
   * @param depository The Depository.
   * @param measurement The Measurement.
   */
  public DepositoryMeasurement(Depository depository, Measurement measurement) {
    this.depositoryId = depository.getId();
    this.measurement = measurement;
  }

  /**
   * Creates a new DepositoryMeasurement.
   * @param depositoryId The Depository's Id.
   * @param measurement The Measurement.
   */
  public DepositoryMeasurement(String depositoryId, Measurement measurement) {
    this.depositoryId = depositoryId;
    this.measurement = measurement;
  }

  /**
   * @return the Depository.
   */
  public String getDepositoryId() {
    return depositoryId;
  }

  /**
   * Sets the Depository Id.
   * @param depositoryId the new Despository.
   */
  public void setDepositoryId(String depositoryId) {
    this.depositoryId = depositoryId;
  }

  /**
   * @return the Measurement.
   */
  public Measurement getMeasurement() {
    return measurement;
  }

  /**
   * Sets the Measurement.
   * @param measurement the new Measurement.
   */
  public void setMeasurement(Measurement measurement) {
    this.measurement = measurement;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof DepositoryMeasurement)) {
      return false;
    }

    DepositoryMeasurement that = (DepositoryMeasurement) o;

    if (depositoryId != null ? !depositoryId.equals(that.depositoryId) : that.depositoryId != null) {
      return false;
    }
    return !(measurement != null ? !measurement.equals(that.measurement) : that.measurement != null);

  }

  @Override
  public int hashCode() {
    int result = depositoryId != null ? depositoryId.hashCode() : 0;
    result = 31 * result + (measurement != null ? measurement.hashCode() : 0);
    return result;
  }
}
