package com.firstlinesoftware.rmrs.client.commands;

import com.firstlinesoftware.base.client.browsers.Browser;
import com.firstlinesoftware.base.client.commands.BrowserCommand;
import com.firstlinesoftware.ecm.shared.dto.Document;
import com.firstlinesoftware.rmrs.client.Rmrs;
import com.firstlinesoftware.rmrs.client.browsers.FilteredFolderBrowser;
import com.firstlinesoftware.rmrs.client.messages.RmrsMessages;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Widget;

import java.util.Set;

public class ClearSelectionCommand extends BrowserCommand<Document> {
    private final RmrsMessages messages = Rmrs.getInjector().getMessages();

    @Override
    public Widget getInstance(final Browser<Document> view) {
        final Button button = view instanceof FilteredFolderBrowser ? createButton(messages.clearSelection(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                ((FilteredFolderBrowser) view).clearSelection();
            }
        }) : null;
        enableOnSelection(button, view);
        return button;
    }

    @Override
    protected boolean isEnabled(Browser<Document> view) {
        final Set<Document> selected = view.getSelectedSet();
        return selected != null && selected.size() > 1;
    }
}
