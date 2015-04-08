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

/**
 * XYInterpolatedValueList - A list of XYInterpolatedValues.
 * @author Cam Moore
 */
public class XYInterpolatedValueList {
  private ArrayList<XYInterpolatedValue> values;

  /**
   * Default constructor.
   */
  public XYInterpolatedValueList() {
    this.values = new ArrayList<XYInterpolatedValue>();
  }

  /**
   * @return The list of XYInterpolatedValues.
   */
  public ArrayList<XYInterpolatedValue> getValues() {
    return values;
  }

  /**
   * Sets the list of XYInterpolatedValues.
   * @param values The new list of values.
   */
  public void setValues(ArrayList<XYInterpolatedValue> values) {
    this.values = values;
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

}
