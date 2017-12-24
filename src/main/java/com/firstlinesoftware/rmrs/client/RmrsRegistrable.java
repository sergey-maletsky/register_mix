package com.firstlinesoftware.rmrs.client;

import com.firstlinesoftware.base.client.AbstractCompositeRegistrable;
import com.firstlinesoftware.base.client.Base;
import com.firstlinesoftware.base.client.browsers.BrowsersFactory;
import com.firstlinesoftware.base.client.browsers.FolderBrowser;
import com.firstlinesoftware.base.client.columns.RowStyleDefinition;
import com.firstlinesoftware.base.client.columns.RowStyleFactory;
import com.firstlinesoftware.base.client.factories.BrowsePlacesFactory;
import com.firstlinesoftware.base.client.factories.DataProviderDefinition;
import com.firstlinesoftware.base.client.factories.DataProviderFactory;
import com.firstlinesoftware.base.client.factories.Registrable;
import com.firstlinesoftware.base.client.models.ValueDataProvider;
import com.firstlinesoftware.base.client.services.ActionCallback;
import com.firstlinesoftware.base.shared.dto.Folder;
import com.firstlinesoftware.ecm.client.Ecm;
import com.firstlinesoftware.ecm.client.EcmRegistrable;
import com.firstlinesoftware.ecm.client.activities.ViewDocumentActivity;
import com.firstlinesoftware.ecm.client.columns.DocumentColumns;
import com.firstlinesoftware.ecm.client.factories.EditDocumentPlaceFactory;
import com.firstlinesoftware.ecm.client.factories.TitleLabelFactory;
import com.firstlinesoftware.ecm.client.places.BrowseTaskFolderPlace;
import com.firstlinesoftware.ecm.client.proxies.TaskProxy;
import com.firstlinesoftware.ecm.client.views.viewers.DocumentViewerFactory;
import com.firstlinesoftware.ecm.client.views.viewers.TaskViewersFactory;
import com.firstlinesoftware.ecm.shared.dto.Document;
import com.firstlinesoftware.ecm.shared.dto.Task;
import com.firstlinesoftware.exec.shared.dto.Visa;
import com.firstlinesoftware.ord.shared.dto.Errand;
import com.firstlinesoftware.orgstruct.client.Orgstruct;
import com.firstlinesoftware.orgstruct.client.proxies.OrgstructureProxy;
import com.firstlinesoftware.rmrs.client.activities.ViewCircularLetterActivity;
import com.firstlinesoftware.rmrs.client.activities.ViewProposalActivity;
import com.firstlinesoftware.rmrs.client.activities.ViewRequirementActivity;
import com.firstlinesoftware.rmrs.client.browsers.FilteredFolderBrowser;
import com.firstlinesoftware.rmrs.client.columns.RequirementColumns;
import com.firstlinesoftware.rmrs.client.columns.RmrsHovers;
import com.firstlinesoftware.rmrs.client.commands.RmrsCommands;
import com.firstlinesoftware.rmrs.client.fields.*;
import com.firstlinesoftware.rmrs.client.messages.RmrsMessages;
import com.firstlinesoftware.rmrs.client.models.RequirementsDataProvider;
import com.firstlinesoftware.rmrs.client.places.*;
import com.firstlinesoftware.rmrs.shared.dto.*;
import com.firstlinesoftware.route.shared.dto.AbstractRoute;
import com.google.gwt.activity.shared.Activity;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.cellview_imported.client.RowStyles;
import com.google.inject.Provider;

import java.util.Comparator;
import java.util.Date;

public class RmrsRegistrable extends AbstractCompositeRegistrable {
    private final TitleLabelFactory titleLabelFactory = Ecm.getInjector().getTitleLabelFactory();
    private final DocumentViewerFactory documentViewerFactory = Ecm.getInjector().getDocumentViewerFactory();
    private final EditDocumentPlaceFactory editDocumentPlaceFactory = Ecm.getInjector().getEditDocumentPlaceFactory();
    private final RmrsMessages messages = Rmrs.getInjector().getMessages();
    private final TaskViewersFactory taskViewersFactory = Ecm.getInjector().getTaskViewersFactory();
    private final BrowsersFactory browsersFactory = Base.getInjector().getBrowsersFactory();
    private final BrowsePlacesFactory browsePlacesFactory = Base.getInjector().getBrowsePlacesFactory();
    private final DataProviderFactory dataProviderFactory = Base.getInjector().getDataProviderFactory();
    private final RowStyleFactory rowStyleFactory = Base.getInjector().getRowStyleFactory();
    private final TaskProxy taskProxy = Ecm.getInjector().getTaskProxy();
    private final OrgstructureProxy orgstructureProxy = Orgstruct.getInjector().getOrgstructureProxy();

