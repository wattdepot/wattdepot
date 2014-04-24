/* 
 *     Wattdepot Permalink system based upon the Wattdepot-apps Single Page Application Permalink system
 *
 *     Kendyll Doi, Christopher Foo, Dylan Kobayashi 2012
 *     
 */

var numVis;
var server;
var depository = new Array();
var sensor = new Array();
var datatype = new Array();
var sdate = new Array();
var edate = new Array();
var valtype = new Array();
var freq = new Array();
var active = new Array();
var nowTime = new Array();
var numFormsFinished = 0;

// allows selecting of URL on select
$(document).ready(function() {
  $('#linkSpace').click(function() {
    this.select();
  });
});

/*
 * Wattdepot-apps getUrlVariable Gets variables placed in url of window. Based
 * on code provided from
 * http://css-tricks.com/snippets/javascript/get-url-variables/
 * 
 * @param search id for variable in url.
 */
function getUrlVariable(search) {
  var query = window.location.search.substring(1);
  var variables = query.split("&");
  for (var i = 0; i < variables.length; i++) {
    var items = variables[i].split("=");
    if (items[0] == search) {
      // alert(items[1]);
      var temp = items[1].split(";");
      return (temp);
    }
  }
  return (null);
}

/*
 * Gets url from the window.
 * 
 */
function getUrl() {
  return window.location.protocol + "//" + window.location.host
      + window.location.pathname;
}

/*
 * Checks URL for variables used in the permalink system placing found variables
 * into script variables for recall.
 * 
 */
function permalinkCheck() {
  if (window.location.search.length == 0) {
    return false
  } else {
    numVis = getUrlVariable('no');
    depository = getUrlVariable('d');
    sensor = getUrlVariable('s');
    sdate = getUrlVariable('start');
    edate = getUrlVariable('end');
    valtype = getUrlVariable('t');
    freq = getUrlVariable('f');
    active = getUrlVariable('act');
    nowTime = getUrlVariable('now');
    return true
  }
}

/*
 * gathers together the declared values to make a permalink.
 * 
 */
function gatherVariables() {
  var temp = '?';
  temp += 'no=' + activeIndex.length;
  var depositline = 'd=';
  var sensorline = 's=';
  var startline = 'start=';
  var endline = 'end=';
  var nowline = 'now=';
  var typeline = 't=';
  var intline = 'f=';
  var actline = 'act=';

  for (var i = 0; i < numRows + 1; i++) {
    if (findActiveIndex(i) + 1) {
      depositline += $('#depositorySelect' + i).val() + ';';
      sensorline += $('#sensorSelect' + i).val() + ';';
      startline += wdClient.getTimestampFromDate(getDate('start', i))
          + ';';
      endline += wdClient.getTimestampFromDate(getDate('end', i))
          + ';';
      if ($('#endTimeNow' + i).is(':checked')) {
        nowline += 'y;';
      } else {
        nowline += 'n;';
      }
      typeline += $('#dataType' + i).val() + ';';
      intline += $('#frequency' + i).val() + ';';
      if ($('#show' + i).is(':checked')) {
        actline += 'y;';
      } else {
        actline += 'n;';
      }
    }
  }
  return temp + '&' + depositline + '&' + sensorline + '&' + startline + '&'
      + endline + '&' + nowline + '&' + typeline + '&' + intline + '&'
      + actline;
}

/*
 * Uses permalink values to recreated page based on all provided variables.
 */
function fillPage() {
  numFormsFinished = 0;
  for (var i = 0; i < numVis; i++) {
    var actTemp = false;
    if (active[i] == 'y') {
      actTemp = true;
    }
    var nowTemp = false;
    if (nowTime[i] == 'y') {
      nowTemp = true;
    }
    if (typeof edate[i] == "undefined") {
      edate[i] = edate[i - 1];
    }
    createFilledVisualizerForm(actTemp, depository[i], sensor[i], wdClient.convertTimestampToDate(sdate[i]),
        wdClient.convertTimestampToDate(edate[i]), nowTemp, valtype[i], freq[i]);
    numFormsFinished++;
    if (numFormsFinished == numVis) {
      visualize();
    }
  }
}

function fixTimeString(timestr) {
  return timestr.replace(' ', '-');
}

function unFixTimeString(timestr) {
  return timestr.replace('-', ' ');
}