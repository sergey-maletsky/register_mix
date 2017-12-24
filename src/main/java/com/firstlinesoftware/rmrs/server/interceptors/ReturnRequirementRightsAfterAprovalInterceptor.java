package com.firstlinesoftware.rmrs.server.interceptors;

import com.firstlinesoftware.base.server.utils.Messages;
import com.firstlinesoftware.ecm.server.services.DocumentService;
import com.firstlinesoftware.ord.server.services.RegistrationService;
import com.firstlinesoftware.orgstruct.server.services.OrgstructService;
import com.firstlinesoftware.rmrs.shared.dto.Requirement;
import com.firstlinesoftware.route.server.process.DocumentRouteProcess;
import com.firstlinesoftware.route.server.services.DocumentRouteService;
import com.firstlinesoftware.route.shared.dto.AbstractRoute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@SuppressWarnings("unused")
public class ReturnRequirementRightsAfterAprovalInterceptor implements DocumentRouteProcess.Interceptor {
    @Autowired
    private Messages messages;
    @Autowired
    private DocumentRouteProcess documentRouteProcess;
    @Autowired
    private DocumentService documentService;
    @Autowired
    private DocumentRouteService documentRouteService;
    @Autowired
    private RegistrationService registrationService;
    @Autowired
    private OrgstructService orgstructService;

    @PostConstruct
    private void init() {
        documentRouteProcess.registerInterceptor(AbstractRoute.LIFECYCLE_ONAPPROVAL, this);
    }

    @Override
    public AbstractRoute runAfter(AbstractRoute route) {
        if (route instanceof Requirement) {
            documentService.resetPermissions(route.id, false);
        }
        return route;
    }

    @Override
    public AbstractRoute runBefore(AbstractRoute route) {
        return route;
    }
}