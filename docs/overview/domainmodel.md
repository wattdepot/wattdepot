# Domain model

A WattDepot server implements its capabilities through the following domain model.

## Administrator

Every WattDepot server installation requires the definition of a single administrator (whose name by default is "root" but which can be changed by the installer) and associated password (with no default value, and which must be specified by the installer). This administrator is automatically placed in the predefined organization named "admin", which grants the administrator special capabilities not available to users.  These capabilities include the ability to:

* add and delete organizations
* add and delete users
* add and delete entities in the "public" organization (measurement types, sensor models)
* login as any user

## Organization

Users, sensors, sensor groups, collectors, depositories, and measurements all belong to a single "organization".  Organizations create a namespace for WattDepot entities. For example, a single WattDepot server might hold data from two organizations, "University of Hawaii" and "Hawaii Pacific University". Organizations are defined with an id, which is a short alphabetic abbreviation suitable for identifying the organization in URLs. The organization "University of Hawaii" might have the id "uh".

The organization id is generally the first element of a WattDepot API call. For example, to retrieve information about the University of Hawaii repositories, you might invoke:

```
GET http://server.wattdepot.org/wattdepot/uh/depositories
```

In general, you can only manipulate entities created within your organization. There is one exception: the pre-defined organization with the id "public" which defines "global" entities such as measurement types and sensor models. 

## User

To use a WattDepot server, begin by requesting a user name and password from the server's administrator. You must also indicate if you are joining an existing organization or if you wish a new organization to be created on the server. Once you have obtained these credentials, you can:

* Add and delete depositories
* Add and delete sensors (and their associated locations, models, and properties)
* Add and delete sensor groups
* Add and delete collector process definitions
* Add and delete measurements (if you are doing this manually).
* Query the server for measurements and measured values in a variety of ways.

All of these manipulations occur within your organization.


## Sensor

Sensors represent a device that measures (or predicts) a physical phenomena. Sensors have the following properties:

* URL: If available for this meter, the URL represents an IP address where a collector can programmatically access measurement(s) made by this sensor. 
* Sensor Model: A description of the sensor. This can potentially include its protocol, version, etc.
* Properties: A potentially empty list of key-value pairs that provide additional information about this sensor.

Note that some physical devices can measure multiple phenomena (such as energy from multiple floors, or both energy and power, or energy and temperature).  In these cases, the physical device will be represented by multiple Sensors. 

## Sensor Group

Clients often find it convenient to request aggregations of sensor data. For example, a client might wish to know the energy consumed by a building, which might involve aggregating the energy measurements associated with Sensors located on each floor of the building.

By defining a Sensor Group and associating individual Sensors with it, clients can obtain aggregate measurements.

All Sensors in a Sensor Group must collect data of the same Measurement Type.

## Collector

Collectors are processes that contact a Sensor, obtain Measurements from it, then store this data in a WattDepot server.

One implementation of a Collector might be a software program that uses the IP address of a Sensor to contact it and request a measurement value.  After obtaining that value, it then contacts a WattDepot server and uses the HTTP API to store that value in the server.

An alternative implementation of a Collector is a person who reads a meter manually to obtain a measurement, then uses a GUI interface to a WattDepot server to store that value.

In order for a Collector to do its job, it must be able to obtain information about the Sensor it is supposed to contact. It is also useful for the Collector to know how often it should collect data, what data to collect, and where to store it in the server. WattDepot represents this information with the "Collector Process Definition" entity.

In prior versions of WattDepot, the collector software programs hard-wired information about when, where, and how often to collect data into their source code. In this version, we expect Collector programs to first query the WattDepot server to obtain their process definition, and use that definition to determine what to do.  By representing the task associated with a Collector in the server explicitly as process definition, the server can provide significant new capabilities to users. For example, a server can now understand how frequently to receive data from a Collector, and signal the user if data is not being received as expected.

## Depository

Depositories store measurements made by Sensors and collected by Collectors.

Depositories can store Measurements made by different Sensors and different Collectors, but all Measurements must be of one and only one Measurement Type.

## Measurement Type

WattDepot provides all the units of measurement provided by the [JScience API](http://www.unitsofmeasurement.org/). Supported units include both [SI Units](http://jscience.org/api/javax/measure/unit/SI.html) and [Non-SI Units](http://jscience.org/api/javax/measure/unit/NonSI.html).

In addition, it is possible in WattDepot to define new Measurement Types. Adding a new Measurement Type requires changes to the source code and rebuilding of the system.

Measurement Types are members of the "public" organization, which makes them visible to all users of a server.

## Measurement

Measurements are made by Sensors and collected by Collectors.  A Measurement includes the following information:

* the Sensor that made the Measurement,
* the Collector that collected the Measurement
* a timestamp indicating when the Measurement was made by the Sensor
* the numeric value of the Measurement
* the Measurement Type

Sensors can either measure phenomena as they occur, or predict phenomena that may or may not occur.  Sensors can provide measurements for phenomena with a timestamp in the future.  It is the responsibility of clients to check the sensor associated with a measurement to determine its validity and meaning.

## Interpolated Value

Interpolated Values are different from Measurements. Measurements are the "raw data" in WattDepot.  For example, a Collector may obtain a power measurement from a Sensor every 10 seconds.

Unfortunately, WattDepot clients often want to know a value at a specific timestamp regardless of whether or not a Collector has obtained a Measurement at that timestamp.  For example, a client might want a visualization that shows the power being consumed by a building every hour on the hour.

For this use case, WattDepot provides Interpolated Values.  Interpolated Values differ from Measurements in that WattDepot will interpolate in the cases where there does not exist a Measurement corresponding to the precise timestamp requested by the client. (In real life, this is the case virtually all of the time).

WattDepot supports two kinds of Interpolated Values: point values and difference values. 

Point values are calculated for a single timestamp. If there exists a Measurement corresponding to the timestamp, WattDepot will return that value. If the timestamp is between two Measurements, WattDepot
calculates the linear interpolated value and returns it.  If the timestamp is not bounded by two Measurements, then WattDepot will return an error.

For difference values, WattDepot gets the point values for the starting timestamp and the ending timestamp and returns the difference.

It is the responsibility of the client to assess the validity of Interpolated Values returned by WattDepot.
