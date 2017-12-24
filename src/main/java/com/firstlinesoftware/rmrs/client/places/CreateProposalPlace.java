package com.firstlinesoftware.rmrs.client.places;

import com.firstlinesoftware.base.shared.utils.ReflectablePlaceTokenizer;
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
public final class CreateProposalPlace extends Place implements Reflection {

    public String relationType;
    public String requirementId;

    public CreateProposalPlace(String relationType, String requirementId) {
        this.relationType = relationType;
        this.requirementId = requirementId;
    }

    public CreateProposalPlace() {
    }

    @Prefix("create-proposal")
    public static class Tokenizer extends ReflectablePlaceTokenizer<CreateProposalPlace> implements PlaceTokenizer<CreateProposalPlace> {

        @Override
        public Class<CreateProposalPlace> getClassType() {
            return CreateProposalPlace.class;
        }


        @Override
        public CreateProposalPlace createPlace() {
            return new CreateProposalPlace();
        }
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof CreateProposalPlace
                && Objects.equal(relationType, ((CreateProposalPlace) o).relationType)
                && Objects.equal(requirementId, ((CreateProposalPlace) o).requirementId)
                ;
    }
}
