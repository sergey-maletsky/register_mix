package com.firstlinesoftware.rmrs.client.init;

import com.firstlinesoftware.base.client.Base;
import com.firstlinesoftware.ecm.client.Ecm;
import com.firstlinesoftware.ecm.client.events.FolderItemAddedEvent;
import com.firstlinesoftware.ecm.client.events.FolderItemRemovedEvent;
import com.firstlinesoftware.ecm.client.events.TaskCompletedEvent;
import com.firstlinesoftware.ecm.client.events.TaskCreatedEvent;
import com.firstlinesoftware.ecm.client.factories.NotificationRendererFactory;
import com.firstlinesoftware.ecm.client.init.AbstractTaskFolders;
import com.firstlinesoftware.ecm.client.proxies.TaskProxy;
import com.firstlinesoftware.ecm.shared.dto.Task;
import com.firstlinesoftware.orgstruct.client.Orgstruct;
import com.firstlinesoftware.orgstruct.shared.dto.OrgstructureItemFormatter;
import com.firstlinesoftware.orgstruct.shared.dto.Position;
import com.firstlinesoftware.rmrs.client.Rmrs;
import com.firstlinesoftware.rmrs.client.messages.RmrsMessages;
import com.firstlinesoftware.rmrs.shared.dto.RmrsTasks;
import com.google.gwt.event.shared.EventBus;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.firstlinesoftware.exec.shared.dto.ErrandTasks.*;

public class RmrsTaskFolders extends AbstractTaskFolders implements FolderItemAddedEvent.Handler, FolderItemRemovedEvent.Handler {

    private static final String COMMON_FOLDER_ONGOING = "common_routes_ongoing.";
    public static final String ERRANDS_FOLDER = "errands";
    public static final String NOTIFICATIONS_FOLDER = "notification";

    final OrgstructureItemFormatter positionFormatter = Orgstruct.getInjector().getOrgstructureItemFormatter();
    private final RmrsMessages messages = Rmrs.getInjector().getMessages();
    private final EventBus eventBus = Base.getInjector().getEventBus();
    private final NotificationRendererFactory notificationFactory = Ecm.getInjector().getNotificationFactory();
    private final Set<String> lazyTaskFolders = new HashSet<>(Arrays.asList("routes_approved", "routes_signed"));

    private final List<String> lazy = Arrays.asList(FOLDERS_ARCHIVE
            , RmrsTasks.REQ_DRAFTS
            , RmrsTasks.REQ_APPROVED
            , RmrsTasks.REQ_SENT_TO_APPROVAL
            , RmrsTasks.REQ_ON_SIGNING
            , RmrsTasks.REQ_SIGNED
            , RmrsTasks.PROPOSAL_DRAFTS
            , RmrsTasks.PROPOSAL_ON_EXECUTION
            , RmrsTasks.PROPOSAL_ARCHIVED
            , RmrsTasks.CIRCULAR_LETTER_DRAFTS
            , RmrsTasks.CIRCULAR_LETTER_SIGNED
            , RmrsTasks.CIRCULAR_LETTER_SENT_TO_APPROVAL);

    public RmrsTaskFolders() {
        for (String folder : lazy) {
            TaskProxy.markAsLazy(folder);
        }
        eventBus.addHandler(FolderItemAddedEvent.getType(), this);
        eventBus.addHandler(FolderItemRemovedEvent.getType(), this);
    }

