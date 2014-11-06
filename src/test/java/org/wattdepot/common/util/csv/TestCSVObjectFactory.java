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



import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.measure.unit.Unit;

import org.junit.Test;
import org.wattdepot.common.domainmodel.CollectorProcessDefinition;
import org.wattdepot.common.domainmodel.Depository;
import org.wattdepot.common.domainmodel.MeasurementType;
import org.wattdepot.common.domainmodel.Property;
import org.wattdepot.common.domainmodel.Sensor;
import org.wattdepot.common.domainmodel.SensorGroup;
import org.wattdepot.common.util.Slug;

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
}
