package com.firstlinesoftware.rmrs.client.places;

import com.firstlinesoftware.base.shared.utils.ReflectablePlaceTokenizer;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;
import com.gwtent.reflection.client.Reflection;

public class CreateProposalErrandPlace extends Place implements Reflection {
    public String proposalId;
    public String authorId;

    public CreateProposalErrandPlace() {
    }

    public CreateProposalErrandPlace(String proposalId, String authorId) {
        this.proposalId = proposalId;
        this.authorId = authorId;
    }

    @Prefix("create-proposal-errand")
    public static class Tokenizer extends ReflectablePlaceTokenizer<CreateProposalErrandPlace> implements PlaceTokenizer<CreateProposalErrandPlace> {


        @Override
        public Class<CreateProposalErrandPlace> getClassType() {
            return CreateProposalErrandPlace.class;
        }

        @Override
        public CreateProposalErrandPlace createPlace() {
            return new CreateProposalErrandPlace();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CreateProposalErrandPlace that = (CreateProposalErrandPlace) o;

        if (authorId != null ? !authorId.equals(that.authorId) : that.authorId != null) {
            return false;
        }
        if (proposalId != null ? !proposalId.equals(that.proposalId) : that.proposalId != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = proposalId != null ? proposalId.hashCode() : 0;
        result = 31 * result + (authorId != null ? authorId.hashCode() : 0);
        return result;
    }
}
