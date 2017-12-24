package com.firstlinesoftware.rmrs.client.fields;

import com.firstlinesoftware.base.client.Base;
import com.firstlinesoftware.base.client.SuccessCallback;
import com.firstlinesoftware.base.client.columns.DateGridColumnDefinition;
import com.firstlinesoftware.base.client.factories.FormItemFactory;
import com.firstlinesoftware.base.client.factories.Registrable;
import com.firstlinesoftware.base.client.fields.AbstractFormField;
import com.firstlinesoftware.base.client.fields.CheckBoxField;
import com.firstlinesoftware.base.client.fields.DateBoxField;
import com.firstlinesoftware.base.client.fields.TextBoxField;
import com.firstlinesoftware.base.client.services.ActionCallback;
import com.firstlinesoftware.base.client.views.FormView;
import com.firstlinesoftware.base.client.widgets.BaseLabel;
import com.firstlinesoftware.base.client.widgets.ExpandableList;
import com.firstlinesoftware.base.client.widgets.FormItemBase;
import com.firstlinesoftware.base.client.widgets.Tags;
import com.firstlinesoftware.base.shared.dto.DateRange;
import com.firstlinesoftware.ecm.client.Ecm;
import com.firstlinesoftware.ecm.client.fields.DocumentSelectorField;
import com.firstlinesoftware.ecm.client.proxies.DocumentProxy;
import com.firstlinesoftware.ecm.client.widgets.DocumentLabel;
import com.firstlinesoftware.ecm.client.widgets.DocumentSelector;
import com.firstlinesoftware.ecm.shared.dto.Document;
import com.firstlinesoftware.orgstruct.client.fields.PositionField;
import com.firstlinesoftware.orgstruct.shared.dto.Position;
import com.firstlinesoftware.rmrs.client.Rmrs;
import com.firstlinesoftware.rmrs.client.messages.RmrsMessages;
import com.firstlinesoftware.rmrs.client.proxies.RequirementProxy;
import com.firstlinesoftware.rmrs.client.widgets.RequirementLabel;
import com.firstlinesoftware.rmrs.client.widgets.RequirementSelector;
import com.firstlinesoftware.rmrs.shared.dto.CircularLetter;
import com.firstlinesoftware.rmrs.shared.dto.Requirement;
import com.firstlinesoftware.rmrs.shared.dto.RmrsDirectories;
import com.firstlinesoftware.route.client.Route;
import com.firstlinesoftware.route.client.messages.RouteTemplates;
import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.text.shared.AbstractRenderer;
import com.google.gwt.text.shared.Renderer;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Provider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class RequirementFields implements Registrable {
    private static final String DOCUMENT_COUNTING_STYLE = "documentCounting";
    private final FormItemFactory formItemFactory = Base.getInjector().getFormItemFactory();
    private final RmrsMessages messages = Rmrs.getInjector().getMessages();
    private final DocumentProxy documentProxy = Ecm.getInjector().getDocumentProxy();
    private final RequirementProxy requirementProxy = Rmrs.getInjector().getRequirementProxy();

    @Override
    public void register() {
        formItemFactory.register(new ParentField(), messages.includedIn());
        formItemFactory.registerDouble(new PartField(), messages.part(), new VolumeField(), messages.volume());
        formItemFactory.register(new NumberField(), messages.number());
        formItemFactory.register(new IsHeaderField(), messages.header());
        formItemFactory.registerDouble(new EffectiveBeginField(), messages.effectiveBegin(), new EffectiveEndField(), messages.effectiveEnd());
        formItemFactory.register(new ResponsibleField(), messages.responsible());
        formItemFactory.register(new TagsField(), messages.tags());
        formItemFactory.register(new LinkField(), messages.link());
        formItemFactory.register(new CircularLetterField(), messages.circularLetter());
        formItemFactory.register(new PathField(), messages.path());
        formItemFactory.registerHeader(new DocumentCounting(), messages.count());
    }


    private class DocumentCounting implements FormItemFactory.HeaderProvider<Requirement> {
        @Override
        public boolean hasAspect(Object o) {
            return o instanceof Requirement;
        }

        @Override
        public String getStyle(Requirement o) {
            return DOCUMENT_COUNTING_STYLE;
        }

        @Override
        public void getValue(Requirement dto, final SuccessCallback<String> callback) {
            requirementProxy.getDocumentCounting(dto.getId(), new ActionCallback<String>(null) {
                @Override
                public void onActionSuccess(String result) {
                    callback.onSuccess(messages.totalVisits() + " " + result);
                }
            });
        }

        @Override
        public String getTitle(Requirement dto) {
            return messages.numberOfViews();
        }
    }

    private class IsHeaderField extends CheckBoxField<Requirement> {
        @Override
        protected Boolean getValue(Requirement dto) {
            return dto.header;
        }

        @Override
        protected void setValue(Requirement dto, Boolean value) {
            dto.header = value;
        }

        @Override
        public boolean hasAspect(Object o) {
            return o instanceof Requirement;
        }
    }

    private class PartField extends TextBoxField<Requirement> {
        @Override
        protected String getValue(Requirement dto) {
            return dto.part;
        }

        @Override
        protected void setValue(Requirement dto, String value) {
            dto.part = value;
        }

        @Override
        public boolean hasAspect(Object dto) {
            return dto instanceof Requirement;
        }

        @Override
        public FormItemBase.Order getOrder() {
            return FormItemBase.Order.HIGH1;
        }

        @Override
        public Widget getViewWidget() {
            return null;
        }

        @Override
        public Widget getEditorWidget() {
            return null;
        }
    }

    private class VolumeField extends TextBoxField<Requirement> {
        @Override
        protected String getValue(Requirement dto) {
            return dto.volume;
        }

        @Override
        protected void setValue(Requirement dto, String value) {
            dto.volume = value;
        }

        @Override
        public boolean hasAspect(Object dto) {
            return dto instanceof Requirement;
        }

        @Override
        public FormItemBase.Order getOrder() {
            return FormItemBase.Order.HIGH1;
        }

        @Override
        public Widget getViewWidget() {
            return null;
        }

        @Override
        public Widget getEditorWidget() {
            return null;
        }
    }

    private class NumberField extends TextBoxField<Requirement> {
        @Override
        protected String getValue(Requirement dto) {
            return dto.number;
        }

        @Override
        protected void setValue(Requirement dto, String value) {
            dto.number = value;
        }

        @Override
        public boolean hasAspect(Object dto) {
            return dto instanceof Requirement;
        }

        @Override
        public FormItemBase.Order getOrder() {
            return FormItemBase.Order.HIGH1;
        }
    }

    private class ParentField extends DocumentSelectorField<Requirement> {
        protected ParentField() {
            super(Requirement.KIND);
        }

        @Override
        protected Document getValue(Requirement dto) {
            return dto.parent;
        }

        @Override
        protected void setValue(Requirement dto, Document value) {
            dto.parent = (Requirement) value;
        }

        @Override
        public boolean hasAspect(Object dto) {
            return dto instanceof Requirement;
        }

        @Override
        public DocumentSelector getEditorWidget() {
            return new RequirementSelector(true);
        }

        @Override
        public DocumentLabel getViewWidget() {
            return new RequirementLabel();
        }
    }

   private class LinkField extends DocumentSelectorField<Requirement> {
        protected LinkField() {
            super(Requirement.KIND);
        }

        @Override
        protected Document getValue(Requirement dto) {
            return dto;
        }

        @Override
        protected void setValue(Requirement dto, Document value) {
        }

        @Override
        public boolean hasAspect(Object dto) {
            return dto instanceof Requirement;
        }

        @Override
        public DocumentSelector getEditorWidget() {
            return null;
        }

       @Override
       public FormItemBase.Order getOrder() {
           return FormItemBase.Order.LOW2;
       }
   }

    private class EffectiveBeginField extends DateBoxField<Requirement> {
        @Override
        protected Date getValue(Requirement requirement) {
            return requirement.effective != null ? requirement.effective.min : null;
        }

        @Override
        protected void setValue(Requirement requirement, Date date) {
            if(requirement.effective == null) {
                requirement.effective = new DateRange();
            }
            requirement.effective.min = date;
        }

        @Override
        public boolean hasAspect(Object o) {
            return o instanceof Requirement;
        }
    }

    private class EffectiveEndField extends DateBoxField<Requirement> {
        @Override
        protected Date getValue(Requirement requirement) {
            return requirement.effective != null ? requirement.effective.max : null;
        }

        @Override
        protected void setValue(Requirement requirement, Date date) {
            if(requirement.effective == null) {
                requirement.effective = new DateRange();
            }
            requirement.effective.max = date;
        }

        @Override
        public boolean hasAspect(Object o) {
            return o instanceof Requirement;
        }
    }

    private class ResponsibleField extends PositionField<Requirement> {

        @Override
        protected Position getValue(Requirement requirement) {
            return requirement.responsible;
        }

        @Override
        protected void setValue(Requirement requirement, Position position) {
            requirement.responsible = position;

        }

        @Override
        public boolean hasAspect(Object o) {
            return o instanceof Requirement;
        }
    }

    private static class TagsField extends AbstractFormField<List<String>, Requirement> {
        @Override
        public boolean hasAspect(final Object dto) {
            return dto instanceof Requirement;
        }

        @Override
        public List<String> getValue(final Requirement dto) {
            return dto.tags;
        }

        @Override
        public void setValue(final Requirement dto, final List<String> value) {
            dto.tags = value;
        }

        @Override
        public Widget getEditorWidget() {
            return new Tags(RmrsDirectories.TAGS.getType());
        }

        @Override
        public Widget getViewWidget() {
            return new Tags(RmrsDirectories.TAGS.getType(), false);
        }

        @Override
        public FormItemBase.Order getOrder() {
            return FormItemBase.Order.LOW1;
        }
    }

    private class CircularLetterField extends AbstractFormField<String, Requirement> {
        private final RouteTemplates templates = Route.getInjector().getTemplates();
        private final Renderer<CircularLetter> RENDERER = new AbstractRenderer<CircularLetter>() {
            @Override
            public String render(CircularLetter value) {
                if (value == null) {
                    return "";
                }
                final String name = DocumentLabel.RENDERER.render(value);
                final Date archivingDate = value.routeState.archivingDate;
                final SafeHtml timestamp = DateGridColumnDefinition.renderDate(archivingDate);
                final String width = String.valueOf(archivingDate != null ? DateGridColumnDefinition.PREFERRED_WIDTH : 0);
                final String style = archivingDate != null ? "accepted" : "";
                return templates.roundMember("documentNodeSpan", SafeHtmlUtils.fromTrustedString(name), timestamp, style, width).asString();
            }
        };
        @Override
        protected String getValue(Requirement dto) {
            return dto.circularLetter;
        }

        @Override
        protected void setValue(Requirement dto, String value) {}

        @Override
        public boolean hasAspect(Object dto) {
            return dto instanceof Requirement;
        }

        @Override
        public Widget getViewWidget() {
            return new BaseLabel<String>(new AbstractRenderer<String>() {
                @Override
                public String render(String value) {
                    return value;
                }
            }, com.google.gwt.dom.client.Document.get().createAnchorElement()) {
                @Override
                protected void doRenderer(String value) {
                    documentProxy.get(value, new ActionCallback<CircularLetter>(null) {
                        @Override
                        public void onActionSuccess(CircularLetter result) {
                            renderDocument(result);
                        }
                    });
                }

                @Override
                public String getValue() {
                    return value;
                }

                private void renderDocument(CircularLetter document) {
                    super.doRenderer(RENDERER.render(document));
                    final AnchorElement as = AnchorElement.as(getElement());
                    if (document != null) {
                        as.setHref("#view:id=" + document.getId());
                    } else {
                        as.setHref("javascript:void(0)");
                    }
                }
            };
        }

        @Override
        public Widget getEditorWidget() {
            return null;
        }

        @Override
        public String getViewTab() {
            return messages.route();
        }
    }

    private class PathField extends AbstractFormField<List<Requirement>, Requirement> {

        @Override
        protected List<Requirement> getValue(Requirement requirement) {
            return new ArrayList<>(Collections.singletonList(requirement.parent));

        }

        @Override
        protected void setValue(Requirement requirement, List<Requirement> requirements) {
        }

        @Override
        public boolean hasAspect(Object o) {
            return o instanceof Requirement;
        }

        @Override
        public Widget getViewWidget() {
            final ExpandableList<Document, RequirementLabel> result = new ExpandableList<>(new Provider<RequirementLabel>() {
                @Override
                public RequirementLabel get() {
                    return new RequirementLabel();
                }
            });
            result.setEditable(false);
            return result;
        }

        @Override
        public Widget getEditorWidget() {
            return null;
        }

        @Override
        public String getViewTab() {
            return messages.path();
        }

        @Override
        public Widget createWidget(FormView<Requirement> view, String itemName, boolean editor, Requirement dto) {
            return !editor ? createSingleWidget(view, itemName, false, dto) : null;
        }
    }
}
