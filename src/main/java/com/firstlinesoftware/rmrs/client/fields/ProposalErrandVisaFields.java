package com.firstlinesoftware.rmrs.client.fields;

import com.firstlinesoftware.base.client.Base;
import com.firstlinesoftware.base.client.factories.FormItemFactory;
import com.firstlinesoftware.base.client.factories.Registrable;
import com.firstlinesoftware.base.client.fields.DirectoryComboBoxField;
import com.firstlinesoftware.base.shared.directories.DirectoryType;
import com.firstlinesoftware.ecm.shared.directories.EcmDirectories;
import com.firstlinesoftware.ord.shared.dto.Errand;
import com.firstlinesoftware.rmrs.client.Rmrs;
import com.firstlinesoftware.rmrs.client.messages.RmrsMessages;
import com.firstlinesoftware.rmrs.shared.dto.ProposalErrandVisa;

import static com.firstlinesoftware.rmrs.shared.dto.RmrsTasks.ERRAND_CHANGE_REQUIREMENT;

public class ProposalErrandVisaFields implements Registrable {
    private final FormItemFactory formItemFactory = Base.getInjector().getFormItemFactory();
    private final RmrsMessages messages = Rmrs.getInjector().getMessages();

    @Override
    public void register() {
        formItemFactory.register(new ErrandTypeField(EcmDirectories.DOCUMENT_TYPES, Errand.KIND), messages.kind());
    }

    private class ErrandTypeField extends DirectoryComboBoxField<ProposalErrandVisa> {
        ErrandTypeField(DirectoryType directory, String parent) {
            super(directory, parent);
        }

        @Override
        public boolean hasAspect(Object dto) {
            return dto instanceof ProposalErrandVisa;
        }

        @Override
        protected String getValue(ProposalErrandVisa proposalErrandVisa) {
            return ERRAND_CHANGE_REQUIREMENT;
        }

        @Override
        protected void setValue(ProposalErrandVisa proposalErrandVisa, String createdErrandType) {
            proposalErrandVisa.setCreatedErrandType(createdErrandType);
        }
    }
}
