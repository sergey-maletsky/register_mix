package com.firstlinesoftware.rmrs.server.providers;

import com.firstlinesoftware.base.server.BaseAlfrescoTypes;
import com.firstlinesoftware.base.server.services.RepositoryService;
import com.firstlinesoftware.base.shared.dto.DateRange;
import com.firstlinesoftware.base.shared.dto.Persistent;
import com.firstlinesoftware.base.shared.dto.SearchCriteria;
import com.firstlinesoftware.base.shared.utils.CriteriaBuilder;
import com.firstlinesoftware.ecm.server.services.FolderService;
import com.firstlinesoftware.ecm.server.services.impl.events.DocumentEventSender;
import com.firstlinesoftware.ecm.shared.dto.Document;
import com.firstlinesoftware.exec.server.ExecAlfrescoTypes;
import com.firstlinesoftware.orgstruct.shared.dto.OrgstructureItem;
import com.firstlinesoftware.rmrs.server.RmrsAlfrescoTypes;
import com.firstlinesoftware.rmrs.shared.dto.Requirement;
import com.firstlinesoftware.route.server.providers.impl.AbstractRouteProvider;
import com.google.common.base.Objects;
import org.alfresco.service.namespace.QName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.*;

@Component
public class RequirementDocumentProvider extends AbstractRouteProvider {

    public static final Comparator<Requirement> SORT_BY_FULL_PATH = new Comparator<Requirement>() {
        @Override
        public int compare(Requirement o1, Requirement o2) {
            return o1.fullPath.compareTo(o2.fullPath);
        }
    };
    public static final Comparator<RepositoryService.Node> COMPARE_ORDER = new Comparator<RepositoryService.Node>() {
        @Override
        public int compare(RepositoryService.Node o1, RepositoryService.Node o2) {
            final Integer p1 = o1.get(RmrsAlfrescoTypes.PROP_ORDER);
            final Integer p2 = o2.get(RmrsAlfrescoTypes.PROP_ORDER);
            return p1 != null && p2 != null ? p1.compareTo(p2) : 0;
        }
    };
    @Autowired
    private FolderService folderService;
    @Autowired
    private HasMultiLanguageFileAspectProvider hasMultiLanguageFileAspectProvider;

    @PostConstruct
    private void postConstruct() {
        documentProviderFactory.register(RmrsAlfrescoTypes.TYPE_REQUIREMENT, this);
        typeFactory.register(Requirement.KIND, RmrsAlfrescoTypes.TYPE_REQUIREMENT);
        propertyMapper.register(RmrsAlfrescoTypes.PREFIX_RMRS, Requirement.class, "tags", "parent", "leaf", "effective");
    }

    @Override
    public Requirement createInstance(QName type) {
        return new Requirement();
    }

    @Override
    public void fillDocument(Persistent persistent, RepositoryService.Node properties) {
        super.fillDocument(persistent, properties);
        hasMultiLanguageFileAspectProvider.fillDocument(persistent, properties);
        if (persistent instanceof Requirement) {
            final Requirement requirement = (Requirement) persistent;
            requirement.tags = (List<String>) properties.get(RmrsAlfrescoTypes.PROP_TAGS);
            requirement.isLeaf = (Boolean) properties.get(RmrsAlfrescoTypes.PROP_LEAF);
            requirement.leafHeader = (Boolean) properties.get(RmrsAlfrescoTypes.PROP_LEAF_HEADER);
            final String parent = (String) properties.get(RmrsAlfrescoTypes.PROP_PARENT);
            final String parentNumber = (String) properties.get(RmrsAlfrescoTypes.PROP_PARENT_NUMBER);
            if (parent != null) {
                requirement.parent = new Requirement();
                requirement.parent.id = parent;
                requirement.parent.number = parentNumber;
            }

            final Date begin = (Date) properties.get(ExecAlfrescoTypes.PROP_BEGIN);
            final Date end = (Date) properties.get(ExecAlfrescoTypes.PROP_END);
            if (begin != null || end != null) {
                requirement.effective = new DateRange(begin, end);
            }
            requirement.fullPath = properties.get(RmrsAlfrescoTypes.PROP_FULL_PATH);
        }
    }

