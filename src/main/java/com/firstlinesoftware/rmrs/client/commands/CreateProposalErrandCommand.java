package com.firstlinesoftware.rmrs.client.commands;

import com.firstlinesoftware.ecm.client.commands.EcmCommand;
import com.firstlinesoftware.ecm.shared.dto.Document;
import com.firstlinesoftware.rmrs.client.Rmrs;
import com.firstlinesoftware.rmrs.client.messages.RmrsMessages;
import com.firstlinesoftware.rmrs.client.places.CreateProposalErrandPlace;
import com.google.gwt.user.client.ui.Widget;

public class CreateProposalErrandCommand extends EcmCommand {

    private final RmrsMessages messages = Rmrs.getInjector().getMessages();

    @Override
    protected Widget getInstance(Document dto, String positionId) {
        return createButton(ICONS.errandAdd32(), messages.newErrand(), new CreateProposalErrandPlace(dto.id, positionId));
    }
}
