package usecases.rmrs
import com.firstlinesoftware.base.server.services.RepositoryService
import com.firstlinesoftware.ecm.server.services.DocumentService
import com.firstlinesoftware.ecm.shared.dto.Document
import com.firstlinesoftware.orgstruct.server.services.OrgstructService
import com.firstlinesoftware.orgstruct.shared.dto.Person
import com.firstlinesoftware.rmrs.server.db.dao.StatisticsDAO
import com.firstlinesoftware.rmrs.server.db.entities.RequirementsStatistics
import groovy.transform.Memoized
import org.jboss.resteasy.annotations.GZIP
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import usecases.base.BaseUC

import javax.ws.rs.GET
import javax.ws.rs.QueryParam
import java.sql.Timestamp

@Component
@SuppressWarnings("unused")
public class GetDocumentExtUC extends BaseUC<Document> {
    @Autowired
    DocumentService documentService
    @Autowired
    OrgstructService orgstructService;
    @Autowired
    private StatisticsDAO statisticsDAO;

    @GZIP @GET @Memoized
    public Object run(@QueryParam("id") final String id) {
        return repositoryService.doInTransaction(true, new RepositoryService.RetryingTransactionCallback<Document>() {
            @Override
            public Document execute() throws Exception {
                def document = documentService.get(id)
                countDocument(id);
                return document
            }
        })
    }

    public void countDocument(final String id) {
        long count;
        long userCount;
        Person person = orgstructService.getPersonByUsername(authService.getCurrentPersonId());

        String requirementId = id;
        if (id.indexOf("&") > 0) {
            requirementId = id.substring(0, id.indexOf("&"));
        }

        RequirementsStatistics requirementsStatistics = new RequirementsStatistics();
        requirementsStatistics.setRequirementId(requirementId);
        requirementsStatistics.setUserId(person.getId());

        Calendar calendar = Calendar.getInstance();
        Timestamp currentTimestamp = new Timestamp(calendar.getTime().getTime());
        requirementsStatistics.setVisited(currentTimestamp);
        long currentMilliseconds = 1000 * (currentTimestamp.getTime()/1000);

        if (statisticsDAO.getCountingDocuments().containsKey(requirementId)) {
            count = statisticsDAO.getCountingDocuments().get(requirementId);
        } else {
            count = statisticsDAO.getRequirementCountingById(requirementId);
        }

        Timestamp timestamp = statisticsDAO.getUserVisitedById(requirementId, person.getId());
        if (timestamp != null){
            long dbMilliseconds = 1000 * (timestamp.getTime()/1000);
            if (statisticsDAO.getCountingDocumentsByUser().containsKey(dbMilliseconds)) {
                userCount = statisticsDAO.getCountingDocumentsByUser().get(dbMilliseconds).getCount();
            } else {
                userCount = statisticsDAO.getUserCountingById(requirementId, person.getId());
            }

            requirementsStatistics.setCount(++userCount);
            statisticsDAO.getCountingDocumentsByUser().put(dbMilliseconds, requirementsStatistics);
        } else {
            requirementsStatistics.setCount(++userCount);
            statisticsDAO.getCountingDocumentsByUser().put(currentMilliseconds, requirementsStatistics);
        }

        statisticsDAO.getCountingDocuments().put(requirementId, ++count);
    }
}
