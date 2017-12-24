package com.firstlinesoftware.rmrs.client.desktop;

import com.firstlinesoftware.base.client.Base;
import com.firstlinesoftware.base.client.factories.NavigationPanelsFactory;
import com.firstlinesoftware.base.client.models.NavigatorModel;
import com.firstlinesoftware.base.client.services.ActionCallback;
import com.firstlinesoftware.base.shared.dto.DTO;
import com.firstlinesoftware.ecm.client.Ecm;
import com.firstlinesoftware.ecm.client.proxies.DocumentProxy;
import com.firstlinesoftware.rmrs.client.Rmrs;
import com.firstlinesoftware.rmrs.client.events.RequirementsSelectionChangedEvent;
import com.firstlinesoftware.rmrs.client.models.RequirementsNavigatorModel;
import com.firstlinesoftware.rmrs.client.places.BrowseRequirementsPlace;
import com.firstlinesoftware.rmrs.shared.dto.Requirement;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.cellview_imported.client.SmartCellTree;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.TreeViewModel;

import java.util.Arrays;
import java.util.List;

/**
 * User: VAntonov
 * Date: 8/2/12
 * Time: 11:44 AM
 */
public class RequirementsPanel extends SmartCellTree<DTO> implements NavigationPanelsFactory.NavigationPanel {
    public static final RequirementsNavigatorModel model = new RequirementsNavigatorModel();

    private static final EventBus eventBus = Base.getInjector().getEventBus();
    private final DocumentProxy documentProxy = Ecm.getInjector().getDocumentProxy();
    private static boolean skipEvent;


    public RequirementsPanel() {
        super(initModel(), NavigatorModel.ROOT);
        eventBus.addHandler(RequirementsSelectionChangedEvent.getType(), new RequirementsSelectionChangedEvent.Handler() {
            @Override
            public void onRequirementSelected(RequirementsSelectionChangedEvent event) {
                if (event.folderItemId != null) {
                    documentProxy.get(event.folderItemId, new ActionCallback<Requirement>(null) {
                        @Override
                        public void onActionSuccess(Requirement dto) {
                            assert dto != null;
                            if (dto.fullPath != null) {
                                final String[] ids = dto.fullPath.split("\\.");
                                documentProxy.getDocuments(Arrays.asList(ids), new ActionCallback<List<Requirement>>(Rmrs.getInjector().getMessages().errorWhileGettingDocuments()) {
                                    @Override
                                    public void onActionSuccess(List<Requirement> result) {
                                        expandTreeByChain(result);
                                        if(!result.isEmpty()) {
                                            skipEvent = true;
                                            model.selectFolder(result.get(result.size() - 1));
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });
    }

    private static TreeViewModel initModel() {
        model.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange(SelectionChangeEvent selectionChangeEvent) {
                if(!skipEvent) {
                    final Requirement selectedFolder = model.getSelectedFolder();
                    if (selectedFolder != null) {
                        Base.getInjector().getPlaceController().goTo(new BrowseRequirementsPlace(selectedFolder.id));
                    }
                }
                skipEvent = false;
            }
        });
        return model;
    }

    @Override
    public void unselect() {
        model.unselect();
    }

    @Override
    public boolean hasSelection() {
        return model.getSelectedFolder() != null;
    }

    @Override
    public HandlerRegistration addSelectionChangeHandler(SelectionChangeEvent.Handler handler) {
        return model.addSelectionChangeHandler(handler);
    }

}
