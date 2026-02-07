# Design Document – Human Duration Library

## 1. Overview

The goal of this project is to design and implement a **Java library** for parsing, normalizing, formatting, and manipulating **human-readable duration strings**, such as:

* `2h30m`
* `90s`
* `1d 12h 45m`
* `1w 2d 3h 4m 5s`

The library is designed as a **reusable open-source component**, with a strong emphasis on:

* Clear API design
* Separation of concerns
* Immutability
* Testability
* Build-tool-driven workflows (no IDE dependency)

---

## 2. Design Goals

### 2.1 Functional Goals

* Parse duration strings using fixed units: weeks, days, hours, minutes, seconds
* Enforce strict format rules (ordering, no duplicates, no decimals)
* Normalize durations (e.g. `90s → 1m30s`)
* Support arithmetic operations (add, subtract)
* Provide conversion to total units (milliseconds, seconds, minutes, etc.)
* Provide both compact and human-readable string representations

### 2.2 Non-Functional Goals

* Immutable value object design
* Clear separation between parsing, normalization, formatting, and arithmetic
* High test coverage (≥70% line and branch)
* No reliance on IDE features
* Fully reproducible via Maven commands
* Suitable for publishing to Maven Central

---

## 3. High-Level Architecture

The library is organized into two conceptual layers:

```
Public API Layer
└── Duration

Internal Implementation Layer
├── DurationParser
├── DurationNormalizer
├── DurationFormatter
└── DurationMath
```

### Key Principle

**Public API exposes behavior; internal components encapsulate logic.**

Users interact only with `Duration`. All parsing, normalization, formatting, and arithmetic logic is delegated to internal classes.

---

## 4. Core Abstractions

### 4.1 Duration (Public API)

`Duration` represents an **immutable value object** that models a duration as a single scalar value:

```java
private final long totalSeconds;
```

#### Design Rationale

* Simplifies arithmetic (addition, subtraction)
* Avoids partial normalization states
* Provides a single source of truth
* Enables consistent comparison and conversion

Once created, a `Duration` instance **never mutates**.

#### Responsibilities

* Validate input via parsing
* Expose conversion methods (`toSeconds`, `toMinutes`, etc.)
* Delegate formatting and normalization
* Support arithmetic and comparison

---

### 4.2 DurationParser

**Responsibility:** Convert a human-readable duration string into a signed `long` representing total seconds.

#### Design Decisions

* Regex-based token scanning
* Iterative parsing with strict validation:

  * Unit order enforcement (weeks → days → hours → minutes → seconds)
  * No duplicate units
  * No decimals
  * No invalid characters between tokens
* Explicit error reporting via `InvalidDurationFormatException`

Parser logic is intentionally isolated to keep `Duration` simple and readable.

---

### 4.3 DurationNormalizer

**Responsibility:** Convert a scalar total number of seconds into normalized duration components.

#### Output Model

Normalization produces a `NormalizedParts` record:

```java
record NormalizedParts(
  int sign,
  long weeks,
  long days,
  long hours,
  long minutes,
  long seconds
)
```

#### Normalization Rules

* Seconds: 0–59
* Minutes: 0–59
* Hours: 0–23
* Days: 0–6
* Weeks: unbounded

The sign is handled separately to keep all unit values non-negative.

---

### 4.4 DurationFormatter

**Responsibility:** Convert a `Duration` into a string representation.

Two formats are supported:

1. **Normalized compact form** (e.g. `1m30s`, `2h`, `1w1d`)
2. **Human-readable form** (e.g. `"1 minute 30 seconds"`)

Formatter relies exclusively on `DurationNormalizer` output, ensuring:

* No duplicated logic
* Consistent normalization across all string outputs

---

### 4.5 DurationMath

**Responsibility:** Perform arithmetic operations on durations.

* `add(Duration a, Duration b)`
* `subtract(Duration a, Duration b)`

All operations:

* Use `Math.addExact` / `Math.subtractExact`
* Detect overflow explicitly
* Return new `Duration` instances

This preserves immutability and safety.

---

## 5. Error Handling Strategy

All invalid inputs result in a single, consistent exception type:

```java
InvalidDurationFormatException
```

This includes:

* Parsing errors
* Invalid factory method inputs
* Overflow during arithmetic
* Normalization edge cases

Using a domain-specific exception simplifies API usage and testing.

---

## 6. Testing Strategy

### 6.1 Test Scope

* Public API tests for `Duration`
* Component-level tests for all internal classes
* Dedicated tests for exception behavior

### 6.2 Coverage

* Line coverage ≥ 70%
* Branch coverage ≥ 70%

Tests cover:

* Valid inputs
* Invalid formats
* Boundary values
* Overflow cases
* Negative durations
* Normalization edge cases

---

## 7. Build and Tooling

### Build Tool

* **Apache Maven**

### Integrated Tools

* JUnit 5 – unit testing
* JaCoCo – test coverage reporting
* Checkstyle (Google Java Style) – coding style enforcement
* SpotBugs – static analysis
* Javadoc – API documentation

All tools are executed via Maven commands only.

---

## 8. Extensibility Considerations

The design allows future extensions such as:

* Additional units (e.g. milliseconds)
* Custom formatting styles
* Localization of human-readable output
* ISO-8601 duration support

These features can be added without changing the core `Duration` abstraction.

---

## 9. Summary

This library is designed as a **production-quality Java utility**, emphasizing:

* Clear separation of concerns
* Immutable core model
* Deterministic normalization
* Strong validation and error handling
* Comprehensive testing and documentation
* Fully automated build and quality checks

The result is a maintainable, testable, and reusable duration library suitable for open-source distribution.