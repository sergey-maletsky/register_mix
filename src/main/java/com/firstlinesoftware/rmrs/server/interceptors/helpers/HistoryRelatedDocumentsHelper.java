package com.firstlinesoftware.rmrs.server.interceptors.helpers;

import com.firstlinesoftware.base.server.utils.Messages;
import com.firstlinesoftware.ecm.server.services.DocumentService;
import com.firstlinesoftware.ecm.shared.dto.CompositeDocument;
import com.firstlinesoftware.ecm.shared.dto.Document;
import com.firstlinesoftware.ecm.shared.dto.DocumentHistoryItem;
import com.firstlinesoftware.ecm.shared.dto.RelatedDocument;
import com.firstlinesoftware.exec.shared.dto.AbstractErrand;
import com.firstlinesoftware.rmrs.shared.dto.CircularLetter;
import com.firstlinesoftware.rmrs.shared.dto.Proposal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class HistoryRelatedDocumentsHelper {
    private final Messages messages;
    private final DocumentService documentService;

    @Autowired
    public HistoryRelatedDocumentsHelper(Messages messages, DocumentService documentService) {
        this.messages = messages;
        this.documentService = documentService;
    }

    public void addHistory(CompositeDocument compositeDocument, List<RelatedDocument> relatedDocuments) {
        if (relatedDocuments != null && relatedDocuments.size() > 0) {
            final Map<String, List<Document>> relatedDocumentsByTypes = new HashMap<>();
            for (RelatedDocument relatedDocument : relatedDocuments) {
                final String type = getTypeOfRelatedDocuments(relatedDocument);
                if (type != null) {
                    List<Document> documentsOfThisType = relatedDocumentsByTypes.get(type);
                    if (documentsOfThisType == null) {
                        documentsOfThisType = new ArrayList<>();
                        relatedDocumentsByTypes.put(type, documentsOfThisType);
                    }
                    documentsOfThisType.add(relatedDocument.document);
                }
            }

            for (Map.Entry<String, List<Document>> pair : relatedDocumentsByTypes.entrySet()) {
                final DocumentHistoryItem documentHistoryItem =
                        new DocumentHistoryItem(messages.getMessage(String.format("%s.included.in.document", pair.getKey())),
                                compositeDocument.getName(), compositeDocument.getId(), true);
                documentService.update(pair.getValue(), documentHistoryItem);
            }
        }
    }

    public String getTypeOfRelatedDocuments(RelatedDocument relatedDocument) {
        if (relatedDocument.document instanceof CircularLetter) {
            return CircularLetter.KIND;
        } else if (relatedDocument.document instanceof Proposal) {
            return Proposal.KIND;
        } else if (relatedDocument.document instanceof AbstractErrand) {
            return AbstractErrand.KIND;
        }
        return null;
    }
}
