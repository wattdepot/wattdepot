/**
 * OrgSummaryServerResource.java This file is part of WattDepot.
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
package org.wattdepot.server.http.api;

import java.util.ArrayList;
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
import org.wattdepot.common.domainmodel.Depository;
import org.wattdepot.common.domainmodel.Labels;
import org.wattdepot.common.domainmodel.MeasurementRateSummary;
import org.wattdepot.common.domainmodel.Organization;
import org.wattdepot.common.domainmodel.Sensor;
import org.wattdepot.common.exception.IdNotFoundException;
import org.wattdepot.common.exception.MisMatchedOwnerException;
import org.wattdepot.common.exception.NoMeasurementException;
import org.wattdepot.common.http.api.API;

/**
 * OrgSummaryServerResource - Summary Web page generator for WattDepot
 * Organizations. It handles ("/wattdepot/{org-id}/summary/").
 * 
 * @author Cam Moore
 * 
 */
public class OrgSummaryServerResource extends WattDepotServerResource {

  /**
   * @return The Organization summary information as an HTML Representation.
   */
  @Get()
  public Representation toHtml() {
    getLogger().log(Level.INFO, "GET " + API.BASE_URI + "{" + orgId + "}/" + Labels.SUMMARY + "/");
    if (isInRole(orgId) || isInRole(Organization.ADMIN_GROUP.getId())) {
      Map<String, Object> dataModel = new HashMap<String, Object>();
      dataModel.put("orgId", orgId);
      Representation rep = null;
      TemplateRepresentation template = null;
      try {
        Long startTime = System.nanoTime();
        List<Depository> depos = depot.getDepositories(orgId);
        Long endTime = System.nanoTime();
        Long diff = endTime - startTime;
        getLogger().log(Level.INFO,
            "getDepositories took " + (diff / 1E9) + " seconds");
        List<Sensor> sensors = new ArrayList<Sensor>();
        List<MeasurementRateSummary> summaries = new ArrayList<MeasurementRateSummary>();
        for (Depository d : depos) {
          for (String sensorId : depot.listSensors(d.getId(), orgId)) {
            startTime = System.nanoTime();
            sensors.add(depot.getSensor(sensorId, orgId));
            endTime = System.nanoTime();
            diff = endTime - startTime;
            getLogger().log(Level.INFO,
                "getSensor took " + (diff / 1E9) + " seconds");
            startTime = System.nanoTime();
            summaries.add(depot.getRateSummary(d.getId(), orgId, sensorId));
            endTime = System.nanoTime();
            diff = endTime - startTime;
            getLogger().log(Level.INFO,
                "getRateSummary took " + (diff / 1E9) + " seconds");
          }
        }
        dataModel.put("depositories", depos);
        dataModel.put("sensors", sensors);
        dataModel.put("summaries", summaries);
        rep = new ClientResource(LocalReference.createClapReference(getClass().getPackage())
            + "/OrganizationSummary.ftl").get();
      }
      catch (IdNotFoundException e) {
        e.printStackTrace();
        setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
        return null;
      }
      catch (MisMatchedOwnerException e) {
        e.printStackTrace();
        setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
        return null;
      }
      catch (NoMeasurementException e) {
        e.printStackTrace();
        setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
        return null;
      }
      template = new TemplateRepresentation(rep, dataModel, MediaType.TEXT_HTML);
      return template;
    }
    return null;
  }
}
