package com.firstlinesoftware.rmrs.client.activities;

import com.firstlinesoftware.base.client.SuccessCallback;
import com.firstlinesoftware.base.client.processes.EditDTOProcess;
import com.firstlinesoftware.base.client.services.ActionCallback;
import com.firstlinesoftware.base.client.services.UserActionCallback;
import com.firstlinesoftware.base.client.widgets.popups.MenuPopupPresenter;
import com.firstlinesoftware.ecm.client.activities.EditDocumentBaseActivity;
import com.firstlinesoftware.ecm.client.views.editors.DocumentEditorImpl;
import com.firstlinesoftware.ecm.shared.dto.Document;
import com.firstlinesoftware.orgstruct.client.Orgstruct;
import com.firstlinesoftware.orgstruct.client.proxies.OrgstructureProxy;
import com.firstlinesoftware.orgstruct.shared.dto.Position;
import com.firstlinesoftware.rmrs.client.Rmrs;
import com.firstlinesoftware.rmrs.client.messages.RmrsMessages;
import com.firstlinesoftware.rmrs.client.places.CreateRequirementPlace;
import com.firstlinesoftware.rmrs.client.places.EditRequirementPlace;
import com.firstlinesoftware.rmrs.client.proxies.RequirementProxy;
import com.firstlinesoftware.rmrs.shared.dto.Requirement;
import com.firstlinesoftware.route.client.Route;
import com.firstlinesoftware.route.client.activities.EditAbstractRouteActivity;
import com.firstlinesoftware.route.client.proxies.AbstractRouteProxy;
import com.firstlinesoftware.route.shared.dto.PositionRoles;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import java.util.List;

public class EditRequirementActivity extends EditDocumentBaseActivity<Requirement, DocumentEditorImpl<Requirement>> {

    private final RmrsMessages messages = Rmrs.getInjector().getMessages();
    private final RequirementProxy requirementProxy = Rmrs.getInjector().getRequirementProxy();
    protected final AbstractRouteProxy abstractRouteProxy = Route.getInjector().getAbstractRouteProxy();
    private final OrgstructureProxy orgstructureProxy = Orgstruct.getInjector().getOrgstructureProxy();

    private String parentId;
    private Requirement template;
    private String errandId;
    public boolean rootRequirement;

    public EditRequirementActivity(CreateRequirementPlace place) {
        parentId = place.parentId;
        template = place.template;
        errandId = place.errandId;
        rootRequirement = place.rootRequirement;
        defaultKind = Requirement.KIND;
    }

    public EditRequirementActivity(EditRequirementPlace place) {
        id = place.id;
        errandId = place.errandId;
        defaultKind = Requirement.KIND;
    }

    @Override
    public void save() {
        final MenuPopupPresenter presenter = new MenuPopupPresenter(view.save, true);
        presenter.addCommand(messages.saveAsDraft(), new Command() {
            @Override
            public void execute() {
                saveRequirement(false);
            }
        });
        presenter.addCommand(messages.saveAndSend(), new Command() {
            @Override
            public void execute() {
                saveRequirement(true);
            }
        });
        if (id != null && orgstructureProxy.hasRole(null, PositionRoles.ROLE_FALCIFICATE_DOCUMENT)) {
            presenter.addCommand(messages.save(), new Command() {
                @Override
                public void execute() {
                    flush();
                    save(new SuccessCallback<Void>() {
                        @Override
                        public void onSuccess(Void result) {
                            requirementProxy.falsificate(dto, new UserActionCallback<Void>(
                                    messages.documentSaved(),
                                    messages.errorWhileUpdatingDocument()
                            ) {
                                @Override
                                public void onActionSuccess(Void result) {
                                    onResultSuccess(null);
                                }
                            });
                        }
                    });
                }
            });
        }
        presenter.showModal();
    }

    private void saveRequirement(final boolean sendToRoute) {
        final Document parent = view.getFormItemValue(messages.includedIn());
        view.setRequired(sendToRoute, messages.forApproval());
        save(new SuccessCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                dto.kind = Requirement.KIND;
                if (id == null) {
                    requirementProxy.create(dto, parent != null ? parent.id : "requirements", errandId, template != null ? template.id : null, sendToRoute, createDocumentCallback);
                } else {
                    requirementProxy.update(dto, errandId, sendToRoute, updateDocumentCallback);
                }
            }
        }, sendToRoute);
    }


    @Override
    protected EditDTOProcess<Requirement> getProcess() {
        return Rmrs.getInjector().getEditRequirementProcess();
    }

    @Override
    protected DocumentEditorImpl<Requirement> createView() {
        return Rmrs.getInjector().getRequirementEditor();
    }

    @Override
    protected void setupEditor(AcceptsOneWidget panel, Requirement dto) {
        super.setupEditor(panel, dto);
        setupAuthorSelector(view.getAuthor(), null);
        view.setFormItemWidgetValueIfEmpty(messages.responsible(), view.getAuthor().getValue());
        approveWithBoss(view.getAuthor().getValue(), messages.approvalByBoss());
    }

    @Override
    protected void setupRequiredFields() {
        super.setupRequiredFields();
        final boolean editRoot = dto.id != null && dto.parent == null;
        view.setRequired(!editRoot, messages.russianContent());
        view.setRequired(false/*!rootRequirement && !editRoot*/, messages.includedIn());
    }

    @Override
    protected void setupFields(Requirement dto) {
        super.setupFields(dto);
        if (parentId != null) {
            documentProxy.get(parentId, new ActionCallback<Document>(messages.errorWhileGettingDocument()) {
                @Override
                public void onActionSuccess(Document parent) {
                    view.setFormItemWidgetValueIfEmpty(messages.includedIn(), parent);
                }
            });
        } else if (template != null) {
            view.getName().setValue(template.getName());
            view.setFormItemWidgetValue(messages.number(), template.number);
            view.setFormItemWidgetValue(messages.includedIn(), template.parent);
            view.setFormItemWidgetValue(messages.responsible(), template.responsible);
        }
//        if (errandId != null && !CompositeDocument.containsId(dto.relatedDocuments, errandId)) {
//            if (dto.relatedDocuments == null) {
//                dto.relatedDocuments = new ArrayList<>();
//            }
//            dto.relatedDocuments.add(new RelatedDocument(RmrsDirectories.RELATION_TYPE_BASED_ON, true, errandId));
//        }
        if (rootRequirement) {
            view.setFormItemWidgetValue(messages.header(), Boolean.TRUE);
            view.setFormItemWidgetValue(messages.includedIn(), null);
        }
        view.setEnabled(!rootRequirement, messages.header(), messages.includedIn());
    }


    protected void approveWithBoss(final Position value, final String id) {
        if (value != null && (value.rank == null || value.rank < Position.RANK_BOSS)) {
            orgstructureProxy.getPositionsByRunk(Position.RANK_BOSS, value.department.id, new ActionCallback<List<Position>>(null) {
                @Override
                public void onActionSuccess(List<Position> result) {
                    EditAbstractRouteActivity.addRound(result, id, view);
                }
            });
        }
    }
}
