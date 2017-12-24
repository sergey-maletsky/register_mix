package com.firstlinesoftware.rmrs.client.widgets;

import com.firstlinesoftware.base.client.widgets.ImageButton;
import com.google.gwt.user.client.ui.FlowPanel;

public class StackPanelHeaderWithButtons extends FlowPanel {

    public StackPanelHeaderWithButtons(String header, ImageButton[] buttons) {
        super();
        addStyleName("gwt-HTML");
        getElement().setInnerHTML(header);

        FlowPanel buttonsPanel = new FlowPanel();
        buttonsPanel.addStyleName("stackHeader-buttons");

        for(ImageButton button: buttons) {
            buttonsPanel.add(button);
        }

        add(buttonsPanel);
    }
}
