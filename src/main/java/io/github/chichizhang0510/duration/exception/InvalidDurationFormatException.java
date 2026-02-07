package io.github.chichizhang0510.duration.exception;

/** Exception thrown when an invalid duration format is encountered. */
public class InvalidDurationFormatException extends IllegalArgumentException {

  /** 
   * Constructs a new InvalidDurationFormatException with the specified message. 
   * 
   * @param message the message to be set
   */
  public InvalidDurationFormatException(String message) {
    super(message);
  }

  /** 
   * Constructs a new InvalidDurationFormatException with the specified message and cause. 
   * 
   * @param message the message to be set
   * @param cause the cause of the exception
   * */
  public InvalidDurationFormatException(String message, Throwable cause) {
    super(message, cause);
  }
}
