package org.lareferencia.contrib.rcaap.search.merge;

import java.util.List;

public interface IMergeStrategy<T> {

    boolean shouldMerge(List<T> candidates);

    MergeResult<T> merge(List<T> candidates);

    Class<T> getSupportedType();
}
