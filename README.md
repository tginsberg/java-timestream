# Java-Timestream
A set of builders that create streams of java.time objects. 

To use this library in your project, add this to your dependencies:

```xml
<dependency>
    <groupId>com.ginsberg</groupId>
    <artifactId>java-timestream</artifactId>
    <version>1.0.0</version>
</dependency>
```

These are fully functional streams, so you can use them like any other stream, once created. 

Once `Stream.takeWhile()` support is added in Java 9, this library will not be very useful. The lack
of a convenient `takeWhile(Predicate<T> predicate)` method in Java 8 is what lead to the creation
of this library. 

   
This library can create streams of the following java.time classes:

+ `LocalDate` via `LocalDateStream`
+ `LocalDateTime` via `LocalDateTimeStream`
+ `YearMonth` via `YearMonthStream`
+ `ZonedDateTime` via `ZoneDateTimeStream`

And has support for...

+ Inclusive end point (`to`)
+ Exclusive end point (`until`)
+ Configurable period between stream elements (`every`)
+ Streams can move forward or backward through time
+ Infinite streams (by not providing an end point)

## Usage

Every builder needs a non-null point in time to begin the stream. Therefore, each
builder can be created using one of two methods:

+ `.fromNow()` - Assumes 'now'
+ `.from(T from)` - Type-specific starting point, provided by caller

To set the optional point in time where the stream ends (inclusive) you can call one of two methods:

+ `.to(T to)` - Type-specific end point. Can be null to indicate forever
+ `.to(amount, units)` - Where `amount` is a positive integer (for forward through time) or a negative integer (for backward through time), and `unit` is a valid `ChronoUnit`

To make the optional end of the stream exclusive, you can call one of two methods:

+ `.until(T to)` - Type-specific end point. Can be null to indicate forever
+ `.until(amount, units)` - Where `amount` is a positive integer (for forward through time) or a negative integer (for backward through time), and `unit` is a valid `ChronoUnit`

Note that providing an end time (via `to` or `until`) is optional. In that case, the stream will
have no end and should produce values until you stop it.

## Examples

Create a stream of `LocalDateTime` objects, between now and hour from now, every two minutes:

```java
final Stream<LocalDateTime> stream = LocalDateTimeStream
        .fromNow()
        .to(1, ChronoUnit.HOURS)
        .every(2, ChronoUnit.MINUTES)
        .stream();
```

Or (equivalent):

```java
final Stream<LocalDateTime> stream = LocalDateTimeStream
        .from(LocalDateTime.now())
        .to(LocalDateTime.now().plusHours(1))
        .every(2, ChronoUnit.MINUTES)
        .stream();
```

Create a stream of `YearMonth` objects from today, to a year ago (backward through time), stopping before the end (exclusive),
every month:

```java
final Stream<YearMonth> stream = YearMonthStream
        .fromNow()
        .until(-12, ChronoUnit.MONTHS)
        .every(1, ChronoUnit.MONTHS) // This is the default
        .stream();
```

Replace this code that does something with every minute of time over the last hour, going backwards:

```java
final LocalDateTime end = LocalDateTime.now().minusHours(1);
LocalDateTime when = LocalDateTime.now();

while(when.isAfter(end)) {
    doSomething(when);
    when = when.minusMinutes(1);
}
```

... with this:

```java
LocalDateTimeStream
    .fromNow()
    .until(-1, ChronoUnit.HOURS)
    .every(1, ChronoUnit.MINUTES)
    .stream()
    .forEach(this::doSomething);
```

It's not less code, but it certainly makes it easier to understand.

There are also plenty of examples in the unit tests.

## Contributing and Issues

Please feel free to file issues for change requests or bugs. If you would like to contribute new functionality, please contact me first!
