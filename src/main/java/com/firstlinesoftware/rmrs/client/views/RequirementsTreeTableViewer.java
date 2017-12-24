package com.firstlinesoftware.rmrs.client.views;

import com.firstlinesoftware.base.client.events.PrePopupEvent;
import com.firstlinesoftware.base.client.events.PrePopupHandler;
import com.firstlinesoftware.base.client.events.SavePopupEvent;
import com.firstlinesoftware.base.client.events.SavePopupHandler;
import com.firstlinesoftware.base.client.widgets.*;
import com.firstlinesoftware.base.client.widgets.popups.EditorPopupPresenter;
import com.firstlinesoftware.base.shared.dto.Pair;
import com.firstlinesoftware.rmrs.client.Rmrs;
import com.firstlinesoftware.rmrs.client.columns.RmrsHovers;
import com.firstlinesoftware.rmrs.client.messages.RmrsMessages;
import com.firstlinesoftware.rmrs.client.models.RequirementsTreeTableModel;
import com.firstlinesoftware.rmrs.client.widgets.RequirementsFilterPanel;
import com.firstlinesoftware.rmrs.shared.dto.Requirement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Label;

import java.util.Date;

public class RequirementsTreeTableViewer extends TreeTableViewer<Requirement> {

    private final RmrsMessages messages = Rmrs.getInjector().getMessages();
    private final ImageButton filter = new ImageButton("images/icons/24/not_only_effective.png", messages.effectiveDates(), new ClickHandler() {
        @Override
        public void onClick(ClickEvent event) {
            final RequirementsFilterPanel panel = new RequirementsFilterPanel();
            final EditorPopupPresenter<Pair<Boolean, Date>, RequirementsFilterPanel> presenter = new EditorPopupPresenter<>(panel, RequirementsTreeTableViewer.this.filter, true);
            presenter.addPrePopupHandler(new PrePopupHandler() {
                @Override
                public void onPrePopup(PrePopupEvent event1) {
                    panel.setValue(new Pair<Boolean, Date>(null, new Date()));
                }
            });
            presenter.addSavePopupHandler(new SavePopupHandler<Pair<Boolean, Date>>() {
                @Override
                public void onSave(SavePopupEvent<Pair<Boolean, Date>> event) {
                    final Date date = event.getResult().getSecond();
                    final Boolean onlySigned = event.getResult().getFirst();
                    ((RequirementsTreeTableModel) getModel()).setOnlyEffective(date, onlySigned);
                    filter.setUrl(date != null || Boolean.TRUE.equals(onlySigned) ? "images/icons/24/effective.png" : "images/icons/24/not_only_effective.png");
                }
            });
            presenter.showModal();
        }
    });
    private final SwitchImageButton language = new SwitchImageButton("images/flags/24/eng.png", "images/flags/24/rus.png", new SwitchImageButton.Command() {
        @Override
        public void execute(boolean on) {
            RmrsHovers.setLanguage(on);
            reload();
        }
    });
    private final SwitchImageButton recursive = new SwitchImageButton("images/buttons/24/navigate_down.png", "images/buttons/24/navigate_up.png", new SwitchImageButton.Command() {
        @Override
        public void execute(boolean on) {
            ((RequirementsTreeTableModel) getModel()).setShowRecursive(!on);
            reload();
        }
    });
    private ImageButton compact = new ImageButton("images/icons/24/compact.png", messages.switchMode(), new ClickHandler() {
        @Override
        public void onClick(ClickEvent event) {
            setCompactMode(!Boolean.TRUE.equals(getCompactMode()));
            reload();
        }
    });

    public RequirementsTreeTableViewer(RequirementsTreeTableModel model) {
        super(model);
    }

    @Override
    public void setCompactMode(Boolean compactMode) {
        super.setCompactMode(compactMode);
        RmrsHovers.setCompactMode(compactMode);
        language.setEnabled(!compactMode);
    }

    @Override
    protected TreeTableToolbar createTreeTableToolbar(final AbstractTreeTableContainer<Requirement> container, SearchPanel searchPanel, Label label, SwitchImageButton.Command command) {
        final TreeTableToolbar result = new TreeTableToolbar(this, getSearchPanel(), new Label(), null);
        language.setTitle(messages.selectLanguage());
        recursive.setTitle(messages.showRecursive());
        recursive.setEnabled(true);
        language.setEnabled(true);
        result.addButton(filter);
        result.addButton(recursive);
        result.addButton(language);
        result.addButton(compact);
        RmrsHovers.setCompactMode(getTable().isCompactRowMode());
        language.setOn(RmrsHovers.isRussian());
        return result;
    }
}
