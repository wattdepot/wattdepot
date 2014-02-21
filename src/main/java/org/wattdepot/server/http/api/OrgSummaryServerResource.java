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

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
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
import org.wattdepot.server.ServerProperties;

/**
 * OrgSummaryServerResource - Summary Web page generator for WattDepot
 * Organizations. It handles ("/wattdepot/{org-id}/summary/").
 * 
 * @author Cam Moore
 * 
 */
public class OrgSummaryServerResource extends WattDepotServerResource {

  private static DescriptiveStatistics averageGetTime = new DescriptiveStatistics();
  private static DescriptiveStatistics dbTime = new DescriptiveStatistics();

  /**
   * @return The Organization summary information as an HTML Representation.
   */
  @Get()
  public Representation toHtml() {
    getLogger().log(Level.INFO, "GET " + API.BASE_URI + "{" + orgId + "}/" + Labels.SUMMARY + "/");
    Long startTime = null;
    Long dbStartTime = null;
    Long totalMeasurements = 0l;
    boolean timingp = depot.getServerProperties().get(ServerProperties.SERVER_TIMING_KEY)
        .equals(ServerProperties.TRUE);
    if (timingp) {
      startTime = System.nanoTime();
    }
    if (isInRole(orgId) || isInRole(Organization.ADMIN_GROUP.getId())) {
      Map<String, Object> dataModel = new HashMap<String, Object>();
      dataModel.put("orgId", orgId);
      Representation rep = null;
      TemplateRepresentation template = null;
      try {
        if (timingp) {
          dbStartTime = System.nanoTime();
        }
        List<Depository> depos = depot.getDepositories(orgId);
        List<Sensor> sensors = new ArrayList<Sensor>();
        List<MeasurementRateSummary> summaries = new ArrayList<MeasurementRateSummary>();
        for (Depository d : depos) {
          for (String sensorId : depot.listSensors(d.getId(), orgId)) {
            sensors.add(depot.getSensor(sensorId, orgId));
            MeasurementRateSummary sum = depot.getRateSummary(d.getId(), orgId, sensorId);
            totalMeasurements += sum.getTotalCount();
            summaries.add(sum);
          }
        }
        dataModel.put("depositories", depos);
        dataModel.put("sensors", sensors);
        dataModel.put("summaries", summaries);
        if (depot.getServerProperties().get(ServerProperties.SERVER_TIMING_KEY)
            .equals(ServerProperties.TRUE)) {
          Long dbEndTime = System.nanoTime();
          Long diff = dbEndTime - dbStartTime;
          dbTime.addValue(diff / 1E9);
          getLogger().log(
              Level.SEVERE,
              "OrgSummary database time = " + (diff / 1E9) + " running average = "
                  + dbTime.getMean());
        }

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
      if (timingp) {
        Long endTime = System.nanoTime();
        Long diff = endTime - startTime;
        averageGetTime.addValue(diff / 1E9);
        getLogger().log(
            Level.SEVERE,
            "Building OrgSummary Page took " + (diff / 1E9) + " running average = "
                + averageGetTime.getMean() + " for " + totalMeasurements + " measurements");
      }
      return template;
    }
    return null;
  }
}
