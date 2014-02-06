# Performance Evaluation

## Setting up performance evaluation

1. Install WattDepot following the [installation instructions](installationguide/installation). You need to install version 3.0.0-M8 or above.
2. For performance testing we recommend you reduce the logging to a minimum. In the wattdepot-server.properties file change the *wattdepot-server.loggin.level* to SEVERE.
3. [Start the WattDepot server](installationguide/installation#Run the server). 
4. Define an Organization and User to use in the performance testing. Remember the User's password, we'll use it later.
5. In a new shell start up the StressTestCollector.
    
    $ java -cp wattdepot-*version*.jar org.wattdepot.client.http.api.collector.StressTestCollector -s *server URI* -u *userId* -p *password* -o *organizationId* -n *numThreads* -m *sleep*

6. Open the 

## Results

what the results of prior performance evaluation runs have been.