    @Override
    protected void createFolders(Position position) {
        final String positionId = position != null ? position.id : null;
        createMyTasks(position);
        TaskProxy.createFolder(getRoot(), RmrsTasks.REQ_TASK_TYPE, messages.requirements(), false, positionId);
        {
            TaskProxy.createFolder(RmrsTasks.REQ_TASK_TYPE, RmrsTasks.REQ_DRAFTS, messages.drafts(), true, positionId);
            TaskProxy.createFolder(RmrsTasks.REQ_TASK_TYPE, RmrsTasks.REQ_SENT_TO_APPROVAL, messages.sentToApproval(), true, positionId);
            TaskProxy.createFolder(RmrsTasks.REQ_TASK_TYPE, RmrsTasks.REQ_ON_APPROVAL, messages.forApproval(), true, positionId);
            TaskProxy.createFolder(RmrsTasks.REQ_TASK_TYPE, RmrsTasks.REQ_APPROVED, messages.approved(), true, positionId);
            TaskProxy.createFolder(RmrsTasks.REQ_TASK_TYPE, RmrsTasks.REQ_ON_SIGNING, messages.onEditorialBoard(), true, positionId);
            TaskProxy.createFolder(RmrsTasks.REQ_TASK_TYPE, RmrsTasks.REQ_SIGNED, messages.signed(), true, positionId);
            TaskProxy.createFolder(RmrsTasks.REQ_TASK_TYPE, RmrsTasks.REQ_REJECTED, messages.rejected(), true, positionId);
        }
        TaskProxy.createFolder(getRoot(), RmrsTasks.PROPOSAL_TASK_TYPE, messages.proposals(), false, positionId);
        {
            TaskProxy.createFolder(RmrsTasks.PROPOSAL_TASK_TYPE, RmrsTasks.PROPOSAL_DRAFTS, messages.drafts(), true, positionId);
            TaskProxy.createFolder(RmrsTasks.PROPOSAL_TASK_TYPE, RmrsTasks.PROPOSAL_ON_EXECUTION, messages.onHandling(), true, positionId);
            TaskProxy.createFolder(RmrsTasks.PROPOSAL_TASK_TYPE, RmrsTasks.PROPOSAL_ARCHIVED, messages.archived(), true, positionId);
        }
        TaskProxy.createFolder(getRoot(), RmrsTasks.CIRCULAR_LETTER_TASK_TYPE, messages.circularLetters(), false, positionId);
        {
            TaskProxy.createFolder(RmrsTasks.CIRCULAR_LETTER_TASK_TYPE, RmrsTasks.CIRCULAR_LETTER_DRAFTS, messages.drafts(), true, positionId);
            TaskProxy.createFolder(RmrsTasks.CIRCULAR_LETTER_TASK_TYPE, RmrsTasks.CIRCULAR_LETTER_SENT_TO_APPROVAL, messages.sentToApproval(), true, positionId);
            TaskProxy.createFolder(RmrsTasks.CIRCULAR_LETTER_TASK_TYPE, RmrsTasks.CIRCULAR_LETTER_ON_APPROVAL, messages.forApproval(), true, positionId);
            TaskProxy.createFolder(RmrsTasks.CIRCULAR_LETTER_TASK_TYPE, RmrsTasks.CIRCULAR_LETTER_ON_SIGNING, messages.forSignature(), true, positionId);
            TaskProxy.createFolder(RmrsTasks.CIRCULAR_LETTER_TASK_TYPE, RmrsTasks.CIRCULAR_LETTER_SIGNED, messages.signed(), true, positionId);
            TaskProxy.createFolder(RmrsTasks.CIRCULAR_LETTER_TASK_TYPE, RmrsTasks.CIRCULAR_LETTER_REJECTED, messages.rejected(), true, positionId);
        }
        TaskProxy.createFolder(getRoot(), ERRANDS_FOLDER, messages.errands(), false, positionId);
        {
            TaskProxy.createFolder(ERRANDS_FOLDER, RmrsTasks.ERRAND_TASK_REJECTED, messages.errandsRejected(), true, positionId);
            TaskProxy.createFolder(ERRANDS_FOLDER, FOLDERS_ASSIGNED, messages.errandsAssigned(), true, positionId);
            TaskProxy.createFolder(ERRANDS_FOLDER, FOLDERS_REVIEW, messages.errandsReviewReport(), true, positionId);
            TaskProxy.createFolder(ERRANDS_FOLDER, FOLDERS_CHANGE_REQUESTED, messages.errandsDateChangeRequest(), true, positionId);
            TaskProxy.createFolder(ERRANDS_FOLDER, FOLDERS_ISSUED, messages.errandsIssued(), true, positionId);
            TaskProxy.createFolder(ERRANDS_FOLDER, FOLDERS_ON_MY_CONTROL, messages.errandsOnMyControl(), true, positionId);
            TaskProxy.createFolder(ERRANDS_FOLDER, FOLDERS_ARCHIVE, messages.errandsArchive(), false, positionId);
            {
                TaskProxy.createFolder(FOLDERS_ARCHIVE, FOLDERS_ACCOMPLISHED, messages.errandsArchiveAssigned(), false, positionId);
                {
                    TaskProxy.createFolder(FOLDERS_ACCOMPLISHED, FOLDERS_ACCOMPLISHED + LAST_MONTHS, messages.errandsArchivedLastMonths(), true, positionId);
                    TaskProxy.createFolder(FOLDERS_ACCOMPLISHED, FOLDERS_ACCOMPLISHED + LAST_YEARS, messages.errandsArchivedLastYears(), true, positionId);
                }
                TaskProxy.createFolder(FOLDERS_ARCHIVE, FOLDERS_COMPLETED, messages.errandsArchiveIssued(), false, positionId);
                {
                    TaskProxy.createFolder(FOLDERS_COMPLETED, FOLDERS_COMPLETED + LAST_MONTHS, messages.errandsArchivedLastMonths(), true, positionId);
                    TaskProxy.createFolder(FOLDERS_COMPLETED, FOLDERS_COMPLETED + LAST_YEARS, messages.errandsArchivedLastYears(), true, positionId);
                }
                TaskProxy.createFolder(FOLDERS_ARCHIVE, FOLDERS_CANCELLED, messages.errandsArchiveCancelled(), false, positionId);
                {
                    TaskProxy.createFolder(FOLDERS_CANCELLED, FOLDERS_CANCELLED + LAST_MONTHS, messages.errandsArchivedLastMonths(), true, positionId);
                    TaskProxy.createFolder(FOLDERS_CANCELLED, FOLDERS_CANCELLED + LAST_YEARS, messages.errandsArchivedLastYears(), true, positionId);
                }
                TaskProxy.createFolder(FOLDERS_ARCHIVE, FOLDERS_DELEGATED, messages.errandsArchiveDelegated(), false, positionId);
                {
                    TaskProxy.createFolder(FOLDERS_DELEGATED, FOLDERS_DELEGATED + LAST_MONTHS, messages.errandsArchivedLastMonths(), true, positionId);
                    TaskProxy.createFolder(FOLDERS_DELEGATED, FOLDERS_DELEGATED + LAST_YEARS, messages.errandsArchivedLastYears(), true, positionId);
                }
            }
        }
        TaskProxy.createFolder(getRoot(), NOTIFICATIONS_FOLDER, messages.notifications(), false, positionId);
        {
            for (String n : notificationFactory.getNames()) {
                TaskProxy.createFolder(NOTIFICATIONS_FOLDER, n, notificationFactory.get(n.toLowerCase()), true, positionId);
            }
        }
    }


