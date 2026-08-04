package org.lareferencia.contrib.rcaap.search.server;

import org.lareferencia.contrib.rcaap.search.merge.EntityType;

public interface ISearchEngineClientResolver {
    ISearchEngineClient resolve(EntityType entityType);
}
