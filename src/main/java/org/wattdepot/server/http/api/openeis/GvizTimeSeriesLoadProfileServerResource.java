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

package org.wattdepot.server.http.api.openeis;

import org.wattdepot.common.domainmodel.InterpolatedValueList;
import org.wattdepot.common.http.api.openeis.TimeSeriesLoadProfileGvizResource;
import org.wattdepot.common.util.GvizHelper;

/**
 * TimeSeriesLoadProfileServerResource - ServerResource that handles GET requests for OpenEIS Time Series Load Profiles.
 * @author Cam Moore
 */
public class GvizTimeSeriesLoadProfileServerResource extends TimeSeriesLoadProfileServer implements TimeSeriesLoadProfileGvizResource {
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

  @Override
  public String retrieve() {
    InterpolatedValueList mList = doRetrieve();
    if (mList != null) {
      return GvizHelper.getGvizResponse(mList, tqxString, tqString);
    }
    return null;
  }
}
