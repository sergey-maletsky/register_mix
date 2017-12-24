package com.firstlinesoftware.rmrs.client.fields;

import com.firstlinesoftware.base.client.Base;
import com.firstlinesoftware.base.client.factories.FormItemFactory;
import com.firstlinesoftware.base.client.factories.Registrable;
import com.firstlinesoftware.base.client.services.ActionCallback;
import com.firstlinesoftware.base.client.views.FormView;
import com.firstlinesoftware.base.client.widgets.GridViewer;
import com.firstlinesoftware.base.shared.dto.SearchCriteria;
import com.firstlinesoftware.base.shared.utils.CriteriaBuilder;
import com.firstlinesoftware.ecm.client.Ecm;
import com.firstlinesoftware.ecm.client.proxies.SearchProxy;
import com.firstlinesoftware.orgstruct.shared.dto.PositionInfo;
import com.firstlinesoftware.rmrs.client.Rmrs;
import com.firstlinesoftware.rmrs.client.messages.RmrsMessages;
import com.firstlinesoftware.rmrs.shared.dto.Requirement;
import com.google.gwt.user.client.ui.Widget;

import java.util.List;

public class RmrsPositionFields implements Registrable {
    private final FormItemFactory formItemFactory = Base.getInjector().getFormItemFactory();
    private final RmrsMessages messages = Rmrs.getInjector().getMessages();
    private final SearchProxy searchProxy = Ecm.getInjector().getSearchProxy();

    @Override
    public void register() {
        formItemFactory.register(new ImResponsibleOfField(), messages.requirements());

    }

    private class ImResponsibleOfField extends RequirementsGridField<PositionInfo> {

        @Override
        protected List<Requirement> getValue(PositionInfo position) {
            return null;
        }

        @Override
        protected void setValue(PositionInfo position, List<Requirement> requirements) {

        }

        @Override
        public boolean hasAspect(Object o) {
            return o instanceof PositionInfo;
        }

        @Override
        public void setWidgetValue(final Widget widget, PositionInfo dto, boolean editor) {
            assert widget instanceof GridViewer;
            final CriteriaBuilder<SearchCriteria> builder = new CriteriaBuilder<>().addMustHave("rmrs:responsible", dto.position.id);
            searchProxy.search(builder.build(), new ActionCallback<List<Requirement>>(messages.errorWhileGettingDocuments()) {
                @Override
                public void onActionSuccess(List<Requirement> documents) {
                    ((GridViewer<Requirement>) widget).setValue(documents);
                    widget.setVisible(documents != null && !documents.isEmpty());
                }
            });
        }

        @Override
        public String getViewTab() {
            return messages.requirements();
        }

        @Override
        public Widget createWidget(FormView<PositionInfo> view, String itemName, boolean editor, PositionInfo dto) {
            return !editor ? createSingleWidget(view, itemName, false, dto) : null;
        }
    }
}
