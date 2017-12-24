package com.firstlinesoftware.rmrs.server.interceptors;

import com.firstlinesoftware.base.server.services.DirectoryService;
import com.firstlinesoftware.exec.server.services.process.ErrandProcess;
import com.firstlinesoftware.exec.shared.dto.AbstractErrand;
import com.firstlinesoftware.ord.server.services.RegistrationService;
import com.firstlinesoftware.rmrs.shared.dto.ChangeRequirementErrand;
import com.firstlinesoftware.rmrs.shared.dto.ConsiderProposalErrand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class SetConsiderProposalErrandNumberInterceptor implements ErrandProcess.Interceptor {

    @Autowired
    private ErrandProcess errandProcess;
    @Autowired
    private RegistrationService registrationService;
    @Autowired
    DirectoryService directoryService;

    @PostConstruct
    private void init() {
        errandProcess.registerInterceptor(AbstractErrand.LIFECYCLE_DRAFT, this);
    }

    @Override
    public void runBefore(AbstractErrand errand) {
        if(errand instanceof ConsiderProposalErrand || errand instanceof ChangeRequirementErrand) {
            errand.number = registrationService.getRegistrationNumber(errand);
        }
    }

    @Override
    public void runAfter(AbstractErrand route) {
    }
}