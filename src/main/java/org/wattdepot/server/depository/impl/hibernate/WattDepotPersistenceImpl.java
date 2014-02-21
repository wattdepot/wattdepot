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
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.datatype.XMLGregorianCalendar;

import org.hibernate.Session;
import org.wattdepot.common.domainmodel.CollectorProcessDefinition;
import org.wattdepot.common.domainmodel.Depository;
import org.wattdepot.common.domainmodel.InterpolatedValue;
import org.wattdepot.common.domainmodel.Measurement;
import org.wattdepot.common.domainmodel.MeasurementRateSummary;
import org.wattdepot.common.domainmodel.MeasurementType;
import org.wattdepot.common.domainmodel.Organization;
import org.wattdepot.common.domainmodel.Property;
import org.wattdepot.common.domainmodel.Sensor;
import org.wattdepot.common.domainmodel.SensorGroup;
import org.wattdepot.common.domainmodel.SensorMeasurementSummary;
import org.wattdepot.common.domainmodel.SensorModel;
import org.wattdepot.common.domainmodel.UserInfo;
import org.wattdepot.common.domainmodel.UserPassword;
import org.wattdepot.common.exception.BadSlugException;
import org.wattdepot.common.exception.IdNotFoundException;
import org.wattdepot.common.exception.MeasurementGapException;
import org.wattdepot.common.exception.MeasurementTypeException;
import org.wattdepot.common.exception.MisMatchedOwnerException;
import org.wattdepot.common.exception.NoMeasurementException;
import org.wattdepot.common.exception.UniqueIdException;
import org.wattdepot.common.util.DateConvert;
import org.wattdepot.common.util.SensorModelHelper;
import org.wattdepot.common.util.Slug;
import org.wattdepot.common.util.tstamp.Tstamp;
import org.wattdepot.server.ServerProperties;
import org.wattdepot.server.StrongAES;
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
    // try {
    // Session validate = Manager.getValidateFactory(properties).openSession();
    // validate.close();
    // }
    // catch (HibernateException e) { // NOPMD
    // // e.printStackTrace();
    // // might be able to just use the 'update' sessionFactory.
    // // Session create = Manager.getCreateFactory(properties).openSession();
    // // create.close();
    // }
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
      catch (IdNotFoundException e) {
        // What do we do here?
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
      catch (IdNotFoundException e) {
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
    UserInfo adminUser = null;
    try {
      adminUser = getUser(UserInfo.ROOT.getUid(), Organization.ADMIN_GROUP.getId());
    }
    catch (IdNotFoundException e) {
      try {
        defineUserInfo(UserInfo.ROOT.getUid(), UserInfo.ROOT.getFirstName(),
            UserInfo.ROOT.getLastName(), UserInfo.ROOT.getEmail(),
            Organization.ADMIN_GROUP.getId(), UserInfo.ROOT.getProperties(), StrongAES
                .getInstance().decrypt(UserPassword.ROOT.getEncryptedPassword()));
        if (checkSession && getSessionClose() != getSessionOpen()) {
          throw new RuntimeException("opens and closed mismatched.");
        }
      }
      catch (UniqueIdException e1) {
        // what do we do here?
        e1.printStackTrace();
      }
      catch (IdNotFoundException e1) {
        // this shouldn't happen
        e1.printStackTrace();
      }
    }
    if (checkSession && getSessionClose() != getSessionOpen()) {
      throw new RuntimeException("opens and closed mismatched.");
    }
    if (adminUser != null) {
      try {
        updateUserInfo(UserInfo.ROOT);
      }
      catch (IdNotFoundException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      if (checkSession && getSessionClose() != getSessionOpen()) {
        throw new RuntimeException("opens and closed mismatched.");
      }
    }
    if (!Organization.ADMIN_GROUP.getUsers().contains(UserInfo.ROOT.getUid())) {
      Organization.ADMIN_GROUP.add(UserInfo.ROOT.getUid());
      try {
        updateOrganization(Organization.ADMIN_GROUP);
      }
      catch (IdNotFoundException e) { // NOPMD
        // There is a problem
      }
    }

    UserPassword adminPassword;
    try {
      adminPassword = getUserPassword(UserInfo.ROOT.getUid(), UserInfo.ROOT.getOrganizationId());
      updateUserPassword(adminPassword);
      if (checkSession && getSessionClose() != getSessionOpen()) {
        throw new RuntimeException("opens and closed mismatched.");
      }
    }
    catch (IdNotFoundException e2) { // NOPMD
      // adminPassword no defined.
      // we are in trouble.
    }
    if (checkSession && getSessionClose() != getSessionOpen()) {
      throw new RuntimeException("opens and closed mismatched.");
    }
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
      String orgId) throws UniqueIdException, MisMatchedOwnerException, IdNotFoundException,
      BadSlugException {
    getOrganization(orgId);
    getSensor(sensorId, orgId);
    getDepository(depositoryId, orgId);
    if (!Slug.validateSlug(id)) {
      throw new BadSlugException(id + " is not a valid slug.");
    }
    try {
      CollectorProcessDefinition cpd = getCollectorProcessDefinition(id, orgId);
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
    OrganizationImpl org = retrieveOrganization(session, orgId);
    SensorImpl sensor = retrieveSensor(session, sensorId, orgId);
    DepositoryImpl depot = retrieveDepository(session, depositoryId, orgId);
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
      String orgId) throws UniqueIdException, IdNotFoundException, BadSlugException {
    if (!Slug.validateSlug(id)) {
      throw new BadSlugException(id + " is not a valid id.");
    }
    getOrganization(orgId);
    getMeasurementType(measurementType.getId());
    Depository d = null;
    try {
      d = getDepository(id, orgId);
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
    OrganizationImpl org = retrieveOrganization(session, orgId);
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
      throws UniqueIdException, BadSlugException, IdNotFoundException {
    Organization ret = null;
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
    for (String uid : users) {
      getUser(uid, id);
    }
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    Set<UserInfoImpl> u = new HashSet<UserInfoImpl>();
    for (String uid : users) {
      u.add(retrieveUser(session, uid, id));
    }
    OrganizationImpl impl = new OrganizationImpl(id, name, u);
    ret = impl.toOrganization();
    session.save(impl);
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    return ret;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#defineSensor(java.lang.String,
   * java.lang.String, java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  public Sensor defineSensor(String id, String name, String uri, String modelId,
      Set<Property> properties, String orgId) throws UniqueIdException, MisMatchedOwnerException,
      IdNotFoundException, BadSlugException {
    if (!Slug.validateSlug(id)) {
      throw new BadSlugException(id + " is not a valid slug.");
    }
    getOrganization(orgId);
    getSensorModel(modelId);
    Sensor s = null;
    try {
      s = getSensor(id, orgId);
      if (s != null) {
        throw new UniqueIdException(id + " is already a defined Sensor.");
      }
    }
    catch (IdNotFoundException e) { // NOPMD
      // this is expected.
    }
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    OrganizationImpl org = retrieveOrganization(session, orgId);
    SensorModelImpl model = retrieveSensorModel(session, modelId);
    Set<PropertyImpl> prop = new HashSet<PropertyImpl>();
    for (Property p : properties) {
      prop.add(new PropertyImpl(p));
    }
    SensorImpl impl = new SensorImpl(id, name, uri, model, prop, org);
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
  public SensorGroup defineSensorGroup(String id, String name, Set<String> sensorIds, String orgId)
      throws UniqueIdException, MisMatchedOwnerException, IdNotFoundException, BadSlugException {
    if (!Slug.validateSlug(id)) {
      throw new BadSlugException(id + " is not a valid slug.");
    }
    Organization owner = getOrganization(orgId);
    for (String sensorId : sensorIds) {
      Sensor sensor = getSensor(sensorId, orgId);
      if (!orgId.equals(sensor.getOrganizationId())) {
        throw new MisMatchedOwnerException(orgId + " is not the owner of all the sensors.");
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
    OrganizationImpl org = retrieveOrganization(session, orgId);
    Set<SensorImpl> sensors = new HashSet<SensorImpl>();
    for (String sensorId : sensorIds) {
      sensors.add(retrieveSensor(session, sensorId, orgId));
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
      String orgId, Set<Property> properties, String password) throws UniqueIdException,
      IdNotFoundException {
    getOrganization(orgId);
    UserInfo u = null;
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
      PropertyImpl pi = new PropertyImpl(p);
      props.add(pi);
      session.saveOrUpdate(pi);
    }
    UserInfoImpl impl = new UserInfoImpl(id, firstName, lastName, email, props, org);
    session.saveOrUpdate(impl);
    u = impl.toUserInfo();
    UserPasswordImpl up = new UserPasswordImpl(impl, password, org);
    session.saveOrUpdate(up);
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    return u;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.server.WattDepot#deleteCollectorProcessDefinition(java.lang
   * .String, java.lang.String)
   */
  @Override
  public void deleteCollectorProcessDefinition(String id, String orgId) throws IdNotFoundException,
      MisMatchedOwnerException {
    getCollectorProcessDefinition(id, orgId);
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    CollectorProcessDefinitionImpl impl = retrieveCollectorProcessDefinition(session, id, orgId);
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
  public void deleteDepository(String id, String orgId) throws IdNotFoundException,
      MisMatchedOwnerException {
    getDepository(id, orgId);
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    DepositoryImpl impl = retrieveDepository(session, id, orgId);
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
  public void deleteMeasurementType(String id) throws IdNotFoundException {
    getMeasurementType(id);
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    MeasurementTypeImpl impl = retrieveMeasurementType(session, id);
    session.delete(impl);
    session.getTransaction().commit();
    session.close();
    sessionClose++;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#deleteOrganization(java.lang.String)
   */
  @Override
  public void deleteOrganization(String id) throws IdNotFoundException {
    getOrganization(id);
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
      for (String sensorId : listSensors(d.getId(), id)) {
        for (Measurement m : getMeasurements(d.getId(), id, sensorId)) {
          MeasurementImpl mi = retrieveMeasurement(session, d.getId(), id, m.getId());
          if (mi != null) {
            session.delete(mi);
          }
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
    OrganizationImpl orgImpl = retrieveOrganization(session, id);
    for (UserInfoImpl u : retrieveUsers(session, id)) {
      orgImpl.getUsers().remove(u);
      session.delete(u);
    }
    session.update(orgImpl);
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
    orgImpl = retrieveOrganization(session, id);
    session.delete(orgImpl);
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
  public void deleteSensor(String id, String orgId) throws IdNotFoundException,
      MisMatchedOwnerException {
    getSensor(id, orgId);
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    SensorImpl impl = retrieveSensor(session, id, orgId);
    session.delete(impl);
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
  public void deleteSensorGroup(String id, String orgId) throws IdNotFoundException,
      MisMatchedOwnerException {
    getSensorGroup(id, orgId);
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    SensorGroupImpl impl = retrieveSensorGroup(session, id, orgId);
    session.delete(impl);
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
    getSensorModel(id);
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    SensorModelImpl impl = retrieveSensorModel(session, id);
    session.delete(impl);
    session.getTransaction().commit();
    session.close();
    sessionClose++;
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
    OrganizationImpl orgImpl = retrieveOrganization(session, orgId);
    UserInfoImpl impl = retrieveUser(session, id, orgId);
    UserPasswordImpl up = retrieveUserPassword(session, id, orgId);
    orgImpl.removeUser(impl);
    session.saveOrUpdate(orgImpl);
    session.delete(up);
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
  public CollectorProcessDefinition getCollectorProcessDefinition(String id, String orgId)
      throws IdNotFoundException {
    CollectorProcessDefinition ret = null;
    getOrganization(orgId);
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    CollectorProcessDefinitionImpl cpd = retrieveCollectorProcessDefinition(session, id, orgId);
    if (cpd != null) {
      ret = cpd.toCPD();
    }
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    if (ret == null) {
      throw new IdNotFoundException(id + " is not a defined CollectorProcessDefinition's id.");
    }
    return ret;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#getCollectorProcessDefinitionIds()
   */
  @Override
  public List<String> getCollectorProcessDefinitionIds(String orgId) throws IdNotFoundException {
    ArrayList<String> ret = new ArrayList<String>();
    for (CollectorProcessDefinition s : getCollectorProcessDefinitions(orgId)) {
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
  public List<CollectorProcessDefinition> getCollectorProcessDefinitions(String orgId)
      throws IdNotFoundException {
    getOrganization(orgId);
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    List<CollectorProcessDefinitionImpl> r = retrieveCollectorProcessDefinitions(session, orgId);
    List<CollectorProcessDefinition> ret = new ArrayList<CollectorProcessDefinition>();
    for (CollectorProcessDefinitionImpl cpd : r) {
      ret.add(cpd.toCPD());
    }
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    return ret;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#getWattDepositories(java.lang.String)
   */
  @Override
  public List<Depository> getDepositories(String orgId) throws IdNotFoundException {
    getOrganization(orgId);
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    List<DepositoryImpl> r = retrieveDepositories(session, orgId);
    List<Depository> ret = new ArrayList<Depository>();
    for (DepositoryImpl d : r) {
      ret.add(d.toDepository());
    }
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    return ret;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#getWattDeposiory(java.lang.String,
   * java.lang.String)
   */
  @Override
  public Depository getDepository(String id, String orgId) throws IdNotFoundException {
    Depository ret = null;
    getOrganization(orgId);
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    DepositoryImpl impl = retrieveDepository(session, id, orgId);
    if (impl != null) {
      ret = impl.toDepository();
    }
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    if (ret == null) {
      throw new IdNotFoundException(id + " is not a defined Depository's id.");
    }
    return ret;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#getWattDepositoryIds()
   */
  @Override
  public List<String> getDepositoryIds(String orgId) throws IdNotFoundException {
    ArrayList<String> ret = new ArrayList<String>();
    for (Depository d : getDepositories(orgId)) {
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
    MeasurementType ret = null;
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    MeasurementTypeImpl impl = retrieveMeasurementType(session, id);
    if (impl != null) {
      ret = impl.toMeasurementType();
    }
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    if (ret == null) {
      throw new IdNotFoundException(id + " is not a defined MeasurementType.");
    }
    return ret;
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
    List<MeasurementType> types = new ArrayList<MeasurementType>();
    for (MeasurementTypeImpl i : ret) {
      types.add(i.toMeasurementType());
    }
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    return types;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#getOrganization(java.lang.String)
   */
  @Override
  public Organization getOrganization(String id) throws IdNotFoundException {
    Organization ret = null;
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    OrganizationImpl org = retrieveOrganization(session, id);
    if (org != null) {
      ret = org.toOrganization();
    }
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    if (ret == null) {
      throw new IdNotFoundException(id + " isn't a defined Organization's id.");
    }
    return ret;
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
  public Sensor getSensor(String id, String orgId) throws IdNotFoundException {
    Sensor ret = null;
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    SensorImpl impl = retrieveSensor(session, id, orgId);
    if (impl != null) {
      ret = impl.toSensor();
    }
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    if (ret == null) {
      throw new IdNotFoundException(id + " is not a defined Sensor id.");
    }
    return ret;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#getSensorGroup(java.lang.String,
   * java.lang.String)
   */
  @Override
  public SensorGroup getSensorGroup(String id, String orgId) throws IdNotFoundException {
    SensorGroup ret = null;
    getOrganization(orgId);
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    SensorGroupImpl impl = retrieveSensorGroup(session, id, orgId);
    if (impl != null) {
      ret = impl.toSensorGroup();
    }
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    if (ret == null) {
      throw new IdNotFoundException(id + " is not a defined SensorGroup id.");
    }
    return ret;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#getSensorGroupIds()
   */
  @Override
  public List<String> getSensorGroupIds(String orgId) throws IdNotFoundException {
    ArrayList<String> ret = new ArrayList<String>();
    for (SensorGroup s : getSensorGroups(orgId)) {
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
  public List<SensorGroup> getSensorGroups(String orgId) throws IdNotFoundException {
    getOrganization(orgId);
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    List<SensorGroupImpl> r = retrieveSensorGroups(session, orgId);
    List<SensorGroup> ret = new ArrayList<SensorGroup>();
    for (SensorGroupImpl s : r) {
      ret.add(s.toSensorGroup());
    }
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
  public List<String> getSensorIds(String orgId) throws IdNotFoundException {
    ArrayList<String> ret = new ArrayList<String>();
    for (Sensor s : getSensors(orgId)) {
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
    SensorModel ret = null;
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    SensorModelImpl impl = retrieveSensorModel(session, id);
    if (impl != null) {
      ret = impl.toSensorModel();
    }
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    if (ret == null) {
      throw new IdNotFoundException(id + " is not a valid SensorModel id.");
    }
    return ret;
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
    List<SensorModel> ret = new ArrayList<SensorModel>();
    for (SensorModelImpl s : r) {
      ret.add(s.toSensorModel());
    }
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    return ret;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#getSensors(java.lang.String)
   */
  @Override
  public List<Sensor> getSensors(String orgId) throws IdNotFoundException {
    getOrganization(orgId);
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    List<SensorImpl> r = retrieveSensors(session, orgId);
    List<Sensor> ret = new ArrayList<Sensor>();
    for (SensorImpl s : r) {
      ret.add(s.toSensor());
    }
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
  @Override
  public UserInfo getUser(String id, String orgId) throws IdNotFoundException {
    getOrganization(orgId);
    UserInfo ret = null;
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    UserInfoImpl impl = retrieveUser(session, id, orgId);
    if (impl != null) {
      ret = impl.toUserInfo();
    }
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    if (ret == null) {
      throw new IdNotFoundException(id + " is not a defined user id.");
    }
    return ret;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#getUserIds()
   */
  @Override
  public List<String> getUserIds(String orgId) throws IdNotFoundException {
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
    UserPassword ret = null;
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    UserPasswordImpl impl = retrieveUserPassword(session, id, orgId);
    if (impl != null) {
      ret = impl.toUserPassword();
    }
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    if (ret == null) {
      throw new IdNotFoundException(id + " is not a valid UserPassword id.");
    }
    return ret;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#getUsers()
   */
  @Override
  public List<UserInfo> getUsers(String orgId) throws IdNotFoundException {
    getOrganization(orgId);
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    List<UserInfoImpl> result = retrieveUsers(session, orgId);
    ArrayList<UserInfo> ret = new ArrayList<UserInfo>();
    for (UserInfoImpl u : result) {
      ret.add(u.toUserInfo());
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
  @Override
  public List<UserInfo> getUsers() {
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    List<UserInfoImpl> result = retrieveUsers(session);
    ArrayList<UserInfo> ret = new ArrayList<UserInfo>();
    for (UserInfoImpl u : result) {
      ret.add(u.toUserInfo());
    }
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    return ret;
  }

  /**
   * @param session The session with an open transaction.
   * @param id The CollectorProcessDefinition's id.
   * @param orgId The owner Organization's id.
   * @return the CollectorProcessDefinition if defined.
   */
  @SuppressWarnings("unchecked")
  private CollectorProcessDefinitionImpl retrieveCollectorProcessDefinition(Session session,
      String id, String orgId) {
    OrganizationImpl org = retrieveOrganization(session, orgId);
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
   * @param orgId The owner's Organization id.
   * @return a List of CollectorProcessDefinitions owned by orgId.
   */
  @SuppressWarnings("unchecked")
  private List<CollectorProcessDefinitionImpl> retrieveCollectorProcessDefinitions(Session session,
      String orgId) {
    OrganizationImpl org = retrieveOrganization(session, orgId);
    List<CollectorProcessDefinitionImpl> ret = (List<CollectorProcessDefinitionImpl>) session
        .createQuery("FROM CollectorProcessDefinitionImpl WHERE org = :org")
        .setParameter("org", org).list();
    return ret;
  }

  /**
   * @param session The session with an open transaction.
   * @param id the Depository's id.
   * @param orgId The owner's Organization id.
   * @return A List of the Depositories owned by orgId.
   */
  @SuppressWarnings("unchecked")
  private DepositoryImpl retrieveDepository(Session session, String id, String orgId) {
    OrganizationImpl org = retrieveOrganization(session, orgId);
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
   * @param orgId The owner's Organization id.
   * @return A List of the Depositories owned by orgId.
   */
  @SuppressWarnings("unchecked")
  private List<DepositoryImpl> retrieveDepositories(Session session, String orgId) {
    OrganizationImpl org = retrieveOrganization(session, orgId);
    List<DepositoryImpl> ret = (List<DepositoryImpl>) session
        .createQuery("from DepositoryImpl WHERE org = :org").setParameter("org", org).list();
    return ret;
  }

  /**
   * @param session The session with an open transaction.
   * @param depotId the id of the Depository.
   * @param orgId the organization's id.
   * @param measId the measurement's id.
   * @return the MeasurementImpl.
   */
  private MeasurementImpl retrieveMeasurement(Session session, String depotId, String orgId,
      String measId) {
    DepositoryImpl depot = retrieveDepository(session, depotId, orgId);
    @SuppressWarnings("unchecked")
    List<MeasurementImpl> result = (List<MeasurementImpl>) session
        .createQuery("FROM MeasurementImpl WHERE depository = :depository AND id = :id")
        .setParameter("depository", depot).setParameter("id", measId).list();
    if (result.size() == 1) {
      return result.get(0);
    }
    return null;
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
   * @param orgId The owner's Organization id.
   * @return The Sensor with the given id and owned by orgId.
   */
  @SuppressWarnings("unchecked")
  private SensorImpl retrieveSensor(Session session, String id, String orgId) {
    OrganizationImpl org = retrieveOrganization(session, orgId);
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
   * @param orgId The owner's Organization id.
   * @return A List of the Sensors owned by orgId.
   */
  @SuppressWarnings("unchecked")
  private List<SensorImpl> retrieveSensors(Session session, String orgId) {
    OrganizationImpl org = retrieveOrganization(session, orgId);
    List<SensorImpl> ret = (List<SensorImpl>) session
        .createQuery("FROM SensorImpl WHERE org = :org").setParameter("org", org).list();
    return ret;
  }

  /**
   * @param session The session with an open transaction.
   * @param id the SensorGroup's id.
   * @param orgId The owner's Organization id.
   * @return The SensorGroup with the given id, owned by orgId.
   */
  @SuppressWarnings("unchecked")
  private SensorGroupImpl retrieveSensorGroup(Session session, String id, String orgId) {
    OrganizationImpl org = retrieveOrganization(session, orgId);
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
   * @param orgId The owner's Organization id.
   * @return a List of the SensorGroups owned by orgId.
   */
  @SuppressWarnings("unchecked")
  private List<SensorGroupImpl> retrieveSensorGroups(Session session, String orgId) {
    OrganizationImpl org = retrieveOrganization(session, orgId);
    List<SensorGroupImpl> result = (List<SensorGroupImpl>) session
        .createQuery("FROM SensorGroupImpl WHERE org = :org").setParameter("org", org).list();
    return result;
  }

  /**
   * @param session The Session with an open transaction.
   * @param id the id of the SensorModel to retrieve.
   * @return A List of the SensorModels owned by the orgId.
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
   * @return A List of the SensorModels owned by the orgId.
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
   * @param session The Session with an open transaction.
   * @return A List of all the defined users.
   */
  @SuppressWarnings("unchecked")
  private List<UserInfoImpl> retrieveUsers(Session session) {
    List<UserInfoImpl> result = (List<UserInfoImpl>) session.createQuery("FROM UserInfoImpl")
        .list();
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
    getCollectorProcessDefinition(process.getId(), process.getOrganizationId());
    getDepository(process.getDepositoryId(), process.getOrganizationId());
    getSensor(process.getSensorId(), process.getOrganizationId());
    CollectorProcessDefinition ret = null;
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    CollectorProcessDefinitionImpl impl = retrieveCollectorProcessDefinition(session,
        process.getId(), process.getOrganizationId());
    impl.setId(process.getId());
    impl.setName(process.getName());
    impl.setPollingInterval(process.getPollingInterval());
    impl.setDepository(retrieveDepository(session, process.getDepositoryId(),
        process.getOrganizationId()));
    impl.setSensor(retrieveSensor(session, process.getSensorId(), process.getOrganizationId()));
    impl.setOrg(retrieveOrganization(session, process.getOrganizationId()));
    storeCollectorProcessDefinition(session, impl);
    ret = impl.toCPD();
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    return ret;
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
    MeasurementTypeImpl impl = retrieveMeasurementType(session, type.getId());
    impl.setName(type.getName());
    impl.setUnits(type.getUnits());
    session.saveOrUpdate(impl);
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
    Organization ret = null;
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    OrganizationImpl impl = retrieveOrganization(session, org.getId());
    impl.setId(org.getId());
    impl.setName(org.getName());
    for (String s : org.getUsers()) {
      UserInfoImpl u = retrieveUser(session, s, org.getId());
      if (u != null) {
        impl.getUsers().add(u);
      }
    }
    session.saveOrUpdate(impl);
    ret = impl.toOrganization();
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    return ret;
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
    getOrganization(sensor.getOrganizationId());
    getSensorModel(sensor.getModelId());
    Sensor ret = null;
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    SensorImpl impl = retrieveSensor(session, sensor.getId(), sensor.getOrganizationId());
    impl.setId(sensor.getId());
    impl.setName(sensor.getName());
    impl.setOrg(retrieveOrganization(session, sensor.getOrganizationId()));
    impl.setModel(retrieveSensorModel(session, sensor.getModelId()));
    Set<PropertyImpl> props = new HashSet<PropertyImpl>();
    for (Property p : sensor.getProperties()) {
      props.add(new PropertyImpl(p));
    }
    impl.setProperties(props);
    storeSensor(session, impl);
    ret = impl.toSensor();
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    return ret;
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
    getOrganization(group.getOrganizationId());
    getSensorGroup(group.getId(), group.getOrganizationId());
    // validate the list of sensor ids.
    for (String id : group.getSensors()) {
      getSensor(id, group.getOrganizationId());
    }
    SensorGroup ret = null;
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    OrganizationImpl org = retrieveOrganization(session, group.getOrganizationId());
    SensorGroupImpl impl = retrieveSensorGroup(session, group.getId(), group.getOrganizationId());
    impl.setId(group.getId());
    impl.setName(group.getName());
    impl.setOrg(org);
    Set<SensorImpl> sensors = new HashSet<SensorImpl>();
    for (String s : group.getSensors()) {
      sensors.add(retrieveSensor(session, s, group.getOrganizationId()));
    }
    impl.setSensors(sensors);
    session.saveOrUpdate(impl);
    ret = impl.toSensorGroup();
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    return ret;
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
    SensorModel ret = null;
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
    ret = impl.toSensorModel();
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    return ret;
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
    UserInfo ret = null;
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    OrganizationImpl orgImpl = retrieveOrganization(session, user.getOrganizationId());
    UserInfoImpl impl = retrieveUser(session, user.getUid(), user.getOrganizationId());
    impl.setUid(user.getUid());
    impl.setFirstName(user.getFirstName());
    impl.setLastName(user.getLastName());
    impl.setEmail(user.getEmail());
    Set<PropertyImpl> props = new HashSet<PropertyImpl>();
    for (Property p : user.getProperties()) {
      PropertyImpl pi = new PropertyImpl(p);
      props.add(pi);
      session.saveOrUpdate(pi);
    }
    impl.setProperties(props);
    impl.setOrg(orgImpl);
    session.saveOrUpdate(orgImpl);
    session.saveOrUpdate(impl);
    ret = impl.toUserInfo();
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    return ret;
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
    UserPassword ret = null;
    getUserPassword(password.getUid(), password.getOrganizationId());
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    UserPasswordImpl impl = retrieveUserPassword(session, password.getUid(),
        password.getOrganizationId());
    impl.setEncryptedPassword(password.getEncryptedPassword());
    impl.setOrg(retrieveOrganization(session, password.getOrganizationId()));
    impl.setUser(retrieveUser(session, password.getUid(), password.getOrganizationId()));
    session.saveOrUpdate(impl);
    ret = impl.toUserPassword();
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    return ret;
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

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.server.WattDepotPersistence#deleteMeasurement(java.lang.String
   * , java.lang.String)
   */
  @Override
  public void deleteMeasurement(String depotId, String orgId, String measId)
      throws IdNotFoundException {
    getOrganization(orgId);
    getDepository(depotId, orgId);
    getMeasurement(depotId, orgId, measId);
    Session session = Manager.getFactory(getServerProperties()).openSession();
    session.beginTransaction();
    MeasurementImpl impl = retrieveMeasurement(session, depotId, orgId, measId);
    session.delete(impl);
    session.getTransaction().commit();
    session.close();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.server.WattDepotPersistence#getEarliestMeasuredValue(java
   * .lang.String, java.lang.String)
   */
  @Override
  public InterpolatedValue getEarliestMeasuredValue(String depotId, String orgId, String sensorId)
      throws NoMeasurementException, IdNotFoundException {
    InterpolatedValue value = null;
    getOrganization(orgId);
    getDepository(depotId, orgId);
    getSensor(sensorId, orgId);
    Session session = Manager.getFactory(getServerProperties()).openSession();
    session.beginTransaction();
    DepositoryImpl depot = retrieveDepository(session, depotId, orgId);
    SensorImpl sensor = retrieveSensor(session, sensorId, orgId);
    @SuppressWarnings("unchecked")
    List<MeasurementImpl> result = (List<MeasurementImpl>) session
        .createQuery(
            "FROM MeasurementImpl WHERE depository = :depot AND sensor = :sensor "
                + "AND timestamp IN (SELECT min(timestamp) FROM MeasurementImpl WHERE "
                + "depository = :depot AND sensor = :sensor)").setParameter("depot", depot)
        .setParameter("sensor", sensor).list();
    if (result.size() > 0) {
      MeasurementImpl meas = result.get(0);
      value = new InterpolatedValue(sensorId, meas.getValue(), depot.getType().toMeasurementType(),
          meas.getTimestamp());
    }
    session.getTransaction().commit();
    session.close();
    return value;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.server.WattDepotPersistence#getLatestMeasuredValue(java.lang
   * .String, java.lang.String)
   */
  @Override
  public InterpolatedValue getLatestMeasuredValue(String depotId, String orgId, String sensorId)
      throws NoMeasurementException, IdNotFoundException {
    InterpolatedValue value = null;
    getOrganization(orgId);
    getDepository(depotId, orgId);
    getSensor(sensorId, orgId);
    Session session = Manager.getFactory(getServerProperties()).openSession();
    session.beginTransaction();
    DepositoryImpl depot = retrieveDepository(session, depotId, orgId);
    SensorImpl sensor = retrieveSensor(session, sensorId, orgId);
    @SuppressWarnings("unchecked")
    List<MeasurementImpl> result = (List<MeasurementImpl>) session
        .createQuery(
            "FROM MeasurementImpl WHERE depository = :depot AND sensor = :sensor "
                + "AND timestamp IN (SELECT max(timestamp) FROM MeasurementImpl WHERE "
                + "depository = :depot AND sensor = :sensor)").setParameter("depot", depot)
        .setParameter("sensor", sensor).list();
    if (result.size() > 0) {
      MeasurementImpl meas = result.get(0);
      value = new InterpolatedValue(sensorId, meas.getValue(), depot.getType().toMeasurementType(),
          meas.getTimestamp());
    }
    session.getTransaction().commit();
    session.close();
    return value;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.server.WattDepotPersistence#getMeasurement(java.lang.String,
   * java.lang.String)
   */
  @Override
  public Measurement getMeasurement(String depotId, String orgId, String measId)
      throws IdNotFoundException {
    Measurement ret = null;
    getOrganization(orgId);
    getDepository(depotId, orgId);
    Session session = Manager.getFactory(getServerProperties()).openSession();
    session.beginTransaction();
    DepositoryImpl depot = retrieveDepository(session, depotId, orgId);
    @SuppressWarnings("unchecked")
    List<MeasurementImpl> result = (List<MeasurementImpl>) session
        .createQuery("FROM MeasurementImpl WHERE depository = :depot AND id = :id")
        .setParameter("depot", depot).setParameter("id", measId).list();
    if (result.size() == 1) {
      ret = result.get(0).toMeasurement();
    }
    session.getTransaction().commit();
    session.close();
    return ret;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.server.WattDepotPersistence#getMeasurements(java.lang.String,
   * java.lang.String)
   */
  @Override
  public List<Measurement> getMeasurements(String depotId, String orgId, String sensorId)
      throws IdNotFoundException {
    getOrganization(orgId);
    getDepository(depotId, orgId);
    getSensor(sensorId, orgId);
    List<Measurement> ret = new ArrayList<Measurement>();
    Session session = Manager.getFactory(getServerProperties()).openSession();
    session.beginTransaction();
    DepositoryImpl depot = retrieveDepository(session, depotId, orgId);
    SensorImpl sensor = retrieveSensor(session, sensorId, orgId);
    @SuppressWarnings("unchecked")
    List<MeasurementImpl> result = (List<MeasurementImpl>) session
        .createQuery("FROM MeasurementImpl WHERE depository = :depot AND sensor = :sensor")
        .setParameter("depot", depot).setParameter("sensor", sensor).list();
    for (MeasurementImpl mi : result) {
      ret.add(mi.toMeasurement());
    }
    session.getTransaction().commit();
    session.close();
    return ret;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.server.WattDepotPersistence#getMeasurements(java.lang.String,
   * java.lang.String, java.util.Date, java.util.Date)
   */
  @Override
  public List<Measurement> getMeasurements(String depotId, String orgId, String sensorId,
      Date start, Date end) throws IdNotFoundException {
    getOrganization(orgId);
    getDepository(depotId, orgId);
    getSensor(sensorId, orgId);
    List<Measurement> ret = new ArrayList<Measurement>();
    Session session = Manager.getFactory(getServerProperties()).openSession();
    session.beginTransaction();
    DepositoryImpl depot = retrieveDepository(session, depotId, orgId);
    SensorImpl sensor = retrieveSensor(session, sensorId, orgId);
    @SuppressWarnings("unchecked")
    List<MeasurementImpl> measurements = (List<MeasurementImpl>) session
        .createQuery(
            "FROM MeasurementImpl WHERE timestamp >= :start AND timestamp <= :end AND depository = :depository AND sensor = :sensor")
        .setParameter("start", start).setParameter("end", end).setParameter("depository", depot)
        .setParameter("sensor", sensor).list();
    for (MeasurementImpl mi : measurements) {
      ret.add(mi.toMeasurement());
    }
    session.getTransaction().commit();
    session.close();
    return ret;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepotPersistence#getValue(java.lang.String,
   * java.lang.String, java.util.Date)
   */
  @Override
  public Double getValue(String depotId, String orgId, String sensorId, Date timestamp)
      throws NoMeasurementException, IdNotFoundException {
    getOrganization(orgId);
    getDepository(depotId, orgId);
    getSensor(sensorId, orgId);
    Double ret = null;
    Session session = Manager.getFactory(getServerProperties()).openSession();
    session.beginTransaction();
    DepositoryImpl depot = retrieveDepository(session, depotId, orgId);
    SensorImpl sensor = retrieveSensor(session, sensorId, orgId);
    @SuppressWarnings("unchecked")
    List<MeasurementImpl> result = (List<MeasurementImpl>) session
        .createQuery(
            "FROM MeasurementImpl WHERE timestamp = :time AND depository = :depot AND sensor = :sensor")
        .setParameter("time", timestamp).setParameter("depot", depot)
        .setParameter("sensor", sensor).setMaxResults(1).list();
    if (result.size() > 0) {
      ret = result.get(0).getValue();
    }
    else {
      // need to get the stradle
      @SuppressWarnings("unchecked")
      List<MeasurementImpl> before = (List<MeasurementImpl>) session
          .createQuery(
              "FROM MeasurementImpl WHERE timestamp <= :time AND depository = :depot AND sensor = :sensor ORDER BY timestamp desc")
          .setParameter("time", timestamp).setParameter("depot", depot)
          .setParameter("sensor", sensor).setMaxResults(1).list();
      @SuppressWarnings("unchecked")
      List<MeasurementImpl> after = (List<MeasurementImpl>) session
          .createQuery(
              "FROM MeasurementImpl WHERE timestamp >= :time AND depository = :depot AND sensor = :sensor ORDER BY timestamp asc")
          .setParameter("time", timestamp).setMaxResults(1).setParameter("depot", depot)
          .setParameter("sensor", sensor).list();
      MeasurementImpl justBefore = null;
      for (MeasurementImpl b : before) {
        if (b.getSensor().getId().equals(sensorId)) {
          if (justBefore == null) {
            justBefore = b;
          }
          else if (b.getTimestamp().compareTo(justBefore.getTimestamp()) > 0) {
            justBefore = b;
          }
        }
      }
      if (justBefore == null) {
        session.getTransaction().commit();
        session.close();
        throw new NoMeasurementException("Cannot find measurement before " + timestamp);
      }
      MeasurementImpl justAfter = null;
      for (MeasurementImpl a : after) {
        if (a.getSensor().getId().equals(sensorId)) {
          if (justAfter == null) {
            justAfter = a;
          }
          else if (a.getTimestamp().compareTo(justBefore.getTimestamp()) > 0) {
            justAfter = a;
          }
        }
      }
      if (justAfter == null) {
        session.getTransaction().commit();
        session.close();
        throw new NoMeasurementException("Cannot find measurement after " + timestamp);
      }
      Double val1 = justBefore.getValue();
      Double val2 = justAfter.getValue();
      Double deltaV = val2 - val1;
      Long t1 = justBefore.getTimestamp().getTime();
      Long t2 = justAfter.getTimestamp().getTime();
      Long deltaT = t2 - t1;
      Long t3 = timestamp.getTime();
      Long toDate = t3 - t1;
      Double slope = deltaV / deltaT;
      ret = val1 + (slope * toDate);
    }
    session.getTransaction().commit();
    session.close();
    return ret;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepotPersistence#getValue(java.lang.String,
   * java.lang.String, java.util.Date, java.util.Date)
   */
  @Override
  public Double getValue(String depotId, String orgId, String sensorId, Date start, Date end)
      throws NoMeasurementException, IdNotFoundException {
    Double endVal = getValue(depotId, orgId, sensorId, end);
    Double startVal = getValue(depotId, orgId, sensorId, start);
    if (endVal != null && startVal != null) {
      return endVal - startVal;
    }
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepotPersistence#getValue(java.lang.String,
   * java.lang.String, java.util.Date, java.util.Date, java.lang.Long)
   */
  @Override
  public Double getValue(String depotId, String orgId, String sensorId, Date start, Date end,
      Long gapSeconds) throws NoMeasurementException, MeasurementGapException, IdNotFoundException {
    Double endVal = getValue(depotId, orgId, sensorId, end, gapSeconds);
    Double startVal = getValue(depotId, orgId, sensorId, start, gapSeconds);
    if (endVal != null && startVal != null) {
      return endVal - startVal;
    }
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepotPersistence#getValue(java.lang.String,
   * java.lang.String, java.util.Date, java.lang.Long)
   */
  @Override
  public Double getValue(String depotId, String orgId, String sensorId, Date timestamp,
      Long gapSeconds) throws NoMeasurementException, MeasurementGapException, IdNotFoundException {
    getOrganization(orgId);
    getDepository(depotId, orgId);
    getSensor(sensorId, orgId);
    Double ret = null;
    Session session = Manager.getFactory(getServerProperties()).openSession();
    session.beginTransaction();
    DepositoryImpl depot = retrieveDepository(session, depotId, orgId);
    SensorImpl sensor = retrieveSensor(session, sensorId, orgId);
    @SuppressWarnings("unchecked")
    List<MeasurementImpl> result = (List<MeasurementImpl>) session
        .createQuery(
            "FROM MeasurementImpl WHERE timestamp = :time AND depository = :depot AND sensor = :sensor")
        .setParameter("time", timestamp).setParameter("depot", depot)
        .setParameter("sensor", sensor).list();
    if (result.size() > 0) {
      ret = result.get(0).getValue();
    }
    else {
      // need to get the stradle
      @SuppressWarnings("unchecked")
      List<MeasurementImpl> before = (List<MeasurementImpl>) session
          .createQuery(
              "FROM MeasurementImpl WHERE timestamp <= :time AND depository = :depot AND sensor = :sensor ORDER BY timestamp desc")
          .setParameter("time", timestamp).setParameter("depot", depot)
          .setParameter("sensor", sensor).setMaxResults(1).list();
      @SuppressWarnings("unchecked")
      List<MeasurementImpl> after = (List<MeasurementImpl>) session
          .createQuery(
              "FROM MeasurementImpl WHERE timestamp >= :time AND depository = :depot AND sensor = :sensor ORDER BY timestamp asc")
          .setParameter("time", timestamp).setMaxResults(1).setParameter("depot", depot)
          .setParameter("sensor", sensor).list();
      MeasurementImpl justBefore = null;
      for (MeasurementImpl b : before) {
        if (b.getSensor().getId().equals(sensorId)) {
          if (justBefore == null) {
            justBefore = b;
          }
          else if (b.getTimestamp().compareTo(justBefore.getTimestamp()) > 0) {
            justBefore = b;
          }
        }
      }
      if (justBefore == null) {
        session.getTransaction().commit();
        session.close();
        throw new NoMeasurementException("Cannot find measurement before " + timestamp);
      }
      MeasurementImpl justAfter = null;
      for (MeasurementImpl a : after) {
        if (a.getSensor().getId().equals(sensorId)) {
          if (justAfter == null) {
            justAfter = a;
          }
          else if (a.getTimestamp().compareTo(justBefore.getTimestamp()) > 0) {
            justAfter = a;
          }
        }
      }
      if (justAfter == null) {
        session.getTransaction().commit();
        session.close();
        throw new NoMeasurementException("Cannot find measurement after " + timestamp);
      }
      Double val1 = justBefore.getValue();
      Double val2 = justAfter.getValue();
      Double deltaV = val2 - val1;
      Long t1 = justBefore.getTimestamp().getTime();
      Long t2 = justAfter.getTimestamp().getTime();
      Long deltaT = t2 - t1;
      if ((deltaT / 1000) > gapSeconds) {
        session.getTransaction().commit();
        session.close();
        throw new MeasurementGapException("Gap of " + (deltaT / 1000) + "s is longer than "
            + gapSeconds);
      }
      Long t3 = timestamp.getTime();
      Long toDate = t3 - t1;
      Double slope = deltaV / deltaT;
      ret = val1 + (slope * toDate);
    }
    session.getTransaction().commit();
    session.close();
    return ret;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.server.WattDepotPersistence#listSensors(java.lang.String)
   */
  @Override
  public List<String> listSensors(String depotId, String orgId) throws IdNotFoundException {
    getOrganization(orgId);
    getDepository(depotId, orgId);
    Session session = Manager.getFactory(getServerProperties()).openSession();
    session.beginTransaction();
    DepositoryImpl depot = retrieveDepository(session, depotId, orgId);
    @SuppressWarnings("unchecked")
    List<SensorImpl> result = (List<SensorImpl>) session
        .createQuery(
            "select distinct meas.sensor FROM MeasurementImpl meas WHERE meas.depository = :depot")
        .setParameter("depot", depot).list();
    ArrayList<String> sensorIds = new ArrayList<String>();
    for (SensorImpl sensor : result) {
      if (!sensorIds.contains(sensor.getId())) {
        sensorIds.add(sensor.getId());
      }
    }
    session.getTransaction().commit();
    session.close();
    return sensorIds;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.server.WattDepotPersistence#putMeasurement(java.lang.String,
   * org.wattdepot.common.domainmodel.Measurement)
   */
  @Override
  public void putMeasurement(String depotId, String orgId, Measurement meas)
      throws MeasurementTypeException, IdNotFoundException {
    getOrganization(orgId);
    Depository d = getDepository(depotId, orgId);
    if (!meas.getMeasurementType().equals(d.getMeasurementType().getUnits())) {
      throw new MeasurementTypeException("Measurement's type " + meas.getMeasurementType()
          + " does not match " + d.getMeasurementType());
    }
    Session session = Manager.getFactory(getServerProperties()).openSession();
    session.beginTransaction();
    DepositoryImpl depot = retrieveDepository(session, depotId, orgId);
    SensorImpl sensor = retrieveSensor(session, meas.getSensorId(), orgId);
    MeasurementImpl impl = new MeasurementImpl();
    impl.setDepository(depot);
    impl.setSensor(sensor);
    impl.setId(meas.getId());
    impl.setTimestamp(meas.getDate());
    impl.setValue(meas.getValue());
    impl.setUnits(meas.getMeasurementType().toString());
    session.saveOrUpdate(impl);
    session.getTransaction().commit();
    session.close();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepotPersistence#getSummary(java.lang.String,
   * java.lang.String, java.lang.String, java.util.Date, java.util.Date)
   */
  @Override
  public SensorMeasurementSummary getSummary(String depotId, String orgId, String sensorId,
      Date start, Date end) throws IdNotFoundException {
    List<Measurement> list = getMeasurements(depotId, orgId, sensorId, start, end);
    SensorMeasurementSummary ret = new SensorMeasurementSummary(sensorId, depotId, start, end,
        list.size());
    return ret;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.server.WattDepotPersistence#getRateSummary(java.lang.String,
   * java.lang.String, java.lang.String)
   */
  @Override
  public MeasurementRateSummary getRateSummary(String depotId, String orgId, String sensorId)
      throws IdNotFoundException, NoMeasurementException {
    XMLGregorianCalendar now = Tstamp.makeTimestamp();
    XMLGregorianCalendar minAgo = Tstamp.incrementMinutes(now, -1);
    MeasurementRateSummary ret = new MeasurementRateSummary();
    ret.setDepositoryId(depotId);
    ret.setSensorId(sensorId);
    ret.setTimestamp(DateConvert.convertXMLCal(now));
    // Long startTime = System.nanoTime();
    Long count = getMeasurementsCount(depotId, orgId, sensorId, DateConvert.convertXMLCal(minAgo),
        DateConvert.convertXMLCal(now));
    // Long endTime = System.nanoTime();
    // Long diff = endTime - startTime;
    // System.out.println("getMeasurementCount(minAgo) took " + (diff / 1E9) +
    // " seconds");
    ret.setOneMinuteCount(count);
    ret.setOneMinuteRate(count / 60.0);
    // startTime = System.nanoTime();
    InterpolatedValue val = getLatestMeasuredValue(depotId, orgId, sensorId);
    // endTime = System.nanoTime();
    // diff = endTime - startTime;
    // System.out.println("getLatestMeasuredValue() took " + (diff / 1E9) +
    // " seconds");
    ret.setLatestValue(val.getValue());
    ret.setType(val.getMeasurementType());
    // startTime = System.nanoTime();
    count = getMeasurementsCount(depotId, orgId, sensorId);
    // endTime = System.nanoTime();
    // diff = endTime - startTime;
    // System.out.println("getMeasurementsCount(total) took " + (diff / 1E9) +
    // " seconds");
    ret.setTotalCount(count);
    return ret;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.server.WattDepotPersistence#getMeasurementsCount(java.lang
   * .String, java.lang.String, java.lang.String)
   */
  @Override
  public Long getMeasurementsCount(String depotId, String orgId, String sensorId)
      throws IdNotFoundException {
    getOrganization(orgId);
    getDepository(depotId, orgId);
    getSensor(sensorId, orgId);
    Long ret = null;
    Session session = Manager.getFactory(getServerProperties()).openSession();
    session.beginTransaction();
    DepositoryImpl depot = retrieveDepository(session, depotId, orgId);
    SensorImpl sensor = retrieveSensor(session, sensorId, orgId);
    @SuppressWarnings("unchecked")
    List<Long> result = (List<Long>) session
        .createQuery(
            "SELECT count(*) FROM MeasurementImpl WHERE depository = :depot AND sensor = :sensor")
        .setParameter("depot", depot).setParameter("sensor", sensor).list();
    ret = result.get(0);
    session.getTransaction().commit();
    session.close();

    return ret;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.server.WattDepotPersistence#getMeasurementsCount(java.lang
   * .String, java.lang.String, java.lang.String, java.util.Date,
   * java.util.Date)
   */
  @Override
  public Long getMeasurementsCount(String depotId, String orgId, String sensorId, Date start,
      Date end) throws IdNotFoundException {
    getOrganization(orgId);
    getDepository(depotId, orgId);
    getSensor(sensorId, orgId);
    Long ret = null;
    Session session = Manager.getFactory(getServerProperties()).openSession();
    session.beginTransaction();
    DepositoryImpl depot = retrieveDepository(session, depotId, orgId);
    SensorImpl sensor = retrieveSensor(session, sensorId, orgId);
    Long result = (Long) session
        .createQuery(
            "SELECT count(*) FROM MeasurementImpl WHERE timestamp >= :start AND timestamp <= :end AND depository = :depository AND sensor = :sensor")
        .setParameter("start", start).setParameter("end", end).setParameter("depository", depot)
        .setParameter("sensor", sensor).iterate().next();
    ret = result;
    session.getTransaction().commit();
    session.close();
    return ret;
  }

}
