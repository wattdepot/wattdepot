/**
 * GvizHelper.java This file is part of WattDepot.
 * <p/>
 * Copyright (C) 2013  Yongwen Xu
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

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.XMLGregorianCalendar;

import org.restlet.resource.ServerResource;
import org.wattdepot.common.domainmodel.InterpolatedValue;
import org.wattdepot.common.domainmodel.InterpolatedValueList;
import org.wattdepot.common.domainmodel.Measurement;
import org.wattdepot.common.domainmodel.MeasurementList;
import org.wattdepot.common.domainmodel.MeasurementType;

import com.google.visualization.datasource.base.DataSourceException;
import com.google.visualization.datasource.base.ReasonType;
import com.google.visualization.datasource.base.TypeMismatchException;
import com.google.visualization.datasource.datatable.ColumnDescription;
import com.google.visualization.datasource.datatable.DataTable;
import com.google.visualization.datasource.datatable.TableRow;
import com.google.visualization.datasource.datatable.value.DateTimeValue;
import com.google.visualization.datasource.datatable.value.ValueType;
import com.google.visualization.datasource.render.JsonRenderer;
import org.wattdepot.common.domainmodel.XYInterpolatedValue;
import org.wattdepot.common.domainmodel.XYInterpolatedValueList;

/**
 * GvizHelper - Utility class that handles Google Visualization using the Google Visualization
 * Datasource library. 
 *
 * * @see <a
 * href="http://code.google.com/apis/chart/interactive/docs/dev/implementing_data_source.html">Google
 * Visualization Datasource API</a>
 *
 * @author Yongwen Xu
 *
 */
public class GvizHelper {
  /** Conversion factor for milliseconds per minute. */
  private static final long MILLISECONDS_PER_MINUTE = 60L * 1000;

  /**
   * @param server
   *          the restlet ServerResource
   * @param type
   *          type of the gviz query string, such as tqx, or tq
   * @return the value of the gviz query string from the request
   */
  public static String getGvizQueryString(ServerResource server, String type) {
    String qs = server.getRequest().getResourceRef().getQueryAsForm().getFirstValue(type);
    try {
      if (qs != null) {
        qs = URLDecoder.decode(qs, "UTF-8");
      }
    }
    catch (UnsupportedEncodingException e) {
      qs = null;
    }
    return qs;
  }

  /**
   * @param resource
   *          server resource object
   * @param tqxString
   *          gviz tqx query string, i.e., request id
   * @param tqString
   *          gviz tq query string, selectable fields
   * @return gviz response 
   */
  public static String getGvizResponse(Object resource, String tqxString, String tqString) {
    DataTable table = null;
    try {
      if (resource instanceof InterpolatedValue) {
        table = getDataTable((InterpolatedValue) resource);
      }
      if (resource instanceof MeasurementList) {
        table = getDataTable((MeasurementList) resource);
      }
      if (resource instanceof InterpolatedValueList) {
        table = getDataTable((InterpolatedValueList) resource);
      }
      if (resource instanceof XYInterpolatedValueList) {
        table = getDataTable((XYInterpolatedValueList) resource);
      }
    }
    catch (DataSourceException e) {
      return getGvizDataErrorResponse(e);
    }

    return getGvizResponseFromDataTable(table, tqxString/*, tqString*/);
  }

  /**
   * @param mValue
   *          mesured value
   * @return gviz DataTable
   * @throws DataSourceException
   *          data source exception
   */
  private static DataTable getDataTable(InterpolatedValue mValue) throws DataSourceException {

    MeasurementType mType = mValue.getMeasurementType();

    DataTable data = new DataTable();

    try {
      data.addColumn(
          new ColumnDescription("Date", ValueType.DATETIME, "Date & Time"));
      data.addColumn(
          new ColumnDescription(mType.getName(), ValueType.NUMBER, mType.toString()));

      TableRow row = new TableRow();

      XMLGregorianCalendar xgcal = DateConvert.convertDate(mValue.getStart());
      row.addCell(new DateTimeValue(convertTimestamp(xgcal)));
      row.addCell(mValue.getValue());

      data.addRow(row);
    }
    catch (TypeMismatchException | DatatypeConfigurationException e) {
      throw new DataSourceException(ReasonType.INTERNAL_ERROR, "Problem adding data to table"); // NOPMD
    }
    return data;
  }

