var numRows = 0;
var wdClient = null;
var activeIndex = []; // The array containing the form indexes of the active
// form rows
var numShow = 0;
var loaded = false; // If the google visualization API has been loaded.
var dataArray = []; // The array of data tables retrieved from the queries
var numFinished = 0; // The number of queries finished
var canceled = false; // If the queries have been canceled.
var numDataPointsRetrieved; // The number of data points that have been
// retrieved in the current visualization query.
var totalNumPoints; // The total number of points needed for a visualization
// request.
var averageTimePerPoint; // The average time it takes to get a single data
// point.
var dataQueries = []; // The queries and other related data to get the data from
// the WattDepot Server
var maxQuerySize = 100; // The maximum size of a single query to the WattDepot
// server. Any larger query will be split.
var maxQuerySize = 100; // The maximum size of a single query to the WattDepot
// server. Any larger query will be split.
var totalNumQueries = 0; // The total number of queries (not subqueries).
var totalNumQueriesReturned = 0; // The total number of queries that have
// returned from the server.
var queryTimeOut = 300; // The number of seconds the application should wait
// before its queries time out.
var numDataTypeReturned = 0; // The number of data type queries that have
// returned.
var numVisExecuted = 0; // The number of visualizations executed since the page
// was loaded.
var currentVisID; // The ID of the visualization being executed.

/**
 * Adds an empty visualization selection row to the page.
 */
function addRow() {
  numRows++;
  if (findActiveIndex(numRows) == -1) {
    activeIndex.push({
      formIndex : numRows,
      disabled : false,
      depositorySelected : false
    });
  }
  insertRowHTML(numRows);
};

/**
 * Creates a filled in visualization selection row to the page.
 * 
 * @param show
 *          boolean whether to enable the row or not.
 * @param selectedDepository
 *          The selected depository.
 * @param selectedSensor
 *          The selected sensor.
 * @param startDate
 *          The start date of the visualization.
 * @param endDate
 *          The end date of the visualization.
 * @param now
 *          Whether to use now as the end date.
 * @param valueType
 *          The type of the InterpolatedValue to use.
 * @param frequency
 *          The time between interpolated values.
 */
function createFilledVisualizerForm(show, selectedDepository, selectedSensor,
    startDate, endDate, now, valueType, frequency) {

  var rowCopy = numRows;
  numRows++;
  insertRowHTML(rowCopy);

  // Update array of active form indexes
  if (findActiveIndex(rowCopy) == -1) {
    if (selectedDepository != "") {
      activeIndex.push({
        formIndex : rowCopy,
        disabled : !show,
        depositorySelected : true
      });
    } else {
      activeIndex.push({
        formIndex : rowCopy,
        disabled : !show,
        depositorySelected : false
      });
    }
    if (show) {
      numShow++;
    } else {
      $('#form' + rowCopy).css('background-color', '#ddd');
    }
  }
  // Select depository, sensor, value type
  selectDepository(rowCopy, selectedDepository);
  selectSensor(rowCopy, selectedSensor);
  setDate('start', rowCopy, startDate);
  if (!now) {
    setDate('end', rowCopy, endDate);
  } else {
    $("#endTimeNow" + rowCopy).checked = true;
  }
  selectValType(rowCopy, valueType);
  selectFrequency(rowCopy, frequency);
}

/**
 * Creates a time string from the given number of seconds.
 * 
 * @param seconds
 *          The number of seconds in the time string.
 */
function createTimeString(seconds) {
  var hours = 0;
  var minutes = 0;
  var returnString = "";

  if (seconds < 0) {
    seconds = 0;
  }

  while (seconds >= 3600) {
    hours++;
    seconds -= 3600;
  }
  while (seconds >= 60) {
    minutes++;
    seconds -= 60;
  }
  if (hours > 0) {
    returnString += hours.toString() + " hours ";
  }

  if (minutes > 0 || hours > 0) {
    returnString += minutes.toString() + " minutes ";
  }

  returnString += seconds.toString() + " seconds";
  return returnString;
}

