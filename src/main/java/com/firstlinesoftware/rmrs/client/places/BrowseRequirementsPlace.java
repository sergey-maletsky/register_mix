package com.firstlinesoftware.rmrs.client.places;

import com.firstlinesoftware.base.client.places.HasItemId;
import com.firstlinesoftware.base.shared.utils.ReflectablePlaceTokenizer;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;
import com.gwtent.reflection.client.Reflection;

public class BrowseRequirementsPlace extends Place implements HasItemId, Reflection {
    public String id;
    public String documentId;

    public BrowseRequirementsPlace() {
    }

    public BrowseRequirementsPlace(String id) {
        this.id = id;
    }

    public BrowseRequirementsPlace(String id, String documentId) {
        this.id = id;
        this.documentId = documentId;
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
        return new BrowseRequirementsPlace(id,  documentId);
    }

    @Prefix("requirements")
    public static class Tokenizer extends ReflectablePlaceTokenizer<BrowseRequirementsPlace> implements PlaceTokenizer<BrowseRequirementsPlace> {

        @Override
        public Class<BrowseRequirementsPlace> getClassType() {
            return BrowseRequirementsPlace.class;
        }

        @Override
        public BrowseRequirementsPlace createPlace() {
            return new BrowseRequirementsPlace();
        }
    }

    @Override
    public boolean equals(Object o) {
        return o != null && o.getClass().equals(this.getClass())
                && ((BrowseRequirementsPlace) o).id.equals(id)
                && (documentId == null ? ((BrowseRequirementsPlace) o).documentId == null : documentId.equals(((BrowseRequirementsPlace) o).documentId));
    }
}
