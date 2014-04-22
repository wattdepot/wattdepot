/**
 * TestMeasurementGarbageCollector.java This file is part of WattDepot.
 *
 * Copyright (C) 2014  Cam Moore
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
package org.wattdepot.server.garbage.collector;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.List;

import javax.measure.unit.Unit;
import javax.xml.datatype.XMLGregorianCalendar;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.wattdepot.common.domainmodel.Depository;
import org.wattdepot.common.domainmodel.GarbageCollectionDefinition;
import org.wattdepot.common.domainmodel.InstanceFactory;
import org.wattdepot.common.domainmodel.Measurement;
import org.wattdepot.common.domainmodel.MeasurementType;
import org.wattdepot.common.domainmodel.Organization;
import org.wattdepot.common.domainmodel.Sensor;
import org.wattdepot.common.domainmodel.SensorModel;
import org.wattdepot.common.domainmodel.UserInfo;
import org.wattdepot.common.domainmodel.UserPassword;
import org.wattdepot.common.exception.BadSlugException;
import org.wattdepot.common.exception.IdNotFoundException;
import org.wattdepot.common.exception.MeasurementTypeException;
import org.wattdepot.common.exception.MisMatchedOwnerException;
import org.wattdepot.common.exception.UniqueIdException;
import org.wattdepot.common.util.DateConvert;
import org.wattdepot.common.util.UnitsHelper;
import org.wattdepot.common.util.tstamp.Tstamp;
import org.wattdepot.server.ServerProperties;
import org.wattdepot.server.WattDepotPersistence;
import org.wattdepot.server.depository.impl.hibernate.WattDepotPersistenceImpl;

/**
 * TestMeasurementGarbageCollector test cases for the
 * MeasurementGarbageCollector class.
 * 
 * @author Cam Moore
 * 
 */
public class TestMeasurementGarbageCollector {

  private static WattDepotPersistenceImpl impl;
  private static UserInfo testUser;
  private static UserPassword testPassword;
  private static Organization testOrg;
  private static ServerProperties properties;
  private static Depository depository;
  private static Sensor sensor;
  private static GarbageCollectionDefinition gcd;

  private XMLGregorianCalendar now;
  private XMLGregorianCalendar start;
  private XMLGregorianCalendar end;
  private int totalMeasurements;

  /**
   * Sets up the WattDepotPersistenceImpl using test properties.
   */
  @BeforeClass
  public static void setupImpl() {
    properties = new ServerProperties();
    properties.setTestProperties();
    impl = new WattDepotPersistenceImpl(properties);
    testUser = InstanceFactory.getUserInfo();
    testPassword = InstanceFactory.getUserPassword();
    testOrg = InstanceFactory.getOrganization();
    // shouldn't have to do this.
    testUser.setOrganizationId(testOrg.getId());
    // prime the MeasurementTypes
    UnitsHelper.quantities.containsKey("W");
  }

