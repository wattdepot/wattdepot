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

import org.wattdepot.common.domainmodel.InterpolatedValue;

import java.util.ArrayList;
import java.util.Date;

/**
 * LoadAnalysis - Represents several load analyses for a sensor over a period of days.
 *
 * @author Cam Moore
 *         Created by carletonmoore on 4/20/15.
 */
public class LoadAnalysis {
  /**
   * Start of the analysis period.
   */
  private Date start;
  /**
   * End of the analysis period.
   */
  private Date end;
  /**
   * Peak value during the period.
   */
  private Double peak;
  /**
   * Average daily maximum.
   */
  private Double aveDailyMax;
  /**
   * Average daily minimum.
   */
  private Double aveDailyMin;
  /**
   * Average daily range.
   */
  private Double aveDailyRange;
  /**
   * 5th percentile to 95th percentile ratio.
   */
  private Double aveBaseToPeakRatio;

  /**
   * Any missing data.
   */
  private ArrayList<InterpolatedValue> missingData;

  /**
   * Constructs a new LoadAnalysis with the given data.
   *
   * @param start              The start of the period.
   * @param end                The end of the period.
   * @param peak               The peak value of the period.
   * @param aveDailyMax        The average daily maximum.
   * @param aveDailyMin        The average daily minimum.
   * @param aveDailyRange      The average daily range.
   * @param aveBaseToPeakRatio The average base to peak ratio.
   */
  public LoadAnalysis(Date start, Date end, Double peak, Double aveDailyMax, Double aveDailyMin, Double aveDailyRange, Double aveBaseToPeakRatio) {
    this.start = start;
    this.end = end;
    this.peak = peak;
    this.aveDailyMax = aveDailyMax;
    this.aveDailyMin = aveDailyMin;
    this.aveDailyRange = aveDailyRange;
    this.aveBaseToPeakRatio = aveBaseToPeakRatio;
  }

  /**
   * @return The start of the period.
   */
  public Date getStart() {
    return start;
  }

  /**
   * Sets the start of the peiord.
   *
   * @param start The new start.
   */
  public void setStart(Date start) {
    this.start = start;
  }

  /**
   * @return The end of the period.
   */
  public Date getEnd() {
    return end;
  }

  /**
   * Sets the end of the period.
   *
   * @param end The new end.
   */
  public void setEnd(Date end) {
    this.end = end;
  }

  /**
   * @return The Peak value over the period.
   */
  public Double getPeak() {
    return peak;
  }

  /**
   * Sets the peak value for the period.
   *
   * @param peak The new peak value.
   */
  public void setPeak(Double peak) {
    this.peak = peak;
  }

  /**
   * @return The average daily maximum for the period.
   */
  public Double getAveDailyMax() {
    return aveDailyMax;
  }

  /**
   * Sets the average daily maximum for the peroid.
   *
   * @param aveDailyMax The new average daily maximum.
   */
  public void setAveDailyMax(Double aveDailyMax) {
    this.aveDailyMax = aveDailyMax;
  }

  /**
   * @return The average daily minimum for the period.
   */
  public Double getAveDailyMin() {
    return aveDailyMin;
  }

  /**
   * Sets the average daily minimum for the peroid.
   *
   * @param aveDailyMin The new average daily minimum.
   */
  public void setAveDailyMin(Double aveDailyMin) {
    this.aveDailyMin = aveDailyMin;
  }

  /**
   * @return The average daily range over the period.
   */
  public Double getAveDailyRange() {
    return aveDailyRange;
  }

  /**
   * Sets the average daily range over the period.
   *
   * @param aveDailyRange The new average daily range.
   */
  public void setAveDailyRange(Double aveDailyRange) {
    this.aveDailyRange = aveDailyRange;
  }

  /**
   * @return The average base to peak ratio.
   */
  public Double getAveBaseToPeakRatio() {
    return aveBaseToPeakRatio;
  }

  /**
   * Sets the average base to peak ratio.
   *
   * @param aveBaseToPeakRatio The new average base to peak ratio.
   */
  public void setAveBaseToPeakRatio(Double aveBaseToPeakRatio) {
    this.aveBaseToPeakRatio = aveBaseToPeakRatio;
  }

  /**
   * @return Any missing data in the Analysis period.
   */
  public ArrayList<InterpolatedValue> getMissingData() {
    return missingData;
  }

  /**
   * Sets the missing data for the analysis period.
   *
   * @param missingData The missing data.
   */
  public void setMissingData(ArrayList<InterpolatedValue> missingData) {
    this.missingData = missingData;
  }


}
