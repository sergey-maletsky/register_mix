package com.firstlinesoftware.rmrs.client.activities;

import com.firstlinesoftware.ecm.client.views.viewers.CompositeDocumentViewImpl;
import com.firstlinesoftware.rmrs.shared.dto.Proposal;
import com.firstlinesoftware.route.client.activities.ViewAbstractRouteActivity;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class ViewProposalActivity extends ViewAbstractRouteActivity<Proposal, CompositeDocumentViewImpl<Proposal>> {

    public ViewProposalActivity(final Proposal requirement) {
        super(requirement);
    }

    @Override
    protected void setupEditor(AcceptsOneWidget panel, Proposal dto) {
        super.setupEditor(panel, dto);
        view.getTabLayout().getTabWidget(1).setWidth("100px");
    }
}


