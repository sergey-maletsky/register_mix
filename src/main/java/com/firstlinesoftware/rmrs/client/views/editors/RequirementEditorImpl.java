package com.firstlinesoftware.rmrs.client.views.editors;

import com.firstlinesoftware.base.client.SuccessCallback;
import com.firstlinesoftware.base.client.widgets.AttachmentSelector;
import com.firstlinesoftware.ecm.client.views.editors.DocumentEditorImpl;
import com.firstlinesoftware.rmrs.client.Rmrs;
import com.firstlinesoftware.rmrs.client.messages.RmrsMessages;
import com.firstlinesoftware.rmrs.shared.dto.Requirement;

public class RequirementEditorImpl extends DocumentEditorImpl<Requirement> {

    private final RmrsMessages messages = Rmrs.getInjector().getMessages();
    private boolean attachListenersRegistered;

    @Override
    public void startUploading() {
        final AttachmentSelector russianContent = getFormItemWidget(messages.russianContent());
        russianContent.start();
    }

    @Override
    public void buildFields() {
        if(!attachListenersRegistered) {
            final AttachmentSelector russianContent = getFormItemWidget(messages.russianContent());
            final AttachmentSelector englishContent = getFormItemWidget(messages.englishContent());
            MultiLanguageFileUploadHelper.buildFields(russianContent, englishContent, new SuccessCallback() {
                @Override
                public void onSuccess(Object o) {
                    getPresenter().onFilesUploaded();
                }
            });

            attachListenersRegistered = true;
        }
        super.buildFields();
    }

}
