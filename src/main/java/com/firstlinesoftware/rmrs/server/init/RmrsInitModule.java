package com.firstlinesoftware.rmrs.server.init;

import com.firstlinesoftware.base.server.init.InitFactory;
import com.firstlinesoftware.base.server.init.InitModule;
import com.firstlinesoftware.ecm.server.providers.TaskProviderFactory;
import com.firstlinesoftware.rmrs.server.providers.CircularLetterTaskProvider;
import com.firstlinesoftware.rmrs.server.providers.ProposalTaskProvider;
import com.firstlinesoftware.rmrs.server.providers.RequirementTaskProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@DependsOn("routeInitModule")
public class RmrsInitModule implements InitModule {
    @Autowired
    private InitFactory initFactory;
    //    @Autowired
//    private RequirementsXmlImporter requirementsXmlImporter;
    @Autowired
    private TaskProviderFactory taskProviderFactory;

    @Autowired
    private RequirementTaskProvider requirementTaskProvider;

    @Autowired
    private ProposalTaskProvider proposalTaskProvider;

    @Autowired
    private CircularLetterTaskProvider circularLetterTaskProvider;


    @Override
    public void init() {
//        requirementsXmlImporter.doImport();
//        for (String folder : AbstractRouteTaskProvider.POSITION_FOLDERS) {
//            taskProviderFactory.registerFolder(folder, requirementTaskProvider);
//        }
        for (String lifecycle : RequirementTaskProvider.lifecycle.keySet()) {
            taskProviderFactory.registerFolder(lifecycle, requirementTaskProvider);
        }

        for (String lifecycle : ProposalTaskProvider.lifecycle.keySet()) {
            taskProviderFactory.registerFolder(lifecycle, proposalTaskProvider);
        }

       for (String lifecycle : CircularLetterTaskProvider.lifecycle.keySet()) {
            taskProviderFactory.registerFolder(lifecycle, circularLetterTaskProvider);
        }
    }

    @Override
    public void initDomain(String domain) {
    }

    @PostConstruct
    private void register() {
        initFactory.register(this);
    }
}
