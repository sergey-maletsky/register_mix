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
public class ApproveProposalErrandReportPlace extends Place implements Reflection{
    public String errandId;
    public String reportId;
    public String positionId;

    public ApproveProposalErrandReportPlace(String errandId, String reportId, String positionId) {
        this.errandId = errandId;
        this.reportId = reportId;
        this.positionId = positionId;
    }

    public ApproveProposalErrandReportPlace() {
    }

    @Prefix("approve-proposal-errand-report")
    public static class Tokenizer extends ReflectablePlaceTokenizer<ApproveProposalErrandReportPlace> implements PlaceTokenizer<ApproveProposalErrandReportPlace> {

        @Override
        public Class<ApproveProposalErrandReportPlace> getClassType() {
            return ApproveProposalErrandReportPlace.class;
        }

        @Override
        public ApproveProposalErrandReportPlace createPlace() {
            return new ApproveProposalErrandReportPlace();
        }
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof ApproveProposalErrandReportPlace
                && errandId != null && errandId.equals(((ApproveProposalErrandReportPlace) o).errandId)
                && reportId != null && reportId.equals(((ApproveProposalErrandReportPlace) o).reportId)
                && positionId != null && positionId.equals(((ApproveProposalErrandReportPlace) o).positionId);
    }
}
