package com.firstlinesoftware.rmrs.client.commands;

import com.firstlinesoftware.exec.client.commands.ErrandCommand;
import com.firstlinesoftware.exec.shared.dto.AbstractErrand;
import com.firstlinesoftware.rmrs.client.Rmrs;
import com.firstlinesoftware.rmrs.client.messages.RmrsMessages;
import com.firstlinesoftware.rmrs.client.places.CreateProposalErrandReportPlace;
import com.google.gwt.user.client.ui.Widget;

public class ApproveProposalCommand extends ErrandCommand {

    private final RmrsMessages messages = Rmrs.getInjector().getMessages();

    @Override
    protected Widget getInstance(AbstractErrand dto) {
        return orgstructureProxy.isMyPosition(dto.executor)
                ? createButton(ICONS.ok32(), messages.accept(), new CreateProposalErrandReportPlace(true, dto.id, dto.executor.id))
                : null;
    }
}
