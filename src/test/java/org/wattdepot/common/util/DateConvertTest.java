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

package org.wattdepot.common.util;

import org.junit.Test;

import javax.xml.datatype.XMLGregorianCalendar;
import java.text.ParseException;
import java.util.Date;

import static org.junit.Assert.*;

public class DateConvertTest {

  @Test
  public void testParseCalString() throws Exception {
    String testTime1 = "2015-02-17T11:51:32.000-1000";
    String testTime2 = "2015-02-17T11:51:32.000-00:00";
    String testTime3 = "2015-02-17T11:51:32.000-0900";
    XMLGregorianCalendar cal1 = DateConvert.parseCalString(testTime1);
    XMLGregorianCalendar cal2 = DateConvert.parseCalString(testTime2);
    XMLGregorianCalendar cal3 = DateConvert.parseCalString(testTime3);
    assertTrue("Got " + cal1 + " == " + cal2, cal1.getHour() == cal2.getHour() + 10);
    assertTrue("Got " + cal1 + " > " + cal3, cal1.getHour() > cal3.getHour());
  }


  @Test
  public void testParseCalStringToDate() throws Exception {
    String testTime1 = "2015-02-17T11:51:32.000-1000";
    String testTime2 = "2015-02-17T11:51:32.000";
    String testTime3 = "2015-02-17T11:51:32.000-0900";
    Date time1 = DateConvert.parseCalStringToDate(testTime1);
    Date time2 = null;
    try {
      time2 = DateConvert.parseCalStringToDate(testTime2);
    }
    catch (ParseException pe) {
      // expected.
    }
    Date time3 = DateConvert.parseCalStringToDate(testTime3);
    assertNotNull(time1);
    assertNull(time2);
    assertNotNull(time3);
    assertTrue(time1.compareTo(time3) > 0);
  }

  @Test
  public void testConvertDate() throws Exception {
//    fail("Not implemented.");
  }

  @Test
  public void testConvertXMLCal() throws Exception {
//    fail("Not implemented.");
  }
}