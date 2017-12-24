package com.firstlinesoftware.rmrs.client.commands;

import com.firstlinesoftware.ecm.client.Ecm;
import com.firstlinesoftware.ecm.client.factories.EditDocumentPlaceFactory;
import com.firstlinesoftware.ecm.client.places.AbstractEditPlace;
import com.firstlinesoftware.route.client.commands.AbstractRouteCommand;
import com.firstlinesoftware.route.shared.dto.AbstractRoute;
import com.firstlinesoftware.route.shared.dto.PositionRoles;
import com.google.gwt.user.client.ui.Widget;

public class FalsificateNonRouteDocumentCommand extends AbstractRouteCommand {
    private final EditDocumentPlaceFactory editDocumentPlaceFactory = Ecm.getInjector().getEditDocumentPlaceFactory();

    @Override
    protected Widget getInstance(final AbstractRoute dto, final String positionId) {
        if (orgstructureProxy.hasRole(null, PositionRoles.ROLE_FALCIFICATE_DOCUMENT)) {
            final AbstractEditPlace place = editDocumentPlaceFactory.get(dto.kind);
            place.setId(dto.id);
            return createButton(ICONS.edit32(), ecmMessages.edit(), place);
        } else {
            return null;
        }
    }

}
