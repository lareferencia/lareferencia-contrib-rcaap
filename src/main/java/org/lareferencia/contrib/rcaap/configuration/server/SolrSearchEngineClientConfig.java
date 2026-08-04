package org.lareferencia.contrib.rcaap.configuration.server;

import java.util.HashMap;
import java.util.Map;
import org.lareferencia.contrib.rcaap.search.server.ISearchEngineClient;
import org.lareferencia.contrib.rcaap.search.server.SolrSearchEngineClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(SolrServerProperties.class)
public class SolrSearchEngineClientConfig {
    @Bean
    public Map<String, ISearchEngineClient> searchEngineClients(
            SolrServerProperties props,
            SolrClientFactory factory) {

        Map<String, ISearchEngineClient> clients = new HashMap<>();

        props.getCores().forEach((key, coreProps) -> {
            clients.put(key,
                new SolrSearchEngineClient(factory.create(coreProps)));
        });

        return clients;
    }
}
