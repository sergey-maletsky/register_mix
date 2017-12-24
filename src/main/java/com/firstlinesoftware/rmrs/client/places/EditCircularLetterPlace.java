package com.firstlinesoftware.rmrs.client.places;

import com.firstlinesoftware.base.shared.utils.ReflectablePlaceTokenizer;
import com.firstlinesoftware.ecm.client.places.AbstractEditCompositeDocumentPlace;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;
import com.gwtent.reflection.client.Reflection;

public class EditCircularLetterPlace extends AbstractEditCompositeDocumentPlace implements Reflection{
    public EditCircularLetterPlace(String id) {
        super(id);
    }

    public EditCircularLetterPlace() {
    }

    @Override
    public AbstractEditCompositeDocumentPlace createInstance() {
        return new EditCircularLetterPlace();
    }

    @Override
    public boolean equals(Object o) {
        return o != null && (o.getClass().equals(this.getClass())) && id != null && this.id.equals(((EditCircularLetterPlace)o).id);
    }

    @Prefix("edit-circular")
    public static class Tokenizer extends ReflectablePlaceTokenizer<EditCircularLetterPlace> implements PlaceTokenizer<EditCircularLetterPlace>{

        @Override
        public Class<EditCircularLetterPlace> getClassType() {
            return EditCircularLetterPlace.class;
        }

        @Override
        public EditCircularLetterPlace createPlace() {
            return new EditCircularLetterPlace();
        }
    }
}
