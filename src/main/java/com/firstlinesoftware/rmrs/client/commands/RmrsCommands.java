package com.firstlinesoftware.rmrs.client.commands;

import com.firstlinesoftware.base.client.Base;
import com.firstlinesoftware.base.client.commands.BaseCommandsFactory;
import com.firstlinesoftware.base.client.commands.BrowserCommandsFactory;
import com.firstlinesoftware.base.client.factories.Registrable;
import com.firstlinesoftware.ecm.client.Ecm;
import com.firstlinesoftware.ecm.client.activities.BrowseTaskFolderActivity;
import com.firstlinesoftware.ecm.client.commands.DocumentCommandsFactory;
import com.firstlinesoftware.ecm.client.commands.EditNonRouteDocumentCommand;
import com.firstlinesoftware.ecm.shared.dto.Document;
import com.firstlinesoftware.exec.client.commands.AcceptControlDateChangeCommand;
import com.firstlinesoftware.exec.client.commands.DeclineControlDateChangeCommand;
import com.firstlinesoftware.exec.client.commands.RejectErrandReportCommand;
import com.firstlinesoftware.exec.client.commands.RequestControlDateChangeCommand;
import com.firstlinesoftware.exec.shared.dto.AbstractErrand;
import com.firstlinesoftware.rmrs.shared.dto.*;
import com.firstlinesoftware.route.client.commands.ApproveCommand;
import com.firstlinesoftware.route.client.commands.RejectCommand;
import com.firstlinesoftware.route.client.commands.RevokeDocumentCommand;
import com.firstlinesoftware.route.shared.dto.AbstractRoute;

import static com.firstlinesoftware.rmrs.shared.dto.RmrsTasks.ERRAND_CHANGE_REQUIREMENT;

public class RmrsCommands implements Registrable {
    private final BaseCommandsFactory baseCommandsFactory = Base.getInjector().getBaseCommandsFactory();
    private final DocumentCommandsFactory documentCommandsFactory = Ecm.getInjector().getCommandsFactory();
    private final BrowserCommandsFactory browserCommandsFactory = Base.getInjector().getBrowserCommandsFactory();

