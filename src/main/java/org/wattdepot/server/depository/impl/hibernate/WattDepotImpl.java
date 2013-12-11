/**
 * WattDepotImpl.java This file is part of WattDepot.
 *
 * Copyright (C) 2013  Cam Moore
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.wattdepot.server.depository.impl.hibernate;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.measure.unit.Unit;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.wattdepot.common.domainmodel.CollectorMetaData;
import org.wattdepot.common.domainmodel.Depository;
import org.wattdepot.common.domainmodel.Measurement;
import org.wattdepot.common.domainmodel.MeasurementType;
import org.wattdepot.common.domainmodel.Property;
import org.wattdepot.common.domainmodel.Sensor;
import org.wattdepot.common.domainmodel.SensorGroup;
import org.wattdepot.common.domainmodel.SensorLocation;
import org.wattdepot.common.domainmodel.SensorModel;
import org.wattdepot.common.domainmodel.UserGroup;
import org.wattdepot.common.domainmodel.UserInfo;
import org.wattdepot.common.domainmodel.UserPassword;
import org.wattdepot.common.exception.IdNotFoundException;
import org.wattdepot.common.exception.MissMatchedOwnerException;
import org.wattdepot.common.exception.UniqueIdException;
import org.wattdepot.common.util.SensorModelHelper;
import org.wattdepot.common.util.Slug;
import org.wattdepot.server.ServerProperties;
import org.wattdepot.server.WattDepot;

/**
 * WattDepotImpl - Hibernate implementation of the WattDepot abstract class.
 * 
 * @author Cam Moore
 * 
 */
public class WattDepotImpl extends WattDepot {

  private int sessionOpen = 0;
  private int sessionClose = 0;

