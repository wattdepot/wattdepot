# Writing clients

WattDepot clients can be written in any language that supports HTTP communication.  This page provides example code 
illustrating common types of client operations using Java. 

## Defining organizations and users

One of the important tasks of a WattDepot administrator is to define the set of organizations and users in a server. While this can be accomplished through the web interface, it is also possible programmatically.  The code assumes that 
various values (admin name, admin password, server host name, etc.) are available in the System properties object.

```java
import java.util.Properties;
import org.wattdepot.client.http.api.WattDepotAdminClient;
import org.wattdepot.common.domainmodel.Organization;

public class DefineUhOrganization {

  // Get server details from System properties object.
  private Properties props = System.getProperties();
  private String serverURL = "http://" + props.get("wattdepot.server.hostname") + ":" + 
                              props.get("wattdepot.server.port") + "/";
  private String adminUser = (String)props.get("wattdepot.admin.name");
  private String adminPassword = (String)props.get("wattdepot.admin.password");

  // Note that various subclasses of WattDepotException will be thrown if calls fail.
  public void main() throws WattDepotException {

    // Create an sys admin client connecting to a wattdepot server
    WattDepotAdminClient admin = new WattDepotAdminClient(serverURL, adminUser, adminPassword);    
    
    // Create an organizational representation.
    String uhID = "uh";
    Organization uhOrg = new Organization(uhID, "University of Hawaii, Manoa");

    // Put the organization to the server.
    admin.putOrganization(uhOrg);

    // Create two users representations
    User user1 = new User(uhID, "yxu", "password1");
    User user2 = new User(uhID, "johnson", "password2");

    // Put the users to the server.
    admin.putUser(user1);
    admin.putUser(user2);
  }
}
```

##  Defining an organizational domain model

Once the admin has defined at least one organization and one user, then that user can set up their
organization's domain model.  Here is sample code for creating a simple domain model. 
The username and password are provided through System properties. 

```java
import java.util.Properties;
import org.wattdepot.client.http.api.WattDepotClient;
import org.wattdepot.common.domainmodel.CollectorProcessDefinition;
import org.wattdepot.common.domainmodel.Depository;
import org.wattdepot.common.domainmodel.MeasurementType;
import org.wattdepot.common.domainmodel.Sensor;
import org.wattdepot.common.domainmodel.SensorGroup;
import org.wattdepot.common.domainmodel.SensorLocation;

public class DefineUhDomainModel {

  private Properties props = System.getProperties();
  private String serverURL = "http://" + props.get("wattdepot.server.hostname") + ":" + 
                              props.get("wattdepot.server.port") + "/";
  private String userName = (String)props.get("wattdepot.user.name");
  private String userPassword = (String)props.get("wattdepot.user.password");

  public void main() throws Exception {
    
    // Create a client connection.
    WattDepotClient client = new WattDepotClient(serverURL, "uh", userName, userPassword);

    // Create a sensor location for the POST Building, Room 307.
    SensorLocation post307Loc = new SensorLocation("post307", new Double(21.294642), new Double(-157.812727), new Double(30), "POST Room 307");
    client.putLocation(post307Loc);

    // create two energy sensors for the two rooms in POST 307
    Sensor post307A = new Sensor("post307A", "http://sensor1.example.com", post307Loc, "shark");
    client.putSensor(post307A);
    Sensor post307B = new Sensor("post307B", "http://sensor1.example.com", post307Loc, "shark");
    client.putSensor(post307B);

    // create sensor group representing the total energy for POST 307
    // Use varArgs facility in Java
    SensorGroup post307 = new SensorGroup("post307", "post307A", "post307B");
    client.putSensorGroup(post307);

    // create an energy depository for the organization.
    MeasurementType type = client.getMeasurementType("energy-wh");
    Depository energy = new Depository("energy", type);
    client.putDepository(energy);
      
    // create the collector process definition for the two sensors energy
    CollectorProcessDefinition energyCPD1 = new CollectorProcessDefinition("post307A-energy", "post307A", 10L, energy);
    CollectorProcessDefinition energyCPD2 = new CollectorProcessDefinition("post307B-energy", "post307B", 10L, energy);
    client.putCollectorProcessDefinition(energyCPD1);
    client.putCollectorProcessDefinition(energyCPD2);
    
  }
}
```

## Sending measurements to a server

