package org.wattdepot.common.http.api;

import org.restlet.resource.Get;
import org.wattdepot.common.domainmodel.InterpolatedValue;

/**
 * DepositoryLatestValueResource - HTTP Interface for getting an
 * InterpolatedValueList of the latest value for a sensor or sensor group. <br>
 * (/wattdepot/{org-id}/depository/{depository-id}/latest/value/)
 *
 * @author Cam Moore
 *
 */
@SuppressWarnings("PMD.UnusedModifier")
public interface DepositoryLatestValueResource {
  /**
   * Defines GET <br/>
   * /wattdepot/{org-id}/depository/{depository-id}/values/latest/?sensor={sensor_id}.
   *
   * @return The InterpolatedValueList.
   */
  @Get("json")
  // Use JSON as transport encoding.
  public InterpolatedValue retrieve();

}
