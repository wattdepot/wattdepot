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
  }

  /**
   * Test method for
   * {@link org.wattdepot.common.domainmodel.MeasurementRateSummary#getSensorId()}
   * .
   */
  @Test
  public void testGetSensorId() {
    assertEquals(base.getSensorId(), InstanceFactory.getSensor().getId());
  }

  /**
   * Test method for
   * {@link org.wattdepot.common.domainmodel.MeasurementRateSummary#getDepositoryId()}
   * .
   */
  @Test
  public void testGetDepositoryId() {
    assertEquals(base.getDepositoryId(), InstanceFactory.getDepository().getId());
  }

  /**
   * Test method for
   * {@link org.wattdepot.common.domainmodel.MeasurementRateSummary#getTimestamp()}
   * .
   */
  @Test
  public void testGetTimestamp() {
    assertEquals(base.getTimestamp(), InstanceFactory.getTimeBeforeM1());
  }

  /**
   * Test method for
   * {@link org.wattdepot.common.domainmodel.MeasurementRateSummary#getLatestValue()}
   * .
   */
  @Test
  public void testGetLatestValue() {
    assertEquals(base.getLatestValue(), InstanceFactory.LATEST_VALUE);
  }

  /**
   * Test method for
   * {@link org.wattdepot.common.domainmodel.MeasurementRateSummary#getType()}.
   */
  @Test
  public void testGetType() {
    assertEquals(base.getType(), InstanceFactory.getMeasurementType());
  }

  /**
   * Test method for
   * {@link org.wattdepot.common.domainmodel.MeasurementRateSummary#getOneMinuteCount()}
   * .
   */
  @Test
  public void testGetOneMinuteCount() {
    assertEquals(base.getOneMinuteCount(), InstanceFactory.ONE_MINUTE_COUNT);
  }

  /**
   * Test method for
   * {@link org.wattdepot.common.domainmodel.MeasurementRateSummary#getOneMinuteRate()}
   * .
   */
  @Test
  public void testGetOneMinuteRate() {
    assertEquals(base.getOneMinuteRate(), InstanceFactory.ONE_MINUTE_RATE);
  }

  /**
   * Test method for
   * {@link org.wattdepot.common.domainmodel.MeasurementRateSummary#getTotalCount()}
   * .
   */
  @Test
  public void testGetTotalCount() {
    assertEquals(base.getTotalCount(), InstanceFactory.TOTAL_COUNT);
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
