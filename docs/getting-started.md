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
  
![Admin Login](http://raw.githubusercontent.com/wattdepot/wattdepot/master/docs/Admin-password.png "Figure 1. WattDepot Login")
**Figure 1. WattDepot Login**

You will have to provide the Administrator's username and password to view the page.

## Step 2. Define Your Organization and Users

WattDepot uses organizations to manage the different objects. Every user, sensor, depository, collector, etc. belong to a single organization.  This allows a single WattDepot server to support multiple groups while maintaining their data privacy.

![Organization and User Administration](http://raw.githubusercontent.com/wattdepot/wattdepot/master/docs/wattdepot-admin-start.png "Figure 2. Organization and User Administration")
**Figure 2. Organization and User Administration**

Figure 2 shows the empty Organization and Users administration page.  WattDepot starts with a default public organization.  You can define your own organization by clicking the add button in the upper right corner of the Organizations tab.

![Add Organization UH](http://raw.githubusercontent.com/wattdepot/wattdepot/master/docs/add-org-dialog.png "Figure 3. Add Organization Dialog Box.")
**Figure 3. Add Organization Dialog Box**

Figure 3 shows the definition of the University of Hawaii, Manoa organization.  The organization's id must be unique for all organizations. Clicking Save Changes will create the new organization.

![UH Defined](http://raw.githubusercontent.com/wattdepot/wattdepot/master/docs/uh-defined.png "Figure 4. Organization UH is defined.")
**Figure 4. Organization UH is defined.**

Figure 4 shows the public and University of Hawaii, Manoa organizations. Currently, there are not members of either organization. To create Users click on the Users tab.

![Users Tab](http://raw.githubusercontent.com/wattdepot/wattdepot/master/docs/user-admin.png "Figure 5. Users Tab.")
**Figure 5. Users tab**

Click the add user button, the plus button in the upper right, to open the add user dialog box.

![Add User](http://raw.githubusercontent.com/wattdepot/wattdepot/master/docs/add-user.png "Figure 6. Add User Dialog box.")
**Figure 6. Add User Dialog box.**

Fill in the information about the user and choose the organization they belong to. In this case Cam belongs to the Univerisity of Hawaii, Manoa organization. Remember the user's password, we'll be using it to login later.

![Cam defined](http://raw.githubusercontent.com/wattdepot/wattdepot/master/docs/add-user.png "Figure 7. User Defined.")
**Figure 7. User cmoore defined.**

## Step 3. Define Your Depositories, Sensors, Sensor Groups, Collectors, and Measurement Pruning scheme.
To define the objects in the University of Hawaii, Manoa organization we need to login as a user in that organization. Close your browser and start it again. Open the Administration URL. This time supply the username and password for your user, not the WattDepot administrator.

![Login as a User](http://raw.githubusercontent.com/wattdepot/wattdepot/master/docs/login-as-user.png "Figure 8. Login as a User.")
**Figure 8. Login as a User**

Figure 9. shows the Administration page for an Organization. Here you can define the depositories, sensors, sensor groups, collector processes, and measurment pruning.

![UHM Organization Admin](http://raw.githubusercontent.com/wattdepot/wattdepot/master/docs/organization-admin-start.png "Figure 9. UHM Organization Administration.")
**Figure 9. UHM Organization Admin**

The first thing to do is to define the Depositories. Depositories hold measurements of the same type. To collect Energy data we need to define an Energy depository. To define a new Depository click the Add button on the Depositories tab.  Figure 10 shows the Add Depository Dialog box, filled out for our Energy depository. Choose the id, name and measurement type for your depository. 

WattDepot has fourteen defined measurement types.

* Mass
  * Kilograms (kg)
  * Avoirdupois Pounds (lb)
* Volume
  * Liters (L)
  * Gallons (gal)
* Flow Rate
  * Liters per second (L/s)
  * Gallons per second (gal/s)
* Temperature
  * Degrees Celcius (C)
  * Degrees Fahrenheit (F)
* Power
  * Watts (W)
* Energy
  * Watt hours (Wh)
* Frequency
  * Hertz (Hz)
* Concentration
  * Parts per million (ppm)
* Humidity
  * Percentage (%)
* Cloud Coverage
  * Percentage (%)

![Add Energy Depository Dialog](http://raw.githubusercontent.com/wattdepot/wattdepot/master/docs/add-energy-depository.png "Figure 10. Add Energy Depository Dialog.")
**Figure 10. Add Energy Depository Dialog**

You need a Depository for every type of measurement you are interested in collecting. Figure 11 shows a simple example of the depositories for collecting energy, power, and the temperature.

![Depositories Defined](http://raw.githubusercontent.com/wattdepot/wattdepot/master/docs/depositories-defined.png "Figure 11. Depositories for Energy, Power, and Temperature.")
**Figure 11. Energy, Power and Temperature Depositories**

The second thing to do is to define the Sensors that will be making the measurements. Sensors represent a device that measures (or predicts) a physical phenomena. They have a SensorModel, the type of sensor and protocol used to read the measurement or prediction. WattDepot has four pre-defined sensor models:

1. eGauge, www.egauge.net. eGauge is an affordable, flexible, secure, web-based electric energy and power meter that can measure up to 12 circuits on up to 3-phases (120V−480V, 50−60Hz).
2. Shark, www.electroind.com. The Electro Industries Shark meters use the industrial protocol standard Modbus.
3. NOAA Weather, National Oceanic and Atmospheric Administration's National Weather Service. NOAA provides current weather observations in XML format for the United States.
4. Stress, a stress test model. The stress test sensor model is used by developers to stress WattDepot servers to determine their measurement through put.
 
To define a sensor use the add sensor button. Figure 12 shows the Add Sensor Dialog box. Provide the sensor id, name, URI and choose the sensor's model.  The URI is the uniform resource identifier used to connect to the physical meter to get the measurements or predictions.

## Step 4. Start your Collectors.


## Step 5. Start your Measurement Pruning.