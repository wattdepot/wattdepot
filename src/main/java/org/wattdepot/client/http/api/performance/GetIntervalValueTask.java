/**
 * GetIntervalValueTask.java This file is part of WattDepot.
 *
 * Copyright (C) 2014  Cam Moore
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
package org.wattdepot.client.http.api.performance;

import java.util.Date;

import org.wattdepot.common.exception.BadCredentialException;
import org.wattdepot.common.exception.BadSensorUriException;
import org.wattdepot.common.exception.IdNotFoundException;
import org.wattdepot.common.exception.NoMeasurementException;

/**
 * GetIntervalValueTask gets the value for the interval from the WattDepot Server.
 * 
 * @author Cam Moore
 * 
 */
public class GetIntervalValueTask extends PerformanceTimedTask {

  /** The start time of the value to retrieve. */
  private Date start;
  /** The end time of the value to retrieve. */
  private Date end;
  /**
   * Initializes the GetIntervalValueTask.
   * 
   * @param serverUri The URI for the WattDepot server.
   * @param username The name of a user defined in the WattDepot server.
   * @param orgId the id of the organization the user is in.
   * @param password The password for the user.
   * @param debug flag for debugging messages.
   * @throws BadCredentialException if the user or password don't match the
   *         credentials in WattDepot.
   * @throws IdNotFoundException if the processId is not defined.
   * @throws BadSensorUriException if the Sensor's URI isn't valid.
   */
  public GetIntervalValueTask(String serverUri, String username, String orgId, String password,
      boolean debug) throws BadCredentialException, IdNotFoundException, BadSensorUriException {
    super(serverUri, username, orgId, password, debug);
    Date latest = client.getLatestValue(depository, sensor).getDate();
    Date earliest = client.getEarliestValue(depository, sensor).getDate();
    Long oneThird = (earliest.getTime() + latest.getTime()) / 2;
    Long halfDiff = (latest.getTime() - earliest.getTime()) / 4;
    this.start = new Date(oneThird - halfDiff);
    this.end = new Date(oneThird + halfDiff);
  }


  /* (non-Javadoc)
   * @see org.wattdepot.client.http.api.performance.PerformanceTimedTask#clientTask()
   */
  @Override
  public void clientTask() {
    try {
      this.client.getValue(depository, sensor, start, end);
    }
    catch (NoMeasurementException nme) {
      nme.printStackTrace();
    }
  }

}
