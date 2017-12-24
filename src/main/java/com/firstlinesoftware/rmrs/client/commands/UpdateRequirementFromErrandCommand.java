package com.firstlinesoftware.rmrs.client.commands;

import com.firstlinesoftware.base.client.widgets.popups.MenuPopupPresenter;
import com.firstlinesoftware.exec.client.commands.ErrandCommand;
import com.firstlinesoftware.exec.shared.dto.AbstractErrand;
import com.firstlinesoftware.rmrs.client.Rmrs;
import com.firstlinesoftware.rmrs.client.messages.RmrsMessages;
import com.firstlinesoftware.rmrs.client.places.EditRequirementPlace;
import com.firstlinesoftware.rmrs.shared.dto.ChangeRequirementErrand;
import com.firstlinesoftware.rmrs.shared.dto.Requirement;
import com.google.gwt.user.client.ui.Widget;

import java.util.Set;

public class UpdateRequirementFromErrandCommand extends ErrandCommand {
    private final RmrsMessages messages = Rmrs.getInjector().getMessages();

    @Override
    protected Widget getInstance(final AbstractErrand dto) {
        if (orgstructureProxy.isMyPosition(dto.executor) && dto instanceof ChangeRequirementErrand
                && ((ChangeRequirementErrand) dto).hasModifyExisting()) {
            final Set<String> completed = ((ChangeRequirementErrand) dto).collectCompleted();
            final Widget button = createButton(ICONS.edit32(), messages.modifyRequirements());
            final MenuPopupPresenter presenter = new MenuPopupPresenter(button);
            for (Requirement requirement : ((ChangeRequirementErrand) dto).modifyExisting) {
                if(!completed.contains(requirement.id)) {
                    presenter.addPlace(requirement.getTitle(), new EditRequirementPlace(requirement.id, dto.id));
                }
            }
            return presenter.isEnabled() ? button : null;
        }
        else return null;
    }

}
