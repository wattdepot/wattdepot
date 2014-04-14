/** The URL to the WattDepot server. */
var serverUrl = null;
/** The WattDepot organization. */
var organization = null;
/** The name of the depository holding power data. */
var powerDepository = null;
/** The sensor's or group's slug that is collecting power measurements. */
var powerSensor = null;
/** The name of the sensor or group. */
var powerSensorName = null;
/** The refresh time for power data. */
var powerRefresh = null;
/** the latest power data as a google data table. */
var powerData = null;
/** The name of the depository holding energy data. */
var energyDepository = null;
/** The name of the sensor or group collecting energy measurements. */
var energySensor = null;
/** The daily energy data for the last 7 days.*/
var energyData = null;
/** The name of the depository holding temperature data. */
var temperatureDepository = null;
/** The name of the sensor or group collecting temperature measurements. */
var temperatureSensor = null;
/** The refresh time for temperature data. */
var temperatureRefresh = null;
/** the latest temperature data as a google data table. */
var temperatureData = null;
/** The name of the depository holding humidity data. */
var humidityDepository = null;
/** The name of the sensor or group collecting humidity measurements. */
var humiditySensor = null;
/** The refresh time for humidity data. */
var humidityRefresh = null;
/** the latest humidity data as a google data table. */
var humidityData = null;
/** The name of the depository holding cloud coverage data. */
var cloudDepository = null;
/** The name of the sensor or group collecting cloud coverage measurements. */
var cloudSensor = null;
/** The refresh time for cloud data. */
var cloudRefresh = null;
/** the latest cloud data as a google data table. */
var cloudData = null;

var averageTempData = null;

var averageHumidityData = null;

var averageCloudData = null;

function debug(msg) {
  if (typeof (console) != 'undefined') {
    console.info(msg);
  }
}

function CurrentPowerMeter() {
  var pquery = null;

  google.setOnLoadCallback(pcallback());

  return false;

  function pcallback() {
    var element = $("#current-power");
    element.append('<div class="temp-holder1"><p>Loading graph...</p></div>');
    var gvizUrl = serverUrl + "/" + organization + "/depository/"
        + powerDepository + "/value/gviz/?sensor=" + powerSensor
        + "&latest=true";
    pquery = new google.visualization.Query(gvizUrl);
    pquery.setRefreshInterval(powerRefresh);
    // Set a callback to run when the data has been retrieved.
    pquery.send(function(response) {
      responseHandlerP(response);
    });
  }

  function responseHandlerP(response) {
    // Process errors, if any
    if (response.isError()) {
      debug('Error in query: ' + response.getMessage() + ' '
          + response.getDetailedMessage());
      return;
    }
    var element = $("#current-power");
    element.empty();
    powerData = response.getDataTable();

    drawP(fixPowerData(powerData));
  }

  function fixPowerData(datatable) {
    var powerTable = new google.visualization.DataTable();
    var numPowerTableRows = 1;
    powerTable.addColumn('string');
    powerTable.addColumn('number'); // the power in W.
    powerTable.addRows(numPowerTableRows);
    var powerLabel = "";
    var powerVal = Number(datatable.getValue(0,1).toFixed(0));
    powerTable.setCell(0, 0, powerLabel);
    powerTable.setCell(0, 1, powerVal);
    return powerTable;
  }
  
  function drawP(dataTable) {

    var powerOptions = {
      width: 155,
      height: 155,
      min : 50000,
      max : 150000
    };
    var powerChart = new google.visualization.Gauge(document
        .getElementById('current-power'));
    powerChart.draw(dataTable, powerOptions);

    updatePowerCaption();
  }
  
  function updatePowerCaption() {
    var element = $("#power-time");
    element.empty();
    var dateFormatter = new google.visualization.DateFormat({pattern:'MMM d, yyyy, h:mm:ss a'});
    var updateTime = dateFormatter.formatValue(powerData.getValue(0,0));
    element.text(updateTime);
    element = $("#power-units");
    element.text(powerData.getColumnId(1));

  }


};

