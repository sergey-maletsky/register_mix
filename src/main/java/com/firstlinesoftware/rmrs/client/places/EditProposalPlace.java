package com.firstlinesoftware.rmrs.client.places;

import com.firstlinesoftware.base.shared.utils.ReflectablePlaceTokenizer;
import com.firstlinesoftware.ecm.client.places.AbstractEditCompositeDocumentPlace;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;
import com.gwtent.reflection.client.Reflection;

public class EditProposalPlace extends AbstractEditCompositeDocumentPlace implements Reflection{
    public EditProposalPlace(String id) {
        super(id);
    }

    public EditProposalPlace() {
    }

    @Override
    public AbstractEditCompositeDocumentPlace createInstance() {
        return new EditProposalPlace();
    }

    @Override
    public boolean equals(Object o) {
        return o != null && (o.getClass().equals(this.getClass())) && id != null && this.id.equals(((EditProposalPlace)o).id);
    }

    @Prefix("edit-proposal")
    public static class Tokenizer extends ReflectablePlaceTokenizer<EditProposalPlace> implements PlaceTokenizer<EditProposalPlace>{

        @Override
        public Class<EditProposalPlace> getClassType() {
            return EditProposalPlace.class;
        }

        @Override
        public EditProposalPlace createPlace() {
            return new EditProposalPlace();
        }
    }
}
