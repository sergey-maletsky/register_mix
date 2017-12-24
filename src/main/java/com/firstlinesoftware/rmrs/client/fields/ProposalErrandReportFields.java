package com.firstlinesoftware.rmrs.client.fields;

import com.firstlinesoftware.base.client.Base;
import com.firstlinesoftware.base.client.factories.FormItemFactory;
import com.firstlinesoftware.base.client.factories.Registrable;
import com.firstlinesoftware.base.client.widgets.GridEditor;
import com.firstlinesoftware.rmrs.client.Rmrs;
import com.firstlinesoftware.rmrs.client.messages.RmrsMessages;
import com.firstlinesoftware.rmrs.shared.dto.ConsiderProposalErrand;
import com.firstlinesoftware.rmrs.shared.dto.Requirement;

import java.util.List;

public class ProposalErrandReportFields implements Registrable {
    private final FormItemFactory formItemFactory = Base.getInjector().getFormItemFactory();
    private final RmrsMessages messages = Rmrs.getInjector().getMessages();

    @Override
    public void register() {
        formItemFactory.register(new CreateRequirementsField(), messages.createRequirements());
        formItemFactory.register(new ModifyRequirementsField(), messages.modifyRequirements());
    }

    private class CreateRequirementsField extends RequirementsGridField<ConsiderProposalErrand> {
        @Override
        protected List<Requirement> getValue(ConsiderProposalErrand dto) {
            return dto.getLastReport().getCreateNew();
        }

        @Override
        protected void setValue(ConsiderProposalErrand dto, List<Requirement> value) {
        }

        @Override
        public boolean hasAspect(Object dto) {
            return isErrandReport(dto);
        }

        @Override
        public GridEditor getEditorWidget() {
            return null;
        }

        @Override
        public String getViewTab() {
            return messages.report();
        }
    }

    private class ModifyRequirementsField extends RequirementsGridField<ConsiderProposalErrand> {
        @Override
        protected List<Requirement> getValue(ConsiderProposalErrand dto) {
            return dto.getLastReport().getModifyExisting();
        }

        @Override
        protected void setValue(ConsiderProposalErrand dto, List<Requirement> value) {
        }

        @Override
        public boolean hasAspect(Object dto) {
            return isErrandReport(dto);
        }

        @Override
        public GridEditor getEditorWidget() {
            return null;
        }

        @Override
        public String getViewTab() {
            return messages.report();
        }
    }


    private static boolean isErrandReport(Object dto) {
        return dto instanceof ConsiderProposalErrand && ((ConsiderProposalErrand) dto).getLastReport() != null;
    }

}
