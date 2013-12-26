/**
 * InstanceFactory.java This file is part of WattDepot.
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

import java.text.ParseException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.measure.unit.Unit;
import javax.xml.datatype.DatatypeConfigurationException;

import org.wattdepot.common.util.DateConvert;
import org.wattdepot.common.util.UnitsHelper;

/**
 * InstanceFactory - Utility class that has static methods for creating
 * datamodel instances for testing.
 * 
 * @author Cam Moore
 * 
 */
public class InstanceFactory {

  /**
   * @return A Depository instance for testing.
   */
  public static Depository getDepository() {
    return new Depository("Test Depository", getMeasurementType(),
        getOrganization().getSlug());
  }

  /**
   * @return A Location instance for testing.
   */
  public static SensorLocation getLocation() {
    return new SensorLocation("Test Location Ilima 6th", new Double(21.294642),
        new Double(-157.812727), new Double(30),
        "Hale Aloha Ilima residence hall 6th floor", getOrganization().getSlug());
  }

  /**
   * @return A Measurement instance for testing.
   */
  public static Measurement getMeasurementOne() {
    try {
      Date measTime = DateConvert
          .parseCalStringToDate("2013-11-20T14:35:27.925-1000");
      Double value = 100.0;
      return new Measurement(getSensor().getSlug(), measTime, value, getMeasurementType()
          .unit());
    }
    catch (ParseException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    catch (DatatypeConfigurationException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return null;
  }

  /**
   * @return A Measurement instance for testing.
   */
  public static Measurement getMeasurementThree() {
    try {
      Date measTime = DateConvert
          .parseCalStringToDate("2013-11-20T14:45:37.925-1000");
      Double value = 100.0;
      return new Measurement(getSensor().getSlug(), measTime, value, getMeasurementType()
          .unit());
    }
    catch (ParseException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    catch (DatatypeConfigurationException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return null;
  }

  /**
   * @return A Measurement instance for testing.
   */
  public static Measurement getMeasurementTwo() {
    try {
      Date measTime = DateConvert
          .parseCalStringToDate("2013-11-20T14:35:37.925-1000");
      Double value = 100.0;
      return new Measurement(getSensor().getSlug(), measTime, value, getMeasurementType()
          .unit());
    }
    catch (ParseException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    catch (DatatypeConfigurationException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return null;
  }

  /**
   * @return A MeasurementType instance for testing.
   */
  public static MeasurementType getMeasurementType() {
    Unit<?> unit = UnitsHelper.quantities.get("Flow Rate (gal/s)");
    return new MeasurementType("Test MeasurementType Name", unit);
  }

  /**
   * @return A Property instance for testing.
   */
  public static Property getProperty() {
    return new Property("test_key", "test_value");
  }

  /**
   * @return A Sensor instance for testing.
   */
  public static Sensor getSensor() {
    return new Sensor("Test Sensor", "test_sensor_uri", getLocation().getSlug(),
        getSensorModel().getSlug(), getOrganization().getSlug());
  }

  /**
   * @return A SensorGroup instance for testing.
   */
  public static SensorGroup getSensorGroup() {
    Set<String> sensors = new HashSet<String>();
    sensors.add(getSensor().getSlug());
    return new SensorGroup("Test Sensor Group", sensors, getOrganization().getSlug());
  }

  /**
   * @return A SensorModel instance for testing.
   */
  public static SensorModel getSensorModel() {
    return new SensorModel("Test Sensor Model", "test_model_protocol",
        "test_model_type", "test_model_version");
  }

  /**
   * @return A CollectorMetaData instance for testing.
   */
  public static CollectorProcessDefinition getCollectorMetaData() {
    return new CollectorProcessDefinition("Test Collector Metadata", getSensor().getSlug(), 10L,
        "test_depository", getOrganization().getSlug());
  }

  /**
   * @return A Organization instance for testing.
   */
  public static Organization getOrganization() {
    Set<String> users = new HashSet<String>();
    users.add(getUserInfo().getId());
    return new Organization("Test User Group", users);
  }

  /**
   * @return A UserInfo instance for testing.
   */
  public static UserInfo getUserInfo() {
    Set<Property> properties = new HashSet<Property>();
    properties.add(getProperty());
    return new UserInfo("test_user_id", "test_first_name", "test_last_name",
        "test_email@test.com", "test-user-group", properties);
  }

  /**
   * @return A UserPassword instance for testing.
   */
  public static UserPassword getUserPassword() {
    return new UserPassword("test_user_id", "plain_text_password");
  }
}
