package com.firstlinesoftware.rmrs.client.views.selectors;

import com.firstlinesoftware.base.client.models.SearchPersistentsModel;
import com.firstlinesoftware.base.client.views.HasEditorFields;
import com.firstlinesoftware.base.client.views.selectors.SearchPersistentImpl;
import com.firstlinesoftware.base.client.widgets.TreeTableViewer;
import com.firstlinesoftware.ecm.client.models.SearchDocumentsModel;
import com.firstlinesoftware.rmrs.client.Rmrs;
import com.firstlinesoftware.rmrs.client.columns.RequirementColumns;
import com.firstlinesoftware.rmrs.client.messages.RmrsMessages;
import com.firstlinesoftware.rmrs.shared.dto.Requirement;
import com.firstlinesoftware.rmrs.shared.dto.SearchRequirementData;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Button;

public class SearchRequirementsImpl extends SearchPersistentImpl<SearchRequirementData, Requirement> implements SearchRequirements, HasEditorFields {
    public boolean extendedMode;
    private Button changeMode;

    @Override
    protected void createUI(UiBinder binder) {
        super.createUI(binder);
        final RmrsMessages messages = Rmrs.getInjector().getMessages();
        changeMode = new Button(messages.extendedSearch() + ">>", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                extendedMode = !extendedMode;
                changeMode.setText(extendedMode ? messages.simpleSearch() + "<<" : messages.extendedSearch() + ">>");
                getPresenter().changeMode(extendedMode);
            }
        });
        addButton(changeMode);
    }

    @Override
    protected SearchPersistentsModel<Requirement> createModel() {
        return new SearchDocumentsModel<>();
    }

    @Override
    protected TreeTableViewer<Requirement> createTable() {
        final TreeTableViewer<Requirement> table = super.createTable();
        table.setClassName(RequirementColumns.ROW_CLASS);
//        table.setHoverClassName(RequirementColumns.GRID_ROW_CLASS);
        return table;
    }

    @Override
    public SearchRequirements.Presenter getPresenter() {
        return (SearchRequirements.Presenter) super.getPresenter();
    }


}
