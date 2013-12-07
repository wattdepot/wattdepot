/**
 * TestWattDepotClient.java This file is part of WattDepot 3.
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
package org.wattdepot.client.restlet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.wattdepot.client.ClientProperties;
import org.wattdepot.client.restlet.WattDepotAdminClient;
import org.wattdepot.client.restlet.WattDepotClient;
import org.wattdepot.datamodel.CollectorMetaData;
import org.wattdepot.datamodel.CollectorMetaDataList;
import org.wattdepot.datamodel.Depository;
import org.wattdepot.datamodel.InstanceFactory;
import org.wattdepot.datamodel.Measurement;
import org.wattdepot.datamodel.MeasurementType;
import org.wattdepot.datamodel.MeasurementTypeList;
import org.wattdepot.datamodel.Sensor;
import org.wattdepot.datamodel.SensorGroup;
import org.wattdepot.datamodel.SensorGroupList;
import org.wattdepot.datamodel.SensorList;
import org.wattdepot.datamodel.SensorLocation;
import org.wattdepot.datamodel.SensorModel;
import org.wattdepot.datamodel.SensorModelList;
import org.wattdepot.datamodel.UserGroup;
import org.wattdepot.datamodel.UserInfo;
import org.wattdepot.datamodel.UserPassword;
import org.wattdepot.exception.IdNotFoundException;
import org.wattdepot.exception.MeasurementTypeException;
import org.wattdepot.exception.NoMeasurementException;
import org.wattdepot.server.WattDepotServer;

/**
 * TestWattDepotClient - Test cases for the WattDepotClient class.
 * 
 * @author Cam Moore
 * 
 */
public class TestWattDepotClient {

  protected static WattDepotServer server;

  /** The handle on the client. */
  private static WattDepotAdminClient admin;
  private static WattDepotClient test;
  private static UserInfo testUser = InstanceFactory.getUserInfo();
  private static UserPassword testPassword = InstanceFactory.getUserPassword();
  private static UserGroup testGroup = InstanceFactory.getUserGroup();

  /**
   * Starts up a WattDepotServer to start the testing.
   * 
   * @throws Exception
   *           if there is a problem starting the server.
   */
  @BeforeClass
  public static void setupServer() throws Exception {
    server = WattDepotServer.newTestInstance();
  }

  /**
   * Shuts down the WattDepotServer.
   * 
   * @throws Exception
   *           if there is a problem.
   */
  @AfterClass
  public static void stopServer() throws Exception {
    server.stop();
  }

  /**
   * @throws java.lang.Exception
   *           if there is a problem.
   */
  // @BeforeClass
  // public static void setUpClass() throws Exception {
  // System.out.println("setUp()");
  // admin = new WattDepotAdminClient("http://localhost:8119/", "admin",
  // "admin");
  // admin.putUserPassword(testPassword);
  // admin.putUser(testUser);
  // admin.putUserGroup(testGroup);
  // admin.putMeasurementType(InstanceFactory.getMeasurementType());
  // test = new WattDepotClient("http://localhost:8119/", testPassword.getId(),
  // testPassword.getPlainText());
  // }

  /**
   * @throws java.lang.Exception
   *           if there is a problem.
   */
  // @AfterClass
  // public static void tearDownClass() throws Exception {
  // System.out.println("tearDown()");
  // admin.deleteUser(testPassword.getId());
  // admin.deleteUserGroup(testGroup.getId());
  // }

  /**
   * @throws java.lang.Exception
   *           if there is a problem.
   */
  @Before
  public void setUp() throws Exception {
    System.out.println("setUp()");
    ClientProperties props = new ClientProperties();
    props.setTestProperties();
    String serverURL = "http://" + props.get(ClientProperties.WATTDEPOT_SERVER_HOST) + ":"
        + props.get(ClientProperties.PORT_KEY) + "/";
    admin = new WattDepotAdminClient(serverURL, props.get(ClientProperties.USER_NAME),
        props.get(ClientProperties.USER_PASSWORD));
    admin.putUserPassword(testPassword);
    admin.putUser(testUser);
    admin.putUserGroup(testGroup);
    admin.putMeasurementType(InstanceFactory.getMeasurementType());
    test = new WattDepotClient(serverURL, testPassword.getId(), testPassword.getPlainText());
  }

  /**
   * @throws java.lang.Exception
   *           if there is a problem.
   */
  @After
  public void tearDown() throws Exception {
    System.out.println("tearDown()");
    admin.deleteUser(testPassword.getId());
    admin.deleteUserGroup(testGroup.getId());
  }

  /**
   * Test method for
   * {@link org.wattdepot.client.restlet.WattDepotClient#WattDepotClient(java.lang.String, java.lang.String, java.lang.String)}
   * .
   */
  @Test
  public void testWattDepotClient() {
    assertNotNull(test);
  }

  /**
   * Test method for Depositories.
   */
  @Test
  public void testDepository() {
    // Depository depo = InstanceFactory.getDepository();
    // test.putDepository(depo);
    // try {
    // Depository ret = test.getDepository(depo.getId());
    // assertEquals(depo, ret);
    // test.deleteDepository(ret);
    // try {
    // ret = test.getDepository(depo.getName());
    // assertNull(ret);
    // }
    // catch (IdNotFoundException e) {
    // // this is what we want.
    // }
    // }
    // catch (IdNotFoundException e) {
    // fail("Should have " + depo);
    // }
    //
  }

