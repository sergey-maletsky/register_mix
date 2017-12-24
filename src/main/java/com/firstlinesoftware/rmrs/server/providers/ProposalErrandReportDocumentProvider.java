package com.firstlinesoftware.rmrs.server.providers;

import com.firstlinesoftware.base.server.services.RepositoryService;
import com.firstlinesoftware.base.shared.dto.Persistent;
import com.firstlinesoftware.ecm.shared.dto.Document;
import com.firstlinesoftware.exec.server.providers.impl.AbstractErrandReportDocumentProvider;
import com.firstlinesoftware.rmrs.server.RmrsAlfrescoTypes;
import com.firstlinesoftware.rmrs.shared.dto.ProposalErrandReport;
import org.alfresco.service.namespace.QName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class ProposalErrandReportDocumentProvider extends AbstractErrandReportDocumentProvider {
    @Autowired
    private HasChangesToRequirementsAspectProvider hasChangesToRequirementsAspectProvider;

    @PostConstruct
    private void postConstruct() {
        documentProviderFactory.register(RmrsAlfrescoTypes.TYPE_PROPOSAL_ERRAND_REPORT, this);
        typeFactory.register(ProposalErrandReport.KIND, RmrsAlfrescoTypes.TYPE_PROPOSAL_ERRAND_REPORT);
//        typeFactory.registerSearchable(RmrsAlfrescoTypes.TYPE_PROPOSAL_ERRAND_REPORT);
    }

    @Override
    public Document createInstance(QName type) {
        return new ProposalErrandReport();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void fillDocument(Persistent persistent, RepositoryService.Node properties) {
        super.fillDocument(persistent, properties);
        if(persistent instanceof ProposalErrandReport) {
            final ProposalErrandReport report = (ProposalErrandReport) persistent;
            hasChangesToRequirementsAspectProvider.fillDocument(persistent, properties);
            report.accepted = properties.get(RmrsAlfrescoTypes.PROP_ACCEPTED);
        }
    }

    @Override
    public void fillNode(RepositoryService.Node node, Persistent persistent) {
        super.fillNode(node, persistent);
        if(persistent instanceof ProposalErrandReport) {
            final ProposalErrandReport report = (ProposalErrandReport) persistent;
            hasChangesToRequirementsAspectProvider.fillNode(node, persistent);
            node.add(RmrsAlfrescoTypes.PROP_ACCEPTED, report.accepted);
        }
    }

    @Override
    public void fillVersion(RepositoryService.Node version, RepositoryService.Node node) {
        super.fillVersion(version, node);
        version.add(RmrsAlfrescoTypes.PROP_ACCEPTED, node.get(RmrsAlfrescoTypes.PROP_ACCEPTED));
        hasChangesToRequirementsAspectProvider.fillVersion(version, node);
    }
}
