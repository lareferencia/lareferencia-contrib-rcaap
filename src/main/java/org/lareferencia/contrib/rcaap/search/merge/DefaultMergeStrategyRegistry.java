package org.lareferencia.contrib.rcaap.search.merge;

import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class DefaultMergeStrategyRegistry  implements IMergeStrategyRegistry {

    private final Map<String, IMergeStrategy<?>> strategies;

    public DefaultMergeStrategyRegistry(Map<String, IMergeStrategy<?>> strategies) {
        this.strategies = strategies;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> IMergeStrategy<T> getStrategy(String name, Class<T> type) {
        IMergeStrategy<?> strategy = strategies.get(name);

        if (strategy == null) {
            throw new IllegalArgumentException("Strategy not found: " + name);
        }

        if (!supportsType(strategy, type)) {
            throw new IllegalStateException(
                "Strategy " + name + " does not support type " + type.getSimpleName()
            );
        }

        return (IMergeStrategy<T>) strategy;
    }

    private <T> boolean supportsType(IMergeStrategy<?> strategy, Class<T> type) {
        return strategy.getSupportedType().equals(type);
    }
}