package com.firstlinesoftware.rmrs.client.processes;

import com.firstlinesoftware.base.client.processes.SearchPersistentProcess;
import com.firstlinesoftware.rmrs.shared.dto.SearchRequirementData;

public class SearchRequirementProcess extends SearchPersistentProcess<SearchRequirementData> {
    @Override
    protected SearchRequirementData createDTO() {
        return new SearchRequirementData();
    }
}
