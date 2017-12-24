package com.firstlinesoftware.rmrs.client.commands;

import com.firstlinesoftware.base.client.events.PrePopupEvent;
import com.firstlinesoftware.base.client.events.PrePopupHandler;
import com.firstlinesoftware.base.client.events.SavePopupEvent;
import com.firstlinesoftware.base.client.events.SavePopupHandler;
import com.firstlinesoftware.base.client.services.VoidUserActionCallback;
import com.firstlinesoftware.base.client.widgets.popups.EditorPopupPresenter;
import com.firstlinesoftware.exec.client.commands.ErrandCommand;
import com.firstlinesoftware.exec.shared.dto.AbstractErrand;
import com.firstlinesoftware.orgstruct.client.widgets.DelegatePopupPanel;
import com.google.gwt.user.client.ui.Widget;

public class DelegateErrandCommand extends ErrandCommand {

    @Override
    protected Widget getInstance(final AbstractErrand dto) {
        if (orgstructureProxy.isMyPosition(dto.controller) || orgstructureProxy.isMyPosition(dto.author)
                || orgstructureProxy.isMyPosition(dto.executor)) {
            final Widget button = createButton(ICON_POSITION_CHANGE, execMessages.delegateErrand());
            final DelegatePopupPanel panel = new DelegatePopupPanel();
            final EditorPopupPresenter<DelegatePopupPanel.DelegateInfo, DelegatePopupPanel> presenter = new EditorPopupPresenter<>(panel, button);
            presenter.addPrePopupHandler(new PrePopupHandler() {
                @Override
                public void onPrePopup(PrePopupEvent event) {
                    panel.setValue(null);
                }
            });
            presenter.addSavePopupHandler(new SavePopupHandler<DelegatePopupPanel.DelegateInfo>() {
                @Override
                public void onSave(final SavePopupEvent<DelegatePopupPanel.DelegateInfo> event) {
                    if (event.getResult() != null && event.getResult().position != null) {
                        errandProxy.delegateErrand(dto.id, event.getResult().position.id, event.getResult().comment, new VoidUserActionCallback(
                                execMessages.errandDelegated(),
                                execMessages.errorWhileDelegatingErrand()));
                    }
                }
            });

            return button;
        }

        return null;
    }

}
