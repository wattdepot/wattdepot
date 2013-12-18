# Defining WattDepot entities

Now that the system is running, the first step is to login to the web interface and define the 
sensors, collectors, etc. Open your favorite browser and navigate to the WattDepot Server URL 
(http://wattdepot-uh.herokuapp.com/wattdepot/admin/ or http://localhost:8192/wattdepot/admin/).
You will see the login dialog: 

<img class="img-responsive" src="https://raw.github.com/wattdepot/wattdepot/master/docs/userguide/LoginDialog.png">

Enter your login information.  

## Define Users and User Groups

If you logged in as an administrator you will see the admin user interface.

<img class="img-responsive" src="https://raw.github.com/wattdepot/wattdepot/master/docs/userguide/WattDepotAdminUI.png">. 

The Admin UI is the only place to define new Users and UserGroups. Click the Add User button to add new Users.

<img class="img-responsive" src="https://raw.github.com/wattdepot/wattdepot/master/docs/userguide/AddUserDialog.png">

Once you have your users defined you can create new UserGroups. Click the Add User Group button to create groups.

<img class="img-responsive" src="https://raw.github.com/wattdepot/wattdepot/master/docs/userguide/AddUserGroupDialog.png">

Select the users you want in the group from the list of defined users.  Users can only be in one group.

Once your users and user groups are defined, you can close your browser and login as a regular user 
or just navigate to the UserGroup Admin UI **/wattdepot/{group-id}/**. 

<img class="img-responsive" src="https://raw.github.com/wattdepot/wattdepot/master/docs/userguide/GroupUHAdminUI.png">

*Note* The group you are logged in under is displayed on the right-hand side of the Navigation Bar. 

## Define Depositories

WattDepot Depositories hold Measurements of a single MeasurementType. To define the Depositories you 
are interested in Click on the Depositories tab. To create a new Depository Click the Add Depository 
button. You will see the Add/Edit Depository Dialog.

<img class="img-responsive" src="https://raw.github.com/wattdepot/wattdepot/master/docs/userguide/GroupUHAdminUI.png">

Choose a descriptive name and the MeasurementType. Once added the new Depository will show up in the Depositories table.

<img class="img-responsive" src="https://raw.github.com/wattdepot/wattdepot/master/docs/userguide/DepositoryDefined.png">
 
## Define Sensors
Sensors represent physical sensors in the environment that collect measurements about the environment. 
They have SensorLocations, where they are located in the environment and SensoModels, what kind of 
sensor they are.

### Define the Locations
The first step to defining a Sensor is to define its location. Click on the Sensor Locations label to open
the Sensor Locations table.  All the defined locations are shown in the table. To add a new Location click
the Add a Sensor Location button. 

<img class="img-responsive" src="https://raw.github.com/wattdepot/wattdepot/master/docs/userguide/AddLocationDialog.png">

Provide a good name, the location's latitude, longitude and altitude, a description of the location. Press Save.

### Optionally, Define new Sensor Models
WattDepot has some pre-defined Sensor Models. If you need to define your own new Sensor Model use the Add Sensor Model button.
 
<img class="img-responsive" src="https://raw.github.com/wattdepot/wattdepot/master/docs/userguide/AddModelDialog.png">

Once you have the locations and models defined you can create your new sensor. Click the Add Sensor button.

<img class="img-responsive" src="https://raw.github.com/wattdepot/wattdepot/master/docs/userguide/AddSensorDialog.png">

Name the Sensor, provide the URL to the sensor, choose the location, and model. Once the sensor is saved it will appear 
in the Sensors table.

<img class="img-responsive" src="https://raw.github.com/wattdepot/wattdepot/master/docs/userguide/SensorDefined.png">

## Define Collector Metadata

Collector Metadata tells the Collector processes which sensor to poll, how often to poll the sensor 
and which depository to store the measurements in. 

<img class="img-responsive" src="https://raw.github.com/wattdepot/wattdepot/master/docs/userguide/AddMetadataDialog.png">

Name the Collector, names must be unique. Choose the sensor, add a polling interval in seconds, choose the depository, click Save.

<img class="img-responsive" src="https://raw.github.com/wattdepot/wattdepot/master/docs/userguide/MetadataDefined.png">


