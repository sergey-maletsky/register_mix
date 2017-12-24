package com.firstlinesoftware.rmrs.client.activities;

import com.firstlinesoftware.base.client.Base;
import com.firstlinesoftware.base.client.views.selectors.SearchPersistentImpl;
import com.firstlinesoftware.base.shared.dto.DateRange;
import com.firstlinesoftware.base.shared.dto.DirectoryItemSearchField;
import com.firstlinesoftware.base.shared.dto.SearchCriteria;
import com.firstlinesoftware.base.shared.dto.SearchField;
import com.firstlinesoftware.base.shared.utils.CriteriaBuilder;
import com.firstlinesoftware.ecm.shared.directories.EcmDirectories;
import com.firstlinesoftware.orgstruct.shared.dto.Position;
import com.firstlinesoftware.rmrs.client.Rmrs;
import com.firstlinesoftware.rmrs.client.messages.RmrsMessages;
import com.firstlinesoftware.rmrs.client.widgets.RequirementSelector;
import com.firstlinesoftware.rmrs.shared.dto.Requirement;
import com.google.common.base.Strings;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class BaseSearchRequirementActivity {
    private final RmrsMessages messages = Rmrs.getInjector().getMessages();

    private final SearchPersistentImpl view;

    public BaseSearchRequirementActivity(SearchPersistentImpl view) {
        this.view = view;
    }

    public SearchCriteria fillSearchCriteria() {
        final CriteriaBuilder builder = new CriteriaBuilder();
        final List<SearchField> mandatory = (List<SearchField>) view.getFormItemValue(messages.mandatoryFields());
        final List<SearchField> optional = (List<SearchField>) view.getFormItemValue(messages.optionalFields());
        if (mandatory != null) {
            for (SearchField searchField : mandatory) {
                searchField.addToCriteria(builder, true);
            }
        }
        if (optional != null) {
            for (SearchField searchField : optional) {
                searchField.addToCriteria(builder, false);
            }
        }
        String name = (String) view.getFormItemValue(messages.name());
        String number = (String) view.getFormItemValue(messages.numberAndNomenclature());
        if (!Strings.isNullOrEmpty(name) && name.indexOf('*') == -1) {
            name += '*';
        }
        if (!Strings.isNullOrEmpty(number) && number.indexOf('*') == -1) {
            number += '*';
        }

        final Position author = (Position) view.getFormItemValue(messages.author());

        RequirementSelector requirementSelector = (RequirementSelector) view.getFormItemWidget(messages.includedIn());
        Requirement parent = (Requirement) requirementSelector.getValue();

        String fullPathPrefix = null;
        if (parent != null) {
            fullPathPrefix =  (parent.fullPath != null ? parent.fullPath + '.' : "")  + parent.id + "*";
        }

        builder
                .addMustHave("ecm:number", number)
                .addMustHave("ecm:documentAuthor", author != null ? author.id : null)
                .addMustHave("ecm:lifecycle", (Serializable) view.getFormItemValue(messages.lifecycle()))
                .addMustHave("rmrs:fullPath", fullPathPrefix)
                .addMustHaveDateRange("exec:begin", (DateRange) view.getFormItemValue(messages.effectiveBegin()))
                .addMustHaveDateRange("exec:end", (DateRange) view.getFormItemValue(messages.effectiveEnd()))
                .addShouldHave("cm:title", name)
                .addShouldHave("rmrs:tags", name);
        if (!builder.build().isEmpty()) {
            builder.addMustHave("ecm:kind", Requirement.KIND);
        }
        return builder.build();
    }

    public void changeMode(boolean extended) {
        view.setVisible(!extended, messages.content(), messages.lifecycle(), messages.effectiveBegin(), messages.effectiveEnd());
        view.setVisible(extended, messages.mandatoryFields(), messages.optionalFields());
        if (extended) {
            view.setFormItemWidgetValue(messages.content(), null);
            view.setFormItemWidgetValue(messages.lifecycle(), null);
            view.setFormItemWidgetValue(messages.effectiveBegin(), null);
            view.setFormItemWidgetValue(messages.effectiveEnd(), null);
            final SearchField searchByType = new DirectoryItemSearchField(Base.getInjector().getDirectoryProxy().getByValue(EcmDirectories.DOCUMENT_TYPES.getType(), Requirement.KIND));
            view.setFormItemWidgetValue(messages.mandatoryFields(), Arrays.asList(searchByType));
        } else {
            view.setFormItemWidgetValue(messages.mandatoryFields(), null);
            view.setFormItemWidgetValue(messages.optionalFields(), null);
        }
        view.onResize();
    }
}
