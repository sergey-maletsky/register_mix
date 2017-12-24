package usecases.rmrs

import com.firstlinesoftware.base.server.services.AuthService
import com.firstlinesoftware.base.server.services.RepositoryService
import com.firstlinesoftware.base.shared.dto.DTO
import com.firstlinesoftware.ecm.server.services.DocumentService
import com.firstlinesoftware.ecm.shared.dto.DocumentHistoryItem
import com.firstlinesoftware.orgstruct.server.services.OrgstructService
import com.firstlinesoftware.orgstruct.shared.dto.Position
import com.firstlinesoftware.rmrs.server.services.RequirementsService
import com.firstlinesoftware.rmrs.shared.dto.Requirement
import com.firstlinesoftware.route.shared.dto.PositionRoles
import org.springframework.beans.factory.annotation.Autowired
import usecases.orgstruct.CheckedUC

import javax.ws.rs.POST

class FalsificateRequirementUC extends CheckedUC<Void> {
    @Autowired
    private DocumentService documentService;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private AuthService authService;

    @Autowired
    private RequirementsService requirementsService;

    @Autowired
    private OrgstructService orgstructService;


    @POST
    public Object run(final DTO object) {
        return repositoryService.doInTransaction(false, new RepositoryService.RetryingTransactionCallback<Object>() {
            @Override
            public Object execute() throws Exception {
                updateRequirement((Requirement) object)
                return null;
            }
        });
    }

    private void updateRequirement(Requirement document) {
        check(runIfHasRole(PositionRoles.ROLE_FALCIFICATE_DOCUMENT, {
            if (document.parent) {
                documentService.move(document, document.parent.id)
            }
            final Position author = orgstructService.getPosition(document.author.id)
            def person = orgstructService.getPerson(author.person.id)
            if ("admin".equals(person.userName) && document.responsible) {
                requirementsService.fixAuthorInChildren(document)
                document.author = document.responsible;
            }
            documentService.update(document, new DocumentHistoryItem(messages.getMessage("requirement.updated"), null));
        } as AuthService.RunAs<Void>))
    }
}