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

import javax.measure.unit.Unit;

import org.hibernate.Session;
import org.wattdepot.common.domainmodel.CollectorProcessDefinition;
import org.wattdepot.common.domainmodel.Depository;
import org.wattdepot.common.domainmodel.Measurement;
import org.wattdepot.common.domainmodel.MeasurementType;
import org.wattdepot.common.domainmodel.Organization;
import org.wattdepot.common.domainmodel.Property;
import org.wattdepot.common.domainmodel.Sensor;
import org.wattdepot.common.domainmodel.SensorGroup;
import org.wattdepot.common.domainmodel.SensorLocation;
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
   * @param properties
   *          The ServerProperties.
   */
  public WattDepotPersistenceImpl(ServerProperties properties) {
    super();
    setServerProperties(properties);
    this.checkSession = properties.get(ServerProperties.CHECK_SESSIONS).equals(
        "true");
    UserPasswordImpl adminPassword = (UserPasswordImpl) getUserPassword(
        UserInfo.ROOT.getUid(), UserInfo.ROOT.getOrganizationId());
    if (checkSession && getSessionClose() != getSessionOpen()) {
      throw new RuntimeException("opens and closed mismatched.");
    }
    if (adminPassword == null) {
      try {
        defineUserPassword(UserPassword.ADMIN.getId(),
            UserPassword.ADMIN.getOrganizationId(),
            UserPassword.ADMIN.getPlainText());
        if (checkSession && getSessionClose() != getSessionOpen()) {
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
      if (checkSession && getSessionClose() != getSessionOpen()) {
        throw new RuntimeException("opens and closed mismatched.");
      }
    }
    Organization pub = null;
    try {
      pub = getOrganization(Organization.PUBLIC_GROUP.getSlug());
    }
    catch (IdNotFoundException e1) { // NOPMD
      // this is ok. We may need to create the Organization.
    }
    if (pub == null) {
      try {
        defineOrganization(Organization.PUBLIC_GROUP.getSlug(),
            Organization.PUBLIC_GROUP.getName(), new HashSet<String>());
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
      admin = getOrganization(Organization.ADMIN_GROUP.getSlug());
    }
    catch (IdNotFoundException e1) { // NOPMD
      // this is ok, we might need to create the organization.
    }
    if (checkSession && getSessionClose() != getSessionOpen()) {
      throw new RuntimeException("opens and closed mismatched.");
    }
    if (admin == null) {
      try {
        defineOrganization(Organization.ADMIN_GROUP.getSlug(),
            Organization.ADMIN_GROUP.getName(),
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
  public CollectorProcessDefinition defineCollectorProcessDefinition(
      String slug, String name, String sensorId, Long pollingInterval,
      String depositoryId, Set<Property> properties, String ownerId)
      throws UniqueIdException, MisMatchedOwnerException, IdNotFoundException,
      BadSlugException {
    // really need the Impls
    OrganizationImpl owner = (OrganizationImpl) getOrganization(ownerId);
    SensorImpl sensor = (SensorImpl) getSensor(sensorId, ownerId);
    DepositoryImpl depo = (DepositoryImpl) getDepository(depositoryId, ownerId);
    if (!ownerId.equals(sensor.getOwnerId())) {
      throw new MisMatchedOwnerException(ownerId + " does not own the sensor "
          + sensorId);
    }
    if (!Slug.validateSlug(slug)) {
      throw new BadSlugException(slug + " is not a valid slug.");
    }
    try {
      CollectorProcessDefinition cpd = getCollectorProcessDefinition(slug,
          owner.getSlug());
      if (cpd != null) {
        throw new UniqueIdException(slug
            + " is already a CollectorProcessDefinition id.");
      }
    }
    catch (IdNotFoundException e) { // NOPMD
      // this is expected.
    }
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    CollectorProcessDefinitionImpl impl = new CollectorProcessDefinitionImpl(
        slug, name, sensorId, pollingInterval, depositoryId, properties,
        ownerId);
    impl.setSensorFk(sensor.getPk());
    impl.setDepositoryFk(depo.getPk());
    impl.setOwnerFk(owner.getPk());
    storeCollectorProcessDefinition(session, impl);
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    return impl;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#defineWattDepository(java.lang.String,
   * java.lang.String, java.lang.String, org.wattdepot.datamodel.Organization)
   */
  @Override
  public Depository defineDepository(String slug, String name,
      MeasurementType measurementType, String ownerId)
      throws UniqueIdException, IdNotFoundException, BadSlugException {
    if (!Slug.validateSlug(slug)) {
      throw new BadSlugException(slug + " is not a valid slug.");
    }
    OrganizationImpl owner = (OrganizationImpl) getOrganization(ownerId);
    Depository d = null;
    try {
      d = getDepository(slug, owner.getSlug());
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
    DepositoryImpl impl = new DepositoryImpl(slug, name, measurementType,
        ownerId);
    impl.setOwnerFk(owner.getPk());
    session.saveOrUpdate(impl);
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
  public MeasurementType defineMeasurementType(String slug, String name,
      String units) throws UniqueIdException, BadSlugException {
    if (!Slug.validateSlug(slug)) {
      throw new BadSlugException(slug + " is not a valid slug.");
    }
    MeasurementType mt = null;
    mt = getMeasurementType(slug);
    if (mt != null) {
      throw new UniqueIdException(slug + " is already a MeasurementType id.");
    }
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    Unit<?> unit = Unit.valueOf(units);
    mt = new MeasurementType(slug, name, unit);
    session.save(mt);
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
  public Organization defineOrganization(String slug, String name,
      Set<String> users) throws UniqueIdException, BadSlugException {
    if (!Slug.validateSlug(slug)) {
      throw new BadSlugException(slug + " is not a valid slug.");
    }
    OrganizationImpl g;
    try {
      g = (OrganizationImpl) getOrganization(slug);
      if (g != null) {
        throw new UniqueIdException(slug + " is already a Organization id.");
      }
    }
    catch (IdNotFoundException e) { // NOPMD
      // is ok.
    }
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    g = new OrganizationImpl(slug, name, users);
    session.saveOrUpdate(g);
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    return g;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#defineSensor(java.lang.String,
   * java.lang.String, java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  public Sensor defineSensor(String slug, String name, String uri,
      String locationId, String modelId, Set<Property> properties,
      String ownerId) throws UniqueIdException, MisMatchedOwnerException,
      IdNotFoundException, BadSlugException {
    if (!Slug.validateSlug(slug)) {
      throw new BadSlugException(slug + " is not a valid slug.");
    }
    SensorLocationImpl loc = (SensorLocationImpl) getSensorLocation(locationId,
        ownerId);
    SensorModelImpl model = (SensorModelImpl) getSensorModel(modelId);
    if (model == null) {
      throw new IdNotFoundException(modelId
          + " is not a defined SensorModel id.");
    }
    OrganizationImpl org = (OrganizationImpl) getOrganization(ownerId);
    if (!ownerId.equals(loc.getOwnerId())) {
      throw new MisMatchedOwnerException(ownerId
          + " does not match location's.");
    }
    Sensor s = null;
    try {
      s = getSensor(slug, org.getSlug());
    }
    catch (IdNotFoundException e) { // NOPMD
      // this is expected.
    }
    if (s != null) {
      throw new UniqueIdException(slug + " is already a defined Sensor.");
    }
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    SensorImpl impl = new SensorImpl(slug, name, uri, locationId, modelId,
        properties, ownerId);
    impl.setLocationFk(loc.getPk());
    impl.setModelFk(model.getPk());
    impl.setOwnerFk(org.getPk());
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
  public SensorGroup defineSensorGroup(String slug, String name,
      Set<String> sensorIds, String ownerId) throws UniqueIdException,
      MisMatchedOwnerException, IdNotFoundException, BadSlugException {
    if (!Slug.validateSlug(slug)) {
      throw new BadSlugException(slug + " is not a valid slug.");
    }
    OrganizationImpl owner = (OrganizationImpl) getOrganization(ownerId);
    for (String sensorId : sensorIds) {
      Sensor sensor = getSensor(sensorId, ownerId);
      if (!ownerId.equals(sensor.getOwnerId())) {
        throw new MisMatchedOwnerException(ownerId
            + " is not the owner of all the sensors.");
      }
    }
    SensorGroup sg = null;
    try {
      sg = getSensorGroup(slug, owner.getSlug());
      if (sg != null) {
        throw new UniqueIdException(slug + " is already a SensorGroup id.");
      }
    }
    catch (IdNotFoundException e) { // NOPMD
      // this is ok since we are defining it.
    }
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    SensorGroupImpl impl = new SensorGroupImpl(slug, name, sensorIds, ownerId);
    impl.setOwnerFk(owner.getPk());
    session.save(impl);
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    return sg;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#defineLocation(java.lang.String,
   * java.lang.Double, java.lang.Double, java.lang.Double, java.lang.String,
   * java.lang.String)
   */
  @Override
  public SensorLocation defineSensorLocation(String slug, String name,
      Double latitude, Double longitude, Double altitude, String description,
      String ownerId) throws UniqueIdException, IdNotFoundException, BadSlugException {
    if (!Slug.validateSlug(slug)) {
      throw new BadSlugException(slug + " is not a valid slug.");
    }
    OrganizationImpl owner = (OrganizationImpl) getOrganization(ownerId);
    SensorLocation l = null;
    try {
      l = getSensorLocation(slug, ownerId);
    }
    catch (IdNotFoundException e) { // NOPMD
      // OK, since we are defining the SensorLocation.
    }
    if (l != null) {
      throw new UniqueIdException(slug + " is already a SensorLocation id.");
    }
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    SensorLocationImpl impl = new SensorLocationImpl(slug, name, latitude, longitude,
        altitude, description, ownerId);
    impl.setOwnerFk(owner.getPk());
    session.save(impl);
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    return l;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#defineSensorModel(java.lang.String,
   * java.lang.String, java.lang.String, java.lang.String,
   * org.wattdepot.datamodel.Organization)
   */
  @Override
  public SensorModel defineSensorModel(String slug, String name, String protocol,
      String type, String version) throws UniqueIdException, BadSlugException {
    if (!Slug.validateSlug(slug)) {
      throw new BadSlugException(slug + " is not a valid slug.");
    }
    SensorModel sm = null;
    sm = getSensorModel(slug);
    if (sm != null) {
      throw new UniqueIdException(slug + " is already a SensorModel id.");
    }
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    SensorModelImpl impl = new SensorModelImpl(name, protocol, type, version);
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
  public UserInfo defineUserInfo(String id, String firstName, String lastName,
      String email, String orgId, Set<Property> properties)
      throws UniqueIdException {
    UserInfo u = getUser(id, orgId);
    if (u != null) {
      throw new UniqueIdException(id + " is already a UserInfo id.");
    }
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    u = new UserInfoImpl(id, firstName, lastName, email, orgId, properties);
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
  public UserPassword defineUserPassword(String id, String orgId,
      String password) throws UniqueIdException {
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    UserPasswordImpl up = new UserPasswordImpl(id, orgId, password);
    session.saveOrUpdate(up);
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    return up;
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
    CollectorProcessDefinitionImpl s = (CollectorProcessDefinitionImpl) getCollectorProcessDefinition(
        id, groupId);
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
   * @see org.wattdepot.server.WattDepot#deleteWattDepository(java.lang.String,
   * java.lang.String)
   */
  @Override
  public void deleteDepository(String id, String groupId)
      throws IdNotFoundException, MisMatchedOwnerException {
    Depository d = getDepository(id, groupId);
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
    for (CollectorProcessDefinition sp : retrieveCollectorProcessDefinitions(
        session, id)) {
      session.delete(sp);
    }
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    // Remove Organization owned SensorGroups
    session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    for (SensorGroup sg : retrieveSensorGroups(session, id)) {
      session.delete(sg);
    }
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    // Remove Organization owned Measurements
    session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    for (Depository d : retrieveDepositories(session, id)) {
      DepositoryImpl impl = new DepositoryImpl(d);
      for (String sensorId : impl.listSensors(session)) {
        for (Measurement m : impl.getMeasurements(session, sensorId)) {
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
    for (Sensor s : retrieveSensors(session, id)) {
      session.delete(s);
    }
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    // Remove Organization owned SensorModels
    session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    for (SensorModel sm : retrieveSensorModels(session)) {
      if (!SensorModelHelper.models.containsKey(sm.getName())) {
        session.delete(sm);
      }
    }
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    // Remove Organization owned SensorLocations
    session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    for (SensorLocation l : retrieveSensorLocations(session, id)) {
      session.delete(l);
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
  public void deleteSensor(String id, String groupId)
      throws IdNotFoundException, MisMatchedOwnerException {
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
  public void deleteSensorGroup(String id, String groupId)
      throws IdNotFoundException, MisMatchedOwnerException {
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
   * @see org.wattdepot.server.WattDepot#deleteLocation(java.lang.String,
   * java.lang.String)
   */
  @Override
  public void deleteSensorLocation(String id, String groupId)
      throws IdNotFoundException, MisMatchedOwnerException {
    SensorLocationImpl l = (SensorLocationImpl) getSensorLocation(id, groupId);
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    session.delete(l);
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
  // * The Organization to delete.
  // */
  // private void deleteOrganization(Session session, Organization group) {
  // for (CollectorProcessDefinition sp :
  // getCollectorProcessDefinitions(session, group.getId()))
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
    UserInfoImpl u = (UserInfoImpl) getUser(id, orgId);
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

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#deleteUserPassword(java.lang.String)
   */
  @Override
  public void deleteUserPassword(String userId, String orgId)
      throws IdNotFoundException {
    UserPasswordImpl up = (UserPasswordImpl) getUserPassword(userId, orgId);
    if (up == null) {
      throw new IdNotFoundException(userId
          + " is not a defined user password id.");
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
   * @see
   * org.wattdepot.server.WattDepot#getCollectorProcessDefinition(java.lang.
   * String, java.lang.String)
   */
  @Override
  public CollectorProcessDefinition getCollectorProcessDefinition(String id,
      String ownerId) throws IdNotFoundException {
    getOrganization(ownerId);
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    CollectorProcessDefinitionImpl ret = retrieveCollectorProcessDefinition(
        session, id, ownerId);
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    if (ret == null) {
      throw new IdNotFoundException(id
          + " is not a defined CollectorProcessDefinition's id.");
    }
    return ret;
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
      ret.add(s.getSlug());
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
  public List<CollectorProcessDefinition> getCollectorProcessDefinitions(
      String groupId) {
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    List<CollectorProcessDefinitionImpl> r = retrieveCollectorProcessDefinitions(
        session, groupId);
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    List<CollectorProcessDefinition> ret = new ArrayList<CollectorProcessDefinition>();
    for (CollectorProcessDefinition cpd : r) {
      ret.add(cpd);
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
      ret.add(d);
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
  public Depository getDepository(String id, String ownerId)
      throws IdNotFoundException {
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
    return ret;
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
      ret.add(d.getSlug());
    }
    return ret;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#getMeasurementType(java.lang.String)
   */
  @Override
  public MeasurementType getMeasurementType(String slug) {
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    MeasurementType ret = retrieveMeasurementType(session, slug);
    session.getTransaction().commit();
    session.close();
    sessionClose++;
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
    List<MeasurementType> ret = retrieveMeasurementTypes(session);
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    return ret;
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
    ret = retrieveOrganization(session, id);
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
      ret.add(u.getSlug());
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
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    List<Organization> ret = new ArrayList<Organization>();
    for (OrganizationImpl o : r) {
      ret.add(o);
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
    return ret;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#getSensorGroup(java.lang.String,
   * java.lang.String)
   */
  @Override
  public SensorGroup getSensorGroup(String id, String ownerId)
      throws IdNotFoundException {
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
    return ret;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#getSensorGroupIds()
   */
  @Override
  public List<String> getSensorGroupIds(String groupId)
      throws IdNotFoundException {
    ArrayList<String> ret = new ArrayList<String>();
    for (SensorGroup s : getSensorGroups(groupId)) {
      ret.add(s.getSlug());
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
      ret.add(s);
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
      ret.add(s.getSlug());
    }
    return ret;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#getLocation(java.lang.String,
   * java.lang.String)
   */
  @Override
  public SensorLocation getSensorLocation(String id, String ownerId)
      throws IdNotFoundException {
    getOrganization(ownerId);
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    SensorLocationImpl ret = retrieveSensorLocation(session, id, ownerId);
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    if (ret == null) {
      throw new IdNotFoundException(id + " is not a defined SensorLocation id.");
    }
    return ret;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#getLocationIds()
   */
  @Override
  public List<String> getSensorLocationIds(String groupId) {
    ArrayList<String> ret = new ArrayList<String>();
    for (SensorLocation l : getSensorLocations(groupId)) {
      ret.add(l.getSlug());
    }
    return ret;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#getLocations(java.lang.String)
   */
  @Override
  public List<SensorLocation> getSensorLocations(String groupId) {
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    List<SensorLocationImpl> r = retrieveSensorLocations(session, groupId);
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    List<SensorLocation> ret = new ArrayList<SensorLocation>();
    for (SensorLocationImpl l : r) {
      ret.add(l);
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
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    SensorModelImpl ret = retrieveSensorModel(session, id);
    session.getTransaction().commit();
    session.close();
    sessionClose++;
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
      ret.add(s.getSlug());
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
      ret.add(s);
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
      ret.add(s);
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
  public UserInfo getUser(String id, String orgId) {
    UserInfoImpl ret = null;
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    @SuppressWarnings("rawtypes")
    List result = session.createQuery("from UserInfoImpl").list();
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    for (UserInfoImpl u : (List<UserInfoImpl>) result) {
      if (id.equals(u.getUid()) && orgId.equals(u.getOrganizationId())) {
        ret = u;
      }
    }
    return ret;
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
  public UserPassword getUserPassword(String id, String orgId) {
    UserPasswordImpl ret = null;
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    @SuppressWarnings("unchecked")
    List<UserPasswordImpl> result = (List<UserPasswordImpl>) session
        .createQuery("from UserPasswordImpl").list();
    for (UserPasswordImpl up : result) {
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
  public List<UserInfo> getUsers(String orgId) {
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    @SuppressWarnings("rawtypes")
    List result = session.createQuery("from UserInfoImpl").list();
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    ArrayList<UserInfo> ret = new ArrayList<UserInfo>();
    for (UserInfoImpl u : (List<UserInfoImpl>) result) {
      if (u.getOrganizationId().equals(orgId)) {
        ret.add(u);
      }
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
   * @param session
   *          The session with an open transaction.
   * @param id
   *          The CollectorProcessDefinition's id.
   * @param ownerId
   *          The owner Organization's id.
   * @return the CollectorProcessDefinition if defined.
   */
  @SuppressWarnings("unchecked")
  private CollectorProcessDefinitionImpl retrieveCollectorProcessDefinition(
      Session session, String id, String ownerId) {
    List<CollectorProcessDefinitionImpl> cpds = (List<CollectorProcessDefinitionImpl>) session
        .createQuery(
            "FROM CollectorProcessDefinitionImpl WHERE slug = :id AND ownerId = :owner")
        .setParameter("id", id).setParameter("owner", ownerId).list();
    if (cpds.size() == 1) {
      return cpds.get(0);
    }
    else {
      return null;
    }
  }

  /**
   * @param session
   *          The session with an open transaction.
   * @param ownerId
   *          The owner's Organization id.
   * @return a List of CollectorProcessDefinitions owned by ownerId.
   */
  @SuppressWarnings("unchecked")
  private List<CollectorProcessDefinitionImpl> retrieveCollectorProcessDefinitions(
      Session session, String ownerId) {
    // probably want to get the owner OrganizationImpl and create the list using
    // just a single session.createQuery() call with WHERE ownerFk ==
    // org.getPk().
    List<CollectorProcessDefinitionImpl> ret = (List<CollectorProcessDefinitionImpl>) session
        .createQuery(
            "FROM CollectorProcessDefinitionImpl WHERE ownerId = :owner")
        .setParameter("owner", ownerId).list();
    return ret;
  }

  /**
   * @param session
   *          The session with an open transaction.
   * @param id
   *          the Depository's id.
   * @param ownerId
   *          The owner's Organization id.
   * @return A List of the Depositories owned by groupId.
   */
  @SuppressWarnings("unchecked")
  private DepositoryImpl retrieveDepository(Session session, String id,
      String ownerId) {
    List<DepositoryImpl> ret = (List<DepositoryImpl>) session
        .createQuery(
            "from DepositoryImpl WHERE slug = :id AND ownerId = :owner")
        .setParameter("id", id).setParameter("owner", ownerId).list();
    if (ret.size() == 1) {
      return ret.get(0);
    }
    return null;
  }

  /**
   * @param session
   *          The session with an open transaction.
   * @param ownerId
   *          The owner's Organization id.
   * @return A List of the Depositories owned by groupId.
   */
  @SuppressWarnings("unchecked")
  private List<DepositoryImpl> retrieveDepositories(Session session,
      String ownerId) {
    List<DepositoryImpl> ret = (List<DepositoryImpl>) session
        .createQuery("from DepositoryImpl WHERE ownerId = :owner")
        .setParameter("owner", ownerId).list();
    return ret;
  }

  /**
   * @param session
   *          A Session with an open transaction.
   * @param id
   *          The id of the MeasurementType.
   * @return The MeasurementType with the given id.
   */
  @SuppressWarnings("unchecked")
  private MeasurementType retrieveMeasurementType(Session session, String id) {
    List<MeasurementType> ret = (List<MeasurementType>) session
        .createQuery("FROM MeasurementType WHERE slug = :id")
        .setParameter("id", id).list();
    if (ret.size() == 1) {
      return ret.get(0);
    }
    return null;
  }

  /**
   * @param session
   *          A Session with an open transaction.
   * @return The list of defined MeasurementTypes.
   */
  @SuppressWarnings("unchecked")
  private List<MeasurementType> retrieveMeasurementTypes(Session session) {
    List<MeasurementType> ret = (List<MeasurementType>) session.createQuery(
        "FROM MeasurementType").list();
    return ret;
  }

  /**
   * @param session
   *          A Session with an open transaction.
   * @param id
   *          The id of the Organization.
   * @return The Organization with the given id.
   */
  @SuppressWarnings("unchecked")
  private OrganizationImpl retrieveOrganization(Session session, String id) {
    List<OrganizationImpl> ret = (List<OrganizationImpl>) session
        .createQuery("from OrganizationImpl WHERE slug = :id")
        .setParameter("id", id).list();
    if (ret.size() == 1) {
      return ret.get(0);
    }
    return null;
  }

  /**
   * @param session
   *          A Session with an open transaction.
   * @return The List of defined Organizations.
   */
  @SuppressWarnings("unchecked")
  private List<OrganizationImpl> retrieveOrganizations(Session session) {
    List<OrganizationImpl> ret = (List<OrganizationImpl>) session.createQuery(
        "from OrganizationImpl").list();
    return ret;

  }

  /**
   * @param session
   *          The session with an open transaction.
   * @param id
   *          the Sensor's id.
   * @param ownerId
   *          The owner's Organization id.
   * @return The Sensor with the given id and owned by ownerId.
   */
  @SuppressWarnings("unchecked")
  private SensorImpl retrieveSensor(Session session, String id, String ownerId) {
    List<SensorImpl> ret = (List<SensorImpl>) session
        .createQuery("FROM SensorImpl WHERE slug = :id AND ownerId = :owner")
        .setParameter("id", id).setParameter("owner", ownerId).list();
    if (ret.size() == 1) {
      return ret.get(0);
    }
    return null;
  }

  /**
   * @param session
   *          The session with an open transaction.
   * @param ownerId
   *          The owner's Organization id.
   * @return A List of the Sensors owned by ownerId.
   */
  @SuppressWarnings("unchecked")
  private List<SensorImpl> retrieveSensors(Session session, String ownerId) {
    List<SensorImpl> ret = (List<SensorImpl>) session
        .createQuery("FROM SensorImpl WHERE ownerId = :owner")
        .setParameter("owner", ownerId).list();
    return ret;
  }

  /**
   * @param session
   *          The session with an open transaction.
   * @param id
   *          the SensorLocation's id.
   * @param ownerId
   *          The owner's Organization id.
   * @return A List of the SensorLocations owned by ownerId.
   */
  @SuppressWarnings("unchecked")
  private SensorLocationImpl retrieveSensorLocation(Session session, String id,
      String ownerId) {
    List<SensorLocationImpl> ret = (List<SensorLocationImpl>) session
        .createQuery(
            "FROM SensorLocationImpl WHERE slug = :id AND ownerId = :owner")
        .setParameter("id", id).setParameter("owner", ownerId).list();
    if (ret.size() == 1) {
      return ret.get(0);
    }
    return null;
  }

  /**
   * @param session
   *          The session with an open transaction.
   * @param ownerId
   *          The owner's Organization id.
   * @return A List of the SensorLocations owned by ownerId.
   */
  @SuppressWarnings("unchecked")
  private List<SensorLocationImpl> retrieveSensorLocations(Session session,
      String ownerId) {
    List<SensorLocationImpl> ret = (List<SensorLocationImpl>) session
        .createQuery("FROM SensorLocationImpl WHERE ownerId = :owner")
        .setParameter("owner", ownerId).list();
    return ret;
  }

  /**
   * @param session
   *          The session with an open transaction.
   * @param id
   *          the SensorGroup's id.
   * @param ownerId
   *          The owner's Organization id.
   * @return The SensorGroup with the given id, owned by ownerId.
   */
  @SuppressWarnings("unchecked")
  private SensorGroupImpl retrieveSensorGroup(Session session, String id,
      String ownerId) {
    List<SensorGroupImpl> result = (List<SensorGroupImpl>) session
        .createQuery(
            "FROM SensorGroupImpl WHERE slug = :id AND ownerId = :owner")
        .setParameter("id", id).setParameter("owner", ownerId).list();
    if (result.size() == 1) {
      return result.get(0);
    }
    return null;
  }

  /**
   * @param session
   *          The session with an open transaction.
   * @param ownerId
   *          The owner's Organization id.
   * @return a List of the SensorGroups owned by groupId.
   */
  @SuppressWarnings("unchecked")
  private List<SensorGroupImpl> retrieveSensorGroups(Session session,
      String ownerId) {
    List<SensorGroupImpl> result = (List<SensorGroupImpl>) session
        .createQuery("FROM SensorGroupImpl WHERE ownerId = :owner")
        .setParameter("owner", ownerId).list();
    return result;
  }

  /**
   * @param session
   *          The Session with an open transaction.
   * @param id
   *          the id of the SensorModel to retrieve.
   * @return A List of the SensorModels owned by the groupId.
   */
  @SuppressWarnings("unchecked")
  private SensorModelImpl retrieveSensorModel(Session session, String id) {
    List<SensorModelImpl> result = (List<SensorModelImpl>) session
        .createQuery("FROM SensorModelImpl WHERE slug = :id")
        .setParameter("id", id).list();
    if (result.size() == 1) {
      return result.get(0);
    }
    return null;
  }

  /**
   * @param session
   *          The Session with an open transaction.
   * @return A List of the SensorModels owned by the groupId.
   */
  @SuppressWarnings("unchecked")
  private List<SensorModelImpl> retrieveSensorModels(Session session) {
    List<SensorModelImpl> ret = (List<SensorModelImpl>) session.createQuery(
        "from SensorModelImpl").list();
    return ret;
  }

  /**
   * @param session
   *          The Session with an open transaction.
   * @param orgId
   *          The organization id.
   * @return a List of the user passwords in the given organization.
   */
  @SuppressWarnings("unchecked")
  private List<UserPasswordImpl> retrieveUserPasswords(Session session,
      String orgId) {
    @SuppressWarnings("rawtypes")
    List result = session.createQuery("from UserPasswordImpl").list();
    ArrayList<UserPasswordImpl> ret = new ArrayList<UserPasswordImpl>();
    for (UserPasswordImpl u : (List<UserPasswordImpl>) result) {
      if (u.getOrganizationId() != null && u.getOrganizationId().equals(orgId)) {
        ret.add(u);
      }
    }
    return ret;
  }

  /**
   * @param session
   *          The Session with an open transaction.
   * @param orgId
   *          The organization id.
   * @return a List of the users in the given organization.
   */
  @SuppressWarnings("unchecked")
  private List<UserInfoImpl> retrieveUsers(Session session, String orgId) {
    @SuppressWarnings("rawtypes")
    List result = session.createQuery("from UserInfoImpl").list();
    ArrayList<UserInfoImpl> ret = new ArrayList<UserInfoImpl>();
    for (UserInfoImpl u : (List<UserInfoImpl>) result) {
      if (u.getOrganizationId().equals(orgId)) {
        ret.add(u);
      }
    }
    return ret;
  }

  /**
   * Use this method after beginning a transaction.
   * 
   * @param session
   *          The Session, a transaction must be in progress.
   * @param cpd
   *          The CollectorProcessDefinitionImpl to save.
   */
  private void storeCollectorProcessDefinition(Session session,
      CollectorProcessDefinitionImpl cpd) {
    for (Property p : cpd.getProperties()) {
      session.saveOrUpdate(p);
    }
    session.saveOrUpdate(cpd);

  }

  /**
   * Use this method after beginning a transaction.
   * 
   * @param session
   *          The Session, a transaction must be in progress.
   * @param sensor
   *          The SensorImpl to save.
   */
  private void storeSensor(Session session, SensorImpl sensor) {
    for (Property p : sensor.getProperties()) {
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
    try {
      CollectorProcessDefinitionImpl cpd = (CollectorProcessDefinitionImpl) getCollectorProcessDefinition(
          process.getSlug(), process.getOwnerId());
      cpd.setSlug(process.getSlug());
      cpd.setName(process.getName());
      cpd.setSensorId(process.getSensorId());
      Session session = Manager.getFactory(getServerProperties()).openSession();
      sessionOpen++;
      session.beginTransaction();
      storeCollectorProcessDefinition(session, cpd);
      session.getTransaction().commit();
      session.close();
      sessionClose++;
    }
    catch (BadSlugException e) {
      // Shouldn't happen.
      e.printStackTrace();
    }
    return process;
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
  public Organization updateOrganization(Organization org)
      throws IdNotFoundException {
    OrganizationImpl impl = (OrganizationImpl) getOrganization(org.getSlug());
    impl.setName(org.getName());
    impl.setSlug(org.getSlug());
    impl.setUsers(org.getUsers());
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    session.saveOrUpdate(impl);
    session.getTransaction().commit();
    session.close();
    sessionClose++;

    return org;
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
    OrganizationImpl org = (OrganizationImpl) getOrganization(sensor
        .getOwnerId());
    SensorLocationImpl locImpl = (SensorLocationImpl) getSensorLocation(
        sensor.getSensorLocationId(), org.getSlug());
    SensorModelImpl modelImpl = (SensorModelImpl) getSensorModel(sensor
        .getModelId());
    SensorImpl impl = (SensorImpl) getSensor(sensor.getSlug(),
        sensor.getOwnerId());
    impl.setSlug(sensor.getSlug());
    impl.setName(sensor.getName());
    impl.setSensorLocationId(sensor.getSensorLocationId());
    impl.setLocationFk(locImpl.getPk());
    impl.setModelId(sensor.getModelId());
    impl.setModelFk(modelImpl.getPk());
    impl.setOwnerFk(org.getPk());
    impl.setOwnerId(org.getSlug());
    impl.setProperties(sensor.getProperties());
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    session.saveOrUpdate(impl);
    for (Property p : impl.getProperties()) {
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
  public SensorGroup updateSensorGroup(SensorGroup group)
      throws IdNotFoundException {
    OrganizationImpl org = (OrganizationImpl) getOrganization(group
        .getOwnerId());
    SensorGroupImpl impl = (SensorGroupImpl) getSensorGroup(group.getSlug(),
        org.getSlug());
    // validate the list of sensor ids.
    for (String id : group.getSensors()) {
      getSensor(id, org.getSlug());
    }
    try {
      impl.setSlug(group.getSlug());
      impl.setName(group.getName());
      impl.setOwnerId(org.getSlug());
      impl.setOwnerFk(org.getPk());
      impl.setSensors(group.getSensors());
      Session session = Manager.getFactory(getServerProperties()).openSession();
      sessionOpen++;
      session.beginTransaction();
      session.saveOrUpdate(impl);
      session.getTransaction().commit();
      session.close();
      sessionClose++;
    }
    catch (BadSlugException e) { // NOPMD
      // not sure what to do here.
    }

    return group;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#updateLocation(org.wattdepot.datamodel
   * .Location)
   */
  @Override
  public SensorLocation updateSensorLocation(SensorLocation loc)
      throws IdNotFoundException {
    try {
      OrganizationImpl org = (OrganizationImpl) getOrganization(loc
          .getOwnerId());
      SensorLocationImpl impl = (SensorLocationImpl) getSensorLocation(
          loc.getSlug(), loc.getOwnerId());
      impl.setSlug(loc.getSlug());
      impl.setName(loc.getName());
      impl.setDescription(loc.getDescription());
      impl.setLatitude(loc.getLatitude());
      impl.setLongitude(loc.getLongitude());
      impl.setAltitude(loc.getAltitude());
      impl.setOwnerId(loc.getOwnerId());
      impl.setOwnerFk(org.getPk());
      Session session = Manager.getFactory(getServerProperties()).openSession();
      sessionOpen++;
      session.beginTransaction();
      session.saveOrUpdate(impl);
      session.getTransaction().commit();
      session.close();
      sessionClose++;
    }
    catch (BadSlugException e) {
      // Shouldn't happen
      e.printStackTrace();
    }

    return loc;
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
    SensorModelImpl impl = (SensorModelImpl) getSensorModel(model.getSlug());
    try {
      impl.setName(model.getName());
      impl.setProtocol(model.getProtocol());
      impl.setSlug(model.getSlug());
      impl.setType(model.getType());
      impl.setVersion(model.getVersion());
      Session session = Manager.getFactory(getServerProperties()).openSession();
      sessionOpen++;
      session.beginTransaction();
      session.saveOrUpdate(impl);
      session.getTransaction().commit();
      session.close();
      sessionClose++;
    }
    catch (BadSlugException e) { // NOPMD
      // shouldn't happen.
    }
    return model;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#updateUserInfo(org.wattdepot.datamodel
   * .UserInfo)
   */
  @Override
  public UserInfo updateUserInfo(UserInfo user) {
    UserInfoImpl ui = (UserInfoImpl) getUser(user.getUid(),
        user.getOrganizationId());
    if (ui == null) {
      ui = new UserInfoImpl();
    }
    ui.setUid(user.getUid());
    ui.setFirstName(user.getFirstName());
    ui.setLastName(user.getLastName());
    ui.setEmail(user.getEmail());
    ui.setOrganizationId(user.getOrganizationId());
    ui.setProperties(user.getProperties());
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    session.saveOrUpdate(ui);
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    return ui;
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
    UserPasswordImpl upi = (UserPasswordImpl) getUserPassword(password.getId(),
        password.getOrganizationId());
    if (upi == null) {
      upi = new UserPasswordImpl();
    }
    upi.setId(password.getId());
    upi.setOrganizationId(password.getOrganizationId());
    upi.setPlainText(password.getPlainText());
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    session.saveOrUpdate(upi);
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    return password;
  }

}
