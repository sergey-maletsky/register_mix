package com.firstlinesoftware.rmrs.client.places;

import com.firstlinesoftware.base.shared.utils.ReflectablePlaceTokenizer;
import com.google.common.base.Objects;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;
import com.gwtent.reflection.client.Reflection;

public final class SendRequirementsToApprovalPlace extends Place implements Reflection {
    public String errandId;

    public String defaultPositionId;
    public String defaultRoundName;
    public Integer defaultDays;

    public SendRequirementsToApprovalPlace(String errandId) {
        this.errandId = errandId;
    }

    public SendRequirementsToApprovalPlace(String errandId, String defaultPositionId, String defaultRoundName, Integer defaultDays) {
        this.errandId = errandId;
        this.defaultPositionId = defaultPositionId;
        this.defaultRoundName = defaultRoundName;
        this.defaultDays = defaultDays;
    }

    public SendRequirementsToApprovalPlace() {
    }

    @Prefix("send-requirements-to-approval")
    public static class Tokenizer extends ReflectablePlaceTokenizer<SendRequirementsToApprovalPlace> implements PlaceTokenizer<SendRequirementsToApprovalPlace> {

        @Override
        public Class<SendRequirementsToApprovalPlace> getClassType() {
            return SendRequirementsToApprovalPlace.class;
        }

        @Override
        public SendRequirementsToApprovalPlace createPlace() {
            return new SendRequirementsToApprovalPlace();
        }
    }

    @Override
    public boolean equals(Object o) {
        return o != null && o.getClass().equals(getClass())
                && Objects.equal(errandId, ((SendRequirementsToApprovalPlace) o).errandId)
                && Objects.equal(defaultPositionId, ((SendRequirementsToApprovalPlace) o).defaultPositionId)
                && Objects.equal(defaultRoundName, ((SendRequirementsToApprovalPlace) o).defaultRoundName)
                && Objects.equal(defaultDays, ((SendRequirementsToApprovalPlace) o).defaultDays)
                ;
    }
}