function CurrentTemperatureMeter() {
  var tquery = null;

  google.setOnLoadCallback(tcallback());

  return false;

  function tcallback() {
    var gvizUrl = serverUrl + "/" + organization + "/depository/"
        + temperatureDepository + "/value/gviz/?sensor=" + temperatureSensor
        + "&latest=true";
    tquery = new google.visualization.Query(gvizUrl);
    tquery.setRefreshInterval(temperatureRefresh);
    // Set a callback to run when the data has been retrieved.
    tquery.send(function(response) {
      responseHandlerT(response);
    });
  }

  function responseHandlerT(response) {
    // Process errors, if any
    if (response.isError()) {
      debug('Error in query: ' + response.getMessage() + ' '
          + response.getDetailedMessage());
      return;
    }
    temperatureData = response.getDataTable();

    drawT(fixTemperatureData(temperatureData));
  }

  function fixTemperatureData(datatable) {
    var temperatureTable = new google.visualization.DataTable();
    var numTemperatureTableRows = 1;
    temperatureTable.addColumn('string');
    temperatureTable.addColumn('number'); // the temperature in W.
    temperatureTable.addRows(numTemperatureTableRows);
    var temperatureLabel = "";
    var temperatureVal = datatable.getValue(0,1);
    temperatureTable.setCell(0, 0, temperatureLabel);
    temperatureTable.setCell(0, 1, temperatureVal);
    return temperatureTable;
  }
  
  function drawT(dataTable) {

    var temperatureOptions = {
      width: 155,
      height: 155,
      min : 45,
      max : 110
    };
    var temperatureChart = new google.visualization.Gauge(document
        .getElementById('current-temperature'));
    temperatureChart.draw(dataTable, temperatureOptions);

    updateTemperatureCaption();
  }
  
  function updateTemperatureCaption() {
    var element = $("#temperature-time");
    element.empty();
    var dateFormatter = new google.visualization.DateFormat({pattern:'MMM d, yyyy, h:mm:ss a'});
    var updateTime = dateFormatter.formatValue(temperatureData.getValue(0,0));
    element.text(updateTime);
    element = $("#temperature-units");
    element.text(temperatureData.getColumnId(1));

  }
};

function CurrentHumidityMeter() {
  var hquery = null;

  google.setOnLoadCallback(hcallback());

  return false;

  function hcallback() {
    var gvizUrl = serverUrl + "/" + organization + "/depository/"
        + humidityDepository + "/value/gviz/?sensor=" + humiditySensor
        + "&latest=true";
    hquery = new google.visualization.Query(gvizUrl);
    hquery.setRefreshInterval(humidityRefresh);
    // Set a callback to run when the data has been retrieved.
    hquery.send(function(response) {
      responseHandlerH(response);
    });
  }

  function responseHandlerH(response) {
    // Process errors, if any
    if (response.isError()) {
      debug('Error in query: ' + response.getMessage() + ' '
          + response.getDetailedMessage());
      return;
    }
    humidityData = response.getDataTable();
    drawH(fixHumidityData(humidityData));
  }

  function fixHumidityData(datatable) {
    var humidityTable = new google.visualization.DataTable();
    var numHumidityTableRows = 1;
    humidityTable.addColumn('string');
    humidityTable.addColumn('number'); 
    humidityTable.addRows(numHumidityTableRows);
    var humidityLabel = "";
    var humidityVal = datatable.getValue(0,1);
    humidityTable.setCell(0, 0, humidityLabel);
    humidityTable.setCell(0, 1, humidityVal);
    return humidityTable;
  }
  
  function drawH(dataTable) {

    var humidityOptions = {
      width: 155,
      height: 155
    };
    var humidityChart = new google.visualization.Gauge(document
        .getElementById('current-humidity'));
    humidityChart.draw(dataTable, humidityOptions);

    updateHumidityCaption();
  }
  
  function updateHumidityCaption() {
    var element = $("#humidity-time");
    element.empty();
    var dateFormatter = new google.visualization.DateFormat({pattern:'MMM d, yyyy, h:mm:ss a'});
    var updateTime = dateFormatter.formatValue(humidityData.getValue(0,0));
    element.text(updateTime);
    element = $("#humidity-units");
    element.text(humidityData.getColumnId(1));

  }
};

