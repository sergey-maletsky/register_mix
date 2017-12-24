package usecases.rmrs

import com.firstlinesoftware.base.server.services.RepositoryService
import com.firstlinesoftware.ecm.shared.dto.Document
import com.firstlinesoftware.rmrs.server.db.dao.StatisticsDAO
import org.jboss.resteasy.annotations.GZIP
import org.jboss.resteasy.annotations.cache.NoCache
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import usecases.base.BaseUC

import javax.ws.rs.GET
import javax.ws.rs.QueryParam

@Component
@SuppressWarnings("unused")
public class GetDocumentCountingUC extends BaseUC<Document> {
    @Autowired
    private StatisticsDAO statisticsDAO;

    @GZIP @NoCache @GET
    public Object run(@QueryParam("id") final String id) {
        return repositoryService.doInTransaction(true, new RepositoryService.RetryingTransactionCallback<String>() {
            @Override
            public String execute() throws Exception {
                return getDocumentCounting(id);
            }
        });
    }

    public String getDocumentCounting(final String id) {
        long count;
        if (statisticsDAO.getCountingDocuments().containsKey(id)) {
            count = statisticsDAO.getCountingDocuments().get(id);
        } else {
            count = statisticsDAO.getRequirementCountingById(id);
        }

        return count.toString()
    }
}
