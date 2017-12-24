package com.firstlinesoftware.rmrs.client;

import com.firstlinesoftware.rmrs.client.desktop.ExchangeMenuBar;
import com.firstlinesoftware.rmrs.client.desktop.HistoryMenuBar;
import com.firstlinesoftware.rmrs.client.icons.RmrsIcons;
import com.firstlinesoftware.rmrs.client.messages.RmrsMessages;
import com.firstlinesoftware.rmrs.client.processes.*;
import com.firstlinesoftware.rmrs.client.proxies.RequirementProxy;
import com.firstlinesoftware.rmrs.client.proxies.RequirementsReportProxy;
import com.firstlinesoftware.rmrs.client.proxies.RestOrgstructSyncProxy;
import com.firstlinesoftware.rmrs.client.views.editors.CircularLetterEditorImpl;
import com.firstlinesoftware.rmrs.client.views.editors.CreateProposalErrandReportEditorImpl;
import com.firstlinesoftware.rmrs.client.views.editors.ProposalEditorImpl;
import com.firstlinesoftware.rmrs.client.views.editors.RequirementEditorImpl;
import com.firstlinesoftware.rmrs.client.views.selectors.SearchRequirementsImpl;
import com.firstlinesoftware.rmrs.client.views.selectors.SelectRequirementsForDeleteImpl;
import com.firstlinesoftware.rmrs.client.views.selectors.SelectRequirementsImpl;
import com.google.gwt.inject.client.GinModules;
import com.google.gwt.inject.client.Ginjector;

@GinModules(RmrsModule.class)
public interface RmrsGinjector extends Ginjector {
    RmrsMessages getMessages();
    RmrsIcons getIcons();
    ExchangeMenuBar getExchangeMenuBar();

    RequirementProxy getRequirementProxy();
    RequirementsReportProxy getRequirementReportProxy();

    SearchRequirementsImpl getSearchRequirements();
    SelectRequirementsImpl getSelectRequirements();

    EditRequirementProcess getEditRequirementProcess();
    SearchRequirementProcess getSearchRequirementProcess();
    EditProposalProcess getEditProposalProcess();
    EditCircularLetterProcess getEditCircularLetterProcess();
    CreateProposalErrandReportProcess getCreateProposalErrandReportProcess();

    RequirementEditorImpl getRequirementEditor();
    ProposalEditorImpl getProposalEditor();
    CircularLetterEditorImpl getCircularLetterEditor();
    CreateProposalErrandReportEditorImpl getCreateProposalErrandReportEditor();

    CreateProposalErrandVisaProcess getCreateProposalErrandVisaProcess();

    SelectRequirementsForDeleteImpl getSelectRequirementsForDelete();

    CreateProposalErrandProcess getCreateProposalErrandProcess();

    RestOrgstructSyncProxy getOrgstructSyncProxy();

    HistoryMenuBar getHistoryMenuBar();
}