function CurrentCloudMeter() {
  var cquery = null;

  google.setOnLoadCallback(ccallback());

  return false;

  function ccallback() {
    var gvizUrl = serverUrl + "/" + organization + "/depository/"
        + cloudDepository + "/value/gviz/?sensor=" + cloudSensor
        + "&latest=true";
    cquery = new google.visualization.Query(gvizUrl);
    cquery.setRefreshInterval(cloudRefresh);
    // Set a callback to run when the data has been retrieved.
    cquery.send(function(response) {
      responseHandlerC(response);
    });
  }

  function responseHandlerC(response) {
    // Process errors, if any
    if (response.isError()) {
      debug('Error in query: ' + response.getMessage() + ' '
          + response.getDetailedMessage());
      return;
    }
    cloudData = response.getDataTable();

    drawC(fixCloudData(cloudData));
  }

  function fixCloudData(datatable) {
    var cloudTable = new google.visualization.DataTable();
    var numCloudTableRows = 1;
    cloudTable.addColumn('string');
    cloudTable.addColumn('number'); // the cloud in W.
    cloudTable.addRows(numCloudTableRows);
    var cloudLabel = "";
    var cloudVal = datatable.getValue(0,1);
    cloudTable.setCell(0, 0, cloudLabel);
    cloudTable.setCell(0, 1, cloudVal);
    return cloudTable;
  }
  
  function drawC(dataTable) {

    var cloudOptions = {
      width: 155,
      height: 155
    };
    var cloudChart = new google.visualization.Gauge(document
        .getElementById('current-cloud'));
    cloudChart.draw(dataTable, cloudOptions);

    updateCloudCaption();
  }
  
  function updateCloudCaption() {
    var element = $("#cloud-time");
    element.empty();
    var dateFormatter = new google.visualization.DateFormat({pattern:'MMM d, yyyy, h:mm:ss a'});
    var updateTime = dateFormatter.formatValue(cloudData.getValue(0,0));
    element.text(updateTime);
    element = $("#cloud-units");
    element.text(cloudData.getColumnId(1));

  }
};

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

function DailyEnergyGraph() {
  var energyQuery = null;

  google.setOnLoadCallback(callbackEnergy());

  return false;

  function callbackEnergy() {
    var element = $("#daily-energy");
    element.append('<div class="temp-holder"><p>Loading graph...</p></div>');
    var now = new Date();
    var end = new Date(now.getFullYear(), now.getMonth(), now.getDay() - 1, 23, 59, 59, 999);
    var start = new Date(now.getFullYear(), now.getMonth(), now.getDay() - 7, 0, 0, 0 ,0);
    var gvizUrl = serverUrl + "/" + organization + "/depository/"
        + energyDepository + "/values/gviz/?sensor=" + energySensor
        + "&start=" + getTimestampFromDate(start) + "&end=" + getTimestampFromDate(end) 
        + "&interval=1440&value-type=interval";
    energyQuery = new google.visualization.Query(gvizUrl);
    // Set a callback to run when the data has been retrieved.
    energyQuery.send(function(response) {
      responseHandlerEnergy(response);
    });
  }

  function responseHandlerEnergy(response) {
    // Process errors, if any
    if (response.isError()) {
      debug('Error in query: ' + response.getMessage() + ' '
          + response.getDetailedMessage());
      return;
    }
    var element = $("#daily-energy");
    element.empty();
    
    energyData = response.getDataTable();
    updateEnergyCaption();
    drawEnergy(fixEnergyData(energyData));
  }
 
  function fixEnergyData(dataTable) {
    var energyTable = new google.visualization.DataTable();
    energyTable.addColumn('number', 'Daily Energy');
    energyTable.addRows(7);
    for (var i = 1; i < dataTable.getNumberOfRows(); i++) {
      energyTable.setCell(i-1, 0, dataTable.getValue(i, 1));
    }
    return energyTable;
  }
  
  function drawEnergy(dataTable) {

    var energyOptions = {
        width: 155,
        height: 155,
        showAxisLines: true,
        showValueLabels: true,
        labelPosition: 'none'
    };
    
    var energyChart = new google.visualization.ImageSparkLine(document
        .getElementById('daily-energy'));
    energyChart.draw(dataTable, energyOptions);
    
  }
  
  function updateEnergyCaption() {
    var element = $("#daily-energy-time");
    element.empty();
    var dateFormatter = new google.visualization.DateFormat({pattern:'MMM d, yyyy, h:mm:ss a'});
    var updateTime = dateFormatter.formatValue(new Date());
    element.text(updateTime);
    element = $("#daily-energy-units");
    element.text(energyData.getColumnId(1));

  }
};

