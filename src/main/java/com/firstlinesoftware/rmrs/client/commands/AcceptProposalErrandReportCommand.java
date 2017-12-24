package com.firstlinesoftware.rmrs.client.commands;

import com.firstlinesoftware.base.client.SuccessCallback;
import com.firstlinesoftware.base.client.services.VoidUserActionCallback;
import com.firstlinesoftware.base.client.widgets.popups.StandardPopups;
import com.firstlinesoftware.exec.client.commands.ErrandCommand;
import com.firstlinesoftware.exec.shared.dto.AbstractErrand;
import com.firstlinesoftware.exec.shared.dto.Visa;
import com.firstlinesoftware.rmrs.client.Rmrs;
import com.firstlinesoftware.rmrs.client.messages.RmrsMessages;
import com.firstlinesoftware.rmrs.client.places.ApproveProposalErrandReportPlace;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Widget;

public class AcceptProposalErrandReportCommand extends ErrandCommand {

    private final RmrsMessages messages = Rmrs.getInjector().getMessages();

    @Override
    protected Widget getInstance(final AbstractErrand dto) {
        final boolean onMyControl = dto.lifecycle.equals(AbstractErrand.LIFECYCLE_ONCONTROL) && orgstructureProxy.isMyPosition(dto.controller);
        final boolean onMyReview = dto.lifecycle.equals(AbstractErrand.LIFECYCLE_ONREVIEW) && orgstructureProxy.isMyPosition(dto.author);
        if (onMyControl || onMyReview) {
            if (dto.parentErrand != null) {
                return createButton(ICONS.ok32(), messages.confirm(), new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        StandardPopups.confirm(messages.acceptErrandReport() + '?', new SuccessCallback<Void>() {
                            @Override
                            public void onSuccess(Void result) {
                                errandProxy.acceptReport(dto.id, new Visa(), new VoidUserActionCallback(messages.reportAccepted(), messages.errorWhileAcceptingReport()));
                            }
                        });
                    }
                });
            } else {
                return createButton(ICONS.ok32(), messages.confirm(),
                        new ApproveProposalErrandReportPlace(dto.id, dto.getLastReport().id, dto.controller != null ? dto.controller.id : dto.author.id));
            }
        } else return null;
    }
}
