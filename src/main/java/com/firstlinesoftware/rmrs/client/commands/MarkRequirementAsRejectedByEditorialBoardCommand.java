package com.firstlinesoftware.rmrs.client.commands;

import com.firstlinesoftware.base.client.SuccessCallback;
import com.firstlinesoftware.base.client.browsers.Browser;
import com.firstlinesoftware.base.client.commands.BrowserCommand;
import com.firstlinesoftware.base.client.services.VoidUserActionCallback;
import com.firstlinesoftware.base.client.widgets.AutoLayoutTextArea;
import com.firstlinesoftware.base.client.widgets.popups.StandardPopups;
import com.firstlinesoftware.base.shared.dto.DTO;
import com.firstlinesoftware.rmrs.client.Rmrs;
import com.firstlinesoftware.rmrs.client.messages.RmrsMessages;
import com.firstlinesoftware.rmrs.client.proxies.RequirementProxy;
import com.firstlinesoftware.rmrs.shared.dto.Requirement;
import com.firstlinesoftware.route.shared.dto.AbstractRoute;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Widget;

import java.util.ArrayList;
import java.util.Set;

public class MarkRequirementAsRejectedByEditorialBoardCommand extends BrowserCommand<DTO> {
    private final RmrsMessages messages = Rmrs.getInjector().getMessages();
    private final RequirementProxy requirementProxy = Rmrs.getInjector().getRequirementProxy();
    private Button button;

    @Override
    public Widget getInstance(final Browser<DTO> view) {
        button = createButton(messages.rejected(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                final Set<DTO> selected = view.getSelectedSet();
                assert selected != null && !selected.isEmpty();
                StandardPopups.enterValue(new AutoLayoutTextArea(), messages.comment(), null, button, new SuccessCallback<String>() {
                    @Override
                    public void onSuccess(String comment) {
                        final ArrayList<String> ids = Lists.newArrayList(Collections2.transform(selected, DTO.GET_IDS));
                        requirementProxy.setLifecycle(AbstractRoute.LIFECYCLE_REJECTED, ids, comment, new VoidUserActionCallback(
                                messages.statusChanged(),
                                messages.errorWhileChangingStatus()));
                    }
                });
            }
        });
        enableOnSelection(button, view);
        return button;
    }

    @Override
    protected boolean isEnabled(Browser<DTO> view) {
        final Set<DTO> selected = view.getSelectedSet();
        if(selected != null && !selected.isEmpty()) {
            for (DTO s : selected) {
                if(!(s instanceof Requirement && AbstractRoute.LIFECYCLE_ONSIGNING.equals(((Requirement) s).lifecycle))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
}