    @Override
    public void onFolderItemAdded(FolderItemAddedEvent event) {
        if (isDraft(event.folderId) || lazy.contains(event.folderId) || lazyTaskFolders.contains(getFolderId(event.folderId))) {
            Task task = new Task();
            task.id = event.documentId;
            task.folderId = event.folderId;
            eventBus.fireEvent(new TaskCreatedEvent(task));
        }
    }

    @Override
    public void onFolderItemRemoved(FolderItemRemovedEvent event) {
        if (isDraft(event.folderId) || lazy.contains(event.folderId) || lazyTaskFolders.contains(getFolderId(event.folderId))) {
            eventBus.fireEvent(new TaskCompletedEvent());
        }
    }

    private boolean isDraft(String folderId) {
        return Arrays.asList("draft", "requirements").contains(folderId);
    }

    public static String getFolderId(String event) {
        return event != null && event.startsWith(COMMON_FOLDER_ONGOING) ? event.substring(COMMON_FOLDER_ONGOING.length()) : event;
    }

    protected void createMyTasks(Position position) {
        if (hasSubstitutions) {
            final String positionId = position != null ? position.id : null;
            final String name = positionFormatter.format(position);
            TaskProxy.createFolder("/", "my_tasks", position != null ? name : messages.myTasks(), false, positionId);
        }
    }


}
