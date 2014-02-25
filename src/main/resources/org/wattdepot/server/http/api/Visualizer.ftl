<!DOCTYPE html>
<html>
<head>
<title>WattDepot Organization Measurement Visualization</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<!-- Bootstrap -->
<link rel="stylesheet" href="/webroot/dist/css/bootstrap.min.css">
<!-- Optional theme -->
<link rel="stylesheet" href="/webroot/dist/css/bootstrap-theme.min.css">
<link rel="stylesheet/less" type="text/css" href="/webroot/dist/css/style.less">
<script src="/webroot/dist/js/less-1.3.0.min.js"></script>
<!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
<script src="/webroot/dist/js/jquery.js"></script>
<script src="/webroot/dist/js/bootstrap.min.js"></script>
<script src="/webroot/dist/js/wattdepot-visualizer.js"></script>
<script src="/webroot/dist/js/wattdepot-client.js"></script>
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
        <div class="col-xs-7">
          <div class="col-xs-5">Start Time:</div>
          <div class="col-xs-5">End Time:</div>
          <div class="col-xs-2">Interval:</div>
        </div>
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
                <select id="depositorySelect1" class="col-xs-12 chzn-select" data-placehoder="Choose Depository...">
                  <#list depositories as d>
                  <option value="${d.id}">${d.name}</option>
                  </#list>
                </select>
              </div>
            </div>
            <div id="sensorDiv1" class="col-xs-2 control-group">
              <select id="sensorSelect1" class="col-xs-12">
                <#list sensors as s>
                <option value="${s.id}">${s.name}</option>
                </#list>
              </select>
            </div>
            <div id="timeDiv1" class="col-xs-7">
              <div class="col-xs-5 control-group">
                <div class="input-append bootstrap-timepicker-component col-xs-5">
                  <input type="text" id="startTimePicker1" class="timepicker-default input-small col-xs-12">
                </div>
                <span id="startTimePickerSpan1" class="glyphicon glyphicon-time col-xs-2"></span>
                <div class="col-xs-5">
                  <input type="text" value="current date" id="startDatePicker1" class="col-xs-12">
                </div>
              </div>
              <div class="col-xs-5 control-group">
                <div class="input-append bootstrap-timepicker-component col-xs-5">
                  <input type="text" id="endTimePicker1" class="timepicker-default input-small col-xs-12">
                </div>
                <span id="endTimePickerSpan1" class="glyphicon glyphicon-time col-xs-2"></span>
                <div class="col-xs-5">
                  <input type="text" value="current date" id="endDatePicker1" class="col-xs-12">
                </div>
              </div>
              <div class="col-xs-2 control-group">
                <div class="col-xs-10">
                  <select id="interval1" onchange="myFunction()">
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
                <div class="col-xs-2" id="removeDiv1">
                  <a href="#" rel="tooltip" data-placement="left" title="Remove the data.">
                    <button id="remove1" class="btn btn-xs"> - </button>
                  </a>
                </div>
              </div>
            </div> 
          </div>
        </div>
      </div>
      <div id="controlPanelButtonRow" class="row">
        <div class="col-xs-8 control-group">
          <button id="visualizeButton" class="col-xs-2 btn btn-primary" onclick="visualize();" diabled>Visualize!</button>
      </div>
    </div>
  </div>
<script>

function myFunction() {
  alert("Something changed.");
};

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
DEPO_SENSOR["${key}"] = [
<#assign len = depoSensors[key]?size>
<#list depoSensors[key] as s> 
${s.id}<#if len != 1>, </#if><#assign len = len - 1>
</#list>
]
</#list>

</script>
</body>
</html>