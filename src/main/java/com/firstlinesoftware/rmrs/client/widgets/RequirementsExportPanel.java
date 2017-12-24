package com.firstlinesoftware.rmrs.client.widgets;

import com.firstlinesoftware.base.client.utils.PopupButtonContentBuilder;
import com.firstlinesoftware.base.client.widgets.DatePicker;
import com.firstlinesoftware.base.client.widgets.ImageButton;
import com.firstlinesoftware.base.client.widgets.popups.StandardEditorPanel;
import com.firstlinesoftware.base.shared.dto.Pair;
import com.firstlinesoftware.base.shared.dto.SearchCriteria;
import com.firstlinesoftware.base.shared.utils.CriteriaBuilder;
import com.firstlinesoftware.ecm.client.widgets.DocumentSelector;
import com.firstlinesoftware.ecm.shared.dto.Document;
import com.firstlinesoftware.rmrs.client.Rmrs;
import com.firstlinesoftware.rmrs.client.messages.RmrsMessages;
import com.firstlinesoftware.rmrs.shared.dto.Requirement;
import com.google.gwt.regexp.shared.RegExp;

import java.util.Date;

public class RequirementsExportPanel extends StandardEditorPanel<Pair<String, Date>> {

    private final RmrsMessages messages = Rmrs.getInjector().getMessages();

    private final DatePicker date = new DatePicker();
    private DocumentSelector root;

    @Override
    protected void appendContent(PopupButtonContentBuilder builder) {
        root = new DocumentSelector() {
            @Override
            protected ImageButton createPickerButton() {
                return null;
            }

            @Override
            protected CriteriaBuilder<SearchCriteria> getCriteriaBuilder(String query) {
                return new CriteriaBuilder<>()
                        .setType("rmrs:requirement")
                        .addMustHave("ecm:number", query + '*')
                        .addMustBeNull("rmrs:parent");
//                        .addMustHave(EcmDirectories.LIFECYCLE.getType(), lifecycle);
            }

            @Override
            protected DocumentSuggestion getDocumentSuggestion(RegExp regExp, Document item) {
                return new DocumentSuggestion(item,  regExp) {
                    @Override
                    public String getDisplayStringPlain() {
                        return item instanceof Requirement
                                ? (((Requirement) item).number != null ? ((Requirement) item).number + " " : "") + item.getTitle()
                                : "";
                    }
                };
            }
        };
        root.addValueChangeHandler(this.<Document>valueChangeHandlerDelegate());
        date.addValueChangeHandler(this.<Date>valueChangeHandlerDelegate());
        builder.addRequiredWidget(messages.placeInStructure(), root);
        builder.addRequiredWidget(messages.effectiveDates(), date);
    }

    @Override
    public boolean validate() {
        return date.getValue() != null && root.getValue() != null;
    }

    @Override
    public void setValue(Pair<String, Date> value, boolean fireEvents) {
        if (value == null) {
            root.setValue(null, false);
            date.setValue(null, false);
//        } else {
//            root.setValue(value.getFirst());
//            date.setValue(value.getSecond());
        }
    }

    @Override
    public Pair<String, Date> getValue() {
        return new Pair<>(root.getValue().id, date.getValue());
    }
}
