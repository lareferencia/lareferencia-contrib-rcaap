package org.lareferencia.contrib.rcaap.backend.workers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lareferencia.backend.domain.Network;
import org.lareferencia.backend.domain.OAIRecord;
import org.lareferencia.backend.domain.SnapshotIndexStatus;
import org.lareferencia.backend.services.SnapshotLogService;
import org.lareferencia.contrib.rcaap.search.merge.EntityMergeConfig;
import org.lareferencia.contrib.rcaap.search.merge.EntityType;
import org.lareferencia.contrib.rcaap.search.merge.MergeConfigurations;
import org.lareferencia.contrib.rcaap.search.merge.SingleExecutor;
import org.lareferencia.contrib.rcaap.search.server.ISearchEngineClientResolver;
import org.lareferencia.contrib.rcaap.search.server.model.AbstractBaseEntity;
import org.lareferencia.contrib.rcaap.search.server.model.BaseEntityType;
import org.lareferencia.contrib.rcaap.search.services.IFacet;
import org.lareferencia.contrib.rcaap.search.services.IFacetValue;
import org.lareferencia.contrib.rcaap.search.services.SearchService;
import org.lareferencia.core.metadata.IMetadataRecordStoreService;
import org.lareferencia.core.worker.BaseBatchWorker;
import org.lareferencia.core.worker.BaseWorker;
import org.lareferencia.core.worker.NetworkRunningContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import lombok.Getter;
import lombok.Setter;

public class SingletonResolutionWorker extends BaseWorker<NetworkRunningContext> {
    private static Logger logger = LogManager.getLogger(SingletonResolutionWorker.class);
    private static final int SINGLE_PAGING_ROWS = 1000;
    private static final int SINGLE_FACETS_BATCH_NUMBER = 10;

    @Getter
    @Setter
    private Class<?> type;

    @Autowired
    private SnapshotLogService snapshotLogService;

    @Autowired
    private IMetadataRecordStoreService metadataStoreService;

    private Long snapshotId;

    @Getter
    @Setter
    protected String name = "SingletonResolutionWorker";

    private boolean wasStopped = false;

    @Getter
    @Setter
    private boolean debugMode = true;

    private final MergeConfigurations mergeConfig;
    private EntityMergeConfig entityMergeConfig;
    private final ISearchEngineClientResolver searchEngineClientResolver;
    
    private EntityType entityType;
    private SearchService searchService;

    public SingletonResolutionWorker(
            MergeConfigurations mergeConfig,
            ISearchEngineClientResolver searchEngineClientResolver) {
        
        this.mergeConfig = mergeConfig;
        this.searchEngineClientResolver = searchEngineClientResolver;

    }

