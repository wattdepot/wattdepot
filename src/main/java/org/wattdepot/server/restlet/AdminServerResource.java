/**
 * AdministratorServerResource.java This file is part of WattDepot 3.
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
package org.wattdepot.server.restlet;

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
import org.wattdepot.datamodel.CollectorMetaData;
import org.wattdepot.datamodel.Depository;
import org.wattdepot.datamodel.MeasurementType;
import org.wattdepot.datamodel.Sensor;
import org.wattdepot.datamodel.SensorGroup;
import org.wattdepot.datamodel.SensorLocation;
import org.wattdepot.datamodel.SensorModel;
import org.wattdepot.datamodel.UserGroup;
import org.wattdepot.datamodel.UserInfo;
import org.wattdepot.server.depository.impl.hibernate.WattDepotImpl;

/**
 * AdministratorServerResource - Administrative interface for WattDepot. It
 * handles the HTTP API ("/wattdepot/{group_id}/").
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
    getLogger().log(Level.INFO, "GET /wattdepot/{" + groupId + "}/");
    if (!isInRole(groupId) && !isInRole("admin")) {
      User user = getClientInfo().getUser();
      UserInfo info = depot.getUser(user.getIdentifier());
      UserGroup group = depot.getUsersGroup(info);
      if (group != null) {
        redirectPermanent("/wattdepot/" + group.getId() + "/");
      }
      else {
        setStatus(Status.CLIENT_ERROR_FORBIDDEN);
      }
    }
    Map<String, Object> dataModel = new HashMap<String, Object>();
    // get some stuff from the database
    List<UserInfo> users = depot.getUsers();
    List<UserGroup> groups = depot.getUserGroups();
    List<Depository> depos = depot.getWattDepositories(groupId);
    List<SensorLocation> locs = depot.getLocations(groupId);
    List<Sensor> sensors = depot.getSensors(groupId);
    List<SensorModel> sensorModels = depot.getSensorModels();
    List<SensorGroup> sensorGroups = depot.getSensorGroups(groupId);
    List<CollectorMetaData> sensorProcesses = depot.getCollectorMetaDatas(groupId);
    List<MeasurementType> measurementTypes = depot.getMeasurementTypes();
    dataModel.put("users", users);
    dataModel.put("groups", groups);
    dataModel.put("groupId", groupId);
    dataModel.put("depositories", depos);
    dataModel.put("locations", locs);
    dataModel.put("sensors", sensors);
    dataModel.put("sensorgroups", sensorGroups);
    dataModel.put("sensormodels", sensorModels);
    dataModel.put("sensorprocesses", sensorProcesses);
    dataModel.put("measurementtypes", measurementTypes);
    dataModel.put("opens", ((WattDepotImpl) depot).getSessionOpen());
    dataModel.put("closes", ((WattDepotImpl) depot).getSessionClose());
    Representation rep = new ClientResource(LocalReference.createClapReference(getClass()
        .getPackage()) + "/Admin.ftl").get();

    return new TemplateRepresentation(rep, dataModel, MediaType.TEXT_HTML);
  }
}
