package com.firstlinesoftware.rmrs.server.providers;

import com.firstlinesoftware.base.shared.dto.SearchCriteria;
import com.firstlinesoftware.base.shared.utils.CriteriaBuilder;
import com.firstlinesoftware.ecm.shared.dto.Document;
import com.firstlinesoftware.rmrs.server.RmrsAlfrescoTypes;
import com.firstlinesoftware.rmrs.shared.dto.RmrsTasks;
import com.firstlinesoftware.route.server.providers.impl.AbstractRouteTaskProvider;
import com.firstlinesoftware.route.shared.dto.AbstractRoute;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Component
public class CircularLetterTaskProvider extends AbstractRouteTaskProvider {
    public static final Map<String, String> lifecycle = new HashMap<>();
    static {
        lifecycle.put(RmrsTasks.CIRCULAR_LETTER_DRAFTS, Document.DOCUMENT_LIFECYCLE_DRAFT);
        lifecycle.put(RmrsTasks.CIRCULAR_LETTER_REJECTED, AbstractRoute.LIFECYCLE_REJECTED);
        lifecycle.put(RmrsTasks.CIRCULAR_LETTER_ON_APPROVAL, AbstractRoute.LIFECYCLE_ONAPPROVAL);
        lifecycle.put(RmrsTasks.CIRCULAR_LETTER_SENT_TO_APPROVAL, AbstractRoute.LIFECYCLE_ONAPPROVAL);
        lifecycle.put(RmrsTasks.CIRCULAR_LETTER_ON_SIGNING, AbstractRoute.LIFECYCLE_ONSIGNING);
        lifecycle.put(RmrsTasks.CIRCULAR_LETTER_SIGNED, Document.DOCUMENT_LIFECYCLE_ARCHIVED);
    }

    @PostConstruct
    private void postConstruct() {
        taskProviderFactory.register(RmrsTasks.CIRCULAR_LETTER_TASK_TYPE, RmrsAlfrescoTypes.TYPE_CIRCULAR_LETTER, this);
    }

    @Override
    public Integer getTasksCount(String positionId, String folderId) {
        final CriteriaBuilder<SearchCriteria> builder = new CriteriaBuilder<>()
                .setType("rmrs:circular")
                .addMustHave("ecm:lifecycle", lifecycle.get(folderId))
                .addMustHave(RmrsTasks.CIRCULAR_LETTER_SIGNED.equals(folderId) ? "ecm:tags" : "ecm:documentAuthor", positionId);
        final int count = repositoryService.queryCount(builder.build());
        return count > 0 ? count : null;
    }
}
