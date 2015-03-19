/**
 * TestMeasuredValue.java This file is part of WattDepot.
 *
 * Copyright (C) 2013  Cam Moore
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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.Test;

/**
 * Tests MeasuredValue class.
 * 
 * @author Cam Moore
 * 
 */
public class TestInterpolatedValue {

  /**
   * Test method for
   * {@link org.wattdepot.common.domainmodel.InterpolatedValue#InterpolatedValue()}.
   */
  @Test
  public void testEquals() {
    Date now = new Date();
    InterpolatedValue i1 = new InterpolatedValue("sensorId", 10.1,
        InstanceFactory.getMeasurementType(), now);
    assertTrue(i1.equals(i1));
    assertFalse(i1.equals(null));
    assertFalse(i1.equals("foo"));
    assertFalse(i1.equals(new Object()));
    InterpolatedValue i2 = new InterpolatedValue();
    assertFalse(i2.hashCode() == i1.hashCode());
    assertFalse(i2.equals(i1));
    Date later = new Date();
    i2.setStart(later);
    assertFalse(i2.hashCode() == i1.hashCode());
    assertFalse(i2.equals(i1));
    i2.setStart(i1.getStart());
    assertFalse(i2.hashCode() == i1.hashCode());
    assertFalse(i2.equals(i1));
    i2.setEnd(later);
    assertFalse(i2.hashCode() == i1.hashCode());
    assertFalse(i2.equals(i1));
    i2.setEnd(i1.getEnd());
    assertFalse(i2.hashCode() == i1.hashCode());
    assertFalse(i2.equals(i1));
    i2.setMeasurementType(InstanceFactory.getMeasurementType2());
    assertFalse(i2.hashCode() == i1.hashCode());
    assertFalse(i2.equals(i1));
    i2.setMeasurementType(i1.getMeasurementType());
    assertFalse(i2.hashCode() == i1.hashCode());
    assertFalse(i2.equals(i1));
    i2.setSensorId("foo");
    assertFalse(i2.hashCode() == i1.hashCode());
    assertFalse(i2.equals(i1));
    i2.setSensorId(i1.getSensorId());
    assertFalse(i2.hashCode() == i1.hashCode());
    assertFalse(i2.equals(i1));
    i2.setValue(-1.0);
    assertFalse(i2.hashCode() == i1.hashCode());
    assertFalse(i2.equals(i1));
    i2.setValue(i1.getValue());
    assertTrue(i2.hashCode() == i1.hashCode());
    assertTrue(i2.equals(i1));
  }

}
