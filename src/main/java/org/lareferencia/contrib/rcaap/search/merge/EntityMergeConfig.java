package org.lareferencia.contrib.rcaap.search.merge;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

public class EntityMergeConfig {

    @Getter @Setter private EntityType type;
    @Getter @Setter private String strategy;
    @Getter @Setter private List<String> fields;

}