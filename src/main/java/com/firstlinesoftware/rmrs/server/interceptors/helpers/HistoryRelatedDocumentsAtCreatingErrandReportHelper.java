package com.firstlinesoftware.rmrs.server.interceptors.helpers;

import com.firstlinesoftware.base.server.utils.Messages;
import com.firstlinesoftware.ecm.server.services.DocumentService;
import com.firstlinesoftware.ecm.shared.dto.RelatedDocument;
import com.firstlinesoftware.ord.shared.directories.OrdDirectories;
import com.firstlinesoftware.rmrs.shared.dto.Requirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public final class HistoryRelatedDocumentsAtCreatingErrandReportHelper extends HistoryRelatedDocumentsHelper {
    @Autowired
    public HistoryRelatedDocumentsAtCreatingErrandReportHelper(Messages messages, DocumentService documentService) {
        super(messages, documentService);
    }

    @Override
    public String getTypeOfRelatedDocuments(RelatedDocument relatedDocument) {
        String type = super.getTypeOfRelatedDocuments(relatedDocument);
        if (type == null && relatedDocument.document instanceof Requirement &&
                relatedDocument.relationType.equals(OrdDirectories.RELATION_TYPE_BASED_ON)) {
            return Requirement.KIND;
        }
        return type;
    }
}
