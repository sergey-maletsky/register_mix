package com.firstlinesoftware.rmrs.shared.dto;

import com.firstlinesoftware.base.shared.dto.DateRange;
import com.firstlinesoftware.ecm.shared.dto.SearchDocumentsData;
import com.firstlinesoftware.orgstruct.shared.dto.Position;

public class SearchRequirementData extends SearchDocumentsData {
    public String text;
    public Requirement parent;
    public DateRange effective;
    public String lifecycle;
    public Position author;

    @Override
    protected SearchRequirementData createInstance() {
        return new SearchRequirementData();
    }

    @Override
    public SearchRequirementData clone() {
        final SearchRequirementData result = (SearchRequirementData) super.clone();
        result.text = text;
        result.parent = parent;
        result.effective = effective;
        result.lifecycle = lifecycle;
        result.author = author;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj)
                && equals(text, ((SearchRequirementData) obj).text)
                && equals(parent, ((SearchRequirementData) obj).parent)
                && equals(effective, ((SearchRequirementData) obj).effective)
                && equals(lifecycle, ((SearchRequirementData) obj).lifecycle)
                && equals(author, ((SearchRequirementData) obj).author)
                ;
    }
}
