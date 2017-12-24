package com.firstlinesoftware.rmrs.client.commands;

import com.firstlinesoftware.base.client.services.ActionCallback;
import com.firstlinesoftware.ecm.shared.dto.Document;
import com.firstlinesoftware.ecm.shared.dto.RelatedDocument;
import com.firstlinesoftware.exec.client.commands.ErrandCommand;
import com.firstlinesoftware.exec.shared.dto.AbstractErrand;
import com.firstlinesoftware.orgstruct.shared.dto.Position;
import com.firstlinesoftware.rmrs.client.Rmrs;
import com.firstlinesoftware.rmrs.client.messages.RmrsMessages;
import com.firstlinesoftware.rmrs.client.places.SendRequirementsToApprovalPlace;
import com.firstlinesoftware.rmrs.shared.dto.ChangeRequirementErrand;
import com.firstlinesoftware.rmrs.shared.dto.Requirement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Widget;

import java.util.List;

public class SendErrandRequirementsToApprovalCommand extends ErrandCommand {
    protected static final String ICON_PROCESS = "images/buttons/32/send_to_audit.png";

    private final RmrsMessages messages = Rmrs.getInjector().getMessages();

    @Override
    public Widget getInstance(final AbstractErrand dto) {
        if (dto instanceof ChangeRequirementErrand && orgstructureProxy.isMyPosition(dto.executor)) {
            assert dto.relatedDocuments != null;
            for (RelatedDocument r : dto.relatedDocuments) {
                if (r.document instanceof Requirement && Document.DOCUMENT_LIFECYCLE_DRAFT.equals(r.document.lifecycle)) {
                    return createButton(ICON_PROCESS, messages.sendToApproval(), new ClickHandler() {
                        @Override
                        public void onClick(ClickEvent event) {
                            final List<Position> my = orgstructureProxy.getUserProfile().getPositions();
                            assert my != null && !my.isEmpty();
                            if (my.get(0).rank == null || my.get(0).rank < Position.RANK_BOSS) {
                                orgstructureProxy.getPositionsByRunk(Position.RANK_BOSS, my.get(0).department.id, new ActionCallback<List<Position>>(null) {
                                    @Override
                                    public void onActionSuccess(List<Position> boss) {
                                        if (boss != null && !boss.isEmpty()) {
                                            placeController.goTo(new SendRequirementsToApprovalPlace(dto.id, boss.get(0).id, messages.approvalByBoss(), null));

                                        } else {
                                            placeController.goTo(new SendRequirementsToApprovalPlace(dto.id));
                                        }
                                    }
                                });
                            } else {
                                placeController.goTo(new SendRequirementsToApprovalPlace(dto.id));
                            }
                        }
                    });

                }
            }
        }
        return null;
    }
}
