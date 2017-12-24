package com.firstlinesoftware.rmrs.client.messages;

import com.firstlinesoftware.route.client.messages.RouteMessages;

public interface RmrsMessages extends RouteMessages {
    String shortDescription();

    String header();

    String englishContent();

    String russianContent();

    String includedIn();

    String setOfRequirements();

    String list();

    String saveAsDraft();

    String saveAndSend();

    String executives();

    String changedRequirements();

    String requirement();

    String requirements();

    String drafts();

    String proposals();

    String personalCabinet();

    String part();

    String volume();

    String effectiveBegin();

    String effectiveEnd();

    String responsible();

    String sendToApproval();

    String approvalByBoss();

    String errorWhileGettingResponsible();

    String switchMode();

    String order();

    String reorder();

    String errorWhileReordering();

    String reordered();

    String newOrder();

    String rejected();

    String circularLetter();

    String circularNumber();

    String circularReferred();

    String circularObservable();

    String circularCommissioning();

    String circularValidTo();

    String circularValidExtendedUntil();

    String circularContent();

    String circularAction();

    String circularReferredEn();

    String circularObservableEn();

    String circularCommissioningEn();

    String circularValidToEn();

    String circularValidExtendedUntilEn();

    String circularContentEn();

    String circularActionEn();

    String circularApprover();

    String circularChangedContent();

    String circularApprovedRequirements();

    String attachment();

    String topic();

    String exchange();

    String importRequirements();

    String exportRequirements();


    String errorWhileImportingRequirements();

    String gotoRoot();

    String simpleSearch();

    String languageRu();

    String languageEng();

    String createRequirements();

    String modifyRequirements();

    String rejectProposal();

    String approveProposal();

    String proposalApproved();

    String proposalRejected();

    String multiAdd();

    String placeInStructure();

    String createEmpty();

    String linksToOtherCircularLetter();

    String circularBusinessCaseNumber();

    String completeErrand();

    String approved();

    String sendToEditorialBoard();

    String statusChanged();

    String errorWhileChangingStatus();

    String onSigning();

    String signedByEditorialBoard();

    String onHandling();

    String requirementsImported();

    String tags();

    String signedDate();

    String noErrands();

    String saveInSet();

    String sentToApproval();

    String onEditorialBoard();

    String createProposal();

    String proposalBasedOnRequirement();

    String proposalToChangeRequirement();

    String errandsRejected();

    String confirm();

    String numberAndNomenclature();

    String saveAndSendWithoutAgreement();

    String proposalErrandText();

    String viewContent();

    String assignReqiurementsToPostion();

    String unassignReqiurementsToPostion();

    String addResponsibilityForRequirements();

    String removeResponsibilityForRequirements();

    String createAcceptanceProposalErrandReport();

    String createRejectionProposalErrandReport();

    String createAcceptanceReportVisa();

    String createRejectionReportVisa();

    String clearSelection();

    String signed();

    String acceptErrandReport();

    String searchByExecutor();

    String parentNumber();

    String startOrgstructSync();

    String syncFinished();

    String syncFailed();

    String numberOfViews();

    String createSection();

    String totalVisits();

    String count();

    String notification();

    String requirementSearch();

    String directories();

    String requiremntEdit();

    String createDirectory();

    String createRequirement();

    String createCircularLetter();

    String bbErrandsArchiveAssigned();

    String bbErrandsArchiveIssued();

    String bbErrandsArchiveCancelled();

    String bbErrandsArchiveDelegated();

    String link();

    String onlySigned();

    String circularLetters();

    String selectLanguage();

    String showRecursive();

    String authorManual();

    String instructorManual();

    String path();
}
