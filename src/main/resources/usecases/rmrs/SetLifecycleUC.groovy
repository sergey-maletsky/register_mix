package usecases.rmrs
import com.firstlinesoftware.base.server.services.AuthService
import com.firstlinesoftware.base.server.services.RepositoryService
import com.firstlinesoftware.base.server.utils.Messages
import com.firstlinesoftware.base.shared.actions.RestResult
import com.firstlinesoftware.ecm.server.services.DocumentHistoryService
import com.firstlinesoftware.ecm.shared.dto.DocumentHistoryItem
import com.firstlinesoftware.orgstruct.server.services.OrgstructService
import com.firstlinesoftware.orgstruct.shared.dto.Position
import com.firstlinesoftware.rmrs.server.services.RequirementsService
import com.firstlinesoftware.rmrs.shared.dto.Requirement
import com.firstlinesoftware.route.shared.dto.AbstractRoute
import org.springframework.beans.factory.annotation.Autowired
import usecases.base.BaseUC

import javax.ws.rs.POST
import javax.ws.rs.QueryParam

class SetLifecycleUC extends BaseUC<Void> {
    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private DocumentHistoryService documentHistoryService;

    @Autowired
    private Messages messages;

    @Autowired
    private AuthService authService;

    @Autowired
    private OrgstructService orgstructService

    @Autowired
    RequirementsService requirementsService;


    @POST
    public Object run(final RestResult object, @QueryParam("lifecycle") final String lifecycle, @QueryParam("comment") final String comment) {
        return repositoryService.doInTransaction(false, new RepositoryService.RetryingTransactionCallback<Object>() {
            @Override
            public Object execute() throws Exception {
                setLifecycle(object.strings, lifecycle, comment)
                return null;
            }
        });
    }

    private void setLifecycle(List<String> ids, String lifecycle, String comment) {
        final List<Position> positions = orgstructService.getCurrentPositions()
        final Position position = positions != null && positions.size() > 0 ? positions.get(positions.size() - 1) : null

        authService.runAsAdmin({
            if (comment) {
                documentHistoryService.add(ids, new DocumentHistoryItem(position, messages.getMessage("status.changed"), comment))
            }
            if (AbstractRoute.LIFECYCLE_REJECTED.equals(lifecycle)) {
                requirementsService.reject(ids)
            } else if (AbstractRoute.LIFECYCLE_ONSIGNING.equals(lifecycle)) {
                requirementsService.sendToSigning(ids)
            } else if (Requirement.LIFECYCLE_SIGNED.equals(lifecycle)) {
                requirementsService.sign(ids)
            }
        } as AuthService.RunAs<Void>)
    }
}