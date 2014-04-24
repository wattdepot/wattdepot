/**
 * MeasurementTypeServerResource.java This file is part of WattDepot.
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

import java.util.logging.Level;

import org.restlet.data.Status;
import org.restlet.resource.ResourceException;
import org.wattdepot.common.domainmodel.Labels;
import org.wattdepot.common.domainmodel.MeasurementType;
import org.wattdepot.common.domainmodel.Organization;
import org.wattdepot.common.exception.IdNotFoundException;
import org.wattdepot.common.http.api.MeasurementTypeResource;

/**
 * MeasurementTypeServerResource - Handles the MeasurementType HTTP API
 * ("/wattdepot/measurement-type/" and
 * "/wattdepot/measurement-type/{measurementtype-id}").
 * 
 * @author Cam Moore
 * 
 */
public class MeasurementTypeServerResource extends WattDepotServerResource implements
    MeasurementTypeResource {

  private String typeSlug;

  /*
   * (non-Javadoc)
   * 
   * @see org.restlet.resource.Resource#doInit()
   */
  @Override
  protected void doInit() throws ResourceException {
    super.doInit();
    this.typeSlug = getAttribute(Labels.MEASUREMENT_TYPE_ID);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.restlet.MeasurementTypeResource#retrieve()
   */
  @Override
  public MeasurementType retrieve() {
    getLogger().log(Level.INFO, "GET /wattdepot/public/measurement-type/{" + typeSlug + "}");
    MeasurementType mt = null;
    try {
      mt = depot.getMeasurementType(typeSlug, true);
    }
    catch (IdNotFoundException e) {
      setStatus(Status.CLIENT_ERROR_BAD_REQUEST, "MeasurementType " + typeSlug
          + " is not defined.");
    }
    return mt;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.restlet.MeasurementTypeResource#store(org.wattdepot.datamodel
   * .MeasurementType)
   */
  @Override
  public void update(MeasurementType measurementType) {
    getLogger().log(Level.INFO,
        "POST /wattdepot/public/measurement-type/{" + typeSlug + "} with " + measurementType);
    if (isInRole(Organization.ADMIN_GROUP.getId())) {
      MeasurementType mt;
      try {
        mt = depot.getMeasurementType(measurementType.getId(), true);
        if (mt != null) {
          depot.updateMeasurementType(measurementType);
        }
      }
      catch (IdNotFoundException e) {
        setStatus(Status.CLIENT_ERROR_BAD_REQUEST,
            "No such Measurement type defined. Cannot update undefined MeasurementType.");
      }
    }
    else {
      setStatus(Status.CLIENT_ERROR_BAD_REQUEST, "Only admin may update MeasurementTypes.");
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.restlet.MeasurementTypeResource#remove()
   */
  @Override
  public void remove() {
    getLogger().log(Level.INFO, "DEL /wattdepot/public/measurement-type/{" + typeSlug + "}");
    if (isInRole(Organization.ADMIN_GROUP.getId())) {
      try {
        depot.deleteMeasurementType(typeSlug);
      }
      catch (IdNotFoundException e) {
        setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
      }
    }
    else {
      setStatus(Status.CLIENT_ERROR_BAD_REQUEST, "Only admin may remove MeasurementTypes.");
    }
  }

}
