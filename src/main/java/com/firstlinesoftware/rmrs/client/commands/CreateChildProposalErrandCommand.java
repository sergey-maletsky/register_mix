package com.firstlinesoftware.rmrs.client.commands;

import com.firstlinesoftware.exec.client.commands.ErrandCommand;
import com.firstlinesoftware.exec.shared.dto.AbstractErrand;
import com.firstlinesoftware.rmrs.client.places.CreateProposalChildErrandPlace;
import com.google.gwt.user.client.ui.Widget;

public class CreateChildProposalErrandCommand extends ErrandCommand {
    @Override
    protected Widget getInstance(final AbstractErrand dto) {
        return orgstructureProxy.isMyPosition(dto.executor)
                ? createButton(ICON_ERRAND_ADD, execMessages.newErrand(), new CreateProposalChildErrandPlace(dto.id, dto.executor.id))
                : null;
    }
}