/**
 * This function deletes a cookie of the given name by setting expiration time
 * to negative(removes on next update) Note: this makes it possible to remove a
 * cookie that doesn't exist prior to function call
 * 
 */
function deleteCookie(name) {
  makeCookie(name, "", -1);
}

/**
 * Finds the index of the object in activeIndex that corresponds to the given
 * formIndex.
 * 
 * @param formIndex
 *          The index of the form to find.
 * 
 * @return The index of the form in activeIndex, or -1 if it is not found.
 */
function findActiveIndex(formIndex) {
  for (var i = 0; i < activeIndex.length; i++) {
    if (activeIndex[i].formIndex == formIndex) {
      return i;
    }
  }
  return -1;
}

/**
 * This returns the data portion of a cookie given a name. If name doesn't
 * match, returns null.
 * 
 */
function getCookie(name) {
  var cName = name + "=";
  var info = document.cookie.split(';');
  for (var i = 0; i < info.length; i++) {
    var c = info[i];
    while (c.charAt(0) == ' ') {
      c = c.substring(1, c.length);
    }
    if (c.indexOf(cName) == 0) {
      return c.substring(cName.length, c.length);
    }

  }

  return null;
}

/**
 * Gets the Date for the specified time field.
 * 
 * @param id
 *          The id prefix of the time field to get (i.e. 'start' or 'end').
 * @param index
 *          The index of the time field form to get.
 * 
 * @return A Date representing the fields of the given form.
 */
function getDate(id, index) {
  var time = $('#' + id + 'datetimepicker' + index).data("DateTimePicker")
      .getDate();
  var error = false;
  if (time == null) {
    error = true;
    $('#' + id + 'datetimepicker' + index).addClass('invalid');
    $('#' + id + 'datetimepicker' + index).css('border', '3px red solid');
    $('#' + id + 'datetimepicker' + index)
        .wrap(
            '<a href="#" rel="tooltip" data-placement="top" title="Error: Time must be selected.">');
  }
  if (error) {
    return null;
  }
  if ($('#' + id + 'datetimepicker' + index).hasClass('invalid')) {
    $('#' + id + 'datetimepicker' + index).css('border', '');
    $('#' + id + 'datetimepicker' + index).unwrap();
    $('#' + id + 'datetimepicker' + index).removeClass('invalid');
  }
  return time.toDate();
}

/**
 * Get the timestamp string used in WattDepot server by given the date object in
 * javascript.
 * 
 * @param date
 *          A javascript date object.
 * @return the timestamp string.
 */
function getTimestampFromDate(date) {
  function padZero(number, length) {
    var str = '' + number;
    while (str.length < length) {
      str = '0' + str;
    }
    return str;
  }

  var timestamp = date.getFullYear() + '-' + padZero(date.getMonth() + 1, 2)
      + '-' + padZero(date.getDate(), 2);
  timestamp = timestamp + 'T' + padZero(date.getHours(), 2) + ':'
      + padZero(date.getMinutes(), 2) + ':' + padZero(date.getSeconds(), 2);
  timestamp = timestamp + '.' + padZero(date.getMilliseconds(), 3);
  return timestamp;
};

/**
 * Inserts the HTML code for a visualizer selection row.
 * 
 * @param index
 *          The index number of the row.
 */
