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
  ONE_WEEK (7),
  TWO_WEEKS (14),
  THREE_WEEKS (21),
  FOUR_WEEKS (28),
  ONE_MONTH (31),
  TWO_MONTHS (62),
  THREE_MONTHS (93),
  FOUR_MONTHS (124),
  FIVE_MONTHS (155),
  SIX_MONTHS (186),
  ONE_YEAR (365);


  private final int numDays;

  /**
   * Creates a TimeInterval with a given number of days.
   * @param days the number of days.
   */
  TimeInterval(int days) {
    this.numDays = days;
  }

  /**
   * @return the number of days associated with this TimeInterval.
   */
  public int getNumDays() {
    return numDays;
  }
}
