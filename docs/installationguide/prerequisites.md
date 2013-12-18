# Environment prerequisites

## Operating System

The core development team has developed and run WattDepot on OS X, Windows 7, and Linux, so any of those platforms should be fine for development.

## Java

WattDepot requires [Java Platform Standard Edition] (http://www.oracle.com/us/technologies/java/standard-edition/overview/index.html) version 1.7 or above. If do not have Java installed you can get it from the previous link. Note that you will want the JDK (Java Development Kit) rather than the JRE (Java Runtime Environment) since you will be developing. To check the version of Java installed on your computer type `java -version` from the command line.

    $ java -version
    java version "1.7.0_40"
    Java(TM) SE Runtime Environment (build 1.7.0_40-b43)
    Java HotSpot(TM) 64-Bit Server VM (build 24.0-b56, mixed mode)

## PostgreSQL

Currently, WattDepot is using Hibernate for the persistence layer and the [PostgreSQL] (http://www.postgresql.org/) database for storage. We recommend version 9.1 or higher. You can configure Hibernate to use other database backends, but we do not have experience with other backends at this time. To check the version of PostgreSQL type `psql -version` at the command line.

    $ psql --version
    psql (9.2.4)

This actually checks the version of the PostgreSQL client.

