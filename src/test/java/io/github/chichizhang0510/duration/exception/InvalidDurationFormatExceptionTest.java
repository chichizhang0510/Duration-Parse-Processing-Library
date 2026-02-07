package io.github.chichizhang0510.duration.exception;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class InvalidDurationFormatExceptionTest {

  @Test
  void constructor_withMessage_setsMessage() {
    String message = "Invalid duration format";
    InvalidDurationFormatException ex = new InvalidDurationFormatException(message);

    assertEquals(message, ex.getMessage());
    assertNull(ex.getCause());
  }

  @Test
  void constructor_withMessageAndCause_setsBoth() {
    String message = "Invalid duration format";
    RuntimeException cause = new RuntimeException("root cause");

    InvalidDurationFormatException ex = new InvalidDurationFormatException(message, cause);

    assertEquals(message, ex.getMessage());
    assertSame(cause, ex.getCause());
  }
}
