package com.firstlinesoftware.rmrs.shared.dto;

import com.firstlinesoftware.exec.shared.dto.AbstractErrandReport;

import java.util.List;

public class ProposalErrandReport extends AbstractErrandReport implements HasChangesToRequirements {
    public static final String KIND = "errand.report.proposal";

    public Boolean accepted;

    public List<Requirement> createNew;
    public List<Requirement> modifyExisting;

    public ProposalErrandReport() {
        kind = KIND;
    }

    @Override
    protected ProposalErrandReport createInstance() {
        return new ProposalErrandReport();
    }

    @Override
    public ProposalErrandReport clone() {
        final ProposalErrandReport r = (ProposalErrandReport) super.clone();
        r.accepted = accepted;
        r.createNew = cloneArray(createNew);
        r.modifyExisting = cloneArray(modifyExisting);
        return r;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj)
                && equals(accepted, ((ProposalErrandReport) obj).accepted)
                && equals(createNew, ((ProposalErrandReport) obj).createNew)
                && equals(modifyExisting, ((ProposalErrandReport) obj).modifyExisting);
    }

    @Override
    public List<Requirement> getCreateNew() {
        return createNew;
    }

    @Override
    public void setCreateNew(List<Requirement> createNew) {
        this.createNew = createNew;
    }

    @Override
    public List<Requirement> getModifyExisting() {
        return modifyExisting;
    }

    @Override
    public void setModifyExisting(List<Requirement> modifyExisting) {
        this.modifyExisting = modifyExisting;
    }
}
