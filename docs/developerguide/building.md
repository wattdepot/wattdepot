# Building WattDepot

WattDepot uses [Maven 3](http://maven.apache.org/index.html) as a build system. Maven is configured with the pom.xml file found at the root of the WattDepot source directory. Maven has a number of lifecycle phases - the most useful in this project are clean, compile, test, package, verify, and site.

  * `mvn clean` will delete the target directory.
  * `mvn compile` will compile the project.
  * `mvn test` will compile the project, then run JUnit and QA tests. (Note: the postgres storage implementation requires you to setup the wattdepot-server.properties file in order to supply the wattdepot-server.db.username property.)
    * FindBugs
    * Checkstyle 
    * PMD
    * JavaDocs are created
  * `mvn package` will compile the project, run the tests, and then create .jar for a distribution
    * The .jar file
    * The POM zips everything into a distribution
  * `mvn verify` runs the compile, test and package phases and ensures the result is valid
  * `mvn site` executes FindBugs, Checkstyle, PMD, and JavaDocs, then presents the results in an easy to read website (located in target/site)

Rather than executing a whole phase, you can also run an individual goal. For example, to run FindBugs without doing anything else, use:

    $ mvn findbugs:check
    
The same works for `mvn pmd:check` and `mvn checkstyle:check`. However, note that this does not recompile the project. If you make changes to the source code, you should run `mvn compile` before running any other checks. 

Another useful tip is adding properties to Maven calls with the `-D` argument. For example, to run the package phase without running JUnit tests, use:
    
    $mvn package -DskipTests
    
This can be a great time saver if you've just run the test phase and now want to package. These properties can also affect the running of WattDepot. Say you want to test multiple configurations without changing the wattdepot-server.properties file between each test. Try:

    $ mvn test -Dwattdepot-server.db.username=test

## Creating a Distribution

To create a distribution, run `mvn package`. It will create a jar file under the "target" directory.



