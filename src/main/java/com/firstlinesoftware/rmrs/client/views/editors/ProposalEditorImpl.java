package com.firstlinesoftware.rmrs.client.views.editors;

import com.firstlinesoftware.base.client.SuccessCallback;
import com.firstlinesoftware.base.client.widgets.AttachmentSelector;
import com.firstlinesoftware.base.client.widgets.AttachmentsGridEditor;
import com.firstlinesoftware.rmrs.client.Rmrs;
import com.firstlinesoftware.rmrs.client.messages.RmrsMessages;
import com.firstlinesoftware.rmrs.shared.dto.Proposal;
import com.firstlinesoftware.route.client.views.editors.AbstractRouteEditorImpl;

public class ProposalEditorImpl extends AbstractRouteEditorImpl<Proposal> {

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
            final AttachmentsGridEditor attachedFiles = getAttachedFiles();
            MultiLanguageFileUploadHelper.buildFields(russianContent, englishContent, new SuccessCallback() {
                @Override
                public void onSuccess(Object o) {
                    attachedFiles.start();
                }
            });

            attachListenersRegistered = true;
        }
        super.buildFields();
    }
}
