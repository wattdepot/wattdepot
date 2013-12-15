# HTTP API Guide

WattDepot's primary interface is the HTTP API. Clients use the HTTP API for Creating, Reading, 
Updating and Deleting instances of the [WattDepot Domain Model]
(http://wattdepot.viewdocs.io/wattdepot/overview/domainmodel) classes.

## Understanding the API

All WattDepot URLs begin with the `keyword` */wattdepot/*. Path elements that have '{}' surrounding 
them are variables.  The actual value will be an ID for the type. For example {group-id} should be 
replaced with a valid UserGroup's id. The URIs below do not have the WattDepot server's address.

## GET

*/wattdepot/{group-id}/* URI for the group administration. (e.g. **http://server.wattdepot.org/wattdepot/uh/** 
brings up the user group uh's administration page). 
This will bring up the Web interface for managing WattDepot for the given user group.

*/wattdepot/{group-id}/collector-metadata/{collector-metadata-id}* URI for getting CollectorMetaData instances.

*/wattdepot/{group-id}/collector-metadatas/* URI for getting all the defined CollectorMetaData.

*/wattdepot/{group-id}/depository/{depository-id}* URI for getting Depository instances. 

*/wattdepot/{group-id}/depositories/* URI for getting all defined Depositories.

*/wattdepot/{group-id}/depository/{depository-id}/measurement/{measurement-id}* URI for manipulating a measurement in the depsository. Supports GET and DELETE requests.

*/wattdepot/{group-id}/depository/{depository-id}/measurements/* URI to get all the measurements in the depsository. **Add parameters.**

*/wattdepot/{group-id}/depository/{depository-id}/measurements/gviz/* URI to get all the measurements in the depsository. **Add parameters.**

*/wattdepot/{group-id}/depository/{depository-id}/sensors/* URI for getting all the sensors that have stored measurements in the depository.

*/wattdepot/{group-id}/depository/{depository-id}/value/* URI to get all the measured value at the given time. **Add parameters.**

*/wattdepot/{group-id}/depository/{depository-id}/value/gviz/* URI to get all the measured value. **Add parameters.**

*/wattdepot/public/measurement-type/{measurement-type-id}* URI for getting MeasurmentType instances.

*/wattdepot/public/measurement-types/* URI for getting all defined MeasurmentTypes.

*/wattdepot/{group-id}/sensor/{sensor-id}* URI for getting Sensor instances. 

*/wattdepot/{group-id}/sensors/* URI to get all defined Sensors.

*/wattdepot/{group-id}/sensor-group/{sensor-group-id}* URI for getting SensorGroup instances.

*/wattdepot/{group-id}/sensor-groups/* URI to get all defined SensorGroups.

*/wattdepot/{group-id}/location/{location-id}* URI getting SensorLocation instances.

*/wattdepot/{group-id}/locations/* URI to get all defined SensorLocations.

*/wattdepot/public/sensor-model/{sensor-model-id}* URI for getting SensorModel instances.

*/wattdepot/public/sensor-models/* URI for getting all SensorModels.

*/wattdepot/{group-id}/user/{user-id}* URI for getting UserInfo instances.

*/wattdepot/{group-id}/user-group/{user-group-id}* URI for getting SensorGroup instances.

*/wattdepot/{group-id}/user-groups/* URI to get all defined SensorGroups.

## PUT
*/wattdepot/{group-id}/collector-metadata/* URI for storing new CollectorMetaData instances.

*/wattdepot/{group-id}/depository/* URI for storing new Depository instances.

*/wattdepot/{group-id}/depository/{depository-id}/measurement/* URI for putting a measurement into the depsository.

*/wattdepot/public/measurement-type/* URI for storing new MeasurmentType instances.

*/wattdepot/{group-id}/sensor/* URI to store new Sensor instances.

*/wattdepot/{group-id}/sensor-group/* URI to store new SensorGroup instances. 

*/wattdepot/{group-id}/location/* URI to store new SensorLocation instances. 

*/wattdepot/public/sensor-model/* URI for storing new SensorModel instances.

*/wattdepot/{group-id}/user/* URI to store new UserInfo instances.

*/wattdepot/{group-id}/user-group/* URI to store new UserGroup instances. 

** POST

*/wattdepot/{group-id}/collector-metadata/{collector-metadata-id}* URI for updating CollectorMetaData instances.

*/wattdepot/{group-id}/depository/{depository-id}* URI for updating Depository instances.

*/wattdepot/public/measurement-type/{measurement-type-id}* URI for updating MeasurmentType instances.

*/wattdepot/{group-id}/sensor/{sensor-id}* URI for updating Sensor instances.

*/wattdepot/{group-id}/sensor-group/{sensor-group-id}* URI for updating SensorGroup instances.

*/wattdepot/{group-id}/location/{location-id}* URI updating SensorLocation instances.

*/wattdepot/public/sensor-model/{sensor-model-id}* URI for updating SensorModel instances.

*/wattdepot/{group-id}/user/{user-id}* URI for updating UserInfo instances.

*/wattdepot/{group-id}/user-group/{user-group-id}* URI for updating SensorGroup instances.


** DELETE

*/wattdepot/{group-id}/collector-metadata/{collector-metadata-id}* URI for deleting CollectorMetaData instances.

*/wattdepot/{group-id}/depository/{depository-id}* URI for deleting Depository instances.

*/wattdepot/{group-id}/depository/{depository-id}/measurement/{measurement-id}* URI for deleting a measurement in the depsository.

*/wattdepot/public/measurement-type/{measurement-type-id}* URI for deleting MeasurmentType instances.

*/wattdepot/{group-id}/sensor/{sensor-id}* URI for deleting Sensor instances.

*/wattdepot/{group-id}/sensor-group/{sensor-group-id}* URI for deleting SensorGroup instances.

*/wattdepot/{group-id}/location/{location-id}* URI deleting SensorLocation instances.

*/wattdepot/public/sensor-model/{sensor-model-id}* URI for deleting SensorModel instances.

*/wattdepot/{group-id}/user/{user-id}* URI for deleting UserInfo instances.

*/wattdepot/{group-id}/user-group/{user-group-id}* URI for deleting SensorGroup instances.

