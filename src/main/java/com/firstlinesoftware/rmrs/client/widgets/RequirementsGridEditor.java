package com.firstlinesoftware.rmrs.client.widgets;

import com.firstlinesoftware.base.client.activities.AbstractActivity;
import com.firstlinesoftware.base.client.events.PrePopupEvent;
import com.firstlinesoftware.base.client.events.PrePopupHandler;
import com.firstlinesoftware.base.client.events.SavePopupEvent;
import com.firstlinesoftware.base.client.events.SavePopupHandler;
import com.firstlinesoftware.base.client.widgets.*;
import com.firstlinesoftware.base.client.widgets.popups.BaseFormPopupPresenter;
import com.firstlinesoftware.base.client.widgets.popups.EditorPopupPresenter;
import com.firstlinesoftware.base.client.widgets.popups.StandardEditorPanel;
import com.firstlinesoftware.ecm.shared.dto.Document;
import com.firstlinesoftware.rmrs.client.Rmrs;
import com.firstlinesoftware.rmrs.client.activities.SelectRequirementActivity;
import com.firstlinesoftware.rmrs.client.columns.RequirementColumns;
import com.firstlinesoftware.rmrs.client.columns.RmrsHovers;
import com.firstlinesoftware.rmrs.client.icons.RmrsIcons;
import com.firstlinesoftware.rmrs.client.messages.RmrsMessages;
import com.firstlinesoftware.rmrs.shared.dto.Requirement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Label;

import java.util.List;
import java.util.Set;

public class RequirementsGridEditor extends GridEditor<Requirement> {

    private final RmrsMessages messages = Rmrs.getInjector().getMessages();
    private final RmrsIcons icons = Rmrs.getInjector().getIcons();

    private final ImageButton addButton;
    private final ImageButton deleteButton;

    public RequirementsGridEditor() {
        this((String) null);
    }

    public RequirementsGridEditor(final String lifecycle) {
        super(RequirementColumns.ROW_CLASS);
        setHoverClassName(RequirementColumns.ROW_CLASS);
        addButton = new ImageButton(icons.add32(), messages.add(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                final BaseFormPopupPresenter<Set<Document>> presenter = new BaseFormPopupPresenter<Set<Document>>() {
                    @Override
                    protected AbstractActivity createActivity() {
                        return new SelectRequirementActivity(false, lifecycle) {
                            @Override
                            public void onResultSuccess(Set<Requirement> selection) {
                                for (Requirement requirement : selection) {
                                    addItem(requirement);
                                }
                                hidePanel();
                            }

                            @Override
                            public void onResultCancel() {
                                hidePanel();
                            }
                        };
                    }
                };
                presenter.showModal();
            }
        });
        deleteButton = new ImageButton("images/buttons/32/remove.png", messages.remove(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                removeSelectedItems();
            }
        });
        add(addButton, false);
        add(deleteButton, true);
    }

    public RequirementsGridEditor(final StandardEditorPanel<Requirement> panel) {
        super(RequirementColumns.ROW_CLASS);
        setHoverClassName(RequirementColumns.ROW_CLASS);

        addButton = new ImageButton(icons.add32(), messages.add(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                EditorPopupPresenter<Requirement, StandardEditorPanel<Requirement>> presenter = new EditorPopupPresenter(panel, addButton, true);
                presenter.addPrePopupHandler(new PrePopupHandler() {
                    public void onPrePopup(PrePopupEvent event) {
                        panel.setValue(null, false);
                    }
                });
                presenter.addSavePopupHandler(new SavePopupHandler<Requirement>() {
                    public void onSave(SavePopupEvent<Requirement> event) {
                        addItem(event.getResult());
                    }
                });
                presenter.showModal();
            }
        });
        deleteButton = new ImageButton("buttons/32/remove.png", messages.remove(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                removeSelectedItems();
            }
        });
        add(addButton, false);
        add(deleteButton, true);
    }

    @Override
    protected AbstractTreeTableContainer<Requirement> createViewer() {
        return new GridViewer<Requirement>() {
            @Override
            protected TreeTableToolbar createTreeTableToolbar(final AbstractTreeTableContainer<Requirement> container, SearchPanel searchPanel, Label label, SwitchImageButton.Command command) {
                final TreeTableToolbar result = super.createTreeTableToolbar(container, searchPanel, label, command);
                result.addButton(new ImageButton("images/icons/24/compact.png", Rmrs.getInjector().getMessages().switchMode(), new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        setCompactMode(!Boolean.TRUE.equals(container.getCompactMode()));
                        reload();
                    }
                }));
                RmrsHovers.setCompactMode(container.getTable().isCompactRowMode());
                return result;
            }

            @Override
            public void setCompactMode(Boolean compactMode) {
                super.setCompactMode(compactMode);
                RmrsHovers.setCompactMode(compactMode);
            }
        };
    }

    @Override
    public void addItem(Requirement item) {
        final List<Requirement> value = getValue();
        if (value != null && value.size() > 0 && value.contains(item)) {
            return;
        }
        super.addItem(item);
    }


}