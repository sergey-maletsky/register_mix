package com.firstlinesoftware.rmrs.server.interceptors;

import com.firstlinesoftware.base.server.utils.Messages;
import com.firstlinesoftware.base.shared.dto.DTO;
import com.firstlinesoftware.ecm.server.services.DocumentService;
import com.firstlinesoftware.ecm.shared.dto.DocumentHistoryItem;
import com.firstlinesoftware.orgstruct.server.services.OrgstructService;
import com.firstlinesoftware.orgstruct.shared.dto.Position;
import com.firstlinesoftware.rmrs.server.services.RequirementsService;
import com.firstlinesoftware.rmrs.shared.dto.CircularLetter;
import com.firstlinesoftware.rmrs.shared.dto.Requirement;
import com.firstlinesoftware.route.server.process.DocumentRouteProcess;
import com.firstlinesoftware.route.shared.dto.AbstractRoute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
@SuppressWarnings("unused")
public class MarkRequirementsAsSignedByCircularLetterInterceptor implements DocumentRouteProcess.Interceptor {
    @Autowired
    private Messages messages;
    @Autowired
    private DocumentRouteProcess documentRouteProcess;
    @Autowired
    private DocumentService documentService;
    @Autowired
    private RequirementsService requirementsService;
    @Autowired
    private OrgstructService orgstructService;

    @PostConstruct
    private void init() {
        documentRouteProcess.registerInterceptor(AbstractRoute.LIFECYCLE_ONREGISTRATION, this);
    }

    @Override
    public AbstractRoute runAfter(AbstractRoute route) {
        if (route instanceof CircularLetter) {
            final List<Requirement> requirements = ((CircularLetter) route).approvedRequirements;
            if (requirements != null && !requirements.isEmpty()) {
                requirementsService.sign(DTO.getIDs(requirements));

                final List<Position> positions = orgstructService.getCurrentPositions();
                final Position position = positions != null && !positions.isEmpty() ? positions.get(positions.size() - 1) : null;
                final DocumentHistoryItem documentHistoryItem =
                        new DocumentHistoryItem(position, messages.getMessage("requirement.approved.in.circularletter"),
                                route.getName(), route.getId(), true);
                for (Requirement requirement : requirements) {
                    documentService.addHistory(requirement.id, documentHistoryItem);
                }
            }
        }
        return route;
    }

    @Override
    public AbstractRoute runBefore(AbstractRoute route) {
        return route;
    }
}