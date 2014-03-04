<!DOCTYPE html>
<html>
<head>
<title>WattDepot Organization Measurement Visualization</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<!-- Bootstrap -->
<link rel="stylesheet" href="/webroot/bower_components/bootstrap/dist/css/bootstrap.min.css">
<!-- Optional theme -->
<link rel="stylesheet" href="/webroot/bower_components/bootstrap/dist/css/bootstrap-theme.min.css">
<link rel="stylesheet" href="/webroot/bower_components/eonasdan-bootstrap-datetimepicker/build/css/bootstrap-datetimepicker.min.css">
<link rel="stylesheet/less" type="text/css" href="/webroot/dist/css/style.less">
<script src="/webroot/dist/js/less-1.3.0.min.js"></script>
<!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
<script src="/webroot/bower_components/jquery/dist/jquery.js"></script>
<script src="/webroot//bower_components/moment/min/moment.min.js"></script>
<script src="/webroot/bower_components/bootstrap/dist/js/bootstrap.min.js"></script>
<script src="/webroot/bower_components/eonasdan-bootstrap-datetimepicker/build/js/bootstrap-datetimepicker.min.js"></script>
<script src="/webroot/dist/js/wattdepot-visualizer.js"></script>
<script src="/webroot/dist/js/org.wattdepot.client.js"></script>

<script> 
</script> 

</head>
<body>
<nav class="navbar navbar-default" role="navigation">
  <!-- Brand and toggle get grouped for better mobile display -->
  <div class="navbar-header">
    <button type="button" class="navbar-toggle" data-toggle="collapse" data-target="#bs-example-navbar-collapse-1">
      <span class="sr-only">Toggle navigation</span>
      <span class="icon-bar"></span>
      <span class="icon-bar"></span>
      <span class="icon-bar"></span>
    </button>
    <a class="navbar-brand" href="#"><img src="/webroot/dist/wattdepot-logo.png"> WattDepot</a>
  </div>

  <!-- Collect the nav links, forms, and other content for toggling -->
  <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
    <ul class="nav navbar-nav">
      <li><a href="/wattdepot/${orgId}/">Definitions</a></li>
      <li><a href="/wattdepot/${orgId}/summary/">Measurement, Depository Summary</a></li>
      <li class="active"><a href="/wattdepot/${orgId}/visualize/">Measurement Visualization</a></li>
    </ul>
    <ul class="nav navbar-nav navbar-right">
      <li><a href="#">${orgId}</a></li>
    </ul>
  </div><!-- /.navbar-collapse -->
</nav>
 
  <div class="container">
    <!-- Visualizer Control Panel -->
    <div id="visualizerControlPanel">
      <div id="formLabels" class="row">
        <div class="col-xs-2">Show?</div>
        <div class="col-xs-2">Sensor:</div>
        <div class="col-xs-3">Start Time:</div>
        <div class="col-xs-3">End Time:</div>
        <div class="col-xs-2">Sample Interval:</div>
      </div>
      <div id="visualizerFormsDiv">
        <div class="row form" id="form1">
          <div class="row">
            <div id="depositoryDiv1" class="col-xs-2 control-group">
              <div class="col-xs-2">
                <a href="#" rel="tooltip" data-placement="right" title="Show / Hide the data.">
                  <input type="checkbox" class="col-xs-11" id="show1"/>
                </a>
              </div>
              <div class="col-xs-9">
                <select id="depositorySelect1" class="col-xs-12 chzn-select" data-placehoder="Choose Depository..." onchange="selectedDepository(1)" data-toggle="tooltip" title="Choose Depository...">
                  <#list depositories as d>
                  <option value="${d.id}">${d.name}</option>
                  </#list>
                </select>
              </div>
            </div>
            <div id="sensorDiv1" class="col-xs-2 control-group">
              <select id="sensorSelect1" class="col-xs-12" onchange="selectedSensor(1)">
              </select>
            </div>
            <div class="form-group col-xs-2">
                <div class='input-group date' id='startdatetimepicker1'>
                    <input type='text' class="form-control" data-format="MM/DD/YY HH:mm"/>
                    <span class="input-group-addon"><span class="glyphicon glyphicon-calendar"></span>
                    </span>
                </div>
                <div id="startInfo1"></div>
            </div>
            <div class="form-group col-xs-2">
                <div class='input-group date' id='enddatetimepicker1'>
                    <input type='text' class="form-control" data-format="MM/DD/YY HH:mm"/>
                    <span class="input-group-addon"><span class="glyphicon glyphicon-calendar"></span>
                    </span>
                </div>
                <div id="endInfo1"></div>
            </div>
            <div class="col-xs-2 control-group">
              <select id="dateInterval1">
                <option value="instant">Instantaneous Value</option>
                <option value="cumm">Cummulative Value</option>
              </select>
            </div>
            <div class="col-xs-2 control-group">
              <select id="interval1">
                <option value="5">5 mins</option>
                <option value="15">15 mins</option>
                <option value="30">30 mins</option>
                <option value="60">1 hr</option>
                <option value="120">2 hr</option>
                <option value="1400">1 Day</option>
                <option value="10080">1 Week</option>
                <option value="43200">1 Month</option>
              </select>
            </div> 
          </div>
        </div>
      </div>
      <div id="controlPanelButtonRow" class="row">
        <div class="col-xs-8 control-group">
          <button id="visualizeButton" class="col-xs-2 btn btn-primary" onclick="visualize();" diabled>Visualize!</button>
        </div>
        <div class="col-xs-3"></div>
        <div class="col-xs-1 control-group">
          <button id="addRowButton" class="btn btn-primary" onclick="addRow();" data-toggle="tooltip" data-placement="left" title="Add another row"><span class="glyphicon glyphicon-plus"></span></button>
        </div>
      </div>
    </div>
  </div>
