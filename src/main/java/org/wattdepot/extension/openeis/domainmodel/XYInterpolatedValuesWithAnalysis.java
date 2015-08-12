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

package org.wattdepot.extension.openeis.domainmodel;

import org.wattdepot.common.domainmodel.XYInterpolatedValueList;

import java.util.Map;

/**
 * XYInterpolatedValuesWithAnalysis - Combination of the data and a map of analyses on the data.
 *
 * @author Cam Moore
 *         Created by carletonmoore on 4/15/15.
 */
public class XYInterpolatedValuesWithAnalysis {
  private XYInterpolatedValueList dataPoints;
  private Map<String, Double> analysis;

  /**
   * @return The data points as an XYInterpolatedValueList.
   */
  public XYInterpolatedValueList getDataPoints() {
    return dataPoints;
  }

  /**
   * Sets the data points.
   *
   * @param dataPoints The new data points.
   */
  public void setDataPoints(XYInterpolatedValueList dataPoints) {
    this.dataPoints = dataPoints;
  }

  /**
   * @return The analyses as a map of Strings (names) to Doubles (values).
   */
  public Map<String, Double> getAnalysis() {
    return analysis;
  }

  /**
   * Sets the map of analyses.
   *
   * @param analysis the new map of analyses.
   */
  public void setAnalysis(Map<String, Double> analysis) {
    this.analysis = analysis;
  }
}
