package com.firstlinesoftware.rmrs.server.interceptors;

import com.firstlinesoftware.exec.server.services.process.ErrandProcess;
import com.firstlinesoftware.exec.shared.dto.AbstractErrand;
import com.firstlinesoftware.orgstruct.server.services.OrgstructService;
import com.firstlinesoftware.rmrs.shared.dto.ConsiderProposalErrand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@SuppressWarnings("unused")
public class SetConsiderProposalErrandControllerInterceptor implements ErrandProcess.Interceptor {
    @Autowired
    private ErrandProcess errandProcess;

    @Autowired
    private OrgstructService orgstructService;

    @PostConstruct
    private void init() {
        errandProcess.registerInterceptor(AbstractErrand.LIFECYCLE_DRAFT, this);
    }

    @Override
    public void runBefore(AbstractErrand errand) {
        if (errand instanceof ConsiderProposalErrand && errand.controller == null) {
            errand.controller = orgstructService.getBoss(errand.executor.id);
        }
    }

    @Override
    public void runAfter(AbstractErrand errand) {
    }
}
