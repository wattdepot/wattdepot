/**
 * DepositoryMeasurementServerResource.java This file is part of WattDepot.
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


import org.restlet.resource.ResourceException;
import org.wattdepot.common.domainmodel.*;

import org.wattdepot.common.http.api.DepositoryMeasurementPutResources;

/**
 * DepositoryMeasurementsServerResource - Handles the Measurements HTTP API
 * ("/wattdepot/{org-id}/depository/{depository_id}/measurements/").
 *
 * @author John Smedegaard
 *
 */
public class DepositoryMeasurementsPutServerResource extends WattDepotServerResource implements
        DepositoryMeasurementPutResources {
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

    /*
     * (non-Javadoc)
     *
     * @see
     * org.wattdepot.restlet.DepositoryMeasurementResource#store(org.wattdepot3
     * .datamodel.Measurement)
     */
    @Override
    public void store(MeasurementList measurementList) {
       //TODO will store each of the measurements in the measurementlist
    }
}
