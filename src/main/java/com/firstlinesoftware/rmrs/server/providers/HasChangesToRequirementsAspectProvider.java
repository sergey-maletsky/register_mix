package com.firstlinesoftware.rmrs.server.providers;

import com.firstlinesoftware.base.server.services.RepositoryService;
import com.firstlinesoftware.base.shared.dto.Persistent;
import com.firstlinesoftware.ecm.server.providers.impl.AbstractAspectProvider;
import com.firstlinesoftware.orgstruct.shared.dto.Position;
import com.firstlinesoftware.rmrs.server.RmrsAlfrescoTypes;
import com.firstlinesoftware.rmrs.shared.dto.HasChangesToRequirements;
import com.firstlinesoftware.rmrs.shared.dto.Requirement;
import com.firstlinesoftware.route.shared.dto.RouteState;
import com.google.common.base.Function;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

@Component
public class HasChangesToRequirementsAspectProvider extends AbstractAspectProvider {
    public static final TypeReference<List<Requirement>> TYPE_REQUIREMENTS = new TypeReference<List<Requirement>>() {};
    public static final Function<Requirement, Requirement> CONVERT_TO_JSON = new Function<Requirement, Requirement>() {
        @Nullable
        @Override
        public Requirement apply(Requirement input) {
            final Requirement r = input.clone();
            if (input.parent != null) {
                r.parent = new Requirement();
                r.routeState = null;
                r.parent.id = input.parent.id;
            }
            r.routeState = null;
            r.responsible = input.responsible != null ? new Position(input.responsible.id) : null;
            return r;
        }
    };
    private Function<Requirement, Requirement> convertFromJson = new Function<Requirement, Requirement>() {
        @Nullable
        @Override
        public Requirement apply(@Nullable Requirement r) {
            if (r != null) {
                r.parent = r.parent != null ? documentService.<Requirement>getProperties(r.parent.id) : null;
                r.routeState = new RouteState();
                r.responsible = r.responsible != null ? orgstructService.getPosition(r.responsible.id) : null;
            }
            return r;
        }
    };

    @Override
    @SuppressWarnings("unchecked")
    public void fillDocument(Persistent persistent, RepositoryService.Node properties) {
        super.fillDocument(persistent, properties);
        if(persistent instanceof HasChangesToRequirements) {
            final HasChangesToRequirements report = (HasChangesToRequirements) persistent;
            report.setCreateNew(readJSON(properties, RmrsAlfrescoTypes.PROP_CREATE_NEW, TYPE_REQUIREMENTS, convertFromJson));
            report.setModifyExisting(documentService.<Requirement>getDocumentsByIds((Collection<String>) properties.get(RmrsAlfrescoTypes.PROP_MODIFY_EXISTING)));
        }
    }

    @Override
    public void fillNode(RepositoryService.Node node, Persistent persistent) {
        super.fillNode(node, persistent);
        if(persistent instanceof HasChangesToRequirements) {
            final HasChangesToRequirements report = (HasChangesToRequirements) persistent;
            node.add(RmrsAlfrescoTypes.PROP_MODIFY_EXISTING, (Serializable) getIds(report.getModifyExisting()));
            writeJSON(node, RmrsAlfrescoTypes.PROP_CREATE_NEW, report.getCreateNew(), TYPE_REQUIREMENTS, CONVERT_TO_JSON);
        }
    }

    @Override
    public void fillVersion(RepositoryService.Node version, RepositoryService.Node node) {
        super.fillVersion(version, node);
        version.add(RmrsAlfrescoTypes.PROP_ACCEPTED, node.get(RmrsAlfrescoTypes.PROP_ACCEPTED));
        version.add(RmrsAlfrescoTypes.PROP_CREATE_NEW, node.get(RmrsAlfrescoTypes.PROP_CREATE_NEW));
        version.add(RmrsAlfrescoTypes.PROP_MODIFY_EXISTING, node.get(RmrsAlfrescoTypes.PROP_MODIFY_EXISTING));
    }
}
