/**
 * TestMeasurementRateSummary.java This file is part of WattDepot.
 *
 * Copyright (C) 2014  Cam Moore
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

/**
 * TestMeasurementRateSummary - Test cases for the MeasurementRateSummary class.
 * 
 * @author Cam Moore
 * 
 */
public class TestMeasurementRateSummary {

  private MeasurementRateSummary base;

  
  /**
   * @throws java.lang.Exception If there is a problem.
   */
  @Before
  public void setUp() throws Exception {
    base = InstanceFactory.getMeasurementRateSummary();
  }

  /**
   * @throws java.lang.Exception if there is a problem.
   */
  @After
  public void tearDown() throws Exception {
    base = null;
  }

  /**
   * Test method for
   * {@link org.wattdepot.common.domainmodel.MeasurementRateSummary#hashCode()}.
   */
  @Test
  public void testHashCode() {
    int value = base.hashCode();
    assertTrue(base.hashCode() == value);
    MeasurementRateSummary sum = new MeasurementRateSummary();
    assertFalse(sum.hashCode() == value);
    sum.setLatestValue(base.getLatestValue());
    assertFalse(sum.hashCode() == value);
    sum.setDepositoryId(base.getDepositoryId());
    assertFalse(sum.hashCode() == value);
    sum.setOneMinuteCount(base.getOneMinuteCount());
    assertFalse(sum.hashCode() == value);
    sum.setOneMinuteRate(base.getOneMinuteRate());
    assertFalse(sum.hashCode() == value);
    sum.setSensorId(base.getSensorId());
    assertFalse(sum.hashCode() == value);
    sum.setTimestamp(base.getTimestamp());
    assertFalse(sum.hashCode() == value);
    sum.setTotalCount(base.getTotalCount());
    assertTrue(sum.hashCode() == value);
  }

  /**
   * Test method for
   * {@link org.wattdepot.common.domainmodel.MeasurementRateSummary#equals(java.lang.Object)}
   * .
   */
  @Test
  public void testEqualsObject() {
    assertFalse(base.equals(null));
    assertTrue(base.equals(base));
    assertFalse(base.equals(""));
    MeasurementRateSummary sum = new MeasurementRateSummary();
    assertFalse(sum.equals(base));
    sum.setLatestValue(-1.0);
    assertFalse(sum.equals(base));
    sum.setLatestValue(base.getLatestValue());
    assertFalse(sum.equals(base));
    sum.setDepositoryId("foo");
    assertFalse(sum.equals(base));
    sum.setDepositoryId(base.getDepositoryId());
    assertFalse(sum.equals(base));
    sum.setOneMinuteCount(-100l);
    assertFalse(sum.equals(base));
    sum.setOneMinuteCount(base.getOneMinuteCount());
    assertFalse(sum.equals(base));
    sum.setOneMinuteRate(-10.0);
    assertFalse(sum.equals(base));
    sum.setOneMinuteRate(base.getOneMinuteRate());
    assertFalse(sum.equals(base));
    sum.setSensorId("foo");
    assertFalse(sum.equals(base));
    sum.setSensorId(base.getSensorId());
    assertFalse(sum.equals(base));
    sum.setTimestamp(new Date());
    assertFalse(sum.equals(base));
    sum.setTimestamp(base.getTimestamp());
    assertFalse(sum.equals(base));
    sum.setTotalCount(-392l);
    assertFalse(sum.equals(base));
    sum.setTotalCount(base.getTotalCount());
    assertTrue(sum.equals(base));
    assertTrue(base.equals(InstanceFactory.getMeasurementRateSummary()));
  }

  /**
   * Test method for
   * {@link org.wattdepot.common.domainmodel.MeasurementRateSummary#toString()}.
   */
  @Test
  public void testToString() {
    assertNotNull(base.toString());
    assertEquals(base.toString(), InstanceFactory.getMeasurementRateSummary().toString());
  }

}
