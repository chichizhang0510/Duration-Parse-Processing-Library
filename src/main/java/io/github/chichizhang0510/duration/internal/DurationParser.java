package io.github.chichizhang0510.duration.internal;

import io.github.chichizhang0510.duration.exception.InvalidDurationFormatException;
import java.util.EnumSet;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for parsing human-readable duration strings into total seconds.
 *
 * <p>Supported units: w, d, h, m, s Rules: - Units must appear in descending order: w -> d -> h ->
 * m -> s - Each unit appears at most once - Whitespace between units is optional - No decimals -
 * Negative durations supported with a leading '-'
 */
public class DurationParser {

  private DurationParser() {}

  /**
   * The pattern to match the duration string. It matches the digits and the unit. (\\d+) is the
   * digits, \d represents any digit, + means one or more. () is used to group the digits, meaning
   * the digits are captured and can be accessed later. \\s* is the whitespace, \s represents any
   * whitespace character, * means zero or more. ([wdhms]) is the unit, w represents weeks, d
   * represents days, h represents hours, m represents minutes, s represents seconds.
   */
  private static final Pattern TOKEN = Pattern.compile("(\\d+)\\s*([wdhms])");

  /**
   * Parses a human-readable duration string into total seconds. It supports negative durations with
   * a leading '-'. It throws an InvalidDurationFormatException if the input is not a valid duration
   * format.
   *
   * @param input duration string
   * @return total seconds (can be negative)
   * @throws InvalidDurationFormatException if the input is not a valid duration format
   */
  public static long parseToTotalSeconds(String input) {
    String raw = requireNonBlank(input);
    String s = raw.trim();

    SignAndBody sab = extractLeadingSign(s);
    String body = sab.body();

    if (body.isBlank()) {
      throw invalid("Empty duration after sign.", raw);
    }

    return parseBodyToSeconds(body, sab.sign(), raw);
  }

  // ------------------- Core body parsing -------------------
  /**
   * Parses the body of the duration string into total seconds. It throws an
   * InvalidDurationFormatException if the input is not a valid duration format.
   *
   * @param body the body of the duration string
   * @param sign the sign of the duration string
   * @param rawInput the raw input string
   * @return the total seconds
   * @throws InvalidDurationFormatException if the input is not a valid duration format
   */
  private static long parseBodyToSeconds(String body, int sign, String rawInput) {
    Matcher matcher =
        TOKEN.matcher(body); // matcher can scan the body string for matches of the pattern.

    long totalSeconds = 0L; // total seconds of the duration string
    int tokenCount = 0;

    // Track order and duplicates
    Unit prevUnit =
        null; // previous unit, used to check if the units are in descending order w→d→h→m→s.
    EnumSet<Unit> seen = EnumSet.noneOf(Unit.class);

    int cursor = 0; // cursor to track the position of the next token.

    // find() will return true if the matcher finds a match of the pattern in the body string.
    while (matcher.find()) {
      tokenCount++; // find a token, increment the token count.

      // Gap check: only whitespace allowed between tokens
      requireOnlyWhitespaceBetween(body, cursor, matcher.start(), rawInput);

      // parse the value and unit of the token.
      long value = parseTokenValue(matcher.group(1), rawInput);
      Unit unit = parseTokenUnit(matcher.group(2).charAt(0), rawInput);

      requireNotDuplicate(seen, unit, rawInput); // check if the unit is duplicated.
      requireDescendingOrder(
          prevUnit, unit, rawInput); // check if the units are in descending order.

      totalSeconds =
          addExactSeconds(
              totalSeconds,
              value,
              unit,
              rawInput); // add the value in the given unit to the total seconds.

      // update the set of seen units and the previous unit.
      seen.add(unit); // add the unit to the set of seen units.
      prevUnit = unit; // update the previous unit.
      cursor = matcher.end(); // update the cursor to the next token.
    }

    // if the duration string has nothing but whitespace, it throws an
    // InvalidDurationFormatException.
    if (tokenCount == 0) {
      throw invalid("No duration tokens found.", rawInput);
    }

    // Trailing gap check: to avoid "2h30mxx" like this.
    requireOnlyWhitespaceBetween(body, cursor, body.length(), rawInput);

    return applySign(totalSeconds, sign, rawInput); // apply the sign to the total seconds.
  }

  // ------------------- Validation helpers -------------------

  /**
   * Requires that the input is not null and not empty.
   *
   * @param input the input string
   * @return the input string
   * @throws InvalidDurationFormatException if the input is null or empty
   */
  private static String requireNonBlank(String input) {
    if (input == null) {
      throw invalid("Input is null.", null);
    }
    if (input.trim().isEmpty()) {
      throw invalid("Input is empty.", input);
    }
    return input;
  }

  /**
   * Extracts the leading sign and body from the input string.
   *
   * @param s the input string
   * @return the sign and body
   */
  private static SignAndBody extractLeadingSign(String s) {
    String trimmed = s.trim();
    if (trimmed.startsWith("-")) {
      return new SignAndBody(-1, trimmed.substring(1));
    }
    return new SignAndBody(1, trimmed);
  }

