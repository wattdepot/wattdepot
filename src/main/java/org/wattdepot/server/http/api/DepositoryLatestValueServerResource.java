package org.wattdepot.server.http.api;

import org.restlet.data.Status;
import org.restlet.resource.ResourceException;
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

  /*
 * (non-Javadoc)
 *
 * @see org.restlet.resource.Resource#doInit()
 */
  @Override
  protected void doInit() throws ResourceException {
    super.doInit();
    this.sensorId = getQuery().getValues(Labels.SENSOR);
    this.depositoryId = getAttribute(Labels.DEPOSITORY_ID);
  }


  @Override
  public InterpolatedValue retrieve() {
    getLogger().log(
        Level.INFO,
        "GET /wattdepot/{" + orgId + "}/" + Labels.DEPOSITORY + "/{" + depositoryId + "}/"
            + Labels.LATEST + "/" + Labels.VALUE + "/?" + Labels.SENSOR + "={" + sensorId + "}");
    if (isInRole(orgId)) {
      try {
        Depository depository = depot.getDepository(depositoryId, orgId, true);
        Sensor sensor = depot.getSensor(sensorId, orgId, false);
        InterpolatedValue value = new InterpolatedValue(sensorId, 0.0, depository.getMeasurementType(), new Date());
        if (sensor != null) {
          try {
            return depot.getLatestMeasuredValue(depositoryId, orgId, sensorId, false);
          }
          catch (NoMeasurementException e) {
            value.addMissingSensor(sensorId);
            return value;
          }
        }
        else {
          SensorGroup group = depot.getSensorGroup(sensorId, orgId, false);
          if (group != null) {
            for (String s : group.getSensors()) {
              sensor = depot.getSensor(s, orgId, false);
              if (sensor != null) {
                try {
                  InterpolatedValue latest = depot.getLatestMeasuredValue(depositoryId, orgId, s, false);
                  value.setValue(value.getValue() + latest.getValue());
                  value.setStart(latest.getStart());
                  value.setEnd(latest.getEnd());
                }
                catch (NoMeasurementException e) {
                  value.addMissingSensor(s);
                }
              }
              else {
                value.addMissingSensor(s);
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
}
