/**
Tests were pulled form WattDepotClientTest.js

I believe everything was reverted after finding qUnit which seems to work with what was already made.
Not entirely sure though.


*/



var testServerUrl = "http://server.wattdepot.org:8182/wattdepot/";
var testSourceName = "SIM_UH_KELLER";
var testTimestampStart = "2010-04-13T13:26:46.218";
var testTimestampEnd = "2010-04-13T13:27:46.218";
var testSourceLink = "http://server.wattdepot.org:8182/wattdepot/sources/SIM_UH_KELLER";
var testSamplingInterval = 1;
var tPTF = false;//ADDTN:10-05-2012

module("org.wattdepot.client.Creation");

test("Test WattDepot Javascript Client Creation", function() {
  expect(1);
  var wdclient = new org.WattDepot.Client("");
  tPTF = equal(typeof(wdclient), "object", "org.WattDepot.Client object should be defined in the scope.");
  
  
  
});

module("org.wattdepot.client.UnitlityFunction");

test("Test getJsonNode function", function() {
  expect(10);
  var wdclient = new org.WattDepot.Client("");
  equal(typeof(wdclient.getJsonNode),  "function", "getJsonNode should be a function");
  var testJson = {
	a : 'a',
	b : {
	  c : ['c','d']
	}
  };
  equal(typeof(wdclient.getJsonNode()), "undefined", "no argument should return undefined");
  equal(typeof(wdclient.getJsonNode(testJson)), "undefined", "one argument should return undefined");
  equal(typeof(wdclient.getJsonNode("b", testJson, "a")), "undefined",
	"overload will take the first 2 as the arguments. getJsonNode('b', testJson, 'a') should return undefined");
  equal(wdclient.getJsonNode(testJson, "a", "b"), "a",
	"overload will take the first 2 as the arguments. getJsonNode(testJson, 'a', 'b') should be defined");
  equal(wdclient.getJsonNode(testJson, "a"), "a", "Node a of testJson object should equal 'a'"); 
  equal(typeof(wdclient.getJsonNode(testJson, "b")), "object", "Node b of testJson object should be an object");
  equal(wdclient.getJsonNode(testJson, "c") instanceof Array, true, "Node c of testJson object should be an array");
  equal(typeof(wdclient.getJsonNode(wdclient.getJsonNode(testJson, "b"), "c")), "object", "Node b should contain node c");
  equal(typeof(wdclient.getJsonNode(wdclient.getJsonNode(testJson, "b"), "a")), "undefined", "Node b should not contain node a");
});

test("Test getUnit function", function() {
  expect(4);
  var wdclient = new org.WattDepot.Client("");
  equal(typeof(wdclient.getUnit),  "function", "getUnit should be a function");
  equal(wdclient.getUnit("Power Consumed"), "Watt", "Unit is Watt for 'power consumed'");
  equal(wdclient.getUnit("Energy Consumed"), "Watt Hour", "Unit is Watt Hour for 'energy consumed'");
  equal(typeof(wdclient.getUnit("foo")), "undefined", "Unit is undefined for 'foo'");
});

test("Test getSourceNameFromLink function", function() {
  expect(2);
  var wdclient = new org.WattDepot.Client("");
  equal(typeof(wdclient.getSourceNameFromLink),  "function", "getSourceNameFromLink should be a function");
  equal(wdclient.getSourceNameFromLink(testSourceLink), testSourceName, "SIM_UH_KELLER should be returned'");
});

test("Test convertTimestampToDate function", function() {
  expect(3);
  var wdclient = new org.WattDepot.Client("");
  equal(typeof(wdclient.convertTimestampToDate),  "function", "convertTimestampToDate should be a function");
  var testStart = wdclient.convertTimestampToDate(testTimestampStart);
  equal(typeof(testStart), "object", "Timestamp string should be a Javascript Date Object now.");
  equal(typeof(testStart.getYear), "function", "getYear should be a function in Date object.");  
});

test("Test getTimestampFromDate function", function() {
  expect(2);
  var wdclient = new org.WattDepot.Client("");
  equal(typeof(wdclient.getTimestampFromDate),  "function", "getTimestampFromDate should be a function");
  var testStart = wdclient.convertTimestampToDate(testTimestampStart);
  equal(wdclient.getTimestampFromDate(testStart), testTimestampStart, "Returned timestamp string should be same as the testTimestampStart"); 
});

module("org.wattdepot.client.QueryFunction");

test("Test getSourceNodeByName function", function() {
  expect(2);
  var wdclient = new org.WattDepot.Client(testServerUrl);
  equal(typeof(wdclient.getSourceNodeByName),  "function", "getSourceNodeByName should be a function");
  wdclient.getAllSource(testResponse);
  stop();
  function testResponse(response) {
	start();
	var sourceArray = wdclient.getJsonNode(response, "Source");
	equal(typeof(wdclient.getSourceNodeByName(sourceArray, testSourceName)), "object", 
	  "SIM_UH_KELLER should be found in the source array");
  }
});

test("Test getAllSource function", function() {
  expect(3);
  var wdclient = new org.WattDepot.Client(testServerUrl);
  equal(typeof(wdclient.getAllSource),  "function", "getAllSource should be a function");
  wdclient.getAllSource(testResponse);
  stop();
  function testResponse(response) {
	equal(typeof(response), "object", "Response should be a json object.");
	equal(typeof(wdclient.getJsonNode(response, "Sources")), "object", "'Sources' should be part of the response object.");
	start();
  }
});

