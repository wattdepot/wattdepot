/**
 * DefinitionFileWriter.java This file is part of WattDepot.
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

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.wattdepot.common.domainmodel.CollectorProcessDefinition;
import org.wattdepot.common.domainmodel.Depository;
import org.wattdepot.common.domainmodel.Measurement;
import org.wattdepot.common.domainmodel.MeasurementPruningDefinition;
import org.wattdepot.common.domainmodel.Sensor;
import org.wattdepot.common.domainmodel.SensorGroup;

/**
 * DefinitionFileWriter writes out a WattDepot object definition file from the
 * domain model instances.
 * 
 * @author Cam Moore
 * 
 */
public class DefinitionFileWriter {

  private BufferedWriter writer;
  private Set<Depository> depositories;
  private Set<SensorGroup> groups;
  private Set<Sensor> sensors;
  private Set<CollectorProcessDefinition> cpds;
  private Set<MeasurementPruningDefinition> gcds;
  private Map<Depository, List<Measurement>> measurementMap;

  /**
   * Creates a new DefinitionFileReader.
   * 
   * @param fileName The name of the definition file, a CSV file of WattDepot
   *        object definitions.
   * @throws IOException if there is a problem reading the file.
   */
  public DefinitionFileWriter(String fileName) throws IOException {
    this.writer = new BufferedWriter(new FileWriter(fileName));
    this.depositories = new HashSet<Depository>();
    this.groups = new HashSet<SensorGroup>();
    this.sensors = new HashSet<Sensor>();
    this.cpds = new HashSet<CollectorProcessDefinition>();
    this.gcds = new HashSet<MeasurementPruningDefinition>();
    this.measurementMap = new HashMap<Depository, List<Measurement>>();
  }

  /**
   * @param arg0 The CollectorProcessDefinition to add.
   * @return true if is added.
   * @see java.util.Set#add(java.lang.Object)
   */
  public boolean add(CollectorProcessDefinition arg0) {
    return cpds.add(arg0);
  }

  /**
   * @param arg0 The Depository to add.
   * @return true if it is added.
   * @see java.util.Set#add(java.lang.Object)
   */
  public boolean add(Depository arg0) {
    return depositories.add(arg0);
  }

  /**
   * @param d The Depository holding the measurement.
   * @param m The Measurement to add.
   * @return the old value.
   * @see java.util.Map#put(Object, Object)
   */
  public boolean put(Depository d, Measurement m) {
    List<Measurement> measurements = measurementMap.get(d);
    if (measurements == null) {
      measurements = new ArrayList<Measurement>();
      measurementMap.put(d, measurements);
    }
    return measurements.add(m);
  }

  /**
   * @param arg0 The MeasurementPruningDefinition to add.
   * @return true if it is added.
   * @see java.util.Set#add(java.lang.Object)
   */
  public boolean add(MeasurementPruningDefinition arg0) {
    return gcds.add(arg0);
  }

  /**
   * @param arg0 The Sensor to add.
   * @return true if it is added.
   * @see java.util.Set#add(java.lang.Object)
   */
  public boolean add(Sensor arg0) {
    return sensors.add(arg0);
  }

  /**
   * @param arg0 The SensorGroup to add.
   * @return true if it is added.
   * @see java.util.Set#add(java.lang.Object)
   */
  public boolean add(SensorGroup arg0) {
    return groups.add(arg0);
  }

  /**
   * @return the CollectorProcessDefinitions.
   */
  public Set<CollectorProcessDefinition> getCollectorProcessDefinitions() {
    return cpds;
  }

  /**
   * @return the Depositories
   */
  public Set<Depository> getDepositories() {
    return depositories;
  }

  /**
   * @param d The Depository to get the meaurements for.
   * @return the Measurements
   */
  public List<Measurement> getMeasurements(Depository d) {
    return measurementMap.get(d);
  }

  /**
   * @return the gcds
   */
  public Set<MeasurementPruningDefinition> getMeasurementPruningDefinitions() {
    return gcds;
  }

  /**
   * @return the SensorGroups
   */
  public Set<SensorGroup> getSensorGroups() {
    return groups;
  }

  /**
   * @return the Sensors
   */
  public Set<Sensor> getSensors() {
    return sensors;
  }

  /**
   * @param cpds the Set of CollectorProcessDefinition to set
   */
  public void setCollectorProcessDefinitions(Set<CollectorProcessDefinition> cpds) {
    this.cpds = cpds;
  }

  /**
   * @param depositories the Set of Depositories to set
   */
  public void setDepositories(Set<Depository> depositories) {
    this.depositories = depositories;
  }

  /**
   * @param gcds the gcds to set
   */
  public void setMeasurementPruningDefinitions(Set<MeasurementPruningDefinition> gcds) {
    this.gcds = gcds;
  }

  /**
   * @param groups the Set of SensorGroups to set
   */
  public void setSensorGroups(Set<SensorGroup> groups) {
    this.groups = groups;
  }

  /**
   * @param sensors the Set of Sensors to set
   */
  public void setSensors(Set<Sensor> sensors) {
    this.sensors = sensors;
  }

  /**
   * @throws IOException if there is a problem writing the file.
   */
  public void writeFile() throws IOException {
    // Write out the Depositories
    writer
        .write("# Depositories: 'Depository', Name, MeasurementType Name, MeasurementType Unit, OrgId");
    writer.write("\r\n");
    for (Depository d : depositories) {
      writer.write(CSVObjectFactory.toCSV(d));
      writer.write("\r\n");
    }
    // Sensors
    writer
        .write("# Sensors: 'Sensor', Name, URI, ModelId, OrgId, num properties, prop1.key, prop1,value, ...,");
    writer.write("\r\n");
    for (Sensor s : sensors) {
      writer.write(CSVObjectFactory.toCSV(s));
      writer.write("\r\n");
    }
    // SensorGroups
    writer
        .write("# SensorGroups: 'SensorGroup', Name, OrgId, num sensors, sensorId1, sensorId2, ...,");
    writer.write("\r\n");
    for (SensorGroup g : groups) {
      writer.write(CSVObjectFactory.toCSV(g));
      writer.write("\r\n");
    }
    // CollectorProcessDefinitions
    writer
        .write("# CollectorProcessDefinitions: 'CollectorProcessDefinition', Name, SensorId, Polling, DepositoryId, OrgId, num properties, prop1.key, prop1.value, ...,");
    writer.write("\r\n");
    for (CollectorProcessDefinition cpd : cpds) {
      writer.write(CSVObjectFactory.toCSV(cpd));
      writer.write("\r\n");
    }
    // MeasurementPruningDefinitions
    writer
        .write("# MeasurementPruningDefinitions: 'MeasurementPruningDefinition', Name, DepositoryId, SensorId, OrgId, ignore window days, collect window days, minimum gap seconds");
    writer.write("\r\n");
    for (MeasurementPruningDefinition gcd : gcds) {
      writer.write(CSVObjectFactory.toCSV(gcd));
      writer.write("\r\n");
    }
    // Measurements
    writer.write("# Measurements: 'Measurement', DepositoryId, SensorId, Date, Value, Measurement Type, ");
    for (Depository d : measurementMap.keySet()) {
      for (Measurement m : measurementMap.get(d)) {
        writer.write(CSVObjectFactory.toCSV(d, m));
        writer.write("\r\n");
      }
    }
    writer.flush();
  }
}
