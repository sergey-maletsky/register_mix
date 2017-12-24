package com.firstlinesoftware.rmrs.client.models;

import com.firstlinesoftware.base.client.factories.BaseDefinition;
import com.firstlinesoftware.base.client.models.NavigatorModel;
import com.firstlinesoftware.base.client.models.PersistentSearchResultsDataProvider;
import com.firstlinesoftware.base.client.models.TakesFolder;
import com.firstlinesoftware.base.client.services.ActionCallback;
import com.firstlinesoftware.base.shared.dto.DTO;
import com.firstlinesoftware.base.shared.dto.SearchCriteria;
import com.firstlinesoftware.base.shared.utils.CriteriaBuilder;
import com.firstlinesoftware.ecm.client.Ecm;
import com.firstlinesoftware.ecm.client.events.DocumentChangedEvent;
import com.firstlinesoftware.ecm.client.events.DocumentRemovedEvent;
import com.firstlinesoftware.ecm.client.events.FolderItemAddedEvent;
import com.firstlinesoftware.ecm.client.messages.EcmMessages;
import com.firstlinesoftware.ecm.client.proxies.SearchProxy;
import com.firstlinesoftware.rmrs.client.Rmrs;
import com.firstlinesoftware.rmrs.client.columns.RequirementColumns;
import com.firstlinesoftware.rmrs.client.events.OrderChangeEvent;
import com.firstlinesoftware.rmrs.client.events.OrderChangeFinishedEvent;
import com.firstlinesoftware.rmrs.client.events.OrderChangeStartedEvent;
import com.firstlinesoftware.rmrs.client.proxies.RequirementProxy;
import com.firstlinesoftware.rmrs.shared.dto.Requirement;
import com.google.common.base.Objects;
import com.google.common.collect.Lists;

import java.util.*;

public class RequirementsDataProvider extends PersistentSearchResultsDataProvider<DTO> implements TakesFolder<DTO>, DocumentChangedEvent.Handler, FolderItemAddedEvent.Handler, OrderChangeEvent.OrderChangeEventHandler, OrderChangeStartedEvent.OrderChangeStartedEventHandler, OrderChangeFinishedEvent.OrderChangeFinishedEventHandler, DocumentRemovedEvent.Handler {
    private static Map<String, String> ordersById = new HashMap<>();
    private static final Comparator<DTO> COMPARE_NUMBER = new Comparator<DTO>() {
        public int compare(DTO o1, DTO o2) {
            return o1 instanceof Requirement && o2 instanceof Requirement ?
                    BaseDefinition.compareNullables(RequirementColumns.addLeadingZeros(((Requirement) o1).number), RequirementColumns.addLeadingZeros(((Requirement) o2).number))
                    : 0;
        }
    };
    private static final Comparator<DTO> COMPARE_ORDER = new Comparator<DTO>() {
        @Override
        public int compare(DTO o1, DTO o2) {
            final String r1 = ordersById.get(o1.id);
            final String r2 = ordersById.get(o2.id);
            return r1 != null ? r2 != null ? r1.compareTo(r2) : -1 : 1;
        }
    };

    private final SearchProxy searchProxy = Ecm.getInjector().getSearchProxy();
    private final EcmMessages messages = Ecm.getInjector().getMessages();
    private final RequirementProxy requirementProxy = Rmrs.getInjector().getRequirementProxy();

    private final ActionCallback<List<Requirement>> finishUpdating = new ActionCallback<List<Requirement>>(messages.errorWhileGettingDocuments()) {
        @Override
        public void onActionSuccess(List<Requirement> result) {
            setValue(result != null ? Lists.<DTO>newArrayList(result) : null);
            ids = result != null ? new HashSet<>(DTO.getIDs(result)) : null;
        }
    };
    private final boolean onlyHeaders;
    private Date effectiveDate;
    private String folder;
    private boolean sorted;
    private List<DTO> beforeOrder;
    private boolean showRecursive;
    private Set<String> ids;
    private Boolean showOnlySigned;


    public RequirementsDataProvider(boolean onlyHeaders) {
        this.onlyHeaders = onlyHeaders;
    }

    public void setFolder(final DTO folder) {
        this.folder = folder.id;
        startUpdating();
    }

    public void setFolder(final String folder) {
        this.folder = folder;
        startUpdating();
    }

    @Override
    public void setComparator(Comparator<DTO> currentComparator) {
        sorted = currentComparator != null;
        super.setComparator(currentComparator);
    }

    @Override
    protected List<DTO> sort(List<DTO> input) {
        return onlyHeaders && NavigatorModel.ROOT.id.equals(folder) ? sortByNumber(input) : showRecursive || !sorted ? sortByOrderRecursive(input) : super.sort(input);
    }

    private List<DTO> sortByNumber(List<DTO> input) {
        final List<DTO> sorted = new ArrayList<>(input);
        Collections.sort(sorted, COMPARE_NUMBER);
        return sorted;

    }

