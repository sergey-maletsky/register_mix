package com.firstlinesoftware.rmrs.client.commands;

import com.firstlinesoftware.base.client.widgets.popups.MenuPopupPresenter;
import com.firstlinesoftware.exec.client.commands.ErrandCommand;
import com.firstlinesoftware.exec.shared.dto.AbstractErrand;
import com.firstlinesoftware.rmrs.client.Rmrs;
import com.firstlinesoftware.rmrs.client.messages.RmrsMessages;
import com.firstlinesoftware.rmrs.client.places.CreateRequirementPlace;
import com.firstlinesoftware.rmrs.shared.dto.ChangeRequirementErrand;
import com.firstlinesoftware.rmrs.shared.dto.Requirement;
import com.google.gwt.user.client.ui.Widget;

import java.util.Set;

public class CreateRequirementFromErrandCommand extends ErrandCommand {
    private final RmrsMessages messages = Rmrs.getInjector().getMessages();

    @Override
    protected Widget getInstance(final AbstractErrand dto) {
        if (orgstructureProxy.isMyPosition(dto.executor)
                && dto instanceof ChangeRequirementErrand
                && ((ChangeRequirementErrand) dto).hasCreateNew()) {
            final Set<String> completed = ((ChangeRequirementErrand) dto).collectCompleted();
            final Widget button = createButton(ICONS.addDocument32(), messages.createRequirements());
            final MenuPopupPresenter presenter = new MenuPopupPresenter(button);
            for (Requirement requirement : ((ChangeRequirementErrand) dto).createNew) {
                if(!completed.contains(requirement.id)) {
                    presenter.addPlace(requirement.getTitle(), new CreateRequirementPlace(requirement, dto.id));
                }
            }
            return presenter.isEnabled() ? button : null;
        }
        else return null;
    }
}
