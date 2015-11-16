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
 * InstanceFactory - Utility class that has static methods for creating datamodel instances for testing.
 * 
 * @author Cam Moore
 * 
 */
public class InstanceFactory {

  /** The latest value. */
  public static final Double LATEST_VALUE = 100.0;
  /** The one minute count. */
  public static final Long ONE_MINUTE_COUNT = 4l;
  /** The one minute rate. */
  public static final Double ONE_MINUTE_RATE = 0.667;
  /** The total count. */
  public static final Long TOTAL_COUNT = 1000000l;
  
  /**
   * @return A CollectorProcessDefinition instance for testing.
   */
  public static CollectorProcessDefinition getCollectorProcessDefinition() {
    return new CollectorProcessDefinition("Test Collector Process Defintion", getSensor().getId(),
        10L, getDepository().getId(), getOrganization().getId());
  }

  /**
   * @return A Depository instance for testing.
   */
  public static Depository getDepository() {
    return new Depository("Test Depository", getMeasurementType(), getOrganization().getId());
  }

  /**
   * @return A Depository instance for testing.
   */
  public static Depository getDepository2() {
    return new Depository("Test Depository2", getMeasurementType(), getOrganization().getId());
  }

  /**
   * @return A MeasurementPruningDefinition for testing.
   */
  public static MeasurementPruningDefinition getMeasurementPruningDefinition() {
    return new MeasurementPruningDefinition("Test MeasurementPruningDefinition", getDepository()
        .getId(), getSensor().getId(), getOrganization().getId(), 1, 1, 300);
  }

  /**
   * @return A MeasurementRateSummary for testing.
   */
  public static MeasurementRateSummary getMeasurementRateSummary() {
    MeasurementRateSummary ret = new MeasurementRateSummary();
    ret.setDepositoryId(getDepository().getId());
    ret.setLatestValue(LATEST_VALUE);
    ret.setOneMinuteCount(ONE_MINUTE_COUNT);
    ret.setOneMinuteRate(ONE_MINUTE_RATE);
    ret.setSensorId(getSensor().getId());
    ret.setTimestamp(getTimeBeforeM1());
    ret.setTotalCount(TOTAL_COUNT);
    ret.setType(getMeasurementType());
    return ret;
  }
  
