# HTTP API

WattDepot provides a language-independent interface to its server using HTTP. To understand this interface,
it helps to be familiar with the [WattDepot Domain Model](http://wattdepot.viewdocs.io/wattdepot/overview/domainmodel).

Path elements that have '{}' surrounding them are variables.  They are generally substituted with an ID for the type. For example {org-id} should be replaced with a valid organization id. The URLs below do not include the WattDepot server's address.

*(We need to figure out how to document the arguments and return values from these calls. One possibility is to provide more complete documentation in the JavaDocs, and then provide a (stable) link to the latest released JavaDoc on this page.)*

*(Also, it might be useful to compare this API with the [WattDepot 2.0 API](https://code.google.com/p/wattdepot/wiki/RestApi), to ensure that we are not missing anything important, and that we are documenting the appropriate features. Some things I notice are missing from this page: (1) Access control info. (Admin vs. regular user); (2) health API call; (3) sample return values; (4) HTTP status codes.)*


---------------
## Organization

See the [Organization domain model description](overview/domainmodel#organization) for details about this concept.

Organization operations require administrator credentials.

### GET /wattdepot/{org-id}/

Retrieve the web interface for managing an organization associated with a WattDepot server.

*(There should be more organization operations, right?)* 

-------------
## User

See the [User domain model description](overview/domainmodel#user) for details about this concept.

### GET /wattdepot/{org-id}/user/{user-id}

Retrieve a representation of a user.

*(Who can do this? Anyone in the group?)*

### GET /wattdepot/{org-id}/users/

Retrieve a representation of all users associated with this group.

*(This was not in the API, but should be, right?)*

### PUT /wattdepot/{org-id}/user/

Create a new user.

*(Restricted to the admin, right?)*

### POST /wattdepot/{org-id}/user/{user-id}

Update this representation of a user.

*(Who can do this? The user? The admin?)*

### DELETE /wattdepot/{org-id}/user/{user-id}

Delete this representation of a user.

*(Who can do this? The user? The admin? What happens to the data sent by this user? )*


---------
## Sensor

See the [Sensor domain model description](overview/domainmodel#sensor) for details about this concept.

### GET /wattdepot/{org-id}/sensor/{sensor-id}

Retrieve a representation of this sensor.

### GET /wattdepot/{org-id}/sensors/

Retrieve a representation of all sensors associated with this group.

### PUT /wattdepot/{org-id}/sensor/

Create a new sensor.

### POST /wattdepot/{org-id}/sensor/{sensor-id}

Update this representation of a sensor.

### DELETE /wattdepot/{org-id}/sensor/{sensor-id}

Delete this sensor.

*(What happens to all the measurements associated with this sensor?)*


----------
## Sensor Group

See the [Sensor Group domain model description](overview/domainmodel#sensor-group) for details about this concept.

### GET /wattdepot/{org-id}/sensor-group/{sensor-org-id}

Retrieve a representation of this sensor group.

### PUT /wattdepot/{org-id}/sensor-group/

Create a new sensor group.

### POST /wattdepot/{org-id}/sensor-group/{sensor-org-id}

Update the representation of this sensor group.

### DELETE /wattdepot/{org-id}/sensor-group/{sensor-org-id}

Delete this sensor group.

-----------
## Sensor Model

See the [Sensor domain model description](overview/domainmodel#sensor) for details about sensor models, which are part of the Sensor description.

Note that Sensor Models are always defined in the *public* group.

*(Are these calls restricted to the admin user?)*

### GET /wattdepot/public/sensor-model/{sensor-model-id}

Retrieve a representation of a sensor model.

### GET /wattdepot/public/sensor-models/

Retrieve a representation of all sensor models.

### PUT /wattdepot/public/sensor-model/

Create a new sensor model.

### POST /wattdepot/public/sensor-model/{sensor-model-id}

Update this representation of a sensor model.

### DELETE /wattdepot/public/sensor-model/{sensor-model-id}

Delete this representation of a sensor model.


-----------
## Collector

See the [Collector domain model description](overview/domainmodel#collector) for details about this concept.

*(Do we want to call this "collector-metadata", or just "collector"? Which is less confusing?)*

### GET /wattdepot/{org-id}/collector-metadata/{collector-metadata-id}

Retrieve the representation of this Collector metadata instance.

### GET /wattdepot/{org-id}/collector-metadatas/

Retrieve a list of representations of all Collector metadata instances associated with this group.

### PUT /wattdepot/{org-id}/collector-metadata/

Create a new representation of a collector's metadata.

### POST /wattdepot/{org-id}/collector-metadata/{collector-metadata-id}

Update a pre-existing collector's metadata.

### DELETE /wattdepot/{org-id}/collector-metadata/{collector-metadata-id}

Delete this metadata representation.


-------------
## Depository

See the [Depository domain model description](overview/domainmodel#depository) for details about this concept.

### GET /wattdepot/{org-id}/depository/{depository-id}

Retrieve a representation of this depository.

### GET /wattdepot/{org-id}/depositories/

Retrieve a list of representations of all depositories associated with this group.

### PUT /wattdepot/{org-id}/depository/

Create a new depository. 

### POST /wattdepot/{org-id}/depository/{depository-id}

Update a pre-existing depository's representation.

### DELETE /wattdepot/{org-id}/depository/{depository-id}

Delete this repository.

*(All measurements in this repository will be lost?)*

--------------
## Measurement

See the [Measurement domain model description](overview/domainmodel#measurement) for details about this concept.

### GET /wattdepot/{org-id}/depository/{depository-id}/measurement/{measurement-id}

Retrieve a representation of a single measurement in the depository. 

*(Question: shouldn't measurement-id be a timestamp?)*

### GET /wattdepot/{org-id}/depository/{depository-id}/measurements/

Retrieve a representation of all the measurements in the depository. 

*(Isn't this expensive? Add parameters?)*

### GET /wattdepot/{org-id}/depository/{depository-id}/measurements/gviz/

Retrieve a representation (in Google Visualization format) for all the measurements in the depository.

*(Isn't this expensive? Add parameters? Provide link to gviz format?)*

### GET /wattdepot/{org-id}/depository/{depository-id}/sensors/

Retrieve a representation of all the sensors that have stored measurements in this depository.

### PUT /wattdepot/{org-id}/depository/{depository-id}/measurement/

Store a new measurement in this depository.

### DELETE /wattdepot/{org-id}/depository/{depository-id}/measurement/{measurement-id}

Delete this measurement.


------------
## Location

See the [Sensor domain model description](overview/domainmodel#sensor) for details about locations, which are part of the Sensor description.

*(Do we need to provide locations as an independent part of the API?  Why not just manipulate them directly as part of the Sensor representation? Wouldn't that be simpler and more easily understandable from the user side, given that locations have no meaning apart from the sensors they are associated with?)*

### GET /wattdepot/{org-id}/location/{location-id}

Retrieve a representation of a location.

### GET /wattdepot/{org-id}/locations/

Retrieve a representation of all locations associated with this group.

### PUT /wattdepot/{org-id}/location/

Create a new location.

### POST /wattdepot/{org-id}/location/{location-id}

Update this representation of a location.

### DELETE /wattdepot/{org-id}/location/{location-id}

Delete this location.


----------
## Measured Value

See the [Measured Value domain model description](overview/domainmodel#measured-value) for details about this concept.

### GET /wattdepot/{org-id}/depository/{depository-id}/value/

Retrieve the measured value.

*(Obviously there are some required parameters to be documented.)*

### GET /wattdepot/{org-id}/depository/{depository-id}/value/gviz

Retrieve the measured value in Google Visualization format.

*(Obviously there are some required parameters to be documented.)*

---------------
## Measurement Type

See the [Measurement Type domain model description](overview/domainmodel#measurement-type) for details about this concept.

### GET /wattdepot/public/measurement-type/{measurement-type-id}

Retrieve a representation of this measurement type.

### GET /wattdepot/public/measurement-types/

Retrieve a representation of all defined measurement types.

### PUT /wattdepot/public/measurement-type/

Store a new measurement type.

*(Is this even possible? I thought measurement types were implemented in code?)*

### POST /wattdepot/public/measurement-type/{measurement-type-id}

*(Is this even possible? I thought measurement types were implemented in code?)*

### DELETE /wattdepot/public/measurement-type/{measurement-type-id}

*(Is this even possible? I thought measurement types were implemented in code?)*

