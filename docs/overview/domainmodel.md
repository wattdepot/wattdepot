# Domain model

WattDepot models the domain of energy collection, storage, and analysis through the following
conceptual entities.

## <a name="sensors"></a> Sensors

Sensors represent the device that measures the physical universe. Sensors have:
* URIs - A Internet address where collector processes can access their measurements. 
* Locations - The location on the earth of the sensor (e.g. Latitude, Longitude, Altitude, Description).
* Models - A description of the type of sensor, the protocol and version it is using.
* Properties - A potentially empty list of properties that describe the individual sensor.

## <a name="sensorgroups"></a> Sensor Groups

Sensor groups are, simply, groups of sensors. WattDepot uses these groups for aggregation. We can
group all the energy sensors in a building into a single group.

## <a name="collectors"></a> Collectors

Collectors are processes that contact a Sensor via its URI and produce measurements. Collector
Metadata is a representation of how often to poll a Sensor and the Depository in which to store the
measurements. Collectors should use the Collector Metadata to control their activities.

## <a name="depositories"></a> Depositories

Depositories are responsible for storing the measurements made by the Sensors. They have an
associated measurement type.

## <a name="measurements"></a> Measurements

Measurements are made by Sensors and consist of the sensor that made the measurement, the time the
measurement was made, the value of the measurement and the measurement type.

We can use measurements to represent forecast information, just create a forecast sensor and any
measurements for the sensor are forecasts. This way forecast information and actual sensor
measurements will not be confused.

## <a name="measuredvalues"></a> Measured Values

Measured values are often interpolated from sensor measurements. There are two primary Measured
Values, point values and difference values.

Point values are calculated at a single point in time. If the time corresponds to a measurement
then WattDepot returns the measurement's value. If the time is between two measurements WattDepot
calculates the linear interpolated value and returns it.

For Difference values, WattDepot gets the value for the start and end points then subtracts the
start value from the end value and returns the result.

WattDepot allows the client to get both types of values for all types of measurements. It is the
responsibility of the client to know if it makes sense to subtract the end value from the start
value for the particular measurement type.
