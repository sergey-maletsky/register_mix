package com.firstlinesoftware.rmrs.server.interceptors;

import com.firstlinesoftware.base.server.utils.Messages;
import com.firstlinesoftware.ecm.server.services.DocumentService;
import com.firstlinesoftware.ord.server.services.RegistrationService;
import com.firstlinesoftware.rmrs.shared.dto.CircularLetter;
import com.firstlinesoftware.route.server.process.DocumentRouteProcess;
import com.firstlinesoftware.route.server.services.DocumentRouteService;
import com.firstlinesoftware.route.shared.dto.AbstractRoute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Date;

@Component
@SuppressWarnings("unused")
public class SetCircularLetterNumberInterceptor implements DocumentRouteProcess.Interceptor {

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

    @PostConstruct
    private void init() {
        documentRouteProcess.registerInterceptor(AbstractRoute.LIFECYCLE_ONREGISTRATION, this);
    }

    @Override
    public AbstractRoute runAfter(AbstractRoute route) {
        return route;
    }

    @Override
    public AbstractRoute runBefore(AbstractRoute route) {
        if (route instanceof CircularLetter) {
            final String n = registrationService.createRegistrationNumber(route.id);
            documentRouteService.register(route.id, ((CircularLetter) route).businessCaseNumber + n, null, new Date(), false, false);
        }
        return route;
    }
}