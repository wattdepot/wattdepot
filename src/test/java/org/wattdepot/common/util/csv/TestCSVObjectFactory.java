/**
 * TestObjectFactory.java This file is part of WattDepot.
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
package org.wattdepot.common.util.csv;



import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.measure.unit.Unit;
import javax.xml.datatype.DatatypeConfigurationException;

import org.junit.Test;
import org.wattdepot.common.domainmodel.CollectorProcessDefinition;
import org.wattdepot.common.domainmodel.Depository;
import org.wattdepot.common.domainmodel.Measurement;
import org.wattdepot.common.domainmodel.MeasurementPruningDefinition;
import org.wattdepot.common.domainmodel.MeasurementType;
import org.wattdepot.common.domainmodel.Property;
import org.wattdepot.common.domainmodel.Sensor;
import org.wattdepot.common.domainmodel.SensorGroup;
import org.wattdepot.common.util.DateConvert;
import org.wattdepot.common.util.Slug;
import org.wattdepot.common.util.UnitsHelper;

/**
 * TestObjectFactory test the ObjectFactory methods.
 * 
 * @author Cam Moore
 * 
 */
public class TestCSVObjectFactory {

  /**
   * Test method for CollectorProcessDefinitions.
   */
  @Test
  public void testCollectorProcessDefinition() {
    String name = "name";
    String sensorId = "sensorId";
    Long polling = 13l;
    String depositoryId = "depositoryId";
    String orgId = "orgId";
    Set<Property> properties = new HashSet<Property>();
    Property p = new Property("key1", "value1");
    properties.add(p);
    p = new Property("key2", "value2");
    properties.add(p);
    CollectorProcessDefinition cpd = new CollectorProcessDefinition(Slug.slugify(name), name,
        sensorId, polling, depositoryId, properties, orgId);
    String csv = CSVObjectFactory.toCSV(cpd);
    assertNotNull(csv);
    try {
      CollectorProcessDefinition result = CSVObjectFactory.buildCPD(csv);
      assertNotNull(result);
      assertTrue(cpd.equals(result));
    }
    catch (IOException e) {
      e.printStackTrace();
      fail(e.getMessage() + " should not happen");
    }
  }
  
  /**
   * Test method for Depositories. 
   */
  @Test
  public void testDepository() {
    String name = "name";
    MeasurementType type = new MeasurementType("test", Unit.valueOf("W"));
    String orgId = "orgId";
    Depository depo = new Depository(name, type, orgId);
    String csv = CSVObjectFactory.toCSV(depo);
    assertNotNull(csv);
    try {
      Depository result = CSVObjectFactory.buildDepository(csv);
      assertNotNull(result);
      assertTrue(depo.equals(result));
    }
    catch (IOException e) {
      e.printStackTrace();
      fail(e.getMessage() + " should not happen");
    }
  }

  /**
   * Test method for Sensors. 
   */
  @Test
  public void testSensor() {
    String name = "name";
    String uri = "uri";
    String modelId = "modelId";
    Set<Property> properties = new HashSet<Property>();
    Property p = new Property("key1", "value1");
    properties.add(p);
    String orgId = "orgId";
    Sensor sensor = new Sensor(Slug.slugify(name), name, uri, modelId, properties, orgId);
    String csv = CSVObjectFactory.toCSV(sensor);
    assertNotNull(csv);
    try {
      Sensor result = CSVObjectFactory.buildSensor(csv);
      assertNotNull(result);
      assertTrue(sensor.equals(result));
    }
    catch (IOException e) {
      e.printStackTrace();
      fail(e.getMessage() + " should not happen");
    }
  }
  
  /**
   * Test method for SensorGroups.
   */
  @Test
  public void testSensorGroup() {
    String name = "name";
    String orgId = "orgId";
    Set<String> sensors = new HashSet<String>();
    sensors.add("sensor1");
    sensors.add("sensor2");
    SensorGroup group = new SensorGroup(Slug.slugify(name), name, sensors, orgId);
    String csv = CSVObjectFactory.toCSV(group);
    assertNotNull(csv);
    try {
      SensorGroup result = CSVObjectFactory.buildSensorGroup(csv);
      assertNotNull(result);
      assertTrue(group.equals(result));
    }
    catch (IOException e) {
      e.printStackTrace();
      fail(e.getMessage() + " should not happen");
    }
  }

  /**
   * Test method for MeasurementPruningDefinitions.
   */
  @Test
  public void testMeasurementPruningDefinition() {
    String name = "name";
    String depositoryId = "depositoryId";
    String sensorId = "sensorId";
    String orgId = "orgId";
    Integer ignoreWindow = 5;
    Integer collectWindow = 10;
    Integer gapSeconds = 300;
    MeasurementPruningDefinition mpd = new MeasurementPruningDefinition(name, depositoryId, sensorId, orgId, ignoreWindow, collectWindow, gapSeconds);
    String csv = CSVObjectFactory.toCSV(mpd);
    assertNotNull(csv);
    try {
      MeasurementPruningDefinition result = CSVObjectFactory.buildMeasurementPruningDefinition(csv);
      assertNotNull(result);
      assertTrue(result.equals(mpd));
    }
    catch (IOException e) {
      e.printStackTrace();
      fail(e.getMessage() + " should not happen");
    }
  }

  /**
   * Test method for Measurements.
   */
  @Test
  public void testMeasurement() throws ParseException, DatatypeConfigurationException {
    String name = "depository";
    MeasurementType type = new MeasurementType("test", Unit.valueOf("W"));
    String orgId = "orgId";
    Depository depo = new Depository(name, type, orgId);
    String sensorId = "sensorId";
    Date timeStamp = DateConvert.parseCalStringToDate("2015-09-20T14:35:27.925-1000");
    Double value = 3.2;
    Unit<?> unit = type.unit();
    Measurement measurement = new Measurement(sensorId, timeStamp, value, unit);

    String csv = CSVObjectFactory.toCSV(depo, measurement);
    assertNotNull(csv);
    try {
      DepositoryMeasurement result = CSVObjectFactory.buildMeasurement(csv);
      assertNotNull(result);
      assertTrue(result.getMeasurement().equals(measurement));
    }
    catch (IOException e) {
      e.printStackTrace();
      fail(e.getMessage() + " should not happen");
    }
  }
}