    private List<DTO> sortByOrderRecursive(List<DTO> input) {
        final Map<String, Requirement> reqs = new HashMap<>();
        for (DTO dto : input) {
            if (dto instanceof Requirement) {
                reqs.put(dto.id, (Requirement) dto);
            }
        }
        ordersById.clear();
        for (Requirement dto : reqs.values()) {
            fillOrder(dto, reqs);
        }

        final List<DTO> sorted = new ArrayList<>(input);
        Collections.sort(sorted, COMPARE_ORDER);
        return sorted;
    }

    private void fillOrder(Requirement r, Map<String, Requirement> reqs) {
        if (r != null && !ordersById.containsKey(r.id)) {
            if (r.parent != null) {
                fillOrder(reqs.get(r.parent.id), reqs);
                final String p = ordersById.get(r.parent.id);
                ordersById.put(r.id, (p != null ? p + '.' : "") + addLeadingZeros(r.order));
            } else {
                ordersById.put(r.id, addLeadingZeros(r.order));
            }
        }
    }

    private String addLeadingZeros(Integer number) {
        if (number == null) {
            return "0000";
        }
        final StringBuilder result = new StringBuilder();
        final String it = String.valueOf(number);
        for (int i = 0; i < 4 - it.length(); i++) {
            result.append('0');
        }
        result.append(it);
        return result.toString();
    }


    public void setShowAllItems(Date on, Boolean showOnlySigned) {
        this.effectiveDate = on;
        this.showOnlySigned = showOnlySigned;
        startUpdating();
    }

    @Override
    protected void update() {
        if (searchCriteria != null) {
            super.update();
        } else {
            requirementProxy.getByFolderWithAttachVersions(folder, showRecursive, onlyHeaders, finishUpdating);
        }
    }

    public static void getDocuments(String id, boolean onlyHeaders, ActionCallback<List<Requirement>> callback) {
        final CriteriaBuilder<SearchCriteria> builder = new CriteriaBuilder<>();
        if (Objects.equal(id, NavigatorModel.ROOT.id) || id == null) {
            builder.addMustBeNull("rmrs:parent").setType("rmrs:requirement");
        } else {
            builder.addMustHave("rmrs:parent", id);
        }
        if (onlyHeaders) {
            builder.addMustHave("rmrs:header", true);
        }
        Ecm.getInjector().getSearchProxy().search(builder.build(), callback);
    }

    @Override
    protected void search() {
        searchProxy.search(searchCriteria, finishUpdating);
    }

    @Override
    protected List<DTO> filter(List<DTO> input) {
        if (effectiveDate != null || Boolean.TRUE.equals(showOnlySigned)) {
            Iterator<DTO> it = input.iterator();
            while (it.hasNext()) {
                Requirement r = (Requirement) it.next();
                if (!r.isEffective(effectiveDate) || (Boolean.TRUE.equals(showOnlySigned) && !Requirement.LIFECYCLE_SIGNED.equals(r.lifecycle))) {
                    it.remove();
                }
            }
        }
        return super.filter(input);
    }

    @Override
    protected void addEventHandlers() {
        addEventBusHandler(FolderItemAddedEvent.getType(), this);
        addEventBusHandler(DocumentChangedEvent.getType(), this);
        addEventBusHandler(DocumentRemovedEvent.getType(), this);
        addEventBusHandler(OrderChangeStartedEvent.getType(), this);
        addEventBusHandler(OrderChangeFinishedEvent.getType(), this);
        addEventBusHandler(OrderChangeEvent.getType(), this);
    }

    @Override
    public void onDocumentChanged(DocumentChangedEvent event) {
        if (Objects.equal(event.document.id, folder) || ids != null && ids.contains(event.document.id)) {
            startUpdating();
        }
    }

    @Override
    public void onDocumentRemoved(DocumentRemovedEvent event) {
        if (ids != null && ids.contains(event.documentId)) {
            startUpdating();
        }
    }

    @Override
    public void onFolderItemAdded(FolderItemAddedEvent event) {
        final String folderId = event.folderId;
        if (isOurParent(folderId)) {
            startUpdating();
        }
    }

    @Override
    public void onOrderChanged(OrderChangeEvent event) {
        if (value != null && beforeOrder != null) {
            setValue(new ArrayList<DTO>(event.value));
        }
    }

    @Override
    public void onOrderChangeStarted(OrderChangeStartedEvent event) {
        if (value != null && isOurParent(event.parent)) {
            assert beforeOrder == null;
            beforeOrder = value;
        }
    }

    @Override
    public void onOrderChangeFinished(OrderChangeFinishedEvent event) {
        if (beforeOrder != null && !event.save) {
            setValue(beforeOrder);
        }
        beforeOrder = null;
    }

    private boolean isOurParent(String folderId) {
        return Objects.equal(folderId, folder) || Objects.equal("requirements", folderId) && Objects.equal(NavigatorModel.ROOT.id, folder);
    }

    public void setShowRecursive(boolean on) {
        this.showRecursive = on;
    }
}
