package org.lareferencia.contrib.rcaap.search.merge;

import org.lareferencia.contrib.rcaap.search.server.model.EntityStatusType;
import org.lareferencia.contrib.rcaap.search.server.model.Person;
import org.springframework.stereotype.Component;

@Component("personMergeStrategy")
public class PersonMergeStrategy extends AbstractBaseMergeStrategy<Person> {

    @Override
    public Class<Person> getSupportedType() {
        return Person.class;
    }

    @Override
    protected String getId(Person entity) {
        return entity.getId();
    }

    @Override
    protected void addDynamicField(Person entity, String field, String value) {
        entity.addDynamicField(field, value);
    }

    @Override
    protected void setDirty(Person entity, Long value) {
        entity.setDirty(value);
    }

    @Override
    protected void setCanonicalId(Person entity, String canonicalId) {
        entity.addDynamicField("canonical_id_str", entity.getId());
    }

    @Override
    protected int completenessScore(Person entity) {
        int score = 0;

        if (entity.getSemanticIdentifiers() != null) score = score + entity.getSemanticIdentifiers().size();
        if (entity.getTitle() != null) score++;
        if (entity.getCienciaIDs() != null) score++;
        if (entity.getOrcids() != null) score++;

        // promote records that are public
        if (entity.getVisible() > 0L) score = score + 1000;

        // A canonical record should have a maximum score in merging process = 1000 Points
        if (EntityStatusType.CANONICAL.equals(entity.getEntityStatusType())) score = score + 1000;

        return score;
    }

    @Override
    protected void setStatus(Person entity, EntityStatusType status) {
        entity.setStatus(status);
    }

    @Override
    protected String getField(Person entity, String field) {
        return entity.getField(field);
    }

    @Override
    protected void addCanonicalRepoFacetMultiField(Person entity, String canonicalId) {
        entity.addCanonicalRepoFacetMultiField(canonicalId);
    }

    @Override
    protected void addCanonicalInstFacetMultiField(Person entity, String canonicalId) {
        entity.addCanonicalInstFacetMultiField(canonicalId);
    }

}