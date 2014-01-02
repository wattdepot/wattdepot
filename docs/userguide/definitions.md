# Defining the WattDepot Domain Model

Once a WattDepot server is installed and running, the next step is to login to the server's web interface and define the 
[domain model](overview/domainmodel).

To do this, navigate to &lt;host&gt;/wattdepot/admin/.  This should display the login dialog: 

<a href="https://raw.github.com/wattdepot/wattdepot/master/docs/userguide/LoginDialog.png">
<img width="400" src="https://raw.github.com/wattdepot/wattdepot/master/docs/userguide/LoginDialog.png">
</a>

Enter your user name and password.

## Define Users and User Groups (Admin only)

If you logged in as an administrator you will see the admin user interface.

<a href="https://raw.github.com/wattdepot/wattdepot/master/docs/userguide/WattDepotAdminUI.png">
<img width="400" src="https://raw.github.com/wattdepot/wattdepot/master/docs/userguide/WattDepotAdminUI.png">
</a>

The Admin UI is the only place to define new Users and UserGroups. Click the Add User button to add new Users.

<a href="https://raw.github.com/wattdepot/wattdepot/master/docs/userguide/AddUserDialog.png">
<img width="400" src="https://raw.github.com/wattdepot/wattdepot/master/docs/userguide/AddUserDialog.png">
</a>

Once you have your users defined you can create new UserGroups. Click the Add User Group button to create groups.

<a href="https://raw.github.com/wattdepot/wattdepot/master/docs/userguide/AddUserGroupDialog.png">
<img width="400" src="https://raw.github.com/wattdepot/wattdepot/master/docs/userguide/AddUserGroupDialog.png">
</a>

Select the users you want in the group from the list of defined users.  Users can only be in one group.

Once your users and user groups are defined, you can close your browser and login as a regular user 
or just navigate to the UserGroup Admin UI **/wattdepot/{group-id}/**. 

<a href="https://raw.github.com/wattdepot/wattdepot/master/docs/userguide/GroupUHAdminUI.png">
<img width="400" src="https://raw.github.com/wattdepot/wattdepot/master/docs/userguide/GroupUHAdminUI.png">
</a>

*Note* The group you are logged in under is displayed on the right-hand side of the Navigation Bar. 

## Define Depositories

WattDepot Depositories hold Measurements of a single MeasurementType. To define the Depositories you 
are interested in Click on the Depositories tab. To create a new Depository, click the Add Depository 
button. You will see the Add/Edit Depository Dialog.

<a href="https://raw.github.com/wattdepot/wattdepot/master/docs/userguide/GroupUHAdminUI.png">
<img width="400" src="https://raw.github.com/wattdepot/wattdepot/master/docs/userguide/GroupUHAdminUI.png">
</a>

Choose a descriptive name and the MeasurementType. Once added the new Depository will show up in the Depositories table.

<a href="https://raw.github.com/wattdepot/wattdepot/master/docs/userguide/DepositoryDefined.png">
<img width="400" src="https://raw.github.com/wattdepot/wattdepot/master/docs/userguide/DepositoryDefined.png">
</a>

 
## Define Sensors
Sensors represent physical sensors in the environment that collect measurements about the environment. 
They have SensorLocations, where they are located in the environment and SensoModels, what kind of 
sensor they are.

### Define the Locations
The first step to defining a Sensor is to define its location. Click on the Sensor Locations label to open
the Sensor Locations table.  All the defined locations are shown in the table. To add a new Location click
the Add a Sensor Location button. 

<a href="https://raw.github.com/wattdepot/wattdepot/master/docs/userguide/AddLocationDialog.png">
<img width="400" src="https://raw.github.com/wattdepot/wattdepot/master/docs/userguide/AddLocationDialog.png">
</a>

Provide a good name, the location's latitude, longitude and altitude, a description of the location. Press Save.

### Optionally, Define new Sensor Models
WattDepot has some pre-defined Sensor Models. If you need to define your own new Sensor Model use the Add Sensor Model button.
 
<a href="https://raw.github.com/wattdepot/wattdepot/master/docs/userguide/AddModelDialog.png">
<img width="400" src="https://raw.github.com/wattdepot/wattdepot/master/docs/userguide/AddModelDialog.png">
</a>

Once you have the locations and models defined you can create your new sensor. Click the Add Sensor button.

<a href="https://raw.github.com/wattdepot/wattdepot/master/docs/userguide/AddSensorDialog.png">
<img width="400" src="https://raw.github.com/wattdepot/wattdepot/master/docs/userguide/AddSensorDialog.png">
</a>

Name the Sensor, provide the URL to the sensor, choose the location, and model. Once the sensor is saved it will appear 
in the Sensors table.

<a href="https://raw.github.com/wattdepot/wattdepot/master/docs/userguide/SensorDefined.png">
<img width="400" src="https://raw.github.com/wattdepot/wattdepot/master/docs/userguide/SensorDefined.png">
</a>

## Define Collector Process Definition

Collector Process Definition tells the Collector processes which sensor to poll, how often to poll the sensor 
and which depository to store the measurements in. 

<a href="https://raw.github.com/wattdepot/wattdepot/master/docs/userguide/AddMetadataDialog.png">
<img width="400" src="https://raw.github.com/wattdepot/wattdepot/master/docs/userguide/AddMetadataDialog.png">
</a>

Name the Collector, names must be unique. Choose the sensor, add a polling interval in seconds, choose the depository, click Save.

<a href="https://raw.github.com/wattdepot/wattdepot/master/docs/userguide/MetadataDefined.png">
<img width="400" src="https://raw.github.com/wattdepot/wattdepot/master/docs/userguide/MetadataDefined.png">
</a>




