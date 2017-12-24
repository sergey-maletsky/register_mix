package com.firstlinesoftware.rmrs.client.views.selectors;

import com.firstlinesoftware.base.client.views.View;
import com.firstlinesoftware.rmrs.shared.dto.Requirement;
import java.util.Set;

public interface SelectRequirementsForDelete {
    public interface Presenter extends View.Presenter {
        void select(Set<Requirement> requirements);

        void cancel();

    }
}