  /**
   * @return A Measurement instance for testing.
   */
  public static Measurement getMeasurementOne() {
    try {
      Date measTime = DateConvert.parseCalStringToDate("2013-11-20T14:35:27.925-1000");
      Double value = 100.0;
      return new Measurement(getSensor().getId(), measTime, value, getMeasurementType().unit());
    }
    catch (ParseException e) {
      e.printStackTrace();
    }
    catch (DatatypeConfigurationException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * @return A Measurement instance for testing.
   */
  public static Measurement getMeasurementThree() {
    try {
      Date measTime = DateConvert.parseCalStringToDate("2013-11-20T14:45:37.925-1000");
      Double value = 100.0;
      return new Measurement(getSensor().getId(), measTime, value, getMeasurementType().unit());
    }
    catch (ParseException e) {
      e.printStackTrace();
    }
    catch (DatatypeConfigurationException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * @return A Measurement instance for testing.
   */
  public static Measurement getMeasurementTwo() {
    try {
      Date measTime = DateConvert.parseCalStringToDate("2013-11-20T14:35:37.925-1000");
      Double value = 100.0;
      return new Measurement(getSensor().getId(), measTime, value, getMeasurementType().unit());
    }
    catch (ParseException e) {
      e.printStackTrace();
    }
    catch (DatatypeConfigurationException e) {
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
   * @return A MeasurementType instance for testing.
   */
  public static MeasurementType getMeasurementType2() {
    Unit<?> unit = UnitsHelper.quantities.get("Flow Rate (gal/s)");
    return new MeasurementType("Test MeasurementType Name2", unit);
  }

  /**
   * @return A Organization instance for testing.
   */
  public static Organization getOrganization() {
    Set<String> users = new HashSet<String>();
    users.add(getUserInfo().getUid());
    return new Organization("Test User Group", users);
  }

  /**
   * @return A second Organization instance for testing.
   */
  public static Organization getOrganization2() {
    Set<String> users = new HashSet<String>();
    users.add(getUserInfo2().getUid());
    return new Organization("Test User Group2", users);
  }

  /**
   * @return A third Organization instance for testing.
   */
  public static Organization getOrganization3() {
    Set<String> users = new HashSet<String>();
    users.add(getUserInfo3().getUid());
    return new Organization("Test User Group3", users);
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
    return new Sensor("Test Sensor", "test_sensor_uri", getSensorModel().getId(), getOrganization()
        .getId());
  }

  /**
   * @return A SensorGroup instance for testing.
   */
  public static SensorGroup getSensorGroup() {
    Set<String> sensors = new HashSet<String>();
    sensors.add(getSensor().getId());
    return new SensorGroup("Test Sensor Group", sensors, getOrganization().getId());
  }

  /**
   * @return A SensorModel instance for testing.
   */
  public static SensorModel getSensorModel() {
    return new SensorModel("Test Sensor Model", "test_model_protocol", "test_model_type",
        "test_model_version");
  }

  /**
   * @return a Date after measurement 3.
   */
  public static Date getTimeAfterM3() {
    try {
      return DateConvert.parseCalStringToDate("2013-11-20T14:45:47.925-1000");
    }
    catch (ParseException e) {
      e.printStackTrace();
    }
    catch (DatatypeConfigurationException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * @return a Date before measurement 1.
   */
  public static Date getTimeBeforeM1() {
    try {
      return DateConvert.parseCalStringToDate("2013-11-20T13:35:32.290-1000");
    }
    catch (ParseException e) {
      e.printStackTrace();
    }
    catch (DatatypeConfigurationException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * @return a Date between measurement 1 and 2.
   */
  public static Date getTimeBetweenM1andM2() {
    try {
      return DateConvert.parseCalStringToDate("2013-11-20T14:35:32.290-1000");
    }
    catch (ParseException e) {
      e.printStackTrace();
    }
    catch (DatatypeConfigurationException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * @return a Date after measurement 1 and before measurement 3.
   */
  public static Date getTimeBetweenM1andM3() {
    try {
      return DateConvert.parseCalStringToDate("2013-11-20T14:39:32.290-1000");
    }
    catch (ParseException e) {
      e.printStackTrace();
    }
    catch (DatatypeConfigurationException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * @return A UserInfo instance for testing.
   */
  public static UserInfo getUserInfo() {
    Set<Property> properties = new HashSet<Property>();
    properties.add(getProperty());
    return new UserInfo("test_user_id", "test_first_name", "test_last_name", "test_email@test.com",
        "test-user-group", properties, "secret1");
  }

  /**
   * @return A UserInfo instance for testing.
   */
  public static UserInfo getUserInfo2() {
    Set<Property> properties = new HashSet<Property>();
    properties.add(getProperty());
    return new UserInfo("test_user_id2", "test_first_name2", "test_last_name2",
        "test_email2@test.com", "test-user-group2", properties, "secret2");
  }

  /**
   * @return A UserInfo instance for testing.
   */
  public static UserInfo getUserInfo3() {
    Set<Property> properties = new HashSet<Property>();
    properties.add(getProperty());
    return new UserInfo("test_user_id3", "test_first_name3", "test_last_name3",
        "test_email3@test.com", "test-user-group3", properties, "secret3");
  }

  /**
   * @return A UserPassword instance for testing.
   */
  public static UserPassword getUserPassword() {
    return new UserPassword("test_user_id", "test-user-group", "plain_text_password");
  }

  /**
   * @return A second UserPassword instance for testing.
   */
  public static UserPassword getUserPassword2() {
    return new UserPassword("test_user_id2", "test-user-group", "plain_text_password2");
  }
}
