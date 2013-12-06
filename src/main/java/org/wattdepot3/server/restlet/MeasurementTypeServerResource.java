/**
 * MeasurementTypeServerResource.java This file is part of WattDepot 3.
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
package org.wattdepot3.server.restlet;

import java.util.logging.Level;

import org.restlet.data.Status;
import org.restlet.resource.ResourceException;
import org.wattdepot3.datamodel.MeasurementType;
import org.wattdepot3.exception.IdNotFoundException;
import org.wattdepot3.exception.UniqueIdException;
import org.wattdepot3.restlet.MeasurementTypeResource;

/**
 * MeasurementTypeServerResource - Handles the MeasurementType HTTP API
 * ("/wattdepot/measurementtype/" and
 * "/wattdepot/measurementtype/{measurementtype_id}").
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
    this.typeSlug = getAttribute("measurementtype_id");
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot3.restlet.MeasurementTypeResource#retrieve()
   */
  @Override
  public MeasurementType retrieve() {
    getLogger().log(Level.INFO, "GET /wattdepot/measurementtype/{" + typeSlug + "}");
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
   * org.wattdepot3.restlet.MeasurementTypeResource#store(org.wattdepot3.datamodel
   * .MeasurementType)
   */
  @Override
  public void store(MeasurementType measurementType) {
    getLogger().log(Level.INFO, "PUT /wattdepot/measurementtype/ with " + measurementType);
    MeasurementType mt = depot.getMeasurementType(measurementType.getId());
    if (mt == null) {
      try {
        depot.defineMeasurementType(measurementType.getName(), measurementType.getUnits());
      }
      catch (UniqueIdException e) {
        setStatus(Status.CLIENT_ERROR_CONFLICT, e.getMessage());
      }
    }
    else {
      depot.updateMeasurementType(measurementType);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot3.restlet.MeasurementTypeResource#remove()
   */
  @Override
  public void remove() {
    getLogger().log(Level.INFO, "DEL /wattdepot/measurementtype/{" + typeSlug + "}");
    try {
      depot.deleteMeasurementType(typeSlug);
    }
    catch (IdNotFoundException e) {
      setStatus(Status.CLIENT_ERROR_FAILED_DEPENDENCY, e.getMessage());
    }
  }

}
