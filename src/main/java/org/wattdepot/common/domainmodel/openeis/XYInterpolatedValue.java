package org.wattdepot.common.domainmodel.openeis;

import org.wattdepot.common.domainmodel.InterpolatedValue;
import org.wattdepot.common.domainmodel.MeasurementType;

import java.util.Date;

/**
 * Two value InterpolatedValue.
 * @author Cam Moore.
 */
public class XYInterpolatedValue extends InterpolatedValue {
  /** The id of the sensor making the measurement. */
  private String ySensorId;
  /** The value of the measurement. */
  private Double yValue;
  /** The type of the measurement. */
  private MeasurementType yMeasurementType;

  /**
   * Default constructor.
   */
  public XYInterpolatedValue() {
    super();
  }

  /**
   * Creates a new XYInterpolatedValue.
   * @param xSensorId The x SensorId.
   * @param xValue The x value.
   * @param xMeasurementType The x measurement type.
   * @param date The time of both values.
   * @param ySensorId The y SensorId.
   * @param yValue The y value.
   * @param yMeasurementType The y measurement type.
   */
  public XYInterpolatedValue(String xSensorId, Double xValue, MeasurementType xMeasurementType, Date date,
                             String ySensorId, Double yValue, MeasurementType yMeasurementType) {
    super(xSensorId, xValue, xMeasurementType, date);
    this.ySensorId = ySensorId;
    this.yValue = yValue;
    this.yMeasurementType = yMeasurementType;
  }

  /**
   * Creates a new XYInterpolatedValue.
   * @param xSensorId The x SensorId.
   * @param xValue The x value.
   * @param xMeasurementType The x measurement type.
   * @param startDate The start time of both values.
   * @param endDate The end time of both values.
   * @param ySensorId The y SensorId.
   * @param yValue The y value.
   * @param yMeasurementType The y measurement type.
   */
  public XYInterpolatedValue(String xSensorId, Double xValue, MeasurementType xMeasurementType, Date startDate,
                             Date endDate, String ySensorId, Double yValue, MeasurementType yMeasurementType) {
    super(xSensorId, xValue, xMeasurementType, startDate, endDate);
    this.ySensorId = ySensorId;
    this.yValue = yValue;
    this.yMeasurementType = yMeasurementType;
  }

  /**
   * Create a new XYInterpolatedValue from the two Interpolated values.
   * @param x The X value.
   * @param y The Y value.
   */
  public XYInterpolatedValue(InterpolatedValue x, InterpolatedValue y) {
    super(x.getSensorId(), x.getValue(), x.getMeasurementType(), x.getStart(), x.getEnd());
    if (!x.getStart().equals(y.getStart()) ||
      !x.getEnd().equals(y.getEnd())) {
      throw new IllegalArgumentException("X and Y don't have the same start and end times.");
    }
    this.ySensorId = y.getSensorId();
    this.yValue = y.getValue();
    this.yMeasurementType = y.getMeasurementType();

  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }

    XYInterpolatedValue that = (XYInterpolatedValue) o;

    if (ySensorId != null ? !ySensorId.equals(that.ySensorId) : that.ySensorId != null) {
      return false;
    }
    if (yValue != null ? !yValue.equals(that.yValue) : that.yValue != null) {
      return false;
    }
    return !(yMeasurementType != null ? !yMeasurementType.equals(that.yMeasurementType) : that.yMeasurementType != null);

  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + (ySensorId != null ? ySensorId.hashCode() : 0);
    result = 31 * result + (yValue != null ? yValue.hashCode() : 0);
    result = 31 * result + (yMeasurementType != null ? yMeasurementType.hashCode() : 0);
    return result;
  }

  /**
   * @return The Y SensorId.
   */
  public String getYSensorId() {
    return ySensorId;
  }

  /**
   * Sets the Y SensorID.
   * @param ySensorId The new Y SensorId.
   */
  public void setYSensorId(String ySensorId) {
    this.ySensorId = ySensorId;
  }

  /**
   * @return The Y value.
   */
  public Double getYValue() {
    return yValue;
  }

  /**
   * Sets the Y value.
   * @param yValue The new Y value.
   */
  public void setYValue(Double yValue) {
    this.yValue = yValue;
  }

  /**
   * @return The Y MeasurementType.
   */
  public MeasurementType getYMeasurementType() {
    return yMeasurementType;
  }

  /**
   * Sets the Y MeasurementType.
   * @param yMeasurementType The new Y MeasurementType.
   */
  public void setYMeasurementType(MeasurementType yMeasurementType) {
    this.yMeasurementType = yMeasurementType;
  }

  /**
   * @return The X SensorId.
   */
  public String getXSensorId() {
    return getSensorId();
  }

  /**
   * Sets the X SensorId.
   * @param xSensorId The new X SensorId.
   */
  public void setXSensorId(String xSensorId) {
    this.setSensorId(xSensorId);
  }

  /**
   * @return The X value.
   */
  public Double getXValue() {
    return getValue();
  }

  /**
   * Sets the X value.
   * @param xValue The new X value.
   */
  public void setXValue(Double xValue) {
    this.setValue(xValue);
  }

  /**
   * @return The X MeasurementType.
   */
  public MeasurementType getXMeasurementType() {
    return getMeasurementType();
  }

  /**
   * Sets the X MeasurementType.
   * @param xMeasurementType The new X MeasurementType.
   */
  public void setXMeasurementType(MeasurementType xMeasurementType) {
    this.setMeasurementType(xMeasurementType);
  }
}
