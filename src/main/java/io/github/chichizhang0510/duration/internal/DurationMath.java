package io.github.chichizhang0510.duration.internal;

import io.github.chichizhang0510.duration.Duration;
import io.github.chichizhang0510.duration.exception.InvalidDurationFormatException;
import java.util.Objects;

/** Utility class for arithmetic operations on durations. */
public class DurationMath {

  private DurationMath() {}

  /**
   * Adds two durations.
   *
   * @param a the first duration
   * @param b the second duration
   * @return the result duration
   */
  public static Duration add(Duration a, Duration b) {
    Objects.requireNonNull(a, "a");
    Objects.requireNonNull(b, "b");

    long resultSeconds = addExact(extractTotalSeconds(a), extractTotalSeconds(b));
    return Duration.fromSeconds(resultSeconds);
  }

  /**
   * Subtracts two durations.
   *
   * @param a the first duration
   * @param b the second duration
   * @return the result duration
   */
  public static Duration subtract(Duration a, Duration b) {
    Objects.requireNonNull(a, "a");
    Objects.requireNonNull(b, "b");

    long resultSeconds = subtractExact(extractTotalSeconds(a), extractTotalSeconds(b));
    return Duration.fromSeconds(resultSeconds);
  }

  // ------------------- Helpers -------------------

  /**
   * Extracts the total seconds from a duration.
   *
   * @param duration the duration
   * @return the total seconds
   */
  private static long extractTotalSeconds(Duration duration) {
    return duration.toSeconds();
  }

  /**
   * Adds two long values.
   *
   * @param x the first value
   * @param y the second value
   * @return the result
   */
  private static long addExact(long x, long y) {
    try {
      return Math.addExact(x, y);
    } catch (ArithmeticException e) {
      throw new InvalidDurationFormatException("Duration arithmetic overflow (add).", e);
    }
  }

  /**
   * Subtracts two long values.
   *
   * @param x the first value
   * @param y the second value
   * @return the result
   */
  private static long subtractExact(long x, long y) {
    try {
      return Math.subtractExact(x, y);
    } catch (ArithmeticException e) {
      throw new InvalidDurationFormatException("Duration arithmetic overflow (subtract).", e);
    }
  }
}