  /**
   * @param mList
   *          measurement list
   * @return gviz data table
   * @throws DataSourceException
   *          data source exception
   */
  private static DataTable getDataTable(MeasurementList mList) throws DataSourceException {
    DataTable data = new DataTable();

    // Sets up the columns requested by any SELECT in the datasource query
    ArrayList<Measurement> measurements = mList.getMeasurements();
    if (!measurements.isEmpty()) {
      String type = measurements.get(0).getMeasurementType();
      try {
        data.addColumn(
            new ColumnDescription("Timestamp", ValueType.DATETIME, "Date & Time"));
        data.addColumn(
            new ColumnDescription(type, ValueType.NUMBER, type));

        for (Measurement measurement : measurements) {
          TableRow row = new TableRow();
          XMLGregorianCalendar xgcal = DateConvert.convertDate(measurement.getDate());
          row.addCell(new DateTimeValue(convertTimestamp(xgcal)));
          row.addCell(measurement.getValue());
          data.addRow(row);
        }
      }
      catch (NumberFormatException e) {
        // String value in database couldn't be converted to a number.
        throw new DataSourceException(ReasonType.INTERNAL_ERROR, "Found bad number in database"); // NOPMD
      }
      catch (TypeMismatchException | DatatypeConfigurationException e) {
        throw new DataSourceException(ReasonType.INTERNAL_ERROR, "Problem adding data to table"); // NOPMD
      }
    }
    return data;
  }

  /**
   * @param mList
   *          measured value list
   * @return gviz data table
   * @throws DataSourceException
   *          data source exception
   */
  private static DataTable getDataTable(InterpolatedValueList mList) throws DataSourceException {
    DataTable data = new DataTable();

    // Sets up the columns requested by any SELECT in the datasource query
    ArrayList<InterpolatedValue> values = mList.getInterpolatedValues();
    if (!values.isEmpty()) {
      String type = values.get(0).getMeasurementType().getUnits();
      try {
        data.addColumn(
            new ColumnDescription("Timestamp", ValueType.DATETIME, "Date & Time"));
        data.addColumn(
            new ColumnDescription(type, ValueType.NUMBER, type));

        for (InterpolatedValue value : values) {
          TableRow row = new TableRow();
          XMLGregorianCalendar xgcal = DateConvert.convertDate(value.getStart());
          row.addCell(new DateTimeValue(convertTimestamp(xgcal)));
          if (value.getValue() != null) {
            row.addCell(value.getValue());
          }
          data.addRow(row);
        }
      }
      catch (NumberFormatException e) {
        // String value in database couldn't be converted to a number.
        throw new DataSourceException(ReasonType.INTERNAL_ERROR, "Found bad number in database"); // NOPMD
      }
      catch (TypeMismatchException | DatatypeConfigurationException e) {
        throw new DataSourceException(ReasonType.INTERNAL_ERROR, "Problem adding data to table"); // NOPMD
      }
    }
    return data;
  }

  /**
   * @param list The XYInterpolatedValueList.
   * @return The gviz data table.
   * @throws DataSourceException if there is a data source problem.
   */
  protected static DataTable getDataTable(XYInterpolatedValueList list) throws DataSourceException {
    DataTable data = new DataTable();

    ArrayList<XYInterpolatedValue> values = list.getValues();
    if (!values.isEmpty()) {
      String xType = values.get(0).getXMeasurementType().getUnits();
      String yType = values.get(0).getYMeasurementType().getUnits();
      try {
        data.addColumn(new ColumnDescription(xType, ValueType.NUMBER, xType));
        data.addColumn(new ColumnDescription(yType, ValueType.NUMBER, yType));

        for (XYInterpolatedValue value : values) {
          TableRow row = new TableRow();
          if (value.getXValue() != null && value.getYValue() != null) {
            row.addCell(value.getXValue());
            row.addCell(value.getYValue());
          }
          data.addRow(row);
        }

      }
      catch (NumberFormatException e) {
        // String value in database couldn't be converted to a number.
        throw new DataSourceException(ReasonType.INTERNAL_ERROR, "Found bad number in database"); // NOPMD
      }
      catch (TypeMismatchException e) {
        throw new DataSourceException(ReasonType.INTERNAL_ERROR, "Problem adding data to table"); // NOPMD
      }
    }
    return data;
  }

