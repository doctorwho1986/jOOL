/**
 * Copyright (c) 2009-2014, Data Geekery GmbH (http://www.datageekery.com)
 * All rights reserved.
 *
 * This work is dual-licensed
 * - under the Apache Software License 2.0 (the "ASL")
 * - under the jOOQ License and Maintenance Agreement (the "jOOQ License")
 * =============================================================================
 * You may choose which license applies to you:
 *
 * - If you're using this work with Open Source databases, you may choose
 *   either ASL or jOOQ License.
 * - If you're using this work with at least one commercial database, you must
 *   choose jOOQ License
 *
 * For more information, please visit http://www.jooq.org/licenses
 *
 * Apache Software License 2.0:
 * -----------------------------------------------------------------------------
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * jOOQ License and Maintenance Agreement:
 * -----------------------------------------------------------------------------
 * Data Geekery grants the Customer the non-exclusive, timely limited and
 * non-transferable license to install and use the Software under the terms of
 * the jOOQ License and Maintenance Agreement.
 *
 * This library is distributed with a LIMITED WARRANTY. See the jOOQ License
 * and Maintenance Agreement for more details: http://www.jooq.org/licensing
 */
package org.jooq.lambda;

import org.jooq.lambda.tuple.Tuple2;
import org.junit.Test;

import java.util.HashSet;
import java.util.List;
import java.util.function.Supplier;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.jooq.lambda.tuple.Tuple.tuple;
import static org.junit.Assert.assertEquals;

/**
 * @author Lukas Eder
 */
public class SeqTest {

    @Test
    public void testZipEqualLength() {
        List<Tuple2<Integer, String>> list = Seq.of(1, 2, 3).zip(Seq.of("a", "b", "c")).collect(toList());

        assertEquals(3, list.size());
        assertEquals(1, (int) list.get(0).v1);
        assertEquals(2, (int) list.get(1).v1);
        assertEquals(3, (int) list.get(2).v1);
        assertEquals("a", list.get(0).v2);
        assertEquals("b", list.get(1).v2);
        assertEquals("c", list.get(2).v2);
    }

    @Test
    public void testZipDifferingLength() {
        List<Tuple2<Integer, String>> list = Seq.of(1, 2).zip(Seq.of("a", "b", "c", "d")).collect(toList());

        assertEquals(2, list.size());
        assertEquals(1, (int) list.get(0).v1);
        assertEquals(2, (int) list.get(1).v1);
        assertEquals("a", list.get(0).v2);
        assertEquals("b", list.get(1).v2);
    }

    @Test
    public void testZipWithIndex() {
        assertEquals(asList(), Seq.of().zipWithIndex().collect(toList()));
        assertEquals(asList(tuple("a", 0L)), Seq.of("a").zipWithIndex().collect(toList()));
        assertEquals(asList(tuple("a", 0L), tuple("b", 1L)), Seq.of("a", "b").zipWithIndex().collect(toList()));
        assertEquals(asList(tuple("a", 0L), tuple("b", 1L), tuple("c", 2L)), Seq.of("a", "b", "c").zipWithIndex().collect(toList()));
    }

    @Test
    public void testDuplicate() {
        Supplier<Tuple2<Seq<Integer>, Seq<Integer>>> reset = () -> Seq.of(1, 2, 3).duplicate();
        Tuple2<Seq<Integer>, Seq<Integer>> duplicate;

        duplicate = reset.get();
        assertEquals(asList(1, 2, 3), duplicate.v1.collect(toList()));
        assertEquals(asList(1, 2, 3), duplicate.v2.collect(toList()));

        duplicate = reset.get();
        assertEquals(asList(1, 2, 3, 1, 2, 3), Streams.concat(duplicate.v1, duplicate.v2).collect(toList()));

        duplicate = reset.get();
        assertEquals(asList(tuple(1, 1), tuple(2, 2), tuple(3, 3)), Streams.zip(duplicate.v1, duplicate.v2).collect(toList()));
    }

