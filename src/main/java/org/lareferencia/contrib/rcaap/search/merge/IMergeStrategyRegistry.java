package org.lareferencia.contrib.rcaap.search.merge;

public interface IMergeStrategyRegistry {
    <T> IMergeStrategy<T> getStrategy(String name, Class<T> type);
}
