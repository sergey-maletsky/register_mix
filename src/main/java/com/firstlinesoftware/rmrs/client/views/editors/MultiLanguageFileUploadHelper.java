package com.firstlinesoftware.rmrs.client.views.editors;

import com.firstlinesoftware.base.client.SuccessCallback;
import com.firstlinesoftware.base.client.widgets.AttachmentSelector;
import com.firstlinesoftware.base.client.widgets.AttachmentsUploader;
import com.firstlinesoftware.base.shared.dto.AttachedFile;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

import java.util.List;
import java.util.Map;

public class MultiLanguageFileUploadHelper {

    public static void buildFields(final AttachmentSelector russianField, final AttachmentSelector englishField,
                                   final SuccessCallback callback) {
            russianField.addListener(new AttachmentsUploader.UploadListener() {
                @Override
                public void onFileAdded(List<AttachedFile> files) {
                    if (files.size() > 0) {
                        russianField.setValue(files.get(0));
                    }
                }

                @Override
                public void onFilesUploaded(Map<String, String> newIds) {
                    final AttachedFile value = russianField.getValue();
                    for (Map.Entry<String, String> e : newIds.entrySet()) {
                        if (value.id.equals(e.getKey())) {
                            value.id = e.getValue();
                        }
                    }
                    englishField.start();
                }
            });
            russianField.addClearClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    russianField.setValue(null);
                }
            });
            englishField.addListener(new AttachmentsUploader.UploadListener() {
                @Override
                public void onFileAdded(List<AttachedFile> files) {
                    if (files.size() > 0) {
                        englishField.setValue(files.get(0));
                    }
                }

                @Override
                public void onFilesUploaded(Map<String, String> newIds) {
                    final AttachedFile value = englishField.getValue();
                    for (Map.Entry<String, String> e : newIds.entrySet()) {
                        if (value.id.equals(e.getKey())) {
                            value.id = e.getValue();
                        }
                    }
                    callback.onSuccess(null);
                }
            });
            englishField.addClearClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    englishField.setValue(null);
                }
            });
        }
}
