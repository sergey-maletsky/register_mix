package com.firstlinesoftware.rmrs.client.places;

import com.firstlinesoftware.base.shared.utils.ReflectablePlaceTokenizer;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;
import com.gwtent.reflection.client.Reflection;

public class RemoveResponsibilityForRequirementPlace extends Place implements Reflection{
    public String positionId;

    public RemoveResponsibilityForRequirementPlace(String positionId) {
        this.positionId = positionId;
    }

    public RemoveResponsibilityForRequirementPlace() {
    }

    @Prefix("remove-responsibility-for_requirement")
    public static class Tokenizer extends ReflectablePlaceTokenizer<RemoveResponsibilityForRequirementPlace>
            implements PlaceTokenizer<RemoveResponsibilityForRequirementPlace> {

        @Override
        public Class<RemoveResponsibilityForRequirementPlace> getClassType() {
            return RemoveResponsibilityForRequirementPlace.class;
        }

        @Override
        public RemoveResponsibilityForRequirementPlace createPlace() {
            return new RemoveResponsibilityForRequirementPlace();
        }
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof RemoveResponsibilityForRequirementPlace
                && positionId != null && positionId.equals(((RemoveResponsibilityForRequirementPlace) o).positionId);
    }
}
