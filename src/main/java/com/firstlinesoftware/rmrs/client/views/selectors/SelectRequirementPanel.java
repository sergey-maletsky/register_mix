package com.firstlinesoftware.rmrs.client.views.selectors;

import com.firstlinesoftware.base.client.utils.PopupButtonContentBuilder;
import com.firstlinesoftware.base.client.widgets.popups.StandardEditorPanel;
import com.firstlinesoftware.rmrs.shared.dto.Requirement;

public class SelectRequirementPanel extends StandardEditorPanel<Requirement> {
    @Override
    protected void appendContent(PopupButtonContentBuilder builder) {

    }

    @Override
    public boolean validate() {
        return false;
    }

    @Override
    public void setValue(Requirement value, boolean fireEvents) {

    }

    @Override
    public Requirement getValue() {
        return null;
    }
}
