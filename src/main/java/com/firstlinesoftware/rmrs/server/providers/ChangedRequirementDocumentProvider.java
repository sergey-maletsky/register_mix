package com.firstlinesoftware.rmrs.server.providers;

import com.firstlinesoftware.base.client.exceptions.ValidationException;
import com.firstlinesoftware.base.server.services.RepositoryService;
import com.firstlinesoftware.base.server.utils.Messages;
import com.firstlinesoftware.base.shared.dto.Persistent;
import com.firstlinesoftware.ecm.shared.dto.Document;
import com.firstlinesoftware.exec.server.providers.impl.AbstractErrandDocumentProvider;
import com.firstlinesoftware.rmrs.server.RmrsAlfrescoTypes;
import com.firstlinesoftware.rmrs.shared.dto.ChangeRequirementErrand;
import org.alfresco.service.namespace.QName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class ChangedRequirementDocumentProvider extends AbstractErrandDocumentProvider {
    @Autowired
    private HasChangesToRequirementsAspectProvider hasChangesToRequirementsAspectProvider;

    @Autowired
    private Messages messages;


    @PostConstruct
    private void postConstruct() {
        documentProviderFactory.register(RmrsAlfrescoTypes.TYPE_CHANGE_REQUIREMENT, this);
        typeFactory.register(ChangeRequirementErrand.KIND, RmrsAlfrescoTypes.TYPE_CHANGE_REQUIREMENT);
        typeFactory.registerSearchable(RmrsAlfrescoTypes.TYPE_CHANGE_REQUIREMENT);
    }

    @Override
    public Document createInstance(QName type) {
        return new ChangeRequirementErrand();
    }

    @Override
    public void validate(Document document) {
        super.validate(document);
        if(document instanceof ChangeRequirementErrand) {
            final ChangeRequirementErrand errand = (ChangeRequirementErrand) document;
            if((errand.createNew == null || errand.createNew.isEmpty()) && (errand.modifyExisting == null || errand.modifyExisting.isEmpty())) {
                throw new ValidationException(messages.getMessage("change.errand.empty"));
            }

        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void fillDocument(Persistent persistent, RepositoryService.Node properties) {
        super.fillDocument(persistent, properties);
        hasChangesToRequirementsAspectProvider.fillDocument(persistent, properties);
    }

    @Override
    public void fillNode(RepositoryService.Node node, Persistent persistent) {
        super.fillNode(node, persistent);
        hasChangesToRequirementsAspectProvider.fillNode(node, persistent);
    }

    @Override
    public void fillVersion(RepositoryService.Node version, RepositoryService.Node node) {
        super.fillVersion(version, node);
        hasChangesToRequirementsAspectProvider.fillVersion(version, node);
    }
}
