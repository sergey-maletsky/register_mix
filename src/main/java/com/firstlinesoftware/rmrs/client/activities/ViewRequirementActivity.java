package com.firstlinesoftware.rmrs.client.activities;

import com.firstlinesoftware.base.client.services.ActionCallback;
import com.firstlinesoftware.base.client.widgets.ExpandableList;
import com.firstlinesoftware.ecm.client.Ecm;
import com.firstlinesoftware.ecm.client.views.viewers.CompositeDocumentViewImpl;
import com.firstlinesoftware.ecm.shared.dto.Document;
import com.firstlinesoftware.rmrs.client.Rmrs;
import com.firstlinesoftware.rmrs.shared.dto.Requirement;
import com.firstlinesoftware.route.client.activities.ViewAbstractRouteActivity;

import java.util.Arrays;
import java.util.List;

/**
 * User: VAntonov
 * Date: 15.12.2010
 * Time: 15:57:57
 */
public class ViewRequirementActivity extends ViewAbstractRouteActivity<Requirement, CompositeDocumentViewImpl<Requirement>> {

    public ViewRequirementActivity(final Requirement requirement) {
        super(requirement);
    }

    @Override
    public void setupFields(Requirement dto) {
        super.setupFields(dto);
        final ExpandableList path = view.getFormItemWidget(Rmrs.getInjector().getMessages().path());
        assert path != null;
        if(dto.fullPath != null) {
            final String[] ids = dto.fullPath.split("\\.");
            Ecm.getInjector().getDocumentProxy().getDocuments(Arrays.asList(ids), new ActionCallback<List<Document>>(Rmrs.getInjector().getMessages().errorWhileGettingDocuments()) {
                @Override
                public void onActionSuccess(List<Document> result) {
                    path.setValue(result);
                }
            });
        } else {
            path.setValue(null);
        }
    }
}
