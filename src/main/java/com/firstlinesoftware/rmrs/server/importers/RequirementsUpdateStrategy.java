package com.firstlinesoftware.rmrs.server.importers;

import com.firstlinesoftware.base.server.BaseAlfrescoTypes;
import com.firstlinesoftware.base.server.importers.AbstractExternalAspectStrategy;
import com.firstlinesoftware.base.server.importers.ImportStrategy;
import com.firstlinesoftware.base.server.services.RepositoryService;
import com.firstlinesoftware.base.server.utils.Batcher;
import com.firstlinesoftware.base.server.utils.Messages;
import com.firstlinesoftware.ecm.server.providers.impl.CompositeDocumentProvider;
import com.firstlinesoftware.ecm.server.services.DocumentService;
import com.firstlinesoftware.ecm.server.services.FolderService;
import com.firstlinesoftware.orgstruct.server.services.OrgstructService;
import com.firstlinesoftware.orgstruct.shared.dto.Position;
import com.firstlinesoftware.rmrs.server.RmrsAlfrescoTypes;
import com.firstlinesoftware.rmrs.server.providers.RequirementsFolderProvider;
import com.firstlinesoftware.rmrs.shared.dto.Requirement;
import com.firstlinesoftware.rmrs.shared.dto.Requirements;
import com.google.common.base.Function;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class RequirementsUpdateStrategy implements ImportStrategy<Requirements> {

    private final Logger logger = Logger.getLogger(this.getClass());
    private final AtomicInteger count = new AtomicInteger();
    private final Map<String, Requirement> requirementsByExternalId = new ConcurrentHashMap<>();
    @Autowired
    private DocumentService documentService;
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private FolderService folderService;
    @Autowired
    private Batcher batcher;
    @Autowired
    private Messages messages;
    @Autowired
    private OrgstructService orgstructService;
    private String rootFolderId = RequirementsFolderProvider.FOLDER;
    private Position author;

    @Override
    public void startImport() {
    }

    @Override
    public synchronized void importItem(final Requirements requirements) {
        if (!requirements.getRequirements().isEmpty()) {
            final Position admin = orgstructService.getSystemAdministrator();
            count.set(0);
            requirementsByExternalId.clear();
            Logger.getLogger(getClass()).info("start importing requirements");
            final long started = System.currentTimeMillis();

            final Set<String> parents = new HashSet<>();
            for (Requirement requirement : requirements.getRequirements()) {
                if (requirement.parent != null) {
                    assert requirement.parent.externalId != null;
                    parents.add(requirement.parent.externalId);
                }
            }

            final List<Requirement> newHeaders = new ArrayList<>();
            final Multimap<String, Requirement> newLeafsByParent = HashMultimap.create();
            final List<Requirement> update = new ArrayList<>();
            repositoryService.doInTransaction(true, new RepositoryService.RetryingTransactionCallback<Void>() {
                @Override
                public Void execute() throws Exception {
                    author = orgstructService.getSystemAdministrator();
                    for (final Requirement requirement : requirements.getRequirements()) {
                        if (requirement != null) {
                            try {
                                Requirement existingReq = getByExternalId(requirement.externalId);
                                if (existingReq == null) {
                                    requirement.responsible = admin;
                                    if (parents.contains(requirement.externalId)) {
                                        newHeaders.add(requirement);
                                    } else {
                                        assert requirement.parent != null && requirement.parent.externalId != null;
                                        newLeafsByParent.put(requirement.parent.externalId, requirement);
                                    }
                                } else if (AbstractExternalAspectStrategy.isNewer(requirement, existingReq)) {
                                    requirement.setId(existingReq.getId());
                                    if (requirement.parent != null) {
                                        requirement.parent = getByExternalId(requirement.parent.externalId);
                                        requirement.responsible = existingReq.responsible;
                                        if (requirement.responsible == null) {
                                            requirement.responsible = admin;
                                        }
                                    }
                                    update.add(requirement);
                                }
                            } catch (RuntimeException ex) {
                                logger.error("Requirement import error", ex);
                            }
                        }
                    }
                    return null;
                }
            });
            updateRequirements(update);
            createHeaders(newHeaders);
            createLeafs(newLeafsByParent);

            Logger.getLogger(getClass()).info("finish importing  " + count.get() + " requirements in " + (System.currentTimeMillis() - started) / 1000.0 + " sec");
        }
    }

    @Override
    public void stopImport() {
    }

    private void updateRequirements(final List<Requirement> requirements) {
        Logger.getLogger(getClass()).info("start updating...");
        final Batcher.Task<List<Requirement>> task = batcher.startParallel(null, 100, Integer.MAX_VALUE, new Function<List<Requirement>, Void>() {
            @Nullable
            @Override
            public Void apply(@Nullable List<Requirement> batch) {
                CompositeDocumentProvider.storeLocalFiles.set(true);
                try {
                    assert batch != null;
                    documentService.update(batch, null);
                    count.addAndGet(batch.size());
                } finally {
                    CompositeDocumentProvider.storeLocalFiles.set(false);
                }
                return null;
            }
        });
        for (List<Requirement> t : Lists.partition(requirements, 100)) {
            task.addToBatch(t);
        }
        task.finish();
    }

    private void createHeaders(List<Requirement> batch) {
        Logger.getLogger(getClass()).info("start creating headers...");
        final Batcher.Task<Requirement> task = batcher.startSingleThreaded(null, 100, new Function<Requirement, Void>() {
            @Nullable
            @Override
            public Void apply(Requirement requirement) {
                CompositeDocumentProvider.storeLocalFiles.set(true);
                try {
                    requirement.setId(createRequirement(requirement));
                    count.incrementAndGet();
                } finally {
                    CompositeDocumentProvider.storeLocalFiles.set(false);
                }
                return null;
            }
        });
        for (Requirement requirement : batch) {
            task.addToBatch(requirement);
        }
        task.finish();
    }

    private void createLeafs(final Multimap<String, Requirement> leafs) {
        Logger.getLogger(getClass()).info("start creating leafs...");
        final Batcher.Task<Map.Entry<String, Collection<Requirement>>> task = batcher.startSingleThreaded(null, 100, new Function<Map.Entry<String, Collection<Requirement>>, Void>() {
            @Nullable
            @Override
            public Void apply(Map.Entry<String, Collection<Requirement>> batch) {
                CompositeDocumentProvider.storeLocalFiles.set(true);
                try {
                    final Requirement parent = getByExternalId(batch.getKey());
                    assert parent != null;
                    final List<Requirement> leafsInParent = Lists.newArrayList(batch.getValue());
                    for (Requirement requirement : leafsInParent) {
                        requirement.setAuthor(author);
                        requirement.lifecycle = Requirement.LIFECYCLE_SIGNED;
                        requirement.parent.id = parent.id;
                        requirement.parent.fullPath = parent.fullPath;
                    }
                    documentService.create(parent.id, BaseAlfrescoTypes.ASSOC_CONTAINS, leafsInParent, null);
                } finally {
                    CompositeDocumentProvider.storeLocalFiles.set(false);
                }
                return null;
            }
        });
        for (Map.Entry<String, Collection<Requirement>> entry : leafs.asMap().entrySet()) {
            task.addToBatch(entry);
        }
        task.finish();
    }

    private String createRequirement(final Requirement requirement) {
        requirement.setAuthor(author);
        requirement.lifecycle = Requirement.LIFECYCLE_SIGNED;
        if (requirement.parent != null && requirement.parent.externalId != null) {
            Requirement parent = getByExternalId(requirement.parent.externalId);
            if (parent != null) {
                requirement.parent.id = parent.id;
                requirement.parent.fullPath = parent.fullPath;
                return documentService.create(parent.id, requirement);
            }
            return null;
        } else {
            return documentService.create(rootFolderId, requirement);
        }
    }

    private Requirement getByExternalId(String externalId) {
        if (externalId != null) {
            final Requirement requirement = requirementsByExternalId.get(externalId);
            if (requirement != null) {
                return requirement;
            }
            final List<String> existingIds = repositoryService.search(RmrsAlfrescoTypes.TYPE_REQUIREMENT, BaseAlfrescoTypes.PROP_EXTERNAL_ID, externalId);
            if (existingIds != null && existingIds.size() > 0) {
                final Requirement result = documentService.get(existingIds.get(0));
                requirementsByExternalId.put(externalId, result);
                return result;
            }
        }
        return null;
    }
}
