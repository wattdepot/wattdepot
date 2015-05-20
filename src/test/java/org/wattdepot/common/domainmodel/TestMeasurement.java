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

import java.util.Date;

import static org.junit.Assert.*;

public class TestMeasurement {

  @Test
  public void testEquals() throws Exception {
    Measurement m1 = InstanceFactory.getMeasurementOne();
    assertTrue(m1.equals(m1));
    assertFalse(m1.equals(null));
    assertFalse(m1.equals("foo"));
    assertFalse(m1.equals(new Object()));
    Measurement m2 = new Measurement();
    assertFalse(m2.hashCode() == m1.hashCode());
    assertFalse(m2.equals(m1));
    m2.setMeasurementType("gal");
    assertFalse(m2.hashCode() == m1.hashCode());
    assertFalse(m2.equals(m1));
    m2.setMeasurementType(m1.getMeasurementType());
    assertFalse(m2.hashCode() == m1.hashCode());
    assertFalse(m2.equals(m1));
    m2.setSensorId("foo");
    assertFalse(m2.hashCode() == m1.hashCode());
    assertFalse(m2.equals(m1));
    m2.setSensorId(m1.getSensorId());
    assertFalse(m2.hashCode() == m1.hashCode());
    assertFalse(m2.equals(m1));
    m2.setDate(new Date());
    assertFalse(m2.hashCode() == m1.hashCode());
    assertFalse(m2.equals(m1));
    m2.setDate(m1.getDate());
    assertFalse(m2.hashCode() == m1.hashCode());
    assertFalse(m2.equals(m1));
    m2.setValue(-100.0);
    assertFalse(m2.hashCode() == m1.hashCode());
    assertFalse(m2.equals(m1));
    m2.setValue(m1.getValue());
    assertTrue(m2.hashCode() == m1.hashCode());
    assertTrue(m2.equals(m1));
  }

  @Test
  public void testEquivalent() {
    Measurement m1 = InstanceFactory.getMeasurementOne();
    Measurement m2 = new Measurement();
    InterpolatedValue i1 = new InterpolatedValue();
    assertTrue(m2.equivalent(i1));
    assertFalse(m1.equivalent(i1));
    i1.setSensorId(m1.getSensorId());
    assertFalse(m1.equivalent(i1));
    i1.setStart(m1.getDate());
    assertFalse(m1.equivalent(i1));
    i1.setValue(m1.getValue());
    assertFalse(m1.equivalent(i1));
    i1.setMeasurementType(InstanceFactory.getMeasurementType());
    assertTrue(m1.equivalent(i1));
  }
}