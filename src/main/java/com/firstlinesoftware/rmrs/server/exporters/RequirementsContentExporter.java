package com.firstlinesoftware.rmrs.server.exporters;

import com.firstlinesoftware.base.server.BaseAlfrescoTypes;
import com.firstlinesoftware.base.server.services.RepositoryService;
import com.firstlinesoftware.base.shared.dto.AttachedFile;
import com.firstlinesoftware.base.shared.dto.SearchCriteria;
import com.firstlinesoftware.base.shared.utils.CriteriaBuilder;
import com.firstlinesoftware.ecm.server.services.DocumentService;
import com.firstlinesoftware.rmrs.shared.dto.Requirement;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Component
public class RequirementsContentExporter {
    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private DocumentService documentService;

    public String export(String parentId, Date date) throws IOException {
        Requirement parent = documentService.getProperties(parentId);
        if (parent != null) {
            final CriteriaBuilder<SearchCriteria> builder = new CriteriaBuilder<>()
                    .addMustHave("rmrs:fullPath", (parent.fullPath != null ? parent.fullPath + '.' : "") + parent.id + '*')
                    .addMustHave("ecm:lifecycle", Requirement.LIFECYCLE_SIGNED);
            final List<Requirement> result = documentService.search(builder.build());
            final Set<String> attachments = new HashSet<>();
            for (Requirement r : result) {
                if (r.russian != null) {
                    attachments.add(r.russian.id);
                }
                if (r.english != null) {
                    attachments.add(r.english.id);
                }
            }

            final Map<String, String> names = new HashMap<>();
            for (RepositoryService.Node node : repositoryService.getPropertiesUnsafe(attachments)) {
                String name = node.get(BaseAlfrescoTypes.PROP_NAME);
                names.put(node.getId(), name);
            }

            Set<String> created = new HashSet<>();
            final File f = File.createTempFile("exported_", ".zip", repositoryService.getTempDir());
            try (final ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(f))) {
                for (Requirement requirement : result) {
                    if (requirement.effective == null
                            || (requirement.effective.min == null || !requirement.effective.min.after(date))
                            && (requirement.effective.max == null || !requirement.effective.max.before(date))) {
                        exportAttachment(zip, requirement, "RU", requirement.russian, names, created);
                        exportAttachment(zip, requirement, "EN", requirement.english, names, created);
                    }
                }
                zip.flush();
                return f.getName();
            }
        } else {
            return null;
        }
    }

    private void exportAttachment(ZipOutputStream zip, Requirement requirement, String lang, AttachedFile file, Map<String, String> names, Set<String> created) throws IOException {
        if (file != null) {
            String name = names.get(file.id);
            if (name == null) {
                name = repositoryService.createUniqueName();
            }
            final int dot = name.lastIndexOf('.');
            String fileName = lang + (requirement.part != null ? '_' + requirement.part : "") + (requirement.number != null ? '_' + requirement.number : "") + (dot != -1 ? name.substring(dot) : "");
            while (created.contains(fileName)) {
                fileName = '_' + fileName;
            }
            created.add(fileName);
            zip.putNextEntry(new ZipEntry(fileName));
            final RepositoryService.Content content = repositoryService.getContent(file.id);
            try (InputStream inputStream = content.stream) {
                IOUtils.copy(inputStream, zip);
            }
        }
    }
}
