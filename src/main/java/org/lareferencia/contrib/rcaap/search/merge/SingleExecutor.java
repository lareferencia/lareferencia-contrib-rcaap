package org.lareferencia.contrib.rcaap.search.merge;

import org.lareferencia.contrib.rcaap.search.server.ISearchEngineClient;
import org.lareferencia.contrib.rcaap.search.server.model.AbstractBaseEntity;
import org.lareferencia.contrib.rcaap.search.server.model.EntityStatusType;
import org.lareferencia.contrib.rcaap.search.services.SearchService;
import org.springframework.data.domain.Page;

public class SingleExecutor<T extends AbstractBaseEntity> {
    private final ISearchEngineClient client;

    public SingleExecutor(SearchService searchService) {
        this.client = searchService.getClient();
    }

    public AbstractBaseEntity process(AbstractBaseEntity doc) throws Exception {

        // just process single dirty records
        doc.setDirty(0L);
        doc.setStatus(EntityStatusType.SINGLETON.toString());

        copyFieldsToCanonical(doc);

        return doc;
    }

    public void persist(Page<? extends AbstractBaseEntity> result) throws Exception {
        if (!result.hasContent()) return;
        client.addAll(result.getContent());
        client.commit();
    }

    private void copyFieldsToCanonical(AbstractBaseEntity entity) {
        if (entity == null) {
            return;
        }
        String repoFacet = entity.getField("repo_facet_str");
        String instFacet = entity.getField("inst_facet_str");

        entity.addCanonicalRepoFacetMultiField(repoFacet);
        entity.addCanonicalInstFacetMultiField(instFacet);

    }
}
