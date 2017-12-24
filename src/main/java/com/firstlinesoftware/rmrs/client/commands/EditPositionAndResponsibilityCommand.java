package com.firstlinesoftware.rmrs.client.commands;

import com.firstlinesoftware.base.client.widgets.popups.MenuPopupPresenter;
import com.firstlinesoftware.base.shared.dto.DTO;
import com.firstlinesoftware.orgstruct.client.Orgstruct;
import com.firstlinesoftware.orgstruct.client.commands.OrgstructCommand;
import com.firstlinesoftware.orgstruct.client.messages.OrgstructMessages;
import com.firstlinesoftware.orgstruct.client.places.EditPositionPlace;
import com.firstlinesoftware.orgstruct.shared.dto.Position;
import com.firstlinesoftware.rmrs.client.Rmrs;
import com.firstlinesoftware.rmrs.client.messages.RmrsMessages;
import com.firstlinesoftware.rmrs.client.places.AddResponsibilityForRequirementPlace;
import com.firstlinesoftware.rmrs.client.places.RemoveResponsibilityForRequirementPlace;
import com.google.gwt.user.client.ui.Widget;

public class EditPositionAndResponsibilityCommand extends OrgstructCommand {
    private final RmrsMessages rmrsMessages = Rmrs.getInjector().getMessages();
    private final OrgstructMessages orgstructMessages = Orgstruct.getInjector().getMessages();

    private Widget imageButton;

    @Override
    protected Widget getInstance(DTO dto) {
        if (dto instanceof Position) {
            final Position position = (Position) dto;
            imageButton = createButton(ICONS.edit32(), orgstructMessages.editPosition());
            final MenuPopupPresenter presenter = new MenuPopupPresenter(imageButton);
            presenter.addPlace(messages.editPosition(), new EditPositionPlace(dto.id));
            presenter.addPlace(rmrsMessages.assignReqiurementsToPostion(),
                    new AddResponsibilityForRequirementPlace(position.id));
            presenter.addPlace(rmrsMessages.unassignReqiurementsToPostion(),
                    new RemoveResponsibilityForRequirementPlace(position.id));
            return imageButton;
        } else {
            return null;
        }
    }
}
