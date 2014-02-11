# Performance Evaluation

## Goals

There are several goals for WattDepot performance evaluation. We want to answer the following questions.

1. What is the maximal throughput for Measurements?

2. What is the responsiveness, providing information about the Measurements and defined objects?

3. What happens to the system (throughput, responsiveness) after 24 hours, 7 days, ... of load?

4. What is the performance under a variety of simulated "real world" mixtures of puts and gets?

5. What are the answers to all of these questions on different platform installations (local machine, Mopsa, Heroku, ...)?



## Performance Evaluation Tools

We have provided several tools to help evaluate WattDepot's performance for several low level operations. These operations are:

* Put a measurement.
* Get an interpolated value at a given time.
* Get an interpolated value from a start time to an end time.


## Real World Scenarios

We've used WattDepot in two distinct real world situations.  Supporting the Kukui Cup at University of Hawaii, Manoa and Hawaii Pacific University. <link to documentation?>

### Kukui Cup: University of Hawaii, Manoa

#### Measurement Generation

40 Sensors producing two measurements, power and energy, every 15 seconds (20 measurements/second).

#### WattDepot Queries

Makahiki status page generates queries about the latest measurement for each Lounge every 10 seconds. There are 20 Lounges (3.333 queries/second). The JavaScript calls *server_url + "/depository/power/value/gviz/?sensor=" + source + "&latest=true";* for each lounge.

Additionally, there are several activities that ask the players to explore the energy and power information stored in WattDepot.

Heat Map, Hot Spot, and Visualizer calls   *host_uri + '/depository/' + dataType + '/values/gviz/?sensor=' + source[i] +'&start=' + startTime + '&end=' + endTime + '&interval='+interval*
PowerMeter calls *server_url + "/depository/power/value/gviz/?sensor=" + source + "&latest=true";*

Approximately 350 players times X % who do the activities equals N queries in a typical two week challenge. Based upon the 2014 Kukui Cup there were 61, 22, 21, 44 = 148 players who completed the activities.

### Kukui Cup: Hawaii Pacific University

#### Measurement Generation

6 Sensors producing two measurements, power and energy, every 10 seconds (2 measurements/second).

#### WattDepot Queries

Makahiki status page generates queries about the latest measurement for each Team every 10 seconds. There are 6 teams (1 query/second). 

Additionally, there are several activities that ask the players to explore the energy and power information stored in WattDepot.

Heat Map, Hot Spot, and Visualizer calls   *host_uri + '/depository/' + dataType + '/values/gviz/?sensor=' + source[i] +'&start=' + startTime + '&end=' + endTime + '&interval='+interval*
PowerMeter calls *server_url + "/depository/power/value/gviz/?sensor=" + source + "&latest=true";*


### Aarhus University

#### Measurement Generation

#### WattDepot Queries


## Methodology

### Setting up performance evaluation

1. Install WattDepot following the [installation instructions](installationguide/installation). You need to install version 3.0.0-M8 or above.
2. For performance testing we recommend you reduce the logging to a minimum. In the wattdepot-server.properties file change the *wattdepot-server.loggin.level* to SEVERE.
3. [Start the WattDepot server](installationguide/installation#Run the server). 
4. Define an Organization and User to use in the performance testing. Remember the User's password, we'll use it later.
5. In a new shell start up the StressTestCollector.
    
    $ java -cp wattdepot-*version*.jar org.wattdepot.client.http.api.collector.StressTestCollector -s *server URI* -u *userId* -p *password* -o *organizationId* -n *numThreads* -m *sleep*

6. Open the 

## Results

what the results of prior performance evaluation runs have been.
