var wdClient = null;

/**
 * Loads the page either using the permalink or starting fresh.
 */
function loadPage() {
  wdClient = org.WattDepot.Client(server);
}

function handleSummaryData(data, rowNum) {
  console.log("Got " + data + " for row " + rowNum);
//  $("#row" + rowNum + " ")
}

function getDetails(depositoryId, orgId, sensorId, rowNum) {
  console.log("getDetails(" + depositoryId + ", " + orgId + ", " + sensorId + ")");
  wdClient.getDepositorySensorSummary(depositoryId, sensorId, orgId, function(data) {
    handleSummaryData(data, rowNum);
  });
}