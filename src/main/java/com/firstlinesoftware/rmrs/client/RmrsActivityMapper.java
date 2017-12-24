package com.firstlinesoftware.rmrs.client;

import com.firstlinesoftware.base.client.RunCallback;
import com.firstlinesoftware.base.client.SuccessCallback;
import com.firstlinesoftware.base.client.activities.AsyncActivityMapper;
import com.firstlinesoftware.exec.client.places.RejectErrandReportPlace;
import com.firstlinesoftware.rmrs.client.activities.*;
import com.firstlinesoftware.rmrs.client.places.*;
import com.google.gwt.activity.shared.Activity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;

public class RmrsActivityMapper implements AsyncActivityMapper {

    @Override
    public Activity getActivity(Place place) {
        if(place instanceof BrowseRequirementDraftsPlace) {
            return new BrowseRequirementDraftActivity(((BrowseRequirementDraftsPlace) place));
        }
        if(place instanceof BrowseRequirementsPlace) {
            return new BrowseRequirementsActivity(((BrowseRequirementsPlace) place));
        }
        if(place instanceof SearchRequirementsPlace) {
            return new SearchRequirementActivity(((SearchRequirementsPlace) place));
        }
        if(place instanceof CreateRequirementPlace) {
            return new EditRequirementActivity(((CreateRequirementPlace) place));
        }
        if(place instanceof EditRequirementPlace) {
            return new EditRequirementActivity(((EditRequirementPlace) place));
        }
        if(place instanceof CreateProposalPlace) {
            return new EditProposalActivity(((CreateProposalPlace) place));
        }
        if(place instanceof EditProposalPlace) {
            return new EditProposalActivity(((EditProposalPlace) place));
        }
        if(place instanceof CreateCircularLetterPlace) {
            return new EditCircularLetterActivity(((CreateCircularLetterPlace) place));
        }
        if(place instanceof EditCircularLetterPlace) {
            return new EditCircularLetterActivity(((EditCircularLetterPlace) place));
        }
        if(place instanceof CreateProposalErrandReportPlace) {
            return new CreateProposalErrandReportActivity((CreateProposalErrandReportPlace) place);
        }
        if(place instanceof ApproveProposalErrandReportPlace) {
            return new ApproveProposalErrandReportActivity ((ApproveProposalErrandReportPlace) place);
        }
        if(place instanceof RejectErrandReportPlace) {
            return new RejectErrandReportActivity((RejectErrandReportPlace) place);
        }
        if(place instanceof SendRequirementsToApprovalPlace) {
            return new SendRequirementsToApprovalActivity((SendRequirementsToApprovalPlace) place);
        }
        if(place instanceof AddResponsibilityForRequirementPlace) {
            return new AddResponsibilityForRequirementsActivity ((AddResponsibilityForRequirementPlace) place);
        }
        if(place instanceof RemoveResponsibilityForRequirementPlace) {
            return new RemoveResponsibilityForRequirementsActivity ((RemoveResponsibilityForRequirementPlace) place);
        }
        if (place instanceof CreateProposalChildErrandPlace) {
            return new CreateProposalErrandActivity((CreateProposalChildErrandPlace) place);
        }
        if (place instanceof CreateProposalErrandPlace) {
            return new CreateProposalErrandActivity((CreateProposalErrandPlace) place);
        }
        return null;
    }

    @Override
    public void getActivity(final Place place, final SuccessCallback<Activity> callback) {
        GWT.runAsync(new RunCallback() {
            @Override
            public void onSuccess() {
                callback.onSuccess(getActivity(place));
            }
        });
    }
}
