package com.firstlinesoftware.rmrs.client.places;

import com.firstlinesoftware.base.shared.utils.ReflectablePlaceTokenizer;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;
import com.gwtent.reflection.client.Reflection;

public final class CreateCircularLetterPlace extends Place implements Reflection {
    @Prefix("create-circular")
    public static class Tokenizer extends ReflectablePlaceTokenizer<CreateCircularLetterPlace> implements PlaceTokenizer<CreateCircularLetterPlace> {

        @Override
        public Class<CreateCircularLetterPlace> getClassType() {
            return CreateCircularLetterPlace.class;
        }


        @Override
        public CreateCircularLetterPlace createPlace() {
            return new CreateCircularLetterPlace();
        }
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof CreateCircularLetterPlace;
    }
}
