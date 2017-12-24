package com.firstlinesoftware.rmrs.client.widgets;

import com.firstlinesoftware.base.client.utils.PopupButtonContentBuilder;
import com.firstlinesoftware.base.client.widgets.DatePicker;
import com.firstlinesoftware.base.client.widgets.FixedHeightCheckBox;
import com.firstlinesoftware.base.client.widgets.popups.StandardEditorPanel;
import com.firstlinesoftware.base.shared.dto.Pair;
import com.firstlinesoftware.rmrs.client.Rmrs;
import com.firstlinesoftware.rmrs.client.messages.RmrsMessages;

import java.util.Date;

public class RequirementsFilterPanel extends StandardEditorPanel<Pair<Boolean, Date>> {

    private final RmrsMessages messages = Rmrs.getInjector().getMessages();

    private final DatePicker date = new DatePicker();
    private FixedHeightCheckBox onlySigned;

    @Override
    protected void appendContent(PopupButtonContentBuilder builder) {
        onlySigned = new FixedHeightCheckBox();
        builder.addWidget(messages.onlySigned(), onlySigned);
        builder.addWidget(messages.effectiveDates(), date);
    }

    @Override
    public boolean validate() {
        return true;
    }

    @Override
    public void setValue(Pair<Boolean, Date> value, boolean fireEvents) {
        if (value == null) {
            onlySigned.setValue(null, false);
            date.setValue(null, false);
        } else {
            onlySigned.setValue(value.getFirst());
            date.setValue(value.getSecond());
        }
    }

    @Override
    public Pair<Boolean, Date> getValue() {
        return new Pair<>(onlySigned.getValue(), date.getValue());
    }
}
