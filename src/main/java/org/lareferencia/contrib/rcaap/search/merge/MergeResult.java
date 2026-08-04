package org.lareferencia.contrib.rcaap.search.merge;

import java.util.List;

public class MergeResult<T> {

    private T canonical;
    private List<T> duplicates;

    public MergeResult(T canonical, List<T> duplicates) {
        this.canonical = canonical;
        this.duplicates = duplicates;
    }

    public T getCanonical() {
        return canonical;
    }

    public List<T> getDuplicates() {
        return duplicates;
    }
}
