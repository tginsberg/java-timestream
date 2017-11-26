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

import org.junit.Test;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class ZonedDateTimeStreamTest {

    final ZonedDateTime now = ZonedDateTime.now();

    @Test
    public void stopsBeforeUntilDateGivenByChronoUnits() {
        final Stream<ZonedDateTime> stream = ZonedDateTimeStream
                .from(now)
                .until(2, ChronoUnit.SECONDS)
                .stream();
        assertThat(stream)
                .isNotNull()
                .containsExactly(now, now.plusSeconds(1));
    }

    @Test
    public void stopsBeforeUntilDateGivenByZonedDateTime() {
        final Stream<ZonedDateTime> stream = ZonedDateTimeStream
                .from(now)
                .until(now.plusSeconds(2))
                .stream();
        assertThat(stream)
                .isNotNull()
                .containsExactly(now, now.plusSeconds(1));
    }

    @Test
    public void stopsOnToDateGivenByChronoUnits() {
        final Stream<ZonedDateTime> stream = ZonedDateTimeStream
                .from(now)
                .to(2, ChronoUnit.SECONDS)
                .stream();
        assertThat(stream)
                .isNotNull()
                .containsExactly(now, now.plusSeconds(1), now.plusSeconds(2));
    }

    @Test
    public void stopsOnToDateGivenByZonedDateTime() {
        final Stream<ZonedDateTime> stream = ZonedDateTimeStream
                .from(now)
                .to(now.plusSeconds(2))
                .stream();
        assertThat(stream)
                .isNotNull()
                .containsExactly(now, now.plusSeconds(1), now.plusSeconds(2));
    }

    @Test
    public void stopsBeforeToWhenEveryIsAfterEndDate() {
        final Stream<ZonedDateTime> stream = ZonedDateTimeStream
                .from(now)
                .to(3, ChronoUnit.SECONDS)
                .every(2, ChronoUnit.SECONDS)
                .stream();
        assertThat(stream)
                .isNotNull()
                .containsExactly(now, now.plusSeconds(2));
    }

    @Test
    public void identicalFromAndToCreateOnePointStream() {
        final Stream<ZonedDateTime> stream = ZonedDateTimeStream
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
        final Stream<ZonedDateTime> stream = ZonedDateTimeStream
                .from(now)
                .stream()
                .limit(iterations);
        assertThat(stream)
                .isNotNull()
                .endsWith(now.plus(iterations - 1, ChronoUnit.SECONDS))
                .hasSize(iterations);
    }

    @Test
    public void toBeforeFromRunsBackThroughTime() {
        final Stream<ZonedDateTime> stream = ZonedDateTimeStream
                .from(now)
                .to(-2, ChronoUnit.SECONDS)
                .stream();
        assertThat(stream)
                .isNotNull()
                .containsExactly(now, now.minusSeconds(1), now.minusSeconds(2));
    }

    @Test
    public void stopsBeforeToWhenEveryDurationIsAfterEndDate() {
        final Duration duration = Duration.parse("PT2S");
        final Stream<ZonedDateTime> stream = ZonedDateTimeStream
                .from(now)
                .to(3, ChronoUnit.SECONDS)
                .every(duration)
                .stream();
        assertThat(stream)
                .isNotNull()
                .containsExactly(now, now.plusSeconds(2));
    }

    @Test(expected = NullPointerException.class)
    public void mustHaveFromDate() {
        ZonedDateTimeStream.from(null);
    }

    @Test(expected = NullPointerException.class)
    public void toByUnitsMustHaveUnit() {
        ZonedDateTimeStream.fromNow().to(1, null);
    }

    @Test(expected = NullPointerException.class)
    public void untilByUnitsMustHaveUnit() {
        ZonedDateTimeStream.fromNow().until(1, null);
    }
}