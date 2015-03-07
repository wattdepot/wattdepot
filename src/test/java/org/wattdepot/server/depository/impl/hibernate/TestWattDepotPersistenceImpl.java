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

import java.util.HashSet;
import java.util.List;

import org.junit.*;
import org.wattdepot.common.domainmodel.CollectorProcessDefinition;
import org.wattdepot.common.domainmodel.Depository;
import org.wattdepot.common.domainmodel.MeasurementPruningDefinition;
import org.wattdepot.common.domainmodel.InstanceFactory;
import org.wattdepot.common.domainmodel.InterpolatedValue;
import org.wattdepot.common.domainmodel.Measurement;
import org.wattdepot.common.domainmodel.MeasurementType;
import org.wattdepot.common.domainmodel.Organization;
import org.wattdepot.common.domainmodel.Property;
import org.wattdepot.common.domainmodel.Sensor;
import org.wattdepot.common.domainmodel.SensorGroup;
import org.wattdepot.common.domainmodel.SensorModel;
import org.wattdepot.common.domainmodel.UserInfo;
import org.wattdepot.common.domainmodel.UserPassword;
import org.wattdepot.common.exception.BadSlugException;
import org.wattdepot.common.exception.IdNotFoundException;
import org.wattdepot.common.exception.MeasurementGapException;
import org.wattdepot.common.exception.MeasurementTypeException;
import org.wattdepot.common.exception.MisMatchedOwnerException;
import org.wattdepot.common.exception.NoMeasurementException;
import org.wattdepot.common.exception.UniqueIdException;
import org.wattdepot.common.util.UnitsHelper;
import org.wattdepot.server.ServerProperties;
import org.wattdepot.server.StrongAES;

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
  public static void setupImpl() throws Exception {
    ServerProperties properties = new ServerProperties();
    properties.setTestProperties();
    impl = new WattDepotPersistenceImpl(properties);
    testUser = InstanceFactory.getUserInfo();
    testPassword = InstanceFactory.getUserPassword();
    testOrg = InstanceFactory.getOrganization();
    // shouldn't have to do this.
    testUser.setOrganizationId(testOrg.getId());
    // prime the MeasurementTypes
    UnitsHelper.quantities.containsKey("W");
    // 1. define the organization w/o any users.
    try {
      impl.getOrganization(testOrg.getId(), true);
    }
    catch (IdNotFoundException e) {
      try {
        impl.defineOrganization(testOrg.getId(), testOrg.getName(), new HashSet<String>());
      }
      catch (BadSlugException e1) {
        e1.printStackTrace();
      }
      catch (IdNotFoundException e1) {
        e1.printStackTrace();
      }
      catch (UniqueIdException e1) {
        e1.printStackTrace();
      }
    }
    // 2. define the user
    try {
      impl.getUser(testUser.getUid(), testUser.getOrganizationId(), true);
    }
    catch (IdNotFoundException e) {
      try {
        impl.defineUserInfo(testUser.getUid(), testUser.getFirstName(), testUser.getLastName(),
            testUser.getEmail(), testUser.getOrganizationId(), testUser.getProperties(),
            testUser.getPassword());
      }
      catch (IdNotFoundException e1) {
        // Shouldn't happen!
        e1.printStackTrace();
      }
      catch (UniqueIdException e1) {
        e1.printStackTrace();
      }
    }
    // 3. update the organization
    try {
      impl.updateOrganization(testOrg);
    }
    catch (IdNotFoundException e1) {
      // this shouldn't happen
      e1.printStackTrace();
    }
  }

  /**
   * Cleans up the test user, password, and organization.
   */
  @AfterClass
  public static void tearDown() {
    Organization testO;
    try {
      testO = impl.getOrganization(testOrg.getId(), true);
      if (testO != null) {
        impl.deleteOrganization(testO.getId());
      }
    }
    catch (IdNotFoundException e) {
      e.printStackTrace();
    }
    try {
      impl.deleteOrganization(InstanceFactory.getUserInfo2().getOrganizationId());
    }
    catch (IdNotFoundException e1) { // NOPMD
      // not a problem
    }
    try {
      impl.deleteOrganization(InstanceFactory.getUserInfo3().getOrganizationId());
    }
    catch (IdNotFoundException e1) { // NOPMD
      // not a problem
    }
    UserPassword testP;
    try {
      testP = impl.getUserPassword(testPassword.getUid(), testPassword.getOrganizationId(), true);
      if (testP != null) {
        impl.deleteUserPassword(testP.getUid(), testPassword.getOrganizationId());
      }
    }
    catch (IdNotFoundException e) { // NOPMD
      // e.printStackTrace();
    }
    UserInfo testU;
    try {
      testU = impl.getUser(testUser.getUid(), testUser.getOrganizationId(), true);
      if (testU != null) {
        impl.deleteUser(testU.getUid(), testU.getOrganizationId());
      }
    }
    catch (IdNotFoundException e) { // NOPMD
      // e.printStackTrace();
    }
  }

  /**
   * Test methods for dealing with CollectorProcessDefinitions.
   */
  @Test
  public void testCollectorProcessDefinitions() {
    // get the list of CollectorProcessDefinitions
    List<CollectorProcessDefinition> list = null;
    try {
      list = impl.getCollectorProcessDefinitions(testOrg.getId(), true);
    }
    catch (IdNotFoundException e3) {
      fail(e3.getMessage() + " should not happen");
    }
    int numCollector = list.size();
    assertTrue(numCollector >= 0);
    // Create a new instance
    CollectorProcessDefinition cpd = InstanceFactory.getCollectorProcessDefinition();
    try {
      impl.defineCollectorProcessDefinition(cpd.getSensorId(), cpd.getName(), cpd.getSensorId(),
          cpd.getPollingInterval(), cpd.getDepositoryId(), cpd.getProperties(),
          cpd.getOrganizationId());
    }
    catch (UniqueIdException e) {
      fail(cpd.getId() + " should not exist in persistence.");
    }
    catch (MisMatchedOwnerException e) {
      fail(cpd.getId() + " should be owned by " + cpd.getOrganizationId() + ".");
    }
    catch (BadSlugException e) {
      fail(cpd.getId() + " is valid.");
    }
    catch (IdNotFoundException e) {
      // This is expected.
      try {
        addSensor();
        addMeasurementType();
        addDepository();
      }
      catch (MisMatchedOwnerException e1) {
        e1.printStackTrace();
        fail(e1.getMessage() + " shouldn't happen");
      }
      catch (UniqueIdException e1) {
        e1.printStackTrace();
        fail(e1.getMessage() + " shouldn't happen");
      }
      catch (IdNotFoundException e1) {
        e1.printStackTrace();
        fail(e1.getMessage() + " shouldn't happen");
      }
    }
    try {
      impl.defineCollectorProcessDefinition(cpd.getId(), cpd.getName(), cpd.getSensorId(),
          cpd.getPollingInterval(), cpd.getDepositoryId(), cpd.getProperties(),
          cpd.getOrganizationId());
    }
    catch (UniqueIdException e) {
      e.printStackTrace();
      fail(e.getMessage() + " shouldn't happen");
    }
    catch (MisMatchedOwnerException e) {
      e.printStackTrace();
      fail(e.getMessage() + " shouldn't happen");
    }
    catch (IdNotFoundException e) {
      e.printStackTrace();
      fail(e.getMessage() + " shouldn't happen");
    }
    catch (BadSlugException e) {
      e.printStackTrace();
      fail(e.getMessage() + " shouldn't happen");
    }
    // try to define it again.
    try {
      impl.defineCollectorProcessDefinition(cpd.getId(), cpd.getName(), cpd.getSensorId(),
          cpd.getPollingInterval(), cpd.getDepositoryId(), cpd.getProperties(),
          cpd.getOrganizationId());
    }
    catch (UniqueIdException e2) {
      // expected result
    }
    catch (MisMatchedOwnerException e2) {
      e2.printStackTrace();
      fail(e2.getMessage() + " shouldn't happen");
    }
    catch (IdNotFoundException e2) {
      e2.printStackTrace();
      fail(e2.getMessage() + " shouldn't happen");
    }
    catch (BadSlugException e) {
      e.printStackTrace();
      fail(e.getMessage() + " shouldn't happen");
    }

    try {
      list = impl.getCollectorProcessDefinitions(testOrg.getId(), true);
    }
    catch (IdNotFoundException e2) {
      fail(e2.getMessage() + " should not happen");
    }
    assertTrue(numCollector + 1 == list.size());
    List<String> ids = null;
    try {
      ids = impl.getCollectorProcessDefinitionIds(testOrg.getId(), true);
    }
    catch (IdNotFoundException e2) {
      fail(e2.getMessage() + " should not happen");
    }
    assertTrue(list.size() == ids.size());
    try {
      CollectorProcessDefinition defined = impl.getCollectorProcessDefinition(cpd.getId(),
          cpd.getOrganizationId(), true);
      assertNotNull(defined);
      assertTrue(defined.equals(cpd));
      assertTrue(defined.toString().equals(cpd.toString()));
      defined.setName("New Name");
      // Update the instance
      impl.updateCollectorProcessDefinition(defined);
      CollectorProcessDefinition updated = impl.getCollectorProcessDefinition(cpd.getId(),
          cpd.getOrganizationId(), true);
      assertNotNull(updated);
      assertTrue(defined.equals(updated));
      assertFalse(updated.equals(cpd));
      assertTrue(defined.hashCode() == updated.hashCode());
      assertFalse(updated.hashCode() == cpd.hashCode());
    }
    catch (IdNotFoundException e) {
      e.printStackTrace();
      fail("should not happen");
    }
    // Delete the instance
    try {
      impl.deleteCollectorProcessDefinition("bogus-collector-3921", cpd.getOrganizationId());
      fail("should be able to delete bogus CollectorProcessDefinition.");
    }
    catch (IdNotFoundException e1) {
      // expected.
    }
    catch (MisMatchedOwnerException e1) {
      e1.printStackTrace();
      fail("should not happen");
    }
    try {
      impl.deleteCollectorProcessDefinition(cpd.getId(), cpd.getOrganizationId());
      list = impl.getCollectorProcessDefinitions(testOrg.getId(), true);
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
    try {
      deleteSensor();
    }
    catch (MisMatchedOwnerException e) {
      e.printStackTrace();
      fail("should not happen");
    }
    catch (IdNotFoundException e) {
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
    List<Depository> list = null;
    try {
      list = impl.getDepositories(testOrg.getId(), true);
    }
    catch (IdNotFoundException e3) {
      fail(e3.getMessage() + " should not happen");
    }
    int numDepository = list.size();
    assertTrue(numDepository >= 0);
    // Create a new instance
    Depository dep = InstanceFactory.getDepository2();
    try {
      impl.defineDepository(dep.getId(), dep.getName(), dep.getMeasurementType(),
          dep.getOrganizationId());
    }
    catch (BadSlugException e) {
      e.printStackTrace();
      fail(e.getMessage() + " shouldn't happen");
    }
    catch (UniqueIdException e) {
      e.printStackTrace();
      fail("should not happen");
    }
    catch (IdNotFoundException e) {
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
      impl.defineDepository(dep.getId(), dep.getName(), dep.getMeasurementType(),
          dep.getOrganizationId());
      list = impl.getDepositories(testOrg.getId(), true);
      assertTrue(numDepository + 1 == list.size());
      List<String> ids = impl.getDepositoryIds(testOrg.getId(), true);
      assertTrue(list.size() == ids.size());
    }
    catch (BadSlugException e) {
      e.printStackTrace();
      fail(e.getMessage() + " shouldn't happen");
    }
    catch (UniqueIdException e) {
      e.printStackTrace();
      fail(e.getMessage() + " should not happen");
    }
    catch (IdNotFoundException e) {
      e.printStackTrace();
      fail(e.getMessage() + " should not happen");
    }
    // try to define it again.
    try {
      impl.defineDepository(dep.getId(), dep.getName(), dep.getMeasurementType(),
          dep.getOrganizationId());
      fail("Should not be able to define second depository with same name.");
    }
    catch (UniqueIdException e2) {
      // expected result
    }
    catch (BadSlugException e) {
      e.printStackTrace();
      fail(e.getMessage() + " shouldn't happen");
    }
    catch (IdNotFoundException e) {
      e.printStackTrace();
      fail(e.getMessage() + " should not happen");
    }
    try {
      Depository defined = impl.getDepository(dep.getId(), dep.getOrganizationId(), true);
      assertNotNull(defined);
      assertTrue(defined.equals(dep));
      assertTrue(defined.toString().equals(dep.toString()));
      Depository copy = impl.getDepository(dep.getId(), dep.getOrganizationId(), true);
      assertTrue(copy.equals(defined));
    }
    catch (IdNotFoundException e) {
      e.printStackTrace();
      fail("should not happen");
    }
    try {
      impl.deleteDepository("bogus-depository-3258", dep.getOrganizationId());
      fail("Shouldn't be able to delete bogus-depository-3258");
    }
    catch (IdNotFoundException e1) {
      // this is expected
    }
    catch (MisMatchedOwnerException e1) {
      e1.printStackTrace();
      fail("should not happen");
    }
    try {
      impl.deleteDepository(dep.getId(), dep.getOrganizationId());
    }
    catch (IdNotFoundException e) {
      e.printStackTrace();
      fail("should not happen");
    }
    catch (MisMatchedOwnerException e) {
      e.printStackTrace();
      fail("should not happen");
    }
    try {
      list = impl.getDepositories(testOrg.getId(), true);
    }
    catch (IdNotFoundException e1) {
      fail(e1.getMessage() + " should not happen");
    }
    assertTrue(numDepository == list.size());
    try {
      deleteMeasurementType();
    }
    catch (IdNotFoundException e) {
      e.printStackTrace();
      fail("should not happen");
    }
    try {
      deleteMeasurementType();
    }
    catch (IdNotFoundException e) { // NOPMD
      // not a problem.
    }
  }

  /**
   * Tests for MeasurementPruningDefinitions.
   */
  @Test
  public void testMeasurementPruningDefinitions() {
    // get the list of MeasurementPruningDefinitions
    List<MeasurementPruningDefinition> list = null;
    try {
      list = impl.getMeasurementPruningDefinitions(testOrg.getId(), true);
    }
    catch (IdNotFoundException e3) {
      fail(e3.getMessage() + " should not happen");
    }
    int numCollector = list.size();
    assertTrue(numCollector >= 0);
    // Create a new instance
    MeasurementPruningDefinition gcd = InstanceFactory.getMeasurementPruningDefinition();
    try {
      impl.defineMeasurementPruningDefinition(gcd.getId(), gcd.getName(), gcd.getDepositoryId(),
          gcd.getSensorId(), gcd.getOrganizationId(), gcd.getIgnoreWindowDays(),
          gcd.getCollectWindowDays(), gcd.getMinGapSeconds());
    }
    catch (UniqueIdException e) {
      fail(gcd.getId() + " should not exist in persistence.");
    }
    catch (BadSlugException e) {
      fail(gcd.getId() + " is valid.");
    }
    catch (IdNotFoundException e) {
      // This is expected.
      try {
        addSensor();
        addMeasurementType();
        addDepository();
      }
      catch (MisMatchedOwnerException e1) {
        e1.printStackTrace();
        fail(e1.getMessage() + " shouldn't happen");
      }
      catch (UniqueIdException e1) {
        e1.printStackTrace();
        fail(e1.getMessage() + " shouldn't happen");
      }
      catch (IdNotFoundException e1) {
        e1.printStackTrace();
        fail(e1.getMessage() + " shouldn't happen");
      }
    }
    try {
      impl.defineMeasurementPruningDefinition(gcd.getId(), gcd.getName(), gcd.getDepositoryId(),
          gcd.getSensorId(), gcd.getOrganizationId(), gcd.getIgnoreWindowDays(),
          gcd.getCollectWindowDays(), gcd.getMinGapSeconds());
    }
    catch (UniqueIdException e) {
      e.printStackTrace();
      fail(e.getMessage() + " shouldn't happen");
    }
    catch (IdNotFoundException e) {
      e.printStackTrace();
      fail(e.getMessage() + " shouldn't happen");
    }
    catch (BadSlugException e) {
      e.printStackTrace();
      fail(e.getMessage() + " shouldn't happen");
    }
    // try to define it again.
    try {
      impl.defineMeasurementPruningDefinition(gcd.getId(), gcd.getName(), gcd.getDepositoryId(),
          gcd.getSensorId(), gcd.getOrganizationId(), gcd.getIgnoreWindowDays(),
          gcd.getCollectWindowDays(), gcd.getMinGapSeconds());
    }
    catch (UniqueIdException e2) {
      // expected result
    }
    catch (IdNotFoundException e2) {
      e2.printStackTrace();
      fail(e2.getMessage() + " shouldn't happen");
    }
    catch (BadSlugException e) {
      e.printStackTrace();
      fail(e.getMessage() + " shouldn't happen");
    }

    try {
      list = impl.getMeasurementPruningDefinitions(testOrg.getId(), true);
    }
    catch (IdNotFoundException e2) {
      fail(e2.getMessage() + " should not happen");
    }
    assertTrue(numCollector + 1 == list.size());
    List<String> ids = null;
    try {
      ids = impl.getMeasurementPruningDefinitionIds(testOrg.getId(), true);
    }
    catch (IdNotFoundException e2) {
      fail(e2.getMessage() + " should not happen");
    }
    assertTrue(list.size() == ids.size());
    try {
      MeasurementPruningDefinition defined = impl.getMeasurementPruningDefinition(gcd.getId(),
          gcd.getOrganizationId(), true);
      assertNotNull(defined);
      assertTrue(defined.equals(gcd));
//      System.out.println(defined.toString());
//      System.out.println(gcd.toString());
//      assertTrue(defined.toString().equals(gcd.toString()));
      defined.setName("New Name");
      // Update the instance
      impl.updateMeasurementPruningDefinition(defined);
      MeasurementPruningDefinition updated = impl.getMeasurementPruningDefinition(gcd.getId(),
          gcd.getOrganizationId(), true);
      assertNotNull(updated);
      assertTrue(defined.equals(updated));
      assertFalse(updated.equals(gcd));
      assertTrue(defined.hashCode() == updated.hashCode());
      assertFalse(updated.hashCode() == gcd.hashCode());
    }
    catch (IdNotFoundException e) {
      e.printStackTrace();
      fail("should not happen");
    }
    // Delete the instance
    try {
      impl.deleteMeasurementPruningDefinition("bogus-collector-3921", gcd.getOrganizationId());
      fail("should be able to delete bogus MeasurementPruningDefinition.");
    }
    catch (IdNotFoundException e1) {
      // expected.
    }
    try {
      impl.deleteMeasurementPruningDefinition(gcd.getId(), gcd.getOrganizationId());
      list = impl.getMeasurementPruningDefinitions(testOrg.getId(), true);
      assertTrue(numCollector == list.size());
    }
    catch (IdNotFoundException e) {
      e.printStackTrace();
      fail("should not happen");
    }
    try {
      deleteSensor();
    }
    catch (MisMatchedOwnerException e) {
      e.printStackTrace();
      fail("should not happen");
    }
    catch (IdNotFoundException e) {
      e.printStackTrace();
      fail("should not happen");
    }
  }

  /**
   * Tests for dealing with Measurements.
   */
  @Test
  public void testMeasurements() {
    try {
      addMeasurementType();
    }
    catch (UniqueIdException e1) {
      e1.printStackTrace();
      fail("should not happen");
    }
    // create a depository to store the measurements.
    Depository dep = InstanceFactory.getDepository();
    String sensorId = null;
    try {
      try {
        impl.defineDepository(dep.getId(), dep.getName(), dep.getMeasurementType(),
            dep.getOrganizationId());
      }
      catch (UniqueIdException uie) {
        // it is already in.
      }
      Depository d = impl.getDepository(dep.getId(), dep.getOrganizationId(), true);
      MeasurementType type = d.getMeasurementType();
      assertTrue(type.equals(InstanceFactory.getMeasurementType()));
      Measurement m1 = InstanceFactory.getMeasurementOne();
      Measurement m2 = InstanceFactory.getMeasurementTwo();
      Measurement m3 = InstanceFactory.getMeasurementThree();
      sensorId = m1.getSensorId();
      addSensor();
      impl.putMeasurement(dep.getId(), dep.getOrganizationId(), m1);
      impl.putMeasurement(dep.getId(), dep.getOrganizationId(), m2);
      impl.putMeasurement(dep.getId(), dep.getOrganizationId(), m3);
      Measurement defined = impl.getMeasurement(dep.getId(), dep.getOrganizationId(), m1.getId(),
          true);
      assertNotNull(defined);
      assertTrue(defined.equals(m1));
      assertTrue(defined.toString().equals(m1.toString()));
      // assertTrue(defined.hashCode() == m1.hashCode());
      List<Measurement> list = impl.getMeasurements(dep.getId(), dep.getOrganizationId(), sensorId,
          true);
      assertTrue(list.size() == 3);
      list = impl.getMeasurements(dep.getId(), dep.getOrganizationId(), sensorId,
          InstanceFactory.getTimeBetweenM1andM2(), InstanceFactory.getTimeBetweenM1andM3(), true);
      assertTrue(list.size() == 1);
      try {
        Double val = impl.getValue(dep.getId(), dep.getOrganizationId(), sensorId,
            InstanceFactory.getTimeBetweenM1andM2(), true);
        assertTrue(Math.abs(val - m1.getValue()) < 0.001);
        try {
          Double val2 = impl.getValue(dep.getId(), dep.getOrganizationId(), sensorId,
              InstanceFactory.getTimeBetweenM1andM2(), 700L, true);
          assertTrue(Math.abs(val - val2) < 0.001);
        }
        catch (MeasurementGapException e) {
          e.printStackTrace();
          fail(e.getMessage() + " should not happen");
        }
        try {
          Double val2 = impl.getValue(dep.getId(), dep.getOrganizationId(), sensorId, m2.getDate(),
              700L, true);
          assertTrue(Math.abs(val - val2) < 0.001);
        }
        catch (MeasurementGapException e) {
          e.printStackTrace();
          fail(e.getMessage() + " should not happen");
        }
        try {
          impl.getValue(dep.getId(), dep.getOrganizationId(), sensorId,
              InstanceFactory.getTimeBetweenM1andM2(), 7L, true);
          fail("Should detect gap.");
        }
        catch (MeasurementGapException e) {
          // expected
        }
        val = impl.getValue(dep.getId(), dep.getOrganizationId(), sensorId, m1.getDate(), true);
        assertTrue(Math.abs(val - m1.getValue()) < 0.0001);
      }
      catch (NoMeasurementException e) {
        e.printStackTrace();
        fail(e.getMessage() + " should not happen");
      }
      try {
        impl.getValue(dep.getId(), dep.getOrganizationId(), sensorId,
            InstanceFactory.getTimeBeforeM1(), true);
        fail("Should throw NoMeasurementException.");
      }
      catch (NoMeasurementException e) {
        // expected
      }
      try {
        impl.getValue(dep.getId(), dep.getOrganizationId(), sensorId,
            InstanceFactory.getTimeAfterM3(), true);
        fail("Should throw NoMeasurementException.");
      }
      catch (NoMeasurementException e) {
        // expected
      }
      try {
        impl.getValue(dep.getId(), dep.getOrganizationId(), sensorId,
            InstanceFactory.getTimeBeforeM1(), 700L, true);
        fail("Should throw NoMeasurementException.");
      }
      catch (NoMeasurementException e) {
        // expected
      }
      catch (MeasurementGapException e) {
        e.printStackTrace();
        fail(e.getMessage() + " should not happen");
      }
      try {
        impl.getValue(dep.getId(), dep.getOrganizationId(), sensorId,
            InstanceFactory.getTimeAfterM3(), 700L, true);
        fail("Should throw NoMeasurementException.");
      }
      catch (NoMeasurementException e) {
        // expected
      }
      catch (MeasurementGapException e) {
        e.printStackTrace();
        fail(e.getMessage() + " should not happen");
      }
      try {
        Double val = impl.getValue(dep.getId(), dep.getOrganizationId(), sensorId,
            InstanceFactory.getTimeBetweenM1andM2(), InstanceFactory.getTimeBetweenM1andM3(), true);
        assertTrue(Math.abs(val - 0.0) < 0.001);
      }
      catch (NoMeasurementException e) {
        e.printStackTrace();
        fail(e.getMessage() + " shouldn't happen");
      }
      try {
        Double val = impl.getValue(dep.getId(), dep.getOrganizationId(), sensorId,
            InstanceFactory.getTimeBetweenM1andM2(), InstanceFactory.getTimeBetweenM1andM3(), 700L,
            true);
        assertTrue(Math.abs(val - 0.0) < 0.001);
      }
      catch (NoMeasurementException e) {
        e.printStackTrace();
        fail(e.getMessage() + " shouldn't happen");
      }
      catch (MeasurementGapException e) {
        e.printStackTrace();
        fail(e.getMessage() + " shouldn't happen");
      }
      try {
        InterpolatedValue earliest = impl.getEarliestMeasuredValue(dep.getId(),
            dep.getOrganizationId(), sensorId, true);
        assertNotNull(earliest);
        assertTrue(earliest.equivalent(m1));
        assertTrue(earliest.getSensorId().equals(m1.getSensorId()));
        assertTrue(earliest.getValue().equals(m1.getValue()));
        assertTrue(earliest.getMeasurementType().getUnits().equals(m1.getMeasurementType()));
        assertTrue(earliest.getStart().equals(m1.getDate()));
        InterpolatedValue latest = impl.getLatestMeasuredValue(dep.getId(),
            dep.getOrganizationId(), sensorId, true);
        assertNotNull(latest);
        assertTrue(latest.equivalent(m3));
        assertTrue(latest.getSensorId().equals(m3.getSensorId()));
      }
      catch (NoMeasurementException e) {
        e.printStackTrace();
        fail(e.getMessage() + " should not happen");
      }
      List<String> sensors = impl.listSensors(dep.getId(), dep.getOrganizationId(), true);
      assertNotNull(sensors);
      assertTrue(sensors.contains(sensorId));
      impl.deleteMeasurement(dep.getId(), dep.getOrganizationId(), m1.getId());
      impl.deleteMeasurement(dep.getId(), dep.getOrganizationId(), m2.getId());
      impl.deleteMeasurement(dep.getId(), dep.getOrganizationId(), m3.getId());
      impl.deleteDepository(dep.getId(), dep.getOrganizationId());
      deleteSensor();
    }
    catch (UniqueIdException e) {
      e.printStackTrace();
    }
    catch (MisMatchedOwnerException e) {
      e.printStackTrace();
    }
    catch (MeasurementTypeException e) {
      e.printStackTrace();
    }
    catch (IdNotFoundException e) {
      e.printStackTrace();
    }
    catch (BadSlugException e1) {
      e1.printStackTrace();
    }

    // Clean up

    try {
      deleteMeasurementType();
    }
    catch (IdNotFoundException e) {
      e.printStackTrace();
      fail("should not happen");
    }

  }

  /**
   * Test methods for dealing with MeasurementTypes.
   */
  @Test
  public void testMeasurementTypes() {
    List<MeasurementType> list = impl.getMeasurementTypes();
    int numMeasurementTypes = list.size();
    assertTrue(numMeasurementTypes >= 0);
    MeasurementType type = InstanceFactory.getMeasurementType2();
    // create a MeasurementType
    try {
      impl.defineMeasurementType(type.getId(), type.getName(), type.getUnits());
      list = impl.getMeasurementTypes();
      assertTrue(numMeasurementTypes + 1 == list.size());
    }
    catch (UniqueIdException e) {
      e.printStackTrace();
      fail("should not happen");
    }
    catch (BadSlugException e) {
      e.printStackTrace();
      fail("should not happen.");
    }
    MeasurementType defined = null;
    try {
      defined = impl.getMeasurementType(type.getId(), true);
    }
    catch (IdNotFoundException e1) {
      e1.printStackTrace();
      fail(e1.getMessage() + " should not happen");
    }
    assertNotNull(defined);
    assertTrue(type.equals(defined));
    assertTrue(defined.equals(type));
    assertTrue(defined.toString().equals(type.toString()));
    assertTrue(defined.hashCode() == type.hashCode());
    defined.setName("New Name");
    impl.updateMeasurementType(defined);
    list = impl.getMeasurementTypes();
    assertTrue(numMeasurementTypes + 1 == list.size());
    MeasurementType update = null;
    try {
      update = impl.getMeasurementType(type.getId(), true);
    }
    catch (IdNotFoundException e1) {
      e1.printStackTrace();
      fail(e1.getMessage() + " should not happen");
    }
    assertFalse(type.equals(update));
    // delete
    try {
      impl.deleteMeasurementType("bogus-measurementtype-9520");
      fail("shouldn't be able to delete bogus MeasurementType");
    }
    catch (IdNotFoundException e) {
      // expected result
    }
    try {
      impl.deleteMeasurementType(type.getId());
      list = impl.getMeasurementTypes();
      assertTrue(numMeasurementTypes == list.size());
    }
    catch (IdNotFoundException e) {
      e.printStackTrace();
      fail("should not happen");
    }
  }

  /**
   * Test methods for dealing with Organizations.
   */
  @Test
  public void testOrganizations() {
    // get the defined organizations
    List<Organization> list = impl.getOrganizations();
    int numOrgs = list.size();
    assertTrue(numOrgs >= 0);
    Organization org = InstanceFactory.getOrganization3();
    try {
      try {
        impl.defineOrganization(org.getId(), org.getName(), org.getUsers());
        fail("Shouldn't be able to define the org before the user.");
      }
      catch (IdNotFoundException e1) {
        // expected since user isn't defined
        impl.defineOrganization(org.getId(), org.getName(), new HashSet<String>());
        // no users
      }
      list = impl.getOrganizations();
      assertTrue(numOrgs + 1 == list.size());
      List<String> ids = impl.getOrganizationIds();
      assertTrue(list.size() == ids.size());
      Organization defined = impl.getOrganization(org.getId(), true);
      assertNotNull(defined);
      // assertTrue(defined.equals(org));
      // assertTrue(defined.toString().equals(org.toString()));
      // can't check hash codes since we are dealing with Organizations and
      // OrganizationImpls.
      // assertTrue(defined.hashCode() == org.hashCode());
      defined.setName("New Name");
      impl.updateOrganization(defined);
      list = impl.getOrganizations();
      assertTrue(numOrgs + 1 == list.size());
      Organization updated = impl.getOrganization(org.getId(), true);
      assertFalse(org.equals(updated));
      assertFalse(org.toString().equals(updated.toString()));
      assertFalse(org.hashCode() == updated.hashCode());
    }
    catch (UniqueIdException e) {
      e.printStackTrace();
      fail(e.getMessage() + " should not happen");
    }
    catch (IdNotFoundException e) {
      e.printStackTrace();
      fail(e.getMessage() + " should not happen.");
    }
    catch (BadSlugException e) {
      e.printStackTrace();
      fail("should not happen.");
    }
    try {
      impl.deleteOrganization("bogus-organization-9491");
    }
    catch (IdNotFoundException e) {
      // expected
    }
    try {
      impl.deleteOrganization(org.getId());
    }
    catch (IdNotFoundException e) {
      e.printStackTrace();
      fail("should not happen");
    }
  }

  /**
   * Test methods for dealing with Sensors.
   */
  @Test
  public void testSensors() {
    List<Sensor> list = null;
    try {
      list = impl.getSensors(testOrg.getId(), true);
    }
    catch (IdNotFoundException e2) {
      fail(e2.getMessage() + " should not happen");
    }
    int numSensors = list.size();
    assertTrue(numSensors >= 0);
    Sensor sens = InstanceFactory.getSensor();
    try {
      impl.defineSensor(sens.getId(), sens.getName(), sens.getUri(), sens.getModelId(),
          sens.getProperties(), sens.getOrganizationId());
    }
    catch (BadSlugException e) {
      e.printStackTrace();
      fail("should not happen.");
    }
    catch (UniqueIdException e) {
      e.printStackTrace();
      fail(e.getMessage() + " should not happen");
    }
    catch (MisMatchedOwnerException e) {
      e.printStackTrace();
      fail(e.getMessage() + " should not happen");
    }
    catch (IdNotFoundException e) {
      // expected result.
      try {
        addSensorModel();
      }
      catch (UniqueIdException e1) {
        e1.printStackTrace();
        fail(e1.getMessage() + " should not happen");
      }
    }
    try {
      impl.defineSensor(sens.getId(), sens.getName(), sens.getUri(), sens.getModelId(),
          sens.getProperties(), sens.getOrganizationId());
      list = impl.getSensors(testOrg.getId(), true);
      assertTrue(numSensors + 1 == list.size());
      List<String> ids = impl.getSensorIds(testOrg.getId(), true);
      assertTrue(list.size() == ids.size());
      Sensor defined = impl.getSensor(sens.getId(), sens.getOrganizationId(), true);
      assertNotNull(defined);
      assertTrue(defined.equals(sens));
      assertTrue(defined.toString().equals(sens.toString()));
      defined.setName("New Name");
      impl.updateSensor(defined);
      Sensor updated = impl.getSensor(sens.getId(), sens.getOrganizationId(), true);
      assertFalse(updated.equals(sens));
      assertFalse(updated.toString().equals(sens.toString()));
      list = impl.getSensors(testOrg.getId(), true);
      assertTrue(numSensors + 1 == list.size());
    }
    catch (BadSlugException e) {
      e.printStackTrace();
      fail("should not happen.");
    }
    catch (UniqueIdException e) {
      e.printStackTrace();
      fail(e.getMessage() + " should not happen");
    }
    catch (MisMatchedOwnerException e) {
      e.printStackTrace();
      fail(e.getMessage() + " should not happen");
    }
    catch (IdNotFoundException e) {
      e.printStackTrace();
      fail(e.getMessage() + " should not happen");
    }
    // try to define the same sensor a second time
    try {
      impl.defineSensor(sens.getId(), sens.getName(), sens.getUri(), sens.getModelId(),
          sens.getProperties(), sens.getOrganizationId());
    }
    catch (UniqueIdException e1) {
      // expected.
    }
    catch (BadSlugException e) {
      e.printStackTrace();
      fail("should not happen.");
    }
    catch (MisMatchedOwnerException e1) {
      e1.printStackTrace();
      fail(e1.getMessage() + " should not happen");
    }
    catch (IdNotFoundException e1) {
      e1.printStackTrace();
      fail(e1.getMessage() + " should not happen");
    }
    try {
      impl.defineSensor(sens.getId(), sens.getName(), sens.getUri(), "bogus-sensor-model-id-4921",
          sens.getProperties(), sens.getOrganizationId());
    }
    catch (BadSlugException e) {
      e.printStackTrace();
      fail("should not happen.");
    }
    catch (UniqueIdException e1) {
      e1.printStackTrace();
      fail(e1.getMessage() + " should not happen");
    }
    catch (MisMatchedOwnerException e1) {
      e1.printStackTrace();
      fail(e1.getMessage() + " should not happen");
    }
    catch (IdNotFoundException e1) {
      // expected.
    }

    try {
      impl.deleteSensor(sens.getId(), sens.getOrganizationId());
    }
    catch (IdNotFoundException e) {
      e.printStackTrace();
      fail(e.getMessage() + " should not happen");
    }
    catch (MisMatchedOwnerException e) {
      e.printStackTrace();
      fail(e.getMessage() + " should not happen");
    }
    try {
      deleteSensorModel();
    }
    catch (IdNotFoundException e) {
      e.printStackTrace();
      fail(e.getMessage() + " should not happen");
    }
  }

  /**
   * Test methods for dealing with SensorGroups.
   */
  @Test
  public void testSensorGroups() {
    List<SensorGroup> list = null;
    int numGroups = 0;
    try {
      list = impl.getSensorGroups(testOrg.getId(), true);
    }
    catch (IdNotFoundException e2) {
      fail(e2.getMessage() + " should not happen");
    }
    numGroups = list.size();
    assertTrue(numGroups >= 0);
    SensorGroup group = InstanceFactory.getSensorGroup();
    try {
      impl.defineSensorGroup(group.getId(), group.getName(), group.getSensors(),
          group.getOrganizationId());
    }
    catch (BadSlugException e) {
      e.printStackTrace();
      fail("should not happen.");
    }
    catch (UniqueIdException e) {
      e.printStackTrace();
      fail(e.getMessage() + " should not happen");
    }
    catch (MisMatchedOwnerException e) {
      e.printStackTrace();
      fail(e.getMessage() + " should not happen");
    }
    catch (IdNotFoundException e) {
      // expected.
      try {
        addSensor();
      }
      catch (MisMatchedOwnerException e1) {
        e1.printStackTrace();
        fail(e1.getMessage() + " should not happen");
      }
      catch (UniqueIdException e1) {
        e1.printStackTrace();
        fail(e1.getMessage() + " should not happen");
      }
      catch (IdNotFoundException e1) {
        e1.printStackTrace();
        fail(e1.getMessage() + " should not happen");
      }
    }
    try {
      impl.defineSensorGroup(group.getId(), group.getName(), group.getSensors(),
          group.getOrganizationId());
      list = impl.getSensorGroups(testOrg.getId(), true);
      assertTrue(numGroups + 1 == list.size());
      List<String> ids = impl.getSensorGroupIds(testOrg.getId(), true);
      assertTrue(list.size() == ids.size());
      SensorGroup defined = impl.getSensorGroup(group.getId(), group.getOrganizationId(), true);
      assertNotNull(defined);
      assertTrue(defined.equals(group));
      assertTrue(defined.toString().equals(group.toString()));
      defined.setName("New Name");
      impl.updateSensorGroup(defined);
      list = impl.getSensorGroups(testOrg.getId(), true);
      assertTrue(numGroups + 1 == list.size());
      SensorGroup updated = impl.getSensorGroup(group.getId(), group.getOrganizationId(), true);
      assertFalse(updated.equals(group));
      assertFalse(updated.toString().equals(group.toString()));
      assertFalse(updated.hashCode() == group.hashCode());
    }
    catch (BadSlugException e) {
      e.printStackTrace();
      fail("should not happen.");
    }
    catch (UniqueIdException e) {
      e.printStackTrace();
      fail(e.getMessage() + " should not happen");
    }
    catch (MisMatchedOwnerException e) {
      e.printStackTrace();
      fail(e.getMessage() + " should not happen");
    }
    catch (IdNotFoundException e) {
      e.printStackTrace();
      fail(e.getMessage() + " should not happen");
    }
    try {
      impl.deleteSensorGroup("bogus-sensor-group-id-8440", group.getOrganizationId());
    }
    catch (IdNotFoundException e) {
      // expected
    }
    catch (MisMatchedOwnerException e) {
      e.printStackTrace();
      fail(e.getMessage() + " should not happen");
    }
    try {
      impl.deleteSensorGroup(group.getId(), group.getOrganizationId());
      list = impl.getSensorGroups(testOrg.getId(), true);
      assertTrue(numGroups == list.size());
      deleteSensor();
    }
    catch (IdNotFoundException e) {
      e.printStackTrace();
      fail(e.getMessage() + " should not happen");
    }
    catch (MisMatchedOwnerException e) {
      e.printStackTrace();
      fail(e.getMessage() + " should not happen");
    }

  }

  /**
   * Test methods for dealing with SensorModels.
   */
  @Test
  public void testSensorModels() {
    List<SensorModel> list = impl.getSensorModels();
    int numModels = list.size();
    assertTrue(numModels >= 0);
    SensorModel model = InstanceFactory.getSensorModel();
    try {
      impl.defineSensorModel(model.getId(), model.getName(), model.getProtocol(), model.getType(),
          model.getVersion());
      list = impl.getSensorModels();
      assertTrue(numModels + 1 == list.size());
      List<String> ids = impl.getSensorModelIds();
      assertTrue(list.size() == ids.size());
      SensorModel defined = null;
      try {
        defined = impl.getSensorModel(model.getId(), true);
      }
      catch (IdNotFoundException e) {
        e.printStackTrace();
        fail(e.getMessage() + " should not happen");
      }
      assertNotNull(defined);
      assertTrue(defined.equals(model));
      assertTrue(defined.toString().equals(model.toString()));
      defined.setName("New Name");
      try {
        impl.updateSensorModel(defined);
      }
      catch (IdNotFoundException e) {
        e.printStackTrace();
        fail(e.getMessage() + " should not happen");
      }
      list = impl.getSensorModels();
      assertTrue(numModels + 1 == list.size());
      SensorModel updated = null;
      try {
        updated = impl.getSensorModel(model.getId(), true);
      }
      catch (IdNotFoundException e) {
        e.printStackTrace();
        fail(e.getMessage() + " should not happen");
      }
      assertNotNull(updated);
      assertFalse(updated.equals(model));
      assertFalse(updated.toString().equals(model.toString()));
      assertFalse(updated.hashCode() == model.hashCode());
    }
    catch (BadSlugException e) {
      e.printStackTrace();
      fail("should not happen.");
    }
    catch (UniqueIdException e) {
      e.printStackTrace();
      fail(e.getMessage() + " should not happen");
    }
    try {
      impl.deleteSensorModel("bogus-sensor-model-id-4926");
      fail("should not be able to delete bogus sensor model");
    }
    catch (IdNotFoundException e) {
      // expected
    }
    try {
      impl.deleteSensorModel(model.getId());
    }
    catch (IdNotFoundException e) {
      e.printStackTrace();
      fail(e.getMessage() + " should not happen");
    }

  }

  /**
   * Test method for dealing with UserInfos.
   */
  @Test
  public void testUsers() {
    UserInfo user = InstanceFactory.getUserInfo2();
    List<UserInfo> list = null;
    try {
      list = impl.getUsers(user.getOrganizationId(), true);
    }
    catch (IdNotFoundException e2) { // NOPMD
      // expected org2 not defined.
    }
    int numUsers = 0;
    if (list != null) {
      numUsers = list.size();
    }
    assertTrue(numUsers >= 0);
    try {
      try {
        impl.defineUserInfo(user.getUid(), user.getFirstName(), user.getLastName(),
            user.getEmail(), user.getOrganizationId(), user.getProperties(), user.getPassword());
      }
      catch (IdNotFoundException e1) {
        addEmptyOrganization(InstanceFactory.getOrganization2());
        try {
          impl.defineUserInfo(user.getUid(), user.getFirstName(), user.getLastName(),
              user.getEmail(), user.getOrganizationId(), user.getProperties(), user.getPassword());
        }
        catch (IdNotFoundException e) {
          e.printStackTrace();
          fail(e.getMessage() + " should not happen");
        }
      }
      list = impl.getUsers(user.getOrganizationId(), true);
      assertTrue("expecting " + (numUsers + 1) + " got " + list.size(), numUsers + 1 == list.size());
      List<String> ids = impl.getUserIds(user.getOrganizationId(), true);
      assertTrue(list.size() == ids.size());
      UserInfo defined = null;
      try {
        defined = impl.getUser(user.getUid(), user.getOrganizationId(), true);
      }
      catch (IdNotFoundException e) {
        e.printStackTrace();
        fail(e.getMessage() + " should not happen");
      }
      assertNotNull(defined);
      assertTrue(defined.equals(user));
      assertTrue(defined.toString().equals(user.toString()));
      defined.setFirstName("New Name");
      try {
        impl.updateUserInfo(defined);
      }
      catch (IdNotFoundException e) {
        e.printStackTrace();
        fail(e.getMessage() + " should not happen");
      }
      list = impl.getUsers(user.getOrganizationId(), true);
      assertTrue(numUsers + 1 == list.size());
      UserInfo updated = null;
      try {
        updated = impl.getUser(user.getUid(), user.getOrganizationId(), true);
      }
      catch (IdNotFoundException e) {
        e.printStackTrace();
        fail(e.getMessage() + " should not happen");
      }
      assertNotNull(updated);
      assertFalse(updated.equals(user));
      assertFalse(updated.toString().equals(user.toString()));
      assertFalse(updated.hashCode() == user.hashCode());
      Organization group = null;
      try {
        group = impl.getOrganization(user.getOrganizationId(), true);
      }
      catch (IdNotFoundException e) {
        e.printStackTrace();
        fail(e.getMessage() + " should not happen.");
      }
      assertNotNull(group);
      try {
        group = impl.getOrganization(InstanceFactory.getUserInfo2().getOrganizationId(), true);
      }
      catch (IdNotFoundException e) {
        e.printStackTrace();
        fail(e.getMessage() + " should not happen.");
      }
      assertNotNull(group);
      assertTrue("expecting " + group.getId() + " got " + user.getOrganizationId(), group.getId()
          .equals(user.getOrganizationId()));
    }
    catch (UniqueIdException e) {
      e.printStackTrace();
      fail(e.getMessage() + " should not happen");
    }
    catch (IdNotFoundException e1) {
      fail(e1.getMessage() + " should not happen");
    }
    try {
      impl.deleteUser("bogus-user-id-4502", "bogus-organization-9520");
      fail("should be able to delete bogus user.");
    }
    catch (IdNotFoundException e) {
      // expected.
    }
    try {
      impl.deleteUser(user.getUid(), user.getOrganizationId());
      list = impl.getUsers(user.getOrganizationId(), true);
      assertTrue(numUsers == list.size());
    }
    catch (IdNotFoundException e) {
      e.printStackTrace();
      fail(e.getMessage() + " should not happen");
    }
  }

  /**
   * Test methods for dealing with UserPasswords.
   */
  @Test
  public void testUserPasswords() {
    UserPassword password = InstanceFactory.getUserPassword2();
    try {
      impl.defineUserInfo(password.getUid(), password.getEncryptedPassword(),
          password.getEncryptedPassword(), password.getEncryptedPassword(),
          password.getOrganizationId(), new HashSet<Property>(),
          StrongAES.getInstance().decrypt(password.getEncryptedPassword()));
    }
    catch (UniqueIdException e) {
      e.printStackTrace();
      fail(e.getMessage() + " should not happen");
    }
    catch (IdNotFoundException e) {
      e.printStackTrace();
      fail(e.getMessage() + " should not happen");
    }
    UserPassword defined = null;
    try {
      defined = impl.getUserPassword(password.getUid(), password.getOrganizationId(), true);
    }
    catch (IdNotFoundException e) {
      e.printStackTrace();
      fail(e.getMessage() + " should not happen");
    }
    assertNotNull(defined);
    assertTrue(defined.equals(password));
    defined.setPassword("New plainText");
    try {
      impl.updateUserPassword(defined);
    }
    catch (IdNotFoundException e) {
      e.printStackTrace();
      fail(e.getMessage() + " should not happen");
    }
    UserPassword updated = null;
    try {
      updated = impl.getUserPassword(password.getUid(), password.getOrganizationId(), true);
    }
    catch (IdNotFoundException e) {
      e.printStackTrace();
      fail(e.getMessage() + " should not happen");
    }
    assertNotNull(updated);
    assertFalse(updated.equals(password));
    assertFalse(updated.toString().equals(password.toString()));
    try {
      impl.deleteUserPassword("bogus-user-password-id-9685", "bogus-organization-5928");
      fail("Should not be able to delete bogus password.");
    }
    catch (IdNotFoundException e) {
      // expected.
    }
    try {
      impl.deleteUserPassword(password.getUid(), password.getOrganizationId());
    }
    catch (IdNotFoundException e) {
      e.printStackTrace();
      fail(e.getMessage() + " should not happen");
    }
  }

  /**
   * @throws UniqueIdException if the Model is already defined.
   */
  private void addSensorModel() throws UniqueIdException {
    SensorModel model = InstanceFactory.getSensorModel();
    try {
      impl.getSensorModel(model.getId(), true);
    }
    catch (IdNotFoundException e1) {
      try {
        impl.defineSensorModel(model.getId(), model.getName(), model.getProtocol(),
            model.getType(), model.getVersion());
      }
      catch (BadSlugException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * @throws IdNotFoundException if the SensorModel is not defined.
   */
  private void deleteSensorModel() throws IdNotFoundException {
    SensorModel model = InstanceFactory.getSensorModel();
    SensorModel defined = impl.getSensorModel(model.getId(), true);
    if (defined != null) {
      impl.deleteSensorModel(model.getId());
    }
  }

  /**
   * @throws MisMatchedOwnerException if there is a mismatch in the ownership.
   * @throws UniqueIdException if the Sensor is already defined.
   * @throws IdNotFoundException if the SensorLocation or SensorModel aren't
   *         defined.
   */
  private void addSensor() throws MisMatchedOwnerException, UniqueIdException, IdNotFoundException {
    addSensorModel();
    Sensor sensor = InstanceFactory.getSensor();
    try {
      impl.getSensor(sensor.getId(), sensor.getOrganizationId(), true);
    }
    catch (IdNotFoundException e) {
      try {
        impl.defineSensor(sensor.getId(), sensor.getName(), sensor.getUri(), sensor.getModelId(),
            sensor.getProperties(), sensor.getOrganizationId());
      }
      catch (BadSlugException e1) {
        e1.printStackTrace();
      }
    }
  }

  /**
   * @throws MisMatchedOwnerException if there is a mismatch in ownership.
   * @throws IdNotFoundException if the Sensor is not defined.
   */
  private void deleteSensor() throws MisMatchedOwnerException, IdNotFoundException {
    Sensor sensor = InstanceFactory.getSensor();
    Sensor defined = impl.getSensor(sensor.getId(), sensor.getOrganizationId(), true);
    if (defined != null) {
      impl.deleteSensor(sensor.getId(), sensor.getOrganizationId());
    }
    deleteSensorModel();
  }

  /**
   * @throws UniqueIdException if there is already a Depository defined.
   * @throws IdNotFoundException if there are problems with the ids.
   */
  private void addDepository() throws UniqueIdException, IdNotFoundException {
    Depository depot = InstanceFactory.getDepository();
    try {
      impl.getDepository(depot.getId(), depot.getOrganizationId(), true);
    }
    catch (IdNotFoundException e) {
      try {
        impl.defineDepository(depot.getId(), depot.getName(), depot.getMeasurementType(),
            depot.getOrganizationId());
      }
      catch (BadSlugException e1) {
        e1.printStackTrace();
      }
    }
  }

  /**
   * @throws MisMatchedOwnerException if there is a mismatch in ownership.
   * @throws UniqueIdException if the CollectorProcessDefinition is already
   *         defined.
   * @throws IdNotFoundException if the Sensor is not defined.
   */
  @SuppressWarnings("unused")
  private void addCollectorProcessDefinition() throws MisMatchedOwnerException, UniqueIdException,
      IdNotFoundException {
    addSensor();
    CollectorProcessDefinition cpd = InstanceFactory.getCollectorProcessDefinition();
    CollectorProcessDefinition defined = impl.getCollectorProcessDefinition(cpd.getId(),
        cpd.getOrganizationId(), true);
    if (defined == null) {
      try {
        impl.defineCollectorProcessDefinition(cpd.getId(), cpd.getName(), cpd.getSensorId(),
            cpd.getPollingInterval(), cpd.getDepositoryId(), cpd.getProperties(),
            cpd.getOrganizationId());
      }
      catch (BadSlugException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * @throws UniqueIdException if the MeasurementType is already defined.
   */
  private void addMeasurementType() throws UniqueIdException {
    MeasurementType type = InstanceFactory.getMeasurementType();
    try {
      impl.getMeasurementType(type.getId(), true);
    }
    catch (IdNotFoundException e1) {
      try {
        impl.defineMeasurementType(type.getId(), type.getName(), type.getUnits());
      }
      catch (BadSlugException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * @throws IdNotFoundException if the MeasurementType isn't defined.
   */
  private void deleteMeasurementType() throws IdNotFoundException {
    MeasurementType type = InstanceFactory.getMeasurementType();
    MeasurementType defined = impl.getMeasurementType(type.getId(), true);
    if (defined != null) {
      impl.deleteMeasurementType(type.getId());
    }
  }

  // private void addUserInfo(UserInfo user) {
  // try {
  // impl.getUser(user.getUid(), user.getOrganizationId());
  // }
  // catch (IdNotFoundException e) {
  // try {
  // impl.defineUserInfo(user.getUid(), user.getFirstName(), user.getLastName(),
  // user.getEmail(), user.getOrganizationId(), user.getProperties(),
  // user.getPassword());
  // }
  // catch (UniqueIdException e1) {
  // e1.printStackTrace();
  // }
  // catch (IdNotFoundException e1) {
  // e1.printStackTrace();
  // }
  // }
  // }

  /**
   * @param org The empty Organization.
   */
  private void addEmptyOrganization(Organization org) {
    try {
      impl.getOrganization(org.getId(), true);
    }
    catch (IdNotFoundException e) {
      try {
        impl.defineOrganization(org.getId(), org.getName(), new HashSet<String>());
      }
      catch (UniqueIdException e1) {
        e1.printStackTrace();
      }
      catch (BadSlugException e1) {
        e1.printStackTrace();
      }
      catch (IdNotFoundException e1) {
        e1.printStackTrace();
      }
    }
  }
}
