/**
 * OrganizationDomain.java This file is part of WattDepot.
 * <p/>
 * Copyright (C) 2013  Cam Moore
 * <p/>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.wattdepot.client.http.api.csv;

import org.wattdepot.client.http.api.WattDepotClient;
import org.wattdepot.common.domainmodel.CollectorProcessDefinition;
import org.wattdepot.common.domainmodel.Depository;
import org.wattdepot.common.domainmodel.Measurement;
import org.wattdepot.common.domainmodel.MeasurementPruningDefinition;
import org.wattdepot.common.domainmodel.Sensor;
import org.wattdepot.common.domainmodel.SensorGroup;
import org.wattdepot.common.exception.BadCredentialException;
import org.wattdepot.common.exception.BadSensorUriException;
import org.wattdepot.common.exception.IdNotFoundException;
import org.wattdepot.common.exception.MeasurementTypeException;
import org.wattdepot.common.util.csv.DefinitionFileReader;
import org.wattdepot.common.util.csv.DefinitionFileWriter;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * OrganizationDomain - Provides way of initializing an Organization's domain
 * from a definition file and saving out an Organization's domain to a file.
 *
 * @author Cam Moore
 *
 */
public class OrganizationDomain {

  /** The client to communicate with the WattDepot server. */
  private WattDepotClient client;
  /** The name of the definition file. */
  private String fileName;

  /**
   * Initializes the OrganizationDomain.
   *
   * @param serverUri The URI for the WattDepot server.
   * @param username The name of a user defined in the WattDepot server.
   * @param orgId the id of the organization the user is in.
   * @param password The password for the user.
   * @param fileName The name of the definition file.
   * @throws BadCredentialException if the user or password don't match the
   *         credentials in WattDepot.
   * @throws IdNotFoundException if the processId is not defined.
   * @throws BadSensorUriException if the Sensor's URI isn't valid.
   */
  public OrganizationDomain(String serverUri, String username, String orgId, String password,
                            String fileName) throws BadCredentialException, IdNotFoundException, BadSensorUriException {
    this.client = new WattDepotClient(serverUri, username, orgId, password);
    this.fileName = fileName;
  }

  /**
   * Ensures that the WattDepot server has all the instances defined in the
   * definition file.
   *
   * @throws IOException if there is a problem reading the file.
   * @throws ParseException if there is a problem with the measurements.
   */
  public void initializeFromFile() throws IOException, ParseException {
    this.initializeFromFile(fileName);
  }

  /**
   * Ensures that the WattDepot server has all the instances defined in the
   * definition file.
   *
   * @param name the name of the definition file.
   * @throws IOException if there is a problem reading the file.
   * @throws ParseException if there is a problem with the measurements.
   */
  public void initializeFromFile(String name) throws IOException, ParseException {
    DefinitionFileReader reader = new DefinitionFileReader(name);
    loadDomainInfo(reader);
  }

  /**
   * Saves out the Organization's Depositories, Sensors, SensorGroups, and
   * CollectorProcessDefinitions.
   *
   * @throws IOException if there is a problem writing the file.
   */
  public void saveCurrentDomain() throws IOException {
    this.saveCurrentDomain(fileName);
  }

  /**
   * Saves out the Organization's Depositories, Sensors, SensorGroups, and
   * CollectorProcessDefinitions.
   *
   * @param name the name of the file to save to.
   * @throws IOException if there is a problem writing the file.
   */
  public void saveCurrentDomain(String name) throws IOException {
    DefinitionFileWriter writer = new DefinitionFileWriter(name);
    saveDomainInfo(writer);
    writer.writeFile();
  }

  /**
   * Exports the Organization's data from the start time to the end time.
   * @param start The start Date.
   * @param end The end Date.
   * @throws IOException If there is a problem with the file.
   */
  public void exportData(Date start, Date end) throws IOException {
    this.exportData(fileName, start, end);
  }

  /**
   * Exports the Organziation's data to the given file name, from start to end.
   * @param name The name of the file.
   * @param start The start Date.
   * @param end The end Date.
   * @throws IOException if there is a problem with the file.
   */
  public void exportData(String name, Date start, Date end) throws IOException {
    DefinitionFileWriter writer = new DefinitionFileWriter(name);
    saveDomainInfo(writer);
    Set<MeasurementPruningDefinition> mpds = writer.getMeasurementPruningDefinitions();
    for (Depository d : client.getDepositories().getDepositories()) {
      MeasurementPruningDefinition definition = findMDP(mpds, d);
      try {
        for (Sensor s : client.getDepositorySensors(d.getId()).getSensors()) {
          List<Measurement> measurements = client.getMeasurements(d, s, start, end).getMeasurements();
          if (definition != null) {
            measurements = pruneMeasurements(measurements, definition.getMinGapSeconds());
          }
          for (Measurement measurement : measurements) {
            writer.put(d, measurement);
          }
        }
      }
      catch (IdNotFoundException inf) {
        inf.printStackTrace();
      }
    }
    writer.writeFile();

  }

