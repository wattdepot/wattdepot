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

/**
 *
 * Created by carletonmoore on 4/10/15.
 */

var wdClient = null;
var loaded = false; // If the google visualization API has been loaded.


/**
 * Loads the page either using the permalink or starting fresh.
 */
function loadPage() {
  wdClient = org.WattDepot.Client(server);
}

/**
 * Gets the currently selected depository for the given row. Then updates the
 * choices of sensors.
 *
 * @param index
 *          the row number.
 */
function selectedPowerDepository() {
  var depoId = $("#timeSeriesDepository option:selected").val();
  var depoName = $("#timeSeriesDepository option:selected").text();
  var sensors = DEPO_SENSORS[depoId];
  var select = $("#timeSeriesSensor");
  select.empty();
  var i = 0;
  var length = sensors.length;
  for (i = 0; i < length; i++) {
    select.append($("<option></option>").attr("value", sensors[i]).text(
        SENSORS[sensors[i]].name));
  }

};
/**
 * Updates the Sensor selection options of the heat map visualization when the depository changes.
 *
 * @param sensors
 *          the list of sensors.
 */
function selectedHMPowerDepository() {
  var depoId = $("#heatMapDepository option:selected").val();
  var depoName = $("#heatMapDepository option:selected").text();
  var sensors = DEPO_SENSORS[depoId];
  var select = $("#heatMapSensor");
  select.empty();
  var i = 0;
  var length = sensors.length;
  for (i = 0; i < length; i++) {
    select.append($("<option></option>").attr("value", sensors[i]).text(
        SENSORS[sensors[i]].name));
  }
};

/**
 * Updates the Sensor selection options of the energy signature visualization.
 *
 * @param sensors
 *          the list of sensors.
 */
function selectedSigPowerDepository() {
  var depoId = $("#sigPowerDepository option:selected").val();
  var sensors = DEPO_SENSORS[depoId];
  var select = $("#sigPowerSensor");
  select.empty();
  var i = 0;
  var length = sensors.length;
  for (i = 0; i < length; i++) {
    select.append($("<option></option>").attr("value", sensors[i]).text(
        SENSORS[sensors[i]].name));
  }
};

/**
 * Updates the temperature Sensor selection options of the energy signature visualization.
 *
 * @param sensors
 *          the list of sensors.
 */
function selectedSigTempDepository() {
  var depoId = $("#sigTempDepository option:selected").val();
  var sensors = DEPO_SENSORS[depoId];
  var select = $("#sigTempSensor");
  select.empty();
  var i = 0;
  var length = sensors.length;
  for (i = 0; i < length; i++) {
    select.append($("<option></option>").attr("value", sensors[i]).text(
        SENSORS[sensors[i]].name));
  }
};


function timeSeriesPlot() {
  var timeInterval = $("#timeSeriesDuration").val();
  var depositoryId = $("#timeSeriesDepository").val();
  var sensorId = $('#timeSeriesSensor').val();
  var uri = server + ORGID + '/openeis/time-series-load-profiling/gviz/?depository='
      + depositoryId + '&sensor=' + sensorId + "&duration=" + timeInterval;
  console.log(uri);
  document.getElementById("tslp").style.cursor = "wait";
  var query = new google.visualization.Query(uri);
  query.send(function (response) {
    timeSeriesResponse(response);
  });
}

function timeSeriesResponse(response) {
  document.getElementById("tslp").style.cursor = "auto";
  if (response.isError()) {
    alert('Error in query: ' + response.getMessage() + ' '
    + response.getDetailedMessage());
    return;
  }
  var table = response.getDataTable();
  $('#tslpChart').show();
  var chart = new google.visualization.ComboChart(document.getElementById('tslpChart'));
  chart.draw(table, {});

}

function heatMapPlot() {
  var duration = $("#heatMapDuration").val();
  var depositoryId = $("#heatMapDepository").val();
  var sensorId = $('#heatMapSensor').val();
  var uri = server + ORGID + '/openeis/heat-map/gviz/?depository='
      + depositoryId + '&sensor=' + sensorId + '&duration=' + duration;
  document.getElementById("heat_map").style.cursor = "wait";
  //console.log(uri);
  var query = new google.visualization.Query(uri);
  query.send(function (response) {
    heatMapResponse(response);
  });
}

