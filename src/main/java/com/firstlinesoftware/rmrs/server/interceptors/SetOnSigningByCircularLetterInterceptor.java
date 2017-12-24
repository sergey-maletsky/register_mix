package com.firstlinesoftware.rmrs.server.interceptors;

import com.firstlinesoftware.base.server.exceptions.NonLoggableServerException;
import com.firstlinesoftware.base.server.utils.Messages;
import com.firstlinesoftware.base.shared.dto.DTO;
import com.firstlinesoftware.ecm.server.services.DocumentService;
import com.firstlinesoftware.ecm.shared.dto.Document;
import com.firstlinesoftware.ecm.shared.dto.DocumentHistoryItem;
import com.firstlinesoftware.orgstruct.server.services.OrgstructService;
import com.firstlinesoftware.rmrs.server.RmrsAlfrescoTypes;
import com.firstlinesoftware.rmrs.server.interceptors.helpers.HistoryRelatedDocumentsHelper;
import com.firstlinesoftware.rmrs.shared.dto.CircularLetter;
import com.firstlinesoftware.rmrs.shared.dto.Requirement;
import com.firstlinesoftware.route.server.process.DocumentRouteProcess;
import com.firstlinesoftware.route.shared.dto.AbstractRoute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
@SuppressWarnings("unused")
public class SetOnSigningByCircularLetterInterceptor {
    @Autowired
    private Messages messages;
    @Autowired
    private DocumentRouteProcess documentRouteProcess;
    @Autowired
    private DocumentService documentService;
    @Autowired
    private HistoryRelatedDocumentsHelper historyRelatedDocumentsHelper;
    @Autowired
    private OrgstructService orgstructService;

    @PostConstruct
    private void init() {
        documentRouteProcess.registerInterceptor(Document.DOCUMENT_LIFECYCLE_DRAFT,
                new CircularLetterInterceptorAtDraft());

        documentRouteProcess.registerInterceptor(AbstractRoute.LIFECYCLE_REJECTED, new DocumentRouteProcess.Interceptor() {
            @Override
            public AbstractRoute runAfter(AbstractRoute route) {
                return route;
            }

            @Override
            public AbstractRoute runBefore(AbstractRoute route) {
                if (route instanceof CircularLetter) {
                    final List<Requirement> requirements = ((CircularLetter) route).approvedRequirements;
                    if (requirements != null) {
                        documentService.setProperty(DTO.getIDs(requirements), RmrsAlfrescoTypes.PROP_CIRCULAR_LETTER, null);
                        for (Requirement requirement : requirements) {
                            requirement.circularLetter = null;
                        }
                    }
                }
                return route;
            }
        });
    }

    private class CircularLetterInterceptorAtDraft implements DocumentRouteProcess.Interceptor {
        @Override
        public AbstractRoute runAfter(AbstractRoute route) {
            if (route instanceof CircularLetter) {
                final List<Requirement> requirements = ((CircularLetter) route).approvedRequirements;
                if (requirements != null) {
                    final List<Requirement> existing = documentService.getDocumentsByIds(DTO.getIDs(requirements));
                    for (Requirement r : existing) {
                        if (r.circularLetter != null) {
                            final CircularLetter prev = documentService.getProperties(r.circularLetter);
                            throw new NonLoggableServerException(messages.getMessage("demand.already.in.circular.letter",
                                    r.number,
                                    prev.getDocumentNumber() != null ? prev.getDocumentNumber() : prev.businessCaseNumber,
                                    prev.getAuthor(),
                                    prev.getName()));
                        }
                    }
                    for (Requirement requirement : requirements) {
                        requirement.circularLetter = ((CircularLetter) route).id;
                    }
                    documentService.setProperty(DTO.getIDs(requirements), RmrsAlfrescoTypes.PROP_CIRCULAR_LETTER, route.id);
                    addHistoryForApprovedRequirements(((CircularLetter) route));
                }
                historyRelatedDocumentsHelper.addHistory(route, route.relatedDocuments);
            }
            return route;
        }

        @Override
        public AbstractRoute runBefore(AbstractRoute route) {
            return route;
        }

        private void addHistoryForApprovedRequirements(CircularLetter circularLetter) {
            final String action = messages.getMessage("requirement.add.to.circular");
            final DocumentHistoryItem newDocumentHistoryItem =
                    new DocumentHistoryItem(circularLetter.author, action,
                            circularLetter.getName(), circularLetter.id, null);

            external:
            for (Requirement approvedRequirement : circularLetter.approvedRequirements) {
                final List<DocumentHistoryItem> documentHistoryItems =
                        ((Requirement) documentService.get(approvedRequirement.id)).getHistory();

                if (documentHistoryItems != null) {
                    for (final DocumentHistoryItem documentHistoryItem : documentHistoryItems) {
                        if (documentHistoryItem.action.equals(action) &&
                                documentHistoryItem.reference.equals(circularLetter.id)) {
                            continue external;
                        }
                    }
                }
                documentService.addHistory(approvedRequirement.id, newDocumentHistoryItem);
            }
        }
    }
}