This next sample program shows how data is sent to a server. Normally, this is done by a collector process, but this sample program sends
some static data simply to illustrate how the API works. 

```java
import java.util.Date;
import java.util.Properties;
import javax.measure.unit.Unit;

import org.wattdepot.client.http.api.WattDepotClient;
import org.wattdepot.common.domainmodel.Depository;
import org.wattdepot.common.domainmodel.Measurement;
import org.wattdepot.common.domainmodel.MeasurementType;

public class WattDepotStoreData {

  private Properties props = System.getProperties();
  private String serverURL = "http://" + props.get("wattdepot.server.hostname") + ":" + 
                              props.get("wattdepot.server.port") + "/";
  private String userName = (String)props.get("wattdepot.user.name");
  private String userPassword = (String)props.get("wattdepot.user.password");

  public void main() throws Exception {

    // Create a client connection.
    WattDepotClient client = new WattDepotClient(serverURL, "uh", userName, userPassword);

    // get the depository for storing the data
    Depository energy = client.getDepository("energy");

    // get the measurement unit from the depository
    MeasurementType measType = energy.getMeasurementType();
    Unit<?> measUnit = Unit.valueOf(measType.getUnits());

    // data for sensorA
    String sensorA = "post307A";    
    Date timestampA = new Date();
    Double valueA = 1001.8;

    // store the sensorA data to the server
    Measurement measurementA = new Measurement(sensorA, timestampA, valueA, measUnit);
    client.putMeasurement(energy, measurementA);

    // data for sensorB
    String sensorB = "post307A";    
    Date timestampB = new Date();
    Double valueB = 2001.8;
    
    // store the sensorB data to the server
    Measurement measurementB = new Measurement(sensorB, timestampB, valueB, measUnit);
    client.putMeasurement(energy, measurementB);    
  }
}
```
## Data queries

This next sample program shows how to retrieve data from a server. Again, it assumes that a user and organization have been defined. 
In addition, it assumes that data has been sent to the server.

```java
import java.util.Date;
import java.util.Properties;
import org.wattdepot.client.ClientProperties;
import org.wattdepot.client.http.api.WattDepotClient;
import org.wattdepot.common.domainmodel.Depository;
import org.wattdepot.common.domainmodel.DepositoryList;
import org.wattdepot.common.domainmodel.Measurement;
import org.wattdepot.common.domainmodel.MeasurementList;
import org.wattdepot.common.domainmodel.SensorGroup;
import org.wattdepot.common.domainmodel.SensorGroupList;
import org.wattdepot.common.domainmodel.SensorList;

public class WattDepotDataQuery {

  private Properties props = System.getProperties();
  private String serverURL = "http://" + props.get("wattdepot.server.hostname") + ":" + 
                              props.get("wattdepot.server.port") + "/";
  private String userName = (String)props.get("wattdepot.user.name");
  private String userPassword = (String)props.get("wattdepot.user.password");

  public void main() throws Exception {

    // Create a client instance.
    WattDepotClient client = new WattDepotClient(serverURL, "uh", userName, userPassword);
    
    // Check system health
    Boolean status = client.isHealthy();
    
    // Get a list of depositories.
    DepositoryList dlist = client.getDepositories();
    
    // Get a specific depository.
    Depository dep = client.getDepository("energy");
    
    // Get all sensors.
    SensorList slist = client.getSensors();
    
    // Get all sensor groups.
    SensorGroupList sglist = client.getSensorGroups();
    
    // Get a specific sensor group.
    SensorGroup group = client.getSensorGroup("post307");
    
    // Get the latest measurement from the sensor group.
    Measurement m = client.getLatestMeasurement(dep, group);
    
    // Get measurements within a specific time range from the sensor group
    Date start = new Date();
    Date end = new Date();
    MeasurementList mlist = client.getMeasurements(dep, group, start, end);
    
    // Get a calculated value for a timestamp from the sensor group
    Date timestamp = new Date();
    Double value = client.getValue(dep, group, timestamp);
    
    // Get the latest value from the sensor group
    value = client.getLatestValue(dep, group);
  }
}
```

## WattDepot 2 Client Programs

For comparison, [this page](https://code.google.com/p/wattdepot/wiki/WritingWattDepotClients) provides examples of how to write clients in WattDepot 2. 



      
      
      

      
