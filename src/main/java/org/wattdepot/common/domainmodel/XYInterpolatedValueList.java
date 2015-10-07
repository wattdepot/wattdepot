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

package org.wattdepot.common.domainmodel;

import java.util.ArrayList;
import java.util.Date;

/**
 * XYInterpolatedValueList - A list of XYInterpolatedValues.
 *
 * @author Cam Moore
 */
public class XYInterpolatedValueList {
  private ArrayList<XYInterpolatedValue> values;
  private ArrayList<XYInterpolatedValue> missingData;

  /**
   * Default constructor.
   */
  public XYInterpolatedValueList() {
    this.values = new ArrayList<XYInterpolatedValue>();
    this.missingData = new ArrayList<XYInterpolatedValue>();
  }

  /**
   * @return The list of XYInterpolatedValues.
   */
  public ArrayList<XYInterpolatedValue> getValues() {
    return values;
  }

  /**
   * Sets the list of XYInterpolatedValues.
   *
   * @param values The new list of values.
   */
  public void setValues(ArrayList<XYInterpolatedValue> values) {
    this.values = values;
  }

  /**
   * @return the missing data.
   */
  public ArrayList<XYInterpolatedValue> getMissingData() {
    return missingData;
  }

  /**
   * Sets the list of missing data.
   *
   * @param missingData The new list of missing data.
   */
  public void setMissingData(ArrayList<XYInterpolatedValue> missingData) {
    this.missingData = missingData;
  }

  /*
    * (non-Javadoc)
    *
    * @see java.lang.Object#toString()
    */
  @Override
  public String toString() {
    return "XYInterpolatedValueList [values=" + values + "]";
  }

  /**
   * Collapses the missing data combining adjacent InterpolatedValues into a single InterpolatedValue.
   */
  public void collapseMissingData() {
    ArrayList<XYInterpolatedValue> temp = new ArrayList<XYInterpolatedValue>();
    Date collapseStart = null;
    Date collapseEnd = null;
    String xSensorId = null;
    MeasurementType xType = null;
    String ySensorId = null;
    MeasurementType yType = null;
    for (XYInterpolatedValue v : missingData) {
      xSensorId = v.getXSensorId();
      xType = v.getYMeasurementType();
      ySensorId = v.getYSensorId();
      yType = v.getXMeasurementType();
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
          temp.add(new XYInterpolatedValue(xSensorId, null, xType, collapseStart, collapseEnd, ySensorId, null, yType));
          collapseStart = null;
          collapseEnd = null;
        }
      }
    }
    if (collapseStart != null && collapseEnd != null) {
      temp.add(new XYInterpolatedValue(xSensorId, null, xType, collapseStart, collapseEnd, ySensorId, null, yType));
    }
    this.missingData = temp;
  }

}
