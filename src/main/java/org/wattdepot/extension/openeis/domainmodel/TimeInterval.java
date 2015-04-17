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

/**
 * TimeInterval - Standard time intervals for doing OpenEIS analyses.
 * @author Cam Moore
 * Created by carletonmoore on 4/16/15.
 */
public enum TimeInterval {
  ONE_WEEK (7, "1w"),
  TWO_WEEKS (14, "2w"),
  THREE_WEEKS (21, "3w"),
  FOUR_WEEKS (28, "4w"),
  ONE_MONTH (31, "1m"),
  TWO_MONTHS (62, "2m"),
  THREE_MONTHS (93, "3m"),
  FOUR_MONTHS (124, "4m"),
  FIVE_MONTHS (155, "5m"),
  SIX_MONTHS (186, "6m"),
  ONE_YEAR (365, "1y");


  private final int numDays;
  private final String parameter;

  /**
   * Creates a TimeInterval with a given number of days.
   * @param days the number of days.
   */
  TimeInterval(int days, String param) {
    this.numDays = days;
    this.parameter = param;
  }

  /**
   * @return the number of days associated with this TimeInterval.
   */
  public int getNumDays() {
    return numDays;
  }

  /**
   * @return the string parameter associated with this TimeInterval.
   */
  public String getParameter() {
    return parameter;
  }

  /**
   * Returns the defined TimeInterval for the given parameter, defaults to ONE_MONTH for
   * unknown strings.
   * @param parameter the pramamter.
   * @return the defined TimeInterval for the given parameter, defaults to ONE_MONTH for
   * unknown strings.
   */
  public static TimeInterval fromParameter(String parameter) {
    TimeInterval interval = null;
    switch (parameter) {
      case "1w":
        interval = TimeInterval.ONE_WEEK;
        break;
      case "2w":
        interval = TimeInterval.TWO_WEEKS;
        break;
      case "3w":
        interval = TimeInterval.THREE_WEEKS;
        break;
      case "4w":
        interval = TimeInterval.FOUR_WEEKS;
        break;
      case "1m":
        interval = TimeInterval.ONE_MONTH;
        break;
      case "2m":
        interval = TimeInterval.TWO_MONTHS;
        break;
      case "3m":
        interval = TimeInterval.THREE_MONTHS;
        break;
      case "4m":
        interval = TimeInterval.FOUR_MONTHS;
        break;
      case "5m":
        interval = TimeInterval.FIVE_MONTHS;
        break;
      case "6m":
        interval = TimeInterval.SIX_MONTHS;
        break;
      case "1y":
        interval = TimeInterval.ONE_YEAR;
        break;
      default:
        interval = TimeInterval.ONE_MONTH;
    }
    return interval;
  }
}
