package com.firstlinesoftware.rmrs.client.activities;

import com.firstlinesoftware.base.client.services.ActionCallback;
import com.firstlinesoftware.ecm.client.processes.CreateCompositeDocumentProcess;
import com.firstlinesoftware.ecm.shared.dto.RelatedDocument;
import com.firstlinesoftware.exec.shared.dto.AbstractErrand;
import com.firstlinesoftware.rmrs.client.Rmrs;
import com.firstlinesoftware.rmrs.client.messages.RmrsMessages;
import com.firstlinesoftware.rmrs.client.places.CreateProposalErrandReportPlace;
import com.firstlinesoftware.rmrs.client.views.editors.CreateProposalErrandReportEditorImpl;
import com.firstlinesoftware.rmrs.shared.dto.ProposalErrandReport;
import com.firstlinesoftware.rmrs.shared.dto.RmrsDirectories;
import com.firstlinesoftware.route.client.Route;
import com.firstlinesoftware.route.client.activities.CreateAbstractErrandReportActivity;

import java.util.ArrayList;
import java.util.List;

public class CreateProposalErrandReportActivity extends CreateAbstractErrandReportActivity<ProposalErrandReport, CreateProposalErrandReportEditorImpl> {
    private final RmrsMessages messages = Rmrs.getInjector().getMessages();

    private final boolean accepted;

    public CreateProposalErrandReportActivity(CreateProposalErrandReportPlace place) {
        super(place.parentErrand, place.executorId);
        accepted = place.accepted;
        defaultKind = ProposalErrandReport.KIND;
    }

    @Override
    public void save() {
        flush();
        getDto().accepted = accepted;
        super.save();
    }

    @Override
    protected void setupView() {
        super.setupView();
        setDefaultTab(messages.report());
        view.save.setText(messages.saveAndSendWithoutAgreement());
    }

    @Override
    protected CreateProposalErrandReportEditorImpl createView() {
        return Rmrs.getInjector().getCreateProposalErrandReportEditor();
    }

    @Override
    protected void setupVisibility() {
        super.setupVisibility();
        view.setVisible(accepted, messages.createRequirements(), messages.modifyRequirements());
    }

    @Override
    protected void setupFields(ProposalErrandReport dto) {
        super.setupFields(dto);
        if (parentErrand != null) {
            documentProxy.get(parentErrand, new ActionCallback<AbstractErrand>(messages.errorWhileGettingErrandInfo()) {
                @Override
                public void onActionSuccess(AbstractErrand result) {
                    if (result != null && result.relatedDocuments != null) {
                        final List<Object> reqsToChange = new ArrayList<>();
                        for (RelatedDocument r : result.relatedDocuments) {
                            if (RmrsDirectories.RELATION_TYPE_CHANGES.equals(r.relationType)) {
                                reqsToChange.add(r.document);
                            }
                        }
                        view.setFormItemWidgetValueIfEmpty(messages.changedRequirements(), reqsToChange);
                        view.getRelatedDocuments().setValue(result.relatedDocuments);
                    }
                }
            });
        }
    }

    @Override
    protected CreateCompositeDocumentProcess<ProposalErrandReport> getProcess() {
        return Rmrs.getInjector().getCreateProposalErrandReportProcess();
    }

    @Override
    public String getTitle() {
        return accepted ? messages.createAcceptanceProposalErrandReport() : messages.createRejectionProposalErrandReport();
    }
}
