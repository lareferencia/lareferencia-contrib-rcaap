
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

package org.lareferencia.contrib.rcaap.entity.indexing.solr;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.request.DirectXmlRequest;
import org.lareferencia.contrib.rcaap.entity.indexing.solr.model.Field;
import org.lareferencia.contrib.rcaap.entity.indexing.solr.model.Fields;
import org.lareferencia.contrib.rcaap.entity.indexing.solr.model.ObjectFactory;
import org.lareferencia.contrib.rcaap.entity.indexing.solr.model.Relationship;
import org.lareferencia.contrib.rcaap.entity.indexing.solr.model.Relationships;
import org.lareferencia.contrib.rcaap.entity.indexing.solr.model.Resource;
import org.lareferencia.core.entity.domain.Entity;
import org.lareferencia.core.entity.domain.EntityRelationException;
import org.lareferencia.core.entity.domain.EntityType;
import org.lareferencia.core.entity.domain.FieldOccurrence;
import org.lareferencia.core.entity.domain.Relation;
import org.lareferencia.core.entity.domain.RelationType;
import org.lareferencia.core.entity.indexing.service.EntityIndexingException;
import org.lareferencia.core.entity.services.EntityDataService;
import org.lareferencia.core.metadata.IMDFormatTransformer;
import org.lareferencia.core.metadata.MDFormatTranformationException;
import org.lareferencia.core.metadata.MDFormatTransformerService;
import org.lareferencia.core.metadata.MedatadaDOMHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;
import lombok.Getter;
import lombok.Setter;

/**
 * This simple entity indexer will be responsible for transforming entities into
 * solr documents and index those. It doesn't use configuration just one xsl
 * transformer
 * 
 * @author pgraca
 *
 */
public class SimpleSolrEntityIndexer implements IEntityRCAAPIndexer {

    private static Logger logger = LogManager.getLogger(SimpleSolrEntityIndexer.class);

    @Autowired
    private MDFormatTransformerService trfService;

    @Autowired
    EntityDataService entityDataService;

    private IMDFormatTransformer metadataTransformer;

    @Getter
    @Setter
    private boolean debugMode = false;

    @Getter
    @Setter
    private String sourceSchemaName = "entitiy_rcaap";

    @Getter
    @Setter
    private String targetSchemaName = "entity_index";

    private HttpSolrClient solrClient;

    private String solrRecordIDField = "id";

    @Getter
    @Setter
    String solrURL = "http://localhost:8983/solr/entity";

    List<Document> documentsBuffer = new LinkedList<>();
    
    private Marshaller jaxbMarshaller;
    DocumentBuilder dbFactory;
    