function heatMapResponse(response) {
  document.getElementById("heat_map").style.cursor = "auto";
  if (response.isError()) {
    alert('Error in query: ' + response.getMessage() + ' '
    + response.getDetailedMessage());
    return;
  }
  var table = response.getDataTable();
  $('#heatChart').show();
  var chart = new org.systemsbiology.visualization.BioHeatMap(document.getElementById('heatChart'));
  chart.draw(table, {
    startColor: {r: 0, g: 0, b: 255, a: 1},
    endColor: {r: 255, g: 0, b: 0, a: 1},
    passThroughWhite: true,
  });

}

function energySigPlot() {
  var sigDuration = $("#energySigDuration").val();
  var powerDepoId = $("#sigPowerDepository").val();
  var powerSensorId = $('#sigPowerSensor').val();
  var tempDepoId = $('#sigTempDepository').val();
  var tempSensorId = $('#sigTempSensor').val();
  var uri = server + ORGID + '/openeis/energy-signature/gviz/?power-depository='
      + powerDepoId + '&power-sensor=' + powerSensorId + "&temperature-depository="
      + tempDepoId + '&temperature-sensor=' + tempSensorId + "&duration=" + sigDuration;
  document.getElementById("energy_signature").style.cursor = "wait";
  //console.log(uri);
  var query = new google.visualization.Query(uri);
  query.send(function (response) {
    energySigResponse(response);
  });
}

function energySigResponse(response) {
  document.getElementById("energy_signature").style.cursor = "auto";
  if (response.isError()) {
    alert('Error in query: ' + response.getMessage() + ' '
    + response.getDetailedMessage());
    return;
  }
  var analyses = response.getReasons();
  $('#energySigAnalyses').empty();
  for (i = 0; i < analyses.length; i++) {
    $('#energySigAnalyses').append('<h4>' + analyses[i] + '</h4>');
  }
  var table = response.getDataTable();
  $('#energySigChart').show();
  var chart = new google.visualization.ScatterChart(document.getElementById('energySigChart'));
  chart.draw(table, {});

}

function longitudePlot() {
  var longDepository = $('#longitudeDepository').val();
  var longSensor = $('#longitudeSensor').val();
  var baselineStart = getDate('startdatetimepicker');
  var baselineDuration = $('#longitudeDuration').val();
  var comparisonStart = getDate('comparisonstartdatetimepicker');
  var numIntervals = $('#numIntervals').val();
  var uri = server + ORGID + '/openeis/longitudinal-benchmarking/gviz/?depository='
      + longDepository + '&sensor=' + longSensor + '&baseline-start=' + wdClient.getTimestampFromDate(baselineStart)
      + '&baseline-duration=' + baselineDuration + '&comparison-start=' + wdClient.getTimestampFromDate(comparisonStart)
      + '&num-intervals=' + numIntervals;
  document.getElementById("energy_signature").style.cursor = "wait";
  console.log(uri);
  var query = new google.visualization.Query(uri);
  query.send(function (response) {
    longitudeResponse(response);
  });
}

function longitudeResponse(response) {
  document.getElementById("energy_signature").style.cursor = "auto";
  if (response.isError()) {
    alert('Error in query: ' + response.getMessage() + ' '
    + response.getDetailedMessage());
    return;
  }
  var table = response.getDataTable();
  $('#longitudeChart').show();
  var chart = new google.visualization.ColumnChart(document.getElementById('longitudeChart'));
  chart.draw(table, {});

}

/**
 * Gets the Date for the specified time field.
 *
 * @param id
 *          The id prefix of the time field to get (i.e. 'start' or 'comparison').
 * @param index
 *          The index of the time field form to get.
 *
 * @return A Date representing the fields of the given form.
 */
function getDate(id) {
  var picker = $('#' + id );
  var data = picker.data("DateTimePicker");
  var time = data.date();
  var error = false;
  if (time == null) {
    error = true;
    $('#' + id ).addClass('invalid');
    $('#' + id ).css('border', '3px red solid');
    $('#' + id )
        .wrap(
        '<a href="#" rel="tooltip" data-placement="top" title="Error: Time must be selected.">');
  }
  if (error) {
    return null;
  }
  if ($('#' + id ).hasClass('invalid')) {
    $('#' + id ).css('border', '');
    $('#' + id ).unwrap();
    $('#' + id ).removeClass('invalid');
  }
  return time.toDate();
}
