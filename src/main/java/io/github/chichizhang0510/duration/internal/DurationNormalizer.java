package io.github.chichizhang0510.duration.internal;

import io.github.chichizhang0510.duration.exception.InvalidDurationFormatException;

/**
 * Utility class for normalizing a total seconds to a normalized parts.
 */
public class DurationNormalizer {

    private static final long SEC_PER_MIN = 60L;
    private static final long SEC_PER_HOUR = 60L * 60L;
    private static final long SEC_PER_DAY = 24L * 60L * 60L;
    private static final long SEC_PER_WEEK = 7L * 24L * 60L * 60L;

    /**
     * Private constructor to prevent instantiation.
     */
    private DurationNormalizer() {}

    /**
     * Normalizes a total seconds to a normalized parts.
     * @param totalSeconds the total seconds
     * @return the normalized parts
     */
    public static NormalizedParts normalize(long totalSeconds) {
        if (totalSeconds == 0L) {
            return NormalizedParts.zero();
        }

        int sign = signOf(totalSeconds);

        // Handle Long.MIN_VALUE safely (Math.abs(Long.MIN_VALUE) overflows)
        long absSeconds = safeAbs(totalSeconds);

        long weeks = absSeconds / SEC_PER_WEEK;
        absSeconds %= SEC_PER_WEEK;

        long days = absSeconds / SEC_PER_DAY;
        absSeconds %= SEC_PER_DAY;

        long hours = absSeconds / SEC_PER_HOUR;
        absSeconds %= SEC_PER_HOUR;

        long minutes = absSeconds / SEC_PER_MIN;
        long seconds = absSeconds % SEC_PER_MIN;

        return new NormalizedParts(sign, weeks, days, hours, minutes, seconds);
    }

    /**
     * Returns the sign of a value.
     * @param value the value
     * @return the sign
     */
    private static int signOf(long value) {
        return value < 0L ? -1 : 1;
    }

    /**
     * Returns the absolute value of a value.
     * @param value the value
     * @return the absolute value
     */
    private static long safeAbs(long value) {
        if (value == Long.MIN_VALUE) {
            // This is extremely unlikely in normal usage, but we fail deterministically.
            // You could also choose to saturate, but throwing is clearer.
            throw new InvalidDurationFormatException("Duration is too large to normalize (overflow).");
        }
        return Math.abs(value);
    }

    /**
     * A record that contains the normalized parts.
     * @param sign the sign of the normalized parts
     * @param weeks the number of weeks
     * @param days the number of days
     * @param hours the number of hours
     * @param minutes the number of minutes
     * @param seconds the number of seconds
     */
    public record NormalizedParts(
                int sign,
                long weeks,
                long days,
                long hours,
                long minutes,
                long seconds
        ) {

            public static NormalizedParts zero() {
                return new NormalizedParts(1, 0L, 0L, 0L, 0L, 0L);
            }

            public boolean isZero() {
                return weeks == 0 && days == 0 && hours == 0 && minutes == 0 && seconds == 0;
            }

            public boolean isNegative() {
                return sign < 0;
            }
        }
}
