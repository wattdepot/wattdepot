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
import org.wattdepot.common.domainmodel.MeasurementType;
import org.wattdepot.common.exception.IdNotFoundException;
import org.wattdepot.common.exception.UniqueIdException;
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
    this.typeSlug = getAttribute("measurement_type_id");
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.restlet.MeasurementTypeResource#retrieve()
   */
  @Override
  public MeasurementType retrieve() {
    getLogger().log(Level.INFO, "GET /wattdepot/measurement-type/{" + typeSlug + "}");
    MeasurementType mt = null;
    mt = depot.getMeasurementType(typeSlug);
    if (mt == null) {
      setStatus(Status.CLIENT_ERROR_EXPECTATION_FAILED, "MeasurementType " + typeSlug
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
        "POST /wattdepot/measurement-type/{" + typeSlug + "} with " + measurementType);
    if (isInRole("admin")) {
      MeasurementType mt = depot.getMeasurementType(measurementType.getId());
      if (mt != null) {
        depot.updateMeasurementType(measurementType);
      }
      else {
        setStatus(Status.CLIENT_ERROR_CONFLICT,
            "No such Measurement type defined. Cannot update undefined MeasurementType.");
      }
    }
    else {
      setStatus(Status.CLIENT_ERROR_FORBIDDEN, "Only admin may update MeasurementTypes.");
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.restlet.MeasurementTypeResource#remove()
   */
  @Override
  public void remove() {
    getLogger().log(Level.INFO, "DEL /wattdepot/measurement-type/{" + typeSlug + "}");
    if (isInRole("admin")) {
      try {
        depot.deleteMeasurementType(typeSlug);
      }
      catch (IdNotFoundException e) {
        setStatus(Status.CLIENT_ERROR_FAILED_DEPENDENCY, e.getMessage());
      }
    }
    else {
      setStatus(Status.CLIENT_ERROR_FORBIDDEN, "Only admin may remove MeasurementTypes.");
    }
  }

}
