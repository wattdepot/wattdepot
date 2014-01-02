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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Tests MeasuredValue class.
 * 
 * @author Cam Moore
 * 
 */
public class TestMeasuredValue {

  /**
   * Test method for
   * {@link org.wattdepot.common.domainmodel.MeasuredValue#MeasuredValue()}.
   */
  @Test
  public void testMeasuredValue() {
    MeasuredValue val = new MeasuredValue("sensorId", 10.1, InstanceFactory.getMeasurementType());
    assertNotNull(val);
    assertTrue("sensorId".equals(val.getSensorId()));
    assertTrue(10.1 == val.getValue());
    assertTrue(InstanceFactory.getMeasurementType().equals(val.getMeasurementType()));
  }

}
