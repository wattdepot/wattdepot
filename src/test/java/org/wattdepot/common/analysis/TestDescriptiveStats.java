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

package org.wattdepot.common.analysis;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.wattdepot.common.domainmodel.InstanceFactory;
import org.wattdepot.common.domainmodel.Measurement;
import org.wattdepot.common.domainmodel.MeasurementList;

import static org.junit.Assert.*;

/**
 * Test cases for DescriptiveStats.
 *
 * @author Cam Moore
 */
public class TestDescriptiveStats {

  private DescriptiveStats stats1;
  private Double[] theValues;

  private static final Double ERROR = 0.0001;
  /**
   * Sets up the tests.
   */
  @Before
  public void setUp() {
    theValues = new Double[3];
    MeasurementList measurementList = new MeasurementList();
    Measurement m1 = InstanceFactory.getMeasurementOne();
    theValues[0] = m1.getValue();
    Measurement m2 = InstanceFactory.getMeasurementTwo();
    m2.setValue(2 * m1.getValue());
    theValues[1] = m2.getValue();
    Measurement m3 = InstanceFactory.getMeasurementThree();
    theValues[2] = m3.getValue();
    measurementList.getMeasurements().add(m1);
    measurementList.getMeasurements().add(m2);
    measurementList.getMeasurements().add(m3);
    this.stats1 = new DescriptiveStats(measurementList);
  }

  /**
   * Cleans up after tests.
   */
  @After
  public void tearDown() {
  }

  @Test
  public void testGetVariance() throws Exception {
    assertEquals(3333.333333333333, stats1.getVariance(), ERROR);
  }

  @Test
  public void testGetValues() throws Exception {
    double[] vals = stats1.getValues();
    for (int i = 0; i < vals.length; i++) {
      assertEquals(theValues[i], vals[i], ERROR);
    }
  }

  @Test
  public void testGetSumsq() throws Exception {

  }

  @Test
  public void testGetSum() throws Exception {

  }

  @Test
  public void testGetStandardDeviation() throws Exception {

  }

  @Test
  public void testGetSortedValues() throws Exception {

  }

  @Test
  public void testGetSkewness() throws Exception {

  }

  @Test
  public void testGetPopulationVariance() throws Exception {

  }

  @Test
  public void testGetPercentile() throws Exception {

  }

  @Test
  public void testGetN() throws Exception {

  }

  @Test
  public void testGetMin() throws Exception {

  }

  @Test
  public void testGetMean() throws Exception {

  }

  @Test
  public void testGetMax() throws Exception {

  }

  @Test
  public void testGetKurtosis() throws Exception {

  }

  @Test
  public void testGetGeometricMean() throws Exception {

  }

  @Test
  public void testGetElement() throws Exception {

  }

  @Test
  public void testAddValue() throws Exception {

  }
}