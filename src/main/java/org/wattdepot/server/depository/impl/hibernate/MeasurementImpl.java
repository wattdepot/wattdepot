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

import java.sql.Date;

import javax.measure.unit.Unit;

import org.wattdepot.common.domainmodel.Depository;
import org.wattdepot.common.domainmodel.Measurement;
import org.wattdepot.common.domainmodel.Sensor;

/**
 * MeasurementImpl - Hibernate persistant version of Measurement. It is 'stored'
 * in a Depository.
 * 
 * @author Cam Moore
 * 
 */
public class MeasurementImpl extends Measurement {
  /** The depository storing this measurement. */
  private Depository depository;

  /**
   * @return the depository
   */
  public Depository getDepository() {
    return depository;
  }

  /**
   * @param depository the depository to set
   */
  public void setDepository(Depository depository) {
    this.depository = depository;
  }

  /**
   * Default constructor.
   */
  public MeasurementImpl() {
    super();
  }

  /**
   * @param sensor
   *          The sensor that made the measurement.
   * @param timestamp
   *          The time of the measurement.
   * @param value
   *          The value measured.
   * @param units
   *          The type of the measurement.
   */
  public MeasurementImpl(Sensor sensor, Date timestamp, Double value, Unit<?> units) {
    super(sensor, timestamp, value, units);
  }

  /**
   * Creates a new MeasurementImpl from the Measurement.
   * 
   * @param meas
   *          the Measurement to clone.
   */
  public MeasurementImpl(Measurement meas) {
    super(meas.getSensor(), meas.getDate(), meas.getValue(), meas.units());
  }


  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((depository == null) ? 0 : depository.hashCode());
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
    if (!super.equals(obj)) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    MeasurementImpl other = (MeasurementImpl) obj;
    if (depository == null) {
      if (other.depository != null) {
        return false;
      }
    }
    else if (!depository.equals(other.depository)) {
      return false;
    }
    return true;
  }

}
