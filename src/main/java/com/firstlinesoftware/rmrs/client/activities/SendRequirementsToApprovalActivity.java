package com.firstlinesoftware.rmrs.client.activities;

import com.firstlinesoftware.base.client.services.ActionCallback;
import com.firstlinesoftware.base.client.services.UserActionCallback;
import com.firstlinesoftware.ecm.client.Ecm;
import com.firstlinesoftware.ecm.client.proxies.DocumentProxy;
import com.firstlinesoftware.rmrs.client.Rmrs;
import com.firstlinesoftware.rmrs.client.messages.RmrsMessages;
import com.firstlinesoftware.rmrs.client.places.SendRequirementsToApprovalPlace;
import com.firstlinesoftware.rmrs.client.proxies.RequirementProxy;
import com.firstlinesoftware.rmrs.shared.dto.ChangeRequirementErrand;
import com.firstlinesoftware.route.client.activities.SendToApprovalActivity;
import com.firstlinesoftware.route.client.places.SendToApprovalPlace;
import com.firstlinesoftware.route.shared.dto.Rounds;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class SendRequirementsToApprovalActivity extends SendToApprovalActivity {
    private final DocumentProxy documentProxy = Ecm.getInjector().getDocumentProxy();
    private final RequirementProxy requirementProxy = Rmrs.getInjector().getRequirementProxy();
    private final RmrsMessages messages = Rmrs.getInjector().getMessages();

    private final String errandId;
    private ChangeRequirementErrand errand;

    public SendRequirementsToApprovalActivity(SendRequirementsToApprovalPlace place) {
        super(new SendToApprovalPlace(place.errandId, place.defaultPositionId, place.defaultRoundName, place.defaultDays));
        errandId = place.errandId;
    }

    @Override
    public void start(final AcceptsOneWidget panel, final EventBus eventBus) {
        if(errandId != null) {
            documentProxy.get(errandId, new ActionCallback<ChangeRequirementErrand>(messages.errorWhileGettingDocument()) {
                @Override
                public void onActionSuccess(ChangeRequirementErrand errand) {
                    SendRequirementsToApprovalActivity.this.errand = errand;
                    SendRequirementsToApprovalActivity.super.start(panel, eventBus);
                }
            });
        } else {
            super.start(panel, eventBus);
        }
    }

    @Override
    public void save() {
        if (validate()) {
            requirementProxy.sendRequirementsToApproval(errand.collectCompleted(), new Rounds(view.rounds.getValue()), new UserActionCallback<Void>(
                    messages.documentSentToApproval(),
                    messages.errorWhileUpdatingDocument()) {
                @Override
                public void onActionSuccess(Void result) {
                    onResultCancel();
                }
            });
        }

    }
}
