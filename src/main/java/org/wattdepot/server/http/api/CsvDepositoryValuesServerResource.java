package org.wattdepot.server.http.api;

import org.restlet.data.Disposition;
import org.restlet.data.MediaType;
import org.restlet.representation.StringRepresentation;
import org.wattdepot.common.domainmodel.InterpolatedValue;
import org.wattdepot.common.domainmodel.InterpolatedValueList;
import org.wattdepot.common.http.api.CsvDepositoryValuesResource;

import java.text.SimpleDateFormat;

/**
 * CsvDepositoryValuesServerResource - ServerResouce that handles the GET
 * /wattdepot/{org-id}/depository/{depository-id}/values/csv/ response.
 *
 * @author Brian Frølund
 */
public class CsvDepositoryValuesServerResource extends DepositoryValuesServer implements
    CsvDepositoryValuesResource {

  /*
  * (non-Javadoc)
  *
  * @see org.wattdepot.restlet.CsvDepositoryValuesResource#retrieve()
  */
  @Override
  public StringRepresentation retrieve() {
    InterpolatedValueList mList = doRetrieve();

    //RFC4180 CSV http://tools.ietf.org/html/rfc4180
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
    StringBuilder sb = new StringBuilder();
    sb.append("Start,End,Value\r\n");
    for (InterpolatedValue interpolatedValue : mList.getInterpolatedValues()) {
      sb.append(sdf.format(interpolatedValue.getStart()));
      sb.append(",");
      sb.append(sdf.format(interpolatedValue.getEnd()));
      sb.append(",");
      sb.append(interpolatedValue.getValue());
      sb.append("\r\n");
    }
    StringRepresentation response = new StringRepresentation(sb.toString(), MediaType.TEXT_CSV);
    response.setDisposition(new Disposition(Disposition.TYPE_ATTACHMENT));
    response.getDisposition().setFilename(String.format("%s_%s_%s.csv", depositoryId, sensorId, dataType));
    return response;
  }
}
