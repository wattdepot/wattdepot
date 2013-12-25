/**
 * AdministratorServerResource.java This file is part of WattDepot.
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
package org.wattdepot.server.http.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.restlet.data.LocalReference;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.freemarker.TemplateRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.Get;
import org.restlet.security.User;
import org.wattdepot.common.domainmodel.CollectorProcessDefinition;
import org.wattdepot.common.domainmodel.Depository;
import org.wattdepot.common.domainmodel.MeasurementType;
import org.wattdepot.common.domainmodel.Sensor;
import org.wattdepot.common.domainmodel.SensorGroup;
import org.wattdepot.common.domainmodel.SensorLocation;
import org.wattdepot.common.domainmodel.SensorModel;
import org.wattdepot.common.domainmodel.Organization;
import org.wattdepot.common.domainmodel.UserInfo;
import org.wattdepot.server.depository.impl.hibernate.WattDepotPersistenceImpl;

/**
 * AdministratorServerResource - Administrative interface for WattDepot. It
 * handles the HTTP API ("/wattdepot/{org-id}/").
 * 
 * @author Cam Moore
 * 
 */
public class AdminServerResource extends WattDepotServerResource {

  /**
   * @return The admin user interface as an HTML Representation.
   */
  @Get()
  public Representation toHtml() {
    getLogger().log(Level.INFO, "GET /wattdepot/{" + orgId + "}/");
    if (!isInRole(orgId) && !isInRole("admin")) {
      User user = getClientInfo().getUser();
      UserInfo info = depot.getUser(user.getIdentifier());
      Organization group = depot.getUsersGroup(info);
      if (group != null) {
        redirectPermanent("/wattdepot/" + group.getSlug() + "/");
      }
      else {
        setStatus(Status.CLIENT_ERROR_FORBIDDEN);
      }
    }
    Map<String, Object> dataModel = new HashMap<String, Object>();
    // get some stuff from the database
    List<UserInfo> users = depot.getUsers();
    List<Organization> groups = depot.getOrganizations();
    List<Depository> depos = depot.getWattDepositories(orgId);
    List<SensorLocation> locs = depot.getLocations(orgId);
    List<Sensor> sensors = depot.getSensors(orgId);
    List<SensorModel> sensorModels = depot.getSensorModels();
    List<SensorGroup> sensorGroups = depot.getSensorGroups(orgId);
    List<CollectorProcessDefinition> sensorProcesses = depot.getCollectorProcessDefinitions(orgId);
    List<MeasurementType> measurementTypes = depot.getMeasurementTypes();
    dataModel.put("users", users);
    dataModel.put("groups", groups);
    dataModel.put("groupId", orgId);
    dataModel.put("depositories", depos);
    dataModel.put("locations", locs);
    dataModel.put("sensors", sensors);
    dataModel.put("sensorgroups", sensorGroups);
    dataModel.put("sensormodels", sensorModels);
    dataModel.put("sensorprocesses", sensorProcesses);
    dataModel.put("measurementtypes", measurementTypes);
    dataModel.put("opens", ((WattDepotPersistenceImpl) depot).getSessionOpen());
    dataModel.put("closes", ((WattDepotPersistenceImpl) depot).getSessionClose());
    Representation rep = new ClientResource(LocalReference.createClapReference(getClass()
        .getPackage()) + "/Admin.ftl").get();

    return new TemplateRepresentation(rep, dataModel, MediaType.TEXT_HTML);
  }
}
