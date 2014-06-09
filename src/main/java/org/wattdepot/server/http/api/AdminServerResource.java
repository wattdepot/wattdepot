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
import org.wattdepot.common.domainmodel.MeasurementPruningDefinition;
import org.wattdepot.common.domainmodel.MeasurementType;
import org.wattdepot.common.domainmodel.Organization;
import org.wattdepot.common.domainmodel.Sensor;
import org.wattdepot.common.domainmodel.SensorGroup;
import org.wattdepot.common.domainmodel.SensorModel;
import org.wattdepot.common.domainmodel.UserInfo;
import org.wattdepot.common.exception.IdNotFoundException;
import org.wattdepot.common.http.api.API;

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
    getLogger().log(Level.INFO, "GET " + API.BASE_URI + "{" + orgId + "}/");
    if (!isInRole(orgId) && !isInRole(Organization.ADMIN_GROUP.getId())) {
      User user = getClientInfo().getUser();
      // The user isn't in the orgId. Search for the user in other
      // organizations.
      boolean foundUser = false;
      for (UserInfo ui : depot.getUsers()) {
        if (ui.getUid().equals(user.getIdentifier()) && isInRole(ui.getOrganizationId())) {
          redirectPermanent("/wattdepot/" + ui.getOrganizationId() + "/");
          foundUser = true;
        }
      }
      if (!foundUser) {
        setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
      }
    }
    else {
      Map<String, Object> dataModel = new HashMap<String, Object>();
      dataModel.put("orgId", orgId);
      Representation rep = null;
      TemplateRepresentation template = null;
      // decide what to show based upon the orgId.
      if (orgId.equals(Organization.ADMIN_GROUP.getId())) {
        // admin can manipulate users and organizations
        List<UserInfo> users = depot.getUsers();
        List<Organization> orgs = depot.getOrganizations();
        dataModel.put("users", users);
        dataModel.put("orgs", orgs);
        dataModel.put("rootUid", UserInfo.ROOT.getUid());
        dataModel.put("adminId", Organization.ADMIN_GROUP.getId());
        rep = new ClientResource(LocalReference.createClapReference(getClass().getPackage())
            + "/UserAdmin.ftl").get();
      }
      else {
        try {
          // regular organization, can manipulate sensors, depositories
          depot.getOrganization(orgId, true);
          List<Depository> depos = depot.getDepositories(orgId, false);
          List<Sensor> sensors = depot.getSensors(orgId, false);
          List<SensorModel> sensorModels = depot.getSensorModels();
          List<SensorGroup> sensorGroups = depot.getSensorGroups(orgId, false);
          List<CollectorProcessDefinition> cpds = depot
              .getCollectorProcessDefinitions(orgId, false);
          List<MeasurementType> measurementTypes = depot.getMeasurementTypes();
          List<MeasurementPruningDefinition> gcds = depot.getMeasurementPruningDefinitions(orgId,
              false);
          dataModel.put("depositories", depos);
          dataModel.put("sensors", sensors);
          dataModel.put("sensorgroups", sensorGroups);
          dataModel.put("sensormodels", sensorModels);
          dataModel.put("cpds", cpds);
          dataModel.put("measurementtypes", measurementTypes);
          dataModel.put("gcds", gcds);
          rep = new ClientResource(LocalReference.createClapReference(getClass().getPackage())
              + "/OrganizationAdmin.ftl").get();
        }
        catch (IdNotFoundException e) {
          setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
        }
      }
      template = new TemplateRepresentation(rep, dataModel, MediaType.TEXT_HTML);
      return template;
    }
    return null;
  }
}
