package org.lareferencia.contrib.rcaap.search.merge;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.lareferencia.contrib.rcaap.search.server.model.EntityStatusType;

public abstract class AbstractBaseMergeStrategy<T> implements IMergeStrategy<T> {

    @Override
    public boolean shouldMerge(List<T> candidates) {
        return candidates != null && candidates.size() > 1;
    }

    @Override
    public MergeResult<T> merge(List<T> candidates) {

        T canonical = selectCanonical(candidates);

        List<T> duplicates = candidates.stream()
                .filter(doc -> !getId(doc).equals(getId(canonical)))
                .collect(Collectors.toList());

        // remove dirty flag
        setDirty(canonical, 0L);
        setStatus(canonical, EntityStatusType.CANONICAL);

        // copy fields from canonical
        copyFieldsToCanonical(canonical, canonical);

        // flag the others as dirty
        // and set the canonical ID
        for (T doc : duplicates) {
            setDirty(doc, 1L);
            setStatus(doc, EntityStatusType.DUPLICATE);
            setCanonicalId(doc, getId(canonical));

            copyFieldsToCanonical(doc, canonical);
        }

        return new MergeResult<>(canonical, duplicates);
    }

    protected T selectCanonical(List<T> docs) {

        return docs.stream()
                .max(Comparator.comparingInt(this::completenessScore))
                .orElseThrow();
    }

    protected abstract String getId(T entity);

    protected abstract String getField(T entity, String field);

    protected abstract void addDynamicField(T entity, String field, String value);

    protected abstract void setDirty(T entity, Long value);

    protected abstract void setStatus(T entity, EntityStatusType status);

    protected abstract void setCanonicalId(T entity, String canonicalId);

    protected abstract void addCanonicalRepoFacetMultiField(T entity, String canonicalId);

    protected abstract void addCanonicalInstFacetMultiField(T entity, String canonicalId);

    protected abstract int completenessScore(T entity);
    

    /**
     * Add duplicated fields to the canonical record
     * this is useful when the canonical record should aggregate
     * fields from other entities
     * 
     * @param entity
     * @param canonical
     */
    protected void copyFieldsToCanonical(T entity, T canonical) {
        String repoFacet = getField(entity, "repo_facet_str");
        String instFacet = getField(entity, "inst_facet_str");

        if (repoFacet == null || canonical == null) {
            return;
        }

        addCanonicalRepoFacetMultiField(canonical, repoFacet);
        addCanonicalInstFacetMultiField(canonical, instFacet);

    }
}
