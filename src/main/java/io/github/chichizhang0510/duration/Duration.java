package io.github.chichizhang0510.duration;

import io.github.chichizhang0510.duration.internal.DurationParser;
import io.github.chichizhang0510.duration.internal.DurationFormatter;
import io.github.chichizhang0510.duration.internal.DurationMath;
import io.github.chichizhang0510.duration.exception.InvalidDurationFormatException;

import java.util.Objects;

/**
 * A duration represents a period of time.
 * It is immutable and thread-safe.
 */
public final class Duration implements Comparable<Duration> {
    private final long totalSeconds;

    /**
     * Parses a human-readable duration string (e.g. "2h30m", "1d 12h", "-90s").
     *
     * @param input duration string
     * @throws InvalidDurationFormatException if the input is not a valid duration format
     */
    public Duration(String input) {
        this(DurationParser.parseToTotalSeconds(input));
    }

    /**
     * Internal constructor used by factories and operations.
     */
    Duration(long totalSeconds) {
        this.totalSeconds = totalSeconds;
    }

    long totalSeconds() {
        return totalSeconds;
    }
    

    // ---------- Conversions (total values) ----------

    /**
     * Returns the total number of milliseconds represented by this duration.
     */
    public long toMilliseconds() {
        return Math.multiplyExact(totalSeconds, 1000L);
    }

    /**
     * Returns the total number of seconds represented by this duration.
     */
    public long toSeconds() {
        return totalSeconds;
    }

    /**
     * Returns the total number of minutes represented by this duration.
     */
    public long toMinutes() {
        return totalSeconds / 60L;
    }

    /**
     * Returns the total number of hours represented by this duration.
     */
    public long toHours() {
        return totalSeconds / 3600L;
    }

    /**
     * Returns the total number of days represented by this duration.
     */
    public long toDays() {
        return totalSeconds / 86400L;
    }

    /**
     * Returns the total number of weeks represented by this duration.
     */
    public long toWeeks() {
        return totalSeconds / 604800L;
    }


    // ---------- Arithmetic ----------
    /**
     * Returns a new duration that is the sum of this duration and another duration.
     */
    public Duration add(Duration other) {
        Objects.requireNonNull(other, "other");
        return DurationMath.add(this, other);
    }

    /**
     * Returns a new duration that is the difference of this duration and another duration.
     */
    public Duration subtract(Duration other) {
        Objects.requireNonNull(other, "other");
        return DurationMath.subtract(this, other);
    }


    // ---------- Formatting ----------

    /**
     * Returns a string representation of this duration.
     * <p>
     * Design choice: return normalized compact form for stability.
     */
    @Override
    public String toString() {
        return toNormalizedString();
    }

    /**
     * Returns a normalized compact representation, e.g. "1m30s".
     */
    public String toNormalizedString() {
        return DurationFormatter.toNormalizedString(this);
    }

    /**
     * Returns a human-readable representation, e.g. "2 hours 30 minutes".
     */
    public String format() {
        return DurationFormatter.format(this);
    }


    // ---------- Comparable / equality ----------

    /**
     * Compares this duration to another duration.
     */
    @Override
    public int compareTo(Duration other) {
        Objects.requireNonNull(other, "other");
        return Long.compare(this.totalSeconds, other.totalSeconds);
    }

    /**
     * Returns true if this duration is equal to another duration.
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Duration)) return false;
        Duration other = (Duration) o;
        return this.totalSeconds == other.totalSeconds;
    }

    /**
     * Returns the hash code of this duration.
     */
    @Override
    public int hashCode() {
        return Objects.hash(totalSeconds);
    }


    // ---------- Static factories ----------
    
    /**
     * Returns a new duration from a given number of milliseconds.
     */
    public static Duration fromMilliseconds(long milliseconds) {
        if (milliseconds % 1000L != 0L) {
            throw new InvalidDurationFormatException("Milliseconds must be a multiple of 1000 (no fractional seconds).");
        }
        return new Duration(milliseconds / 1000L);
    }

    /**
     * Returns a new duration from a given number of seconds.
     */
    public static Duration fromSeconds(long seconds) {
        return new Duration(seconds);
    }

    /**
     * Returns a new duration from a given number of minutes.
     */
    public static Duration fromMinutes(long minutes) {
        return new Duration(Math.multiplyExact(minutes, 60L));
    }

    /**
     * Returns a new duration from a given number of hours.
     */
    public static Duration fromHours(long hours) {
        return new Duration(Math.multiplyExact(hours, 3600L));
    }

    /**
     * Returns a new duration from a given number of days.
     */
    public static Duration fromDays(long days) {
        return new Duration(Math.multiplyExact(days, 86400L));
    }

    /**
     * Returns a new duration from a given number of weeks.
     */
    public static Duration fromWeeks(long weeks) {
        return new Duration(Math.multiplyExact(weeks, 604800L));
    }
}