    @Override
    public void fillNode(RepositoryService.Node node, Persistent dto) {
        super.fillNode(node, dto);
        hasMultiLanguageFileAspectProvider.fillNode(node, dto);
        if (dto instanceof Requirement) {
            final Requirement requirement = (Requirement) dto;
            node.add(RmrsAlfrescoTypes.PROP_PARENT, requirement.parent != null ? requirement.parent.id : null);
            node.add(RmrsAlfrescoTypes.PROP_PARENT_NUMBER, requirement.parent != null ? requirement.parent.number : null);
            node.add(RmrsAlfrescoTypes.PROP_TAGS, (Serializable) requirement.tags);
            if (dto.id == null) {
                node.add(RmrsAlfrescoTypes.PROP_LEAF, true);
                node.add(RmrsAlfrescoTypes.PROP_LEAF_HEADER, true);
            } else {
                node.add(RmrsAlfrescoTypes.PROP_LEAF, requirement.isLeaf);
                node.add(RmrsAlfrescoTypes.PROP_LEAF_HEADER, requirement.leafHeader);
            }
            if (requirement.effective != null) {
                node.add(ExecAlfrescoTypes.PROP_BEGIN, requirement.effective.min);
                node.add(ExecAlfrescoTypes.PROP_END, requirement.effective.max);
            }
            if (requirement.order == null) {
                node.add(RmrsAlfrescoTypes.PROP_ORDER, getLast(requirement.parent));
            }
            if (requirement.parent != null) {
                final String newPath = getFullPath(requirement);
                node.add(RmrsAlfrescoTypes.PROP_FULL_PATH, newPath);
            }
        }
    }

    @Override
    public void fillAssoc(Document document, List<RepositoryService.Node> assocs, Map<String, ? extends OrgstructureItem> positions) {
        super.fillAssoc(document, assocs, positions);
        hasMultiLanguageFileAspectProvider.fillAssoc(document, assocs, positions);
        if (document instanceof Requirement) {
            final Requirement requirement = (Requirement) document;
            if (requirement.parent != null) {
                requirement.parent = documentService.getProperties(requirement.parent.id);
            }
        }
    }

    @Override
    public void fillVersion(RepositoryService.Node version, RepositoryService.Node node) {
        super.fillVersion(version, node);
        hasMultiLanguageFileAspectProvider.fillVersion(version, node);
        version.add(RmrsAlfrescoTypes.PROP_ORDER, node.get(RmrsAlfrescoTypes.PROP_ORDER));
        version.add(RmrsAlfrescoTypes.PROP_PARENT, node.get(RmrsAlfrescoTypes.PROP_PARENT));
        version.add(RmrsAlfrescoTypes.PROP_PARENT_NUMBER, node.get(RmrsAlfrescoTypes.PROP_PARENT_NUMBER));
        version.add(RmrsAlfrescoTypes.PROP_PART, node.get(RmrsAlfrescoTypes.PROP_PART));
        version.add(RmrsAlfrescoTypes.PROP_VOLUME, node.get(RmrsAlfrescoTypes.PROP_VOLUME));
        version.add(RmrsAlfrescoTypes.PROP_EXPIRED, node.get(RmrsAlfrescoTypes.PROP_EXPIRED));
        version.add(RmrsAlfrescoTypes.PROP_HEADER, node.get(RmrsAlfrescoTypes.PROP_HEADER));
        version.add(RmrsAlfrescoTypes.PROP_LEAF, node.get(RmrsAlfrescoTypes.PROP_LEAF));
        version.add(RmrsAlfrescoTypes.PROP_LEAF_HEADER, node.get(RmrsAlfrescoTypes.PROP_LEAF_HEADER));
        version.add(RmrsAlfrescoTypes.PROP_TAGS, node.get(RmrsAlfrescoTypes.PROP_TAGS));
        version.add(RmrsAlfrescoTypes.PROP_CIRCULAR_LETTER, node.get(RmrsAlfrescoTypes.PROP_CIRCULAR_LETTER));
        version.add(ExecAlfrescoTypes.PROP_BEGIN, node.get(ExecAlfrescoTypes.PROP_BEGIN));
        version.add(ExecAlfrescoTypes.PROP_END, node.get(ExecAlfrescoTypes.PROP_END));
    }

    @Override
    public void postUpdate(Document document) {
//        super.postUpdate(document);
        hasMultiLanguageFileAspectProvider.postUpdate(document);
        if (document instanceof Requirement) {
            final Requirement requirement = (Requirement) document;
            updateParent(requirement);
            updateRelatedDocuments(requirement, requirement.id);
            updateChildrenPaths(requirement.id);
            updateOrder(requirement, requirement.id);
        }
    }

    @Override
    public void postCreate(Persistent persistent, String id) {
        super.postCreate(persistent, id);
        hasMultiLanguageFileAspectProvider.postCreate(persistent, id);
        if (persistent instanceof Requirement) {
            updateParent((Requirement) persistent);
            updateOrder((Requirement) persistent, id);
        }
    }

