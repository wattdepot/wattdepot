/**
 * MeasurementGarbageCollector.java This file is part of WattDepot.
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
package org.wattdepot.server.garbage.collector;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.XMLGregorianCalendar;

import org.wattdepot.common.domainmodel.GarbageCollectionDefinition;
import org.wattdepot.common.domainmodel.Measurement;
import org.wattdepot.common.exception.IdNotFoundException;
import org.wattdepot.common.util.DateConvert;
import org.wattdepot.common.util.tstamp.Tstamp;
import org.wattdepot.server.ServerProperties;
import org.wattdepot.server.WattDepotPersistence;

/**
 * MeasurementGarbageCollector - Removes measurements from the WattDepot
 * repository that are at a higher frequency sampling rate than desired.
 * 
 * @author Cam Moore
 * 
 */
public class MeasurementGarbageCollector extends TimerTask {

  private WattDepotPersistence persistance;
  private GarbageCollectionDefinition definition;

  /**
   * Create a MeasurementGarbageCollector.
   * 
   * @param properties The ServerProperties that define the type of persistence.
   * @param gcdId The id of the GarbageCollectionDefintion.
   * @param orgId The id of the Organization.
   * @throws Exception If there is a problem instantiating the
   *         WattDepotPersistence.
   */
  public MeasurementGarbageCollector(ServerProperties properties, String gcdId, String orgId)
      throws Exception {
    // Get the WattDepotPersistence implementation.
    String depotClass = properties.get(ServerProperties.WATT_DEPOT_IMPL_KEY);
    this.persistance = (WattDepotPersistence) Class.forName(depotClass)
        .getConstructor(ServerProperties.class).newInstance(properties);
    this.definition = this.persistance.getGarbageCollectionDefinition(gcdId, orgId, true);
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.util.TimerTask#run()
   */
  @Override
  public void run() {

  }

  /**
   * @return A list of measurements to garbage collect. They are at a higher
   *         sample rate than desired.
   * @throws IdNotFoundException if there is a problem with the
   *         GarbageCollectionDefinition.
   */
  public List<Measurement> getMeasurementsToDelete() throws IdNotFoundException {
    List<Measurement> ret = new ArrayList<Measurement>();
    List<Measurement> check = getMeasurementsToCheck();
    int size = check.size();
    int index = 1;
    int baseIndex = 0;
    while (index < size - 1) {
      long secondsBetween = Math.abs((check.get(index).getDate().getTime() - check.get(baseIndex)
          .getDate().getTime()) / 1000);
      if (secondsBetween < definition.getMinGapSeconds()) {
        ret.add(check.get(index++));
      }
      else {
        baseIndex = index;
        index++;
      }
    }
    return ret;
  }

  /**
   * @return The Measurements in the collection window.
   * @throws IdNotFoundException if there is a problem with the
   *         GarbageCollectionDefintion.
   */
  private List<Measurement> getMeasurementsToCheck() throws IdNotFoundException {
    Date start = getStartDate();
    Date end = getEndDate();
    return this.persistance.getMeasurements(this.definition.getDepositoryId(),
        this.definition.getOrgId(), this.definition.getSensorId(), end, start, false);
  }

  /**
   * @return The end of the collection window.
   */
  private Date getEndDate() {
    XMLGregorianCalendar now;
    Date ret = null;
    try {
      now = DateConvert.convertDate(new Date());
      XMLGregorianCalendar endCal = Tstamp.incrementDays(now, -1 * definition.getIgnoreWindowDays());
      endCal = Tstamp.incrementDays(endCal, -1 * definition.getCollectWindowDays());
      ret = DateConvert.convertXMLCal(endCal);
    }
    catch (DatatypeConfigurationException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return ret;
  }

  /**
   * @return The start of the collection window.
   */
  private Date getStartDate() {
    XMLGregorianCalendar now;
    Date ret = null;
    try {
      now = DateConvert.convertDate(new Date());
      XMLGregorianCalendar startCal = Tstamp.incrementDays(now, -1 * definition.getIgnoreWindowDays());
      ret = DateConvert.convertXMLCal(startCal);
    }
    catch (DatatypeConfigurationException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return ret;
  }
}
