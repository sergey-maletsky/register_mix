package com.firstlinesoftware.rmrs.client.widgets;

import com.firstlinesoftware.ecm.client.widgets.DocumentLabel;
import com.firstlinesoftware.ecm.shared.dto.Document;
import com.google.gwt.dom.client.AnchorElement;

public class RequirementLabel extends DocumentLabel {
    @Override
    protected void doRenderer(final Document value) {
        super.doRenderer(value);
        final AnchorElement as = AnchorElement.as(getElement());
        if (value != null) {
            as.setHref("#requirements:id=" + value.getId());
        } else {
            as.setHref("javascript:void(0)");
        }

    }

}
