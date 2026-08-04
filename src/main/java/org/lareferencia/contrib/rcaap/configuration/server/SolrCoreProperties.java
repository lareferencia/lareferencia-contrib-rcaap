package org.lareferencia.contrib.rcaap.configuration.server;

import lombok.Getter;
import lombok.Setter;

public class SolrCoreProperties {
    @Getter @Setter private String url;
    @Getter @Setter private String core;
    @Getter @Setter private int connectionTimeout;
    @Getter @Setter private int socketTimeout;
}