function insertRowHTML(index) {
  $('#visualizerFormsDiv')
      .append(
          '<div class="row form" id="form'
              + index
              + '">'
              + '    <div id="depositoryDiv'
              + index
              + '" class="col-xs-3 control-group">'
              + '      <div class="col-xs-2">'
              + '      <label class="checkbox row-checkbox"><input type="checkbox" id="show'
              + index
              + '" value="show"></label>'
              + '      </div>'
              + '      <div class="col-xs-9">'
              + '        <select id="depositorySelect'
              + index
              + '" class="col-xs-12 depository-select" data-placehoder="Choose Depository..." onchange="selectedDepository('
              + index
              + ')" data-toggle="tooltip" title="Choose Depository...">'
              + '        </select>'
              + '      </div>'
              + '    </div>'
              + '    <div id="sensorDiv'
              + index
              + '" class="col-xs-2 control-group">'
              + '      <select id="sensorSelect'
              + index
              + '" class="col-xs-12 sensor-select" onchange="selectedSensor('
              + index
              + ')">'
              + '      </select>'
              + '    </div>'
              + '    <div class="form-group col-xs-2">'
              + '        <div class="input-group date" id="startdatetimepicker'
              + index
              + '">'
              + '            <input type="text" class="form-control" data-format="MM/DD/YY HH:mm"/>'
              + '            <span class="input-group-addon"><span class="glyphicon glyphicon-calendar"></span>'
              + '            </span>'
              + '        </div>'
              + '        <div id="startInfo'
              + index
              + '"></div>'
              + '    </div>'
              + '    <div class="form-group col-xs-2">'
              + '        <div class="row">'
              + '            <div class="input-group date col-xs-12" id="enddatetimepicker'
              + index
              + '">'
              + '                <input type="text" class="form-control" data-format="MM/DD/YY HH:mm"/>'
              + '                <span class="input-group-addon"><span class="glyphicon glyphicon-calendar"></span>'
              + '                </span>'
              + '            </div>'
              + '        </div>'
              + '        <div class="row">'
              + '            <div class="col-xs-12" id="endInfo'
              + index
              + '"></div>'
              + '        </div>'
              + '        <div class="row">'
              + '            <div class="col-xs-12"><label class="checkbox"><input type="checkbox" id="endTimeNow'
              + index + '" value="now">Now</label></div>' + '        </div>'
              + '    </div>' + '    <div class="col-xs-2 control-group">'
              + '      <select id="dataType' + index + '">'
              + '        <option value="point">Point Value</option>'
              + '        <option value="interval">Interval Value</option>'
              + '      </select>' + '    </div>'
              + '    <div class="col-xs-1 control-group">'
              + '      <select id="frequency' + index + '">'
              + '        <option value="5">5 mins</option>'
              + '        <option value="15">15 mins</option>'
              + '        <option value="30">30 mins</option>'
              + '        <option value="60">1 hr</option>'
              + '        <option value="120">2 hr</option>'
              + '        <option value="1400">1 Day</option>'
              + '        <option value="10080">1 Week</option>'
              + '        <option value="43200">1 Month</option>'
              + '      </select>' + '    </div> ' + '</div>');

  $("#show" + index).prop('checked', true);
  // Bind event for when Show checkbox is checked
  $("#show" + index).click(function() {
    var form = activeIndex[findActiveIndex(index)];
    if ($("#show" + index).is(":checked")) {
      if ($("#visualizeButton").prop("disabled")) {
        $('#visualizeButton').prop("disabled", false);
      }
      form.disabled = false;
      numShow++;
      $("#form" + index).css('background-color', '#fff');
    } else {
      form.disabled = true;
      numShow--;
      $("#form" + index).css('background-color', '#ddd');
    }
  });

  // Bind event for when the Now checkbox is checked
  $("#endTimeNow" + index).click(function() {
    if ($("#endTimeNow" + index).is(':checked')) {
      $("#enddatetimepicker" + index).data("DateTimePicker").disable();
    } else {
      $("#enddatetimepicker" + index).data("DateTimePicker").enable();
    }
  });

  $('#startdatetimepicker' + index).datetimepicker();
  $('#enddatetimepicker' + index).datetimepicker();

  // set up the depository options.
  for ( var key in DEPOSITORIES) {
    $("#depositorySelect" + index).append(
        "<option value=\"" + key + "\">" + DEPOSITORIES[key]['name']
            + "</option>");
  }
  selectedDepository(index);

};

/**
 * Loads the page either using the permalink or starting fresh.
 */
function loadPage() {
  wdClient = org.WattDepot.Client(server);

  if (permalinkCheck()) {
    fillPage();
  } else {
    addRow();
  }
}