  /**
   * Requires that the only whitespace between the tokens.
   *
   * @param body the body of the duration string
   * @param from the start index of the gap
   * @param to the end index of the gap
   * @param rawInput the raw input string
   */
  private static void requireOnlyWhitespaceBetween(String body, int from, int to, String rawInput) {
    if (from > to) {
      throw invalid("Internal parser error: invalid token boundaries.", rawInput);
    }
    String gap = body.substring(from, to);
    if (!gap.isBlank()) {
      throw invalid("Invalid characters between tokens: '" + gap + "'", rawInput);
    }
  }

  /**
   * Parses the value of the token.
   *
   * @param digits the digits of the token
   * @param rawInput the raw input string
   * @return the value of the token
   */
  private static long parseTokenValue(String digits, String rawInput) {
    // TOKEN ensures digits only, so decimals are already excluded.
    try {
      return Long.parseLong(digits);
    } catch (NumberFormatException e) {
      throw invalid("Duration value is too large: " + digits, rawInput);
    }
  }

  /**
   * Parses the unit of the token.
   *
   * @param unitChar the unit character
   * @param rawInput the raw input string
   * @return the unit of the token
   */
  private static Unit parseTokenUnit(char unitChar, String rawInput) {
    Unit unit = Unit.fromChar(unitChar);
    if (unit == null) {
      throw invalid("Invalid duration unit: " + unitChar, rawInput);
    }
    return unit;
  }

  /**
   * Requires that the unit is not duplicated. If the unit is duplicated, it throws an
   * InvalidDurationFormatException.
   *
   * @param seen the set of units that have been seen
   * @param unit the unit to check
   * @param rawInput the raw input string
   */
  private static void requireNotDuplicate(EnumSet<Unit> seen, Unit unit, String rawInput) {
    Objects.requireNonNull(seen, "seen");
    if (seen.contains(unit)) {
      throw invalid("Duplicate unit: " + unit.symbol, rawInput);
    }
  }

  /**
   * Requires that the units are in descending order. If the units are not in descending order, it
   * throws an InvalidDurationFormatException.
   *
   * @param prev the previous unit
   * @param next the next unit
   * @param rawInput the raw input string
   */
  private static void requireDescendingOrder(Unit prev, Unit next, String rawInput) {
    if (prev == null) {
      return;
    }
    if (next.rank >= prev.rank) {
      throw invalid("Units must be in descending order (w d h m s).", rawInput);
    }
  }

  // ------------------- Math helpers -------------------

  /**
   * Adds the value in the given unit to the total seconds. It uses Math.multiplyExact and
   * Math.addExact to avoid overflow. It throws an InvalidDurationFormatException if the duration is
   * too large (overflow). The specifical logic is: totalSeconds + value * unit.secondsFactor.
   *
   * @param totalSeconds the total seconds
   * @param value the value to add
   * @param unit the unit of the value
   * @param rawInput the raw input string
   * @return the total seconds
   */
  private static long addExactSeconds(long totalSeconds, long value, Unit unit, String rawInput) {
    // Convert value in given unit to seconds with overflow checks.
    long secondsToAdd;
    try {
      secondsToAdd = Math.multiplyExact(value, unit.secondsFactor);
      return Math.addExact(totalSeconds, secondsToAdd);
    } catch (ArithmeticException e) {
      throw invalid("Duration is too large (overflow).", rawInput);
    }
  }

  /**
   * Applies the sign to the total seconds.
   *
   * @param totalSeconds the total seconds
   * @param sign the sign to apply
   * @param rawInput the raw input string
   * @return the total seconds
   */
  private static long applySign(long totalSeconds, int sign, String rawInput) {
    if (sign == 1) {
      return totalSeconds;
    }
    try {
      return Math.multiplyExact(totalSeconds, -1L);
    } catch (ArithmeticException e) {
      throw invalid("Duration is too large (overflow).", rawInput);
    }
  }

  // ------------------- Error handling -------------------

  /**
   * Creates an InvalidDurationFormatException with a message and a raw input.
   *
   * @param message the message
   * @param rawInput the raw input
   * @return the InvalidDurationFormatException
   */
  private static InvalidDurationFormatException invalid(String message, String rawInput) {
    String suffix = (rawInput == null) ? "" : " Input: '" + rawInput + "'";
    return new InvalidDurationFormatException(message + suffix);
  }

  // ------------------- Supporting types -------------------
  /**
   * A record that contains the sign and body of the duration string.
   *
   * @param sign the sign of the duration string
   * @param body the body of the duration string
   */
  private record SignAndBody(int sign, String body) {}

  /**
   * An enum that represents the units of the duration string.
   *
   * @param symbol the symbol of the unit
   * @param rank the rank of the unit
   * @param secondsFactor the seconds factor of the unit
   */
  private enum Unit {
    W('w', 5, 7L * 24L * 3600L),
    D('d', 4, 24L * 3600L),
    H('h', 3, 3600L),
    M('m', 2, 60L),
    S('s', 1, 1L);

    final char symbol;
    final int rank; // bigger rank = larger unit
    final long secondsFactor;

    // constructor of the Unit enum.
    Unit(char symbol, int rank, long secondsFactor) {
      this.symbol = symbol;
      this.rank = rank;
      this.secondsFactor = secondsFactor;
    }

    /**
     * Converts a character to a Unit enum.
     *
     * @param c the character to convert
     * @return the Unit enum
     */
    static Unit fromChar(char c) {
      return switch (c) {
        case 'w' -> W;
        case 'd' -> D;
        case 'h' -> H;
        case 'm' -> M;
        case 's' -> S;
        default -> null;
      };
    }
  }
}
