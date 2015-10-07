package org.wattdepot.server.http.api;

import org.restlet.data.Status;
import org.restlet.resource.ResourceException;
import org.wattdepot.common.domainmodel.CollectorProcessDefinition;
import org.wattdepot.common.domainmodel.Depository;
import org.wattdepot.common.domainmodel.InterpolatedValue;
import org.wattdepot.common.domainmodel.Labels;
import org.wattdepot.common.domainmodel.Sensor;
import org.wattdepot.common.domainmodel.SensorGroup;
import org.wattdepot.common.exception.IdNotFoundException;
import org.wattdepot.common.exception.MisMatchedOwnerException;
import org.wattdepot.common.exception.NoMeasurementException;
import org.wattdepot.common.http.api.DepositoryLatestValueResource;

import java.util.Date;
import java.util.List;
import java.util.logging.Level;

/**
 * DepositoryLatestValueServerResource - Returns the latest value for the sensor or sensor group stored in the
 * depository as an InterpolatedValue with missing sensors.
 *
 * @author Cam Moore
 */
public class DepositoryLatestValueServerResource extends WattDepotServerResource implements DepositoryLatestValueResource {
  private String depositoryId;
  private String sensorId;
  private String window;

  /*
 * (non-Javadoc)
 *
 * @see org.restlet.resource.Resource#doInit()
 */
  @Override
  protected void doInit() throws ResourceException {
    super.doInit();
    this.sensorId = getQuery().getValues(Labels.SENSOR);
    this.window = getQuery().getValues(Labels.WINDOW);
    this.depositoryId = getAttribute(Labels.DEPOSITORY_ID);
  }


  @Override
  public InterpolatedValue retrieve() {
    getLogger().log(
        Level.INFO,
        "GET /wattdepot/{" + orgId + "}/" + Labels.DEPOSITORY + "/{" + depositoryId + "}/"
            + Labels.LATEST + "/" + Labels.VALUE + "/?" + Labels.SENSOR + "={" + sensorId + "}&"
            + Labels.WINDOW + "={" + window + "}");
    int width = 5;
    if (window != null && !window.equals("")) {
      try {
        width = Integer.parseInt(window);
      }
      catch (NumberFormatException nfe) {
        width = 5;
      }
    }
    if (isInRole(orgId)) {
      try {
        Depository depository = depot.getDepository(depositoryId, orgId, true);
        Sensor sensor = depot.getSensor(sensorId, orgId, false);
        InterpolatedValue value = new InterpolatedValue(sensorId, 0.0, depository.getMeasurementType(), new Date());
        if (sensor != null) {
          value.addDefinedSensor(sensorId);
          CollectorProcessDefinition cpd = findCPD(depositoryId, sensorId, orgId);
          try {
            if (cpd != null) {
              return depot.getLatestMeasuredValue(depositoryId, orgId, sensorId, width * cpd.getPollingInterval(), false);
            }
            else {
              return depot.getLatestMeasuredValue(depositoryId, orgId, sensorId, false);
            }
          }
          catch (NoMeasurementException e) {
            return value;
          }
        }
        else {
          SensorGroup group = depot.getSensorGroup(sensorId, orgId, false);
          if (group != null) {
            for (String s : group.getSensors()) {
              sensor = depot.getSensor(s, orgId, false);
              value.addDefinedSensor(s);
              if (sensor != null) {
                CollectorProcessDefinition cpd = findCPD(depositoryId, s, orgId);
                try {
                  InterpolatedValue latest;
                  if (cpd != null) {
                    latest = depot.getLatestMeasuredValue(depositoryId, orgId, s, width * cpd.getPollingInterval(), false);
                  }
                  else {
                    latest = depot.getLatestMeasuredValue(depositoryId, orgId, s, false);
                  }
                  value.setValue(value.getValue() + latest.getValue());
                  value.addReportingSensor(s);
                  value.setStart(latest.getStart());
                  value.setEnd(latest.getEnd());
                }
                catch (NoMeasurementException e) { //NOPMD

                }
              }
            }
            return value;
          }
        }
      }
      catch (IdNotFoundException e) {
        setStatus(Status.CLIENT_ERROR_BAD_REQUEST, depositoryId + " is not a Depository id.");
        return null;
      }
      catch (MisMatchedOwnerException e) {
        setStatus(Status.CLIENT_ERROR_BAD_REQUEST, depositoryId + " is not in organization " + orgId + ".");
        return null;
      }
    }
    return null;
  }

  /**
   * @param depositoryId The depository id.
   * @param sensorId     The sensor id.
   * @param orgId        The orgainzation id.
   * @return The CollectorProcessDefinition for the depository and sensor, or null if not defined.
   */
  private CollectorProcessDefinition findCPD(String depositoryId, String sensorId, String orgId) {
    try {
      List<CollectorProcessDefinition> cpds = depot.getCollectorProcessDefinitions(orgId, false);
      for (CollectorProcessDefinition cpd : cpds) {
        if (cpd.getDepositoryId().equals(depositoryId) && cpd.getSensorId().equals(sensorId)) {
          return cpd;
        }
      }
    }
    catch (IdNotFoundException e) {
      e.printStackTrace();
    }
    return null;
  }
}
