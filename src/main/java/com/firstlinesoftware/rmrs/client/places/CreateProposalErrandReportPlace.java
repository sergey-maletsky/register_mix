package com.firstlinesoftware.rmrs.client.places;

import com.firstlinesoftware.base.shared.utils.ReflectablePlaceTokenizer;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;
import com.gwtent.reflection.client.Reflection;

/**
 * User: Vaan
 * Date: 16.11.2010
 * Time: 14:31:38
 */
public class CreateProposalErrandReportPlace extends Place implements Reflection {

    public boolean accepted;
    public String parentErrand;
    public String executorId;

    public CreateProposalErrandReportPlace() {
    }

    public CreateProposalErrandReportPlace(boolean accepted, String errandID, String executorId){
        this.accepted = accepted;
        this.parentErrand = errandID;
        this.executorId = executorId;
    }

    @Prefix("create-proposal-errand-report")
    public static class Tokenizer extends ReflectablePlaceTokenizer<CreateProposalErrandReportPlace> implements PlaceTokenizer<CreateProposalErrandReportPlace> {

        @Override
        public Class<CreateProposalErrandReportPlace> getClassType() {
            return CreateProposalErrandReportPlace.class;
        }

        @Override
        public CreateProposalErrandReportPlace createPlace() {
            return new CreateProposalErrandReportPlace();
        }
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof CreateProposalErrandReportPlace;
    }
}
