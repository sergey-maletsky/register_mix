package com.firstlinesoftware.rmrs.server.services;

import com.firstlinesoftware.base.client.models.NavigatorModel;
import com.firstlinesoftware.base.server.BaseAlfrescoTypes;
import com.firstlinesoftware.base.server.exceptions.NonLoggableServerException;
import com.firstlinesoftware.base.server.services.RepositoryService;
import com.firstlinesoftware.base.server.utils.Messages;
import com.firstlinesoftware.base.shared.dto.DateRange;
import com.firstlinesoftware.base.shared.dto.SearchCriteria;
import com.firstlinesoftware.base.shared.utils.CriteriaBuilder;
import com.firstlinesoftware.base.shared.utils.DateUtils;
import com.firstlinesoftware.ecm.server.EcmAlfrescoTypes;
import com.firstlinesoftware.ecm.server.services.DocumentService;
import com.firstlinesoftware.ecm.server.services.impl.events.DocumentEventSender;
import com.firstlinesoftware.ecm.shared.dto.CompositeDocument;
import com.firstlinesoftware.ecm.shared.dto.Document;
import com.firstlinesoftware.ecm.shared.dto.DocumentHistoryItem;
import com.firstlinesoftware.ecm.shared.dto.RelatedDocument;
import com.firstlinesoftware.exec.server.services.process.ErrandProcess;
import com.firstlinesoftware.rmrs.server.providers.RequirementsFolderProvider;
import com.firstlinesoftware.rmrs.shared.dto.ChangeRequirementErrand;
import com.firstlinesoftware.rmrs.shared.dto.Requirement;
import com.firstlinesoftware.rmrs.shared.dto.RmrsDirectories;
import com.firstlinesoftware.rmrs.shared.dto.RmrsTasks;
import com.firstlinesoftware.route.server.RouteAlfrescoTypes;
import com.firstlinesoftware.route.server.services.DocumentRouteService;
import com.firstlinesoftware.route.shared.dto.AbstractRoute;
import com.firstlinesoftware.route.shared.dto.RouteState;
import com.google.common.base.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RequirementsService {
    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private DocumentService documentService;

    @Autowired
    private ErrandProcess errandProcess;

    @Autowired
    protected Messages messages;

    @Autowired
    private DocumentRouteService documentRouteService;


    public String getResponsible(String id) {
        while (id != null) {
            final Requirement p = documentService.getProperties(id);
            if (p.responsible != null) {
                return p.responsible.id;
            }
            id = p.parent != null ? p.parent.id : null;
        }
        return null;

    }

    public void createRequirementByErrand(String errandId, String reqId, String template) {
        if (template != null) {
            final ChangeRequirementErrand errand = documentService.getProperties(errandId);
            if (errand.hasCreateNew()) {
                for (Requirement t : errand.createNew) {
                    if (Objects.equal(template, t.id)) {
                        t.id = reqId;
                        documentService.updateProperties(errand, null);
                        final Requirement created = documentService.get(reqId);
                        final RelatedDocument createdBasedOn = new RelatedDocument(RmrsDirectories.RELATION_TYPE_BASED_ON, true, errandId);
                        if (created.relatedDocuments == null) {
                            created.relatedDocuments = Collections.singletonList(createdBasedOn);
                        } else {
                            created.relatedDocuments.add(createdBasedOn);
                        }
                        documentService.update(created);
                        break;
                    }
                }
            }
        }
        updateRequirementByErrand(errandId);
    }

    public String update(Requirement d, String errandId) {
        final Requirement existing = documentService.get(d.id);
        d.routeState = new RouteState();
        d.routeState.round = 0;
        if (Document.DOCUMENT_LIFECYCLE_DRAFT.equals(existing.lifecycle)) {
            documentService.setProperty(d.id, EcmAlfrescoTypes.PROP_AUTHOR, d.author.id);//?
            if (d.parent != null) {
                documentService.move(d, d.parent.id);
            }
            if (errandId != null && !CompositeDocument.containsId(existing.relatedDocuments, errandId)) {
                final RelatedDocument updatedBasedOn = new RelatedDocument(RmrsDirectories.RELATION_TYPE_BASED_ON, true, errandId);
                if (d.relatedDocuments == null) {
                    d.relatedDocuments = Collections.singletonList(updatedBasedOn);
                } else {
                    d.relatedDocuments.add(updatedBasedOn);
                }
            }
            documentService.update(d, new DocumentHistoryItem(messages.getMessage("requirement.updated"), null));
            return d.id;
        } else {
            if (errandId != null && !CompositeDocument.containsId(existing.relatedDocuments, errandId)) {
                final RelatedDocument updatedBasedOn = new RelatedDocument(RmrsDirectories.RELATION_TYPE_BASED_ON, true, errandId);
                if (existing.relatedDocuments == null) {
                    existing.relatedDocuments = Collections.singletonList(updatedBasedOn);
                } else {
                    existing.relatedDocuments.add(updatedBasedOn);
                }
                documentService.update(existing);
            }
//            documentService.resetPermissions(d.id, false);
            d.lifecycle = Document.DOCUMENT_LIFECYCLE_DRAFT;
            if (d.effective == null || d.effective.min == null) {
                throw new NonLoggableServerException(messages.getMessage("requirement.effective.required"));
            }
            final RelatedDocument replace = new RelatedDocument(RmrsDirectories.RELATION_TYPE_REPLACE_BY, true, existing.id);
            if (d.relatedDocuments == null) {
                d.relatedDocuments = Collections.singletonList(replace);
            } else {
                d.relatedDocuments.add(replace);
            }
            d.attachedFiles = null;
            d.order = existing.order + 1;
            return documentService.create(d.parent != null ? d.parent.id : RequirementsFolderProvider.FOLDER, d);
        }
    }

    public void updateRequirementByErrand(String errandId) {
        final ChangeRequirementErrand updated = documentService.get(errandId);
        final Set<String> completed = updated.collectCompleted();

        if (updated.hasCreateNew()) {
            for (Requirement r : updated.createNew) {
                if (!completed.contains(r.id)) {
                    return;
                }
            }
        }
        if (updated.hasModifyExisting()) {
            for (Requirement r : updated.modifyExisting) {
                if (!completed.contains(r.id)) {
                    return;
                }
            }
        }
        errandProcess.completeErrand(updated, null, true);
    }

    public void checkExistingDraftDates(Requirement d) {
        final Requirement existing = documentService.get(d.id);
        if (!Document.DOCUMENT_LIFECYCLE_DRAFT.equals(existing.lifecycle) && d.relatedDocuments != null) {
            for (RelatedDocument r : d.relatedDocuments) {
                if (RmrsDirectories.RELATION_TYPE_REPLACE_BY.equals(r.relationType) && r.document instanceof Requirement) {
                    if (Objects.equal(d.effective, ((Requirement) r.document).effective)) {
                        throw new NonLoggableServerException(messages.getMessage("requirement.version.conflict"));
                    }
                }
            }
        }
    }

    public void reject(List<String> ids) {
        for (String id : ids) {
            documentRouteService.returnToAuthor(id);
            DocumentEventSender.sendFolderItemRemovedEvent(id, RmrsTasks.REQ_ON_SIGNING);
            DocumentEventSender.sendFolderItemAddedEvent(id, RmrsTasks.REQ_REJECTED);
        }
    }

    public void sendToSigning(List<String> ids) {
        documentService.setProperty(ids, EcmAlfrescoTypes.PROP_LIFECYCLE, AbstractRoute.LIFECYCLE_ONSIGNING);
        for (String id : ids) {
            DocumentEventSender.sendFolderItemRemovedEvent(id, RmrsTasks.REQ_APPROVED);
            DocumentEventSender.sendFolderItemAddedEvent(id, RmrsTasks.REQ_ON_SIGNING);
        }
    }

    public void sign(List<String> ids) {
        documentService.setProperty(ids, EcmAlfrescoTypes.PROP_LIFECYCLE, Requirement.LIFECYCLE_SIGNED);
        documentService.setProperty(ids, RouteAlfrescoTypes.PROP_SIGNING_DATE, new Date());
        for (String id : ids) {
            Requirement d = documentService.get(id);
            syncEffectiveDates(d, d, true);
            documentService.updateProperties(d, null);
            DocumentEventSender.sendFolderItemRemovedEvent(id, RmrsTasks.REQ_ON_SIGNING);
            DocumentEventSender.sendFolderItemAddedEvent(id, RmrsTasks.REQ_SIGNED);
        }
    }


    private void syncEffectiveDates(Requirement signed, Requirement d, Boolean direction) {
        if (d.relatedDocuments != null) {
            for (RelatedDocument r : d.relatedDocuments) {
                if (!r.id.equals(signed.id)
                        && RmrsDirectories.RELATION_TYPE_REPLACE_BY.equals(r.relationType)
                        && r.document instanceof Requirement
                        && direction.equals(r.parent)
                        && Requirement.LIFECYCLE_SIGNED.equals(r.document.lifecycle)
                        ) {
                    final Requirement related = documentService.get(r.id);
                    makeNotIntersecting(signed, related);
                    documentService.updateProperties(related, null);
                    syncEffectiveDates(signed, related, false);
                }
            }
        }
    }

    private void makeNotIntersecting(Requirement d, Requirement r) {
        assert d.effective != null && d.effective.min != null;
        if (r.effective == null) {
            r.effective = new DateRange(null, DateUtils.addDays(d.effective.min, -1));
        } else if (r.effective.min != null) {
            if (d.effective.min.before(r.effective.min)) {
                if ((d.effective.max == null || d.effective.max.after(r.effective.min))) {
                    d.effective.max = DateUtils.addDays(r.effective.min, -1);
                }
            } else if (r.effective.max == null || r.effective.max.after(d.effective.min)) {
                r.effective.max = DateUtils.addDays(d.effective.min, -1);
            }
        } else {
            assert r.effective.max != null;
            if (d.effective.min.before(r.effective.max)) {
                d.effective.min = DateUtils.addDays(r.effective.max, -1);
            }
        }
    }

    public List<Requirement> getByFolderWithAttachVersions(String id, boolean showRecursive, boolean onlyHeaders) {
        if (showRecursive) {
            Requirement parent = documentService.getProperties(id);
            if (parent != null) {
                final CriteriaBuilder<SearchCriteria> builder = new CriteriaBuilder<>().addMustHave("rmrs:fullPath", (parent.fullPath != null ? parent.fullPath + '.' : "") + parent.id + '*');
                final List<Requirement> result = documentService.search(builder.build());
                return replaceAttachmentsWithVersions(result);
            }
        }
        final CriteriaBuilder<SearchCriteria> builder = new CriteriaBuilder<>();
        if (Objects.equal(id, NavigatorModel.ROOT.id) || id == null) {
            builder.addMustBeNull("rmrs:parent").setType("rmrs:requirement");
        } else {
            builder.addMustHave("rmrs:parent", id);
        }
        if (onlyHeaders) {
            builder.addMustHave("rmrs:header", true);
        }
        return replaceAttachmentsWithVersions(documentService.<Requirement>search(builder.build()));
    }

    private List<Requirement> replaceAttachmentsWithVersions(List<Requirement> result) {
        final Set<String> attachments = new HashSet<>();
        for (Requirement r : result) {
            if (r.russian != null) {
                attachments.add(r.russian.id);
            }
            if (r.english != null) {
                attachments.add(r.english.id);
            }
        }
        final Map<String, String> attachmentVersions = new HashMap<>();
        for (RepositoryService.Node node : repositoryService.getPropertiesUnsafe(attachments)) {
            attachmentVersions.put(node.getId(), node.<String>get(BaseAlfrescoTypes.PROP_CURRENT_VERSION));
        }
        for (Requirement r : result) {
            if (r.russian != null) {
                r.russian.id = attachmentVersions.get(r.russian.id);
            }
            if (r.english != null) {
                r.english.id = attachmentVersions.get(r.english.id);
            }
        }
        return result;
    }

    public void fixAuthorInChildren(Requirement p) {
        final CriteriaBuilder<SearchCriteria> builder = new CriteriaBuilder<>().addMustHave("rmrs:fullPath", (p.fullPath != null ? p.fullPath + '.' : "") + p.id + '*');
        final List<String> children = repositoryService.search(builder.build());
        documentService.setProperty(children, EcmAlfrescoTypes.PROP_AUTHOR, p.responsible.id);
        documentService.setProperty(p.id, EcmAlfrescoTypes.PROP_AUTHOR, p.responsible.id);
    }
}