  /**
   * Prunes the measurements that are closer in time than the minGapSeconds.
   * @param measurements The list of measurements to prune.
   * @param minGapSeconds The minimum number of seconds between measurements.
   * @return The pruned list.
   */
  private List<Measurement> pruneMeasurements(List<Measurement> measurements, Integer minGapSeconds) {
    List<Measurement> measurementList = new ArrayList<Measurement>();
    Long start = -1l;
    for (Measurement m : measurements) {
      if (start == -1l) {
        start = m.getDate().getTime();
        measurementList.add(m);
      }
      else if (m.getDate().getTime() - start >= minGapSeconds * 1000) {
        start = m.getDate().getTime();
        measurementList.add(m);
      }
    }
    return measurementList;
  }

  /**
   * Imports the data from the exported file.
   * @throws IOException If there is a problem with the file.
   * @throws ParseException If there is a problem with the measurements.
   * @throws MeasurementTypeException If there is a missmatch between despository and measurement.
   */
  public void importData() throws IOException, ParseException, MeasurementTypeException {
    importData(fileName);
  }

  /**
   * Imports the organization's data from the given file name.
   * @param name The exported data file's name.
   * @throws IOException If there is a problem with the file.
   * @throws ParseException If there is a problem with the measurements.
   * @throws MeasurementTypeException If there is a missmatch between despository and measurement.
   */
  public void importData(String name) throws IOException, ParseException, MeasurementTypeException {
    DefinitionFileReader reader = new DefinitionFileReader(name);
    loadDomainInfo(reader);
    for (Depository d : reader.getDepositories()) {
      List<Measurement> measurements = reader.getMeasurements(d.getId());
      for (Measurement m : measurements) {
        client.putMeasurement(d, m);
      }
    }
  }

  /**
   * Gets the Organization's Domain information and saves it in the DefinitionFileWriter.
   * @param writer The DefinitionFileWriter.
   */
  private void saveDomainInfo(DefinitionFileWriter writer) {
    for (Depository d : client.getDepositories().getDepositories()) {
      writer.add(d);
    }
    for (Sensor s : client.getSensors().getSensors()) {
      writer.add(s);
    }
    for (SensorGroup g : client.getSensorGroups().getGroups()) {
      writer.add(g);
    }
    for (CollectorProcessDefinition cpd : client.getCollectorProcessDefinitions().getDefinitions()) {
      writer.add(cpd);
    }
    for (MeasurementPruningDefinition mpd : client.getMeasurementPruningDefinitions().getDefinitions()) {
      writer.add(mpd);
    }
  }

  /**
   * Loads just the organization's domain information.
   * @param reader The DefinitionFileReader that processed the file.
   * @throws IOException If there is a problem with the csv file.
   */
  private void loadDomainInfo(DefinitionFileReader reader) throws IOException {
    Set<String> orgIds = reader.getOrgIds();
    if (orgIds.size() != 1) {
      throw new IOException("Bad WattDepot definition file has more than one organization id.");
    }
    if (!orgIds.contains(client.getOrganizationId())) {
      throw new IOException(client.getOrganizationId()
          + " does not match the Organization ID in the file.");
    }
    for (Depository d : reader.getDepositories()) {
      if (client.getOrganizationId().equals(d.getOrganizationId())
          && !client.isDefinedDepository(d.getId())) {
        client.putDepository(d);
      }
    }
    for (Sensor s : reader.getSensors()) {
      if (client.getOrganizationId().equals(s.getOrganizationId())
          && !client.isDefinedSensor(s.getId())) {
        client.putSensor(s);
      }
    }
    for (SensorGroup g : reader.getSensorGroups()) {
      if (client.getOrganizationId().equals(g.getOrganizationId())
          && !client.isDefinedSensorGroup(g.getId())) {
        client.putSensorGroup(g);
      }
    }
    for (CollectorProcessDefinition cpd : reader.getCollectorProcessDefinitions()) {
      if (client.getOrganizationId().equals(cpd.getOrganizationId())
          && !client.isDefinedCollectorProcessDefinition(cpd.getId())) {
        client.putCollectorProcessDefinition(cpd);
      }
    }
    for (MeasurementPruningDefinition gcd : reader.getMeasurementPruningDefinitions()) {
      if (client.getOrganizationId().equals(gcd.getOrganizationId())
          && !client.isDefinedMeasurementPruningDefinition(gcd.getId())) {
        client.putMeasurementPruningDefinition(gcd);
      }
    }
  }

  /**
   * @param mdps The defined MeasurementPruningDefinitions.
   * @param d The Depository under consideration.
   * @return The MeasurementPruningDefinition for the depository.
   */
  private MeasurementPruningDefinition findMDP(Set<MeasurementPruningDefinition> mdps, Depository d) {
    for (MeasurementPruningDefinition mpd : mdps) {
      if (mpd.getDepositoryId().equals(d.getId())) {
        return mpd;
      }
    }
    return null;
  }
}
