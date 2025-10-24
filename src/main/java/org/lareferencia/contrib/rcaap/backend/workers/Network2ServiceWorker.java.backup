
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

package org.lareferencia.contrib.rcaap.backend.workers;

import java.text.NumberFormat;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.json.JSONObject;
import org.json.XML;
import org.lareferencia.backend.domain.Network;
import org.lareferencia.backend.repositories.jpa.NetworkRepository;
import org.lareferencia.core.entity.domain.EntityRelationException;
import org.lareferencia.core.entity.services.EntityDataService;
import org.lareferencia.core.metadata.IMDFormatTransformer;
import org.lareferencia.core.metadata.MDFormatTranformationException;
import org.lareferencia.core.metadata.MDFormatTransformerService;
import org.lareferencia.core.metadata.MedatadaDOMHelper;
import org.lareferencia.core.worker.BaseWorker;
import org.lareferencia.core.worker.NetworkRunningContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;

import lombok.Getter;
import lombok.Setter;

public class Network2ServiceWorker extends BaseWorker<NetworkRunningContext> {

    @Autowired
    private NetworkRepository networkRepository;

    private static Logger logger = LogManager.getLogger(Network2ServiceWorker.class);

    @Autowired
    private EntityDataService erService;

    @Autowired
    private MDFormatTransformerService trfService;

    private IMDFormatTransformer metadataTransformer;

    @Getter
    @Setter
    private String provenancePrefix = "urn:repositoryAcronym:";

    NumberFormat percentajeFormat = NumberFormat.getPercentInstance();

    @Getter
    @Setter
    private boolean debugMode = false;

    @Getter
    @Setter
    private String targetSchemaName = "entities-rcaap";

    @Getter
    @Setter
    private String sourceSchemaName = "network-atributtes";

    @Override
    public String toString() {
        return "Network2Entity";
    }

    private String normalizeXML(String xml) {
        // we need to remove @class and add a root node
        return "<attributes>" + xml.replaceAll("@class", "class") + "</attributes>";
    }

    @Override
    public void run() {
        // it will only be processed one single network, so we don't need more methods
        // or the paginator
        Long id = runningContext.getNetwork().getId();
        Optional<Network> optCurrentNetwork = networkRepository.findById(id);

        if (!optCurrentNetwork.isPresent()) {
            logger.error("Network with id: " + id + " doesn't exist.");
            this.stop();
        }

        String networkAcronym = optCurrentNetwork.get().getAcronym();
        // Convert raw json to xml
        JSONObject json = new JSONObject(optCurrentNetwork.get().getAttributes());

        // we need to remove @class
        String xml = normalizeXML(XML.toString(json));
        if (debugMode) {
            logger.info(xml);
        }

        try {
            metadataTransformer = trfService.getMDTransformer(sourceSchemaName, targetSchemaName);

            // record parameters to transformer
            metadataTransformer.setParameter("identifier", provenancePrefix + networkAcronym);
            metadataTransformer.setParameter("timestamp", new DateTime().toString());

            metadataTransformer.setParameter("networkAcronym", networkAcronym);
            metadataTransformer.setParameter("name", optCurrentNetwork.get().getName());

            metadataTransformer.setParameter("institutionName", optCurrentNetwork.get().getInstitutionName());
            metadataTransformer.setParameter("institutionAcronym", optCurrentNetwork.get().getInstitutionAcronym());

            // transformed attributes to entities
            Document entityDataDocument = metadataTransformer.transform(MedatadaDOMHelper.XMLString2Document(xml));
            if (debugMode) {
                logger.info(MedatadaDOMHelper.document2XMLString(entityDataDocument));
            }

            // Parse and persist entities
            erService.parseAndPersistEntityRelationDataFromXMLDocument(entityDataDocument, false);

            this.postRun();            
            
            logger.info("END - Entities generated for: " + networkAcronym);
            

        } catch (MDFormatTranformationException e) {
            logger.error("Attributes transformation ERROR for network: " + networkAcronym
                    + " (id:" + id + ") with message: " + e.getMessage());
            this.stop();
        } catch (EntityRelationException e1) {
            logger.error("ERROR ingesting network: " + networkAcronym + " (id:" + id
                    + "), with message: " + e1.getMessage());
            this.stop();
        } catch (Exception e2) {
            logger.error("ERROR with network: " + networkAcronym + " (id:" + id
                    + "), with message: " + e2.getMessage());
            e2.printStackTrace();
            this.stop();
        }
    }
    
    private void postRun () {
        logger.info("Merging entities relation data ...");
        erService.mergeEntityRelationData();
    }

}
