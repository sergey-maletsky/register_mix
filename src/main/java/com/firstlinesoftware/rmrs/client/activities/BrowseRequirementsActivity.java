package com.firstlinesoftware.rmrs.client.activities;

import com.firstlinesoftware.base.client.Base;
import com.firstlinesoftware.base.client.SuccessCallback;
import com.firstlinesoftware.base.client.activities.BrowseActivity;
import com.firstlinesoftware.base.client.activities.ViewerActivity;
import com.firstlinesoftware.base.client.browsers.Browser;
import com.firstlinesoftware.base.client.services.ActionCallback;
import com.firstlinesoftware.ecm.client.Ecm;
import com.firstlinesoftware.ecm.client.activities.ViewDocumentActivity;
import com.firstlinesoftware.ecm.client.proxies.DocumentProxy;
import com.firstlinesoftware.rmrs.client.Rmrs;
import com.firstlinesoftware.rmrs.client.browsers.RequirementsBrowser;
import com.firstlinesoftware.rmrs.client.events.RequirementsSelectionChangedEvent;
import com.firstlinesoftware.rmrs.client.messages.RmrsMessages;
import com.firstlinesoftware.rmrs.client.places.BrowseRequirementsPlace;
import com.firstlinesoftware.rmrs.shared.dto.Requirement;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class BrowseRequirementsActivity extends BrowseActivity<Requirement> {
    private final RmrsMessages messages = Rmrs.getInjector().getMessages();
    private final DocumentProxy documentProxy = Ecm.getInjector().getDocumentProxy();


    public BrowseRequirementsActivity(BrowseRequirementsPlace place) {
        super(place.id);
    }

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    protected void setupView(AcceptsOneWidget panel) {
        super.setupView(panel);
        setupCommands(Requirement.KIND);
    }

    @Override
    protected void createBrowser(final SuccessCallback<Browser<Requirement>> successCallback) {
        final RequirementsBrowser result = new RequirementsBrowser();
        documentProxy.get(itemId, new ActionCallback<Requirement>(messages.errorWhileGettingDocument()) {
            @Override
            public void onActionSuccess(Requirement r) {
                if (r != null) {
                    Base.getInjector().getDesktop().setTitle(r.getTitle());
                }
                result.setParentRequirement(r);
                successCallback.onSuccess(result);
            }
        });
    }

    @Override
    protected ViewerActivity createViewerActivity() {
        return new ViewDocumentActivity<>();
    }

    @Override
    public void onItemSelected() {
        super.onItemSelected();
        itemId = browser.getSelectedObject().id;
        eventBus.fireEvent(new RequirementsSelectionChangedEvent(itemId));
        setupViewer();
    }
}
