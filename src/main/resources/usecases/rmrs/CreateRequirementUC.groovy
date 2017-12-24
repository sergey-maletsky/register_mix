package usecases.rmrs

import com.firstlinesoftware.base.server.services.AuthService
import com.firstlinesoftware.base.server.services.RepositoryService
import com.firstlinesoftware.base.shared.dto.DTO
import com.firstlinesoftware.ecm.server.services.DocumentService
import com.firstlinesoftware.ecm.server.services.impl.events.DocumentEventSender
import com.firstlinesoftware.ecm.shared.dto.DocumentHistoryItem
import com.firstlinesoftware.orgstruct.client.Orgstruct
import com.firstlinesoftware.orgstruct.server.services.OrgstructService
import com.firstlinesoftware.rmrs.server.services.RequirementsService
import com.firstlinesoftware.rmrs.shared.dto.Requirement
import com.firstlinesoftware.rmrs.shared.dto.RmrsTasks
import com.firstlinesoftware.route.server.process.DocumentRouteProcess
import org.springframework.beans.factory.annotation.Autowired
import usecases.base.BaseUC

import javax.ws.rs.POST
import javax.ws.rs.QueryParam

class CreateRequirementUC extends BaseUC<Void> {
    @Autowired
    private DocumentService documentService;

    @Autowired
    private DocumentRouteProcess documentRouteProcess;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private AuthService authService;

    @Autowired
    private RequirementsService requirementsService;
    @Autowired
    private OrgstructService orgstructService;


    @POST
    public Object run(final DTO object,
            @QueryParam("folderId") final String folderId,
            @QueryParam("errand") final String errandId,
            @QueryParam("template") final String template,
            @QueryParam("sendToRoute") final boolean sendToRoute) {
        return repositoryService.doInTransaction(false, new RepositoryService.RetryingTransactionCallback<Object>() {
            @Override
            public Object execute() throws Exception {
                createRequirement((Requirement) object, folderId, errandId, template, sendToRoute)
                return null;
            }
        });
    }

    private void createRequirement(Requirement document, String folderId, String errandId, String template, boolean sendToRoute) {
        final DocumentHistoryItem historyItem =
                new DocumentHistoryItem(orgstructService.getCurrentPositions().get(0),
                        messages.getMessage("requirement.created"), null);
        authService.runAsAdmin({
            final String id = documentService.create(folderId, document, historyItem);
            if (errandId && template) {
                requirementsService.createRequirementByErrand(errandId, id, template)
            }
            if (sendToRoute) {
                documentRouteProcess.startRouteFromBeginning(document, id, null, null)
            }
            DocumentEventSender.sendDocumentCreatedEvent(documentService.get(id), folderId);
            DocumentEventSender.sendFolderItemAddedEvent(id, sendToRoute ? RmrsTasks.REQ_SENT_TO_APPROVAL : "draft")
        } as AuthService.RunAs<Void>)
    }
}