    @Override
    public Registrable[] getRegistrables() {
        return new Registrable[]{
                new ProposalErrandVisaFields(),
                new RequirementFields(),
                new HasChangesToRequirementsFields(),
                new ConsiderProposalErrandFields(),
                new ProposalErrandReportFields(),
                new SearchRequirementFields(),
                new RmrsPositionFields(),
                new ProposalFields(),
                new CircularLetterFields(),
                new HasMultiLanguageFileFields(),
                new RequirementColumns(),
                new RmrsCommands(),
                new RmrsHovers(),
                new Registrable() {
                    @Override
                    public void register() {
                        registerTitleLabels();
                        registerDocumentViewers();
                        registerDocumentEditors();
                        registerTaskViewers();
                        registerBrowsers();
                        registerPlaces();
                        registerDataProviders();
                        registerRowStyles();
                        registerTaskFolders();
                    }
                }

        };
    }

    private void registerDataProviders() {
        dataProviderFactory.register(new EcmRegistrable.TaskFolderDataProviderDefinition(), RmrsTasks.REQ_TASK_TYPE);
        dataProviderFactory.register(new EcmRegistrable.TaskFolderDataProviderDefinition(), RmrsTasks.PROPOSAL_TASK_TYPE);
        dataProviderFactory.register(new EcmRegistrable.TaskFolderDataProviderDefinition(), RmrsTasks.CIRCULAR_LETTER_TASK_TYPE);
        dataProviderFactory.register(new DataProviderDefinition() {
            @Override
            public ValueDataProvider createDataProvider(Object value, Folder folder, Comparator comparator) {
                if (value instanceof Requirement) {
                    RequirementsDataProvider requirementsDataProvider = new RequirementsDataProvider(false);
                    requirementsDataProvider.setFolder((Requirement)value);
                    return requirementsDataProvider;
                } else {
                    return null;
                }
            }
        }, Requirement.KIND);
    }

    private void registerRowStyles() {
        rowStyleFactory.register("EffectiveStyle", new RowStyleDefinition() {
            @Override
            public RowStyles rowStyles(Object o) {
                return new RowStyles() {
                    @Override
                    public String getStyleNames(Object o, int i) {
                        if (o instanceof Requirement) {
                            Requirement requirement = (Requirement) o;
                            if (requirement.effective != null) {
                                if (requirement.effective.min != null && requirement.effective.min.after(new Date())) {
                                    return "requirementNotYetEffective";
                                }
                                if (requirement.effective.max != null && requirement.effective.max.before(new Date())) {
                                    return "requirementExpired";
                                }
                            }
                            return "effective";
                        }
                        return null;
                    }
                };
            }
        });
    }

    private void registerPlaces() {
        browsePlacesFactory.register(new BrowsePlacesFactory.Provider() {
            @Override
            public Place getInstance(String folderId, String positionId) {
                return new BrowseTaskFolderPlace(folderId, positionId, null);
            }
        }, RmrsTasks.REQ_TASK_TYPE);
        browsePlacesFactory.register(new BrowsePlacesFactory.Provider() {
            @Override
            public Place getInstance(String folderId, String positionId) {
                return new BrowseTaskFolderPlace(folderId, positionId, null);
            }
        }, RmrsTasks.PROPOSAL_TASK_TYPE);
        browsePlacesFactory.register(new BrowsePlacesFactory.Provider() {
            @Override
            public Place getInstance(String folderId, String positionId) {
                return new BrowseTaskFolderPlace(folderId, positionId, null);
            }
        }, RmrsTasks.CIRCULAR_LETTER_TASK_TYPE);
    }

