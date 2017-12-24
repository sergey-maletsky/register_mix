package com.firstlinesoftware.rmrs.shared.dto;

import com.firstlinesoftware.base.shared.dto.AttachedFile;
import com.firstlinesoftware.ecm.shared.dto.HasComments;
import com.firstlinesoftware.ecm.shared.dto.HasDepartmentTemplates;
import com.firstlinesoftware.ecm.shared.dto.HasHistory;
import com.firstlinesoftware.ecm.shared.dto.HasLifecycle;
import com.firstlinesoftware.exec.shared.dto.AbstractErrand;
import com.firstlinesoftware.exec.shared.dto.HasControlDate;
import com.firstlinesoftware.orgstruct.shared.dto.Position;
import com.firstlinesoftware.route.shared.dto.AbstractRoute;
import com.firstlinesoftware.route.shared.dto.HasErrands;
import com.firstlinesoftware.route.shared.dto.HasRegistration;
import com.firstlinesoftware.route.shared.dto.IsRegistrable;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.Date;
import java.util.List;

public class Proposal extends AbstractRoute implements HasControlDate, HasMultiLanguageFile, HasErrands, HasRegistration, HasHistory, HasLifecycle, IsRegistrable, HasDepartmentTemplates, HasComments {
    public static final String KIND = "routes.proposal";
    private static final Predicate<AbstractErrand> APPROVAL_ERRANDS = new Predicate<AbstractErrand>() {
        @Override
        public boolean apply(AbstractErrand input) {
            return input instanceof ConsiderProposalErrand;
        }
    };
    public static final Predicate<AbstractErrand> EXECUTION_ERRANDS = new Predicate<AbstractErrand>() {
        @Override
        public boolean apply(AbstractErrand input) {
            return input instanceof ChangeRequirementErrand;
        }
    };

    public Date controlDate;
    public Integer controlDays;
    public Boolean businessOnlyControlDays;
    public AttachedFile russian;
    public AttachedFile english;

    public List<Requirement> changedRequirements;
    public List<Position> executives;

    public List<AbstractErrand> errands;
    public String errandText;
    public String comment;

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj)
                && equals(controlDate, ((Proposal) obj).controlDate)
                && equals(controlDays, ((Proposal) obj).controlDays)
                && equals(businessOnlyControlDays, ((Proposal) obj).businessOnlyControlDays)
                && equals(executives, ((Proposal) obj).executives)
                && equals(changedRequirements, ((Proposal) obj).changedRequirements)
                && equals(errands, ((Proposal) obj).errands)
                && equals(russian, ((Proposal) obj).russian)
                && equals(english, ((Proposal) obj).english)
                && equals(errandText, ((Proposal) obj).errandText)
                && equals(comment, ((Proposal) obj).comment)
                ;
    }

    @Override
    protected Proposal createInstance() {
        return new Proposal();
    }

    @Override
    public Proposal clone() {
        final Proposal r = (Proposal) super.clone();
        r.controlDate = controlDate;
        r.controlDays = controlDays;
        r.businessOnlyControlDays = businessOnlyControlDays;
        r.executives = executives;
        r.changedRequirements = changedRequirements;
        r.errands = errands;
        r.russian = russian;
        r.english = english;
        r.errandText = errandText;
        r.comment = comment;
        return r;
    }

    @Override
    public Date getControlDate() {
        return controlDate;
    }

    @Override
    public void setControlDate(final Date controlDate) {
        this.controlDate = controlDate;
    }

    @Override
    public Integer getControlDays() {
        return controlDays;
    }

    @Override
    public void setControlDays(final Integer controlDays) {
        this.controlDays = controlDays;
    }

    @Override
    public Boolean getBusinessOnlyControlDays() {
        return businessOnlyControlDays;
    }

    @Override
    public void setBusinessOnlyControlDays(Boolean businessOnlyControlDays) {
        this.businessOnlyControlDays = businessOnlyControlDays;
    }

    @Override
    public boolean isNegativeDatePossible() {
        return false;
    }

    @Override
    public AttachedFile getRussian() {
        return russian;
    }

    @Override
    public AttachedFile getEnglish() {
        return english;
    }

    @Override
    public void setEnglish(AttachedFile english) {
        this.english = english;
    }

    @Override
    public void setRussian(AttachedFile russian) {
        this.russian = russian;
    }

    @Override
    public List<AbstractErrand> getApprovalErrands() {
        return errands != null ? Lists.newArrayList(Iterables.filter(errands, APPROVAL_ERRANDS))  : Collections.<AbstractErrand>emptyList();
    }

    @Override
    public List<AbstractErrand> getExecutionErrands() {
        return errands != null ? Lists.newArrayList(Iterables.filter(errands, EXECUTION_ERRANDS))  : Collections.<AbstractErrand>emptyList();
    }

    @Override
    public List<AbstractErrand> getErrands() {
        return errands;
    }

    @Override
    public void setErrands(List<AbstractErrand> errands) {
        this.errands = errands;
    }

    public String getErrandText() {
        return errandText;
    }

    public void setErrandText(String errandText) {
        this.errandText = errandText;
    }
}
