/**
 * TestWattDepotImpl.java This file is part of WattDepot.
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
package org.wattdepot.server.depository.impl.hibernate;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.hibernate.exception.ConstraintViolationException;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.wattdepot.common.domainmodel.CollectorProcessDefinition;
import org.wattdepot.common.domainmodel.Depository;
import org.wattdepot.common.domainmodel.InstanceFactory;
import org.wattdepot.common.domainmodel.MeasurementType;
import org.wattdepot.common.domainmodel.Organization;
import org.wattdepot.common.domainmodel.Sensor;
import org.wattdepot.common.domainmodel.SensorLocation;
import org.wattdepot.common.domainmodel.SensorModel;
import org.wattdepot.common.domainmodel.UserInfo;
import org.wattdepot.common.domainmodel.UserPassword;
import org.wattdepot.common.exception.IdNotFoundException;
import org.wattdepot.common.exception.MisMatchedOwnerException;
import org.wattdepot.common.exception.UniqueIdException;
import org.wattdepot.server.ServerProperties;

/**
 * TestWattDepotImpl - Test cases for the WattDepotImpl.
 * 
 * @author Cam Moore
 * 
 */
public class TestWattDepotPersistenceImpl {

  private static WattDepotPersistenceImpl impl;
  private static UserInfo testUser;
  private static UserPassword testPassword;
  private static Organization testOrg;

  /**
   * Sets up the WattDepotPersistenceImpl using test properties.
   */
  @BeforeClass
  public static void setupImpl() {
    ServerProperties properties = new ServerProperties();
    properties.setTestProperties();
    impl = new WattDepotPersistenceImpl(properties);
    testUser = InstanceFactory.getUserInfo();
    testPassword = InstanceFactory.getUserPassword();
    testOrg = InstanceFactory.getOrganization();
    // shouldn't have to do this.
    testUser.setOrganizationId(testOrg.getSlug());
  }

  /**
   * Sets up the test case by ensuring we have test user, password and
   * organization.
   * 
   * @throws UniqueIdException
   *           if there is a big problem with the database.
   */
  @Before
  public void setUp() throws UniqueIdException {
    Organization testO = impl.getOrganization(testOrg.getSlug());
    if (testO == null) {
      impl.defineOrganization(testOrg.getSlug(), testOrg.getUsers());
    }
    UserInfo testU = impl.getUser(testUser.getId());
    if (testU == null) {
      impl.defineUserInfo(testUser.getId(), testUser.getFirstName(),
          testUser.getLastName(), testUser.getEmail(),
          testUser.getOrganizationId(), testUser.getProperties());
    }
    UserPassword testP = impl.getUserPassword(testPassword.getId());
    if (testP == null) {
      impl.defineUserPassword(testPassword.getId(), testPassword.getPlainText());
    }
  }

  /**
   * Cleans up the test user, password, and organization.
   * 
   * @throws java.lang.Exception
   *           if there is a problem.
   */
  @After
  public void tearDown() throws Exception {
    UserPassword testP = impl.getUserPassword(testPassword.getId());
    if (testP != null) {
      impl.deleteUserPassword(testP.getId());
    }
    UserInfo testU = impl.getUser(testUser.getId());
    if (testU != null) {
      impl.deleteUser(testU.getId());
    }
    Organization testO = impl.getOrganization(testOrg.getSlug());
    if (testO != null) {
      impl.deleteOrganization(testO.getSlug());
    }
  }

