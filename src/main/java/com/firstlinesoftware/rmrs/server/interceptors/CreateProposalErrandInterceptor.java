package com.firstlinesoftware.rmrs.server.interceptors;

import com.firstlinesoftware.base.server.exceptions.ServerException;
import com.firstlinesoftware.base.server.utils.Messages;
import com.firstlinesoftware.ecm.server.EcmAlfrescoTypes;
import com.firstlinesoftware.ecm.server.services.DocumentService;
import com.firstlinesoftware.ecm.shared.dto.DocumentHistoryItem;
import com.firstlinesoftware.exec.server.services.process.ErrandProcess;
import com.firstlinesoftware.orgstruct.server.services.OrgstructService;
import com.firstlinesoftware.orgstruct.shared.dto.Position;
import com.firstlinesoftware.rmrs.server.interceptors.helpers.HistoryRelatedDocumentsHelper;
import com.firstlinesoftware.rmrs.server.services.RequirementsService;
import com.firstlinesoftware.rmrs.shared.dto.ConsiderProposalErrand;
import com.firstlinesoftware.rmrs.shared.dto.Proposal;
import com.firstlinesoftware.rmrs.shared.dto.Requirement;
import com.firstlinesoftware.route.server.process.DocumentRouteProcess;
import com.firstlinesoftware.route.shared.dto.AbstractRoute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;

@Component
@SuppressWarnings("unused")
public class CreateProposalErrandInterceptor implements DocumentRouteProcess.Interceptor {
    @Autowired
    private DocumentRouteProcess documentRouteProcess;
    @Autowired
    private ErrandProcess errandProcess;
    @Autowired
    private RequirementsService requirementsService;
    @Autowired
    private OrgstructService orgstructService;
    @Autowired
    private Messages messages;
    @Autowired
    private DocumentService documentService;
    @Autowired
    private HistoryRelatedDocumentsHelper historyRelatedDocumentsHelper;

    @PostConstruct
    private void init() {
        documentRouteProcess.registerInterceptor(AbstractRoute.LIFECYCLE_ONEXECUTION, this);
    }

    @Override
    public AbstractRoute runAfter(AbstractRoute route) {
        return route;
    }

    @Override
    public AbstractRoute runBefore(AbstractRoute route) {
        if (route instanceof Proposal) {
            createConsiderProposals((Proposal) route);
            addHistoryForChangedRequirements(((Proposal) route));
            historyRelatedDocumentsHelper.addHistory(route, route.relatedDocuments);
            documentService.setProperty(route.id, EcmAlfrescoTypes.PROP_LIFECYCLE, AbstractRoute.LIFECYCLE_ONEXECUTION);
            return null;
        }
        return route;
    }

    private void createConsiderProposals(final Proposal proposal) {
        final Map<String, ConsiderProposalErrand> errands = new HashMap<>();
        for (Position executor : collectExecutors(proposal)) {
            final Position boss = orgstructService.getBoss(executor.id);
            if (!errands.containsKey(boss.id)) {
                final ConsiderProposalErrand errand = new ConsiderProposalErrand();
                errand.document = proposal;
                if (proposal.errandText != null && !proposal.errandText.isEmpty()) {
                    errand.setName(proposal.errandText);
                } else {
                    errand.setName(messages.getMessage("proposalErrandText"));
                }
                errand.author = proposal.author;
                errand.controlDate = proposal.controlDate;
                errand.controlDays = proposal.controlDays;
                errand.businessOnlyControlDays = proposal.businessOnlyControlDays;
                errand.executors = new ArrayList<>();
                errand.executors.add(executor);
                errand.controller = boss;
                errand.relatedDocuments = proposal.relatedDocuments;
                errand.attachedFiles = proposal.attachedFiles;
                errands.put(executor.id, errand);
            } else {
                errands.get(boss.id).executors.add(executor);
            }
        }
        for (ConsiderProposalErrand errand : errands.values()) {
            errandProcess.create(errand);
        }
    }

    private Set<Position> collectExecutors(Proposal proposal) {
        final Set<Position> executors = proposal.executives != null ? new HashSet<>(proposal.executives) : new HashSet<Position>();
        if (proposal.changedRequirements != null) {
            for (Requirement requirement : proposal.changedRequirements) {
                final Position responsible = orgstructService.getPosition(requirementsService.getResponsible(requirement.id));
                if (responsible != null) {
                    executors.add(responsible);
                } else {
                    throw new ServerException(messages.getMessage("responsible.not.found", requirement.getName()));
                }
            }
        }
        if (executors.isEmpty()) {
            throw new ServerException(messages.getMessage("route.exception.noresponsible", ""));
        }
        return executors;
    }

    private void addHistoryForChangedRequirements(Proposal proposal) {
        if (proposal.changedRequirements != null && proposal.changedRequirements.size() > 0) {
            final DocumentHistoryItem documentHistoryItem =
                    new DocumentHistoryItem(proposal.author,
                            messages.getMessage("requirement.included.in.proposal"),
                            proposal.getName(), proposal.getId(), true);
            documentService.update(proposal.changedRequirements, documentHistoryItem);
        }
    }
}
