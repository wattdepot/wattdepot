# Installing the system

This document explains how to install the WattDepot server on a local computer server.

## Requirements

To run the WattDepot server, you just need a computer running Java 1.7 or later. If you do not
already have Java installed, you can download it. Java is not included on Mac OS X 10.7 (Lion), so
you will be prompted to download it after trying to run a Java program for the first time. You can
initiate this by typing `java -version` in a Terminal window.

You can run the WattDepot server from your own account, but you may want to create a separate
wattdepot account and run it from that, to keep things separate.

## Download WattDepot

Download the latest release of WattDepot from the [releases]
(https://github.com/wattdepot/wattdepot/releases) page.

## Set up WattDepot server home directory

WattDepot stores all its files in a server home directory. By default, this will be a *.wattdepot3*
directory in your home directory. If you want to create the *.wattdepot3* directory elsewhere, set
the `wattdepot.user.home` Java property to the desired parent directory. The name *.wattdepot3*
directory name itself is not configurable.

Within the server home directory are subdirectories that store all the data for a particular server
instance. The default subdirectory is server, but this can be changed on the command line with the
-d argument. This allows multiple WattDepot server instances to run in parallel, each writing to a
different subdirectory. Create this directory:

    mkdir ~/.wattdepot3
    mkdir ~/.wattdepot3/server

## Customize the wattdepot-server.properties

The server reads properties on launch from a file named `wattdepot-server.properties` in the server
subdirectory. A default properties file can be found in the wattdepot files you downloaded as
`src/main/resources/wattdepot-server.properties`. Copy that file to your new server
subdirectory (~/.wattdepot3/server if you are using the defaults), and rename it to
`wattdepot-server.properties`.

After the file is copied into place, open it in your favorite text editor and change or uncomment
any lines you want. At a minimum, you will want to change the default administrator username and
password.

## Run the server

Change to the top level of the wattdepot distribution you downloaded. Run the server:

    $ java -cp wattdepot-*version*.jar org.wattdepot.server.WattDepotServer
    