    @Override
    public void setConfig() {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(false);
            dbFactory = dbf.newDocumentBuilder();

            JAXBContext context = JAXBContext.newInstance(Resource.class);
            jaxbMarshaller = context.createMarshaller();
        } catch (JAXBException | ParserConfigurationException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        this.solrClient = new HttpSolrClient.Builder(solrURL).build();

        // Load XSL file
        try {
            metadataTransformer = trfService.getMDTransformer(sourceSchemaName, targetSchemaName);
        } catch (MDFormatTranformationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void index(Entity entity) throws EntityIndexingException {

        try {
            String id = entity.getId().toString();
            // Get the type from a cache mechanism
            EntityType type = entityDataService.getEntityTypeFromId(entity.getEntityTypeId());
                        

            Map<String, Collection<FieldOccurrence>> fieldsMap = entity.getOccurrencesAsMap();
            
            // Convert raw json to xml
            // we need to remove @class
            // String xml = normalizeXML(XML.toString(json));
            ObjectFactory obj = new ObjectFactory();
            Resource resource = obj.createResource();

            resource.setId(id);
            resource.setType(type.getName());

            Fields fields = obj.createFields();
            
            for (Map.Entry<String, Collection<FieldOccurrence>> occurs : fieldsMap.entrySet()) {

                for (FieldOccurrence occur : occurs.getValue()) {
                    Field field = obj.createField();

                    field.setName(occur.getFieldName());
                    field.setLang(occur.getLang());

                    field.getContent().add(occur.getContent());
                    fields.getField().add(field);
                }
            }
            resource.setFields(fields);

            Relationships rels = obj.createRelationships();
            for (Relation relation : entity.getFromRelations()) {
                RelationType relType = entityDataService.getRelationTypeFromId(relation.getRelationTypeId());
                

                Relationship rel = obj.createRelationship();
                Fields relFields = obj.createFields();

                Map<String, Collection<FieldOccurrence>> relationsAttrsMap = relation.getOccurrencesAsMap();

                for (Map.Entry<String, Collection<FieldOccurrence>> occurs : relationsAttrsMap.entrySet()) {
                    for (FieldOccurrence occur : occurs.getValue()) {
                        Field field = obj.createField();

                        field.setName(occur.getFieldName());
                        field.getContent().add(occur.getContent());

                        relFields.getField().add(field);
                    }
                }
                rel.setFields(relFields);
                rel.setId(relation.getToEntity().getId().toString());
                rel.setName(relType.getName());
                
                rels.getRelationship().add(rel);
            }
            resource.setRelationships(rels);

            // transformed attributes to entities
            try {
                Document doc = marshal(resource);
                if (debugMode) {
                    // logger.debug(MedatadaDOMHelper.Node2XMLString(doc));
                    logger.info(MedatadaDOMHelper.Node2XMLString(doc));
                }
                documentsBuffer.add(doc);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        } catch (EntityRelationException e) {
            throw new EntityIndexingException("Indexing error. " + e.getMessage());
        }
    }

    @Override
    public void delete(String entityId) throws EntityIndexingException {
        // Delete entity record
        try {
            this.sendUpdateToSolr("<delete><query>" + solrRecordIDField + ":" + entityId + "</query></delete>");

        } catch (EntityIndexingException e) {
            throw new EntityIndexingException("Index delete entity error (id:" + entityId + ") msg: " + e.getMessage());
        }
    }

    @Override
    public void deleteAll(Collection<String> idList) throws EntityIndexingException {
        List<String> queries = idList.stream().map(id -> solrRecordIDField + ":" + id).collect(Collectors.toList());
        // Delete entity record
        try {
            this.sendUpdateToSolr("<delete><query>" + String.join(" AND ", queries) + "</query></delete>");

        } catch (EntityIndexingException e) {
            throw new EntityIndexingException(
                    "Index delete entity error (ids:" + String.join(",", idList) + ") msg: " + e.getMessage());
        }

    }

    @Override
    public void flush() {
        StringBuffer stringBuffer = new StringBuffer();

        for (Document doc : documentsBuffer) {
            try {
                String transformedXML = metadataTransformer.transformToString(doc);
                stringBuffer.append(transformedXML);
            } catch (MDFormatTranformationException e) {
                e.printStackTrace();
            }
        }

        try {
            this.sendUpdateToSolr("<add>" + stringBuffer.toString() + "</add>");
            if (debugMode) {
                logger.info("<add>" + stringBuffer.toString() + "</add>");
            }
            
        } catch (EntityIndexingException e) {
            e.printStackTrace();
        } finally {
            try {
                this.sendUpdateToSolr("<commit/>");
            } catch (EntityIndexingException e) {
                try {
                    this.sendUpdateToSolr("<rollback/>");
                } catch (EntityIndexingException e1) {
                    e1.printStackTrace();
                }
            }
        }

    }

    private void sendUpdateToSolr(String data) throws EntityIndexingException {
        DirectXmlRequest request = new DirectXmlRequest("/update", data);
        if (debugMode) {
            logger.info(data);
            // logger.debug(data);
        }
        try {
            solrClient.request(request);
        } catch (Exception e) {
            throw new EntityIndexingException("Failed sending Solr instruction: " + e.getMessage());
        }
    }
    
   

    private Document marshal(Resource resource)
            throws JAXBException, TransformerException {
        Document doc = dbFactory.newDocument();
        jaxbMarshaller.marshal(resource, doc);

        return doc;
    }

    @Override
    public void prePage() throws EntityIndexingException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'prePage'");
    }

}
