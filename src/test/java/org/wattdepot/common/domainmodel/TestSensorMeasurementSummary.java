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

public class TestSensorMeasurementSummary {
  private String sensorId = "sensorId";
  private String depositoryId = "depositoryId";
  private Date start = InstanceFactory.getTimeBetweenM1andM2();
  private Date end = InstanceFactory.getTimeBetweenM1andM3();
  private int size = 100;
  private SensorMeasurementSummary val1;
  private SensorMeasurementSummary val2;
  @Before
  public void setUp() throws Exception {
    val1 = new SensorMeasurementSummary(sensorId, depositoryId, start, end, size);
    val2 = new SensorMeasurementSummary();
  }

  @Test
  public void testGetSensorId() throws Exception {
    assertNotNull(val1);
    assertNotNull(val2);
    assertEquals(sensorId, val1.getSensorId());
  }

  @Test
  public void testGetDepositoryId() throws Exception {
    assertNotNull(val1);
    assertNotNull(val2);
    assertEquals(depositoryId, val1.getDepositoryId());
  }

  @Test
  public void testGetNumMeasurements() throws Exception {
    assertTrue(100 == val1.getNumMeasurements());
  }

  @Test
  public void testGetStart() throws Exception {
    assertTrue(start.equals(val1.getStart()));
  }

  @Test
  public void testGetEnd() throws Exception {
    assertTrue(end.equals(val1.getEnd()));
  }

  @Test
  public void testEquals() {
    assertFalse(val2.equals(val1));
    assertTrue(val1.equals(val1));
  }

  @Test
  public void testHashCode() {
    int hashCode1 = val1.hashCode();
    int hashCode2 = val2.hashCode();
    assertNotEquals(hashCode1, hashCode2);
  }

  @Test
  public void testToString() {
    String s1 = val1.toString();
    String s2 = val2.toString();
    assertFalse(s1.equals(s2));
  }
}