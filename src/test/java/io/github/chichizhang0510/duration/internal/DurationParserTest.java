package io.github.chichizhang0510.duration.internal;

import io.github.chichizhang0510.duration.exception.InvalidDurationFormatException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class DurationParserTest {

    // -------------------- Valid parsing --------------------
    @ParameterizedTest
    @CsvSource({
            "90s, 90",
            "0s, 0",
            "2m, 120",
            "2h, 7200",
            "1d, 86400",
            "1w, 604800",
            "2h30m, 9000",
            "'2h 30m', 9000",
            "'1d 12h 45m', 132300",     // 86400 + 43200 + 2700
            "'1w 2d 3h 4m 5s', 788645"   // 604800 + 172800 + 10800 + 240 + 5
    })
    void parse_validInputs(String input, long expectedSeconds) {
        assertEquals(expectedSeconds, DurationParser.parseToTotalSeconds(input));
    }

    @ParameterizedTest
    @CsvSource({
            "-30m, -1800",
            "-2h30m, -9000",
            "'-2h 30m', -9000",
            "-90s, -90"
    })
    void parse_negativeInputs(String input, long expectedSeconds) {
        assertEquals(expectedSeconds, DurationParser.parseToTotalSeconds(input));
    }

    // -------------------- Invalid formats --------------------

    @ParameterizedTest
    @ValueSource(strings = {
            "30m2h",      // wrong order
            "2h2h",       // duplicate unit
            "1.5h",       // decimals not allowed
            "2x",         // invalid unit
            "2h__30m",    // garbage between tokens
            "2h,30m",     // garbage between tokens
            "2h30m!",     // trailing garbage
            "h",          // missing number
            "10",         // missing unit
            "-",          // sign only
            "--2h",       // invalid sign usage
            "+2h"         // plus sign not supported (spec doesn't mention it)
    })
    void parse_invalidInputs_throw(String input) {
        assertThrows(InvalidDurationFormatException.class,
                () -> DurationParser.parseToTotalSeconds(input));
    }

    @Test
    void parse_null_throws() {
        assertThrows(InvalidDurationFormatException.class,
                () -> DurationParser.parseToTotalSeconds(null));
    }

    @Test
    void parse_blank_throws() {
        assertThrows(InvalidDurationFormatException.class,
                () -> DurationParser.parseToTotalSeconds("   "));
    }

    // -------------------- Order and duplicate (explicit messages optional) --------------------

    @Test
    void parse_wrongOrder_hasHelpfulMessage() {
        InvalidDurationFormatException ex = assertThrows(InvalidDurationFormatException.class,
                () -> DurationParser.parseToTotalSeconds("30m2h"));
        assertTrue(ex.getMessage().toLowerCase().contains("descending"));
    }

    @Test
    void parse_duplicateUnit_hasHelpfulMessage() {
        InvalidDurationFormatException ex = assertThrows(InvalidDurationFormatException.class,
                () -> DurationParser.parseToTotalSeconds("2h2h"));
        assertTrue(ex.getMessage().toLowerCase().contains("duplicate"));
    }

    // -------------------- Overflow (optional but good for branch coverage) --------------------

    @Test
    void parse_overflow_throws() {
        // This number * 604800 will overflow long
        String hugeWeeks = "9999999999999999999w";
        assertThrows(InvalidDurationFormatException.class,
                () -> DurationParser.parseToTotalSeconds(hugeWeeks));
    }

}