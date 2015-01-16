package org.wattdepot.common.exception;

/**
 * Created on 19/12/14.
 * MeasurementListSizeExceededException thrown when a MeasurementList does not
 * fulfill size requirements.
 * @author Brian Frølund <bf@cs.au.dk>
 */
public class MeasurementListSizeExceededException extends Exception {

  /** Serial Version ID. */
  private static final long serialVersionUID = -5400140340150411850L;

  /** Default Constructor. */
  public MeasurementListSizeExceededException() {
    super();
  }

  /**
   * @param message
   *          A String message about the exception.
   * @param cause
   *          The Throwable cause of the exception.
   */
  public MeasurementListSizeExceededException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * @param message
   *          A String message about the exception.
   */
  public MeasurementListSizeExceededException(String message) {
    super(message);
  }

  /**
   * @param cause
   *          The Throwable cause of the exception.
   */
  public MeasurementListSizeExceededException(Throwable cause) {
    super(cause);
  }

}
