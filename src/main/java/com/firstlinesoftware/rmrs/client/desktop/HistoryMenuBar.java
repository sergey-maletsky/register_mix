package com.firstlinesoftware.rmrs.client.desktop;

import com.firstlinesoftware.base.client.Base;
import com.firstlinesoftware.base.client.desktop.ChangeTitleListener;
import com.firstlinesoftware.base.client.services.ActionCallback;
import com.firstlinesoftware.base.client.utils.StringUtils;
import com.firstlinesoftware.base.client.widgets.MultiMenuBar;
import com.firstlinesoftware.ecm.client.Ecm;
import com.firstlinesoftware.ecm.client.messages.EcmMessages;
import com.firstlinesoftware.ecm.client.proxies.DocumentProxy;
import com.firstlinesoftware.ecm.shared.dto.Document;
import com.firstlinesoftware.rmrs.client.Rmrs;
import com.firstlinesoftware.rmrs.client.messages.RmrsMessages;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.MenuItem;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashMap;

public class HistoryMenuBar extends MultiMenuBar implements ChangeTitleListener {

    private static final int MAX_HISTORY_MENU_SIZE = 10;
    private MenuItemPair previous;
    private boolean stepBackward;
    private HashMap<String, String> titleMap = new HashMap<>();
    private ArrayList<MenuItemPair> menuList = new ArrayList<>();
    private RmrsMessages rmrsMessages = Rmrs.getInjector().getMessages();
    private final EcmMessages ecmMessages = Ecm.getInjector().getMessages();
    private DocumentProxy documentProxy = Ecm.getInjector().getDocumentProxy();

    public HistoryMenuBar() {
        super(true);
        Base.getInjector().getDesktop().setChangeTitleListener(this);
        initTitleMap();
    }

