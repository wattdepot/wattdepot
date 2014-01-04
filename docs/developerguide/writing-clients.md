# Writing clients

Documents how to write clients.  Could base this on [previous documentation](https://code.google.com/p/wattdepot/wiki/WritingWattDepotClients).

Here is an example of an WattDepot client:

```java
/**
 * WattDepotClientDemo.java
 *
 */
import java.util.Date;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.wattdepot.client.ClientProperties;
import org.wattdepot.client.http.api.WattDepotAdminClient;
import org.wattdepot.client.http.api.WattDepotClient;
import org.wattdepot.common.domainmodel.CollectorProcessDefinition;
import org.wattdepot.common.domainmodel.Depository;
import org.wattdepot.common.domainmodel.MeasurementList;
import org.wattdepot.common.domainmodel.MeasurementType;
import org.wattdepot.common.domainmodel.Organization;
import org.wattdepot.common.domainmodel.Property;
import org.wattdepot.common.domainmodel.Sensor;
import org.wattdepot.common.domainmodel.SensorGroup;
import org.wattdepot.common.domainmodel.SensorList;
import org.wattdepot.common.domainmodel.SensorLocation;
import org.wattdepot.common.domainmodel.UserInfo;
import org.wattdepot.common.domainmodel.UserPassword;
import org.wattdepot.server.WattDepotServer;

/**
 * WattDepotClientDemo - Java usage of WattDepotClient.
 * 
 */
public class WattDepotClientDemo {

  protected static WattDepotServer server;

  /** The handle on the client. */
  private static WattDepotClient client;

  /** The serverUrl. */
  private String serverURL = null;

  public void doSystemAdmin() throws Exception {
    // get server url, username, passwor from system properties
    Properties props = System.getProperties();
    String serverURL = "http://"
        + props.get(ClientProperties.WATTDEPOT_SERVER_HOST) + ":"
        + props.get(ClientProperties.PORT_KEY) + "/";
    String userName = (String)props.get(ClientProperties.USER_NAME);
    String userPassword = (String)props.get(ClientProperties.USER_PASSWORD);
    String orgId = Organization.ADMIN_GROUP_NAME;
    
    // 1. get an admin client connecting to a wattdepot server
    // ? why we need an orgId for AdminClient, should it always be "root" or "admin"
    WattDepotAdminClient admin = new WattDepotAdminClient(serverURL, orgId, userName, userPassword);    
    
    // 2. create an organization
    // ? can we specify slug with the name in the constructor
    Organization org = new Organization("University of Hawaii, Manoa");
    org.setSlug("uh");
    admin.putOrganization(org);

    // 3. create an user
    // ? could we pass in "org" object instead of the org slug
    UserInfo user = new UserInfo("user", "First", "User", "user1@example.com", org.getSlug(),
        new HashSet<Property>());

    // 4. add the user to the organization
    // ? could we pass in "user" object instead of Uid
    // ? could we do org.addUser(user)
    org.getUsers().add(user.getUid());

    // 5. add the user and password to the system
    // ? could we associate user password with the user object, so that we only need
    //    one admin.putUser(user) call
    admin.putUserPassword(new UserPassword(user.getUid(), user.getOrganizationId(), "secret"));
    admin.putUser(user);
    
    // ? is there a way to close the admin client (to release any resource) ?
  }

  public void doOrgAdmin() throws Exception {
    // 1. get a client connect to the wattdepot server
    WattDepotClient client = new WattDepotClient(serverURL, "user", "uh", "secret");

    // 2. create a sensor location
    SensorLocation loc = new SensorLocation("Location 1", new Double(21.294642), new Double(
        -157.812727), new Double(30), "Demo Location", "uh");
    client.putLocation(loc);

    // 3. create sensors
    // ? could we get an "location" object first then use the object instead of slug
    Sensor sensor1 = new Sensor("Sensor 1", "http://sensor1.example.com", loc.getSlug(), "shark",
        loc.getOwnerId());
    client.putSensor(sensor1);

    Sensor sensor2 = new Sensor("Sensor 2", "http://sensor2.example.com", loc.getSlug(), "shark",
        loc.getOwnerId());
    client.putSensor(sensor2);

    // 4. create sensor group with the two sensors
    // ? could we use the sensor object instead of sensor slug
    Set<String> sensors = new HashSet<String>();
    sensors.add(sensor1.getSlug());
    sensors.add(sensor2.getSlug());
    SensorGroup group = new SensorGroup("My Sensor Group", sensors, loc.getOwnerId());
    client.putSensorGroup(group);

    // 5. create an energy depository for the location
    MeasurementType e = null;
    e = client.getMeasurementType("energy-wh");
    Depository energy = new Depository("Location 1 Energy", e, loc.getOwnerId());
    client.putDepository(energy);
      
    // 6. create the collector process definition for the two sensors energy
    // ? could we use the object as parameters instead their slugs
    CollectorProcessDefinition energyCPD1 = new CollectorProcessDefinition(
          "Location Sensor 1 energy", sensor1.getSlug(), 10L, energy.getSlug(), energy.getOwnerId());
    CollectorProcessDefinition energyCPD2 = new CollectorProcessDefinition(
        "Location Sensor 2 energy", sensor2.getSlug(), 10L, energy.getSlug(), energy.getOwnerId());

    client.putCollectorProcessDefinition(energyCPD1);
    client.putCollectorProcessDefinition(energyCPD2);
    
    // 7. is there a way to close the client (to release any resource) ?
  }
  
  public void doOrgDataRetrieval() throws Exception {
    WattDepotClient client = new WattDepotClient(serverURL, "user", "uh", "secret");
    
    // 1. get the depository. 
    Depository dep = client.getDepository("Location 1 Energy");
    
    // 2. get sensor group
    SensorGroup group = client.getSensorGroup("My Sensor Group");
    
    // 3. get the latest measurements
    MeasurementList mList = client.getLatestMeasurements(dep, group);
    
    // 4. get a window measurements
    Date start = new Date();
    Date end = new Date();
    mList = client.getMeasurements(dep, group, start, end);
    
    // 5. get a calculated value
    Date timestamp = new Date();
    Double value = client.getValue(dep, group, timestamp);
    
    // 6. get the latest value
    value = client.getLatestValue(dep, group);
}
```