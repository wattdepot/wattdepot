var wdClient = null;

/**
 * Loads the page either using the permalink or starting fresh.
 */
function loadPage() {
  wdClient = org.WattDepot.Client(server);
}

function handleSummaryData(data, rowNum) {
  var obj = JSON.parse(data);
  var callback = function(){
    // do something after the updateAll method has completed
  };
  var cell = $("#asOf" + rowNum);
  $("#asOf" + rowNum).text(obj.timestamp);
//  $("#summaryTable").trigger("updateCell", [cell, "", callback ]);
  $("#latestValue" + rowNum).text(obj.latestValue);
  $("#lastMinNum" + rowNum).text(obj.oneMinuteCount);
  $("#lastMinRate" + rowNum).text(obj.oneMinuteRate);
  $("#totalCount" + rowNum).text(obj.totalCount);
  $("#details" + rowNum).empty();
  $("#details" + rowNum).text("Update Details");
  $("#summaryTable").trigger("update", [true]);
}

function getDetails(depositoryId, orgId, sensorId, rowNum) {
  console.log("getDetails(" + depositoryId + ", " + orgId + ", " + sensorId + ")");
  wdClient.getDepositorySensorSummary(depositoryId, sensorId, orgId, function(data) {
    handleSummaryData(data, rowNum);
  });
}