    private void registerBrowsers() {
        browsersFactory.register(EcmRegistrable.taskFolderBrowser(RmrsTasks.REQ_ON_APPROVAL), RmrsTasks.REQ_ON_APPROVAL);
        browsersFactory.register(EcmRegistrable.taskFolderBrowser(RmrsTasks.REQ_REJECTED), RmrsTasks.REQ_REJECTED);
        browsersFactory.register(new Provider<FolderBrowser>() {
            @Override
            public FolderBrowser get() {
                return new FilteredFolderBrowser("rmrs:requirement", RequirementColumns.ROW_CLASS, Document.DOCUMENT_LIFECYCLE_DRAFT, true, "rmrs:responsible", "state");
            }
        }, RmrsTasks.REQ_DRAFTS);
        browsersFactory.register(new Provider<FolderBrowser>() {
            @Override
            public FolderBrowser get() {
                return new FilteredFolderBrowser("rmrs:requirement", RequirementColumns.ROW_CLASS, AbstractRoute.LIFECYCLE_ONAPPROVAL, true, "ecm:documentAuthor", "state");
            }
        }, RmrsTasks.REQ_SENT_TO_APPROVAL);
        browsersFactory.register(new Provider<FolderBrowser>() {
            @Override
            public FolderBrowser get() {
                return new FilteredFolderBrowser("rmrs:requirement", RequirementColumns.ROW_CLASS, AbstractRoute.LIFECYCLE_APPROVED, false, "rmrs:responsible", "state");
            }
        }, RmrsTasks.REQ_APPROVED);
        browsersFactory.register(new Provider<FolderBrowser>() {
            @Override
            public FolderBrowser get() {
                return new FilteredFolderBrowser("rmrs:requirement", RequirementColumns.ROW_CLASS, AbstractRoute.LIFECYCLE_ONSIGNING, false, "rmrs:responsible", "state");
            }
        }, RmrsTasks.REQ_ON_SIGNING);
        browsersFactory.register(new Provider<FolderBrowser>() {
            @Override
            public FolderBrowser get() {
                return new FilteredFolderBrowser("rmrs:requirement", RequirementColumns.ROW_CLASS, Requirement.LIFECYCLE_SIGNED, false, "rmrs:responsible", "state");
            }
        }, RmrsTasks.REQ_SIGNED);

        browsersFactory.register(new Provider<FolderBrowser>() {
            @Override
            public FolderBrowser get() {
                return new FilteredFolderBrowser("rmrs:proposal", DocumentColumns.ROW_CLASS, Document.DOCUMENT_LIFECYCLE_DRAFT, "ecm:documentAuthor");
            }
        }, RmrsTasks.PROPOSAL_DRAFTS);
        browsersFactory.register(new Provider<FolderBrowser>() {
            @Override
            public FolderBrowser get() {
                return new FilteredFolderBrowser("rmrs:proposal", DocumentColumns.ROW_CLASS, AbstractRoute.LIFECYCLE_ONEXECUTION, "ecm:documentAuthor");
            }
        }, RmrsTasks.PROPOSAL_ON_EXECUTION);
        browsersFactory.register(new Provider<FolderBrowser>() {
            @Override
            public FolderBrowser get() {
                return new FilteredFolderBrowser("rmrs:proposal", DocumentColumns.ROW_CLASS, AbstractRoute.DOCUMENT_LIFECYCLE_ARCHIVED, "ecm:tags");
            }
        }, RmrsTasks.PROPOSAL_ARCHIVED);

        browsersFactory.register(new Provider<FolderBrowser>() {
            @Override
            public FolderBrowser get() {
                return new FilteredFolderBrowser("rmrs:circular", DocumentColumns.ROW_CLASS, Document.DOCUMENT_LIFECYCLE_DRAFT, "ecm:documentAuthor");
            }
        }, RmrsTasks.CIRCULAR_LETTER_DRAFTS);
        browsersFactory.register(new Provider<FolderBrowser>() {
            @Override
            public FolderBrowser get() {
                return new FilteredFolderBrowser("rmrs:circular", DocumentColumns.ROW_CLASS, Document.DOCUMENT_LIFECYCLE_ARCHIVED, "ecm:tags");
            }
        }, RmrsTasks.CIRCULAR_LETTER_SIGNED);
        browsersFactory.register(new Provider<FolderBrowser>() {
            @Override
            public FolderBrowser get() {
                return new FilteredFolderBrowser("rmrs:circular", DocumentColumns.ROW_CLASS, AbstractRoute.LIFECYCLE_REJECTED, "ecm:documentAuthor");
            }
        }, RmrsTasks.CIRCULAR_LETTER_REJECTED);
        browsersFactory.register(new Provider<FolderBrowser>() {
            @Override
            public FolderBrowser get() {
                return new FilteredFolderBrowser("rmrs:circular", DocumentColumns.ROW_CLASS, AbstractRoute.LIFECYCLE_ONAPPROVAL, true, "ecm:documentAuthor");
            }
        }, RmrsTasks.CIRCULAR_LETTER_SENT_TO_APPROVAL);

        browsersFactory.register(EcmRegistrable.taskFolderBrowser(RmrsTasks.CIRCULAR_LETTER_ON_APPROVAL), RmrsTasks.CIRCULAR_LETTER_ON_APPROVAL);
        browsersFactory.register(EcmRegistrable.taskFolderBrowser(RmrsTasks.CIRCULAR_LETTER_ON_SIGNING), RmrsTasks.CIRCULAR_LETTER_ON_SIGNING);
        browsersFactory.register(EcmRegistrable.taskFolderBrowser(RmrsTasks.ERRAND_TASK_REJECTED), RmrsTasks.ERRAND_TASK_REJECTED);
    }

    private void registerDocumentEditors() {
        editDocumentPlaceFactory.register(new EditRequirementPlace(), Requirement.KIND);
        editDocumentPlaceFactory.register(new EditRequirementPlace(), "req");//old documents
        editDocumentPlaceFactory.register(new EditCircularLetterPlace(), CircularLetter.KIND);
        editDocumentPlaceFactory.register(new EditProposalPlace(), Proposal.KIND);
    }

