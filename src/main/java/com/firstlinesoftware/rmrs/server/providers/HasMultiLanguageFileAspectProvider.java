package com.firstlinesoftware.rmrs.server.providers;

import com.firstlinesoftware.base.server.services.RepositoryService;
import com.firstlinesoftware.base.shared.dto.AttachedFile;
import com.firstlinesoftware.base.shared.dto.Persistent;
import com.firstlinesoftware.ecm.shared.dto.CompositeDocument;
import com.firstlinesoftware.ecm.shared.dto.Document;
import com.firstlinesoftware.orgstruct.shared.dto.OrgstructureItem;
import com.firstlinesoftware.rmrs.server.RmrsAlfrescoTypes;
import com.firstlinesoftware.rmrs.shared.dto.HasMultiLanguageFile;
import com.firstlinesoftware.route.server.providers.impl.AbstractRouteProvider;
import com.google.common.base.Objects;
import org.alfresco.service.namespace.QName;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

@Component
public class HasMultiLanguageFileAspectProvider extends AbstractRouteProvider {

    public HasMultiLanguageFileAspectProvider() {
    }

    @PostConstruct
    public void init() {
        this.propertyMapper.registerExclusions(new QName[]{RmrsAlfrescoTypes.PROP_RUSSIAN, RmrsAlfrescoTypes.PROP_ENGLISH});
    }

    public void fillDocument(Persistent persistent, RepositoryService.Node properties) {
        if (persistent instanceof HasMultiLanguageFile) {
            final HasMultiLanguageFile hasMultiLanguageFile = (HasMultiLanguageFile) persistent;
            final String russian = (String) properties.get(RmrsAlfrescoTypes.PROP_RUSSIAN);
            final String english = (String) properties.get(RmrsAlfrescoTypes.PROP_ENGLISH);
            if (russian != null) {
                AttachedFile russianFile = new AttachedFile();
                russianFile.id = russian;
                hasMultiLanguageFile.setRussian(russianFile);
            }
            if (english != null) {
                AttachedFile englishFile = new AttachedFile();
                englishFile.id = english;
                hasMultiLanguageFile.setEnglish(englishFile);
            }
        }

    }

    public void fillNode(RepositoryService.Node node, Persistent persistent) {
        if (persistent instanceof HasMultiLanguageFile && persistent.getId() != null) {
            final HasMultiLanguageFile hasMultiLanguageFile = (HasMultiLanguageFile) persistent;
            //preserve old values
            node.add(RmrsAlfrescoTypes.PROP_RUSSIAN, repositoryService.getProperty(persistent.getId(), RmrsAlfrescoTypes.PROP_RUSSIAN));
            node.add(RmrsAlfrescoTypes.PROP_ENGLISH, repositoryService.getProperty(persistent.getId(), RmrsAlfrescoTypes.PROP_ENGLISH));
        }

    }

    public void fillVersion(RepositoryService.Node version, RepositoryService.Node node) {
        version.add(RmrsAlfrescoTypes.PROP_RUSSIAN, node.get(RmrsAlfrescoTypes.PROP_RUSSIAN));
        version.add(RmrsAlfrescoTypes.PROP_ENGLISH, node.get(RmrsAlfrescoTypes.PROP_ENGLISH));
    }

    @Override
    public void fillAssoc(Document document, List<RepositoryService.Node> assocs, Map<String, ? extends OrgstructureItem> positions) {
        if (document instanceof HasMultiLanguageFile) {
            if (((CompositeDocument) document).attachedFiles != null) {
                for (AttachedFile file : ((CompositeDocument) document).attachedFiles) {
                    final HasMultiLanguageFile hasMultiLanguageFile = (HasMultiLanguageFile) document;
                    if (hasMultiLanguageFile.getRussian() != null && Objects.equal(hasMultiLanguageFile.getRussian().id, file.id)) {
                        hasMultiLanguageFile.setRussian(file);
                    }
                    if (hasMultiLanguageFile.getEnglish() != null && Objects.equal(hasMultiLanguageFile.getEnglish().id, file.id)) {
                        hasMultiLanguageFile.setEnglish(file);
                    }
                }
            }
        }
    }

    @Override
    public void postCreate(Persistent persistent, String id) {
        if (persistent instanceof HasMultiLanguageFile) {
            final HasMultiLanguageFile hasMultiLanguageFile = (HasMultiLanguageFile) persistent;
            if (hasMultiLanguageFile.getRussian() != null) {
                repositoryService.setProperty(id, RmrsAlfrescoTypes.PROP_RUSSIAN, attachFile(id, hasMultiLanguageFile.getRussian()));
            }
            if (hasMultiLanguageFile.getEnglish() != null) {
                repositoryService.setProperty(id, RmrsAlfrescoTypes.PROP_ENGLISH, attachFile(id, hasMultiLanguageFile.getEnglish()));
            }
        }
    }

    @Override
    public void postUpdate(Document persistent) {
        if (persistent instanceof HasMultiLanguageFile) {
            assert persistent instanceof CompositeDocument;
            final HasMultiLanguageFile d = (HasMultiLanguageFile) persistent;
            repositoryService.setProperty(persistent.getId(), RmrsAlfrescoTypes.PROP_RUSSIAN, updateAttachment(persistent.getId(), d.getRussian(), RmrsAlfrescoTypes.PROP_RUSSIAN));
            repositoryService.setProperty(persistent.getId(), RmrsAlfrescoTypes.PROP_ENGLISH, updateAttachment(persistent.getId(), d.getEnglish(), RmrsAlfrescoTypes.PROP_ENGLISH));
        }
    }

    private String updateAttachment(String docId, AttachedFile file, QName prop) {
        final String old = (String) repositoryService.getProperty(docId, prop);
        if (file != null) {
            if (!Objects.equal(old, file.id)) {
                if (old != null) {
                    deleteAttachment(old);
                }
                return attachFile(docId, file);
            } else {
                return old;
            }
        } else if (old != null) {
            deleteAttachment(old);
        }
        return null;
    }
}
