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
  public void testEquals() {
    assertTrue(val1.equals(val1));
    assertFalse(val1.equals(null));
    assertFalse(val1.equals(new Object()));
    assertFalse(val2.hashCode() == val1.hashCode());
    assertFalse(val2.equals(val1));
    val2.setDepositoryId("foo");
    assertFalse(val2.hashCode() == val1.hashCode());
    assertFalse(val2.equals(val1));
    val2.setDepositoryId(val1.getDepositoryId());
    assertFalse(val2.hashCode() == val1.hashCode());
    assertFalse(val2.equals(val1));
    val2.setEnd(new Date());
    assertFalse(val2.hashCode() == val1.hashCode());
    assertFalse(val2.equals(val1));
    val2.setEnd(val1.getEnd());
    assertFalse(val2.hashCode() == val1.hashCode());
    assertFalse(val2.equals(val1));
    val2.setNumMeasurements(-100);
    assertFalse(val2.hashCode() == val1.hashCode());
    assertFalse(val2.equals(val1));
    val2.setNumMeasurements(val1.getNumMeasurements());
    assertFalse(val2.hashCode() == val1.hashCode());
    assertFalse(val2.equals(val1));
    val2.setSensorId("foo");
    assertFalse(val2.hashCode() == val1.hashCode());
    assertFalse(val2.equals(val1));
    val2.setSensorId(val1.getSensorId());
    assertFalse(val2.hashCode() == val1.hashCode());
    assertFalse(val2.equals(val1));
    val2.setStart(new Date());
    assertFalse(val2.hashCode() == val1.hashCode());
    assertFalse(val2.equals(val1));
    val2.setStart(val1.getStart());
    assertTrue(val2.hashCode() == val1.hashCode());
    assertTrue(val2.equals(val1));

  }

  @Test
  public void testToString() {
    String s1 = val1.toString();
    String s2 = val2.toString();
    assertFalse(s1.equals(s2));
  }
}