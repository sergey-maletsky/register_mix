package usecases.rmrs
import com.firstlinesoftware.base.server.services.AuthService
import com.firstlinesoftware.base.server.services.RepositoryService
import com.firstlinesoftware.base.shared.dto.DTO
import com.firstlinesoftware.ecm.server.services.DocumentService
import com.firstlinesoftware.rmrs.shared.dto.Requirement
import com.firstlinesoftware.route.server.services.DocumentRouteService
import com.firstlinesoftware.route.shared.dto.Rounds
import com.firstlinesoftware.route.shared.dto.RouteState
import org.springframework.beans.factory.annotation.Autowired
import usecases.base.BaseUC

import javax.ws.rs.POST
import javax.ws.rs.QueryParam

class SendRequirementsToApprovalUC extends BaseUC<Void> {
    @Autowired
    private DocumentService documentService;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private DocumentRouteService documentRouteService;

    @Autowired
    private AuthService authService;


    @POST
    public Object run(final DTO object, @QueryParam("ids") final List<String> ids) {
        return repositoryService.doInTransaction(false, new RepositoryService.RetryingTransactionCallback<Object>() {
            @Override
            public Object execute() throws Exception {
                if (ids) {
                    sendRequirementsToApproval(object as Rounds, ids)
                }
                return null;
            }
        });
    }

    private void sendRequirementsToApproval(Rounds rounds, List<String> ids) {
        authService.runAsAdmin({
            for(String id : ids) {
                final Requirement requirement = documentService.getProperties(id)
                requirement.approval = rounds.rounds
                requirement.routeState = new RouteState();
                documentService.updateProperties(requirement, null)
                documentRouteService.startRoute(id)
                documentRouteService.sendToApproval(id)
            }
        } as AuthService.RunAs<Void>)
    }
}