/**
 * This is a function activated after confirming to load the unit test
 * 
 */
function startUT() {
  $('#modalUT').show();
}

var serverAddress; // The current address of the server.
var sources; // The array of sources
var wdClient = null; // The client for querying the WattDepot server
var formIndex = 0; // The current index of the form rows.
var activeIndex = []; // The array containing the form indexes of the active
                      // form rows
var numShow = 0; // The number of active forms that will be shown on the
                  // visualization.
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
var numProfiles = 0; // The number of profiles available

/**
 * Call back function that gets the sources from the client, puts them in the
 * global sources array, and creates the initial source form.
 * 
 * @param data
 *          The data returned by the Ajax call.
 */
function putSources(data) {
  numDataTypeReturned = 0;
  sources = new Array();
  var sensors = eval('(' + data + ')').sensors;
  for (var i = 0; i < sensors.length; i++) {

    // Get the source name
    var source = sensors[i].name;

    sources.push({
      sourceName : source
    });
  }

  // Hide the landing and show the control panel
  $('#visualizerLanding').hide();
  $('#visualizerControlPanel').show();

  $('#formLabels').show();

  if (permalinkCheck()) {
    fillPage();
  } else {
    createVisualizerForm();
  }
}

/**
 * Gets all of the available sources from the WattDepot server or produces an
 * error if the Ajax call fails.
 */
function getSources() {
  if (wdClient == null) {
    alert("Please select a server and submit first please!");
    return;
  } else {
    $('#visualizerControlPanel').hide();
    $('#chartDiv').hide();
    $('#visualizerFormsDiv').empty();
    serverAddress = $('#serverAddress').val();
    sources = null;
    wdClient.getAllSource(putSources, function(xhr) {
      var errorString = "There was an error retrieving the sources from: \n\""
          + $('#serverAddress').val() + "\".\n\n";
      if (xhr.status == 0) {
        errorString += "Server Address is invalid.";
      } else if (xhr.status == 401) {
        errorString += "You are not authorized to access this server."
      } else if (xhr.status == 500) {
        errorString += "Internal server error."
      }
      alert(errorString);
    });
  }
}

/**
 * Loads the WattDepot client to the server and gets the sources from the
 * server. To be called onload.
 */
function loadServer() {

  $('#serverAddress').val(defaultServer);

  // Get client
  var tempServerName = permalinkServer($('#serverAddress').val());

  wdClient = org.WattDepot.Client(tempServerName);

  // Set up server history
  $('#shDefault').append(
      '<li><a onclick="addrClick(\'' + defaultServer + '\')">' + defaultServer
          + '</a></li>');
  seedServerHistory();

  // Load profiles
  $('#profileSelect').empty();
  var proNum = getCookie('proNum');
  if (proNum != null) {
    proNum = parseInt(proNum)
    var link;
    var name;
    for (var i = 0; i < proNum; i++) {
      link = getCookie('proLink' + i);
      name = getCookie('proName' + i);
      if (link == null || name == null) {
        continue;
      }
      $('#profileSelect').append(
          '<option value="' + decodeURIComponent(link) + '">'
              + decodeURIComponent(name) + '</option>');
      numProfiles++;
    }
    $('#profileSelect').prop("disabled", false);
  }

  // Get all sources
  getSources();
}

/**
 * Pads the given value with a leading zero if it positive and is less than 10.
 * 
 * @param value
 *          The value to pad. Should be a positive integer.
 * 
 * @return The padded value.
 */
function padWithZero(value) {
  if (value < 10 && value >= 0) {
    return '0' + value;
  } else {
    return value;
  }
}

/**
 * Generates an option element HTML string for the given value.
 * 
 * @param value
 *          The value to generate an option string for. Should be a positive
 *          integer.
 * 
 * @return The HTML string for the option element.
 */
