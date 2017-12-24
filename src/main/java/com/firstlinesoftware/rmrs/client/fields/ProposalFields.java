package com.firstlinesoftware.rmrs.client.fields;

import com.firstlinesoftware.base.client.Base;
import com.firstlinesoftware.base.client.factories.FormItemFactory;
import com.firstlinesoftware.base.client.factories.Registrable;
import com.firstlinesoftware.base.client.fields.TextAreaField;
import com.firstlinesoftware.base.client.widgets.GridEditor;
import com.firstlinesoftware.orgstruct.client.fields.PositionsGridField;
import com.firstlinesoftware.orgstruct.shared.dto.Position;
import com.firstlinesoftware.rmrs.client.Rmrs;
import com.firstlinesoftware.rmrs.client.icons.RmrsIcons;
import com.firstlinesoftware.rmrs.client.messages.RmrsMessages;
import com.firstlinesoftware.rmrs.shared.dto.Proposal;
import com.firstlinesoftware.rmrs.shared.dto.Requirement;

import java.util.List;

public class ProposalFields implements Registrable {
    private final FormItemFactory formItemFactory = Base.getInjector().getFormItemFactory();
    private final RmrsMessages rmrsMessages = Rmrs.getInjector().getMessages();
    private final RmrsIcons icons = Rmrs.getInjector().getIcons();

    @Override
    public void register() {
        formItemFactory.register(new CommentField(), rmrsMessages.comment());
        formItemFactory.register(new ChangedRequirementsField(), rmrsMessages.placeInStructure());
        formItemFactory.register(new ExecutivesField(), rmrsMessages.executives());
        formItemFactory.register(new ErrantTextField(), rmrsMessages.errand());
    }

    private class ExecutivesField extends PositionsGridField<Proposal> {
        @Override
        protected List<Position> getValue(Proposal dto) {
            return dto.executives;
        }

        @Override
        protected void setValue(Proposal dto, List<Position> value) {
            dto.executives = value;
        }

        @Override
        public boolean hasAspect(Object dto) {
            return dto instanceof Proposal;
        }

        @Override
        public String getEditorTab() {
            return rmrsMessages.executives();
        }

        @Override
        public String getViewTab() {
            return rmrsMessages.executives();
        }

        @Override
        public Integer getTabIndex() {
            return 1;
        }
    }

    private class ChangedRequirementsField extends RequirementsGridField<Proposal> {
        @Override
        protected List<Requirement> getValue(Proposal dto) {
            return dto.changedRequirements;
        }

        @Override
        protected void setValue(Proposal dto, List<Requirement> value) {
            dto.changedRequirements = value;
        }

        @Override
        public boolean hasAspect(Object dto) {
            return dto instanceof Proposal;
        }

        @Override
        public String getEditorTab() {
            return rmrsMessages.executives();
        }

        @Override
        public String getViewTab() {
            return rmrsMessages.executives();
        }

        @Override
        public Integer getTabIndex() {
            return 1;
        }

        @Override
        public GridEditor getEditorWidget() {
            final GridEditor result = super.getEditorWidget();
            result.setColumns("number", "title");
            return result;
        }
    }

    private class ErrantTextField extends TextAreaField<Proposal> {
        @Override
        protected String getValue(Proposal dto) {
            return dto.errandText;
        }

        @Override
        protected void setValue(Proposal dto, String errandText) {
            dto.errandText = errandText;
        }

        @Override
        public boolean hasAspect(Object dto) {
            return dto instanceof Proposal;
        }
    }

    private class CommentField extends TextAreaField<Proposal> {
        @Override
        protected String getValue(Proposal dto) {
            return dto.comment;
        }

        @Override
        protected void setValue(Proposal dto, String value) {
            dto.comment = value;

        }

        @Override
        public boolean hasAspect(Object dto) {
            return dto instanceof Proposal;
        }
    }
}
