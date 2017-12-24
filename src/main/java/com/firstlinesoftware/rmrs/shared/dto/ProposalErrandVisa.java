package com.firstlinesoftware.rmrs.shared.dto;

import com.firstlinesoftware.exec.shared.dto.Visa;

import java.util.List;

public class ProposalErrandVisa extends Visa implements HasChangesToRequirements {
    public static final String KIND = "visa.proposal";
    public String createdErrandType;

    public List<Requirement> createNew;
    public List<Requirement> modifyExisting;

    @Override
    public ProposalErrandVisa clone() {
        final ProposalErrandVisa r = (ProposalErrandVisa) super.clone();
        r.createNew = cloneArray(createNew);
        r.modifyExisting = cloneArray(modifyExisting);
        r.createdErrandType = createdErrandType;
        return r;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj)
                && equals(createNew, ((ProposalErrandVisa) obj).createNew)
                && equals(modifyExisting, ((ProposalErrandVisa) obj).modifyExisting)
                && equals(createdErrandType, ((ProposalErrandVisa) obj).createdErrandType);
    }

    @Override
    protected ProposalErrandVisa createInstance() {
        return new ProposalErrandVisa();
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

    public String getCreatedErrandType() {
        return createdErrandType;
    }

    public void setCreatedErrandType(String createdErrandType) {
        this.createdErrandType = createdErrandType;
    }
}
