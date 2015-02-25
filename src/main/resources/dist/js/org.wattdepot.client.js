/*
 * JavaScript implementation of WattDepotClient.
 * 
 * @param url, the full url to the server including the organization id. (e.g. http://server.wattdepot.org:8192/wattdepot/uh/).
 */
if (typeof (org) != "Object") {
  var org = {
    WattDepot : {}
  };
} else if (typeof (org.WattDepot) != "Object") {
  org.WattDepot = {};
}

/*
 * Create a new client with the given server URL. The server URL must include the organization
 * id. The URL must end with '/'.
 */
org.WattDepot.Client = function(url) {

  var serverUrl = url;

  /*
   * Create XmlHttpRequest object.
   */
  function createXmlHttpRequest() {
    if (window.XMLHttpRequest) {
      // code for IE7+, Firefox, Chrome, Opera, Safari
      return new XMLHttpRequest();
    } else {
      // code for IE6, IE5
      return new ActiveXObject("Microsoft.XMLHTTP");
    }
  }

  /*
   * Create a customized ajax setting for GET type ajax.
   */
  function createGetAjaxSetting(url) {
    return {
      url : url,
      type : "GET",
      async : true
    };
  }

  /*
   * Send xmlhttprequest with a callback function. @param xhr XMLHttpRequest
   * object used to send the ajax request. @param ajaxSetting A JSON object
   * containing the settings for the ajax call. @param callback callback
   * function that will be called when the server responds. @param errorHandler
   * The callback function that will be called if there is an error.
   */
  function ajaxSend(xhr, ajaxSetting, callback, errorHandler) {
    xhr.open(ajaxSetting.type, ajaxSetting.url, ajaxSetting.async);
    var returnThisCallback = function(response) {
      if (callback) {
        callback(response);
      }
    };

    xhr.onreadystatechange = function() {
      if (xhr.readyState == 4) {
        if (xhr.status == 200) {
          returnThisCallback(xhr.responseText);
        } else {
          if (errorHandler) {
            errorHandler(xhr);
          }
        }
      }
    };
    xhr.send();
  }

  /*
   * Get the timestamp string used in WattDepot server by given the date object
   * in javascript. @param date A javascript date object. @return the timestamp
   * string.
   */
  function getTimestampFromDate(date) {
    return date.toISOString();
  }

  /*
   * Convert the timestamp string to a Date object (Safari fail to convert
   * timestamp to Date. So use this function to covert manually.) @param tstamp
   * timestamp string from a WattDepot server response @return the Date object.
   */
  function convertTimestampToDate(tstamp) {
    var year = tstamp.substr(0, 4);
    tstamp = tstamp.substr(tstamp.indexOf("-", 0) + 1, tstamp.length);
    var month = tstamp.substr(0, 2);
    tstamp = tstamp.substr(tstamp.indexOf("-", 0) + 1, tstamp.length);
    var day = tstamp.substr(0, 2);
    tstamp = tstamp.substr(tstamp.indexOf("T", 0) + 1, tstamp.length);
    var hr = tstamp.substr(0, 2);
    tstamp = tstamp.substr(tstamp.indexOf(":", 0) + 1, tstamp.length);
    var min = tstamp.substr(0, 2);
    tstamp = tstamp.substr(tstamp.indexOf(":", 0) + 1, tstamp.length);
    var sec = tstamp.substr(0, 2);
    tstamp = tstamp.substr(tstamp.indexOf(".", 0) + 1, tstamp.length);
    var milisec = tstamp.substr(0, 3);
    return new Date(year, month - 1, day, hr, min, sec, milisec);
  }

  /*
   * Get the health string from a server. Basically 'ping' the server. (BROKEN)
   * 
   * @param callback callback function that will be called when the server
   * responds.
   * 
   * @param errorHandler Callback function to be called if there is an error in
   * the ajax request.
   */
  function getServerHealth(callback, errorHandler) {
    var xhr = createXmlHttpRequest();
    var setting = createGetAjaxSetting(serverUrl);
    ajaxSend(xhr, setting, callback, errorHandler);
  }

  /*
   * Get a list of all the defined Depositories.
   * 
   * @param callback callback function that will be called when the server
   * responds.
   * 
   * @param errorHandler Callback function to be called if there is an error in
   * the ajax request.
   */
  function getAllDepositories(callback, errorHandler) {
    var xhr = createXmlHttpRequest();
    var setting = createGetAjaxSetting(serverUrl + "depositories/");
    ajaxSend(xhr, setting, callback, errorHandler);
  }

  /*
   * Get a the defined Depository for the given id.
   * 
   * @param depositoryId the id to get.
   * 
   * @param callback callback function that will be called when the server
   * responds.
   * 
   * @param errorHandler Callback function to be called if there is an error in
   * the ajax request.
   */
  function getDepository(depositoryId, callback, errorHandler) {
    var xhr = createXmlHttpRequest();
    var setting = createGetAjaxSetting(serverUrl + "depository/" + depositoryId);
    ajaxSend(xhr, setting, callback, errorHandler);
  }

  /*
   * Get a list of all the defined Sensors.
   * 
   * @param callback callback function that will be called when the server
   * responds.
   * 
   * @param errorHandler Callback function to be called if there is an error in
   * the ajax request.
   */
  function getAllSensors(callback, errorHandler) {
    var xhr = createXmlHttpRequest();
    var setting = createGetAjaxSetting(serverUrl + "sensors/");
    ajaxSend(xhr, setting, callback, errorHandler);
  }

  /*
   * Get a the defined Sensor for the given id.
   * 
   * @param sensorId the id to get.
   * 
   * @param callback callback function that will be called when the server
   * responds.
   * 
   * @param errorHandler Callback function to be called if there is an error in
   * the ajax request.
   */
  function getSensor(sensorId, callback, errorHandler) {
    var xhr = createXmlHttpRequest();
    var setting = createGetAjaxSetting(serverUrl + "sensor/" + sensorId);
    ajaxSend(xhr, setting, callback, errorHandler);
  }

  /*
   * Get a list of all the defined SensorGroups.
   * 
   * @param callback callback function that will be called when the server
   * responds.
   * 
   * @param errorHandler Callback function to be called if there is an error in
   * the ajax request.
   */
  function getAllSensorGroups(callback, errorHandler) {
    var xhr = createXmlHttpRequest();
    var setting = createGetAjaxSetting(serverUrl + "sensor-groups/");
    ajaxSend(xhr, setting, callback, errorHandler);
  }

  /*
   * Get a the defined SensorGroup for the given id.
   * 
   * @param groupId the id to get.
   * 
   * @param callback callback function that will be called when the server
   * responds.
   * 
   * @param errorHandler Callback function to be called if there is an error in
   * the ajax request.
   */
  function getSensorGroup(groupId, callback, errorHandler) {
    var xhr = createXmlHttpRequest();
    var setting = createGetAjaxSetting(serverUrl + "sensor-group/" + groupId);
    ajaxSend(xhr, setting, callback, errorHandler);
  }

  /*
   * Get a list of all the defined CollectorProcessDefinitions.
   * 
   * @param callback callback function that will be called when the server
   * responds.
   * 
   * @param errorHandler Callback function to be called if there is an error in
   * the ajax request.
   */
  function getAllCollectorProcessDefinitions(callback, errorHandler) {
    var xhr = createXmlHttpRequest();
    var setting = createGetAjaxSetting(serverUrl
        + "collector-process-definitions/");
    ajaxSend(xhr, setting, callback, errorHandler);
  }

  /*
   * Get a the defined CollectorProcessDefinition for the given id.
   * 
   * @param definitionId the id to get.
   * 
   * @param callback callback function that will be called when the server
   * responds.
   * 
   * @param errorHandler Callback function to be called if there is an error in
   * the ajax request.
   */
  function getCollectorProcessDefinition(definitionId, callback, errorHandler) {
    var xhr = createXmlHttpRequest();
    var setting = createGetAjaxSetting(serverUrl
        + "collector-process-definition/" + definitionId);
    ajaxSend(xhr, setting, callback, errorHandler);
  }
  
  /*
   * Get a list of all the sensors contributing measurements to the given depository.
   * 
   * @param depositoryId the depository id.
   * 
   * @param callback callback function that will be called when the server
   * responds.
   * 
   * @param errorHandler Callback function to be called if there is an error in
   * the ajax request.
   */
  function getDepositorySensors(depositoryId, callback, errorHandler) {
    var xhr = createXmlHttpRequest();
    var setting = createGetAjaxSetting(serverUrl + "depository/" + depositoryId
        + "/sensors/");
    ajaxSend(xhr, setting, callback, errorHandler);
  }

  /*
   * Get a list of Measurements for the given depository, sensor, start time and
   * end time.
   * 
   * @param depositoryId the depository id.
   * 
   * @param sensorId the sensor id.
   * 
   * @param startTime the start time.
   * 
   * @param endTime the end time.
   * 
   * @param callback callback function that will be called when the server
   * responds.
   * 
   * @param errorHandler Callback function to be called if there is an error in
   * the ajax request.
   */
  function getMeasurementsBetween(depositoryId, sensorId, startTime, endTime,
      callback, errorHandler) {
    var xhr = createXmlHttpRequest();
    var setting = createGetAjaxSetting(serverUrl + "depository/" + depositoryId
        + "/measurements/?sensor=" + sensorId + "&start=" + startTime + "&end="
        + endTime);
    ajaxSend(xhr, setting, callback, errorHandler);
  }

  /*
   * Get the interpolated value for the given depository, sensor, and time.
   * 
   * @param depositoryId the depository id.
   * 
   * @param sensorId the sensor id.
   * 
   * @param startTime the start time.
   * 
   * @param endTime the end time.
   * 
   * @param callback callback function that will be called when the server
   * responds.
   * 
   * @param errorHandler Callback function to be called if there is an error in
   * the ajax request.
   */
  function getValueAt(depositoryId, sensorId, timestamp, callback, errorHandler) {
    var xhr = createXmlHttpRequest();
    var setting = createGetAjaxSetting(serverUrl + "depository/" + depositoryId
        + "/value/?sensor=" + sensorId + "&timestamp=" + timestamp);
    ajaxSend(xhr, setting, callback, errorHandler);
  }

  /*
   * Get the interpolated interval value for the given depository, sensor, start
   * time and end time.
   * 
   * @param depositoryId the depository id.
   * 
   * @param sensorId the sensor id.
   * 
   * @param startTime the start time.
   * 
   * @param endTime the end time.
   * 
   * @param callback callback function that will be called when the server
   * responds.
   * 
   * @param errorHandler Callback function to be called if there is an error in
   * the ajax request.
   */
  function getValueBetween(depositoryId, sensorId, startTime, endTime,
      callback, errorHandler) {
    var xhr = createXmlHttpRequest();
    var setting = createGetAjaxSetting(serverUrl + "depository/" + depositoryId
        + "/value/?sensor=" + sensorId + "&start=" + startTime + "&end="
        + endTime);
    ajaxSend(xhr, setting, callback, errorHandler);
  }

  /*
   * Get the earliest interpolated interval value for the given depository, and
   * sensor.
   * 
   * @param depositoryId the depository id.
   * 
   * @param sensorId the sensor id.
   * 
   * @param callback callback function that will be called when the server
   * responds.
   * 
   * @param errorHandler Callback function to be called if there is an error in
   * the ajax request.
   */
  function getEarliestValue(depositoryId, sensorId, callback, errorHandler) {
    var xhr = createXmlHttpRequest();
    var setting = createGetAjaxSetting(serverUrl + "depository/" + depositoryId
        + "/value/?sensor=" + sensorId + "&earliest=true");
    ajaxSend(xhr, setting, callback, errorHandler);
  }

  /*
   * Get the earliest interpolated interval value for the given depository, and
   * sensor.
   * 
   * @param depositoryId the depository id.
   * 
   * @param sensorId the sensor id.
   * 
   * @param callback callback function that will be called when the server
   * responds.
   * 
   * @param errorHandler Callback function to be called if there is an error in
   * the ajax request.
   */
  function getLatestValue(depositoryId, sensorId, callback, errorHandler) {
    var xhr = createXmlHttpRequest();
    var setting = createGetAjaxSetting(serverUrl + "depository/" + depositoryId
        + "/value/?sensor=" + sensorId + "&latest=true");
    ajaxSend(xhr, setting, callback, errorHandler);
  }

  /*
   * Get the measurement summary for the given depository, and
   * sensor.
   * 
   * @param depositoryId the depository id.
   * 
   * @param sensorId the sensor id.
   * 
   * @param orgId the organization id.
   * 
   * @param callback callback function that will be called when the server
   * responds.
   * 
   * @param errorHandler Callback function to be called if there is an error in
   * the ajax request.
   */
  function getDepositorySensorSummary(depositoryId, sensorId, orgId, callback, errorHandler) {
    var xhr = createXmlHttpRequest();
    var setting = createGetAjaxSetting(serverUrl + orgId + "/depository/" + depositoryId
        + "/summary/?sensor=" + sensorId);
    ajaxSend(xhr, setting, callback, errorHandler);
    
  }
  
  return {
    getTimestampFromDate : getTimestampFromDate,
    convertTimestampToDate : convertTimestampToDate,
    getAllDepositories : getAllDepositories,
    getDepository : getDepository,
    getAllSensors : getAllSensors,
    getSensor : getSensor,
    getAllSensorGroups : getAllSensorGroups,
    getSensorGroup : getSensorGroup,
    getAllCollectorProcessDefinitions : getAllCollectorProcessDefinitions,
    getCollectorProcessDefinition : getCollectorProcessDefinition,
    getDepositorySensors : getDepositorySensors,
    getMeasurementsBetween : getMeasurementsBetween,
    getValueAt : getValueAt,
    getValueBetween : getValueBetween,
    getEarliestValue : getEarliestValue,
    getLatestValue : getLatestValue,
    getDepositorySensorSummary : getDepositorySensorSummary
  };
};