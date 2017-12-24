package com.firstlinesoftware.rmrs.client.fields;

import com.firstlinesoftware.base.client.Base;
import com.firstlinesoftware.base.client.factories.FormItemFactory;
import com.firstlinesoftware.base.client.factories.Registrable;
import com.firstlinesoftware.base.client.fields.TextAreaField;
import com.firstlinesoftware.base.client.fields.TextBoxField;
import com.firstlinesoftware.base.client.widgets.FormItemBase;
import com.firstlinesoftware.base.client.widgets.GridEditor;
import com.firstlinesoftware.ecm.client.fields.DocumentSelectorField;
import com.firstlinesoftware.ecm.client.widgets.DocumentSelector;
import com.firstlinesoftware.ecm.shared.dto.Document;
import com.firstlinesoftware.orgstruct.client.fields.PositionField;
import com.firstlinesoftware.orgstruct.shared.dto.Position;
import com.firstlinesoftware.rmrs.client.Rmrs;
import com.firstlinesoftware.rmrs.client.icons.RmrsIcons;
import com.firstlinesoftware.rmrs.client.messages.RmrsMessages;
import com.firstlinesoftware.rmrs.client.widgets.RequirementsGridEditor;
import com.firstlinesoftware.rmrs.shared.dto.CircularLetter;
import com.firstlinesoftware.rmrs.shared.dto.Requirement;
import com.firstlinesoftware.route.shared.dto.AbstractRoute;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Widget;

import java.util.List;

public class CircularLetterFields implements Registrable {
    private final FormItemFactory formItemFactory = Base.getInjector().getFormItemFactory();
    private final RmrsMessages messages = Rmrs.getInjector().getMessages();
    private final RmrsIcons icons = Rmrs.getInjector().getIcons();

    @Override
    public void register() {
        formItemFactory.register(new BusinessCaseNumber(), messages.circularBusinessCaseNumber());
        formItemFactory.register(new ReferredEnField(), messages.circularReferredEn());
        formItemFactory.registerDouble(new ObservableField(), messages.circularObservable(), new ObservableEnField(), messages.circularObservableEn());
        formItemFactory.registerDouble(new CommissioningField(), messages.circularCommissioning(), new CommissioningEnField(), messages.circularCommissioningEn());
        formItemFactory.registerDouble(new ValidToField(), messages.circularValidTo(), new ValidToEnField(), messages.circularValidToEn());
        formItemFactory.registerDouble(new ValidExtendedUntilField(), messages.circularValidExtendedUntil(), new ValidExtendedUntilEnField(), messages.circularValidExtendedUntilEn());
        formItemFactory.register(new ContentField(), messages.circularContent());
        formItemFactory.register(new ContentEnField(), messages.circularContentEn());
        formItemFactory.register(new ActionField(), messages.circularAction());
        formItemFactory.register(new ActionEnField(), messages.circularActionEn());
        formItemFactory.register(new ApprovePositionField(), messages.circularApprover());
        formItemFactory.register(new ChangedContentField(), messages.circularChangedContent());
        formItemFactory.register(new ApprovedRequirementsField(), messages.circularApprovedRequirements());
        formItemFactory.register(new LinkField(), messages.link());
    }

    private class BusinessCaseNumber extends TextBoxField<CircularLetter> {
        @Override
        protected String getValue(CircularLetter dto) {
            return dto.businessCaseNumber;
        }

        @Override
        protected void setValue(CircularLetter dto, String value) {
            dto.businessCaseNumber = value;
        }

        @Override
        public boolean hasAspect(Object dto) {
            return dto instanceof CircularLetter;
        }

        @Override
        public FormItemBase.Order getOrder() {
            return FormItemBase.Order.HIGH1;
        }
    }

    private class ContentField extends TextAreaField<CircularLetter> {
        @Override
        public boolean hasAspect(Object dto) {
            return dto instanceof CircularLetter;
        }

        @Override
        protected String getValue(CircularLetter dto) {
            return dto.content;
        }

        @Override
        protected void setValue(CircularLetter dto, String value) {
            dto.content = value;
        }
    }

    private class ContentEnField extends TextAreaField<CircularLetter> {
        @Override
        public boolean hasAspect(Object dto) {
            return dto instanceof CircularLetter;
        }

        @Override
        protected String getValue(CircularLetter dto) {
            return dto.content_en;
        }

        @Override
        protected void setValue(CircularLetter dto, String value) {
            dto.content_en = value;
        }
    }

    private class ReferredEnField extends TextAreaField<CircularLetter> {
        @Override
        public boolean hasAspect(Object dto) {
            return dto instanceof CircularLetter;
        }

        @Override
        protected String getValue(CircularLetter dto) {
            return dto.referred_en;
        }

        @Override
        protected void setValue(CircularLetter dto, String value) {
            dto.referred_en = value;
        }
    }

    private class ObservableField extends TextBoxField<CircularLetter> {
        @Override
        protected String getValue(CircularLetter dto) {
            return dto.observable;
        }

        @Override
        protected void setValue(CircularLetter dto, String value) {
            dto.observable = value;
        }

        @Override
        public boolean hasAspect(Object dto) {
            return dto instanceof CircularLetter;
        }
    }

    private class ObservableEnField extends TextBoxField<CircularLetter> {
        @Override
        protected String getValue(CircularLetter dto) {
            return dto.observable_en;
        }

        @Override
        protected void setValue(CircularLetter dto, String value) {
            dto.observable_en = value;
        }

        @Override
        public boolean hasAspect(Object dto) {
            return dto instanceof CircularLetter;
        }
    }

    private class CommissioningField extends TextBoxField<CircularLetter> {
        @Override
        protected String getValue(CircularLetter dto) {
            return dto.commissioning;
        }