  /**
   * Test methods for dealing with CollectorProcessDefinitions.
   * 
   */
  @Test
  public void testCollectorProcessDefinitions() {
    // get the list of CollectorProcessDefinitions
    List<CollectorProcessDefinition> list = impl
        .getCollectorProcessDefinitions(testOrg.getSlug());
    int numCollector = list.size();
    assertTrue(numCollector >= 0);
    // Create a new instance
    CollectorProcessDefinition cpd = InstanceFactory
        .getCollectorProcessDefinition();
    try {
      impl.defineCollectorProcessDefinition(cpd.getName(), cpd.getSensorId(),
          cpd.getPollingInterval(), cpd.getDepositoryId(), cpd.getOwnerId());
    }
    catch (UniqueIdException e) {
      fail(cpd.getSlug() + " should not exist in persistence.");
    }
    catch (MisMatchedOwnerException e) {
      fail(cpd.getSlug() + " should be owned by " + cpd.getOwnerId() + ".");
    }
    catch (IdNotFoundException e) {
      // This is expected.
      try {
        addSensor();
      }
      catch (MisMatchedOwnerException e1) {
        e1.printStackTrace();
        fail("shouldn't happen");
      }
      catch (UniqueIdException e1) {
        e1.printStackTrace();
        fail("shouldn't happen");
      }
      catch (IdNotFoundException e1) {
        e1.printStackTrace();
        fail("shouldn't happen");
      }
    }
    try {
      impl.defineCollectorProcessDefinition(cpd.getName(), cpd.getSensorId(),
          cpd.getPollingInterval(), cpd.getDepositoryId(), cpd.getOwnerId());
    }
    catch (UniqueIdException e) {
      e.printStackTrace();
      fail("shouldn't happen");
    }
    catch (MisMatchedOwnerException e) {
      e.printStackTrace();
      fail("shouldn't happen");
    }
    catch (IdNotFoundException e) {
      e.printStackTrace();
      fail("shouldn't happen");
    }
    list = impl.getCollectorProcessDefinitions(testOrg.getSlug());
    assertTrue(numCollector + 1 == list.size());
    try {
      CollectorProcessDefinition defined = impl.getCollectorProcessDefinition(
          cpd.getSlug(), cpd.getOwnerId());
      assertNotNull(defined);
      assertTrue(defined.equals(cpd));
      assertTrue(defined.toString().equals(cpd.toString()));
      defined.setName("New Name");
      // Update the instance
      impl.updateCollectorProcessDefinition(defined);
      CollectorProcessDefinition updated = impl.getCollectorProcessDefinition(
          cpd.getSlug(), cpd.getOwnerId());
      assertNotNull(updated);
      assertTrue(defined.equals(updated));
      assertFalse(updated.equals(cpd));
      assertTrue(defined.hashCode() == updated.hashCode());
      assertFalse(updated.hashCode() == cpd.hashCode());
    }
    catch (MisMatchedOwnerException e) {
      e.printStackTrace();
      fail("should not happen");
    }
    // Delete the instance
    try {
      impl.deleteCollectorProcessDefinition(cpd.getSlug(), cpd.getOwnerId());
      list = impl.getCollectorProcessDefinitions(testOrg.getSlug());
      assertTrue(numCollector == list.size());
    }
    catch (IdNotFoundException e) {
      e.printStackTrace();
      fail("should not happen");
    }
    catch (MisMatchedOwnerException e) {
      e.printStackTrace();
      fail("should not happen");
    }
  }

  /**
   * Test methods for dealing with Depositories.
   */
  @Test
  public void testDepositories() {
    // get the depositories.
    List<Depository> list = impl.getWattDepositories(testOrg.getSlug());
    int numDepository = list.size();
    assertTrue(numDepository >= 0);
    // Create a new instance
    Depository dep = InstanceFactory.getDepository();
    try {
      impl.defineWattDepository(dep.getName(), dep.getMeasurementType(), dep.getOwnerId());
    }
    catch (UniqueIdException e) {
      e.printStackTrace();
      fail("should not happen");
    }
    catch (ConstraintViolationException e) {
      // This is expected since we haven't defined the measurement type.
      try {
        addMeasurementType();
      }
      catch (UniqueIdException e1) {
        e1.printStackTrace();
        fail("should not happen");
      }
    }
    try {
      impl.defineWattDepository(dep.getName(), dep.getMeasurementType(), dep.getOwnerId());
      list = impl.getWattDepositories(testOrg.getSlug());
      assertTrue(numDepository + 1 == list.size());
    }
    catch (UniqueIdException e) {
      e.printStackTrace();
      fail("should not happen");
    }
    try {
      Depository defined = impl.getWattDeposiory(dep.getSlug(), dep.getOwnerId());
      assertNotNull(defined);
      System.out.println(dep);
      System.out.println(defined);      
      assertTrue(defined.equals(dep));
      assertTrue(defined.toString().equals(dep.toString()));
      assertTrue(defined.hashCode() == dep.hashCode());
    }
    catch (MisMatchedOwnerException e) {
      e.printStackTrace();
      fail("should not happen");
    }
    try {
      impl.deleteWattDepository(dep.getSlug(), dep.getOwnerId());
    }
    catch (IdNotFoundException e) {
      e.printStackTrace();
      fail("should not happen");
    }
    catch (MisMatchedOwnerException e) {
      e.printStackTrace();
      fail("should not happen");
    }
    list = impl.getWattDepositories(testOrg.getSlug());
    assertTrue(numDepository == list.size());
  }

