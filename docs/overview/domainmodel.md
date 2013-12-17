# Domain model

A WattDepot server implements its capabilities through the following domain model.

## User

To manipulate a WattDepot server, such as adding or retrieving data, you must provide a user name and a password. Every WattDepot server has an administrator account that can create new users and assign passwords to them.

## Group

Groups provide a "namespace" for WattDepot entities, and are typically associated with an organization. For example, "University of Hawaii at Manoa" could be a group, and "Hawaii Pacific University" could be another group.

All users, sensors, sensor groups, collectors, depositories, measurements, and measurement types belong to one and only one group, and this group must be specified when accessing these entities.  This enables two groups, such as UH and HPU to have separate depositories both named "energy" without confusion.

## Sensor

Sensors represent a device that measures (or predicts) a physical phenomena. Sensors can have the following properties:

* URL: If available for this meter, the URL represents an IP address where a collector can programmatically access measurement(s) made by this sensor. 
* Location: The coordinates (latitude, longitude, altitude) associated with the measurements made by this sensor.
* Model: A description of the sensor. This can potentially include its protocol, version, etc.
* Properties: A potentially empty list of key-value pairs that provide additional information about this sensor.

## Sensor Group

It is extremely convenient to provide aggregations of sensor data. For example, the energy consumed by a building might be the aggregate of the energy measurements associated with several sensors.

By defining a Sensor Group and associating individual Sensors with it, clients can obtain aggregate measures.

## Collector

Collectors are processes that contact a Sensor and obtain measurements from it.

One implementation of a Collector might be a software program that uses the IP address of a Sensor to contact it and request a measurement value.  After obtaining that value, it then contacts a WattDepot server and uses the HTTP API to store that value in the server.

An alternative implementation of a Collector is a person who reads a meter manually to obtain a measurement, then uses a GUI interface to a WattDepot server to store that value.

In order for a Collector to do its job, it must be able to obtain information about the Sensor it is supposed to contact. It is also useful for the Collector to know how often it should collect data, what data to collect, and where to store it in the server. WattDepot represents this information with the "Collector Metadata" entity.

## Depository

Depositories store measurements made by Sensors and collected by Collectors.

Depositories can store measurements made by different Sensors and different Collectors, but all measurements must be of one and only one Measurement Type.

## Measurement Type

WattDepot implements all the units of measurement provided by the [JScience API](http://www.unitsofmeasurement.org/). Supported units include both [SI Units](http://jscience.org/api/javax/measure/unit/SI.html) and [Non-SI Units](http://jscience.org/api/javax/measure/unit/NonSI.html).

In addition, it is possible to define new measurement types. Adding a new measurement type requires changes to the source code and rebuilding of the system.

## Measurement

Measurements are made by Sensors and collected by Collectors.  A measurement includes the following information:

* the sensor that made the measurement,
* the collector that collected the measurment
* a timestamp indicating when the measurement was made by the sensor
* the numeric value of the measurement
* the measurement type

Sensors can either measure phenomena as they occur, or predict phenomena that may or may not occur.  Sensors can provide measurements for phenomena with a timestamp in the future.  It is the responsibility of clients to check the sensor associated with a measurement to determine its validity and meaning.

## Measured Value

Measured Values are different from Measurements. Measurements are the "raw data" in WattDepot.  For example, a Collector may obtain a power measurement from a Sensor every 10 seconds.

Unfortunately, WattDepot clients often want to know the value of a measurement at a specific timestamp regardless of whether or not a Collector has obtained a Measurement at that time or not.  For example, a client might want a visualization that shows the power being consumed by a building every hour on the hour.

For this use case, WattDepot provides Measured Values.  Measured Values differ from Measurements in that WattDepot will interpolate in the cases where there does not exist a Measurement corresponding to the precise timestamp requested by the client. (In real life, this is the case virtually all of the time).

WattDepot supports two kinds of Measured Values: point values and difference values. 

Point values are calculated for a single timestamp. If there exists a Measurement corresponding to the timestamp, WattDepot will return that value. If the timestamp is between two Measurements, WattDepot
calculates the linear interpolated value and returns it.  If the timestamp is not bounded by two Measurements, then WattDepot will return an error.

For difference values, WattDepot gets the point values for the starting timestamp and the ending timestamp and returns the difference.

It is the responsibility of the client to assess the validity of Measured Values returned by WattDepot.