        @Override
        protected void setValue(CircularLetter dto, String value) {
            dto.commissioning = value;
        }

        @Override
        public boolean hasAspect(Object dto) {
            return dto instanceof CircularLetter;
        }
    }

    private class CommissioningEnField extends TextBoxField<CircularLetter> {
        @Override
        protected String getValue(CircularLetter dto) {
            return dto.commissioning_en;
        }

        @Override
        protected void setValue(CircularLetter dto, String value) {
            dto.commissioning_en = value;
        }

        @Override
        public boolean hasAspect(Object dto) {
            return dto instanceof CircularLetter;
        }
    }

    private class ValidToField extends TextBoxField<CircularLetter> {
        @Override
        protected String getValue(CircularLetter dto) {
            return dto.validTo;
        }

        @Override
        protected void setValue(CircularLetter dto, String value) {
            dto.validTo = value;
        }

        @Override
        public boolean hasAspect(Object dto) {
            return dto instanceof CircularLetter;
        }
    }

    private class ValidToEnField extends TextBoxField<CircularLetter> {
        @Override
        protected String getValue(CircularLetter dto) {
            return dto.validTo_en;
        }

        @Override
        protected void setValue(CircularLetter dto, String value) {
            dto.validTo_en = value;
        }

        @Override
        public boolean hasAspect(Object dto) {
            return dto instanceof CircularLetter;
        }
    }

    private class ValidExtendedUntilField extends TextBoxField<CircularLetter> {
        @Override
        protected String getValue(CircularLetter dto) {
            return dto.validExtendedUntil;
        }

        @Override
        protected void setValue(CircularLetter dto, String value) {
            dto.validExtendedUntil = value;
        }

        @Override
        public boolean hasAspect(Object dto) {
            return dto instanceof CircularLetter;
        }
    }

    private class ValidExtendedUntilEnField extends TextBoxField<CircularLetter> {
        @Override
        protected String getValue(CircularLetter dto) {
            return dto.validExtendedUntil_en;
        }

        @Override
        protected void setValue(CircularLetter dto, String value) {
            dto.validExtendedUntil_en = value;
        }

        @Override
        public boolean hasAspect(Object dto) {
            return dto instanceof CircularLetter;
        }
    }

    private class ActionField extends TextAreaField<CircularLetter> {
        @Override
        public boolean hasAspect(Object dto) {
            return dto instanceof CircularLetter;
        }

        @Override
        protected String getValue(CircularLetter dto) {
            return dto.action;
        }

        @Override
        protected void setValue(CircularLetter dto, String value) {
            dto.action = value;
        }
    }

    private class ActionEnField extends TextAreaField<CircularLetter> {
        @Override
        public boolean hasAspect(Object dto) {
            return dto instanceof CircularLetter;
        }

        @Override
        protected String getValue(CircularLetter dto) {
            return dto.action_en;
        }

        @Override
        protected void setValue(CircularLetter dto, String value) {
            dto.action_en = value;
        }
    }

    private class ApprovePositionField extends PositionField<CircularLetter> {
        @Override
        protected Position getValue(CircularLetter dto) {
            return dto.approvePosition;
        }

        @Override
        protected void setValue(CircularLetter dto, Position value) {
            dto.approvePosition = value;

        }

        @Override
        public boolean hasAspect(Object dto) {
            return dto instanceof CircularLetter;
        }

        @Override
        public String getEditorTab() {
            return messages.route();
        }

        @Override
        public Widget getViewWidget() {
            return null;
        }
    }

    private class ChangedContentField extends TextAreaField<CircularLetter> {
        @Override
        public boolean hasAspect(Object dto) {
            return dto instanceof CircularLetter;
        }

        @Override
        protected String getValue(CircularLetter dto) {
            return dto.changedContent;
        }

        @Override
        protected void setValue(CircularLetter dto, String value) {
            dto.changedContent = value;
        }

        @Override
        public String getEditorTab() {
            return messages.attachment();
        }

        @Override
        public String getViewTab() {
            return messages.attachment();
        }

        @Override
        public Integer getTabIndex() {
            return 1;
        }
    }

    private class ApprovedRequirementsField extends RequirementsGridField<CircularLetter> {
        @Override
        protected List<Requirement> getValue(CircularLetter dto) {
            return dto.approvedRequirements;
        }

        @Override
        protected void setValue(CircularLetter dto, List<Requirement> value) {
            dto.approvedRequirements = value;
        }

        @Override
        public boolean hasAspect(Object dto) {
            return dto instanceof CircularLetter;
        }

        @Override
        public GridEditor getEditorWidget() {
            final RequirementsGridEditor result = new RequirementsGridEditor(AbstractRoute.LIFECYCLE_APPROVED);
            result.addImageButton("images/buttons/32/delete.png", messages.deleteAll(), false, new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    result.setValue(null);
                }
            });
            return result;
        }

        @Override
        public String getEditorTab() {
            return messages.attachment();
        }

        @Override
        public String getViewTab() {
            return messages.attachment();
        }

        @Override
        public Integer getTabIndex() {
            return 1;
        }
    }

    private class LinkField extends DocumentSelectorField<CircularLetter> {
        protected LinkField() {
            super(CircularLetter.KIND);
        }

        @Override
        protected Document getValue(CircularLetter dto) {
            return dto;
        }

        @Override
        protected void setValue(CircularLetter dto, Document value) {
        }

        @Override
        public boolean hasAspect(Object dto) {
            return dto instanceof CircularLetter;
        }

        @Override
        public DocumentSelector getEditorWidget() {
            return null;
        }

        @Override
        public FormItemBase.Order getOrder() {
            return FormItemBase.Order.LOW2;
        }
    }

}
