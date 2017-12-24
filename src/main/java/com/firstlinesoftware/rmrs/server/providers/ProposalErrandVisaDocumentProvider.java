package com.firstlinesoftware.rmrs.server.providers;

import com.firstlinesoftware.base.server.services.RepositoryService;
import com.firstlinesoftware.base.shared.dto.Persistent;
import com.firstlinesoftware.ecm.shared.dto.Document;
import com.firstlinesoftware.exec.server.ExecAlfrescoTypes;
import com.firstlinesoftware.exec.server.providers.impl.VisaDocumentProvider;
import com.firstlinesoftware.exec.shared.dto.Visa;
import com.firstlinesoftware.rmrs.server.RmrsAlfrescoTypes;
import com.firstlinesoftware.rmrs.shared.dto.HasChangesToRequirements;
import com.firstlinesoftware.rmrs.shared.dto.ProposalErrandVisa;
import org.alfresco.service.namespace.QName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.Serializable;

@Component
public class ProposalErrandVisaDocumentProvider extends VisaDocumentProvider {
    @Autowired
    private HasChangesToRequirementsAspectProvider hasChangesToRequirementsAspectProvider;

    @PostConstruct
    private void postConstruct() {
        documentProviderFactory.register(RmrsAlfrescoTypes.TYPE_PROPOSAL_ERRAND_VISA, this);
        typeFactory.register(ProposalErrandVisa.KIND, RmrsAlfrescoTypes.TYPE_PROPOSAL_ERRAND_VISA);
    }

    @Override
    public Document createInstance(QName type) {
        return new ProposalErrandVisa();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void fillDocument(Persistent persistent, RepositoryService.Node properties) {
        super.fillDocument(persistent, properties);
        hasChangesToRequirementsAspectProvider.fillDocument(persistent, properties);
        if (persistent instanceof ProposalErrandVisa) {
            ((ProposalErrandVisa) persistent).createdErrandType =
                    properties.get(RmrsAlfrescoTypes.PROP_CREATED_ERRAND_TYPE);
        }
    }

    @Override
    public void fillNode(RepositoryService.Node node, Persistent persistent) {
        super.fillNode(node, persistent);
        hasChangesToRequirementsAspectProvider.fillNode(node, persistent);
        if (persistent instanceof ProposalErrandVisa) {
            node.add(RmrsAlfrescoTypes.PROP_CREATED_ERRAND_TYPE, ((ProposalErrandVisa) persistent).createdErrandType);
        }
    }

    @Override
    public void fillVersion(RepositoryService.Node version, RepositoryService.Node node) {
        super.fillVersion(version, node);
        hasChangesToRequirementsAspectProvider.fillVersion(version, node);
        version.add(RmrsAlfrescoTypes.PROP_CREATED_ERRAND_TYPE, node.get(RmrsAlfrescoTypes.PROP_CREATED_ERRAND_TYPE));
    }
}
