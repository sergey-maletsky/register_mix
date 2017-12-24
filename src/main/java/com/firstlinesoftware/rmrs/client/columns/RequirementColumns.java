package com.firstlinesoftware.rmrs.client.columns;

import com.firstlinesoftware.base.client.Base;
import com.firstlinesoftware.base.client.columns.ColumnsFactory;
import com.firstlinesoftware.base.client.columns.DateGridColumnDefinition;
import com.firstlinesoftware.base.client.columns.GridColumnDefinition;
import com.firstlinesoftware.base.client.columns.IntegerGridColumnDefinition;
import com.firstlinesoftware.base.client.factories.Registrable;
import com.firstlinesoftware.ecm.client.columns.DocumentColumns;
import com.firstlinesoftware.ecm.client.widgets.LifecycleRenderer;
import com.firstlinesoftware.ecm.shared.dto.Document;
import com.firstlinesoftware.orgstruct.client.columns.PositionGridColumnDefinition;
import com.firstlinesoftware.orgstruct.shared.dto.Position;
import com.firstlinesoftware.rmrs.client.Rmrs;
import com.firstlinesoftware.rmrs.client.messages.RmrsMessages;
import com.firstlinesoftware.rmrs.shared.dto.Requirement;
import com.firstlinesoftware.route.shared.dto.AbstractRoute;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;

import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;

public class RequirementColumns implements Registrable {
    public static final String ROW_CLASS = "Requirement";
    public static final String GRID_ROW_CLASS = "RequirementsGrid";

    private static final HashMap<String, String> STATES = new HashMap<>();
    static {
        STATES.put(Document.DOCUMENT_LIFECYCLE_DRAFT, "red");
        STATES.put(AbstractRoute.LIFECYCLE_ONAPPROVAL, "blue");
        STATES.put(AbstractRoute.LIFECYCLE_APPROVED, "green");
        STATES.put(Requirement.LIFECYCLE_SIGNED, "black");
    }

    private final ColumnsFactory columnsFactory = Base.getInjector().getColumnsFactory();
    private final RmrsMessages messages = Rmrs.getInjector().getMessages();
    private final LifecycleRenderer lifecycleRenderer = new LifecycleRenderer();

    public void register() {
        columnsFactory.register(DocumentColumns.author(), ROW_CLASS);
        columnsFactory.register(DocumentColumns.created(), ROW_CLASS);
        columnsFactory.register(number(), ROW_CLASS);
        columnsFactory.register(parentNumber(), ROW_CLASS);
        columnsFactory.register(title(), ROW_CLASS);
        columnsFactory.register(state(), ROW_CLASS);
        columnsFactory.register(effectiveBegin(), ROW_CLASS);
        columnsFactory.register(effectiveEnd(), ROW_CLASS);
        columnsFactory.register(responsible(), ROW_CLASS);
        columnsFactory.register(signedDate(), ROW_CLASS);
        columnsFactory.register(order(), ROW_CLASS);
    }

    private GridColumnDefinition<Requirement, Position> responsible() {
        return new PositionGridColumnDefinition<Requirement>("responsible", messages.responsible()) {
            @Override
            protected Position getCellValue(Requirement requirement) {
                return requirement.responsible;
            }
        };
    }

    private GridColumnDefinition<Requirement, String> title() {
        return new GridColumnDefinition<Requirement, String>("title", messages.shortDescription()) {
            @Override
            public String getCellValue(final Requirement rowValue) {
                return rowValue.getTitle();
            }
        };
    }

    private GridColumnDefinition<Requirement, String> number() {
        return new GridColumnDefinition<Requirement, String>("number", messages.number()) {
            @Override
            public String getCellValue(final Requirement rowValue) {
                return rowValue.number;
            }

            @Override
            public Comparator<Requirement> getRowComparator() {
                return new Comparator<Requirement>() {
                    public int compare(Requirement o1, Requirement o2) {
                        return compareNullables(addLeadingZeros(o1.number), addLeadingZeros(o2.number));
                    }
                };
            }
        };
    }

    private GridColumnDefinition<Requirement, String> parentNumber() {
        return new GridColumnDefinition<Requirement, String>("parentNumber", messages.parentNumber()) {
            @Override
            public String getCellValue(final Requirement requirement) {
                return requirement.parent != null ? requirement.parent.number : null;
            }

            @Override
            public Comparator<Requirement> getRowComparator() {
                return new Comparator<Requirement>() {
                    public int compare(Requirement o1, Requirement o2) {
                        final String d1 = o1.parent != null ? addLeadingZeros(o1.parent.number) : null;
                        final String d2 = o2.parent != null ? addLeadingZeros(o2.parent.number) : null;
                        return compareNullables(d1, d2);
                    }
                };
            }
        };
    }

    public static String addLeadingZeros(String number) {
        if (number == null) {
            return "0";
        }
        StringBuilder result = new StringBuilder();
        String[] tokens = number.split("\\.");
        for (String it : tokens) {
            for (int i = 0; i < 5 - it.length(); i++) {
                result.append("0");
            }
            result.append(it);
        }
        return result.toString();
    }

    private GridColumnDefinition<Requirement, String> state() {
        return new GridColumnDefinition<Requirement, String>("state", "", 21) {
            @Override
            protected SafeHtml renderValue(String value) {
                return SafeHtmlUtils.fromTrustedString(value);
            }

            @Override
            protected String getCellValue(Requirement value) {
                final String state = STATES.get(value.lifecycle);
                final String symbol = Boolean.TRUE.equals(value.header) ? "&#xe2c7;" : "&#xe873;";
                final String lifecycle = lifecycleRenderer.render(value.lifecycle);
                return "<a href=\"#requirements:id="+value.getId()+"\"><span class=\"material-icons\" style=\"font-size:18px;color:" + (state != null ? state : "black") + "\" title=\"" + lifecycle + "\">" + symbol + "</span></a>";
            }

            @Override
            public String render(String value) {
                return value;
            }
        };
    }

    private DateGridColumnDefinition<Requirement> effectiveBegin() {
        return new DateGridColumnDefinition<Requirement>("effectiveBegin", messages.effectiveBegin()) {
            @Override
            protected Date getCellValue(Requirement requirement) {
                return requirement.effective != null ? requirement.effective.min : null;
            }
        };
    }

    private DateGridColumnDefinition<Requirement> effectiveEnd() {
        return new DateGridColumnDefinition<Requirement>("effectiveEnd", messages.effectiveEnd()) {
            @Override
            protected Date getCellValue(Requirement requirement) {
                return requirement.effective != null ? requirement.effective.max : null;
            }
        };
    }

    private DateGridColumnDefinition<Requirement> signedDate() {
        return new DateGridColumnDefinition<Requirement>("signingDate", messages.signedDate()) {
            @Override
            protected Date getCellValue(Requirement requirement) {
                return requirement.routeState.signingDate;
            }
        };
    }

    private GridColumnDefinition<Requirement, Integer> order() {
        return new IntegerGridColumnDefinition<Requirement>("order", messages.order(), 30) {
            @Override
            public Integer getCellValue(final Requirement rowValue) {
                return rowValue.order;
            }
        };
    }
}
