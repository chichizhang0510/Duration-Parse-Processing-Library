# Human Duration Library

A Java library for parsing, normalizing, formatting, and manipulating human-readable duration strings such as 2h30m, 90s, 1d 12h 45m, etc.

The library is designed as an open-source–style Java library with strong emphasis on:
- Code quality
- Documentation
- Automated testing
- Build-tool–driven workflows (no IDE dependency)

---
# Features

- Parse human-readable duration strings (2h30m, 1w 2d 3h 4m 5s)
- Support normalization (90s → 1m30s)
- Arithmetic operations (add, subtract)
- Conversion to total units (milliseconds, seconds, minutes, hours, days)
- Human-readable formatting ("2 hours 30 minutes")

For a detailed design explanation, see [Design Document](docs/design.md).


---
# How to Use This Library

## Dependency

### Maven Dependency
```xml
<dependency>
  <groupId>io.github.chichizhang0510</groupId>
  <artifactId>human-duration</artifactId>
  <version>0.1.0</version>
</dependency>
```

### Gradle Dependency
```gradle
implementation "io.github.chichizhang0510:human-duration:0.1.0"
```


## Maven Central
The library is published to Maven Central: https://repo1.maven.org/maven2/io/github/chichizhang0510/human-duration/



## Code examples
### Parse a duration string
```java
Duration d = new Duration("2h30m");
```

### Convert to total units
```java
d.toSeconds();       // 9000
d.toMinutes();       // 90
d.toHours();         // 2
d.toMilliseconds(); // 9000000
```

### Normalize duration
```java
Duration d = new Duration("90s");
d.toNormalizedString(); // "1m30s"
```

### Human-readable formatting
```java
Duration d = new Duration("2h30m");
d.format(); // "2 hours 30 minutes"
```

### Arithmetic operations
```java
Duration a = new Duration("2h");
Duration b = new Duration("30m");

Duration sum = a.add(b);        // 2h30m
Duration diff = a.subtract(b);  // 1h30m
```

### Static factory methods
```java
Duration.fromSeconds(90);   // 1m30s
Duration.fromMinutes(90);   // 1h30m
Duration.fromHours(25);     // 1d1h
```



---
# How to Develop This Library

## Prerequisites
- Java 17 (or compatible version)
- Apache Maven 3.9+
- Git

## Clone and Build
```bash
git clone https://github.com/chichizhang0510/human-duration.git
cd human-duration
mvn clean compile
```

## Run All Tests
All tests are executed using Maven (no IDE required).
```bash
mvn test
```

## Generate Test Coverage Report (JaCoCo)
The project maintains ≥95% line and branch coverage.
```bash
mvn clean verify
```
Coverage report (HTML) path (from root folder): target/site/jacoco/index.html


## Generate Javadoc
All public APIs are fully documented with zero Javadoc warnings.
```bash
mvn javadoc:javadoc
```
Generated documentation path (from root folder): target/site/apidocs/index.html


## Run Coding Style Checks (Checkstyle)
```bash
mvn checkstyle:check
```
- Uses Google Java Style
- Configuration file: config/checkstyle.xml
- Must report zero warnings and errors

Optional HTML report:
```bash
mvn checkstyle:checkstyle
```


## Run Static Analysis (SpotBugs)
```bash
mvn spotbugs:check
```

Optional report generation:
```bash
mvn spotbugs:spotbugs
```


## Create JAR Package
The JAR includes compiled classes and metadata (name and version).
```bash
mvn package
```
Generated artifact path (from root folder): target/human-duration-0.1.0.jar



---
# License
MIT License