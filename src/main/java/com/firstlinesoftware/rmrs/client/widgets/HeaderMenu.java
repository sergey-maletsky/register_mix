package com.firstlinesoftware.rmrs.client.widgets;

import com.firstlinesoftware.base.client.Base;
import com.firstlinesoftware.base.client.desktop.SearchMenuBar;
import com.firstlinesoftware.base.client.places.AdminPlace;
import com.firstlinesoftware.base.client.widgets.HorizontalIcon;
import com.firstlinesoftware.base.shared.dto.PositionRoles;
import com.firstlinesoftware.ecm.client.Ecm;
import com.firstlinesoftware.ecm.client.desktop.DocumentMenuBar;
import com.firstlinesoftware.orgstruct.client.Orgstruct;
import com.firstlinesoftware.orgstruct.client.proxies.OrgstructureProxy;
import com.firstlinesoftware.rmrs.client.Rmrs;
import com.firstlinesoftware.rmrs.client.desktop.HistoryMenuBar;
import com.firstlinesoftware.rmrs.client.messages.RmrsMessages;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.MenuItemSeparator;
import com.google.gwt.user.client.ui.UIObject;

import java.util.ArrayList;
import java.util.List;

public class HeaderMenu extends com.firstlinesoftware.ecm.client.widgets.HeaderMenu{

    private final RmrsMessages messages = Rmrs.getInjector().getMessages();
    private final OrgstructureProxy orgstructureProxy = Orgstruct.getInjector().getOrgstructureProxy();

    private final HistoryMenuBar historyMenuBar = Rmrs.getInjector().getHistoryMenuBar();
    private final DocumentMenuBar documentMenuBar = Ecm.getInjector().getDocumentMenuBar();
    private final MenuBar searchMenuBar = Base.getInjector().getSearchMenuBar();
//    private final MenuBar helpMenuBar = Base.getInjector().getHelpMenuBar();
    private final MenuBar exchange = Rmrs.getInjector().getExchangeMenuBar();

    private List<UIObject> menuItems;

    public List<UIObject> getMenuItems() {
        if (menuItems == null) {
            menuItems = new ArrayList<>();

            menuItems.add(new MenuItem(new HorizontalIcon(24, "images/icons/24/back.png", messages.back()).getHTML(), true, historyMenuBar));
            menuItems.add(new MenuItemSeparator());
            menuItems.add(new MenuItem(new HorizontalIcon(24, "images/icons/24/create.png", messages.create()).getHTML(), true, documentMenuBar));
//            menuItems.add(new MenuItem(new HorizontalIcon(24, "images/icons/24/print.png", messages.reports()).getHTML(), true, reportMenuBar));
            menuItems.add(new SearchMenuBar.SearchMenuItem(new HorizontalIcon(24, "images/icons/24/search.png", messages.search()).getHTML(), true, searchMenuBar));
            final MenuBar helpMenuBar = new MenuBar(true);
            helpMenuBar.addItem(new HorizontalIcon(24, "images/icons/24/reference.png", messages.authorManual()).getHTML(), true, new Command() {
                @Override
                public void execute() {
                    Window.open(GWT.getHostPageBaseURL() + "help/manual_rmrs.pdf", "_blank", "");
                }
            });
            helpMenuBar.addItem(new HorizontalIcon(24, "images/icons/24/reference.png", messages.instructorManual()).getHTML(), true, new Command() {
                @Override
                public void execute() {
                    Window.open(GWT.getHostPageBaseURL() + "help/manual_rmrs_inspector.pdf", "_blank", "");
                }
            });
            helpMenuBar.addItem(new HorizontalIcon(24, "images/icons/24/manual.png", messages.adminManual()).getHTML(), true, new Command() {
                @Override
                public void execute() {
                    Window.open(GWT.getHostPageBaseURL() + "help/admin_rmrs.pdf", "_blank", "");
                }
            });
            menuItems.add(new MenuItem(new HorizontalIcon(24, "images/icons/24/help.png", messages.help()).getHTML(), true, helpMenuBar));
            if (orgstructureProxy.hasRole(null, "Administrator")) {
                menuItems.add(new MenuItem(new HorizontalIcon(24, "images/icons/24/admin.png", messages.admin()).getHTML(), true, new Scheduler.ScheduledCommand() {
                    @Override
                    public void execute() {
                        Base.getInjector().getPlaceController().goTo(new AdminPlace());
                    }
                }));
            }
            if (orgstructureProxy.hasRole(null, PositionRoles.ROLE_ADMINISTRATOR)) {
                 menuItems.add(new MenuItem(new HorizontalIcon(24, "images/icons/24/export.png", messages.exchange()).getHTML(), true, exchange));
            }
        }
        return menuItems;
    }
//
//    private static void createExchangeMenu() {
//        final ExportMenuBar menuBar = getInjector().getExportMenuBar();
//        menuBar.addItem(new HorizontalIcon(32, "images/icons/32/xmldoc.png", messages.exportToXml()).getHTML(), true, new Scheduler.ScheduledCommand() {
//            @Override
//            public void execute() {
//                exporter.runExport();
//            }
//        });
//        menuBar.addItem(new HorizontalIcon(32, "images/icons/32/xmldoc-blue.png", messages.importFromXml()).getHTML(), true, new Scheduler.ScheduledCommand() {
//            @Override
//            public void execute() {
//                importer.runImport();
//            }
//        });
//        if (orgstructureProxy.hasRole(PositionRoles.ROLE_ADMINISTRATOR)) {
//            menuBar.addItem(new HorizontalIcon(32, "images/icons/32/xmldoc-blue.png", messages.importFrom3b2()).getHTML(), true, new Scheduler.ScheduledCommand() {
//                @Override
//                public void execute() {
//                    Base.getInjector().getPlaceController().goTo(new Import3b2Place());
//                }
//            });
//        }
//        if (orgstructureProxy.hasRole(PositionRoles.ROLE_ADMINISTRATOR)) {
//            menuBar.addItem(new HorizontalIcon(32, "images/icons/32/xmldoc-blue.png", messages.savedHeadersStructure()).getHTML(), true, new Scheduler.ScheduledCommand() {
//                @Override
//                public void execute() {
//                    Base.getInjector().getPlaceController().goTo(new SavedHeadersStructurePlace());
//                }
//            });
//        }
//    }
//
}
