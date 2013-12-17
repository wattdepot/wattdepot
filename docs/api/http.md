# HTTP API

WattDepot provides a language-independent interface to its server using HTTP. To understand this interface,
it helps to be familiar with the [WattDepot Domain Model](http://wattdepot.viewdocs.io/wattdepot/overview/domainmodel).

Path elements that have '{}' surrounding them are variables.  They are generally substituted with an ID for the type. For example {group-id} should be replaced with a valid UserGroup's id. The URIs below do not have the WattDepot server's address.

---------------
## Administrator

See the [Administrator domain model description](overview/domainmodel#administrator) for details about this concept.

The following operations all require administrator credentials.

### GET /wattdepot/{group-id}/

Retrieve the web interface for managing a group associated with a WattDepot server.

-----------
## Collector

See the [Collector domain model description](overview/domainmodel#collector) for details about this concept.

### GET /wattdepot/{group-id}/collector-metadata/{collector-metadata-id}

Retrieve the representation of this Collector metadata instance.

### GET /wattdepot/{group-id}/collector-metadatas/

Retrieve a list of representations of all Collector metadata instances associated with this group.

-------------
## Depository

See the [Depository domain model description](overview/domainmodel#depository) for details about this concept.

### GET /wattdepot/{group-id}/depository/{depository-id}

Retrieve a representation of this depository.

### GET /wattdepot/{group-id}/depositories/

Retrieve a list of representations of all depositories associated with this group.


--------------
## Measurement

See the [Measurement domain model description](overview/domainmodel#measurement) for details about this concept.

### GET /wattdepot/{group-id}/depository/{depository-id}/measurement/{measurement-id}

Retrieve a representation of a single measurement in the depository. 

*(Question: shouldn't measurement-id be a timestamp?)*

### GET /wattdepot/{group-id}/depository/{depository-id}/measurements/

Retrieve a representation of all the measurements in the depository. 

*(Isn't this expensive? Add parameters?)*

### GET /wattdepot/{group-id}/depository/{depository-id}/measurements/gviz/

Retrieve a representation (in Google Visualization format) for all the measurements in the depository.

*(Isn't this expensive? Add parameters? Provide link to gviz format?)*

### GET /wattdepot/{group-id}/depository/{depository-id}/sensors/

Retrieve a representation of all the sensors that have stored measurements in this depository.

---------
## Sensor

See the [Sensor domain model description](overview/domainmodel#sensor) for details about this concept.

### GET /wattdepot/{group-id}/sensor/{sensor-id}

Retrieve a representation of this sensor.

### GET /wattdepot/{group-id}/sensors/

Retrieve a representation of all sensors associated with this group.

----------
## Measured Value

See the [Measured Value domain model description](overview/domainmodel#measured-value) for details about this concept.

### GET /wattdepot/{group-id}/depository/{depository-id}/value/

Retrieve the measured value.

*(Obviously there are some required parameters to be documented.)*

### GET /wattdepot/{group-id}/depository/{depository-id}/value/gviz

Retrieve the measured value in Google Visualization format.

*(Obviously there are some required parameters to be documented.)*

---------------
## Measurement Type

See the [Measurement Type domain model description](overview/domainmodel#measurement-type) for details about this concept.

### GET /wattdepot/public/measurement-type/{measurement-type-id}

Retrieve a representation of this measurement type.

### GET /wattdepot/public/measurement-types/

Retrieve a representation of all defined measurement types.



# OLD STUFF



**/wattdepot/{group-id}/sensor-group/{sensor-group-id}** URI for getting SensorGroup instances.

**/wattdepot/{group-id}/sensor-groups/** URI to get all defined SensorGroups.

**/wattdepot/{group-id}/location/{location-id}** URI getting SensorLocation instances.

**/wattdepot/{group-id}/locations/** URI to get all defined SensorLocations.

**/wattdepot/public/sensor-model/{sensor-model-id}** URI for getting SensorModel instances.

**/wattdepot/public/sensor-models/** URI for getting all SensorModels.

**/wattdepot/{group-id}/user/{user-id}** URI for getting UserInfo instances.

**/wattdepot/{group-id}/user-group/{user-group-id}** URI for getting SensorGroup instances.

**/wattdepot/{group-id}/user-groups/** URI to get all defined SensorGroups.

## PUT
**/wattdepot/{group-id}/collector-metadata/** URI for storing new CollectorMetaData instances.

**/wattdepot/{group-id}/depository/** URI for storing new Depository instances.

**/wattdepot/{group-id}/depository/{depository-id}/measurement/** URI for putting a measurement into the depsository.

**/wattdepot/public/measurement-type/** URI for storing new MeasurmentType instances.

**/wattdepot/{group-id}/sensor/** URI to store new Sensor instances.

**/wattdepot/{group-id}/sensor-group/** URI to store new SensorGroup instances. 

**/wattdepot/{group-id}/location/** URI to store new SensorLocation instances. 

**/wattdepot/public/sensor-model/** URI for storing new SensorModel instances.

**/wattdepot/{group-id}/user/** URI to store new UserInfo instances.

**/wattdepot/{group-id}/user-group/** URI to store new UserGroup instances. 

## POST

**/wattdepot/{group-id}/collector-metadata/{collector-metadata-id}** URI for updating CollectorMetaData instances.

**/wattdepot/{group-id}/depository/{depository-id}** URI for updating Depository instances.

**/wattdepot/public/measurement-type/{measurement-type-id}** URI for updating MeasurmentType instances.

**/wattdepot/{group-id}/sensor/{sensor-id}** URI for updating Sensor instances.

**/wattdepot/{group-id}/sensor-group/{sensor-group-id}** URI for updating SensorGroup instances.

**/wattdepot/{group-id}/location/{location-id}** URI updating SensorLocation instances.

**/wattdepot/public/sensor-model/{sensor-model-id}** URI for updating SensorModel instances.

**/wattdepot/{group-id}/user/{user-id}** URI for updating UserInfo instances.

**/wattdepot/{group-id}/user-group/{user-group-id}** URI for updating SensorGroup instances.


## DELETE

**/wattdepot/{group-id}/collector-metadata/{collector-metadata-id}** URI for deleting CollectorMetaData instances.

**/wattdepot/{group-id}/depository/{depository-id}** URI for deleting Depository instances.

**/wattdepot/{group-id}/depository/{depository-id}/measurement/{measurement-id}** URI for deleting a measurement in the depsository.

**/wattdepot/public/measurement-type/{measurement-type-id}** URI for deleting MeasurmentType instances.

**/wattdepot/{group-id}/sensor/{sensor-id}** URI for deleting Sensor instances.

**/wattdepot/{group-id}/sensor-group/{sensor-group-id}** URI for deleting SensorGroup instances.

**/wattdepot/{group-id}/location/{location-id}** URI deleting SensorLocation instances.

**/wattdepot/public/sensor-model/{sensor-model-id}** URI for deleting SensorModel instances.

**/wattdepot/{group-id}/user/{user-id}** URI for deleting UserInfo instances.

**/wattdepot/{group-id}/user-group/{user-group-id}** URI for deleting SensorGroup instances.

