package org.wattdepot.server.http.api;

import org.restlet.data.Status;
import org.restlet.resource.ResourceException;
import org.wattdepot.common.domainmodel.Depository;
import org.wattdepot.common.domainmodel.InterpolatedValue;
import org.wattdepot.common.domainmodel.InterpolatedValueList;
import org.wattdepot.common.domainmodel.Labels;
import org.wattdepot.common.domainmodel.Sensor;
import org.wattdepot.common.domainmodel.SensorGroup;
import org.wattdepot.common.exception.IdNotFoundException;
import org.wattdepot.common.exception.MisMatchedOwnerException;
import org.wattdepot.common.exception.NoMeasurementException;
import org.wattdepot.common.http.api.DepositoryLatestValuesResource;

import java.util.Date;
import java.util.logging.Level;

/**
 * DepositoryLatestValuesServerResource - Returns the latest value for the sensor or sensor group stored in the
 * depository as an InterpolatedValueList.
 *
 * @author Cam Moore
 */

public class DepositoryLatestValuesServerResource extends WattDepotServerResource implements DepositoryLatestValuesResource {
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
  public InterpolatedValueList retrieve() {
    getLogger().log(
        Level.INFO,
        "GET /wattdepot/{" + orgId + "}/" + Labels.DEPOSITORY + "/{" + depositoryId + "}/"
            + Labels.VALUES + "/" + Labels.LATEST + "/?" + Labels.SENSOR + "={" + sensorId + "}");
    if (isInRole(orgId)) {
      try {
        InterpolatedValueList list = new InterpolatedValueList();
        Depository depository = depot.getDepository(depositoryId, orgId, true);
        Sensor sensor = depot.getSensor(sensorId, orgId, false);
        if (sensor != null) {
          InterpolatedValue value = null;
          try {
            value = depot.getLatestMeasuredValue(depositoryId, orgId, sensorId, false);
            list.getInterpolatedValues().add(value);
          }
          catch (NoMeasurementException e) {
            value = new InterpolatedValue(sensorId, Double.NaN, depository.getMeasurementType(), new Date());
            list.getInterpolatedValues().add(value); // adding null to the list?
          }
        }
        else {
          SensorGroup group = depot.getSensorGroup(sensorId, orgId, false);
          if (group != null) {
            for (String s : group.getSensors()) {
              sensor = depot.getSensor(s, orgId, false);
              if (sensor != null) {
                InterpolatedValue value = null;
                try {
                  value = depot.getLatestMeasuredValue(depositoryId, orgId, s, false);
                  list.getInterpolatedValues().add(value);
                }
                catch (NoMeasurementException e) {
                  value = new InterpolatedValue(s, Double.NaN, depository.getMeasurementType(), new Date());
                  list.getInterpolatedValues().add(value); // adding null to the list?
                }
              }
            }
          }
        }
        return list;
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
    else {
      setStatus(Status.CLIENT_ERROR_BAD_REQUEST, "Bad credentials.");
      return null;
    }
  }
}
