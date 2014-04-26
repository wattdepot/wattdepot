# WattDepot Getting Started Guide

This guide steps you through installing and configuring the WattDepot server.

## Step 1. Decide

The first step is to decide how you are going to use WattDepot:

* If you are planning on using WattDepot to store your electricity and other associated 
environmental data, but do no development on WattDepot. Then there are two server installation 
options available, local deployment or [Heroku](http://www.heroku.com) deployment.

  * If you choose local deployment ensure that your computer meets the [prerequisites](http://wattdepot.viewdocs.io/wattdepot/installationguide/prerequisites). Then follow the [local installation guide](http://wattdepot.viewdocs.io/wattdepot/installationguide/installation).
  * If you choose Heroku deployment follow the [Deploying to Heroku](http://wattdepot.viewdocs.io/wattdepot/installationguide/deploy-heroku) guide.
  
* If you plan on doing development on WattDepot then:

  1. Ensure your computer meets the [development prerequisites](http://wattdepot.viewdocs.io/wattdepot/developerguide/prerequisites).
  2. [Download](http://wattdepot.viewdocs.io/wattdepot/developerguide/downloading) the WattDepot sources.
  3. [Build](http://wattdepot.viewdocs.io/wattdepot/developerguide/building) the system.
  4. Change directory to the top level of the WattDepot distribution you downloaded in step 2. Run the server:

    $ java -cp wattdepot-*version*.jar org.wattdepot.server.WattDepotServer
    
At the end of step 1, you should have a running WattDepot server. In a browser open the Organization and User administration page.

  * Local installation http://localhost:8192/wattdepot/admin/
  * Heroku installation http://[wattdepot_app_name].herokuapp.com/wattdepot/admin/
  
![Admin Login](http://github.com/wattdepot/wattdepot/blob/master/docs/Admin-password.png)

You will have to provide the Administrator's username and password to view the page.

## Step 2. Define Your Organization and Users


## Step 3. Define Your Depositories, Sensors, Sensor Groups, Collectors, and Measurement Pruning scheme.


## Step 4. Start your Collectors.


## Step 5. Start your Measurment Pruning.