/* Too many data in response, cannot be tested in ant(causing out of memory error)
test("Test getSourceSensorData function(This test may make more time)", function() {
  expect(3);
  var wdclient = new org.WattDepot.Client(testServerUrl);
  equal(typeof(wdclient.getSourceSensorData),  "function", "getSourceSensorData should be a function");
  wdclient.getSourceSensorData(testSourceName, testResponse);
  stop();
  function testResponse(response) {
	equal(typeof(response), "object", "Response should be a json object.");
	equal(typeof(wdclient.getJsonNode(response, "SensorDataIndex")), "object", "'SensorDataIndex' should be part of the response object.");
	start();
  }
});
*/

test("Test getSourceLatestSensorData function", function() {
  expect(3);
  var wdclient = new org.WattDepot.Client(testServerUrl);
  equal(typeof(wdclient.getSourceLatestSensorData),  "function", "getSourceLatestSensorData should be a function");
  wdclient.getSourceLatestSensorData(testSourceName, testResponse);
  stop();
  function testResponse(response) {
	equal(typeof(response), "object", "Response should be a json object.");
	equal(typeof(wdclient.getJsonNode(response, "SensorData")), "object", "'SensorData' should be part of the response object.");
	start();
  }
});

test("Test getPropertyValue function", function() {
  expect(3);
  var wdclient = new org.WattDepot.Client(testServerUrl);
  equal(typeof(wdclient.getPropertyValue),  "function", "getPropertyValue should be a function");
  wdclient.getSourceLatestSensorData(testSourceName, testResponse);
  stop();
  function testResponse(response) {
	ok(wdclient.getPropertyValue(response, "powerConsumed"), 
	  "powerConsumed should be found in the sensordata property");
	equal(typeof(wdclient.getSourceNodeByName(response, "foo")), "undefined",
	  "foo should not be found in the sensordata property");  
	start();
  }
});

test("Test getSourceDetail function", function() {
  expect(3);
  var wdclient = new org.WattDepot.Client(testServerUrl);
  equal(typeof(wdclient.getSourceDetail),  "function", "getSourceDetail should be a function");
  wdclient.getSourceDetail(testSourceName, testResponse);
  stop();
  function testResponse(response) {
	equal(typeof(response), "object", "Response should be a json object.");
	equal(typeof(wdclient.getJsonNode(response, "Source")), "object", "'Source' should be part of the response object.");
	start();
  }
});

test("Test getSourceSummary function", function() {
  expect(3);
  var wdclient = new org.WattDepot.Client(testServerUrl);
  equal(typeof(wdclient.getSourceSummary),  "function", "getSourceSummary should be a function");
  wdclient.getSourceSummary(testSourceName, testResponse);
  stop();
  function testResponse(response) {
	equal(typeof(response), "object", "Response should be a json object.");
	equal(typeof(wdclient.getJsonNode(response, "SourceSummary")), "object", "'SourceSummary' should be part of the response object.");
	start();
  }
});

test("Test getSourceSensorDataAt function", function() {
  expect(3);
  var wdclient = new org.WattDepot.Client(testServerUrl);
  equal(typeof(wdclient.getSourceSensorDataAt),  "function", "getSourceSensorDataAt should be a function");
  wdclient.getSourceSensorDataAt(testSourceName, testTimestampStart, testResponse);
  stop();
  function testResponse(response) {
	equal(typeof(response), "object", "Response should be a json object.");
	equal(typeof(wdclient.getJsonNode(response, "SensorData")), "object", "'SensorData' should be part of the response object.");
	start();
  }
});

test("Test getSourceSensorDataBetween function", function() {
  expect(3);
  var wdclient = new org.WattDepot.Client(testServerUrl);
  equal(typeof(wdclient.getSourceSensorDataBetween),  "function", "getSourceSensorDataBetween should be a function");
  wdclient.getSourceSensorDataBetween(testSourceName, testTimestampStart, testTimestampEnd, testResponse);
  stop();
  function testResponse(response) {
	equal(typeof(response), "object", "Response should be a json object.");
	equal(typeof(wdclient.getJsonNode(response, "SensorDatas")), "object", "'SensorDatas' should be part of the response object.");
	start();
  }
});

test("Test getSourcePowerDataAt function", function() {
  expect(3);
  var wdclient = new org.WattDepot.Client(testServerUrl);
  equal(typeof(wdclient.getSourcePowerDataAt),  "function", "getSourcePowerDataAt should be a function");
  wdclient.getSourcePowerDataAt(testSourceName, testTimestampStart, testResponse);
  stop();
  function testResponse(response) {
	equal(typeof(response), "object", "Response should be a json object.");
	equal(typeof(wdclient.getJsonNode(response, "SensorData")), "object", "'SensorData' should be part of the response object.");
	start();
  }
});
/*
module("com.eddgrant.qtr.math");

test("addPositiveNumbers", function() {	
	expect(2);
  ok(org.WattDepot.Client , "org.client");
	equal(4, new org.WattDepot.Client().add(1,3), 'Computed value should be 4');  
});
*/