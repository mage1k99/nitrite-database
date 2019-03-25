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

package org.dizitart.no2.collection.operation;

import org.dizitart.no2.Document;
import org.dizitart.no2.NitriteId;
import org.dizitart.no2.collection.DocumentCursor;
import org.dizitart.no2.collection.Lookup;
import org.dizitart.no2.collection.RecordIterable;
import org.dizitart.no2.exceptions.InvalidOperationException;
import org.dizitart.no2.store.NitriteMap;

import java.util.*;

import static org.dizitart.no2.common.util.ObjectUtils.deepEquals;
import static org.dizitart.no2.exceptions.ErrorMessage.REMOVE_ON_DOCUMENT_ITERATOR_NOT_SUPPORTED;

/**
 * @author Anindya Chatterjee.
 */
class JoinedDocumentIterable implements RecordIterable<Document> {
    private final Collection<NitriteId> resultSet;
    private final NitriteMap<NitriteId, Document> nitriteMap;
    private boolean hasMore;
    private int totalCount;
    private DocumentCursor foreignCursor;
    private Lookup lookup;

    JoinedDocumentIterable(FindResult findResult, DocumentCursor foreignCursor, Lookup lookup) {
        this.foreignCursor = foreignCursor;
        this.lookup = lookup;
        if (findResult.getIdSet() != null) {
            resultSet = findResult.getIdSet();
        } else {
            resultSet = new TreeSet<>();
        }
        this.nitriteMap = findResult.getNitriteMap();
        this.hasMore = findResult.isHasMore();
        this.totalCount = findResult.getTotalCount();
    }

    @Override
    public boolean hasMore() {
        return hasMore;
    }

    @Override
    public int size() {
        return resultSet.size();
    }

    @Override
    public int totalCount() {
        return totalCount;
    }

    @Override
    public Iterator<Document> iterator() {
        return new JoinedDocumentIterator();
    }

    @Override
    public String toString() {
        return toList().toString();
    }

    private class JoinedDocumentIterator implements Iterator<Document> {
        private Iterator<NitriteId> iterator;

        JoinedDocumentIterator() {
            iterator = resultSet.iterator();
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Document next() {
            NitriteId next = iterator.next();
            Document document = nitriteMap.get(next);
            return join(document, foreignCursor, lookup);
        }

        @Override
        public void remove() {
            throw new InvalidOperationException(REMOVE_ON_DOCUMENT_ITERATOR_NOT_SUPPORTED);
        }

        private Document join(Document localDocument, DocumentCursor foreignCursor, Lookup lookup) {
            Object localObject = localDocument.get(lookup.getLocalField());
            if (localObject == null) return localDocument;
            Document resultDocument = new Document(localDocument);
            Set<Document> target = new HashSet<>();

            for (Document foreignDocument: foreignCursor) {
                Object foreignObject = foreignDocument.get(lookup.getForeignField());
                if (foreignObject != null) {
                    if (deepEquals(foreignObject, localObject)) {
                        target.add(foreignDocument);
                    }
                }
            }
            if (!target.isEmpty()) {
                resultDocument.put(lookup.getTargetField(), target);
            }
            return resultDocument;
        }
    }
}
