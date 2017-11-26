/*
 * MIT License
 *
 * Copyright (c) 2016 Todd Ginsberg
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.ginsberg.timestream;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.function.UnaryOperator;

/**
 * A builder that creates a stream of LocalDateTime objects.
 * <p>
 * <pre>
 * {@code
 * // Print all of the LocalDateTimes between now and a day from now, every other minute.
 * LocalDateTimeStream
 *     .fromNow()
 *     .to(1, ChronoUnit.DAYS)
 *     .every(2, ChronoUnit.MINUTES)
 *     .stream()
 *     .forEach(System.out::println);
 * }
 * </pre>
 *
 * @author Todd Ginsberg (todd@ginsberg.com)
 */
public class LocalDateTimeStream extends AbstractComparableStream<LocalDateTime> {
    private long amount = 1;
    private ChronoUnit unit = ChronoUnit.SECONDS;

    private LocalDateTimeStream(final LocalDateTime from) {
        super(from);
    }

    /**
     * Create a LocalDateTimeStream, starting at LocalDateTime.now().
     *
     * @return A non-null LocalDateTimeStream.
     */
    public static LocalDateTimeStream fromNow() {
        return new LocalDateTimeStream(LocalDateTime.now());
    }

    /**
     * Create a LocalDateTimeStream, starting at the given LocalDateTime.
     *
     * @param from A non-null LocalDateTime to begin the stream with.
     * @return A non-null LocalDateTimeStream.
     */
    public static LocalDateTimeStream from(final LocalDateTime from) {
        return new LocalDateTimeStream(from);
    }

    /**
     * Set the inclusive end point of the stream, using an absolute LocalDateTime.
     *
     * @param to A nullable LocalDateTime to end the stream with (null means infinite).
     * @return A non-null LocalDateTimeStream.
     */
    public LocalDateTimeStream to(final LocalDateTime to) {
        setTo(to);
        return this;
    }

    /**
     * Set the inclusive end point of the stream, using a relative duration.
     *
     * @param amount The number of units to use when calculating the duration of the stream. May be negative.
     * @param unit   The non-null unit the amount is denominated in. May not be null.
     * @return A non-null LocalDateTimeStream.
     * @throws java.time.temporal.UnsupportedTemporalTypeException if the unit is not supported.
     * @see ChronoUnit
     */
    public LocalDateTimeStream to(int amount,
                                  final ChronoUnit unit) {
        Objects.requireNonNull(unit);
        setTo(getFrom().plus(amount, unit));
        return this;
    }

    /**
     * Set the exclusive end point of the stream, using an absolute LocalDateTime.
     *
     * @param until A nullable LocalDateTime to end the stream before (null means infinite).
     * @return A non-null LocalDateTimeStream.
     */
    public LocalDateTimeStream until(final LocalDateTime until) {
        setUntil(until);
        return this;
    }

    /**
     * Set the exclusive end point of the stream, using a relative duration.
     *
     * @param amount The number of units to use when calculating the duration of the stream. May be negative.
     * @param unit   The non-null unit the amount is denominated in.
     * @return A non-null LocalDateTimeStream.
     * @throws java.time.temporal.UnsupportedTemporalTypeException if the unit is not supported.
     * @see ChronoUnit
     */
    public LocalDateTimeStream until(int amount,
                                     final ChronoUnit unit) {
        Objects.requireNonNull(unit);
        setUntil(getFrom().plus(amount, unit));
        return this;
    }

    /**
     * Set the duration between successive elements produced by the stream. The default
     * for this builder is 1 Second.
     *
     * @param amount The number of units to use when calculating the next element of the stream.
     * @param unit   The non-null unit the amount is denominated in.
     * @return A non-null LocalDateTimeStream.
     * @throws java.time.temporal.UnsupportedTemporalTypeException if the unit is not supported.
     * @see ChronoUnit
     */
    public LocalDateTimeStream every(int amount,
                                     final ChronoUnit unit) {
        Objects.requireNonNull(unit);
        this.amount = Math.abs(amount);
        this.unit = unit;
        return this;
    }

    /**
     * Set the duration between successive elements produced by the stream. The default
     * for this builder is 1 Second.
     *
     * @param duration The interval to use when calculating the next element of the stream.
     * @return A non-null LocalDateTimeStream.
     * @throws java.time.temporal.UnsupportedTemporalTypeException if the unit is not supported.
     * @see ChronoUnit
     */
    public LocalDateTimeStream every(Duration duration) {
        Objects.requireNonNull(unit);
        this.unit = ChronoUnit.SECONDS;
        this.amount = duration.get(this.unit);
        return this;
    }

    @Override
    UnaryOperator<LocalDateTime> next() {
        return date -> date.plus(isForward() ? amount : 0 - amount, unit);
    }
}
