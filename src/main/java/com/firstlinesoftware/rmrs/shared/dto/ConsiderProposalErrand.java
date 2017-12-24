package com.firstlinesoftware.rmrs.shared.dto;

import com.firstlinesoftware.ecm.shared.dto.HasComments;
import com.firstlinesoftware.exec.shared.dto.AbstractErrand;
import com.firstlinesoftware.route.shared.dto.IsRegistrable;

import java.util.Date;

public class ConsiderProposalErrand extends AbstractErrand implements IsRegistrable, HasComments {
    public static final String KIND = RmrsTasks.ERRAND_CONSIDER_PROPOSAL;

    public ConsiderProposalErrand() {
        this.kind = KIND;
    }

    @Override
    public Date getRegistrationDate() {
        return getCreated();
    }

    @Override
    public boolean isRegistered() {
        return true;
    }

    @Override
    protected ConsiderProposalErrand createInstance() {
        return new ConsiderProposalErrand();
    }

    @Override
    public ConsiderProposalErrand clone() {
        return (ConsiderProposalErrand) super.clone();
    }

    @Override
    public ProposalErrandReport getLastReport() {
        return (ProposalErrandReport) super.getLastReport();
    }
}