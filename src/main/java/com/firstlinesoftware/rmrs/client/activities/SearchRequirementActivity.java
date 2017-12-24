package com.firstlinesoftware.rmrs.client.activities;

import com.firstlinesoftware.base.client.activities.SearchPersistentActivity;
import com.firstlinesoftware.base.client.activities.ViewerActivity;
import com.firstlinesoftware.base.client.processes.SearchPersistentProcess;
import com.firstlinesoftware.base.client.views.selectors.SearchPersistentImpl;
import com.firstlinesoftware.base.shared.dto.SearchCriteria;
import com.firstlinesoftware.ecm.client.activities.ViewDocumentActivity;
import com.firstlinesoftware.rmrs.client.Rmrs;
import com.firstlinesoftware.rmrs.client.places.SearchRequirementsPlace;
import com.firstlinesoftware.rmrs.client.views.selectors.SearchRequirements;
import com.firstlinesoftware.rmrs.shared.dto.Requirement;
import com.firstlinesoftware.rmrs.shared.dto.SearchRequirementData;

public class SearchRequirementActivity<V extends SearchPersistentImpl<SearchRequirementData, Requirement>> extends SearchPersistentActivity<SearchRequirementData, Requirement, V> implements SearchRequirements.Presenter {
    private BaseSearchRequirementActivity delegate;

    public SearchRequirementActivity(SearchRequirementsPlace place) {
        super(null);
    }

    @Override
    protected SearchPersistentProcess getProcess() {
        return Rmrs.getInjector().getSearchRequirementProcess();
    }

    @Override
    public String getTitle() {
        return Rmrs.getInjector().getMessages().search();
    }

    @Override
    protected V createView() {
        return (V) Rmrs.getInjector().getSearchRequirements();
    }

    @Override
    protected void setupFields(SearchRequirementData dto) {
        super.setupFields(dto);
        changeMode(false);
    }

    @Override
    protected void setupView() {
        super.setupView();
        delegate = new BaseSearchRequirementActivity(view);
        setupTitle();
    }

    @Override
    protected ViewerActivity createViewerActivity() {
        return new ViewDocumentActivity<>();
    }

    @Override
    protected SearchCriteria fillSearchCriteria() {
        return delegate.fillSearchCriteria();
    }

    @Override
    public void changeMode(boolean extended) {
        delegate.changeMode(extended);
    }
}
