/*
 *
 * Copyright 2017-2018 Nitrite author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.dizitart.no2.collection.objects;

import org.dizitart.no2.collection.RecordIterable;
import org.dizitart.no2.collection.objects.data.SubEmployee;
import org.dizitart.no2.exceptions.InvalidOperationException;
import org.junit.Test;

import java.util.Iterator;

import static org.dizitart.no2.collection.FindOptions.limit;
import static org.junit.Assert.*;

/**
 * @author Anindya Chatterjee
 */
public class ProjectionTest extends BaseObjectRepositoryTest {

    @Test
    public void testHasMore() {
        RecordIterable<SubEmployee> iterable = employeeRepository.find(limit(0, 5))
                .project(SubEmployee.class);
        assertTrue(iterable.hasMore());
    }

    @Test
    public void testSize() {
        RecordIterable<SubEmployee> iterable = employeeRepository.find(limit(0, 5))
                .project(SubEmployee.class);
        assertEquals(iterable.size(), 5);
    }

    @Test
    public void testTotalCount() {
        RecordIterable<SubEmployee> iterable = employeeRepository.find(limit(0, 5))
                .project(SubEmployee.class);
        assertEquals(iterable.totalCount(), 10);
    }

    @Test
    public void testToString() {
        RecordIterable<SubEmployee> iterable = employeeRepository.find(limit(0, 5))
                .project(SubEmployee.class);
        assertNotNull(iterable.toString());
    }

    @Test(expected = InvalidOperationException.class)
    public void testRemove() {
        RecordIterable<SubEmployee> iterable = employeeRepository.find(limit(0, 5))
                .project(SubEmployee.class);
        Iterator<SubEmployee> iterator = iterable.iterator();
        if (iterator.hasNext()) {
            iterator.next();
            iterator.remove();
        }
    }
}