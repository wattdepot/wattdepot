/*
 * This file is part of WattDepot.
 *
 *  Copyright (C) 2015  Cam Moore
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.wattdepot.extension.openeis.http.api;

import org.restlet.data.LocalReference;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.freemarker.TemplateRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.Get;
import org.wattdepot.common.domainmodel.Depository;
import org.wattdepot.common.domainmodel.InterpolatedValue;
import org.wattdepot.common.domainmodel.Labels;
import org.wattdepot.common.domainmodel.Organization;
import org.wattdepot.common.domainmodel.Sensor;
import org.wattdepot.common.exception.IdNotFoundException;
import org.wattdepot.common.exception.MisMatchedOwnerException;
import org.wattdepot.common.exception.NoMeasurementException;
import org.wattdepot.common.http.api.API;
import org.wattdepot.extension.openeis.OpenEISLabels;
import org.wattdepot.server.http.api.WattDepotServerResource;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

/**
 * OpenEISUIServerResource - User interface for the OpenEIS algorithms. Handles /wattdepot/{orgId}/openeis/ui requests.
 *
 * @author Cam Moore
 *         Created by carletonmoore on 4/10/15.
 */
public class OpenEISUIServerResource extends WattDepotServerResource {

  /**
   * @return The Organization summary information as an HTML Representation.
   */
  @Get()
  public Representation toHtml() {
    getLogger().log(Level.INFO,
        "GET " + API.BASE_URI + "{" + orgId + "}/" + OpenEISLabels.OPENEIS + "/ui/");
    if (isInRole(orgId)) {
      Map<String, Object> dataModel = new HashMap<String, Object>();
      dataModel.put("orgId", orgId);
      Representation rep = null;
      TemplateRepresentation template = null;
      try {
        depot.getOrganization(orgId, true);
        // get the depositories
        List<Depository> depos = depot.getDepositories(orgId, false);
        for (Depository d : depos) {
          String typeName = d.getMeasurementType().getName();
          if (!typeName.startsWith("Power") && !typeName.startsWith("Energy") && !typeName.startsWith("Temperature") && !typeName.startsWith("Area")) {
            depos.remove(d); // remove the ones we aren't interested in.
          }
        }
        Map<String, List<Sensor>> depoSensors = new HashMap<String, List<Sensor>>();
        List<Sensor> sensors = new ArrayList<Sensor>();
        List<Sensor> powerSensors = new ArrayList<Sensor>();
        List<Sensor> temperatureSensors = new ArrayList<Sensor>();
        List<Sensor> energySensors = new ArrayList<Sensor>();
        for (Depository d : depos) {
          Map<String, List<Date>> sensorInfo = new HashMap<String, List<Date>>();
          List<Sensor> sensorList = new ArrayList<Sensor>();
          depoSensors.put(d.getId(), sensorList);
          for (String sensorId : depot.listSensors(d.getId(), orgId, false)) {
            Sensor s = depot.getSensor(sensorId, orgId, false);
            sensorList.add(s);
            if (!sensors.contains(s)) {
              sensors.add(s);
            }
            if (d.getMeasurementType().getName().startsWith("Power") && !powerSensors.contains(s)) {
              powerSensors.add(s);
            }
            else if (d.getMeasurementType().getName().startsWith("Energy") && !energySensors.contains(s)) {
              energySensors.add(s);
            }
            else if (d.getMeasurementType().getName().startsWith("Temperature") && !temperatureSensors.contains(s)) {
              temperatureSensors.add(s);
            }
            try {
              InterpolatedValue earliest = depot.getEarliestMeasuredValue(
                  d.getId(), orgId, sensorId, false);
              InterpolatedValue latest = depot.getLatestMeasuredValue(
                  d.getId(), orgId, sensorId, false);
              List<Date> info = new ArrayList<Date>();
              info.add(earliest.getStart());
              info.add(latest.getEnd());
              sensorInfo.put(sensorId, info);
            }
            catch (NoMeasurementException e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
            }
          }
        }
        dataModel.put("depositories", depos);
        dataModel.put("depoSensors", depoSensors);
        dataModel.put("sensors", sensors);
        dataModel.put("power_sensors", powerSensors);
        dataModel.put("energy_sensors", energySensors);
        dataModel.put("temperature_sensors", temperatureSensors);
      }
      catch (IdNotFoundException e) {
        setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
        return null;
      }
      catch (MisMatchedOwnerException e) {
        setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
        return null;
      }
      rep = new ClientResource(LocalReference.createClapReference(getClass()
          .getPackage()) + "/OpenEISUI.ftl").get();
      template = new TemplateRepresentation(rep, dataModel, MediaType.TEXT_HTML);
      return template;
    }
    return null;
  }
}
