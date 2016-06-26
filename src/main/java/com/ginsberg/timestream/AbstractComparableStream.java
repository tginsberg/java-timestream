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

import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.stream.Stream.iterate;

/**
 * Provides generic support for open and closed-ended ranges of objects
 * that can be compared.
 *
 * @param <T> A type that implements `Comparable`.
 * @author Todd Ginsberg (todd@ginsberg.com)
 */
abstract class AbstractComparableStream<T extends Comparable<? super T>> {
    private final T from;
    private T to;
    private boolean closedRange = false;

    AbstractComparableStream(final T from) {
        Objects.requireNonNull(from);
        this.from = from;
    }

    void setTo(final T to) {
        this.to = to;
        this.closedRange = false;
    }

    void setUntil(final T until) {
        this.to = until;
        this.closedRange = true;
    }

    boolean isForward() {
        return to == null || from.compareTo(to) <= 0;
    }

    T getFrom() {
        return from;
    }

    /**
     * Provide an operator that returns the next value in the series.
     * @return A non-null UnaryOperator
     */
    abstract UnaryOperator<T> next();

    /**
     * Produce a stream between the dates given, skipping
     * by the amount specified.
     *
     * @return A non-null stream of time/date.
     */
    public Stream<T> stream() {
        return StreamSupport.stream(
                TakeWhile.of(
                        iterate(from, next()).spliterator(),
                        canTake()),
                false);

    }

    /**
     * Method that generates a predicate that is used by the Spliterator to
     * implement a limitation on the stream. We can take the next value in
     * the stream if one of the following are true:
     *
     * 1) There is no bound to the stream (to is null).
     * 2) We are before the end when we are moving forward through time.
     * 3) We are after the end when we are moving backward through time.
     * 4) The current time equals the end and we are not a closed range.
     *
     * @return A Predicate
     */
    private Predicate<T> canTake() {
        return x -> {
            if (to == null) {
                return true;
            } else {
                final int compare = x.compareTo(to);
                if (compare < 0) {
                    return isForward();
                } else if (compare > 0) {
                    return !isForward();
                } else {
                    return !closedRange;
                }
            }
        };
    }
}