  /**
   * Creates a new WattDepotImpl instance with the given ServerProperties.
   * 
   * @param properties
   *          The ServerProperties.
   */
  public WattDepotImpl(ServerProperties properties) {
    super();
    setServerProperties(properties);
    UserPassword adminPassword = getUserPassword(UserInfo.ADMIN.getId());
    if (getSessionClose() != getSessionOpen()) {
      throw new RuntimeException("opens and closed mismatched.");
    }
    if (adminPassword == null) {
      try {
        defineUserPassword(UserPassword.ADMIN.getId(), UserPassword.ADMIN.getPlainText());
        if (getSessionClose() != getSessionOpen()) {
          throw new RuntimeException("opens and closed mismatched.");
        }
      }
      catch (UniqueIdException e1) {
        // what do we do here?
        e1.printStackTrace();
      }
    }
    else {
      updateUserPassword(adminPassword);
      if (getSessionClose() != getSessionOpen()) {
        throw new RuntimeException("opens and closed mismatched.");
      }
    }
    UserGroup admin = getUserGroup(UserGroup.ADMIN_GROUP.getId());
    if (getSessionClose() != getSessionOpen()) {
      throw new RuntimeException("opens and closed mismatched.");
    }
    if (admin == null) {
      try {
        defineUserGroup(UserGroup.ADMIN_GROUP.getId(), UserGroup.ADMIN_GROUP.getUsers());
        if (getSessionClose() != getSessionOpen()) {
          throw new RuntimeException("opens and closed mismatched.");
        }
      }
      catch (UniqueIdException e) {
        // what do we do here?
        e.printStackTrace();
      }
    }
    else {
      updateUserGroup(admin);
      if (getSessionClose() != getSessionOpen()) {
        throw new RuntimeException("opens and closed mismatched.");
      }
    }
    UserInfo adminUser = getUser(UserInfo.ADMIN.getId());
    if (getSessionClose() != getSessionOpen()) {
      throw new RuntimeException("opens and closed mismatched.");
    }
    if (adminUser == null) {
      try {
        defineUserInfo(UserInfo.ADMIN.getId(), UserInfo.ADMIN.getFirstName(),
            UserInfo.ADMIN.getLastName(), UserInfo.ADMIN.getEmail(), UserInfo.ADMIN.getAdmin(),
            UserInfo.ADMIN.getProperties());
        if (getSessionClose() != getSessionOpen()) {
          throw new RuntimeException("opens and closed mismatched.");
        }
      }
      catch (UniqueIdException e) {
        // what do we do here?
        e.printStackTrace();
      }
    }
    else {
      updateUserInfo(UserInfo.ADMIN);
      if (getSessionClose() != getSessionOpen()) {
        throw new RuntimeException("opens and closed mismatched.");
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#defineLocation(java.lang.String,
   * java.lang.Double, java.lang.Double, java.lang.Double, java.lang.String,
   * org.wattdepot.datamodel.UserGroup)
   */
  @Override
  public SensorLocation defineLocation(String id, Double latitude, Double longitude,
      Double altitude, String description, UserGroup owner) throws UniqueIdException {
    SensorLocation l = null;
    try {
      l = getLocation(id, UserGroup.ADMIN_GROUP_NAME);
    }
    catch (MissMatchedOwnerException e) {
      // can't happen
      e.printStackTrace();
    }
    if (l != null) {
      throw new UniqueIdException(id + " is already a Location id.");
    }
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    l = new SensorLocation(id, latitude, longitude, altitude, description, owner);
    session.save(l);
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    return l;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.server.WattDepot#defineMeasurementType(java.lang.String,
   * java.lang.String)
   */
  @Override
  public MeasurementType defineMeasurementType(String name, String units) throws UniqueIdException {
    String slug = Slug.slugify(name);
    MeasurementType mt = null;
    mt = getMeasurementType(slug);
    if (mt != null) {
      throw new UniqueIdException(slug + " is already a MeasurementType id.");
    }
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    Unit<?> unit = Unit.valueOf(units);
    mt = new MeasurementType(name, unit);
    session.save(mt);
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    return mt;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#defineSensor(java.lang.String,
   * java.lang.String, org.wattdepot.datamodel.Location,
   * org.wattdepot.datamodel.SensorModel, org.wattdepot.datamodel.UserGroup)
   */
  @Override
  public Sensor defineSensor(String id, String uri, SensorLocation l, SensorModel sm,
      UserGroup owner) throws UniqueIdException, MissMatchedOwnerException {
    if (!owner.equals(l.getOwner())) {
      throw new MissMatchedOwnerException(owner.getId() + " does not match location's.");
    }
    Sensor s = null;
    s = getSensor(id, UserGroup.ADMIN_GROUP_NAME);
    if (s != null) {
      throw new UniqueIdException(id + " is already a Sensor id.");
    }
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    s = new Sensor(id, uri, l, sm, owner);
    saveSensor(session, s);
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    return s;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#defineSensorGroup(java.lang.String,
   * java.util.List, org.wattdepot.datamodel.UserGroup)
   */
  @Override
  public SensorGroup defineSensorGroup(String id, Set<Sensor> sensors, UserGroup owner)
      throws UniqueIdException, MissMatchedOwnerException {
    for (Sensor s : sensors) {
      if (!owner.equals(s.getOwner())) {
        throw new MissMatchedOwnerException(owner.getId() + " is not the owner of all the sensors.");
      }
    }
    SensorGroup sg = null;
    sg = getSensorGroup(id, UserGroup.ADMIN_GROUP_NAME);
    if (sg != null) {
      throw new UniqueIdException(id + " is already a SensorGroup id.");
    }
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    sg = new SensorGroup(id, sensors, owner);
    for (Sensor s : sensors) {
      saveSensor(session, s);
    }
    session.save(sg);
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    return sg;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#defineSensorModel(java.lang.String,
   * java.lang.String, java.lang.String, java.lang.String,
   * org.wattdepot.datamodel.UserGroup)
   */
  @Override
  public SensorModel defineSensorModel(String id, String protocol, String type, String version)
      throws UniqueIdException {
    SensorModel sm = null;
    sm = getSensorModel(id);
    if (sm != null) {
      throw new UniqueIdException(id + " is already a SensorModel id.");
    }
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    sm = new SensorModel(id, protocol, type, version);
    session.save(sm);
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    return sm;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.server.WattDepot#defineCollectorMetaData(java.lang.String,
   * org.wattdepot.datamodel.Sensor, java.lang.Long, java.lang.String,
   * org.wattdepot.datamodel.UserGroup)
   */
  @Override
  public CollectorMetaData defineCollectorMetaData(String id, Sensor sensor, Long pollingInterval,
      String depositoryId, UserGroup owner) throws UniqueIdException, MissMatchedOwnerException {
    if (!owner.equals(sensor.getOwner())) {
      throw new MissMatchedOwnerException(owner.getId() + " does not own the sensor "
          + sensor.getId());
    }
    CollectorMetaData sp = null;
    sp = getCollectorMetaData(id, UserGroup.ADMIN_GROUP_NAME);
    if (sp != null) {
      throw new UniqueIdException(id + " is already a SensorModel id.");
    }
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    sp = new CollectorMetaData(id, sensor, pollingInterval, depositoryId, owner);
    saveSensor(session, sensor);
    session.save(sp);
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    return sp;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#defineUserGroup(java.lang.String,
   * java.util.List)
   */
  @Override
  public UserGroup defineUserGroup(String id, Set<UserInfo> users) throws UniqueIdException {
    UserGroup g = getUserGroup(id);
    if (g != null) {
      throw new UniqueIdException(id + " is already a UserGroup id.");
    }
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    for (UserInfo u : users) {
      for (Property p : u.getProperties()) {
        session.saveOrUpdate(p);
      }
      session.saveOrUpdate(u);
    }
    g = new UserGroup(id, users);
    session.saveOrUpdate(g);
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    return g;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#defineUserInfo(java.lang.String,
   * java.lang.String, java.lang.String, java.lang.String, java.lang.String,
   * java.lang.Boolean, java.util.Set)
   */
  @Override
  public UserInfo defineUserInfo(String id, String firstName, String lastName, String email,
      Boolean admin, Set<Property> properties) throws UniqueIdException {
    UserInfo u = getUser(id);
    if (u != null) {
      throw new UniqueIdException(id + " is already a UserInfo id.");
    }
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    u = new UserInfo(id, firstName, lastName, email, admin, properties);
    session.saveOrUpdate(u);
    for (Property p : properties) {
      session.saveOrUpdate(p);
    }
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    return u;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#defineUserPassword(java.lang.String,
   * java.lang.String)
   */
  @Override
  public UserPassword defineUserPassword(String id, String password) throws UniqueIdException {
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    UserPassword up = new UserPassword(id, password);
    session.saveOrUpdate(up);
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    return up;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#defineWattDepository(java.lang.String,
   * java.lang.String, java.lang.String, org.wattdepot.datamodel.UserGroup)
   */
  @Override
  public Depository defineWattDepository(String name, MeasurementType measurementType,
      UserGroup owner) throws UniqueIdException {
    Depository d = null;
    try {
      d = getWattDeposiory(name, owner.getId());
    }
    catch (MissMatchedOwnerException e) {
      throw new UniqueIdException(name + " is used by another owner.");
    }
    if (d != null) {
      throw new UniqueIdException(name + " is already a Depository name.");
    }
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    d = new DepositoryImpl(name, measurementType, owner);
    session.saveOrUpdate(d);
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    return d;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#deleteLocation(java.lang.String,
   * java.lang.String)
   */
  @Override
  public void deleteLocation(String id, String groupId) throws IdNotFoundException,
      MissMatchedOwnerException {
    SensorLocation l = getLocation(id, groupId);
    if (l != null) {
      Session session = Manager.getFactory(getServerProperties()).openSession();
      sessionOpen++;
      session.beginTransaction();
      session.delete(l);
      session.getTransaction().commit();
      session.close();
      sessionClose++;
    }
    else {
      throw new IdNotFoundException(id + " was not found for owner " + groupId);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.server.WattDepot#deleteMeasurementType(java.lang.String)
   */
  @Override
  public void deleteMeasurementType(String slug) throws IdNotFoundException {
    MeasurementType mt = getMeasurementType(slug);
    if (mt != null) {
      Session session = Manager.getFactory(getServerProperties()).openSession();
      sessionOpen++;
      session.beginTransaction();
      session.delete(mt);
      session.getTransaction().commit();
      session.close();
      sessionClose++;
    }
    else {
      throw new IdNotFoundException(slug + " was not found.");
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#deleteSensor(java.lang.String,
   * java.lang.String)
   */
  @Override
  public void deleteSensor(String id, String groupId) throws IdNotFoundException,
      MissMatchedOwnerException {
    Sensor s = getSensor(id, groupId);
    if (s != null) {
      Session session = Manager.getFactory(getServerProperties()).openSession();
      sessionOpen++;
      session.beginTransaction();
      session.delete(s);
      session.getTransaction().commit();
      session.close();
      sessionClose++;
    }
    else {
      throw new IdNotFoundException(id + " was not found for owner " + groupId);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#deleteSensorGroup(java.lang.String,
   * java.lang.String)
   */
  @Override
  public void deleteSensorGroup(String id, String groupId) throws IdNotFoundException,
      MissMatchedOwnerException {
    SensorGroup s = getSensorGroup(id, groupId);
    if (s != null) {
      Session session = Manager.getFactory(getServerProperties()).openSession();
      sessionOpen++;
      session.beginTransaction();
      session.delete(s);
      session.getTransaction().commit();
      session.close();
      sessionClose++;
    }
    else {
      throw new IdNotFoundException(id + " was not found for owner " + groupId);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#deleteSensorModel(java.lang.String,
   * java.lang.String)
   */
  @Override
  public void deleteSensorModel(String id) throws IdNotFoundException {
    SensorModel s = getSensorModel(id);
    if (s != null) {
      Session session = Manager.getFactory(getServerProperties()).openSession();
      sessionOpen++;
      session.beginTransaction();
      session.delete(s);
      session.getTransaction().commit();
      session.close();
      sessionClose++;
    }
    else {
      throw new IdNotFoundException(id + " was not found.");
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.server.WattDepot#deleteCollectorMetaData(java.lang.String,
   * java.lang.String)
   */
  @Override
  public void deleteCollectorMetaData(String id, String groupId) throws IdNotFoundException,
      MissMatchedOwnerException {
    CollectorMetaData s = getCollectorMetaData(id, groupId);
    if (s != null) {
      Session session = Manager.getFactory(getServerProperties()).openSession();
      sessionOpen++;
      session.beginTransaction();
      session.delete(s);
      session.getTransaction().commit();
      session.close();
      sessionClose++;
    }
    else {
      throw new IdNotFoundException(id + " was not found for owner " + groupId);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#deleteUser(java.lang.String)
   */
  @Override
  public void deleteUser(String id) throws IdNotFoundException {
    UserInfo u = getUser(id);
    if (u == null) {
      throw new IdNotFoundException(id + " is not a defined user id.");
    }
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    session.delete(u);
    session.getTransaction().commit();
    session.close();
    sessionClose++;
  }

  // /**
  // * Deletes all the objects that have group as their owner.
  // *
  // * @param session
  // * The Session, a transaction must be in progress.
  // * @param group
  // * The UserGroup to delete.
  // */
  // private void deleteUserGroup(Session session, UserGroup group) {
  // for (CollectorMetaData sp : getCollectorMetaDatas(session, group.getId()))
  // {
  // session.delete(sp);
  // }
  // for (SensorGroup sg : getSensorGroups(session, group.getId())) {
  // session.delete(sg);
  // }
  // for (Depository d : getWattDepositories(session, group.getId())) {
  // for (Sensor s : d.listSensors(session)) {
  // for (Measurement m : d.getMeasurements(session, s)) {
  // session.delete(m);
  // }
  // }
  // session.delete(d);
  // }
  // for (Sensor s : getSensors(session, group.getId())) {
  // session.delete(s);
  // }
  // for (SensorModel sm : getSensorModels(session, group.getId())) {
  // session.delete(sm);
  // }
  // for (SensorLocation l : getLocations(session, group.getId())) {
  // session.delete(l);
  // }
  // session.delete(group);
  // }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#deleteUserGroup(java.lang.String)
   */
  @Override
  public void deleteUserGroup(String id) throws IdNotFoundException {
    UserGroup g = getUserGroup(id);
    if (g == null) {
      throw new IdNotFoundException(id + " is not a defined user group id.");
    }
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    for (CollectorMetaData sp : getCollectorMetaDatas(session, id)) {
      session.delete(sp);
    }
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    for (SensorGroup sg : getSensorGroups(session, id)) {
      session.delete(sg);
    }
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    for (Depository d : getWattDepositories(session, id)) {
      for (Sensor s : d.listSensors(session)) {
        for (Measurement m : d.getMeasurements(session, s)) {
          session.delete(m);
        }
      }
    }
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    List<Depository> depositories = getWattDepositories(session, id);
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    for (Depository d : depositories) {
      session.delete(d);
    }
    for (Sensor s : getSensors(session, id)) {
      session.delete(s);
    }
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    for (SensorModel sm : getSensorModels(session)) {
      if (!SensorModelHelper.models.containsKey(sm.getName())) {
        session.delete(sm);
      }
    }
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    for (SensorLocation l : getLocations(session, id)) {
      session.delete(l);
    }
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    session.delete(g);
    session.getTransaction().commit();
    session.close();
    sessionClose++;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#deleteUserPassword(java.lang.String)
   */
  @Override
  public void deleteUserPassword(String userId) throws IdNotFoundException {
    UserPassword up = getUserPassword(userId);
    if (up == null) {
      throw new IdNotFoundException(userId + " is not a defined user password id.");
    }
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    session.delete(up);
    session.getTransaction().commit();
    session.close();
    sessionClose++;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#deleteWattDepository(java.lang.String,
   * java.lang.String)
   */
  @Override
  public void deleteWattDepository(String id, String groupId) throws IdNotFoundException,
      MissMatchedOwnerException {
    Depository d = getWattDeposiory(id, groupId);
    if (d == null) {
      throw new IdNotFoundException(id + " is not a defined depository");
    }
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    session.delete(d);
    session.getTransaction().commit();
    session.close();
    sessionClose++;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#getLocation(java.lang.String,
   * java.lang.String)
   */
  @Override
  public SensorLocation getLocation(String id, String groupId) throws MissMatchedOwnerException {
    // search through all the known locations
    for (SensorLocation l : getLocations(UserGroup.ADMIN_GROUP_NAME)) {
      if (l.getId().equals(id)) {
        if (l.getOwner().getId().equals(groupId) || groupId.equals(UserGroup.ADMIN_GROUP_NAME)) {
          return l;
        }
        else {
          throw new MissMatchedOwnerException(id + " is not owned by " + groupId);
        }
      }
    }
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#getLocationIds()
   */
  @Override
  public List<String> getLocationIds(String groupId) {
    ArrayList<String> ret = new ArrayList<String>();
    for (SensorLocation l : getLocations(groupId)) {
      ret.add(l.getId());
    }
    return ret;
  }

  /**
   * @param session
   *          The session with an open transaction.
   * @param groupId
   *          The group id.
   * @return A List of the Locations owned by the groupId.
   */
  @SuppressWarnings("unchecked")
  private List<SensorLocation> getLocations(Session session, String groupId) {
    @SuppressWarnings("rawtypes")
    List result = session.createQuery("from SensorLocation").list();
    ArrayList<SensorLocation> ret = new ArrayList<SensorLocation>();
    for (SensorLocation d : (List<SensorLocation>) result) {
      if (groupId.equals(UserGroup.ADMIN_GROUP_NAME) || groupId.equals(d.getOwner().getId())) {
        ret.add(d);
      }
    }
    return ret;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#getLocations(java.lang.String)
   */
  @Override
  public List<SensorLocation> getLocations(String groupId) {
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    List<SensorLocation> ret = getLocations(session, groupId);
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    return ret;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#getMeasurementType(java.lang.String)
   */
  @Override
  public MeasurementType getMeasurementType(String slug) {
    for (MeasurementType mt : getMeasurementTypes()) {
      if (mt.getId().equals(slug)) {
        return mt;
      }
    }
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#getMeasurementTypes()
   */
  @SuppressWarnings("unchecked")
  @Override
  public List<MeasurementType> getMeasurementTypes() {
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    @SuppressWarnings("rawtypes")
    List result = session.createQuery("from MeasurementType").list();
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    ArrayList<MeasurementType> ret = new ArrayList<MeasurementType>();
    for (MeasurementType mt : (List<MeasurementType>) result) {
      ret.add(mt);
    }
    return ret;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#getSensor(java.lang.String,
   * java.lang.String)
   */
  @Override
  public Sensor getSensor(String id, String groupId) throws MissMatchedOwnerException {
    for (Sensor s : getSensors(UserGroup.ADMIN_GROUP_NAME)) {
      if (s.getId().equals(id)) {
        if (s.getOwner().getId().equals(groupId) || groupId.contains(UserGroup.ADMIN_GROUP_NAME)) {
          Hibernate.initialize(s.getSensorLocation());
          Hibernate.initialize(s.getModel());
          return s;
        }
        else {
          throw new MissMatchedOwnerException(id + " is not owned by " + groupId);
        }
      }
    }
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#getSensorGroup(java.lang.String,
   * java.lang.String)
   */
  @Override
  public SensorGroup getSensorGroup(String id, String groupId) throws MissMatchedOwnerException {
    for (SensorGroup s : getSensorGroups(UserGroup.ADMIN_GROUP_NAME)) {
      if (s.getId().equals(id)) {
        if (s.getOwner().getId().equals(groupId) || groupId.contains(UserGroup.ADMIN_GROUP_NAME)) {
          for (Sensor sens : s.getSensors()) {
            Hibernate.initialize(sens);
            Hibernate.initialize(sens.getSensorLocation());
            Hibernate.initialize(sens.getModel());
          }
          return s;
        }
        else {
          throw new MissMatchedOwnerException(id + " is not owned by " + groupId);
        }
      }
    }
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#getSensorGroupIds()
   */
  @Override
  public List<String> getSensorGroupIds(String groupId) {
    ArrayList<String> ret = new ArrayList<String>();
    for (SensorGroup s : getSensorGroups(groupId)) {
      ret.add(s.getId());
    }
    return ret;
  }

  /**
   * @param session
   *          The session with an open transaction.
   * @param groupId
   *          The group id.
   * @return a List of the SensorGroups owned by groupId.
   */
  @SuppressWarnings("unchecked")
  private List<SensorGroup> getSensorGroups(Session session, String groupId) {
    @SuppressWarnings("rawtypes")
    List result = session.createQuery("from SensorGroup").list();
    ArrayList<SensorGroup> ret = new ArrayList<SensorGroup>();
    for (SensorGroup d : (List<SensorGroup>) result) {
      if (groupId.equals(UserGroup.ADMIN_GROUP_NAME) || groupId.equals(d.getOwner().getId())) {
        // for (Sensor sens : d.getSensors()) {
        // Hibernate.initialize(sens);
        // Hibernate.initialize(sens.getLocation());
        // Hibernate.initialize(sens.getModel());
        // }
        ret.add(d);
      }
    }
    return ret;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#getSensorGroups(java.lang.String)
   */
  @Override
  public List<SensorGroup> getSensorGroups(String groupId) {
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    List<SensorGroup> ret = getSensorGroups(session, groupId);
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    return ret;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#getSensorIds()
   */
  @Override
  public List<String> getSensorIds(String groupId) {
    ArrayList<String> ret = new ArrayList<String>();
    for (Sensor s : getSensors(groupId)) {
      ret.add(s.getId());
    }
    return ret;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#getSensorModel(java.lang.String,
   * java.lang.String)
   */
  @Override
  public SensorModel getSensorModel(String id) {
    for (SensorModel s : getSensorModels()) {
      if (s.getId().equals(id)) {
        return s;
      }
    }
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#getSensorModelIds()
   */
  @Override
  public List<String> getSensorModelIds() {
    ArrayList<String> ret = new ArrayList<String>();
    for (SensorModel s : getSensorModels()) {
      ret.add(s.getId());
    }
    return ret;
  }

  /**
   * @param session
   *          The Session with an open transaction.
   * @return A List of the SensorModels owned by the groupId.
   */
  @SuppressWarnings("unchecked")
  private List<SensorModel> getSensorModels(Session session) {
    @SuppressWarnings("rawtypes")
    List result = session.createQuery("from SensorModel").list();
    ArrayList<SensorModel> ret = new ArrayList<SensorModel>();
    for (SensorModel d : (List<SensorModel>) result) {
      ret.add(d);
    }
    return ret;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#getSensorModels(java.lang.String)
   */
  @Override
  public List<SensorModel> getSensorModels() {
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    List<SensorModel> ret = getSensorModels(session);
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    return ret;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#getCollectorMetaData(java.lang.String,
   * java.lang.String)
   */
  @Override
  public CollectorMetaData getCollectorMetaData(String id, String groupId)
      throws MissMatchedOwnerException {
    for (CollectorMetaData s : getCollectorMetaDatas(UserGroup.ADMIN_GROUP_NAME)) {
      if (s.getId().equals(id)) {
        if (s.getOwner().getId().equals(groupId) || groupId.contains(UserGroup.ADMIN_GROUP_NAME)) {
          return s;
        }
        else {
          throw new MissMatchedOwnerException(id + " is not owned by " + groupId);
        }
      }
    }
    return null;
  }

  /**
   * @param session
   *          The session with an open transaction.
   * @param groupId
   *          The group id.
   * @return a List of CollectorMetaDatas owned by groupId.
   */
  @SuppressWarnings("unchecked")
  private List<CollectorMetaData> getCollectorMetaDatas(Session session, String groupId) {
    @SuppressWarnings("rawtypes")
    List result = session.createQuery("from CollectorMetaData").list();
    ArrayList<CollectorMetaData> ret = new ArrayList<CollectorMetaData>();
    for (CollectorMetaData d : (List<CollectorMetaData>) result) {
      if (groupId.equals(UserGroup.ADMIN_GROUP_NAME) || groupId.equals(d.getOwner().getId())) {
        ret.add(d);
      }
    }
    return ret;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.server.WattDepot#getCollectorMetaDatas(java.lang.String)
   */
  @Override
  public List<CollectorMetaData> getCollectorMetaDatas(String groupId) {
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    List<CollectorMetaData> ret = getCollectorMetaDatas(session, groupId);
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    return ret;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#getCollectorMetaDataIds()
   */
  @Override
  public List<String> getCollectorMetaDataIds(String groupId) {
    ArrayList<String> ret = new ArrayList<String>();
    for (CollectorMetaData s : getCollectorMetaDatas(groupId)) {
      ret.add(s.getId());
    }
    return ret;
  }

  /**
   * @param session
   *          The session with an open transaction.
   * @param groupId
   *          The group id.
   * @return A List of the Sensors owned by groupId.
   */
  @SuppressWarnings("unchecked")
  private List<Sensor> getSensors(Session session, String groupId) {
    @SuppressWarnings("rawtypes")
    List result = session.createQuery("from Sensor").list();
    ArrayList<Sensor> ret = new ArrayList<Sensor>();
    for (Sensor d : (List<Sensor>) result) {
      if (groupId.equals(UserGroup.ADMIN_GROUP_NAME) || groupId.equals(d.getOwner().getId())) {
        ret.add(d);
      }
    }
    return ret;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#getSensors(java.lang.String)
   */
  @Override
  public List<Sensor> getSensors(String groupId) {
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    List<Sensor> ret = getSensors(session, groupId);
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    return ret;
  }

  /**
   * @return the sessionClose
   */
  public int getSessionClose() {
    return sessionClose;
  }

  /**
   * @return the sessionOpen
   */
  public int getSessionOpen() {
    return sessionOpen;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#getUser(java.lang.String)
   */
  @SuppressWarnings("unchecked")
  @Override
  public UserInfo getUser(String id) {
    UserInfo ret = null;
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    @SuppressWarnings("rawtypes")
    List result = session.createQuery("from UserInfo").list();
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    for (UserInfo u : (List<UserInfo>) result) {
      if (id.equals(u.getId())) {
        ret = u;
      }
    }
    return ret;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#getUserGroup(java.lang.String)
   */
  @SuppressWarnings("unchecked")
  @Override
  public UserGroup getUserGroup(String id) {
    UserGroup ret = null;
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    @SuppressWarnings("rawtypes")
    List result = session.createQuery("from UserGroup").list();
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    for (UserGroup g : (List<UserGroup>) result) {
      if (id.equals(g.getId())) {
        ret = g;
      }
    }
    return ret;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#getUserGroupIds()
   */
  @Override
  public List<String> getUserGroupIds() {
    ArrayList<String> ret = new ArrayList<String>();
    for (UserGroup u : getUserGroups()) {
      ret.add(u.getId());
    }
    return ret;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#getUserGroups()
   */
  @SuppressWarnings("unchecked")
  @Override
  public List<UserGroup> getUserGroups() {
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    @SuppressWarnings("rawtypes")
    List result = session.createQuery("from UserGroup").list();
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    return (List<UserGroup>) result;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#getUserIds()
   */
  @Override
  public List<String> getUserIds() {
    ArrayList<String> ret = new ArrayList<String>();
    for (UserInfo u : getUsers()) {
      ret.add(u.getId());
    }
    return ret;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#getUserPassword(java.lang.String)
   */
  @Override
  public UserPassword getUserPassword(String id) {
    UserPassword ret = null;
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    @SuppressWarnings("unchecked")
    List<UserPassword> result = (List<UserPassword>) session.createQuery("from UserPassword")
        .list();
    for (UserPassword up : result) {
      if (up.getId().equals(id)) {
        ret = up;
      }
    }
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    return ret;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#getUsers()
   */
  @SuppressWarnings("unchecked")
  @Override
  public List<UserInfo> getUsers() {
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    @SuppressWarnings("rawtypes")
    List result = session.createQuery("from UserInfo").list();
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    return (List<UserInfo>) result;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.server.WattDepot#getUsersGroup(org.wattdepot.datamodel.
   * UserInfo)
   */
  @Override
  public UserGroup getUsersGroup(UserInfo user) {
    for (UserGroup group : getUserGroups()) {
      if (group.getUsers().contains(user)) {
        return group;
      }
    }
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#getWattDeposiory(java.lang.String,
   * java.lang.String)
   */
  @Override
  public Depository getWattDeposiory(String id, String groupId) throws MissMatchedOwnerException {
    List<Depository> all = getWattDepositories(groupId);
    Depository ret = null;
    for (Depository d : all) {
      if (d.getId().equals(id)) {
        ret = new DepositoryImpl(d);
      }
    }
    return ret;
  }

  /**
   * @param session
   *          The session with an open transaction.
   * @param groupId
   *          The group id.
   * @return A List of the Depositories owned by groupId.
   */
  @SuppressWarnings("unchecked")
  private List<Depository> getWattDepositories(Session session, String groupId) {
    @SuppressWarnings("rawtypes")
    List result = session.createQuery("from DepositoryImpl").list();
    ArrayList<Depository> ret = new ArrayList<Depository>();
    for (Depository d : (List<Depository>) result) {
      if (groupId.equals(UserGroup.ADMIN_GROUP_NAME) || groupId.equals(d.getOwner().getId())) {
        ret.add(new DepositoryImpl(d));
      }
    }
    return ret;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#getWattDepositories(java.lang.String)
   */
  @Override
  public List<Depository> getWattDepositories(String groupId) {
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    List<Depository> ret = getWattDepositories(session, groupId);
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    return ret;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#getWattDepositoryIds()
   */
  @Override
  public List<String> getWattDepositoryIds(String groupId) {
    ArrayList<String> ret = new ArrayList<String>();
    for (Depository d : getWattDepositories(groupId)) {
      ret.add(d.getName());
    }
    return ret;
  }

  /**
   * Use this method after beginning a transaction.
   * 
   * @param session
   *          The Session, a transaction must be in progress.
   * @param sensor
   *          The Sensor to save.
   */
  private void saveSensor(Session session, Sensor sensor) {
    for (Property p : sensor.getProperties()) {
      session.saveOrUpdate(p);
    }
    session.saveOrUpdate(sensor.getSensorLocation());
    session.saveOrUpdate(sensor.getModel());
    session.saveOrUpdate(sensor);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.server.WattDepot#updateLocation(org.wattdepot.datamodel
   * .Location)
   */
  @Override
  public SensorLocation updateLocation(SensorLocation loc) {
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    session.saveOrUpdate(loc);
    session.getTransaction().commit();
    session.close();
    sessionClose++;

    return loc;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.server.WattDepot#updateMeasurementType(org.wattdepot.datamodel
   * .MeasurementType)
   */
  @Override
  public MeasurementType updateMeasurementType(MeasurementType type) {
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    session.saveOrUpdate(type);
    session.getTransaction().commit();
    session.close();
    sessionClose++;

    return type;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.server.WattDepot#updateSensor(org.wattdepot.datamodel.Sensor
   * )
   */
  @Override
  public Sensor updateSensor(Sensor sensor) {
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    session.saveOrUpdate(sensor);
    for (Property p : sensor.getProperties()) {
      session.saveOrUpdate(p);
    }
    session.getTransaction().commit();
    session.close();
    sessionClose++;

    return sensor;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.server.WattDepot#updateSensorGroup(org.wattdepot.datamodel
   * .SensorGroup)
   */
  @Override
  public SensorGroup updateSensorGroup(SensorGroup group) {
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    session.saveOrUpdate(group);
    session.getTransaction().commit();
    session.close();
    sessionClose++;

    return group;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.server.WattDepot#updateSensorModel(org.wattdepot.datamodel
   * .SensorModel)
   */
  @Override
  public SensorModel updateSensorModel(SensorModel model) {
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    session.saveOrUpdate(model);
    session.getTransaction().commit();
    session.close();
    sessionClose++;

    return model;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.server.WattDepot#updateCollectorMetaData(org.wattdepot.
   * datamodel .CollectorMetaData)
   */
  @Override
  public CollectorMetaData updateCollectorMetaData(CollectorMetaData process) {
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    session.saveOrUpdate(process);
    for (Property p : process.getProperties()) {
      session.saveOrUpdate(p);
    }
    session.getTransaction().commit();
    session.close();
    sessionClose++;

    return process;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.server.WattDepot#updateUserGroup(org.wattdepot.datamodel
   * .UserGroup)
   */
  @Override
  public UserGroup updateUserGroup(UserGroup group) {
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    session.saveOrUpdate(group);
    session.getTransaction().commit();
    session.close();
    sessionClose++;

    return group;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.server.WattDepot#updateUserInfo(org.wattdepot.datamodel
   * .UserInfo)
   */
  @Override
  public UserInfo updateUserInfo(UserInfo user) {
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    session.saveOrUpdate(user);
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    return user;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.server.WattDepot#updateUserPassword(org.wattdepot.datamodel
   * .UserPassword)
   */
  @Override
  public UserPassword updateUserPassword(UserPassword password) {
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    session.saveOrUpdate(password);
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    return password;
  }
}
