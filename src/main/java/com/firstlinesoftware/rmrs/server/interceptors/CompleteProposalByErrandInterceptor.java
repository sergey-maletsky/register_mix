package com.firstlinesoftware.rmrs.server.interceptors;

import com.firstlinesoftware.ecm.server.services.DocumentService;
import com.firstlinesoftware.ecm.shared.dto.Document;
import com.firstlinesoftware.exec.server.services.process.ErrandProcess;
import com.firstlinesoftware.exec.shared.dto.AbstractErrand;
import com.firstlinesoftware.ord.server.interceptors.CheckForUncompletedErrandsInterceptor;
import com.firstlinesoftware.rmrs.shared.dto.ConsiderProposalErrand;
import com.firstlinesoftware.route.server.process.DocumentRouteProcess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@SuppressWarnings("unused")
public class CompleteProposalByErrandInterceptor implements ErrandProcess.Interceptor {
    @Autowired
    private ErrandProcess errandProcess;

    @Autowired
    private DocumentRouteProcess documentRouteProcess;

    @Autowired
    private DocumentService documentService;
    @Autowired
    private CheckForUncompletedErrandsInterceptor checkForUncompletedErrandsInterceptor;


    @PostConstruct
    private void init() {
        errandProcess.registerInterceptor(AbstractErrand.LIFECYCLE_COMPLETED, this);
    }

    @Override
    public void runAfter(AbstractErrand errand) {
        if (errand instanceof ConsiderProposalErrand) {
            final Document document = documentService.get(errand.document.id);
            if (checkForUncompletedErrandsInterceptor.checkForUncompletedErrands(document)) {
                documentRouteProcess.tryToFinishExecution(document, errand.executor.id);
            }
        }
    }

    @Override
    public void runBefore(AbstractErrand route) {
    }
}
