<!DOCTYPE html>
<html>
<head>
  <title>WattDepot OpenEIS Algorithm Visualizations</title>
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <!-- Bootstrap -->
  <link rel="stylesheet" href="/webroot/dist/css/bootstrap.min.css">
  <!-- Optional theme -->
  <link rel="stylesheet"
        href="/webroot/dist/css/bootstrap-theme.min.css">
  <link rel="stylesheet" href="/webroot/dist/css/themes/blue/style.css">
  <link rel="stylesheet/less" type="text/css" href="/webroot/dist/css/style.less">
  <link rel="stylesheet" type="text/css" href="/webroot/dist/css/parsley.css">
  <script src="/webroot/dist/js/less-1.3.0.min.js"></script>

  <script type="text/javascript" src="http://www.google.com/jsapi"></script>
  <script type="text/javascript">
    google.load("visualization", "1", {packages: ["corechart"]});
    google.load("prototype", "1.6");
  </script>
  <script type="text/javascript" src="/webroot/dist/js/bioheatmap.js"></script>
  <script src="/webroot/dist/js/wattdepot-openeis.js"></script>
  <script src="/webroot/dist/js/org.wattdepot.client.js"></script>
  <!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
  <script src="/webroot/dist/js/jquery.js"></script>
  <script src="/webroot/dist/js/jquery.tablesorter.js"></script>
  <script src="/webroot/dist/js/bootstrap.min.js"></script>
  <script src="/webroot/dist/js/parsley.js"></script>
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
    <a class="navbar-brand" href="#"><img src="/webroot/dist/wattdepot-logo.png"></a>
  </div>

  <!-- Collect the nav links, forms, and other content for toggling -->
  <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
    <ul class="nav navbar-nav">
      <li><a href="/wattdepot/${orgId}/">Settings</a></li>
      <li><a href="/wattdepot/${orgId}/summary/">Summary</a></li>
      <li><a href="/wattdepot/${orgId}/visualize/">Chart</a></li>
      <li class="active"><a href="/wattdepot/${orgId}/openeis/ui">OpenEIS</a></li>
    </ul>
    <ul class="nav navbar-nav navbar-right">
      <li><a href="#">${orgId}</a></li>
    </ul>
  </div>
  <!-- /.navbar-collapse -->
</nav>

