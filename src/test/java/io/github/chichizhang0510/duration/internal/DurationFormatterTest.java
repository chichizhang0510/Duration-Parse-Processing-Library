package io.github.chichizhang0510.duration.internal;

import io.github.chichizhang0510.duration.Duration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import io.github.chichizhang0510.duration.exception.InvalidDurationFormatException;
import static org.junit.jupiter.api.Assertions.*;

public class DurationFormatterTest {

    // -------------------- toNormalizedString --------------------

    @ParameterizedTest
    @CsvSource({
            // input duration string, expected normalized string
            "0s, 0s",
            "1s, 1s",
            "60s, 1m",            // carry
            "90s, 1m30s",         // carry + remainder
            "90m, 1h30m",
            "25h, 1d1h",
            "8d, 1w1d",
            "2h30m, 2h30m",
            "'2h 30m', 2h30m",
            "1w2d3h4m5s, 1w2d3h4m5s"
    })
    void toNormalizedString_formatsCorrectly(String input, String expected) {
        Duration d = new Duration(input);
        assertEquals(expected, DurationFormatter.toNormalizedString(d));
    }

    @Test
    void toNormalizedString_negativeHasLeadingMinus() {
        Duration d = new Duration("-90s");
        assertEquals("-1m30s", DurationFormatter.toNormalizedString(d));
    }

    @Test
    void toNormalizedString_skipsZeroUnits() {
        // 1h0m0s should render as "1h" (zeros skipped)
        Duration d = Duration.fromSeconds(3600);
        assertEquals("1h", DurationFormatter.toNormalizedString(d));
    }

    // -------------------- format (human-readable) --------------------

    @ParameterizedTest
    @CsvSource({
            "0s, 0 seconds",
            "1s, 1 second",
            "2s, 2 seconds",
            "60s, 1 minute",
            "90s, 1 minute 30 seconds",
            "3600s, 1 hour",
            "5400s, 1 hour 30 minutes",
            "90000s, 1 day 1 hour" // 25h
    })
    void format_humanReadablePluralization(String secondsInput, String expected) {
        // Here we use factory to avoid parsing questions; input is in seconds for clarity.
        long seconds = Long.parseLong(secondsInput.substring(0, secondsInput.length() - 1));
        Duration d = Duration.fromSeconds(seconds);
        assertEquals(expected, DurationFormatter.format(d));
    }


    @Test
    void format_negativeHasLeadingMinus() {
        Duration d = new Duration("-90s");
        assertEquals("-1 minute 30 seconds", DurationFormatter.format(d));
    }

    @Test
    void format_skipsZeroUnits() {
        Duration d = Duration.fromSeconds(3600); // 1 hour
        assertEquals("1 hour", DurationFormatter.format(d));
    }

    // -------------------- Edge/overflow behavior --------------------

    @Test
    void format_longMinValue_throws() {
        assertThrows(InvalidDurationFormatException.class,
                () -> DurationFormatter.format(Duration.fromSeconds(Long.MIN_VALUE)));
    }
}
