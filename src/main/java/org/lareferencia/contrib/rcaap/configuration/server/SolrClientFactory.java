package org.lareferencia.contrib.rcaap.configuration.server;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.springframework.stereotype.Component;

@Component
public class SolrClientFactory {

    public SolrClient create(SolrCoreProperties props) {
        return new HttpSolrClient.Builder(
                props.getUrl() + "/" + props.getCore())
                .withConnectionTimeout(props.getConnectionTimeout())
                .withSocketTimeout(props.getSocketTimeout())
                .build();
    }
}
