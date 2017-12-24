package com.firstlinesoftware.rmrs.client.commands;

import com.firstlinesoftware.base.client.widgets.popups.MenuPopupPresenter;
import com.firstlinesoftware.rmrs.client.Rmrs;
import com.firstlinesoftware.rmrs.client.messages.RmrsMessages;
import com.firstlinesoftware.rmrs.client.places.CreateProposalPlace;
import com.firstlinesoftware.rmrs.shared.dto.Requirement;
import com.firstlinesoftware.rmrs.shared.dto.RmrsDirectories;
import com.firstlinesoftware.route.client.commands.AbstractRouteCommand;
import com.firstlinesoftware.route.shared.dto.AbstractRoute;
import com.google.gwt.user.client.ui.Widget;

public class CreateProposalFromRequirementCommand extends AbstractRouteCommand {

    private final RmrsMessages messages = Rmrs.getInjector().getMessages();

    @Override
    public Widget getInstance(final AbstractRoute dto, final String positionId) {
        if (dto instanceof Requirement) {
            final Widget button;
            button = createButton(ICONS.addDocument32(), messages.createProposal());
            final MenuPopupPresenter presenter = new MenuPopupPresenter(button);
            presenter.addPlace(messages.proposalBasedOnRequirement(), new CreateProposalPlace(RmrsDirectories.RELATION_TYPE_CREATED_ON, dto.id));
            presenter.addPlace(messages.proposalToChangeRequirement(), new CreateProposalPlace(RmrsDirectories.RELATION_TYPE_CHANGES, dto.id));
            return button;
        }
        return null;
    }
}
