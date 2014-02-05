/**
 * MeasurementTypePutServerResource.java This file is part of WattDepot.
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
import org.wattdepot.common.domainmodel.MeasurementType;
import org.wattdepot.common.domainmodel.Organization;
import org.wattdepot.common.exception.BadSlugException;
import org.wattdepot.common.exception.IdNotFoundException;
import org.wattdepot.common.exception.UniqueIdException;
import org.wattdepot.common.http.api.MeasurementTypePutResource;

/**
 * MeasurementTypePutServerResource handles the HTTP PUT API for
 * MeasurementTypes.
 * 
 * @author Cam Moore
 * 
 */
public class MeasurementTypePutServerResource extends WattDepotServerResource
    implements MeasurementTypePutResource {

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.restlet.MeasurementTypeResource#store(org.wattdepot.datamodel
   * .MeasurementType)
   */
  @Override
  public void store(MeasurementType measurementType) {
    getLogger().log(Level.INFO,
        "PUT /wattdepot/public/measurement-type/ with " + measurementType);
    if (isInRole(Organization.ADMIN_GROUP.getId())) {
      MeasurementType mt = null;
      try {
        mt = depot.getMeasurementType(measurementType.getId());
      }
      catch (IdNotFoundException e1) { // NOPMD
        // possible out come.
      }
      if (mt == null) {
        try {
          depot.defineMeasurementType(measurementType.getId(),
              measurementType.getName(), measurementType.getUnits());
        }
        catch (UniqueIdException e) {
          setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
        }
        catch (BadSlugException e) {
          setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
        }
      }
    }
    else {
      setStatus(Status.CLIENT_ERROR_BAD_REQUEST,
          "Only admin may add new MeasurementTypes.");
    }
  }

}
