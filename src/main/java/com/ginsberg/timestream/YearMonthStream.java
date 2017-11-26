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

import java.time.Period;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.function.UnaryOperator;

/**
 * A builder that creates a stream of YearMonth objects.
 * <p>
 * <pre>
 * {@code
 * // Print all of the YearMonths between now and a year from now, every other month.
 * YearMonthStream
 *     .fromNow()
 *     .to(1, ChronoUnit.YEARS)
 *     .every(2, ChronoUnit.MONTHS)
 *     .stream()
 *     .forEach(System.out::println);
 * }
 * </pre>
 *
 * @author Todd Ginsberg (todd@ginsberg.com)
 */
public class YearMonthStream extends AbstractComparableStream<YearMonth> {
    private long amount = 1;
    private ChronoUnit unit = ChronoUnit.MONTHS;

    private YearMonthStream(final YearMonth from) {
        super(from);
    }

    /**
     * Create a YearMonthStream, starting at YearMonth.now().
     *
     * @return A non-null YearMonthStream.
     */
    public static YearMonthStream fromNow() {
        return new YearMonthStream(YearMonth.now());
    }

    /**
     * Create a YearMonthStream, starting at the given YearMonth.
     *
     * @param from A non-null YearMonth to begin the stream with.
     * @return A non-null YearMonthStream.
     */
    public static YearMonthStream from(final YearMonth from) {
        return new YearMonthStream(from);
    }

    /**
     * Set the inclusive end point of the stream, using an absolute YearMonth.
     *
     * @param to A nullable YearMonth to end the stream with (null means infinite).
     * @return A non-null YearMonthStream.
     */
    public YearMonthStream to(final YearMonth to) {
        setTo(to);
        return this;
    }

    /**
     * Set the inclusive end point of the stream, using a relative duration.
     *
     * @param amount The number of units to use when calculating the duration of the stream. May be negative.
     * @param unit   The non-null unit the amount is denominated in. May not be null.
     * @return A non-null YearMonthStream.
     * @throws java.time.temporal.UnsupportedTemporalTypeException if the unit is not supported.
     * @see ChronoUnit
     */
    public YearMonthStream to(int amount,
                              final ChronoUnit unit) {
        Objects.requireNonNull(unit);
        setTo(getFrom().plus(amount, unit));
        return this;
    }

    /**
     * Set the exclusive end point of the stream, using an absolute YearMonth.
     *
     * @param until A nullable YearMonth to end the stream before (null means infinite).
     * @return A non-null YearMonthStream.
     */
    public YearMonthStream until(final YearMonth until) {
        setUntil(until);
        return this;
    }

    /**
     * Set the exclusive end point of the stream, using a relative duration.
     *
     * @param amount The number of units to use when calculating the duration of the stream. May be negative.
     * @param unit   The non-null unit the amount is denominated in.
     * @return A non-null YearMonthStream.
     * @throws java.time.temporal.UnsupportedTemporalTypeException if the unit is not supported.
     * @see ChronoUnit
     */
    public YearMonthStream until(int amount,
                                 final ChronoUnit unit) {
        Objects.requireNonNull(unit);
        setUntil(getFrom().plus(amount, unit));
        return this;
    }

    /**
     * Set the duration between successive elements produced by the stream. The default
     * for this builder is 1 Month.
     *
     * @param amount The number of units to use when calculating the next element of the stream.
     * @param unit   The non-null unit the amount is denominated in.
     * @return A non-null YearMonthStream.
     * @throws java.time.temporal.UnsupportedTemporalTypeException if the unit is not supported.
     * @see ChronoUnit
     */
    public YearMonthStream every(int amount,
                                 final ChronoUnit unit) {
        Objects.requireNonNull(unit);
        this.amount = Math.abs(amount);
        this.unit = unit;
        YearMonth.now().plus(0, unit); // Fail fast test
        return this;
    }

    /**
     * Set the duration between successive elements produced by the stream. The default
     * for this builder is 1 Month.
     *
     * @return A non-null YearMonthStream.
     * @throws java.time.temporal.UnsupportedTemporalTypeException if the unit is not supported.
     * @see ChronoUnit
     */
    public YearMonthStream every(Period period) {
        Objects.requireNonNull(unit);
        this.unit = ChronoUnit.MONTHS;
        this.amount = period.get(this.unit);
        YearMonth.now().plus(0, unit); // Fail fast test
        return this;
    }

    @Override
    UnaryOperator<YearMonth> next() {
        return date -> date.plus(isForward() ? amount : 0 - amount, unit);
    }
}
