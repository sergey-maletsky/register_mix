package com.firstlinesoftware.rmrs.server.providers;

import com.firstlinesoftware.base.server.services.RepositoryService;
import com.firstlinesoftware.base.shared.dto.SearchCriteria;
import com.firstlinesoftware.base.shared.utils.CriteriaBuilder;
import com.firstlinesoftware.ecm.shared.dto.Document;
import com.firstlinesoftware.ecm.shared.dto.Task;
import com.firstlinesoftware.rmrs.server.RmrsAlfrescoTypes;
import com.firstlinesoftware.rmrs.shared.dto.Requirement;
import com.firstlinesoftware.rmrs.shared.dto.RmrsTasks;
import com.firstlinesoftware.route.server.providers.impl.AbstractRouteTaskProvider;
import com.firstlinesoftware.route.shared.dto.AbstractRoute;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Component
public class RequirementTaskProvider extends AbstractRouteTaskProvider {
    public static final Map<String, String> lifecycle = new HashMap<>();
    static {
        lifecycle.put(RmrsTasks.REQ_DRAFTS, Document.DOCUMENT_LIFECYCLE_DRAFT);
        lifecycle.put(RmrsTasks.REQ_ON_APPROVAL, AbstractRoute.LIFECYCLE_ONAPPROVAL);
        lifecycle.put(RmrsTasks.REQ_SENT_TO_APPROVAL, AbstractRoute.LIFECYCLE_ONAPPROVAL);
        lifecycle.put(RmrsTasks.REQ_APPROVED, AbstractRoute.LIFECYCLE_APPROVED);
        lifecycle.put(RmrsTasks.REQ_ON_SIGNING, AbstractRoute.LIFECYCLE_ONSIGNING);
        lifecycle.put(RmrsTasks.REQ_SIGNED, Requirement.LIFECYCLE_SIGNED);
    }

    @PostConstruct
    private void postConstruct() {
        taskProviderFactory.register(RmrsTasks.REQ_TASK_TYPE, RmrsAlfrescoTypes.TYPE_REQUIREMENT, this);
    }

    @Override
    protected void fillTask(Task task, RepositoryService.Node node) {
        super.fillTask(task, node);
        task.type = RmrsTasks.REQ_TASK_TYPE;
    }

    @Override
    public Integer getTasksCount(String positionId, String folderId) {
        final CriteriaBuilder<SearchCriteria> builder = new CriteriaBuilder<>()
                .setType("rmrs:requirement")
                .addMustHave("ecm:lifecycle", lifecycle.get(folderId))
                .addMustHave(RmrsTasks.REQ_SENT_TO_APPROVAL.equals(folderId) ? "ecm:documentAuthor" : "rmrs:responsible", positionId);
        final int count = repositoryService.queryCount(builder.build());
        return count > 0 ? count : null;
    }
}