function AverageTempGraph() {
  var tempQuery = null;

  google.setOnLoadCallback(callbackAveTemp());

  return false;

  function callbackAveTemp() {
    var element = $("#average-temp");
    element.append('<div class="temp-holder"><p>Loading graph...</p></div>');
    var now = new Date();
    var end = new Date(now.getFullYear(), now.getMonth(), now.getDate() - 1, 23, 59, 59, 999);
    var start = new Date(now.getFullYear(), now.getMonth(), now.getDate() - 9, 23, 59, 59 ,999);
    var gvizUrl = serverUrl + "/" + organization + "/depository/"
        + temperatureDepository + "/values/average/gviz/?sensor=" + temperatureSensor
        + "&start=" + getTimestampFromDate(start) + "&end=" + getTimestampFromDate(end) 
        + "&interval=1440&value-type=point";
 
    tempQuery = new google.visualization.Query(gvizUrl);
    // Set a callback to run when the data has been retrieved.
    tempQuery.send(function(response) {
      responseHandlerTemp(response);
    });
  }

  function responseHandlerTemp(response) {
    var element = $("#average-temp");
    element.empty();
    
    averageTempData = response.getDataTable();
    updateTemperatureCaption();
    drawTemp(fixTempData(averageTempData));
  }
  
  function fixTempData(dataTable) {
    var tempTable = new google.visualization.DataTable();
    tempTable.addColumn('number', 'Average Tempurature');
    tempTable.addRows(7);
    for (var i = 0; i < dataTable.getNumberOfRows() - 1; i++) {
      tempTable.setCell(i, 0, dataTable.getValue(i, 1));
    }
    return tempTable;
  }
  
  function drawTemp(dataTable) {

    var tempOptions = {
        width: 155,
        height: 155,
        showAxisLines: true,
        showValueLabels: true,
        labelPosition: 'none'
    };
    
    var tempChart = new google.visualization.ImageSparkLine(document
        .getElementById('average-temp'));
    tempChart.draw(dataTable, tempOptions);
    
  }
  
  function updateTemperatureCaption() {
    var element = $("#average-temp-time");
    element.empty();
    var dateFormatter = new google.visualization.DateFormat({pattern:'MMM d, yyyy, h:mm:ss a'});
    var updateTime = dateFormatter.formatValue(new Date());
    element.text(updateTime);
    element = $("#average-temp-units");
    element.text(averageTempData.getColumnId(1));

  }
};