  /**
   * Test methods for dealing with SensorLocations.
   */
  @Test
  public void testSensorLocations() {
  }

  /**
   * Test methods for dealing with MeasurementTypes.
   */
  @Test
  public void testMeasurementTypes() {
  }

  /**
   * Test methods for dealing with Organizations.
   */
  @Test
  public void testOrganizations() {
  }

  /**
   * Test methods for dealing with Sensors.
   */
  @Test
  public void testSensors() {
  }

  /**
   * Test methods for dealing with SensorGroups.
   */
  @Test
  public void testSensorGroups() {
  }

  /**
   * Test methods for dealing with SensorModels.
   */
  @Test
  public void testSensorModels() {
  }

  /**
   * Test method for dealing with UserInfos.
   */
  @Test
  public void testUsers() {
  }

  /**
   * Test methods for dealing with UserPasswords.
   */
  @Test
  public void testUserPasswords() {
  }

  /**
   * @throws MisMatchedOwnerException
   *           if there is a mismatch in the ownership.
   * @throws UniqueIdException
   *           if the Location is already defined.
   */
  private void addSensorLocation() throws MisMatchedOwnerException,
      UniqueIdException {
    SensorLocation loc = InstanceFactory.getLocation();
    SensorLocation defined = impl.getLocation(loc.getSlug(), loc.getOwnerId());
    if (defined == null) {
      impl.defineLocation(loc.getSlug(), loc.getLatitude(), loc.getLongitude(),
          loc.getAltitude(), loc.getDescription(), loc.getOwnerId());
    }
  }

  /**
   * @throws UniqueIdException
   *           if the Model is already defined.
   */
  private void addSensorModel() throws UniqueIdException {
    SensorModel model = InstanceFactory.getSensorModel();
    SensorModel defined = impl.getSensorModel(model.getSlug());
    if (defined == null) {
      impl.defineSensorModel(model.getSlug(), model.getProtocol(),
          model.getType(), model.getVersion());
    }
  }

  /**
   * @throws MisMatchedOwnerException
   *           if there is a mismatch in the ownership.
   * @throws UniqueIdException
   *           if the Sensor is already defined.
   * @throws IdNotFoundException
   *           if the SensorLocation or SensorModel aren't defined.
   */
  private void addSensor() throws MisMatchedOwnerException, UniqueIdException,
      IdNotFoundException {
    addSensorLocation();
    addSensorModel();
    Sensor sensor = InstanceFactory.getSensor();
    Sensor defined = impl.getSensor(sensor.getSlug(), sensor.getOwnerId());
    if (defined == null) {
      impl.defineSensor(sensor.getName(), sensor.getUri(),
          sensor.getSensorLocationId(), sensor.getModelId(),
          sensor.getOwnerId());
    }
  }
  
  /**
   * @throws UniqueIdException if the MeasurementType is already defined.
   */
  private void addMeasurementType() throws UniqueIdException {
    MeasurementType type = InstanceFactory.getMeasurementType();
    MeasurementType defined = impl.getMeasurementType(type.getSlug());
    if (defined == null) {
      impl.defineMeasurementType(type.getName(), type.getUnits());
    }
  }
}
