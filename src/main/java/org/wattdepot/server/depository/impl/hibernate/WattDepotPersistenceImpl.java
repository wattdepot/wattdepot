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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Session;
import org.wattdepot.common.domainmodel.CollectorProcessDefinition;
import org.wattdepot.common.domainmodel.Depository;
import org.wattdepot.common.domainmodel.Measurement;
import org.wattdepot.common.domainmodel.MeasurementType;
import org.wattdepot.common.domainmodel.Organization;
import org.wattdepot.common.domainmodel.Property;
import org.wattdepot.common.domainmodel.Sensor;
import org.wattdepot.common.domainmodel.SensorGroup;
import org.wattdepot.common.domainmodel.SensorModel;
import org.wattdepot.common.domainmodel.UserInfo;
import org.wattdepot.common.domainmodel.UserPassword;
import org.wattdepot.common.exception.BadSlugException;
import org.wattdepot.common.exception.IdNotFoundException;
import org.wattdepot.common.exception.MisMatchedOwnerException;
import org.wattdepot.common.exception.UniqueIdException;
import org.wattdepot.common.util.SensorModelHelper;
import org.wattdepot.common.util.Slug;
import org.wattdepot.server.ServerProperties;
import org.wattdepot.server.WattDepotPersistence;

/**
 * WattDepotImpl - Hibernate implementation of the WattDepot abstract class.
 * 
 * @author Cam Moore
 * 
 */
public class WattDepotPersistenceImpl extends WattDepotPersistence {

  private boolean checkSession;
  private int sessionOpen = 0;
  private int sessionClose = 0;

