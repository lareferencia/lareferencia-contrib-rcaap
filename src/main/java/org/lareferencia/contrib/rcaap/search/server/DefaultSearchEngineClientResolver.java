package org.lareferencia.contrib.rcaap.search.server;

import java.util.Map;
import org.lareferencia.contrib.rcaap.search.merge.EntityType;
import org.springframework.stereotype.Component;

@Component
public class DefaultSearchEngineClientResolver implements ISearchEngineClientResolver {

    private final Map<String, ISearchEngineClient> clients;

    public DefaultSearchEngineClientResolver(
            Map<String, ISearchEngineClient> clients) {
        this.clients = clients;
    }

    @Override
    public ISearchEngineClient resolve(EntityType entityType) {
        //EntityType entityType = clazz.getAnnotation(BaseEntityType.class).type();
        ISearchEngineClient client = clients.get(entityType.name().toLowerCase());

        if (client == null) {
            throw new IllegalArgumentException("No client for type: " + entityType.name());
        }

        return client;
    }

//    public DefaultSearchEngineClientResolver(
//            @Qualifier("solrSearchEngineClient") ISearchEngineClient client) {
//        this.client = client;
//    }

//    @Override
//    public ISearchEngineClient getClient() {
//        return client;
//    }
}