<div class="container">
  <!-- Nav tabs -->
  <ul class="nav nav-tabs" id="tabs">
    <li><a id="tslp_tab_link" href="#tslp" data-toggle="tab" name="tslp">Time Series Load Profile</a></li>
    <li><a id="heat_map_tab_link" href="#heat_map" data-toggle="tab" name="heat_map">Heat Map</a></li>
    <li><a id="energy_signature_tab_link" href="#energy_signature" data-toggle="tab" name="energy_signature">Energy
      Signature</a></li>
    <li><a id="longitude_baseline_link" href="#longitude_baseline" data-toggle="tab" name="longitude_baseline">Longitude Baseline</a></li>
  </ul>

  <!-- Tab panes -->
  <div class="tab-content">
    <div class="tab-pane active" id="tslp">
      <div class="panel-group" id="help">
        <div class="panel panel-default">
          <div class="panel-heading">
            <a class="panel-title text-right accordion-toggle collapsed" data-toggle="collapse" data-parent="#help"
               href="#tslpCollapseHelp">Help </a>
          </div>
          <div id="tslpCollapseHelp" class="panel-collapse collapse">
            <div class="panel-body">
              <p><i>Time series load profiling</i> is used on a daily or weekly basis to understand the relationship
                between energy use and time of day.</p>
            </div>
          </div>
        </div>
      </div>
      <div class="row">
        <div class="col-xs-2"><h3>Time Window</h3></div>
        <div class="col-xs-2"><h3>Depository</h3></div>
        <div class="col-xs-2"><h3>Sensor</h3></div>
        <div class="col-xs-4"></div>
        <div class="col-xs-2"></div>
      </div>
      <div class="row form">
        <div class="col-xs-2"><select id="timeSeriesDuration" class="col-xs-10 sensor-select">
          <option value="1w">1 week</option>
          <option value="2w">2 weeks</option>
          <option value="3w">3 weeks</option>
          <option value="4w">4 weeks</option>
          <option value="1m" selected="true">1 month</option>
          <option value="2m">2 months</option>
          <option value="3m">3 months</option>
          <option value="4m">4 months</option>
          <option value="5m">5 months</option>
          <option value="6m">6 months</option>
          <option value="1y">1 year</option>
        </select></div>
        <div class="col-xs-2"><select id="timeSeriesDepository" class="col-xs-10 sensor-select"
                                      onchange="selectedPowerDepository()">
        <#list power_depositories as d>
          <option value="${d.id}">${d.name}</option>
        </#list>
        <#list energy_depositories as d>
          <option value="${d.id}">${d.name}</option>
        </#list>
        </select></div>
        <div class="col-xs-2"><select id="timeSeriesSensor" class="col-xs-10 sensor-select">
        <#list power_sensors as s>
          <option value="${s.id}">${s.name}</option>
        </#list>
        </select></div>
        <div class="col-xs-4"></div>
        <div class="col-xs-2">
          <button class="btn btn-primary btn-sm add-button" onclick="timeSeriesPlot()">Show Time Series Load Profile
          </button>
        </div>
      </div>
      <div class="row">
        <div id="tslpChart" class="col-xs-12" style="height: 500px;"></div>
      </div>
    </div>
    <div class="tab-pane" id="heat_map">
      <div class="panel-group" id="help">
        <div class="panel panel-default">
          <div class="panel-heading">
            <a class="panel-title text-right accordion-toggle collapsed" data-toggle="collapse" data-parent="#help"
               href="#heatMapCollapseHelp">Help </a>
          </div>
          <div id="heatMapCollapseHelp" class="panel-collapse collapse">
            <div class="panel-body">
              <p><i>Heat maps</i> are a means of visualizing and presenting the information that is contained in a
                time series load profile. The maps color-code the size of the load so that “hot spots” and patterns
                are easily identified. Time of day is plotted on the x- axis, and day or date is indicated on the
                y-axis</p>
            </div>
          </div>
        </div>
      </div>
      <div class="row">
        <div class="col-xs-2"><h3>Time Window</h3></div>
        <div class="col-xs-2"><h3>Depository</h3></div>
        <div class="col-xs-2"><h3>Sensor</h3></div>
        <div class="col-xs-4"></div>
        <div class="col-xs-2"></div>
      </div>
      <div class="row form">
        <div class="col-xs-2"><select id="heatMapDuration" class="col-xs-10 sensor-select">
          <option value="1w">1 week</option>
          <option value="2w">2 weeks</option>
          <option value="3w">3 weeks</option>
          <option value="4w">4 weeks</option>
          <option value="1m" selected="true">1 month</option>
          <option value="2m">2 months</option>
          <option value="3m">3 months</option>
          <option value="4m">4 months</option>
          <option value="5m">5 months</option>
          <option value="6m">6 months</option>
          <option value="1y">1 year</option>
        </select></div>
        <div class="col-xs-2"><select id="heatMapDepository" class="col-xs-10 sensor-select"
                                      onchange="selectedHMPowerDepository()">
        <#list power_depositories as d>
          <option value="${d.id}">${d.name}</option>
        </#list>
        <#list energy_depositories as d>
          <option value="${d.id}">${d.name}</option>
        </#list>
        </select></div>
        <div class="col-xs-2"><select id="heatMapSensor" class="col-xs-10 sensor-select">
        <#list power_sensors as s>
          <option value="${s.id}">${s.name}</option>
        </#list>
        </select></div>
        <div class="col-xs-4"></div>
        <div class="col-xs-2">
          <button class="btn btn-primary btn-sm add-button" onclick="heatMapPlot();">Show Heat Map
          </button>
        </div>
      </div>
      <div class="row">
        <div id="heatChart" class="col-xs-12" style="height: 500px;"></div>
      </div>
    </div>
    <div class="tab-pane" id="energy_signature">
      <div class="panel-group" id="help">
        <div class="panel panel-default">
          <div class="panel-heading">
            <a class="panel-title text-right accordion-toggle collapsed" data-toggle="collapse" data-parent="#help"
               href="#energySignatureCollapseHelp">Help </a>
          </div>
          <div id="energySignatureCollapseHelp" class="panel-collapse collapse">
            <div class="panel-body">
              <p>Energy signatures are used to monitor and maintain the performance of temperature‐dependent
                loads such as whole‐building electric or gas use, or heating and cooling systems or
                components. They can reveal problems with insulation, outside air intake, or system
                efficiency.</p>
            </div>
          </div>
        </div>
      </div>
      <div class="row">
        <div class="col-xs-2"><h3>Time Window</h3></div>
        <div class="col-xs-2"><h3>Power Depository</h3></div>
        <div class="col-xs-2"><h3>Power Sensor</h3></div>
        <div class="col-xs-2"><h3>Temp. Depository</h3></div>
        <div class="col-xs-2"><h3>Temp. Sensor</h3></div>
        <div class="col-xs-2"></div>
      </div>
      <div class="row form">
        <div class="col-xs-2"><select id="energySigDuration" class="col-xs-10 sensor-select">
          <option value="1w">1 week</option>
          <option value="2w">2 weeks</option>
          <option value="3w">3 weeks</option>
          <option value="4w">4 weeks</option>
          <option value="1m" selected="true">1 month</option>
          <option value="2m">2 months</option>
          <option value="3m">3 months</option>
          <option value="4m">4 months</option>
          <option value="5m">5 months</option>
          <option value="6m">6 months</option>
          <option value="1y">1 year</option>
        </select></div>
        <div class="col-xs-2"><select id="sigPowerDepository" class="col-xs-10 sensor-select"
                                      onchange="selectedSigPowerDepository()">
        <#list power_depositories as d>
          <option value="${d.id}">${d.name}</option>
        </#list>
        <#list energy_depositories as d>
          <option value="${d.id}">${d.name}</option>
        </#list>
        </select></div>
        <div class="col-xs-2"><select id="sigPowerSensor" class="col-xs-10 sensor-select">
        <#list power_sensors as s>
          <option value="${s.id}">${s.name}</option>
        </#list>
        </select></div>
        <div class="col-xs-2"><select id="sigTempDepository" class="col-xs-10 sensor-select"
                                      onchange="selectedSigTempDepository()">
        <#list temperature_depositories as d>
          <option value="${d.id}">${d.name}</option>
        </#list>
        </select></div>
        <div class="col-xs-2"><select id="sigTempSensor" class="col-xs-10 sensor-select">
        <#list temperature_sensors as s>
          <option value="${s.id}">${s.name}</option>
        </#list>
        </select></div>
        <div class="col-xs-2"></div>
        <div class="col-xs-2">
          <button class="btn btn-primary btn-sm add-button" onclick="energySigPlot();">View Energy Signature
          </button>
        </div>
      </div>
      <div class="row">
        <div id="energySigAnalyses" class="col-xs-12"></div>
      </div>
      <div class="row">
        <div id="energySigChart" class="col-xs-12" style="height: 500px;"></div>
      </div>
    </div>
    <div class="tab-pane" id="longitude_baseline">
      <div class="panel-group" id="help">
        <div class="panel panel-default">
          <div class="panel-heading">
            <a class="panel-title text-right accordion-toggle collapsed" data-toggle="collapse" data-parent="#help"
               href="#longitudeBaselineCollapseHelp">Help </a>
          </div>
          <div id="longitudeBaselineCollapseHelp" class="panel-collapse collapse">
            <div class="panel-body">
              <p><i>Longitudinal benchmarking</i> compares the energy usage in a fixed period for a building, system,
                or component to that in a comparable “baseline” or “base” period of the same length, to determine if
                performance has deteriorated or improved, to set goals for a building or system, or to monitor for
                unexpectedly high usage.</p>
            </div>
          </div>
        </div>
      </div>
      <div class="row">
        <div class="col-xs-2"><h3>Baseline Window</h3></div>
        <div class="col-xs-2"><h3>Depository</h3></div>
        <div class="col-xs-2"><h3>Sensor</h3></div>
        <div class="col-xs-4"></div>
        <div class="col-xs-2"></div>
      </div>
      <div class="row form">
        <div class="col-xs-2"><select id="longitudeDuration" class="col-xs-10 sensor-select">
          <option value="1w">1 week</option>
          <option value="2w">2 weeks</option>
          <option value="3w">3 weeks</option>
          <option value="4w">4 weeks</option>
          <option value="1m" selected="true">1 month</option>
          <option value="2m">2 months</option>
          <option value="3m">3 months</option>
          <option value="4m">4 months</option>
          <option value="5m">5 months</option>
          <option value="6m">6 months</option>
          <option value="1y">1 year</option>
        </select></div>
        <div class="col-xs-2"><select id="longitudeDepository" class="col-xs-10 sensor-select"
                                      onchange="selectedLongitudeDepository()">
        <#list power_depositories as d>
          <option value="${d.id}">${d.name}</option>
        </#list>
        <#list energy_depositories as d>
          <option value="${d.id}">${d.name}</option>
        </#list>
        </select></div>
        <div class="col-xs-2"><select id="longitudeSensor" class="col-xs-10 sensor-select">
        <#list power_sensors as s>
          <option value="${s.id}">${s.name}</option>
        </#list>
        </select></div>
        <div class="col-xs-4"></div>
        <div class="col-xs-2">
          <button class="btn btn-primary btn-sm add-button" onclick="longitudePlot();">View Longitude Baseline
          </button>
        </div>
      </div>
      <div class="row">
        <div id="longitudeChart" class="col-xs-12" style="height: 500px;"></div>
      </div>
    </div>
  </div>

  <!-- Visualizer Control Panel -->
  <!--
  <div id="visualizerControlPanel">
    <div id="formLabels" class="row">
      <div class="col-xs-3">Depository:</div>
      <div class="col-xs-2">Sensor:</div>
      <div class="col-xs-3">Start Time:</div>
      <div class="col-xs-2">End Time:</div>
      <div class="col-xs-2">Sample Frequency:</div>
    </div>
    <div id="visualizerFormsDiv">
    </div>
    <div id="controlPanelButtonRow" class="row">
      <div class="col-xs-8 control-group">
        <button id="visualizeButton" class="col-xs-2 btn btn-primary" onclick="visualize();" diabled>Visualize!</button>
        <div class="col-xs-10 control-group" id="permalink" style="display:none">Share URL:<input id="linkSpace" class="input-large" type="text" placeholder="Your Permalink" style="width:80%"></div>
      </div>
      <div class="col-xs-3"></div>
      <div class="col-xs-1 control-group">
        <button id="addRowButton" class="btn btn-primary" onclick="addRow();" data-toggle="tooltip" data-placement="left" title="Add another row"><span class="glyphicon glyphicon-plus"></span></button>
      </div>
    </div>
  </div>
  -->
  <div id='chartDiv' style='height: 500px;'></div>