function generateTimeOptionString(value) {
  var optionString = '<option value="';
  optionString += padWithZero(value);
  optionString += '">';
  optionString += padWithZero(value);
  optionString += '</option>';
  return optionString;
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
  var time = $('#' + id + 'TimePicker' + index).val();
  var date = $('#' + id + 'Datepicker' + index).val();
  var error = false;
  var timePattern = /^\d{2}:\d{2}:\d{2}$/;
  var datePattern = /^\d{1,2}\/\d{1,2}\/\d{4}$/;
  if (!timePattern.test(time)) {
    error = true;
    $('#' + id + 'TimePicker' + index).addClass('invalid');
    $('#' + id + 'TimePicker' + index).css('border', '3px red solid');
    $('#' + id + 'TimePicker' + index)
        .wrap(
            '<a href="#" rel="tooltip" data-placement="top" title="Error: Time must be in the format (HH:MM:SS)">');
  }

  if (!datePattern.test(date)) {
    error = true;
    $('#' + id + 'Datepicker' + index).addClass('invalid');
    $('#' + id + 'Datepicker' + index).css('border', '3px red solid');
    $('#' + id + 'Datepicker' + index)
        .wrap(
            '<a href="#" rel="tooltip" data-placement="top" title="Error: Date must be in the format (MM/DD/YYYY)">');
  }
  if (error) {
    return null;
  }

  if ($('#' + id + 'TimePicker' + index).hasClass('invalid')) {
    $('#' + id + 'TimePicker' + index).css('border', '');
    $('#' + id + 'TimePicker' + index).unwrap();
    $('#' + id + 'TimePicker' + index).removeClass('invalid');
  }

  if ($('#' + id + 'Datepicker' + index).hasClass('invalid')) {
    $('#' + id + 'Datepicker' + index).css('border', '');
    $('#' + id + 'Datepicker' + index).unwrap();
    $('#' + id + 'Datepicker' + index).removeClass('invalid');
  }

  time = time.split(':');
  date = date.split('/');
  return new Date(date[2], date[0] - 1, date[1], time[0], time[1], time[2], 0);

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

/**
 * Gets the larges number of data points requested by a form.
 * 
 * @returns The maximum number of data points requested by the form in the
 *          current visualization query.
 */
function getMaxDataPoints() {
  var max = 0;
  var current;
  var i;
  for (i = 0; i < activeIndex.length; i++) {
    current = getNumDataPoints(activeIndex[i].formIndex);
    if (max < current) {
      max = current;
    }
  }
  return max;
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

  interval = parseInt($('#interval' + index + ' option:selected').val()) * 60 * 1000;
  return timeInterval / interval;

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
  var source;

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
  source = dataQueries[queryIndex].source;
  table = response.getDataTable();
  numColumns = table.getNumberOfColumns();

  // Fix labels
  for (var i = 1; i < numColumns; i++) {
    table.setColumnLabel(i, source + ' ' + table.getColumnLabel(i));
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
    for (i = 1; i < dataArray.length; i++) {

      // Add
      for (j = 1; j < dataArray[i].getNumberOfColumns(); j++) {
        newColumn = mergedTable.addColumn(dataArray[i].getColumnType(j),
            dataArray[i].getColumnLabel(j));
        for (var k = 0; k < dataArray[i].getNumberOfRows(); k++) {
          matchingRows = mergedTable.getFilteredRows([ {
            column : 0,
            value : dataArray[i].getValue(k, 0)
          } ]);
          if (matchingRows.length < 1) {
            newRow = mergedTable.addRow();
            mergedTable.setValue(newRow, 0, dataArray[i].getValue(k, 0));
            mergedTable
                .setValue(newRow, newColumn, dataArray[i].getValue(k, j));
          } else {
            for (l = 0; l < matchingRows.length; l++) {
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
    $('#profile').show();
    $('#newProfileDiv').show();
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
 * Splits the query for a source if it is too large.
 * 
 * @param server
 *          The WattDepot server to send the query to.
 * 
 * @param source
 *          The source the query is getting data for.
 * 
 * @param dataType
 *          The type of data the query is retrieving.
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
function splitQuery(server, source, dataType, startTime, endTime, interval) {
  var returnArray = [];
  var tempTime = new Date(startTime.getTime());
  var query;
  /*
   * tempTime.setMinutes(tempTime.getMinutes() + maxQuerySize * interval,
   * tempTime.getSeconds(), tempTime.getMilliseconds());
   *  // Split the query while(tempTime < endTime) { query = new
   * google.visualization.Query(server
   * +'wattdepot/admin/depository/power/measurements/gviz/?sensor='+ source
   * +'&start='+wdClient.getTimestampFromDate(startTime)+'&end='+wdClient.getTimestampFromDate(tempTime)+'&samplingInterval='+interval);
   * query.setQuery('select timePoint, ' + dataType);
   * query.setTimeout(queryTimeOut); returnArray.push(query); startTime = new
   * Date(tempTime.getTime()); tempTime.setMinutes(tempTime.getMinutes() +
   * maxQuerySize * interval, tempTime.getSeconds(),
   * tempTime.getMilliseconds()); }
   */
  // Make query for the remaining interval
  query = new google.visualization.Query(server + 'wattdepot/admin/depository/'
      + dataType + '/values/gviz/?sensor=' + source + '&start='
      + wdClient.getTimestampFromDate(startTime) + '&end='
      + wdClient.getTimestampFromDate(endTime) + '&interval=' + interval);
  query.setTimeout(queryTimeOut);
  returnArray.push(query);
  return returnArray;
}

/**
 * Creates the visualization for the visualizer. Called when the Visualize!
 * button is clicked.
 */
function visualize() {
  var server;
  var dataType;
  var source;
  var interval;
  var startTime;
  var endTime;
  var formIndex;
  var error = false;

  // No forms to visualize...
  if (activeIndex.length < 1) {
    return;
  }

  // Disable visualize button
  $('#visualizeButton').prop("disabled", true);
  $('#visualizeButton').empty();
  $('#visualizeButton').append("Loading...");

  $('#chartDiv').hide();

  // Initialize required globals
  dataQueries = [];
  totalNumQueries = 0;

  server = serverAddress;

  // Set up all of the queries
  for (var loopIndex = 0; loopIndex < activeIndex.length; loopIndex++) {
    if (activeIndex[loopIndex].disabled) {
      continue;
    }
    totalNumQueries++;
    formIndex = activeIndex[loopIndex].formIndex;
    dataType = $('#dataType' + formIndex + ' option:selected').val();

    if (activeIndex[loopIndex].sourceSelected) {
      source = $('#sourceSelect' + formIndex + ' option:selected').val();
      if (findSource(source) != -1) {
        if ($('#sourceSelect' + formIndex + "_chzn").hasClass('invalid')) {
          $('#sourceSelect' + formIndex + "_chzn").parent().tooltip('hide');
          $('#sourceSelect' + formIndex + "_chzn").unwrap();
          $('#sourceSelect' + formIndex + "_chzn").removeClass('invalid');
        }
      }

      else {
        error = true;
        $('#sourceSelect' + formIndex + "_chzn")
            .wrap(
                '<a href="#" rel="tooltip" data-placement="top" title="Error: Source is not a valid source on the current server." />');
        $('#sourceSelect' + formIndex + "_chzn").addClass('invalid');
      }
    } else {
      error = true;
      $('#sourceSelect' + formIndex + "_chzn")
          .wrap(
              '<a href="#" rel="tooltip" data-placement="top" title="Error: No source has been selected." />');
      $('#sourceSelect' + formIndex + "_chzn").addClass('invalid');
    }
    interval = $('#interval' + formIndex + ' option:selected').val();
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
      $('#interval' + formIndex).addClass('invalid');
      $('#interval' + formIndex)
          .wrap(
              '<a href="#" rel="tooltip" data-placement="top" title="Error: Interval is larger than the data range.  Please chooose a smaller interval." />');
    } else if ($('#interval' + formIndex).hasClass('invalid')) {
      $('#interval' + formIndex).unwrap();
      $('#interval' + formIndex).removeClass('invalid');
    }

    switch (dataType) {
    case "power":
    case "energy":
      if ($('#dataType' + formIndex).hasClass('invalid')) {
        $('#dataType' + formIndex).unwrap();
        $('#dataType' + formIndex).removeClass('invalid');
      }
      if (!error) {
        dataQueries.push({
          'source' : source,
          'numReturned' : 0,
          'queries' : splitQuery(server, source, dataType, startTime, endTime,
              interval),
          'queryResults' : null
        });
      }
      break;
    default:
      error = true;
      if (typeof dataType === "undefined") {
        $('#dataType' + formIndex)
            .wrap(
                '<a href="#" rel="tooltip" data-placement="top" title="Error: The data type to retrieve was not selected." />');
      } else {
        $('#dataType' + formIndex).wrap(
            '<a href="#" rel="tooltip" data-placement="top" title=""Error: Data type '
                + dataType + ' is not supported." />');
      }
      $('#dataType' + formIndex).addClass('invalid');

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

/**
 * Finds the index of the object in sources that has the given name..
 * 
 * @param sourceName
 *          The name of the source to find.
 * 
 * @return The index of the source in sources, or -1 if it is not found.
 */
function findSource(sourceName) {
  for (var i = 0; i < sources.length; i++) {
    if (sources[i].sourceName == sourceName) {
      return i;
    }
  }
  return -1;
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
 * Fills the dataType options and selects the given dataType. To be passed as a
 * parameter to sourceChange.
 * 
 * @param data
 *          The data returned from the AJAX query for the latest sensor data.
 * 
 * @param formIndex
 *          The index of the form to modify.
 * 
 * @param dataType
 *          The dataType to automatically select.
 */
function putAndSelectDataTypes(data, formIndex, dataType) {
  putDataTypes(data, formIndex);
  $('#dataType' + formIndex).val(dataType);
}

/**
 * Fills the dataType options. To be passed as a parameter by sourceChange.
 * 
 * @param data
 *          The data returned from the AJAX query for the latest sensor data.
 * 
 * @param formIndex
 *          The index of the form to modify.
 */
function putDataTypes(data, formIndex) {
  // Add valid data type options to the dataType select field.
  $('#dataType' + formIndex).append('<option value="power">Power</option>');
  $('#dataType' + formIndex).append('<option value="energy">Energy</option>');

  /*
   * var properties = data['SensorData']['Properties']['Property']; for(var i in
   * properties) { switch(properties[i]["Key"]){ case "powerConsumed":
   * $('#dataType' + formIndex).append('<option value="powerConsumed">Power
   * Consumed</option>'); break; case "powerGenerated": $('#dataType' +
   * formIndex).append('<option value="powerGenerated">Power Generated</option>');
   * break; case "energyConsumedToDate": $('#dataType' + formIndex).append('<option
   * value="energyConsumed">Energy Consumed</option>'); break; case
   * "energyGenerated": $('#dataType' + formIndex).append('<option
   * value="energyGenerated">Energy Generated</option>'); break; case
   * "carbonGenerated": $('#dataType' + formIndex).append('<option
   * value="carbonGenerated">Carbon Generated</option>'); break;
   *  // Default for user-defined
   *  } }
   */
}

/**
 * Handles the event when the selected option of the sourceSelect is changed.
 * 
 * @param formIndex
 *          The index of the form of the sourceSelect to handle.
 * 
 * @param dataTypeGenerator
 *          A function that takes either two parameters (data, formIndex) or
 *          three parameters (data, formIndex, selectedDataType) that generates
 *          the dataType options and possibly other actions.
 * 
 * @param selectedDataType
 *          The dataType that should be automatically selected. Set to null if
 *          none should be selected.
 */
function sourceChange(formIndex, dataTypeGenerator, selectedDataType) {

  // Fill in the DataType options.
  activeIndex[findActiveIndex(formIndex)].sourceSelected = true;
  $('#dataType' + formIndex).empty();
  if (selectedDataType == "") {
    dataTypeGenerator(null, formIndex);
    /*
     * wdClient.getSourceLatestSensorData($('#sourceSelect'+formIndex).val(),
     * function(data) { dataTypeGenerator(data, formIndex); });
     */
  } else {
    wdClient.getSourceLatestSensorData($('#sourceSelect' + formIndex).val(),
        function(data) {
          dataTypeGenerator(data, formIndex, selectedDataType);
        });
  }

  // ----------------------------
  //
  // Note: made changes to the: $('#visualizerFormsDiv').append() to account for
  // ids and layout.
  //
  // marking off work for flsendata
  //
  // This will be called(along with above code) whenever a source is changed.
  // it calls get first sensor function (located on index.html) sending the name
  // of the source and the name of the div id.
  getFirstSensor($('#sourceSelect' + formIndex).val(), '#earliestSensor'
      + formIndex);
  // Latestsensor functioncall
  // same idea, send the name of the source and the div id.
  getLatestSensor($('#sourceSelect' + formIndex).val(), '#latestSensor'
      + formIndex);
  // end mark off for flsendata
  // ----------------------------
}

/**
 * Inserts the HTML for a new visualizer form into the DOM with the given index.
 * 
 * @param formIndex
 *          The index of the form being created.
 */
function insertVisualizerFormHTML(formIndex) {
  var currentDate = new Date();
  $('#visualizerFormsDiv')
      .append(
          '<div class="row-fluid form" id="form'
              + formIndex
              + '">'
              + '<div class="row-fluid">'
              + '<div id="sourceDiv'
              + formIndex
              + '" class="span2 control-group">'
              + '<div class="span2"><a href="#" rel="tooltip" data-placement="right" title="Show / Hide the source.">'
              + '<input type="checkbox" class="span11" id="show'
              + formIndex
              + '" /></a></div>'
              + '<div class="span10">'
              + '<select id="sourceSelect'
              + formIndex
              + '" class="span12 chzn-select" data-placeholder="Choose a source...">'
              + '<option value="" selected></option></select></div></div>'
              + '<div id="dataTypeDiv'
              + formIndex
              + '" class="span2 control-group">'
              + '<select class="span12" id="dataType'
              + formIndex
              + '"></select></div>'
              + '<div class="span7" id="timeDiv'
              + formIndex
              + '">'
              + '<div class="span5 control-group">'
              + '<div class="input-append bootstrap-timepicker-component span5">'
              + '<input type="text" id="startTimePicker'
              + formIndex
              + '" class="timepicker-default input-small">'
              + '<span id="startTimePickerSpan'
              + formIndex
              + '" class="add-on"><i class="icon-time"></i></span></div>'
              + '<div class="span5">'
              + '<input class="span12" type="text" value="'
              + (currentDate.getMonth() + 1)
              + '/'
              + (currentDate.getDate())
              + '/'
              + currentDate.getFullYear()
              + '" id="startDatepicker'
              + formIndex
              + '"></div>'
              + '<div class="span12 boundaryData"><div class="span5">Earliest Sensor:</div>'
              + '<div class="span6" id="earliestSensor'
              + formIndex
              + '"></div></div></div>'
              + '<div class="span5 control-group"><div class="input-append bootstrap-timepicker-component span5">'
              + '<input type="text" id="endTimePicker'
              + formIndex
              + '" class="timepicker-default input-small">'
              + '<span id="endTimePickerSpan'
              + formIndex
              + '" class="add-on"><i class="icon-time"></i></span></div>'
              + '<div class="span5"><input class="span12" type="text" value="'
              + (currentDate.getMonth() + 1)
              + '/'
              + currentDate.getDate()
              + '/'
              + currentDate.getFullYear()
              + '" id="endDatepicker'
              + formIndex
              + '"></div>'
              + '<div class="span2"><label class="checkbox"><input type="checkbox" id="endTimeNow'
              + formIndex
              + '" value="now">Now</label></div>'
              + '<div class="span12 boundaryData"><div class="span5">Latest Sensor:</div>'
              + '<div id="latestSensor'
              + formIndex
              + '" class="span6"></div></div></div>'
              + '<div class="span2 control-group"><select class="span12" id="interval'
              + formIndex
              + '">'
              + '<option value="5">5 mins</option>'
              + '<option value="15">15 mins</option>'
              + '<option value="30">30 mins</option>'
              + '<option value="60">1 hr</option>'
              + '<option value="120">2 hr</option>'
              + '<option value="1440">1 Day</option>'
              + '<option value="10080">1 Week</option>'
              + '<option value="43200">1 Month</option></select></div></div>'
              + '<div class="span1" id="removeDiv'
              + formIndex
              + '">'
              + '<a href="#" rel="tooltip" data-placement="left" title="Remove the source.">'
              + '<button id="remove'
              + formIndex
              + '" class="btn"><i class="icon-minus-sign"></i></button></a></div></div></div></div>');

  // Bind event for when the Now time checkbox is checked
  $('#endTimeNow' + formIndex).click(function() {
    if ($('#endTimeNow' + formIndex).is(':checked')) {
      $('#endDatepicker' + formIndex).prop("disabled", true);
      $('#endTimePicker' + formIndex).prop("disabled", true);
      $('#endTimePickerSpan' + formIndex).prop("disabled", true);
    } else {
      $('#endDatepicker' + formIndex).prop("disabled", false);
      $('#endTimePicker' + formIndex).prop("disabled", false);
      $('#endTimePickerSpan' + formIndex).prop("disabled", false);
    }
  });

  // Bind event for when the remove button is clicked
  $('#remove' + formIndex).click(function() {
    $('#remove' + formIndex).parent().tooltip('destroy');
    $('#form' + formIndex).remove();
    activeIndex.splice(findActiveIndex(formIndex), 1);
    if (activeIndex.length < 1) {
      $('#visualizeButton').prop("disabled", true);
    }
  })

  // Bind event for when the Show checkbox is checked
  $('#show' + formIndex).click(function() {
    var form = activeIndex[findActiveIndex(formIndex)];
    if ($('#show' + formIndex).is(':checked')) {
      if ($('#visualizeButton').prop("disabled")) {
        $('#visualizeButton').prop("disabled", false);
      }
      form.disabled = false;
      numShow++;
      $('#form' + formIndex).css('background-color', '#fff');
    } else {
      form.disabled = true;
      $('#form' + formIndex).css('background-color', '#ddd');
      numShow--;

      if (numShow < 1) {
        $('#visualizeButton').prop("disabled", true);
      }
    }
  });

  // Set up source select
  for ( var source in sources) {
    $('#sourceSelect' + formIndex).append(
        '<option value="' + sources[source].sourceName + '">'
            + sources[source].sourceName + '</option>');
  }

  $('#sourceSelect' + formIndex).chosen();
  $("#sourceSelect" + formIndex).chosen().change(function() {
    sourceChange(formIndex, putDataTypes, "");
  });

  // Enable tooltips
  $("[rel=tooltip]").tooltip();
}

/**
 * Creates a new empty source form for the Visualizer and adds it to the DOM.
 */
function createVisualizerForm() {
  if (wdClient == null) {
    alert("Please select a server and submit first please!");
    return;
  }

  var formIndexCopy = formIndex;
  formIndex++;

  // Add visualizer form to the DOM
  insertVisualizerFormHTML(formIndexCopy);

  // Update array of active form indexes
  if (findActiveIndex(formIndexCopy) == -1) {
    activeIndex.push({
      formIndex : formIndexCopy,
      disabled : false,
      sourceSelected : false
    });
    numShow++;
  }

  // Enable time picker fields
  $('#startTimePicker' + formIndexCopy).timepicker({
    minuteStep : 1,
    showSeconds : true,
    showMeridian : false,
    defaultTime : '00:00:00'
  });
  $('#endTimePicker' + formIndexCopy).timepicker({
    minuteStep : 1,
    showSeconds : true,
    showMeridian : false,
    defaultTime : '23:59:00'
  });

  // Enable date picker fields
  $('#startDatepicker' + formIndexCopy).datepicker();
  $('#endDatepicker' + formIndexCopy).datepicker();

  $('#show' + formIndexCopy).prop('checked', true);

  if ($('#visualizeButton').prop("disabled") && numShow >= 1) {
    $('#visualizeButton').prop("disabled", false);
  }
}

/**
 * Creates a visualizer form pre-filled with the given values.
 * 
 * @param show
 *          If show is checked or not (boolean true or false).
 * 
 * @param selectedSource
 *          The name of the source that should be selected (string). ""
 *          indicates no source and no dataType will be selected as well.
 * 
 * @param dataType
 *          The value of the data type option that should be selected (string).
 *          Ignored if selectedSource == "".
 * 
 * @param startTime
 *          The starting time (string "HH:MM:SS").
 * 
 * @param startDate
 *          The starting date (string "MM/DD/YYYY").
 * 
 * @param endTime
 *          The ending time (string "HH:MM:SS").
 * 
 * @param endDate
 *          The ending date (string "MM/DD/YYYY").
 * 
 * @param now
 *          If now is checked (boolean true or false).
 * 
 * @param interval
 *          The interval to poll data at (string "# of minutes").
 * 
 * @param dataTypeGenerator
 *          A function that handles the generation of he data type options and
 *          possibly other actions.
 */
function createFilledVisualizerForm(show, selectedSource, dataType, startTime,
    startDate, endTime, endDate, now, interval, dataTypeGenerator) {
  if (wdClient == null) {
    alert("Please select a server and submit first please!");
    return;
  }

  var formIndexCopy = formIndex;
  formIndex++;

  // Add visualizer form to the DOM
  insertVisualizerFormHTML(formIndexCopy);

  // Update array of active form indexes
  if (findActiveIndex(formIndexCopy) == -1) {
    if (selectedSource != "") {
      activeIndex.push({
        formIndex : formIndexCopy,
        disabled : !show,
        sourceSelected : true
      });
    } else {
      activeIndex.push({
        formIndex : formIndexCopy,
        disabled : !show,
        sourceSelected : false
      });
    }
    if (show) {
      numShow++;
    } else {
      $('#form' + formIndexCopy).css('background-color', '#ddd');
    }
  }

  // Select appropriate source and data type
  if (selectedSource != "") {
    $('#sourceSelect' + formIndexCopy).val(selectedSource);
    $('#sourceSelect' + formIndexCopy).trigger("liszt:updated");
    sourceChange(formIndexCopy, dataTypeGenerator, dataType);
  }

  // Enable time picker fields
  $('#startTimePicker' + formIndexCopy).timepicker({
    minuteStep : 1,
    showSeconds : true,
    showMeridian : false,
    defaultTime : startTime
  });
  $('#endTimePicker' + formIndexCopy).timepicker({
    minuteStep : 1,
    showSeconds : true,
    showMeridian : false,
    defaultTime : endTime
  });

  $('#endTimeNow' + formIndexCopy).prop('checked', now);
  if (now) {
    $('#endTimeNow' + formIndexCopy).click();
    $('#endTimeNow' + formIndexCopy).prop('checked', true);
  }

  // Enable date picker fields
  document.getElementById('startDatepicker' + formIndexCopy).value = startDate;
  $('#startDatepicker' + formIndexCopy).datepicker();
  document.getElementById('endDatepicker' + formIndexCopy).value = endDate;
  $('#endDatepicker' + formIndexCopy).datepicker();

  $('#interval' + formIndexCopy).val(interval);

  $('#show' + formIndexCopy).prop('checked', show);

  if ($('#visualizeButton').prop("disabled") && numShow >= 1) {
    $('#visualizeButton').prop("disabled", false);
  }
}

/**
 * Adds a new visualizer form with the same values as the previous one. Called
 * on click by the add button.
 */
function addVisualizerForm() {
  if (activeIndex.length == 0) {
    createVisualizerForm();
  } else {
    var lastActiveFormIndex = activeIndex[activeIndex.length - 1].formIndex;
    if (activeIndex[activeIndex.length - 1].sourceSelected) {
      createFilledVisualizerForm($('#show' + lastActiveFormIndex)
          .is(':checked'), $(
          '#sourceSelect' + lastActiveFormIndex + ' option:selected').val(), $(
          '#dataType' + lastActiveFormIndex + ' option:selected').val(), $(
          '#startTimePicker' + lastActiveFormIndex).val(), $(
          '#startDatepicker' + lastActiveFormIndex).val(), $(
          '#endTimePicker' + lastActiveFormIndex).val(), $(
          '#endDatepicker' + lastActiveFormIndex).val(), $(
          '#endTimeNow' + lastActiveFormIndex).is(':checked'), $(
          '#interval' + lastActiveFormIndex + ' option:selected').val(),
          putAndSelectDataTypes);
    } else {
      createFilledVisualizerForm($('#show' + lastActiveFormIndex)
          .is(':checked'), "", "", $('#startTimePicker' + lastActiveFormIndex)
          .val(), $('#startDatepicker' + lastActiveFormIndex).val(), $(
          '#endTimePicker' + lastActiveFormIndex).val(), $(
          '#endDatepicker' + lastActiveFormIndex).val(), $(
          '#endTimeNow' + lastActiveFormIndex).is(':checked'), $(
          '#interval' + lastActiveFormIndex + ' option:selected').val(),
          putAndSelectDataTypes);
    }
  }
}

/**
 * Adds a new, user named profile. Called on click by the newProfileButton
 * (Save).
 */
function addNewProfile() {
  var link = $('#linkSpace').val();
  var name = $('#newProfileText').val();
  $('#profileSelect').append(
      '<option value="' + link + '">' + name + '</option>');
  numProfiles++;
  var numPro = getCookie('proNum');
  var index;

  if (numProfiles > 1) {
    $('#profileSelect').prop("disabled", false);
  }

  if (numPro == null) {
    makeCookie('proNum', 1, 31);
    index = 0;
  } else {
    numPro = parseInt(numPro);
    numPro += 1;
    makeCookie('proNum', numPro, 31);
    index = numPro - 1;
  }
  makeCookie('proName' + index, encodeURIComponent(name), 31);
  makeCookie('proLink' + index, encodeURIComponent(link), 31);
  $('#newProfileText').val("");
  $('#newProfileButton').prop('disabled', true);
  $('#newProfileDiv').hide();
  $('#profileSelect').val(link);
}

/**
 * Redirects the page to load the selected profile if it is not the current one.
 * Called when the profileSelect changes.
 */
function selectProfile() {
  if ($('#profileSelect option:selected').val() == $('#linkSpace').val()) {
    return;
  }
  window.location = $('#profileSelect option:selected').val();
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
