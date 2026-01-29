
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

import java.io.IOException;
import java.text.NumberFormat;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.request.DirectXmlRequest;
import org.joda.time.DateTime;
import org.json.JSONObject;
import org.json.XML;
import org.lareferencia.backend.domain.Network;
import org.lareferencia.backend.repositories.jpa.NetworkRepository;
import org.lareferencia.core.metadata.IMDFormatTransformer;
import org.lareferencia.core.metadata.MDFormatTranformationException;
import org.lareferencia.core.metadata.MDFormatTransformerService;
import org.lareferencia.core.metadata.MedatadaDOMHelper;
import org.lareferencia.core.worker.BaseWorker;
import org.lareferencia.core.worker.NetworkRunningContext;
import org.springframework.beans.factory.annotation.Autowired;
import lombok.Getter;
import lombok.Setter;

public class NetworkIndexWorker extends BaseWorker<NetworkRunningContext> {

    @Autowired
    private NetworkRepository networkRepository;

    private static Logger logger = LogManager.getLogger(NetworkIndexWorker.class);

    @Autowired
    private MDFormatTransformerService trfService;

    private IMDFormatTransformer metadataTransformer;

    private HttpSolrClient solrClient;

    @Getter
    @Setter
    private String solrNetworkIDField;

    @Getter
    @Setter
    private String solrRecordIDField;

    @Getter
    @Setter
    private String provenancePrefix = "urn:repositoryAcronym:";

    NumberFormat percentajeFormat = NumberFormat.getPercentInstance();
    private StringBuffer stringBuffer;

    @Getter
    @Setter
    private boolean debugMode = false;

    @Getter
    @Setter
    private boolean executeDeletion;

    @Getter
    @Setter
    private boolean indexNetworkAttributes = true;

    @Getter
    @Setter
    private String targetSchemaName = "vufind-service";

    @Getter
    @Setter
    private String sourceSchemaName = "network-atributtes";

    public NetworkIndexWorker(String solrURL) {
        super();

        this.solrClient = new HttpSolrClient.Builder(solrURL).build();
    }

    @Override
    public String toString() {
        return "Network" + (executeDeletion ? " delete index" : " index");
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

        if (executeDeletion) {
            this.delete(Long.toString(id));
            logger.info("Delete network from index: " + id);
        }

        try {
            this.preRun();

            metadataTransformer = trfService.getMDTransformer(sourceSchemaName, targetSchemaName);

            // record parameters to transformer
            metadataTransformer.setParameter("identifier", provenancePrefix + networkAcronym);
            metadataTransformer.setParameter("timestamp", new DateTime().toString());

            metadataTransformer.setParameter("networkAcronym", networkAcronym);
            metadataTransformer.setParameter("networkName", optCurrentNetwork.get().getName());

            metadataTransformer.setParameter("institutionName", optCurrentNetwork.get().getInstitutionName());
            metadataTransformer.setParameter("institutionAcronym", optCurrentNetwork.get().getInstitutionAcronym());


            // transformed attributes to entities
            String solrDocument = metadataTransformer.transformToString(MedatadaDOMHelper.XMLString2Document(xml));
            
            if (debugMode) {
                logger.info(solrDocument);
            }

            stringBuffer.append(solrDocument);
            logger.debug("Indexed record size: " + solrDocument.length());
            this.add(stringBuffer.toString());
            

            this.postRun();

            logger.info("END - indexing network: " + networkAcronym);

        } catch (MDFormatTranformationException e) {
            logger.error("Attributes transformation ERROR for network: " + networkAcronym
                    + " (id:" + id + ") with message: " + e.getMessage());
            this.stop();
        } catch (Exception e2) {
            logger.error("ERROR with network: " + networkAcronym + " (id:" + id
                    + "), with message: " + e2.getMessage());
            e2.printStackTrace();
            this.stop();
        }

    }

    private void preRun () {
        stringBuffer = new StringBuffer();
    }
    
    private void postRun () {
        try {
            this.sendUpdateToSolr("<commit/>");

            logInfo("End of index tasks: "+ runningContext.toString() + "(" + this.targetSchemaName + ")");
        } catch (SolrServerException | IOException | HttpSolrClient.RemoteSolrException e) {
            logError("Issues when commiting to SOLR: " + runningContext.toString() + ": " + e.getMessage());
            error();
        }
    }

    private void add(String networkAcronym) {
        if (stringBuffer.length() > 0) {
            try {
                this.sendUpdateToSolr("<add>" + stringBuffer.toString() + "</add>");
            } catch (SolrServerException e) {
                logError("Issues whe connecting to SOLR: " + runningContext.toString() + ": " + e.getMessage());
                logger.debug(stringBuffer);
                solrRollback();
                error();

            } catch (IOException e) {
                logError("Issues when sending to SOLR - I/O: " + runningContext.toString() 
                + ": " + e.getMessage());
                logger.debug(stringBuffer);
                solrRollback();
                error();

            } catch (Exception e) {
                logError("Issues with the index process - Undetermined: " + runningContext.toString() 
                        + ": " + e.getMessage());
                logger.debug(stringBuffer);
                solrRollback();
                error();
            }
        }
    }
    
    private void delete(String networkAcronym) {
        // Borrado de la red
        try {
            this.sendUpdateToSolr(
                    "<delete><query>" + this.solrNetworkIDField + ":" + networkAcronym + "</query></delete>");
        } catch (SolrServerException | IOException | HttpSolrClient.RemoteSolrException e) {
            logError("Issues when deleting index: " + runningContext.toString() + ": " + e.getMessage());
            error();
        }
    }

    private void sendUpdateToSolr(String data)
            throws SolrServerException, IOException, HttpSolrClient.RemoteSolrException {
        DirectXmlRequest request = new DirectXmlRequest("/update", data);
        solrClient.request(request);
    }

    private void error() {
        this.stop();
    }

    private void logError(String message) {
        logger.error(message);
    }

    private void logInfo(String message) {
        logger.info(message);
    }

    private void solrRollback() {
        try {
            this.sendUpdateToSolr("<rollback/>");
        } catch (SolrServerException | IOException | HttpSolrClient.RemoteSolrException e) {
            logError("Issues with rollback  " + runningContext.toString() + ": " + e.getMessage());
            error();
        }
    }
}
