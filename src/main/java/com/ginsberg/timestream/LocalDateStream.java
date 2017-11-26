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

import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.function.UnaryOperator;

/**
 * A builder that creates a stream of LocalDate objects.
 * <p>
 * <pre>
 * {@code
 * // Print all of the LocalDates between now and a year from now, every other day.
 * LocalDateStream
 *     .fromNow()
 *     .to(1, ChronoUnit.YEARS)
 *     .every(2, ChronoUnit.DAYS)
 *     .stream()
 *     .forEach(System.out::println);
 * }
 * </pre>
 *
 * @author Todd Ginsberg (todd@ginsberg.com)
 */
public class LocalDateStream extends AbstractComparableStream<LocalDate> {
    private long amount = 1;
    private ChronoUnit unit = ChronoUnit.DAYS;

    private LocalDateStream(final LocalDate from) {
        super(from);
    }

    /**
     * Create a LocalDateStream, starting at LocalDate.now().
     *
     * @return A non-null LocalDateStream.
     */
    public static LocalDateStream fromNow() {
        return new LocalDateStream(LocalDate.now());
    }

    /**
     * Create a LocalDateStream, starting at the given LocalDate.
     *
     * @param from A non-null LocalDate to begin the stream with.
     * @return A non-null LocalDateStream.
     */
    public static LocalDateStream from(final LocalDate from) {
        return new LocalDateStream(from);
    }

    /**
     * Set the inclusive end point of the stream, using an absolute LocalDate.
     *
     * @param to A nullable LocalDate to end the stream with (null means infinite).
     * @return A non-null LocalDateStream.
     */
    public LocalDateStream to(final LocalDate to) {
        setTo(to);
        return this;
    }

    /**
     * Set the inclusive end point of the stream, using a relative duration.
     *
     * @param amount The number of units to use when calculating the duration of the stream. May be negative.
     * @param unit   The non-null unit the amount is denominated in. May not be null.
     * @return A non-null LocalDateStream.
     * @throws java.time.temporal.UnsupportedTemporalTypeException if the unit is not supported.
     * @see ChronoUnit
     */
    public LocalDateStream to(int amount,
                              final ChronoUnit unit) {
        Objects.requireNonNull(unit);
        setTo(getFrom().plus(amount, unit));
        return this;
    }

    /**
     * Set the exclusive end point of the stream, using an absolute LocalDate.
     *
     * @param until A nullable LocalDate to end the stream before (null means infinite).
     * @return A non-null LocalDateStream.
     */
    public LocalDateStream until(final LocalDate until) {
        setUntil(until);
        return this;
    }

    /**
     * Set the exclusive end point of the stream, using a relative duration.
     *
     * @param amount The number of units to use when calculating the duration of the stream. May be negative.
     * @param unit   The non-null unit the amount is denominated in.
     * @return A non-null LocalDateStream.
     * @throws java.time.temporal.UnsupportedTemporalTypeException if the unit is not supported.
     * @see ChronoUnit
     */
    public LocalDateStream until(int amount,
                                 final ChronoUnit unit) {
        Objects.requireNonNull(unit);
        setUntil(getFrom().plus(amount, unit));
        return this;
    }

    /**
     * Set the duration between successive elements produced by the stream. The default
     * for this builder is 1 Day.
     *
     * @param amount The number of units to use when calculating the next element of the stream.
     * @param unit   The non-null unit the amount is denominated in.
     * @return A non-null LocalDateStream.
     * @throws java.time.temporal.UnsupportedTemporalTypeException if the unit is not supported.
     * @see ChronoUnit
     */
    public LocalDateStream every(int amount,
                                 final ChronoUnit unit) {
        Objects.requireNonNull(unit);
        this.amount = Math.abs(amount);
        this.unit = unit;
        LocalDate.now().plus(0, unit); // Fail fast test
        return this;
    }

    /**
     * Set the duration between successive elements produced by the stream. The default
     * for this builder is 1 Day.
     *
     * @param period The interval to use when calculating the next element of the stream.
     * @return A non-null LocalDateStream.
     * @throws java.time.temporal.UnsupportedTemporalTypeException if the period is not supported.
     * @see ChronoUnit
     */
    public LocalDateStream every(Period period) {
        Objects.requireNonNull(period);
        this.unit = ChronoUnit.DAYS;
        this.amount = period.get(this.unit);
        LocalDate.now().plus(0, unit); // Fail fast test
        return this;
    }

    @Override
    UnaryOperator<LocalDate> next() {
        return date -> date.plus(isForward() ? amount : 0 - amount, unit);
    }
}
