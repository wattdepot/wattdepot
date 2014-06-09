/**
 * DefinitionFileReader.java This file is part of WattDepot.
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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Set;
import java.util.HashSet;

import org.wattdepot.common.domainmodel.CollectorProcessDefinition;
import org.wattdepot.common.domainmodel.Depository;
import org.wattdepot.common.domainmodel.MeasurementPruningDefinition;
import org.wattdepot.common.domainmodel.Sensor;
import org.wattdepot.common.domainmodel.SensorGroup;

/**
 * DefinitionFileReader reads in a WattDepot object definition file creating the
 * domain model instances.
 * 
 * @author Cam Moore
 * 
 */
public class DefinitionFileReader {

  private BufferedReader reader;
  private Set<Depository> depositories;
  private Set<SensorGroup> groups;
  private Set<Sensor> sensors;
  private Set<CollectorProcessDefinition> cpds;
  private Set<MeasurementPruningDefinition> gcds;

  /**
   * Creates a new DefinitionFileReader.
   * 
   * @param fileName The name of the definition file, a CSV file of WattDepot
   *        object definitions.
   * @throws IOException if there is a problem reading the file.
   */
  public DefinitionFileReader(String fileName) throws IOException {
    this.reader = new BufferedReader(new FileReader(fileName));
    this.depositories = new HashSet<Depository>();
    this.groups = new HashSet<SensorGroup>();
    this.sensors = new HashSet<Sensor>();
    this.cpds = new HashSet<CollectorProcessDefinition>();
    this.gcds = new HashSet<MeasurementPruningDefinition>();
    processFile();
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
   * @throws IOException if there is a problem reading the file.
   */
  private void processFile() throws IOException {
    String line = reader.readLine();
    while (line != null) {
      line = line.trim();
      if (!line.startsWith("#") || line.length() > 0) {
        // decide what we have
        if (line.startsWith("Depository")) {
          depositories.add(CSVObjectFactory.buildDepository(line));
        }
        else if (line.startsWith("SensorGroup")) {
          groups.add(CSVObjectFactory.buildSensorGroup(line));
        }
        else if (line.startsWith("Sensor")) {
          sensors.add(CSVObjectFactory.buildSensor(line));
        }
        else if (line.startsWith("CollectorProcessDefinition")) {
          cpds.add(CSVObjectFactory.buildCPD(line));
        }
        else if (line.startsWith("MeasurementPruningDefinition")) {
          gcds.add(CSVObjectFactory.buildMeasurementPruningDefinition(line));
        }
      }
      line = reader.readLine();
    }
  }
}
