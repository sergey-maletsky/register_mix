package com.firstlinesoftware.rmrs.server.interceptors;

import com.firstlinesoftware.ecm.server.services.DocumentService;
import com.firstlinesoftware.ecm.server.services.NotificationEmailService;
import com.firstlinesoftware.ecm.server.services.PositionFoldersService;
import com.firstlinesoftware.ecm.shared.dto.Document;
import com.firstlinesoftware.orgstruct.server.services.OrgstructService;
import com.firstlinesoftware.rmrs.server.services.RequirementsService;
import com.firstlinesoftware.rmrs.shared.dto.Requirement;
import com.firstlinesoftware.route.server.process.DocumentRouteProcess;
import com.firstlinesoftware.route.server.services.CommonRouteService;
import com.firstlinesoftware.route.server.services.impl.RouteEventSender;
import com.firstlinesoftware.route.shared.dto.AbstractRoute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public final class RejectRequirementInterceptor implements DocumentRouteProcess.Interceptor {
    private final DocumentRouteProcess documentRouteProcess;
    private final RequirementsService requirementsService;
    private final CommonRouteService commonRouteService;
    private final PositionFoldersService positionFoldersService;
    private final DocumentService documentService;
    private final RouteEventSender routeEventSender;
    private final NotificationEmailService notificationEmailService;
    private final OrgstructService orgstructService;

    @Autowired
    public RejectRequirementInterceptor(DocumentRouteProcess documentRouteProcess, RequirementsService requirementsService,
                                        CommonRouteService commonRouteService, PositionFoldersService positionFoldersService,
                                        DocumentService documentService, RouteEventSender routeEventSender,
                                        NotificationEmailService notificationEmailService,
                                        OrgstructService orgstructService) {
        this.documentRouteProcess = documentRouteProcess;
        this.requirementsService = requirementsService;
        this.commonRouteService = commonRouteService;
        this.positionFoldersService = positionFoldersService;
        this.documentService = documentService;
        this.routeEventSender = routeEventSender;
        this.notificationEmailService = notificationEmailService;
        this.orgstructService = orgstructService;
    }

    @PostConstruct
    private void init() {
        documentRouteProcess.registerInterceptor(AbstractRoute.LIFECYCLE_REJECTED, this);
    }

    @Override
    public AbstractRoute runAfter(AbstractRoute route) {
        return route;
    }

    @Override
    public AbstractRoute runBefore(AbstractRoute route) {
        if (route instanceof Requirement) {
            final String responsibleId = requirementsService.getResponsible(route.id);
            if (responsibleId != null) {
                positionFoldersService.link(route.id, responsibleId, AbstractRoute.LIFECYCLE_REJECTED);

                final Document updated = commonRouteService.createUpdated(route);
                updated.id = route.id;
                commonRouteService.setLifecycle(updated, AbstractRoute.LIFECYCLE_REJECTED, null);
                documentService.setReadWrite(route.id, responsibleId);

                final Document afterUpdate = documentService.get(route.id);
                routeEventSender.sendTaskCreatedServerEvent(afterUpdate, orgstructService.getPosition(responsibleId),
                        AbstractRoute.LIFECYCLE_REJECTED);
                notificationEmailService.sendTask(route.id, responsibleId, AbstractRoute.LIFECYCLE_REJECTED);

                return null;
            }
        }
        return route;
    }
}
