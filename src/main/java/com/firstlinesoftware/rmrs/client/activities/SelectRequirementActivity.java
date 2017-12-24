package com.firstlinesoftware.rmrs.client.activities;

import com.firstlinesoftware.base.client.processes.SearchPersistentProcess;
import com.firstlinesoftware.base.client.services.ActionCallback;
import com.firstlinesoftware.base.shared.dto.Folder;
import com.firstlinesoftware.base.shared.dto.SearchCriteria;
import com.firstlinesoftware.ecm.client.Ecm;
import com.firstlinesoftware.ecm.client.activities.SelectDocumentsActivity;
import com.firstlinesoftware.ecm.client.places.SelectDocumentsPlace;
import com.firstlinesoftware.ecm.client.proxies.DocumentProxy;
import com.firstlinesoftware.rmrs.client.Rmrs;
import com.firstlinesoftware.rmrs.client.messages.RmrsMessages;
import com.firstlinesoftware.rmrs.client.views.selectors.SelectRequirements;
import com.firstlinesoftware.rmrs.client.views.selectors.SelectRequirementsImpl;
import com.firstlinesoftware.rmrs.client.widgets.RequirementSelector;
import com.firstlinesoftware.rmrs.shared.dto.Requirement;
import com.firstlinesoftware.rmrs.shared.dto.SearchRequirementData;

public class SelectRequirementActivity extends SelectDocumentsActivity<SearchRequirementData, Requirement, SelectRequirementsImpl> implements SelectRequirements.Presenter {

    private final RmrsMessages messages = Rmrs.getInjector().getMessages();
    protected final DocumentProxy documentProxy = Ecm.getInjector().getDocumentProxy();
    private BaseSearchRequirementActivity searchDelegate;

    private boolean singleSelection = true;
    private String lifecycle;

    public SelectRequirementActivity() {
        super(new SelectDocumentsPlace());
    }

    public SelectRequirementActivity(boolean singleSelection) {
        this();
        this.singleSelection = singleSelection;
    }

    public SelectRequirementActivity(boolean singleSelection, String lifecycle) {
        this(singleSelection);
        this.lifecycle = lifecycle;
    }

    @Override
    protected SelectRequirementsImpl createView() {
        return Rmrs.getInjector().getSelectRequirements();
    }

    @Override
    public void folderSelected(Folder folder) {
        final RequirementSelector selector = view.getFormItemWidget(messages.includedIn());
        documentProxy.get(folder.id, new ActionCallback<Requirement>("") {
            @Override
            public void onActionSuccess(Requirement result) {
                selector.setValue(result);
                view.getSelectButton().setEnabled(false);
                search();
            }
        });

    }

    @Override
    protected void stopViewer() {
    }

    @Override
    protected void setupView() {
        super.setupView();
        searchDelegate = new BaseSearchRequirementActivity(view);
        view.getDocumentsGrid().deselectAll();
        view.getDocumentsGrid().setSingleSelection(singleSelection);
    }

    @Override
    protected void setupFields(SearchRequirementData dto) {
        super.setupFields(dto);
        searchDelegate.changeMode(false);
        final RequirementSelector selector = view.getFormItemWidget(messages.includedIn());
        selector.setUseSearch(false);
        view.setFormItemWidgetValueIfEmpty(messages.lifecycle(), lifecycle);
    }

    @Override
    protected SearchCriteria fillSearchCriteria() {
        return searchDelegate.fillSearchCriteria();
    }


    @Override
    protected SearchPersistentProcess getProcess() {
        return Rmrs.getInjector().getSearchRequirementProcess();
    }
}