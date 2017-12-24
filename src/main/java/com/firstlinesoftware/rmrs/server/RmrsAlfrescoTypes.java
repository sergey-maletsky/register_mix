package com.firstlinesoftware.rmrs.server;

import org.alfresco.service.namespace.QName;

public interface RmrsAlfrescoTypes {
    String PREFIX_RMRS = "{rmrs.firstlinesoftware.com}";
    QName TYPE_REQUIREMENT = QName.createQName(PREFIX_RMRS + "requirement");
    QName TYPE_REQUIREMENT_FOLDER = QName.createQName(PREFIX_RMRS + "requirementsFolder");
    QName TYPE_PROPOSAL = QName.createQName(PREFIX_RMRS + "proposal");
    QName TYPE_CIRCULAR_LETTER = QName.createQName(PREFIX_RMRS + "circular");
    QName TYPE_CONSIDER_PROPOSAL = QName.createQName(PREFIX_RMRS + "considerProposalErrand");
    QName TYPE_PROPOSAL_ERRAND_REPORT = QName.createQName(PREFIX_RMRS + "proposalErrandReport");
    QName TYPE_PROPOSAL_ERRAND_VISA = QName.createQName(PREFIX_RMRS + "proposalErrandVisa");
    QName TYPE_CHANGE_REQUIREMENT = QName.createQName(PREFIX_RMRS + "changeRequirementErrand");

    //requirement
    QName PROP_ORDER = QName.createQName(PREFIX_RMRS + "order");
    QName PROP_PARENT = QName.createQName(PREFIX_RMRS + "parent");
    QName PROP_PARENT_NUMBER = QName.createQName(PREFIX_RMRS + "parentNumber");
//    QName PROP_NUMBER = QName.createQName(PREFIX_RMRS + "number");
    QName PROP_PART = QName.createQName(PREFIX_RMRS + "part");
    QName PROP_VOLUME = QName.createQName(PREFIX_RMRS + "volume");
    QName PROP_RUSSIAN = QName.createQName(PREFIX_RMRS + "russian");
    QName PROP_ENGLISH = QName.createQName(PREFIX_RMRS + "english");
    QName PROP_EXPIRED = QName.createQName(PREFIX_RMRS + "expired");
    QName PROP_HEADER = QName.createQName(PREFIX_RMRS + "header");
    QName PROP_LEAF = QName.createQName(PREFIX_RMRS + "leaf");
    QName PROP_LEAF_HEADER = QName.createQName(PREFIX_RMRS + "leafHeader");
    QName PROP_TAGS = QName.createQName(PREFIX_RMRS + "tags");
    QName PROP_CIRCULAR_LETTER = QName.createQName(PREFIX_RMRS + "circularLetter");
    QName PROP_FULL_PATH = QName.createQName(PREFIX_RMRS + "fullPath");

    //proposal
    QName ASSOC_CHANGED_REQUIREMENTS = QName.createQName(PREFIX_RMRS + "changedRequirements");
    QName PROP_EXECUTIVES = QName.createQName(PREFIX_RMRS + "executives");
    QName PROP_ERRAND_TEXT = QName.createQName(PREFIX_RMRS + "errandText");
    QName PROP_COMMENT = QName.createQName(PREFIX_RMRS + "comment");

    //circular letter
    QName PROP_CONTENT = QName.createQName(PREFIX_RMRS + "content");
    QName PROP_OBSERVABLE = QName.createQName(PREFIX_RMRS + "observable");
    QName PROP_COMMISSIONING = QName.createQName(PREFIX_RMRS + "commissioning");
    QName PROP_VALID_TO = QName.createQName(PREFIX_RMRS + "validTo");
    QName PROP_VALID_EXTENDED_UNTIL = QName.createQName(PREFIX_RMRS + "validExtendedUntil");
    QName PROP_ACTION = QName.createQName(PREFIX_RMRS + "action");
    QName PROP_REFERRED_EN = QName.createQName(PREFIX_RMRS + "referred_en");
    QName PROP_CONTENT_EN = QName.createQName(PREFIX_RMRS + "content_en");
    QName PROP_OBSERVABLE_EN = QName.createQName(PREFIX_RMRS + "observable_en");
    QName PROP_COMMISSIONING_EN = QName.createQName(PREFIX_RMRS + "commissioning_en");
    QName PROP_VALID_TO_EN = QName.createQName(PREFIX_RMRS + "validTo_en");
    QName PROP_VALID_EXTENDED_UNTIL_EN = QName.createQName(PREFIX_RMRS + "validExtendedUntil_en");
    QName PROP_ACTION_EN = QName.createQName(PREFIX_RMRS + "action_en");
    QName PROP_APPROVE_POSITION = QName.createQName(PREFIX_RMRS + "approvePosition");
    QName PROP_CHANGED_CONTENT = QName.createQName(PREFIX_RMRS + "changedContent");
    QName PROP_APPROVED_REQUIREMENTS = QName.createQName(PREFIX_RMRS + "approvedRequirements");
    QName PROP_BUSINESS_CASE_NUMBER= QName.createQName(PREFIX_RMRS + "businessCaseNumber");

    //proposalErrandReport
    QName PROP_ACCEPTED = QName.createQName(PREFIX_RMRS + "accepted");
    QName PROP_CREATE_NEW = QName.createQName(PREFIX_RMRS + "createNew");
    QName PROP_MODIFY_EXISTING = QName.createQName(PREFIX_RMRS + "modifyExisting");
    QName PROP_CREATED_ERRAND_TYPE = QName.createQName(PREFIX_RMRS + "createdErrandType");
}
