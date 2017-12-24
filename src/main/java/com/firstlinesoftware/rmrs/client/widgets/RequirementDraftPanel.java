package com.firstlinesoftware.rmrs.client.widgets;

import com.firstlinesoftware.base.client.utils.PopupButtonContentBuilder;
import com.firstlinesoftware.base.client.widgets.AutoLayoutTextArea;
import com.firstlinesoftware.base.client.widgets.FormItemBase;
import com.firstlinesoftware.base.client.widgets.TrimTextBox;
import com.firstlinesoftware.base.client.widgets.popups.StandardEditorPanel;
import com.firstlinesoftware.base.shared.utils.GUID;
import com.firstlinesoftware.orgstruct.client.widgets.PositionSelector;
import com.firstlinesoftware.rmrs.client.Rmrs;
import com.firstlinesoftware.rmrs.client.messages.RmrsMessages;
import com.firstlinesoftware.rmrs.shared.dto.Requirement;
import com.google.gwt.event.logical.shared.ValueChangeEvent;

public class RequirementDraftPanel extends StandardEditorPanel<Requirement> {
    private final RmrsMessages messages = Rmrs.getInjector().getMessages();

    private final AutoLayoutTextArea description = new AutoLayoutTextArea();
    private final PositionSelector responsible = new PositionSelector();
    private final RequirementSelector includedIn = new RequirementSelector(true);
    private final TrimTextBox number = new TrimTextBox();

    @Override
    protected void appendContent(PopupButtonContentBuilder b) {
        number.setWidth("100%");
        description.setWidth(FormItemBase.MIN_SELECTOR_WIDTH + "px");

        b.addWidget(messages.number(), number);
        b.addRequiredWidget(messages.shortDescription(), description);
        b.addWidget(messages.includedIn(), includedIn);
        b.addWidget(messages.responsible(), responsible);
    }

    @Override
    public boolean validate() {
        return description.getValue() != null;
    }

    @Override
    public void setValue(Requirement value, boolean fireEvents) {
        if(value != null) {
            number.setValue(value.number, false);
            description.setValue(value.getName(), false);
            includedIn.setValue(value.parent, false);
            responsible.setValue(value.responsible, false);
        } else {
            number.setValue(null, false);
            description.setValue(null, false);
            includedIn.setValue(null, false);
            responsible.setValue(null, false);
        }
        if(fireEvents) {
            ValueChangeEvent.fire(this, value);
        }
    }

    @Override
    public Requirement getValue() {
        final Requirement r = new Requirement();
        r.setName(description.getValue());
        r.number = number.getValue();
        r.parent = (Requirement) includedIn.getValue();
        r.responsible = responsible.getValue();
        r.id = GUID.generate();
        return r;
    }
}
