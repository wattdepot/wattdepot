# Deploying WattDepot locally

Cover the running of a wattdepot system, including bringing up a server, bringing up collectors, etc. locally.

## Requirements

### Java

WattDepot requires [Java Platform Standard Edition] (http://www.oracle.com/us/technologies/java/standard-edition/overview/index.html) version 1.7 or above. 

### PostgreSQL

Currently, WattDepot is using Hibernate for the persistence layer and the [PostgreSQL] (http://www.postgresql.org/) database for storage. We recommend version 9.1 or higher.

## Install WattDepot

Follow the [installation instructions](http://wattdepot.viewdocs.io/wattdepot/installationguide/installation).
Make sure the WattDepot Server is running by accessing the WattDepot Administration web page (http://localhost:8192/wattdepot/admin/).

## Define WattDepot entities

Follow the [entity definition instructions](http://wattdepot.viewdocs.io/wattdepot/userguide/definitions) to define your WattDepot entities.

## Run the collectors

Once the CollectorProcessDefinitions are defined in the system, you can start the Collector processes.

Change to the top level of the wattdepot distribution you downloaded. Run the Collectors. Currently, 
you have to start a separate process for each Collector you want to run.

    $ java -cp wattdepot-*version*.jar org.wattdepot.client.http.api.collector.MultiThreadedCollector -c *collector-meta-data-id*
    
The MultiThreadedCollector will start up the Collector process defined in the given *collector-meta-data-id*.
