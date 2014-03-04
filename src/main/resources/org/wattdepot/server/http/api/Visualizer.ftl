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
<script type='text/javascript' src='http://www.google.com/jsapi'></script>

<script> 
   // Load the Visualization API and Annotated Timeline visualization
   google.load('visualization', '1', {
     'packages':['annotatedtimeline']
   });
           
   // Set a callback to run when the API is loaded.
   google.setOnLoadCallback(function() {
     loaded = true;
   });

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
        <div class="col-xs-3">Depository:</div>
        <div class="col-xs-2">Sensor:</div>
        <div class="col-xs-3">Start Time:</div>
        <div class="col-xs-2">End Time:</div>
        <div class="col-xs-2">Sample Interval:</div>
      </div>
      <div id="visualizerFormsDiv">
        <div class="row form" id="form1">
            <div id="depositoryDiv1" class="col-xs-3 control-group">
              <div class="col-xs-2">
                <label class="checkbox"><input type="checkbox" id="show1" value="now"></label>
              </div>
              <div class="col-xs-9">
                <select id="depositorySelect1" class="col-xs-12" data-placehoder="Choose Depository..." onchange="selectedDepository(1)" data-toggle="tooltip" title="Choose Depository...">
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
                <div><label class="checkbox"><input type="checkbox" id="endTimeNow1" value="now">Now</label></div>
            </div>
            <div class="col-xs-2 control-group">
              <select id="dateInterval1">
                <option value="instant">Instantaneous Value</option>
                <option value="cumm">Cummulative Value</option>
              </select>
            </div>
            <div class="col-xs-1 control-group">
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
    <div id='chartDiv' style='height: 400px; z-index: -1;'></div>    
  </div>
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
        </div><!-- /.modal-content -->
    </div><!-- /.modal-dialog -->
  </div>
<script>

var numRows = 1;
var activeIndex = []; // The array containing the form indexes of the active form rows
var numShow = 0;
var maxQuerySize = 100; // The maximum size of a single query to the WattDepot server.  Any larger query will be split.
var maxQuerySize = 100; // The maximum size of a single query to the WattDepot server.  Any larger query will be split.
var totalNumQueries = 0; // The total number of queries (not subqueries).
var totalNumQueriesReturned = 0; // The total number of queries that have returned from the server.
var queryTimeOut = 300; // The number of seconds the application should wait before its queries time out.
var numDataTypeReturned = 0; // The number of data type queries that have returned.
var server = window.location.protocol + "//" + window.location.host + "/";
var wdClient = org.WattDepot.Client(server);

$(document).ready(function() {
    selectedDepository(1);
});

$(function () {
    activeIndex.push({
        formIndex: 1,
        disabled: false,
        sourceSelected: false
    });
    $("#show1").prop('checked', true); 
    // Bind event for when Show checkbox is checked
    $("#show1").click(function() {
        var form = activeIndex[findActiveIndex(1)];    
        if($("#show1").is(":checked")) {
            if($("#visualizeButton").prop("disabled")) {
                $('#visualizeButton').prop("disabled", false);
            }
            form.disabled = false; 
            numShow++;
            $("#form1").css('background-color', '#fff');
        }
        else {
            form.disabled = true;
            numShow--; 
            $("#form1").css('background-color', '#ddd');
        }
    });

    // Bind event for when the Now checkbox is checked
    $("#endTimeNow1").click(function() {
        if($("#endTimeNow1").is(':checked')) {
            $("#enddatetimepicker1").data("DateTimePicker").disable();
        }
        else {
            $("#enddatetimepicker1").data("DateTimePicker").enable();     
        }
    });

    $('#startdatetimepicker1').datetimepicker();
    $('#enddatetimepicker1').datetimepicker();
});

/**
* Stops the executing queries by stopping the browser.
*
* Copied from wattdepot-apps by Edward Meyer, Kendyll Doi, Bao Huy Ung
*/
function stopQueries() {
    /**particularly checks for Internet Explorer since it uses a different command to stop the page from loading. */
    if(navigator.appName == "Microsoft Internet Explorer") {  
        window.document.execCommand('Stop');
    }
    /**stops the page to other browsers specifications. */
    else {
        window.stop();
    }
    canceled = true;
}

