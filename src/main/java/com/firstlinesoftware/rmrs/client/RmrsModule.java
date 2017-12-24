package com.firstlinesoftware.rmrs.client;

import com.firstlinesoftware.rmrs.client.desktop.ExchangeMenuBar;
import com.firstlinesoftware.rmrs.client.icons.RmrsIcons;
import com.firstlinesoftware.rmrs.client.messages.RmrsMessages;
import com.firstlinesoftware.rmrs.client.processes.*;
import com.firstlinesoftware.rmrs.client.proxies.RequirementProxy;
import com.firstlinesoftware.rmrs.client.proxies.RequirementsReportProxy;
import com.firstlinesoftware.rmrs.client.views.editors.CircularLetterEditorImpl;
import com.firstlinesoftware.rmrs.client.views.editors.CreateProposalErrandReportEditorImpl;
import com.firstlinesoftware.rmrs.client.views.editors.ProposalEditorImpl;
import com.firstlinesoftware.rmrs.client.views.editors.RequirementEditorImpl;
import com.firstlinesoftware.rmrs.client.views.selectors.SearchRequirementsImpl;
import com.firstlinesoftware.rmrs.client.views.selectors.SelectRequirementsForDeleteImpl;
import com.firstlinesoftware.rmrs.client.views.selectors.SelectRequirementsImpl;
import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;

public class RmrsModule extends AbstractGinModule {
    @Override
    protected void configure() {
        bind(RmrsMessages.class).in(Singleton.class);
        bind(RmrsIcons.class).in(Singleton.class);
        bind(ExchangeMenuBar.class).in(Singleton.class);

        bind(RequirementProxy.class).in(Singleton.class);
        bind(RequirementsReportProxy.class).in(Singleton.class);

        bind(SelectRequirementsImpl.class).in(Singleton.class);
        bind(SearchRequirementsImpl.class).in(Singleton.class);

        bind(EditRequirementProcess.class).in(Singleton.class);
        bind(SearchRequirementProcess.class).in(Singleton.class);
        bind(CreateProposalErrandReportProcess.class).in(Singleton.class);
        bind(EditProposalProcess.class).in(Singleton.class);
        bind(CreateProposalErrandVisaProcess.class).in(Singleton.class);
        bind(EditCircularLetterProcess.class).in(Singleton.class);

        bind(RequirementEditorImpl.class).in(Singleton.class);
        bind(ProposalEditorImpl.class).in(Singleton.class);
        bind(CircularLetterEditorImpl.class).in(Singleton.class);
        bind(CreateProposalErrandReportEditorImpl.class).in(Singleton.class);
        bind(SelectRequirementsForDeleteImpl.class).in(Singleton.class);
    }
}
