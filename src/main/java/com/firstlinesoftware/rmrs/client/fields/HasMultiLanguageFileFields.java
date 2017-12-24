package com.firstlinesoftware.rmrs.client.fields;

import com.firstlinesoftware.base.client.Base;
import com.firstlinesoftware.base.client.factories.FormItemFactory;
import com.firstlinesoftware.base.client.factories.Registrable;
import com.firstlinesoftware.base.client.fields.AbstractFormField;
import com.firstlinesoftware.base.shared.dto.AttachedFile;
import com.firstlinesoftware.rmrs.client.Rmrs;
import com.firstlinesoftware.rmrs.client.messages.RmrsMessages;
import com.firstlinesoftware.rmrs.client.widgets.RequirementAttachmentSelector;
import com.firstlinesoftware.rmrs.shared.dto.HasMultiLanguageFile;
import com.google.gwt.user.client.ui.Widget;

public class HasMultiLanguageFileFields implements Registrable {
    private final FormItemFactory formItemFactory = Base.getInjector().getFormItemFactory();
    private final RmrsMessages messages = Rmrs.getInjector().getMessages();

    @Override
    public void register() {
        formItemFactory.register(new RussianField(), messages.russianContent());
        formItemFactory.register(new EnglishField(), messages.englishContent());
    }

    private class RussianField extends AbstractFormField<AttachedFile, HasMultiLanguageFile> {
        @Override
        protected AttachedFile getValue(HasMultiLanguageFile dto) {
            return dto.getRussian();
        }

        @Override
        protected void setValue(HasMultiLanguageFile dto, AttachedFile value) {
            dto.setRussian(value);
        }

        @Override
        public boolean hasAspect(Object dto) {
            return dto instanceof HasMultiLanguageFile;
        }

        @Override
        public Widget getViewWidget() {
            return null;
        }

        @Override
        public Widget getEditorWidget() {
            return new RequirementAttachmentSelector("requirement_ru.docx");
        }
    }

    private class EnglishField extends AbstractFormField<AttachedFile, HasMultiLanguageFile> {
        @Override
        protected AttachedFile getValue(HasMultiLanguageFile dto) {
            return dto.getEnglish();
        }

        @Override
        protected void setValue(HasMultiLanguageFile dto, AttachedFile value) {
            dto.setEnglish(value);
        }

        @Override
        public boolean hasAspect(Object dto) {
            return dto instanceof HasMultiLanguageFile;
        }

        @Override
        public Widget getViewWidget() {
            return null;
        }

        @Override
        public Widget getEditorWidget() {
            return new RequirementAttachmentSelector("requirement_en.docx");
        }
    }
}
