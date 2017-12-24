package com.firstlinesoftware.rmrs.client.browsers;

import com.firstlinesoftware.base.client.Base;
import com.firstlinesoftware.base.client.browsers.AbstractBrowser;
import com.firstlinesoftware.base.client.columns.ColumnsFactory;
import com.firstlinesoftware.base.client.columns.GridColumnDefinition;
import com.firstlinesoftware.base.client.widgets.TreeTableViewer;
import com.firstlinesoftware.rmrs.client.columns.RequirementColumns;
import com.firstlinesoftware.rmrs.client.events.OrderChangeStartedEvent;
import com.firstlinesoftware.rmrs.client.models.RequirementsTreeTableModel;
import com.firstlinesoftware.rmrs.client.places.BrowseRequirementsPlace;
import com.firstlinesoftware.rmrs.client.views.RequirementsTreeTableViewer;
import com.firstlinesoftware.rmrs.shared.dto.Requirement;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.cellview_imported.client.ColumnSortEvent;
import com.google.gwt.user.cellview_imported.client.ColumnSortList;
import com.google.gwt.user.client.Event;

/**
 * User: AOstrovsky
 * Date: 22.04.11
 */
public class RequirementsBrowser extends AbstractBrowser<Requirement> {

    private final ColumnsFactory columnsFactory = Base.getInjector().getColumnsFactory();
    private boolean uiCreated;

    public RequirementsBrowser() {

        super(new RequirementsTreeTableModel(), "requirements");
        className = RequirementColumns.ROW_CLASS;
    }

    public void setParentRequirement(Requirement parent) {
        ((RequirementsTreeTableModel) getModel()).setParent(parent != null ? parent.id : null);
        if (!uiCreated) {
            createUI();
            uiCreated = true;
        }
    }

    @Override
    protected void createTable() {
        super.createTable();
        table.setPersistentColumns("state");
        table.setPlain(true);
        table.setRowStyles("EffectiveStyle");
    }

    @Override
    protected TreeTableViewer<Requirement> createTreeTableViewer() {
        final RequirementsTreeTableViewer viewer = new RequirementsTreeTableViewer((RequirementsTreeTableModel) model);
        viewer.sinkEvents(Event.ONDBLCLICK);
        viewer.addHandler(new DoubleClickHandler() {
            @Override
            public void onDoubleClick(DoubleClickEvent event) {
                final Requirement requirement = viewer.getLastSelectedObject();
                if (requirement != null && !Boolean.TRUE.equals(requirement.isLeaf)) {
                    Base.getInjector().getPlaceController().goTo(new BrowseRequirementsPlace(requirement.id));
                }
            }
        }, DoubleClickEvent.getType());
        return viewer;
    }

    @Override
    protected void createUI(UiBinder binder) {
        super.createUI(binder);
        Base.getInjector().getEventBus().addHandler(OrderChangeStartedEvent.getType(), new OrderChangeStartedEvent.OrderChangeStartedEventHandler() {
            @Override
            public void onOrderChangeStarted(OrderChangeStartedEvent event) {
                final GridColumnDefinition<Requirement, ?> definition = columnsFactory.get(className, "order");
                if (definition != null) {
                    final ColumnSortList sortList = table.getTreeTableHeader().sortList;
                    if(sortList.size() == 0 || !sortList.get(0).getColumn().equals(definition.column)) {
                        sortList.push(new ColumnSortList.ColumnSortInfo(definition.column, true));
                        ColumnSortEvent.fire(table.getTable(), sortList);
                    }
                }
            }
        });
    }

}
