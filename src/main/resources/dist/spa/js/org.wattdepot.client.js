/*
 * WattDepotClient's constructor.
 *
 * @param url WattDepot Server url for this WDClient object.
 */
if (typeof(org) != "Object") {
  var org = {
    WattDepot : {}
  };
}
else if (typeof(org.WattDepot) != "Object") {
  org.WattDepot = {};
}

org.WattDepot.Client = function(url) {
   
   var serverUrl = url;

  /*
   * Create XmlHttpRequest object.
   */
  function createXmlHttpRequest() {
    if (window.XMLHttpRequest) {
    // code for IE7+, Firefox, Chrome, Opera, Safari
      return new XMLHttpRequest();
    }
    else {
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
   * Send xmlhttprequest with a callback function.
   * @param xhr XMLHttpRequest object used to send the ajax request.
   * @param ajaxSetting A JSON object containing the settings for the ajax call.
   * @param callback callback function that will be called when the server responsed.
   * @param errorHandler The callback function that will be called if there is an error.
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
        }
        else {
          if (errorHandler) {
            errorHandler(xhr);
          }
        }
      }
    };
    xhr.send();
  }
  
  /*
   * Convert xml to json object
   * @param xml text that need to be converted.
   * @return the result Json object
   */
  function xmlToJson(xml) {  
    var obj = {}; 
    // check if element is self closed, if so it might has empty text node.
    var selfClosed = false;
    // element  
    if (xml.nodeType == 1) {
      // check attributes
      if (xml.attributes.length > 0) {
        for (var j = 0; j < xml.attributes.length; j++) {  
          obj[xml.attributes[j].nodeName] = xml.attributes[j].nodeValue;  
        }  
      }      
    }
    // check child node  
    if (xml.hasChildNodes()) {
      for(var i = 0; i < xml.childNodes.length; i++) {
        // check if the child node has another child node, if not,
        // this child node is the text value of the parent node.
        if (xml.childNodes[i].nodeType == 3 && i < xml.childNodes.length - 1) {
          selfClosed = true;
        }
        if (xml.childNodes[i].nodeType != 3 || selfClosed) {
          //check if this node name is existed in obj, if not, add 
          //this node as a property to current obj
          if (typeof(obj[xml.childNodes[i].nodeName]) == 'undefined') {
            obj[xml.childNodes[i].nodeName] = xmlToJson(xml.childNodes[i]);
          }
          else {
            //check if there is the array for this node name, if not, create 
            //an array and add the existing one to the array
            if (!(obj[xml.childNodes[i].nodeName] instanceof Array)) {
              var old = obj[xml.childNodes[i].nodeName];  
              obj[xml.childNodes[i].nodeName] = [];  
              obj[xml.childNodes[i].nodeName].push(old);
            }
            obj[xml.childNodes[i].nodeName].push(xmlToJson(xml.childNodes[i]));
          }     
        }
        else {
          //if it is a text node, just return the value. No object created 
          //for text node.
          return xml.childNodes[i].nodeValue;
        }
      }
    }
    //return this object
    return obj;  
  }

  /*
   * Get a specific node of a json object. If the target node appears more than one time in the object,
   * get the first appearance as the result.
   * @param json json object that contains the target node
   * @param target node name of the target node
   * @return the first found node with the target node name, return undefined if nothing matches.
   */
  function getJsonNode(json, target) {
    var result;
    for (nodeName in json) {
      if (nodeName == target) {
        result = json[nodeName];
      }
      else if (typeof(json[nodeName]) == "object") {
        result = getJsonNode(json[nodeName], target);
      }
      if (typeof(result) != "undefined") {
        return result;
      }
    }
    return result;
  }
  
  /*
   * Get a specific source node from an array of source nodes. 
   * If the target node appears more than one time in the object,
   * get the first appearance as the result.
   * @param sourceArray source array that contains the target node
   * @param sourceName source name of the target node
   * @return the first found source node with the target node name, 
   * return undefined if nothing matches.
   */
  function getSourceNodeByName(sourceArray, sourceName) {
    var result;
    for (var i = 0; i < sourceArray.length; i++) {
      if (getJsonNode(sourceArray[i], "Name") == sourceName) {
        result = sourceArray[i];
      }
      else if (sourceArray[i] instanceof Array) {
        result = getSourceNodeByName(sourceArray[i], sourceName);
      }
      if (typeof(result) != "undefined") {
        return result;
      }
    }
    return result;
  }
  
  /*
   * Get the property value for a specific property key. This can be used in any response that has
   * a "property" node
   * @param json json object that contains the "property" node
   * @param propertyKey the "key" value of the property
   * @return the value of the property that has the given key, return undefined if nothing found.
   */
  function getPropertyValue(json, propertyKey) {
    var value;
    var property = getJsonNode(json, "Property");
    //Check if there is only one property
    if (property && property.Key == propertyKey) {
      value = property.Value;
    }
    else {
      //Property is expected to be an array of objects
      for (i in property) {
        if (getJsonNode(property[i], "Key") == propertyKey) {
          value = getJsonNode(property[i], "Value");
        }
      }
    }
    return value;
  }  
  
  /*
   * Get the data unit for the given data type
   * @param dataType data are displayed in the web application.
   * @return the unit for the given data type
   */
  function getUnit(dataType) {
    var unit;
    if (dataType.toLowerCase().match("power")) {
      unit = "Watt";
    }
    else if (dataType.toLowerCase().match("energy")) {
      unit = "Watt Hour";
    }
    return unit;
  }

  /*
   * Get the source name by parsing the source link
   * @param sourceLink the source link string that contains the source name.
   * @return the source name
   */
  function getSourceNameFromLink(sourceLink) {
    while(sourceLink.indexOf("/", 0) >= 0){
      sourceLink = sourceLink.substr(sourceLink.indexOf("/",0) + 1, sourceLink.length);
    }
    return sourceLink;
  }

  /*
   * Get the timestamp string used in WattDepot server by given the date object in javascript.
   * @param date A javascript date object.
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

    var timestamp = date.getFullYear() + '-' + padZero(date.getMonth() + 1, 2) + '-' + padZero(date.getDate(), 2);
    timestamp = timestamp + 'T' + padZero(date.getHours(), 2) + ':' + padZero(date.getMinutes(), 2) + ':' + padZero(date.getSeconds(), 2);
    timestamp = timestamp + '.' + padZero(date.getMilliseconds(), 3);
    return timestamp;
  }
  

  /*
   * Convert the timestamp string to a Date object
   * (Safari fail to convert timestamp to Date. So use this function to covert manually.)
   * @param tstamp timestamp string from a WattDepot server response
   * @return the Date object.
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
   * Convert a number from Watt to KWatt.
   * @param Watt number in Watt.
   * @return number in KWatt.
   */
  function convertToKW(Watt) {
    var KW = Watt/1000;
    return parseFloat(KW.toFixed(1));
  }
 
  /*
   * Get the health string from a server. Basically 'ping' the server. (BROKEN)
   *
   * @param callback callback function that will be called when the server responsed.
   * @param errorHandler Callback function to be called if there is an error in the ajax request.
   */
  function getServerHealth(callback, errorHandler) {
    var xhr = createXmlHttpRequest();
    var setting = createGetAjaxSetting(serverUrl + "health");
    ajaxSend(xhr, setting, callback, errorHandler);
  }
 
  /*
   * Get the summary for all the sources in the WattDepot server.
   * @param callback callback function that will be called when the server responsed.
   * @param errorHandler Callback function to be called if there is an error in the ajax request.
   */
  function getAllSource(callback, errorHandler) {
    var xhr = createXmlHttpRequest();
    var setting = createGetAjaxSetting(serverUrl + "wattdepot/admin/sensors/");
    ajaxSend(xhr, setting, callback, errorHandler);
  }
  
  /*
   * Get the source detail information for a specific source from the WattDepot server 
   * with a callback function.
   *
   * @param source name of the source.
   * @param callback callback function that will be called when the server responsed.
   * @param errorHandler Callback function to be called if there is an error in the ajax request.
   */
  function getSourceDetail(source, callback, errorHandler) {
    var xhr = createXmlHttpRequest();
    var setting = createGetAjaxSetting(serverUrl + "wattdepot/admin/sensor/" + source);
    ajaxSend(xhr, setting, callback, errorHandler);
  }
  
  /*
   * Get the source summary for a specific sourcefrom the WattDepot server 
   * with a callback function.
   *
   * @param source name of the source.
   * @param callback callback function that will be called when the server responsed.
   * @param errorHandler Callback function to be called if there is an error in the ajax request.
   */
  function getSourceSummary(source, callback, errorHandler) {
    var xhr = createXmlHttpRequest();
    var setting = createGetAjaxSetting(serverUrl + "wattdepot/admin/sensor/" + source);
    ajaxSend(xhr, setting, callback, errorHandler);
  }
  
  /*
   * Get all sensordata for a specific source from the WattDepot server 
   * with a callback function.
   *
   * @param source name of the source.
   * @param callback callback function that will be called when the server responsed.
   * @param errorHandler Callback function to be called if there is an error in the ajax request.
   */
   function getSourceSensorData(source, callback, errorHandler) {
    var xhr = createXmlHttpRequest();
    var setting = createGetAjaxSetting(serverUrl + "wattdepot/admin/sensors/" + source + "/sensordata/");
    ajaxSend(xhr, setting, callback, errorHandler);
  }
  
   /*
    * Get the earliest sensordata for a specific source from the WattDepot server 
    * with a callback function.
    *
    * @param source name of the source.
    * @param callback callback function that will be called when the server responsed.
    * @param errorHandler Callback function to be called if there is an error in the ajax request.
    */
   function getSourceEarliestSensorData(source, callback, errorHandler) {
     var xhr = createXmlHttpRequest();
     var setting = createGetAjaxSetting(serverUrl + "wattdepot/admin/depository/power/value/?sensor=" + source + "&earliest=true");
     ajaxSend(xhr, setting, callback, errorHandler);
   }
   
  /*
   * Get the latest sensordata for a specific source from the WattDepot server 
   * with a callback function.
   *
   * @param source name of the source.
   * @param callback callback function that will be called when the server responsed.
   * @param errorHandler Callback function to be called if there is an error in the ajax request.
   */
  function getSourceLatestSensorData(source, callback, errorHandler) {
    var xhr = createXmlHttpRequest();
    var setting = createGetAjaxSetting(serverUrl + "wattdepot/admin/depository/power/value/?sensor=" + source + "&latest=true");
    ajaxSend(xhr, setting, callback, errorHandler);
  }
  
  /*
   * Get the sensordata at a specific time for a specific source from the WattDepot server 
   * with a callback function.
   *
   * @param source name of the source.
   * @param timestamp The timestamp at which to get the sensor data.
   * @param callback callback function that will be called when the server responsed.
   * @param errorHandler Callback function to be called if there is an error in the ajax request.
   */
  function getSourceSensorDataAt(source, timestamp, callback, errorHandler) {
    var xhr = createXmlHttpRequest();
    var setting = createGetAjaxSetting(serverUrl + "wattdepot/admin/sensors/" + source + "/sensordata/" + timestamp);
    ajaxSend(xhr, setting, callback, errorHandler);
  }
  
  /*
   * Get the sensordata for a specific source between two timespots from the WattDepot server 
   * with a callback function.
   *
   * @param source name of the source.
   * @param startTime The starting time from which to get the sensor data.
   * @param endTime The ending time at which to stop getting the stored sensor data.
   * @param callback callback function that will be called when the server responsed.
   * @param errorHandler Callback function to be called if there is an error in the ajax request.
   */
  function getSourceSensorDataBetween(source, startTime, endTime, callback, errorHandler) {
    var xhr = createXmlHttpRequest();
    var urlString = serverUrl + "sources/" + source + "/sensordata/?startTime=" + startTime + "&endTime=" + endTime + "&fetchAll=true";
    var setting = createGetAjaxSetting(urlString);
    ajaxSend(xhr, setting, callback, errorHandler);
  }
  
  /*
   * Get the power data for a specific source at specific timespots from the WattDepot server 
   * with a callback function.
   *
   * @param source name of the source.
   * @param timestamp The timestamp at which to get the power data.
   * @param callback callback function that will be called when the server responsed.
   * @param errorHandler Callback function to be called if there is an error in the ajax request.
   */
  function getSourcePowerDataAt(source, timestamp, callback, errorHandler) {
    var xhr = createXmlHttpRequest();
    var setting = createGetAjaxSetting(serverUrl + "sources/" + source + "/power/" + timestamp);
    ajaxSend(xhr, setting, callback, errorHandler);
  }
  
  /*
   * Get the energy data for a specific source between two timespots from the WattDepot server 
   * with a callback function.
   *
   * @param source name of the source.
   * @param startTime The starting time from which to start returning stored energy data.
   * @param endTime The time at which to stop returning stored energy data.
   * @param callback callback function that will be called when the server responsed.
   * @param errorHandler Callback function to be called if there is an error in the ajax request.
   */
  function getSourceEnergyDataBetween(source, startTime, endTime, callback, errorHandler) {
    var xhr = createXmlHttpRequest();
    var urlString = serverUrl + "sources/" + source + "/energy/?startTime=" + startTime + "&endTime=" + endTime;
    var setting = createGetAjaxSetting(urlString);
    ajaxSend(xhr, setting, callback, errorHandler); 
  }
  
  /*
   * Get the carbon data for a specific source between two timespots from the WattDepot server 
   * with a callback function.
   *
   * @param source name of the source.
   * @param startTime The starting time from which to start returning stored carbon data.
   * @param endTime The time at which to stop returning stored carbon data.
   * @param callback callback function that will be called when the server responsed.
   * @param errorHandler Callback function to be called if there is an error in the ajax request.
   */
  function getCarbonDataBetween(source, startTime, endTime, callback, errorHandler) {
    var xhr = createXmlHttpRequest();
    var urlString = serverUrl + "sources/" + source + "/carbon/?startTime=" + startTime + "&endTime=" + endTime;
    var setting = createGetAjaxSetting(urlString);
    ajaxSend(xhr, setting, callback, errorHandler); 
  }
  
  return {
    getJsonNode : getJsonNode,
  
    getUnit : getUnit,

    getPropertyValue : getPropertyValue,

    getSourceNameFromLink : getSourceNameFromLink,

    getTimestampFromDate : getTimestampFromDate,

    convertTimestampToDate : convertTimestampToDate,

    convertToKW : convertToKW,

    getSourceNodeByName : getSourceNodeByName,

    getAllSource : getAllSource,
  
    getSourceSensorData : getSourceSensorData,
  
    getSourceLatestSensorData : getSourceLatestSensorData,

    getSourceEarliestSensorData : getSourceEarliestSensorData,

    getServerHealth : getServerHealth,

    getSourceDetail : getSourceDetail,

    getSourceSummary : getSourceSummary,

    getSourceSensorDataAt : getSourceSensorDataAt,

    getSourceSensorDataBetween : getSourceSensorDataBetween,

    getSourcePowerDataAt : getSourcePowerDataAt,

    getSourceEnergyDataBetween : getSourceEnergyDataBetween,
    
    getCarbonDataBetween : getCarbonDataBetween
  };
};
 
