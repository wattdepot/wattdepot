# Motivation

The design of WattDepot is motivated by the need to provide a unique set of features for energy collection, storage, and analysis:

*WattDepot must be agnostic about the kinds of meters used to monitor data, and whether the data is utility-scale or personal-scale.* WattDepot implements an HTTP-based protocol for data transmission that can be used to implement clients for a wide variety of devices; the major constraint is that these devices need to have Internet access. WattDepot clients can be written in any language that supports the HTTP protocol. We provide a high-level client library for Java.

*WattDepot must represent aggregations of data sources.* For example, a building might have multiple meters monitoring energy consumption, one per floor. WattDepot can represent the power consumed by individual floors, as well as an aggregate source representing the building as a whole. Aggregations can be nested, so that floors can be aggregated into buildings, buildings into neighborhoods, and neighborhoods into cities.

*WattDepot must automatically perform data interpolation when necessary.* For example, a meter might provide a snapshot of energy usage once per hour for a given device. Clients can request the power consumed by this device at any time instant, and WattDepot will automatically provide interpolation when the requested time does not match a time for which actual sensor data is available. This becomes particularly important and useful for aggregate power sources. For example, the meters on individual floors of a building will rarely send their data at exactly the same time instant, so providing an aggregate value for power consumed requires combining individual data values obtained at different times.

*WattDepot must be architecturally decoupled from the underlying data storage technology.* This supports experimentation with both traditional relational as well as NoSQL technologies, and facilitates scalability. 

*WattDepot must be designed to support both PaaS and local installation.* We have deployed WattDepot to the Heroku cloud-based hosting service.