/**
 * MeasurementGarbageCollector.java This file is part of WattDepot.
 *
 * Copyright (C) 2014  Cam Moore
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
package org.wattdepot.server.garbage.collector;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.wattdepot.common.domainmodel.GarbageCollectionDefinition;
import org.wattdepot.common.domainmodel.Measurement;
import org.wattdepot.common.domainmodel.Organization;
import org.wattdepot.common.domainmodel.SensorGroup;
import org.wattdepot.common.exception.IdNotFoundException;
import org.wattdepot.common.util.DateConvert;
import org.wattdepot.common.util.tstamp.Tstamp;
import org.wattdepot.server.ServerProperties;
import org.wattdepot.server.WattDepotPersistence;

/**
 * MeasurementGarbageCollector - Removes measurements from the WattDepot
 * repository that are at a higher frequency sampling rate than desired.
 * 
 * @author Cam Moore
 * 
 */
public class MeasurementGarbageCollector extends TimerTask {

  /**
   * The window to get the measurements. Hopefully allows for quicker
   * performance.
   */
  public static final int PRUNE_WINDOW = 6 * 60;

  private WattDepotPersistence persistance;
  private GarbageCollectionDefinition definition;
  private boolean debug;

  /**
   * Create a MeasurementGarbageCollector.
   * 
   * @param properties The ServerProperties that define the type of persistence.
   * @param gcdId The id of the GarbageCollectionDefintion.
   * @param orgId The id of the Organization.
   * @param debug true if want debugging information.
   * @throws Exception If there is a problem instantiating the
   *         WattDepotPersistence.
   */
  public MeasurementGarbageCollector(ServerProperties properties, String gcdId, String orgId,
      boolean debug) throws Exception {
    // Get the WattDepotPersistence implementation.
    String depotClass = properties.get(ServerProperties.WATT_DEPOT_IMPL_KEY);
    this.persistance = (WattDepotPersistence) Class.forName(depotClass)
        .getConstructor(ServerProperties.class).newInstance(properties);
    this.definition = this.persistance.getGarbageCollectionDefinition(gcdId, orgId, true);
    this.debug = debug;
  }

  /**
   * @return the definition
   */
  public GarbageCollectionDefinition getDefinition() {
    return definition;
  }

