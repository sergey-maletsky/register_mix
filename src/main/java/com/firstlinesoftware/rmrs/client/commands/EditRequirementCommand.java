package com.firstlinesoftware.rmrs.client.commands;

import com.firstlinesoftware.ecm.client.Ecm;
import com.firstlinesoftware.ecm.client.commands.EcmCommand;
import com.firstlinesoftware.ecm.client.factories.EditDocumentPlaceFactory;
import com.firstlinesoftware.ecm.client.places.AbstractEditPlace;
import com.firstlinesoftware.ecm.shared.dto.Document;
import com.google.gwt.user.client.ui.Widget;

public class EditRequirementCommand extends EcmCommand {
    private final EditDocumentPlaceFactory editDocumentPlaceFactory = Ecm.getInjector().getEditDocumentPlaceFactory();

    @Override
    protected Widget getInstance(final Document dto, final String positionId) {
        final AbstractEditPlace place = editDocumentPlaceFactory.get(dto.kind);
        place.setId(dto.id);
        return createButton(ICONS.edit32(), ecmMessages.edit(), place);
    }

}
