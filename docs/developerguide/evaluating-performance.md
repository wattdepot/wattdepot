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

* Put a measurement. Storing data in WattDepot.
* Get the earliest value. Information about the measurements in WattDepot.
* Get the latest value. This is typically how you would get the current power.
* Get a value at a given time. This is generally how you would get the power or temperature at a given time.
* Get a value from a start time to an end time. This is generally how you would calculate the Energy used.

In the package

    org.wattdepot.client.http.api.performance

you will find five programs:

    FindMeasurementThroughput    
    FindGetEarliestValueThroughput
    FindGetLatestValueThroughput
    FindGetValueDateThroughput
    FindGetValueDateDateThroughput

Each of the programs have the same command line arguments.

* -s <server URI>
: The URI to the WattDepot server. e.g. http://server.wattdepot.org:8192/

* -u <username>
: The User Id of a defined WattDepot user.

* -p <password>
: The User's password, used to connect to the WattDepot server.

* -o <organizationId>
: The User's organization id.

* -n <numSeconds>
: The number of seconds to run before checking the performance rates. We recommend values between 15 and 90. Shorter doesn't let the threads run often enough for a good average. Longer an you have to wait too long to see results.

The programs start a monitoring thread that wakes up every *numSeconds* and checks the average time it took to perform the operation. The monitoring thread then calculates a new rate for the number of operations per second. The new rate is the average of the previous average times. Since it is an average of averages, the rate should slowly converge on the maximum rate possible. The monitoring thread then starts up the new rate worker threads that perform the operation once a second.


### Using the tools

1. Install WattDepot following the [installation instructions](installationguide/installation). You need to install version 3.0.0-M9 or above.
2. For performance testing we recommend you reduce the logging to a minimum. In the wattdepot-server.properties file change the *wattdepot-server.loggin.level* to SEVERE.
3. [Start the WattDepot server](installationguide/installation#Run the server). 
4. Define an Organization and User to use in the performance testing. Remember the User's password, we'll use it later.
5. Choose the performance evaluation(s) you want to run, Measurements, Earliest value, Latest value, Value at a given time, or Value from one time to another. 
  * We do not recommend running more than three of these tools at the same time. They can overload your WattDepot server.
  * We recommend you run the Measurements with the Get Values. This simulates a more realistic scenario, getting values while measurements are being added to the Server.  

For example if you run the following:

    $ java -cp wattdepot-3.0.0-M9.jar org.wattdepot.client.http.api.performance.FindGetValueDateThroughput -s http://localhost:8192/ -u cmoore -p secret -o uh -n 15

You should see something like:

    Starting the Apache HTTP client
    Stopping the HTTP client
    Ave get value (date) time = 0.04042766666666667 => 25 gets/sec.
    Setting rate to 25
    Ave get value (date) time = 0.016198 => 62 gets/sec.
    Setting rate to 35
    Ave get value (date) time = 0.012042333333333334 => 83 gets/sec.
    Setting rate to 44
    Ave get value (date) time = 0.011197 => 89 gets/sec.
    Setting rate to 50
    Ave get value (date) time = 0.011237909090909092 => 89 gets/sec.
    Setting rate to 55
    Ave get value (date) time = 0.010520545454545454 => 95 gets/sec.
    Setting rate to 59

Every 15 second two more lines are added to the output.  The output tells you what the average time it to perform the operation was and how many operations/second this results in. The second line tells you what the rate the tool is setting to get the next set of performance data. 

The above data shows that we can perform about 90 get value at a given time operations per second.

6. Record your results.

### Methodology

#### Setting up performance evaluation

5. In a new shell start up the StressTestCollector.
    
    $ java -cp wattdepot-*version*.jar org.wattdepot.client.http.api.collector.StressTestCollector -s *server URI* -u *userId* -p *password* -o *organizationId* -n *numThreads* -m *sleep*

6. Open the 



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



## Results

what the results of prior performance evaluation runs have been.
