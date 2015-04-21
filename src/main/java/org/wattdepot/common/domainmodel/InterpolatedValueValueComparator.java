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

import java.util.Comparator;

/**
 * InterpolatedValueValueComparator - Comparator of InterpolatedValues that compares the value.
 *
 * @author Cam Moore
 *         Created by carletonmoore on 4/20/15.
 */
public class InterpolatedValueValueComparator implements Comparator<InterpolatedValue> {
  @Override
  public int compare(InterpolatedValue o1, InterpolatedValue o2) {
    if (o1.getValue() < o2.getValue()) {
      return -1;
    }
    else if (o1.getValue() > o2.getValue()) {
      return 1;
    }
    return 0;
  }
}