    public void register() {

        documentCommandsFactory.register("reorder", Requirement.KIND, "*", new ReorderCommand());
        documentCommandsFactory.register("edit", Requirement.KIND, "*", new EditRequirementCommand());
        documentCommandsFactory.register("sendToApproval", Requirement.KIND, new String[]{
                Document.DOCUMENT_LIFECYCLE_DRAFT,
                AbstractRoute.LIFECYCLE_REJECTED
        }, new SendRequirementToApprovalCommand());
        documentCommandsFactory.register("revokeDocument", Requirement.KIND, AbstractRoute.LIFECYCLE_ONAPPROVAL, new RevokeDocumentCommand());
        documentCommandsFactory.register("approve", Requirement.KIND, AbstractRoute.LIFECYCLE_ONAPPROVAL, new ApproveCommand());
        documentCommandsFactory.register("reject", Requirement.KIND, AbstractRoute.LIFECYCLE_ONAPPROVAL, new RejectCommand());
        documentCommandsFactory.register("createProposal", Requirement.KIND, AbstractRoute.LIFECYCLE_APPROVED, new CreateProposalFromRequirementCommand());

        browserCommandsFactory.register("sendToEditorialBoard", BrowseTaskFolderActivity.TASKS_COMMAND_PREFIX + RmrsTasks.REQ_APPROVED, new SendRequirementToEditorialBoardCommand());
        browserCommandsFactory.register("markRequirementAsSignedByEditorialBoard", BrowseTaskFolderActivity.TASKS_COMMAND_PREFIX + RmrsTasks.REQ_ON_SIGNING, new MarkRequirementAsSignedByEditorialBoardCommand());
        browserCommandsFactory.register("markRequirementAsRejectedByEditorialBoardCommand", BrowseTaskFolderActivity.TASKS_COMMAND_PREFIX + RmrsTasks.REQ_ON_SIGNING, new MarkRequirementAsRejectedByEditorialBoardCommand());

        browserCommandsFactory.register("clearSelection", BrowseTaskFolderActivity.TASKS_COMMAND_PREFIX + RmrsTasks.REQ_APPROVED, new ClearSelectionCommand());
        browserCommandsFactory.register("clearSelection", BrowseTaskFolderActivity.TASKS_COMMAND_PREFIX + RmrsTasks.REQ_ON_SIGNING, new ClearSelectionCommand());

        documentCommandsFactory.register("approveProposal", ConsiderProposalErrand.KIND, AbstractErrand.LIFECYCLE_ONEXECUTION, new ApproveProposalCommand());
        documentCommandsFactory.register("rejectProposal", ConsiderProposalErrand.KIND, AbstractErrand.LIFECYCLE_ONEXECUTION, new RejectProposalCommand());
        documentCommandsFactory.register("acceptErrandReport", ConsiderProposalErrand.KIND, AbstractErrand.LIFECYCLE_ONCONTROL, new AcceptProposalErrandReportCommand());
        documentCommandsFactory.register("rejectErrandReport", ConsiderProposalErrand.KIND, AbstractErrand.LIFECYCLE_ONCONTROL, new RejectErrandReportCommand());

        documentCommandsFactory.register("createRequirementFromErrand", ERRAND_CHANGE_REQUIREMENT, AbstractErrand.LIFECYCLE_ONEXECUTION, new CreateRequirementFromErrandCommand());
        documentCommandsFactory.register("updateRequirementFromErrand", ERRAND_CHANGE_REQUIREMENT, AbstractErrand.LIFECYCLE_ONEXECUTION, new UpdateRequirementFromErrandCommand());
        documentCommandsFactory.register("sendRequirementToApproval", ERRAND_CHANGE_REQUIREMENT, AbstractErrand.LIFECYCLE_COMPLETED, new SendErrandRequirementsToApprovalCommand());

        documentCommandsFactory.register("requestControlDateChange", new String[]{
                ConsiderProposalErrand.KIND,
                ChangeRequirementErrand.KIND
        }, AbstractErrand.LIFECYCLE_ONEXECUTION, new RequestControlDateChangeCommand());
        documentCommandsFactory.register("acceptDateChangeRequest", new String[]{
                ConsiderProposalErrand.KIND,
                ChangeRequirementErrand.KIND
        }, AbstractErrand.LIFECYCLE_ONEXECUTION, new AcceptControlDateChangeCommand());
        documentCommandsFactory.register("declineDateChangeRequest", new String[]{
                ConsiderProposalErrand.KIND,
                ChangeRequirementErrand.KIND
        }, AbstractErrand.LIFECYCLE_ONEXECUTION, new DeclineControlDateChangeCommand());

        documentCommandsFactory.register("edit", new String[]{Proposal.KIND, CircularLetter.KIND}, new String[]{
                Document.DOCUMENT_LIFECYCLE_DRAFT,
                AbstractRoute.LIFECYCLE_REJECTED
        }, new EditNonRouteDocumentCommand());

        documentCommandsFactory.register("edit", new String[]{Proposal.KIND, CircularLetter.KIND}, new String[]{
                AbstractRoute.LIFECYCLE_ONAPPROVAL,
                AbstractRoute.LIFECYCLE_ONSIGNING,
                AbstractRoute.LIFECYCLE_ONEXECUTION,
        }, new FalsificateNonRouteDocumentCommand());

        documentCommandsFactory.register("print", CircularLetter.KIND, "*", new PrintCircularLetterCommand());

        documentCommandsFactory.register("addErrand", Proposal.KIND, AbstractRoute.LIFECYCLE_ONEXECUTION, new CreateProposalErrandCommand());
        documentCommandsFactory.register("addChildErrand", ConsiderProposalErrand.KIND, AbstractErrand.LIFECYCLE_ONEXECUTION, new CreateChildProposalErrandCommand());
        //override
        documentCommandsFactory.register("delegateErrand", AbstractErrand.KIND, AbstractErrand.LIFECYCLE_ONEXECUTION, new DelegateErrandCommand());
        baseCommandsFactory.register("editPosition", "orgstructure.position", new EditPositionAndResponsibilityCommand());
    }
}
