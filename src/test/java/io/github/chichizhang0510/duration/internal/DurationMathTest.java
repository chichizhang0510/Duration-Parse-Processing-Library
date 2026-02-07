package io.github.chichizhang0510.duration.internal;

import static org.junit.jupiter.api.Assertions.*;

import io.github.chichizhang0510.duration.Duration;
import io.github.chichizhang0510.duration.exception.InvalidDurationFormatException;
import org.junit.jupiter.api.Test;

public class DurationMathTest {

  // -------------------- add --------------------

  @Test
  void add_addsSecondsCorrectly() {
    Duration a = new Duration("2h");
    Duration b = new Duration("30m");

    Duration sum = DurationMath.add(a, b);

    assertEquals(9000L, sum.toSeconds());
    assertEquals("2h30m", sum.toNormalizedString());
  }

  @Test
  void add_doesNotMutateInputs() {
    Duration a = new Duration("2h");
    Duration b = new Duration("30m");

    DurationMath.add(a, b);

    assertEquals("2h", a.toNormalizedString());
    assertEquals("30m", b.toNormalizedString());
  }

  @Test
  void add_nullThrows() {
    Duration a = new Duration("1s");
    assertThrows(NullPointerException.class, () -> DurationMath.add(a, null));
    assertThrows(NullPointerException.class, () -> DurationMath.add(null, a));
  }

  // -------------------- subtract --------------------

  @Test
  void subtract_subtractsSecondsCorrectly() {
    Duration a = new Duration("1m");
    Duration b = new Duration("90s");

    Duration diff = DurationMath.subtract(a, b);

    assertEquals(-30L, diff.toSeconds());
    assertEquals("-30s", diff.toNormalizedString());
  }

  @Test
  void subtract_nullThrows() {
    Duration a = new Duration("1s");
    assertThrows(NullPointerException.class, () -> DurationMath.subtract(a, null));
    assertThrows(NullPointerException.class, () -> DurationMath.subtract(null, a));
  }

  // -------------------- overflow behavior --------------------

  @Test
  void add_overflowThrows() {
    Duration max = Duration.fromSeconds(Long.MAX_VALUE);
    Duration one = Duration.fromSeconds(1);

    assertThrows(InvalidDurationFormatException.class, () -> DurationMath.add(max, one));
  }

  @Test
  void subtract_overflowThrows() {
    Duration min = Duration.fromSeconds(Long.MIN_VALUE);
    Duration one = Duration.fromSeconds(1);

    assertThrows(InvalidDurationFormatException.class, () -> DurationMath.subtract(min, one));
  }
}
