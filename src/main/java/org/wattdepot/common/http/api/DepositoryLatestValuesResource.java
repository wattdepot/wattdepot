package org.wattdepot.common.http.api;

import org.restlet.resource.Get;
import org.wattdepot.common.domainmodel.InterpolatedValueList;

/**
 * DepositoryLatestValuesResource - HTTP Interface for getting an
 * InterpolatedValueList of the latest values for a sensor or sensor group. <br>
 * (/wattdepot/{org-id}/depository/{depository-id}/values/latest/)
 *
 * @author Cam Moore
 *
 */
@SuppressWarnings("PMD.UnusedModifier")
public interface DepositoryLatestValuesResource {
  /**
   * Defines GET <br/>
   * /wattdepot/{org-id}/depository/{depository-id}/values/latest/?sensor={sensor_id}.
   *
   * @return The InterpolatedValueList.
   */
  @Get("json")
  // Use JSON as transport encoding.
  public InterpolatedValueList retrieve();
}
