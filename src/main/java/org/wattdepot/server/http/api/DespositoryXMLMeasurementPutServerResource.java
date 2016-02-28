/*
 * This file is part of WattDepot.
 *
 *  Copyright (C) 2016  Cam Moore
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
import org.restlet.ext.xml.DomRepresentation;
import org.restlet.resource.ResourceException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.wattdepot.common.domainmodel.Depository;
import org.wattdepot.common.domainmodel.Labels;
import org.wattdepot.common.domainmodel.Measurement;
import org.wattdepot.common.domainmodel.Sensor;
import org.wattdepot.common.exception.IdNotFoundException;
import org.wattdepot.common.exception.MeasurementTypeException;
import org.wattdepot.common.exception.MisMatchedOwnerException;
import org.wattdepot.common.http.api.DepositoryXMLMeasurementPutResource;
import org.wattdepot.common.util.DateConvert;
import org.wattdepot.common.util.UnitsHelper;
import org.wattdepot.common.util.tstamp.Tstamp;

import javax.measure.unit.SI;
import java.io.IOException;
import java.util.logging.Level;

/**
 * DepositoryXMLMeasurementPutServerResource - Handles the Measurement HTTP API
 * ("/wattdepot/{org-id}/depository/{depository_id}/xml/measurement/").
 *
 * @author Cam Moore
 *
 */
public class DespositoryXMLMeasurementPutServerResource extends WattDepotServerResource implements DepositoryXMLMeasurementPutResource {
  private String depositoryId;

  /*
   * (non-Javadoc)
   *
   * @see org.restlet.resource.Resource#doInit()
   */
  @Override
  protected void doInit() throws ResourceException {
    super.doInit();
    this.depositoryId = getAttribute(Labels.DEPOSITORY_ID);
  }

  /**
   * Stores the xml representation of a measurement.
   * @param measRep The XML representation of a measurement.
   */
  @Override
  public void store(DomRepresentation measRep) {
    getLogger().log(
        Level.INFO,
        "POST /wattdepot/{" + orgId + "}/depository/{" + depositoryId + "}/xml/measurement/ with " + measRep);
    if (isInRole(orgId)) {
      try {
        Document doc = measRep.getDocument();
        Element measElt = doc.getDocumentElement();
        Element sensorElt = (Element) measElt.getElementsByTagName("sensor").item(0);
        Element powerElt = (Element) measElt.getElementsByTagName("power").item(0);
        Element timestampElt = (Element) measElt.getElementsByTagName("timestamp").item(0);
        Measurement meas = new Measurement(sensorElt.getTextContent(), DateConvert.convertXMLCal(Tstamp.makeTimestamp(timestampElt.getTextContent())), Double.parseDouble(powerElt.getTextContent()), UnitsHelper.quantities.get(UnitsHelper.buildName("Power", SI.WATT)));
        getLogger().log(Level.INFO, meas.toString());
        //        store(meas);
        Depository depository = depot.getDepository(depositoryId, orgId, true);
        if (depository != null) {
          Sensor sensor = depot.getSensor(meas.getSensorId(), orgId, true);
          if (sensor != null) {
            depot.putMeasurement(depositoryId, orgId, meas);
          }
          else {
            setStatus(Status.CLIENT_ERROR_BAD_REQUEST, "Sensor " + meas.getSensorId() + " does not exist");
          }
        }
        else {
          setStatus(Status.CLIENT_ERROR_BAD_REQUEST, depositoryId + " does not exist.");
        }
      }
      catch (IOException e) {
        setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
      }
      catch (MeasurementTypeException e) {
        setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
      }
      catch (IdNotFoundException e) {
        setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
      }
      catch (MisMatchedOwnerException e) {
        setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
      }
      catch (Exception e) {
        setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
      }
    }
    else {
      setStatus(Status.CLIENT_ERROR_BAD_REQUEST, "Bad credentials.");
    }
  }
}
