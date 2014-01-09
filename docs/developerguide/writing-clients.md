# Writing clients

Documents how to write clients.  Could base this on [previous documentation](https://code.google.com/p/wattdepot/wiki/WritingWattDepotClients).

Here are some examples of Java clients accessing an WattDepot server:

```java
/**
 * WattDepotSysAdmin - Demo usage of WattDepotSysAdminClient for doing system administration
 *  of WattDepot. 
 *  
 *  The system administration can only be done via the built-in sys admin user called "admin".
 *  The password is specified by the WattDepot server configuration.
 *  
 *  The system administration includes:
 *    1. create or delete organizations
 *    2. create or delete organization users (admin or non-admin)
 */
import java.util.HashSet;
import java.util.Properties;

import org.wattdepot.client.ClientProperties;
import org.wattdepot.common.domainmodel.Organization;
import org.wattdepot.common.domainmodel.Property;

public class WattDepotSysAdmin {

  public void main() throws Exception {

    // get server url, username, passwor from system properties
    Properties props = System.getProperties();
    
    String serverURL = "http://"
        + props.get(ClientProperties.WATTDEPOT_SERVER_HOST) + ":"
        + props.get(ClientProperties.PORT_KEY) + "/";
    
    // PROP 1: create a property called SYS_ADMIN_PASSWORD
    String sysAdminPassword = (String)props.get(ClientProperties.SYS_ADMIN_PASSWORD);
        
    // 1. get an sys admin client connecting to a wattdepot server
    // PROP 2: rename WattDepotAdminClient to WattDepotSysAdminClient
    // PROP 3: since we only have one system admin (root) account, simply the WattDepotAdminClient
    //   constructor to include only serverURL and sysAdminPassword
    WattDepotSysAdminClient admin = new WattDepotSysAdminClient(serverURL, sysAdminPassword);    
    
    // 2. create an organization
    // PROP 4: specify slug with the name and slug in the Organization constructor
    Organization org = new Organization("University of Hawaii, Manoa", "uh");
    admin.putOrganization(org);

    // 3. create an organization admin user
    // PROP 5: encapsulate user password into an User object, the restlet could return the
    //    UserInfo object instead.
    // PROP 6: introduce org admin user with the flag isAdmin
    User uhAdmin = new User("uh_admin", "secret1", "UH", "Admin", "uh_admin@example.com", true,
        new HashSet<Property>());

    // 4. create an organization non-admin user
    User uhUser = new User("uh_user", "secret2", "UH", "User", "uh_user@example.com", false,
        new HashSet<Property>());
    
    // 5. add the users to the organization
    // PROP 7: use addUser() instead of org.getUsers().add() then admin.putUser()
    org.addUser(uhAdmin);
    org.addUser(uhUser);
    
  }
}
```

```java
/**
 * WattDepotOrgAdmin - Demo usage of WattDepotClient for doing organization level administration
 *  of WattDepot. 
 *  
 *  The organization administration can only be done by the org admin user created by the sys admin.
 *  
 *  The org administration includes:
 *  1. create or delete Locations
 *  2. create or delete Sensors
 *  3. create or delete SensorGroups
 *  4. create or delete Depositories
 *  5. create or delete CollectorProcessDefinitions
 */
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.wattdepot.client.ClientProperties;
import org.wattdepot.client.http.api.WattDepotClient;
import org.wattdepot.common.domainmodel.CollectorProcessDefinition;
import org.wattdepot.common.domainmodel.Depository;
import org.wattdepot.common.domainmodel.MeasurementType;
import org.wattdepot.common.domainmodel.Sensor;
import org.wattdepot.common.domainmodel.SensorGroup;
import org.wattdepot.common.domainmodel.SensorLocation;

public class WattDepotOrgAdmin {

  public void main() throws Exception {

    // get server url, username, passwor from system properties
    Properties props = System.getProperties();
    
    String serverURL = "http://"
        + props.get(ClientProperties.WATTDEPOT_SERVER_HOST) + ":"
        + props.get(ClientProperties.PORT_KEY) + "/";
    String userName = (String)props.get(ClientProperties.USER_NAME);
    String userPassword = (String)props.get(ClientProperties.USER_PASSWORD);
    
    // 1. get a client connect to the wattdepot server
    // PROP 1: remove orgId from the wattdepotclient constructor since the system should know the
    //   user's orgid.
    WattDepotClient client = new WattDepotClient(serverURL, userName, userPassword);

    // PROP 1.1 The following org admin operations can only be done by org admin. 
    
    // 2. create a sensor location
    // PROP 2: remove the "ownerID" from the API. It could be deferred from the client
    SensorLocation loc = new SensorLocation("Location 1", new Double(21.294642), new Double(
        -157.812727), new Double(30), "Demo Location");
    client.putLocation(loc);

    // 3. create sensors
    // PROP 3: use "location" object instead of slug, remove ownerID from API
    Sensor sensor1 = new Sensor("Sensor 1", "http://sensor1.example.com", loc, "shark");
    client.putSensor(sensor1);

    Sensor sensor2 = new Sensor("Sensor 2", "http://sensor2.example.com", loc, "shark");
    client.putSensor(sensor2);

    // 4. create sensor group with the two sensors
    // PROP4: use the sensor object instead of sensor slug, remove ownerID from API
    Set<Sensor> sensors = new HashSet<Sensor>();
    sensors.add(sensor1);
    sensors.add(sensor2);
    SensorGroup group = new SensorGroup("My Sensor Group", sensors);
    client.putSensorGroup(group);

    // 5. create an energy depository for the location
    // PROP 5: remove owerID from API
    MeasurementType type = client.getMeasurementType("energy-wh");
    Depository energy = new Depository("Location 1 Energy", type);
    client.putDepository(energy);
      
    // 6. create the collector process definition for the two sensors energy
    // PROP 5: use the objects as parameters instead their slugs, remove ownerID from API
    CollectorProcessDefinition energyCPD1 = new CollectorProcessDefinition(
          "Location Sensor 1 energy", sensor1, 10L, energy);
    CollectorProcessDefinition energyCPD2 = new CollectorProcessDefinition(
        "Location Sensor 2 energy", sensor2, 10L, energy);

    client.putCollectorProcessDefinition(energyCPD1);
    client.putCollectorProcessDefinition(energyCPD2);
    
  }
}
```

```java
/**
 * WattDepotDataQuery.java
 *
 */
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

/**
 * WattDepotDataQuery - Demo usage of WattDepotClient for doing data queries from WattDepot server. 
 *     
 *  The data queries include:
 *  1. check server health
 *  2. list of depositories and a specific depository info
 *  3. list of sensors and a specific sensor
 *  4. list of sensorgroups and a specifc sensorgroup
 *  5. measurements
 *  6. values
 */
public class WattDepotDataQuery {

  public void main() throws Exception {

    // get server url, username, passwor from system properties
    Properties props = System.getProperties();
    
    String serverURL = "http://"
        + props.get(ClientProperties.WATTDEPOT_SERVER_HOST) + ":"
        + props.get(ClientProperties.PORT_KEY) + "/";
    String userName = (String)props.get(ClientProperties.USER_NAME);
    String userPassword = (String)props.get(ClientProperties.USER_PASSWORD);
    
    // 0. get a client connect to the wattdepot server
    // PROP 1: remove orgId from the wattdepotclient constructor since the system should know the
    //   user's orgid.
    WattDepotClient client = new WattDepotClient(serverURL, userName, userPassword);
    
    // 1. check system health
    Boolean status = client.isHealthy();
    
    // 2.1. get all my depository
    DepositoryList dlist = client.getDepositories();
    
    // 2.2. get a specific depository. 
    Depository dep = client.getDepository("Location 1 Energy");
    
    // 3.1. get all my sensors
    SensorList slist = client.getSensors();
    
    // 3.2. get all my sensor groups
    SensorGroupList sglist = client.getSensorGroups();
    
    // 3.3. get specific sensor group
    SensorGroup group = client.getSensorGroup("My Sensor Group");
    
    // 4.1. get the latest measurement
    Measurement m = client.getLatestMeasurement(dep, group);
    
    // 4.2. get the measurements of a specific time range
    Date start = new Date();
    Date end = new Date();
    MeasurementList mlist = client.getMeasurements(dep, group, start, end);
    
    // 5.1. get a calculated value for a timestamp
    Date timestamp = new Date();
    Double value = client.getValue(dep, group, timestamp);
    
    // 5.2. get the latest value
    value = client.getLatestValue(dep, group);
    
  }
}
```