  /**
   * Creates a new WattDepotImpl instance with the given ServerProperties.
   * 
   * @param properties The ServerProperties.
   */
  public WattDepotPersistenceImpl(ServerProperties properties) {
    super();
    setServerProperties(properties);
    this.checkSession = properties.get(ServerProperties.CHECK_SESSIONS).equals("true");
    // Start with the Organizations
    Organization pub = null;
    try {
      pub = getOrganization(Organization.PUBLIC_GROUP.getId());
    }
    catch (IdNotFoundException e1) { // NOPMD
      // this is ok. We may need to create the Organization.
    }
    if (pub == null) {
      try {
        defineOrganization(Organization.PUBLIC_GROUP.getId(), Organization.PUBLIC_GROUP.getName(),
            new HashSet<String>());
        if (checkSession && getSessionClose() != getSessionOpen()) {
          throw new RuntimeException("opens and closed mismatched.");
        }
      }
      catch (UniqueIdException e) {
        // what do we do here?
        e.printStackTrace();
      }
      catch (BadSlugException e) {
        // what do we do here?
        e.printStackTrace();
      }
    }
    else {
      try {
        updateOrganization(pub);
      }
      catch (IdNotFoundException e) {
        // Should not happen.
        e.printStackTrace();
      }
      if (checkSession && getSessionClose() != getSessionOpen()) {
        throw new RuntimeException("opens and closed mismatched.");
      }
    }
    if (checkSession && getSessionClose() != getSessionOpen()) {
      throw new RuntimeException("opens and closed mismatched.");
    }
    Organization admin = null;
    try {
      admin = getOrganization(Organization.ADMIN_GROUP.getId());
    }
    catch (IdNotFoundException e1) { // NOPMD
      // this is ok, we might need to create the organization.
    }
    if (checkSession && getSessionClose() != getSessionOpen()) {
      throw new RuntimeException("opens and closed mismatched.");
    }
    if (admin == null) {
      try {
        defineOrganization(Organization.ADMIN_GROUP.getId(), Organization.ADMIN_GROUP.getName(),
            Organization.ADMIN_GROUP.getUsers());
        if (checkSession && getSessionClose() != getSessionOpen()) {
          throw new RuntimeException("opens and closed mismatched.");
        }
      }
      catch (UniqueIdException e) {
        // what do we do here?
        e.printStackTrace();
      }
      catch (BadSlugException e) {
        // what do we do here?
        e.printStackTrace();
      }
    }
    else {
      try {
        updateOrganization(admin);
      }
      catch (IdNotFoundException e) {
        // Should not happen.
        e.printStackTrace();
      }
      if (checkSession && getSessionClose() != getSessionOpen()) {
        throw new RuntimeException("opens and closed mismatched.");
      }
    }
    // UserInfo adminUser = getUser(UserInfo.ROOT.getId());
    // if (checkSession && getSessionClose() != getSessionOpen()) {
    // throw new RuntimeException("opens and closed mismatched.");
    // }
    // TODO: check on this we don't want to have the root user defined in the
    // database, but we need
    // them in the Restlet Application/Component.
    // if (adminUser == null) {
    // try {
    // defineUserInfo(UserInfo.ROOT.getId(), UserInfo.ROOT.getFirstName(),
    // UserInfo.ROOT.getLastName(), UserInfo.ROOT.getEmail(),
    // UserInfo.ROOT.getProperties());
    // if (checkSession && getSessionClose() != getSessionOpen()) {
    // throw new RuntimeException("opens and closed mismatched.");
    // }
    // }
    // catch (UniqueIdException e) {
    // // what do we do here?
    // e.printStackTrace();
    // }
    // }
    // else {
    // updateUserInfo(UserInfo.ROOT);
    // if (checkSession && getSessionClose() != getSessionOpen()) {
    // throw new RuntimeException("opens and closed mismatched.");
    // }
    // }
//    
//    UserPassword adminPassword;
//    try {
//      adminPassword = getUserPassword(UserInfo.ROOT.getUid(), UserInfo.ROOT.getOrganizationId());
//      updateUserPassword(adminPassword);
//      if (checkSession && getSessionClose() != getSessionOpen()) {
//        throw new RuntimeException("opens and closed mismatched.");
//      }
//    }
//    catch (IdNotFoundException e2) {
//      // adminPassword no defined.
//      try {
//        defineUserPassword(UserPassword.ADMIN.getUid(), UserPassword.ADMIN.getOrganizationId(),
//            UserPassword.ADMIN.getPlainText(), UserPassword.ADMIN.getEncryptedPassword());
//        if (checkSession && getSessionClose() != getSessionOpen()) {
//          throw new RuntimeException("opens and closed mismatched.");
//        }
//      }
//      catch (UniqueIdException e1) {
//        // what do we do here?
//        e1.printStackTrace();
//      }
//    }
//    if (checkSession && getSessionClose() != getSessionOpen()) {
//      throw new RuntimeException("opens and closed mismatched.");
//    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.server.WattDepot#defineCollectorProcessDefinition(java.lang
   * .String, java.lang.String, java.lang.Long, java.lang.String,
   * java.lang.String)
   */
  @Override
  public CollectorProcessDefinition defineCollectorProcessDefinition(String id, String name,
      String sensorId, Long pollingInterval, String depositoryId, Set<Property> properties,
      String ownerId) throws UniqueIdException, MisMatchedOwnerException, IdNotFoundException,
      BadSlugException {
    getOrganization(ownerId);
    getSensor(sensorId, ownerId);
    getDepository(depositoryId, ownerId);
    if (!Slug.validateSlug(id)) {
      throw new BadSlugException(id + " is not a valid slug.");
    }
    try {
      CollectorProcessDefinition cpd = getCollectorProcessDefinition(id, ownerId);
      if (cpd != null) {
        throw new UniqueIdException(id + " is already a CollectorProcessDefinition id.");
      }
    }
    catch (IdNotFoundException e) { // NOPMD
      // this is expected.
    }
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    OrganizationImpl org = retrieveOrganization(session, ownerId);
    SensorImpl sensor = retrieveSensor(session, sensorId, ownerId);
    DepositoryImpl depot = retrieveDepository(session, depositoryId, ownerId);
    Set<PropertyImpl> props = new HashSet<PropertyImpl>();
    for (Property p : properties) {
      props.add(new PropertyImpl(p));
    }
    CollectorProcessDefinitionImpl impl = new CollectorProcessDefinitionImpl(id, name, sensor,
        pollingInterval, depot, props, org);
    storeCollectorProcessDefinition(session, impl);
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    return impl.toCPD();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#defineWattDepository(java.lang.String,
   * java.lang.String, java.lang.String, org.wattdepot.datamodel.Organization)
   */
  @Override
  public Depository defineDepository(String id, String name, MeasurementType measurementType,
      String ownerId) throws UniqueIdException, IdNotFoundException, BadSlugException {
    if (!Slug.validateSlug(id)) {
      throw new BadSlugException(id + " is not a valid id.");
    }
    getOrganization(ownerId);
    Depository d = null;
    try {
      d = getDepository(id, ownerId);
      if (d != null) {
        throw new UniqueIdException(name + " is already a Depository name.");
      }
    }
    catch (IdNotFoundException e) { // NOPMD
      // ok since we are defining it.
    }
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    OrganizationImpl org = retrieveOrganization(session, ownerId);
    MeasurementTypeImpl type = retrieveMeasurementType(session, measurementType.getId());
    DepositoryImpl impl = new DepositoryImpl(id, name, type, org);
    session.save(impl);
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    return d;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#defineMeasurementType(java.lang.String,
   * java.lang.String)
   */
  @Override
  public MeasurementType defineMeasurementType(String id, String name, String units)
      throws UniqueIdException, BadSlugException {
    if (!Slug.validateSlug(id)) {
      throw new BadSlugException(id + " is not a valid slug.");
    }
    MeasurementType mt = null;
    try {
      mt = getMeasurementType(id);
      if (mt != null) {
        throw new UniqueIdException(id + " is already a MeasurementType id.");
      }
    }
    catch (IdNotFoundException e) { // NOPMD
      // expected.
    }
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    MeasurementTypeImpl impl = new MeasurementTypeImpl(id, name, units);
    session.save(impl);
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    return mt;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#defineOrganization(java.lang.String,
   * java.util.Set<String>)
   */
  @Override
  public Organization defineOrganization(String id, String name, Set<String> users)
      throws UniqueIdException, BadSlugException {
    if (!Slug.validateSlug(id)) {
      throw new BadSlugException(id + " is not a valid slug.");
    }
    Organization g;
    try {
      g = getOrganization(id);
      if (g != null) {
        throw new UniqueIdException(id + " is already a Organization id.");
      }
    }
    catch (IdNotFoundException e) { // NOPMD
      // is ok.
    }
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    Set<UserInfoImpl> u = new HashSet<UserInfoImpl>();
    for (String uid : users) {
      u.add(retrieveUser(session, uid, id));
    }
    OrganizationImpl impl = new OrganizationImpl(id, name, u);
    session.save(impl);
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    return impl.toOrganization();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#defineSensor(java.lang.String,
   * java.lang.String, java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  public Sensor defineSensor(String slug, String name, String uri, String modelId,
      Set<Property> properties, String ownerId) throws UniqueIdException, MisMatchedOwnerException,
      IdNotFoundException, BadSlugException {
    if (!Slug.validateSlug(slug)) {
      throw new BadSlugException(slug + " is not a valid slug.");
    }
    getOrganization(ownerId);
    SensorModel m = getSensorModel(modelId);
    if (m == null) {
      throw new IdNotFoundException(modelId + " is not a defined SensorModel id.");
    }
    Sensor s = null;
    try {
      s = getSensor(slug, ownerId);
      if (s != null) {
        throw new UniqueIdException(slug + " is already a defined Sensor.");
      }
    }
    catch (IdNotFoundException e) { // NOPMD
      // this is expected.
    }
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    OrganizationImpl org = retrieveOrganization(session, ownerId);
    SensorModelImpl model = retrieveSensorModel(session, modelId);
    Set<PropertyImpl> prop = new HashSet<PropertyImpl>();
    for (Property p : properties) {
      prop.add(new PropertyImpl(p));
    }
    SensorImpl impl = new SensorImpl(slug, name, uri, model, prop, org);
    storeSensor(session, impl);
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    return s;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#defineSensorGroup(java.lang.String,
   * java.util.List, org.wattdepot.datamodel.Organization)
   */
  @Override
  public SensorGroup defineSensorGroup(String id, String name, Set<String> sensorIds, String ownerId)
      throws UniqueIdException, MisMatchedOwnerException, IdNotFoundException, BadSlugException {
    if (!Slug.validateSlug(id)) {
      throw new BadSlugException(id + " is not a valid slug.");
    }
    Organization owner = getOrganization(ownerId);
    for (String sensorId : sensorIds) {
      Sensor sensor = getSensor(sensorId, ownerId);
      if (!ownerId.equals(sensor.getOwnerId())) {
        throw new MisMatchedOwnerException(ownerId + " is not the owner of all the sensors.");
      }
    }
    SensorGroup sg = null;
    try {
      sg = getSensorGroup(id, owner.getId());
      if (sg != null) {
        throw new UniqueIdException(id + " is already a SensorGroup id.");
      }
    }
    catch (IdNotFoundException e) { // NOPMD
      // this is ok since we are defining it.
    }
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    OrganizationImpl org = retrieveOrganization(session, ownerId);
    Set<SensorImpl> sensors = new HashSet<SensorImpl>();
    for (String sensorId : sensorIds) {
      sensors.add(retrieveSensor(session, sensorId, ownerId));
    }
    SensorGroupImpl impl = new SensorGroupImpl(id, name, sensors, org);
    session.save(impl);
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
   * org.wattdepot.datamodel.Organization)
   */
  @Override
  public SensorModel defineSensorModel(String id, String name, String protocol, String type,
      String version) throws UniqueIdException, BadSlugException {
    if (!Slug.validateSlug(id)) {
      throw new BadSlugException(id + " is not a valid id.");
    }
    SensorModel sm = null;
    try {
      sm = getSensorModel(id);
      if (sm != null) {
        throw new UniqueIdException(id + " is already a SensorModel id.");
      }
    }
    catch (IdNotFoundException e) { // NOPMD
      // expected.
    }
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    SensorModelImpl impl = new SensorModelImpl(id, name, protocol, type, version);
    session.save(impl);
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    return sm;
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
      String orgId, Set<Property> properties) throws UniqueIdException {
    UserInfo u;
    try {
      u = getUser(id, orgId);
      if (u != null) {
        throw new UniqueIdException(id + " is already a UserInfo id.");
      }
    }
    catch (IdNotFoundException e) { // NOPMD
      // excpected since we are defining a new user.
    }
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    OrganizationImpl org = retrieveOrganization(session, orgId);
    Set<PropertyImpl> props = new HashSet<PropertyImpl>();
    for (Property p : properties) {
      props.add(new PropertyImpl(p));
    }
    UserInfoImpl impl = new UserInfoImpl(id, firstName, lastName, email, props, org);
    session.saveOrUpdate(impl);
    for (Property p : properties) {
      session.saveOrUpdate(p);
    }
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    return impl.toUserInfo();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#defineUserPassword(java.lang.String,
   * java.lang.String)
   */
  @Override
  public UserPassword defineUserPassword(String id, String orgId, String password, String encrypted)
      throws UniqueIdException {
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    OrganizationImpl org = retrieveOrganization(session, orgId);
    UserInfoImpl user = retrieveUser(session, id, orgId);
    UserPasswordImpl up = new UserPasswordImpl(user, password, encrypted, org);
    session.saveOrUpdate(up);
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    return up.toUserPassword();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.server.WattDepot#deleteCollectorProcessDefinition(java.lang
   * .String, java.lang.String)
   */
  @Override
  public void deleteCollectorProcessDefinition(String id, String groupId)
      throws IdNotFoundException, MisMatchedOwnerException {
    getCollectorProcessDefinition(id, groupId);
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    CollectorProcessDefinitionImpl impl = retrieveCollectorProcessDefinition(session, id, groupId);
    session.delete(impl);
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
  public void deleteDepository(String id, String groupId) throws IdNotFoundException,
      MisMatchedOwnerException {
    getDepository(id, groupId);
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    DepositoryImpl impl = retrieveDepository(session, id, groupId);
    session.delete(impl);
    session.getTransaction().commit();
    session.close();
    sessionClose++;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#deleteMeasurementType(java.lang.String)
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
   * @see org.wattdepot.server.WattDepot#deleteOrganization(java.lang.String)
   */
  @Override
  public void deleteOrganization(String id) throws IdNotFoundException {
    Organization g = getOrganization(id);
    // Remove Organization owned CollectorProcessDefinitions
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    for (CollectorProcessDefinitionImpl sp : retrieveCollectorProcessDefinitions(session, id)) {
      session.delete(sp);
    }
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    // Remove Organization owned SensorGroups
    session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    for (SensorGroupImpl sg : retrieveSensorGroups(session, id)) {
      session.delete(sg);
    }
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    // Remove Organization owned Measurements
    session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    for (DepositoryImpl d : retrieveDepositories(session, id)) {
      for (String sensorId : d.listSensors(session)) {
        for (Measurement m : d.getMeasurements(session, sensorId)) {
          // not going to work need to get the MeasurementImpls.
          session.delete(m);
        }
      }
    }
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    // Remove Organization owned Depositories and Sensors
    session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    List<DepositoryImpl> depositories = retrieveDepositories(session, id);
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    for (DepositoryImpl d : depositories) {
      session.delete(d);
    }
    for (SensorImpl s : retrieveSensors(session, id)) {
      session.delete(s);
    }
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    // Remove Organization owned SensorModels
    session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    for (SensorModelImpl sm : retrieveSensorModels(session)) {
      if (!SensorModelHelper.models.containsKey(sm.getName())) {
        session.delete(sm);
      }
    }
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    // Remove Users in the Organization
    session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    for (UserInfoImpl u : retrieveUsers(session, id)) {
      session.delete(u);
    }
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    // Remove UserPasswords in the Organization
    session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    for (UserPasswordImpl u : retrieveUserPasswords(session, id)) {
      session.delete(u);
    }
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    // Remove the organization
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
   * @see org.wattdepot.server.WattDepot#deleteSensor(java.lang.String,
   * java.lang.String)
   */
  @Override
  public void deleteSensor(String id, String groupId) throws IdNotFoundException,
      MisMatchedOwnerException {
    Sensor s = getSensor(id, groupId);
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    session.delete(s);
    session.getTransaction().commit();
    session.close();
    sessionClose++;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#deleteSensorGroup(java.lang.String,
   * java.lang.String)
   */
  @Override
  public void deleteSensorGroup(String id, String groupId) throws IdNotFoundException,
      MisMatchedOwnerException {
    SensorGroup s = getSensorGroup(id, groupId);
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    session.delete(s);
    session.getTransaction().commit();
    session.close();
    sessionClose++;
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
   * @see org.wattdepot.server.WattDepot#deleteUser(java.lang.String)
   */
  @Override
  public void deleteUser(String id, String orgId) throws IdNotFoundException {
    getUser(id, orgId);
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    UserInfoImpl impl = retrieveUser(session, id, orgId);
    session.delete(impl);
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
  public void deleteUserPassword(String userId, String orgId) throws IdNotFoundException {
    getUserPassword(userId, orgId);
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    UserPasswordImpl impl = retrieveUserPassword(session, userId, orgId);
    session.delete(impl);
    session.getTransaction().commit();
    session.close();
    sessionClose++;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.server.WattDepot#getCollectorProcessDefinition(java.lang.
   * String, java.lang.String)
   */
  @Override
  public CollectorProcessDefinition getCollectorProcessDefinition(String id, String ownerId)
      throws IdNotFoundException {
    getOrganization(ownerId);
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    CollectorProcessDefinitionImpl ret = retrieveCollectorProcessDefinition(session, id, ownerId);
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    if (ret == null) {
      throw new IdNotFoundException(id + " is not a defined CollectorProcessDefinition's id.");
    }
    return ret.toCPD();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#getCollectorProcessDefinitionIds()
   */
  @Override
  public List<String> getCollectorProcessDefinitionIds(String groupId) {
    ArrayList<String> ret = new ArrayList<String>();
    for (CollectorProcessDefinition s : getCollectorProcessDefinitions(groupId)) {
      ret.add(s.getId());
    }
    return ret;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.server.WattDepot#getCollectorProcessDefinitions(java.lang
   * .String)
   */
  @Override
  public List<CollectorProcessDefinition> getCollectorProcessDefinitions(String groupId) {
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    List<CollectorProcessDefinitionImpl> r = retrieveCollectorProcessDefinitions(session, groupId);
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    List<CollectorProcessDefinition> ret = new ArrayList<CollectorProcessDefinition>();
    for (CollectorProcessDefinitionImpl cpd : r) {
      ret.add(cpd.toCPD());
    }
    return ret;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#getWattDepositories(java.lang.String)
   */
  @Override
  public List<Depository> getDepositories(String groupId) {
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    List<DepositoryImpl> r = retrieveDepositories(session, groupId);
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    List<Depository> ret = new ArrayList<Depository>();
    for (DepositoryImpl d : r) {
      ret.add(d.toDepository());
    }
    return ret;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#getWattDeposiory(java.lang.String,
   * java.lang.String)
   */
  @Override
  public Depository getDepository(String id, String ownerId) throws IdNotFoundException {
    getOrganization(ownerId);
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    DepositoryImpl ret = retrieveDepository(session, id, ownerId);
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    if (ret == null) {
      throw new IdNotFoundException(id + " is not a defined Depository's id.");
    }
    return ret.toDepository();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#getWattDepositoryIds()
   */
  @Override
  public List<String> getDepositoryIds(String groupId) {
    ArrayList<String> ret = new ArrayList<String>();
    for (Depository d : getDepositories(groupId)) {
      ret.add(d.getId());
    }
    return ret;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#getMeasurementType(java.lang.String)
   */
  @Override
  public MeasurementType getMeasurementType(String id) throws IdNotFoundException {
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    MeasurementTypeImpl ret = retrieveMeasurementType(session, id);
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    if (ret == null) {
      throw new IdNotFoundException(id + " is not a defined MeasurementType.");
    }
    return ret.toMeasurementType();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#getMeasurementTypes()
   */
  @Override
  public List<MeasurementType> getMeasurementTypes() {
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    List<MeasurementTypeImpl> ret = retrieveMeasurementTypes(session);
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    List<MeasurementType> types = new ArrayList<MeasurementType>();
    for (MeasurementTypeImpl i : ret) {
      types.add(i.toMeasurementType());
    }
    return types;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#getOrganization(java.lang.String)
   */
  @Override
  public Organization getOrganization(String id) throws IdNotFoundException {
    OrganizationImpl ret = null;
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    ret = retrieveOrganization(session, id);
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    if (ret == null) {
      throw new IdNotFoundException(id + " isn't a defined Organization's id.");
    }
    return ret.toOrganization();
  }

  /*
   * (non-Javadoc)m
   * 
   * @see org.wattdepot.server.WattDepot#getOrganizationIds()
   */
  @Override
  public List<String> getOrganizationIds() {
    ArrayList<String> ret = new ArrayList<String>();
    for (Organization u : getOrganizations()) {
      ret.add(u.getId());
    }
    return ret;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#getOrganizations()
   */
  @Override
  public List<Organization> getOrganizations() {
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    List<OrganizationImpl> r = retrieveOrganizations(session);
    List<Organization> ret = new ArrayList<Organization>();
    for (OrganizationImpl o : r) {
      ret.add(o.toOrganization());
    }
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    return ret;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#getSensor(java.lang.String,
   * java.lang.String)
   */
  @Override
  public Sensor getSensor(String id, String ownerId) throws IdNotFoundException {
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    SensorImpl ret = retrieveSensor(session, id, ownerId);
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    if (ret == null) {
      throw new IdNotFoundException(id + " is not a defined Sensor id.");
    }
    return ret.toSensor();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#getSensorGroup(java.lang.String,
   * java.lang.String)
   */
  @Override
  public SensorGroup getSensorGroup(String id, String ownerId) throws IdNotFoundException {
    getOrganization(ownerId);
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    SensorGroupImpl ret = retrieveSensorGroup(session, id, ownerId);
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    if (ret == null) {
      throw new IdNotFoundException(id + " is not a defined SensorGroup id.");
    }
    return ret.toSensorGroup();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#getSensorGroupIds()
   */
  @Override
  public List<String> getSensorGroupIds(String groupId) throws IdNotFoundException {
    ArrayList<String> ret = new ArrayList<String>();
    for (SensorGroup s : getSensorGroups(groupId)) {
      ret.add(s.getId());
    }
    return ret;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#getSensorGroups(java.lang.String)
   */
  @Override
  public List<SensorGroup> getSensorGroups(String ownerId) {
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    List<SensorGroupImpl> r = retrieveSensorGroups(session, ownerId);
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    List<SensorGroup> ret = new ArrayList<SensorGroup>();
    for (SensorGroupImpl s : r) {
      ret.add(s.toSensorGroup());
    }
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
  public SensorModel getSensorModel(String id) throws IdNotFoundException {
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    SensorModelImpl ret = retrieveSensorModel(session, id);
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    if (ret == null) {
      throw new IdNotFoundException(id + " is not a valid SensorModel id.");
    }
    return ret.toSensorModel();
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
    List<SensorModelImpl> r = retrieveSensorModels(session);
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    List<SensorModel> ret = new ArrayList<SensorModel>();
    for (SensorModelImpl s : r) {
      ret.add(s.toSensorModel());
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
    List<SensorImpl> r = retrieveSensors(session, groupId);
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    List<Sensor> ret = new ArrayList<Sensor>();
    for (SensorImpl s : r) {
      ret.add(s.toSensor());
    }
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
  public UserInfo getUser(String id, String orgId) throws IdNotFoundException {
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    UserInfoImpl user = retrieveUser(session, id, orgId);
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    if (user == null) {
      throw new IdNotFoundException(id + " is not a defined user id.");
    }
    return user.toUserInfo();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#getUserIds()
   */
  @Override
  public List<String> getUserIds(String orgId) {
    ArrayList<String> ret = new ArrayList<String>();
    for (UserInfo u : getUsers(orgId)) {
      ret.add(u.getUid());
    }
    return ret;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#getUserPassword(java.lang.String)
   */
  @Override
  public UserPassword getUserPassword(String id, String orgId) throws IdNotFoundException {
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    UserPasswordImpl ret = retrieveUserPassword(session, id, orgId);
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    if (ret == null) {
      throw new IdNotFoundException(id + " is not a valid UserPassword id.");
    }
    return ret.toUserPassword();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#getUsers()
   */
  @SuppressWarnings("unchecked")
  @Override
  public List<UserInfo> getUsers(String orgId) {
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    List<UserInfoImpl> result = retrieveUsers(session, orgId);
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    ArrayList<UserInfo> ret = new ArrayList<UserInfo>();
    for (UserInfoImpl u : result) {
      ret.add(u.toUserInfo());
    }
    return ret;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#getUsersGroup(org.wattdepot.datamodel.
   * UserInfo)
   */
  @Override
  public Organization getUsersGroup(UserInfo user) {
    for (Organization group : getOrganizations()) {
      if (group.getUsers().contains(user.getUid())) {
        return group;
      }
    }
    return null;
  }

  /**
   * @param session The session with an open transaction.
   * @param id The CollectorProcessDefinition's id.
   * @param ownerId The owner Organization's id.
   * @return the CollectorProcessDefinition if defined.
   */
  @SuppressWarnings("unchecked")
  private CollectorProcessDefinitionImpl retrieveCollectorProcessDefinition(Session session,
      String id, String ownerId) {
    OrganizationImpl org = retrieveOrganization(session, ownerId);
    List<CollectorProcessDefinitionImpl> cpds = (List<CollectorProcessDefinitionImpl>) session
        .createQuery("FROM CollectorProcessDefinitionImpl WHERE id = :id AND org = :org")
        .setParameter("id", id).setParameter("org", org).list();
    if (cpds.size() == 1) {
      return cpds.get(0);
    }
    else {
      return null;
    }
  }

  /**
   * @param session The session with an open transaction.
   * @param ownerId The owner's Organization id.
   * @return a List of CollectorProcessDefinitions owned by ownerId.
   */
  @SuppressWarnings("unchecked")
  private List<CollectorProcessDefinitionImpl> retrieveCollectorProcessDefinitions(Session session,
      String ownerId) {
    OrganizationImpl org = retrieveOrganization(session, ownerId);
    List<CollectorProcessDefinitionImpl> ret = (List<CollectorProcessDefinitionImpl>) session
        .createQuery("FROM CollectorProcessDefinitionImpl WHERE org = :org")
        .setParameter("org", org).list();
    return ret;
  }

  /**
   * @param session The session with an open transaction.
   * @param id the Depository's id.
   * @param ownerId The owner's Organization id.
   * @return A List of the Depositories owned by groupId.
   */
  @SuppressWarnings("unchecked")
  private DepositoryImpl retrieveDepository(Session session, String id, String ownerId) {
    OrganizationImpl org = retrieveOrganization(session, ownerId);
    List<DepositoryImpl> ret = (List<DepositoryImpl>) session
        .createQuery("from DepositoryImpl WHERE id = :id AND org = :org").setParameter("id", id)
        .setParameter("org", org).list();
    if (ret.size() == 1) {
      return ret.get(0);
    }
    return null;
  }

  /**
   * @param session The session with an open transaction.
   * @param ownerId The owner's Organization id.
   * @return A List of the Depositories owned by groupId.
   */
  @SuppressWarnings("unchecked")
  private List<DepositoryImpl> retrieveDepositories(Session session, String ownerId) {
    OrganizationImpl org = retrieveOrganization(session, ownerId);
    List<DepositoryImpl> ret = (List<DepositoryImpl>) session
        .createQuery("from DepositoryImpl WHERE org = :org").setParameter("org", org).list();
    return ret;
  }

  /**
   * @param session A Session with an open transaction.
   * @param id The id of the MeasurementType.
   * @return The MeasurementType with the given id.
   */
  @SuppressWarnings("unchecked")
  private MeasurementTypeImpl retrieveMeasurementType(Session session, String id) {
    List<MeasurementTypeImpl> ret = (List<MeasurementTypeImpl>) session
        .createQuery("FROM MeasurementTypeImpl WHERE id = :id").setParameter("id", id).list();
    if (ret.size() == 1) {
      return ret.get(0);
    }
    return null;
  }

  /**
   * @param session A Session with an open transaction.
   * @return The list of defined MeasurementTypes.
   */
  @SuppressWarnings("unchecked")
  private List<MeasurementTypeImpl> retrieveMeasurementTypes(Session session) {
    List<MeasurementTypeImpl> ret = (List<MeasurementTypeImpl>) session.createQuery(
        "FROM MeasurementTypeImpl").list();
    return ret;
  }

  /**
   * @param session A Session with an open transaction.
   * @param id The id of the Organization.
   * @return The Organization with the given id.
   */
  @SuppressWarnings("unchecked")
  private OrganizationImpl retrieveOrganization(Session session, String id) {
    List<OrganizationImpl> ret = (List<OrganizationImpl>) session
        .createQuery("from OrganizationImpl WHERE id = :id").setParameter("id", id).list();
    if (ret.size() == 1) {
      return ret.get(0);
    }
    return null;
  }

  /**
   * @param session A Session with an open transaction.
   * @return The List of defined Organizations.
   */
  @SuppressWarnings("unchecked")
  private List<OrganizationImpl> retrieveOrganizations(Session session) {
    List<OrganizationImpl> ret = (List<OrganizationImpl>) session.createQuery(
        "from OrganizationImpl").list();
    return ret;

  }

  /**
   * @param session The session with an open transaction.
   * @param id the Sensor's id.
   * @param ownerId The owner's Organization id.
   * @return The Sensor with the given id and owned by ownerId.
   */
  @SuppressWarnings("unchecked")
  private SensorImpl retrieveSensor(Session session, String id, String ownerId) {
    OrganizationImpl org = retrieveOrganization(session, ownerId);
    List<SensorImpl> ret = (List<SensorImpl>) session
        .createQuery("FROM SensorImpl WHERE id = :id AND org = :org").setParameter("id", id)
        .setParameter("org", org).list();
    if (ret.size() == 1) {
      return ret.get(0);
    }
    return null;
  }

  /**
   * @param session The session with an open transaction.
   * @param ownerId The owner's Organization id.
   * @return A List of the Sensors owned by ownerId.
   */
  @SuppressWarnings("unchecked")
  private List<SensorImpl> retrieveSensors(Session session, String ownerId) {
    OrganizationImpl org = retrieveOrganization(session, ownerId);
    List<SensorImpl> ret = (List<SensorImpl>) session
        .createQuery("FROM SensorImpl WHERE org = :org").setParameter("org", org).list();
    return ret;
  }

  /**
   * @param session The session with an open transaction.
   * @param id the SensorGroup's id.
   * @param ownerId The owner's Organization id.
   * @return The SensorGroup with the given id, owned by ownerId.
   */
  @SuppressWarnings("unchecked")
  private SensorGroupImpl retrieveSensorGroup(Session session, String id, String ownerId) {
    OrganizationImpl org = retrieveOrganization(session, ownerId);
    List<SensorGroupImpl> result = (List<SensorGroupImpl>) session
        .createQuery("FROM SensorGroupImpl WHERE id = :id AND org = :org").setParameter("id", id)
        .setParameter("org", org).list();
    if (result.size() == 1) {
      return result.get(0);
    }
    return null;
  }

  /**
   * @param session The session with an open transaction.
   * @param ownerId The owner's Organization id.
   * @return a List of the SensorGroups owned by groupId.
   */
  @SuppressWarnings("unchecked")
  private List<SensorGroupImpl> retrieveSensorGroups(Session session, String ownerId) {
    OrganizationImpl org = retrieveOrganization(session, ownerId);
    List<SensorGroupImpl> result = (List<SensorGroupImpl>) session
        .createQuery("FROM SensorGroupImpl WHERE org = :org").setParameter("org", org).list();
    return result;
  }

  /**
   * @param session The Session with an open transaction.
   * @param id the id of the SensorModel to retrieve.
   * @return A List of the SensorModels owned by the groupId.
   */
  @SuppressWarnings("unchecked")
  private SensorModelImpl retrieveSensorModel(Session session, String id) {
    List<SensorModelImpl> result = (List<SensorModelImpl>) session
        .createQuery("FROM SensorModelImpl WHERE id = :id").setParameter("id", id).list();
    if (result.size() == 1) {
      return result.get(0);
    }
    return null;
  }

  /**
   * @param session The Session with an open transaction.
   * @return A List of the SensorModels owned by the groupId.
   */
  @SuppressWarnings("unchecked")
  private List<SensorModelImpl> retrieveSensorModels(Session session) {
    List<SensorModelImpl> ret = (List<SensorModelImpl>) session.createQuery("FROM SensorModelImpl")
        .list();
    return ret;
  }

  /**
   * @param session The session with an open transaction.
   * @param uid the User's id.
   * @param orgId the Organziation's id.
   * @return the UserInfoImpl.
   */
  @SuppressWarnings("unchecked")
  private UserInfoImpl retrieveUser(Session session, String uid, String orgId) {
    OrganizationImpl org = retrieveOrganization(session, orgId);
    List<UserInfoImpl> result = (List<UserInfoImpl>) session
        .createQuery("FROM UserInfoImpl WHERE uid = :uid AND org = :org").setParameter("uid", uid)
        .setParameter("org", org).list();
    if (result.size() == 1) {
      return result.get(0);
    }
    return null;

  }

  /**
   * @param session The Session with an open transaction.
   * @param id the user's id.
   * @param orgId the organization's id.
   * @return the UserPasswordImpl.
   */
  @SuppressWarnings("unchecked")
  private UserPasswordImpl retrieveUserPassword(Session session, String id, String orgId) {
    OrganizationImpl org = retrieveOrganization(session, orgId);
    UserInfoImpl user = retrieveUser(session, id, orgId);
    List<UserPasswordImpl> result = (List<UserPasswordImpl>) session
        .createQuery("FROM UserPasswordImpl WHERE user = :user AND org = :org")
        .setParameter("user", user).setParameter("org", org).list();
    if (result.size() == 1) {
      return result.get(0);
    }
    return null;
  }

  /**
   * @param session The Session with an open transaction.
   * @param orgId The organization id.
   * @return a List of the user passwords in the given organization.
   */
  @SuppressWarnings("unchecked")
  private List<UserPasswordImpl> retrieveUserPasswords(Session session, String orgId) {
    OrganizationImpl org = retrieveOrganization(session, orgId);
    List<UserPasswordImpl> result = (List<UserPasswordImpl>) session
        .createQuery("FROM UserPasswordImpl WHERE org = :org").setParameter("org", org).list();
    return result;
  }

  /**
   * @param session The Session with an open transaction.
   * @param orgId The organization id.
   * @return a List of the users in the given organization.
   */
  @SuppressWarnings("unchecked")
  private List<UserInfoImpl> retrieveUsers(Session session, String orgId) {
    OrganizationImpl org = retrieveOrganization(session, orgId);
    List<UserInfoImpl> result = (List<UserInfoImpl>) session
        .createQuery("FROM UserInfoImpl WHERE org = :org").setParameter("org", org).list();
    return result;
  }

  /**
   * Use this method after beginning a transaction.
   * 
   * @param session The Session, a transaction must be in progress.
   * @param cpd The CollectorProcessDefinitionImpl to save.
   */
  private void storeCollectorProcessDefinition(Session session, CollectorProcessDefinitionImpl cpd) {
    for (PropertyImpl p : cpd.getProperties()) {
      session.saveOrUpdate(p);
    }
    session.saveOrUpdate(cpd);

  }

  /**
   * Use this method after beginning a transaction.
   * 
   * @param session The Session, a transaction must be in progress.
   * @param sensor The SensorImpl to save.
   */
  private void storeSensor(Session session, SensorImpl sensor) {
    for (PropertyImpl p : sensor.getProperties()) {
      session.saveOrUpdate(p);
    }
    session.saveOrUpdate(sensor);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.server.WattDepot#updateCollectorProcessDefinition(org.wattdepot
   * . datamodel .CollectorProcessDefinition)
   */
  @Override
  public CollectorProcessDefinition updateCollectorProcessDefinition(
      CollectorProcessDefinition process) throws IdNotFoundException {
    getCollectorProcessDefinition(process.getId(), process.getOwnerId());
    getDepository(process.getDepositoryId(), process.getOwnerId());
    getSensor(process.getSensorId(), process.getOwnerId());
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    CollectorProcessDefinitionImpl impl = retrieveCollectorProcessDefinition(session,
        process.getId(), process.getOwnerId());
    impl.setId(process.getId());
    impl.setName(process.getName());
    impl.setPollingInterval(process.getPollingInterval());
    impl.setDepository(retrieveDepository(session, process.getDepositoryId(), process.getOwnerId()));
    impl.setSensor(retrieveSensor(session, process.getSensorId(), process.getOwnerId()));
    impl.setOrg(retrieveOrganization(session, process.getOwnerId()));
    storeCollectorProcessDefinition(session, impl);
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    return impl.toCPD();
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
   * org.wattdepot.server.WattDepot#updateOrganization(org.wattdepot.datamodel
   * .Organization)
   */
  @Override
  public Organization updateOrganization(Organization org) throws IdNotFoundException {
    getOrganization(org.getId());
    for (String s : org.getUsers()) {
      getUser(s, org.getId());
    }
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    OrganizationImpl impl = retrieveOrganization(session, org.getId());
    impl.setId(org.getId());
    impl.setName(org.getName());
    Set<UserInfoImpl> users = new HashSet<UserInfoImpl>();
    for (String s : org.getUsers()) {
      users.add(retrieveUser(session, s, org.getId()));
    }
    impl.setUsers(users);
    session.saveOrUpdate(impl);
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    return impl.toOrganization();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.server.WattDepot#updateSensor(org.wattdepot.datamodel.Sensor
   * )
   */
  @Override
  public Sensor updateSensor(Sensor sensor) throws IdNotFoundException {
    getOrganization(sensor.getOwnerId());
    getSensorModel(sensor.getModelId());
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    SensorImpl impl = retrieveSensor(session, sensor.getId(), sensor.getOwnerId());
    impl.setId(sensor.getId());
    impl.setName(sensor.getName());
    impl.setOrg(retrieveOrganization(session, sensor.getOwnerId()));
    impl.setModel(retrieveSensorModel(session, sensor.getModelId()));
    Set<PropertyImpl> props = new HashSet<PropertyImpl>();
    for (Property p : sensor.getProperties()) {
      props.add(new PropertyImpl(p));
    }
    impl.setProperties(props);
    storeSensor(session, impl);
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    return impl.toSensor();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.server.WattDepot#updateSensorGroup(org.wattdepot.datamodel
   * .SensorGroup)
   */
  @Override
  public SensorGroup updateSensorGroup(SensorGroup group) throws IdNotFoundException {
    getOrganization(group.getOwnerId());
    getSensorGroup(group.getId(), group.getOwnerId());
    // validate the list of sensor ids.
    for (String id : group.getSensors()) {
      getSensor(id, group.getOwnerId());
    }
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    OrganizationImpl org = retrieveOrganization(session, group.getOwnerId());
    SensorGroupImpl impl = retrieveSensorGroup(session, group.getId(), group.getOwnerId());
    impl.setId(group.getId());
    impl.setName(group.getName());
    impl.setOrg(org);
    Set<SensorImpl> sensors = new HashSet<SensorImpl>();
    for (String s : group.getSensors()) {
      sensors.add(retrieveSensor(session, s, group.getOwnerId()));
    }
    impl.setSensors(sensors);
    session.saveOrUpdate(impl);
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    return impl.toSensorGroup();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.server.WattDepot#updateSensorModel(org.wattdepot.datamodel
   * .SensorModel)
   */
  @Override
  public SensorModel updateSensorModel(SensorModel model) throws IdNotFoundException {
    getSensorModel(model.getId());
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    SensorModelImpl impl = retrieveSensorModel(session, model.getId());
    impl.setName(model.getName());
    impl.setProtocol(model.getProtocol());
    impl.setId(model.getId());
    impl.setType(model.getType());
    impl.setVersion(model.getVersion());
    session.saveOrUpdate(impl);
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    return impl.toSensorModel();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#updateUserInfo(org.wattdepot.datamodel
   * .UserInfo)
   */
  @Override
  public UserInfo updateUserInfo(UserInfo user) throws IdNotFoundException {
    getUser(user.getUid(), user.getOrganizationId());
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    UserInfoImpl impl = retrieveUser(session, user.getUid(), user.getOrganizationId());
    impl.setUid(user.getUid());
    impl.setFirstName(user.getFirstName());
    impl.setLastName(user.getLastName());
    impl.setEmail(user.getEmail());
    Set<PropertyImpl> props = new HashSet<PropertyImpl>();
    for (Property p : user.getProperties()) {
      props.add(new PropertyImpl(p));
    }
    impl.setProperties(props);
    impl.setOrg(retrieveOrganization(session, user.getOrganizationId()));
    session.saveOrUpdate(impl);
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    return impl.toUserInfo();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.server.WattDepot#updateUserPassword(org.wattdepot.datamodel
   * .UserPassword)
   */
  @Override
  public UserPassword updateUserPassword(UserPassword password) throws IdNotFoundException {
    getUserPassword(password.getUid(), password.getOrganizationId());
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    UserPasswordImpl impl = retrieveUserPassword(session, password.getUid(),
        password.getOrganizationId());
    impl.setPlainText(password.getPlainText());
    impl.setEncryptedPassword(password.getEncryptedPassword());
    impl.setOrg(retrieveOrganization(session, password.getOrganizationId()));
    impl.setUser(retrieveUser(session, password.getUid(), password.getOrganizationId()));
    session.saveOrUpdate(impl);
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    return impl.toUserPassword();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepotPersistence#stop()
   */
  @Override
  public void stop() {
    Manager.closeSession();
  }

}
