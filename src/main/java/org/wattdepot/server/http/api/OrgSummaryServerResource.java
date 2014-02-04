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

import javax.xml.datatype.XMLGregorianCalendar;

import org.restlet.data.LocalReference;
import org.restlet.data.MediaType;
import org.restlet.ext.freemarker.TemplateRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.Get;
import org.wattdepot.common.domainmodel.Depository;
import org.wattdepot.common.domainmodel.Labels;
import org.wattdepot.common.domainmodel.Organization;
import org.wattdepot.common.domainmodel.Sensor;
import org.wattdepot.common.domainmodel.SensorMeasurementSummary;
import org.wattdepot.common.exception.IdNotFoundException;
import org.wattdepot.common.exception.MisMatchedOwnerException;
import org.wattdepot.common.http.api.API;
import org.wattdepot.common.util.DateConvert;
import org.wattdepot.common.util.tstamp.Tstamp;

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
        List<Depository> depos = depot.getDepositories(orgId);
        List<Sensor> sensors = new ArrayList<Sensor>();
        List<SensorMeasurementSummary> minuteSummaries = new ArrayList<SensorMeasurementSummary>();
        List<SensorMeasurementSummary> hourSummaries = new ArrayList<SensorMeasurementSummary>();
        List<SensorMeasurementSummary> daySummaries = new ArrayList<SensorMeasurementSummary>();
        List<SensorMeasurementSummary> weekSummaries = new ArrayList<SensorMeasurementSummary>();
        List<SensorMeasurementSummary> monthSummaries = new ArrayList<SensorMeasurementSummary>();
        XMLGregorianCalendar now = Tstamp.makeTimestamp();
        XMLGregorianCalendar minAgo = Tstamp.incrementMinutes(now, -1);
        XMLGregorianCalendar hourAgo = Tstamp.incrementHours(now, -1);
        XMLGregorianCalendar dayAgo = Tstamp.incrementDays(now, -1);
//        XMLGregorianCalendar weekAgo = Tstamp.incrementDays(now, -7);
//        XMLGregorianCalendar monthAgo = Tstamp.incrementDays(now, -30);

        for (Depository d : depos) {
          for (String sensorId : depot.listSensors(d.getId(), orgId)) {
            sensors.add(depot.getSensor(sensorId, orgId));
            minuteSummaries.add(depot.getSummary(d.getId(), orgId, sensorId,
                DateConvert.convertXMLCal(minAgo), DateConvert.convertXMLCal(now)));
            hourSummaries.add(depot.getSummary(d.getId(), orgId, sensorId,
                DateConvert.convertXMLCal(hourAgo), DateConvert.convertXMLCal(now)));
            daySummaries.add(depot.getSummary(d.getId(), orgId, sensorId,
                DateConvert.convertXMLCal(dayAgo), DateConvert.convertXMLCal(now)));
//            weekSummaries.add(depot.getSummary(d.getId(), orgId, sensorId,
//                DateConvert.convertXMLCal(weekAgo), DateConvert.convertXMLCal(now)));
//            monthSummaries.add(depot.getSummary(d.getId(), orgId, sensorId,
//                DateConvert.convertXMLCal(monthAgo), DateConvert.convertXMLCal(now)));
          }
        }
        dataModel.put("depositories", depos);
        dataModel.put("sensors", sensors);
        dataModel.put("minuteSummaries", minuteSummaries);
        dataModel.put("hourSummaries", hourSummaries);
        dataModel.put("daySummaries", daySummaries);
        dataModel.put("weekSummaries", weekSummaries);
        dataModel.put("monthSummaries", monthSummaries);
        rep = new ClientResource(LocalReference.createClapReference(getClass().getPackage())
            + "/OrganizationSummary.ftl").get();
      }
      catch (IdNotFoundException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      catch (MisMatchedOwnerException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      template = new TemplateRepresentation(rep, dataModel, MediaType.TEXT_HTML);
      return template;
    }
    return null;
  }
}
