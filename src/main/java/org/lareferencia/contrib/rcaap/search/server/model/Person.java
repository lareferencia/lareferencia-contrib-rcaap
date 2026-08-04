package org.lareferencia.contrib.rcaap.search.server.model;

import java.util.List;
import org.apache.solr.client.solrj.beans.Field;
import org.lareferencia.contrib.rcaap.search.merge.EntityType;
import lombok.Getter;
import lombok.Setter;

@BaseEntityType(type = EntityType.PERSON)
public class Person extends AbstractBaseEntity {

    @Field("semanticIdentifier_str_mv")
    @Getter @Setter private List<String> semanticIdentifiers;

    @Field("orcid.fl_str_mv")
    @Getter @Setter private List<String> orcids;

    @Field("cienciaID.fl_str_mv")
    @Getter @Setter private List<String> cienciaIDs;

    public Person() {
    }

    public Person(String id, String title) {
        this.setId(id);
        this.setTitle(title);
    }

}
