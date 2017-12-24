package com.firstlinesoftware.rmrs.client.activities;

import com.firstlinesoftware.base.client.services.ActionCallback;
import com.firstlinesoftware.orgstruct.client.Orgstruct;
import com.firstlinesoftware.orgstruct.client.proxies.OrgstructureProxy;
import com.firstlinesoftware.orgstruct.shared.dto.Position;
import com.firstlinesoftware.orgstruct.shared.dto.UserProfile;
import com.firstlinesoftware.rmrs.client.Rmrs;
import com.firstlinesoftware.rmrs.client.messages.RmrsMessages;
import com.firstlinesoftware.rmrs.client.places.AddResponsibilityForRequirementPlace;
import com.firstlinesoftware.rmrs.client.proxies.RequirementProxy;
import com.firstlinesoftware.rmrs.shared.dto.Requirement;

import java.util.Set;

public class AddResponsibilityForRequirementsActivity extends SelectRequirementActivity {
    private String positionId;
    private OrgstructureProxy orgstructureProxy = Orgstruct.getInjector().getOrgstructureProxy();
    private RmrsMessages messages = Rmrs.getInjector().getMessages();
    private final RequirementProxy requirementProxy = Rmrs.getInjector().getRequirementProxy();

    public AddResponsibilityForRequirementsActivity() {
        super(false);
    }

    public AddResponsibilityForRequirementsActivity(AddResponsibilityForRequirementPlace place) {
        this();
        positionId = place.positionId;
    }

    @Override
    public String getTitle() {
        return messages.addResponsibilityForRequirements();
    }

    @Override
    public void onResultSuccess(final Set<Requirement> selection) {
        orgstructureProxy.getPosition(positionId, new ActionCallback<Position>(messages.errorWhileGettingResponsible()) {
            @Override
            public void onActionSuccess(final Position result) {
                final UserProfile currentUserProfile = orgstructureProxy.getUserProfile();

                for (Requirement requirement : selection) {
                    requirement.responsible = result;

                    requirementProxy.falsificate(requirement, new ActionCallback<Void>(messages.errorWhileUpdatingDocument()) {
                                @Override
                                public void onActionSuccess(Void aVoid) {
                                }
                            });
                }
                AddResponsibilityForRequirementsActivity.super.onResultSuccess(selection);
            }
        });
    }
}
