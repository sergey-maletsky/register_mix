package com.firstlinesoftware.rmrs.client.widgets;

import com.firstlinesoftware.base.client.SuccessCallback;
import com.firstlinesoftware.base.client.activities.AbstractActivity;
import com.firstlinesoftware.base.client.widgets.popups.BaseFormPopupPresenter;
import com.firstlinesoftware.base.shared.dto.SearchCriteria;
import com.firstlinesoftware.base.shared.utils.CriteriaBuilder;
import com.firstlinesoftware.ecm.client.widgets.DocumentSelector;
import com.firstlinesoftware.ecm.shared.dto.Document;
import com.firstlinesoftware.rmrs.client.activities.SelectRequirementActivity;
import com.firstlinesoftware.rmrs.shared.dto.Requirement;
import com.google.gwt.regexp.shared.RegExp;

import java.util.HashSet;
import java.util.Set;

public class RequirementSelector extends DocumentSelector {

    private final boolean onlyHeaders;
    private String lifecycle;
    private boolean useSearch = true;

    public RequirementSelector(boolean onlyHeaders) {
        this.onlyHeaders = onlyHeaders;
    }

    public RequirementSelector(boolean onlyHeaders, String lifecycle) {
        this.onlyHeaders = onlyHeaders;
        this.lifecycle = lifecycle;
    }

    @Override
    protected void selectDocument(SuccessCallback<Set<Document>> successCallback) {
        final BaseFormPopupPresenter<Set<Document>> presenter = new BaseFormPopupPresenter<Set<Document>>(successCallback) {
            @Override
            protected AbstractActivity createActivity() {
                return new SelectRequirementActivity() {
                    @Override
                    public void onResultSuccess(Set<Requirement> selection) {
                        onSuccess(new HashSet<Document>(selection));
                    }

                    @Override
                    public void onResultCancel() {
                        hidePanel();
                    }
                };
            }
        };
        presenter.showModal();
    }

    @Override
    protected CriteriaBuilder<SearchCriteria> getCriteriaBuilder(final String query) {

        final CriteriaBuilder<SearchCriteria> result = super.getCriteriaBuilder("*" + query + "*");
        result.addShouldHave("ecm:number", "*" + query + "*");
        if (onlyHeaders) {
            result.addMustHave("rmrs:header", true);
        }
        if (lifecycle != null) {
            result.addMustHave("ecm:lifecycle", lifecycle);
        }
        return result;
    }

    @Override
    protected DocumentSuggestion getDocumentSuggestion(RegExp regExp, Document item) {
        return new DocumentSuggestion(item, regExp) {
            @Override
            public String getDisplayStringPlain() {
                return item instanceof Requirement
                        ? (((Requirement) item).number != null ? ((Requirement) item).number + " " : "") + item.getTitle()
                        : "";
            }

        };
    }

    @Override
    protected void setButtonsVisibility(Document value) {
        super.setButtonsVisibility(value);
        picker.setEnabled(isEnabled() && useSearch);
    }

    public void setUseSearch(boolean useSearch) {
        this.useSearch = useSearch;
        setButtonsVisibility(getValue());
    }
}
