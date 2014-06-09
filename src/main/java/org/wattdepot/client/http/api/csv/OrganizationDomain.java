/**
 * OrganizationDomain.java This file is part of WattDepot.
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
package org.wattdepot.client.http.api.csv;

import java.io.IOException;

import org.wattdepot.client.http.api.WattDepotClient;
import org.wattdepot.common.domainmodel.CollectorProcessDefinition;
import org.wattdepot.common.domainmodel.Depository;
import org.wattdepot.common.domainmodel.MeasurementPruningDefinition;
import org.wattdepot.common.domainmodel.Sensor;
import org.wattdepot.common.domainmodel.SensorGroup;
import org.wattdepot.common.exception.BadCredentialException;
import org.wattdepot.common.exception.BadSensorUriException;
import org.wattdepot.common.exception.IdNotFoundException;
import org.wattdepot.common.util.csv.DefinitionFileReader;
import org.wattdepot.common.util.csv.DefinitionFileWriter;

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
  public OrganizationDomain(String serverUri, String username, String orgId,
      String password, String fileName) throws BadCredentialException,
      IdNotFoundException, BadSensorUriException {
    this.client = new WattDepotClient(serverUri, username, orgId, password);
    this.fileName = fileName;
  }

  /**
   * Ensures that the WattDepot server has all the instances defined in the
   * definition file.
   * 
   * @throws IOException if there is a problem reading the file.
   */
  public void initializeFromFile() throws IOException {
    this.initializeFromFile(fileName);
  }

  /**
   * Ensures that the WattDepot server has all the instances defined in the
   * definition file.
   * 
   * @param name the name of the definition file.
   * @throws IOException if there is a problem reading the file.
   */
  public void initializeFromFile(String name) throws IOException {
    DefinitionFileReader reader = new DefinitionFileReader(name);
    for (Depository d : reader.getDepositories()) {
      if (!client.isDefinedDepository(d.getId())) {
        client.putDepository(d);
      }
    }
    for (Sensor s : reader.getSensors()) {
      if (!client.isDefinedSensor(s.getId())) {
        client.putSensor(s);
      }
    }
    for (SensorGroup g : reader.getSensorGroups()) {
      if (!client.isDefinedSensorGroup(g.getId())) {
        client.putSensorGroup(g);
      }
    }
    for (CollectorProcessDefinition cpd : reader
        .getCollectorProcessDefinitions()) {
      if (!client.isDefinedCollectorProcessDefinition(cpd.getId())) {
        client.putCollectorProcessDefinition(cpd);
      }
    }
    for (MeasurementPruningDefinition gcd : reader.getGarbageCollectionDefinitions()) {
      if (!client.isDefinedMeasurementPruningDefinition(gcd.getId())) {
        client.putMeasurementPruningDefinition(gcd);
      }
    }
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
    for (Depository d : client.getDepositories().getDepositories()) {
      writer.add(d);
    }
    for (Sensor s : client.getSensors().getSensors()) {
      writer.add(s);
    }
    for (SensorGroup g : client.getSensorGroups().getGroups()) {
      writer.add(g);
    }
    for (CollectorProcessDefinition cpd : client
        .getCollectorProcessDefinitions().getDefinitions()) {
      writer.add(cpd);
    }
    writer.writeFile();
  }
}
