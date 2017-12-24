package com.firstlinesoftware.rmrs.client.activities;

import com.firstlinesoftware.ecm.client.views.viewers.CompositeDocumentViewImpl;
import com.firstlinesoftware.rmrs.shared.dto.CircularLetter;
import com.firstlinesoftware.rmrs.shared.dto.Requirement;
import com.firstlinesoftware.route.client.activities.ViewAbstractRouteActivity;

public class ViewCircularLetterActivity extends ViewAbstractRouteActivity<CircularLetter, CompositeDocumentViewImpl<CircularLetter>> {

    public ViewCircularLetterActivity(final CircularLetter requirement) {
        super(requirement);
    }
}
