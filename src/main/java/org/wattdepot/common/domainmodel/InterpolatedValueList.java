/**
 * MeasuredValueList.java This file is part of WattDepot.
 *
 * Copyright (C) 2013  Yongwen Xu
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

import java.util.ArrayList;
import java.util.Date;

/**
 * MeasuredValueList - a list of measured values.
 * 
 * @author Yongwen Xu
 * 
 */
public class InterpolatedValueList {
  private ArrayList<InterpolatedValue> interpolatedValues;
  private ArrayList<InterpolatedValue> missingData;

  /**
   * Default Constructor.
   */
  public InterpolatedValueList() {
    interpolatedValues = new ArrayList<InterpolatedValue>();
    missingData = new ArrayList<InterpolatedValue>();
  }

  /**
   * @return the measuredValues
   */
  public ArrayList<InterpolatedValue> getInterpolatedValues() {
    return interpolatedValues;
  }

  /**
   * @param interpolatedValues the measuredValues to set
   */
  public void setInterpolatedValues(ArrayList<InterpolatedValue> interpolatedValues) {
    this.interpolatedValues = interpolatedValues;
  }

  /**
   * @return the missing data InterpolatedValues with null for the value.
   */
  public ArrayList<InterpolatedValue> getMissingData() {
    return missingData;
  }

  /**
   * @param missingData the missing data to set.
   */
  public void setMissingData(ArrayList<InterpolatedValue> missingData) {
    this.missingData = missingData;
  }

  /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
  @Override
  public String toString() {
    return "InterpolatedValueList [InterpolatedValues=" + interpolatedValues + ", MissingData=" + missingData + "]";
  }

  /**
   * Collapses the missing data combining adjacent InterpolatedValues into a single InterpolatedValue.
   */
  public void collapseMissingData() {
    ArrayList<InterpolatedValue> temp = new ArrayList<InterpolatedValue>();
    Date collapseStart = null;
    Date collapseEnd = null;
    String sensorId = null;
    MeasurementType type = null;
    for (InterpolatedValue v : missingData) {
      sensorId = v.getSensorId();
      type = v.getMeasurementType();
      Date start = v.getStart();
      Date end = v.getEnd();
      if (collapseStart == null) {
        collapseStart = start;
        collapseEnd = end;
      }
      else {
        if (collapseEnd.getTime() == start.getTime()) { // old end == start collapse the interval
          collapseEnd = end;
        }
        else { // not adjacent
          temp.add(new InterpolatedValue(sensorId, null, type, collapseStart, collapseEnd));
          collapseStart = null;
          collapseEnd = null;
        }
      }
    }
    if (collapseStart != null && collapseEnd != null) {
      temp.add(new InterpolatedValue(sensorId, null, type, collapseStart, collapseEnd));
    }
    this.missingData = temp;
  }

  /**
   * Collapses the missing data combining adjacent InterpolatedValues into a single InterpolatedValue from
   * now to the past.
   */
  public void collapseMissingDataNowToPast() {
    ArrayList<InterpolatedValue> temp = new ArrayList<InterpolatedValue>();
    Date collapseStart = null;
    Date collapseEnd = null;
    String sensorId = null;
    MeasurementType type = null;
    for (InterpolatedValue v : missingData) {
      sensorId = v.getSensorId();
      type = v.getMeasurementType();
      Date start = v.getStart();
      Date end = v.getEnd();
      if (collapseStart == null) {
        collapseStart = start;
        collapseEnd = end;
      }
      else {
        if (collapseStart.getTime() == end.getTime()) { // old end == start collapse the interval
          collapseStart = start;
        }
        else { // not adjacent
          temp.add(new InterpolatedValue(sensorId, null, type, collapseStart, collapseEnd));
          collapseStart = null;
          collapseEnd = null;
        }
      }
    }
    if (collapseStart != null && collapseEnd != null) {
      temp.add(new InterpolatedValue(sensorId, null, type, collapseStart, collapseEnd));
    }
    this.missingData = temp;
  }
}
