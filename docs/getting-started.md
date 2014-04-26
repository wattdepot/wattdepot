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

![Organization and User Administration](http://raw.githubusercontent.com/wattdepot/wattdepot/master/docs/organization-start.png "Figure 2. Organization and User Administration")
**Figure 2. Organization and User Administration**

## Step 2. Define Your Organization and Users

WattDepot uses organizations to manage the different objects. Every user, sensor, depository, collector, etc. belong to a single organization.  This allows a single WattDepot server to support multiple groups while maintaining their data privacy.

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
**Figure 8. Login as a User***


## Step 4. Start your Collectors.


## Step 5. Start your Measurement Pruning.