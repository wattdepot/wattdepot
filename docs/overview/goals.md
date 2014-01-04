# Goals

The design of WattDepot is motivated by the following primary goals for data collection, storage, and analysis:

*WattDepot must be agnostic about the platform, programming language, and meters used to monitor the environment.*

WattDepot will run on all major operating systems. We provide an HTTP API which can be used by any modern programming language or environment to communicate with WattDepot servers. To simply development, we provide client libraries for popular languages including Java, Javascript, and Python.  

WattDepot can support any meter that implements an HTTP-based protocol for data transmission.

*WattDepot can collect and analyze a variety of data relevant to energy analysis.*

While WattDepot's origins are in energy data collection, storage, and analysis, it is clear that understanding energy usually requires knowledge of other environmental factors. WattDepot can store other forms of data in addition to energy. 

*WattDepot must represent aggregations of data sources.*

For example, a building might have multiple meters monitoring energy consumption, one per floor. WattDepot can represent the power consumed by individual floors, as well as an aggregate source representing the building as a whole. Aggregations can be nested, so that floors can be aggregated into buildings, buildings into neighborhoods, and neighborhoods into cities.

*WattDepot must support data interpolation.*

For example, a meter might provide a snapshot of energy usage once per hour for a given device. Clients can request the power consumed by this device at any time instant, and WattDepot will automatically provide interpolation when the requested time does not match a time for which actual sensor data is available. This becomes particularly important and useful for aggregate power sources. For example, the meters on individual floors of a building will rarely send their data at exactly the same time instant, so providing an aggregate value for power consumed requires combining individual data values obtained at different times.

*WattDepot must be architecturally decoupled from the underlying data storage technology.*

We want to allow WattDepot to support both traditional relational as well as NoSQL technologies.

*WattDepot must support both cloud and local installation.*

We will document both local and Heroku installation.  Use of other cloud providers such as CloudBees should be straightforward. 