/**
 * Creates a cookie with given 'name' Which will contain 'data' and expires an
 * amount of 'time' from now. Time is done in terms of days. Note: this also
 * works for overwriting a cookie with new information.
 * 
 */
function makeCookie(name, data, time) {
  if (time) {
    var date = new Date();
    date.setTime(date.getTime() + (time * 1000 * 60 * 60 * 24)); // sets date
    // to time in
    // days from
    // now.
    var expire = "; expires=" + date.toGMTString();
  } else {
    var expire = "";
  }

  document.cookie = name + "=" + data + expire + "; path=/";
}

/**
 * Merges the data tables and creates the visualization. Called when all
 * visualization subqueries have returned.
 */
function makeVisualization() {
  $('#progressLabel').empty();
  $('#progressLabel').append("Processing data...");
  if (dataArray.length >= 1) {
    var mergedTable = dataArray[0];
    var newColumn;

    // Merge tables
    for (var i = 1; i < dataArray.length; i++) {

      // Add
      for (var j = 1; j < dataArray[i].getNumberOfColumns(); j++) {
        newColumn = mergedTable.addColumn(dataArray[i].getColumnType(j),
            dataArray[i].getColumnLabel(j));
        for (var k = 0; k < dataArray[i].getNumberOfRows(); k++) {
          var matchingRows = mergedTable.getFilteredRows([ {
            column : 0,
            value : dataArray[i].getValue(k, 0)
          } ]);
          if (matchingRows.length < 1) {
            var newRow = mergedTable.addRow();
            mergedTable.setValue(newRow, 0, dataArray[i].getValue(k, 0));
            mergedTable
                .setValue(newRow, newColumn, dataArray[i].getValue(k, j));
          } else {
            for (var l = 0; l < matchingRows.length; l++) {
              mergedTable.setValue(matchingRows[l], newColumn, dataArray[i]
                  .getValue(k, j));
            }
          }
        }
      }
    }
    $('#visualizeProgressBar').width("100%");

    // Chart it!
    mergedTable.sort([ {
      column : 0
    } ]);
    $('#progressLabel').empty();
    $('#progressLabel').append("Visualizing data...");
    $('#chartDiv').show();
    $('#permalink').show();
    // $('#profile').show();
    // $('#newProfileDiv').show();
    $('#addButtonDiv').removeClass('offset3');
    var chart = new google.visualization.AnnotatedTimeLine(document
        .getElementById('chartDiv'));
    chart.draw(mergedTable, {
      displayAnnotations : false,
      legendPosition : 'newRow'
    });
  }

  // Update cookie with updated time per point in seconds.
  makeCookie('timePerPoint', averageTimePerPoint, 31);

  // Re-enable Visualize! Button
  $('#visualizeButton').prop("disabled", false);
  $('#visualizeButton').empty();
  $('#visualizeButton').append("Visualize!");
  $('#progressWindow').modal('hide');

  // Stop any still running queries
  stopQueries();

  // Free up memory for garbage collection.
  dataArray = null;
  dataQueries = null;
}

/**
 * Selects the depository for the given visualization selection row and id.
 * 
 * @param index
 *          the row number
 * @param depoId
 *          the depository id.
 */
function selectDepository(index, depoId) {
  $("#depositorySelect" + index).val(depoId);
  var sensors = DEPO_SENSORS[depoId]
  updateSensorSelection(index, sensors);
  selectedSensor(index);
}

/**
 * Gets the currently selected depository for the given row. Then updates the
 * choices of sensors.
 * 
 * @param index
 *          the row number.
 */
function selectedDepository(index) {
  var depoId = $("#depositorySelect" + index + " option:selected").val();
  var depoName = $("#depositorySelect" + index + " option:selected").text();
  var sensors = DEPO_SENSORS[depoId];
  updateSensorSelection(index, sensors);
  selectedSensor(index);
};

