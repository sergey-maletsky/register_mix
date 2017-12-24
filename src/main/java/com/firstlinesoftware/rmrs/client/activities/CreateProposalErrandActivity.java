package com.firstlinesoftware.rmrs.client.activities;

import com.firstlinesoftware.base.client.SuccessCallback;
import com.firstlinesoftware.base.client.services.UserActionCallback;
import com.firstlinesoftware.base.shared.dto.DateDuration;
import com.firstlinesoftware.exec.client.Exec;
import com.firstlinesoftware.exec.client.activities.EditAbstractErrandActivity;
import com.firstlinesoftware.exec.client.processes.CreateAbstractErrandProcess;
import com.firstlinesoftware.exec.client.proxies.ErrandProxy;
import com.firstlinesoftware.rmrs.client.Rmrs;
import com.firstlinesoftware.rmrs.client.messages.RmrsMessages;
import com.firstlinesoftware.rmrs.client.places.CreateProposalChildErrandPlace;
import com.firstlinesoftware.rmrs.client.places.CreateProposalErrandPlace;
import com.firstlinesoftware.rmrs.shared.dto.ConsiderProposalErrand;
import com.firstlinesoftware.rmrs.shared.dto.Proposal;

public class CreateProposalErrandActivity extends EditAbstractErrandActivity<ConsiderProposalErrand> {
    private final RmrsMessages messages = Rmrs.getInjector().getMessages();
    private final ErrandProxy errandProxy = Exec.getInjector().getErrandProxy();
    private final SuccessCallback<Void> uploadingCallback = new SuccessCallback<Void>() {
        @Override
        public void onSuccess(Void result) {
            errandProxy.create(dto, parentId, new UserActionCallback<String>(
                    execMessages.errandCreated(),
                    execMessages.errorWhileCreatingErrand()) {
                @Override
                public void onActionSuccess(String errandIds) {
                    onResultSuccess(dto);
                }
            });
        }
    };

    public CreateProposalErrandActivity(CreateProposalChildErrandPlace place) {
        authorId = place.authorId;
        parentId = place.parentId;
        defaultKind = ConsiderProposalErrand.KIND;
    }

    public CreateProposalErrandActivity(CreateProposalErrandPlace place) {
        authorId = place.authorId;
        documentId = place.proposalId;
        defaultKind = ConsiderProposalErrand.KIND;
    }

    @Override
    protected CreateAbstractErrandProcess<ConsiderProposalErrand> getProcess() {
        return Rmrs.getInjector().getCreateProposalErrandProcess();
    }


    @Override
    public void setupFields(ConsiderProposalErrand dto) {
        super.setupFields(dto);
        if (parentErrand != null) {
            view.getName().setValue(parentErrand.getName());
            view.relatedDocuments.setValue(parentErrand.relatedDocuments);
            view.setFormItemWidgetValue(execMessages.controller(), parentErrand.executor);
        } else if (parentDocument instanceof Proposal) {
            final Proposal proposal = (Proposal) parentDocument;
            view.getName().setValue(proposal.errandText);
            view.setFormItemWidgetValue(messages.executionDeadline(), new DateDuration(proposal.controlDate, null, null));
            view.getRelatedDocuments().setValue(proposal.relatedDocuments);
            view.getAttachedFiles().setValue(proposal.attachedFiles);
        }
        disableKind();
    }

    @Override
    public void save() {
        save(uploadingCallback);
    }
}
