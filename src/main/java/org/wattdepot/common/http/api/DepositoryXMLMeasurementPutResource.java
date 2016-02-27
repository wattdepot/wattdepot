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

package org.wattdepot.common.http.api;

import org.restlet.ext.xml.DomRepresentation;
import org.restlet.resource.Post;
import org.wattdepot.common.domainmodel.Measurement;

/**
 * DepositoryMeasurementResource - HTTP Interface for storing xml Measurements.
 *
 * @author Cam Moore
 *
 */
public interface DepositoryXMLMeasurementPutResource {

  /**
   * Defines PUT /wattdepot/{org-id}/depository/{depository_id}/measurement/
   * API call.
   *
   * @param measRep
   *          The Measurement to store.
   */
  @Post
  public void store(DomRepresentation measRep);

}
