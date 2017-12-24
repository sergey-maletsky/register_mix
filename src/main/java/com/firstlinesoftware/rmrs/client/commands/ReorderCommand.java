package com.firstlinesoftware.rmrs.client.commands;

import com.firstlinesoftware.base.client.Base;
import com.firstlinesoftware.base.client.events.*;
import com.firstlinesoftware.base.client.services.ActionCallback;
import com.firstlinesoftware.base.client.services.VoidUserActionCallback;
import com.firstlinesoftware.base.client.widgets.FormItemBase;
import com.firstlinesoftware.base.client.widgets.Spinner;
import com.firstlinesoftware.base.client.widgets.popups.BasePopupPresenter;
import com.firstlinesoftware.base.client.widgets.popups.EditValuePopupPanel;
import com.firstlinesoftware.base.client.widgets.popups.EditorPopupPresenter;
import com.firstlinesoftware.base.shared.dto.DTO;
import com.firstlinesoftware.ecm.client.commands.EcmCommand;
import com.firstlinesoftware.ecm.shared.dto.Document;
import com.firstlinesoftware.rmrs.client.Rmrs;
import com.firstlinesoftware.rmrs.client.events.OrderChangeEvent;
import com.firstlinesoftware.rmrs.client.events.OrderChangeFinishedEvent;
import com.firstlinesoftware.rmrs.client.events.OrderChangeStartedEvent;
import com.firstlinesoftware.rmrs.client.messages.RmrsMessages;
import com.firstlinesoftware.rmrs.client.models.RequirementsDataProvider;
import com.firstlinesoftware.rmrs.client.places.BrowseRequirementsPlace;
import com.firstlinesoftware.rmrs.shared.dto.Requirement;
import com.google.common.base.Objects;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.Widget;

import java.util.ArrayList;
import java.util.List;

public class ReorderCommand extends EcmCommand {

    private final RmrsMessages messages = Rmrs.getInjector().getMessages();
    private final EventBus eventBus = Base.getInjector().getEventBus();

    private Widget button;
    private Integer order;

    @Override
    protected Widget getInstance(final Document document, String positionId) {
        final Place where = placeController.getWhere();
        if (document instanceof Requirement && where instanceof BrowseRequirementsPlace) {
            final Requirement r = (Requirement) document;
            final String parent = r.parent != null ? r.parent.id : "requirements";
            if (Objects.equal(parent, ((BrowseRequirementsPlace) where).id)) {
               button = createButton("images/buttons/32/down.png", messages.reorder(), new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        RequirementsDataProvider.getDocuments(parent, false, new ActionCallback<List<Requirement>>(ecmMessages.errorWhileGettingDocuments()) {
                            @Override
                            public void onActionSuccess(final List<Requirement> documents) {
                                eventBus.fireEvent(new OrderChangeStartedEvent(parent));
                                order = r.order != null ? r.order : documents.size();
                                final List<Requirement> value = copyValue(documents);
                                final Spinner editor = new Spinner(1, documents.size(), 1, order) {
                                    @Override
                                    public void setWidth(String width) {
                                        super.setWidth(FormItemBase.SPINNER_WIDTH + "px");
                                    }
                                };
                                editor.addValueChangeHandler(new ValueChangeHandler<Integer>() {
                                    @Override
                                    public void onValueChange(ValueChangeEvent<Integer> event) {
                                        reorderRequirements(r.id, event.getValue(), value);
                                        eventBus.fireEvent(new OrderChangeEvent(value));
                                    }
                                });
                                final EditValuePopupPanel<Integer> panel = new EditValuePopupPanel<>(messages.newOrder(), editor, false);
                                final EditorPopupPresenter<Integer, EditValuePopupPanel<Integer>> presenter = new EditorPopupPresenter<>(panel, button, BasePopupPresenter.DO_NOT_ADD_CLICK_HANDLER);
                                presenter.addPrePopupHandler(new PrePopupHandler() {
                                    @Override
                                    public void onPrePopup(PrePopupEvent event) {
                                        panel.setValue(order, false);
                                    }
                                });
                                presenter.addSavePopupHandler(new SavePopupHandler<Integer>() {
                                    @Override
                                    public void onSave(SavePopupEvent<Integer> event) {
                                        eventBus.fireEvent(new OrderChangeFinishedEvent(true));
                                        final List<Document> updated = fidnUpdated(value, documents);
                                        if (!updated.isEmpty()) {
                                            documentProxy.update(updated, new VoidUserActionCallback(messages.reordered(), messages.errorWhileReordering()));
                                        }
                                    }
                                });
                                presenter.addCancelEditorHandler(new CancelEditorHandler<Integer>() {
                                    @Override
                                    public void onCancel(CancelEditorEvent<Integer> event) {
                                        eventBus.fireEvent(new OrderChangeFinishedEvent(false));
                                    }
                                });
                                presenter.showModal();
                            }
                        });

                    }
                });
                return button;
            }
        }
        return null;
    }

    private static List<Requirement> copyValue(List<Requirement> documents) {
        final List<Requirement> value = new ArrayList<>();
        for (Requirement requirement : documents) {
            value.add(requirement.clone());
        }
        return value;
    }

    private static List<Document> fidnUpdated(List<Requirement> newValue, List<Requirement> oldValue) {
        final List<Document> updated = new ArrayList<>();
        for (Requirement r : newValue) {
            for (Requirement d : oldValue) {
                if (Objects.equal(d.id, r.id) && !Objects.equal(d.order, r.order)) {
                    final Requirement u = new Requirement();
                    u.id = r.id;
                    u.order = r.order;
                    updated.add(u);
                }
            }
        }
        return updated;
    }

    private void reorderRequirements(String id, Integer newOrder, List<Requirement> value) {
        for (DTO dto : value) {
            if (dto instanceof Requirement) {
                final Requirement r = (Requirement) dto;
                if (Objects.equal(id, dto.id)) {
                    r.order = newOrder;
                } else if (Objects.equal(r.order, newOrder)) {
                    r.order = order;
                }
            }
        }
        order = newOrder;
    }
}
