/**
 * DepositoryMeasurementsServerResource.java This file is part of WattDepot.
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

import org.wattdepot.common.domainmodel.MeasurementList;
import org.wattdepot.common.httpapi.GvizDepositoryMeasurementsResource;
import org.wattdepot.common.util.GvizHelper;

/**
 * DepositoryMeasurementsServerResource - Handles the Depository measurements
 * HTTP API ("/wattdepot/{group_id}/depository/{depository_id}/measurements/").
 * 
 * @author Cam Moore
 * 
 */
public class GvizDepositoryMeasurementsServerResource extends DepositoryMeasurementsServer implements
    GvizDepositoryMeasurementsResource {
  
  /** The GViz tqx query string. */
  private String tqxString = null;
  
  /** The GViz tq query string. */
  private String tqString = null;

  /**
   * Initialize with attributes from the Request.
   */
  @Override
  protected void doInit() {
    super.doInit();

    this.tqxString = GvizHelper.getGvizQueryString(this, "tqx");
    this.tqString = GvizHelper.getGvizQueryString(this, "tq");
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.restlet.DepositoryMeasurementsResource#retrieve()
   */
  @Override
  public String retrieve() {
    MeasurementList mList = doRetrieve();
    return GvizHelper.getGvizResponse(mList, tqxString, tqString);
  }

}
