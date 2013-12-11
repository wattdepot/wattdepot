/**
 * DateConvert.java This file is part of WattDepot.
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
package org.wattdepot.common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * DateUtilities - Utility functions dealing with java.util.Date and
 * java.xml.datatype.XMLGregorianCalendar instances.
 * 
 * @author Cam Moore
 * 
 */
public class DateConvert {

  /**
   * Parses the given String representation of an XMLGregorianCalendar and
   * returns the instance.
   * 
   * @param s
   *          The String representation of an XMLGregorianCalendar with the
   *          format yyyy-MM-dd'T'HH:mm:ss.SSS
   * @return The XMLGregorianCalendar instance.
   * @throws ParseException
   *           if the string has the wrong format.
   * @throws DatatypeConfigurationException
   *           if there is a problems instantiating the DatatypeFactory.
   */
  public static XMLGregorianCalendar parseCalString(String s) throws ParseException,
      DatatypeConfigurationException {
    XMLGregorianCalendar result = null;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
    result = convertDate(simpleDateFormat.parse(s));
    return result;
  }

  /**
   * @param s
   *          The String representation in the format yyyy-MM-dd'T'HH:mm:ss.SSS.
   * @return The Date instance for the time.
   * @throws ParseException
   *           if the string has the wrong format.
   * @throws DatatypeConfigurationException
   *           if there is a problem initializing the DatatypeFactory.
   */
  public static Date parseCalStringToDate(String s) throws ParseException,
      DatatypeConfigurationException {
    return convertXMLCal(parseCalString(s));
  }

  /**
   * Converts a java.util.Date to a javax.xml.datatype.XMLGregorianCalendar.
   * 
   * @param date
   *          The Date to convert.
   * @return The XMLGregorianCalendar representation of the given Date.
   * @throws DatatypeConfigurationException
   *           if there is a problem instantiating the DatatypeFactory.
   */
  public static XMLGregorianCalendar convertDate(Date date) throws DatatypeConfigurationException {
    XMLGregorianCalendar result = null;
    GregorianCalendar gregorianCalendar = (GregorianCalendar) GregorianCalendar.getInstance();
    gregorianCalendar.setTime(date);
    result = DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);
    return result;
  }

  /**
   * Converts the given XMLGregorianCalendar to a java.util.Date.
   * 
   * @param xgc
   *          The XMLGregorianCalendar.
   * @return The Date representation of the XMLGregorianCalendar.
   */
  public static Date convertXMLCal(XMLGregorianCalendar xgc) {
    return xgc.toGregorianCalendar().getTime();
  }
}
