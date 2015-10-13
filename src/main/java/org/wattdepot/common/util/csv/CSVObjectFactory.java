/**
 * ObjectFactory.java This file is part of WattDepot.
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

import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.measure.unit.Unit;

import org.wattdepot.common.domainmodel.CollectorProcessDefinition;
import org.wattdepot.common.domainmodel.Depository;
import org.wattdepot.common.domainmodel.Measurement;
import org.wattdepot.common.domainmodel.MeasurementPruningDefinition;
import org.wattdepot.common.domainmodel.MeasurementType;
import org.wattdepot.common.domainmodel.Property;
import org.wattdepot.common.domainmodel.Sensor;
import org.wattdepot.common.domainmodel.SensorGroup;
import org.wattdepot.common.util.Slug;

import au.com.bytecode.opencsv.CSVReader;

/**
 * ObjectFactory factory class for converting WattDepot domain model instances
 * into a CSV entry and converting a CSV entry into a domain model instance.
 * 
 * @author Cam Moore
 * 
 */
public class CSVObjectFactory {

  /**
   * @param cpd the CollectorProcessDefinition.
   * @return The CSV entity for the given CollectorProcessDefinition.
   */
  public static String toCSV(CollectorProcessDefinition cpd) {
    StringBuffer buf = new StringBuffer();
    // class name
    buf.append(cpd.getClass().getSimpleName());
    buf.append(",");
    // name
    buf.append(cpd.getName());
    buf.append(",");
    // sensor id
    buf.append(cpd.getSensorId());
    buf.append(",");
    // polling
    buf.append(cpd.getPollingInterval());
    buf.append(",");
    // depository id
    buf.append(cpd.getDepositoryId());
    buf.append(",");
    buf.append(cpd.getOrganizationId());
    buf.append(",");
    buf.append(getPropertiesCSV(cpd.getProperties()));
    return buf.toString();
  }

  /**
   * @param csv the CSV to parse.
   * @return the CollectorProcessDefinition
   * @throws IOException if there is a problem parsing the csv.
   */
  public static CollectorProcessDefinition buildCPD(String csv) throws IOException {
    CSVReader reader = new CSVReader(new StringReader(csv));
    try {
      String[] line = reader.readNext();
      if (CollectorProcessDefinition.class.getSimpleName().equals(line[0]) && line.length >= 7) {
        String name = line[1];
        String sensorId = line[2];
        Long polling = Long.parseLong(line[3]);
        String depositoryId = line[4];
        String orgId = line[5];
        Set<Property> properties = buildProperties(line, 6);
        return new CollectorProcessDefinition(Slug.slugify(name), name, sensorId, polling,
            depositoryId, properties, orgId);
      }
    }
    finally {
      reader.close();
    }
    return null;
  }

  /**
   * @param depo the Depository.
   * @return The CSV entity for the given Depository.
   */
  public static String toCSV(Depository depo) {
    StringBuffer buf = new StringBuffer();
    // class name
    buf.append(depo.getClass().getSimpleName());
    buf.append(",");
    // name
    buf.append(depo.getName());
    buf.append(",");
    // measurement Type name
    buf.append(depo.getMeasurementType().getName());
    buf.append(",");
    // measurement Type units
    buf.append(depo.getMeasurementType().getUnits());
    buf.append(",");
    // organization
    buf.append(depo.getOrganizationId());
    return buf.toString();
  }

  /**
   * @param csv The CSV entity to parse.
   * @return The Depository
   * @throws IOException if there is a problem parsing the String.
   */
  public static Depository buildDepository(String csv) throws IOException {
    CSVReader reader = new CSVReader(new StringReader(csv));
    try {
      String[] line = reader.readNext();
      if (Depository.class.getSimpleName().equals(line[0]) && line.length == 5) {
        String name = line[1];
        String measTypeName = line[2];
        String measTypeUnits = line[3];
        MeasurementType type = new MeasurementType(measTypeName, Unit.valueOf(measTypeUnits));
        String orgId = line[4];
        return new Depository(Slug.slugify(name), name, type, orgId);
      }
    }
    finally {
      reader.close();
    }
    return null;
  }

  /**
   * @param sensor The Sensor.
   * @return The CSV string representing the Sensor.
   */
  public static String toCSV(Sensor sensor) {
    StringBuffer buf = new StringBuffer();
    // class name
    buf.append(sensor.getClass().getSimpleName());
    buf.append(",");
    // name
    buf.append(sensor.getName());
    buf.append(",");
    // uri
    buf.append(sensor.getUri());
    buf.append(",");
    // modelId
    buf.append(sensor.getModelId());
    buf.append(",");
    // orgId
    buf.append(sensor.getOrganizationId());
    buf.append(",");
    // properties
    buf.append(getPropertiesCSV(sensor.getProperties()));
    return buf.toString();
  }

  /**
   * @param csv The CSV entity to parse.
   * @return the Sensor.
   * @throws IOException if there is a problem parsing the String.
   */
  public static Sensor buildSensor(String csv) throws IOException {
    CSVReader reader = new CSVReader(new StringReader(csv));
    try {
      String[] line = reader.readNext();
      if (Sensor.class.getSimpleName().equals(line[0]) && line.length >= 6) {
        String name = line[1];
        String uri = line[2];
        String modelId = line[3];
        String orgId = line[4];
        Set<Property> properties = buildProperties(line, 5);
        return new Sensor(Slug.slugify(name), name, uri, modelId, properties, orgId);
      }
    }
    finally {
      reader.close();
    }
    return null;
  }

