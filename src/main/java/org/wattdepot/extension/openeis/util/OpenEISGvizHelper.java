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

package org.wattdepot.extension.openeis.util;

import com.google.visualization.datasource.base.DataSourceException;
import com.google.visualization.datasource.base.TypeMismatchException;
import com.google.visualization.datasource.datatable.ColumnDescription;
import com.google.visualization.datasource.datatable.DataTable;
import com.google.visualization.datasource.datatable.TableRow;
import com.google.visualization.datasource.datatable.value.ValueType;
import com.google.visualization.datasource.render.JsonRenderer;
import org.wattdepot.common.domainmodel.InterpolatedValue;
import org.wattdepot.common.domainmodel.InterpolatedValueList;
import org.wattdepot.extension.openeis.domainmodel.XYInterpolatedValuesWithAnalysis;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Map;

/**
 * GvizHelper - Utility class that handles Google Visualization using the Google Visualization
 * Datasource library.
 * <p/>
 * * @see <a
 * href="http://code.google.com/apis/chart/interactive/docs/dev/implementing_data_source.html">Google
 * Visualization Datasource API</a>
 *
 * @author Cam Moore
 */
public class OpenEISGvizHelper extends org.wattdepot.common.util.GvizHelper {

  /**
   * Returns the daily DataTable based upon one value per hour.
   *
   * @param valueList The hourly values.
   * @return The DataTable with a row per day an 24 hourly entries.
   */
  public static DataTable getRow24HourPerDayDataTable(InterpolatedValueList valueList) {
    DataTable data = new DataTable();
    DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
    // Sets up the columns requested by any SELECT in the datasource query
    ArrayList<InterpolatedValue> values = valueList.getInterpolatedValues();
    if (!values.isEmpty()) {
      int numDays = values.size() / 24;
      data.addColumn(
          new ColumnDescription("Date", ValueType.TEXT, "Date"));
      for (int i = 0; i < 24; i++) {
        data.addColumn(
            new ColumnDescription("Hour" + i, ValueType.NUMBER, "" + i));
      }
      for (int j = 0; j < numDays; j++) {
        TableRow row = new TableRow();
        row.addCell(df.format(values.get(j * 24).getStart())); // hour
        for (int k = 0; k < 24; k++) {
          Double value = values.get(j * 24 + k).getValue();
          if (value != null) {
            row.addCell(value);
          }
        }
        try {
          data.addRow(row);
        }
        catch (TypeMismatchException e) {
          e.printStackTrace();
        }
      }
    }
    return data;
  }

  /**
   * @param resource  server resource object
   * @param tqxString gviz tqx query string, i.e., request id
   * @param tqString  gviz tq query string, selectable fields
   * @return gviz response
   */
  public static String getDailyGvizResponse(Object resource, String tqxString, String tqString) {
    DataTable table = null;
    if (resource instanceof InterpolatedValueList) {
      table = getRow24HourPerDayDataTable((InterpolatedValueList) resource);
    }
    return getGvizResponseFromDataTable(table, tqxString/*, tqString*/);
  }

  /**
   * @param resource  server resource object
   * @param tqxString gviz tqx query string, i.e., request id
   * @param tqString  gviz tq query string, selectable fields
   * @return gviz response
   */
  public static String getGvizResponse(Object resource, String tqxString, String tqString) {
    if (resource instanceof XYInterpolatedValuesWithAnalysis) {
      XYInterpolatedValuesWithAnalysis analysis = (XYInterpolatedValuesWithAnalysis) resource;
      try {
        DataTable table = getDataTable(analysis.getDataPoints());
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

        if (analysis.getAnalysis().entrySet().size() > 0) {
          response += "({status:'warning',";
        }
        else {
          response += "({status:'ok',";
        }


        if (reqId != null) {
          response += "reqId:'" + reqId + "',";
        }
        response += "warnings:[";
        DecimalFormat df = new DecimalFormat("0.00");
        for (Map.Entry e : analysis.getAnalysis().entrySet()) {
          response += "{reason:'" + e.getKey() + ": " + df.format(e.getValue()) + "'},";
        }
        response = response.substring(0, response.length() - 1);
        response += "],";

        response += "table:" + tableString + "});";

        return response;
      }
      catch (DataSourceException e) {
        return getGvizDataErrorResponse(e);
      }
    }
    else {
      return org.wattdepot.common.util.GvizHelper.getGvizResponse(resource, tqxString, tqString);
    }
  }

  /**
   * Returns the google visualization query string to create a benchmark column chart.
   * @param mList The list of values, the first value is the benchmark.
   * @param tqxString gviz tqx query string, i.e., request id
   * @param tqString  gviz tq query string, selectable fields
   * @return gviz response
   */
  public static String getBenchmarkGvizResponse(InterpolatedValueList mList, String tqxString, String tqString) {
    try {
      return getGvizResponseFromDataTable(getColumnDataTable(mList), tqxString);
    }
    catch (DataSourceException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * Returns the DataTable suitable for a Column chart.
   * @param mList The data.
   * @return The DataTable for a column chart.
   * @throws DataSourceException if there is a problem.
   */
  private static DataTable getColumnDataTable(InterpolatedValueList mList) throws DataSourceException {
    DataTable table = new DataTable();
    // Sets up the columns requested by any SELECT in the datasource query
    ArrayList<InterpolatedValue> values = mList.getInterpolatedValues();
    if (!values.isEmpty()) {
      String type = values.get(0).getMeasurementType().getUnits();
      table.addColumn(new ColumnDescription("Value", ValueType.TEXT, "Interval"));
      table.addColumn(new ColumnDescription(type, ValueType.NUMBER, type));
      boolean once = true;
      SimpleDateFormat format = new SimpleDateFormat("MM/dd/YY");

      for (InterpolatedValue value : values) {
        TableRow row = new TableRow();
        String label = "";
        if (once) {
          label = "Baseline: ";
          once = false;
        }
        row.addCell(label + format.format(value.getStart()) + " - " + format.format(value.getEnd()));
        if (value.getValue() != null) {
          row.addCell(value.getValue());
        }
        table.addRow(row);
      }
    }
    return table;
  }
}
