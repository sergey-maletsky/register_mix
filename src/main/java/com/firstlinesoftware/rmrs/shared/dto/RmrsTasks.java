package com.firstlinesoftware.rmrs.shared.dto;

public interface RmrsTasks {
    String REQ_TASK_TYPE = "requirements";
    String REQ_DRAFTS = "requirements.drafts";
    String REQ_SENT_TO_APPROVAL = "requirements.sent.to.approval";
    String REQ_ON_APPROVAL = "requirements.approval";
    String REQ_APPROVED = "requirements.approved";
    String REQ_ON_SIGNING = "requirements.signing";
    String REQ_SIGNED = "requirements.signed";
    String REQ_REJECTED = "requirements.rejected";

    String PROPOSAL_TASK_TYPE = "proposal";
    String PROPOSAL_DRAFTS = "proposal.drafts";
    String PROPOSAL_ON_EXECUTION = "proposal.execution";
    String PROPOSAL_ARCHIVED = "proposal.archived";

    String CIRCULAR_LETTER_TASK_TYPE = "circular";
    String CIRCULAR_LETTER_DRAFTS = "circular.drafts";
    String CIRCULAR_LETTER_REJECTED = "circular.rejected";
    String CIRCULAR_LETTER_ON_APPROVAL = "circular.approval";
    String CIRCULAR_LETTER_SENT_TO_APPROVAL = "circular.sent.to.approval";;
    String CIRCULAR_LETTER_ON_SIGNING = "circular.signing";
    String CIRCULAR_LETTER_SIGNED = "circular.signed";

    String ERRAND_TASK_REJECTED = "errands_rejected";
    String ERRAND_CONSIDER_PROPOSAL = "errand.considerProposal";
    String ERRAND_CHANGE_REQUIREMENT = "errand.changeRequirement";
}
