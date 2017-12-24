package com.firstlinesoftware.rmrs.client.places;

import com.firstlinesoftware.base.shared.utils.ReflectablePlaceTokenizer;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;
import com.gwtent.reflection.client.Reflection;

public class AddResponsibilityForRequirementPlace extends Place implements Reflection{
    public String positionId;

    public AddResponsibilityForRequirementPlace(String positionId) {
        this.positionId = positionId;
    }

    public AddResponsibilityForRequirementPlace() {
    }

    @Prefix("add-responsibility-for_requirement")
    public static class Tokenizer extends ReflectablePlaceTokenizer<AddResponsibilityForRequirementPlace>
            implements PlaceTokenizer<AddResponsibilityForRequirementPlace> {

        @Override
        public Class<AddResponsibilityForRequirementPlace> getClassType() {
            return AddResponsibilityForRequirementPlace.class;
        }

        @Override
        public AddResponsibilityForRequirementPlace createPlace() {
            return new AddResponsibilityForRequirementPlace();
        }
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof AddResponsibilityForRequirementPlace
                && positionId != null && positionId.equals(((AddResponsibilityForRequirementPlace) o).positionId);
    }
}
