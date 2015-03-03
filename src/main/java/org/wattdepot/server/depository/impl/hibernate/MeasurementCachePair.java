/*
 * This file is part of WattDepot.
 *
 *  Copyright (C) 2014  Cam Moore
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

import org.wattdepot.common.domainmodel.InterpolatedValue;

/**
 * Represents the earliest and latest measurements for a sensor in a depository.
 * Created by Cam Moore on 12/3/14.
 * @author Cam Moore.
 */
public class MeasurementCachePair {
  private InterpolatedValue earliest;
  private InterpolatedValue latest;

  /**
   * Creates a new MeasurementCachePair from the given InterpolatedValue.
   * @param meas The InterpolatedValue.
   */
  public MeasurementCachePair(InterpolatedValue meas) {
    this.earliest = meas;
    this.latest = meas;
  }

  /**
   * Returns the earliest Measurement.
   * @return The earliest Measurement.
   */
  public InterpolatedValue getEarliest() {
    return earliest;
  }

  /**
   * Returns the latest Measurement.
   * @return The latest Measurement.
   */
  public InterpolatedValue getLatest() {
    return latest;
  }

  /**
   * Updates the earliest and latest measurements based upon the given measurement.
   * @param m the measurement to check.
   */
  public void update(InterpolatedValue m) {
    if (earliest == null) {
      earliest = m;
    }
    else if (earliest.getStart().after(m.getStart())) {
      earliest = m;
    }
    if (latest == null) {
      latest = m;
    }
    else if (latest.getEnd().before(m.getEnd())) {
      latest = m;
    }
  }
}