  /**
   * @param e
   *          DataSourceException
   * @return the error message
   */
  protected static String getGvizDataErrorResponse(DataSourceException e) {
    return "({status:'error',errors:[{reason:'"
        + e.getReasonType().toString() + "',message:'" + e.getMessageToUser() + "'}]});";
  }

  /**
   * @param table
   *          gviz data table
   * @param tqxString
   *          gviz query string
   * @return the gviz json response
   */
  protected static String getGvizResponseFromDataTable(DataTable table, String tqxString/*, String tqString*/) {
    String response = "google.visualization.Query.setResponse";

    String reqId = null;
    if (tqxString != null) {
      String[] tqxArray = tqxString.split(";");
      for (String s : tqxArray) {
        if (s.contains("reqId")) {
          reqId = s.substring(s.indexOf(":") + 1, s.length());
        }
      }
    }

    String tableString = JsonRenderer.renderDataTable(table, true, true, true).toString();

    response += "({status:'ok',";

    if (reqId != null) {
      response += "reqId:'" + reqId + "',";
    }
    response += "table:" + tableString + "});";

    return response;
  }

  /**
   * Takes an XMLGregorianCalendar (the native WattDepot timestamp format) and returns a 
   * com.ibm.icu.util.GregorianCalendar (<b>note:</b>
   * this is <b>not</b> the same as a java.util.GregorianCalendar!) with the time zone set to GMT,
   * but with the timestamp adjusted by the provided offset.
   *
   * For example, if the input value is 10 AM Hawaii Standard Time (HST is -10 hours from UTC), and
   * the offset is -600 minutes then the output will be 10 AM UTC, effectively subtracting 10 hours
   * from the timestamp (in addition to the conversion between types).
   *
   * This rigamarole is needed because the Google Visualization data source library only accepts
   * timestamps in UTC, and the Annonated Timeline visualization displays the UTC values. Without
   * this conversion, any graphs of meter data recorded in the Hawaii time zone (for instance) would
   * display the data 10 hours later in the graph, which is not acceptable. The timezoneOffset is
   * provided if in the future we want the capability to display data normalized to an arbitrary
   * time zone, rather than just the one the data was collected in.
   *
   * @param timestamp the input XMLGregorianCalendar timestamp
   * @return a com.ibm.icu.util.GregorianCalendar suitable for use by the Google visualization data
   * source library, and normalized by timeZoneOffset but with timeZone field set to GMT.
   */
  private static com.ibm.icu.util.GregorianCalendar convertTimestamp(XMLGregorianCalendar timestamp) {
    // Calendar conversion hell. GViz library uses com.ibm.icu.util.GregorianCalendar, which is
    // different from java.util.GregorianCalendar. So we go from our native format
    // XMLGregorianCalendar -> GregorianCalendar, extract milliseconds since epoch, feed that
    // to the IBM GregorianCalendar.

    int timeZoneOffset = timestamp.getTimezone();

    com.ibm.icu.util.GregorianCalendar gvizCal = new com.ibm.icu.util.GregorianCalendar();

    // Added bonus, DateTimeValue only accepts GregorianCalendars if the timezone is GMT.
    gvizCal.setTimeZone(com.ibm.icu.util.TimeZone.getTimeZone("GMT"));

    java.util.GregorianCalendar standardCal = timestamp.toGregorianCalendar();

    // Add the timeZoneOffset to the epoch time after converting to milliseconds
    gvizCal.setTimeInMillis(standardCal.getTimeInMillis()
        + timeZoneOffset * MILLISECONDS_PER_MINUTE);

    return gvizCal;
  }
}