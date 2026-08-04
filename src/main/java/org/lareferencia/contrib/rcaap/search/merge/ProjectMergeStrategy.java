package org.lareferencia.contrib.rcaap.search.merge;

import org.lareferencia.contrib.rcaap.search.server.model.EntityStatusType;
import org.lareferencia.contrib.rcaap.search.server.model.Person;
import org.lareferencia.contrib.rcaap.search.server.model.Project;
import org.springframework.stereotype.Component;

@Component("projectMergeStrategy")
public class ProjectMergeStrategy extends AbstractBaseMergeStrategy<Project> {

    @Override
    public Class<Project> getSupportedType() {
        return Project.class;
    }

    @Override
    protected String getId(Project entity) {
        return entity.getId();
    }

    @Override
    protected void addDynamicField(Project entity, String field, String value) {
        entity.addDynamicField(field, value);
    }

    @Override
    protected void setDirty(Project entity, Long value) {
        entity.setDirty(value);
    }

    @Override
    protected void setCanonicalId(Project entity, String canonicalId) {
        entity.addDynamicField("canonical_id_str", entity.getId());
    }

    @Override
    protected int completenessScore(Project entity) {
        int score = 0;

        if (entity.getSemanticIdentifiers() != null) score = score + entity.getSemanticIdentifiers().size();
        if (entity.getTitle() != null) score++;

        // promote records that are public
        if (entity.getVisible() > 0L) score = score + 1000;

        // A canonical record should have a maximum score in merging process = 1000 Points
        if (EntityStatusType.CANONICAL.equals(entity.getEntityStatusType())) score = score + 1000;

        return score;
    }

    @Override
    protected void setStatus(Project entity, EntityStatusType status) {
        entity.setStatus(status);
    }

    @Override
    protected String getField(Project entity, String field) {
        return entity.getField(field);
    }

    @Override
    protected void addCanonicalRepoFacetMultiField(Project entity, String canonicalId) {
        entity.addCanonicalRepoFacetMultiField(canonicalId);
    }

    @Override
    protected void addCanonicalInstFacetMultiField(Project entity, String canonicalId) {
        entity.addCanonicalInstFacetMultiField(canonicalId);
    }

}