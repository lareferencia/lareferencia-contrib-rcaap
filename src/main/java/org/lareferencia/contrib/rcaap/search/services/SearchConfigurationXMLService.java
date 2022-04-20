
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.PropertyException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lareferencia.contrib.rcaap.search.helper.SearchConfigurationsCache;
import org.lareferencia.contrib.rcaap.search.model.Configuration;
import org.lareferencia.contrib.rcaap.search.model.Configurations;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.ResourceUtils;
import org.xml.sax.SAXException;

import lombok.Getter;
import lombok.Setter;

/**
 * This class is a service to manage search configurations based on an XML
 * config file
 * 
 * @author pgraca
 *
 */

public class SearchConfigurationXMLService implements SearchConfigurationService {
    protected Configurations searchConfigurations;

    private static Logger logger = LogManager.getLogger(SearchConfigurationXMLService.class);
    
    private static final String XSD_SEARCH_SCHEMA_FILE = "/xsd/search-configuration.xsd";

    @Getter
    @Setter
    private String searchConfigFile;

    public SearchConfigurationXMLService() {
        //DON'T DO NOTHING
    }

    @Override
    public void init() {        
        logger.info("Loading: " + searchConfigFile);

        try {
            //Load from classpath
            this.searchConfigurations = loadConfigFromFile( new ClassPathResource(
                    searchConfigFile).getInputStream());
        } catch (IOException e) {
            logger.error("File not found: " + searchConfigFile);
            e.printStackTrace();
        }
    }

    public static Configurations loadConfigFromFile(InputStream file) {
        Configurations config = new Configurations();

        try {
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = factory.newSchema(
                    new StreamSource(SearchConfigurationXMLService.class.getResourceAsStream(XSD_SEARCH_SCHEMA_FILE)));

            JAXBContext jContext = JAXBContext.newInstance(config.getClass());
            Unmarshaller unmarshaller = jContext.createUnmarshaller();
            // validate xml against the schema
            unmarshaller.setSchema(schema);
            config = (Configurations) unmarshaller.unmarshal(file);
            // TODO: create an exception class and throw it
        } catch (PropertyException e) {
            logger.error("Unable to process property: " + e.getMessage());
            e.printStackTrace();
        } catch (JAXBException e) {
            logger.error("Unable to process JAXB: " + e.getMessage());
            e.printStackTrace();
        } catch (SAXException e) {
            logger.error("Unable to process SAX: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            logger.error("Unable to process Unmarshaling process: " + e.getMessage());
            e.printStackTrace();
        }
        return config;
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