/**
 * Gets the selected depository and sensor and updates the start and end date
 * information.
 * 
 * @param index
 *          the visualization selection row.
 */
function selectedSensor(index) {
  var depoId = $("#depositorySelect" + index + " option:selected").val();
  var sensorId = $("#sensorSelect" + index + " option:selected").val();
  $("#startInfo" + index).remove();
  $("#startdatetimepicker" + index).parent().append(
      "<div id=\"startInfo" + index + "\"><small>Earliest: "
          + DEPO_SENSOR_INFO[depoId][sensorId]['earliest'] + "</small></div>");
  $("#endInfo" + index).remove();
  $("#enddatetimepicker" + index).parent().append(
      "<div id=\"endInfo" + index + "\" class=\"col-xs-12\"><small>Latest: "
          + DEPO_SENSOR_INFO[depoId][sensorId]['latest'] + "</small></div>");
};

/**
 * Sets the frequency selector in the given row to the given value.
 * 
 * @param index
 *          the row.
 * @param frequency
 *          the frequency.
 */
function selectFrequency(index, frequency) {
  $("#frequency" + index).val(frequency);
}

/**
 * Sets the selected sensor in the given visualization selection row.
 * 
 * @param index
 *          the index of the row.
 * @param sensorId
 *          The sensor id to select.
 */
function selectSensor(index, sensorId) {
  $("#sensorSelect" + index).val(sensorId);
  selectedSensor(index);
}

/**
 * Sets the InterpolatedValue type selector in the given row to the given value.
 * 
 * @param index
 *          the row.
 * @param type
 *          the value.
 */
function selectValType(index, type) {
  $("#dataType" + index).val(type);
}

/**
 * Sets the Date for the specified time field.
 * 
 * @param id
 *          The id prefix of the time field to get (i.e. 'start' or 'end').
 * @param index
 *          The index of the time field form to set.
 * @param value
 *          the value to set.
 * 
 * @return A Date representing the fields of the given form.
 */
function setDate(id, index, value) {
  $('#' + id + 'datetimepicker' + index).data("DateTimePicker").setDate(value);
}

/**
 * Splits the query for a source if it is too large.
 * 
 * @param server
 *          The WattDepot server to send the query to.
 * 
 * @param depository
 *          The depository the query is getting data for.
 * 
 * @param sensor
 *          The sensor the query is retrieving.
 * 
 * @param startTime
 *          The starting time of the query (should be a Javascript Date object).
 * 
 * @param endTime
 *          The ending time of the query (should be a Javascript Date object).
 * 
 * @param interval
 *          The interval at which data should be sampled in minutes.
 * 
 * @return An array containing the queries that resulted from splitting the
 *         original query.
 */
function splitQuery(server, depository, sensor, startTime, endTime, dataType,
    interval) {
  var returnArray = [];
  var tempTime = new Date(startTime.getTime());
  var query;
  tempTime.setMinutes(tempTime.getMinutes() + maxQuerySize * interval, tempTime
      .getSeconds(), tempTime.getMilliseconds());

  // Split the query
  while (tempTime < endTime) {
    query = new google.visualization.Query(server + ORGID + '/depository/'
        + depository + '/values/gviz/?sensor=' + sensor + '&start='
        + wdClient.getTimestampFromDate(startTime) + '&end='
        + wdClient.getTimestampFromDate(tempTime) + '&interval=' + interval
        + '&value-type=' + dataType);
    query.setQuery('select timePoint, ' + depository);
    query.setTimeout(queryTimeOut);
    returnArray.push(query);
    startTime = new Date(tempTime.getTime());
    tempTime.setMinutes(tempTime.getMinutes() + maxQuerySize * interval,
        tempTime.getSeconds(), tempTime.getMilliseconds());
  }

  // Make query for the remaining interval
  query = new google.visualization.Query(server + ORGID + '/depository/'
      + depository + '/values/gviz/?sensor=' + sensor + '&start='
      + wdClient.getTimestampFromDate(startTime) + '&end='
      + wdClient.getTimestampFromDate(endTime) + '&interval=' + interval
      + '&value-type=' + dataType);
  query.setQuery('select timePoint, ' + depository);
  query.setTimeout(queryTimeOut);
  returnArray.push(query);
  return returnArray;
}

