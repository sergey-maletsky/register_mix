package com.firstlinesoftware.rmrs.client.activities;

import com.firstlinesoftware.base.client.activities.AbstractFormActivity;
import com.firstlinesoftware.base.client.services.ActionCallback;
import com.firstlinesoftware.base.client.utils.Utils;
import com.firstlinesoftware.orgstruct.client.Orgstruct;
import com.firstlinesoftware.orgstruct.client.proxies.OrgstructureProxy;
import com.firstlinesoftware.orgstruct.shared.dto.Position;
import com.firstlinesoftware.rmrs.client.Rmrs;
import com.firstlinesoftware.rmrs.client.messages.RmrsMessages;
import com.firstlinesoftware.rmrs.client.places.RemoveResponsibilityForRequirementPlace;
import com.firstlinesoftware.rmrs.client.proxies.RequirementProxy;
import com.firstlinesoftware.rmrs.client.views.selectors.SelectRequirementsForDelete;
import com.firstlinesoftware.rmrs.client.views.selectors.SelectRequirementsForDeleteImpl;
import com.firstlinesoftware.rmrs.shared.dto.Requirement;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import java.util.ArrayList;
import java.util.Set;

public class RemoveResponsibilityForRequirementsActivity
        extends AbstractFormActivity<Position, SelectRequirementsForDeleteImpl>
        implements SelectRequirementsForDelete.Presenter {

    private final RmrsMessages messages = Rmrs.getInjector().getMessages();
    private OrgstructureProxy orgstructureProxy = Orgstruct.getInjector().getOrgstructureProxy();
    private final RequirementProxy requirementProxy = Rmrs.getInjector().getRequirementProxy();


    private String positionId;

    public RemoveResponsibilityForRequirementsActivity() {
        super();
    }

    public RemoveResponsibilityForRequirementsActivity(RemoveResponsibilityForRequirementPlace place) {
        this();
        positionId = place.positionId;
    }


    @Override
    public void start(final AcceptsOneWidget panel,final EventBus eventBus) {
        orgstructureProxy.getPosition(positionId, new ActionCallback<Position>("Error getting position") {
            @Override
            public void onActionSuccess(Position result) {
                dto = result;
                RemoveResponsibilityForRequirementsActivity.super.start(panel, eventBus);
                setupEditor(panel, dto);
            }
        });

    }

    @Override
    protected SelectRequirementsForDeleteImpl createView() {

        return Rmrs.getInjector().getSelectRequirementsForDelete();
    }

    public void onResultSuccess(Set<Requirement> selection) {
        ArrayList<Requirement> documents = new ArrayList<>();
        for (Requirement requirement : selection) {
            requirement.responsible = null;
            documents.add(requirement);
        }
        requirementProxy.removeResponsibilityForRequirements(documents, new ActionCallback<Void>("Update error") {
            @Override
            public void onActionSuccess(Void aVoid) {
                Utils.back();
            }
        });
    }



    public void onResultCancel() {

        Utils.back();
    }

    @Override
    public String getTitle() {

        return messages.removeResponsibilityForRequirements();
    }

    @Override
    protected void setupEditor(AcceptsOneWidget panel, Position dto) {

        super.setupEditor(panel, dto);
    }

    @Override
    protected void setupView() {

        super.setupView();
        view.searchModel.setParent(dto);
    }


    @Override
    public void select(Set<Requirement> requirements) {
        onResultSuccess(requirements);
    }

    @Override
    public void cancel() {
        onResultCancel();
    }

}