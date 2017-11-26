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

import org.assertj.core.util.Sets;
import org.junit.Test;

import java.time.Period;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import java.util.stream.Stream;

import static com.ginsberg.timestream.util.Assertions.expectingChronoUnitException;
import static com.ginsberg.timestream.util.Assertions.notExpectingChronoUnitException;
import static org.assertj.core.api.Assertions.assertThat;

public class YearMonthStreamTest {

    final YearMonth now = YearMonth.now();
    private final Set<ChronoUnit> validChronoUnits = Sets.newLinkedHashSet(ChronoUnit.MONTHS, ChronoUnit.YEARS,
            ChronoUnit.DECADES, ChronoUnit.CENTURIES, ChronoUnit.ERAS, ChronoUnit.MILLENNIA);

    @Test
    public void stopsBeforeUntilDateGivenByChronoUnits() {
        final Stream<YearMonth> stream = YearMonthStream
                .from(now)
                .until(2, ChronoUnit.MONTHS)
                .stream();
        assertThat(stream)
                .isNotNull()
                .containsExactly(now, now.plusMonths(1));
    }

    @Test
    public void stopsBeforeUntilDateGivenByYearMonth() {
        final Stream<YearMonth> stream = YearMonthStream
                .from(now)
                .until(now.plusMonths(2))
                .stream();
        assertThat(stream)
                .isNotNull()
                .containsExactly(now, now.plusMonths(1));
    }

    @Test
    public void stopsOnToDateGivenByChronoUnits() {
        final Stream<YearMonth> stream = YearMonthStream
                .from(now)
                .to(2, ChronoUnit.MONTHS)
                .stream();
        assertThat(stream)
                .isNotNull()
                .containsExactly(now, now.plusMonths(1), now.plusMonths(2));
    }

    @Test
    public void stopsOnToDateGivenByYearMonth() {
        final Stream<YearMonth> stream = YearMonthStream
                .from(now)
                .to(now.plusMonths(2))
                .stream();
        assertThat(stream)
                .isNotNull()
                .containsExactly(now, now.plusMonths(1), now.plusMonths(2));
    }

    @Test
    public void stopsBeforeToWhenEveryIsAfterEndDate() {
        final Stream<YearMonth> stream = YearMonthStream
                .from(now)
                .to(3, ChronoUnit.MONTHS)
                .every(2, ChronoUnit.MONTHS)
                .stream();
        assertThat(stream)
                .isNotNull()
                .containsExactly(now, now.plusMonths(2));
    }

    @Test
    public void identicalFromAndToCreateOnePointStream() {
        final Stream<YearMonth> stream = YearMonthStream
                .from(now)
                .to(now)
                .stream();
        assertThat(stream)
                .isNotNull()
                .containsExactly(now);
    }

    @Test
    public void noToDateRunsForever() {
        // No real way to test that a stream never ends so we will just make sure that this generates a lot of iterations.
        final int iterations = 1_000_000;
        final Stream<YearMonth> stream = YearMonthStream
                .from(now)
                .stream()
                .limit(iterations);
        assertThat(stream)
                .isNotNull()
                .endsWith(now.plus(iterations - 1, ChronoUnit.MONTHS))
                .hasSize(iterations);
    }

    @Test
    public void toBeforeFromRunsBackThroughTime() {
        final Stream<YearMonth> stream = YearMonthStream
                .from(now)
                .to(-2, ChronoUnit.MONTHS)
                .stream();
        assertThat(stream)
                .isNotNull()
                .containsExactly(now, now.minusMonths(1), now.minusMonths(2));
    }

    @Test
    public void stopsBeforeToWhenEveryDurationIsAfterEndDate() {
        final Period period = Period.parse("P2M");
        final Stream<YearMonth> stream = YearMonthStream
                .from(now)
                .to(3, ChronoUnit.MONTHS)
                .every(period)
                .stream();
        assertThat(stream)
                .isNotNull()
                .containsExactly(now, now.plusMonths(2));
    }

    @Test(expected = NullPointerException.class)
    public void mustHaveFromDate() {
        YearMonthStream.from(null);
    }

    @Test(expected = NullPointerException.class)
    public void toByUnitsMustHaveUnit() {
        YearMonthStream.fromNow().to(1, null);
    }

    @Test(expected = NullPointerException.class)
    public void untilByUnitsMustHaveUnit() {
        YearMonthStream.fromNow().until(1, null);
    }

    @Test
    public void everyWithInvalidChronoUnitFailsFast() {
        expectingChronoUnitException(u -> YearMonthStream.fromNow().every(0, u), validChronoUnits);
    }

    @Test
    public void everyWithValidChronoUnit() {
        notExpectingChronoUnitException(u -> YearMonthStream.fromNow().every(0, u), validChronoUnits);
    }

    @Test
    public void toWithInvalidChronoUnitFailsFast() {
        expectingChronoUnitException(u -> YearMonthStream.fromNow().to(0, u), validChronoUnits);
    }

    @Test
    public void toWithValidChronoUnit() {
        notExpectingChronoUnitException(u -> YearMonthStream.fromNow().to(0, u), validChronoUnits);
    }

    @Test
    public void untilWithInvalidChronoUnitFailsFast() {
        expectingChronoUnitException(u -> YearMonthStream.fromNow().until(0, u), validChronoUnits);
    }

    @Test
    public void untilWithValidChronoUnit() {
        notExpectingChronoUnitException(u -> YearMonthStream.fromNow().until(0, u), validChronoUnits);
    }

}