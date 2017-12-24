package com.firstlinesoftware.rmrs.server.providers;

import com.firstlinesoftware.ecm.shared.dto.Document;
import com.firstlinesoftware.exec.server.providers.impl.AbstractErrandDocumentProvider;
import com.firstlinesoftware.rmrs.server.RmrsAlfrescoTypes;
import com.firstlinesoftware.rmrs.shared.dto.ConsiderProposalErrand;
import org.alfresco.service.namespace.QName;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class ConsiderProposalDocumentProvider extends AbstractErrandDocumentProvider {

    @PostConstruct
    private void postConstruct() {
        documentProviderFactory.register(RmrsAlfrescoTypes.TYPE_CONSIDER_PROPOSAL, this);
        typeFactory.register(ConsiderProposalErrand.KIND, RmrsAlfrescoTypes.TYPE_CONSIDER_PROPOSAL);
        typeFactory.registerSearchable(RmrsAlfrescoTypes.TYPE_CONSIDER_PROPOSAL);
        propertyMapper.register(RmrsAlfrescoTypes.PREFIX_RMRS, ConsiderProposalErrand.class);
    }

    @Override
    public Document createInstance(QName type) {
        return new ConsiderProposalErrand();
    }
}
