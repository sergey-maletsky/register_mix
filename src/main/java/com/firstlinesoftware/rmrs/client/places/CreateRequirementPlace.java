package com.firstlinesoftware.rmrs.client.places;

import com.firstlinesoftware.base.shared.utils.ReflectablePlaceTokenizer;
import com.firstlinesoftware.rmrs.shared.dto.Requirement;
import com.google.common.base.Objects;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;
import com.gwtent.reflection.client.Reflection;

/**
 * User: Vaan
 * Date: 16.11.2010
 * Time: 14:31:38
 */
public final class CreateRequirementPlace extends Place implements Reflection {
    public String parentId;
    public Requirement template;
    public String errandId;
    public boolean rootRequirement;

    public CreateRequirementPlace(boolean rootRequirement) {
        this.rootRequirement = rootRequirement;
    }

    public CreateRequirementPlace(String parentId) {
        this.parentId = parentId;
    }

    public CreateRequirementPlace(Requirement template, String id) {
        this.template = template;
        errandId = id;
    }

    @Prefix("create-requirement")
    public static class Tokenizer extends ReflectablePlaceTokenizer<CreateRequirementPlace> implements PlaceTokenizer<CreateRequirementPlace> {

        @Override
        public Class<CreateRequirementPlace> getClassType() {
            return CreateRequirementPlace.class;
        }


        @Override
        public CreateRequirementPlace createPlace() {
            return new CreateRequirementPlace(false);
        }
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof CreateRequirementPlace
                && Objects.equal(parentId, ((CreateRequirementPlace) o).parentId)
                && Objects.equal(errandId, ((CreateRequirementPlace) o).errandId)
                && Objects.equal(template, ((CreateRequirementPlace) o).template)
                ;
    }
}