    @Override
    public void run() {
        Network runningNetwork = runningContext.getNetwork();

        String networkAcronym = runningNetwork.getAcronym();

        this.preRun();

        for (String field : entityMergeConfig.getFields()) {
            if ( wasStopped ) break;
            try {

                // Now process all single items with the same field
                // we will use negative filtering, it's much faster 
                Optional<IFacet> facets = searchService.findSinglesFacetsByNetwork(field, networkAcronym);
                if (facets.isPresent()) {
                    processSingles(field, networkAcronym, facets.get().getFacetValues());
                } else {

                    if (debugMode) {
                        logger.info("No unique records to process");
                    }

                    

/*                    
                    int start=0;
                    while (true) {
                        List<String> identifiers = new ArrayList<>(SINGLE_FACETS_BATCH_NUMBER);
                        Long countFacets = facets.get().getFacetValues()..getTotalElements();
                        
                        
                        if (countFacets == 0L) {
                            break;
                        }
                        processSingles(field, networkAcronym, identifiers);
                    }
                    
                    
                    List<String> identifiers = new ArrayList<>(SINGLE_FACETS_BATCH_NUMBER);
                    Long countFacets = singles.get().getFacetValues().getTotalElements();
                    if (countFacets > 0L) {
                        for (IFacetValue entry : singles.get().getFacetValues()) {
                            String identifier = entry.getValue();

                            //Process limited rows number
                            identifiers.add(identifier);

                            if (identifiers.size() == SINGLE_FACETS_BATCH_NUMBER) {
                                if (debugMode) {
                                    logger.info("about to update singles by facet: " + field + ":" + String.join(", ", identifiers));
                                }
                                processSingles(field, networkAcronym, identifiers);
                                identifiers.clear();       // clear for next batch
                            }

                        }

                        if (debugMode) {
                            logger.info("about to update singles by facet: " + field + ":" + String.join(", ", identifiers));
                        }
                        if (!identifiers.isEmpty()) {
                            processSingles(field, identifiers);
                        }
                    }*/
                }

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
        this.postRun();
    }

    private void preRun () {

        // busca el lgk
        snapshotId = metadataStoreService.findLastGoodKnownSnapshot(runningContext.getNetwork());

        if (snapshotId == null) {
            logError("No snapshot found for the network: " + runningContext.toString());
            error();
        }

        this.entityType = type.getAnnotation(BaseEntityType.class).type();
        this.searchService = new SearchService(this.searchEngineClientResolver,this.entityType);
        this.entityMergeConfig = findConfig(this.entityType); 
        logInfo("SINGLE: "+ runningContext.toString() + "(type:" + this.entityType.toString() + ")");

    }

    private void postRun () {
        if ( wasStopped ) {
            logInfo("END SINGLE: STOPPED "+ runningContext.toString() + "(type:" + this.entityType.toString() + ")");
        } else {
            logInfo("END SINGLE: "+ runningContext.toString() + "(type:" + this.entityType.toString() + ")");
        }
    }


    @SuppressWarnings("unchecked")
    private <T extends AbstractBaseEntity> void processSingles(
            String field, 
            String networkAcronym,
            Page<? extends IFacetValue> facets) {

        Class<T> clazz = (Class<T>) type;

        SingleExecutor<T> executor = new SingleExecutor<>(searchService);

        try {
            // buffer with SINGLE_PAGING_ROWS size
            List<AbstractBaseEntity> buffer = new ArrayList<>(SINGLE_PAGING_ROWS);

            List<? extends IFacetValue> all = facets.getContent();

            int pages = (all.size() / SINGLE_FACETS_BATCH_NUMBER) + 1;

            for (int i = 0; i < all.size(); i += SINGLE_FACETS_BATCH_NUMBER) {

                int end = Math.min(i + SINGLE_FACETS_BATCH_NUMBER, all.size());
                List<? extends IFacetValue> batch = all.subList(i, end);

                List<String> identifiers = batch.stream()
                        .map(IFacetValue::getValue)
                        .collect(Collectors.toList());

                if (debugMode) {
                    logger.info("about to update singles by facet: " + field + ":" + String.join(", ", identifiers));
                }

                int pageNumber = (i / SINGLE_FACETS_BATCH_NUMBER) + 1;
                logInfo("Processing page " + pageNumber + " of " + pages );

                int start = 0;

                while (true) {

                    Page<? extends AbstractBaseEntity> docs =
                            searchService.findSingleDocumentsByFacets(
                                    field,
                                    networkAcronym,
                                    identifiers,
                                    clazz,
                                    start,
                                    SINGLE_PAGING_ROWS
                            );

                    for (AbstractBaseEntity doc : docs.getContent()) {
                        try {
                            executor.process(doc);
                            buffer.add(doc);
                        } catch (Exception e) {
                            logError("Couldn't process doc on page: " + docs.getNumber());
                        }
                    }

                    if (buffer.size() >= SINGLE_PAGING_ROWS) {
                        try {
                            Page<AbstractBaseEntity> bufferPage =
                                    new PageImpl<>(new ArrayList<>(buffer)); // create a page
                            if (debugMode) {
                                logInfo("Persisting " + docs.getNumberOfElements() + " records");
                            }

                            executor.persist(bufferPage);
                            buffer.clear();
                        } catch (Exception e) {
                            logError("Couldn't persist batch of "+ SINGLE_PAGING_ROWS +" docs");
                        }
                    }

                    if (!docs.hasNext()) {
                        break;
                    }

                    start += SINGLE_PAGING_ROWS;
                }
            }

         // if the buffer isn't empty
            if (!buffer.isEmpty()) {
                Page<AbstractBaseEntity> finalPage =
                        new PageImpl<>(new ArrayList<>(buffer));
                if (debugMode) {
                    logInfo("Persisting " + finalPage.getNumberOfElements() + " records");
                }
                executor.persist(finalPage);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private EntityMergeConfig findConfig(EntityType type) {

        return mergeConfig.getMergeConfigs().stream()
                .filter(e -> e.getType() == type)
                .findFirst()
                .orElseThrow();
    }

    private void logError(String message) {
        logger.error(message);
        if (snapshotId != null) {
            snapshotLogService.addEntry(snapshotId, "ERROR: " + message);
        }
    }

    private void logInfo(String message) {
        logger.info(message);
        if (snapshotId != null) {
            snapshotLogService.addEntry(snapshotId, "INFO: " + message);
        }
    }

    private void error() {
        if ( snapshotId != null) {
            metadataStoreService.updateSnapshotIndexStatus(snapshotId, SnapshotIndexStatus.FAILED);
            metadataStoreService.saveSnapshot(snapshotId);
        }
        this.stop();
    }

    @Override
    public void stop() {
        wasStopped = true;
        super.stop();
    }

    @Override
    public String toString() {
        return this.getName() + " ["+ type.getAnnotation(BaseEntityType.class).type() +"]";
    }
}

