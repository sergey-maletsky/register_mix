package com.firstlinesoftware.rmrs.client.places;

import com.firstlinesoftware.base.shared.utils.ReflectablePlaceTokenizer;
import com.firstlinesoftware.ecm.client.places.AbstractEditCompositeDocumentPlace;
import com.google.common.base.Objects;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;
import com.gwtent.reflection.client.Reflection;

/**
 * Author: MPhilippov
 * Time: 20.06.2012
 */

public class EditRequirementPlace extends AbstractEditCompositeDocumentPlace implements Reflection {

    public String errandId;

    public EditRequirementPlace() {
    }

    public EditRequirementPlace(String id, String errandId) {
        super(id);
        this.errandId = errandId;
    }

    @Prefix("edit-requirement")
    public static class Tokenizer extends ReflectablePlaceTokenizer<EditRequirementPlace> implements PlaceTokenizer<EditRequirementPlace> {

        @Override
        public Class<EditRequirementPlace> getClassType() {
            return EditRequirementPlace.class;
        }

        @Override
        public EditRequirementPlace createPlace() {
            return new EditRequirementPlace();
        }
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof EditRequirementPlace && Objects.equal(((EditRequirementPlace) o).errandId, errandId);
    }

    @Override
    public AbstractEditCompositeDocumentPlace createInstance() {
        return new EditRequirementPlace();
    }
}
