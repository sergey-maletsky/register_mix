package com.firstlinesoftware.rmrs.client.activities;

import com.firstlinesoftware.base.client.SuccessCallback;
import com.firstlinesoftware.base.client.services.UserActionCallback;
import com.firstlinesoftware.base.client.widgets.AutoLayoutTextArea;
import com.firstlinesoftware.base.client.widgets.popups.MenuPopupPresenter;
import com.firstlinesoftware.ecm.client.processes.CreateCompositeDocumentProcess;
import com.firstlinesoftware.ecm.shared.dto.Document;
import com.firstlinesoftware.rmrs.client.Rmrs;
import com.firstlinesoftware.rmrs.client.messages.RmrsMessages;
import com.firstlinesoftware.rmrs.client.places.CreateProposalPlace;
import com.firstlinesoftware.rmrs.client.places.EditProposalPlace;
import com.firstlinesoftware.rmrs.shared.dto.Proposal;
import com.firstlinesoftware.route.client.activities.EditAbstractRouteActivity;
import com.firstlinesoftware.route.client.views.editors.AbstractRouteEditorImpl;
import com.firstlinesoftware.route.shared.dto.AbstractRoute;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class EditProposalActivity extends EditAbstractRouteActivity<Proposal, AbstractRouteEditorImpl<Proposal>> {

    private final RmrsMessages messages = Rmrs.getInjector().getMessages();

    private String relationType;
    private String relatedTo;

    public EditProposalActivity(CreateProposalPlace place) {
        defaultKind = Proposal.KIND;
        relationType = place.relationType;
        relatedTo = place.requirementId;
    }

    public EditProposalActivity(EditProposalPlace place) {
        id = place.id;
        defaultKind = Proposal.KIND;
    }

    @Override
    protected CreateCompositeDocumentProcess<Proposal> getProcess() {
        return Rmrs.getInjector().getEditProposalProcess();
    }

    @Override
    protected AbstractRouteEditorImpl<Proposal> createView() {
        return Rmrs.getInjector().getProposalEditor();
    }

    @Override
    public void save() {
        if(getDto().lifecycle == null || getDto().lifecycle.equals(Document.DOCUMENT_LIFECYCLE_DRAFT)|| getDto().lifecycle.equals(AbstractRoute.LIFECYCLE_REJECTED)) {
            final MenuPopupPresenter presenter = new MenuPopupPresenter(view.save, true);
            presenter.addCommand(messages.saveAsDraft(), new Command() {
                @Override
                public void execute() {
                    flush();
                    saveAsDraft();
                }
            });
            presenter.addCommand(messages.saveAndSend(), new Command() {
                @Override
                public void execute() {
                    saveAndSend();
                }
            });
            presenter.showModal();
        } else {
            save(new SuccessCallback<Void>() {
                @Override
                public void onSuccess(Void result) {
                    abstractRouteProxy.falsificateDocument(dto, new UserActionCallback<Void>(
                            messages.documentSaved(),
                            messages.errorWhileUpdatingDocument()
                    ) {
                        @Override
                        public void onActionSuccess(Void result) {
                            onResultSuccess(null);
                        }
                    });
                }
            }, false);
        }
    }

    @Override
    protected void setupEditor(AcceptsOneWidget panel, Proposal dto) {
        super.setupEditor(panel, dto);
        setupAuthorSelector(view.getAuthor(), null);
        addRelation(dto, relationType, relatedTo, true, null);
    }

    @Override
    protected void gotoNextStep(String id, AbstractRoute dto) {
        onResultSuccess(null);
    }

    @Override
    protected void setupFields(Proposal dto) {
        super.setupFields(dto);
        if (dto.errandText == null || dto.errandText.isEmpty()) {
            ((AutoLayoutTextArea) view.getFormItemWidget(messages.errand())).setText(messages.proposalErrandText());
        }
    }
}
