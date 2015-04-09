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
import com.google.visualization.datasource.base.ReasonType;
import com.google.visualization.datasource.base.TypeMismatchException;
import com.google.visualization.datasource.datatable.ColumnDescription;
import com.google.visualization.datasource.datatable.DataTable;
import com.google.visualization.datasource.datatable.TableRow;
import com.google.visualization.datasource.datatable.value.ValueType;
import org.wattdepot.common.domainmodel.InterpolatedValue;
import org.wattdepot.common.domainmodel.InterpolatedValueList;

import javax.xml.datatype.DatatypeConfigurationException;
import java.util.ArrayList;

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

  public static DataTable getRow24HourPerDayDataTable(InterpolatedValueList valueList) {
    DataTable data = new DataTable();

    // Sets up the columns requested by any SELECT in the datasource query
    ArrayList<InterpolatedValue> values = valueList.getInterpolatedValues();
    if (!values.isEmpty()) {
      int numDays = values.size() / 24;
      String type = values.get(0).getMeasurementType().getUnits();
      data.addColumn(
          new ColumnDescription("Hour", ValueType.NUMBER, "Hour"));
      for (int i = 0; i < 24; i++) {
        data.addColumn(
            new ColumnDescription(type + i, ValueType.NUMBER, type));
      }
      for (int j = 0; j < numDays; j++) {
        TableRow row = new TableRow();
        row.addCell(j); // hour
        for (int k = 0; k < 24; k++) {
          row.addCell(values.get(j * 24 + k).getValue());
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
}