    @Test
    public void testToString() {
        assertEquals("123", Seq.of(1, 2, 3).toString());
        assertEquals("1, 2, 3", Seq.of(1, 2, 3).toString(", "));
        assertEquals("1, null, 3", Seq.of(1, null, 3).toString(", "));
    }

    @Test
    public void testSlice() {
        Supplier<Seq<Integer>> s = () -> Seq.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        assertEquals(asList(3, 4, 5), s.get().slice(2, 5).collect(toList()));
        assertEquals(asList(4, 5, 6), s.get().slice(3, 6).collect(toList()));
        assertEquals(asList(), s.get().slice(4, 1).collect(toList()));
        assertEquals(asList(1, 2, 3, 4, 5, 6), s.get().slice(0, 6).collect(toList()));
        assertEquals(asList(1, 2, 3, 4, 5, 6), s.get().slice(-1, 6).collect(toList()));
        assertEquals(asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10), s.get().slice(-1, 12).collect(toList()));
    }

    @Test
    public void testToList() {
        assertEquals(asList(1, 2, 2, 3), Seq.of(1, 2, 2, 3).toList());
    }

    @Test
    public void testToSet() {
        assertEquals(new HashSet<>(asList(1, 2, 3)), Seq.of(1, 2, 2, 3).toSet());
    }

    @Test
    public void testSkipWhile() {
        Supplier<Seq<Integer>> s = () -> Seq.of(1, 2, 3, 4, 5);

        assertEquals(asList(1, 2, 3, 4, 5), s.get().skipWhile(i -> false).collect(toList()));
        assertEquals(asList(3, 4, 5), s.get().skipWhile(i -> i % 3 != 0).collect(toList()));
        assertEquals(asList(3, 4, 5), s.get().skipWhile(i -> i < 3).collect(toList()));
        assertEquals(asList(4, 5), s.get().skipWhile(i -> i < 4).collect(toList()));
        assertEquals(asList(), s.get().skipWhile(i -> true).collect(toList()));
    }

    @Test
    public void testSkipUntil() {
        Supplier<Seq<Integer>> s = () -> Seq.of(1, 2, 3, 4, 5);

        assertEquals(asList(), s.get().skipUntil(i -> false).collect(toList()));
        assertEquals(asList(3, 4, 5), s.get().skipUntil(i -> i % 3 == 0).collect(toList()));
        assertEquals(asList(3, 4, 5), s.get().skipUntil(i -> i == 3).collect(toList()));
        assertEquals(asList(4, 5), s.get().skipUntil(i -> i == 4).collect(toList()));
        assertEquals(asList(1, 2, 3, 4, 5), s.get().skipUntil(i -> true).collect(toList()));
    }

    @Test
    public void testLimitWhile() {
        Supplier<Seq<Integer>> s = () -> Seq.of(1, 2, 3, 4, 5);

        assertEquals(asList(), s.get().limitWhile(i -> false).collect(toList()));
        assertEquals(asList(1, 2), s.get().limitWhile(i -> i % 3 != 0).collect(toList()));
        assertEquals(asList(1, 2), s.get().limitWhile(i -> i < 3).collect(toList()));
        assertEquals(asList(1, 2, 3), s.get().limitWhile(i -> i < 4).collect(toList()));
        assertEquals(asList(1, 2, 3, 4, 5), s.get().limitWhile(i -> true).collect(toList()));
    }

    @Test
    public void testLimitUntil() {
        Supplier<Seq<Integer>> s = () -> Seq.of(1, 2, 3, 4, 5);

        assertEquals(asList(1, 2, 3, 4, 5), s.get().limitUntil(i -> false).collect(toList()));
        assertEquals(asList(1, 2), s.get().limitUntil(i -> i % 3 == 0).collect(toList()));
        assertEquals(asList(1, 2), s.get().limitUntil(i -> i == 3).collect(toList()));
        assertEquals(asList(1, 2, 3), s.get().limitUntil(i -> i == 4).collect(toList()));
        assertEquals(asList(), s.get().limitUntil(i -> true).collect(toList()));
    }
}