    @Override
    public void titleChange(final String title) {

        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                HistoryMenuBar.this.execute(title);
            }
        });
    }

    private void execute(final String title) {

        if(title == null) {
            return;
        }

        final String[] urlAndPage = Window.Location.getHref().split("#");
        final String urlInfoPart = urlAndPage.length > 1 ?  urlAndPage[1] : "";

        if(!StringUtils.STR_EMPTY.equals(title) && !StringUtils.STR_EMPTY.equals(urlInfoPart)) {

            if(itIsBackwardStep()) {
                goBack();
                stepBackward = true;
            }

            for(MenuItemPair menuItemPair: menuList) {
                if(menuItemPair.itemUrl.equals(Window.Location.getHref())) {
                    return;
                }
            }

            if(urlInfoPart.contains("documentId") || urlInfoPart.contains("taskId")) {

                String docId = null;
                if(urlInfoPart.contains("documentId")) {
                    docId = urlInfoPart.split("documentId=")[1];
                } else if(urlInfoPart.contains("taskId")) {
                    docId = urlInfoPart.split("taskId=")[1];
                }

                documentProxy.get(docId, new ActionCallback<Document>(ecmMessages.errorWhileGettingDocument()) {
                    @Override
                    public void onActionSuccess(Document result) {
                        String comboTitle = getPrefix(urlInfoPart)
                                + title
                                + StringUtils.STR_DOT
                                + result.getTitle();

                        addMenuItem(comboTitle, Window.Location.getHref());
                    }
                });
            } else {
                String comboTitle = getPrefix(urlInfoPart) + title;
                addMenuItem(comboTitle, Window.Location.getHref());
            }
        }
    }

    private void addMenuItem(String title, final String url) {

        MenuItem menuItem = new MenuItem(StringUtils.ellipsize(title, 150), new Command() {
            @Override
            public void execute() {
                Window.Location.replace(url);
            }
        });

        if(previous != null && !stepBackward) {
            if (getItems().size() > 0) {
                insertItem(previous.menuItem, 0);
            } else {
                addItem(previous.menuItem);
            }

            menuList.add(new MenuItemPair(previous.menuItem, previous.itemUrl, false));

            if (menuList.size() == MAX_HISTORY_MENU_SIZE + 1) {
                removeItem(menuList.get(0).menuItem);
                menuList.remove(0);
            }
        }

        previous = new MenuItemPair(menuItem, Window.Location.getHref(), false);
        stepBackward = false;
    }

    private void goBack() {
        MenuItemPair pairToRemove = menuList.get(menuList.size() - 1);
        removeItem(pairToRemove.menuItem);
        menuList.remove(pairToRemove);
    }

    private void initTitleMap() {

        titleMap.put("requirements", rmrsMessages.setOfRequirements());
        titleMap.put("edit-requirement", rmrsMessages.requiremntEdit());

        titleMap.put("edit-directory-schema", rmrsMessages.createDirectory());
        titleMap.put("create-errand", rmrsMessages.createErrand());
        titleMap.put("create-requirement", rmrsMessages.createRequirement());
        titleMap.put("create-proposal", rmrsMessages.createProposal());
        titleMap.put("create-circular", rmrsMessages.createCircularLetter());

        titleMap.put("requirements.drafts", rmrsMessages.requirements());
        titleMap.put("requirements.approved", rmrsMessages.requirements());
        titleMap.put("requirements.sent.to.approval", rmrsMessages.requirements());
        titleMap.put("requirements.signed", rmrsMessages.requirements());
        titleMap.put("requirements.approval", rmrsMessages.requirements());

        titleMap.put("proposal.drafts", rmrsMessages.proposals());
        titleMap.put("proposal.execution", rmrsMessages.proposals());
        titleMap.put("proposal.archived", rmrsMessages.proposals());

        titleMap.put("circular.drafts", rmrsMessages.circularLetter());
        titleMap.put("circular.sent.to.approval", rmrsMessages.circularLetter());
        titleMap.put("circular.approval", rmrsMessages.circularLetter());
        titleMap.put("circular.signed", rmrsMessages.circularLetter());
        titleMap.put("circular.rejected", rmrsMessages.circularLetter());
        titleMap.put("circular.signing", rmrsMessages.circularLetter());

        titleMap.put("errands_assigned", rmrsMessages.errands());
        titleMap.put("errands_issued", rmrsMessages.errands());
        titleMap.put("errands_archive_", rmrsMessages.errands());
        titleMap.put("errands_on_my_control", rmrsMessages.errands());
        titleMap.put("errands_review_report", rmrsMessages.errands());
        titleMap.put("errands_rejected", rmrsMessages.errands());
        titleMap.put("errands_archive_assigned", rmrsMessages.errands());
        titleMap.put("errands_archive_assigned_last_months", rmrsMessages.bbErrandsArchiveAssigned());
        titleMap.put("errands_archive_assigned_last_years", rmrsMessages.bbErrandsArchiveAssigned());
        titleMap.put("errands_archive_issued", rmrsMessages.errands());
        titleMap.put("errands_archive_issued_last_months", rmrsMessages.bbErrandsArchiveIssued());
        titleMap.put("errands_archive_issued_last_years", rmrsMessages.bbErrandsArchiveIssued());
        titleMap.put("errands_archive_cancelled", rmrsMessages.errands());
        titleMap.put("errands_archive_cancelled_last_months", rmrsMessages.bbErrandsArchiveCancelled());
        titleMap.put("errands_archive_cancelled_last_years", rmrsMessages.bbErrandsArchiveCancelled());
        titleMap.put("errands_archive_delegated", rmrsMessages.errands());
        titleMap.put("errands_archive_delegated_last_months", rmrsMessages.bbErrandsArchiveDelegated());
        titleMap.put("errands_archive_delegated_last_years", rmrsMessages.bbErrandsArchiveDelegated());

        titleMap.put("notification", rmrsMessages.notification());
        titleMap.put("notification_route_rejected", rmrsMessages.notification());
        titleMap.put("notification_route_revoked", rmrsMessages.notification());
        titleMap.put("notification_errand_controller_assigned", rmrsMessages.notification());
        titleMap.put("notification_route_approved", rmrsMessages.notification());
        titleMap.put("notification_route_signed", rmrsMessages.notification());
        titleMap.put("notification_errand_deadline_soon", rmrsMessages.notification());
        titleMap.put("notification_errand_deadline", rmrsMessages.notification());

        titleMap.put("search", rmrsMessages.search());
        titleMap.put("search-requirements", rmrsMessages.requirementSearch());

        titleMap.put("view-addressees", rmrsMessages.directories());
        titleMap.put("edit-directory", rmrsMessages.directories());
        titleMap.put("settings", rmrsMessages.directories());
        titleMap.put("edit-orgstructure", rmrsMessages.directories());
        titleMap.put("browse-rules", rmrsMessages.directories());
        titleMap.put("browse-persons", rmrsMessages.directories());
    }

    private String getPrefix(String urlInfoPart) {

        String result;

        String[] urlInfo = urlInfoPart.split(":");

        result = titleMap.get(urlInfo[0]);
        if(result == null && urlInfo.length > 1) {
            result = titleMap.get(urlInfo[1].split("&")[0].split("=")[1]);
        }

        return result != null ? result + StringUtils.STR_DOT : StringUtils.STR_EMPTY;
    }

    private boolean itIsBackwardStep() {
       return menuList.size() > 0 && menuList.get(menuList.size() - 1).itemUrl.equals(Window.Location.getHref());
    }

    private class MenuItemPair {

        MenuItem menuItem;
        String itemUrl;
        String fullTitle;
        boolean needFix;

        private MenuItemPair(@NotNull MenuItem menuItem,@NotNull String itemUrl,@NotNull boolean needFix ) {
            this.menuItem = menuItem;
            this.itemUrl = itemUrl;
            this.needFix = needFix;
            fullTitle = menuItem.getText();
        }
    }
}