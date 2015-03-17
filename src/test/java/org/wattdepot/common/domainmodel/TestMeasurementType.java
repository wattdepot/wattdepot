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

import org.junit.Test;

import static org.junit.Assert.*;

public class TestMeasurementType {

  @Test
  public void testEquals() throws Exception {
    MeasurementType type1 = InstanceFactory.getMeasurementType();
    assertFalse(type1.equals(null));
    assertFalse(type1.equals(""));
    MeasurementType type2 = new MeasurementType();
    assertFalse(type2.hashCode() == type1.hashCode());
    assertFalse(type2.equals(type1));
    type2.setName("foo");
    assertFalse(type2.hashCode() == type1.hashCode());
    assertFalse(type2.equals(type1));
    type2.setName(type1.getName());
    assertFalse(type2.hashCode() == type1.hashCode());
    assertFalse(type2.equals(type1));
    type2.setId("foo");
    assertFalse(type2.hashCode() == type1.hashCode());
    assertFalse(type2.equals(type1));
    type2.setId(type1.getId());
    assertFalse(type2.hashCode() == type1.hashCode());
    assertFalse(type2.equals(type1));
    type2.setUnits(type1.getUnits());
    assertTrue(type2.hashCode() == type1.hashCode());
    assertTrue(type2.equals(type1));
  }
}