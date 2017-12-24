package usecases.rmrs
import com.firstlinesoftware.base.server.services.RepositoryService
import com.firstlinesoftware.rmrs.server.services.RequirementsService
import com.firstlinesoftware.rmrs.shared.dto.Requirement
import org.jboss.resteasy.annotations.GZIP
import org.jboss.resteasy.annotations.cache.NoCache
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import usecases.base.BaseUC

import javax.ws.rs.GET
import javax.ws.rs.QueryParam

@Component
@SuppressWarnings("unused")
public class GetByFolderWithAttachVersionsUC extends BaseUC<Void> {
    @Autowired
    private RequirementsService requirementsService;

    @GZIP
    @NoCache
    @GET
    public Object run(@QueryParam("folder") final String id, @QueryParam("showRecursive") final boolean showRecursive, @QueryParam("onlyHeaders") final boolean onlyHeaders) {
        return repositoryService.doInTransaction(true, new RepositoryService.RetryingTransactionCallback<List<Requirement>>() {
            @Override
            public List<Requirement> execute() throws Exception {
                return requirementsService.getByFolderWithAttachVersions(id, showRecursive, onlyHeaders);
            }
        });
    }
}