  /**
   * @throws java.lang.Exception if there is a problem.
   */
  @Before
  public void setUp() throws Exception {
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
    }
    // 3. update the organization
    try {
      impl.updateOrganization(testOrg);
    }
    catch (IdNotFoundException e1) {
      // this shouldn't happen
      e1.printStackTrace();
    }
    addSensor();
    addMeasurementType();
    addDepository();
    addGarbageCollectionDefinition();
  }

  /**
   * @throws java.lang.Exception if there is a problem.
   */
  @After
  public void tearDown() throws Exception {
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
   * Test method for
   * {@link org.wattdepot.server.garbage.collector.MeasurementGarbageCollector#getMeasurementsToDelete()}
   * .
   * 
   * @throws Exception if there is a problem.
   */
  @Test
  public void testGetMeasurementsToDelete() throws Exception {
    populateMeasurements(2);
    MeasurementGarbageCollector mgc = new MeasurementGarbageCollector(properties, gcd.getId(),
        gcd.getOrganizationId());
    List<Measurement> toDel = mgc.getMeasurementsToDelete();
    assertTrue(toDel.size() > 0);
    assertTrue(toDel.size() < totalMeasurements);
  }

  /**
   * @throws Exception if there is a problem.
   */
  @Test
  public void testGarbageCollection() throws Exception {
    populateMeasurements(2);
    MeasurementGarbageCollector mgc = new MeasurementGarbageCollector(properties, gcd.getId(),
        gcd.getOrganizationId());
    int numToDel = mgc.getMeasurementsToDelete().size();
    mgc.run();
    List<Measurement> toDel = mgc.getMeasurementsToDelete();
    assertTrue(toDel.size() == 0);
    WattDepotPersistence p = mgc.getPersistance();
    List<Measurement> data = p.getMeasurements(gcd.getDepositoryId(), gcd.getOrganizationId(),
        gcd.getSensorId(), false);
    assertNotNull(data);
    assertTrue(data.size() < totalMeasurements);
    assertTrue(data.size() == totalMeasurements - numToDel);
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
   * @throws MisMatchedOwnerException if there is a mismatch in the ownership.
   * @throws UniqueIdException if the Sensor is already defined.
   * @throws IdNotFoundException if the SensorLocation or SensorModel aren't
   *         defined.
   */
  private void addSensor() throws MisMatchedOwnerException, UniqueIdException, IdNotFoundException {
    addSensorModel();
    TestMeasurementGarbageCollector.sensor = InstanceFactory.getSensor();
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
   * @throws UniqueIdException if there is already a Depository defined.
   * @throws IdNotFoundException if there are problems with the ids.
   */
  private void addDepository() throws UniqueIdException, IdNotFoundException {
    TestMeasurementGarbageCollector.depository = InstanceFactory.getDepository();
    try {
      impl.getDepository(depository.getId(), depository.getOrganizationId(), true);
    }
    catch (IdNotFoundException e) {
      try {
        impl.defineDepository(depository.getId(), depository.getName(),
            depository.getMeasurementType(), depository.getOrganizationId());
      }
      catch (BadSlugException e1) {
        e1.printStackTrace();
      }
    }
  }

  /**
   * Adds the predefined GarbageCollectionDefinition.
   */
  private void addGarbageCollectionDefinition() {
    TestMeasurementGarbageCollector.gcd = InstanceFactory.getGarbageCollectionDefinition();
    try {
      impl.getGarbageCollectionDefinition(gcd.getId(), gcd.getOrganizationId(), true);
    }
    catch (IdNotFoundException e) {
      try {
        impl.defineGarbageCollectionDefinition(gcd.getId(), gcd.getName(), gcd.getDepositoryId(),
            gcd.getSensorId(), gcd.getOrganizationId(), gcd.getIgnoreWindowDays(),
            gcd.getCollectWindowDays(), gcd.getMinGapSeconds());
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

  /**
   * @param time The time of the measurement.
   * @return A fake measurement at the given time.
   */
  private Measurement makeMeasurement(XMLGregorianCalendar time) {
    return new Measurement(InstanceFactory.getSensor().getId(), DateConvert.convertXMLCal(time),
        123.0, Unit.valueOf(InstanceFactory.getMeasurementType().getUnits()));
  }

  /**
   * @param minBetween The number of minutes between measurements.
   * @throws MeasurementTypeException This shouldn't happen.
   * @throws IdNotFoundException If there is a problem with the ids. This
   *         shouldn't happen.
   */
  private void populateMeasurements(int minBetween) throws MeasurementTypeException,
      IdNotFoundException {
    // Need to add high frequency Measurements to the database.
    now = Tstamp.makeTimestamp();
    start = Tstamp.incrementDays(now, -1 * gcd.getIgnoreWindowDays());
    end = Tstamp.incrementDays(start, -1 * gcd.getCollectWindowDays());
    List<XMLGregorianCalendar> measTimes = Tstamp.getTimestampList(end, now, minBetween);
    totalMeasurements = measTimes.size();
    for (XMLGregorianCalendar cal : measTimes) {
      impl.putMeasurement(depository.getId(), depository.getOrganizationId(), makeMeasurement(cal));
    }
  }
}
