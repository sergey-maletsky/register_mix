package com.firstlinesoftware.rmrs.client.places;

import com.firstlinesoftware.base.client.places.HasItemId;
import com.firstlinesoftware.base.shared.utils.ReflectablePlaceTokenizer;
import com.google.common.base.Objects;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;
import com.gwtent.reflection.client.Reflection;

public class BrowseRequirementDraftsPlace extends Place implements HasItemId, Reflection {
    public String id;
    public String positionId;
    public String documentId;

    public BrowseRequirementDraftsPlace() {
    }

    public BrowseRequirementDraftsPlace(String id, String positionId) {
        this.id = id;
        this.positionId = positionId;
    }

    public String getId() {
        return id;
    }

    @Override
    public void setItemId(String itemId) {
        documentId = itemId;
    }

    @Override
    public String getItemId() {
        return documentId;
    }

    @Override
    public HasItemId clone() {
        return new BrowseRequirementDraftsPlace(id,  documentId);
    }

    @Prefix("requirement-drafts")
    public static class Tokenizer extends ReflectablePlaceTokenizer<BrowseRequirementDraftsPlace> implements PlaceTokenizer<BrowseRequirementDraftsPlace> {

        @Override
        public Class<BrowseRequirementDraftsPlace> getClassType() {
            return BrowseRequirementDraftsPlace.class;
        }

        @Override
        public BrowseRequirementDraftsPlace createPlace() {
            return new BrowseRequirementDraftsPlace();
        }
    }

    @Override
    public boolean equals(Object o) {
        return o != null && o.getClass().equals(this.getClass())
                && Objects.equal(id, ((BrowseRequirementDraftsPlace) o).id)
                && Objects.equal(positionId, ((BrowseRequirementDraftsPlace) o).positionId)
                && Objects.equal(documentId, ((BrowseRequirementDraftsPlace) o).documentId);
    }
}
