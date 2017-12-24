package com.firstlinesoftware.rmrs.server.providers;

import com.firstlinesoftware.exec.server.providers.impl.AbstractErrandTaskProvider;
import com.firstlinesoftware.rmrs.server.RmrsAlfrescoTypes;
import com.firstlinesoftware.rmrs.shared.dto.RmrsTasks;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class ConsiderProposalTaskProvider extends AbstractErrandTaskProvider {
    @PostConstruct
    private void postConstruct() {
        taskProviderFactory.register(RmrsTasks.ERRAND_CONSIDER_PROPOSAL, RmrsAlfrescoTypes.TYPE_CONSIDER_PROPOSAL, this);
    }
}