  /**
   * @param group The SensorGroup to convert.
   * @return The CSV for the group.
   */
  public static String toCSV(SensorGroup group) {
    StringBuffer buf = new StringBuffer();
    // class name
    buf.append(group.getClass().getSimpleName());
    buf.append(",");
    // name
    buf.append(group.getName());
    buf.append(",");
    // orgId
    buf.append(group.getOrganizationId());
    buf.append(",");
    buf.append(group.getSensors().size());
    buf.append(",");
    for (String sensor : group.getSensors()) {
      buf.append(sensor);
      buf.append(",");
    }
    return buf.toString();
  }

  /**
   * @param csv The CSV representation of the SensorGroup
   * @return The SensorGroup
   * @throws IOException if there is a problem parsing the String.
   */
  public static SensorGroup buildSensorGroup(String csv) throws IOException {
    CSVReader reader = new CSVReader(new StringReader(csv));
    try {
      String[] line = reader.readNext();
      if (line.length >= 4 && SensorGroup.class.getSimpleName().equals(line[0])) {
        String name = line[1];
        String orgId = line[2];
        Integer numSensors = Integer.parseInt(line[3]);
        Set<String> sensors = new HashSet<String>();
        for (int i = 0; i < numSensors; i++) {
          sensors.add(line[4 + i]);
        }
        return new SensorGroup(Slug.slugify(name), name, sensors, orgId);
      }
    }
    finally {
      reader.close();
    }
    return null;
  }

  /**
   * @param gcd The MeasurementPruningDefinition to convert.
   * @return The CSV for the MeasurementPruningDefinition.
   */
  public static String toCSV(MeasurementPruningDefinition gcd) {
    StringBuffer buf = new StringBuffer();
    // class name
    buf.append(gcd.getClass().getSimpleName());
    buf.append(",");
    // name
    buf.append(gcd.getName());
    buf.append(",");
    // depositoryId
    buf.append(gcd.getDepositoryId());
    buf.append(",");
    // sensorId
    buf.append(gcd.getSensorId());
    buf.append(",");
    // organizationId
    buf.append(gcd.getOrgId());
    buf.append(",");
    // ignore Window
    buf.append(gcd.getIgnoreWindowDays());
    buf.append(",");
    // collection window
    buf.append(gcd.getCollectWindowDays());
    buf.append(",");
    // min gap
    buf.append(gcd.getMinGapSeconds());
    buf.append(",");
    return buf.toString();
  }

  /**
   * @param csv The CSV representation of the MeasurementPruningDefinition.
   * @return The MeasurementPruningDefinition
   * @throws IOException if there is a problem parsing the String.
   */
  public static MeasurementPruningDefinition buildMeasurementPruningDefinition(String csv)
      throws IOException {
    CSVReader reader = new CSVReader(new StringReader(csv));
    try {
      String[] line = reader.readNext();
      if (line.length == 9 && MeasurementPruningDefinition.class.getSimpleName().equals(line[0])) {
        String name = line[1];
        String depositoryId = line[2];
        String sensorId = line[3];
        String orgId = line[4];
        Integer ignore = Integer.parseInt(line[5]);
        Integer collect = Integer.parseInt(line[6]);
        Integer gap = Integer.parseInt(line[7]);
        return new MeasurementPruningDefinition(name, depositoryId, sensorId, orgId, ignore,
            collect, gap);
      }
    }
    finally {
      reader.close();
    }
    return null;

  }

  /**
   * @param measurement the Measurement to convert.
   * @return The CSV for the Measurement.
   */
  public static String toCSV(Measurement measurement) {
    //RFC4180 CSV http://tools.ietf.org/html/rfc4180
    StringBuffer buf = new StringBuffer();
    // class name
    buf.append(measurement.getClass().getSimpleName());
    buf.append(",");
    // sensorId
    buf.append(measurement.getSensorId());
    buf.append(",");
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
    // timestamp
    buf.append(simpleDateFormat.format(measurement.getDate()));
    buf.append(",");
    // value
    buf.append(measurement.getValue());
    buf.append(",");
    // measurement units
    buf.append(measurement.getMeasurementType());
    buf.append(",");
    return buf.toString();
  }

  /**
   * @param csv The CSV representation of a Measurement.
   * @return The Measurement
   * @throws IOException if there is a problem parsing the String.
   * @throws ParseException if there is a problem parsing the date string.
   */
  public static Measurement buildMeasurement(String csv) throws IOException, ParseException {
    CSVReader reader = new CSVReader(new StringReader(csv));
    try {
      String[] line = reader.readNext();
      if (line.length == 6 && Measurement.class.getSimpleName().equals(line[0])) {
        String sensorId = line[1];
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        Date timeStamp = simpleDateFormat.parse(line[2]);
        Double value = Double.parseDouble(line[3]);
        Unit<?> measUnit = Unit.valueOf(line[4]);
        return new Measurement(sensorId, timeStamp, value, measUnit);
      }
    }
    finally {
      reader.close();
    }
    return null;
  }

  /**
   * @param properties Set of properties.
   * @return The properties as a CSV entry with trailing ,.
   */
  private static String getPropertiesCSV(Set<Property> properties) {
    StringBuffer buf = new StringBuffer();
    buf.append(properties.size());
    buf.append(",");
    for (Property p : properties) {
      buf.append(p.getKey());
      buf.append(",");
      buf.append(p.getValue());
      buf.append(",");
    }
    return buf.toString();
  }

  /**
   * @param values The String [] holding the properties.
   * @param index The index to where the properties are stored.
   * @return a Set of Properties.
   */
  private static Set<Property> buildProperties(String[] values, int index) {
    Set<Property> properties = new HashSet<Property>();
    int numProps = Integer.parseInt(values[index]);
    for (int i = 0; i < numProps; i++) {
      Property p = new Property();
      p.setKey(values[index + i * 2 + 1]);
      p.setValue(values[index + i * 2 + 2]);
      properties.add(p);
    }
    return properties;
  }
}
