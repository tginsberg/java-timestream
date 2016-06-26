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

import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Implementation of TakeWhile, which makes most of this library possible.
 *
 * This behavior (along with the complementary dropWhile) is coming
 * in JDK 9, but since I need it now, here it is.
 *
 * @param <T> Type that the predicate and spliterator handle.
 * @see java.util.Spliterators.AbstractSpliterator
 * @author Todd Ginsberg (todd@ginsberg.com)
 */
public class TakeWhile<T> extends Spliterators.AbstractSpliterator<T> {

    private final Spliterator<T> spliterator;
    private final Predicate<? super T> predicate;
    private boolean hasEnded = false;

    public static <T> TakeWhile<T> of(final Spliterator<T> spliterator,
                                      final Predicate<? super T> predicate) {
        return new TakeWhile<>(spliterator, predicate);
    }

    private TakeWhile(final Spliterator<T> spliterator,
                     final Predicate<? super T> predicate) {
        super(spliterator.estimateSize(), 0);
        this.spliterator = spliterator;
        this.predicate = predicate;
    }

    @Override
    public boolean tryAdvance(final Consumer<? super T> consumer) {
        if (!hasEnded) {
            final boolean hadNext = spliterator.tryAdvance(e -> {
                if (predicate.test(e)) {
                    consumer.accept(e);
                } else {
                    hasEnded = true;
                }
            });
            return hadNext && !hasEnded;
        }
        return false;
    }
}
