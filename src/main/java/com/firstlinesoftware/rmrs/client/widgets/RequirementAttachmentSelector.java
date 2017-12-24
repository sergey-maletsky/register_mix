package com.firstlinesoftware.rmrs.client.widgets;

import com.firstlinesoftware.base.client.Base;
import com.firstlinesoftware.base.client.events.AttachedFileUpdatedEvent;
import com.firstlinesoftware.base.client.widgets.AttachmentSelector;
import com.firstlinesoftware.base.shared.dto.AttachedFile;
import com.firstlinesoftware.ecm.client.widgets.EditButtonField;
import com.firstlinesoftware.rmrs.client.Rmrs;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Window;

public class RequirementAttachmentSelector extends AttachmentSelector {
    private static int count = 0;
    private String templatePath = "templates/empty.docx";
    private final String id = "id_req_selector" + count++;

    private final EventBus eventBus = Base.getInjector().getEventBus();

    private final EditButtonField create;
    private final String fileName;

    private static final String CREATE_PROPOSAL = "create-proposal";
    private static final String RU = "ru";

    public RequirementAttachmentSelector(final String fileName) {
        getElement().setId(id);
        this.fileName = fileName;
        create = GWT.create(EditButtonField.class);
        create.setUrl(getAddImage());
        create.setEditTooltiptext(Rmrs.getInjector().getMessages().createEmpty());
        create.setWidth("18px");
        create.setHeight("22px");
        if (Window.Location.getHref().contains(CREATE_PROPOSAL)) {
            if (fileName.contains(RU)) {
                templatePath = "templates/template_ru.docx";
            } else {
                templatePath = "templates/template_en.docx";
            }
        }
        create.setDocument(templatePath, "", fileName, "application/vnd.openxmlformats-officedocument.wordprocessingml.document", id, true, true, false, true);
        addButton(create);
        eventBus.addHandler(AttachedFileUpdatedEvent.getType(), new AttachedFileUpdatedEvent.Handler() {
            @Override
            public void onFileUpdated(AttachedFileUpdatedEvent event) {
                if (event.listenerId.equals(RequirementAttachmentSelector.this.id)) {
                    final AttachedFile value = new AttachedFile();
                    value.setId(event.updatedFileId);
                    value.setName(getValue() != null ? getValue().name : fileName);
                    setValue(value);
                }
            }
        });
    }

    @Override
    public void setValue(AttachedFile value, boolean fireEvents) {
        super.setValue(value, fireEvents);
        if(value != null) {
            create.setDocument(value.id, "", value.name, "application/vnd.openxmlformats-officedocument.wordprocessingml.document", id, true, true, false, true);
            create.setText(Rmrs.getInjector().getMessages().viewContent());
        } else {
            create.setDocument(templatePath, "", fileName, "application/vnd.openxmlformats-officedocument.wordprocessingml.document", id, true, true, false, true);
            create.setText(Rmrs.getInjector().getMessages().createEmpty());
        }
    }
}