/**
 * Stops the executing queries by stopping the browser.
 * 
 * Copied from wattdepot-apps by Edward Meyer, Kendyll Doi, Bao Huy Ung
 */
function stopQueries() {
  /**
   * particularly checks for Internet Explorer since it uses a different command
   * to stop the page from loading.
   */
  if (navigator.appName == "Microsoft Internet Explorer") {
    window.document.execCommand('Stop');
  }
  /** stops the page to other browsers specifications. */
  else {
    window.stop();
  }
  canceled = true;
}

/**
 * Updates the Sensor selection options of the visualization at the given row.
 * 
 * @param index
 *          the row's index.
 * @param sensors
 *          the list of sensors.
 */
function updateSensorSelection(index, sensors) {
  var select = $("#sensorSelect" + index);
  select.empty();
  select.append($("<optgroup>").attr("label", "Groups"));
  for ( var group in SENSORGROUPS) {
    select.append($("<option></option>").attr("value", group).text(
        SENSORGROUPS[group].name));
  }
  select.append($("</optgroup>"));
  var i = 0;
  var length = sensors.length;
  for (i = 0; i < length; i++) {
    select.append($("<option></option>").attr("value", sensors[i]).text(
        SENSORS[sensors[i]].name));
  }
};

function countGroups() {
  var count = 0;
  for ( var prop in SENSORGROUPS) {
    console.log(prop);
    count++;
  }
  return count;
}

/**
 * The response to a visualizer request. The callback function for each
 * visualize subquery.
 * 
 * @param response
 *          The response from the visualizer query.
 * 
 * @param queryIndex
 *          The index of the query in the dataQueries array.
 * 
 * @param startQueryTime
 *          The time at which the query was sent from the Date's getTime()
 *          method.
 * 
 * @param queryID
 *          The ID of the query that this response is for.
 */
