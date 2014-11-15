/*
 * This file is part of WattDepot.
 *
 *  Copyright (C) 2014  Cam Moore
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

import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class TestIntervalValue {
  private String sensorId = "sensorId";
  private Double dVal = 10.1;
  private MeasurementType type = InstanceFactory.getMeasurementType();
  private Date start = InstanceFactory.getTimeBetweenM1andM2();
  private Date end = InstanceFactory.getTimeBetweenM1andM3();
  private IntervalValue val1;
  private IntervalValue val2;

  /**
   * Sets up the private IntervalValue instances.
   */
  @Before
  public void setUp() {
    val1 = new IntervalValue(sensorId, dVal, type, start, end);
    val2 = new IntervalValue();

  }

  /**
   * Tests the constructor.
   * @throws Exception if there is a problem.
   */
  @Test
  public void testConstructor() throws Exception {
    assertNotNull(val1);
  }

  @Test
  public void testEquals() throws Exception {
    assertFalse(val2.equals(val1));
    assertTrue(val1.equals(val1));
  }

  @Test
  public void testGetEnd() throws Exception {
    assertEquals("Wrong end", end, val1.getEnd());
    val1.setEnd(start);
    assertFalse(val1.getEnd().equals(end));
  }

  @Test
  public void testGetMeasurementType() throws Exception {
    assertEquals("Wrong MeasurementType", type, val1.getMeasurementType());
    val1.setMeasurementType(InstanceFactory.getMeasurementType2());
    assertFalse(val1.getMeasurementType().equals(type));
  }

  @Test
  public void testGetSensorId() throws Exception {
    assertEquals("Wrong sensorId", sensorId, val1.getSensorId());
    val1.setSensorId("foo");
    assertFalse("Failed setSensorId", val1.getSensorId().equals(sensorId));
  }

  @Test
  public void testGetStart() throws Exception {
    assertEquals("Wrong start", start, val1.getStart());
    val1.setStart(end);
    assertFalse("Failed setStart", val1.getStart().equals(start));
  }

  @Test
  public void testGetValue() throws Exception {
    assertEquals("Wrong value", dVal, val1.getValue(), 0.000001);
    val1.setValue(dVal + 2);
    assertNotEquals("failed setValue", dVal, val1.getValue());
  }

  @Test
  public void testHashCode() throws Exception {
    int hashCode1 = val1.hashCode();
    int hashCode2 = val2.hashCode();
    assertNotEquals(hashCode1, hashCode2);
  }

  @Test
  public void testToString() throws Exception {
    String s1 = val1.toString();
    String s2 = val2.toString();
    assertNotEquals(s1, s2);
  }
}