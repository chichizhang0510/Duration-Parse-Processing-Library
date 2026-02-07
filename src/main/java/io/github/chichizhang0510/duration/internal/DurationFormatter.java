package io.github.chichizhang0510.duration.internal;

import io.github.chichizhang0510.duration.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DurationFormatter {

    /**
     * Private constructor to prevent instantiation.
     */
    private DurationFormatter() {}

    public static String toNormalizedString(Duration duration) {
        Objects.requireNonNull(duration, "duration");

        long totalSeconds = extractTotalSeconds(duration);
        DurationNormalizer.NormalizedParts p = DurationNormalizer.normalize(totalSeconds);

        if (p.isZero()) {
            return "0s";
        }

        StringBuilder sb = new StringBuilder();
        if (p.isNegative()) {
            sb.append('-');
        }

        // Append in canonical order, skipping zeros
        appendUnit(sb, p.weeks(), 'w');
        appendUnit(sb, p.days(), 'd');
        appendUnit(sb, p.hours(), 'h');
        appendUnit(sb, p.minutes(), 'm');
        appendUnit(sb, p.seconds(), 's');

        // Safety: if everything was zero (shouldn't happen due to isZero), return 0s
        return sb.length() == 0 || sb.toString().equals("-") ? "0s" : sb.toString();
    }

    /**
     * Formats a duration to a human-readable string.
     * @param duration the duration
     * @return the human-readable string
     */
    public static String format(Duration duration) {
        Objects.requireNonNull(duration, "duration");

        long totalSeconds = extractTotalSeconds(duration);
        DurationNormalizer.NormalizedParts p = DurationNormalizer.normalize(totalSeconds);

        if (p.isZero()) {
            return "0 seconds";
        }

        List<String> parts = new ArrayList<>(5);

        // Build parts in canonical order, skipping zeros
        addHumanPart(parts, p.weeks(), "week");
        addHumanPart(parts, p.days(), "day");
        addHumanPart(parts, p.hours(), "hour");
        addHumanPart(parts, p.minutes(), "minute");
        addHumanPart(parts, p.seconds(), "second");

        String joined = String.join(" ", parts);
        return p.isNegative() ? "-" + joined : joined;
    }


    // ------------------- Helpers -------------------

    /**
     * Extracts the total seconds from a duration.
     * @param duration the duration
     * @return the total seconds
     */
    private static long extractTotalSeconds(Duration duration) {
        return duration.toSeconds();
    }

    /**
     * Appends a unit to a StringBuilder.
     * @param sb the StringBuilder
     * @param value the value
     * @param unitSymbol the unit symbol
     */
    private static void appendUnit(StringBuilder sb, long value, char unitSymbol) {
        if (value <= 0) return;
        sb.append(value).append(unitSymbol);
    }

    /**
     * Adds a human part to a list.
     * @param parts the list
     * @param value the value
     * @param unitBase the unit base
     */
    private static void addHumanPart(List<String> parts, long value, String unitBase) {
        if (value <= 0) return;
        parts.add(value + " " + pluralize(unitBase, value));
    }

    /**
     * Pluralizes a unit base.
     * @param unitBase the unit base
     * @param value the value
     * @return the pluralized unit base
     */
    private static String pluralize(String unitBase, long value) {
        return value == 1 ? unitBase : unitBase + "s";
    }

}
