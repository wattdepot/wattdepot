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
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

import org.wattdepot.common.domainmodel.CollectorProcessDefinition;
import org.wattdepot.common.domainmodel.Depository;
import org.wattdepot.common.domainmodel.Measurement;
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
  private Set<String> orgIds;
  private Map<String, List<Measurement>> measurementMap;

  /**
   * Creates a new DefinitionFileReader.
   * 
   * @param fileName The name of the definition file, a CSV file of WattDepot
   *        object definitions.
   * @throws IOException if there is a problem reading the file.
   * @throws ParseException if there is a problem with the measurements.
   */
  public DefinitionFileReader(String fileName) throws IOException, ParseException {
    this.reader = new BufferedReader(new FileReader(fileName));
    this.depositories = new HashSet<Depository>();
    this.groups = new HashSet<SensorGroup>();
    this.sensors = new HashSet<Sensor>();
    this.cpds = new HashSet<CollectorProcessDefinition>();
    this.gcds = new HashSet<MeasurementPruningDefinition>();
    this.orgIds = new HashSet<String>();
    this.measurementMap = new HashMap<String, List<Measurement>>();
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
   * @return the orgIds.
   */
  public Set<String> getOrgIds() {
    return orgIds;
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
   * @param depositoryId The depository to get the measurements from.
   * @return the Measurements.
   */
  public List<Measurement> getMeasurements(String depositoryId) {
    return measurementMap.get(depositoryId);
  }

  /**
   * @throws IOException if there is a problem reading the file.
   * @throws ParseException if there is a problem with the measurements.
   */
  private void processFile() throws IOException, ParseException {
    String line = reader.readLine();
    while (line != null) {
      line = line.trim();
      if (!line.startsWith("#") || line.length() > 0) {
        // decide what we have
        if (line.startsWith("Depository")) {
          Depository d = CSVObjectFactory.buildDepository(line);
          orgIds.add(d.getOrganizationId());
          depositories.add(d);
        }
        else if (line.startsWith("SensorGroup")) {
          SensorGroup sg = CSVObjectFactory.buildSensorGroup(line);
          orgIds.add(sg.getOrganizationId());
          groups.add(sg);
        }
        else if (line.startsWith("Sensor")) {
          Sensor s = CSVObjectFactory.buildSensor(line);
          orgIds.add(s.getOrganizationId());
          sensors.add(s);
        }
        else if (line.startsWith("CollectorProcessDefinition")) {
          CollectorProcessDefinition cpd = CSVObjectFactory.buildCPD(line);
          orgIds.add(cpd.getOrganizationId());
          cpds.add(cpd);
        }
        else if (line.startsWith("MeasurementPruningDefinition")) {
          MeasurementPruningDefinition mpd = CSVObjectFactory.buildMeasurementPruningDefinition(line);
          orgIds.add(mpd.getOrganizationId());
          gcds.add(mpd);
        }
        else if (line.startsWith("Measurement")) {
          DepositoryMeasurement depoMeas = CSVObjectFactory.buildMeasurement(line);
          List<Measurement> measurements = measurementMap.get(depoMeas.getDepositoryId());
          if (measurements == null) {
            measurements = new ArrayList<Measurement>();
            measurementMap.put(depoMeas.getDepositoryId(), measurements);
          }
          measurements.add(depoMeas.getMeasurement());
        }
      }
      line = reader.readLine();
    }
  }
}
