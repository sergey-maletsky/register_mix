package com.firstlinesoftware.rmrs.client.processes;

import com.firstlinesoftware.ecm.client.processes.CreateCompositeDocumentProcess;
import com.firstlinesoftware.rmrs.shared.dto.Proposal;

public class EditProposalProcess extends CreateCompositeDocumentProcess<Proposal> {
    @Override
    protected Proposal createDTO() {
        return new Proposal();
    }


}
