package io.github.chichizhang0510.duration.exception;

/**
 * Exception thrown when an invalid duration format is encountered.
 */
public class InvalidDurationFormatException  extends IllegalArgumentException {
    
    /**
     * Constructs a new InvalidDurationFormatException with the specified message.
     */
    public InvalidDurationFormatException(String message) {
        super(message);
    }

    /**
     * Constructs a new InvalidDurationFormatException with the specified message and cause.
     */
    public InvalidDurationFormatException(String message, Throwable cause) {
        super(message, cause);
    }
}
