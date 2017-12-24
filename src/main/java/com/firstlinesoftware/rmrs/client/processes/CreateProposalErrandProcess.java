package com.firstlinesoftware.rmrs.client.processes;

import com.firstlinesoftware.exec.client.processes.CreateAbstractErrandProcess;
import com.firstlinesoftware.rmrs.shared.dto.ConsiderProposalErrand;

public class CreateProposalErrandProcess extends CreateAbstractErrandProcess<ConsiderProposalErrand> {
    @Override
    protected ConsiderProposalErrand createDTO() {
        return new ConsiderProposalErrand();
    }
}
