package com.firstlinesoftware.rmrs.server.interceptors;

import com.firstlinesoftware.ecm.server.services.DocumentService;
import com.firstlinesoftware.ecm.shared.dto.DocumentHistoryItem;
import com.firstlinesoftware.ecm.shared.dto.RelatedDocument;
import com.firstlinesoftware.exec.server.services.process.ErrandProcess;
import com.firstlinesoftware.exec.shared.dto.AbstractErrand;
import com.firstlinesoftware.ord.shared.directories.OrdDirectories;
import com.firstlinesoftware.rmrs.server.interceptors.helpers.HistoryRelatedDocumentsAtCreatingErrandReportHelper;
import com.firstlinesoftware.rmrs.shared.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.firstlinesoftware.base.server.utils.Messages;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
public final class ProposalErrandReportInterceptor implements ErrandProcess.Interceptor {
    private final Messages messages;
    private final DocumentService documentService;
    private final ErrandProcess errandProcess;
    private final HistoryRelatedDocumentsAtCreatingErrandReportHelper historyRelatedDocuments;

    @Autowired
    public ProposalErrandReportInterceptor(Messages messages, DocumentService documentService,
                                           ErrandProcess errandProcess,
                                           HistoryRelatedDocumentsAtCreatingErrandReportHelper historyRelatedDocuments) {
        this.messages = messages;
        this.documentService = documentService;
        this.errandProcess = errandProcess;
        this.historyRelatedDocuments = historyRelatedDocuments;
    }

    @PostConstruct
    private void init() {
        errandProcess.registerInterceptor(AbstractErrand.LIFECYCLE_ONEXECUTION, this);
    }

    @Override
    public void runBefore(AbstractErrand route) {
    }

    @Override
    public void runAfter(AbstractErrand route) {
        if (route instanceof ConsiderProposalErrand) {
            addHistoryForChangedRequirements(((ConsiderProposalErrand) route));
            final ProposalErrandReport proposalErrandReport = documentService.get(route.getLastReport().id);
            historyRelatedDocuments.addHistory(route, proposalErrandReport.relatedDocuments);
        }
    }

    private void addHistoryForChangedRequirements(ConsiderProposalErrand considerProposalErrand) {
        final List<Requirement> modifyExisting = considerProposalErrand.getLastReport().modifyExisting;
        if (modifyExisting != null && modifyExisting.size() > 0) {
            final DocumentHistoryItem documentHistoryItem =
                    new DocumentHistoryItem(considerProposalErrand.author,
                            messages.getMessage("requirement.included.in.errand"),
                            considerProposalErrand.getName(), considerProposalErrand.getId(), true);
            documentService.update(modifyExisting, documentHistoryItem);
        }
    }
}