<script>
var numRows = 1;

$(document).ready(function() {
    selectedDepository(1);
});

$(function () {
    $('#startdatetimepicker1').datetimepicker();
    $('#enddatetimepicker1').datetimepicker();
});

function addRow() {
    numRows++;
    insertRowHTML(numRows);
};

function insertRowHTML(index) {
    $('#visualizerFormsDiv').append(
        '<div class="row form" id="form'+index+'">'
        +'  <div class="row">'
        +'    <div id="depositoryDiv' + index + '" class="col-xs-2 control-group">'
        +'      <div class="col-xs-2">'
        +'        <input type="checkbox" class="col-xs-11" id="show' + index +'"/>'
        +'      </div>'
        +'      <div class="col-xs-9">'
        +'        <select id="depositorySelect' + index + '" class="col-xs-12 chzn-select" data-placehoder="Choose Depository..." onchange="selectedDepository('+index+')" data-toggle="tooltip" title="Choose Depository...">'
        +'        </select>'
        +'      </div>'
        +'    </div>'
        +'    <div id="sensorDiv' + index + '" class="col-xs-2 control-group">'
        +'      <select id="sensorSelect' + index + '" class="col-xs-12" onchange="selectedSensor(' + index + ')">'
        +'      </select>'
        +'    </div>'
        +'    <div class="form-group col-xs-2">'
        +'        <div class="input-group date" id="startdatetimepicker' + index + '">'
        +'            <input type="text" class="form-control" data-format="MM/DD/YY HH:mm"/>'
        +'            <span class="input-group-addon"><span class="glyphicon glyphicon-calendar"></span>'
        +'            </span>'
        +'        </div>'
        +'        <div id="startInfo' + index + '"></div>'
        +'    </div>'
        +'    <div class="form-group col-xs-2">'
        +'        <div class="input-group date" id="enddatetimepicker' + index + '">'
        +'            <input type="text" class="form-control" data-format="MM/DD/YY HH:mm"/>'
        +'            <span class="input-group-addon"><span class="glyphicon glyphicon-calendar"></span>'
        +'            </span>'
        +'        </div>'
        +'        <div id="endInfo' + index + '"></div>'
        +'    </div>'
        +'    <div class="col-xs-2 control-group">'
        +'      <select id="dateInterval' + index + '">'
        +'        <option value="instant">Instantaneous Value</option>'
        +'        <option value="cumm">Cummulative Value</option>'
        +'      </select>'
        +'    </div>'
        +'    <div class="col-xs-2 control-group">'
        +'      <select id="interval' + index + '">'
        +'        <option value="5">5 mins</option>'
        +'        <option value="15">15 mins</option>'
        +'        <option value="30">30 mins</option>'
        +'        <option value="60">1 hr</option>'
        +'        <option value="120">2 hr</option>'
        +'        <option value="1400">1 Day</option>'
        +'        <option value="10080">1 Week</option>'
        +'        <option value="43200">1 Month</option>'
        +'      </select>'
        +'    </div> '
        +'  </div>'
        +'</div>'
        );

    // set up the depository options.    
    for(var key in DEPOSITORIES) {
        $("#depositorySelect"+index).append("<option value=\"" + key + "\">" + DEPOSITORIES[key]['name'] + "</option>");
    }
    selectedDepository(index);
    
};

