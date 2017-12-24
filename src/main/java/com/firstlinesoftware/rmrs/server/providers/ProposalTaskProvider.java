package com.firstlinesoftware.rmrs.server.providers;

import com.firstlinesoftware.base.server.services.RepositoryService;
import com.firstlinesoftware.base.shared.dto.SearchCriteria;
import com.firstlinesoftware.base.shared.utils.CriteriaBuilder;
import com.firstlinesoftware.ecm.shared.dto.Document;
import com.firstlinesoftware.rmrs.server.RmrsAlfrescoTypes;
import com.firstlinesoftware.rmrs.shared.dto.RmrsTasks;
import com.firstlinesoftware.route.server.providers.impl.AbstractRouteTaskProvider;
import com.firstlinesoftware.route.shared.dto.AbstractRoute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Component
public class ProposalTaskProvider extends AbstractRouteTaskProvider {
    public static final Map<String, String> lifecycle = new HashMap<>();
    static {
        lifecycle.put(RmrsTasks.PROPOSAL_DRAFTS, Document.DOCUMENT_LIFECYCLE_DRAFT);
        lifecycle.put(RmrsTasks.PROPOSAL_ON_EXECUTION, AbstractRoute.LIFECYCLE_ONEXECUTION);
        lifecycle.put(RmrsTasks.PROPOSAL_ARCHIVED, Document.DOCUMENT_LIFECYCLE_ARCHIVED);
    }

    @Autowired
    RepositoryService repositoryService;

    @PostConstruct
    private void postConstruct() {
        taskProviderFactory.register(RmrsTasks.PROPOSAL_TASK_TYPE, RmrsAlfrescoTypes.TYPE_PROPOSAL, this);
    }

    @Override
    public Integer getTasksCount(String positionId, String folderId) {
        final CriteriaBuilder<SearchCriteria> builder = new CriteriaBuilder<>()
                .setType("rmrs:proposal")
                .addMustHave("ecm:lifecycle", lifecycle.get(folderId))
                .addMustHave("ecm:documentAuthor", positionId);
        final int count = repositoryService.queryCount(builder.build());
        return count > 0 ? count : null;
    }
}
