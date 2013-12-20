# Deploying WattDepot to Heroku

This document explains how to deploy the WattDepot server to Heroku.

## Requirements

You will need to install Heroku toolbelt and set up your Heroku account.

If you have not use Heroku before, sign up for an account and install the Heroku toolbelt 
following the instructions in the [Heroku Cheat Sheet](http://devcenter.heroku.com/articles/quickstart).
This involves:
  * Signing up with the Heroku service
  * Install the Heroku Toolbelt (provides the "git" and "heroku" commands).
  * Logging in to Heroku.

You must tell Heroku about your SSH keys. Follow
https://devcenter.heroku.com/articles/keys to upload your keys to Heroku.

## Download the Wattdepot source

To download the Wattdepot source, type the following::

    $ git clone git://github.com/wattdepot/wattdepot.git

This will create a directory called "wattdepot" containing the source code
for the system.

## Create the Heroku application

Once the wattdepot source is downloaded, you can create a Heroku app for wattdepot:

    $ cd wattdepot
    $ heroku create <wattdepot_app_name>

## Setup environment variables

To deploy Wattdepot on Heroku, you must define several environment variables that will be
used by the instance on Heroku:

    $ heroku config:set WATTDEPOT_ADMIN_NAME=<admin name>
    $ heroku config:set WATTDEPOT_ADMIN_PASSWORD=<admin password>
    
## Deploy to Heroku
Here is the command to deploy to Heroku:

    $ git push heroku

This step will deploy the wattdepot system to Heroku and initialize the wattdepot system. After the initialization, you need to set the following Heroku environment variable to prevent the system
to run initialization again (This step may be removed in the future release):

    $ heroku config:set WATTDEPOT_DB_UPDATE=update
        
## Access the server admin interface

You can access the server by using your browser to open the url of your Heroku app: 

    http://[wattdepot_app_name].herokuapp.com/wattdepot/admin

## Update the Heroku instance

You can update the deployed Heroku instance by running the following command:

    $ git push heroku