function visualizerResponse(response, queryIndex, startQueryTime, queryID) {
  var table;
  var numColumns;
  var timeDiff;
  var timePerPoint;
  var numDataPoints;
  var depository;
  var sensor;

  // If the queries have been canceled, ignore the response.
  if (canceled || queryID != currentVisID) {
    return;
  }

  // If something went wrong with the query, display error and cancel queries.
  if (response.isError()) {
    alert('Error in query: ' + response.getMessage() + ' '
        + response.getDetailedMessage());
    // Re-enable Visualize! Button
    $('#visualizeButton').prop("disabled", false);
    $('#visualizeButton').empty();
    $('#visualizeButton').append("Visualize!");
    $('#progressWindow').modal('hide');
    canceled = true;
    return;
  }

  // Otherwise, query was successful and handle the data from the response.
  depository = dataQueries[queryIndex].depository;
  sensor = dataQueries[queryIndex].sensor;
  table = response.getDataTable();
  numColumns = table.getNumberOfColumns();

  // Fix labels
  for (var i = 1; i < numColumns; i++) {
    table.setColumnLabel(i, depository + ': ' + sensor + ' '
        + table.getColumnLabel(i));
  }
  if (dataQueries[queryIndex].queryResults == null) {
    dataQueries[queryIndex].queryResults = table;
  }

  // Merge tables
  else {
    var numRows = table.getNumberOfRows();
    var newRow;
    var matchingRows;
    for (i = 1; i < numColumns; i++) {
      for (var j = 0; j < numRows; j++) {
        // Check if row exists
        matchingRows = dataQueries[queryIndex].queryResults.getFilteredRows([ {
          column : 0,
          value : table.getValue(j, 0)
        } ]);

        // Add new row if needed
        if (matchingRows.length < 1) {
          newRow = dataQueries[queryIndex].queryResults.addRow();
          dataQueries[queryIndex].queryResults.setValue(newRow, 0, table
              .getValue(j, 0));
          dataQueries[queryIndex].queryResults.setValue(newRow, i, table
              .getValue(j, i));
        }

        // Else update matching rows
        else {
          for (var l = 0; l < matchingRows.length; l++) {
            dataQueries[queryIndex].queryResults.setValue(matchingRows[l], i,
                table.getValue(j, i));
          }
        }
      }
    }
  }
  dataQueries[queryIndex].numReturned++;
  totalNumQueriesReturned++;
  numDataPoints = table.getNumberOfRows()
  numDataPointsRetrieved += numDataPoints;

  // Calculate timings.
  timeDiff = new Date().getTime() - startQueryTime;
  timePerPoint = (timeDiff / numDataPoints) / 1000;

  if (averageTimePerPoint == 0) {
    averageTimePerPoint = timePerPoint;
  } else {
    averageTimePerPoint = (averageTimePerPoint + timePerPoint)
        / totalNumQueriesReturned;
  }

  // Update visuals.
  $('#visualizeProgressBar').width(
      (numDataPointsRetrieved / totalNumPoints) * 100 + "%");
  $('#progressLabel').empty();
  $('#progressLabel').append(
      "Getting data... " + Math.round(numDataPointsRetrieved) + " / "
          + Math.round(totalNumPoints) + " completed.");
  $('#timeLabel').empty();
  $('#timeLabel')
      .append(
          "Estimated Time Remaining: "
              + createTimeString(Math
                  .round((averageTimePerPoint * (totalNumPoints - numDataPointsRetrieved))))
              + ".");

  // If all of the queries for the source have returned, pus the data to the
  // completed data array.
  if (dataQueries[queryIndex].numReturned == dataQueries[queryIndex].queries.length) {
    dataArray[queryIndex] = dataQueries[queryIndex].queryResults;
    numFinished++;
    dataQueries[queryIndex] = null;
  }

  // Finished all queries
  if (numFinished == totalNumQueries) {
    makeVisualization();
  }
}

/**
 * Gets the number of data points requested by the form with the given index.
 * 
 * @param index
 *          The index of the form to calculate the number of data points of.
 * 
 * @return The number of data points requested by the specified form.
 */
function getNumDataPoints(index) {
  var timeInterval;
  var interval;
  var start, end;
  start = getDate("start", index);
  end = getDate("end", index);
  if (start > end) {
    timeInterval = start.getTime() - end.getTime();
  } else {
    timeInterval = end.getTime() - start.getTime();
  }

  interval = parseInt($('#frequency' + index + ' option:selected').val()) * 60 * 1000;
  return timeInterval / interval;

}

/**
 * Gets the total number of data points needed by all forms in the visualization
 * request.
 * 
 * @return The number of data points needed for the whole visualization request.
 */
function getTotalDataPoints() {
  var totalPoints = 0;
  var i;
  for (i = 0; i < activeIndex.length; i++) {
    if (!activeIndex[i].disabled) {
      totalPoints += getNumDataPoints(activeIndex[i].formIndex);
    }
  }
  return totalPoints;
}

