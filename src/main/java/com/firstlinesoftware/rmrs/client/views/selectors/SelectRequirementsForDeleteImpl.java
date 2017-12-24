package com.firstlinesoftware.rmrs.client.views.selectors;

import com.firstlinesoftware.base.client.views.FormViewImpl;
import com.firstlinesoftware.base.client.widgets.Form;
import com.firstlinesoftware.base.client.widgets.TreeTableViewer;
import com.firstlinesoftware.orgstruct.shared.dto.Position;
import com.firstlinesoftware.rmrs.client.columns.RequirementColumns;
import com.firstlinesoftware.rmrs.client.models.RequirementsListModel;
import com.firstlinesoftware.rmrs.shared.dto.Requirement;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.view.client.SelectionChangeEvent;

public class SelectRequirementsForDeleteImpl extends FormViewImpl<Position> {
    @UiField
    public Button select;
    @UiField
    public Button cancel;
    @Ignore
    @UiField(provided = true)
    public TreeTableViewer<Requirement> requirementsViewer;
    public RequirementsListModel searchModel;

    @UiHandler({"select"})
    public void onClickSelect(ClickEvent e) {
        getPresenter().select(requirementsViewer.getSelectedSet());
    }

    @UiHandler({"cancel"})
    public void onClickCancel(ClickEvent e) {
        getPresenter().cancel();
    }

    @Override
    protected void createUI(UiBinder binder) {
        searchModel = createModel();
        requirementsViewer = createTable();
        super.createUI(GWT.<UiBinder>create(SelectRequirementsForDeleteImpl.ViewUiBinder.class));
        requirementsViewer.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange(SelectionChangeEvent event) {
                select.setEnabled(requirementsViewer.getSelectedSet().size() > 0);
            }
        });

    }

    protected TreeTableViewer<Requirement> createTable() {
        final TreeTableViewer table = new TreeTableViewer(searchModel);
        table.setClassName(RequirementColumns.ROW_CLASS);
        table.setHoverClassName(RequirementColumns.ROW_CLASS);
        table.setHasSearchPanel(false);
        table.setPlain(true);
        table.setSingleSelection(false);
        return table;
    }

    protected RequirementsListModel createModel() {
        RequirementsListModel model = new RequirementsListModel();
        return model;
    }

    @Override
    public SelectRequirementsForDelete.Presenter getPresenter() {
        return (SelectRequirementsForDelete.Presenter) super.getPresenter();
    }
    //    @Override
//    protected NavigatorModel createFoldersModel() {
//        final RequirementsNavigatorModel model = new RequirementsNavigatorModel();
//        model.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
//            @Override
//            public void onSelectionChange(SelectionChangeEvent event) {
//                final Requirement selected = model.getSelectedFolder();
//                getPresenter().folderSelected(new Folder(selected.id));
//            }
//        });
//        return model;
//    }
//
//    @Override
//    protected SearchPersistentsModel<Requirement> createModel() {
//        return new RequirementsTreeTableModel();
//    }
//
//    @Override
//    protected TreeTableViewer<Requirement> createTable() {
//        final TreeTableViewer<Requirement> table = new RequirementsTreeTableViewer((RequirementsTreeTableModel) searchModel);
//        table.setClassName(RequirementColumns.ROW_CLASS);
//        table.setHoverClassName(RequirementColumns.ROW_CLASS);
//        table.setHasSearchPanel(false);
//        table.setPlain(true);
//        return table;
//    }

    interface ViewUiBinder extends UiBinder<Form, SelectRequirementsForDeleteImpl> {
    }

}
