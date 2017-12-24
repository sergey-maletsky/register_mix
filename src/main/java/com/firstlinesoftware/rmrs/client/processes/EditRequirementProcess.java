package com.firstlinesoftware.rmrs.client.processes;

import com.firstlinesoftware.ecm.client.processes.CreateCompositeDocumentProcess;
import com.firstlinesoftware.rmrs.shared.dto.Requirement;

public class EditRequirementProcess extends CreateCompositeDocumentProcess<Requirement> {
    @Override
    protected Requirement createDTO() {
        return new Requirement();
    }


}
