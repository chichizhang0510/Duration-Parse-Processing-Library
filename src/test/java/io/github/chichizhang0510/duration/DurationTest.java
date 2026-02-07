package io.github.chichizhang0510.duration;

import io.github.chichizhang0510.duration.exception.InvalidDurationFormatException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DurationTest {


    // -------------------- totalSeconds (package-private) --------------------

    @Test
    void totalSeconds_returnsInternalValue() {
        Duration d = new Duration("2h30m"); // 9000s
        assertEquals(9000L, d.totalSeconds());
    }

    // -------------------- Parsing (valid) --------------------
    @Test
    void constructor_parsesCombinedWithoutSpaces() {
        Duration d = new Duration("2h30m");
        assertEquals(9000L, d.toSeconds());
        assertEquals("2h30m", d.toNormalizedString());
    }

    @Test
    void constructor_parsesCombinedWithSpaces() {
        Duration d = new Duration("2h 30m");
        assertEquals(9000L, d.toSeconds());
        assertEquals("2h30m", d.toNormalizedString());
    }

    @Test
    void constructor_parsesNegative() {
        Duration d = new Duration("-30m");
        assertEquals(-1800L, d.toSeconds());
        assertEquals("-30m", d.toNormalizedString());
    }

    @Test
    void constructor_parsesZero() {
        Duration d = new Duration("0s");
        assertEquals(0L, d.toSeconds());
        assertEquals("0s", d.toNormalizedString());
        assertEquals("0 seconds", d.format());
    }

    // -------------------- Normalization (examples) --------------------
    @Test
    void normalizedString_90sBecomes1m30s() {
        Duration d = new Duration("90s");
        assertEquals("1m30s", d.toNormalizedString());
    }

    @Test
    void normalizedString_90mBecomes1h30m() {
        Duration d = new Duration("90m");
        assertEquals("1h30m", d.toNormalizedString());
    }

    @Test
    void normalizedString_25hBecomes1d1h() {
        Duration d = new Duration("25h");
        assertEquals("1d1h", d.toNormalizedString());
    }

    @Test
    void normalizedString_8dBecomes1w1d() {
        Duration d = new Duration("8d");
        assertEquals("1w1d", d.toNormalizedString());
    }

    @Test
    void normalizedString_negative90sBecomesNegative1m30s() {
        Duration d = new Duration("-90s");
        assertEquals("-1m30s", d.toNormalizedString());
    }

    // -------------------- Parsing (invalid) --------------------
    @Test
    void constructor_rejectsWrongOrder() {
        assertThrows(InvalidDurationFormatException.class, () -> new Duration("30m2h"));
    }

    @Test
    void constructor_rejectsDuplicateUnits() {
        assertThrows(InvalidDurationFormatException.class, () -> new Duration("2h2h"));
    }

    @Test
    void constructor_rejectsDecimals() {
        assertThrows(InvalidDurationFormatException.class, () -> new Duration("1.5h"));
    }


    @Test
    void constructor_rejectsInvalidUnit() {
        assertThrows(InvalidDurationFormatException.class, () -> new Duration("2x"));
    }

    @Test
    void constructor_rejectsGarbageBetweenTokens() {
        assertThrows(InvalidDurationFormatException.class, () -> new Duration("2h__30m"));
    }

    @Test
    void constructor_rejectsEmptyString() {
        assertThrows(InvalidDurationFormatException.class, () -> new Duration("   "));
    }

    @Test
    void constructor_rejectsNull() {
        assertThrows(InvalidDurationFormatException.class, () -> new Duration(null));
    }

    // -------------------- Conversion (total values) --------------------

    @Test
    void conversions_returnTotalValues() {
        Duration d = new Duration("1h30m");
        assertEquals(5400L, d.toSeconds());
        assertEquals(90L, d.toMinutes());
        assertEquals(1L, d.toHours()); // integer division
        assertEquals(0L, d.toDays());
        assertEquals(5_400_000L, d.toMilliseconds());
    }

    @Test
    void conversions_acrossDays() {
        Duration d = new Duration("1d1h");
        assertEquals(25L, d.toHours());
        assertEquals(1L, d.toDays());
    }

    @Test
    void toWeeks_returnsTotalWeeks_truncated() {
        // 1w + 6d = 13 days -> still 1 week when truncated
        Duration d = new Duration("1w6d");
        assertEquals(1L, d.toWeeks());
    }

    @Test
    void toWeeks_negativeWorks() {
        Duration d = new Duration("-1w1d");
        // total weeks in long trunc division should be -1
        assertEquals(-1L, d.toWeeks());
    }


    // -------------------- Arithmetic --------------------

    @Test
    void add_returnsNewDuration_andDoesNotMutateOriginal() {
        Duration a = new Duration("2h");
        Duration b = new Duration("30m");

        Duration sum = a.add(b);

        assertEquals("2h30m", sum.toNormalizedString());
        assertEquals("2h", a.toNormalizedString());   // a unchanged
        assertEquals("30m", b.toNormalizedString());  // b unchanged
    }

    @Test
    void subtract_canProduceNegative() {
        Duration a = new Duration("1m");
        Duration b = new Duration("90s");

        Duration diff = a.subtract(b);

        assertEquals("-30s", diff.toNormalizedString());
        assertEquals(-30L, diff.toSeconds());
    }

    // -------------------- Comparable --------------------

    @Test
    void compareTo_ordersByTotalSeconds() {
        Duration neg = Duration.fromSeconds(-1);
        Duration zero = Duration.fromSeconds(0);
        Duration pos = Duration.fromSeconds(1);

        assertTrue(neg.compareTo(zero) < 0);
        assertTrue(zero.compareTo(pos) < 0);
        assertEquals(0, Duration.fromSeconds(60).compareTo(new Duration("1m")));
    }

    @Test
    void equals_sameReference_isTrue() {
        Duration d = new Duration("1m");
        assertEquals(d, d);
    }

    @Test
    void equals_null_isFalse() {
        Duration d = new Duration("1m");
        assertNotEquals(null, d);
    }

    @Test
    void equals_differentType_isFalse() {
        Duration d = new Duration("1m");
        assertNotEquals("1m", d);
    }

    @Test
    void equals_sameValueDifferentConstruction_isTrue() {
        Duration a = new Duration("60s");
        Duration b = new Duration("1m");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode(), "Equal objects must have same hashCode");
    }

    @Test
    void equals_differentValue_isFalse() {
        Duration a = new Duration("1m");
        Duration b = new Duration("61s");
        assertNotEquals(a, b);
    }

    @Test
    void hashCode_isStable() {
        Duration d = new Duration("2h30m");
        int h1 = d.hashCode();
        int h2 = d.hashCode();
        assertEquals(h1, h2);
    }
    
    // -------------------- Formatting --------------------

    @Test
    void format_isHumanReadableWithPluralization() {
        Duration d = new Duration("2h30m");
        assertEquals("2 hours 30 minutes", d.format());
    }

    @Test
    void toString_returnsNormalizedCompactForm() {
        Duration d = new Duration("90s");
        assertEquals("1m30s", d.toString());
    }


    // -------------------- Factories --------------------

    @Test
    void factories_fromSecondsAndMinutes() {
        assertEquals("1m30s", Duration.fromSeconds(90).toNormalizedString());
        assertEquals("1h30m", Duration.fromMinutes(90).toNormalizedString());
    }

    @Test
    void factory_fromMilliseconds_acceptsWholeSecondsOnly() {
        assertEquals("1s", Duration.fromMilliseconds(1000).toNormalizedString());
        assertThrows(InvalidDurationFormatException.class, () -> Duration.fromMilliseconds(1500));
    }

    
    @Test
    void fromHours_createsCorrectDuration() {
        Duration d = Duration.fromHours(25); // 25h -> 1d1h normalized
        assertEquals(25L, d.toHours());
        assertEquals("1d1h", d.toNormalizedString());
    }

    @Test
    void fromDays_createsCorrectDuration() {
        Duration d = Duration.fromDays(8); // 8d -> 1w1d normalized
        assertEquals(8L, d.toDays());
        assertEquals("1w1d", d.toNormalizedString());
    }

    @Test
    void fromWeeks_createsCorrectDuration() {
        Duration d = Duration.fromWeeks(2);
        assertEquals(2L, d.toWeeks());
        assertEquals("2w", d.toNormalizedString());
    }
    
}
