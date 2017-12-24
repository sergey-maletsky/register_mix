package com.firstlinesoftware.rmrs.client.activities;

import com.firstlinesoftware.base.client.SuccessCallback;
import com.firstlinesoftware.base.client.services.UserActionCallback;
import com.firstlinesoftware.base.client.widgets.popups.MenuPopupPresenter;
import com.firstlinesoftware.ecm.client.activities.EditCompositeDocumentBaseActivity;
import com.firstlinesoftware.ecm.client.processes.CreateCompositeDocumentProcess;
import com.firstlinesoftware.ecm.shared.dto.Document;
import com.firstlinesoftware.orgstruct.shared.dto.Position;
import com.firstlinesoftware.rmrs.client.Rmrs;
import com.firstlinesoftware.rmrs.client.messages.RmrsMessages;
import com.firstlinesoftware.rmrs.client.places.CreateCircularLetterPlace;
import com.firstlinesoftware.rmrs.client.places.EditCircularLetterPlace;
import com.firstlinesoftware.rmrs.shared.dto.CircularLetter;
import com.firstlinesoftware.route.client.activities.EditAbstractRouteActivity;
import com.firstlinesoftware.route.client.views.editors.AbstractRouteEditorImpl;
import com.firstlinesoftware.route.shared.dto.AbstractRoute;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import java.util.Collections;

public class EditCircularLetterActivity extends EditAbstractRouteActivity<CircularLetter, AbstractRouteEditorImpl<CircularLetter>> {

    private final RmrsMessages messages = Rmrs.getInjector().getMessages();

    public EditCircularLetterActivity(CreateCircularLetterPlace place) {
        defaultKind = CircularLetter.KIND;
    }

    public EditCircularLetterActivity(EditCircularLetterPlace place) {
        id = place.id;
        defaultKind = CircularLetter.KIND;
        isNew = false;
    }

    @Override
    protected CreateCompositeDocumentProcess<CircularLetter> getProcess() {
        return Rmrs.getInjector().getEditCircularLetterProcess();
    }

    @Override
    protected AbstractRouteEditorImpl<CircularLetter> createView() {
        return Rmrs.getInjector().getCircularLetterEditor();
    }

    @Override
    public void save() {
        flush();
        if(getDto().lifecycle == null || getDto().lifecycle.equals(Document.DOCUMENT_LIFECYCLE_DRAFT)|| getDto().lifecycle.equals(AbstractRoute.LIFECYCLE_REJECTED)) {
            final MenuPopupPresenter presenter = new MenuPopupPresenter(view.save, true);
            presenter.addCommand(messages.saveAsDraft(), new Command() {
                @Override
                public void execute() {
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
    }

    @Override
    protected void setupEditor(AcceptsOneWidget panel, CircularLetter dto) {
        super.setupEditor(panel, dto);
        setupAuthorSelector(view.getAuthor(), null);
        approveWithBoss(view.getAuthor().getValue(), messages.approvalByBoss());
        view.getFormItem(view.getRelatedDocuments()).setText(messages.linksToOtherCircularLetter());
        if (dto.content == null || dto.content.isEmpty()) {
            view.setFormItemWidgetValue(messages.circularContent(), applicationProperties.get("standardTextInCPField"));
        }
    }

    @Override
    protected void setupRequiredFields() {
        super.setupRequiredFields();
        view.setRequired(true, messages.circularBusinessCaseNumber(), messages.circularApprover());
    }

    @Override
    protected void setupVisibility() {
        super.setupVisibility();
        setupRequiredLifecycles(Collections.<String>emptyList());
    }

    @Override
    public void saveAndSend() {
        if(validate()) {
            saveAttachments(new SuccessCallback<Void>() {
                @Override
                public void onSuccess(Void result) {
                    dto.routeState.round = null;
                    dto.relatedDocuments = EditCompositeDocumentBaseActivity.cutRelatedDocuments(dto);
                    final Position signer = view.getFormItemValue(messages.circularApprover());
                    if(signer != null) {
                        dto.signing = Collections.singletonList(signer);
                    }
                    abstractRouteProxy.sendToRoute(dto, new UserActionCallback<String>(messages.documentSentToRoute(), ecmMessages.errorWhileCreatingDocument()) {
                        @Override
                        public void onActionSuccess(String result) {
                            super.onActionSuccess(result);
                            onResultSuccess(null);
                        }
                    });
                }
            });
        }
    }


}
