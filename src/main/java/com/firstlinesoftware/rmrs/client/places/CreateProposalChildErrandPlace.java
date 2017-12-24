package com.firstlinesoftware.rmrs.client.places;

import com.firstlinesoftware.base.shared.utils.ReflectablePlaceTokenizer;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;
import com.gwtent.reflection.client.Reflection;

public class CreateProposalChildErrandPlace extends Place implements Reflection {
    public String parentId;
    public String authorId;

    public CreateProposalChildErrandPlace() {
    }

    public CreateProposalChildErrandPlace(String parentId, String authorId) {
        this.parentId = parentId;
        this.authorId = authorId;
    }

    @Prefix("create-proposal-child-errand")
    public static class Tokenizer extends ReflectablePlaceTokenizer<CreateProposalChildErrandPlace> implements PlaceTokenizer<CreateProposalChildErrandPlace> {


        @Override
        public Class<CreateProposalChildErrandPlace> getClassType() {
            return CreateProposalChildErrandPlace.class;
        }

        @Override
        public CreateProposalChildErrandPlace createPlace() {
            return new CreateProposalChildErrandPlace();
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

        CreateProposalChildErrandPlace that = (CreateProposalChildErrandPlace) o;

        if (authorId != null ? !authorId.equals(that.authorId) : that.authorId != null) {
            return false;
        }
        if (parentId != null ? !parentId.equals(that.parentId) : that.parentId != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = parentId != null ? parentId.hashCode() : 0;
        result = 31 * result + (authorId != null ? authorId.hashCode() : 0);
        return result;
    }
}
