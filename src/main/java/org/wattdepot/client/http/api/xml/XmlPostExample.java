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

package org.wattdepot.client.http.api.xml;

import org.restlet.data.MediaType;
import org.restlet.ext.xml.DomRepresentation;
import org.restlet.representation.StringRepresentation;
import org.wattdepot.client.http.api.WattDepotClient;
import org.wattdepot.common.domainmodel.Depository;
import org.wattdepot.common.exception.BadCredentialException;
import org.wattdepot.common.exception.IdNotFoundException;
import org.wattdepot.common.util.tstamp.Tstamp;

import javax.xml.datatype.XMLGregorianCalendar;

/**
 * Trying to get performance information for the Xml Post.
 * @author Cam Moore
 */
public class XmlPostExample {

  /**
   * Runs the Xml Post example.
   * @param args ignored.
   */
  public static void main(String[] args) {
    // set up the WattDepotClient
    try {
      WattDepotClient client = new WattDepotClient("http://localhost:8192/", "xml", "testing-xml", "xml");
      Depository depository = client.getDepository("xmltest");
      Long start = System.nanoTime();
      int number = 1000;
      for (int i = 0; i < number; i++) {
        XMLGregorianCalendar now = Tstamp.makeTimestamp();
        DomRepresentation measRep = new DomRepresentation(new StringRepresentation(
            "<measurement><sensor>xmlsensor</sensor><power>" + (Math.random() * 14000.0) + "</power><timestamp>" + now + "</timestamp></measurement>",
            MediaType.TEXT_XML));
        client.putXmlMeasurement(depository, measRep);
      }
      Long end = System.nanoTime();
      System.out.println("It took " + ((end - start)/1e9) + " Seconds to store " + number + " measurements");
    }
    catch (BadCredentialException e) {
      e.printStackTrace();
    }
    catch (IdNotFoundException e) {
      e.printStackTrace();
    }

  }
}