</div>
<!--
<div class="modal fade" id="progressWindow" tabindex="-1"
     role="dialog">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <h3>Visualizing...</h3>
      </div>
      <div class="modal-body">
        <div id="visualizeProgress" class="progress">
          <div id="visualizeProgressBar" class="bar" style="width: 0%;"></div>
        </div>
        <p id="progressLabel"></p>

        <p id="timeLabel"></p>
      </div>
      <div class="modal-footer">
        <button class="btn btn-primary" type="button" id="cancelQuery" data-dismiss="modal">Cancel</button>
        <script>
          $('#cancelQuery').click(function () {
            stopQueries();
            $('#visualizeButton').attr("disabled", false);
            $('#visualizeButton').empty();
            $('#visualizeButton').append("Visualize!");
          })
        </script>
      </div>
    </div>
  </div>
</div>
</div>
-->
<script>
  // A $( document ).ready() block.
  $(document).ready(function () {
    console.log("ready!");
  });
  var server = window.location.protocol + "//" + window.location.host + "/wattdepot/";

  var ORGID = "${orgId}";
  var DEPOSITORIES = {};
  <#list depositories as d>
  DEPOSITORIES["${d.id}"] = {
    "id": "${d.id}",
    "name": "${d.name}",
    "measurementType": "${d.measurementType.id}",
    "typeString": "${d.measurementType.units}",
    "organizationId": "${d.organizationId}"
  };
  </#list>
  var POWER_DEPOSITORIES = {};
  <#list power_depositories as d>
  POWER_DEPOSITORIES["${d.id}"] = {
    "id": "${d.id}",
    "name": "${d.name}",
    "measurementType": "${d.measurementType.id}",
    "typeString": "${d.measurementType.units}",
    "organizationId": "${d.organizationId}"
  };
  </#list>
  var ENERGY_DEPOSITORIES = {};
  <#list energy_depositories as d>
  ENERGY_DEPOSITORIES["${d.id}"] = {
    "id": "${d.id}",
    "name": "${d.name}",
    "measurementType": "${d.measurementType.id}",
    "typeString": "${d.measurementType.units}",
    "organizationId": "${d.organizationId}"
  };
  </#list>
  var SENSORS = {};
  <#list sensors as s>
  SENSORS["${s.id}"] = {
    "id": "${s.id}",
    "name": "${s.name}",
    "uri": "${s.uri}",
    "modelId": "<#if s.getModelId()??>${s.modelId}</#if>",
    "organizationId": "${s.organizationId}",
    "properties": [<#assign k = s.properties?size><#list s.properties as p>{
      "key": "${p.key}",
      "value": "${p.value}"
    }<#if k != 1>,</#if><#assign k = k -1></#list>]
  };
  </#list>
  var POWER_SENSORS = {};
  <#list power_sensors as s>
  POWER_SENSORS["${s.id}"] = {
    "id": "${s.id}",
    "name": "${s.name}",
    "uri": "${s.uri}",
    "modelId": "<#if s.getModelId()??>${s.modelId}</#if>",
    "organizationId": "${s.organizationId}",
    "properties": [<#assign k = s.properties?size><#list s.properties as p>{
      "key": "${p.key}",
      "value": "${p.value}"
    }<#if k != 1>,</#if><#assign k = k -1></#list>]
  };
  </#list>
  var ENERGY_SENSORS = {};
  <#list energy_sensors as s>
  ENERGY_SENSORS["${s.id}"] = {
    "id": "${s.id}",
    "name": "${s.name}",
    "uri": "${s.uri}",
    "modelId": "<#if s.getModelId()??>${s.modelId}</#if>",
    "organizationId": "${s.organizationId}",
    "properties": [<#assign k = s.properties?size><#list s.properties as p>{
      "key": "${p.key}",
      "value": "${p.value}"
    }<#if k != 1>,</#if><#assign k = k -1></#list>]
  };
  </#list>
  var TEMPERATURE_SENSORS = {};
  <#list temperature_sensors as s>
  TEMPERATURE_SENSORS["${s.id}"] = {
    "id": "${s.id}",
    "name": "${s.name}",
    "uri": "${s.uri}",
    "modelId": "<#if s.getModelId()??>${s.modelId}</#if>",
    "organizationId": "${s.organizationId}",
    "properties": [<#assign k = s.properties?size><#list s.properties as p>{
      "key": "${p.key}",
      "value": "${p.value}"
    }<#if k != 1>,</#if><#assign k = k -1></#list>]
  };
  </#list>

  <#--var SENSORGROUPS = {};-->
  <#--<#list sensorgroups as sg>-->
  <#--SENSORGROUPS["${sg.id}"] = {-->
  <#--"id": "${sg.id}", "name": "${sg.name}", "sensors": [-->
  <#--<#assign sgLen = sg.sensors?size>-->
  <#--<#list sg.sensors as s>-->
  <#--{"id": "${s}"}<#if sgLen != 1>,</#if><#assign sgLen = sgLen - 1>-->
  <#--</#list>-->
  <#--], "organizationId": "${sg.organizationId}"-->
  <#--};-->
  <#--</#list>-->

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
  <#--<#assign depos = depotSensorInfo?keys>-->
  <#--<#list depos as dep>-->
  <#--DEPO_SENSOR_INFO["${dep}"] = {};-->
  <#--<#assign sensorIds = depotSensorInfo[dep]?keys>-->
  <#--<#list sensorIds as id>-->
  <#--DEPO_SENSOR_INFO["${dep}"]["${id}"] = {-->
  <#--"earliest": "${depotSensorInfo[dep][id][0]?datetime?iso_local_ms}",-->
  <#--"latest": "${depotSensorInfo[dep][id][1]?datetime?iso_local_ms}"-->
  <#--};-->

  <#--</#list>-->
  <#--</#list>-->
</script>
</body>
</html>