function selectedDepository(index) {
    var depoId = $("#depositorySelect" + index + " option:selected").val();
    var depoName = $("#depositorySelect" + index + " option:selected").text();
    var sensors = DEPO_SENSORS[depoId];
    updateSensorSelection(index, sensors);
    selectedSensor(index);

};

function updateSensorSelection(index, sensors) {
    var select = $("#sensorSelect" + index);
    select.empty();
    var length = sensors.length;
    var i = 0;
    for (i = 0; i < length; i++) {
      select.append($("<option></option>")
         .attr("value",sensors[i])
         .text(SENSORS[sensors[i]].name)); 
    }
    
};

function selectedSensor(index) {
    var depoId = $("#depositorySelect" + index + " option:selected").val();
    var sensorId = $("#sensorSelect" + index + " option:selected").val();
    $("#startInfo" + index).remove();    
    $("#startdatetimepicker" + index).parent().append("<div id=\"startInfo" + index + "\"><small>Earliest: " + DEPO_SENSOR_INFO[depoId][sensorId]['earliest'] + "</small></div>");
    $("#endInfo" + index).remove();    
    $("#enddatetimepicker" + index).parent().append("<div id=\"endInfo" + index + "\"><small>Latest: " + DEPO_SENSOR_INFO[depoId][sensorId]['latest'] + "</small></div>");
};

function myFunction() {
  alert("Something changed.");
};

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
  



var ORGID = "${orgId}";
var DEPOSITORIES = {};
<#list depositories as d>
DEPOSITORIES["${d.id}"] = {"id": "${d.id}", "name": "${d.name}", "measurementType": "${d.measurementType.id}", "organizationId": "${d.organizationId}"};
</#list>
var SENSORS = {};
<#list sensors as s>
SENSORS["${s.id}"] = {"id": "${s.id}", "name": "${s.name}", "uri": "${s.uri}",  "modelId": "<#if s.getModelId()??>${s.modelId}</#if>", "organizationId": "${s.organizationId}", "properties" : [<#assign k = s.properties?size><#list s.properties as p>{"key":"${p.key}", "value":"${p.value}"}<#if k != 1>,</#if><#assign k = k -1></#list>]};
</#list>
var SENSORGROUPS = {};
<#list sensorgroups as sg>
SENSORGROUPS["${sg.id}"] = {"id": "${sg.id}", "name": "${sg.name}", "sensors": [
<#assign sgLen = sg.sensors?size>
<#list sg.sensors as s>
{"id": "${s}"}<#if sgLen != 1>,</#if><#assign sgLen = sgLen - 1>
</#list>
], "organizationId": "${sg.organizationId}"};
</#list>
var DEPO_SENSORS = {};

<#assign keys = depoSensors?keys>
<#list keys as key>
DEPO_SENSORS["${key}"] = [
<#assign len = depoSensors[key]?size>
<#list depoSensors[key] as s> 
"${s.id}"<#if len != 1>, </#if><#assign len = len - 1>
</#list>
]
</#list>
var DEPO_SENSOR_INFO = {};
<#assign depos = depotSensorInfo?keys>
<#list depos as dep>
DEPO_SENSOR_INFO["${dep}"] = {};
<#assign sensorIds = depotSensorInfo[dep]?keys>
<#list sensorIds as id>
DEPO_SENSOR_INFO["${dep}"]["${id}"] = {"earliest" : "${depotSensorInfo[dep][id][0]?datetime?iso_local_ms}", "latest" : "${depotSensorInfo[dep][id][1]?datetime?iso_local_ms}"};

</#list>
</#list>
</script>
</body>
</html>