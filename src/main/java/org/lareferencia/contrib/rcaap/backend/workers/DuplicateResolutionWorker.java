package org.lareferencia.contrib.rcaap.backend.workers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lareferencia.backend.domain.Network;
import org.lareferencia.backend.domain.SnapshotIndexStatus;
import org.lareferencia.backend.services.SnapshotLogService;
import org.lareferencia.contrib.rcaap.search.merge.EntityMergeConfig;
import org.lareferencia.contrib.rcaap.search.merge.EntityType;
import org.lareferencia.contrib.rcaap.search.merge.IMergeStrategy;
import org.lareferencia.contrib.rcaap.search.merge.IMergeStrategyRegistry;
import org.lareferencia.contrib.rcaap.search.merge.MergeConfigurations;
import org.lareferencia.contrib.rcaap.search.merge.MergeExecutor;
import org.lareferencia.contrib.rcaap.search.merge.MergeResult;
import org.lareferencia.contrib.rcaap.search.server.ISearchEngineClientResolver;
import org.lareferencia.contrib.rcaap.search.server.model.AbstractBaseEntity;
import org.lareferencia.contrib.rcaap.search.server.model.BaseEntityType;
import org.lareferencia.contrib.rcaap.search.services.IFacet;
import org.lareferencia.contrib.rcaap.search.services.IFacetValue;
import org.lareferencia.contrib.rcaap.search.services.SearchService;
import org.lareferencia.core.metadata.IMetadataRecordStoreService;
import org.lareferencia.core.worker.BaseWorker;
import org.lareferencia.core.worker.NetworkRunningContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import lombok.Getter;
import lombok.Setter;

public class DuplicateResolutionWorker extends BaseWorker<NetworkRunningContext> {
    private static Logger logger = LogManager.getLogger(DuplicateResolutionWorker.class);

    private static final int MERGE_PAGING_ROWS = 1000;

    @Getter
    @Setter
    private Class<?> type;

    @Autowired
    private SnapshotLogService snapshotLogService;

    @Autowired
    private IMetadataRecordStoreService metadataStoreService;

    private Long snapshotId;

    
    private boolean wasStopped = false;

    @Getter
    @Setter
    protected String name = "DuplicateResolutionWorker";

    @Getter
    @Setter
    private boolean debugMode = true;

    /**
     * Only process the network that its in the context
     */
    @Getter
    @Setter
    private boolean networkOnly = true;

    private final IMergeStrategyRegistry mergeRegistry;
    private final MergeConfigurations mergeConfig;
    private EntityMergeConfig entityMergeConfig;
    private final ISearchEngineClientResolver searchEngineClientResolver;
    
    private EntityType entityType;
    private SearchService searchService;

    public DuplicateResolutionWorker(
            IMergeStrategyRegistry mergeRegistry,
            MergeConfigurations mergeConfig,
            ISearchEngineClientResolver searchEngineClientResolver) {
        
        this.mergeRegistry = mergeRegistry;
        this.mergeConfig = mergeConfig;
        this.searchEngineClientResolver = searchEngineClientResolver;

    }

    @Override
    public void run() {
        Network runningNetwork = runningContext.getNetwork();

        String networkAcronym;
        if (networkOnly) {
            networkAcronym = runningNetwork.getAcronym(); 
        } else {
            networkAcronym = null;
        }

        this.preRun();

        for (String field : entityMergeConfig.getFields()) {
            if ( wasStopped ) break;
            try {
                //TODO: currently we are limiting to 10000 facets - we need to review this
                Optional<IFacet> duplicates = searchService.findAllDuplicatesFacetsByNetwork(field, networkAcronym);
                if (duplicates.isPresent()) {
                    processMerge(field, networkAcronym, duplicates.get().getFacetValues());

                } else {
                    if (debugMode) {
                        logger.info("No duplicate records to process");
                    }
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
        logInfo("MERGING: "+ runningContext.toString() + "(type:" + this.entityType.toString() + ")");

    }

    private void postRun () {
        if ( wasStopped ) {
            logInfo("END MERGING: STOPPED "+ runningContext.toString() + "(type:" + this.entityType.toString() + ")");
        } else {
            logInfo("END MERGING: "+ runningContext.toString() + "(type:" + this.entityType.toString() + ")");
        }
    }

    @SuppressWarnings("unchecked")
    private <T extends AbstractBaseEntity> void processMerge(
            String field,
            String networkAcronym,
            Page<? extends IFacetValue> facetsValues) {

        Class<T> clazz = (Class<T>) type;

        IMergeStrategy<T> strategy =
                mergeRegistry.getStrategy(entityMergeConfig.getStrategy(), clazz);

        MergeExecutor<T> executor = new MergeExecutor<>(
            clazz,
            strategy,
            searchService
        );

        try {

            // buffer with SINGLE_PAGING_ROWS size
            List<AbstractBaseEntity> buffer = new ArrayList<>(MERGE_PAGING_ROWS);


            List<? extends IFacetValue> facets = facetsValues.getContent();

            int i = 0;
            int pageNumber = (i / MERGE_PAGING_ROWS);
            int pages = (facets.size() / MERGE_PAGING_ROWS) + 1;

            for (IFacetValue facetValue : facetsValues) {

                if (i % MERGE_PAGING_ROWS == 0) {
                    pageNumber++;
                    logInfo("Processing page " + pageNumber + " of " + pages );
                }

                String identifier = facetValue.getValue();

                try {
                    if (debugMode) {
                        logger.info("about to merge by facet: " + field + ":" + identifier);
                    }

                    List<T> docs = executor.process(field, identifier);
                    buffer.addAll(docs);
                } catch (Exception e) {
                    logError("Couldn't process doc on page: " + pageNumber);
                }

                if (buffer.size() >= MERGE_PAGING_ROWS) {
                    try {
                        Page<AbstractBaseEntity> bufferPage =
                                new PageImpl<>(new ArrayList<>(buffer)); // create a page
                        if (debugMode) {
                            logInfo("Persisting " + buffer.size() + " records");
                        }

                        executor.persist(bufferPage);
                        buffer.clear();
                    } catch (Exception e) {
                        logError("Couldn't persist batch of "+ MERGE_PAGING_ROWS +" docs");
                    }
                }

                i++;

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