  /**
   * @return The end of the collection window.
   */
  private Date getEndDate() {
    XMLGregorianCalendar now;
    Date ret = null;
    try {
      now = DateConvert.convertDate(new Date());
      XMLGregorianCalendar endCal = Tstamp
          .incrementDays(now, -1 * definition.getIgnoreWindowDays());
      ret = DateConvert.convertXMLCal(endCal);
    }
    catch (DatatypeConfigurationException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return ret;
  }

  /**
   * @param sensorId the id of the sensor to get measurements for.
   * @return The Measurements in the collection window.
   * @throws IdNotFoundException if there is a problem with the
   *         GarbageCollectionDefintion.
   */
  private List<Measurement> getMeasurementsToCheck(String sensorId) throws IdNotFoundException {
    Date start = getStartDate();
    Date end = getEndDate();
    if (debug) {
      SimpleDateFormat df = new SimpleDateFormat();
      System.out.println("Collection window for " + sensorId + " is from " + df.format(start)
          + " to " + df.format(end));
    }
    return this.persistance.getMeasurements(this.definition.getDepositoryId(),
        this.definition.getOrgId(), sensorId, end, start, false);
  }

  /**
   * @return A list of measurements to garbage collect. They are at a higher
   *         sample rate than desired.
   * @throws IdNotFoundException if there is a problem with the
   *         GarbageCollectionDefinition.
   */
  public List<Measurement> getMeasurementsToDelete() throws IdNotFoundException {
    Long startTime = 0l;
    Long endTime = 0l;
    Long diff = 0l;
    if (debug) {
      startTime = System.nanoTime();
    }
    List<Measurement> ret = new ArrayList<Measurement>();
    SensorGroup group = this.persistance.getSensorGroup(this.definition.getSensorId(),
        this.definition.getOrgId(), false);
    if (group != null) {
      for (String s : group.getSensors()) {
        ret.addAll(getMeasurementsToDelete(s));
      }
    }
    else {
      ret = getMeasurementsToDelete(this.definition.getSensorId());
    }
    if (debug) {
      endTime = System.nanoTime();
      diff = endTime - startTime;
      System.out.println("getMeasurementsToDelete() took " + (diff / 1E9) + " secs.");
    }
    return ret;
  }

  /**
   * @param sensorId The id of the sensor making the measurements.
   * @return The List of measurements to delete for the given sensor.
   * @throws IdNotFoundException if there is a problem with the sensorId.
   */
  private List<Measurement> getMeasurementsToDelete(String sensorId) throws IdNotFoundException {
    List<Measurement> ret = new ArrayList<Measurement>();
    List<Measurement> check = getMeasurementsToCheck(sensorId);
    int size = check.size();
    int index = 1;
    int baseIndex = 0;
    while (index < size - 1) {
      long secondsBetween = Math.abs((check.get(index).getDate().getTime() - check.get(baseIndex)
          .getDate().getTime()) / 1000);
      if (secondsBetween < definition.getMinGapSeconds()) {
        ret.add(check.get(index++));
      }
      else {
        baseIndex = index;
        index++;
      }
    }
    return ret;

  }

  /**
   * @return the persistance
   */
  public WattDepotPersistence getPersistance() {
    return persistance;
  }

  /**
   * @return The start of the collection window.
   */
  private Date getStartDate() {
    XMLGregorianCalendar now;
    Date ret = null;
    try {
      now = DateConvert.convertDate(new Date());
      XMLGregorianCalendar startCal = Tstamp
          .incrementDays(now, -1 * definition.getIgnoreWindowDays());
      startCal = Tstamp.incrementDays(startCal, -1 * definition.getCollectWindowDays());
      ret = DateConvert.convertXMLCal(startCal);
    }
    catch (DatatypeConfigurationException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return ret;
  }

  /**
   * Processes the command line arguments and runs the
   * MeasurementGarbageCollector one time.
   * 
   * @param args The command line arguments.
   * @throws Exception if there is a problem.
   */
  public static void main(String[] args) throws Exception {
    Options options = new Options();
    options.addOption("h", "help", false,
        "Usage: MeasurementGarbageCollector -o <orgId> -g <garbage collection definition id> [-d]");
    options.addOption("o", "orgId", true, "Organization Id.");
    options.addOption("g", "gcd", true, "GarbageCollectionDefinition Id.");
    options.addOption("d", "debug", false, "Display debugging information.");
    options.addOption("s", "single", false, "Run gc only once, right away.");

    CommandLine cmd = null;
    String orgId = null;
    String gcdId = null;
    boolean debug = false;
    boolean single = false;
    CommandLineParser parser = new PosixParser();
    HelpFormatter formatter = new HelpFormatter();
    try {
      cmd = parser.parse(options, args);
    }
    catch (ParseException e) {
      System.err.println("Command line parsing failed. Reason: " + e.getMessage() + ". Exiting.");
      System.exit(1);
    }
    if (cmd.hasOption("h")) {
      formatter.printHelp("MeasurementGarbageCollector", options);
      System.exit(0);
    }
    if (cmd.hasOption("o")) {
      orgId = cmd.getOptionValue("o");
    }
    else {
      orgId = Organization.ADMIN_GROUP.getId();
    }
    if (cmd.hasOption("g")) {
      gcdId = cmd.getOptionValue("g");
    }
    debug = cmd.hasOption("d");
    single = cmd.hasOption("s");
    if (debug) {
      System.out.println("Measurement Garbage Collection:");
      System.out.println("Org Id = " + orgId);
      System.out.println("GCD Id = " + gcdId);
      System.out.println("Single run = " + single);
    }
    MeasurementGarbageCollector mgc = new MeasurementGarbageCollector(new ServerProperties(),
        gcdId, orgId, debug);
    if (single) {
      mgc.pruneMeasurements();
    }
    else {
      // Set up the TimerTask to run the gc at the right time.
      if (debug) {
        System.out.println("Setting up Timer for " + mgc);
      }
      Timer t = new Timer();
      t.schedule(mgc, mgc.millisToNextRun(), mgc.getGCPeriod());
    }
  }

  /**
   * @return The number of milliseconds to wait till the next expected run time.
   */
  private long millisToNextRun() {
    if (debug) {
      System.out.print("milliseconds to next run is ");
    }
    if (definition.getNextRun() == null) {
      if (debug) {
        System.out.println("0");
      }
      return 0l;
    }
    else {
      long delay = definition.getNextRun().getTime() - (new Date().getTime());
      if (debug) {
        System.out.println(delay);
      }
      return delay;
    }
  }

  /**
   * @return The number of milliseconds to wait between GC runs.
   */
  private long getGCPeriod() {
    int period = 24 * 60 * 60 * 1000; // defaults to once a day
    if (definition.getCollectWindowDays() > 1) {
      period = (definition.getCollectWindowDays() - 1) * 24 * 60 * 60 * 1000;
      if (debug) {
        System.out.println("GC period is " + period + " milliseconds.");
      }
    }
    return period;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.util.TimerTask#run()
   */
  @Override
  public void run() {
    Date lastStarted = new Date();
    if (debug) {
      System.out.println("Starting run at " + new SimpleDateFormat().format(lastStarted));
    }
    Integer deleted = 0;
    try {
      for (Measurement m : getMeasurementsToDelete()) {
        this.persistance.deleteMeasurement(this.definition.getDepositoryId(),
            this.definition.getOrganizationId(), m.getId());
        deleted++;
      }
    }
    catch (IdNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    Date lastCompleted = new Date();
    this.definition.setLastStarted(lastStarted);
    this.definition.setLastCompleted(lastCompleted);
    this.definition.setNumMeasurementsCollected(deleted);
    try {
      this.persistance.updateGarbageCollectionDefinition(this.definition);
    }
    catch (IdNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    if (debug) {
      System.out.println("Finished run at " + new SimpleDateFormat().format(lastCompleted));
      System.out.println("Deleted " + deleted + " measurements.");
    }
  }

  /**
   * Prunes the measurements.
   * 
   * @throws DatatypeConfigurationException if there is a problem with
   *         DateConvert.
   * @throws IdNotFoundException if there is a problem with persistence.
   */
  public void pruneMeasurements() throws DatatypeConfigurationException, IdNotFoundException {
    Date lastStarted = new Date();
    Integer deleted = 0;
    if (debug) {
      System.out.println("Starting run at " + new SimpleDateFormat().format(lastStarted));
    }
    // figure out the 6 hour windows to delete
    XMLGregorianCalendar start = DateConvert.convertDate(getStartDate());
    XMLGregorianCalendar end = DateConvert.convertDate(getEndDate());
    List<XMLGregorianCalendar> windows = Tstamp.getTimestampList(start, end, PRUNE_WINDOW);
    if (debug) {
      System.out.println("Start = " + start + " end = " + end + " windows = " + windows);
    }
    String sensorId = this.definition.getSensorId();
    SensorGroup group = this.persistance
        .getSensorGroup(sensorId, this.definition.getOrgId(), false);
    if (group != null) {
      for (String s : group.getSensors()) {
        for (int i = 0; i < windows.size() - 1; i++) {
          Date windowStart = DateConvert.convertXMLCal(windows.get(i));
          Date windowEnd = DateConvert.convertXMLCal(windows.get(i + 1));
          List<Measurement> check = this.persistance.getMeasurements(
              this.definition.getDepositoryId(), this.definition.getOrgId(), s, windowStart,
              windowEnd, false);
          int size = check.size();
          int index = 1;
          int baseIndex = 0;
          while (index < size - 1) {
            long secondsBetween = Math.abs((check.get(index).getDate().getTime() - check.get(baseIndex)
                .getDate().getTime()) / 1000);
            if (secondsBetween < definition.getMinGapSeconds()) {
              this.persistance.deleteMeasurement(this.definition.getDepositoryId(),
                  this.definition.getOrganizationId(), check.get(index++).getId());
              deleted++;
              if (debug) {
                System.out.print("del " + deleted + " ");
              }
            }
            else {
              baseIndex = index;
              index++;
            }
          }
        }

      }
    }
    else {
      for (int i = 0; i < windows.size() - 1; i++) {
        Date windowStart = DateConvert.convertXMLCal(windows.get(i));
        Date windowEnd = DateConvert.convertXMLCal(windows.get(i + 1));
        List<Measurement> check = this.persistance.getMeasurements(
            this.definition.getDepositoryId(), this.definition.getOrgId(), sensorId, windowStart,
            windowEnd, false);
        int size = check.size();
        if (debug) {
          System.out.println(windowStart + " to " + windowEnd + " has " + size + " measurements");
        }
        int index = 1;
        int baseIndex = 0;
        while (index < size - 1) {
          long secondsBetween = Math.abs((check.get(index).getDate().getTime() - check.get(baseIndex)
              .getDate().getTime()) / 1000);
          if (secondsBetween < definition.getMinGapSeconds()) {
            this.persistance.deleteMeasurement(this.definition.getDepositoryId(),
                this.definition.getOrganizationId(), check.get(index++).getId());
            deleted++;
            if (debug) {
              System.out.print("del " + deleted + " ");
            }
          }
          else {
            baseIndex = index;
            index++;
          }
        }
      }
    }
    Date lastCompleted = new Date();
    if (debug) {
      System.out.println("Finished run at " + new SimpleDateFormat().format(lastCompleted));
      System.out.println("Deleted " + deleted + " measurements.");
    }
  }
}
