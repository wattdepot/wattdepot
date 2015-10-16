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

package org.wattdepot.server.http.api;

import org.restlet.data.Status;
import org.restlet.resource.ResourceException;
import org.wattdepot.common.domainmodel.CollectorProcessDefinition;
import org.wattdepot.common.domainmodel.Labels;
import org.wattdepot.common.domainmodel.Measurement;
import org.wattdepot.common.domainmodel.Sensor;
import org.wattdepot.common.domainmodel.SensorGroup;
import org.wattdepot.common.domainmodel.SensorStatus;
import org.wattdepot.common.domainmodel.SensorStatusEnum;
import org.wattdepot.common.domainmodel.SensorStatusList;
import org.wattdepot.common.exception.IdNotFoundException;
import org.wattdepot.common.exception.MisMatchedOwnerException;
import org.wattdepot.common.http.api.DepositorySensorStatusResource;
import org.wattdepot.common.util.DateConvert;
import org.wattdepot.common.util.tstamp.Tstamp;

import javax.xml.datatype.XMLGregorianCalendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;

/**
 * DepositorySensorStatusServerResource - Returns the SensorStatus(es) for the Sensor or SensorGroup.
 *
 * @author Cam Moore
 */
public class DepositorySensorStatusServerResource extends WattDepotServerResource implements DepositorySensorStatusResource {
  private String depositoryId;
  private String sensorId;
  private Integer numDays;

  /*
   * (non-Javadoc)
   *
   * @see org.restlet.resource.Resource#doInit()
   */
  @Override
  protected void doInit() throws ResourceException {
    super.doInit();
    this.sensorId = getQuery().getValues(Labels.SENSOR);
    this.depositoryId = getAttribute(Labels.DEPOSITORY_ID);
    String numStr = getAttribute(Labels.WINDOW);
    if (numStr != null) {
      try {
        this.numDays = Integer.parseInt(numStr);
      }
      catch (NumberFormatException nfe) {
        this.numDays = 7;
      }
    }
  }

  @Override
  public SensorStatusList retrieve() {
    getLogger().log(
        Level.INFO,
        "GET /wattdepot/{" + orgId + "}/" + Labels.DEPOSITORY + "/{" + depositoryId + "}/"
            + Labels.SENSOR_STATUS + "/?" + Labels.SENSOR + "={" + sensorId + "}&window={" + numDays + "}" );
    if (isInRole(orgId)) {
      SensorStatusList list = new SensorStatusList();
      try {
        XMLGregorianCalendar now = Tstamp.makeTimestamp();
        XMLGregorianCalendar weekAgo = Tstamp.incrementDays(now, -1 * numDays);
        Date endDate = DateConvert.convertXMLCal(now);
        Date startDate = DateConvert.convertXMLCal(weekAgo);
        depot.getDepository(depositoryId, orgId, true);
        Sensor sensor = depot.getSensor(sensorId, orgId, false);
        if (sensor != null) {
          SensorStatus status = new SensorStatus();
          status.setSensorId(sensorId);
          status.setTimestamp(now);
          String cpdName = sensorId + "-" + depositoryId;
          CollectorProcessDefinition cpd = depot.getCollectorProcessDefinition(cpdName, orgId, true);
          Long numExpectedMeasurements = 0l;
          if (cpd != null) {
            numExpectedMeasurements = Math.round(60.0 / cpd.getPollingInterval() * 60.0 * 24 * numDays);
          }
          List<Measurement> measurementList = depot.getMeasurements(depositoryId, orgId, sensorId, startDate, endDate, false);
          int count = measurementList.size();
          if (count > 0.8 * numExpectedMeasurements) {
            status.setStatus(SensorStatusEnum.GREEN);
          }
          else if (count > 0.5 * numExpectedMeasurements) {
            status.setStatus(SensorStatusEnum.YELLOW);
          }
          else if (count > 0) {
            status.setStatus(SensorStatusEnum.RED);
          }
          else {
            status.setStatus(SensorStatusEnum.BLACK);
          }
          list.getStatuses().add(status);
          return list;
        }
        else {
          SensorGroup group = depot.getSensorGroup(sensorId, orgId, false);
          if (group != null) {
            for (String s : group.getSensors()) {
              sensor = depot.getSensor(s, orgId, false);
              if (sensor != null) {
                SensorStatus status = new SensorStatus();
                status.setSensorId(s);
                status.setTimestamp(now);
                String cpdName = s + "-" + depositoryId;
                CollectorProcessDefinition cpd = depot.getCollectorProcessDefinition(cpdName, orgId, false);
                Long numExpectedMeasurements = 60l * 24 * numDays; // one measurement a minute.
                if (cpd != null) {
                  numExpectedMeasurements = Math.round(60.0 / cpd.getPollingInterval() * 60.0 * 24 * numDays);
                }
                List<Measurement> measurementList = depot.getMeasurements(depositoryId, orgId, s, startDate, endDate, false);
                int count = measurementList.size();
                if (count > 0.8 * numExpectedMeasurements) {
                  status.setStatus(SensorStatusEnum.GREEN);
                }
                else if (count > 0.5 * numExpectedMeasurements) {
                  status.setStatus(SensorStatusEnum.YELLOW);
                }
                else if (count > 0) {
                  status.setStatus(SensorStatusEnum.RED);
                }
                else {
                  status.setStatus(SensorStatusEnum.BLACK);
                }
                list.getStatuses().add(status);
              }
            }
            return list;
          }
        }
      }
      catch (IdNotFoundException e) {
        setStatus(Status.CLIENT_ERROR_BAD_REQUEST, depositoryId + " is not a Depository id.");
        return null;
      }
      catch (MisMatchedOwnerException e) {
        setStatus(Status.CLIENT_ERROR_BAD_REQUEST, depositoryId + " is not in organization " + orgId + ".");
        return null;
      }

      return list;
    }
    return null;
  }
}
