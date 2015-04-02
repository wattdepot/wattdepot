/**
 * DescriptiveStats.java This file is part of WattDepot.
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
package org.wattdepot.common.analysis;

import java.util.ArrayList;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.wattdepot.common.domainmodel.InterpolatedValue;
import org.wattdepot.common.domainmodel.Labels;
import org.wattdepot.common.domainmodel.InterpolatedValueList;
import org.wattdepot.common.domainmodel.Measurement;
import org.wattdepot.common.domainmodel.MeasurementList;

/**
 * DescriptiveStats - Simple way of getting descriptive statistics for different
 * WattDepot measurement and interpolated values lists.
 * 
 * @author Cam Moore
 * 
 */
public class DescriptiveStats {

  private DescriptiveStatistics data;

  /**
   * @param meas MeasurementList.
   */
  public DescriptiveStats(MeasurementList meas) {
    this.data = new DescriptiveStatistics();
    for (Measurement m : meas.getMeasurements()) {
      data.addValue(m.getValue());
    }
  }
  
  /**
   * @param meas the Measurements.
   * @param valueType The type of value to use either Labels.POINT or Labels.INTERVAL.
   */
  public DescriptiveStats(MeasurementList meas, String valueType) {
    this.data = new DescriptiveStatistics();
    if (Labels.POINT.equals(valueType)) {
      for (Measurement m : meas.getMeasurements()) {
        data.addValue(m.getValue());
      }
    }
    else if (Labels.INTERVAL.equals(valueType)) {
      ArrayList<Measurement> measurements = meas.getMeasurements();
      for (int i = 1; i < measurements.size(); i++) {
        Measurement start = measurements.get(i - 1);
        Measurement end = measurements.get(i);
        data.addValue(end.getValue() - start.getValue());
      }
    }
  }
  
  /**
   * @param meas MeasuredValueList.
   */
  public DescriptiveStats(InterpolatedValueList meas) {
    this.data = new DescriptiveStatistics();
    for (InterpolatedValue v : meas.getInterpolatedValues()) {
      data.addValue(v.getValue());
    }
  }

  /**
   * @param v the value to add.
   * @see org.apache.commons.math3.stat.descriptive.DescriptiveStatistics#addValue(double)
   */
  public void addValue(double v) {
    data.addValue(v);
  }

  /**
   * 
   * @see org.apache.commons.math3.stat.descriptive.DescriptiveStatistics#clear()
   */
  public void clear() {
    data.clear();
  }

  /**
   * @param index the index.
   * @return get the value at the index.
   * @see org.apache.commons.math3.stat.descriptive.DescriptiveStatistics#getElement(int)
   */
  public double getElement(int index) {
    return data.getElement(index);
  }

  /**
   * @return the geometric mean of the values.
   * @see org.apache.commons.math3.stat.descriptive.DescriptiveStatistics#getGeometricMean()
   */
  public double getGeometricMean() {
    return data.getGeometricMean();
  }

  /**
   * @return the Kurtosis of the values.
   * @see org.apache.commons.math3.stat.descriptive.DescriptiveStatistics#getKurtosis()
   */
  public double getKurtosis() {
    return data.getKurtosis();
  }

  /**
   * @return the maximum value.
   * @see org.apache.commons.math3.stat.descriptive.DescriptiveStatistics#getMax()
   */
  public double getMax() {
    return data.getMax();
  }

  /**
   * @return the mean value.
   * @see org.apache.commons.math3.stat.descriptive.DescriptiveStatistics#getMean()
   */
  public double getMean() {
    return data.getMean();
  }

  /**
   * @return the minimum value.
   * @see org.apache.commons.math3.stat.descriptive.DescriptiveStatistics#getMin()
   */
  public double getMin() {
    return data.getMin();
  }

  /**
   * @return the number of values.
   * @see org.apache.commons.math3.stat.descriptive.DescriptiveStatistics#getN()
   */
  public long getN() {
    return data.getN();
  }

  /**
   * Returns an estimate for the pth percentile of the values.
   * @param p the requested percentile scaled from 0 to 100.
   * @return An estimate for the pth percentile.
   * @see org.apache.commons.math3.stat.descriptive.DescriptiveStatistics#getPercentile(double)
   */
  public double getPercentile(double p) {
    return data.getPercentile(p);
  }

  /**
   * @return the population variance of the values.
   * @see org.apache.commons.math3.stat.descriptive.DescriptiveStatistics#getPopulationVariance()
   */
  public double getPopulationVariance() {
    return data.getPopulationVariance();
  }

  /**
   * @return the skewness of the values.
   * @see org.apache.commons.math3.stat.descriptive.DescriptiveStatistics#getSkewness()
   */
  public double getSkewness() {
    return data.getSkewness();
  }

  /**
   * @return the values in sorted order.
   * @see org.apache.commons.math3.stat.descriptive.DescriptiveStatistics#getSortedValues()
   */
  public double[] getSortedValues() {
    return data.getSortedValues();
  }

  /**
   * @return the standard deviation of the values.
   * @see org.apache.commons.math3.stat.descriptive.DescriptiveStatistics#getStandardDeviation()
   */
  public double getStandardDeviation() {
    return data.getStandardDeviation();
  }

  /**
   * @return the sum of the values.
   * @see org.apache.commons.math3.stat.descriptive.DescriptiveStatistics#getSum()
   */
  public double getSum() {
    return data.getSum();
  }

  /**
   * @return the sum of the square of the values
   * @see org.apache.commons.math3.stat.descriptive.DescriptiveStatistics#getSumsq()
   */
  public double getSumsq() {
    return data.getSumsq();
  }

  /**
   * @return an array of the values.
   * @see org.apache.commons.math3.stat.descriptive.DescriptiveStatistics#getValues()
   */
  public double[] getValues() {
    return data.getValues();
  }

  /**
   * @return the variance of the values.
   * @see org.apache.commons.math3.stat.descriptive.DescriptiveStatistics#getVariance()
   */
  public double getVariance() {
    return data.getVariance();
  }
  
  
}
