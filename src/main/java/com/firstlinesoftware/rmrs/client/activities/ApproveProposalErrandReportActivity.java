package com.firstlinesoftware.rmrs.client.activities;

import com.firstlinesoftware.base.client.Base;
import com.firstlinesoftware.base.client.SuccessCallback;
import com.firstlinesoftware.base.client.desktop.Desktop;
import com.firstlinesoftware.base.client.services.ActionCallback;
import com.firstlinesoftware.base.client.services.UserActionCallback;
import com.firstlinesoftware.base.client.utils.Criteria;
import com.firstlinesoftware.base.client.widgets.DirectoryComboBox;
import com.firstlinesoftware.base.shared.dto.Directory;
import com.firstlinesoftware.ecm.client.activities.EditCompositeDocumentBaseActivity;
import com.firstlinesoftware.ecm.client.views.editors.CompositeDocumentEditorImpl;
import com.firstlinesoftware.exec.client.Exec;
import com.firstlinesoftware.exec.client.proxies.ErrandProxy;
import com.firstlinesoftware.orgstruct.shared.dto.Position;
import com.firstlinesoftware.rmrs.client.Rmrs;
import com.firstlinesoftware.rmrs.client.messages.RmrsMessages;
import com.firstlinesoftware.rmrs.client.places.ApproveProposalErrandReportPlace;
import com.firstlinesoftware.rmrs.client.processes.CreateProposalErrandVisaProcess;
import com.firstlinesoftware.rmrs.shared.dto.ConsiderProposalErrand;
import com.firstlinesoftware.rmrs.shared.dto.ProposalErrandReport;
import com.firstlinesoftware.rmrs.shared.dto.ProposalErrandVisa;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

/**
 * User: VAntonov
 * Date: 09.12.2010
 * Time: 12:58:14
 */
public class ApproveProposalErrandReportActivity extends EditCompositeDocumentBaseActivity<ProposalErrandVisa, CompositeDocumentEditorImpl<ProposalErrandVisa>> {
    private final RmrsMessages messages = Rmrs.getInjector().getMessages();
    private final ErrandProxy errandProxy = Exec.getInjector().getErrandProxy();
    private final Desktop desktop = Base.getInjector().getDesktop();

    private String errandId;
    private String positionId;
    private ConsiderProposalErrand errand;

    public ApproveProposalErrandReportActivity(ApproveProposalErrandReportPlace place) {
        errandId = place.errandId;
        positionId = place.positionId;
        defaultKind = ProposalErrandVisa.KIND;
        setShowWith(errandId);
    }

    @Override
    public void start(final AcceptsOneWidget panel, final EventBus eventBus) {
        documentProxy.get(errandId, new ActionCallback<ConsiderProposalErrand>(ecmMessages.errorWhileGettingDocument()) {
            @Override
            public void onActionSuccess(ConsiderProposalErrand errand) {
                assert errand != null && errand.getLastReport() != null;
                ApproveProposalErrandReportActivity.this.errand = errand;
                ApproveProposalErrandReportActivity.super.start(panel, eventBus);
            }
        });
    }

    @Override
    public void save() {
        flush();
        if(errand.getLastReport().accepted && (dto.createNew == null || dto.createNew.isEmpty()) && (dto.modifyExisting == null || dto.modifyExisting.isEmpty())) {
            desktop.getMessagesPanel().showFailure(messages.errorWhileCreatingErrand(), messages.noErrands());
        } else {
            save(new SuccessCallback<Void>() {
                @Override
                public void onSuccess(Void result) {
                    dto.accepted = true;
                    dto.author = new Position();
                    dto.author.id = positionId;
                    dto.kind = ProposalErrandVisa.KIND;
                    errandProxy.acceptReport(errandId, dto, new UserActionCallback<Void>(
                            messages.reportAccepted(),
                            messages.errorWhileAcceptingReport()) {
                        @Override
                        public void onActionSuccess(Void result1) {
                            onResultSuccess(null);
                        }
                    });
                }
            });
        }
    }

    @Override
    public String getTitle() {
        return messages.createAcceptanceReportVisa();
    }

    @Override
    protected void setupFields(ProposalErrandVisa dto) {
        super.setupFields(dto);
        setupAuthorSelector(view.getAuthor(), positionId);
        final ProposalErrandReport report = errand.getLastReport();
        view.getRelatedDocuments().setValue(report.getRelatedDocuments());
        view.getAttachedFiles().setValue(report.getAttachedFiles());
        if(report.accepted) {
            view.setFormItemWidgetValue(messages.createRequirements(), report.getCreateNew());
            view.setFormItemWidgetValue(messages.modifyRequirements(), report.getModifyExisting());
            final DirectoryComboBox kind = view.getFormItemWidget(messages.kind());
            kind.setCriteria(new Criteria<Directory.Item>() {
                @Override
                public boolean isSatisfy(Directory.Item item) {
                    return !item.getValue().contains("considerProposal") && !item.getValue().contains("withReport");
                }
            });
            kind.addValueChangeHandler(new ValueChangeHandler<String>() {
                @Override
                public void onValueChange(ValueChangeEvent<String> valueChangeEvent) {
                    view.getName().setValue(applicationProperties.get(kind.getValue()));
                }
            });
            view.getName().setValue(applicationProperties.get(kind.getValue()));
        } else {
            view.setFormItemWidgetValue(messages.createRequirements(), null);
            view.setFormItemWidgetValue(messages.modifyRequirements(), null);
            view.setFormItemWidgetValue(messages.kind(), null);
            view.getName().setValue(null);
        }

     }

    @Override
    protected void setupVisibility() {
        super.setupVisibility();
        view.setVisible(errand.getLastReport().accepted, messages.createRequirements(), messages.modifyRequirements(), messages.kind());
    }

    @Override
    protected CreateProposalErrandVisaProcess getProcess() {
        return Rmrs.getInjector().getCreateProposalErrandVisaProcess();
    }

    @Override
    protected void setupView() {
        super.setupView();
        view.save.setText(messages.send());
    }
}
