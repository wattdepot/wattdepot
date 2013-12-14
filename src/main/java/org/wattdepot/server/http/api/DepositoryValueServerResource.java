/**
 * DepositoryValueServerResource.java This file is part of WattDepot.
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

import org.wattdepot.common.domainmodel.MeasuredValue;
import org.wattdepot.common.domainmodel.Sensor;
import org.wattdepot.common.exception.MeasurementGapException;
import org.wattdepot.common.exception.MissMatchedOwnerException;
import org.wattdepot.common.exception.NoMeasurementException;
import org.wattdepot.common.http.api.DepositoryValueResource;
import org.wattdepot.common.util.DateConvert;
import org.wattdepot.server.depository.impl.hibernate.DepositoryImpl;
//import org.wattdepot.common.httpapi.DepositoryValueResource;

/**
 * DepositoryValueServerResource - ServerResouce that handles the GET
 * /wattdepot/{group_id}/depository/{depository_id}/value/ response.
 * 
 * @author Cam Moore
 * 
 */
public class DepositoryValueServerResource extends DepositoryValueServer implements
    DepositoryValueResource {
  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.restlet.DepositoryValueResource#retrieve()
   */
  @Override
  public MeasuredValue retrieve() {
    return doRetrieve();
  }
}
