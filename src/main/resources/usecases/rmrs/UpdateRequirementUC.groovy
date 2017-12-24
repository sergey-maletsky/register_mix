package usecases.rmrs
import com.firstlinesoftware.base.server.services.AuthService
import com.firstlinesoftware.base.server.services.RepositoryService
import com.firstlinesoftware.base.shared.dto.DTO
import com.firstlinesoftware.ecm.server.services.DocumentService
import com.firstlinesoftware.ecm.server.services.impl.events.DocumentEventSender
import com.firstlinesoftware.orgstruct.server.services.OrgstructService
import com.firstlinesoftware.rmrs.server.services.RequirementsService
import com.firstlinesoftware.rmrs.shared.dto.Requirement
import com.firstlinesoftware.route.server.process.DocumentRouteProcess
import org.springframework.beans.factory.annotation.Autowired
import usecases.base.BaseUC

import javax.ws.rs.POST
import javax.ws.rs.QueryParam

class UpdateRequirementUC extends BaseUC<Void> {
    @Autowired
    private DocumentService documentService;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private DocumentRouteProcess documentRouteProcess;

    @Autowired
    private AuthService authService;

    @Autowired
    private RequirementsService requirementsService;

    @Autowired
    private OrgstructService orgstructService;


    @POST
    public Object run(final DTO object, @QueryParam("errand") final String errandId, @QueryParam("sendToRoute") final boolean sendToRoute) {
        return repositoryService.doInTransaction(false, new RepositoryService.RetryingTransactionCallback<Object>() {
            @Override
            public Object execute() throws Exception {
                updateRequirement(object as Requirement, errandId, sendToRoute)
                return null;
            }
        });
    }

    private void updateRequirement(Requirement document, String errandId, boolean sendToRoute) {
        authService.runAsAdmin({
            final id = requirementsService.update(document, errandId)
            if (errandId) {
                requirementsService.updateRequirementByErrand(errandId)
            }
            if (sendToRoute) {
                requirementsService.checkExistingDraftDates(document)
                documentRouteProcess.startRouteFromBeginning(document, id, null, null)
            }
            DocumentEventSender.sendFolderItemAddedEvent(id, "draft");
        } as AuthService.RunAs<Void>)
    }
}