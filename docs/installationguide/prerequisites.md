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

Currently, WattDepot is using [Hibernate](http://hibernate.org/) for the persistence layer and the [PostgreSQL] (http://www.postgresql.org/) database for storage. You can configure Hibernate to use other database backends, but we do not have experience with other backends at this time.

If you do not have PostgreSQL installed already, you will have to [download and install it](http://www.postgresql.org/download/) before running WattDepot. We recommend version 9.1 or higher.

If you already have PostgreSQL, one way to check the version is using the command line client tool `psql`:

    $ psql --version
    psql (9.2.4)

However, it is possible to have the `psql` client installed without the actual database server (such as on OS X Mountain Lion). In that case you might see an error like the following:

    $ psql
    psql: could not connect to server: No such file or directory
	Is the server running locally and accepting
	connections on Unix domain socket "/var/pgsql_socket/.s.PGSQL.5432"?

In this case, you should [download PostgreSQL](http://www.postgresql.org/download/) to get the server as well.
