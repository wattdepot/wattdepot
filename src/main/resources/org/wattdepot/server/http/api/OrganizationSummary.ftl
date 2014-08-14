<!DOCTYPE html>
<html>
<head>
<title>WattDepot Organization Measurement Summary</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<!-- Bootstrap -->
<link rel="stylesheet" href="/webroot/dist/css/bootstrap.min.css">
<!-- Optional theme -->
<link rel="stylesheet" href="/webroot/dist/css/bootstrap-theme.min.css">
<link rel="stylesheet" href="/webroot/dist/css/themes/blue/style.css">
<link rel="stylesheet/less" type="text/css" href="/webroot/dist/css/style.less">
<script src="/webroot/dist/js/less-1.3.0.min.js"></script>
<!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
<script src="/webroot/dist/js/jquery.js"></script>
<script src="/webroot/dist/js/jquery.tablesorter.js"></script>
<script src="/webroot/dist/js/bootstrap.min.js"></script>
<script src="/webroot/dist/js/wattdepot-summary.js"></script>
<script src="/webroot/dist/js/org.wattdepot.client.js"></script>
<script> 
</script> 

</head>
<body onload="loadPage()">
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
      <li class="active"><a href="/wattdepot/${orgId}/summary/">Summary</a></li>
      <li><a href="/wattdepot/${orgId}/visualize/">Chart</a></li>
    </ul>
    <ul class="nav navbar-nav navbar-right">
      <li><a href="#">${orgId}</a></li>
    </ul>
  </div><!-- /.navbar-collapse -->
</nav>
 
  <div class="container">
    <div class="row">
      <div class="col-xs-3"><strong>${name} Summary:</strong></div>
      <div class="col-xs-9"><strong>${depositories?size} Depositories,   ${sensors?size} Sensors reporting,  ${totalMeasurements} Measurements</strong></div>
    </div>
    <p></p>
    <hr/>
    <p></p>
    <div class="row">
      <div class="col-xs-3"><strong>Measurement Rate Summary</strong></div>
    </div>
    <table id="summaryTable" class="table tablesorter">
      <thead>
        <tr>
          <th>Depository Id</th>
          <th>Sensor Id</th>
          <th>As of</th>
          <th>Latest Value</th>
          <th colspan="2">Last Minute</th>
          <th>Total Count</th>
          <th></th>
        </tr>
      </thead>
      <tbody>
      <#assign keys = depositorySensors?keys>
      <#assign row = 1>
      <#list keys as key>
        <#list depositorySensors[key] as s> 
            <tr id="row${row}"><td>${key}</td><td>${s}</td><td id="asOf${row}"></td><td id="latestValue${row}"></td><td id="lastMinNum${row}"></td><td id="lastMinRate${row}"></td><td id="totalCount${row}"></td><td><button id="details${row}" class="btn btn-primary btn-sm" onclick="getDetails('${key}', '${orgId}', '${s}', '${row}');">Show Details</button> </td></tr>
            <#assign row = row + 1>
        </#list>
      </#list>
      
      </tbody>
    </table>
  </div>
<script>
var server = window.location.protocol + "//" + window.location.host + "/wattdepot/";

$(document).ready(function() 
    { 
        $("#summaryTable").tablesorter(); 
    } 
); 
</script>
</body>
</html>