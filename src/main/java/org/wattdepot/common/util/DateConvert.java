/**
 * DateConvert.java This file is part of WattDepot.
 * <p/>
 * Copyright (C) 2013  Cam Moore
 * <p/>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.wattdepot.common.util;

import org.wattdepot.common.domainmodel.Labels;
import org.wattdepot.common.util.tstamp.Tstamp;

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
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
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

  /**
   * Returns the beginning of the hour or day given the time and hour or daily choice.
   * @param time              the current time.
   * @param hourlyDailyChoice how wide the window should be.
   * @return the beginning of the window based upon the current time and window width.
   */
  public static XMLGregorianCalendar getBeginning(XMLGregorianCalendar time, String hourlyDailyChoice) {
    XMLGregorianCalendar begin = null;
    if (hourlyDailyChoice.equals(Labels.HOURLY)) {
      begin = Tstamp.makeTimestamp(time.toGregorianCalendar().getTimeInMillis());
      begin.setMinute(0);
      begin.setSecond(0);
      begin.setMillisecond(0);
    }
    else if (hourlyDailyChoice.equals(Labels.DAILY)) {
      begin = Tstamp.makeTimestamp(time.toGregorianCalendar().getTimeInMillis());
      begin.setHour(0);
      begin.setMinute(0);
      begin.setSecond(0);
      begin.setMillisecond(0);
    }
    return begin;
  }

  /**
   * Returns the end of the hour or day give the time and the hour or daily choice.
   * @param time              the current time.
   * @param hourlyDailyChoice the width of the window.
   * @return the end of the window based upon the current time and the window width.
   */
  public static XMLGregorianCalendar getEnding(XMLGregorianCalendar time, String hourlyDailyChoice) {
    XMLGregorianCalendar end = null;
    if (hourlyDailyChoice.equals(Labels.HOURLY)) {
      end = Tstamp.incrementHours(time, 1);
      end.setMinute(0);
      end.setSecond(0);
      end.setMillisecond(0);
    }
    else if (hourlyDailyChoice.equals(Labels.DAILY)) {
      end = Tstamp.incrementDays(time, 1);
      end.setHour(0);
      end.setMinute(0);
      end.setSecond(0);
      end.setMillisecond(0);
    }
    return end;
  }


}
