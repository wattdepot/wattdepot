/**
xs * WattDepotImpl.java This file is part of WattDepot.
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
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.datatype.XMLGregorianCalendar;

import org.hibernate.Session;
import org.wattdepot.common.domainmodel.CollectorProcessDefinition;
import org.wattdepot.common.domainmodel.Depository;
import org.wattdepot.common.domainmodel.MeasurementPruningDefinition;
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

  private boolean timingp;
  private Logger timingLogger;
  private String padding = "";

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
    timingp = properties.get(ServerProperties.SERVER_TIMING_KEY).equals(ServerProperties.TRUE);
    if (timingp) {
      this.timingLogger = Logger.getLogger(getClass().getName());
    }
    // Start with the Organizations
    Organization pub = null;
    try {
      pub = getOrganization(Organization.PUBLIC_GROUP.getId(), false);
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
      admin = getOrganization(Organization.ADMIN_GROUP.getId(), false);
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
      adminUser = getUser(UserInfo.ROOT.getUid(), Organization.ADMIN_GROUP.getId(), false);
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
      adminPassword = getUserPassword(UserInfo.ROOT.getUid(), UserInfo.ROOT.getOrganizationId(),
          false);
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
    getOrganization(orgId, true);
    getSensor(sensorId, orgId, true);
    getDepository(depositoryId, orgId, true);
    if (!Slug.validateSlug(id)) {
      throw new BadSlugException(id + " is not a valid slug.");
    }
    try {
      CollectorProcessDefinition cpd = getCollectorProcessDefinition(id, orgId, true);
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
    getOrganization(orgId, true);
    getMeasurementType(measurementType.getId(), true);
    Depository d = null;
    try {
      d = getDepository(id, orgId, true);
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
   * @see
   * org.wattdepot.server.WattDepotPersistence#defineGarbageCollectionDefinition
   * (java.lang.String, java.lang.String, java.lang.String, java.lang.String,
   * java.lang.String, java.lang.Integer, java.lang.Integer, java.lang.Integer)
   */
  @Override
  public MeasurementPruningDefinition defineGarbageCollectionDefinition(String id, String name,
      String depositoryId, String sensorId, String orgId, Integer ignore, Integer collect,
      Integer gap) throws UniqueIdException, BadSlugException, IdNotFoundException {
    if (!Slug.validateSlug(id)) {
      throw new BadSlugException(id + " is not a valid id.");
    }
    getOrganization(orgId, true);
    getDepository(depositoryId, orgId, true);
    try {
      getSensor(sensorId, orgId, true);
    }
    catch (IdNotFoundException e) {
      getSensorGroup(sensorId, orgId, true);
    }
    MeasurementPruningDefinition gcd = null;
    try {
      gcd = getGarbageCollectionDefinition(id, orgId, true);
      if (gcd != null) {
        throw new UniqueIdException(name + " is already a GarbageCollectionDefinition name.");
      }
    }
    catch (IdNotFoundException e) { // NOPMD
      // ok since we are defining it.
    }
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    DepositoryImpl dep = retrieveDepository(session, depositoryId, orgId);
    OrganizationImpl org = retrieveOrganization(session, orgId);
    GarbageCollectionDefinitionImpl impl = new GarbageCollectionDefinitionImpl(id, name, dep,
        sensorId, org, ignore, collect, gap);
    gcd = impl.toGCD();
    session.save(impl);
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    return gcd;
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
      mt = getMeasurementType(id, true);
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
      g = getOrganization(id, true);
      if (g != null) {
        throw new UniqueIdException(id + " is already a Organization id.");
      }
    }
    catch (IdNotFoundException e) { // NOPMD
      // is ok.
    }
    for (String uid : users) {
      getUser(uid, id, true);
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
    getOrganization(orgId, true);
    getSensorModel(modelId, true);
    Sensor s = null;
    try {
      s = getSensor(id, orgId, true);
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
    Organization owner = getOrganization(orgId, true);
    for (String sensorId : sensorIds) {
      Sensor sensor = getSensor(sensorId, orgId, true);
      if (!orgId.equals(sensor.getOrganizationId())) {
        throw new MisMatchedOwnerException(orgId + " is not the owner of all the sensors.");
      }
    }
    SensorGroup sg = null;
    try {
      sg = getSensorGroup(id, owner.getId(), true);
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
      sm = getSensorModel(id, true);
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
    getOrganization(orgId, true);
    UserInfo u = null;
    try {
      u = getUser(id, orgId, true);
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
    getCollectorProcessDefinition(id, orgId, true);
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
    getDepository(id, orgId, true);
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    DepositoryImpl impl = retrieveDepository(session, id, orgId);
    for (DepositorySensorContribution dsc : retrieveContributions(session, impl)) {
      session.delete(dsc);
    }
    session.delete(impl);
    session.getTransaction().commit();
    session.close();
    sessionClose++;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.server.WattDepotPersistence#deleteGarbageCollectionDefinition
   * (java.lang.String)
   */
  @Override
  public void deleteGarbageCollectionDefinition(String id, String orgId) throws IdNotFoundException {
    getGarbageCollectionDefinition(id, orgId, true);
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    GarbageCollectionDefinitionImpl impl = retrieveGarbageCollectionDefinition(session, id, orgId);
    session.delete(impl);
    session.getTransaction().commit();
    session.close();
    sessionClose++;
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
    Long startTime = 0l;
    Long endTime = 0l;
    Long diff = 0l;
    if (timingp) {
      timingLogger.log(Level.SEVERE, padding + "Start deleteMeasurement(" + depotId + ", " + orgId
          + ", " + measId + ")");
      padding += "  ";
      startTime = System.nanoTime();
    }
//    getOrganization(orgId, true);
//    getDepository(depotId, orgId, true);
//    getMeasurement(depotId, orgId, measId, true);
    Session session = Manager.getFactory(getServerProperties()).openSession();
    session.beginTransaction();
    MeasurementImpl impl = retrieveMeasurement(session, depotId, orgId, measId);
    if (impl != null) {
      session.delete(impl);
    }
    else {
      throw new IdNotFoundException(measId + " is not a valid measurment id.");
    }
    session.getTransaction().commit();
    session.close();
    if (timingp) {
      endTime = System.nanoTime();
      diff = endTime - startTime;
      padding = padding.substring(0, padding.length() - 2);
      timingLogger.log(Level.SEVERE, padding + "deleteMeasurement(" + depotId + ", " + orgId + ", "
          + measId + ") took " + (diff / 1E9) + " secs.");
    }    
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#deleteMeasurementType(java.lang.String)
   */
  @Override
  public void deleteMeasurementType(String id) throws IdNotFoundException {
    getMeasurementType(id, true);
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    MeasurementTypeImpl impl = retrieveMeasurementType(session, id);
    List<DepositoryImpl> depositories = retrieveDepositories(session, impl);
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    for (DepositoryImpl depository : depositories) {
      try {
        deleteDepository(depository.getId(), depository.getOrg().getId());
      }
      catch (MisMatchedOwnerException e) {
        // Shouldn't happen
        e.printStackTrace();
      }
    }
    session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    impl = retrieveMeasurementType(session, id);
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
    getOrganization(id, true);
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
    // Remove Organization owned GarbageCollectionDefinitions
    session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    for (GarbageCollectionDefinitionImpl sp : retrieveGarbageCollectionDefinitions(session, id)) {
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
      for (String sensorId : listSensors(d.getId(), id, true)) {
        for (Measurement m : getMeasurements(d.getId(), id, sensorId, true)) {
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
    for (DepositoryImpl d : depositories) {
      for (DepositorySensorContribution dsc : retrieveContributions(session, d)) {
        session.delete(dsc);
      }
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
    getSensor(id, orgId, true);
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    SensorImpl impl = retrieveSensor(session, id, orgId);
    for (DepositorySensorContribution dsc : retrieveContributions(session, impl)) {
      session.delete(dsc);
    }
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
    getSensorGroup(id, orgId, true);
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
    getSensorModel(id, true);
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
    getUser(id, orgId, true);
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
    getUserPassword(userId, orgId, true);
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
  public CollectorProcessDefinition getCollectorProcessDefinition(String id, String orgId,
      boolean check) throws IdNotFoundException {
    if (check) {
      getOrganization(orgId, check);
    }
    CollectorProcessDefinition ret = getCollectorProcessDefinitionNoCheck(id, orgId);
    if (check && ret == null) {
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
  public List<String> getCollectorProcessDefinitionIds(String orgId, boolean check)
      throws IdNotFoundException {
    ArrayList<String> ret = new ArrayList<String>();
    for (CollectorProcessDefinition s : getCollectorProcessDefinitions(orgId, check)) {
      ret.add(s.getId());
    }
    return ret;
  }

  /**
   * @param id The CollectorProcessDefinition's id
   * @param orgId The Organization's id.
   * @return The defined CollectorProcessDefinition or null.
   */
  private CollectorProcessDefinition getCollectorProcessDefinitionNoCheck(String id, String orgId) {
    CollectorProcessDefinition ret = null;
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
  public List<CollectorProcessDefinition> getCollectorProcessDefinitions(String orgId, boolean check)
      throws IdNotFoundException {
    if (check) {
      getOrganization(orgId, check);
    }
    return getCollectorProcessDefinitionsNoCheck(orgId);
  }

  /**
   * @param orgId The Organization's id.
   * @return A list of the defined CollectorProcessDefinitions.
   */
  private List<CollectorProcessDefinition> getCollectorProcessDefinitionsNoCheck(String orgId) {
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
  public List<Depository> getDepositories(String orgId, boolean check) throws IdNotFoundException {
    Long startTime = 0l;
    Long endTime = 0l;
    Long diff = 0l;
    if (timingp) {
      timingLogger.log(Level.SEVERE, padding + "Start getDepositories(" + orgId + ")");
      padding += "  ";
      startTime = System.nanoTime();
    }
    if (check) {
      getOrganization(orgId, check);
    }
    List<Depository> ret = getDepositoriesNoCheck(orgId);
    if (timingp) {
      endTime = System.nanoTime();
      diff = endTime - startTime;
      padding = padding.substring(0, padding.length() - 2);
      timingLogger.log(Level.SEVERE, padding + "getDepositories(" + orgId + ") took "
          + (diff / 1E9) + " secs.");
    }
    return ret;
  }

  /**
   * @param orgId The Organization's id.
   * @return A list of the defined Depositories.
   */
  private List<Depository> getDepositoriesNoCheck(String orgId) {
    Long startTime = 0l;
    Long endTime = 0l;
    Long diff = 0l;
    if (timingp) {
      timingLogger.log(Level.SEVERE, padding + "Start getDepositoriesNoCheck(" + orgId + ")");
      padding += "  ";
      startTime = System.nanoTime();
    }
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
    if (timingp) {
      endTime = System.nanoTime();
      diff = endTime - startTime;
      padding = padding.substring(0, padding.length() - 2);
      timingLogger.log(Level.SEVERE, padding + "getDepositoriesNoCheck(" + orgId + ") took "
          + (diff / 1E9) + " secs.");
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
  public Depository getDepository(String id, String orgId, boolean check)
      throws IdNotFoundException {
    Long startTime = 0l;
    Long endTime = 0l;
    Long diff = 0l;
    if (timingp) {
      timingLogger.log(Level.SEVERE, padding + "Start getDepository(" + id + ", " + orgId + ")");
      padding += "  ";
      startTime = System.nanoTime();
    }
    if (check) {
      getOrganization(orgId, check);
    }
    Depository ret = getDepositoryNoCheck(id, orgId);
    if (check && ret == null) {
      throw new IdNotFoundException(id + " is not a defined Depository's id.");
    }
    if (timingp) {
      endTime = System.nanoTime();
      diff = endTime - startTime;
      padding = padding.substring(0, padding.length() - 2);
      timingLogger.log(Level.SEVERE, padding + "getDepository(" + id + ", " + orgId + ") took "
          + (diff / 1E9) + " secs.");
    }
    return ret;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#getWattDepositoryIds()
   */
  @Override
  public List<String> getDepositoryIds(String orgId, boolean check) throws IdNotFoundException {
    Long startTime = 0l;
    Long endTime = 0l;
    Long diff = 0l;
    if (timingp) {
      timingLogger.log(Level.SEVERE, padding + "Start getDepositoryIds(" + orgId + ")");
      padding += "  ";
      startTime = System.nanoTime();
    }
    if (check) {
      getOrganization(orgId, check);
    }
    ArrayList<String> ret = new ArrayList<String>();
    for (Depository d : getDepositoriesNoCheck(orgId)) {
      ret.add(d.getId());
    }
    if (timingp) {
      endTime = System.nanoTime();
      diff = endTime - startTime;
      padding = padding.substring(0, padding.length() - 2);
      timingLogger.log(Level.SEVERE, padding + "getDepositoryIds(" + orgId + ") took "
          + (diff / 1E9) + " secs.");
    }
    return ret;
  }

  /**
   * @param id The Depository's id.
   * @param orgId The Organization's id.
   * @return The defined Depository or null.
   */
  private Depository getDepositoryNoCheck(String id, String orgId) {
    Long startTime = 0l;
    Long endTime = 0l;
    Long diff = 0l;
    if (timingp) {
      timingLogger.log(Level.SEVERE, padding + "Start getDepositoryNoCheck(" + id + ", " + orgId
          + ")");
      padding += "  ";
      startTime = System.nanoTime();
    }
    Depository ret = null;
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
    if (timingp) {
      endTime = System.nanoTime();
      diff = endTime - startTime;
      padding = padding.substring(0, padding.length() - 2);
      timingLogger.log(Level.SEVERE, padding + "getDepositoryNoCheck(" + id + ", " + orgId
          + ") took " + (diff / 1E9) + " secs.");
    }
    return ret;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.server.WattDepotPersistence#getEarliestMeasuredValue(java
   * .lang.String, java.lang.String)
   */
  @Override
  public InterpolatedValue getEarliestMeasuredValue(String depotId, String orgId, String sensorId,
      boolean check) throws NoMeasurementException, IdNotFoundException {
    Long startTime = 0l;
    Long endTime = 0l;
    Long diff = 0l;
    if (timingp) {
      timingLogger.log(Level.SEVERE, padding + "Start getEarliestMeasuredValue(" + depotId + ", "
          + orgId + ", " + sensorId + ")");
      padding += "  ";
      startTime = System.nanoTime();
    }
    if (check) {
      getOrganization(orgId, check);
      getDepository(depotId, orgId, check);
      getSensor(sensorId, orgId, check);
    }
    InterpolatedValue value = getEarliestMeasuredValueNoCheck(depotId, orgId, sensorId);
    if (check && value == null) {
      throw new NoMeasurementException("No " + depotId + " measurements for " + sensorId);
    }
    if (timingp) {
      endTime = System.nanoTime();
      diff = endTime - startTime;
      padding = padding.substring(0, padding.length() - 2);
      timingLogger.log(Level.SEVERE, padding + "getEarliestMeasuredValue(" + depotId + ", " + orgId
          + ", " + sensorId + ") took " + (diff / 1E9) + " secs.");
    }
    return value;
  }

  /**
   * @param depotId The Depository's id.
   * @param orgId The Organization's id.
   * @param sensorId The Sensor id.
   * @return The earliest MeasuredValue for the given depository, organization
   *         and sensor.
   */
  private InterpolatedValue getEarliestMeasuredValueNoCheck(String depotId, String orgId,
      String sensorId) {
    Long startTime = 0l;
    Long endTime = 0l;
    Long diff = 0l;
    if (timingp) {
      timingLogger.log(Level.SEVERE, padding + "Start getEarliestMeasuredValueNoCheck(" + depotId
          + ", " + orgId + ", " + sensorId + ")");
      padding += "  ";
      startTime = System.nanoTime();
    }
    InterpolatedValue value = null;
    Session session = Manager.getFactory(getServerProperties()).openSession();
    session.beginTransaction();
    DepositoryImpl depot = retrieveDepository(session, depotId, orgId);
    SensorImpl sensor = retrieveSensor(session, sensorId, orgId);
    @SuppressWarnings("unchecked")
    List<MeasurementImpl> result = (List<MeasurementImpl>) session
        .createQuery(
            "FROM MeasurementImpl WHERE depository = :depot AND sensor = :sensor ORDER BY timestamp asc")
        .setParameter("depot", depot).setParameter("sensor", sensor).setMaxResults(1).list();
    // List<MeasurementImpl> result = (List<MeasurementImpl>) session
    // .createQuery(
    // "FROM MeasurementImpl WHERE depository = :depot AND sensor = :sensor "
    // + "AND timestamp IN (SELECT min(timestamp) FROM MeasurementImpl WHERE "
    // + "depository = :depot AND sensor = :sensor)").setParameter("depot",
    // depot)
    // .setParameter("sensor", sensor).list();
    if (result.size() > 0) {
      MeasurementImpl meas = result.get(0);
      value = new InterpolatedValue(sensorId, meas.getValue(), depot.getType().toMeasurementType(),
          meas.getTimestamp());
    }
    session.getTransaction().commit();
    session.close();
    if (timingp) {
      endTime = System.nanoTime();
      diff = endTime - startTime;
      padding = padding.substring(0, padding.length() - 2);
      timingLogger.log(Level.SEVERE, padding + "getEarliestMeasuredValueNoCheck(" + depotId + ", "
          + orgId + ", " + sensorId + ") took " + (diff / 1E9) + " secs.");
    }
    return value;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.server.WattDepotPersistence#getGarbageCollectionDefinition
   * (java.lang.String, java.lang.String, boolean)
   */
  @Override
  public MeasurementPruningDefinition getGarbageCollectionDefinition(String id, String orgId,
      boolean check) throws IdNotFoundException {
    Long startTime = 0l;
    Long endTime = 0l;
    Long diff = 0l;
    if (timingp) {
      timingLogger.log(Level.SEVERE, padding + "Start getGarbageCollectionDefinition(" + id + ", "
          + orgId + ")");
      padding += "  ";
      startTime = System.nanoTime();
    }
    if (check) {
      getOrganization(orgId, check);
    }
    MeasurementPruningDefinition gcd = getGarbageCollectionDefinitionNoCheck(id, orgId);
    if (timingp) {
      endTime = System.nanoTime();
      diff = endTime - startTime;
      padding = padding.substring(0, padding.length() - 2);
      timingLogger.log(Level.SEVERE, padding + "getGarbageCollectionDefinition(" + id + ", "
          + orgId + ") took " + (diff / 1E9) + " secs.");
    }
    if (gcd == null) {
      throw new IdNotFoundException(id + " is not a defined GarbageCollectionDefinition id.");
    }
    return gcd;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.server.WattDepotPersistence#getGarbageCollectionDefinitionIds
   * (java.lang.String, boolean)
   */
  @Override
  public List<String> getGarbageCollectionDefinitionIds(String orgId, boolean check)
      throws IdNotFoundException {
    ArrayList<String> ret = new ArrayList<String>();
    for (MeasurementPruningDefinition gcd : getGarbageCollectionDefinitions(orgId, check)) {
      ret.add(gcd.getId());
    }
    return ret;
  }

  /**
   * @param id The GarbageCollectionDefinition's id.
   * @param orgId The Organization's id.
   * @return The GarbageCollectionDefinition with the given id and orgId, or
   *         null if not defined.
   */
  private MeasurementPruningDefinition getGarbageCollectionDefinitionNoCheck(String id, String orgId) {
    MeasurementPruningDefinition ret = null;
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    GarbageCollectionDefinitionImpl gcd = retrieveGarbageCollectionDefinition(session, id, orgId);
    if (gcd != null) {
      ret = gcd.toGCD();
    }
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    return ret;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.server.WattDepotPersistence#getGarbageCollectionDefinitions
   * (java.lang.String, boolean)
   */
  @Override
  public List<MeasurementPruningDefinition> getGarbageCollectionDefinitions(String orgId,
      boolean check) throws IdNotFoundException {
    if (check) {
      getOrganization(orgId, check);
    }
    return getGarbageCollectionDefinitionsNoCheck(orgId);
  }

  /**
   * @param orgId The Organization's id.
   * @return A list of defined GarbageCollectionDefinitions.
   */
  private List<MeasurementPruningDefinition> getGarbageCollectionDefinitionsNoCheck(String orgId) {
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    List<GarbageCollectionDefinitionImpl> r = retrieveGarbageCollectionDefinitions(session, orgId);
    List<MeasurementPruningDefinition> ret = new ArrayList<MeasurementPruningDefinition>();
    for (GarbageCollectionDefinitionImpl gcd : r) {
      ret.add(gcd.toGCD());
    }
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    return ret;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.server.WattDepotPersistence#getLatestMeasuredValue(java.lang
   * .String, java.lang.String)
   */
  @Override
  public InterpolatedValue getLatestMeasuredValue(String depotId, String orgId, String sensorId,
      boolean check) throws NoMeasurementException, IdNotFoundException {
    Long startTime = 0l;
    Long endTime = 0l;
    Long diff = 0l;
    if (timingp) {
      timingLogger.log(Level.SEVERE, padding + "Start getLaestMeasuredValue(" + depotId + ", "
          + orgId + ", " + sensorId + ")");
      padding += "  ";
      startTime = System.nanoTime();
    }
    if (check) {
      getOrganization(orgId, check);
      getDepository(depotId, orgId, check);
      getSensor(sensorId, orgId, check);
    }
    InterpolatedValue value = getLatestMeasuredValueNoCheck(depotId, orgId, sensorId);
    if (check && value == null) {
      throw new NoMeasurementException("No " + depotId + " measurements for " + sensorId);
    }
    if (timingp) {
      endTime = System.nanoTime();
      diff = endTime - startTime;
      padding = padding.substring(0, padding.length() - 2);
      timingLogger.log(Level.SEVERE, padding + "getEarliestMeasuredValue(" + depotId + ", " + orgId
          + ", " + sensorId + ") took " + (diff / 1E9) + " secs.");
    }
    return value;
  }

  /**
   * @param depotId The depository's id.
   * @param orgId The organization's id.
   * @param sensorId The sensors's id.
   * @return The latest measured value or null if no measurements.
   */
  private InterpolatedValue getLatestMeasuredValueNoCheck(String depotId, String orgId,
      String sensorId) {
    Long startTime = 0l;
    Long endTime = 0l;
    Long diff = 0l;
    if (timingp) {
      timingLogger.log(Level.SEVERE, padding + "Start getLatestMeasuredValueNoCheck(" + depotId
          + ", " + orgId + ", " + sensorId + ")");
      padding += "  ";
      startTime = System.nanoTime();
    }
    InterpolatedValue value = null;
    Session session = Manager.getFactory(getServerProperties()).openSession();
    session.beginTransaction();
    DepositoryImpl depot = retrieveDepository(session, depotId, orgId);
    SensorImpl sensor = retrieveSensor(session, sensorId, orgId);
    @SuppressWarnings("unchecked")
    List<MeasurementImpl> result = (List<MeasurementImpl>) session
        .createQuery(
            "FROM MeasurementImpl WHERE depository = :depot AND sensor = :sensor ORDER BY timestamp desc")
        .setParameter("depot", depot).setParameter("sensor", sensor).setMaxResults(1).list();

    // List<MeasurementImpl> result = (List<MeasurementImpl>) session
    // .createQuery(
    // "FROM MeasurementImpl WHERE depository = :depot AND sensor = :sensor "
    // + "AND timestamp IN (SELECT max(timestamp) FROM MeasurementImpl WHERE "
    // + "depository = :depot AND sensor = :sensor)").setParameter("depot",
    // depot)
    // .setParameter("sensor", sensor).list();
    if (result.size() > 0) {
      MeasurementImpl meas = result.get(0);
      value = new InterpolatedValue(sensorId, meas.getValue(), depot.getType().toMeasurementType(),
          meas.getTimestamp());
    }
    session.getTransaction().commit();
    session.close();
    if (timingp) {
      endTime = System.nanoTime();
      diff = endTime - startTime;
      padding = padding.substring(0, padding.length() - 2);
      timingLogger.log(Level.SEVERE, padding + "getLatestMeasuredValueNoCheck(" + depotId + ", "
          + orgId + ", " + sensorId + ") took " + (diff / 1E9) + " secs.");
    }
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
  public Measurement getMeasurement(String depotId, String orgId, String measId, boolean check)
      throws IdNotFoundException {
    Long startTime = 0l;
    Long endTime = 0l;
    Long diff = 0l;
    if (timingp) {
      timingLogger.log(Level.SEVERE, padding + "Start getMeasurement(" + depotId + ", " + orgId
          + ", " + measId + ")");
      padding += "  ";
      startTime = System.nanoTime();
    }
    if (check) {
      getOrganization(orgId, check);
      getDepository(depotId, orgId, check);
    }
    Measurement ret = getMeasurementNoCheck(depotId, orgId, measId);
    if (timingp) {
      endTime = System.nanoTime();
      diff = endTime - startTime;
      padding = padding.substring(0, padding.length() - 2);
      timingLogger.log(Level.SEVERE, padding + "getMeasurement(" + depotId + ", " + orgId + ", "
          + measId + ") took " + (diff / 1E9) + " secs.");
    }
    return ret;
  }

  /**
   * @param depotId The depository's id.
   * @param orgId The organization's id.
   * @param measId The measurement's id.
   * @return The Measurement with the given ids or null.
   */
  private Measurement getMeasurementNoCheck(String depotId, String orgId, String measId) {
    Long startTime = 0l;
    Long endTime = 0l;
    Long diff = 0l;
    if (timingp) {
      timingLogger.log(Level.SEVERE, padding + "Start getMeasurementNoCheck(" + depotId + ", "
          + orgId + ", " + measId + ")");
      padding += "  ";
      startTime = System.nanoTime();
    }
    Measurement ret = null;
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
    if (timingp) {
      endTime = System.nanoTime();
      diff = endTime - startTime;
      padding = padding.substring(0, padding.length() - 2);
      timingLogger.log(Level.SEVERE, padding + "getMeasurementNoCheck(" + depotId + ", " + orgId
          + ", " + measId + ") took " + (diff / 1E9) + " secs.");
    }
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
  public List<Measurement> getMeasurements(String depotId, String orgId, String sensorId,
      boolean check) throws IdNotFoundException {
    Long startTime = 0l;
    Long endTime = 0l;
    Long diff = 0l;
    if (timingp) {
      timingLogger.log(Level.SEVERE, padding + "Start getMeasurements(" + depotId + ", " + orgId
          + ", " + sensorId + ")");
      padding += "  ";
      startTime = System.nanoTime();
    }
    if (check) {
      getOrganization(orgId, check);
      getDepository(depotId, orgId, check);
      getSensor(sensorId, orgId, check);
    }
    List<Measurement> ret = getMeasurementsNoCheck(depotId, orgId, sensorId);
    if (timingp) {
      endTime = System.nanoTime();
      diff = endTime - startTime;
      padding = padding.substring(0, padding.length() - 2);
      timingLogger.log(Level.SEVERE, padding + "getMeasurements(" + depotId + ", " + orgId + ", "
          + sensorId + ") took " + (diff / 1E9) + " secs.");
    }
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
      Date start, Date end, boolean check) throws IdNotFoundException {
    Long startTime = 0l;
    Long endTime = 0l;
    Long diff = 0l;
    if (timingp) {
      timingLogger.log(Level.SEVERE, padding + "Start getMeasurementsNoCheck(" + depotId + ", "
          + orgId + ", " + sensorId + ")");
      padding += "  ";
      startTime = System.nanoTime();
    }
    if (check) {
      getOrganization(orgId, check);
      getDepository(depotId, orgId, check);
      getSensor(sensorId, orgId, check);
    }
    List<Measurement> ret = getMeasurementsNoCheck(depotId, orgId, sensorId, start, end);
    if (timingp) {
      endTime = System.nanoTime();
      diff = endTime - startTime;
      padding = padding.substring(0, padding.length() - 2);
      timingLogger.log(Level.SEVERE, padding + "getMeasurementsNoCheck(" + depotId + ", " + orgId
          + ", " + sensorId + ") took " + (diff / 1E9) + " secs.");
    }
    return ret;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.server.WattDepotPersistence#getMeasurementsCount(java.lang
   * .String, boolean)
   */
  @Override
  public Long getMeasurementsCount(String orgId, boolean check) throws IdNotFoundException {
    Long startTime = 0l;
    Long endTime = 0l;
    Long diff = 0l;
    if (timingp) {
      timingLogger.log(Level.SEVERE, padding + "Start getMeasurementsCount(" + orgId + ")");
      padding += "  ";
      startTime = System.nanoTime();
    }
    if (check) {
      getOrganization(orgId, check);
    }
    Long ret = 0l;
    Session session = Manager.getFactory(getServerProperties()).openSession();
    session.beginTransaction();
    for (DepositoryImpl depot : retrieveDepositories(session, orgId)) {
      @SuppressWarnings("unchecked")
      List<Long> result = (List<Long>) session
          .createQuery("SELECT count(*) FROM MeasurementImpl WHERE depository = :depot")
          .setParameter("depot", depot).list();
      ret += result.get(0);
    }
    session.getTransaction().commit();
    session.close();

    if (timingp) {
      endTime = System.nanoTime();
      diff = endTime - startTime;
      padding = padding.substring(0, padding.length() - 2);
      timingLogger.log(Level.SEVERE, padding + "getMeasurementsCount(" + orgId + ") took "
          + (diff / 1E9) + " secs.");
    }
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
  public Long getMeasurementsCount(String depotId, String orgId, String sensorId, boolean check)
      throws IdNotFoundException {
    Long startTime = 0l;
    Long endTime = 0l;
    Long diff = 0l;
    if (timingp) {
      timingLogger.log(Level.SEVERE, padding + "Start getMeasurementsCount(" + depotId + ", "
          + orgId + ", " + sensorId + ")");
      padding += "  ";
      startTime = System.nanoTime();
    }
    if (check) {
      getOrganization(orgId, check);
      getDepository(depotId, orgId, check);
      getSensor(sensorId, orgId, check);
    }
    Long ret = getMeasurementsCountNoCheck(depotId, orgId, sensorId);
    if (timingp) {
      endTime = System.nanoTime();
      diff = endTime - startTime;
      padding = padding.substring(0, padding.length() - 2);
      timingLogger.log(Level.SEVERE, padding + "getMeasurementsCount(" + depotId + ", " + orgId
          + ", " + sensorId + ") took " + (diff / 1E9) + " secs.");
    }
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
      Date end, boolean check) throws IdNotFoundException {
    Long startTime = 0l;
    Long endTime = 0l;
    Long diff = 0l;
    if (timingp) {
      startTime = System.nanoTime();
      timingLogger.log(Level.SEVERE, padding + "Starting getMeasurementsCount(" + depotId + ", "
          + orgId + ", " + sensorId + ")");
      padding += "  ";
    }
    if (check) {
      getOrganization(orgId, check);
      getDepository(depotId, orgId, check);
      getSensor(sensorId, orgId, check);
    }
    Long ret = getMeasurementsCountNoCheck(depotId, orgId, sensorId, start, end);
    if (timingp) {
      endTime = System.nanoTime();
      diff = endTime - startTime;
      padding = padding.substring(0, padding.length() - 2);
      timingLogger.log(Level.SEVERE, padding + "getMeasurementsCount(" + depotId + ", " + orgId
          + ", " + sensorId + ") took " + (diff / 1E9) + " secs.");
    }
    return ret;
  }

  /**
   * @param depotId The depository's id.
   * @param orgId The organization's id.
   * @param sensorId The sensor's id.
   * @return the number of measurements made by the given sensor stored in the
   *         given depository.
   */
  private Long getMeasurementsCountNoCheck(String depotId, String orgId, String sensorId) {
    Long startTime = 0l;
    Long endTime = 0l;
    Long diff = 0l;
    if (timingp) {
      timingLogger.log(Level.SEVERE, padding + "Start getMeasurementsCountNoCheck(" + depotId
          + ", " + orgId + ", " + sensorId + ")");
      padding += "  ";
      startTime = System.nanoTime();
    }
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
    if (timingp) {
      endTime = System.nanoTime();
      diff = endTime - startTime;
      padding = padding.substring(0, padding.length() - 2);
      timingLogger.log(Level.SEVERE, padding + "getMeasurementsCountNoCheck(" + depotId + ", "
          + orgId + ", " + sensorId + ") took " + (diff / 1E9) + " secs.");
    }
    return ret;
  }

  /**
   * @param depotId The depository's id.
   * @param orgId The organization's id.
   * @param sensorId The sensor's id.
   * @param start The start of the interval.
   * @param end The end of the interval.
   * @return a list of the measurements made by the given sensor stored in the
   *         given depository during the interval.
   */
  private Long getMeasurementsCountNoCheck(String depotId, String orgId, String sensorId,
      Date start, Date end) {
    Long startTime = 0l;
    Long endTime = 0l;
    Long diff = 0l;
    if (timingp) {
      startTime = System.nanoTime();
      timingLogger.log(Level.SEVERE, padding + "Starting getMeasurementsCountNoCheck(" + depotId
          + ", " + orgId + ", " + sensorId + ")");
      padding += "  ";
    }
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
    if (timingp) {
      endTime = System.nanoTime();
      diff = endTime - startTime;
      padding = padding.substring(0, padding.length() - 2);
      timingLogger.log(Level.SEVERE, padding + "getMeasurementsCountNoCheck(" + depotId + ", "
          + orgId + ", " + sensorId + ") took " + (diff / 1E9) + " secs.");
    }
    return ret;
  }

  /**
   * @param depotId The depository's id.
   * @param orgId The Organization's id.
   * @param sensorId The sensor's id.
   * @return a List of all the Measurements.
   */
  private List<Measurement> getMeasurementsNoCheck(String depotId, String orgId, String sensorId) {
    List<Measurement> ret = new ArrayList<Measurement>();
    Long startTime = 0l;
    Long endTime = 0l;
    Long diff = 0l;
    if (timingp) {
      timingLogger.log(Level.SEVERE, padding + "Start getMeasurementsNoCheck(" + depotId + ", "
          + orgId + ", " + sensorId + ")");
      padding += "  ";
      startTime = System.nanoTime();
    }
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
    if (timingp) {
      endTime = System.nanoTime();
      diff = endTime - startTime;
      padding = padding.substring(0, padding.length() - 2);
      timingLogger.log(Level.SEVERE, padding + "getMeasurementsNoCheck(" + depotId + ", " + orgId
          + ", " + sensorId + ") took " + (diff / 1E9) + " secs.");
    }
    return ret;
  }

  /**
   * @param depotId The depository's id.
   * @param orgId The organization's id.
   * @param sensorId The sensor's id.
   * @param start The start of the interval.
   * @param end The end of the interval.
   * @return All the measurements during the interval for the given depository,
   *         organization and sensor.
   */
  private List<Measurement> getMeasurementsNoCheck(String depotId, String orgId, String sensorId,
      Date start, Date end) {
    Long startTime = 0l;
    Long endTime = 0l;
    Long diff = 0l;
    if (timingp) {
      timingLogger.log(Level.SEVERE, padding + "Start getMeasurementsNoCheck(" + depotId + ", "
          + orgId + ", " + sensorId + ")");
      padding += "  ";
      startTime = System.nanoTime();
    }
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
    if (timingp) {
      endTime = System.nanoTime();
      diff = endTime - startTime;
      padding = padding.substring(0, padding.length() - 2);
      timingLogger.log(Level.SEVERE, padding + "getMeasurementsNoCheck(" + depotId + ", " + orgId
          + ", " + sensorId + ") took " + (diff / 1E9) + " secs.");
    }
    return ret;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#getMeasurementType(java.lang.String)
   */
  @Override
  public MeasurementType getMeasurementType(String id, boolean check) throws IdNotFoundException {
    Long startTime = 0l;
    Long endTime = 0l;
    Long diff = 0l;
    if (timingp) {
      timingLogger.log(Level.SEVERE, padding + "Start getMeasurementType(" + id + ")");
      padding += "  ";
      startTime = System.nanoTime();
    }
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
    if (timingp) {
      endTime = System.nanoTime();
      diff = endTime - startTime;
      padding = padding.substring(0, padding.length() - 2);
      timingLogger.log(Level.SEVERE, padding + "getMeasurementType(" + id + ") took "
          + (diff / 1E9) + " secs.");
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
    Long startTime = 0l;
    Long endTime = 0l;
    Long diff = 0l;
    if (timingp) {
      timingLogger.log(Level.SEVERE, padding + "Start getMeasurementTypes()");
      padding += "  ";
      startTime = System.nanoTime();
    }
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
    if (timingp) {
      endTime = System.nanoTime();
      diff = endTime - startTime;
      padding = padding.substring(0, padding.length() - 2);
      timingLogger.log(Level.SEVERE, padding + "getMeasurementTypes() took " + (diff / 1E9)
          + " secs.");
    }
    return types;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#getOrganization(java.lang.String)
   */
  @Override
  public Organization getOrganization(String id, boolean check) throws IdNotFoundException {
    Long startTime = 0l;
    Long endTime = 0l;
    Long diff = 0l;
    if (timingp) {
      timingLogger.log(Level.SEVERE, padding + "Start getOrganization(" + id + ")");
      startTime = System.nanoTime();
    }
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
    if (timingp) {
      endTime = System.nanoTime();
      diff = endTime - startTime;
      timingLogger.log(Level.SEVERE, padding + "getOrganization(" + id + ") took " + (diff / 1E9)
          + " secs.");
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
    Long startTime = 0l;
    Long endTime = 0l;
    Long diff = 0l;
    if (timingp) {
      timingLogger.log(Level.SEVERE, padding + "Start getOrganizationIds()");
      padding += "  ";
      startTime = System.nanoTime();
    }
    ArrayList<String> ret = new ArrayList<String>();
    for (Organization u : getOrganizations()) {
      ret.add(u.getId());
    }
    if (timingp) {
      endTime = System.nanoTime();
      diff = endTime - startTime;
      padding = padding.substring(0, padding.length() - 2);
      timingLogger.log(Level.SEVERE, padding + "getOrganizationIds() took " + (diff / 1E9)
          + " secs.");
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
    Long startTime = 0l;
    Long endTime = 0l;
    Long diff = 0l;
    if (timingp) {
      timingLogger.log(Level.SEVERE, padding + "Start getOrganizations()");
      padding += "  ";
      startTime = System.nanoTime();
    }
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
    if (timingp) {
      endTime = System.nanoTime();
      diff = endTime - startTime;
      padding = padding.substring(0, padding.length() - 2);
      timingLogger
          .log(Level.SEVERE, padding + "getOrganizations() took " + (diff / 1E9) + " secs.");
    }
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
  public MeasurementRateSummary getRateSummary(String depotId, String orgId, String sensorId,
      boolean check) throws IdNotFoundException, NoMeasurementException {
    Long startTime = 0l;
    Long endTime = 0l;
    Long diff = 0l;
    if (timingp) {
      startTime = System.nanoTime();
      timingLogger.log(Level.SEVERE, padding + "Starting getRateSummary(" + depotId + ", " + orgId
          + ", " + sensorId + ")");
      padding += "  ";
    }
    if (check) {
      getOrganization(orgId, check);
      getDepository(depotId, orgId, check);
      getSensor(sensorId, orgId, check);
    }
    XMLGregorianCalendar now = Tstamp.makeTimestamp();
    XMLGregorianCalendar minAgo = Tstamp.incrementMinutes(now, -1);
    MeasurementRateSummary ret = new MeasurementRateSummary();
    ret.setDepositoryId(depotId);
    ret.setSensorId(sensorId);
    ret.setTimestamp(DateConvert.convertXMLCal(now));
    Long count = getMeasurementsCountNoCheck(depotId, orgId, sensorId,
        DateConvert.convertXMLCal(minAgo), DateConvert.convertXMLCal(now));
    ret.setOneMinuteCount(count);
    ret.setOneMinuteRate(count / 60.0);
    InterpolatedValue val = getLatestMeasuredValueNoCheck(depotId, orgId, sensorId);
    ret.setLatestValue(val.getValue());
    ret.setType(val.getMeasurementType());
    count = getMeasurementsCountNoCheck(depotId, orgId, sensorId);
    ret.setTotalCount(count);
    if (timingp) {
      endTime = System.nanoTime();
      diff = endTime - startTime;
      padding = padding.substring(0, padding.length() - 2);
      timingLogger.log(Level.SEVERE, padding + "getRateSummary(" + depotId + ", " + orgId + ", "
          + sensorId + ") took " + (diff / 1E9) + " secs.");
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
  public Sensor getSensor(String id, String orgId, boolean check) throws IdNotFoundException {
    Long startTime = 0l;
    Long endTime = 0l;
    Long diff = 0l;
    if (timingp) {
      timingLogger.log(Level.SEVERE, padding + "Start getSensor(" + id + ", " + orgId + ")");
      padding += "  ";
      startTime = System.nanoTime();
    }
    if (check) {
      getOrganization(orgId, check);
    }
    Sensor ret = getSensorNoCheck(id, orgId);
    if (check && ret == null) {
      throw new IdNotFoundException(id + " is not a defined Sensor id.");
    }
    if (timingp) {
      endTime = System.nanoTime();
      diff = endTime - startTime;
      padding = padding.substring(0, padding.length() - 2);
      timingLogger.log(Level.SEVERE, padding + "getSensor(" + id + ", " + orgId + ") took "
          + (diff / 1E9) + " secs.");
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
  public SensorGroup getSensorGroup(String id, String orgId, boolean check)
      throws IdNotFoundException {
    Long startTime = 0l;
    Long endTime = 0l;
    Long diff = 0l;
    if (timingp) {
      timingLogger.log(Level.SEVERE, padding + "Start getSensorGroup(" + id + ", " + orgId + ")");
      padding += "  ";
      startTime = System.nanoTime();
    }
    if (check) {
      getOrganization(orgId, check);
    }
    SensorGroup ret = getSensorGroupNoCheck(id, orgId);
    if (check && ret == null) {
      throw new IdNotFoundException(id + " is not a defined SensorGroup id.");
    }
    if (timingp) {
      endTime = System.nanoTime();
      diff = endTime - startTime;
      padding = padding.substring(0, padding.length() - 2);
      timingLogger.log(Level.SEVERE, padding + "getSensorGroup(" + id + ", " + orgId + ") took "
          + (diff / 1E9) + " secs.");
    }
    return ret;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#getSensorGroupIds()
   */
  @Override
  public List<String> getSensorGroupIds(String orgId, boolean check) throws IdNotFoundException {
    Long startTime = 0l;
    Long endTime = 0l;
    Long diff = 0l;
    if (timingp) {
      timingLogger.log(Level.SEVERE, padding + "Start getSensorGroupIds(" + orgId + ")");
      padding += "  ";
      startTime = System.nanoTime();
    }
    if (check) {
      getOrganization(orgId, check);
    }
    List<String> ret = getSensorGroupIdsNoCheck(orgId);
    if (timingp) {
      endTime = System.nanoTime();
      diff = endTime - startTime;
      padding = padding.substring(0, padding.length() - 2);
      timingLogger.log(Level.SEVERE, padding + "getSensorGroupIds(" + orgId + ") took "
          + (diff / 1E9) + " secs.");
    }
    return ret;
  }

  /**
   * @param orgId The organization's id.
   * @return A List of the SensorGroups' ids.
   */
  private List<String> getSensorGroupIdsNoCheck(String orgId) {
    Long startTime = 0l;
    Long endTime = 0l;
    Long diff = 0l;
    if (timingp) {
      timingLogger.log(Level.SEVERE, padding + "Start getSensorGroupIds(" + orgId + ")");
      padding += "  ";
      startTime = System.nanoTime();
    }
    ArrayList<String> ret = new ArrayList<String>();
    for (SensorGroup s : getSensorGroupsNoCheck(orgId)) {
      ret.add(s.getId());
    }
    if (timingp) {
      endTime = System.nanoTime();
      diff = endTime - startTime;
      padding = padding.substring(0, padding.length() - 2);
      timingLogger.log(Level.SEVERE, padding + "getSensorGroupIds(" + orgId + ") took "
          + (diff / 1E9) + " secs.");
    }
    return ret;
  }

  /**
   * @param id The sensor group's id.
   * @param orgId The organization's id.
   * @return The defined SensorGroup or null.
   */
  private SensorGroup getSensorGroupNoCheck(String id, String orgId) {
    Long startTime = 0l;
    Long endTime = 0l;
    Long diff = 0l;
    if (timingp) {
      timingLogger.log(Level.SEVERE, padding + "Start getSensorGroupNoCheck(" + id + ", " + orgId
          + ")");
      padding += "  ";
      startTime = System.nanoTime();
    }
    SensorGroup ret = null;
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
    if (timingp) {
      endTime = System.nanoTime();
      diff = endTime - startTime;
      padding = padding.substring(0, padding.length() - 2);
      timingLogger.log(Level.SEVERE, padding + "getSensorGroupNoCheck(" + id + ", " + orgId
          + ") took " + (diff / 1E9) + " secs.");
    }
    return ret;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#getSensorGroups(java.lang.String)
   */
  @Override
  public List<SensorGroup> getSensorGroups(String orgId, boolean check) throws IdNotFoundException {
    Long startTime = 0l;
    Long endTime = 0l;
    Long diff = 0l;
    if (timingp) {
      timingLogger.log(Level.SEVERE, padding + "Start getSensorGroups(" + orgId + ")");
      padding += "  ";
      startTime = System.nanoTime();
    }
    if (check) {
      getOrganization(orgId, check);
    }
    List<SensorGroup> ret = getSensorGroupsNoCheck(orgId);
    if (timingp) {
      endTime = System.nanoTime();
      diff = endTime - startTime;
      padding = padding.substring(0, padding.length() - 2);
      timingLogger.log(Level.SEVERE, padding + "getSensorGroups(" + orgId + ") took "
          + (diff / 1E9) + " secs.");
    }
    return ret;
  }

  /**
   * @param orgId The organization's id.
   * @return All the defined SensorGroups.
   */
  private List<SensorGroup> getSensorGroupsNoCheck(String orgId) {
    Long startTime = 0l;
    Long endTime = 0l;
    Long diff = 0l;
    if (timingp) {
      timingLogger.log(Level.SEVERE, padding + "Start getSensorGroupsNoCheck(" + orgId + ")");
      padding += "  ";
      startTime = System.nanoTime();
    }
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
    if (timingp) {
      endTime = System.nanoTime();
      diff = endTime - startTime;
      padding = padding.substring(0, padding.length() - 2);
      timingLogger.log(Level.SEVERE, padding + "getSensorGroupsNoCheck(" + orgId + ") took "
          + (diff / 1E9) + " secs.");
    }
    return ret;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#getSensorIds()
   */
  @Override
  public List<String> getSensorIds(String orgId, boolean check) throws IdNotFoundException {
    Long startTime = 0l;
    Long endTime = 0l;
    Long diff = 0l;
    if (timingp) {
      timingLogger.log(Level.SEVERE, padding + "Start getSensorIds(" + orgId + ")");
      padding += "  ";
      startTime = System.nanoTime();
    }
    List<String> ret = getSensorIdsNoCheck(orgId);
    if (timingp) {
      endTime = System.nanoTime();
      diff = endTime - startTime;
      padding = padding.substring(0, padding.length() - 2);
      timingLogger.log(Level.SEVERE, padding + "getSensorIds(" + orgId + ") took " + (diff / 1E9)
          + " secs.");
    }
    return ret;
  }

  /**
   * @param orgId The organization's id.
   * @return The defined Sensors' ids.
   */
  private List<String> getSensorIdsNoCheck(String orgId) {
    Long startTime = 0l;
    Long endTime = 0l;
    Long diff = 0l;
    if (timingp) {
      timingLogger.log(Level.SEVERE, padding + "Start getSensorIdsNoCheck(" + orgId + ")");
      padding += "  ";
      startTime = System.nanoTime();
    }
    ArrayList<String> ret = new ArrayList<String>();
    for (Sensor s : getSensorsNoCheck(orgId)) {
      ret.add(s.getId());
    }
    if (timingp) {
      endTime = System.nanoTime();
      diff = endTime - startTime;
      padding = padding.substring(0, padding.length() - 2);
      timingLogger.log(Level.SEVERE, padding + "getSensorIdsNoCheck(" + orgId + ") took "
          + (diff / 1E9) + " secs.");
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
  public SensorModel getSensorModel(String id, boolean check) throws IdNotFoundException {
    Long startTime = 0l;
    Long endTime = 0l;
    Long diff = 0l;
    if (timingp) {
      timingLogger.log(Level.SEVERE, padding + "Start getSensorModel(" + id + ")");
      padding += "  ";
      startTime = System.nanoTime();
    }
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
    if (timingp) {
      endTime = System.nanoTime();
      diff = endTime - startTime;
      padding = padding.substring(0, padding.length() - 2);
      timingLogger.log(Level.SEVERE, padding + "getSensorModel(" + id + ") took " + (diff / 1E9)
          + " secs.");
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
    Long startTime = 0l;
    Long endTime = 0l;
    Long diff = 0l;
    if (timingp) {
      timingLogger.log(Level.SEVERE, padding + "Start getSensorModelIds()");
      padding += "  ";
      startTime = System.nanoTime();
    }
    ArrayList<String> ret = new ArrayList<String>();
    for (SensorModel s : getSensorModels()) {
      ret.add(s.getId());
    }
    if (timingp) {
      endTime = System.nanoTime();
      diff = endTime - startTime;
      padding = padding.substring(0, padding.length() - 2);
      timingLogger.log(Level.SEVERE, padding + "getSensorModelIds() took " + (diff / 1E9)
          + " secs.");
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
    Long startTime = 0l;
    Long endTime = 0l;
    Long diff = 0l;
    if (timingp) {
      timingLogger.log(Level.SEVERE, padding + "Start getSensorModels()");
      padding += "  ";
      startTime = System.nanoTime();
    }
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
    if (timingp) {
      endTime = System.nanoTime();
      diff = endTime - startTime;
      padding = padding.substring(0, padding.length() - 2);
      timingLogger.log(Level.SEVERE, padding + "getSensorModels() took " + (diff / 1E9) + " secs.");
    }
    return ret;
  }

  /**
   * @param id The sensor's id.
   * @param orgId The organization's id.
   * @return The defined Sensor or null.
   */
  private Sensor getSensorNoCheck(String id, String orgId) {
    Long startTime = 0l;
    Long endTime = 0l;
    Long diff = 0l;
    if (timingp) {
      timingLogger.log(Level.SEVERE, padding + "Start getSensorNoCheck(" + id + ", " + orgId + ")");
      startTime = System.nanoTime();
    }
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
    if (timingp) {
      endTime = System.nanoTime();
      diff = endTime - startTime;
      timingLogger.log(Level.SEVERE, padding + "getSensorNoCheck(" + id + ", " + orgId + ") took "
          + (diff / 1E9) + " secs.");
    }
    return ret;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#getSensors(java.lang.String)
   */
  @Override
  public List<Sensor> getSensors(String orgId, boolean check) throws IdNotFoundException {
    Long startTime = 0l;
    Long endTime = 0l;
    Long diff = 0l;
    if (timingp) {
      timingLogger.log(Level.SEVERE, padding + "Start getSensors(" + orgId + ")");
      padding += "  ";
      startTime = System.nanoTime();
    }
    if (check) {
      getOrganization(orgId, check);
    }
    List<Sensor> ret = getSensorsNoCheck(orgId);
    if (timingp) {
      endTime = System.nanoTime();
      diff = endTime - startTime;
      padding = padding.substring(0, padding.length() - 2);
      timingLogger.log(Level.SEVERE, padding + "getSensors(" + orgId + ") took " + (diff / 1E9)
          + " secs.");
    }
    return ret;
  }

  /**
   * @param orgId The organization's id.
   * @return A list of the defined Sensors.
   */
  private List<Sensor> getSensorsNoCheck(String orgId) {
    Long startTime = 0l;
    Long endTime = 0l;
    Long diff = 0l;
    if (timingp) {
      timingLogger.log(Level.SEVERE, padding + "Start getSensorsNoCheck(" + orgId + ")");
      padding += "  ";
      startTime = System.nanoTime();
    }
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
    if (timingp) {
      endTime = System.nanoTime();
      diff = endTime - startTime;
      padding = padding.substring(0, padding.length() - 2);
      timingLogger.log(Level.SEVERE, padding + "getSensorsNoCheck(" + orgId + ") took "
          + (diff / 1E9) + " secs.");
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
   * @see org.wattdepot.server.WattDepotPersistence#getSummary(java.lang.String,
   * java.lang.String, java.lang.String, java.util.Date, java.util.Date)
   */
  @Override
  public SensorMeasurementSummary getSummary(String depotId, String orgId, String sensorId,
      Date start, Date end, boolean check) throws IdNotFoundException {
    Long startTime = 0l;
    Long endTime = 0l;
    Long diff = 0l;
    if (timingp) {
      timingLogger.log(Level.SEVERE, padding + "Start getSummary(" + depotId + ", " + orgId + ", "
          + sensorId + ")");
      padding += "  ";
      startTime = System.nanoTime();
    }
    List<Measurement> list = getMeasurements(depotId, orgId, sensorId, start, end, check);
    SensorMeasurementSummary ret = new SensorMeasurementSummary(sensorId, depotId, start, end,
        list.size());
    if (timingp) {
      endTime = System.nanoTime();
      diff = endTime - startTime;
      padding = padding.substring(0, padding.length() - 2);
      timingLogger.log(Level.SEVERE, padding + "getSummary(" + depotId + ", " + orgId + ", "
          + sensorId + ") took " + (diff / 1E9) + " secs.");
    }
    return ret;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#getUser(java.lang.String)
   */
  @Override
  public UserInfo getUser(String id, String orgId, boolean check) throws IdNotFoundException {
    Long startTime = 0l;
    Long endTime = 0l;
    Long diff = 0l;
    if (timingp) {
      timingLogger.log(Level.SEVERE, padding + "Start getUser(" + id + ", " + orgId + ")");
      padding += "  ";
      startTime = System.nanoTime();
    }
    if (check) {
      getOrganization(orgId, check);
    }
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
    if (check && ret == null) {
      throw new IdNotFoundException(id + " is not a defined user id.");
    }
    if (timingp) {
      endTime = System.nanoTime();
      diff = endTime - startTime;
      padding = padding.substring(0, padding.length() - 2);
      timingLogger.log(Level.SEVERE, padding + "getUser(" + id + ", " + orgId + ") took "
          + (diff / 1E9) + " secs.");
    }
    return ret;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#getUserIds()
   */
  @Override
  public List<String> getUserIds(String orgId, boolean check) throws IdNotFoundException {
    Long startTime = 0l;
    Long endTime = 0l;
    Long diff = 0l;
    if (timingp) {
      timingLogger.log(Level.SEVERE, padding + "Start getUserIds(" + orgId + ")");
      padding += "  ";
      startTime = System.nanoTime();
    }
    ArrayList<String> ret = new ArrayList<String>();
    for (UserInfo u : getUsers(orgId, check)) {
      ret.add(u.getUid());
    }
    if (timingp) {
      endTime = System.nanoTime();
      diff = endTime - startTime;
      padding = padding.substring(0, padding.length() - 2);
      timingLogger.log(Level.SEVERE, padding + "getUserIds(" + orgId + ") took " + (diff / 1E9)
          + " secs.");
    }
    return ret;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#getUserPassword(java.lang.String)
   */
  @Override
  public UserPassword getUserPassword(String id, String orgId, boolean check)
      throws IdNotFoundException {
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

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#getUsers()
   */
  @Override
  public List<UserInfo> getUsers(String orgId, boolean check) throws IdNotFoundException {
    if (check) {
      getOrganization(orgId, check);
    }
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
   * @see org.wattdepot.server.WattDepotPersistence#getValue(java.lang.String,
   * java.lang.String, java.util.Date)
   */
  @Override
  public Double getValue(String depotId, String orgId, String sensorId, Date timestamp,
      boolean check) throws NoMeasurementException, IdNotFoundException {
    if (check) {
      getOrganization(orgId, check);
      getDepository(depotId, orgId, check);
      getSensor(sensorId, orgId, check);
    }
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
  public Double getValue(String depotId, String orgId, String sensorId, Date start, Date end,
      boolean check) throws NoMeasurementException, IdNotFoundException {
    Double endVal = getValue(depotId, orgId, sensorId, end, check);
    Double startVal = getValue(depotId, orgId, sensorId, start, check);
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
      Long gapSeconds, boolean check) throws NoMeasurementException, MeasurementGapException,
      IdNotFoundException {
    Double endVal = getValue(depotId, orgId, sensorId, end, gapSeconds, check);
    Double startVal = getValue(depotId, orgId, sensorId, start, gapSeconds, check);
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
      Long gapSeconds, boolean check) throws NoMeasurementException, MeasurementGapException,
      IdNotFoundException {
    if (check) {
      getOrganization(orgId, check);
      getDepository(depotId, orgId, check);
      getSensor(sensorId, orgId, check);
    }
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
  public List<String> listSensors(String depotId, String orgId, boolean check)
      throws IdNotFoundException {
    Long startTime = 0l;
    Long endTime = 0l;
    Long diff = 0l;
    if (timingp) {
      timingLogger.log(Level.SEVERE, padding + "Start listSensors(" + depotId + ", " + orgId + ")");
      padding += "  ";
      startTime = System.nanoTime();
    }
    if (check) {
      getOrganization(orgId, check);
      getDepository(depotId, orgId, check);
    }
    Session session = Manager.getFactory(getServerProperties()).openSession();
    session.beginTransaction();
    DepositoryImpl depot = retrieveDepository(session, depotId, orgId);
    @SuppressWarnings("unchecked")
    // List<SensorImpl> result = (List<SensorImpl>) session
    // .createQuery(
    // "select distinct meas.sensor FROM MeasurementImpl meas WHERE meas.depository = :depot")
    // .setParameter("depot", depot).list();
    List<DepositorySensorContribution> result = (List<DepositorySensorContribution>) session
        .createQuery("from DepositorySensorContribution where depository = :depository")
        .setParameter("depository", depot).list();
    ArrayList<String> sensorIds = new ArrayList<String>();
    for (DepositorySensorContribution contrib : result) {
      sensorIds.add(contrib.getSensor().getId());
    }
    // for (SensorImpl sensor : result) {
    // if (!sensorIds.contains(sensor.getId())) {
    // sensorIds.add(sensor.getId());
    // }
    // }
    session.getTransaction().commit();
    session.close();
    if (timingp) {
      endTime = System.nanoTime();
      diff = endTime - startTime;
      padding = padding.substring(0, padding.length() - 2);
      timingLogger.log(Level.SEVERE, padding + "listSensors(" + depotId + ", " + orgId + ") took "
          + (diff / 1E9) + " secs.");
    }
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
    getOrganization(orgId, true);
    Depository d = getDepository(depotId, orgId, true);
    if (!meas.getMeasurementType().equals(d.getMeasurementType().getUnits())) {
      throw new MeasurementTypeException("Measurement's type " + meas.getMeasurementType()
          + " does not match " + d.getMeasurementType());
    }
    Session session = Manager.getFactory(getServerProperties()).openSession();
    session.beginTransaction();
    DepositoryImpl depot = retrieveDepository(session, depotId, orgId);
    SensorImpl sensor = retrieveSensor(session, meas.getSensorId(), orgId);
    DepositorySensorContribution contrib = retrieveContribution(session, depot, sensor);
    if (contrib == null) {
      contrib = new DepositorySensorContribution();
      contrib.setDepository(depot);
      contrib.setSensor(sensor);
      session.saveOrUpdate(contrib);
    }
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
   * @param depo The Depository.
   * @param sensor The Sensor.
   * @return The DepositorySensorContribution if the sensor has contributed
   *         measurements to the depository, or null.
   */
  @SuppressWarnings("unchecked")
  private DepositorySensorContribution retrieveContribution(Session session, DepositoryImpl depo,
      SensorImpl sensor) {
    List<DepositorySensorContribution> ret = (List<DepositorySensorContribution>) session
        .createQuery(
            "from DepositorySensorContribution where sensor = :sensor and depository = :depository")
        .setParameter("sensor", sensor).setParameter("depository", depo).list();
    if (ret.size() == 1) {
      return ret.get(0);
    }
    return null;
  }

  /**
   * @param session A session with an open transaction.
   * @param depository The depository to search for.
   * @return A List of DepositorySensorContributions for the given depository.
   */
  @SuppressWarnings("unchecked")
  private List<DepositorySensorContribution> retrieveContributions(Session session,
      DepositoryImpl depository) {
    List<DepositorySensorContribution> ret = (List<DepositorySensorContribution>) session
        .createQuery("from DepositorySensorContribution where depository = :depository")
        .setParameter("depository", depository).list();
    return ret;
  }

  /**
   * @param session A session with an open transaction.
   * @param sensor The sensor to search for.
   * @return A List of DepositorySensorContributions for the given sensor.
   */
  @SuppressWarnings("unchecked")
  private List<DepositorySensorContribution> retrieveContributions(Session session,
      SensorImpl sensor) {
    List<DepositorySensorContribution> ret = (List<DepositorySensorContribution>) session
        .createQuery("from DepositorySensorContribution where sensor = :sensor")
        .setParameter("sensor", sensor).list();
    return ret;
  }

  /**
   * @param session The session with an open transaction.
   * @param type The measurement type of the depository.
   * @return A List of all the depositories with the given measurment type.
   */
  @SuppressWarnings("unchecked")
  private List<DepositoryImpl> retrieveDepositories(Session session, MeasurementTypeImpl type) {
    List<DepositoryImpl> ret = (List<DepositoryImpl>) session
        .createQuery("from DepositoryImpl WHERE type = :type").setParameter("type", type).list();
    return ret;
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
   * @param id the GarbageCollectionDefinition's id.
   * @param orgId The Organization's id.
   * @return The GarbageCollectionDefinitionImpl or null if not defined.
   */
  private GarbageCollectionDefinitionImpl retrieveGarbageCollectionDefinition(Session session,
      String id, String orgId) {
    OrganizationImpl org = retrieveOrganization(session, orgId);
    @SuppressWarnings("unchecked")
    List<GarbageCollectionDefinitionImpl> result = (List<GarbageCollectionDefinitionImpl>) session
        .createQuery("FROM GarbageCollectionDefinitionImpl WHERE id = :id AND org = :org")
        .setParameter("id", id).setParameter("org", org).list();
    if (result.size() == 1) {
      return result.get(0);
    }
    return null;
  }

  /**
   * @param session The Session with an open transaction.
   * @param orgId The Organization's id.
   * @return A list of the defined GarbageCollectionDefinitionImpls.
   */
  private List<GarbageCollectionDefinitionImpl> retrieveGarbageCollectionDefinitions(
      Session session, String orgId) {
    OrganizationImpl org = retrieveOrganization(session, orgId);
    @SuppressWarnings("unchecked")
    List<GarbageCollectionDefinitionImpl> result = (List<GarbageCollectionDefinitionImpl>) session
        .createQuery("FROM GarbageCollectionDefinitionImpl WHERE org = :org")
        .setParameter("org", org).list();
    return result;
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
   * @return A List of all the defined users.
   */
  @SuppressWarnings("unchecked")
  private List<UserInfoImpl> retrieveUsers(Session session) {
    List<UserInfoImpl> result = (List<UserInfoImpl>) session.createQuery("FROM UserInfoImpl")
        .list();
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

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepotPersistence#stop()
   */
  @Override
  public void stop() {
    Manager.closeSession();
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
    getCollectorProcessDefinition(process.getId(), process.getOrganizationId(), true);
    getDepository(process.getDepositoryId(), process.getOrganizationId(), true);
    getSensor(process.getSensorId(), process.getOrganizationId(), true);
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
    Set<PropertyImpl> props = new HashSet<PropertyImpl>();
    for (Property p : process.getProperties()) {
      props.add(new PropertyImpl(p));
    }
    impl.setProperties(props);
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
   * org.wattdepot.server.WattDepotPersistence#updateGarbageCollectionDefinition
   * (org.wattdepot.common.domainmodel.GarbageCollectionDefinition)
   */
  @Override
  public MeasurementPruningDefinition updateGarbageCollectionDefinition(
      MeasurementPruningDefinition gcd) throws IdNotFoundException {
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    GarbageCollectionDefinitionImpl impl = retrieveGarbageCollectionDefinition(session,
        gcd.getId(), gcd.getOrganizationId());
    impl.setName(gcd.getName());
    impl.setDepository(retrieveDepository(session, gcd.getDepositoryId(), gcd.getOrganizationId()));
    impl.setSensor(gcd.getSensorId());
    impl.setIgnoreWindowDays(gcd.getIgnoreWindowDays());
    impl.setCollectWindowDays(gcd.getCollectWindowDays());
    impl.setMinGapSeconds(gcd.getMinGapSeconds());
    impl.setLastStarted(gcd.getLastStarted());
    impl.setLastCompleted(gcd.getLastCompleted());
    impl.setNumMeasurementsCollected(gcd.getNumMeasurementsCollected());
    session.saveOrUpdate(impl);
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    return null;
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
    getOrganization(org.getId(), true);
    for (String s : org.getUsers()) {
      getUser(s, org.getId(), true);
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
    getOrganization(sensor.getOrganizationId(), true);
    getSensorModel(sensor.getModelId(), true);
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
    getOrganization(group.getOrganizationId(), true);
    getSensorGroup(group.getId(), group.getOrganizationId(), true);
    // validate the list of sensor ids.
    for (String id : group.getSensors()) {
      getSensor(id, group.getOrganizationId(), true);
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
    getSensorModel(model.getId(), true);
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
    getUser(user.getUid(), user.getOrganizationId(), true);
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
    getUserPassword(password.getUid(), password.getOrganizationId(), true);
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

}
