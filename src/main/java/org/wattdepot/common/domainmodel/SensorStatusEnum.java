package org.wattdepot.common.domainmodel;

/**
 * Enumeration of valid Sensor Statuses.
 *
 * @author Cam Moore
 */
public enum SensorStatusEnum {
  /** Sensor is dead, hasn't reported any measurements. */
  BLACK("black"),
  /** Sensor has report &lt; 50 percent of expected measurements. */
  RED("red"),
  /** Sensor has reported &gt; 50 percent, but &lt; 80 percent of expected measurements. */
  YELLOW("yellow"),
  /** Sensor has reported &gt; 80 percent of expected measurements. */
  GREEN("green");

  /** The label for the status. */
  private String label;

  /** Default constructor.
   * @param str The label.
   */
  SensorStatusEnum(String str) {
    this.label = str;
  }

  /**
   * @return The label associated with the status.
   */
  String getLabel() {
    return label;
  }

}
