package com.firstlinesoftware.rmrs.client.processes;

import com.firstlinesoftware.ecm.client.processes.CreateCompositeDocumentProcess;
import com.firstlinesoftware.rmrs.shared.dto.ProposalErrandVisa;

/**
 * User: VAntonov
 * Date: 21.12.2010
 * Time: 16:59:41
 */
public class CreateProposalErrandVisaProcess extends CreateCompositeDocumentProcess<ProposalErrandVisa> {
    @Override
    protected ProposalErrandVisa createDTO() {
        return new ProposalErrandVisa();
    }
}