function visualize() {

  // Disable visualize button
  $('#visualizeButton').prop("disabled", true);
  $('#visualizeButton').empty();
  $('#visualizeButton').append("Loading...");

  $('#chartDiv').hide();

  // Initialize required globals
  dataQueries = [];
  totalNumQueries = 0;
  var formIndex;
  var depository;
  var sensor;
  var interval;
  var startTime;
  var endTime;
  var dataType;
  var error = false;

  // Set up the queries
  for (var loopIndex = 0; loopIndex < activeIndex.length; loopIndex++) {
    if (activeIndex[loopIndex].disabled) {
      continue;
    }
    totalNumQueries++;
    formIndex = activeIndex[loopIndex].formIndex;
    depository = $("#depositorySelect" + formIndex + " option:selected").val();
    sensor = $("#sensorSelect" + formIndex + " option:selected").val();
    dataType = $("#dataType" + formIndex + " option:selected").val();
    interval = $('#frequency' + formIndex + ' option:selected').val();

    startTime = getDate('start', formIndex);
    if ($('#endTimeNow' + formIndex).is(':checked')) {
      endTime = new Date();
    } else {
      endTime = getDate('end', formIndex);
    }
    if (startTime == null || endTime == null) {
      error = true;
    } else {
      // If start is bigger than end, flip the two
      if (startTime > endTime) {
        var temp = endTime;
        endTime = startTime;
        startTime = temp;
      }
    }
    // Make sure interval is not too big
    if ((endTime - startTime) / 60000 < interval) {
      error = true;
      $('#frequency' + formIndex).addClass('invalid');
      $('#frequency' + formIndex).css('border', '3px red solid');
      $('#frequency' + formIndex)
          .wrap(
              '<a href="#" rel="tooltip" data-placement="bottom" title="Error: Interval is larger than the data range.  Please chooose a smaller interval." />');
    } else if ($('#frequency' + formIndex).hasClass('invalid')) {
      $('#frequency' + formIndex).unwrap();
      $('#frequency' + formIndex).css('border', '');
      $('#frequency' + formIndex).removeClass('invalid');
    }
    if (!error) {
      dataQueries.push({
        'depository' : depository,
        'sensor' : sensor,
        'numReturned' : 0,
        'queries' : splitQuery(server, depository, sensor, startTime, endTime,
            dataType, interval),
        'queryResults' : null
      });
    }

  }

  if (error) {
    $("[rel=tooltip]").tooltip();
    $("[rel=tooltip]").mouseout(function() {
      $(this).tooltip("hide");
    });
    alert("Error: Inputs are invalid.\nPlease check the fields in red.\nYou may hover overthem to get a tooltip describing the error.");
    $('#visualizeButton').prop("disabled", false);
    $('#visualizeButton').empty();
    $('#visualizeButton').append("Visualize!");
    $('#progressWindow').modal('hide');
    return;
  }

  // Initialize rest of the globals
  numFinished = 0;
  dataArray = [];
  canceled = false;
  totalNumPoints = getTotalDataPoints();
  numDataPointsRetrieved = 0;
  totalNumQueriesReturned = 0;

  // Set up progress modal
  $('#visualizeProgressBar').width("0%");
  $('#progressLabel').empty();
  $('#progressLabel').append(
      "Getting data..." + numDataPointsRetrieved + " / "
          + Math.round(totalNumPoints) + " completed.");

  // Set up timing variables.
  var timePerPoint = getCookie('timePerPoint');
  if (timePerPoint == null) {
    $('#timeLabel').empty();
    $('#timeLabel').append("Estimated Time Remaining: Unknown...");
    averageTimePerPoint = 0;
  } else {
    averageTimePerPoint = parseFloat(timePerPoint);
    $('#timeLabel').empty();
    $('#timeLabel')
        .append(
            "Estimated Time Remaining: "
                + createTimeString(Math.round(averageTimePerPoint
                    * totalNumPoints)) + ".");
  }

  // Show progress modal
  $('#progressWindow').modal({
    backdrop : 'static',
    keyboard : false
  });

  // Dummy function to create a closure in order
  // to preserve the proper index.
  var sendQuery = function(query, index, startQueryTime, queryID) {
    query.send(function(response) {
      visualizerResponse(response, index, startQueryTime, queryID);
    });
  }

  console.log(getUrl());
  $('#linkSpace').val(getUrl() + gatherVariables());
  currentVisID = $('#linkSpace').val() + numVisExecuted;
  numVisExecuted++;

  // Send the queries
  for (loopIndex = 0; loopIndex < dataQueries.length; loopIndex++) {
    for (var loopIndex2 = 0; loopIndex2 < dataQueries[loopIndex].queries.length; loopIndex2++) {
      sendQuery(dataQueries[loopIndex].queries[loopIndex2], loopIndex,
          new Date().getTime(), currentVisID);
    }
  }

}
