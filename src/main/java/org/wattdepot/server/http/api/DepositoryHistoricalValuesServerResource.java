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

package org.wattdepot.server.http.api;

import org.wattdepot.common.domainmodel.HistoricalValues;
import org.wattdepot.common.http.api.DepositoryHistoricalValuesResource;

/**
 * Server Resource that handles the GET requests for Historical Values.
 *
 * @author Cam Moore
 */
public class DepositoryHistoricalValuesServerResource extends DepositoryHistoricalValuesServer implements DepositoryHistoricalValuesResource {

  @Override
  public HistoricalValues retrieve() {
    return doRetrieve();
  }
}
