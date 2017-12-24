package com.firstlinesoftware.rmrs.server.providers;

import com.firstlinesoftware.base.shared.dto.Folder;
import com.firstlinesoftware.ecm.server.providers.impl.AbstractFolderProvider;
import com.firstlinesoftware.rmrs.server.RmrsAlfrescoTypes;
import org.alfresco.service.namespace.QName;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;

@Component
public class RequirementsFolderProvider extends AbstractFolderProvider {
    public static final String FOLDER = "requirements";

    @PostConstruct
    private void init() {
        folderProviderFactory.register(FOLDER, this);
    }

    @Override
    protected QName getFolderType() {
        return RmrsAlfrescoTypes.TYPE_REQUIREMENT_FOLDER;
    }

    @Override
    public List<Folder> getFolders() {
        return Collections.emptyList();
    }
}
