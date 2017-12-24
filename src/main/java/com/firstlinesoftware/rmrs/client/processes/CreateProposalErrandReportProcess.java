package com.firstlinesoftware.rmrs.client.processes;

import com.firstlinesoftware.ecm.client.processes.CreateCompositeDocumentProcess;
import com.firstlinesoftware.rmrs.shared.dto.ProposalErrandReport;

public class CreateProposalErrandReportProcess extends CreateCompositeDocumentProcess<ProposalErrandReport> {
    @Override
    protected ProposalErrandReport createDTO() {
        return new ProposalErrandReport();
    }
}
