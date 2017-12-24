package com.firstlinesoftware.rmrs.client;


import com.firstlinesoftware.rmrs.client.places.*;
import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.place.shared.WithTokenizers;

/**
 * User: Vaan
 * Date: 16.11.2010
 * Time: 15:00:20
 */
@WithTokenizers({
        CreateRequirementPlace.Tokenizer.class,
        EditRequirementPlace.Tokenizer.class,
        SearchRequirementsPlace.Tokenizer.class,
        CreateProposalPlace.Tokenizer.class,
        EditProposalPlace.Tokenizer.class,
        BrowseRequirementDraftsPlace.Tokenizer.class,
        BrowseRequirementsPlace.Tokenizer.class,
        CreateCircularLetterPlace.Tokenizer.class,
        EditCircularLetterPlace.Tokenizer.class,
        CreateProposalErrandReportPlace.Tokenizer.class,
        CreateProposalErrandPlace.Tokenizer.class,
        CreateProposalChildErrandPlace.Tokenizer.class,
        ApproveProposalErrandReportPlace.Tokenizer.class,
        SendRequirementsToApprovalPlace.Tokenizer.class
})
public interface RmrsPlaceHistoryMapper extends PlaceHistoryMapper {
}
