package org.lareferencia.contrib.rcaap.configuration.server;

import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "merge.solr")
public class SolrServerProperties {

    private Map<String, SolrCoreProperties> cores = new HashMap<>();

    public Map<String, SolrCoreProperties> getCores() {
        return cores;
    }

    public void setCores(Map<String, SolrCoreProperties> cores) {
        this.cores = cores;
    }

}