    private void registerTitleLabels() {
        titleLabelFactory.register(messages.shortDescription(), Requirement.KIND);
        titleLabelFactory.register(messages.topic(), Proposal.KIND);
        titleLabelFactory.register(messages.circularReferred(), CircularLetter.KIND);
        titleLabelFactory.register(messages.visa(), Visa.KIND);
        titleLabelFactory.register(messages.report(), ProposalErrandReport.KIND);
        titleLabelFactory.register(messages.errand(), ConsiderProposalErrand.KIND);
        titleLabelFactory.register(messages.errand(), ChangeRequirementErrand.KIND);
    }

    private void registerTaskViewers() {
        taskViewersFactory.register(new TaskViewersFactory.Provider() {
            @Override
            public void createInstance(final Task task, final ActionCallback<Activity> callback) {
                callback.onActionSuccess(new ViewDocumentActivity(task.id));
            }
        }, RmrsTasks.REQ_TASK_TYPE);
        taskViewersFactory.register(new TaskViewersFactory.Provider() {
            @Override
            public void createInstance(final Task task, final ActionCallback<Activity> callback) {
                callback.onActionSuccess(new ViewDocumentActivity(task.id));
            }
        }, RmrsTasks.PROPOSAL_TASK_TYPE);
        taskViewersFactory.register(new TaskViewersFactory.Provider() {
            @Override
            public void createInstance(final Task task, final ActionCallback<Activity> callback) {
                callback.onActionSuccess(new ViewDocumentActivity(task.id));
            }
        }, RmrsTasks.CIRCULAR_LETTER_TASK_TYPE);
    }

    private void registerDocumentViewers() {
        documentViewerFactory.register(new DocumentViewerFactory.Provider() {
            @Override
            public void getInstance(final Document dto, final ActionCallback<Activity> callback) {
                assert dto instanceof Requirement;
                callback.onActionSuccess(new ViewRequirementActivity(((Requirement) dto)));
            }
        }, Requirement.KIND);
        documentViewerFactory.register(new DocumentViewerFactory.Provider() {
            @Override
            public void getInstance(final Document dto, final ActionCallback<Activity> callback) {
                assert dto instanceof Requirement;
                callback.onActionSuccess(new ViewRequirementActivity(((Requirement) dto)));
            }
        }, "req");//old documents
        documentViewerFactory.register(new DocumentViewerFactory.Provider() {
            @Override
            public void getInstance(final Document dto, final ActionCallback<Activity> callback) {
                assert dto instanceof Proposal;
                callback.onActionSuccess(new ViewProposalActivity(((Proposal) dto)));
            }
        }, Proposal.KIND);
        documentViewerFactory.register(new DocumentViewerFactory.Provider() {
            @Override
            public void getInstance(final Document dto, final ActionCallback<Activity> callback) {
                assert dto instanceof CircularLetter;
                callback.onActionSuccess(new ViewCircularLetterActivity(((CircularLetter) dto)));
            }
        }, CircularLetter.KIND);
    }

    private void registerTaskFolders() {
        taskProxy.registerFolderTranslator(new TaskProxy.TaskFolderTranslator() {
            @Override
            public String getFolder(Task task) {
                if (Requirement.KIND.equals(task.getKind())) {
                    return AbstractRoute.LIFECYCLE_ONAPPROVAL.equals(task.getLifecycle())
                            ? RmrsTasks.REQ_ON_APPROVAL
                            : AbstractRoute.LIFECYCLE_ONSIGNING.equals(task.getLifecycle())
                            ? RmrsTasks.REQ_ON_SIGNING
                            : AbstractRoute.LIFECYCLE_REJECTED.equals(task.getLifecycle())
                            ? RmrsTasks.REQ_REJECTED
                            : null;
                } else if (CircularLetter.KIND.equals(task.getKind())) {
                    return AbstractRoute.LIFECYCLE_ONAPPROVAL.equals(task.getLifecycle())
                            ? RmrsTasks.CIRCULAR_LETTER_ON_APPROVAL
                            : AbstractRoute.LIFECYCLE_REJECTED.equals(task.getLifecycle())
                            ? RmrsTasks.CIRCULAR_LETTER_REJECTED
                            : AbstractRoute.LIFECYCLE_ONSIGNING.equals(task.getLifecycle())
                            ? RmrsTasks.CIRCULAR_LETTER_ON_SIGNING
                            : null;
                } else if (Errand.ERRAND_TASK_TYPE.equals(task.type) && task.get(Errand.REJECTED_DATE_KEY) != null  && !orgstructureProxy.isMyPosition(task.author)) {
                    return RmrsTasks.ERRAND_TASK_REJECTED;
                }
                return null;
            }
        });
    }

}