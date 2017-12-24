package usecases.ecm

import com.firstlinesoftware.base.server.events.ServerEventBus
import com.firstlinesoftware.base.server.services.RepositoryService
import com.firstlinesoftware.base.server.utils.Messages
import com.firstlinesoftware.base.shared.actions.RestResult
import com.firstlinesoftware.ecm.server.services.DocumentService
import com.firstlinesoftware.orgstruct.server.services.OrgstructService
import com.firstlinesoftware.orgstruct.shared.events.PositionChangedServerEvent
import com.firstlinesoftware.rmrs.shared.dto.Requirement
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import usecases.base.BaseUC

import javax.ws.rs.POST

@Component
@SuppressWarnings("unused")
public class RemoveResposibilityForRequirementsUC extends BaseUC<Void> {
    @Autowired
    private DocumentService documentService;
    @Autowired
    private OrgstructService orgstructService;

    @Autowired
    private Messages messages;

    @POST
    public Void run(final RestResult object) {
        return repositoryService.doInTransaction(false, new RepositoryService.RetryingTransactionCallback<Void>() {
            @Override
            public Void execute() throws Exception {

                def administrator = orgstructService.getSystemAdministrator()
                List<Requirement> requirements = object.dtos as List<Requirement>
                for (Requirement requirement : requirements) {
                    requirement.responsible = administrator;
                }
                documentService.updateProperties(requirements, null);
                def ids = orgstructService.getCurrentPositionIds();
                for (String id : ids) {
                    ServerEventBus.addPendingEvent("orgstructure", new PositionChangedServerEvent(id));
                }
                return null;
            }
        });
    }
}
