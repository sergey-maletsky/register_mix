package com.firstlinesoftware.rmrs.client.places;

import com.firstlinesoftware.base.shared.utils.ReflectablePlaceTokenizer;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;
import com.gwtent.reflection.client.Reflection;

public final class SearchRequirementsPlace extends Place implements Reflection {
    @Prefix("search-requirements")
    public static class Tokenizer extends ReflectablePlaceTokenizer<SearchRequirementsPlace> implements PlaceTokenizer<SearchRequirementsPlace> {

        @Override
        public Class<SearchRequirementsPlace> getClassType() {
            return SearchRequirementsPlace.class;
        }


        @Override
        public SearchRequirementsPlace createPlace() {
            return new SearchRequirementsPlace();
        }
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof SearchRequirementsPlace;
    }
}
