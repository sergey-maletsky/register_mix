package com.firstlinesoftware.rmrs.server.interceptors;

import com.firstlinesoftware.base.server.utils.Messages;
import com.firstlinesoftware.ecm.server.services.DocumentService;
import com.firstlinesoftware.ecm.shared.dto.DocumentHistoryItem;
import com.firstlinesoftware.exec.server.services.process.ErrandProcess;
import com.firstlinesoftware.exec.shared.dto.AbstractErrand;
import com.firstlinesoftware.exec.shared.dto.Visa;
import com.firstlinesoftware.orgstruct.server.services.OrgstructService;
import com.firstlinesoftware.orgstruct.shared.dto.Position;
import com.firstlinesoftware.rmrs.shared.dto.ChangeRequirementErrand;
import com.firstlinesoftware.rmrs.shared.dto.ConsiderProposalErrand;
import com.firstlinesoftware.rmrs.shared.dto.ProposalErrandVisa;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;

@Component
@SuppressWarnings("unused")
public class CreateChangeRequirementErrandInterceptor implements ErrandProcess.Interceptor {
    @Autowired
    private ErrandProcess errandProcess;

    @Autowired
    private OrgstructService orgstructService;

    @Autowired
    private DocumentService documentService;

    @Autowired
    private Messages messages;

    @PostConstruct
    private void init() {
        errandProcess.registerInterceptor(AbstractErrand.LIFECYCLE_COMPLETED, this);
    }

    @Override
    public void runAfter(AbstractErrand route) {
        if (route instanceof ConsiderProposalErrand) {
            createChangeRequirementErrand((ConsiderProposalErrand) route);
        }
    }

    @Override
    public void runBefore(AbstractErrand route) {
    }

    private void createChangeRequirementErrand(final ConsiderProposalErrand considerProposalErrand) {
        if(considerProposalErrand.parentErrand == null) {
            final ChangeRequirementErrand errand = new ChangeRequirementErrand();

            assert considerProposalErrand.visaId != null;
            final Visa visa = documentService.getProperties(considerProposalErrand.visaId);
            if(visa instanceof ProposalErrandVisa) {
                final ProposalErrandVisa v = (ProposalErrandVisa) (visa);
                if ((v.createNew != null && !v.createNew.isEmpty()) || (v.modifyExisting != null && !v.modifyExisting.isEmpty())) {
                    errand.kind = v.getCreatedErrandType();
                    errand.setName(visa.getName());
                    errand.setAttachedFiles(visa.getAttachedFiles());
                    errand.setRelatedDocuments(visa.getRelatedDocuments());
                    errand.setModifyExisting(v.getModifyExisting());
                    errand.setCreateNew(v.getCreateNew());

                    errand.document = considerProposalErrand.document;
                    final Position boss = orgstructService.getCurrentPosition(considerProposalErrand.controller);
                    final Position inspector = orgstructService.getCurrentPosition(considerProposalErrand.executor);
                    errand.author = boss;
                    errand.controller = boss;
                    errand.executors = Arrays.asList(inspector);

                    errand.controlDate = considerProposalErrand.controlDate;
                    errand.controlDays = considerProposalErrand.controlDays;
                    errand.businessOnlyControlDays = considerProposalErrand.businessOnlyControlDays;

                    errandProcess.create(errand);
                }
            }

            addHistory(errand);
        }
    }

    private void addHistory(ChangeRequirementErrand changeRequirementErrand) {
        if (changeRequirementErrand.modifyExisting != null && changeRequirementErrand.modifyExisting.size() > 0) {
            final List<Position> positions = orgstructService.getCurrentPositions();
            final Position position =
                    positions != null && positions.size() > 0 ? positions.get(positions.size() - 1) : null;

            final DocumentHistoryItem documentHistoryItem =
                    new DocumentHistoryItem(position, messages.getMessage("requirement.included.in.errand"),
                            changeRequirementErrand.getName(), changeRequirementErrand.getId(), true);
            documentService.update(changeRequirementErrand.modifyExisting, documentHistoryItem);
        }
    }
}
