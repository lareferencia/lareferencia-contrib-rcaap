package org.lareferencia.contrib.rcaap.search.merge;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.lareferencia.contrib.rcaap.search.server.ISearchEngineClient;
import org.lareferencia.contrib.rcaap.search.server.model.AbstractBaseEntity;
import org.lareferencia.contrib.rcaap.search.services.SearchService;
import org.springframework.data.domain.Page;

public class MergeExecutor<T extends AbstractBaseEntity> {

    private final Class<T> type;
    private final IMergeStrategy<T> strategy;
    private final SearchService searchService;
    private final ISearchEngineClient client;

    public MergeExecutor(Class<T> type,
                         IMergeStrategy<T> strategy,
                         SearchService searchService) {
        this.type = type;
        this.strategy = strategy;
        this.searchService = searchService;
        this.client = searchService.getClient();
    }

    public List<T> process(String field, String identifier) throws Exception {
        List<T> all = new ArrayList<>();

        // get duplicated documents
        List<T> docs =
            searchService.findDocumentsByFieldValue(field, identifier, type);

        if (!strategy.shouldMerge(docs)) {
            return List.of();
        }

        // execute merging
        MergeResult<T> result = strategy.merge(docs);
        all.add(result.getCanonical());
        all.addAll(result.getDuplicates());

        return all;
    }

    public void persist(Page<? extends AbstractBaseEntity> result) throws Exception {
        if (!result.hasContent()) return;
        client.addAll(result.getContent());
        client.commit();
    }
}