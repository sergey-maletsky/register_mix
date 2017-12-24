package com.firstlinesoftware.rmrs.server.providers;

import com.firstlinesoftware.base.server.BaseAlfrescoTypes;
import com.firstlinesoftware.base.server.services.RepositoryService;
import com.firstlinesoftware.base.shared.dto.Persistent;
import com.firstlinesoftware.ecm.shared.dto.Document;
import com.firstlinesoftware.ecm.shared.dto.RelatedDocument;
import com.firstlinesoftware.exec.server.providers.impl.HasControlDateAspectProvider;
import com.firstlinesoftware.orgstruct.shared.dto.OrgstructureItem;
import com.firstlinesoftware.rmrs.server.RmrsAlfrescoTypes;
import com.firstlinesoftware.rmrs.shared.dto.Proposal;
import com.firstlinesoftware.rmrs.shared.dto.Requirement;
import com.firstlinesoftware.route.server.providers.impl.AbstractRouteProvider;
import com.firstlinesoftware.route.server.providers.impl.HasErrandsProvider;
import org.alfresco.service.namespace.QName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Component
public class ProposalDocumentProvider extends AbstractRouteProvider {
    @Autowired
    private HasControlDateAspectProvider hasControlDateAspectProvider;
    @Autowired
    private HasMultiLanguageFileAspectProvider hasMultiLanguageFileAspectProvider;
    @Autowired
    private HasErrandsProvider hasErrandsProvider;

    @PostConstruct
    private void postConstruct() {
        documentProviderFactory.register(RmrsAlfrescoTypes.TYPE_PROPOSAL, this);
        typeFactory.register(Proposal.KIND, RmrsAlfrescoTypes.TYPE_PROPOSAL);

    }

    @Override
    public void fillNode(RepositoryService.Node node, Persistent persistent) {
        super.fillNode(node, persistent);
        hasControlDateAspectProvider.fillNode(node, persistent);
        hasMultiLanguageFileAspectProvider.fillNode(node, persistent);
        if (persistent instanceof Proposal) {
            node.add(RmrsAlfrescoTypes.PROP_EXECUTIVES, (Serializable) getIds(((Proposal) persistent).executives));
            node.add(RmrsAlfrescoTypes.PROP_ERRAND_TEXT, ((Proposal) persistent).errandText);
            node.add(RmrsAlfrescoTypes.PROP_COMMENT, ((Proposal) persistent).comment);
        }
    }

    @Override
    public void fillDocument(Persistent persistent, RepositoryService.Node properties) {
        super.fillDocument(persistent, properties);
        hasControlDateAspectProvider.fillDocument(persistent, properties);
        hasMultiLanguageFileAspectProvider.fillDocument(persistent, properties);
        if (persistent instanceof Proposal) {
            ((Proposal) persistent).executives = createLightPositions((Collection<String>) properties.get(RmrsAlfrescoTypes.PROP_EXECUTIVES));
            ((Proposal) persistent).errandText = properties.get(RmrsAlfrescoTypes.PROP_ERRAND_TEXT);
            ((Proposal) persistent).comment = properties.get(RmrsAlfrescoTypes.PROP_COMMENT);
        }
    }

    @Override
    public void fillOrgstructureItems(Document document, Map<String, ? extends OrgstructureItem> prefetched) {
        super.fillOrgstructureItems(document, prefetched);
        hasErrandsProvider.fillOrgstructureItems(document, prefetched);
        if (document instanceof Proposal) {
            ((Proposal) document).executives = fillPositions(((Proposal) document).executives, prefetched);
        }
    }

    @Override
    public void getOrgstructureItems(Collection<String> ids, Document document) {
        super.getOrgstructureItems(ids, document);
        hasErrandsProvider.getOrgstructureItems(ids, document);
        if (document instanceof Proposal) {
            addIds(ids, ((Proposal) document).executives);
        }
    }

    @Override
    public void fillAssoc(Document document, List<RepositoryService.Node> assocs, Map<String, ? extends OrgstructureItem> positions) {
        super.fillAssoc(document, assocs, positions);
        hasMultiLanguageFileAspectProvider.fillAssoc(document, assocs, positions);
        hasErrandsProvider.fillAssoc(document, assocs, positions);
        if (document instanceof Proposal) {
            final Proposal proposal = (Proposal) document;
            for (final RepositoryService.Node assoc : assocs) {
                final Serializable assocType = assoc.get(BaseAlfrescoTypes.PROP_RELATION_TYPE);
                final Serializable assocDirection = assoc.get(BaseAlfrescoTypes.PROP_RELATION_DIRECTION);
                if (RmrsAlfrescoTypes.ASSOC_CHANGED_REQUIREMENTS.equals(assocType)) {
                    assert assocDirection.equals(RelatedDocument.ASSOC_DIRECTION_SOURCE);
                    if (proposal.changedRequirements == null) {
                        proposal.changedRequirements = new ArrayList<>();
                    }
                    proposal.changedRequirements.add(documentService.<Requirement>getProperties(assoc, false));
                }
            }
        }
    }

    @Override
    public void fillVersion(RepositoryService.Node version, RepositoryService.Node node) {
        super.fillVersion(version, node);
        hasControlDateAspectProvider.fillVersion(version, node);
        hasMultiLanguageFileAspectProvider.fillVersion(version, node);
        version.add(RmrsAlfrescoTypes.PROP_EXECUTIVES, node.get(RmrsAlfrescoTypes.PROP_EXECUTIVES));
        version.add(RmrsAlfrescoTypes.PROP_COMMENT, node.get(RmrsAlfrescoTypes.PROP_COMMENT));
        version.add(RmrsAlfrescoTypes.PROP_ERRAND_TEXT, node.get(RmrsAlfrescoTypes.PROP_ERRAND_TEXT));
    }

    @Override
    public void postCreate(Persistent persistent, String id) {
        super.postCreate(persistent, id);
        hasMultiLanguageFileAspectProvider.postCreate(persistent, id);
        if(persistent instanceof Proposal) {
            final List<Requirement> requirements = ((Proposal) persistent).changedRequirements;
            if(requirements != null) {
                for (Requirement requirement : requirements) {
                    repositoryService.addChild(requirement.id, id, RmrsAlfrescoTypes.ASSOC_CHANGED_REQUIREMENTS);
                }
            }
        }
    }

    @Override
    public void postUpdate(Document document) {
//        super.postUpdate(document);
        hasMultiLanguageFileAspectProvider.postUpdate(document);
        if (document instanceof Proposal) {
            updateRelatedDocuments(((Proposal) document), document.id);
        }
    }



    @Override
    public Document createInstance(QName type) {
        return new Proposal();
    }
}
