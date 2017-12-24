package com.firstlinesoftware.rmrs.server.providers;

import com.firstlinesoftware.base.server.services.RepositoryService;
import com.firstlinesoftware.base.shared.dto.Persistent;
import com.firstlinesoftware.ecm.shared.dto.Document;
import com.firstlinesoftware.rmrs.server.RmrsAlfrescoTypes;
import com.firstlinesoftware.rmrs.shared.dto.CircularLetter;
import com.firstlinesoftware.route.server.providers.impl.AbstractRouteProvider;
import org.alfresco.service.namespace.QName;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.List;

@Component
public class CircularLetterProvider extends AbstractRouteProvider {

    @PostConstruct
    private void postConstruct() {
        documentProviderFactory.register(RmrsAlfrescoTypes.TYPE_CIRCULAR_LETTER, this);
        typeFactory.register(CircularLetter.KIND, RmrsAlfrescoTypes.TYPE_CIRCULAR_LETTER);
        propertyMapper.register(RmrsAlfrescoTypes.PREFIX_RMRS, CircularLetter.class, "approvePosition", "approvedRequirements");
    }

    @Override
    public void fillNode(RepositoryService.Node node, Persistent persistent) {
        super.fillNode(node, persistent);
        if (persistent instanceof CircularLetter) {
            CircularLetter letter = (CircularLetter) persistent;
            node.add(RmrsAlfrescoTypes.PROP_APPROVE_POSITION, letter.approvePosition != null ? letter.approvePosition.id : null);
            node.add(RmrsAlfrescoTypes.PROP_COMMISSIONING, letter.commissioning);
            node.add(RmrsAlfrescoTypes.PROP_VALID_TO, letter.validTo);
            node.add(RmrsAlfrescoTypes.PROP_VALID_EXTENDED_UNTIL, letter.validExtendedUntil);
            node.add(RmrsAlfrescoTypes.PROP_REFERRED_EN, letter.referred_en);
            node.add(RmrsAlfrescoTypes.PROP_CONTENT_EN, letter.content_en);
            node.add(RmrsAlfrescoTypes.PROP_OBSERVABLE_EN, letter.observable_en);
            node.add(RmrsAlfrescoTypes.PROP_COMMISSIONING_EN, letter.commissioning_en);
            node.add(RmrsAlfrescoTypes.PROP_VALID_TO_EN, letter.validTo_en);
            node.add(RmrsAlfrescoTypes.PROP_VALID_EXTENDED_UNTIL_EN, letter.validExtendedUntil_en);
            node.add(RmrsAlfrescoTypes.PROP_ACTION_EN, letter.action_en);
            node.add(RmrsAlfrescoTypes.PROP_APPROVED_REQUIREMENTS, (Serializable) getIds(letter.approvedRequirements));
        }
    }

    @Override
    public void fillDocument(Persistent persistent, RepositoryService.Node properties) {
        super.fillDocument(persistent, properties);
        if (persistent instanceof CircularLetter) {
            CircularLetter letter = (CircularLetter) persistent;
            letter.approvePosition = orgstructService.getPosition ((String) properties.get(RmrsAlfrescoTypes.PROP_APPROVE_POSITION));
            letter.commissioning = properties.get(RmrsAlfrescoTypes.PROP_COMMISSIONING);
            letter.validTo = properties.get(RmrsAlfrescoTypes.PROP_VALID_TO);
            letter.validExtendedUntil = properties.get(RmrsAlfrescoTypes.PROP_VALID_EXTENDED_UNTIL);
            letter.referred_en = properties.get(RmrsAlfrescoTypes.PROP_REFERRED_EN);
            letter.content_en = properties.get(RmrsAlfrescoTypes.PROP_CONTENT_EN);
            letter.observable_en = properties.get(RmrsAlfrescoTypes.PROP_OBSERVABLE_EN);
            letter.commissioning_en = properties.get(RmrsAlfrescoTypes.PROP_COMMISSIONING_EN);
            letter.validTo_en = properties.get(RmrsAlfrescoTypes.PROP_VALID_TO_EN);
            letter.validExtendedUntil_en = properties.get(RmrsAlfrescoTypes.PROP_VALID_EXTENDED_UNTIL_EN);
            letter.action_en = properties.get(RmrsAlfrescoTypes.PROP_ACTION_EN);
            letter.approvedRequirements = documentService.getDocumentsByIds((List<String>) properties.get(RmrsAlfrescoTypes.PROP_APPROVED_REQUIREMENTS));
        }
    }

    @Override
    public void fillVersion(RepositoryService.Node version, RepositoryService.Node node) {
        super.fillVersion(version, node);
        version.add(RmrsAlfrescoTypes.PROP_CONTENT, node.get(RmrsAlfrescoTypes.PROP_CONTENT));
        version.add(RmrsAlfrescoTypes.PROP_OBSERVABLE, node.get(RmrsAlfrescoTypes.PROP_OBSERVABLE));
        version.add(RmrsAlfrescoTypes.PROP_COMMISSIONING, node.get(RmrsAlfrescoTypes.PROP_COMMISSIONING));
        version.add(RmrsAlfrescoTypes.PROP_VALID_TO, node.get(RmrsAlfrescoTypes.PROP_VALID_TO));
        version.add(RmrsAlfrescoTypes.PROP_VALID_EXTENDED_UNTIL, node.get(RmrsAlfrescoTypes.PROP_VALID_EXTENDED_UNTIL));
        version.add(RmrsAlfrescoTypes.PROP_ACTION, node.get(RmrsAlfrescoTypes.PROP_ACTION));
        version.add(RmrsAlfrescoTypes.PROP_REFERRED_EN, node.get(RmrsAlfrescoTypes.PROP_REFERRED_EN));
        version.add(RmrsAlfrescoTypes.PROP_CONTENT_EN, node.get(RmrsAlfrescoTypes.PROP_CONTENT_EN));
        version.add(RmrsAlfrescoTypes.PROP_OBSERVABLE_EN, node.get(RmrsAlfrescoTypes.PROP_OBSERVABLE_EN));
        version.add(RmrsAlfrescoTypes.PROP_COMMISSIONING_EN, node.get(RmrsAlfrescoTypes.PROP_COMMISSIONING_EN));
        version.add(RmrsAlfrescoTypes.PROP_VALID_TO_EN, node.get(RmrsAlfrescoTypes.PROP_VALID_TO_EN));
        version.add(RmrsAlfrescoTypes.PROP_VALID_EXTENDED_UNTIL_EN, node.get(RmrsAlfrescoTypes.PROP_VALID_EXTENDED_UNTIL_EN));
        version.add(RmrsAlfrescoTypes.PROP_ACTION_EN, node.get(RmrsAlfrescoTypes.PROP_ACTION_EN));
        version.add(RmrsAlfrescoTypes.PROP_APPROVE_POSITION, node.get(RmrsAlfrescoTypes.PROP_APPROVE_POSITION));
        version.add(RmrsAlfrescoTypes.PROP_CHANGED_CONTENT, node.get(RmrsAlfrescoTypes.PROP_CHANGED_CONTENT));
        version.add(RmrsAlfrescoTypes.PROP_APPROVED_REQUIREMENTS, node.get(RmrsAlfrescoTypes.PROP_APPROVED_REQUIREMENTS));
    }

    @Override
    public Document createInstance(QName type) {
        return new CircularLetter();
    }
}
