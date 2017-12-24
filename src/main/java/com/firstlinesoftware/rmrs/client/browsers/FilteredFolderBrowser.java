package com.firstlinesoftware.rmrs.client.browsers;

import com.firstlinesoftware.base.client.Base;
import com.firstlinesoftware.base.client.browsers.AbstractFolderBrowser;
import com.firstlinesoftware.base.client.events.FolderSelectionChangedEvent;
import com.firstlinesoftware.base.shared.dto.SearchCriteria;
import com.firstlinesoftware.base.shared.utils.CriteriaBuilder;
import com.firstlinesoftware.ecm.client.events.FolderItemAddedEvent;
import com.firstlinesoftware.ecm.client.events.FolderItemChangedEvent;
import com.firstlinesoftware.ecm.client.events.FolderItemRemovedEvent;
import com.firstlinesoftware.ecm.client.models.DocumentFolderModel;
import com.firstlinesoftware.ecm.client.models.DocumentsSearchResultsDataProvider;
import com.firstlinesoftware.ecm.shared.dto.Document;
import com.firstlinesoftware.ecm.shared.dto.TaskFolder;
import com.firstlinesoftware.orgstruct.client.Orgstruct;
import com.firstlinesoftware.orgstruct.client.proxies.OrgstructureProxy;
import com.firstlinesoftware.rmrs.client.init.RmrsTaskFolders;
import com.google.common.base.Objects;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.view.client.AbstractDataProvider;
import com.google.gwt.view.client.SelectionChangeEvent;

import java.util.List;

public class FilteredFolderBrowser extends AbstractFolderBrowser<Document> {
    private final EventBus eventBus = Base.getInjector().getEventBus();
    private final String className;
    private String persistent;

    public FilteredFolderBrowser(final String type, String rowClass, final String lifecycle, final String position) {
        super(new DocumentFolderModel<Document>() {
            @Override
            public AbstractDataProvider<?> createDataProvider(Document parent) {
                final String positionId = getFolder() instanceof TaskFolder ? ((TaskFolder) getFolder()).positionId : null;
                return new FilteredFolderDataProvider(positionId, type, lifecycle, position);
            }

            @Override
            public boolean isLeaf(Object value) {
                return value != null;
            }
        }, null);
        className = rowClass;
    }

    public FilteredFolderBrowser(final String type, String rowClass, final String lifecycle, boolean singleSelect, String position) {
        this(type, rowClass, lifecycle, position);
        model.setSelectionModel(singleSelect);
    }

    public FilteredFolderBrowser(String type, String rowClass, String lifecycle, boolean singleSelect, String position, String persistent) {
        this(type, rowClass, lifecycle, singleSelect, position);
        this.persistent = persistent;
    }

    @Override
    protected void createTable() {
        super.createTable();
        table.setClassName(className);
        table.setHoverClassName("NoHover");
        table.setPersistentColumns(persistent);
    }

    @Override
    public void onFolderItemSelected(SelectionChangeEvent event) {
        detectSelectedObject(event);
        if (getSelectedObject() instanceof Document) {
            eventBus.fireEvent(new FolderSelectionChangedEvent(getSelectedObject().id));
        }
    }

    public void clearSelection() {
        model.getSelectionModel().clearSelection();
    }

    private static class FilteredFolderDataProvider extends DocumentsSearchResultsDataProvider<Document> implements FolderItemAddedEvent.Handler, FolderItemRemovedEvent.Handler, FolderItemChangedEvent.Handler {
        private final OrgstructureProxy orgstructureProxy = Orgstruct.getInjector().getOrgstructureProxy();

        private final String lifecycle;

        public FilteredFolderDataProvider(String positionId, String type, String lifecycle, String position) {
            this.lifecycle = lifecycle;
            final CriteriaBuilder<SearchCriteria> builder = new CriteriaBuilder<>()
                    .setType(type)
                    .addMustHave("ecm:lifecycle", lifecycle)
                    .setLimit(1000);
            if (positionId != null) {
                builder.addMustHave(position, positionId);
            } else {
                final List<String> myPositions = orgstructureProxy.getUserProfile().getPositionIds();
                assert myPositions != null && !myPositions.isEmpty();
                if (myPositions.size() == 1) {
                    builder.addMustHave(position, myPositions.get(0));
                } else {
                    for (String id : myPositions) {
                        builder.addShouldHave(position, id);
                    }
                }
            }
            setSearchCriteria(builder.build());
        }

        @Override
        protected void addEventHandlers() {
            addEventBusHandler(FolderItemAddedEvent.getType(), this);
            addEventBusHandler(FolderItemChangedEvent.getType(), this);
            addEventBusHandler(FolderItemRemovedEvent.getType(), this);
        }

        @Override
        public void onFolderItemAdded(FolderItemAddedEvent event) {
            if (Objects.equal(RmrsTaskFolders.getFolderId(event.folderId), lifecycle)) {
                dirty = true;
                startUpdating();
            } else if ("draft".equals(event.folderId)) {
                dirty = true;
                startUpdating();
            }
        }

        @Override
        public void onFolderItemRemoved(FolderItemRemovedEvent event) {
            if (Objects.equal(RmrsTaskFolders.getFolderId(event.folderId), lifecycle)) {
                dirty = true;
                startUpdating();
            } else if ("draft".equals(event.folderId)) {
                dirty = true;
                startUpdating();
            }
        }

        @Override
        public void onFolderItemChanged(FolderItemChangedEvent event) {
            final String folderId = RmrsTaskFolders.getFolderId(event.folderId);
            if (folderId == null || Objects.equal(folderId, lifecycle)) {
                dirty = true;
                startUpdating();
            }
        }

    }
}
