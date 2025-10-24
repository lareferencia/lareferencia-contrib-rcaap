
/*
 *   Copyright (c) 2013-2022. LA Referencia / Red CLARA and others
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Affero General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 *   This file is part of LA Referencia software platform LRHarvester v4.x
 *   For any further information please contact Lautaro Matas <lmatas@gmail.com>
 */

package org.lareferencia.contrib.rcaap.configuration.backend;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.repository.config.EnableSolrRepositories;

@EntityScan(basePackages = { "org.lareferencia.contrib.rcaap.backend.domain" })
@EnableJpaRepositories(basePackages = { "org.lareferencia.contrib.rcaap.backend.repositories.jpa" })
@EnableSolrRepositories(basePackages = { "org.lareferencia.contrib.rcaap.backend.repositories.solr" })
@ComponentScan(basePackages = { "org.lareferencia.contrib.rcaap.backend.services",
        "org.lareferencia.contrib.rcaap.backend.workers" })

@Configuration
public class BackendConfiguration {

    @Bean(name = "coreSolrClient")
    public SolrClient coreSolrClient(@Value("${core.solr.url}") String solrHost) {
        return new HttpSolrClient.Builder(solrHost).build();
    }

    @Bean(name = "historicSolrClient")
    public SolrClient historicSolrClient(@Value("${historic.solr.url}") String solrHost) {
        return new HttpSolrClient.Builder(solrHost).build();
    }

    @Bean(name = "networkSolrClient")
    public SolrClient networkSolrClient(@Value("${network.solr.server}") String solrHost) {
        return new HttpSolrClient.Builder(solrHost).build();
    }

    @Bean(name = "projectSolrClient")
    public SolrClient projectSolrClient(@Value("${project.solr.server}") String solrHost) {
        return new HttpSolrClient.Builder(solrHost).build();
    }

    @Bean(name = "coreSolrTemplate")
    public SolrTemplate coreSolrTemplate(@Qualifier("coreSolrClient") SolrClient solrClient) throws Exception {
        return new SolrTemplate(solrClient);
    }

    @Bean(name = "historicSolrTemplate")
    public SolrTemplate historicSolrTemplate(@Qualifier("historicSolrClient") SolrClient solrClient) throws Exception {
        return new SolrTemplate(solrClient);
    }

    @Bean(name = "networkSolrTemplate")
    public SolrTemplate networkSolrTemplate(@Qualifier("networkSolrClient") SolrClient solrClient) throws Exception {
        return new SolrTemplate(solrClient);
    }

    @Bean(name = "projectSolrTemplate")
    public SolrTemplate projectSolrTemplate(@Qualifier("projectSolrClient") SolrClient solrClient) throws Exception {
        return new SolrTemplate(solrClient);
    }

}
