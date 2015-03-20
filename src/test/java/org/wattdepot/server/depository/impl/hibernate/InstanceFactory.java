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

package org.wattdepot.server.depository.impl.hibernate;


import java.util.HashSet;
import java.util.Set;

/**
 * Created by carletonmoore on 3/20/15.
 */
public class InstanceFactory {
  private static OrganizationImpl org;
  private static UserInfoImpl user;
  private static Set<PropertyImpl> properties;
  private static PropertyImpl property;
  private static SensorModelImpl sensorModel;
  private static SensorImpl sensor;
  private static MeasurementTypeImpl measType;
  private static DepositoryImpl depository;

  static {
    property = new PropertyImpl("key", "value");
    properties = new HashSet<PropertyImpl>();
    properties.add(property);
    org = new OrganizationImpl("test_org_id", "test_org_name", new HashSet<UserInfoImpl>());
    user =new UserInfoImpl("test_user_id", "test_first_name", "test_last_name", "test_email@test.com",
        properties, org);
    org.getUsers().add(user);
    sensorModel = new SensorModelImpl("test_sensor_model_id", "test_sensor_model_name", "test_protocol", "test_model_type", "test_version");
    sensor = new SensorImpl("test_sensor_id", "test_sensor_name", "test_sensor_uri", sensorModel, properties, org);
    measType = new MeasurementTypeImpl("test_meas_type_id", "test_meas_type_name", "test_units");
    depository = new DepositoryImpl("test_depository_id", "test_depository_name", measType, org);
  }

  /**
   * @return A DepositoryImpl for testing.
   */
  public static DepositoryImpl getDepository() {
    return depository;
  }

  /**
   * @return A MeasurementTypeImpl for testing.
   */
  public static MeasurementTypeImpl getMeasurementType() {
    return measType;
  }


  /**
   * @return An OrganizationImpl for testing.
   */
  public static OrganizationImpl getOrganization() {
    return org;
  }

  /**
   * @return A PropertyImpl for testing.
   */
  public static PropertyImpl getProperty() {
    return property;
  }

  /**
   * @return A SensorImpl for testing.
   */
  public static SensorImpl getSensor() {
    return sensor;
  }

  /**
   * @return A SensorModel for testing.
   */
  public static SensorModelImpl getSensorModel() {
    return sensorModel;
  }
  /**
   * @return A UserInfoImpl instance for testing.
   */
  public static UserInfoImpl getUserInfo() {
    return user;
  }

}
