package com.firstlinesoftware.rmrs.client.models;

import com.firstlinesoftware.base.client.models.NavigatorModel;
import com.firstlinesoftware.base.client.models.PersistentSearchResultsDataProvider;
import com.firstlinesoftware.base.client.services.ActionCallback;
import com.firstlinesoftware.base.shared.actions.RestResult;
import com.firstlinesoftware.base.shared.dto.DTO;
import com.firstlinesoftware.base.shared.dto.SearchCriteria;
import com.firstlinesoftware.base.shared.utils.CriteriaBuilder;
import com.firstlinesoftware.ecm.client.Ecm;
import com.firstlinesoftware.ecm.client.events.DocumentChangedEvent;
import com.firstlinesoftware.ecm.client.events.FolderItemAddedEvent;
import com.firstlinesoftware.ecm.client.messages.EcmMessages;
import com.firstlinesoftware.orgstruct.shared.dto.Position;
import com.firstlinesoftware.rmrs.client.events.OrderChangeEvent;
import com.firstlinesoftware.rmrs.client.events.OrderChangeFinishedEvent;
import com.firstlinesoftware.rmrs.client.events.OrderChangeStartedEvent;
import com.firstlinesoftware.rmrs.shared.dto.Requirement;
import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.google.common.primitives.Ints;

import java.util.*;

public class PersonRequirementsDataProvider extends PersistentSearchResultsDataProvider<DTO> implements DocumentChangedEvent.Handler, FolderItemAddedEvent.Handler, OrderChangeEvent.OrderChangeEventHandler, OrderChangeStartedEvent.OrderChangeStartedEventHandler, OrderChangeFinishedEvent.OrderChangeFinishedEventHandler {

    private final EcmMessages messages = Ecm.getInjector().getMessages();

    private final ActionCallback<List<Requirement>> finishUpdating = new ActionCallback<List<Requirement>>(messages.errorWhileGettingDocuments()) {
        @Override
        public void onActionSuccess(List<Requirement> result) {
            setValue(result != null ? Lists.<DTO>newArrayList(result) : null);
        }
    };
    private final boolean onlyHeaders;
    private boolean onlyEffective = false;
    private Position position;
    private boolean sorted;
    private List<DTO> beforeOrder;

    public PersonRequirementsDataProvider(boolean onlyHeaders) {
        this.onlyHeaders = onlyHeaders;
    }

    public static void getDocuments(String id, boolean onlyHeaders, ActionCallback<List<Requirement>> callback) {
        final CriteriaBuilder<SearchCriteria> builder = new CriteriaBuilder<>()
                .addMustHave("rmrs:responsible", id)
                .setType("rmrs:requirement");
        if (onlyHeaders) {
            builder.addMustHave("rmrs:header", true);
        }
        Ecm.getInjector().getSearchProxy().search(builder.build(), callback);
    }

    public void setPosition(final Position position) {
        this.position = position;
        startUpdating();
    }

    @Override
    public void setComparator(Comparator<DTO> currentComparator) {
        sorted = currentComparator != null;
        super.setComparator(currentComparator);
    }

    @Override
    protected List<DTO> sort(List<DTO> input) {
        if (sorted) {
            return super.sort(input);
        } else {
            final List<DTO> sorted = new ArrayList<>(input);
            Collections.sort(sorted, new Comparator<DTO>() {
                @Override
                public int compare(DTO o1, DTO o2) {
                    if (o1 instanceof Requirement && o2 instanceof Requirement) {
                        final Integer a = ((Requirement) o1).order;
                        final Integer b = ((Requirement) o2).order;
                        return Ints.compare(a != null ? a : Integer.MIN_VALUE, b != null ? b : Integer.MIN_VALUE);
                    } else
                        return 0;
                }
            });
            return sorted;
        }
    }

    public void setOnlyEffective(boolean on) {
        this.onlyEffective = on;
        startUpdating();
    }

    @Override
    protected void update() {
        if (searchCriteria != null) {
            super.update();
        } else {
            if (position != null) {
                getDocuments(position != null ? position.id : null, onlyHeaders, finishUpdating);
            } else {
                finishUpdating.onSuccess(new RestResult());
            }
        }
    }

    @Override
    protected void search() {
        Ecm.getInjector().getSearchProxy().search(searchCriteria, finishUpdating);
    }

    @Override
    protected List<DTO> filter(List<DTO> input) {
        if (onlyEffective) {
            Iterator<DTO> it = input.iterator();
            while (it.hasNext()) {
                Requirement r = (Requirement) it.next();
                if (!r.isEffective(new Date())) {
                    it.remove();
                }
            }
        }
        return input;
    }

    @Override
    protected void addEventHandlers() {
        addEventBusHandler(FolderItemAddedEvent.getType(), this);
        addEventBusHandler(DocumentChangedEvent.getType(), this);
        addEventBusHandler(OrderChangeStartedEvent.getType(), this);
        addEventBusHandler(OrderChangeFinishedEvent.getType(), this);
        addEventBusHandler(OrderChangeEvent.getType(), this);
    }

    @Override
    public void onDocumentChanged(DocumentChangedEvent event) {
        if (Objects.equal(event.document.id, position)) {
            startUpdating();
        }
    }

    @Override
    public void onFolderItemAdded(FolderItemAddedEvent event) {
//        final String folderId = event.folderId;
//        if (isOurParent(folderId)) {
//            startUpdating();
//        }
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
        return Objects.equal(folderId, position) || Objects.equal("requirements", folderId) && Objects.equal(NavigatorModel.ROOT.id, position);
    }
}