  /**
   * Test method for Locations.
   */
  @Test
  public void testLocation() {
    SensorLocation loc = InstanceFactory.getLocation();
    test.putLocation(loc);
    try {
      SensorLocation ret = test.getLocation(loc.getId());
      assertEquals(loc, ret);
      test.deleteLocation(ret);
      try {
        ret = test.getLocation(loc.getId());
        assertNull(ret);
      }
      catch (IdNotFoundException e) {
        // this is what we want.
      }
    }
    catch (IdNotFoundException e) {
      fail("Should have " + loc);
    }
  }

  /**
   * Test method for MeasurementTypes.
   */
  @Test
  public void testMeasurementType() {
    MeasurementType type = InstanceFactory.getMeasurementType();
    test.putMeasurementType(type);
    MeasurementTypeList list = test.getMeasurementTypes();
    assertTrue(list.getMeasurementTypes().contains(type));
    try {
      MeasurementType ret = test.getMeasurementType(type.getId());
      assertEquals(type, ret);
      test.deleteMeasurementType(ret);
      try {
        ret = test.getMeasurementType(type.getId());
        assertNull(ret);
      }
      catch (IdNotFoundException e) {
        // this is what we want.
      }
    }
    catch (IdNotFoundException e) {
      fail("Should have " + type);
    }
  }

  /**
   * Test method for Sensors.
   */
  @Test
  public void testSensor() {
    Sensor sensor = InstanceFactory.getSensor();
    test.putSensor(sensor);
    SensorList list = test.getSensors();
    assertTrue(list.getSensors().contains(sensor));
    try {
      Sensor ret = test.getSensor(sensor.getId());
      assertEquals(sensor, ret);
      test.deleteSensor(ret);
      try {
        ret = test.getSensor(sensor.getId());
        assertNull(ret);
      }
      catch (IdNotFoundException e) {
        // this is what we want.
      }
    }
    catch (IdNotFoundException e) {
      fail("Should have " + sensor);
    }
  }

  /**
   * Test method for SensorGroups.
   */
  @Test
  public void testSensorGroup() {
    SensorGroup group = InstanceFactory.getSensorGroup();
    test.putSensorGroup(group);
    SensorGroupList list = test.getSensorGroups();
    assertTrue(list.getGroups().contains(group));
    try {
      SensorGroup ret = test.getSensorGroup(group.getId());
      assertEquals(group, ret);
      test.deleteSensorGroup(ret);
      try {
        ret = test.getSensorGroup(group.getId());
        assertNull(ret);
      }
      catch (IdNotFoundException e) {
        // this is what we want.
      }
    }
    catch (IdNotFoundException e) {
      fail("Should have " + group);
    }
  }

  /**
   * Test method for SensorModels.
   */
  @Test
  public void testSensorModel() {
    SensorModel model = InstanceFactory.getSensorModel();
    test.putSensorModel(model);
    SensorModelList list = test.getSensorModels();
    assertTrue(list.getModels().contains(model));
    try {
      SensorModel ret = test.getSensorModel(model.getId());
      assertEquals(model, ret);
      test.deleteSensorModel(model);
      try {
        ret = test.getSensorModel(model.getId());
        assertNull(ret);
      }
      catch (IdNotFoundException e) {
        // this is what we want.
      }
    }
    catch (IdNotFoundException e) {
      fail("Should have " + model);
    }
  }

  /**
   * Test method for CollectorMetaDataes.
   */
  @Test
  public void testCollectorMetaData() {
    CollectorMetaData data = InstanceFactory.getCollectorMetaData();
    test.putCollectorMetaData(data);
    CollectorMetaDataList list = test.getCollectorMetaDatas();
    assertTrue(list.getDatas().contains(data));
    try {
      CollectorMetaData ret = test.getCollectorMetaData(data.getId());
      assertEquals(data, ret);
      test.deleteCollectorMetaData(data);
      try {
        ret = test.getCollectorMetaData(data.getId());
        assertNull(ret);
      }
      catch (IdNotFoundException e) {
        // this is what we want.
      }
    }
    catch (IdNotFoundException e) {
      fail("Should have " + data);
    }
  }

  /**
   * Test method for Measurements.
   */
  @Test
  public void testMeasurements() {
    Depository depo = InstanceFactory.getDepository();
    test.putDepository(depo);
    Measurement m1 = InstanceFactory.getMeasurementOne();
    try {
      test.putMeasurement(depo, m1);
      m1 = InstanceFactory.getMeasurementThree();
      test.putMeasurement(depo, m1);
      m1 = InstanceFactory.getMeasurementTwo();
      Double val = test.getValue(depo, m1.getSensor(), m1.getDate());
      assertTrue("Got " + val + " was expecting " + m1.getValue(), m1.getValue().equals(val));
    }
    catch (MeasurementTypeException e) {
      fail(e.getMessage());
    }
    catch (NoMeasurementException e) {
      fail(e.getMessage());
    }

  }

}