function AverageHumidityGraph() {
  var humidQuery = null;

  google.setOnLoadCallback(callbackAveHumidity());

  return false;

  function callbackAveHumidity() {
    var element = $("#average-humidity");
    element.append('<div class="temp-holder"><p>Loading graph...</p></div>');
    var now = new Date();
    var end = new Date(now.getFullYear(), now.getMonth(), now.getDate() - 1, 23, 59, 59, 999);
    var start = new Date(now.getFullYear(), now.getMonth(), now.getDate() - 9, 23, 59, 59 ,999);
    var gvizUrl = serverUrl + "/" + organization + "/depository/"
        + humidityDepository + "/values/average/gviz/?sensor=" + humiditySensor
        + "&start=" + getTimestampFromDate(start) + "&end=" + getTimestampFromDate(end) 
        + "&interval=1440&value-type=point";
 
    humidQuery = new google.visualization.Query(gvizUrl);
    // Set a callback to run when the data has been retrieved.
    humidQuery.send(function(response) {
      responseHandlerHumidity(response);
    });
  }

  function responseHandlerHumidity(response) {
    var element = $("#average-humidity");
    element.empty();
    
    averageHumidityData = response.getDataTable();
    updateHumidityCaption();
    drawHumidity(fixHumidityData(averageHumidityData));
  }
  
  function fixHumidityData(dataTable) {
    var humidityTable = new google.visualization.DataTable();
    humidityTable.addColumn('number', 'Average Humidity');
    humidityTable.addRows(7);
    for (var i = 0; i < dataTable.getNumberOfRows() - 1; i++) {
      humidityTable.setCell(i, 0, dataTable.getValue(i, 1));
    }
    return humidityTable;
  }
  
  function drawHumidity(dataTable) {

    var humidityOptions = {
        width: 155,
        height: 155,
        showAxisLines: true,
        showValueLabels: true,
        labelPosition: 'none'
    };
    
    var humidityChart = new google.visualization.ImageSparkLine(document
        .getElementById('average-humidity'));
    humidityChart.draw(dataTable, humidityOptions);
    
  }
  
  function updateHumidityCaption() {
    var element = $("#average-humidity-time");
    element.empty();
    var dateFormatter = new google.visualization.DateFormat({pattern:'MMM d, yyyy, h:mm:ss a'});
    var updateTime = dateFormatter.formatValue(new Date());
    element.text(updateTime);
    element = $("#average-humidity-units");
    element.text(averageHumidityData.getColumnId(1));

  }
};

function AverageCloudGraph() {
  var humidQuery = null;

  google.setOnLoadCallback(callbackAveCloud());

  return false;

  function callbackAveCloud() {
    var element = $("#average-cloud");
    element.append('<div class="temp-holder"><p>Loading graph...</p></div>');
    var now = new Date();
    var end = new Date(now.getFullYear(), now.getMonth(), now.getDate() - 1, 23, 59, 59, 999);
    var start = new Date(now.getFullYear(), now.getMonth(), now.getDate() - 9, 23, 59, 59 ,999);
    var gvizUrl = serverUrl + "/" + organization + "/depository/"
        + cloudDepository + "/values/average/gviz/?sensor=" + cloudSensor
        + "&start=" + getTimestampFromDate(start) + "&end=" + getTimestampFromDate(end) 
        + "&interval=1440&value-type=point";
 
    humidQuery = new google.visualization.Query(gvizUrl);
    // Set a callback to run when the data has been retrieved.
    humidQuery.send(function(response) {
      responseHandlerCloud(response);
    });
  }

  function responseHandlerCloud(response) {
    var element = $("#average-cloud");
    element.empty();
    
    averageCloudData = response.getDataTable();
    updateCloudCaption();
    drawCloud(fixCloudData(averageCloudData));
  }
  
  function fixCloudData(dataTable) {
    var humidityTable = new google.visualization.DataTable();
    humidityTable.addColumn('number', 'Average Cloud');
    humidityTable.addRows(7);
    for (var i = 0; i < dataTable.getNumberOfRows() - 1; i++) {
      humidityTable.setCell(i, 0, dataTable.getValue(i, 1));
    }
    return humidityTable;
  }
  
  function drawCloud(dataTable) {

    var cloudOptions = {
        width: 155,
        height: 155,
        showAxisLines: true,
        showValueLabels: true,
        labelPosition: 'none'
    };
    
    var cloudChart = new google.visualization.ImageSparkLine(document
        .getElementById('average-cloud'));
    cloudChart.draw(dataTable, cloudOptions);
    
  }
  
  function updateCloudCaption() {
    var element = $("#average-cloud-time");
    element.empty();
    var dateFormatter = new google.visualization.DateFormat({pattern:'MMM d, yyyy, h:mm:ss a'});
    var updateTime = dateFormatter.formatValue(new Date());
    element.text(updateTime);
    element = $("#average-cloud-units");
    element.text(averageCloudData.getColumnId(1));

  }
};
