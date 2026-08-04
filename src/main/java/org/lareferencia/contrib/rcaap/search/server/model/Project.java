package org.lareferencia.contrib.rcaap.search.server.model;

import java.util.List;
import org.apache.solr.client.solrj.beans.Field;
import org.lareferencia.contrib.rcaap.search.merge.EntityType;
import lombok.Getter;
import lombok.Setter;

@BaseEntityType(type = EntityType.PROJECT)
public class Project extends AbstractBaseEntity {

    @Field("semanticIdentifier_str_mv")
    @Getter @Setter private List<String> semanticIdentifiers;

    public Project() {
    }

    public Project(String id, String title) {
        this.setId(id);
        this.setTitle(title);
    }

}