/**
 * Finds the index of the object in activeIndex that
 * corresponds to the given formIndex.
 * 
 * @param formIndex The index of the form to find.
 * 
 * @return The index of the form in activeIndex, or -1 if it is not found.
 */
function findActiveIndex(formIndex) {
    for(var i = 0; i < activeIndex.length; i++) {
        if(activeIndex[i].formIndex == formIndex) {
            return i;
        }
    }
    return -1;
}

function addRow() {
    numRows++;
    if (findActiveIndex(numRows) == -1) {
        activeIndex.push({
            formIndex: numRows,
            disabled: false,
            depositorySelected: false
        });
    }
    insertRowHTML(numRows);
};

function insertRowHTML(index) {
    $('#visualizerFormsDiv').append(
        '<div class="row form" id="form'+index+'">'
        +'    <div id="depositoryDiv' + index + '" class="col-xs-3 control-group">'
        +'      <div class="col-xs-2">'
        +'      <label class="checkbox"><input type="checkbox" id="show' + index + '" value="show"></label>'
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
        +'        <div><label class="checkbox"><input type="checkbox" id="endTimeNow' + index + '" value="now">Now</label></div>'
        +'    </div>'
        +'    <div class="col-xs-2 control-group">'
        +'      <select id="dateInterval' + index + '">'
        +'        <option value="instant">Instantaneous Value</option>'
        +'        <option value="cumm">Cummulative Value</option>'
        +'      </select>'
        +'    </div>'
        +'    <div class="col-xs-1 control-group">'
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
        +'</div>'
        );
        
    $("#show" + index).prop('checked', true); 
    // Bind event for when Show checkbox is checked
    $("#show" + index).click(function() {
        var form = activeIndex[findActiveIndex(index)];    
        if($("#show" + index).is(":checked")) {
            if($("#visualizeButton").prop("disabled")) {
                $('#visualizeButton').prop("disabled", false);
            }
            form.disabled = false; 
            numShow++;
            $("#form" + index).css('background-color', '#fff');
        }
        else {
            form.disabled = true;
            numShow--; 
            $("#form" + index).css('background-color', '#ddd');
        }
    });

    // Bind event for when the Now checkbox is checked
    $("#endTimeNow" + index).click(function() {
        if($("#endTimeNow" + index).is(':checked')) {
            $("#enddatetimepicker" + index).data("DateTimePicker").disable();
        }
        else {
            $("#enddatetimepicker" + index).data("DateTimePicker").enable();
        }
    });

    $('#startdatetimepicker' + index).datetimepicker();
    $('#enddatetimepicker' + index).datetimepicker();

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

/**
 * Creates a cookie with given 'name' Which will contain 'data' and expires an amount of 'time' from now.
 * Time is done in terms of days.
 * Note: this also works for overwriting a cookie with new information.
 *
 */
function makeCookie(name, data, time){
    if(time){
        var date = new Date();
        date.setTime(date.getTime() + (time * 1000 * 60 * 60 * 24)); //sets date to time in days from now.
        var expire = "; expires=" + date.toGMTString();
    }
    else{
        var expire = "";
    }
    
    document.cookie = name + "=" + data + expire + "; path=/";
}

/**
 * This returns the data portion of a cookie given a name.
 * If name doesn't match, returns null.
 *
 */
function getCookie(name){
    var cName = name + "=";
    var info = document.cookie.split(';');
    for( var i = 0; i < info.length; i++){
        var c = info[i];
        while(c.charAt(0) == ' '){
            c = c.substring(1,c.length);
        }
        if(c.indexOf(cName) == 0){
            return c.substring(cName.length, c.length);
        }
        
    }
    
    return null;
}

/**
 * This function deletes a cookie of the given name by setting expiration time to negative(removes on next update)
 * Note: this makes it possible to remove a cookie that doesn't exist prior to function call
 *
 */
function deleteCookie(name){
    makeCookie(name, "", -1);
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
};
  

/**
 * Gets the Date for the specified time field.
 *
 * @param id The id prefix of the time field to get (i.e. 'start'
 *           or 'end').
 * @param index The index of the time field form to get.
 *
 * @return A Date representing the fields of the given form.
 */
function getDate(id, index) {
    var time = $('#'+id+'datetimepicker'+index).data("DateTimePicker").getDate();
    var error = false;
    if (time == null) {
        error = true;
        $('#'+id+'datetimepicker'+index).addClass('invalid');
        $('#'+id+'datetimepicker'+index).css('border', '3px red solid');
        $('#'+id+'datetimepicker'+index).wrap('<a href="#" rel="tooltip" data-placement="top" title="Error: Time must be selected.">');    
    }
    if (error) {
        return null;
    }    
    if($('#'+id+'datetimepicker'+index).hasClass('invalid')) {
        $('#'+id+'datetimepicker'+index).css('border', '');
        $('#'+id+'datetimepicker'+index).unwrap();
        $('#'+id+'datetimepicker'+index).removeClass('invalid');
    }
    console.log(time.toDate());
    return time.toDate();
}

/**
 * Splits the query for a source if it is too large.
 *
 * @param server The WattDepot server to send the query to.
 *
 * @param depository The depository the query is getting data for.
 * 
 * @param sensor The sensor the query is retrieving.
 * 
 * @param startTime The starting time of the query (should be a Javascript Date object).
 * 
 * @param endTime The ending time of the query (should be a Javascript Date object).
 * 
 * @param interval The interval at which data should be sampled in minutes.
 * 
 * @return An array containing the queries that resulted from splitting the original query.
 */
function splitQuery(server, depository, sensor, startTime, endTime, interval) {
    var returnArray = [];
    var tempTime = new Date(startTime.getTime());
    var query;
    tempTime.setMinutes(tempTime.getMinutes() + maxQuerySize * interval, tempTime.getSeconds(), tempTime.getMilliseconds());

    // Split the query
    while(tempTime < endTime) {
        query = new google.visualization.Query(server + ORGID + '/depository/'+ depository +'/values/gviz/?sensor='+ sensor + '&startTime='+wdClient.getTimestampFromDate(startTime)+'&endTime='+wdClient.getTimestampFromDate(tempTime)+'&samplingInterval='+interval);
        query.setQuery('select timePoint, ' + depository);
        query.setTimeout(queryTimeOut);
        returnArray.push(query);
        startTime = new Date(tempTime.getTime());
        tempTime.setMinutes(tempTime.getMinutes() + maxQuerySize * interval, tempTime.getSeconds(), tempTime.getMilliseconds());
    }
    
    // Make query for the remaining interval
    query = new google.visualization.Query(server + ORGID + '/depository/'+ depository +'/values/gviz/?sensor='+ sensor + '&startTime='+wdClient.getTimestampFromDate(startTime)+'&endTime='+wdClient.getTimestampFromDate(endTime)+'&samplingInterval='+interval);
    query.setQuery('select timePoint, ' + depository);
    query.setTimeout(queryTimeOut);
    returnArray.push(query);
    return returnArray;
}   

/**
 * Gets the number of data points requested by the form with the given
 * index.
 *
 * @param index The index of the form to calculate the number of data points of.
 *
 * @return The number of data points requested by the specified form.
 */
function getNumDataPoints(index) {
    var timeInterval;
    var interval;
    var start, end;
    start = getDate("start", index);
    end = getDate("end", index);
    if(start > end) {
        timeInterval = start.getTime() - end.getTime();
    }
    else {
        timeInterval = end.getTime() - start.getTime();
    }
       
    interval =  parseInt($('#interval'+index+' option:selected').val()) * 60 * 1000;
    return timeInterval / interval;

}    


/**
 * Gets the total number of data points needed by all forms in the
 * visualization request.
 *
 * @return The number of data points needed for the whole visualization request.
 */
function getTotalDataPoints() {
    var totalPoints = 0;
    var i;
    for(i = 0; i < activeIndex.length; i++) {
        if(!activeIndex[i].disabled){
            totalPoints += getNumDataPoints(activeIndex[i].formIndex);
        }
    }
    return totalPoints;
}    
 

function visualize() {

    // Disable visualize button
    $('#visualizeButton').prop("disabled", true);
    $('#visualizeButton').empty();
    $('#visualizeButton').append("Loading...");

    $('#chartDiv').hide();

    var dataQueries = [];      
    var totalNumQueries = 0;
    var formIndex;
    var depository;
    var sensor;
    var interval;
    var startTime;
    var endTime;
    var error;
    
    // Set up the queries
    for (var loopIndex = 0; loopIndex < activeIndex.length; loopIndex++) {
        if(activeIndex[loopIndex].disabled) {
            continue;
        }
        error = false;
        totalNumQueries++;
        formIndex = activeIndex[loopIndex].formIndex;
        depository = $("#depositorySelect" + formIndex + " option:selected").val();
        sensor = $("#sensorSelect" + formIndex + " option:selected").val();
        interval = $('#interval'+formIndex+' option:selected').val();
        
        startTime = getDate('start', formIndex);
        if($('#endTimeNow'+formIndex).is(':checked')) {
            endTime = new Date();
        }
        else {
            endTime = getDate('end', formIndex);
        }        
        if(startTime == null || endTime == null) {
            error = true;
        }
        else {
            // If start is bigger than end, flip the two
            if(startTime > endTime) {
                var temp = endTime;
                endTime = startTime;
                startTime = temp;
            }
        }     
        // Make sure interval is not too big
        if((endTime - startTime) / 60000 < interval) {
            error = true;
            $('#interval'+formIndex).addClass('invalid');
            $('#interval'+formIndex).css('border', '3px red solid');
            $('#interval'+formIndex).wrap('<a href="#" rel="tooltip" data-placement="bottom" title="Error: Interval is larger than the data range.  Please chooose a smaller interval." />');
        }
        else if($('#interval'+formIndex).hasClass('invalid')) {
            $('#interval'+formIndex).unwrap();
            $('#interval'+formIndex).css('border', '');
            $('#interval'+formIndex).removeClass('invalid');
        }
        if(!error) {
            dataQueries.push({
                'depository': 'depository', 
                'numReturned':0,
                'queries': splitQuery(server, depository, sensor, startTime, endTime, interval), 
                'queryResults':null
            });
        }
    
    }

    if(error) {
        $("[rel=tooltip]").tooltip();
        $("[rel=tooltip]").mouseout(  
            function(){
                $(this).tooltip("hide");
            });
        alert("Error: Inputs are invalid.\nPlease check the fields in red.\nYou may hover overthem to get a tooltip describing the error.");
        $('#visualizeButton').prop("disabled", false);
        $('#visualizeButton').empty();
        $('#visualizeButton').append("Visualize!");
        $('#progressWindow').modal('hide');
        return;
    }    

    // Initialize rest of the globals
    numFinished = 0;
    dataArray = [];
    canceled = false;
    totalNumPoints = getTotalDataPoints();
    numDataPointsRetrieved = 0;
    totalNumQueriesReturned = 0;    
        
    // Set up progress modal
    $('#visualizeProgressBar').width("0%");
    $('#progressLabel').empty();
    $('#progressLabel').append("Getting data..."+ numDataPointsRetrieved + " / " + Math.round(totalNumPoints) + " completed.");
      
    // Set up timing variables.
    var timePerPoint = getCookie('timePerPoint');
    if(timePerPoint == null) {
        $('#timeLabel').empty();
        $('#timeLabel').append("Estimated Time Remaining: Unknown...");
        averageTimePerPoint = 0;
    }
    else {
        averageTimePerPoint = parseFloat(timePerPoint);
        $('#timeLabel').empty();
        $('#timeLabel').append("Estimated Time Remaining: " + createTimeString(Math.round(averageTimePerPoint * totalNumPoints)) + ".");
    }
    
    // Show progress modal
    $('#progressWindow').modal({
        backdrop: 'static',
        keyboard: false
    });
        
    // Dummy function to create a closure in order
    // to preserve the proper index.
    var sendQuery = function(query, index, startQueryTime, queryID) {
        query.send(function(response){
            visualizerResponse(response, index, startQueryTime, queryID);
        });
    }    
    
    
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