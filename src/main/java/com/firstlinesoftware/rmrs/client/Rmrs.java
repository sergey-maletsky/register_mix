package com.firstlinesoftware.rmrs.client;

import com.firstlinesoftware.base.client.Base;
import com.firstlinesoftware.base.client.BaseActivityMapper;
import com.firstlinesoftware.base.client.BasePlaceHistoryMapperFactory;
import com.firstlinesoftware.base.client.SuccessCallback;
import com.firstlinesoftware.base.client.desktop.DirectoriesPanel;
import com.firstlinesoftware.base.client.desktop.SearchMenuBar;
import com.firstlinesoftware.base.client.events.*;
import com.firstlinesoftware.base.client.factories.NavigationPanelsFactory;
import com.firstlinesoftware.base.client.init.GwtInitModule;
import com.firstlinesoftware.base.client.init.InitFactory;
import com.firstlinesoftware.base.client.models.NavigatorModel;
import com.firstlinesoftware.base.client.proxies.DirectoryProxy;
import com.firstlinesoftware.base.client.proxies.DownloadProxy;
import com.firstlinesoftware.base.client.services.LongRunningActionCallback;
import com.firstlinesoftware.base.client.services.UserActionCallback;
import com.firstlinesoftware.base.client.utils.PopupButtonContentBuilder;
import com.firstlinesoftware.base.client.widgets.*;
import com.firstlinesoftware.base.client.widgets.popups.*;
import com.firstlinesoftware.base.shared.dto.*;
import com.firstlinesoftware.ecm.client.DefaultMain;
import com.firstlinesoftware.ecm.client.Ecm;
import com.firstlinesoftware.ecm.client.desktop.DocumentMenuBar;
import com.firstlinesoftware.ecm.client.desktop.TasksPanel;
import com.firstlinesoftware.ecm.client.models.TaskFolderModel;
import com.firstlinesoftware.ecm.client.places.BrowseDashboardPlace;
import com.firstlinesoftware.ecm.client.places.SearchDocumentsPlace;
import com.firstlinesoftware.ecm.client.views.DashboardView;
import com.firstlinesoftware.ecm.shared.directories.EcmDirectories;
import com.firstlinesoftware.ecm.shared.dto.SearchFieldsData;
import com.firstlinesoftware.ecm.shared.dto.Task;
import com.firstlinesoftware.ecm.shared.dto.TaskFolder;
import com.firstlinesoftware.exec.client.ExecPlaceHistoryMapper;
import com.firstlinesoftware.exec.shared.dto.AbstractErrand;
import com.firstlinesoftware.orgstruct.client.Orgstruct;
import com.firstlinesoftware.orgstruct.client.proxies.OrgstructureProxy;
import com.firstlinesoftware.orgstruct.shared.directories.OrgstructDirectories;
import com.firstlinesoftware.orgstruct.shared.dto.RoleSearchField;
import com.firstlinesoftware.rmrs.client.desktop.ExchangeMenuBar;
import com.firstlinesoftware.rmrs.client.desktop.RequirementsPanel;
import com.firstlinesoftware.rmrs.client.init.RmrsTaskFolders;
import com.firstlinesoftware.rmrs.client.messages.RmrsMessages;
import com.firstlinesoftware.rmrs.client.places.*;
import com.firstlinesoftware.rmrs.client.proxies.RequirementProxy;
import com.firstlinesoftware.rmrs.client.widgets.HeaderMenu;
import com.firstlinesoftware.rmrs.client.widgets.RequirementsExportPanel;
import com.firstlinesoftware.rmrs.client.widgets.RequirementsFilterPanel;
import com.firstlinesoftware.rmrs.client.widgets.StackPanelHeaderWithButtons;
import com.firstlinesoftware.rmrs.shared.dto.CircularLetter;
import com.firstlinesoftware.rmrs.shared.dto.Proposal;
import com.firstlinesoftware.rmrs.shared.dto.Requirement;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.MenuItemSeparator;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Rmrs extends DefaultMain implements GwtInitModule {
    private static final String TAB_ERRANDS = "errands";
    private static final String TAB_DOCUMENTS = "documents";

    private static RmrsGinjector injector = null;

    public void onModuleLoad() {
        InitFactory.register(this);
        super.onModuleLoad();
    }

    public static RmrsGinjector getInjector() {
        if (injector == null) {
            injector = GWT.create(RmrsGinjector.class);
        }
        return injector;
    }

    @Override
    public void initModule() {
        BaseActivityMapper.registerMapper(new RmrsActivityMapper());
        BasePlaceHistoryMapperFactory.registerMapper(GWT.<ExecPlaceHistoryMapper>create(RmrsPlaceHistoryMapper.class));
        new RmrsRegistrable().register();
        MessagesPanel.setMessagesHeight(128);
    }

    @Override
    public void initModuleUser() {
        createDocumentMenu();
        createSearchMenu();
        createExchangeMenu();
        registerPanels();
//        registerDashboardTabs();
    }

    @Override
    protected void createDesktop() {
        Ecm.createDefaultDesktop(new BrowseDashboardPlace(), new HeaderMenu());
    }

    @Override
    protected void initApplicationUser() {
        new RmrsTaskFolders().register();
        InitFactory.initUser();
    }

    private void createExchangeMenu() {
        final OrgstructureProxy orgstructureProxy = Orgstruct.getInjector().getOrgstructureProxy();
        if (orgstructureProxy.hasRole(null, PositionRoles.ROLE_ADMINISTRATOR)) {
            final RmrsMessages messages = Rmrs.getInjector().getMessages();
            final ExchangeMenuBar menuBar = Rmrs.getInjector().getExchangeMenuBar();
            menuBar.addItem(new HorizontalIcon(32, "images/icons/32/xmldoc-blue.png", messages.importRequirements()).getHTML(), true, new Scheduler.ScheduledCommand() {
                @Override
                public void execute() {
                    importRequirements();
                }

            });
            menuBar.addItem(new HorizontalIcon(32, "images/buttons/32/send_to_audit.png", messages.exportRequirements()).getHTML(), true, new Scheduler.ScheduledCommand() {
                @Override
                public void execute() {
                    exportRequirements();
                }

            });
            menuBar.addSeparator(new MenuItemSeparator());
            menuBar.addItem(getInjector().getMessages().startOrgstructSync(), new Scheduler.ScheduledCommand() {
                @Override
                public void execute() {
                    getInjector().getOrgstructSyncProxy().startSyncOrgstruct(new UserActionCallback<String>(messages.syncFinished(),
                            messages.syncFailed()) {
                    });
                }
            });
        }
    }

    private void exportRequirements() {
                final RmrsMessages messages = Rmrs.getInjector().getMessages();
        final RequirementsExportPanel panel = new RequirementsExportPanel();
        final EditorPopupPresenter<Pair<String, Date>, RequirementsExportPanel> presenter = new EditorPopupPresenter<>(panel, null, BasePopupPresenter.DO_NOT_ADD_CLICK_HANDLER);
        presenter.addPrePopupHandler(new PrePopupHandler() {
            @Override
            public void onPrePopup(PrePopupEvent event) {
                panel.setValue(null);
            }
        });
        presenter.addSavePopupHandler(new SavePopupHandler<Pair<String, Date>>() {
            @Override
            public void onSave(SavePopupEvent<Pair<String, Date>> event) {
                Rmrs.getInjector().getRequirementProxy().export(event.getResult().getFirst(), event.getResult().getSecond(), new UserActionCallback<String>(null, messages.errorWhileCreatingDocument()) {
                    @Override
                    public void onActionSuccess(String result) {
                        final CometEventService remoteEventService = Base.getInjector().getCometEventService();
                        remoteEventService.setIgnoreConnectionBreak(true);
                        new Timer() {
                            public void run() {
                                remoteEventService.setIgnoreConnectionBreak(false);
                            }
                        }.schedule(3 * 1000);
                        Window.Location.assign(DownloadProxy.getDocumentURL(result));
                    }
                });
            }
        });
        presenter.showModal();
    }

    private void importRequirements() {
        final RequirementProxy requirementProxy = Rmrs.getInjector().getRequirementProxy();
        final RmrsMessages messages = Rmrs.getInjector().getMessages();
        final AttachmentSelector attachmentSelector = new AttachmentSelector();
        final AttachmentsUploader.UploadListener listener = new AttachmentsUploader.UploadListener() {
            @Override
            public void onFileAdded(List<AttachedFile> files) {
                if (files.size() > 0) {
                    attachmentSelector.setValue(files.get(0));
                }
            }

            @Override
            public void onFilesUploaded(Map<String, String> newIds) {
                attachmentSelector.clear();
                if (!newIds.isEmpty()) {
                    assert newIds.size() == 1;
                    requirementProxy.startImport(newIds.values().iterator().next(), new LongRunningActionCallback<Void>(messages.importRequirements(), messages.errorWhileImportingRequirements()) {
                        @Override
                        protected String getSuccessMessage(Void result) {
                            return messages.requirementsImported();
                        }

                        @Override
                        protected void onSuccessMessageClicked() {
                            Base.getInjector().getPlaceController().goTo(new BrowseRequirementsPlace());
                        }
                    });
                }
            }
        };
        final SelectorPopupPanel<AttachedFile> panel = new SelectorPopupPanel<AttachedFile>(messages.setOfRequirements(), attachmentSelector) {
            @Override
            protected void appendContent(PopupButtonContentBuilder builder) {
                super.appendContent(builder);
                attachmentSelector.addListener(listener);
                attachmentSelector.setZIndex(2);
            }
        };
        final EditorPopupPresenter<AttachedFile, SelectorPopupPanel<AttachedFile>> presenter = new EditorPopupPresenter<>(panel, null, BasePopupPresenter.DO_NOT_ADD_CLICK_HANDLER);
        presenter.addPrePopupHandler(new PrePopupHandler() {
            @Override
            public void onPrePopup(PrePopupEvent event) {
                panel.setValue(null, false);
            }
        });
        presenter.addSavePopupHandler(new SavePopupHandler<AttachedFile>() {
            @Override
            public void onSave(SavePopupEvent<AttachedFile> event) {
                attachmentSelector.start();
            }
        });
        presenter.showModal();
    }

    private static void createDocumentMenu() {
        final DocumentMenuBar create = Ecm.getInjector().getDocumentMenuBar();
        final DirectoryProxy directoryProxy = Base.getInjector().getDirectoryProxy();
        final PlaceController placeController = Base.getInjector().getPlaceController();
        directoryProxy.loadDirectories(new SuccessCallback<Directories>() {
            @Override
            public void onSuccess(Directories result) {
                final Directory.Item req = directoryProxy.getByValue(EcmDirectories.DOCUMENT_TYPES.getType(), Requirement.KIND);
                final Directory.Item proposal = directoryProxy.getByValue(EcmDirectories.DOCUMENT_TYPES.getType(), Proposal.KIND);
                final Directory.Item circular = directoryProxy.getByValue(EcmDirectories.DOCUMENT_TYPES.getType(), CircularLetter.KIND);
                if (req != null && isAuthor()) {
                    create.addItem(new HorizontalIcon(32, "images/icons/32/doc.png", req.getName()).getHTML(), true, new Command() {
                        @Override
                        public void execute() {
                            final Place where = placeController.getWhere();
                            placeController.goTo(where instanceof BrowseRequirementsPlace
                                    ? new CreateRequirementPlace(((BrowseRequirementsPlace) where).getId())
                                    : new CreateRequirementPlace(false));

                        }
                    });
                }
                if (req != null && isAuthor()) {
                    create.addItem(new HorizontalIcon(32, "images/icons/32/doc.png", injector.getMessages().createSection()).getHTML(), true, new Command() {
                        @Override
                        public void execute() {
                            placeController.goTo(new CreateRequirementPlace(true));
                        }
                    });
                }
                if (proposal != null && (isInspector() || isAuthor())) {
                    create.addItem(new HorizontalIcon(32, "images/icons/32/docIn.png", proposal.getName()).getHTML(), true, new CreateProposalPlace());
                }
                if (circular != null && isAuthor()) {
                    create.addItem(new HorizontalIcon(32, "images/icons/32/docOut.png", circular.getName()).getHTML(), true, new CreateCircularLetterPlace());
                }
            }
        });
    }

    private static boolean isAuthor() {
        final OrgstructureProxy proxy = Orgstruct.getInjector().getOrgstructureProxy();
        return proxy.hasRole(null, "author") || proxy.hasRole(null, "advanced");
    }

    private static boolean isInspector() {
        final OrgstructureProxy proxy = Orgstruct.getInjector().getOrgstructureProxy();
        return proxy.hasRole(null, "inspector");
    }

    private void createSearchMenu() {
        final SearchMenuBar searchMenuBar = Base.getInjector().getSearchMenuBar();
        final DirectoryProxy directoryProxy = Base.getInjector().getDirectoryProxy();
        searchMenuBar.addItem(getInjector().getMessages().requirements(), new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                Base.getInjector().getPlaceController().goTo(new SearchRequirementsPlace());
            }
        });
        searchMenuBar.addItem(getInjector().getMessages().searchByExecutor(), new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                final SearchFieldsData fieldsData = new SearchFieldsData();
                fieldsData.mandatory.add(new RoleSearchField(directoryProxy.getByValue(OrgstructDirectories.ORGSTRUCT_ROLES_SEARCH.getType(), "exec:executor")));
                fieldsData.mandatory.add(new DirectoryItemSearchField(directoryProxy.getByValue(EcmDirectories.LIFECYCLE.getType(), AbstractErrand.LIFECYCLE_ONEXECUTION)));
                Base.getInjector().getPlaceController().goTo(new SearchDocumentsPlace(fieldsData));
            }
        });
    }

    private static void registerPanels() {
        final NavigationPanelsFactory navigationPanelsFactory = Base.getInjector().getNavigationPanelsFactory();
        final RmrsMessages messages = Rmrs.getInjector().getMessages();

        navigationPanelsFactory.unregister(messages.incoming());
        navigationPanelsFactory.unregister(messages.documents());
        navigationPanelsFactory.unregister(messages.dictionaries());
        navigationPanelsFactory.register(new TasksPanel(), messages.personalCabinet());

        final RequirementsPanel panel = new RequirementsPanel();
        final ImageButton refreshButton = new ImageButton("images/icons/16/goto-root.png",
                messages.gotoRoot(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                BrowseRequirementsPlace place = new BrowseRequirementsPlace(NavigatorModel.ROOT.id);
                Base.getInjector().getPlaceController().goTo(place);
            }
        });
        final ImageButton filter = new ImageButton("images/icons/16/not_only_effective.png", messages.effectiveDates());
        filter.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                final RequirementsFilterPanel panel = new RequirementsFilterPanel();
                final EditorPopupPresenter<Pair<Boolean, Date>, RequirementsFilterPanel> presenter = new EditorPopupPresenter<>(panel, filter, true);
                presenter.addPrePopupHandler(new PrePopupHandler() {
                    @Override
                    public void onPrePopup(PrePopupEvent event) {
                        panel.setValue(new Pair<Boolean, Date>(null, new Date()));
                    }
                });
                presenter.addSavePopupHandler(new SavePopupHandler<Pair<Boolean, Date>>() {
                    @Override
                    public void onSave(SavePopupEvent<Pair<Boolean, Date>> event) {
                        final Date date = event.getResult().getSecond();
                        final Boolean onlySigned = event.getResult().getFirst();
                        RequirementsPanel.model.setOnlyEffective(date, onlySigned);
                        filter.setUrl(date != null || Boolean.TRUE.equals(onlySigned) ? "images/icons/16/effective.png" : "images/icons/16/not_only_effective.png");
                    }
                });
                presenter.showModal();
            }
        });
        StackPanelHeaderWithButtons stackPanel = new StackPanelHeaderWithButtons(messages.setOfRequirements(), new ImageButton[]{refreshButton, filter});
        navigationPanelsFactory.register(panel, messages.setOfRequirements(), stackPanel);

        navigationPanelsFactory.register(new DirectoriesPanel(), messages.dictionaries());
    }

    private static void registerDashboardTabs() {
        Ecm.registerDashboardTabs(new Ecm.RegisterDashboard() {
            @Override
            public void register(DashboardView view, String positionId) {
                final RmrsMessages messages = getInjector().getMessages();
                final TreeTableViewer<Task> errands = createTree("incoming_errands", positionId);
                view.addTab(messages.errands(), errands, TAB_ERRANDS);

                try {
                    final TreeTableViewer<Task> documents = createTree("incoming_routes", positionId);
                    view.addTab(messages.documents(), documents, TAB_DOCUMENTS);
                } catch (Exception e) {
                    Logger.getLogger("rmrs").log(Level.SEVERE, "routes", e);
                }
            }
        });
    }

    private static TreeTableViewer<Task> createTree(String id, String positionId) {
        final RmrsMessages messages = getInjector().getMessages();
        final TaskFolderModel model = new TaskFolderModel();
        model.setFolder(new TaskFolder(id, positionId, messages.forHandling(), false));

        final TreeTableViewer<Task> result = new TreeTableViewer<>(model);
        result.setClassName("Task");
        result.setDefaultSortingColumn("controlDate");
        result.setRowStyles("Task");
        result.setHasSearchPanel(true);
        result.setSearchPanel(new BrowserFilterPanel<>(model));

        return result;
    }
}