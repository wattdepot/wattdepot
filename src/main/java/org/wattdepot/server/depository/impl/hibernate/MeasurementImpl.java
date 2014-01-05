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

import org.wattdepot.common.domainmodel.Measurement;
import org.wattdepot.common.exception.BadSlugException;

/**
 * MeasurementImpl - Hibernate persistant version of Measurement. It is 'stored'
 * in a Depository.
 * 
 * @author Cam Moore
 * 
 */
@SuppressWarnings("PMD.UselessOverridingMethod")
public class MeasurementImpl extends Measurement {
  /** Database primary key. */
  private Long pk;
  /** Foreign key of the sensor. */
  private Long sensorFk;
  /** Foreign key of the depository. */
  private Long depositoryFk;
  /** The id of the depository storing this measurement. */
  private String depositoryId;

  /**
   * Default constructor.
   */
  public MeasurementImpl() {
    super();
  }

  /**
   * Creates a new MeasurementImpl from the Measurement.
   * 
   * @param meas
   *          the Measurement to clone.
   */
  public MeasurementImpl(Measurement meas) {
    super(meas.getSensorId(), meas.getDate(), meas.getValue(), meas.units());
  }

  /**
   * @param sensorId
   *          The id of the sensor that made the measurement.
   * @param timestamp
   *          The time of the measurement.
   * @param value
   *          The value measured.
   * @param units
   *          The type of the measurement.
   */
  public MeasurementImpl(String sensorId, Date timestamp, Double value, Unit<?> units) {
    super(sensorId, timestamp, value, units);
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
    if (obj.getClass().equals(Measurement.class)) {
      return super.equals(obj);
    }
    if (!super.equals(obj)) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    MeasurementImpl other = (MeasurementImpl) obj;
    if (depositoryFk == null) {
      if (other.depositoryFk != null) {
        return false;
      }
    }
    else if (!depositoryFk.equals(other.depositoryFk)) {
      return false;
    }
    if (depositoryId == null) {
      if (other.depositoryId != null) {
        return false;
      }
    }
    else if (!depositoryId.equals(other.depositoryId)) {
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
    if (sensorFk == null) {
      if (other.sensorFk != null) {
        return false;
      }
    }
    else if (!sensorFk.equals(other.sensorFk)) {
      return false;
    }
    return true;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.common.domainmodel.Measurement#getDate()
   */
  @Override
  public java.util.Date getDate() {
    return super.getDate();
  }

  /**
   * @return the depositoryFk
   */
  public Long getDepositoryFk() {
    return depositoryFk;
  }

  /**
   * @return the depositoryId
   */
  public String getDepositoryId() {
    return depositoryId;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.common.domainmodel.Measurement#getMeasurementType()
   */
  @Override
  public String getMeasurementType() {
    return super.getMeasurementType();
  }

  /**
   * @return the pk
   */
  public Long getPk() {
    return pk;
  }

  /**
   * @return the sensorFk
   */
  public Long getSensorFk() {
    return sensorFk;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.common.domainmodel.Measurement#getSensorId()
   */
  @Override
  public String getSensorId() {
    return super.getSensorId();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.common.domainmodel.Measurement#getSlug()
   */
  @Override
  public String getSlug() {
    return super.getSlug();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.common.domainmodel.Measurement#getValue()
   */
  @Override
  public Double getValue() {
    return super.getValue();
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
    result = prime * result + ((depositoryFk == null) ? 0 : depositoryFk.hashCode());
    result = prime * result + ((depositoryId == null) ? 0 : depositoryId.hashCode());
    result = prime * result + ((pk == null) ? 0 : pk.hashCode());
    result = prime * result + ((sensorFk == null) ? 0 : sensorFk.hashCode());
    return result;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.common.domainmodel.Measurement#setDate(java.util.Date)
   */
  @Override
  public void setDate(java.util.Date timestamp) {
    super.setDate(timestamp);
  }

  /**
   * @param depositoryFk
   *          the depositoryFk to set
   */
  public void setDepositoryFk(Long depositoryFk) {
    this.depositoryFk = depositoryFk;
  }

  /**
   * @param depositoryId
   *          the depositoryId to set
   */
  public void setDepositoryId(String depositoryId) {
    this.depositoryId = depositoryId;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.common.domainmodel.Measurement#setMeasurementType(org.wattdepot
   * .common.domainmodel.String)
   */
  @Override
  public void setMeasurementType(String measurementType) {
    super.setMeasurementType(measurementType);
  }

  /**
   * @param pk
   *          the pk to set
   */
  public void setPk(Long pk) {
    this.pk = pk;
  }

  /**
   * @param sensorFk
   *          the sensorFk to set
   */
  public void setSensorFk(Long sensorFk) {
    this.sensorFk = sensorFk;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.common.domainmodel.Measurement#setSensorId(org.wattdepot.
   * common.domainmodel.String)
   */
  @Override
  public void setSensorId(String sensorId) {
    super.setSensorId(sensorId);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.common.domainmodel.Measurement#setSlug(org.wattdepot.common
   * .domainmodel.String)
   */
  @Override
  public void setSlug(String slug) throws BadSlugException {
    super.setSlug(slug);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.common.domainmodel.Measurement#setValue(java.lang.Double)
   */
  @Override
  public void setValue(Double value) {
    super.setValue(value);
  }

}
