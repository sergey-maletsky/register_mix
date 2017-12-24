package com.firstlinesoftware.rmrs.client.fields;

import com.firstlinesoftware.base.client.Base;
import com.firstlinesoftware.base.client.SuccessCallback;
import com.firstlinesoftware.base.client.factories.FormItemFactory;
import com.firstlinesoftware.base.client.factories.Registrable;
import com.firstlinesoftware.base.client.fields.TextAreaField;
import com.firstlinesoftware.base.client.widgets.FormItemBase;
import com.firstlinesoftware.exec.shared.dto.AbstractErrand;
import com.firstlinesoftware.rmrs.client.Rmrs;
import com.firstlinesoftware.rmrs.client.messages.RmrsMessages;
import com.firstlinesoftware.rmrs.shared.dto.ConsiderProposalErrand;
import com.firstlinesoftware.rmrs.shared.dto.Proposal;
import com.google.gwt.user.client.ui.Widget;

public class ConsiderProposalErrandFields implements Registrable {
    private final FormItemFactory formItemFactory = Base.getInjector().getFormItemFactory();
    private final RmrsMessages messages = Rmrs.getInjector().getMessages();

    @Override
    public void register() {
        formItemFactory.registerHeader(new IsProposalAccepted(), "isProposalAccepted");
        formItemFactory.register(new ProposalTitleField(), messages.topic());
        formItemFactory.register(new ProposalCommentField(), messages.comment());
    }

    private class IsProposalAccepted implements FormItemFactory.HeaderProvider<ConsiderProposalErrand> {
        @Override
        public boolean hasAspect(Object dto) {
            return dto instanceof ConsiderProposalErrand;
        }

        @Override
        public String getStyle(ConsiderProposalErrand dto) {
            return dto.getLastReport() != null ? AbstractErrand.LIFECYCLE_ONCONTROL.equals(dto.lifecycle) ? "onControl" : "wasOnControl" : null;
        }

        @Override
        public void getValue(ConsiderProposalErrand dto, SuccessCallback<String> callback) {
            final Boolean accepted = dto.getLastReport() != null ? dto.getLastReport().accepted : null;
            callback.onSuccess(accepted != null ? accepted ? messages.proposalApproved() : messages.proposalRejected() : null );
        }

        @Override
        public String getTitle(ConsiderProposalErrand dto) {
            return null;
        }
    }

    private class ProposalTitleField extends TextAreaField<ConsiderProposalErrand> {
        @Override
        protected String getValue(ConsiderProposalErrand dto) {
            return dto.document.getTitle();
        }

        @Override
        protected void setValue(ConsiderProposalErrand dto, String value) {
        }

        @Override
        public boolean hasAspect(Object dto) {
            return dto instanceof ConsiderProposalErrand;
        }

        @Override
        public Widget getEditorWidget() {
            return null;
        }

        @Override
        public FormItemBase.Order getOrder() {
            return FormItemBase.Order.HIGH0;
        }
    }

    private class ProposalCommentField extends TextAreaField<ConsiderProposalErrand> {
        @Override
        protected String getValue(ConsiderProposalErrand dto) {
            return dto.document instanceof Proposal ? ((Proposal) dto.document).comment : null;
        }

        @Override
        protected void setValue(ConsiderProposalErrand dto, String value) {
        }

        @Override
        public boolean hasAspect(Object dto) {
            return dto instanceof ConsiderProposalErrand;
        }

        @Override
        public Widget getEditorWidget() {
            return null;
        }

        @Override
        public FormItemBase.Order getOrder() {
            return FormItemBase.Order.HIGH0;
        }
    }
}
