package io.github.chichizhang0510.duration.internal;

import static org.junit.jupiter.api.Assertions.*;

import io.github.chichizhang0510.duration.exception.InvalidDurationFormatException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class DurationNormalizerTest {

  // -------------------- Happy path normalization --------------------

  @ParameterizedTest
  @CsvSource({
    // totalSeconds, sign, weeks, days, hours, minutes, seconds
    "0, 1, 0, 0, 0, 0, 0",

    // basic units
    "1, 1, 0, 0, 0, 0, 1",
    "60, 1, 0, 0, 0, 1, 0",
    "90, 1, 0, 0, 0, 1, 30",

    // minutes -> hours carry
    "5400, 1, 0, 0, 1, 30, 0", // 1h30m
    "3600, 1, 0, 0, 1, 0, 0",

    // hours -> days carry
    "90000, 1, 0, 1, 1, 0, 0", // 25h = 1d1h

    // days -> weeks carry
    "691200, 1, 1, 1, 0, 0, 0", // 8d = 1w1d (8*86400)

    // full mix
    "788645, 1, 1, 2, 3, 4, 5" // 1w2d3h4m5s
  })
  void normalize_positive(
      long totalSeconds, int sign, long weeks, long days, long hours, long minutes, long seconds) {
    DurationNormalizer.NormalizedParts p = DurationNormalizer.normalize(totalSeconds);

    assertEquals(sign, p.sign());
    assertEquals(weeks, p.weeks());
    assertEquals(days, p.days());
    assertEquals(hours, p.hours());
    assertEquals(minutes, p.minutes());
    assertEquals(seconds, p.seconds());

    // Range guarantees (core normalization contract)
    assertTrue(p.days() >= 0 && p.days() <= 6);
    assertTrue(p.hours() >= 0 && p.hours() <= 23);
    assertTrue(p.minutes() >= 0 && p.minutes() <= 59);
    assertTrue(p.seconds() >= 0 && p.seconds() <= 59);
  }

  @Test
  void normalize_negative_preservesSignAndUsesAbsoluteParts() {
    DurationNormalizer.NormalizedParts p = DurationNormalizer.normalize(-90);

    assertTrue(p.isNegative());
    assertEquals(-1, p.sign());
    assertEquals(0, p.weeks());
    assertEquals(0, p.days());
    assertEquals(0, p.hours());
    assertEquals(1, p.minutes());
    assertEquals(30, p.seconds());
  }

  @Test
  void normalize_zero_isZeroAndNotNegative() {
    DurationNormalizer.NormalizedParts p = DurationNormalizer.normalize(0);

    assertTrue(p.isZero());
    assertFalse(p.isNegative());
    assertEquals(1, p.sign());
    assertEquals(0, p.weeks());
    assertEquals(0, p.days());
    assertEquals(0, p.hours());
    assertEquals(0, p.minutes());
    assertEquals(0, p.seconds());
  }

  // -------------------- Edge/overflow behavior --------------------

  @Test
  void normalize_longMinValue_throws() {
    assertThrows(
        InvalidDurationFormatException.class, () -> DurationNormalizer.normalize(Long.MIN_VALUE));
  }
}
