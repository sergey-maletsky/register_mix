package com.firstlinesoftware.rmrs.client.views.selectors;

import com.firstlinesoftware.base.client.models.NavigatorModel;
import com.firstlinesoftware.base.client.models.SearchPersistentsModel;
import com.firstlinesoftware.base.client.widgets.TreeTableViewer;
import com.firstlinesoftware.base.shared.dto.Folder;
import com.firstlinesoftware.ecm.client.views.selectors.SelectDocumentImpl;
import com.firstlinesoftware.rmrs.client.columns.RequirementColumns;
import com.firstlinesoftware.rmrs.client.models.RequirementsNavigatorModel;
import com.firstlinesoftware.rmrs.client.models.RequirementsTreeTableModel;
import com.firstlinesoftware.rmrs.client.views.RequirementsTreeTableViewer;
import com.firstlinesoftware.rmrs.shared.dto.Requirement;
import com.firstlinesoftware.rmrs.shared.dto.SearchRequirementData;
import com.google.gwt.view.client.SelectionChangeEvent;

public class SelectRequirementsImpl extends SelectDocumentImpl<SearchRequirementData, Requirement> {
    @Override
    protected NavigatorModel createFoldersModel() {
        final RequirementsNavigatorModel model = new RequirementsNavigatorModel();
        model.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange(SelectionChangeEvent event) {
                final Requirement selected = model.getSelectedFolder();
                getPresenter().folderSelected(new Folder(selected.id));
            }
        });
        return model;
    }

    @Override
    protected SearchPersistentsModel<Requirement> createModel() {
        return new RequirementsTreeTableModel();
    }

    @Override
    protected TreeTableViewer<Requirement> createTable() {
        final TreeTableViewer<Requirement> table = new RequirementsTreeTableViewer((RequirementsTreeTableModel) searchModel);
        table.setClassName(RequirementColumns.ROW_CLASS);
        table.setHoverClassName(RequirementColumns.ROW_CLASS);
        table.setHasSearchPanel(false);
        table.setPlain(true);
        return table;
    }
}
