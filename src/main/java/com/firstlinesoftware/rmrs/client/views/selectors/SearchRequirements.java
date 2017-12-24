package com.firstlinesoftware.rmrs.client.views.selectors;

import com.firstlinesoftware.base.client.views.selectors.SearchPersistent;
import com.firstlinesoftware.rmrs.shared.dto.Requirement;
import com.firstlinesoftware.rmrs.shared.dto.SearchRequirementData;

public interface SearchRequirements extends SearchPersistent<SearchRequirementData, Requirement> {
    public interface Presenter extends SearchPersistent.Presenter {
        void changeMode(boolean mode);
    }
}
