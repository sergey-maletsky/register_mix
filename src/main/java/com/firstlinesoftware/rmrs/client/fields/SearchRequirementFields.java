package com.firstlinesoftware.rmrs.client.fields;

import com.firstlinesoftware.base.client.Base;
import com.firstlinesoftware.base.client.factories.FormItemFactory;
import com.firstlinesoftware.base.client.factories.Registrable;
import com.firstlinesoftware.base.client.fields.AbstractFormField;
import com.firstlinesoftware.base.client.fields.DateBoxField;
import com.firstlinesoftware.base.client.fields.DirectoryComboBoxField;
import com.firstlinesoftware.base.client.fields.TextBoxField;
import com.firstlinesoftware.base.client.widgets.ExpandableList;
import com.firstlinesoftware.base.client.widgets.FormItemBase;
import com.firstlinesoftware.base.shared.dto.DateRange;
import com.firstlinesoftware.base.shared.dto.SearchField;
import com.firstlinesoftware.ecm.client.fields.DocumentSelectorField;
import com.firstlinesoftware.ecm.client.fields.DocumentSuggestBoxField;
import com.firstlinesoftware.ecm.client.widgets.DocumentSelector;
import com.firstlinesoftware.ecm.client.widgets.DocumentSuggestBox;
import com.firstlinesoftware.ecm.client.widgets.search.SearchFieldBox;
import com.firstlinesoftware.ecm.shared.directories.EcmDirectories;
import com.firstlinesoftware.ecm.shared.dto.Document;
import com.firstlinesoftware.orgstruct.client.fields.PositionField;
import com.firstlinesoftware.orgstruct.shared.dto.Position;
import com.firstlinesoftware.rmrs.client.Rmrs;
import com.firstlinesoftware.rmrs.client.messages.RmrsMessages;
import com.firstlinesoftware.rmrs.client.widgets.RequirementSelector;
import com.firstlinesoftware.rmrs.shared.dto.Requirement;
import com.firstlinesoftware.rmrs.shared.dto.SearchRequirementData;
import com.google.gwt.user.client.ui.Widget;

import java.util.Date;
import java.util.List;

public class SearchRequirementFields implements Registrable {
    private final FormItemFactory formItemFactory = Base.getInjector().getFormItemFactory();
    private final RmrsMessages messages = Rmrs.getInjector().getMessages();

    @Override
    public void register() {
        formItemFactory.registerDouble(new NumberField(), messages.numberAndNomenclature(), new NameField(), messages.name());
        formItemFactory.register(new ParentField(), messages.includedIn());
        formItemFactory.registerDouble(new EffectiveBeginField(), messages.effectiveBegin(), new EffectiveEndField(), messages.effectiveEnd());
        formItemFactory.register(new LifecycleField(), messages.lifecycle());
        formItemFactory.register(new AuthorField(), messages.author());
        formItemFactory.register(new MandatoryField(), messages.mandatoryFields());
        formItemFactory.register(new OptionalField(), messages.optionalFields());
    }


    private class NumberField extends TextBoxField<SearchRequirementData> {
        @Override
        protected String getValue(SearchRequirementData dto) {
            return dto.number;
        }

        @Override
        protected void setValue(SearchRequirementData dto, String value) {
            dto.number = value;
        }

        @Override
        public boolean hasAspect(Object dto) {
            return dto instanceof SearchRequirementData;
        }

        @Override
        public FormItemBase.Order getOrder() {
            return FormItemBase.Order.HIGH1;
        }
    }

    private class NameField extends TextBoxField<SearchRequirementData> {
        @Override
        protected String getValue(SearchRequirementData dto) {
            return dto.text;
        }

        @Override
        protected void setValue(SearchRequirementData dto, String value) {
            dto.text = value;
        }

        @Override
        public boolean hasAspect(Object dto) {
            return dto instanceof SearchRequirementData;
        }

        @Override
        public FormItemBase.Order getOrder() {
            return FormItemBase.Order.HIGH1;
        }
     }

    private class ParentField extends DocumentSelectorField<SearchRequirementData> {
        protected ParentField() {
            super(Requirement.KIND);
        }

        @Override
        protected Requirement getValue(SearchRequirementData dto) {
            return dto.parent;
        }

        @Override
        protected void setValue(SearchRequirementData dto, Document value) {
            dto.parent = (Requirement)value;
        }

        @Override
        public boolean hasAspect(Object dto) {
            return dto instanceof SearchRequirementData;
        }

        @Override
        public DocumentSelector getEditorWidget() {
            return new RequirementSelector(true);
        }
    }

    private class EffectiveBeginField extends DateBoxField<SearchRequirementData> {
        @Override
        protected Date getValue(SearchRequirementData requirement) {
            return requirement.effective != null ? requirement.effective.min : null;
        }

        @Override
        protected void setValue(SearchRequirementData requirement, Date date) {
            if(requirement.effective == null) {
                requirement.effective = new DateRange();
            }
            requirement.effective.min = date;
        }

        @Override
        public boolean hasAspect(Object o) {
            return o instanceof SearchRequirementData;
        }
    }

    private class EffectiveEndField extends DateBoxField<SearchRequirementData> {
        @Override
        protected Date getValue(SearchRequirementData requirement) {
            return requirement.effective != null ? requirement.effective.max : null;
        }

        @Override
        protected void setValue(SearchRequirementData requirement, Date date) {
            if(requirement.effective == null) {
                requirement.effective = new DateRange();
            }
            requirement.effective.max = date;
        }

        @Override
        public boolean hasAspect(Object o) {
            return o instanceof SearchRequirementData;
        }
    }

    private class LifecycleField extends DirectoryComboBoxField<SearchRequirementData> {
        protected LifecycleField() {
            super(EcmDirectories.LIFECYCLE);
        }

        @Override
        protected String getValue(SearchRequirementData dto) {
            return dto.lifecycle;
        }

        @Override
        protected void setValue(SearchRequirementData dto, String value) {
            dto.lifecycle = value;
        }

        @Override
        public boolean hasAspect(Object dto) {
            return dto instanceof SearchRequirementData;
        }
    }

    private class AuthorField extends PositionField<SearchRequirementData> {

        @Override
        protected Position getValue(SearchRequirementData dto) {
            return dto.author;
        }

        @Override
        protected void setValue(SearchRequirementData dto, Position value) {
            dto.author = value;
        }

        @Override
        public boolean hasAspect(Object dto) {
            return dto instanceof SearchRequirementData;
        }
    }

    private class MandatoryField extends AbstractFormField<List<SearchField>, SearchRequirementData> {

        @Override
        protected List<SearchField> getValue(SearchRequirementData dto) {
            return dto.mandatory;
        }

        @Override
        protected void setValue(SearchRequirementData dto, List<SearchField> value) {
            dto.mandatory = value;
        }

        @Override
        public boolean hasAspect(Object dto) {
            return dto instanceof SearchRequirementData;
        }

        @Override
        public Widget getEditorWidget() {
            return new ExpandableList<>(SearchFieldBox.getProvider());
        }
    }

    private class OptionalField extends AbstractFormField<List<SearchField>, SearchRequirementData> {

        @Override
        protected List<SearchField> getValue(SearchRequirementData dto) {
            return dto.optional;
        }

        @Override
        protected void setValue(SearchRequirementData dto, List<SearchField> value) {
            dto.optional = value;
        }

        @Override
        public boolean hasAspect(Object dto) {
            return dto instanceof SearchRequirementData;
        }

        @Override
        public Widget getEditorWidget() {
            return new ExpandableList<>(SearchFieldBox.getProvider());
        }
    }
}
