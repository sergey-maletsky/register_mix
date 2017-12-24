package usecases.rmrs

import com.firstlinesoftware.base.server.services.RepositoryService
import com.firstlinesoftware.rmrs.server.services.RequirementsService
import com.firstlinesoftware.rmrs.shared.dto.Requirement
import org.jboss.resteasy.annotations.cache.NoCache
import org.springframework.beans.factory.annotation.Autowired
import usecases.orgstruct.CheckedUC

import javax.ws.rs.GET
import javax.ws.rs.QueryParam

class GetResponsibleUC extends CheckedUC<String> {
    @Autowired
    private RequirementsService requirementsService;

    @NoCache
    @GET
    String run(@QueryParam("id") String id) {
        return repositoryService.doInTransaction(true, new RepositoryService.RetryingTransactionCallback<String>() {
            @Override
            public String execute() throws Exception {
                return requirementsService.getResponsible(id);
            }
        });
    }
}