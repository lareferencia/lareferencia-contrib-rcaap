
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

package org.lareferencia.contrib.rcaap.search.services;

import org.lareferencia.contrib.rcaap.search.helper.SearchConfigurationsCache;
import org.lareferencia.contrib.rcaap.search.model.Configuration;
import org.lareferencia.contrib.rcaap.search.model.Configurations;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * This class is a service to manage search configurations based on spring beans
 * 
 * @author pgraca
 *
 */

public class SearchConfigurationSpringService implements SearchConfigurationService {
    protected Configurations searchConfigurations;
    private static final String SEARCH_SPRING_CONFIG = "/spring-search-configuration.xml";


    @Override
    public void init() {}

    public SearchConfigurationSpringService() {
        try (ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(SEARCH_SPRING_CONFIG)) {
            this.searchConfigurations = context.getBean("searchConfigurations", Configurations.class);
        }
    }

    @Override
    public Configurations getSearchConfigurations() {
        return this.searchConfigurations;
    }

    @Override
    public Configuration getSearchConfigurationByName(String name) {
        // retrieve from cache
        Configuration cachedConfiguration = SearchConfigurationsCache.getInstance().getCachedConfigurations().get(name);
        if (cachedConfiguration != null) {
            return cachedConfiguration;
        } else {
            if (this.searchConfigurations != null) {
                for (Configuration config : this.searchConfigurations.getConfiguration()) {
                    // cache storing
                    SearchConfigurationsCache.getInstance().getCachedConfigurations().put(name, config);

                    if (name.equals(config.getId())) {
                        return config;
                    }
                }
            }
        }

        return new Configuration();
    }
}
