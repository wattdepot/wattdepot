<!DOCTYPE html>
<html>
<head>
<title>WattDepot Organization Measurement Summary</title>
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
<script src="/webroot/dist/js/wattdepot-user-admin.js"></script>
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
      <li class="active"><a href="/wattdepot/${orgId}/summary/">Measurement, Depository Summary</a></li>
    </ul>
    <ul class="nav navbar-nav navbar-right">
      <li><a href="#">${orgId}</a></li>
    </ul>
  </div><!-- /.navbar-collapse -->
</nav>
 
  <div class="container">
    <table class="table">
      <thead>
        <tr><th colspan="3">Measurement Rate Summary</th></tr>
        <tr>
          <th>Depository</th>
          <th>Sensor Id</th>
          <th>As of</th>
          <th>Latest Value</th>
          <th colspan="2">Last Minute</th>
          <th>Total Count</th>
        </tr>
      </thead>
      <tbody>
      <#list summaries as s>
        <tr>
          <td>${s.depositoryId}</td>
          <td>${s.sensorId}</td>
          <td>${s.timestamp?datetime}</td>
          <td>${s.latestValue} ${s.type.getUnits()}</td>
          <td>${s.oneMinuteCount} meas</td>
          <td>${s.oneMinuteRate} meas/sec</td>
          <td>${s.totalCount} meas</td>
        </tr>
      </#list>
      </tbody>
    </table>
  </div>
<script>
</script>
</body>
</html>