    private void updateChildrenPaths(String id) {
        final String parentPath = (String) repositoryService.getProperty(id, RmrsAlfrescoTypes.PROP_FULL_PATH);
        if(parentPath != null) {
            final List<String> wrongParent = new ArrayList<>();
            for (Document document : documentService.getDocuments(id)) {
                if (document instanceof Requirement) {
                    final String fullPath = ((Requirement) document).fullPath;
                    if(fullPath == null) {//hmmmm...
                        documentService.setProperty(document.id, RmrsAlfrescoTypes.PROP_FULL_PATH, getFullPath((Requirement) document));
                        wrongParent.add(document.id);
                    } else if (!fullPath.startsWith(parentPath)) {
                        wrongParent.add(fullPath + '.' + document.id);
                        documentService.setProperty(document.id, RmrsAlfrescoTypes.PROP_FULL_PATH, getFullPath((Requirement) document));
                    }
                }
            }
            if (!wrongParent.isEmpty()) {
                final CriteriaBuilder<SearchCriteria> builder = new CriteriaBuilder<>();
                for (String c : wrongParent) {
                    builder.addShouldHave("rmrs:fullPath", c + '*');
                }
                final List<Requirement> children = documentService.search(builder.build());
                Collections.sort(children, SORT_BY_FULL_PATH);
                for (Requirement child : children) {
                    documentService.setProperty(child.id, RmrsAlfrescoTypes.PROP_FULL_PATH, getFullPath(child));
                }
            }
        }
    }

    private String getFullPath(Requirement requirement) {
        final String parentPath = requirement.parent.fullPath != null ? requirement.parent.fullPath : (String) repositoryService.getProperty(requirement.parent.id, RmrsAlfrescoTypes.PROP_FULL_PATH);
        return (parentPath != null ? parentPath + '.' : "") + requirement.parent.id;
    }

    private Integer getLast(Requirement parent) {
        int count = 0;
        for (RepositoryService.Node child : getSiblings(parent)) {
            if (RmrsAlfrescoTypes.TYPE_REQUIREMENT.equals(child.type)) {
                count++;
            }
        }
        return count + 1;
    }

    private void updateOrder(Requirement requirement, String id) {
        final List<RepositoryService.Node> siblings = getSiblings(requirement.parent);
        Collections.sort(siblings, COMPARE_ORDER);
        int current = requirement.order != null ? requirement.order : Integer.MAX_VALUE;
        for (RepositoryService.Node node : siblings) {
            final Integer p1 = node.<Integer>get(RmrsAlfrescoTypes.PROP_ORDER);
            if (RmrsAlfrescoTypes.TYPE_REQUIREMENT.equals(node.type) && Objects.equal(p1, current) && !id.equals(node.getId())) {
                documentService.setProperty(node.getId(), RmrsAlfrescoTypes.PROP_ORDER, ++current);
            }
        }

    }

    private List<RepositoryService.Node> getSiblings(Requirement parent) {
        final String p = parent != null ? parent.id : folderService.getFolderId(RequirementsFolderProvider.FOLDER);
        return repositoryService.getChildrenProperties(p, BaseAlfrescoTypes.ASSOC_CONTAINS, true);
    }

    private void updateParent(Requirement requirement) {
        final RepositoryService.Node parent = requirement.parent != null ? repositoryService.getProperties(requirement.parent.id) : null;
        if (parent != null) {
            final Serializable leaf = parent.get(RmrsAlfrescoTypes.PROP_LEAF);
            if (Boolean.TRUE.equals(leaf)) {
                documentService.setProperty(requirement.parent.id, RmrsAlfrescoTypes.PROP_LEAF, false);
            }
            if (Boolean.TRUE.equals(requirement.header)) {
                final Serializable leafHeader = Boolean.TRUE.equals(leaf) ? true : parent.get(RmrsAlfrescoTypes.PROP_LEAF_HEADER);
                if (Boolean.TRUE.equals(leafHeader)) {
                    documentService.setProperty(requirement.parent.id, RmrsAlfrescoTypes.PROP_LEAF_HEADER, false);
                }
            }
            final String parentOfParent = (String) parent.get(RmrsAlfrescoTypes.PROP_PARENT);
            DocumentEventSender.sendFolderItemAddedEvent(requirement.parent.id, parentOfParent != null ? parentOfParent : RequirementsFolderProvider.FOLDER);
        }
    }
}
